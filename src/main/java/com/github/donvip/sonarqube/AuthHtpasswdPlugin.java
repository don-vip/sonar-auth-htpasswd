// License: GPL. For details, see LICENSE file.
package com.github.donvip.sonarqube;

import java.util.List;

import org.sonar.api.Plugin;

public class AuthHtpasswdPlugin implements Plugin {

    @Override
    public void define(Context context) {
        context.addExtensions(List.of(
                HtpasswdSettings.class, HtpasswdSettings.definitions(),
                HtpasswdSecurityRealm.class,
                HtpasswdIdentityProvider.class));
    }
}
