package com.unimib.worldnews.adapter;


import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.unimib.worldnews.R;
import com.unimib.worldnews.database.ArticleRoomDatabase;
import com.unimib.worldnews.model.Article;
import com.unimib.worldnews.util.Constants;

import java.util.List;

public class ArticleRecyclerAdapter extends RecyclerView.Adapter<ArticleRecyclerAdapter.ViewHolder> {

    private int layout;
    private List<Article> articleList;
    private boolean heartVisible;

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView textViewTitle;
        private final TextView textViewAuthor;
        private final CheckBox favoriteCheckbox;

        public ViewHolder(View view) {
            super(view);
            // Define click listener for the ViewHolder's View

            textViewTitle = view.findViewById(R.id.textViewTitle);
            textViewAuthor =  view.findViewById(R.id.textViewAuthor);
            favoriteCheckbox = view.findViewById(R.id.favoriteButton);
        }

        public TextView getTextViewTitle() {
            return textViewTitle;
        }

        public TextView getTextViewAuthor() {
            return textViewAuthor;
        }

        public CheckBox getFavoriteCheckbox() {
            return favoriteCheckbox;
        }
    }

    public ArticleRecyclerAdapter(int layout, List<Article> articleList, boolean heartVisible) {
        this.layout = layout;
        this.articleList = articleList;
        this.heartVisible = heartVisible;
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        // Create a new view, which defines the UI of the list item
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(layout, viewGroup, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, final int position) {
        viewHolder.getTextViewTitle().setText(articleList.get(position).getTitle());
        viewHolder.getTextViewAuthor().setText(articleList.get(position).getAuthor());
        viewHolder.getFavoriteCheckbox().setChecked(articleList.get(position).getLiked());

        viewHolder.getFavoriteCheckbox().setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                Article currentArticle = articleList.get(viewHolder.getAdapterPosition());

                currentArticle.setLiked(b);

                ArticleRoomDatabase.getDatabase(viewHolder.getTextViewAuthor().getContext()).
                            newsDao().updateArticle(currentArticle);
            }
        });

        if (heartVisible == false) {
            viewHolder.getFavoriteCheckbox().setVisibility(View.INVISIBLE);
        }

    }


    @Override
    public int getItemCount() {
        return articleList.size();
    }
}

