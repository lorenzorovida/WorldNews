package com.unimib.worldnews.adapter;


import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.unimib.worldnews.R;
import com.unimib.worldnews.database.ArticleRoomDatabase;
import com.unimib.worldnews.model.Article;
import com.unimib.worldnews.ui.home.HomeActivity;
import com.unimib.worldnews.util.DateTimeUtil;

import java.util.List;

public class ArticleRecyclerAdapter extends RecyclerView.Adapter<ArticleRecyclerAdapter.ViewHolder> {

    public interface OnItemClickListener {
        void onArticleItemClick(Article article);
        void onFavoriteButtonPressed(int position);
    }

    private int layout;
    private List<Article> articleList;
    private boolean heartVisible;
    private Context context;
    private final OnItemClickListener onItemClickListener;

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private final TextView textViewTitle;
        private final TextView textViewAuthor;
        private final TextView textViewTime;
        private final CheckBox favoriteCheckbox;
        private final ImageView imageView;
        private final Button menuButton;

        public ViewHolder(View view) {
            super(view);

            textViewTitle = view.findViewById(R.id.textViewTitle);
            textViewAuthor =  view.findViewById(R.id.textViewAuthor);
            textViewTime = view.findViewById(R.id.textViewTime);
            favoriteCheckbox = view.findViewById(R.id.favoriteButton);
            imageView = view.findViewById(R.id.imageView);
            menuButton = view.findViewById(R.id.buttonMenu);

            favoriteCheckbox.setOnClickListener(this);
            view.setOnClickListener(this);
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

        public Button getMenuButton() {
            return menuButton;
        }

        public ImageView getImageView() { return  imageView; }

        @Override
        public void onClick(View v) {
            if (v.getId() == R.id.favoriteButton) {
                //setImageViewFavoriteNews(!newsList.get(getAdapterPosition()).isFavorite());
                onItemClickListener.onFavoriteButtonPressed(getAdapterPosition());
            } else {
                onItemClickListener.onArticleItemClick(articleList.get(getAdapterPosition()));
            }
        }

    }

    public ArticleRecyclerAdapter(int layout, List<Article> articleList, boolean heartVisible, OnItemClickListener onItemClickListener) {
        this.layout = layout;
        this.articleList = articleList;
        this.heartVisible = heartVisible;
        this.onItemClickListener = onItemClickListener;
    }


    @NonNull
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

        viewHolder.getMenuButton().setOnClickListener(view -> {
            BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(view.getContext());
            bottomSheetDialog.setContentView(LayoutInflater.from(view.getContext()).inflate(R.layout.bottom_sheet_card_article, null));
            bottomSheetDialog.findViewById(R.id.shareButton).setOnClickListener(view1 -> {
                Intent sendIntent = new Intent();
                sendIntent.setAction(Intent.ACTION_SEND);
                sendIntent.putExtra(Intent.EXTRA_TEXT, "Check this article: " + articleList.get(position).getUrl());
                sendIntent.setType("text/plain");

                Intent shareIntent = Intent.createChooser(sendIntent, "");
                context.startActivity(shareIntent);

                bottomSheetDialog.dismiss();
            });

            if (articleList.get(position).getLiked())
                bottomSheetDialog.findViewById(R.id.deleteFavoritesButton).setVisibility(View.VISIBLE);
            else
                bottomSheetDialog.findViewById(R.id.deleteFavoritesButton).setVisibility(View.GONE);

            bottomSheetDialog.show();
        });

        Glide.with(context)
                .load(articleList.get(position).getUrlToImage())
                .placeholder(new ColorDrawable(context.getColor(R.color.placeholder_gray)))
                .into(viewHolder.getImageView());

        if (!heartVisible) {
            viewHolder.getFavoriteCheckbox().setVisibility(View.INVISIBLE);
        }
    }


    @Override
    public int getItemCount() {
        return articleList.size();
    }
}

