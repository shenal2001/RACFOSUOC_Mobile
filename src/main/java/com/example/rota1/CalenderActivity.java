package com.example.rota1;

import android.app.AlarmManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.CalendarView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Calendar;

public class CalenderActivity extends AppCompatActivity {

    private CalendarView calendarView;
    private TextView bookingDetails;
    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calender);
        // Create Notification Channel
        createNotificationChannel();


        calendarView = findViewById(R.id.calendarView);
        bookingDetails = findViewById(R.id.bookingDetails);

        // Initialize Firebase Database reference
        databaseReference = FirebaseDatabase.getInstance().getReference("bookings");

        // Set initial visibility to ensure both are visible
        calendarView.setVisibility(View.VISIBLE);
        bookingDetails.setVisibility(View.VISIBLE);

        calendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(@NonNull CalendarView view, int year, int month, int dayOfMonth) {
                String date = dayOfMonth + "/" + (month + 1) + "/" + year;
                Log.d("CalenderActivity", "Selected Date: " + date); // Debug log to see selected date
                fetchBookingDetails(date);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.calendar_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_booking) {
            Intent intent = new Intent(CalenderActivity.this, BookingActivity.class);
            startActivity(intent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void fetchBookingDetails(String date) {
        Log.d("CalenderActivity", "Fetching booking details for date: " + date);

        databaseReference.child(date).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    Booking booking = dataSnapshot.getValue(Booking.class);
                    if (booking != null) {
                        String bookingInfo = "Topic: " + booking.getTopic() + "\n\nDescription: " + booking.getDescription();
                        bookingDetails.setText(bookingInfo);

                        // Schedule Notifications
                        Calendar calendar = Calendar.getInstance();
                        calendar.set(Calendar.HOUR_OF_DAY, 8);  // Notification at 8 AM
                        calendar.set(Calendar.MINUTE, 0);
                        calendar.set(Calendar.SECOND, 0);

                        // For the day before
                        calendar.add(Calendar.DAY_OF_YEAR, -1);
                        scheduleNotification(date, "Reminder Tomorrow: " + booking.getTopic(), calendar.getTimeInMillis(), 1);

                        // For the day of the event
                        calendar.add(Calendar.DAY_OF_YEAR, 1);
                        scheduleNotification(date, "Today: " + booking.getTopic(), calendar.getTimeInMillis(), 2);
                    }
                } else {
                    bookingDetails.setText("No bookings for " + date);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(CalenderActivity.this, "Failed to load booking details", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void createNotificationChannel() {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            CharSequence name = "BookingReminderChannel";
            String description = "Channel for Booking Reminders";
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel channel = new NotificationChannel("BookingReminder", name, importance);
            channel.setDescription(description);

            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    private void scheduleNotification(String date, String topic, long timeInMillis, int requestCode) {
        Intent notificationIntent = new Intent(this, NotificationReceiver.class);
        notificationIntent.putExtra(NotificationReceiver.NOTIFICATION_TITLE, requestCode == 1 ? "Reminder Tomorrow: " : "Today: ");
        notificationIntent.putExtra(NotificationReceiver.NOTIFICATION_TEXT, topic);
        notificationIntent.putExtra(NotificationReceiver.NOTIFICATION_ID, requestCode);

        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                this, requestCode, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        alarmManager.setExact(AlarmManager.RTC_WAKEUP, timeInMillis, pendingIntent);
    }


}
