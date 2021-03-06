/**
 * JBoss, Home of Professional Open Source
 * Copyright Red Hat, Inc., and individual contributors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jboss.aerogear.unifiedpush.utils.installation;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import org.apache.http.HttpStatus;
import org.jboss.aerogear.test.ContentTypes;
import org.jboss.aerogear.test.Headers;
import org.jboss.aerogear.test.Helper;
import org.jboss.aerogear.test.Session;
import org.jboss.aerogear.test.UnexpectedResponseException;
import org.jboss.aerogear.test.Validate;
import org.jboss.aerogear.unifiedpush.api.Installation;
import org.jboss.aerogear.unifiedpush.api.Variant;
import org.jboss.aerogear.unifiedpush.api.VariantType;
import org.jboss.aerogear.unifiedpush.utils.Picker;
import org.jboss.aerogear.unifiedpush.utils.StringPicker;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import com.jayway.restassured.path.json.JsonPath;
import com.jayway.restassured.response.Response;

public class InstallationUtils {
    private static final int SINGLE = 1;

    private static final String ANDROID_DEFAULT_DEVICE_TYPE = "AndroidPhone";
    private static final String ANDROID_DEFAULT_OPERATING_SYSTEM = "ANDROID";
    private static final String ANDROID_DEFAULT_OPERATING_SYSTEM_VERSION = "4.2.2";
    private static final String[] ANDROID_DEFAULT_CATEGORIES = {};

    private static final String IOS_DEFAULT_DEVICE_TYPE = "IOSPhone";
    private static final String IOS_DEFAULT_OPERATING_SYSTEM = "IOS";
    private static final String IOS_DEFAULT_OPERATING_SYSTEM_VERSION = "6.0";
    private static final String[] IOS_DEFAULT_CATEGORIES = {};

    private static final String SIMPLEPUSH_DEFAULT_DEVICE_TYPE = "web";
    private static final String SIMPLEPUSH_DEFAULT_OPERATING_SYSTEM = "MozillaOS";
    private static final String SIMPLEPUSH_DEFAULT_OPERATING_SYSTEM_VERSION = "1";
    private static final String[] SIMPLEPUSH_DEFAULT_CATEGORIES = { "default_category" };
    private static final String SIMPLEPUSH_DEFAULT_ENDPOINT = "http://localhost:8081/endpoint/%s";

    /**
     * Generates installations for some variant type.
     *
     * @param variantType type of variant to generete installations of
     * @param installationCount number of generated variants of some {@code variantType}
     * @return list of installations of some {@code variantType}
     */
    public static List<Installation> generate(VariantType variantType, int installationCount) {
        List<Installation> installations = new ArrayList<Installation>();

        switch (variantType) {
            case ANDROID:
                installations = generateAndroid(installationCount);
                break;
            case IOS:
                installations = generateIos(installationCount);
                break;
            case SIMPLE_PUSH:
                installations = generateSimplePush(installationCount);
                break;
            default:
                break;
        }

        return installations;
    }

    public static Installation createAndroid(String deviceToken, String alias) {
        return create(deviceToken, alias, getAndroidDefaultDeviceType(), getAndroidDefaultOperatingSystem(),
            getAndroidDefaultOperatingSystemVersion(), getAndroidDefaultCategories(), null);
    }

    public static Installation createIOS(String deviceToken, String alias) {
        return create(deviceToken, alias, getIosDefaultDeviceType(), getIosDefaultOperatingSystem(),
            getIosDefaultOperatingSystemVersion(), getIosDefaultCategories(), null);
    }

    public static Installation createSimplePush(String deviceToken, String alias) {
        return create(deviceToken, alias, getSimplepushDefaultDeviceType(), getSimplepushDefaultOperatingSystem(),
            getSimplepushDefaultOperatingSystemVersion(), getSimplepushDefaultCategories(),
            getSimplepushDefaultEndpoint(deviceToken));
    }

    public static Installation create(String deviceToken, String alias, String deviceType,
        String operatingSystem, String operatingSystemVersion,
        Set<String> categories, String simplePushEndpoint) {
        Installation installation = new Installation();

        installation.setDeviceToken(deviceToken);
        installation.setDeviceType(deviceType);
        installation.setOperatingSystem(operatingSystem);
        installation.setOsVersion(operatingSystemVersion);
        installation.setAlias(alias);
        installation.setCategories(categories);

        return installation;
    }

    public static Installation generateAndroid() {
        return generateAndroid(SINGLE).iterator().next();
    }

    public static List<Installation> generateAndroid(int count) {
        List<Installation> installations = new ArrayList<Installation>();

        for (int i = 0; i < count; i++) {
            String deviceToken = Helper.randomStringOfLength(100);
            String alias = UUID.randomUUID().toString();

            Installation installation = createAndroid(deviceToken, alias);

            installations.add(installation);
        }

        return installations;
    }

    public static Installation generateIos() {
        return generateIos(SINGLE).iterator().next();
    }

    public static List<Installation> setCategories(List<Installation> installations, Set<String> categories, int categoriesPerInstallation) {

        Picker<String> picker = new StringPicker();

        for (Installation installation : installations) {
            installation.setCategories(picker.pick(categories, categoriesPerInstallation));
        }

        return installations;
    }

    public static List<Installation> generateIos(int count) {
        List<Installation> installations = new ArrayList<Installation>();

        for (int i = 0; i < count; i++) {
            String deviceToken = UUID.randomUUID().toString().replaceAll("-", "");
            String alias = UUID.randomUUID().toString();

            Installation installation = createIOS(deviceToken, alias);

            installations.add(installation);
        }

        return installations;
    }

    public static Installation generateSimplePush() {
        return generateSimplePush(SINGLE).iterator().next();
    }

    public static List<Installation> generateSimplePush(int count) {
        List<Installation> installations = new ArrayList<Installation>();

        for (int i = 0; i < count; i++) {
            String deviceToken = UUID.randomUUID().toString();
            String alias = UUID.randomUUID().toString();

            Installation installation = createSimplePush(deviceToken, alias);

            installations.add(installation);
        }

        return installations;
    }

    public static void register(Installation installation, Variant variant, Session session) {
        register(installation, variant, ContentTypes.json(), session);
    }

    public static void register(Installation installation, Variant variant, String contentType, Session session) {

        Response response = session.given()
            .contentType(contentType)
            .auth().preemptive().basic(variant.getVariantID(), variant.getSecret())
            .header(Headers.acceptJson())
            .body(toJSONString(installation))
            .post("/rest/registry/device");

        UnexpectedResponseException.verifyResponse(response, HttpStatus.SC_OK);

        setFromJsonPath(response.jsonPath(), installation);
    }

    public static void registerAll(List<Installation> installations, Variant variant, Session session) {
        registerAll(installations, variant, ContentTypes.json(), session);
    }

    public static void registerAll(List<Installation> installations, Variant variant, String contentType, Session session) {
        for (Installation installation : installations) {
            register(installation, variant, contentType, session);
        }
    }

    public static void unregister(Installation installation, Variant variant, Session session) {
        Response response = session.givenAuthorized()
            .contentType(ContentTypes.json())
            .auth()
            .basic(variant.getVariantID(), variant.getSecret())
            .delete("/rest/registry/device/{deviceToken}", installation.getDeviceToken());

        UnexpectedResponseException.verifyResponse(response, HttpStatus.SC_NO_CONTENT);
    }

    public static void unregisterAll(List<Installation> installations, Variant variant, Session session) {
        for (Installation installation : installations) {
            unregister(installation, variant, session);
        }
    }

    public static List<Installation> listAll(Variant variant, Session session) {
        Validate.notNull(session);

        Response response = session.givenAuthorized()
            .contentType(ContentTypes.json())
            .header(Headers.acceptJson())
            .get("/rest/applications/{variantID}/installations", variant.getVariantID());

        UnexpectedResponseException.verifyResponse(response, HttpStatus.SC_OK);

        List<Installation> installations = new ArrayList<Installation>();

        JsonPath jsonPath = response.jsonPath();

        List<Map<String, ?>> items = jsonPath.getList("");

        for (int i = 0; i < items.size(); i++) {
            jsonPath.setRoot("[" + i + "]");

            Installation installation = fromJsonPath(jsonPath);

            installations.add(installation);
        }

        return installations;
    }

    public static Installation findById(String installationID, Variant variant, Session session) {
        Validate.notNull(session);

        Response response = session.givenAuthorized()
            .contentType(ContentTypes.json())
            .header(Headers.acceptJson())
            .get("/rest/applications/{variantID}/installations/{installationID}",
                variant.getVariantID(), installationID);

        UnexpectedResponseException.verifyResponse(response, HttpStatus.SC_OK);

        return fromJsonPath(response.jsonPath());
    }

    public static void updateInstallation(Installation installation, Variant variant, Session session) {
        Validate.notNull(session);

        Response response = session.givenAuthorized()
            .contentType(ContentTypes.json())
            .header(Headers.acceptJson())
            .body(toJSONString(installation))
            .put("/rest/applications/{variantID}/installations/{installationID}",
                variant.getVariantID(), installation.getId());

        UnexpectedResponseException.verifyResponse(response, HttpStatus.SC_NO_CONTENT);
    }

    public static void delete(Installation installation, Variant variant, Session session) {
        Validate.notNull(session);

        Response response = session.givenAuthorized()
            .header(Headers.acceptJson())
            .delete("/rest/applications/{variantID}/installations/{installationID}",
                variant.getVariantID(), installation.getId());

        UnexpectedResponseException.verifyResponse(response, HttpStatus.SC_NO_CONTENT);
    }

    public static JSONObject toJSONObject(Installation installation) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("deviceToken", installation.getDeviceToken());
        jsonObject.put("deviceType", installation.getDeviceType());
        jsonObject.put("operatingSystem", installation.getOperatingSystem());
        jsonObject.put("osVersion", installation.getOsVersion());
        jsonObject.put("alias", installation.getAlias());

        if (installation.getCategories() != null) {
            // JSONObject doesn't understand Set<String>
            JSONArray categories = new JSONArray();
            for (String category : installation.getCategories()) {
                categories.add(category);
            }
            jsonObject.put("categories", categories);
        }
        return jsonObject;
    }

    public static String toJSONString(Installation installation) {
        return toJSONObject(installation).toJSONString();
    }

    public static Installation fromJsonPath(JsonPath jsonPath) {
        Installation installation = new Installation();

        setFromJsonPath(jsonPath, installation);

        return installation;
    }

    public static void setFromJsonPath(JsonPath jsonPath, Installation installation) {
        installation.setId(jsonPath.getString("id"));
        installation.setPlatform(jsonPath.getString("platform"));
        installation.setEnabled(jsonPath.getBoolean("enabled"));
        installation.setOperatingSystem(jsonPath.getString("operatingSystem"));
        installation.setOsVersion(jsonPath.getString("osVersion"));
        installation.setAlias(jsonPath.getString("alias"));
        installation.setDeviceType(jsonPath.getString("deviceType"));
        installation.setDeviceToken(jsonPath.getString("deviceToken"));
        HashSet<String> categories = new HashSet<String>();
        List<String> jsonCategories = jsonPath.getList("categories");
        if (jsonCategories != null) {
            for (String category : jsonCategories) {
                categories.add(category);
            }
        }
        installation.setCategories(categories);
    }

    public static void assignInstallationsToCategories(List<String> categories, List<Installation> installations,
        int categoriesPerInstallation) {

        for (Installation installation : installations) {
            assignInstallationToCategories(categories, installation, categoriesPerInstallation);
        }
    }

    public static void assignInstallationToCategories(List<String> categories, Installation installation,
        int categoriesPerInstallation) {

        Set<String> pickedCategories = getRandomCategories(categories, categoriesPerInstallation);

        installation.setCategories(pickedCategories);
    }

    private static Set<String> getRandomCategories(List<String> categories, int categoriesPerInstallation) {

        Set<String> picked = new HashSet<String>();

        Collections.shuffle(categories);

        picked.addAll(categories.subList(0, categoriesPerInstallation));

        return picked;
    }

    public static List<String> getAllCategories(Session session) {

        Response response = session.givenAuthorized()
            .contentType(ContentTypes.json())
            .header(Headers.acceptJson())
            .get("/rest/categories/all");

        return Arrays.asList(response.getBody().as(String[].class));
    }

    public static String getAndroidDefaultDeviceType() {
        return ANDROID_DEFAULT_DEVICE_TYPE;
    }

    public static String getAndroidDefaultOperatingSystem() {
        return ANDROID_DEFAULT_OPERATING_SYSTEM;
    }

    public static String getAndroidDefaultOperatingSystemVersion() {
        return ANDROID_DEFAULT_OPERATING_SYSTEM_VERSION;
    }

    public static Set<String> getAndroidDefaultCategories() {
        HashSet<String> categories = new HashSet<String>();
        Collections.addAll(categories, ANDROID_DEFAULT_CATEGORIES);
        return categories;
    }

    public static String getIosDefaultDeviceType() {
        return IOS_DEFAULT_DEVICE_TYPE;
    }

    public static String getIosDefaultOperatingSystem() {
        return IOS_DEFAULT_OPERATING_SYSTEM;
    }

    public static String getIosDefaultOperatingSystemVersion() {
        return IOS_DEFAULT_OPERATING_SYSTEM_VERSION;
    }

    public static Set<String> getIosDefaultCategories() {
        HashSet<String> categories = new HashSet<String>();
        Collections.addAll(categories, IOS_DEFAULT_CATEGORIES);
        return categories;
    }

    public static String getSimplepushDefaultDeviceType() {
        return SIMPLEPUSH_DEFAULT_DEVICE_TYPE;
    }

    public static String getSimplepushDefaultOperatingSystem() {
        return SIMPLEPUSH_DEFAULT_OPERATING_SYSTEM;
    }

    public static String getSimplepushDefaultOperatingSystemVersion() {
        return SIMPLEPUSH_DEFAULT_OPERATING_SYSTEM_VERSION;
    }

    public static Set<String> getSimplepushDefaultCategories() {
        HashSet<String> categories = new HashSet<String>();
        Collections.addAll(categories, SIMPLEPUSH_DEFAULT_CATEGORIES);
        return categories;
    }

    public static String getSimplepushDefaultEndpoint(String deviceToken) {
        return String.format(SIMPLEPUSH_DEFAULT_ENDPOINT, deviceToken);
    }

}
