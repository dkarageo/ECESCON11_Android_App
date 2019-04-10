/*
 * AccessTokenDeserializer.java
 *
 * Created for ECESCON11 Android Application by:
 *  Dimitrios Karageorgiou (dkarageo) - soulrain@outlook.com
 *
 * This file is licensed under the license of ECESCON11 Android Application project.
 *
 * Version: 0.1
 */

package com.sfhmmy.mobile.remoteserver.deserializers;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.sfhmmy.mobile.users.AccessToken;

import java.lang.reflect.Type;


public class AccessTokenDeserializer implements JsonDeserializer<AccessToken> {

    @Override
    public AccessToken deserialize(JsonElement je, Type type, JsonDeserializationContext jdc)
            throws JsonParseException {

        JsonObject jsonObject   = je.getAsJsonObject();
        AccessToken accessToken = new AccessToken();

        if (jsonObject.has("success")) {
            accessToken.setAccessToken(
                    jsonObject.getAsJsonObject("success").get("token").getAsString()
            );
        }

        return accessToken;
    }
}
