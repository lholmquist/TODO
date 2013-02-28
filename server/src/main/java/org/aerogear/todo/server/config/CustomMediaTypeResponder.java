package org.aerogear.todo.server.config;

import java.util.Map.Entry;
import java.util.Set;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;

import org.aerogear.todo.server.rest.ResponseHeaders;
import org.jboss.aerogear.controller.router.MediaType;
import org.jboss.aerogear.controller.router.RouteContext;
import org.jboss.aerogear.controller.router.rest.JsonResponder;

@ApplicationScoped
public class CustomMediaTypeResponder extends JsonResponder {

    public static final MediaType CUSTOM_MEDIA_TYPE = new MediaType(MediaType.JSON.getType(), CustomMediaTypeResponder.class);
    private ResponseHeaders responseHeaders;
    
    @Override
    public MediaType getMediaType() {
        return CUSTOM_MEDIA_TYPE;
    }

    public void loggedInHeaders(@Observes ResponseHeaders headers) { 
        responseHeaders = headers;
    }
    
    @Override
    public void writeResponse(Object entity, RouteContext routeContext) throws Exception {
        if (responseHeaders != null && responseHeaders.getHeaders() != null) {
            Set<Entry<String, String>> entrySet = responseHeaders.getHeaders().entrySet();
            for (Entry<String, String> entry : entrySet) {
                routeContext.getResponse().setHeader(entry.getKey(), entry.getValue());
            }
        }
        super.writeResponse(entity, routeContext);
    }

}
