package com.cognito.api.utils;

import org.springframework.stereotype.Component;
import com.cognito.api.config.CognitoPoolConfig;
import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.jwk.source.RemoteJWKSet;
import com.nimbusds.jose.proc.BadJOSEException;
import com.nimbusds.jose.proc.JWSKeySelector;
import com.nimbusds.jose.proc.JWSVerificationKeySelector;
import com.nimbusds.jose.proc.SecurityContext;
import com.nimbusds.jose.util.DefaultResourceRetriever;
import com.nimbusds.jose.util.ResourceRetriever;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.proc.ConfigurableJWTProcessor;
import com.nimbusds.jwt.proc.DefaultJWTProcessor;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import java.net.URL;
import java.text.ParseException;
import java.net.MalformedURLException;

@Component
@Slf4j
@RequiredArgsConstructor
public class JwtTokenProcessor {
    private final CognitoPoolConfig cognitoPoolConfig;
    private ConfigurableJWTProcessor<SecurityContext> jwtProcessor;

    @PostConstruct
    void init(){
        try{         
            ResourceRetriever resourceRetriever = new DefaultResourceRetriever(0,0);
            URL jwkUrl = new URL(String.format("%s/.well-known/jwks.json", cognitoPoolConfig.getIssuer()));
            RemoteJWKSet<SecurityContext> remoteJWKSet = new RemoteJWKSet<>(jwkUrl, resourceRetriever);
            JWSKeySelector<SecurityContext> keySelector = new JWSVerificationKeySelector<>(JWSAlgorithm.RS256, remoteJWKSet);
            jwtProcessor = new DefaultJWTProcessor<>();
            jwtProcessor.setJWSKeySelector(keySelector);
        }catch(MalformedURLException ex){
            log.info("Malformed URL: {}", ex.getMessage());
        }
    }

    public JWTClaimsSet decodeToken(String token) throws BadJOSEException, JOSEException, ParseException {
        JWTClaimsSet claimsSet = jwtProcessor.process(token, null);
        log.info("Decoded jwt: {}", claimsSet);
        return claimsSet;
    }

    public boolean isCorrectIssuer(JWTClaimsSet claimsSet){
        boolean result = claimsSet.getClaim("iss").equals(cognitoPoolConfig.getIssuer());
        if(!result){
            log.info("Issuer {} in JWT token doesn't match cognito idp {}",
                         claimsSet.getClaim("iss"), cognitoPoolConfig.getIssuer());
        }
        return result;
    }

    public boolean isIdToken(JWTClaimsSet claimsSet){
        boolean result = claimsSet.getClaim("token_use").equals("id");
        if(!result){
            log.info("Jwt token isn't an id token, but {}", claimsSet.getClaim("token_use"));
        }
        return result;
    }

    public boolean isCurrentAudience(JWTClaimsSet claimsSet){
        boolean result = claimsSet.getClaim("aud").equals(cognitoPoolConfig.getClientId());
        if(!result){
            log.info("Audience doesn't match");
        }
        return result;
    }
}
