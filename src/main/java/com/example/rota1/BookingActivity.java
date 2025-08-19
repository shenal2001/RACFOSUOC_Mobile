package com.example.rota1;

import android.os.Bundle;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class BookingActivity extends AppCompatActivity {

    private EditText topicEditText;
    private EditText descriptionEditText;
    private DatePicker datePicker;
    private Button submitButton;
    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_booking);

        topicEditText = findViewById(R.id.topicEditText);
        descriptionEditText = findViewById(R.id.descriptionEditText);
        datePicker = findViewById(R.id.datePicker);
        submitButton = findViewById(R.id.submitButton);

        // Initialize Firebase Database reference
        databaseReference = FirebaseDatabase.getInstance().getReference("bookings");

        submitButton.setOnClickListener(v -> {
            String topic = topicEditText.getText().toString().trim();
            String description = descriptionEditText.getText().toString().trim();

            int day = datePicker.getDayOfMonth();
            int month = datePicker.getMonth() + 1; // Month is 0-indexed
            int year = datePicker.getYear();
            String date = day + "/" + month + "/" + year;

            if (topic.isEmpty() || description.isEmpty()) {
                Toast.makeText(BookingActivity.this, "Please fill out all fields", Toast.LENGTH_SHORT).show();
            } else {
                // Create a Booking object
                Booking booking = new Booking(date, topic, description);

                // Save booking to Firebase using the date as the key
                databaseReference.child(date).setValue(booking).addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(BookingActivity.this, "Booking saved", Toast.LENGTH_SHORT).show();
                        finish(); // Close the activity
                    } else {
                        Toast.makeText(BookingActivity.this, "Failed to save booking", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }
}
