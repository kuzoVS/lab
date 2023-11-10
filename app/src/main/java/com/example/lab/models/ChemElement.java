package com.example.lab.models;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.List;

public class ChemElement {
    public int atomic_num;
    String full_name;

    public ChemElement(JSONObject obj) throws JSONException {
        atomic_num = obj.getInt("atomic_num");
        full_name = obj.getString("full_name");
    }

    @Override
    public String toString() {
        return full_name;
    }

    public static List<ChemElement> parseChemElements(JSONArray jsonArray) throws JSONException {
        List<ChemElement> chemElements = new ArrayList<>();

        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject jsonElement = jsonArray.getJSONObject(i);
            ChemElement chemElement = new ChemElement(jsonElement);
            chemElements.add(chemElement);
        }

        return chemElements;
    }
}
