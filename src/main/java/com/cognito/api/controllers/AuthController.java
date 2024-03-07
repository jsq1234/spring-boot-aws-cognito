package com.cognito.api.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.amazonaws.services.cognitoidp.model.ConfirmSignUpResult;
import com.amazonaws.services.cognitoidp.model.GetUserRequest;
import com.amazonaws.services.cognitoidp.model.GetUserResult;
import com.amazonaws.services.cognitoidp.model.InitiateAuthResult;
import com.amazonaws.services.cognitoidp.model.SignUpResult;
import com.cognito.api.dto.AccessToken;
import com.cognito.api.dto.ConfirmEmailRequest;
import com.cognito.api.dto.LoginRequest;
import com.cognito.api.dto.SignUpRequest;
import com.cognito.api.services.CognitoService;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private CognitoService cognitoService;

    @PostMapping("/signup")
    public ResponseEntity<SignUpResult> createUser(@Valid @RequestBody SignUpRequest signUpRequest){
        SignUpResult result = cognitoService
                                    .registerUser(signUpRequest.email(), 
                                                signUpRequest.phoneNumber(),
                                                signUpRequest.password());
        return ResponseEntity.status(201).body(result);
    }

    @PostMapping("/confirm")
    public ResponseEntity<ConfirmSignUpResult> confirmUser(@Valid @RequestBody ConfirmEmailRequest confirmRequest){
        ConfirmSignUpResult result = cognitoService
                                        .confirmVerificationCode(confirmRequest.email(), confirmRequest.code());
        return ResponseEntity.status(200).body(result);
    }

    @PostMapping("/login")
    public ResponseEntity<InitiateAuthResult> loginUser(@Valid @RequestBody LoginRequest loginRequest){
        InitiateAuthResult result = cognitoService
                                        .signInUser(loginRequest.email(), loginRequest.password());
        return ResponseEntity.ok(result);
    }

    @PostMapping("/user")
    public ResponseEntity<GetUserResult> getUserInfo(@RequestBody AccessToken token){
        GetUserResult result = cognitoService.fetchUserInfo(token.token());
        return ResponseEntity.ok(result);
    }
}

