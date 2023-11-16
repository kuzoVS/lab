package com.example.lab;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.lab.models.Experiment;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class ExperimentActivity extends AppCompatActivity implements AdapterView.OnItemClickListener {
    private ArrayAdapter<Experiment> experimentAdapter;
    private List<Experiment> experiments = new ArrayList<>();
    private List<String> tags = new ArrayList<>(); // Список для хранения тегов
    private String selectedTag; // Выбранный тег
    private Handler handler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_experiment);

        experimentAdapter = new ExperimentAdapter(this, R.layout.experiment_list_item, experiments);
        ListView experimentListView = findViewById(R.id.experimentListView);
        experimentListView.setAdapter(experimentAdapter);
        experimentListView.setOnItemClickListener(this);
        // Асинхронно получаем теги и добавляем их в меню
        loadTags();

        // Запускаем задачу для обновления времени каждую секунду
        handler.postDelayed(updateTimeTask, 1000);
    }

    private Runnable updateTimeTask = new Runnable() {
        @Override
        public void run() {
            // Обновляем время для каждого эксперимента
            for (Experiment experiment : experiments) {
                experimentAdapter.notifyDataSetChanged();
            }

            // Повторяем задачу через 1 секунду
            handler.postDelayed(this, 1000);
        }
    };

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.experiment_menu, menu);

        // Добавляем теги в меню
        for (String tag : tags) {
            menu.add(0, Menu.NONE, Menu.NONE, tag);
        }

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Обработка выбора тега из меню
        selectedTag = item.getTitle().toString();

        // Загрузка экспериментов по выбранному тегу
        loadExperiments();

        return true;
    }

    private void loadTags() {
        // Асинхронно получаем теги и обновляем меню
        ApiHelper req = new ApiHelper(this) {
            @Override
            public void on_ready(String res) {
                try {
                    JSONArray arr = new JSONArray(res);
                    tags = parseTags(arr);
                    invalidateOptionsMenu(); // Обновляем меню
                } catch (JSONException ex) {
                    ex.printStackTrace();
                    // Обработка ошибки при парсинге тегов
                }
            }

            @Override
            public void on_error(int responseCode) {
                // Обработка ошибки при запросе тегов
            }
        };

        req.send("http://194.87.68.149:5003/rpc/get_tags", "{}");
    }

    private List<String> parseTags(JSONArray jsonArray) throws JSONException {
        List<String> parsedTags = new ArrayList<>();

        for (int i = 0; i < jsonArray.length(); i++) {
            parsedTags.add(jsonArray.getString(i));
        }

        return parsedTags;
    }

    private void loadExperiments() {
        Log.d("ExperimentActivity", "loadExperiments called");

        String apiUrl = "http://194.87.68.149:5003/rpc/get_experiments?tagname=" + selectedTag;
        Log.d("ExperimentActivity", "API URL: " + apiUrl);

        ApiHelper req = new ApiHelper(ExperimentActivity.this) {
            @Override
            public void on_ready(String res) {
                Log.d("ExperimentActivity", "Received data: " + res);

                try {
                    JSONArray arr = new JSONArray(res);
                    experiments = parseExperiments(arr);
                    updateExperimentListView();
                } catch (JSONException ex) {
                    ex.printStackTrace();
                }
            }

            @Override
            public void on_error(int responseCode) {
                Log.e("ExperimentActivity", "Error in loadExperiments. Response Code: " + responseCode);
                // Обработка ошибки при запросе экспериментов
            }
        };

        // Формируем корректный JSON-запрос
        req.send("http://194.87.68.149:5003/rpc/get_experiments", "{\"tagname\": \"" + selectedTag + "\"}");
    }

    private List<Experiment> parseExperiments(JSONArray jsonArray) throws JSONException {
        List<Experiment> parsedExperiments = new ArrayList<>();

        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject jsonObject = jsonArray.getJSONObject(i);

            try {
                Experiment experiment = new Experiment(jsonObject);
                parsedExperiments.add(experiment);
            } catch (JSONException e) {
                e.printStackTrace();
                // Handle JSONException for a specific experiment
            }
        }

        return parsedExperiments;
    }

    private void updateExperimentListView() {
        Log.d("ExperimentActivity", "updateExperimentListView called");
        // Обновляем ListView с новым списком экспериментов
        experimentAdapter.clear();
        experimentAdapter.addAll(experiments);
        experimentAdapter.notifyDataSetChanged();
    }
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        // Handle item click here
        Experiment selectedExperiment = experiments.get(position);

        // Open ExperimentDetailsActivity and pass the experiment data
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }
}
