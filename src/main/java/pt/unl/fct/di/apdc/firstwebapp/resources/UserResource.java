package pt.unl.fct.di.apdc.firstwebapp.resources;

import com.google.appengine.repackaged.org.apache.commons.codec.digest.DigestUtils;
import com.google.cloud.Timestamp;
import com.google.cloud.datastore.*;
import com.google.gson.Gson;
import org.glassfish.jersey.media.multipart.FormDataParam;
import org.json.JSONArray;
import org.json.JSONObject;
import pt.unl.fct.di.apdc.firstwebapp.data.*;
import pt.unl.fct.di.apdc.firstwebapp.enums.AccStatus;
import pt.unl.fct.di.apdc.firstwebapp.enums.PfStatus;
import pt.unl.fct.di.apdc.firstwebapp.enums.Roles;
import pt.unl.fct.di.apdc.firstwebapp.utils.Constants;
import pt.unl.fct.di.apdc.firstwebapp.utils.TokenValidator;
import pt.unl.fct.di.apdc.firstwebapp.utils.Validator;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import static pt.unl.fct.di.apdc.firstwebapp.utils.Common.validateToken;
import static pt.unl.fct.di.apdc.firstwebapp.utils.ReturnResponse.generateResponse;

@Path("/user")
@Produces(MediaType.APPLICATION_JSON + ";charset=utf-8")
public class UserResource {

    private static final String BUCKET_NAME = "apdc-bucket";
    private final Gson g = new Gson();
    private final Datastore datastore = DatastoreOptions.getDefaultInstance().getService();

    public UserResource() {
    } // nothing to be done here

    //OP1: Create user
    @POST
    @Path("/create")
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    public Response createUser(@FormDataParam("data") String userData) {
        UserData data = new Gson().fromJson(userData, UserData.class);

        Transaction txn = datastore.newTransaction();

        if (!data.validRegistration()) {
            return generateResponse(txn, Status.BAD_REQUEST, "Missing or wrong parameter.");
        }

        if (data.getPfStatus().isEmpty()) {
            data.setPfStatus(PfStatus.PRIVATE.name());
        } else {
            data.setPfStatus(PfStatus.PUBLIC.name());
        }

        if (!data.getZipCode().isEmpty() && !Validator.validZipCode(data.getZipCode())) {
            return generateResponse(txn, Status.BAD_REQUEST, "Invalid zip code.");
        }

        if (!data.getNif().isEmpty() && !Validator.validNIF(data.getNif())) {
            return generateResponse(txn, Status.BAD_REQUEST, "Invalid NIF.");
        }

        if (!Validator.isValidPhoneNr(data.getPhoneNr())) {
            return generateResponse(txn, Status.BAD_REQUEST, "Invalid phone number.");
        }

        try {
            Key userKey = datastore.newKeyFactory().setKind("User").newKey(data.getUsername());
            Entity user = txn.get(userKey);

            if (user != null) {
                txn.rollback();
                return generateResponse(txn, Status.BAD_REQUEST, Constants.USER_USERNAME + data.getUsername() + " already exists.");
            } else {
                // Check if the email is already in use
                Query<Entity> query = Query.newEntityQueryBuilder()
                        .setKind("User")
                        .setFilter(StructuredQuery.PropertyFilter.eq("Email", data.getEmail()))
                        .build();
                QueryResults<Entity> results = datastore.run(query);

                if (results.hasNext()) {
                    txn.rollback();
                    return generateResponse(txn, Status.BAD_REQUEST, "Email " + data.getEmail() + " is already in use.");
                }

                user = Entity.newBuilder(userKey)
                        .set("Username", data.getUsername())
                        .set("Name", data.getName())
                        .set("Password", DigestUtils.sha512Hex(data.getPassword()))
                        .set("Email", data.getEmail())
                        .set("Phone Number", data.getPhoneNr())
                        .set("Role", Roles.USER.name())
                        .set("Profile Status", data.getPfStatus())
                        .set("Occupation", data.getOccupation())
                        .set("Working Place", data.getWorkingPlace())
                        .set("Address", data.getAddress())
                        .set("Zip Code", data.getZipCode())
                        .set("NIF", data.getNif())
                        .set("Account Status", AccStatus.INACTIVE.name())
                        .set("Creation Time", Timestamp.now())
                        .build();
                txn.add(user);
                txn.commit();

                return generateResponse(txn, Status.OK, "{}");
            }
        } finally {
            if (txn.isActive()) {
                txn.rollback();
            }
        }
    }

    //OP4: Delete user
    @DELETE
    @Path("/delete")
    public Response deleteUser(@HeaderParam("Authorization") String auth, DeleteData data) {
        TokenValidator tokendata = validateToken(auth);

        Transaction txn = datastore.newTransaction();
        Key userKey = datastore.newKeyFactory().setKind("User").newKey(data.getUsernameToDelete());
        Entity user = txn.get(userKey);

        try {
            if (user == null) {
                return generateResponse(txn, Status.FORBIDDEN, Constants.USER_USERNAME + data.getUsernameToDelete() + " does not exist.");
            } else {
                if (isValidRoleForDeletion(tokendata.getTokenData(), data, user)) {
                    txn.delete(userKey);
                    txn.commit();
                    return generateResponse(txn, Status.OK, "{}");
                } else {
                    return generateResponse(txn, Status.BAD_REQUEST, "Cannot delete user with username " + data.getUsernameToDelete() + ".");
                }
            }
        } finally {
            if (txn.isActive()) {
                txn.rollback();
            }
        }
    }

    private boolean isValidRoleForDeletion(TokenData tokenData, DeleteData data, Entity user) {
        switch (tokenData.getRole()) {
            case "SU":
                return true;
            case "GA":
                return user.getString("Role").equals(Roles.GBO.name())
                        || user.getString("Role").equals(Roles.USER.name());
            case "USER":
                return data.getUsernameToDelete().equals(user.getString("Username"))
                        && data.getUsernameToDelete().equals(tokenData.getUser());
            case "GBO":
            default:
                return false;
        }
    }

    //OP5: List users
    @POST
    @Path("/list")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response listUsers(@HeaderParam("Authorization") String auth) {
        TokenValidator tokendata = validateToken(auth);

        Transaction txn = datastore.newTransaction();

        try {
            Query<Entity> query = Query.newEntityQueryBuilder().setKind("User").build();
            QueryResults<Entity> users = datastore.run(query);

            JSONArray usersList = new JSONArray();
            users.forEachRemaining(user -> {
                JSONObject userJson = listUsers(user);
                String role = user.getString("Role");
                String pfStatus = user.getString("Profile Status");
                String accStats = user.getString("Account Status");

                if (tokendata.getTokenData().getRole().equals(Roles.USER.name()) && role.equals(Roles.USER.name()) && pfStatus.equals(PfStatus.PUBLIC.name()) && accStats.equals(AccStatus.ACTIVE.name())) {
                    userJson.put("Name", user.getString("Name"));
                    userJson.put("Email", user.getString("Email"));
                    userJson.put("Username", user.getKey().getName());
                    usersList.put(userJson);
                } else if (tokendata.getTokenData().getRole().equals(Roles.GBO.name()) && role.equals(Roles.USER.name())) {
                    usersList.put(userJson);
                } else if (tokendata.getTokenData().getRole().equals(Roles.GA.name()) && (role.equals(Roles.USER.name()) || role.equals(Roles.GA.name()) || role.equals(Roles.GBO.name()))) {
                    usersList.put(userJson);
                } else if (tokendata.getTokenData().getRole().equals(Roles.SU.name())) {
                    usersList.put(userJson);
                }
            });
            txn.commit();
            return Response.ok(g.toJson(usersList.toString())).build();
        } finally {
            if (txn.isActive()) {
                txn.rollback();
            }
        }
    }

    private JSONObject listUsers(Entity user) {
        JSONObject userJson = new JSONObject();
        userJson.put("Name", user.getString("Name"));
        userJson.put("Email", user.getString("Email"));
        userJson.put("Username", user.getKey().getName());
        userJson.put("Role", user.getString("Role"));
        userJson.put("AccountStatus", user.getString("Account Status"));
        userJson.put("ProfileStatus", user.getString("Profile Status"));
        userJson.put("Password", user.getString("Password"));
        userJson.put("PhoneNumber", user.getString("Phone Number"));
        userJson.put("Occupation", user.getString("Occupation"));
        userJson.put("WorkingPlace", user.getString("Working Place"));
        userJson.put("Address", user.getString("Address"));
        userJson.put("ZipCode", user.getString("Zip Code"));
        userJson.put("NIF", user.getString("NIF"));
        userJson.put("CreationTime", user.getTimestamp("Creation Time"));
        return userJson;
    }

    //OP6: Update user
    @PUT
    @Path("/update")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response updateUser(@HeaderParam("Authorization") String auth, UpdateData data) {
        TokenValidator tokendata = validateToken(auth);

        Transaction txn = datastore.newTransaction();

        Key userKey = datastore.newKeyFactory().setKind("User").newKey(tokendata.getTokenData().getUser());
        Entity user = txn.get(userKey);

        Key userKeyToChange = datastore.newKeyFactory().setKind("User").newKey(data.getUsernameToChange());
        Entity userToChange = txn.get(userKeyToChange);

        String role = user.getString("Role");
        String roleToChange = user.getString("Role");

        if (!data.validRegistration()) {
            return generateResponse(txn, Status.BAD_REQUEST, "Missing or wrong parameter.");
        }

        if (data.getPfStatus().isEmpty()) {
            data.setPfStatus(PfStatus.PRIVATE.name());
        } else {
            data.setPfStatus(PfStatus.PUBLIC.name());
        }

        if (!data.getZipCode().isEmpty() && !Validator.validZipCode(data.getZipCode())) {
            return generateResponse(txn, Status.BAD_REQUEST, "Invalid zip code.");
        }

        if (!data.getNif().isEmpty() && !Validator.validNIF(data.getNif())) {
            return generateResponse(txn, Status.BAD_REQUEST, "Invalid NIF.");
        }

        if (!Validator.isValidPhoneNr(data.getPhoneNr())) {
            return generateResponse(txn, Status.BAD_REQUEST, "Invalid phone number.");
        }

        try {
            if (userToChange == null) {
                return generateResponse(txn, Status.BAD_REQUEST, Constants.USER_USERNAME + data.getUsernameToChange() + " does not exist.");
            } else if (isRoleValidForUpdate(tokendata.getTokenData().getUser(), role, roleToChange, data)) {
                userToChange = updateUserAttributes(userKeyToChange, userToChange, data, role);
                txn.update(userToChange);
                txn.commit();
                return generateResponse(txn, Status.OK, "{}");
            } else {
                return generateResponse(txn, Status.BAD_REQUEST, "User with role " + role + " cannot modify user with username " + tokendata.getTokenData().getUser() + ".");
            }
        } finally {
            if (txn.isActive()) {
                txn.rollback();
            }
        }
    }

    //OP11: Get user
    @POST
    @Path("/get")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response getUser(@HeaderParam("Authorization") String auth, GetUserData data) {
        validateToken(auth);

        Transaction txn = datastore.newTransaction();

        try {
            Key userKey = datastore.newKeyFactory().setKind("User").newKey(data.getUsername());
            Entity user = txn.get(userKey);

            if (user == null) {
                return generateResponse(txn, Status.BAD_REQUEST, Constants.USER_USERNAME + data.getUsername() + " does not exist.");
            } else {
                JSONObject userJson = listUsers(user);
                txn.commit();
                return Response.ok(g.toJson(userJson.toString())).build();
            }
        } finally {
            if (txn.isActive()) {
                txn.rollback();
            }
        }
    }

    private boolean isRoleValidForUpdate(String username, String role, String roleToChange, UpdateData data) {
        switch (role) {
            case "USER":
                return username.equals(data.getUsernameToChange());
            case "GBO":
                return roleToChange.equals(Roles.USER.name());
            case "GA":
                return roleToChange.equals(Roles.USER.name()) || roleToChange.equals(Roles.GBO.name());
            case "SU":
                return true;
            default:
                return false;
        }
    }

    private Entity updateUserAttributes(Key userKey, Entity user, UpdateData data, String role) {
        return Entity.newBuilder(userKey)
                .set("Username", user.getString("Username"))
                .set("Name", role.equals(Roles.USER.name()) ? user.getString("Name") : data.getName())
                .set("Password", user.getString("Password"))
                .set("Email", role.equals(Roles.USER.name()) ? user.getString("Email") : data.getEmail())
                .set("Phone Number", data.getPhoneNr())
                .set("Occupation", data.getOccupation())
                .set("Working Place", data.getWorkingPlace())
                .set("Address", data.getAddress())
                .set("Zip Code", data.getZipCode())
                .set("NIF", data.getNif())
                .set("Role", role.equals(Roles.USER.name()) ? user.getString("Role") : data.getRole().name())
                .set("Profile Status", data.getPfStatus())
                .set("Account Status", role.equals(Roles.USER.name()) ? user.getString("Account Status") : data.getAccStatus().name())
                .set("Creation Time", user.getTimestamp("Creation Time"))
                .build();
    }
}