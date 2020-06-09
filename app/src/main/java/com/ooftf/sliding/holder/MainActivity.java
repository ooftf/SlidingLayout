package com.ooftf.sliding.holder;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;

import com.ooftf.sliding.SlidingLayout;

public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        findViewById(R.id.button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((SlidingLayout) findViewById(R.id.sliding)).smoothTurn();
            }
        });
    }
}
