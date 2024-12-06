package com.unimib.worldnews.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.navigation.Navigation;

import com.google.android.material.card.MaterialCardView;
import com.unimib.worldnews.R;
import com.unimib.worldnews.model.Category;
import com.unimib.worldnews.ui.welcome.fragment.PickCategoriesFragment;

import java.util.ArrayList;

public class CategoriesAdapter extends ArrayAdapter<Category> {

    private int layout;
    private ArrayList<Category> categoriesList;
    private PickCategoriesFragment fragment;

    public CategoriesAdapter(@NonNull Context context, @NonNull int layout, @NonNull ArrayList<Category> categoriesList, PickCategoriesFragment fragment) {
        super(context, layout, categoriesList);
        this.layout = layout;
        this.categoriesList = categoriesList;
        this.fragment = fragment;
    }


    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        convertView = LayoutInflater.from(getContext()).inflate(layout, parent, false);

        TextView title = convertView.findViewById(R.id.textView);
        ImageView imageView = convertView.findViewById(R.id.imageView);

        title.setText(categoriesList.get(position).getName());
        imageView.setImageDrawable(categoriesList.get(position).getImage());

        MaterialCardView cardView = (MaterialCardView) convertView;

        cardView.setOnClickListener(view -> {
            cardView.setChecked(!cardView.isChecked());

            if (cardView.isChecked()) {
                fragment.addCategory(categoriesList.get(position));
            } else {
                fragment.removeCategory(categoriesList.get(position));
            }

            fragment.tryEnableFloatingActionButton();
        });

        return convertView;
    }
}
