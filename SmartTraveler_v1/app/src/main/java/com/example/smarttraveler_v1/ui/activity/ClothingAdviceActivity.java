package com.example.smarttraveler_v1.ui.activity;

import android.os.Bundle;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.example.smarttraveler_v1.R;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Objects;
import android.util.Log;


import okhttp3.*;

public class ClothingAdviceActivity extends AppCompatActivity {

    private TextView tvAdvice;
    private final OkHttpClient client = new OkHttpClient();  // OkHttp client

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_clothing_advice);

        tvAdvice = findViewById(R.id.tv_clothing_advice);

        String city = getIntent().getStringExtra("city");
        String startDate = getIntent().getStringExtra("start_date");
        String endDate = getIntent().getStringExtra("end_date");

        getClothingAdvice(city, startDate, endDate);
    }

    private void getClothingAdvice(String city, String startDate, String endDate) {
        try {
            // 构建 JSON 请求体
            JSONObject bodyJson = new JSONObject();
            bodyJson.put("cities", new JSONArray().put(city));
            bodyJson.put("start_date", startDate);
            bodyJson.put("end_date", endDate);

            RequestBody requestBody = RequestBody.create(
                    MediaType.parse("application/json"),
                    bodyJson.toString()
            );

            Request request = new Request.Builder()
                    .url("http://10.0.2.2:5002/clothing_advice")
                    .post(requestBody)
                    .build();

            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    runOnUiThread(() -> tvAdvice.setText("Failed to get clothing advice."));
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    String responseBody = Objects.requireNonNull(response.body()).string();

                    Log.d("ClothingAdvice", "🟢 Raw response: " + responseBody);

                    try {
                        JSONObject json = new JSONObject(responseBody);
                        String advice = json.getString("clothing_advice");

                        runOnUiThread(() -> tvAdvice.setText(advice));
                    } catch (Exception e) {
                        runOnUiThread(() -> tvAdvice.setText("Failed to parse response."));
                    }
                }
            });
        } catch (Exception e) {
            tvAdvice.setText("Error building request.");
        }
    }
}
