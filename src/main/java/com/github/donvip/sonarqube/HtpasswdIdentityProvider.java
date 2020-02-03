// License: GPL. For details, see LICENSE file.
package com.github.donvip.sonarqube;

import javax.servlet.http.HttpServletRequest;

import org.sonar.api.server.ServerSide;
import org.sonar.api.server.authentication.BaseIdentityProvider;
import org.sonar.api.server.authentication.Display;
import org.sonar.api.server.authentication.Display.Builder;
import org.sonar.api.server.authentication.UserIdentity;
import org.sonar.api.utils.log.Logger;
import org.sonar.api.utils.log.Loggers;

import com.identity4j.connector.htpasswd.HTPasswdConnector;
import com.identity4j.util.crypt.EncoderManager;
import com.identity4j.util.crypt.impl.DefaultEncoderManager;
import com.identity4j.util.crypt.impl.UnixDESEncoder;

@ServerSide
public class HtpasswdIdentityProvider implements BaseIdentityProvider {

    private static final Logger LOGGER = Loggers.get(HtpasswdIdentityProvider.class);

    static {
        try {
            // Fix the UNIX DES encoder
            // Workaround to https://github.com/nervepoint/identity4j/pull/3
            Class.forName(HTPasswdConnector.class.getName());
            EncoderManager manager = DefaultEncoderManager.getInstance();
            manager.removeEncoder(manager.getEncoderById(UnixDESEncoder.ID));
            manager.addEncoder(new UnixDESEncoderFix());
        } catch (ClassNotFoundException ex) {
            LOGGER.error(ex.getMessage(), ex);
        }
    }

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
