/*
 * Copyright (c) 2022-2024. [DarkCube]
 * All rights reserved.
 * You may not use or redistribute this software or any associated files without permission.
 * The above copyright notice shall be included in all copies of this software.
 */

package eu.darkcube.system.commandapi.util;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;

import eu.darkcube.system.libs.com.google.gson.Gson;
import eu.darkcube.system.libs.com.google.gson.GsonBuilder;
import eu.darkcube.system.libs.com.google.gson.JsonElement;
import eu.darkcube.system.libs.com.google.gson.JsonObject;
import eu.darkcube.system.libs.com.google.gson.JsonParseException;
import eu.darkcube.system.libs.com.google.gson.JsonSyntaxException;
import eu.darkcube.system.libs.com.google.gson.stream.JsonReader;

public class JSONUtils {
    private static final Gson GSON = (new GsonBuilder()).create();

    public static boolean isNumber(JsonElement json) {
        return json.isJsonPrimitive() && json.getAsJsonPrimitive().isNumber();
    }

    public static String getString(JsonElement json, String memberName) {
        if (json.isJsonPrimitive()) {
            return json.getAsString();
        }
        throw new JsonSyntaxException("Expected " + memberName + " to be a string, was " + JSONUtils.toString(json));
    }

    public static String getString(JsonObject json, String memberName) {
        if (json.has(memberName)) {
            return JSONUtils.getString(json.get(memberName), memberName);
        }
        throw new JsonSyntaxException("Missing " + memberName + ", expected to find a string");
    }

    public static String getString(JsonObject json, String memberName, String fallback) {
        return json.has(memberName) ? JSONUtils.getString(json.get(memberName), memberName) : fallback;
    }

    public static boolean getBoolean(JsonElement json, String memberName) {
        if (json.isJsonPrimitive()) {
            return json.getAsBoolean();
        }
        throw new JsonSyntaxException("Expected " + memberName + " to be a Boolean, was " + JSONUtils.toString(json));
    }

    public static boolean getBoolean(JsonObject json, String memberName) {
        if (json.has(memberName)) {
            return JSONUtils.getBoolean(json.get(memberName), memberName);
        }
        throw new JsonSyntaxException("Missing " + memberName + ", expected to find a Boolean");
    }

    public static boolean getBoolean(JsonObject json, String memberName, boolean fallback) {
        return json.has(memberName) ? JSONUtils.getBoolean(json.get(memberName), memberName) : fallback;
    }

    public static float getFloat(JsonElement json, String memberName) {
        if (json.isJsonPrimitive() && json.getAsJsonPrimitive().isNumber()) {
            return json.getAsFloat();
        }
        throw new JsonSyntaxException("Expected " + memberName + " to be a Float, was " + JSONUtils.toString(json));
    }

    public static int getInt(JsonElement json, String memberName) {
        if (json.isJsonPrimitive() && json.getAsJsonPrimitive().isNumber()) {
            return json.getAsInt();
        }
        throw new JsonSyntaxException("Expected " + memberName + " to be a Int, was " + JSONUtils.toString(json));
    }

    public static int getInt(JsonObject json, String memberName) {
        if (json.has(memberName)) {
            return JSONUtils.getInt(json.get(memberName), memberName);
        }
        throw new JsonSyntaxException("Missing " + memberName + ", expected to find a Int");
    }

    public static int getInt(JsonObject json, String memberName, int fallback) {
        return json.has(memberName) ? JSONUtils.getInt(json.get(memberName), memberName) : fallback;
    }

    public static JsonObject getJsonObject(JsonElement json, String memberName) {
        if (json.isJsonObject()) {
            return json.getAsJsonObject();
        }
        throw new JsonSyntaxException("Expected " + memberName + " to be a JsonObject, was " + JSONUtils.toString(json));
    }

    public static boolean isEmpty(String str) {
        return str == null || str.isEmpty();
    }

    public static String toString(JsonElement json) {
        var s = JSONUtils.abbreviateMiddle(String.valueOf(json), "...", 10);
        if (json == null) {
            return "null (missing)";
        } else if (json.isJsonNull()) {
            return "null (json)";
        } else if (json.isJsonArray()) {
            return "an array (" + s + ")";
        } else if (json.isJsonObject()) {
            return "an object (" + s + ")";
        } else {
            if (json.isJsonPrimitive()) {
                var jsonprimitive = json.getAsJsonPrimitive();
                if (jsonprimitive.isNumber()) {
                    return "a number (" + s + ")";
                }

                if (jsonprimitive.isBoolean()) {
                    return "a boolean (" + s + ")";
                }
            }

            return s;
        }
    }

    public static String abbreviateMiddle(String str, String middle, int length) {
        if (JSONUtils.isEmpty(str) || JSONUtils.isEmpty(middle)) {
            return str;
        }

        if (length >= str.length() || length < (middle.length() + 2)) {
            return str;
        }

        var targetSting = length - middle.length();
        var startOffset = targetSting / 2 + targetSting % 2;
        var endOffset = str.length() - targetSting / 2;

        return str.substring(0, startOffset) + middle + str.substring(endOffset);
    }

    public static <T> T fromJson(Gson gsonIn, Reader readerIn, Class<T> adapter, boolean lenient) {
        try {
            var jsonreader = new JsonReader(readerIn);
            jsonreader.setLenient(lenient);
            return gsonIn.getAdapter(adapter).read(jsonreader);
        } catch (IOException ioexception) {
            throw new JsonParseException(ioexception);
        }
    }

    public static <T> T fromJson(Gson gsonIn, String json, Class<T> adapter, boolean lenient) {
        return JSONUtils.fromJson(gsonIn, new StringReader(json), adapter, lenient);
    }

    public static <T> T fromJson(Gson gson, Reader reader, Class<T> jsonClass) {
        return JSONUtils.fromJson(gson, reader, jsonClass, false);
    }

    public static <T> T fromJson(Gson gsonIn, String json, Class<T> adapter) {
        return JSONUtils.fromJson(gsonIn, json, adapter, false);
    }

    public static JsonObject fromJson(String json, boolean lenient) {
        return JSONUtils.fromJson(new StringReader(json), lenient);
    }

    public static JsonObject fromJson(Reader reader, boolean lenient) {
        return JSONUtils.fromJson(JSONUtils.GSON, reader, JsonObject.class, lenient);
    }

    public static JsonObject fromJson(String json) {
        return JSONUtils.fromJson(json, false);
    }

    public static JsonObject fromJson(Reader reader) {
        return JSONUtils.fromJson(reader, false);
    }
}
