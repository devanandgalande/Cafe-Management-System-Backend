package com.pseudovert.CafeKraze.utils;

import com.google.common.base.Strings;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.json.JSONArray;
import org.json.JSONException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.io.File;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class CafeUtils {

    private CafeUtils() {

    }

    public static ResponseEntity<String> getResponseEntity(String responseMessage, HttpStatus httpStatus) {
        return new ResponseEntity<>("{ \"message\" : \"" + responseMessage + "\" }", httpStatus);

    }

    public static String getUUID() {
        return "BILL-" + new Date().getTime();
    }

    public static JSONArray getJsonArrayFromString(String data) throws JSONException {
        JSONArray jsonArray = new JSONArray(data);
        return jsonArray;
    }

    public static Map<String, Object> getMapFromJson(String data) {
        if (!Strings.isNullOrEmpty(data))
            return new Gson().fromJson(data, new TypeToken<Map<String, Object>>() {
            }.getType());
        return new HashMap<>();
    }

    public static boolean isFileExist(String path) {
        try {
            File file = new File(path);
            return file.exists();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return false;
    }
}
