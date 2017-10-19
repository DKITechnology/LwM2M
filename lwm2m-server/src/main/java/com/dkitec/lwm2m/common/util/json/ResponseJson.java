package com.dkitec.lwm2m.common.util.json;

import java.lang.reflect.Type;

import org.eclipse.leshan.core.response.DiscoverResponse;
import org.eclipse.leshan.core.response.LwM2mResponse;
import org.eclipse.leshan.core.response.ReadResponse;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;

public class ResponseJson implements JsonSerializer<LwM2mResponse> {

    @Override
    public JsonElement serialize(LwM2mResponse src, Type typeOfSrc, JsonSerializationContext context) {
        JsonObject element = new JsonObject();

        //element.addProperty("status", src.getCode().toString());

        if (typeOfSrc instanceof Class<?>) {
            if (ReadResponse.class.isAssignableFrom((Class<?>) typeOfSrc)) {
                element.add("result", context.serialize(((ReadResponse) src).getContent()));
            }
            else if (DiscoverResponse.class.isAssignableFrom((Class<?>) typeOfSrc)) {
                element.add("objectLinks", context.serialize(((DiscoverResponse) src).getObjectLinks()));
            }
        }

        return element;
    }
}