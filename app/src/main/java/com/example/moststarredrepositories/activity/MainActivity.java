package com.example.moststarredrepositories.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.example.moststarredrepositories.R;

/**
 * @author rachit
 */
public class MainActivity extends AppCompatActivity {

    private static final String LOG_TAG = MainActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Set ActionBar label
        this.setTitle(getResources().getString(R.string.app_name));
    }
}
