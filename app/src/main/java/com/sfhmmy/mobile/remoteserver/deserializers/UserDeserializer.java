/*
 * UserDeserializer.java
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
import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.sfhmmy.mobile.App;
import com.sfhmmy.mobile.R;
import com.sfhmmy.mobile.remoteserver.RemoteServerProxy;
import com.sfhmmy.mobile.users.EducationRank;
import com.sfhmmy.mobile.users.Faculty;
import com.sfhmmy.mobile.users.Institution;
import com.sfhmmy.mobile.users.School;
import com.sfhmmy.mobile.users.User;

import org.threeten.bp.ZonedDateTime;

import java.lang.reflect.Type;


public class UserDeserializer implements JsonDeserializer<User> {

    @Override
    public User deserialize(JsonElement je, Type type, JsonDeserializationContext jdc) {

        Gson gson = new GsonBuilder()
                .registerTypeAdapter(ZonedDateTime.class, new ZonedDateTimeDeserializer())
                .create();

        JsonObject data = je.getAsJsonObject();
        // Single user jsons, encapsulate user data inside a data field, while users lists
        // contain the whole array inside data field, already parsed be higher level deserializers.
        if (data.has("data"))data = data.getAsJsonObject("data");

        // Initially, bind all matching fields.
        User user = gson.fromJson(data, User.class);

        // Deserialize user role.
        JsonArray roles = data.getAsJsonArray("roles");
        for (int i = 0; i < roles.size(); ++i) {
            JsonObject item = roles.get(i).getAsJsonObject();
            String roleText = item.get("name").getAsString();

            if (roleText.equals("student") && user.getRole() != User.Role.SECRETARY) {
                user.setRole(User.Role.VISITOR);
            } else if (roleText.equals("secretariat")) {
                user.setRole(User.Role.SECRETARY);
            }
        }

        // Deserialize gender.
        String genderText = data.get("gender").getAsString();
        switch (genderText) {
            case "male" :
                user.setGender(User.Gender.MALE);
                break;

            case "female":
                user.setGender(User.Gender.FEMALE);
                break;

            case "other":
                user.setGender(User.Gender.OTHER);
                break;

            default:
                Log.e("UserDeserializer", String.format("Invalid gender type: %s", genderText));
                user.setGender(null);
        }

        // Deserialize locale.
        String localeText = data.get("locale").getAsString();
        switch (localeText) {
            case "el" :
                user.setPreferedLanguage(
                        App.getAppContext().getString(R.string.user_preferred_language_el_text)
                );
                break;

            default:
                user.setPreferedLanguage(
                        App.getAppContext().getString(R.string.user_preferred_language_en_text)
                );
        }

        Institution organization  = gson.fromJson(data.get("institution"), Institution.class);
        Faculty department        = gson.fromJson(data.get("faculty_id"), Faculty.class);
        School departmentSpecial  = gson.fromJson(data.get("school_id"), School.class);
        EducationRank rank        = gson.fromJson(data.get("education_rank"), EducationRank.class);

        if (organization != null) user.setOrganization(organization.getName());
        if (department != null) user.setDepartment(department.getName());
        if (departmentSpecial != null) user.setDepartmentSpecialization(departmentSpecial.getName());
        if (rank != null) user.setEducationLevel(rank.getName());

        return user;
    }
}
