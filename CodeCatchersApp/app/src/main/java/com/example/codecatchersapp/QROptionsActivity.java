/**
 * a class
 * @author CMPUT301W23T49
 * @version 1.0
 * @since [Monday April 3]
 */
package com.example.codecatchersapp;

import static android.content.ContentValues.TAG;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Switch;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;

import org.imperiumlabs.geofirestore.GeoFirestore;

import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;

/**
 * Activity to display QR code saving options, such as geolocation, picture, and comment
 * Does not yet connect to true user's account/their collection in the database
 * Does not connect to specific monster's comment collection yet
 */
public class QROptionsActivity extends AppCompatActivity {
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    String newTotalScore;
    String newMonsterCount;
    String newHighestMonsterScore;

    /**
     Takes in choices for photo and geolocation, saves comment to database
     @param savedInstanceState A Bundle object containing the activity's saved state.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        SharedPreferences sharedPreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE);
        String myUserName = sharedPreferences.getString("username", "");

        super.onCreate(savedInstanceState);
        setContentView(R.layout.qr_options);
        Intent intent = getIntent();
        String shaHash = intent.getStringExtra("shaHash");
        String binaryHash = intent.getStringExtra("binaryHash");
//      String hash = intent.getStringExtra("hash");
        String userID = Settings.Secure.getString(getApplicationContext().getContentResolver(), Settings.Secure.ANDROID_ID);
        EditText commentEditText = findViewById(R.id.editTextNewMonComment);
        Switch geolocationToggle = findViewById(R.id.geolocation_switch);
        Switch locationPhotoToggle = findViewById(R.id.photo_switch);
        Button continueMonSettings = findViewById(R.id.continue_photo_button);

        String displayScore;
        double latitude = 0;
        double longitude = 0;
        final int PERMISSIONS_REQUEST_CODE = 123;

        // Get score of scanned monster
        try {
            Score score = new Score(shaHash);
            displayScore = score.getScore();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }

        updateLeaderboardFields(displayScore);

        // TODO: Can be made better by only prompting when user toggles for geo-location in future, but issues arise due to use of "this" in line 50
        // This if statement prompts the user for permission to access their location, if not already granted.
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, PERMISSIONS_REQUEST_CODE);
            Log.d("Success","Used has not already enabled location, and so is prompted to share it.");
        }
        // This if statement gets location, only if access already granted
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED){
            LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
            Location lastKnownLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            if (lastKnownLocation != null) {
                latitude = lastKnownLocation.getLatitude();
                longitude = lastKnownLocation.getLongitude();
                Log.d("Success","Latitude and longitude data have been updated");
            }
        }

        // some boilerplate code for adding coordinates into firebase
        double finalLatitude = latitude;
        double finalLongitude = longitude;
        continueMonSettings.setOnClickListener(new View.OnClickListener() {
            /**
             * Triggers options to save their respective data to the database
             *
             * @param view view that was clicked (continue button)
             */
            @Override
            public void onClick(View view) {

                // Adds comment to firebase


                // TODO: ADD COMMENT TO DATABASE
                saveComment();

                // Adds geolocation data to firebase
                Monster monster = new Monster(shaHash);

                Boolean geolocationToggleState = geolocationToggle.isChecked();
                if (geolocationToggleState == true) {
                    saveGeolocation(monster);
                    //
                } else {
                    // Save to monsterDB
                    CollectionReference collectionReference1 = db.collection("MonsterDB");
                    DocumentReference documentReference = collectionReference1.document(shaHash);
                    documentReference.set(monster);

                    // Save to PlayerDB
                    CollectionReference collectionReference2 = db.collection("PlayerDB/" + userID + "/Monsters/");
                    DocumentReference documentReference2 = collectionReference2.document(shaHash);
                    documentReference2.set(monster);
                }

                Boolean locationPhotoToggleState = locationPhotoToggle.isChecked();
                // TODO: IF TRUE, GO TO CAMERA AFTER CONTINUE CLICKED, ELSE GO MAIN MENU?
                if (locationPhotoToggleState == false) {
                    goMainMenu();
                } else {
                    Intent intent = new Intent(QROptionsActivity.this, CameraActivity.class);
                    startActivity(intent);
                }

                // if user only presses continue => exit
                if (locationPhotoToggleState == false && geolocationToggleState == false) {
                    goMainMenu();
                }
            }

            /**
             * Retrieves user's geolocation and saves it in the database
             * @param monster - Monster object that contains all relevant data to add to DB
             */
            public void saveGeolocation(Monster monster) {
                // TODO: change SomeUserID to current user's ID, change someMonsterID to monster hash
                db = FirebaseFirestore.getInstance();

                // save to monsterDB
                GeoFirestore geoFirestore = new GeoFirestore(db.collection("MonsterDB"));
                GeoPoint geoloc = new GeoPoint(finalLatitude, finalLongitude);
                geoFirestore.setLocation(shaHash, geoloc);

                // save to playerDB
                monster.setGeoPoint(geoloc);

                CollectionReference collectionReference2 = db.collection("PlayerDB/" + userID + "/Monsters/");
                DocumentReference documentReference2 = collectionReference2.document(shaHash);
                documentReference2.set(monster);

                /*
                CollectionReference collectionReferenceGeoLocation = db.collection("PlayerDB/" + userID + "/Monsters/" + shaHash + "/geolocationData");
                Map<String, Object> coordinates = new HashMap<>();
                coordinates.put("geoPoint", geoloc);

                DocumentReference docReference = collectionReferenceGeoLocation.document("geoPoint");
                docReference.set(coordinates)
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Log.d(TAG, "Location added to Firestore");
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.w(TAG, "Error adding location to Firestore", e);
                            }
                        });

                 */
            }


            /**
             * Saves the users comment to the database
             */
            String userName;

            // TODO: CHANGE TO NEW SYSTEM -> MATHEW!!!!
            public void saveComment() {
                CollectionReference collectionReference = db.collection("PlayerDB/" + userID + "/Monsters/" + shaHash + "/comments");
                final String ogComment = commentEditText.getText().toString();
                HashMap<String, String> data = new HashMap<>();

                if (ogComment.length() > 0) {
                                    // TODO: change SomeUserID to current user's ID, change someMonsterID to monster hash
                                    data.put("userName", myUserName);
                                    collectionReference
                                            .document(ogComment)
                                            .set(data)
                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void unused) {
                                                    Log.d("Success", "Comment added successfully!");
                                                }
                                            })
                                            .addOnFailureListener(new OnFailureListener() {
                                                @Override
                                                public void onFailure(@NonNull Exception e) {
                                                    Log.d("Failure", "Comment addition failed" + e.toString());
                                                }
                                            });

                                }
                            }
                        });

            }

            /**
             * Directs activity to main menu
             */
            public void goMainMenu() {
                // Change MainActivity.class to MainMenuActivity.class once merged
                Intent intent = new Intent(QROptionsActivity.this, MainMenuActivity.class);
                startActivity(intent);
            }

    /**
     * Updates the user's score fields so that the leaderboards correctly display their scores.
     * @param scoreString
     */
    private void updateLeaderboardFields(String scoreString){
        String userID = Settings.Secure.getString(getApplicationContext().getContentResolver(), Settings.Secure.ANDROID_ID);
        DocumentReference documentReferenceUserScoreField = db.collection("PlayerDB/").document(userID);

        documentReferenceUserScoreField.get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        if (documentSnapshot.exists()){
                            Map<String, Object> data = documentSnapshot.getData();

                            String oldTotalScore = (String) data.get("totalscore");
                            System.out.println("\n\n\n" + scoreString + "    " + oldTotalScore);
                            String oldMonsterCount = (String) data.get("monstercount");
                            String oldHighestMonsterScore = (String) data.get("highestmonsterscore");

                            Log.e("E","OLD TOTAL SCORE VALUE: " + oldTotalScore);
                            Log.e("E","OLD TOTAL MONSTER COUNT: " + oldMonsterCount);
                            Log.e("E","OLD HIGHEST MONSTER SCORE: " + oldHighestMonsterScore);

                            newTotalScore = String.valueOf(Integer.parseInt(oldTotalScore) + Integer.parseInt(scoreString));
                            newMonsterCount = String.valueOf(Integer.parseInt(oldMonsterCount) + 1);
                            newHighestMonsterScore = oldHighestMonsterScore;

                            // If new score is larger than previous highest score
                            if (Integer.parseInt(scoreString) > Integer.parseInt(oldHighestMonsterScore)) {
                                newHighestMonsterScore = scoreString;
                            }

                            Log.e("E","NEW TOTAL SCORE VALUE: " + newTotalScore);
                            Log.e("E","NEW TOTAL MONSTER COUNT: " + newMonsterCount);
                            Log.e("E","NEW HIGHEST MONSTER SCORE: " + newHighestMonsterScore);

                            Map<String, Object> newLeaderboardInfo = new HashMap<>();
                            newLeaderboardInfo.put("totalscore", newTotalScore);
                            newLeaderboardInfo.put("monstercount", newMonsterCount);
                            newLeaderboardInfo.put("highestmonsterscore", newHighestMonsterScore);

                            for (Map.Entry<String, Object> entry : newLeaderboardInfo.entrySet()){
                                System.out.println("ENTERED LOOP");
                                System.out.println("Key: " + entry.getKey() + " Value: " + entry.getValue());
                            }

                            documentReferenceUserScoreField.update(newLeaderboardInfo)
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void unused) {
                                            Log.e("E","UPDATED FIELDS");
                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Log.e("E","COULD NOT UPDATE FIELDS");
                                        }
                                    });

                        } else {
                            Log.e("E","DOCUMENT DOES NOT EXIST");
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e("E","ERROR GETTING DOCUMENT");
                    }
                });

    }
}
