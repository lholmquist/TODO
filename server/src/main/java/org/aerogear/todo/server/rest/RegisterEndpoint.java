package org.aerogear.todo.server.rest;

import org.jboss.aerogear.security.auth.AuthenticationManager;
import org.jboss.aerogear.security.authz.IdentityManagement;
import org.jboss.aerogear.security.model.AeroGearUser;

import javax.ejb.Stateless;
import javax.inject.Inject;

@Stateless
public class RegisterEndpoint {

    @Inject
    private IdentityManagement configuration;

    @Inject
    private AuthenticationManager authenticationManager;

    public void index() {
        System.out.println("Login page!");
    }

    public AeroGearUser register(AeroGearUser user) {
        configuration.create(user);
        configuration.grant(user.getRole()).to(user);
        authenticationManager.login(user);
        return user;
    }
}
