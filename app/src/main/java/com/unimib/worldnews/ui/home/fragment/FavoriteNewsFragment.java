package com.unimib.worldnews.ui.home.fragment;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.unimib.worldnews.R;
import com.unimib.worldnews.adapter.ArticleRecyclerAdapter;
import com.unimib.worldnews.database.ArticleRoomDatabase;
import com.unimib.worldnews.model.Article;

import java.util.List;

public class FavoriteNewsFragment extends Fragment {


    public FavoriteNewsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_favorite_news, container, false);

        RecyclerView recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(view.getContext()));

        List<Article> articleList =
                ArticleRoomDatabase.getDatabase(getContext())
                        .articleDao().getLiked();

        ArticleRecyclerAdapter adapter =
                    new ArticleRecyclerAdapter(R.layout.card_article, articleList, false);

        recyclerView.setAdapter(adapter);

        return view;

    }
}