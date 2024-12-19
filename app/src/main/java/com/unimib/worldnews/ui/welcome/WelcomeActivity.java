package com.unimib.worldnews.ui.welcome;

import android.os.Bundle;
import android.util.Log;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.unimib.worldnews.R;

public class WelcomeActivity extends AppCompatActivity {

    public static final String TAG = WelcomeActivity.class.getName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);

        //Log.i(TAG, FirebaseAuth.getInstance().getCurrentUser().getEmail());

    }
}