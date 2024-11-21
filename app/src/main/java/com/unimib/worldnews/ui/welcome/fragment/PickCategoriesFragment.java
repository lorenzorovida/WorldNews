package com.unimib.worldnews.ui.welcome.fragment;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

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


        View view = inflater.inflate(R.layout.fragment_pick_categories, container, false);

        floatingActionButton = view.findViewById(R.id.floatingActionButton);

        return view;

    }


    public void tryEnableFloatingActionButton() {
        floatingActionButton.setEnabled(!pickedCategories.isEmpty());
    }
}