package pt.unl.fct.di.apdc.firstwebapp.utils;

import com.google.gson.Gson;
import pt.unl.fct.di.apdc.firstwebapp.data.TokenData;

import javax.ws.rs.core.Cookie;
import javax.ws.rs.core.Response;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.Base64;

public class Common {

    private static TokenData decodeToken(String encodedToken) throws Exception {
        byte[] decodedBytes = Base64.getDecoder().decode(encodedToken);
        TokenData token = new Gson().fromJson(new String(decodedBytes, StandardCharsets.UTF_8), TokenData.class);

        if (!token.isValid()) {
            throw new Exception("Invalid token.");
        }
        return token;
    }

    public static TokenData getTokenDataFromCookie(Cookie cookie) throws Exception {
        if (cookie == null) {
            throw new Exception("No token found.");
        }
        return decodeToken(cookie.getValue().split(";")[0]);
    }

    public static TokenData getTokenDataFromString(String token) throws Exception {
        if (token == null) {
            throw new Exception("No token found.");
        }
        return decodeToken(token);
    }

    public static TokenValidator validateToken(String auth) {
        if (auth == null || !auth.startsWith("Bearer ")) {
            return new TokenValidator(Response.status(Response.Status.UNAUTHORIZED).entity(Constants.ERROR_MESSAGE_INVALID_TOKEN).build(), null);
        }

        String token = auth.substring("Bearer ".length() + 4);
        TokenData tokenData;
        try {
            tokenData = Common.getTokenDataFromString(token);
        } catch (Exception e) {
            return new TokenValidator(Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage()).build(), null);
        }

        if (tokenData.getValidTo().isBefore(LocalDateTime.now())) {
            return new TokenValidator(Response.status(Response.Status.UNAUTHORIZED).entity(Constants.ERROR_MESSAGE_INVALID_TOKEN).build(), null);
        }

        return new TokenValidator(null, tokenData);
    }
}