/*
 * JBoss, Home of Professional Open Source
 * Copyright 2012, Red Hat, Inc., and individual contributors
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

package org.aerogear.todo.server.security.filter;

import org.jboss.picketlink.cdi.authentication.AuthenticationException;
import org.picketbox.cdi.PicketBoxIdentity;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebFilter(filterName = "TokenServletFilter", urlPatterns = "/*")
@ApplicationScoped
public class TokenServletFilter implements Filter {

    private static final String AUTH_PATH = "/auth/";
    private static final String AUTH_TOKEN = "Auth-Token";
    private static final String LOGOUT_PATH = "/auth/logout";

    private FilterConfig config;

    @Inject
    private PicketBoxIdentity identity;

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

        if (!tokenIsValid(token) && (path.contains(LOGOUT_PATH) || !path.contains(AUTH_PATH))) {
            httpServletResponse.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        } else {
            filterChain.doFilter(new XSSServletRequestWrapper(httpServletRequest), servletResponse);
        }


    }

    //TODO maybe provide a class for it don't hurt
    private boolean tokenIsValid(String token) {

        boolean valid = false;

        if (token != null && !token.isEmpty()) {
            try {
                valid = identity.restoreSession(token);
            } catch (AuthenticationException e) {
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
