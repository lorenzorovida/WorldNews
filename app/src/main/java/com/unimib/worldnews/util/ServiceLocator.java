package com.unimib.worldnews.util;

import android.app.Application;

import com.unimib.worldnews.R;
import com.unimib.worldnews.database.ArticleRoomDatabase;
import com.unimib.worldnews.repository.ArticleRepository;
import com.unimib.worldnews.service.ArticleAPIService;
import com.unimib.worldnews.source.ArticleLocalDataSource;
import com.unimib.worldnews.source.ArticleMockDataSource;
import com.unimib.worldnews.source.ArticleRemoteDataSource;
import com.unimib.worldnews.source.BaseArticleLocalDataSource;
import com.unimib.worldnews.source.BaseArticleRemoteDataSource;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ServiceLocator {

    private static volatile ServiceLocator INSTANCE = null;

    private ServiceLocator() {}

    /**
     * Returns an instance of ServiceLocator class.
     * @return An instance of ServiceLocator.
     */
    public static ServiceLocator getInstance() {
        if (INSTANCE == null) {
            synchronized(ServiceLocator.class) {
                if (INSTANCE == null) {
                    INSTANCE = new ServiceLocator();
                }
            }
        }
        return INSTANCE;
    }

    OkHttpClient client = new OkHttpClient.Builder()
            .addInterceptor(chain -> {
                Request request = chain.request().newBuilder()
                        .header("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/91.0.4472.124 Safari/537.36")
                        .build();
                return chain.proceed(request);
            })
            .build();

    /**
     * Returns an instance of NewsApiService class using Retrofit.
     * @return an instance of NewsApiService.
     */
    public ArticleAPIService getArticleAPIService() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Constants.NEWS_API_BASE_URL)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create()).build();
        return retrofit.create(ArticleAPIService.class);
    }

    /**
     * Returns an instance of NewsRoomDatabase class to manage Room database.
     * @param application Param for accessing the global application state.
     * @return An instance of NewsRoomDatabase.
     */
    public ArticleRoomDatabase getNewsDao(Application application) {
        return ArticleRoomDatabase.getDatabase(application);
    }

    /**
     * Returns an instance of INewsRepositoryWithLiveData.
     * @param application Param for accessing the global application state.
     * @param debugMode Param to establish if the application is run in debug mode.
     * @return An instance of INewsRepositoryWithLiveData.
     */
    public ArticleRepository getArticlesRepository(Application application, boolean debugMode) {
        BaseArticleRemoteDataSource newsRemoteDataSource;
        BaseArticleLocalDataSource newsLocalDataSource;
        SharedPreferencesUtils sharedPreferencesUtil = new SharedPreferencesUtils(application);

        if (debugMode) {
            JSONParserUtils jsonParserUtil = new JSONParserUtils(application);
            newsRemoteDataSource =
                    new ArticleMockDataSource(jsonParserUtil);
        } else {
            newsRemoteDataSource =
                    new ArticleRemoteDataSource(application.getString(R.string.news_api_key));
        }

        newsLocalDataSource = new ArticleLocalDataSource(getNewsDao(application), sharedPreferencesUtil);

        return new ArticleRepository(newsRemoteDataSource, newsLocalDataSource);
    }
}
