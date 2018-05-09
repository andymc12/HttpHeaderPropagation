package io.openliberty.propagate;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.core.Response;

import org.eclipse.microprofile.rest.client.annotation.RegisterProvider;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

@Path("/2")
@RegisterProvider(HeaderPropagationFilter.class)
@RegisterRestClient
public interface MyRestClient {

    @GET
    Response get();
}
