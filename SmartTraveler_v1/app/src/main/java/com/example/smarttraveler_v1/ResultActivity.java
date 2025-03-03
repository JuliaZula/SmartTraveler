package com.example.smarttraveler_v1;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import java.util.ArrayList;

public class ResultActivity extends AppCompatActivity {

    private GridLayout resultGrid;
    private TextView tvCost;
    private Button btnBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);

        resultGrid = findViewById(R.id.result_grid);
        tvCost = findViewById(R.id.tv_cost);
        btnBack = findViewById(R.id.btn_back);

        Intent intent = getIntent();
        ArrayList<ArrayList<String>> result = (ArrayList<ArrayList<String>>) intent.getSerializableExtra("result");
        double cost = intent.getDoubleExtra("cost", 0.0);
        ArrayList<Double> costSeparated = (ArrayList<Double>) intent.getSerializableExtra("costSeparated");

        tvCost.setText(String.format("Approx. Price 💵：%.1f €", cost));

        // 插入数据
        if (result != null && costSeparated != null) {
            for (int i = 0; i < result.size(); i++) {
                addGridItem(result.get(i).get(0)); // 国家
                addGridItem(result.get(i).get(1)); // 城市

                // 插入机票费用（除最后一个城市外）
                if (i < costSeparated.size()) {
                    addCostItem(String.format("Flight: %.1f €", costSeparated.get(i)));
                }
            }
        }

        // 返回按钮
        btnBack.setOnClickListener(v -> {
            Intent backIntent = new Intent(ResultActivity.this, MainActivity.class);
            startActivity(backIntent);
            finish();
        });
    }

    // 添加国家或城市的 GridItem
    private void addGridItem(String text) {
        TextView textView = new TextView(this);
        textView.setText(text);
        textView.setTextSize(16);
        textView.setPadding(12, 12, 12, 12);
        textView.setBackgroundResource(android.R.color.darker_gray);
        textView.setTextColor(getResources().getColor(android.R.color.white, getTheme()));

        GridLayout.LayoutParams params = new GridLayout.LayoutParams();
        params.width = 0;
        params.height = GridLayout.LayoutParams.WRAP_CONTENT;
        params.columnSpec = GridLayout.spec(GridLayout.UNDEFINED, 1, 1f);
        params.setMargins(8, 8, 8, 8);
        textView.setLayoutParams(params);

        resultGrid.addView(textView);
    }

    // 插入机票价格的 GridItem（占两列）
    private void addCostItem(String text) {
        TextView textView = new TextView(this);
        textView.setText(text);
        textView.setTextSize(14);
        textView.setPadding(12, 12, 12, 12);
        textView.setBackgroundResource(android.R.color.transparent);
        textView.setTextColor(getResources().getColor(android.R.color.holo_orange_light, getTheme()));
        textView.setGravity(android.view.Gravity.CENTER);

        GridLayout.LayoutParams params = new GridLayout.LayoutParams();
        params.width = 0;
        params.height = GridLayout.LayoutParams.WRAP_CONTENT;
        params.columnSpec = GridLayout.spec(0, 2, 1f); // 占据 2 列
        params.setMargins(8, 4, 8, 12); // 调整间距
        textView.setLayoutParams(params);

        resultGrid.addView(textView);
    }
}
