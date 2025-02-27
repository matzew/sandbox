package com.redhat.service.bridge.integration.tests.common;

import org.keycloak.representations.AccessTokenResponse;

import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;

import static io.restassured.RestAssured.given;

public class BridgeUtils {
    protected static final String USER_NAME = getSystemProperty("bridge.token.username");
    protected static final String PASSWORD = getSystemProperty("bridge.token.password");
    protected static final String CLIENT_ID = getSystemProperty("bridge.client.id");
    protected static final String CLIENT_SECRET = getSystemProperty("bridge.client.secret");

    protected static String token;
    protected static String env_token;
    protected static String keycloakURL = System.getProperty("key-cloak.url");

    public static RequestSpecification jsonRequestWithAuth() {
        env_token = System.getenv("OB_TOKEN");
        if (env_token != null) {
            token = env_token;
        } else if (keycloakURL != null && !keycloakURL.isEmpty()) {
            token = getAccessToken();
        } else {
            throw new RuntimeException("Environment variable token and key-cloak url was not defined for token generation.");
        }
        return given()
                .contentType(ContentType.JSON)
                .when()
                .auth()
                .oauth2(token);
    }

    private static String getAccessToken() {
        return given().param("grant_type", "password")
                .param("username", USER_NAME)
                .param("password", PASSWORD)
                .param("client_id", CLIENT_ID)
                .param("client_secret", CLIENT_SECRET)
                .when()
                .post(keycloakURL + "/protocol/openid-connect/token")
                .as(AccessTokenResponse.class)
                .getToken();
    }

    public static String getSystemProperty(String parameters) {
        if (System.getProperty(parameters).isEmpty()) {
            throw new RuntimeException("Property " + parameters + " was not defined.");
        }
        return System.getProperty(parameters);
    }
}
