
package com.coates.keycloakdemo.userspi;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.keycloak.Config;
import org.keycloak.component.ComponentModel;
import org.keycloak.models.KeycloakSession;
import org.keycloak.storage.UserStorageProviderFactory;

public class PropertyFileUserStorageProviderFactory
		implements UserStorageProviderFactory<PropertyFileUserStorageProvider>
{
	public static final String PROVIDER_NAME = "readonly-property-file";
	private static final Logger logger =
			Logger.getLogger(PropertyFileUserStorageProviderFactory.class.getName());
	protected Properties properties = new Properties();

	@Override
	public void init(Config.Scope config) {
		InputStream is = getClass().getClassLoader().getResourceAsStream("/userdata.properties");

		if (is == null) {
			logger.log(Level.SEVERE, "Could not find users.properties in classpath");
		} else {
			try {
				properties.load(is);
			} catch (IOException ex) {
				logger.log(Level.SEVERE, "Failed to load users.properties file", ex);
			}
		}
	}

	@Override
	public String getId() {
		return PROVIDER_NAME;
	}

	@Override
	public PropertyFileUserStorageProvider create(KeycloakSession session, ComponentModel model) {
		return new PropertyFileUserStorageProvider(session, model, properties);
	}
}
