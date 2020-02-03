// License: GPL. For details, see LICENSE file.
package com.github.donvip.sonarqube;

import static java.lang.String.valueOf;
import static org.sonar.api.PropertyType.BOOLEAN;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.sonar.api.config.Configuration;
import org.sonar.api.config.PropertyDefinition;
import org.sonar.api.server.ServerSide;

@ServerSide
public class HtpasswdSettings {
    protected static final String FILE = "sonar.auth.htpasswd.file";
    protected static final String GROUP_FILE = "sonar.auth.htpasswd.group.file";
    protected static final String ENABLED = "sonar.auth.htpasswd.enabled";
    protected static final String ALLOW_USERS_TO_SIGN_UP = "sonar.auth.htpasswd.allowUsersToSignUp";
    protected static final String NAME = "sonar.auth.htpasswd.name";
    protected static final String ICON = "sonar.auth.htpasswd.icon";
    protected static final String BACKGROUND_COLOR = "sonar.auth.htpasswd.background.color";

    protected static final String CATEGORY = "Htpasswd Authentication";

    private final Configuration config;

    public HtpasswdSettings(Configuration config) {
        this.config = config;
    }

    public static List<PropertyDefinition> definitions() {
        return Arrays.asList(
                PropertyDefinition.builder(ENABLED)
                    .name("Enabled")
                    .description("Enable htpasswd users to login. Value is ignored if htpasswd file path is not defined.")
                    .category(CATEGORY)
                    .type(BOOLEAN)
                    .defaultValue(valueOf(false))
                    .index(1)
                    .build(),
                PropertyDefinition.builder(FILE)
                    .name("htpasswd File")
                    .description("Path to htpasswd file used for authentication.")
                    .category(CATEGORY)
                    .index(2)
                    .build(),
                PropertyDefinition.builder(GROUP_FILE)
                    .name("htpasswd Group File")
                    .description("Path to htpasswd group file used for authentication.")
                    .category(CATEGORY)
                    .index(3)
                    .build(),
                PropertyDefinition.builder(ALLOW_USERS_TO_SIGN_UP)
                    .name("Allow users to sign-up")
                    .description("Allow new users to authenticate. When set to 'false', only existing users will be able to authenticate to the server.")
                    .category(CATEGORY)
                    .type(BOOLEAN)
                    .defaultValue(valueOf(true))
                    .index(4)
                    .build(),
                PropertyDefinition.builder(NAME)
                    .name("Name displayed in login form")
                    .description("Name displayed in login form. Must not be blank.")
                    .category(CATEGORY)
                    .index(5)
                    .build(),
                PropertyDefinition.builder(ICON)
                    .name("Icon displayed in login form")
                    .description("URL path to the icon displayed in login form. It can be an external URL. The recommended format is SVG with a size of 24x24 pixels, Other supported format is PNG, with a size of 40x40 pixels.")
                    .category(CATEGORY)
                    .index(6)
                    .build(),
                PropertyDefinition.builder(BACKGROUND_COLOR)
                    .name("Background color for the button displayed in the login form")
                    .description("Background color for the button displayed in the login form. It's a Hexadecimal value, for instance #205081. If not provided, the default value is #236a97")
                    .category(CATEGORY)
                    .index(7)
                    .build()
               );
    }

    public boolean isEnabled() {
        return config.getBoolean(ENABLED).orElse(Boolean.FALSE) && htpasswdFile().isPresent();
    }

    public Optional<String> htpasswdFile() {
        return config.get(FILE);
    }

    public Optional<String> htpasswdGroupFile() {
        return config.get(GROUP_FILE);
    }

    public boolean allowUsersToSignUp() {
        return config.getBoolean(ALLOW_USERS_TO_SIGN_UP).orElse(Boolean.TRUE);
    }

    public Optional<String> name() {
        return config.get(NAME);
    }

    public Optional<String> icon() {
        return config.get(ICON);
    }

    public Optional<String> backgroundColor() {
        return config.get(BACKGROUND_COLOR);
    }
}
