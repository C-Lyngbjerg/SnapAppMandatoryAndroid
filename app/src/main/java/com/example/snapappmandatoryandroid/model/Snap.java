package com.example.snapappmandatoryandroid.model;

public class Snap {
    private String id;
    private String text;
    private String imageRef;

    public Snap(String id, String text, String imageRef) {
        this.id = id;
        this.text = text;
        this.imageRef = imageRef;
    }

    public String getImageRef() {
        return imageRef;
    }

    public void setImageRef(String imageRef) {
        this.imageRef = imageRef;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
}
