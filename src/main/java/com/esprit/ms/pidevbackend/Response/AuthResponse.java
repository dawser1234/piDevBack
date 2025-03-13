package com.esprit.ms.pidevbackend.Response;

import lombok.Getter;

import java.util.Map;

@Getter
public class AuthResponse {
    private String token;
    private Map<String, Object> userAttributes;

    public AuthResponse(String token) {
        this.token = token;
    }
    public AuthResponse(String token, Map<String, Object> userAttributes) {
        this.token = token;
        this.userAttributes = userAttributes;
    }


    public void setToken(String token) {
        this.token = token;
    }

    public String getToken() {
        return token;
    }
}
