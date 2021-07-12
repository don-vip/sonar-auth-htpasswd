package com.github.donvip.sonarqube;

import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;
import java.util.Properties;

import org.sonar.api.security.Authenticator;
import org.sonar.api.security.ExternalGroupsProvider;
import org.sonar.api.security.ExternalUsersProvider;
import org.sonar.api.security.SecurityRealm;
import org.sonar.api.security.UserDetails;
import org.sonar.api.server.ServerSide;

import com.identity4j.connector.ConnectorBuilder;
import com.identity4j.connector.exception.ConnectorException;
import com.identity4j.connector.flatfile.FlatFileConfiguration;
import com.identity4j.connector.htpasswd.HTPasswdConnector;
import com.identity4j.connector.principal.Identity;
import com.identity4j.util.MultiMap;

@ServerSide
public class HtpasswdSecurityRealm extends SecurityRealm {

    private String htpasswdLocation;
    private String htgroupsLocation;

    private HTPasswdConnector htPasswdConnector;
    private CachedHtFile<HtGroupFile> cachedHtGroupsFile;

    private final HtpasswdSettings settings;

    public HtpasswdSecurityRealm(HtpasswdSettings settings) {
        this.settings = settings;
    }

    @Override
    public String getName() {
        return "htpasswd";
    }

    @Override
    public void init() {
        try {
            htpasswdLocation = settings.htpasswdFile()
                    .orElseThrow(() -> new IllegalStateException(HtpasswdSettings.FILE + " not set"));
            htgroupsLocation = settings.htpasswdGroupFile()
                    .orElseThrow(() -> new IllegalStateException(HtpasswdSettings.GROUP_FILE + " not set"));
            getHTPasswdConnector();
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }

    private HTPasswdConnector getHTPasswdConnector() throws IOException {
        if (htPasswdConnector == null) {
            htPasswdConnector = new HTPasswdConnector();
            try (InputStream inputStream = getClass().getResourceAsStream("/htpasswd-connector.properties")) {
                Properties properties = new Properties();
                properties.load(inputStream);
                properties.put(FlatFileConfiguration.KEY_FILENAME, htpasswdLocation);
                htPasswdConnector.open(new ConnectorBuilder().buildConfiguration(MultiMap.toMultiMap(properties)));
            }
        }
        return htPasswdConnector;
    }

    private HtGroupFile getHtGroupFile() throws IOException, ReflectiveOperationException {
        if (cachedHtGroupsFile == null) {
            cachedHtGroupsFile = new CachedHtFile<>(this.htgroupsLocation, HtGroupFile.class);
        }
        return cachedHtGroupsFile.get();
    }

    @Override
    public Authenticator doGetAuthenticator() {
        return new Authenticator() {
            @Override
            public boolean doAuthenticate(Context context) {
                try {
                    return getHTPasswdConnector().checkCredentials(context.getUsername(), context.getPassword().toCharArray());
                } catch (ConnectorException | IOException e) {
                    throw new IllegalArgumentException(e);
                }
            }
        };
    }

    @Override
    public ExternalUsersProvider getUsersProvider() {
        return new ExternalUsersProvider() {
            @Override
            public UserDetails doGetUserDetails(org.sonar.api.security.ExternalUsersProvider.Context context) {
                try {
                    Identity identity = getHTPasswdConnector().getIdentityByName(context.getUsername());
                    if (identity != null) {
                        UserDetails userDetails = new UserDetails();
                        userDetails.setUserId(context.getUsername());
                        // TODO: e-mail support
                        userDetails.setName(identity.getFullName());
                        return userDetails;
                    }
                    return null;
                } catch (IOException e) {
                    throw new IllegalArgumentException(e);
                }
            }
        };
    }

    @Override
    public ExternalGroupsProvider getGroupsProvider() {
        return new ExternalGroupsProvider() {
            @Override
            public Collection<String> doGetGroups(org.sonar.api.security.ExternalGroupsProvider.Context context) {
                try {
                    return getHtGroupFile().getGroups(context.getUsername());
                } catch (IOException | ReflectiveOperationException e) {
                    throw new IllegalArgumentException(e);
                }
            }
        };
    }
}
