package com.example.rota1;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

public class PostFormActivity extends AppCompatActivity {

    private static final int PICK_IMAGE_REQUEST = 1;

    private EditText editTextTopic, editTextDescription;
    private ImageView imageViewUpload;
    private Button buttonSubmit;
    private ProgressBar progressBar;

    private Uri imageUri;
    private DatabaseReference databaseReference;
    private StorageReference storageReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_form);

        editTextTopic = findViewById(R.id.topic);
        editTextDescription = findViewById(R.id.description);
        imageViewUpload = findViewById(R.id.uploadImage);
        buttonSubmit = findViewById(R.id.buttonSubmit);
        progressBar = findViewById(R.id.progressBar); // Optional for showing progress

        databaseReference = FirebaseDatabase.getInstance().getReference("posts");
        storageReference = FirebaseStorage.getInstance().getReference("post_images");

        imageViewUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openFileChooser();
            }
        });

        buttonSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (imageUri != null) {
                    uploadImage();
                } else {
                    submitPost(null); // Submit without image
                }
            }
        });
    }

    private void openFileChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            imageUri = data.getData();
            imageViewUpload.setImageURI(imageUri);
        }
    }

    private void uploadImage() {
        progressBar.setVisibility(View.VISIBLE);

        final StorageReference fileReference = storageReference.child(System.currentTimeMillis() + ".jpg");

        fileReference.putFile(imageUri).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                fileReference.getDownloadUrl().addOnSuccessListener(uri -> {
                    String imageUrl = uri.toString();
                    submitPost(imageUrl);
                });
            } else {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(PostFormActivity.this, "Image upload failed", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void submitPost(String imageUrl) {
        String topic = editTextTopic.getText().toString().trim();
        String description = editTextDescription.getText().toString().trim();

        if (topic.isEmpty() || description.isEmpty()) {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        String postId = databaseReference.push().getKey();
        Post post = new Post(postId, topic, description, imageUrl);

        databaseReference.child(postId).setValue(post)
                .addOnCompleteListener(task -> {
                    progressBar.setVisibility(View.GONE);
                    if (task.isSuccessful()) {
                        Toast.makeText(PostFormActivity.this, "Post submitted", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(PostFormActivity.this, MainActivity.class));
                        finish();
                    } else {
                        Toast.makeText(PostFormActivity.this, "Failed to submit post", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
