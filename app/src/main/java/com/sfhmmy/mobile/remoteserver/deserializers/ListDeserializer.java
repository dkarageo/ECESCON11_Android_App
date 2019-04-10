package com.sfhmmy.mobile.remoteserver.deserializers;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;


public class ListDeserializer implements JsonDeserializer<List<?>> {

    @Override
    public List deserialize(JsonElement je, Type type, JsonDeserializationContext jdc) {

        Type valueType = ((ParameterizedType) type).getActualTypeArguments()[0];
        JsonObject jsonObject = je.getAsJsonObject();

        List<Object> list = new ArrayList<>();

        for (JsonElement item : jsonObject.getAsJsonArray("data")) {
            list.add(jdc.deserialize(item, valueType));
        }

        return list;
    }

}
