package com.example.lab;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.lab.ApiHelper;
import com.example.lab.R;
import com.example.lab.SpectraView;
import com.example.lab.models.ChemElement;
import com.example.lab.models.SpecLine;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.List;

public class MainActivity extends AppCompatActivity {
    Spinner sp;
    SpectraView sv;
    ArrayAdapter<ChemElement> adp;
    Button b;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        sv = findViewById(R.id.spectraview);
        sp = findViewById(R.id.spinner);
        adp = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1);
        sp.setAdapter(adp);
        Button btnPlus = findViewById(R.id.btn_plus);
        Button btnMinus = findViewById(R.id.btn_minus);
        btnPlus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sv.wlen_min = Math.max(380.0f, sv.wlen_min + 10.0f);
                sv.wlen_max = Math.min(780.0f, sv.wlen_max - 10.0f);
                sv.invalidate();
            }
        });

        btnMinus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sv.wlen_min = Math.max(380.0f, sv.wlen_min - 10.0f);
                sv.wlen_max = Math.min(780.0f, sv.wlen_max + 10.0f);
                sv.invalidate();
            }
        });

        ApiHelper req = new ApiHelper(this) {
            @Override
            public void on_ready(String res) {
                try {
                    JSONArray arr = new JSONArray(res);
                    List<ChemElement> chemElements = ChemElement.parseChemElements(arr);

                    adp.addAll(chemElements);
                    adp.notifyDataSetChanged();
                } catch (JSONException ex) {
                    ex.printStackTrace();
                    Log.e("API_ERROR", "Error parsing elements: " + ex.getMessage());
                }
            }

            @Override
            public void on_error(int responseCode) {
                showNotification("API Error", "Response Code: " + responseCode);
            }
        };

        req.send("http://194.87.68.149:5003/rpc/get_elements", "{}");

        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
            @Override
            public void run() {
                String response = req.getResponse();
                showNotification("API Response", response);
            }
        }, 20000);

        b = findViewById(R.id.btn_load);
        b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ChemElement el = (ChemElement) sp.getSelectedItem();
                ApiHelper req = new ApiHelper(MainActivity.this) {
                    @Override
                    public void on_ready(String res) {
                        try {
                            JSONArray arr = new JSONArray(res);
                            for (int i = 0; i < arr.length(); i++)
                                sv.lines.add(new SpecLine(arr.getJSONObject(i)));
                            sv.invalidate();
                        } catch (JSONException ex) {
                            ex.printStackTrace();
                            Log.e("API_ERROR", "Error parsing lines: " + ex.getMessage());
                        }
                    }

                    @Override
                    public void on_error(int responseCode) {
                        showNotification("API Error", "Response Code: " + responseCode);
                    }
                };
                sv.lines.clear();
                req.send("http://194.87.68.149:5003/rpc/get_lines", "{\"atomic_num\": " + String.valueOf(el.atomic_num) + "}");
            }
        });
        sv.ctx = this;
        sv.setWillNotDraw(false);
        sv.invalidate();
    }

    private void showNotification(String title, String message) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(MainActivity.this, title + ": " + message, Toast.LENGTH_SHORT).show();
            }
        });
    }
}