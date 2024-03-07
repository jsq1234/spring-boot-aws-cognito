package com.cognito.api.services;

import org.springframework.stereotype.Service;
import com.amazonaws.services.cognitoidp.AWSCognitoIdentityProvider;
import com.amazonaws.services.cognitoidp.model.AttributeType;
import com.amazonaws.services.cognitoidp.model.AuthFlowType;
import com.amazonaws.services.cognitoidp.model.ConfirmSignUpRequest;
import com.amazonaws.services.cognitoidp.model.ConfirmSignUpResult;
import com.amazonaws.services.cognitoidp.model.GetGroupRequest;
import com.amazonaws.services.cognitoidp.model.GetUserRequest;
import com.amazonaws.services.cognitoidp.model.GetUserResult;
import com.amazonaws.services.cognitoidp.model.InitiateAuthRequest;
import com.amazonaws.services.cognitoidp.model.InitiateAuthResult;
import com.amazonaws.services.cognitoidp.model.SignUpRequest;
import com.amazonaws.services.cognitoidp.model.SignUpResult;
import com.cognito.api.config.CognitoPoolConfig;
import com.cognito.api.utils.SecretHashUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import java.util.Map;

@RequiredArgsConstructor
@Slf4j
@Service
public class CognitoService{
    private final CognitoPoolConfig cognitoPoolConfig;
    private final AWSCognitoIdentityProvider cognitoIdentityProvider;

    public SignUpResult registerUser(String email, String phoneNumber, String password){
        String secretHash = SecretHashUtil.calculateSecretHash(
                                        cognitoPoolConfig.getClientId(), 
                                        cognitoPoolConfig.getClientSecret(),
                                        email);

        log.info("Creating new user: {}", email);

        SignUpRequest signUpRequest = 
                    new SignUpRequest()
                                .withClientId(cognitoPoolConfig.getClientId())
                                .withUsername(email)
                                .withPassword(password)
                                .withSecretHash(secretHash)
                                .withUserAttributes(
                                    new AttributeType[]{
                                        new AttributeType()
                                            .withName("email")
                                            .withValue(email),
                                        new AttributeType()
                                            .withName("phone_number")
                                            .withValue(phoneNumber)
                                    }
                                );
        SignUpResult signUpResult = cognitoIdentityProvider.signUp(signUpRequest);
        
        log.info("User succesfully created : {}", signUpResult);

        return signUpResult;
    }

    public ConfirmSignUpResult confirmVerificationCode(String email, String verificationCode){
        String secretHash = SecretHashUtil.calculateSecretHash(
                                        cognitoPoolConfig.getClientId(), 
                                        cognitoPoolConfig.getClientSecret(),
                                        email);

        log.info("Confirming user[{}]", email);

        ConfirmSignUpRequest request = 
                    new ConfirmSignUpRequest()
                        .withClientId(cognitoPoolConfig.getClientId())
                        .withConfirmationCode(verificationCode)
                        .withSecretHash(secretHash)
                        .withUsername(email);

        ConfirmSignUpResult confirmationResult = cognitoIdentityProvider.confirmSignUp(request);

        log.info("User[{}] successfully confirmed: {}", email, confirmationResult);

        return confirmationResult;
    }

    public InitiateAuthResult signInUser(String email, String password){
        String secretHash = SecretHashUtil.calculateSecretHash(
                                        cognitoPoolConfig.getClientId(), 
                                        cognitoPoolConfig.getClientSecret(),
                                        email);
        
        Map<String,String> authParams = Map.ofEntries(
            Map.entry("USERNAME", email),
            Map.entry("PASSWORD", password),
            Map.entry("SECRET_HASH", secretHash)
        );

        log.info("Signing in user[{}]", email);

        InitiateAuthRequest authRequest = new InitiateAuthRequest()
                                                .withAuthFlow(AuthFlowType.USER_PASSWORD_AUTH)
                                                .withAuthParameters(authParams)
                                                .withClientId(cognitoPoolConfig.getClientId());
        
        
        InitiateAuthResult result = cognitoIdentityProvider.initiateAuth(authRequest);

        log.info("User authenticated : {}", result);

        return result;

    }

    public GetUserResult fetchUserInfo(String accessToken){
        GetUserRequest request = new GetUserRequest()
                                    .withAccessToken(accessToken);

        GetUserResult result = cognitoIdentityProvider.getUser(request);

        return result;
    }
    
}
