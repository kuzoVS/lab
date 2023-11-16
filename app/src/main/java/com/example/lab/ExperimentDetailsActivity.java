// ExperimentDetailsActivity.java
package com.example.lab;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class ExperimentDetailsActivity extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_experiment_details);

        // Get data passed from the previous activity
        Bundle extras = getIntent().getExtras();
        if (extras != null && extras.containsKey("experimentData")) {
            String experimentData = extras.getString("experimentData");

            // Parse JSON and update UI
            // You may want to use a library like Gson for better JSON parsing
            // For simplicity, we'll use a simple approach here
            try {
                JSONArray jsonArray = new JSONArray(experimentData);
                JSONObject jsonObject = jsonArray.getJSONObject(0);

                String note = jsonObject.getString("note");
                String status = jsonObject.getString("status");
                String b64image = jsonObject.getString("b64image");

                TextView noteTextView = findViewById(R.id.noteTextView);
                TextView statusTextView = findViewById(R.id.statusTextView);
                ImageView imageView = findViewById(R.id.imageView);

                noteTextView.setText("Note: " + note);
                statusTextView.setText("Status: " + status);

                // Decode base64 image and set it to the ImageView
                byte[] decodedBytes = Base64.decode(b64image, Base64.DEFAULT);
                Bitmap decodedBitmap = BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.length);
                imageView.setImageBitmap(decodedBitmap);

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
}
