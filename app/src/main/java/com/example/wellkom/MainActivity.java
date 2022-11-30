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
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.IOException;
import java.util.LinkedList;
import java.util.UUID;

public class MainActivity extends AppCompatActivity {

    public String name, mobile, email, date, time, govId, reason, personToMeet, building;
    EditText VisitorName, Mobile, Email, Date, Time, GovId, Reason, PersonToMeet, Building;
    LinkedList<String> List = new LinkedList<String>();
    Button Submit, Upload, Select;
    ImageView imageView;
    private Uri filePath;

    // request code
    private final int PICK_IMAGE_REQUEST = 22;
    FirebaseStorage storage;
    StorageReference storageReference;
    StorageReference pathReference;

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
        // ATTENTION: This was auto-generated to handle app links.
        Intent appLinkIntent = getIntent();
        String appLinkAction = appLinkIntent.getAction();
        Uri appLinkData = appLinkIntent.getData();
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
                Intent approver = new Intent(MainActivity.this, ApprovalActivity.class);
                MainActivity.this.startActivity(approver);
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
    protected void onActivityResult(int requestCode, int resultCode, Intent data)  {

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

    private void uploadImage(){
        if (filePath != null) {
            ProgressDialog progressDialog = new ProgressDialog(this);
            progressDialog.setTitle("Uploading...");
            progressDialog.show();

            StorageReference filereference = storageReference.child("images/" + UUID.randomUUID().toString());

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
            Submit.setEnabled(!name.isEmpty() && !mobile.isEmpty() && !date.isEmpty() && !time.isEmpty() && !govId.isEmpty() && !reason.isEmpty()&& !building.isEmpty());
        }

        @Override
        public void afterTextChanged(Editable editable) {

        }
    };

}