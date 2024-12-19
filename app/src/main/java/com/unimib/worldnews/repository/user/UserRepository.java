package com.unimib.worldnews.repository.user;

import androidx.lifecycle.MutableLiveData;

import com.unimib.worldnews.model.Article;
import com.unimib.worldnews.model.ArticleAPIResponse;
import com.unimib.worldnews.model.Result;
import com.unimib.worldnews.model.User;
import com.unimib.worldnews.repository.article.ArticleResponseCallback;
import com.unimib.worldnews.source.article.BaseArticleLocalDataSource;
import com.unimib.worldnews.source.user.BaseUserAuthenticationRemoteDataSource;
import com.unimib.worldnews.source.user.BaseUserDataRemoteDataSource;

import java.util.List;
import java.util.Set;


/**
 * Repository class to get the user information.
 */
public class UserRepository implements IUserRepository, UserResponseCallback, ArticleResponseCallback {

    private static final String TAG = UserRepository.class.getSimpleName();

    private final BaseUserAuthenticationRemoteDataSource userRemoteDataSource;
    private final BaseUserDataRemoteDataSource userDataRemoteDataSource;
    private final BaseArticleLocalDataSource articleLocalDataSource;
    private final MutableLiveData<Result> userMutableLiveData;
    private final MutableLiveData<Result> userFavoriteNewsMutableLiveData;
    private final MutableLiveData<Result> userPreferencesMutableLiveData;

    public UserRepository(BaseUserAuthenticationRemoteDataSource userRemoteDataSource,
                          BaseUserDataRemoteDataSource userDataRemoteDataSource,
                          BaseArticleLocalDataSource newsLocalDataSource) {
        this.userRemoteDataSource = userRemoteDataSource;
        this.userDataRemoteDataSource = userDataRemoteDataSource;
        this.articleLocalDataSource = newsLocalDataSource;
        this.userMutableLiveData = new MutableLiveData<>();
        this.userPreferencesMutableLiveData = new MutableLiveData<>();
        this.userFavoriteNewsMutableLiveData = new MutableLiveData<>();
        this.userRemoteDataSource.setUserResponseCallback(this);
        this.userDataRemoteDataSource.setUserResponseCallback(this);
        this.articleLocalDataSource.setArticleCallback(this);
    }

    @Override
    public MutableLiveData<Result> getUser(String email, String password, boolean isUserRegistered) {
        if (isUserRegistered) {
            signIn(email, password);
        } else {
            signUp(email, password);
        }
        return userMutableLiveData;
    }

    @Override
    public MutableLiveData<Result> getGoogleUser(String idToken) {
        signInWithGoogle(idToken);
        return userMutableLiveData;
    }

    @Override
    public MutableLiveData<Result> getUserFavoriteNews(String idToken) {
        userDataRemoteDataSource.getUserFavoriteNews(idToken);
        return userFavoriteNewsMutableLiveData;
    }

    @Override
    public MutableLiveData<Result> getUserPreferences(String idToken) {
        userDataRemoteDataSource.getUserPreferences(idToken);
        return userPreferencesMutableLiveData;
    }

    @Override
    public User getLoggedUser() {
        return userRemoteDataSource.getLoggedUser();
    }

    @Override
    public MutableLiveData<Result> logout() {
        userRemoteDataSource.logout();
        return userMutableLiveData;
    }

    @Override
    public void signUp(String email, String password) {
        userRemoteDataSource.signUp(email, password);
    }

    @Override
    public void signIn(String email, String password) {
        userRemoteDataSource.signIn(email, password);
    }

    @Override
    public void signInWithGoogle(String token) {
        userRemoteDataSource.signInWithGoogle(token);
    }

    @Override
    public void saveUserPreferences(String favoriteCountry, Set<String> favoriteTopics, String idToken) {
        userDataRemoteDataSource.saveUserPreferences(favoriteCountry, favoriteTopics, idToken);
    }

    @Override
    public void onSuccessFromAuthentication(User user) {
        if (user != null) {
            userDataRemoteDataSource.saveUserData(user);
        }
    }

    @Override
    public void onFailureFromAuthentication(String message) {
        Result.Error result = new Result.Error(message);
        userMutableLiveData.postValue(result);
    }

    @Override
    public void onSuccessFromRemoteDatabase(User user) {
        Result.UserSuccess result = new Result.UserSuccess(user);
        userMutableLiveData.postValue(result);
    }

    @Override
    public void onSuccessFromRemoteDatabase(List<Article> articleList) {
        articleLocalDataSource.insertArticles(articleList);
    }

    @Override
    public void onSuccessFromGettingUserPreferences() {
        userPreferencesMutableLiveData.postValue(new Result.UserSuccess(null));
    }

    @Override
    public void onFailureFromRemoteDatabase(String message) {
        Result.Error result = new Result.Error(message);
        userMutableLiveData.postValue(result);
    }

    @Override
    public void onSuccessLogout() {

    }

    @Override
    public void onSuccessFromRemote(ArticleAPIResponse articleAPIResponse, long lastUpdate) {

    }

    @Override
    public void onFailureFromRemote(Exception exception) {

    }

    @Override
    public void onSuccessFromLocal(List<Article> articlesList) {

    }

    @Override
    public void onFailureFromLocal(Exception exception) {

    }

    @Override
    public void onNewsFavoriteStatusChanged(Article article, List<Article> favoriteArticles) {

    }

    @Override
    public void onNewsFavoriteStatusChanged(List<Article> news) {

    }

    @Override
    public void onDeleteFavoriteNewsSuccess(List<Article> favoriteNews) {

    }

    //@Override
    public void onSuccessFromCloudReading(List<Article> newsList) {

    }

    //@Override
    public void onSuccessFromCloudWriting(Article article) {

    }

    //@Override
    public void onFailureFromCloud(Exception exception) {

    }
}
