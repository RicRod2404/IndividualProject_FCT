package pt.unl.fct.di.apdc.firstwebapp.utils;

import pt.unl.fct.di.apdc.firstwebapp.data.TokenData;

import javax.ws.rs.core.Response;

public class TokenValidator {
    private Response response;
    private TokenData tokenData;

    public TokenValidator(Response response, TokenData tokenData) {
        this.response = response;
        this.tokenData = tokenData;
    }

    public Response getResponse() {
        return response;
    }

    public TokenData getTokenData() {
        return tokenData;
    }
}