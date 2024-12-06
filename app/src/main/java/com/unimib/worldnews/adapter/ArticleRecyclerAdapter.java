package com.unimib.worldnews.adapter;


import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.unimib.worldnews.R;
import com.unimib.worldnews.database.ArticleRoomDatabase;
import com.unimib.worldnews.model.Article;
import com.unimib.worldnews.util.DateTimeUtil;

import java.util.List;

public class ArticleRecyclerAdapter extends RecyclerView.Adapter<ArticleRecyclerAdapter.ViewHolder> {

    private int layout;
    private List<Article> articleList;
    private boolean heartVisible;
    private Context context;

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView textViewTitle;
        private final TextView textViewAuthor;
        private final TextView textViewTime;
        private final CheckBox favoriteCheckbox;
        private final ImageView imageView;

        public ViewHolder(View view) {
            super(view);

            textViewTitle = view.findViewById(R.id.textViewTitle);
            textViewAuthor =  view.findViewById(R.id.textViewAuthor);
            textViewTime = view.findViewById(R.id.textViewTime);
            favoriteCheckbox = view.findViewById(R.id.favoriteButton);
            imageView = view.findViewById(R.id.imageView);
        }

        public TextView getTextViewTitle() {
            return textViewTitle;
        }

        public TextView getTextViewAuthor() {
            return textViewAuthor;
        }

        public TextView getTextViewTime() {
            return textViewTime;
        }

        public CheckBox getFavoriteCheckbox() {
            return favoriteCheckbox;
        }

        public ImageView getImageView() { return  imageView; }

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

        if (this.context == null) this.context = viewGroup.getContext();

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, final int position) {
        viewHolder.getTextViewTitle().setText(articleList.get(position).getTitle());
        viewHolder.getTextViewAuthor().setText(articleList.get(position).getAuthor());
        viewHolder.getFavoriteCheckbox().setChecked(articleList.get(position).getLiked());
        viewHolder.getTextViewTime().setText(DateTimeUtil.getDateDelta(articleList.get(position).getPublishedAt()));

        Glide.with(context)
                .load(articleList.get(position).getUrlToImage())
                .placeholder(new ColorDrawable(context.getColor(R.color.placeholder_gray)))
                .into(viewHolder.getImageView());

        viewHolder.getFavoriteCheckbox().setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                Article currentArticle = articleList.get(viewHolder.getAdapterPosition());

                currentArticle.setLiked(b);

                ArticleRoomDatabase.getDatabase(viewHolder.getTextViewAuthor().getContext()).
                        articleDao().updateArticle(currentArticle);
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

