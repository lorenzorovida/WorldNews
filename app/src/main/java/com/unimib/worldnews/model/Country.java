package com.unimib.worldnews.model;

import android.graphics.drawable.Drawable;

public class Country {
    private String name;
    private String code;
    private Drawable image;

    public Country(String name, String code, Drawable image) {
        this.name = name;
        this.code = code;
        this.image = image;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public void setImage(Drawable image) {
        this.image = image;
    }

    public Drawable getImage() {
        return image;
    }
}
