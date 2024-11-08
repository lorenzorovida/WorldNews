package com.unimib.worldnews.ui.welcome.fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridLayout;
import android.widget.TextView;

import com.google.android.material.card.MaterialCardView;
import com.unimib.worldnews.R;

public class PickCountryFragment extends Fragment {

    public static final String TAG = PickCategoriesFragment.class.getName();

    public PickCountryFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_pick_country, container, false);

        GridLayout gridLayout = view.findViewById(R.id.gridLayout);

        gridLayout.removeAllViews();

        String[] countries = getResources().getStringArray(R.array.countries);

        for (String country : countries) {
            gridLayout.addView(generateCountryCard(inflater, container, country));
        }

        return view;
    }

    /*
     * Questa funzione prende in input un LayoutInflater, un ViewGroup e un titolo. Intanzia una nuova
     * View di tipo MaterialCardView usando il LayoutInflater, usando l'id R.layout.country_card.
     * Successivamente, imposta le dimensioni e i margini compatibilmente con il GridLayout (e.g.,
     * weight = 1), imposta il titolo della card accedendo alla TextView, e infine imposta il
     * listener al click per spostarsi al prossimo fragment
     */
    public View generateCountryCard(LayoutInflater inflater, ViewGroup container, String title) {
        MaterialCardView countryCard = (MaterialCardView) inflater.inflate(R.layout.card_country, container, false);

        GridLayout.LayoutParams layoutParams = new GridLayout.LayoutParams(
                GridLayout.spec(GridLayout.UNDEFINED, 1f),
                GridLayout.spec(GridLayout.UNDEFINED, 1f)
        );

        layoutParams.width = 0;

        int margin = (int) getResources().getDimension(R.dimen.margin_small);
        layoutParams.setMargins(margin, margin, margin, margin);

        countryCard.setLayoutParams(layoutParams);

        TextView textView = countryCard.findViewById(R.id.textView);
        textView.setText(title);

        countryCard.setOnClickListener(v -> {
            Bundle bundle = new Bundle();
            bundle.putString(getString(R.string.KEY_SAVED_COUNTRY), ((TextView) v.findViewById(R.id.textView)).getText().toString());

            Navigation.findNavController(v).navigate(
                    R.id.action_pickCountryFragment_to_pickCategoriesFragment,
                    bundle);

        });

        return countryCard;
    }
}