package com.example.snapappmandatoryandroid;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.example.snapappmandatoryandroid.model.Snap;
import com.example.snapappmandatoryandroid.repo.Repo;

public class DetailActivity extends AppCompatActivity implements TaskListener {
    private ImageView imageView;
    private Snap currentSnap;
    private String snapID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        snapID = getIntent().getStringExtra("id");
        currentSnap = Repo.r().getNoteWith(snapID);
        imageView = findViewById(R.id.imageView);
        Repo.r().downloadBitmap(currentSnap,this);
    }

    /*
    To make sure that our app removes the snaps after seeing them, we use the delete() method onClick
     for the back button in activity_detail.
     after calling the two delete methods from Repo we use the finish() method to exit out of the activity.
     - Christoffer */
    public void delete(View view){
        Repo.r().deleteNote(currentSnap.getId());
        Repo.r().deleteImage(currentSnap.getImageRef());
        finish();
    }

    @Override
    public void receive(byte[] bytes) {
        Bitmap bmp = BitmapFactory.decodeByteArray(bytes,0,bytes.length);
        imageView.setImageBitmap(bmp);
    }
}