package com.example.wellkom;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.dynamiclinks.FirebaseDynamicLinks;
import com.google.firebase.dynamiclinks.PendingDynamicLinkData;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class MainActivity extends AppCompatActivity {

    public String name, mobile, email, date, time, govId, reason, personToMeet, building, userId;
    EditText VisitorName, Mobile, Email, Date, Time, GovId, Reason, PersonToMeet, Building;
    Button Submit, Upload, Select;
    ImageView imageView;
    private Uri filePath;

    // request code
    private final int PICK_IMAGE_REQUEST = 22;
    FirebaseStorage storage;
    StorageReference storageReference;
    StorageReference pathReference;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.Theme_WellKom);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
        }
        setupUI();
        details(VisitorName, Mobile, Date, Time, GovId, Reason, Building, Email, PersonToMeet);

        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReferenceFromUrl("gs://wellkom-1cf3a.appspot.com");
        pathReference = storageReference.child("images/cross.png");
        firebaseDatabase = FirebaseDatabase.getInstance("https://wellkom-1cf3a-default-rtdb.firebaseio.com");
        databaseReference = firebaseDatabase.getReference("Visitor's Information");
        // ATTENTION: This was auto-generated to handle app links.
        FirebaseDynamicLinks.getInstance()
                .getDynamicLink(getIntent())
                .addOnSuccessListener(this, new OnSuccessListener<PendingDynamicLinkData>() {
                    @Override
                    public void onSuccess(PendingDynamicLinkData pendingDynamicLinkData) {
                        // Get deep link from result (may be null if no link is found)
                        Uri deepLink = null;
                        if (pendingDynamicLinkData != null) {
                            deepLink = pendingDynamicLinkData.getLink();
                        }
                    }
                })
                .addOnFailureListener(this, new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w("MainActivity", "getDynamicLink:onFailure", e);
                    }
                });

    }

    private void setupUI() {
        VisitorName = (EditText) findViewById(R.id.name);
        Mobile = (EditText) findViewById(R.id.mobilenum);
        Email = (EditText) findViewById(R.id.email);
        Date = (EditText) findViewById(R.id.date);
        Time = (EditText) findViewById(R.id.time);
        GovId = (EditText) findViewById(R.id.uploadnum);
        Reason = (EditText) findViewById(R.id.reason);
        PersonToMeet = (EditText) findViewById(R.id.meetperson);
        Building = (EditText) findViewById(R.id.building);
        imageView = findViewById(R.id.imageView);
        Submit = findViewById(R.id.submit);
        checkCondition();
    }

    private void details(EditText VisitorName, EditText Mobile, EditText Date, EditText Time, EditText GovId, EditText Reason, EditText Building, EditText Email, EditText PersonToMeet) {
        name = VisitorName.getText().toString().trim();
        mobile = Mobile.getText().toString().trim();
        date = Date.getText().toString().trim();
        time = Time.getText().toString().trim();
        govId = GovId.getText().toString().trim();
        reason = Reason.getText().toString().trim();
        building = Building.getText().toString().trim();
        email = Email.getText().toString().trim();
        personToMeet = PersonToMeet.getText().toString().trim();
    }


    public void setUpListeners(View view) {
        Submit = findViewById(R.id.submit);
        Submit.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                details(VisitorName, Mobile, Date, Time, GovId, Reason, Building, Email, PersonToMeet);
                validateInfo();

            }

        });
        Select = findViewById(R.id.select);
        Upload = findViewById(R.id.upload);
        imageView = findViewById(R.id.imageView);
        Select.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SelectImage();
            }
        });
        Upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                userId = databaseReference.push().getKey();
                uploadImage();
            }
        });
    }

    private void SelectImage() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Image from here..."), PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {

            filePath = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), filePath);
                imageView.setImageBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void uploadImage() {
        if (filePath != null) {
            ProgressDialog progressDialog = new ProgressDialog(this);
            progressDialog.setTitle("Uploading...");
            progressDialog.show();

            StorageReference filereference = storageReference.child("images/"+ userId + ".png");

            // adding listeners on upload
            // or failure of image
            filereference.putFile(filePath).addOnSuccessListener(
                            new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                @Override
                                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                                    // Image uploaded successfully
                                    // Dismiss dialog
                                    progressDialog.dismiss();
                                    Toast.makeText(MainActivity.this, "Image Uploaded!!", Toast.LENGTH_SHORT).show();
                                }
                            })

                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {

                            progressDialog.dismiss();
                            Toast.makeText(MainActivity.this, "Failed " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                            double progress = (100.0 * taskSnapshot.getBytesTransferred() / taskSnapshot.getTotalByteCount());
                            progressDialog.setMessage("Uploaded " + (int) progress + "%");
                        }
                    });
        }
    }

    private void checkCondition() {
        VisitorName.addTextChangedListener(checkConditions);
        Mobile.addTextChangedListener(checkConditions);
        Date.addTextChangedListener(checkConditions);
        Time.addTextChangedListener(checkConditions);
        GovId.addTextChangedListener(checkConditions);
        Reason.addTextChangedListener(checkConditions);
        Building.addTextChangedListener(checkConditions);
    }

    private TextWatcher checkConditions = new TextWatcher() {

        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            details(VisitorName, Mobile, Date, Time, GovId, Reason, Building, Email, PersonToMeet);
            Submit.setEnabled(!name.isEmpty() && !mobile.isEmpty() && !date.isEmpty() && !time.isEmpty() && !govId.isEmpty() && !reason.isEmpty() && !building.isEmpty());
        }

        @Override
        public void afterTextChanged(Editable editable) {

        }
    };

    private void addDatatoFirebase() {

        Map<String, Object> mHashmap = new HashMap<>();
//        userId = databaseReference.push().getKey();
        mHashmap.put(userId + "||" + name + "/Name", name);
        mHashmap.put(userId + "||" + name + "/Mobile", mobile);
        mHashmap.put(userId + "||" + name + "/Email", email);
        mHashmap.put(userId + "||" + name + "/Date", date);
        mHashmap.put(userId + "||" + name + "/Time", time);
        mHashmap.put(userId + "||" + name + "/Government ID proof", govId);
        mHashmap.put(userId + "||" + name + "/Reason of Visit", reason);
        mHashmap.put(userId + "||" + name + "/Person to meet", personToMeet);
        mHashmap.put(userId + "||" + name + "/Building", building);

        databaseReference.updateChildren(mHashmap);
    }
    public void validateInfo(){

        if (TextUtils.isEmpty(name) || TextUtils.isEmpty(mobile) || TextUtils.isEmpty(date) || TextUtils.isEmpty(time) || TextUtils.isEmpty(govId) || TextUtils.isEmpty(reason) || TextUtils.isEmpty(building)) {
            Toast.makeText(MainActivity.this, "Please fill mandatory fields.", Toast.LENGTH_SHORT).show();
            VisitorName.setError("Please enter your name");
            Mobile.setError("Please enter your mobile number");
            Date.setError("Please enter the visiting date");
            Time.setError("Please enter your visiting time");
            GovId.setError("Please enter your Government ID");
            Reason.setError("Please enter the reason of visit");
            Building.setError("Please enter the building to visit");

        } else if (!TextUtils.isEmpty(name) && !TextUtils.isEmpty(mobile) && !TextUtils.isEmpty(date) && !TextUtils.isEmpty(time) && !TextUtils.isEmpty(govId) && !TextUtils.isEmpty(reason) && !TextUtils.isEmpty(building)) {
            addDatatoFirebase();
            Intent approver = new Intent(MainActivity.this, ApprovalActivity.class);
            approver.putExtra("Visitor Id: ", "Visitor Id: " + userId);
            MainActivity.this.startActivity(approver);
            MainActivity.this.finish();
        }
    }
}
