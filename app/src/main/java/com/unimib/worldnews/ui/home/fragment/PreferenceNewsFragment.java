package com.unimib.worldnews.ui.home.fragment;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.unimib.worldnews.R;
import com.unimib.worldnews.adapter.ArticleRecyclerAdapter;
import com.unimib.worldnews.database.ArticleRoomDatabase;
import com.unimib.worldnews.model.Article;
import com.unimib.worldnews.model.ArticleAPIResponse;
import com.unimib.worldnews.util.Constants;
import com.unimib.worldnews.util.JSONParserUtils;

import java.io.IOException;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link PreferenceNewsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class PreferenceNewsFragment extends Fragment {

    public static final String TAG = PreferenceNewsFragment.class.getName();


    public PreferenceNewsFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_preference_news, container, false);

        RecyclerView recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(view.getContext()));

        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(
                recyclerView.getContext(), DividerItemDecoration.VERTICAL);

        recyclerView.addItemDecoration(dividerItemDecoration);

        JSONParserUtils jsonParserUtils = new JSONParserUtils(getContext());

        try {
            ArticleAPIResponse response = jsonParserUtils.parseJSONFileWithGSon(Constants.SAMPLE_JSON_FILENAME);

            ArticleRoomDatabase.getDatabase(getContext()).newsDao().insertAll(response.getArticles());

            List<Article> articleList = ArticleRoomDatabase.getDatabase(getContext()).newsDao().getAll();

            ArticleRecyclerAdapter adapter =
                    new ArticleRecyclerAdapter(R.layout.card_article, articleList, true);

            recyclerView.setAdapter(adapter);


        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return view;
    }
}