/*
 * WorkshopDeserializer.java
 *
 * Created for ECESCON11 Android Application by:
 *  Dimitrios Karageorgiou (dkarageo) - soulrain@outlook.com
 *
 * This file is licensed under the license of ECESCON11 Android Application project.
 *
 * Version: 0.1
 */

package com.sfhmmy.mobile.remoteserver.deserializers;

import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.sfhmmy.mobile.workshops.Workshop;

import org.threeten.bp.ZonedDateTime;

import java.lang.reflect.Type;


public class WorkshopDeserializer implements JsonDeserializer<Workshop> {

    @Override
    public Workshop deserialize(JsonElement je, Type type, JsonDeserializationContext jdc) {

        JsonObject jsonObject = je.getAsJsonObject();

        // Deserialize all fields matching to the model.
        Gson gson = new GsonBuilder()
                .registerTypeAdapter(ZonedDateTime.class, new ZonedDateTimeDeserializer())
                .create();
        Workshop workshop = gson.fromJson(jsonObject, Workshop.class);

        String enrollStatusText = jsonObject.get("enroll_status").getAsString();
        switch(enrollStatusText) {

            case "available":
                workshop.setEnrollStatus(Workshop.EnrollStatus.AVAILABLE);
                break;

            case "unavailable":
                workshop.setEnrollStatus(Workshop.EnrollStatus.UNAVAILABLE);
                break;

            case "accepted":
                workshop.setEnrollStatus(Workshop.EnrollStatus.ACCEPTED);
                break;

            case "rejected":
                workshop.setEnrollStatus(Workshop.EnrollStatus.REJECTED);
                break;

            case "pending":
                workshop.setEnrollStatus(Workshop.EnrollStatus.PENDING);
                break;

            default:
                workshop.setEnrollStatus(null);
                Log.e("WorkshopDeserializer",
                      String.format("Invalid role format received: %s", enrollStatusText));
        }

        return workshop;

    }
}
