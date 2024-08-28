package pt.unl.fct.di.apdc.firstwebapp.resources;

import com.google.appengine.repackaged.org.apache.commons.codec.digest.DigestUtils;
import com.google.cloud.datastore.*;
import com.google.gson.Gson;
import pt.unl.fct.di.apdc.firstwebapp.data.LoginData;
import pt.unl.fct.di.apdc.firstwebapp.data.TokenData;
import pt.unl.fct.di.apdc.firstwebapp.utils.Common;

import javax.ws.rs.*;
import javax.ws.rs.core.Cookie;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.NewCookie;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.UUID;

@Path("/")
@Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
public class LoginResource {

    private static final String key = "key";
    private final Datastore datastore = DatastoreOptions.getDefaultInstance().getService();

    public LoginResource() {
    }

    //OP8: User login
    @POST
    @Path("/login")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response login(LoginData data) {
        try {
            Key userKey = datastore.newKeyFactory().setKind("User").newKey(data.getInput());
            Entity userEntity = datastore.get(userKey);

            // If userEntity is null, try to retrieve the user by email
            if (userEntity == null) {
                Query<Entity> query = Query.newEntityQueryBuilder()
                        .setKind("User")
                        .setFilter(StructuredQuery.PropertyFilter.eq("Email", data.getInput()))
                        .build();
                QueryResults<Entity> results = datastore.run(query);
                if (results.hasNext()) {
                    userEntity = results.next();
                }
            }

            // Check if the user exists
            if (userEntity == null) {
                return Response.status(Status.BAD_REQUEST).entity("User " + data.getInput() + " does not exist.").build();
            }

            if (userEntity.getString("Account Status").equals("INACTIVE")) {
                return Response.status(Status.BAD_REQUEST).entity("User " + data.getInput() + " has a inactive account.").build();
            }

            // Check if the provided password matches the one in the datastore
            String storedPassword = userEntity.getString("Password");
            if (!storedPassword.equals(DigestUtils.sha512Hex(data.getPassword()))) {
                return Response.status(Status.BAD_REQUEST).entity("Wrong password.").build();
            }

            // If the credentials are correct, generate a token and set it as a cookie in the response
            LocalDateTime now = LocalDateTime.now();
            LocalDateTime validTo = LocalDateTime.now().plus(Duration.ofHours(1));

            String verifier = UUID.randomUUID().toString();
            TokenData token = new TokenData(data.getInput(), userEntity.getString("Role"), true, now, validTo, verifier);

            // When setting the cookie:
            String tokenJson = new Gson().toJson(token);
            String encodedTokenJson = Base64.getEncoder().encodeToString(tokenJson.getBytes(StandardCharsets.UTF_8));

            NewCookie cookie = new NewCookie(key, encodedTokenJson, "/", "", "token", 1800, false, false);

            return Response.ok("User " + data.getInput() + " logged in.").cookie(cookie).build();
        } catch (Exception e) {
            return Response.status(Status.BAD_REQUEST).entity("Error while trying to login user: " + e.getMessage()).build();
        }
    }

    //OP10: User logout
    @POST
    @Path("/logout")
    public Response logout(@CookieParam("key") Cookie cookie) {
        try {
            TokenData token = Common.getTokenDataFromCookie(cookie);

            // Invalidate the token
            token.setValid(false);
            String tokenJson = new Gson().toJson(token);

            // Set the cookie to expire
            NewCookie newCookie = new NewCookie(key, tokenJson + "; SameSite=Strict; Secure", "/", "", "token", 0, false, false);

            return Response.ok().cookie(newCookie).entity("User logged out.").build();
        } catch (Exception e) {
            return Response.status(Status.BAD_REQUEST).entity("Error while trying to logout user: " + e.getMessage()).build();
        }
    }
}
