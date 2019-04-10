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

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
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

        JsonObject data = je.getAsJsonObject().getAsJsonObject("data");

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

        long organizationId      = data.get("institution_id").getAsLong();
        long departmentId        = data.get("faculty_id").getAsLong();
        long departmentSpecialId = data.get("school_id").getAsLong();
        long educationRankId     = data.get("education_rank_id").getAsLong();

        // Fetch organization detail from remote API.
        RemoteServerProxy proxy = new RemoteServerProxy();

        Institution organization  = proxy.getInstitution(organizationId);
        Faculty department        = proxy.getFaculty(departmentId);
        School departmentSpecial  = proxy.getSchool(departmentSpecialId);
        EducationRank rank        = proxy.getEducationRank(educationRankId);

        if (organization != null) user.setOrganization(organization.getName());
        if (department != null) user.setDepartment(department.getName());
        if (departmentSpecial != null) user.setDepartmentSpecialization(departmentSpecial.getName());
        if (rank != null) user.setEducationLevel(rank.getName());

        return user;
    }
}
