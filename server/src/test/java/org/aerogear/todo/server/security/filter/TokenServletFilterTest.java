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

package org.aerogear.todo.server.security.filter;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.picketlink.authentication.AuthenticationException;
import org.picketlink.extensions.core.pbox.PicketBoxIdentity;

import javax.servlet.FilterChain;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class TokenServletFilterTest {

    public static final String AUTH_TOKEN = "Auth-Token";

    @InjectMocks
    private TokenServletFilter tokenServletFilter;

    @Mock
    private HttpServletRequest servletRequest;

    @Mock
    private HttpServletResponse servletResponse;

    @Mock
    private FilterChain filterChain;

    @Mock
    private PicketBoxIdentity identity;


    @Before
    public void setUp() {
        tokenServletFilter = new TokenServletFilter();
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testIgnoreTokenValidationOnAuthPaths() throws Exception {
        when(servletRequest.getRequestURI()).thenReturn("/mysweetapp/auth/login");
        tokenServletFilter.doFilter(servletRequest, servletResponse, filterChain);
        verify(filterChain).doFilter(any(XSSServletRequestWrapper.class), eq(servletResponse));
    }

    @Test
    public void testTokenValidationOnApplicationPaths() throws AuthenticationException, Exception {
        String token = "bde58803-fc3b-4c9e-b88d-32a9d5d2ce28";

        when(servletRequest.getRequestURI()).thenReturn("/mysweetapp/projects");
        when(servletRequest.getHeader(AUTH_TOKEN)).thenReturn(token);
        when(identity.restoreSession(token)).thenReturn(true);
        tokenServletFilter.doFilter(servletRequest, servletResponse, filterChain);
        verify(filterChain).doFilter(any(XSSServletRequestWrapper.class), eq(servletResponse));
    }

    @Test
    public void testDoNotValidateTokenOnLogin() throws AuthenticationException, Exception {
        String token = null;

        when(servletRequest.getRequestURI()).thenReturn("/mysweetapp/auth/login");
        when(identity.restoreSession(token)).thenReturn(false);
        tokenServletFilter.doFilter(servletRequest, servletResponse, filterChain);
        verify(filterChain).doFilter(any(XSSServletRequestWrapper.class), eq(servletResponse));
    }

    @Test
    public void testInvalidTokenOnApplicationPaths() throws AuthenticationException, Exception {
        String token = "guestguestguestguestguest";

        when(servletRequest.getRequestURI()).thenReturn("/mysweetapp/projects");
        when(servletRequest.getHeader(AUTH_TOKEN)).thenReturn(token);
        when(identity.restoreSession(token)).thenReturn(false);
        tokenServletFilter.doFilter(servletRequest, servletResponse, filterChain);
        verify(servletResponse).setStatus(HttpServletResponse.SC_UNAUTHORIZED);
    }

    @Test
    public void testEmptyTokenOnApplicationPaths() throws AuthenticationException, Exception {
        String token = "";

        when(servletRequest.getRequestURI()).thenReturn("/mysweetapp/projects");
        when(servletRequest.getHeader(AUTH_TOKEN)).thenReturn(token);
        when(identity.restoreSession(token)).thenReturn(true);
        tokenServletFilter.doFilter(servletRequest, servletResponse, filterChain);
        verify(servletResponse).setStatus(HttpServletResponse.SC_UNAUTHORIZED);
    }

    @Test
    public void testInvalidTokenOnLogout() throws AuthenticationException, Exception {
        String token = null;

        when(servletRequest.getRequestURI()).thenReturn("/mysweetapp/auth/logout");
        when(servletRequest.getHeader(AUTH_TOKEN)).thenReturn(token);
        tokenServletFilter.doFilter(servletRequest, servletResponse, filterChain);
        verify(servletResponse).setStatus(HttpServletResponse.SC_UNAUTHORIZED);
    }
}
