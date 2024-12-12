package com.unimib.worldnews.source;

import static com.unimib.worldnews.util.Constants.API_KEY_ERROR;

import com.unimib.worldnews.model.ArticleAPIResponse;
import com.unimib.worldnews.util.Constants;
import com.unimib.worldnews.util.JSONParserUtils;

import org.json.JSONException;

import java.io.IOException;


/**
 * Class to get the news from a local JSON file to simulate the Web Service response.
 */
public class ArticleMockDataSource extends BaseArticleRemoteDataSource {

    private final JSONParserUtils jsonParserUtil;

    public ArticleMockDataSource(JSONParserUtils jsonParserUtil) {
        this.jsonParserUtil = jsonParserUtil;
    }

    @Override
    public void getArticles(String country) {
        ArticleAPIResponse articleAPIResponse = null;

        try {
            articleAPIResponse = jsonParserUtil.parseJSONFileWithGSon(Constants.SAMPLE_JSON_FILENAME);
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (articleAPIResponse != null) {
            articleCallback.onSuccessFromRemote(articleAPIResponse, System.currentTimeMillis());
        } else {
            articleCallback.onFailureFromRemote(new Exception(API_KEY_ERROR));
        }
    }
}
