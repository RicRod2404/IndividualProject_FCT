package pt.unl.fct.di.apdc.firstwebapp.data;

import org.glassfish.jersey.media.multipart.MultiPartFeature;
import org.glassfish.jersey.server.ResourceConfig;

public class MultipartFormData extends ResourceConfig {
    public MultipartFormData() {
        // Register other features, resources, and providers

        // Register MultiPartFeature to enable multipart support
        register(MultiPartFeature.class);
    }
}