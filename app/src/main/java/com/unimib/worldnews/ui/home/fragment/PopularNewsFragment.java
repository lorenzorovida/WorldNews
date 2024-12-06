package com.unimib.worldnews.ui.home.fragment;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import com.google.android.material.snackbar.Snackbar;
import com.unimib.worldnews.R;
import com.unimib.worldnews.adapter.ArticleRecyclerAdapter;
import com.unimib.worldnews.model.Article;
import com.unimib.worldnews.repository.ArticleMockRepository;
import com.unimib.worldnews.repository.ArticleAPIRepository;
import com.unimib.worldnews.repository.IArticleRepository;
import com.unimib.worldnews.util.Constants;
import com.unimib.worldnews.util.NetworkUtil;
import com.unimib.worldnews.util.ResponseCallback;
import com.unimib.worldnews.util.SharedPreferencesUtils;

import java.util.ArrayList;
import java.util.List;

public class PopularNewsFragment extends Fragment implements ResponseCallback {

    public static final String TAG = PopularNewsFragment.class.getName();

    private static final int viewedElements = 20;
    private ArticleRecyclerAdapter articleRecyclerAdapter;
    private List<Article> articleList;
    private IArticleRepository articleRepository;
    private SharedPreferencesUtils sharedPreferencesUtils;

    private LinearLayout shimmerLinearLayout;
    private RecyclerView recyclerView;
    private FrameLayout noInternetView;



    public PopularNewsFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (requireActivity().getResources().getBoolean(R.bool.debug_mode)) {
            articleRepository = new ArticleMockRepository(requireActivity().getApplication(), this);
        } else {
            articleRepository = new ArticleAPIRepository(requireActivity().getApplication(), this);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_preference_news, container, false);

        shimmerLinearLayout = view.findViewById(R.id.shimmerLinearLayout);
        noInternetView = view.findViewById(R.id.noInternetMessage);

        recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(view.getContext()));

        articleList = new ArrayList<>();
        for (int i = 0; i < viewedElements; i++) articleList.add(Article.getSampleArticle());

        articleRecyclerAdapter =
                new ArticleRecyclerAdapter(R.layout.card_article, articleList, true);

        recyclerView.setAdapter(articleRecyclerAdapter);

        String lastUpdate = "0";

        sharedPreferencesUtils = new SharedPreferencesUtils(getContext());
        if (sharedPreferencesUtils.readStringData(
                Constants.SHARED_PREFERENCES_FILENAME, Constants.SHARED_PREFERNECES_LAST_UPDATE) != null) {
            lastUpdate = sharedPreferencesUtils.readStringData(
                    Constants.SHARED_PREFERENCES_FILENAME, Constants.SHARED_PREFERNECES_LAST_UPDATE);
        }

        articleRepository.fetchArticles("us", Constants.TOP_HEADLINES_PAGE_SIZE_VALUE, Long.parseLong(lastUpdate));

        if (!NetworkUtil.isInternetAvailable(getContext())) {
            noInternetView.setVisibility(View.VISIBLE);
        }

        return view;
    }

    @Override
    public void onSuccess(List<Article> articleList, long lastUpdate) {
        sharedPreferencesUtils.writeStringData(Constants.SHARED_PREFERENCES_FILENAME,
                Constants.SHARED_PREFERNECES_LAST_UPDATE,
                String.valueOf(lastUpdate));
        this.articleList.clear();
        this.articleList.addAll(articleList);
        requireActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                articleRecyclerAdapter.notifyDataSetChanged();
                recyclerView.setVisibility(View.VISIBLE);
                shimmerLinearLayout.setVisibility(View.GONE);
            }
        });
    }

    @Override
    public void onFailure(String errorMessage) {
        Snackbar.make(requireActivity().findViewById(android.R.id.content),
                errorMessage, Snackbar.LENGTH_LONG).show();
    }

}