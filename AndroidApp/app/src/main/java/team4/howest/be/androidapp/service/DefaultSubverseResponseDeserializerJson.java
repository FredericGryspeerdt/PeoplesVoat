package team4.howest.be.androidapp.service;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;

import java.lang.reflect.Type;

import team4.howest.be.androidapp.model.DefaultSubverseResponse;

/**
 * Created by Frederic on 16/11/2015.
 */
public class DefaultSubverseResponseDeserializerJson implements JsonDeserializer<DefaultSubverseResponse> {
    @Override
    public DefaultSubverseResponse deserialize(JsonElement je, Type type, JsonDeserializationContext jdc) throws JsonParseException {

        DefaultSubverseResponse defaultSubverseResponse = new DefaultSubverseResponse();

        JsonArray jsonArray = je.getAsJsonArray();

        for(JsonElement element : jsonArray){
            //JsonObject jsonObject = element.getAsJsonObject();
            String defaultSubver = element.getAsString();
            defaultSubverseResponse.add(defaultSubver);
        }
        return defaultSubverseResponse;
    }
}
