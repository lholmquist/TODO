/*
 * JBoss, Home of Professional Open Source
 * Copyright Red Hat Inc., and individual contributors
 * by the @authors tag. See the copyright.txt in the distribution for a
 * full listing of individual contributors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.aerogear.todo.server.filter;

import org.picketlink.Identity;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.logging.Logger;

@ApplicationScoped
public class AuthzServletFilter implements Filter {

    private static final String AUTH_PATH = "/auth/";
    private static final String AUTH_TOKEN = "Auth-Token";

    private static final Logger LOGGER = Logger.getLogger(AuthzServletFilter.class.getSimpleName());

    private FilterConfig config;

    @Inject
    private Identity identity;

    @Override
    public void init(FilterConfig config) throws ServletException {
        this.config = config;
        //TODO to be implemented
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest httpServletRequest = (HttpServletRequest) servletRequest;
        HttpServletResponse httpServletResponse = (HttpServletResponse) servletResponse;

        String path = httpServletRequest.getRequestURI();
        String token = httpServletRequest.getHeader(AUTH_TOKEN);

        LOGGER.info("==================================================================");

        LOGGER.info("Is logged in? " + identity.isLoggedIn());
        LOGGER.info("==================================================================");


        if (!identity.isLoggedIn() && (!path.contains(AUTH_PATH))) {
            LOGGER.info("==================================================================");
            LOGGER.info("NON AUTHORIZED");
            httpServletResponse.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        } else {
            filterChain.doFilter(httpServletRequest, servletResponse);
        }


    }

    //TODO maybe provide a class for it don't hurt
    private boolean tokenIsValid(String token) {

        boolean valid = false;

        if (token != null && !token.isEmpty()) {
            try {
                valid = identity.isLoggedIn();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return valid;
    }

    @Override
    public void destroy() {
        //TODO to be implemented
    }
}