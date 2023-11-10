package com.example.lab.models;

import android.graphics.Paint;

import org.json.JSONException;
import org.json.JSONObject;

public class SpecLine {
    public float wavelength;
    float rel_intensity;
    int red;
    int green;
    int blue;

    public SpecLine(JSONObject obj) throws JSONException {
        wavelength = (float) obj.getDouble("wavelength");
        rel_intensity = (float) obj.getDouble("rel_intensity");

        red = (int) (obj.getDouble("red") * 255.0f);
        green = (int) (obj.getDouble("green") * 255.0f);
        blue = (int) (obj.getDouble("blue") * 255.0f);
    }

    public void setPaintColor(Paint p) {
        p.setARGB(255, red, green, blue);
    }
}
