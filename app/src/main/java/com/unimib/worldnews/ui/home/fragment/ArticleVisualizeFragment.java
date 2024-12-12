package com.unimib.worldnews.ui.home.fragment;

import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.unimib.worldnews.R;
import com.unimib.worldnews.model.Article;
import com.unimib.worldnews.util.Constants;

public class ArticleVisualizeFragment extends Fragment {

    private static final String TAG = ArticleVisualizeFragment.class.getSimpleName();
    private Article currentArticle;

    public ArticleVisualizeFragment() {
        // Required empty public constructor
    }



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        currentArticle = getArguments().getParcelable(Constants.BUNDLE_KEY_CURRENT_ARTICLE);

        ((AppCompatActivity)getActivity()).getSupportActionBar().setTitle(currentArticle.getTitle());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_article_visualize, container, false);

        ((TextView) view.findViewById(R.id.textViewTitle)).setText(currentArticle.getTitle());
        ((TextView) view.findViewById(R.id.textViewBody)).setText(currentArticle.getContent());
        ImageView imageView = view.findViewById(R.id.imageView);

        Glide.with(getContext())
                .load(currentArticle.getUrlToImage())
                .placeholder(new ColorDrawable(getContext().getColor(R.color.placeholder_gray)))
                .into(imageView);


        return view;
    }

}