package io.openliberty.propagate;

import java.io.IOException;
import java.util.List;
import java.util.Map.Entry;

import javax.ws.rs.client.ClientRequestContext;
import javax.ws.rs.client.ClientRequestFilter;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.core.MultivaluedHashMap;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.Provider;

@Provider
public class HeaderPropagationFilter implements ContainerRequestFilter, ClientRequestFilter {

    private static ThreadLocal<MultivaluedMap<String,Object>> headersMap = new ThreadLocal<>();
    private final String[] headersToPropagate;
    
    public HeaderPropagationFilter() {
        headersToPropagate = System.getProperty("io.openliberty.propagate.headersToPropagate", "MyHeader").split(",");
    }

    @Override
    public void filter(ContainerRequestContext reqContext) throws IOException {
        // invoked on incoming request to JAX-RS resource
        // save off the headers we are interested in into a thread local
        MultivaluedMap<String,String> headersFromRequest = reqContext.getHeaders();
        MultivaluedMap<String,Object> headerMapToSend = new MultivaluedHashMap<>();
        for (String header : headersToPropagate) {
            for (String value : headersFromRequest.get(header)) {
                headerMapToSend.add(header, value);
            }
        }
        headersMap.set(headerMapToSend);
    }

    @Override
    public void filter(ClientRequestContext reqContext) throws IOException {
        MultivaluedMap<String,Object> headersToSend = headersMap.get();
        if (headersToSend != null && !headersToSend.isEmpty()) {
            MultivaluedMap<String,Object> actualHeaders = reqContext.getHeaders();
            for (Entry<String, List<Object>> entry : headersToSend.entrySet()) {
                actualHeaders.addAll(entry.getKey(), entry.getValue());
            }
        }
        headersMap.remove();
    }
}
