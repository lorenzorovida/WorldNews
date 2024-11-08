package com.unimib.worldnews.ui.welcome.fragments;

import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridLayout;
import android.widget.TextView;
import android.widget.ImageView;

import com.google.android.material.card.MaterialCardView;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.unimib.worldnews.R;

import java.util.ArrayList;


public class PickCategoriesFragment extends Fragment {

    public static final String TAG = PickCategoriesFragment.class.getName();

    public ArrayList<String> pickedCategories = new ArrayList<String>();

    public ExtendedFloatingActionButton floatingActionButton;

    public PickCategoriesFragment() {}


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        Bundle bundle = getArguments();
        if (bundle != null) {
            Log.d(TAG, "Ricevuto " + bundle.getString(getString(R.string.KEY_SAVED_COUNTRY)));
        }

        View view = inflater.inflate(R.layout.fragment_pick_categories, container, false);

        GridLayout gridLayout = view.findViewById(R.id.gridLayout);

        gridLayout.removeAllViews();

        String[] categories = getResources().getStringArray(R.array.categories);
        try (TypedArray categoryIcons = getResources().obtainTypedArray(R.array.category_icons)) {
            assert categories.length == categoryIcons.length();

            for (int i = 0; i < categories.length; i++) {
                Log.d(TAG, "Aggiungo la categoria \"" + categories[i] + "\" con drawable id: " + categoryIcons.getDrawable(i));
                gridLayout.addView(generateCategoryCard(inflater, container, categories[i], categoryIcons.getDrawable(i)));
            }
        }

        floatingActionButton = view.findViewById(R.id.floatingActionButton);

        return view;

    }

    /*
     * Questa funzione prende in input un LayoutInflater, un ViewGroup e un titolo. Intanzia una nuova
     * View di tipo MaterialCardView usando il LayoutInflater, usando l'id R.layout.country_card.
     * Successivamente, imposta le dimensioni e i margini compatibilmente con il GridLayout (e.g.,
     * weight = 1), imposta il titolo della card accedendo alla TextView, e infine imposta il
     * listener al click per spostarsi al prossimo fragment
     */
    public View generateCategoryCard(LayoutInflater inflater, ViewGroup container, String title, Drawable drawable) {
        MaterialCardView categoryCard = (MaterialCardView) inflater.inflate(R.layout.card_category, container, false);

        GridLayout.LayoutParams layoutParams = new GridLayout.LayoutParams(
                GridLayout.spec(GridLayout.UNDEFINED, 1f),
                GridLayout.spec(GridLayout.UNDEFINED, 1f)
        );

        layoutParams.width = 0;

        int margin = (int) getResources().getDimension(R.dimen.margin_small);
        layoutParams.setMargins(margin, margin, margin, margin);

        categoryCard.setLayoutParams(layoutParams);

        TextView textView = categoryCard.findViewById(R.id.textView);
        textView.setText(title);

        ImageView imageView = categoryCard.findViewById(R.id.imageView);
        imageView.setImageDrawable(drawable);

        categoryCard.setOnClickListener(v -> {
            String category = ((TextView) v.findViewById(R.id.textView)).getText().toString();

            if (!categoryCard.isChecked()) {
                pickedCategories.add(category);
            } else {
                pickedCategories.remove(category);
            }

            categoryCard.setChecked(!categoryCard.isChecked());

            tryEnableFloatingActionButton();
        });

        return categoryCard;
    }

    public void tryEnableFloatingActionButton() {
        floatingActionButton.setEnabled(!pickedCategories.isEmpty());
    }
}