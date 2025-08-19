package com.example.rota1;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import java.util.List;

public class CustomAdampter extends ArrayAdapter<Post> {
    private Context context;
    private int resource;
    private List<Post> postList;

    public CustomAdampter(Context context, int resource, List<Post> postList) {
        super(context, resource, postList);
        this.context = context;
        this.resource = resource;
        this.postList = postList;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        View view = layoutInflater.inflate(resource, null);

        // Initialize the views
        TextView textViewTitle = view.findViewById(R.id.textTitle);
        TextView textViewDes = view.findViewById(R.id.textDescription);
        ImageView imageViewList = view.findViewById(R.id.listImage);
        ImageView likeImage = view.findViewById(R.id.like);
        TextView textLikes = view.findViewById(R.id.textLikes);
        ImageView shareImage = view.findViewById(R.id.share);

        Post post = postList.get(position);

        // Set the data from the post object to the views
        textViewTitle.setText(post.getTopic());
        textViewDes.setText(post.getDescription());
        Picasso.get().load(post.getImageUrl()).into(imageViewList);
        textLikes.setText(post.getLikes() + " Likes");

        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        String userId = currentUser != null ? currentUser.getUid() : "";

        // Handle the like button click
        likeImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!post.getLikedBy().containsKey(userId)) {
                    int currentLikes = post.getLikes();
                    int newLikes = currentLikes + 1;

                    // Update the likes count in the Post object and Firebase
                    post.setLikes(newLikes);
                    post.getLikedBy().put(userId, true);  // Mark this user as having liked the post
                    textLikes.setText(newLikes + " Likes");

                    // Get a reference to the specific post in the database
                    DatabaseReference postRef = FirebaseDatabase.getInstance().getReference("posts").child(post.getPostId());
                    postRef.child("likes").setValue(newLikes);
                    postRef.child("likedBy").setValue(post.getLikedBy());  // Update the likedBy map in Firebase
                }
            }
        });

        // Handle the share button click
        shareImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String shareContent = post.getTopic() + "\n\n" + post.getDescription() + "\n\n" + post.getImageUrl();

                Intent shareIntent = new Intent(Intent.ACTION_SEND);
                shareIntent.setType("text/plain");
                shareIntent.putExtra(Intent.EXTRA_TEXT, shareContent);
                context.startActivity(Intent.createChooser(shareIntent, "Share Post via"));
            }
        });

        return view;
    }
}
