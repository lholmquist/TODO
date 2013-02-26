package org.aerogear.todo.server.config;

import org.jboss.aerogear.controller.router.MediaType;
import org.jboss.aerogear.controller.router.RouteContext;
import org.jboss.aerogear.controller.router.rest.AbstractRestResponder;
import org.jboss.aerogear.security.auth.Token;

import javax.enterprise.inject.Instance;
import javax.inject.Inject;

public class CustomMediaTypeResponder extends AbstractRestResponder {

    public static final MediaType CUSTOM_MEDIA_TYPE = MediaType.JSON;

    @Inject
    @Token
    private Instance<String> token;
    
    /**
     * Sole constructor.
     */
    public CustomMediaTypeResponder() {
        super(CUSTOM_MEDIA_TYPE);
    }

    @Override
    public void writeResponse(Object entity, RouteContext routeContext) throws Exception {
        if (token != null && token.get() != null) {
            routeContext.getResponse().setHeader("Auth-Token", token.get().toString());
        }
    }
}
