package com.example.lab.models;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class Experiment {
    private String id;
    private String created_at;
    private String note;
    private String status;
    private String tag;
    private String b64image;
    private int x0;
    private int x1;
    private int y0;
    private int y1;


    // Добавьте дополнительные поля, если необходимо

    public Experiment(JSONObject jsonObject) throws JSONException {
        this.id = jsonObject.getString("id");
        this.created_at = jsonObject.getString("created_at");
        this.note = jsonObject.getString("note");
        this.status = jsonObject.getString("status");

        // Добавьте код для дополнительных полей, если необходимо
    }

    public String getId() {
        return id;
    }

    public String getCreatedAt() {
        return created_at;
    }

    public String getNote() {
        return note;
    }

    public String getStatus() {
        return status;
    }

    @Override
    public String toString() {
        return note;
    }

    public JSONObject getExperimentDataAsJson() {
        try {
            JSONObject json = new JSONObject();
            json.put("created_at", created_at);
            json.put("tag", tag);
            json.put("note", note);
            json.put("status", status);
            json.put("b64image", b64image);
            json.put("x0", x0);
            json.put("y0", y0);
            json.put("x1", x1);
            json.put("y1", y1);

            return json;
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }
    public String getTimeInProcess() {
        try {
            // Парсим строку времени создания эксперимента в объект Date
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSSSS", Locale.US);
            Date createdAtDate = dateFormat.parse(created_at);

            // Получаем текущее время
            Date currentDate = new Date();

            // Вычисляем разницу в миллисекундах
            long timeDifferenceMillis = currentDate.getTime() - createdAtDate.getTime();

            // Создаем объект Calendar и устанавливаем разницу в миллисекундах
            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(timeDifferenceMillis);

            // Получаем значения времени из Calendar
            int years = calendar.get(Calendar.YEAR) - 1970;
            int months = calendar.get(Calendar.MONTH);
            int days = calendar.get(Calendar.DAY_OF_MONTH) - 1;
            int hours = calendar.get(Calendar.HOUR_OF_DAY);
            int minutes = calendar.get(Calendar.MINUTE);
            int seconds = calendar.get(Calendar.SECOND);

            return String.format(
                    "Time in process: %d years, %d months, %d days, %d hours, %d minutes, %d seconds",
                    years, months, days, hours, minutes, seconds
            );
        } catch (ParseException e) {
            e.printStackTrace();
            return "Error calculating time difference";
        }
    }
}
