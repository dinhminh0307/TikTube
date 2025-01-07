package com.example.tiktube.backend.helpers;

import android.content.Context;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.model.GlideUrl;
import com.bumptech.glide.load.model.LazyHeaders;
import com.example.tiktube.R;
import com.example.tiktube.backend.models.User;

public class ImageBuilder {

    private Context context;

    public ImageBuilder(Context context) {
        this.context = context;
    }

    public void loadImage(ImageView imageView, User user) {
        if (user.getImageUrl() != null && !user.getImageUrl().isEmpty()) {
            GlideUrl glideUrl = new GlideUrl(user.getImageUrl(), new LazyHeaders.Builder()
                    .addHeader("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/58.0.3029.110 Safari/537.3")
                    .build());

            Glide.with(context)
                    .load(glideUrl)
                    .placeholder(R.drawable.ic_account_circle_foreground)
                    .error(R.drawable.ic_account_circle_foreground)
                    .into(imageView);
        } else {
            imageView.setImageResource(R.drawable.ic_account_circle_foreground);
        }
    }
}
