package com.rizilab.keretaku;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class splashactivitiy extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splashactivitiy);
        Thread thread = new Thread() {
            @Override
            public void run() {
                try {
                    sleep(3000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }finally {{
                    startActivity(new Intent(splashactivitiy.this,MainActivity.class));
                    finish();
                }
                }
            }
        };
        thread.start();
    }
}
