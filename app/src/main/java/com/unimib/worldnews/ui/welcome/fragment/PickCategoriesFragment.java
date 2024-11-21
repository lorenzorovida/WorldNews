package com.unimib.worldnews.ui.welcome.fragment;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.ArraySet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;

import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.unimib.worldnews.R;
import com.unimib.worldnews.adapter.CategoriesAdapter;
import com.unimib.worldnews.adapter.CountryAdapter;
import com.unimib.worldnews.model.Category;
import com.unimib.worldnews.ui.home.HomeActivity;
import com.unimib.worldnews.ui.welcome.WelcomeActivity;
import com.unimib.worldnews.util.Constants;
import com.unimib.worldnews.util.SharedPreferencesUtils;

import java.util.ArrayList;
import java.util.Set;


public class PickCategoriesFragment extends Fragment {

    public static final String TAG = PickCategoriesFragment.class.getName();

    public ArrayList<Category> pickedCategories = new ArrayList<Category>();

    public ExtendedFloatingActionButton floatingActionButton;

    public PickCategoriesFragment() {}


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_pick_categories, container, false);

        CategoriesAdapter adapter = new CategoriesAdapter(getContext(),
                R.layout.card_category,
                Constants.generateCategoriesList(getContext()),
                this
        );

        GridView gridView = view.findViewById(R.id.gridView);
        gridView.setAdapter(adapter);

        floatingActionButton = view.findViewById(R.id.floatingActionButton);

        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Set<String> categoriesCodes = new ArraySet<>();

                for (int i = 0; i < categoriesCodes.size(); i++) {
                    categoriesCodes.add(pickedCategories.get(i).getCode());
                }

                SharedPreferencesUtils sharedPreferencesUtils = new SharedPreferencesUtils(getContext());

                sharedPreferencesUtils.writeStringSetData(Constants.SHARED_PREFERENCES_FILENAME,
                        Constants.SHARED_PREFERENCES_CATEGORIES_OF_INTEREST,
                        categoriesCodes);

                Intent intent = new Intent(getContext(), HomeActivity.class);
                startActivity(intent);

            }
        });

        return view;

    }

    public void addCategory(Category category) {
        pickedCategories.add(category);
    }

    public void removeCategory(Category category) {
        pickedCategories.remove(category);
    }

    public void tryEnableFloatingActionButton() {
        floatingActionButton.setEnabled(!pickedCategories.isEmpty());
    }
}