/*
 * ContentPageDeserializer.java
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
import com.sfhmmy.mobile.remoteserver.ContentPage;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;


public class ContentPageDeserializer implements JsonDeserializer<ContentPage<?>> {

    @Override
    public ContentPage deserialize(JsonElement je, Type type, JsonDeserializationContext jdc) {

        Type valueType = ((ParameterizedType) type).getActualTypeArguments()[0];
        JsonObject jsonObject = je.getAsJsonObject();

        ContentPage<Object> page = new ContentPage<>();

        // Deserialize actual content.
        List<Object> content = new ArrayList<>();
        for (JsonElement item : jsonObject.getAsJsonArray("data")) {
            content.add(jdc.deserialize(item, valueType));
        }
        page.setContentList(content);

        // Deserialize meta.
        JsonObject meta = jsonObject.getAsJsonObject("meta");
        int curPage  = meta.get("current_page").getAsInt();
        int lastPage = meta.get("last_page").getAsInt();
        int perPage  = meta.get("per_page").getAsInt();

        page.setCurrentPage(curPage);
        page.setTotalPages(lastPage);
        page.setItemsPerPage(perPage);

        return page;
    }

}
