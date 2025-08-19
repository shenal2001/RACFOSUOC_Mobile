package com.example.rota1;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private FirebaseAuth auth;
    private FirebaseUser user;
    private ListView listView;
    private List<Post> postList;
    private CustomAdampter customAdapter;
    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        listView = findViewById(R.id.list);  // Initialize the ListView
        LinearLayout addPost = findViewById(R.id.addPost);  // Initialize the "addPost" TextView
        postList = new ArrayList<>();
        customAdapter = new CustomAdampter(this, R.layout.list_items, postList);
        listView.setAdapter(customAdapter);

        // Initialize Firebase Authentication
        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();
        // Set the action bar color programmatically (optional)
        if (getSupportActionBar() != null) {
            getSupportActionBar().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#00985a")));
        }


        // Check if the user is not logged in
        if (user == null) {
            Intent intent = new Intent(getApplicationContext(), Logging.class);
            startActivity(intent);
            finish();
        }

        // Navigate to PostFormActivity when "What's on your mind" is clicked
        addPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, PostFormActivity.class);
                startActivity(intent);
            }
        });

        // Retrieve data from Firebase
        databaseReference = FirebaseDatabase.getInstance().getReference("posts");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                postList.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Post post = snapshot.getValue(Post.class);
                    postList.add(post);
                }
                customAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // Handle possible errors.
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_logout) {
            // Handle logout action
            auth.signOut();
            Intent intent = new Intent(getApplicationContext(), Logging.class);
            startActivity(intent);
            return true;
        } else if (id == R.id.action_calendar) {
            // Start the CalendarActivity
            Intent intent = new Intent(MainActivity.this, CalenderActivity.class);
            startActivity(intent);
            return true; // Remove finish() to keep MainActivity in the background
        }

        return super.onOptionsItemSelected(item);
    }
}
