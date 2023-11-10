package com.example.lab;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.SurfaceView;
import android.app.Activity;

import com.example.lab.models.SpecLine;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class SpectraView extends SurfaceView {

    ArrayList<SpecLine> lines = new ArrayList<>();
    float bg_lum = 0.25f;
    boolean have_background = false;
    static float wlen_min = 380.0f;
    static float wlen_max = 780.0f;
    Activity ctx;
    JSONArray arr;
    Paint p;

    public SpectraView(Context context, AttributeSet attrs) {
        super(context, attrs);
        p = new Paint();
    }

    void download_background(final SpectraView me, int steps) {
        ApiHelper req = new ApiHelper((Activity) me.getContext()) { // Изменено
            @Override
            public void on_ready(String res) {
                try {
                    arr = new JSONArray(res);
                } catch (JSONException ex) {
                }
                have_background = true;
                me.invalidate();
            }
        };
        JSONObject obj = new JSONObject();
        try {
            obj.put("nm_from", wlen_min);
            obj.put("nm_to", wlen_max); // Исправлено
            obj.put("steps", steps);
        } catch (JSONException ex) {
        }

        req.send("http://194.87.68.149:5003/rpc/nm_to_rgb_range", obj.toString());
    }


    float lerp(float a, float b, float t) {
        return a + (b - a) * t;
    }

    float unlerp(float x, float x0, float x1) {
        return (x - x0) / (x1 - x0);
    }

    float map(float x, float x0, float x1, float a, float b) {
        float t = unlerp(x, x0, x1);
        return lerp(a, b, t);
    }

    float last_x = 0.0f;
    int img_w;
    boolean moving = false;

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                last_x = event.getX();
                moving = true;
                return true;

            case MotionEvent.ACTION_UP:
                moving = false;
                have_background = false;
                invalidate();
                return true;

            case MotionEvent.ACTION_MOVE:
                float new_x = event.getX();
                float delta_x = new_x - last_x;
                float delta_nm = wlen_max - wlen_min;
                float nm_per_pixel = delta_nm / img_w;
                wlen_min -= delta_x * nm_per_pixel;
                wlen_max -= delta_x * nm_per_pixel;
                last_x = event.getX();
                invalidate();
                return true;
        }
        return super.onTouchEvent(event);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        int w = canvas.getWidth();
        int h = canvas.getHeight();

        img_w = w;

        canvas.drawColor(Color.BLACK);

        if (have_background == false)
            download_background(this, w);
        else {
            if (moving == false)
                for (int i = 0; i < arr.length(); i++) {
                    try {
                        JSONObject obj = arr.getJSONObject(i);
                        int r = (int) (obj.getDouble("red") * bg_lum * 255.0);
                        int g = (int) (obj.getDouble("green") * bg_lum * 255.0);
                        int b = (int) (obj.getDouble("blue") * bg_lum * 255.0);
                        p.setARGB(255, r, g, b);
                    } catch (JSONException ex) {
                    }
                    canvas.drawLine(i, 0, i, h, p);
                }
        }
        for (int i = 0; i < lines.size(); i++) {
            SpecLine sl = lines.get(i);
            float x = map(sl.wavelength, wlen_min, wlen_max, 0, w - 1);
            sl.setPaintColor(p);
            canvas.drawLine(x, 0, x, h, p);
        }
    }
}
