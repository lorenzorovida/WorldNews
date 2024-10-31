package com.unimib.worldnews;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.GridLayout;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.card.MaterialCardView;

public class PickCategoriesActivity extends AppCompatActivity {

    public static final String TAG = PickCategoriesActivity.class.getName();
    private MaterialCardView technologyCard, scienceCard, healthCard, generalCard, entertainmentCard, businessCard, sportsCard ;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_pick_categories);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Non bellissima soluzione, ma per ora va bene così

        technologyCard = findViewById(R.id.card_technology);
        scienceCard = findViewById(R.id.card_science);
        healthCard = findViewById(R.id.card_health);
        generalCard = findViewById(R.id.card_general);
        entertainmentCard = findViewById(R.id.card_entertainment);
        businessCard = findViewById(R.id.card_business);
        sportsCard = findViewById(R.id.card_sports);

        technologyCard.setOnClickListener(view -> {
            technologyCard.setChecked(!technologyCard.isChecked());
        });

        scienceCard.setOnClickListener(view -> {
            scienceCard.setChecked(!scienceCard.isChecked());
        });

        healthCard.setOnClickListener(view -> {
            healthCard.setChecked(!healthCard.isChecked());
        });

        generalCard.setOnClickListener(view -> {
            generalCard.setChecked(!generalCard.isChecked());
        });

        entertainmentCard.setOnClickListener(view -> {
            entertainmentCard.setChecked(!entertainmentCard.isChecked());
        });

        businessCard.setOnClickListener(view -> {
            businessCard.setChecked(!businessCard.isChecked());
        });

        sportsCard.setOnClickListener(view -> {
            sportsCard.setChecked(!sportsCard.isChecked());
        });



    }
}