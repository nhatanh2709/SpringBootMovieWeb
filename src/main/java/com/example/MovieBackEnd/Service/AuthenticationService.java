package com.example.MovieBackEnd.Service;

import com.example.MovieBackEnd.dto.request.AuthenticationRequest;
import com.example.MovieBackEnd.dto.request.IntrospectRequest;
import com.example.MovieBackEnd.dto.request.LogoutRequest;
import com.example.MovieBackEnd.dto.request.RefreshRequest;
import com.example.MovieBackEnd.dto.response.AuthenticationResponse;
import com.example.MovieBackEnd.dto.response.IntrospectResponse;
import com.example.MovieBackEnd.exception.AppException;
import com.example.MovieBackEnd.exception.ErrorCode;
import com.example.MovieBackEnd.models.InvalidatedToken;
import com.example.MovieBackEnd.models.User;
import com.example.MovieBackEnd.repositories.InvalidatedTokenRepository;
import com.example.MovieBackEnd.repositories.UserRepository;
import com.nimbusds.jose.*;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jose.crypto.MACVerifier;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.experimental.NonFinal;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.text.ParseException;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.StringJoiner;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthenticationService {
    private final UserRepository userRepository;
    private final InvalidatedTokenRepository invalidatedTokenRepository;
    @NonFinal
    @Value("${jwt.signerKey}")
    protected String SIGNER_KEY;

    @NonFinal
    @Value("${jwt.valid-duration}")
    protected long VALID_DURATION;

    @NonFinal
    @Value("${jwt.refreshable-duration}")
    protected long REFRESHABLE_DURATION;

    public IntrospectResponse introspect(IntrospectRequest request) throws JOSEException, ParseException {
        var token = request.getToken();

        boolean isValid = true;
        try{
            verifyToken(token , false);
        } catch (Exception e) {
            isValid = false;
        }
        return IntrospectResponse.builder().valid(isValid).build();
    }

    public AuthenticationResponse refreshToken(RefreshRequest request) throws ParseException, JOSEException{
        var signedToken = verifyToken(request.getToken(),true);
        String jit = signedToken.getJWTClaimsSet().getJWTID();
        Date expiredTime = signedToken.getJWTClaimsSet().getExpirationTime();

        InvalidatedToken invalidatedToken = InvalidatedToken.builder().
                id(jit).expiryTime(expiredTime).build();

        invalidatedTokenRepository.save(invalidatedToken);
        var email = signedToken.getJWTClaimsSet().getSubject();

        var user = userRepository.findByEmail(email);
        if(user == null) throw new AppException(ErrorCode.UNAUTHENTICATED);
        var token = generateToken(user);
        return AuthenticationResponse.builder()
                .token(token).authenticated(true).build();

    }
    public AuthenticationResponse authenticate(AuthenticationRequest request){


        User user = userRepository.findByEmail(request.getEmail());
        if(user == null){
            throw  new AppException(ErrorCode.EMAIL_NOT_EXISTED);
        }
        PasswordEncoder passwordEncoder = new BCryptPasswordEncoder(10);
        boolean authenticated =  passwordEncoder.matches(request.getPassword() , user.getPassWord());
        if(!authenticated){
            throw new AppException(ErrorCode.WRONG_PASSWORD);
        }
        var token = generateToken(user);
        return AuthenticationResponse.builder().
                token(token).authenticated(true).email(user.getEmail())
                .userName(user.getUserName()).profilePic(user.getProfilePic()).build();
    }
    private SignedJWT verifyToken(String token, boolean isRefresh) throws JOSEException,ParseException{
        JWSVerifier verifier = new MACVerifier(SIGNER_KEY.getBytes());
        SignedJWT signedJWT = SignedJWT.parse(token);

        Date expiryTime = (isRefresh)
                ? new Date(signedJWT.getJWTClaimsSet().getIssueTime()
                .toInstant().plus(REFRESHABLE_DURATION, ChronoUnit.SECONDS).toEpochMilli())
                : signedJWT.getJWTClaimsSet().getExpirationTime();

        var verified = signedJWT.verify(verifier);

        if (!(verified && expiryTime.after(new Date()))) throw new AppException(ErrorCode.UNAUTHENTICATED);
        if(invalidatedTokenRepository.existsById(signedJWT.getJWTClaimsSet().getJWTID())){
            throw new AppException(ErrorCode.UNAUTHENTICATED);
        }

        return signedJWT;
    }
    public void logout(LogoutRequest request)throws ParseException,JOSEException{
        try{
            var signToken = verifyToken(request.getToken(),true);
            String jit = signToken.getJWTClaimsSet().getJWTID();
            Date expiryTime = signToken.getJWTClaimsSet().getExpirationTime();
            InvalidatedToken invalidatedToken =
                    InvalidatedToken.builder().id(jit).expiryTime(expiryTime).build();
            invalidatedTokenRepository.save(invalidatedToken);
        } catch (AppException exception){
            log.info("Token already expired");
        }

    }



    private String generateToken(User user) {
        JWSHeader header = new JWSHeader(JWSAlgorithm.HS512);
        JWTClaimsSet jwtClaimsSet = new JWTClaimsSet.Builder()
                .subject(user.getEmail())
                .issueTime(new Date())
                .expirationTime(new Date(
                        Instant.now().plus(VALID_DURATION, ChronoUnit.HOURS).toEpochMilli()
                ))
                .jwtID(UUID.randomUUID().toString())
                .claim("scope", buildScope(user))
                .build();

        Payload payload =new Payload(jwtClaimsSet.toJSONObject());

        JWSObject jwsObject = new JWSObject(header,payload);
        try{
            jwsObject.sign(new MACSigner(SIGNER_KEY.getBytes()));
            return jwsObject.serialize();
        } catch (JOSEException e){
            throw new RuntimeException(e);
        }
    }
    private String buildScope(User user){
        StringJoiner stringJoiner = new StringJoiner(" ");
        if(!CollectionUtils.isEmpty(user.getRoles())) {
            user.getRoles().forEach(stringJoiner::add);
        }
        System.out.println(user.getRoles());
        return stringJoiner.toString();
    }
}
