package com.cognito.api.config;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authentication.dao.AbstractUserDetailsAuthenticationProvider;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;

public class CognitoAuthenticationProvider extends AbstractUserDetailsAuthenticationProvider{

    @Override
    protected void additionalAuthenticationChecks(UserDetails userDetails,
            UsernamePasswordAuthenticationToken authentication) throws AuthenticationException {
        // TODO Auto-generated method stub
        // No need for any additional checks for the time being
        throw new UnsupportedOperationException("Unimplemented method 'additionalAuthenticationChecks'");
    }

    @Override
    protected UserDetails retrieveUser(String username,
            UsernamePasswordAuthenticationToken authentication) throws AuthenticationException {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'retrieveUser'");
    }
}
