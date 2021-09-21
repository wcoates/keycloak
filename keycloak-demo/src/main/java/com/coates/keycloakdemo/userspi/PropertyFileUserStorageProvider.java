
package com.coates.keycloakdemo.userspi;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import org.keycloak.component.ComponentModel;
import org.keycloak.credential.CredentialInput;
import org.keycloak.credential.CredentialInputUpdater;
import org.keycloak.credential.CredentialInputValidator;
import org.keycloak.credential.CredentialModel;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.RealmModel;
import org.keycloak.models.UserCredentialModel;
import org.keycloak.models.UserModel;
import org.keycloak.storage.ReadOnlyException;
import org.keycloak.storage.StorageId;
import org.keycloak.storage.UserStorageProvider;
import org.keycloak.storage.adapter.AbstractUserAdapter;
import org.keycloak.storage.user.UserLookupProvider;

public class PropertyFileUserStorageProvider implements UserStorageProvider, UserLookupProvider,
		CredentialInputValidator, CredentialInputUpdater
{
	protected KeycloakSession session;
	protected Properties properties;
	protected ComponentModel model;
	protected Map<String, UserModel> loadedUsers = new HashMap<>();

	public PropertyFileUserStorageProvider(KeycloakSession session, ComponentModel model,
			Properties properties)
	{
		this.session = session;
		this.model = model;
		this.properties = properties;
	}

	@Override
	public void close() {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean supportsCredentialType(String credentialType) {
		return credentialType.equals(CredentialModel.PASSWORD);
	}

	@Override
	public boolean isConfiguredFor(RealmModel realm, UserModel user, String credentialType) {
		String password = properties.getProperty(user.getUsername());
		return credentialType.equals(CredentialModel.PASSWORD) && password != null;
	}

	@Override
	public boolean isValid(RealmModel realm, UserModel user, CredentialInput credentialInput) {
		if (
			!supportsCredentialType(credentialInput.getType())
					|| !(credentialInput instanceof UserCredentialModel)
		) return false;

		UserCredentialModel cred = (UserCredentialModel) credentialInput;
		String password = properties.getProperty(user.getUsername());
		if (password == null) return false;
		return password.equals(cred.getValue());
	}

	@Override
	public UserModel getUserById(String id, RealmModel realm) {
		StorageId storageId = new StorageId(id);
		String username = storageId.getExternalId();
		return getUserByUsername(username, realm);
	}

	@Override
	public UserModel getUserByUsername(String username, RealmModel realm) {
		UserModel adapter = loadedUsers.get(username);
		if (adapter == null) {
			String password = properties.getProperty(username);
			if (password != null) {
				adapter = createAdapter(realm, username);
				loadedUsers.put(username, adapter);
			}
		}
		return adapter;
	}

	@Override
	public UserModel getUserByEmail(String email, RealmModel realm) {
		// TODO Auto-generated method stub
		return null;
	}

	// For making passwords read-only
	@Override
	public boolean updateCredential(RealmModel realm, UserModel user, CredentialInput input) {
		if (
			input.getType().equals(CredentialModel.PASSWORD)
		) throw new ReadOnlyException("user is read only for this update");

		return false;
	}

	// For making passwords read-only
	@Override
	public void disableCredentialType(RealmModel realm, UserModel user, String credentialType) {

	}

	// For making passwords read-only
	@Override
	public Set<String> getDisableableCredentialTypes(RealmModel realm, UserModel user) {
		return Collections.emptySet();
	}

	// For making passwords read-only
	protected UserModel createAdapter(RealmModel realm, String username) {
		return new AbstractUserAdapter(session, realm, model) {
			@Override
			public String getUsername() {
				return username;
			}
		};
	}
}
