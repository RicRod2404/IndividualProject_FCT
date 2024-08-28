package pt.unl.fct.di.apdc.firstwebapp.resources;

import com.google.appengine.repackaged.org.apache.commons.codec.digest.DigestUtils;
import com.google.cloud.datastore.*;
import pt.unl.fct.di.apdc.firstwebapp.data.PasswordData;
import pt.unl.fct.di.apdc.firstwebapp.utils.Constants;
import pt.unl.fct.di.apdc.firstwebapp.utils.TokenValidator;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import static pt.unl.fct.di.apdc.firstwebapp.utils.Common.validateToken;
import static pt.unl.fct.di.apdc.firstwebapp.utils.ReturnResponse.generateResponse;

@Path("/pwd")
@Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
public class PasswordResource {

    private final Datastore datastore = DatastoreOptions.getDefaultInstance().getService();

    public PasswordResource() {
    } // nothing to be done here

    //OP7: Change user password
    @POST
    @Path("/change")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response changePassword(@HeaderParam("Authorization") String auth, PasswordData data) {
        TokenValidator tokendata = validateToken(auth);

        Transaction txn = datastore.newTransaction();

        try {
            Key userKey = datastore.newKeyFactory().setKind("User").newKey(tokendata.getTokenData().getUser());
            Entity user = txn.get(userKey);

            if (user == null) {
                txn.rollback();
                return generateResponse(txn, Response.Status.BAD_REQUEST, Constants.USER_USERNAME + tokendata.getTokenData().getUser() + " does not exist.");
            }

            if (!user.getString("Password").equals(DigestUtils.sha512Hex(data.getOldPassword()))) {
                txn.rollback();
                return generateResponse(txn, Response.Status.BAD_REQUEST, "Wrong password.");
            }

            if (data.getNewPassword().equals(data.getOldPassword())) {
                txn.rollback();
                return generateResponse(txn, Response.Status.BAD_REQUEST, "New password must be different from the old one.");
            }

            if (!data.getNewPassword().equals(data.getConfirmNewPassword())) {
                txn.rollback();
                return generateResponse(txn, Response.Status.BAD_REQUEST, "Passwords do not match.");
            }

            user = updatePassword(userKey, user, DigestUtils.sha512Hex(data.getNewPassword()));
            txn.update(user);
            txn.commit();
            return generateResponse(txn, Response.Status.OK, "{}");
        } finally {
            if (txn.isActive()) {
                txn.rollback();
            }
        }
    }

    private Entity updatePassword(Key userKey, Entity user, String password) {
        return Entity.newBuilder(userKey)
                .set("Username", user.getString("Username"))
                .set("Email", user.getString("Email"))
                .set("Role", user.getString("Role"))
                .set("Creation Time", user.getTimestamp("Creation Time"))
                .set("Password", password)
                .set("Name", user.getString("Name"))
                .set("Phone Number", user.getString("Phone Number"))
                .set("Occupation", user.getString("Occupation"))
                .set("Working Place", user.getString("Working Place"))
                .set("Address", user.getString("Address"))
                .set("Zip Code", user.getString("Zip Code"))
                .set("NIF", user.getString("NIF"))
                .set("Profile Status", user.getString("Profile Status"))
                .set("Account Status", user.getString("Account Status"))
                .build();
    }
}
