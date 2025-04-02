DROP TABLE IF EXISTS public.oauth2_registered_client;
DROP TABLE IF EXISTS public.oauth2_authorization_consent;
DROP TABLE IF EXISTS public.oauth2_authorization;
DROP TABLE IF EXISTS public.users;

CREATE TABLE IF NOT EXISTS public.oauth2_registered_client (
	id varchar(100) NOT NULL,
	client_id varchar(100) NOT NULL,
	client_id_issued_at timestamp without time zone DEFAULT CURRENT_TIMESTAMP NOT NULL,
	client_secret varchar(200) DEFAULT NULL,
	client_secret_expires_at timestamp without time zone DEFAULT NULL,
	client_name varchar(200) NOT NULL,
	client_authentication_methods varchar(1000) NOT NULL,
	authorization_grant_types varchar(1000) NOT NULL,
	redirect_uris varchar(1000) DEFAULT NULL,
	post_logout_redirect_uris varchar(1000) DEFAULT NULL,
	scopes varchar(1000) NOT NULL,
	client_settings varchar(2000) NOT NULL,
	token_settings varchar(2000) NOT NULL,
	CONSTRAINT oauth2_registered_client_pkey PRIMARY KEY (id)
);

CREATE TABLE IF NOT EXISTS oauth2_authorization_consent (
	registered_client_id varchar(100) NOT NULL,
	principal_name varchar(200) NOT NULL,
	authorities varchar(1000) NOT NULL,
	CONSTRAINT oauth2_authorization_consent_pkey PRIMARY KEY (registered_client_id, principal_name)
);

CREATE TABLE IF NOT EXISTS oauth2_authorization (
	id varchar(100) NOT NULL,
	registered_client_id varchar(100) NOT NULL,
	principal_name varchar(200) NOT NULL,
	authorization_grant_type varchar(100) NOT NULL,
	authorized_scopes varchar(1000) DEFAULT NULL,
	attributes text DEFAULT NULL,
	state varchar(500) DEFAULT NULL,
	authorization_code_value text DEFAULT NULL,
	authorization_code_issued_at timestamp without time zone DEFAULT NULL,
	authorization_code_expires_at timestamp without time zone DEFAULT NULL,
	authorization_code_metadata text DEFAULT NULL,
	access_token_value text DEFAULT NULL,
	access_token_issued_at timestamp without time zone DEFAULT NULL,
	access_token_expires_at timestamp without time zone DEFAULT NULL,
	access_token_metadata text DEFAULT NULL,
	access_token_type varchar(100) DEFAULT NULL,
	access_token_scopes varchar(1000) DEFAULT NULL,
	oidc_id_token_value text DEFAULT NULL,
	oidc_id_token_issued_at timestamp without time zone DEFAULT NULL,
	oidc_id_token_expires_at timestamp without time zone DEFAULT NULL,
	oidc_id_token_metadata text DEFAULT NULL,
	refresh_token_value text DEFAULT NULL,
	refresh_token_issued_at timestamp without time zone DEFAULT NULL,
	refresh_token_expires_at timestamp without time zone DEFAULT NULL,
	refresh_token_metadata text DEFAULT NULL,
	user_code_value text DEFAULT NULL,
	user_code_issued_at timestamp without time zone DEFAULT NULL,
	user_code_expires_at timestamp without time zone DEFAULT NULL,
	user_code_metadata text DEFAULT NULL,
	device_code_value text DEFAULT NULL,
	device_code_issued_at timestamp without time zone DEFAULT NULL,
	device_code_expires_at timestamp without time zone DEFAULT NULL,
	device_code_metadata text DEFAULT NULL,
	CONSTRAINT oauth2_authorization_pkey PRIMARY KEY (id)
);

INSERT INTO public.oauth2_registered_client (
	id,
	client_id,
	client_id_issued_at,
	client_secret,
	client_secret_expires_at,
	client_name,
	client_authentication_methods,
	authorization_grant_types,
	redirect_uris,
	post_logout_redirect_uris,
	scopes,
	client_settings,
	token_settings
) VALUES (
	'3eacac0e-0de9-4727-9a64-6bdd4be2ee1f',
	'oidc-client-test',
	CURRENT_TIMESTAMP,
	'$2a$10$.J0Rfg7y2Mu8AN8Dk2vL.eBFa9NGbOYCPOAFEw.QhgGLVXjO7eFDC',
	NULL,
	'Oidc-Client-Test',
	'client_secret_basic',
	'authorization_code,refresh_token',
	'http://spring-oauth2-client:53022/login/oauth2/code/messaging-client-oidc',
	'http://spring-oauth2-client:53022/login/oauth2/code/messaging-client-oidc',
	'openid,profile',
	'{
		"@class":"java.util.Collections$UnmodifiableMap",
		"settings.client.require-proof-key":false,
		"settings.client.require-authorization-consent":true
	}',
	'{
		"@class":"java.util.Collections$UnmodifiableMap",
		"settings.token.reuse-refresh-tokens":true,
		"settings.token.id-token-signature-algorithm":
			["org.springframework.security.oauth2.jose.jws.SignatureAlgorithm","RS256"],
		"settings.token.access-token-time-to-live":
			["java.time.Duration",300.000000000],
		"settings.token.access-token-format":
			{
				"@class":"org.springframework.security.oauth2.server.authorization.settings.OAuth2TokenFormat",
				"value":"self-contained"
			},
		"settings.token.refresh-token-time-to-live":
			["java.time.Duration",3600.000000000],
		"settings.token.authorization-code-time-to-live":
			["java.time.Duration",300.000000000],
		"settings.token.device-code-time-to-live":
			["java.time.Duration",300.000000000]
	}'
);

CREATE TABLE public.users (
	id bigint NOT NULL,
	description varchar(255) NOT NULL,
	email varchar(255) NOT NULL,
	firstname varchar(255) NOT NULL,
	lastname varchar(255) NOT NULL,
	password varchar(255) NOT NULL,
	status int NOT NULL,
	CONSTRAINT users_pkey PRIMARY KEY (id)
);

INSERT INTO public.users (
	id,
	firstname,
	lastname,
	email,
	password,
	description,
	status
) VALUES (
	1,
	'Hanlin',
	'WU',
	'whlawsl@gmail.com',
	'$2a$12$T671lACWlmwCJuLjoaoayOzXIpDLCy/lUNPBcai.GAngLsM0UfYNW',
	'Test insert user',
	1
);
