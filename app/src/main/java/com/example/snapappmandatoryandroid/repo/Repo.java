package com.example.snapappmandatoryandroid.repo;

import android.graphics.Bitmap;


import com.example.snapappmandatoryandroid.model.Snap;
import com.example.snapappmandatoryandroid.TaskListener;
import com.example.snapappmandatoryandroid.Updatable;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class Repo {
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private FirebaseStorage storage = FirebaseStorage.getInstance();
    private static Repo repo = new Repo();
    private final String SNAPTEXT = "snapText";
    public List<Snap> snaps = new ArrayList<>();
    private Updatable activity;

    public void startListener(){
        db.collection(SNAPTEXT).addSnapshotListener((values, error) -> {
            snaps.clear();
            for(DocumentSnapshot snap: values.getDocuments()){
                String text = snap.get("text").toString();
                String id = snap.getId().toString();
                String imageRef = snap.get("imageRef").toString();
                if(text != null){
                    Snap snapi = new Snap(id,text, imageRef);
                    snaps.add(snapi);
                }
                System.out.println("Snap: " +snap.toString());
            }
            activity.update(snaps);
        });
    }

    public void setup(Updatable a, List<Snap> list){
        activity = a;
        snaps = list;
        startListener();
    }

    public Snap getNoteWith(String id){
        for(Snap snap : snaps){
            if(snap.getId().equals(id)){
                return snap;
            }
        }
        return null;
    }

    public void addSnap(String text, String imageRef){
        // Creates a new document
        Map<String, String> map = new HashMap<>();
        map.put("text", text);
        map.put("imageRef", imageRef);
        db.collection(SNAPTEXT).add(map);
        System.out.println(snaps);
        System.out.println("Done inserting new document");
    }

    /*
    Here the bitmap is uploaded to Firebase Storage and we use UUID from util
    to tie the snaps and images together.
    UUID generates an ID for us instead of relying on the FB to do it for us, which was
    causing issues.
    - Christoffer
    */
    public void uploadBitmap(String text, Bitmap bitmap) {
        System.out.println("uploadBitmap called " + bitmap.getByteCount());

        UUID uuid = UUID.randomUUID();
        String uuidAsString = uuid.toString();
        addSnap(text, uuidAsString);
        StorageReference ref = storage.getReference().child("images/"+uuidAsString);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);

        ref.putBytes(baos.toByteArray()).addOnCompleteListener(snapsh -> {
            System.out.println("OK to upload " + snapsh);
        }).addOnFailureListener(exception -> {
            System.out.println("failure to upload " + exception);
        });
    }

    /*
    Here we use the imageRef to download the image from FB Storage. imageRef is the UUID string from
    the above method.
    downloadBitmap() is called from the DetailActivity activity which uses the snap.getImageRef() method
    - Cecilie
    */
    public void downloadBitmap(Snap snap, TaskListener taskListener){ // when to call this method?
        StorageReference ref = storage.getReference("images/"+snap.getImageRef());
        int max = 1024 * 1024; // you are free to set the limit here
        ref.getBytes(max).addOnSuccessListener(bytes -> {
            taskListener.receive(bytes);
            System.out.println("Download OK");
        }).addOnFailureListener(ex -> {
            System.out.println("error in download " + ex);
        });
    }

    public void deleteNote(String id){
        DocumentReference docRef = db.collection(SNAPTEXT).document(id);
        docRef.delete();
    }

    public void deleteImage(String id){
        StorageReference ref = storage.getReference("images/"+id);
        ref.delete();
    }

    public static Repo r(){
        return repo;
    }
}
