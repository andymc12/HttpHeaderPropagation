package io.openliberty.propagate;

import java.util.List;
import java.util.Map.Entry;

import javax.inject.Inject;
import javax.ws.rs.ApplicationPath;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.core.Application;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;

import org.eclipse.microprofile.rest.client.inject.RestClient;

@ApplicationPath("/test")
@Path("/")
public class MyResource extends Application {

    @Inject
    @RestClient
    MyRestClient client;
    
    @GET
    @Path("/1")
    public Response initialEntry() {
        return client.get();
    }
    
    @GET
    @Path("/2")
    public Response resourceForRestClient(@Context HttpHeaders httpHeaders) {
        MultivaluedMap<String,String> headers = httpHeaders.getRequestHeaders();
        System.out.println("Headers sent from MP Rest Client:");
        for (String headerName : headers.keySet()) {
            System.out.println("  " + headerName + " : " + headers.getFirst(headerName));
        }
        
        String returnValue = headers.getFirst("MyHeader");
        if (returnValue != null) {
            return Response.ok(returnValue).build();
        }
        return Response.noContent().build();
        
    }
}
