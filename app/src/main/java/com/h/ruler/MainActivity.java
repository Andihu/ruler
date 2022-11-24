package com.h.ruler;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    RuleView ruleView;
    TextView textView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
//        ruleView = findViewById(R.id.rule_view);
//        textView = findViewById(R.id.value);
//        ruleView.setOnScaleChangeListener(new RuleView.OnScaleChangeListener() {
//            @Override
//            public void onScaleChange(int value) {
//                textView.setText(String.valueOf(value));
//            }
//        });
    }
}