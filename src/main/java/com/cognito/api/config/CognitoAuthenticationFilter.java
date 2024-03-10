package com.cognito.api.config;

import java.io.IOException;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import com.amazonaws.services.cognitoidp.model.AttributeType;
import com.amazonaws.services.cognitoidp.model.UserNotFoundException;
import com.cognito.api.models.User;
import com.cognito.api.services.CognitoService;
import com.cognito.api.utils.JwtTokenProcessor;
import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.proc.BadJOSEException;
import com.nimbusds.jwt.JWTClaimsSet;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.text.ParseException;
import java.util.stream.Collectors;
import java.util.Map;
import java.util.List;

@Component
@Slf4j
@RequiredArgsConstructor
public class CognitoAuthenticationFilter extends OncePerRequestFilter {

    private final CognitoService cognitoService;
    private final JwtTokenProcessor jwtTokenProcessor;
    
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
            FilterChain filterChain) throws ServletException, IOException {
        String token = getTokenFromRequest(request);

        if(token != null){
            try{
                JWTClaimsSet claimsSet = jwtTokenProcessor.decodeToken(token);

                if(!isValid(claimsSet)){
                    log.info("Invalid token");
                }

                String email = claimsSet.getClaim("email").toString();

                var result = cognitoService.fetchUserInfoByEmail(email);

                Map<String, String> userAttributes = result.getUserAttributes().stream().collect(Collectors.toMap(AttributeType::getName, AttributeType::getValue));
                userAttributes.forEach((key, value) -> System.out.println(key + " : " + value));

                String phoneNumber = claimsSet.getClaim("phone_number").toString();
                String userId = claimsSet.getSubject();
                String role = ((List<String>)claimsSet.getClaims().get("cognito:groups")).get(0);
                User user = User.builder()
                                .email(email)
                                .phoneNumber(phoneNumber)
                                .userId(userId)
                                .role(role)
                                .build();
                
                var customToken = new UsernamePasswordAuthenticationToken(user, null, user.getAuthorities());

                SecurityContextHolder.getContext().setAuthentication(customToken);
                
            }catch(BadJOSEException | ParseException | JOSEException ex){
                log.info("Invalid token: {}", ex.getMessage());
            }catch(UserNotFoundException ex){
                log.info("Invalid token: {}", ex.getMessage());
            }
        }

        filterChain.doFilter(request, response);

    }
    
    private String getTokenFromRequest(HttpServletRequest request) {
        // Extract authorization header
        var authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);

        if (authHeader == null || authHeader.isEmpty()) {
            return null;
        }

        // Bearer <JWT TOKEN>
        if (authHeader.length() > 7 && authHeader.startsWith("Bearer ")) {
            return authHeader.substring(7);
        }
        log.info("Invalid authorization bearer");
        return null;
    }

    private boolean isValid(JWTClaimsSet claimsSet){
        return  jwtTokenProcessor.isCorrectIssuer(claimsSet) &&
                jwtTokenProcessor.isCurrentAudience(claimsSet) && 
                jwtTokenProcessor.isIdToken(claimsSet);

    }
}
