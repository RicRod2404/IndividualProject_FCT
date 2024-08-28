package pt.unl.fct.di.apdc.firstwebapp.utils;

import com.google.cloud.datastore.Transaction;

import javax.ws.rs.core.Response;

public class ReturnResponse {

    public ReturnResponse() {
    }

    public static Response generateResponse(Transaction txn, Response.Status status, String entity) {
        if (status != Response.Status.OK) {
            txn.rollback();
        }
        return Response.status(status).entity(entity).build();
    }
}