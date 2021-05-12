package com.example.snapappmandatoryandroid;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.InputType;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;

import com.example.snapappmandatoryandroid.adapter.MyAdapter;
import com.example.snapappmandatoryandroid.model.Snap;
import com.example.snapappmandatoryandroid.repo.Repo;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements Updatable{
    List<Snap> items = new ArrayList<>();
    ListView listView;
    MyAdapter myAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setupListView();
        Repo.r().setup(this, items);

    }

    private void setupListView() {
        listView = findViewById(R.id.listView1);
        myAdapter = new MyAdapter(items, this);
        listView.setAdapter(myAdapter);
        listView.setOnItemClickListener((parent, view, position, id) -> {
            System.out.println("click on row: " + position);
            Intent intent = new Intent(MainActivity.this, DetailActivity.class);
            intent.putExtra("id", items.get(position).getId());
            startActivity(intent);
        });
    }

    public void snapPictureButtonPressed(View view){
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        try {
            startActivityForResult(takePictureIntent, 1);
        } catch (ActivityNotFoundException e) {
            System.out.println(e);
        }
    }
    Bitmap bitmapToUpload;


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == RESULT_OK ) {
            Bitmap bitmap = (Bitmap) data.getExtras().get("data");
            Bundle extras = data.getExtras();
            bitmapToUpload =  bitmap;
            getTextForImage();
        }
    }

    // Creates a popup which asks for some texts which will be added to the image
    // - Christoffer
    private void getTextForImage() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Put text on image");

        final EditText input = new EditText(this);
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        builder.setView(input);
        builder.setPositiveButton("OK", (dialog, which) -> {
            drawTextToBitmap(input.getText().toString());
        });
        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());

        builder.show();
    }

    // The text from the method above is added to the bitmapped image and returned
    // - Cecilie
    public Bitmap drawTextToBitmap(String gText) {
        Bitmap.Config bitmapConfig = bitmapToUpload.getConfig();
        if(bitmapConfig == null) {
            bitmapConfig = Bitmap.Config.ARGB_8888;
        }
        bitmapToUpload = bitmapToUpload.copy(bitmapConfig, true);
        Canvas canvas = new Canvas(bitmapToUpload);
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);// new antialised Paint
        paint.setColor(Color.rgb(161, 161, 161));
        paint.setTextSize((int) (20)); // text size in pixels
        paint.setShadowLayer(1f, 0f, 1f, Color.WHITE); // text shadow
        canvas.drawText(gText, 10, 100, paint);

        Repo.r().uploadBitmap(gText, bitmapToUpload );
        return bitmapToUpload;
    }


    @Override
    public void update(Object o) {
        myAdapter.notifyDataSetChanged();
    }
}