package org.aerogear.todo.server.http;

import org.apache.commons.io.IOUtils;
import org.jboss.aerogear.security.auth.Token;
import org.picketlink.Identity;
import org.picketlink.credential.DefaultLoginCredentials;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Instance;
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
public class AuthzFilter implements Filter {

    private static final Logger LOGGER = Logger.getLogger(AuthzFilter.class.getSimpleName());

    @Inject
    Instance<Identity> identityInstance;

    @Inject
    Instance<DefaultLoginCredentials> credentials;

    @Inject
    @Token
    Instance<String> token;

    @Override
    public void init(FilterConfig config) throws ServletException {
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain chain) throws IOException,
            ServletException {
        if (!HttpServletRequest.class.isInstance(servletRequest)) {
            throw new ServletException("This filter can only process HttpServletRequest requests.");
        }

        final HttpServletRequest request = (HttpServletRequest) servletRequest;
        final HttpServletResponse response = (HttpServletResponse) servletResponse;

        // Force session creation
        request.getSession();

        Identity identity = null;

        try {
            identity = identityInstance.get();
        } catch (Exception e) {
            throw new ServletException("Identity not found - please ensure that the Identity component is created on startup.",
                    e);
        }

        DefaultLoginCredentials creds = null;

        try {
            creds = credentials.get();
        } catch (Exception e) {
            throw new ServletException(
                    "DefaultLoginCredentials not found - please ensure that the DefaultLoginCredentials component is created on startup.",
                    e);
        }

        String test = IOUtils.toString(request.getInputStream());

        LOGGER.info("=============================================");
        LOGGER.info(test);
        LOGGER.info("=============================================");

        chain.doFilter(request, response);
    }

    @Override
    public void destroy() {
        // TODO Auto-generated method stub
    }
}
