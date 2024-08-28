package pt.unl.fct.di.apdc.firstwebapp.resources;

import com.google.cloud.datastore.*;
import pt.unl.fct.di.apdc.firstwebapp.data.RoleData;
import pt.unl.fct.di.apdc.firstwebapp.enums.Roles;
import pt.unl.fct.di.apdc.firstwebapp.utils.Constants;
import pt.unl.fct.di.apdc.firstwebapp.utils.TokenValidator;

import javax.ws.rs.HeaderParam;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import static pt.unl.fct.di.apdc.firstwebapp.utils.Common.validateToken;
import static pt.unl.fct.di.apdc.firstwebapp.utils.ReturnResponse.generateResponse;

@Path("/role")
@Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
public class RoleResource {
    private final Datastore datastore = DatastoreOptions.getDefaultInstance().getService();

    public RoleResource() {
    } // nothing to be done here

    //OP2: Change user role
    @PUT
    @Path("/change")
    public Response changeRole(@HeaderParam("Authorization") String auth, RoleData data) {
        TokenValidator tokendata = validateToken(auth);

        Transaction txn = datastore.newTransaction();

        try {
            Key userKey = datastore.newKeyFactory().setKind("User").newKey(data.getUsername());
            Entity user = txn.get(userKey);

            if (user == null) {
                txn.rollback();
                return generateResponse(txn, Response.Status.BAD_REQUEST, Constants.USER_USERNAME + data.getUsername() + " does not exist.");
            } else {
                String newRole = determineNewRole(tokendata.getTokenData().getRole(), data, user);
                if (newRole == null) {
                    txn.rollback();
                    return generateResponse(txn, Response.Status.BAD_REQUEST, "Cannot change role of user with username " + data.getUsername() + " to " + tokendata.getTokenData().getRole() + ".");
                }
                user = updateRole(userKey, user, newRole);
                txn.update(user);
                txn.commit();
                return generateResponse(txn, Response.Status.OK, "{}");
            }
        } finally {
            if (txn.isActive()) {
                txn.rollback();
            }
        }
    }

    private String determineNewRole(String role, RoleData data, Entity user) {
        switch (role.toUpperCase().trim()) {
            case "GA":
                if (user.getString("Role").equals(Roles.GBO.name())) {
                    return Roles.USER.name();
                } else if (user.getString("Role").equals(Roles.USER.name())) {
                    return Roles.GBO.name();
                }
                break;
            case "SU":
                return data.getRoleToChangeTo().name();
            case "GBO":
            case "USER":
            default:
                return null;
        }
        return null;
    }

    private Entity updateRole(Key userKey, Entity user, String role) {
        return Entity.newBuilder(userKey)
                .set("Username", user.getString("Username"))
                .set("Email", user.getString("Email"))
                .set("Role", role)
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
                .set("Account Status", user.getString("Account Status"))
                .build();
    }
}
