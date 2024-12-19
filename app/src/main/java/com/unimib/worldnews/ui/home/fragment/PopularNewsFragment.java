package com.unimib.worldnews.ui.home.fragment;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
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
import com.unimib.worldnews.model.Result;
import com.unimib.worldnews.repository.article.ArticleRepository;
import com.unimib.worldnews.util.ServiceLocator;
import com.unimib.worldnews.ui.home.viewmodel.ArticleViewModel;
import com.unimib.worldnews.ui.home.viewmodel.ArticleViewModelFactory;
import com.unimib.worldnews.util.Constants;
import com.unimib.worldnews.util.NetworkUtil;
import com.unimib.worldnews.util.SharedPreferencesUtils;

import java.util.ArrayList;
import java.util.List;

public class PopularNewsFragment extends Fragment {

    public static final String TAG = PopularNewsFragment.class.getName();

    private static final int initialShimmerElements = 5;
    private ArticleRecyclerAdapter articleRecyclerAdapter;
    private List<Article> articleList;
    private SharedPreferencesUtils sharedPreferencesUtils;
    private ArticleViewModel articleViewModel;

    private LinearLayout shimmerLinearLayout;
    private RecyclerView recyclerView;
    private FrameLayout noInternetView;



    public PopularNewsFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        sharedPreferencesUtils = new SharedPreferencesUtils(requireActivity().getApplication());

        ArticleRepository articleRepository =
                ServiceLocator.getInstance().getArticlesRepository(
                        requireActivity().getApplication(),
                        requireActivity().getApplication().getResources().getBoolean(R.bool.debug_mode)
                );

        articleViewModel = new ViewModelProvider(
                requireActivity(),
                new ArticleViewModelFactory(articleRepository)).get(ArticleViewModel.class);

        articleList = new ArrayList<>();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_popular_news, container, false);

        shimmerLinearLayout = view.findViewById(R.id.shimmerLinearLayout);
        noInternetView = view.findViewById(R.id.noInternetMessage);

        recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(view.getContext()));

        articleList = new ArrayList<>();
        for (int i = 0; i < initialShimmerElements; i++) articleList.add(Article.getSampleArticle());

        articleRecyclerAdapter =
                new ArticleRecyclerAdapter(R.layout.card_article, articleList, true,
                        new ArticleRecyclerAdapter.OnItemClickListener() {
                            @Override
                            public void onArticleItemClick(Article article) {
                                Bundle bundle = new Bundle();
                                bundle.putParcelable(Constants.BUNDLE_KEY_CURRENT_ARTICLE,
                                        article);

                                Navigation.findNavController(view).navigate(R.id.action_popularNewsFragment_to_articleVisualizeFragment, bundle);
                            }

                            @Override
                            public void onFavoriteButtonPressed(int position) {
                                articleList.get(position).setLiked(!articleList.get(position).getLiked());
                                articleViewModel.updateArticle(articleList.get(position));
                            }
                        });

        recyclerView.setAdapter(articleRecyclerAdapter);

        String lastUpdate = "0";

        sharedPreferencesUtils = new SharedPreferencesUtils(getContext());
        if (sharedPreferencesUtils.readStringData(
                Constants.SHARED_PREFERENCES_FILENAME, Constants.SHARED_PREFERNECES_LAST_UPDATE) != null) {
            lastUpdate = sharedPreferencesUtils.readStringData(
                    Constants.SHARED_PREFERENCES_FILENAME, Constants.SHARED_PREFERNECES_LAST_UPDATE);
        }


        if (!NetworkUtil.isInternetAvailable(getContext())) {
            noInternetView.setVisibility(View.VISIBLE);
            
            //Trick to avoid doing the API call
            lastUpdate = System.currentTimeMillis() + "";
        }


        articleViewModel.getArticles("us", Long.parseLong(lastUpdate)).observe(getViewLifecycleOwner(),
                result -> {
                    if (result.isSuccess()) {
                        int initialSize = this.articleList.size();
                        this.articleList.clear();
                        this.articleList.addAll(((Result.ArticleSuccess) result).getData().getArticles());
                        articleRecyclerAdapter.notifyItemRangeInserted(initialSize, this.articleList.size());
                        recyclerView.setVisibility(View.VISIBLE);
                        shimmerLinearLayout.setVisibility(View.GONE);
                    } else {
                       Snackbar.make(view,
                                       getString(R.string.error_retireving_articles),
                                Snackbar.LENGTH_SHORT).show();
                    }
                });

        return view;
    }

}