package org.aerogear.todo.server.config;

import org.jboss.aerogear.controller.router.MediaType;
import org.jboss.aerogear.controller.router.RouteContext;
import org.jboss.aerogear.controller.router.rest.AbstractRestResponder;

public class CustomMediaTypeResponder extends AbstractRestResponder {

    public static final MediaType CUSTOM_MEDIA_TYPE = MediaType.JSON;

    /**
     * Sole constructor that subclasses should call from tier no-args constructor
     *
     * @param mediaType the media type that this responder accepts.
     */
    public CustomMediaTypeResponder(MediaType mediaType) {
        super(CUSTOM_MEDIA_TYPE);
    }

    @Override
    public void writeResponse(Object entity, RouteContext routeContext) throws Exception {
        routeContext.getResponse().setHeader("Homer", "Simpson");
    }
}
