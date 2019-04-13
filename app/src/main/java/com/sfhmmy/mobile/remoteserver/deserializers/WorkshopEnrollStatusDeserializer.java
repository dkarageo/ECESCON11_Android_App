/*
 * WorkshopEnrollStatusDeserializer.java
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
import com.sfhmmy.mobile.workshops.WorkshopEnrollStatusHelper;

import java.lang.reflect.Type;


public class WorkshopEnrollStatusDeserializer implements JsonDeserializer<WorkshopEnrollStatusHelper> {

    @Override
    public WorkshopEnrollStatusHelper deserialize(JsonElement je, Type type,
                                                  JsonDeserializationContext jdc) {

        JsonObject jsonObject = je.getAsJsonObject();

        // Deserialize all fields matching to the model.
        Gson gson = new GsonBuilder().create();
        WorkshopEnrollStatusHelper enrollStatus = gson.fromJson(
                jsonObject, WorkshopEnrollStatusHelper.class
        );

        String enrollStatusText = jsonObject.get("enroll_status").getAsString();
        switch(enrollStatusText) {

            case "available":
                enrollStatus.setEnrollStatus(Workshop.EnrollStatus.AVAILABLE);
                break;

            case "unavailable":
                enrollStatus.setEnrollStatus(Workshop.EnrollStatus.UNAVAILABLE);
                break;

            case "accepted":
                enrollStatus.setEnrollStatus(Workshop.EnrollStatus.ACCEPTED);
                break;

            case "rejected":
                enrollStatus.setEnrollStatus(Workshop.EnrollStatus.REJECTED);
                break;

            case "pending":
                enrollStatus.setEnrollStatus(Workshop.EnrollStatus.PENDING);
                break;

            default:
                enrollStatus.setEnrollStatus(null);
                Log.e("WorkshopDeserializer",
                      String.format("Invalid role format received: %s", enrollStatusText));
        }

        return enrollStatus;

    }
}
