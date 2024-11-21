package com.unimib.worldnews.ui.welcome.fragment;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

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

        return view;
    }


}