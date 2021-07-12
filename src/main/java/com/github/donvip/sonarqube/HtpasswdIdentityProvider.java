// License: GPL. For details, see LICENSE file.
package com.github.donvip.sonarqube;

import javax.servlet.http.HttpServletRequest;

import org.sonar.api.server.ServerSide;
import org.sonar.api.server.authentication.BaseIdentityProvider;
import org.sonar.api.server.authentication.Display;
import org.sonar.api.server.authentication.Display.Builder;
import org.sonar.api.server.authentication.UserIdentity;

@ServerSide
public class HtpasswdIdentityProvider implements BaseIdentityProvider {

    private final HtpasswdSettings settings;

    public HtpasswdIdentityProvider(HtpasswdSettings settings) {
        this.settings = settings;
    }

    @Override
    public String getKey() {
        return "htpasswd";
    }

    @Override
    public String getName() {
        return settings.name().orElse("htpasswd Authentication");
    }

    @Override
    public Display getDisplay() {
        Builder builder = Display.builder();
        builder.setIconPath(settings.icon().orElse("/static/authhtpasswd/htpasswd.svg"));
        settings.backgroundColor().ifPresent(builder::setBackgroundColor);
        return builder.build();
    }

    @Override
    public boolean isEnabled() {
        return settings.isEnabled();
    }

    @Override
    public boolean allowsUsersToSignUp() {
        return settings.allowUsersToSignUp();
    }

    @Override
    public void init(Context context) {
        HttpServletRequest request = context.getRequest();
        request.getParameter("login");
        request.getParameter("password");
        UserIdentity userIdentity = UserIdentity.builder().build();
        context.authenticate(userIdentity);
    }
}
