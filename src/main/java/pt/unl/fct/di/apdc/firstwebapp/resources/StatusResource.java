package pt.unl.fct.di.apdc.firstwebapp.resources;

import com.google.cloud.datastore.*;
import pt.unl.fct.di.apdc.firstwebapp.data.AccStatusData;
import pt.unl.fct.di.apdc.firstwebapp.enums.AccStatus;
import pt.unl.fct.di.apdc.firstwebapp.enums.Roles;
import pt.unl.fct.di.apdc.firstwebapp.utils.Constants;
import pt.unl.fct.di.apdc.firstwebapp.utils.TokenValidator;

import javax.ws.rs.HeaderParam;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.core.Response;

import static pt.unl.fct.di.apdc.firstwebapp.utils.Common.validateToken;
import static pt.unl.fct.di.apdc.firstwebapp.utils.ReturnResponse.generateResponse;

@Path("/status")
public class StatusResource {
    private final Datastore datastore = DatastoreOptions.getDefaultInstance().getService();

    public StatusResource() {
    }

    //OP3: Change account status
    @PUT
    @Path("/change")
    public Response changeStatus(@HeaderParam("Authorization") String auth, AccStatusData data) {
        TokenValidator tokendata = validateToken(auth);

        Transaction txn = datastore.newTransaction();

        try {
            Key userKey = datastore.newKeyFactory().setKind("User").newKey(data.getUsername());
            Entity user = txn.get(userKey);

            if (user == null) {
                txn.rollback();
                return generateResponse(txn, Response.Status.BAD_REQUEST, Constants.USER_USERNAME + data.getUsername() + " does not exist.");
            } else {

                if (isValidRoleChange(tokendata.getTokenData().getRole(), data, user)) {

                    if (data.getAccStatus().equals(AccStatus.ACTIVE)) {
                        user = updateAccStatus(userKey, user, AccStatus.ACTIVE);
                    } else {
                        user = updateAccStatus(userKey, user, AccStatus.INACTIVE);
                    }

                    txn.update(user);
                    txn.commit();
                    return generateResponse(txn, Response.Status.OK, "{}");
                } else {
                    txn.rollback();
                    return generateResponse(txn, Response.Status.BAD_REQUEST, "Cannot change status of user with username " + data.getUsername() + " to " + data.getAccStatus() + ".");
                }
            }
        } finally {
            if (txn.isActive()) {
                txn.rollback();
            }
        }
    }

    private boolean isValidRoleChange(String role, AccStatusData data, Entity user) {
        switch (role) {
            case "SU":
                return true;
            case "GA":
                return user.getString("Role").equals(Roles.GBO.name()) || user.getString("Role").equals(Roles.USER.name());
            case "GBO":
                return user.getString("Role").equals(Roles.USER.name());
            default:
                return false;
        }
    }

    private Entity updateAccStatus(Key userKey, Entity user, AccStatus accStatus) {
        return Entity.newBuilder(userKey)
                .set("Username", user.getString("Username"))
                .set("Email", user.getString("Email"))
                .set("Role", user.getString("Role"))
                .set("Creation Time", user.getTimestamp("Creation Time"))
                .set("Password", user.getString("Password"))
                .set("Name", user.getString("Name"))
                .set("Phone Number", user.getString("Phone Number"))
                .set("Occupation", user.getString("Occupation"))
                .set("Working Place", user.getString("Working Place"))
                .set("Address", user.getString("Address"))
                .set("Zip Code", user.getString("Zip Code"))
                .set("NIF", user.getString("NIF"))
                .set("Profile Status", user.getString("Profile Status"))
                .set("Account Status", accStatus.name())
                .build();
    }
}
