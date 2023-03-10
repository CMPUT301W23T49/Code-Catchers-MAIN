package com.example.codecatchersapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;

import com.google.firebase.firestore.FirebaseFirestore;

import java.security.NoSuchAlgorithmException;

import retrofit2.http.HEAD;

public class MainActivity extends AppCompatActivity {
    protected String qrCode;
    //FirebaseFirestore db;

    private static final int MAX_BATCH_COUNT = 50; // maximum number of batches to send
    private HashGenerator hashGenerator;

    private final Handler handler = new Handler();

    private int currentBackoffTime = 0; // initially set to 0 to start the write operation immediately

    private final int MAX_BACKOFF_TIME = 5000; // 5 seconds

    // Track the number of batches written
    private int batchCount = 0;

    public static final int BATCH_SIZE = 3; // maximum number of documents to write in a batch

    private final int INITIAL_BACKOFF_TIME = 500; // 0.5 seconds
    private final int BACKOFF_MULTIPLIER = 2;

    // Declare the Runnable object
    private final Runnable myRunnable = new Runnable() {
        @Override
        public void run() {
            try {
                hashGenerator.generateAndWriteHash("test qr code");

                Log.d("Firestore", "Batch write successful.");

                // Reset the backoff time
                currentBackoffTime = 0;

                // Increment the batch count
                batchCount++;

                // Create a new batch write operation if the batch count is less than the maximum batch count
                if (batchCount < MAX_BATCH_COUNT) {
                    // Commit the new batch write operation after a delay
                    handler.postDelayed(myRunnable, currentBackoffTime);
                }
            } catch (NoSuchAlgorithmException e) {
                Log.e("Firestore", "Error generating hash.", e);
                // Increment the backoff time
                currentBackoffTime = Math.min(currentBackoffTime * BACKOFF_MULTIPLIER, MAX_BACKOFF_TIME);

                // Commit the batch write operation after a delay
                handler.postDelayed(myRunnable, currentBackoffTime);
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

//        setContentView(R.layout.map_layout);

        Intent userAccountIntent = new Intent(this, UserAccountActivity.class);
        startActivity(userAccountIntent);



        hashGenerator = new HashGenerator();

        // Start the write operation with a delay
        currentBackoffTime = INITIAL_BACKOFF_TIME;
        handler.postDelayed(myRunnable, currentBackoffTime);
    }
}
