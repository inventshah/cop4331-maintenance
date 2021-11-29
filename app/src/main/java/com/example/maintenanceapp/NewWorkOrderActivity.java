package com.example.maintenanceapp;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Adapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.example.maintenanceapp.Fragments.HomeFragment;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.io.File;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import jp.wasabeef.glide.transformations.RoundedCornersTransformation;
import models.*;

public class NewWorkOrderActivity extends AppCompatActivity
{
    ParseObject role;
    Button submitBtn;
    EditText editTextDescription;
    EditText editTextTitle;
    EditText editTextLocation;
    ImageView ivWorkOrderImage;
    Button btnTakePhoto;
    private File photoFile;
    public String photoFileName = "photo.jpg";
    public static final int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 45; // arbitrary
    public final static int PICK_PHOTO_CODE = 1111; // arbitrary
    public static final String TAG = "NewWorkOrderActivity";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_newworkorder);

        role = (ParseObject) getIntent().getExtras().get("role");
        editTextDescription = findViewById(R.id.etDescription);
        ivWorkOrderImage = findViewById(R.id.ivImagePreview);
        editTextTitle = findViewById(R.id.etTitle);
        editTextLocation = findViewById(R.id.etLocation);
        submitBtn = findViewById(R.id.btnSubmit);
        btnTakePhoto = findViewById(R.id.btnTakePhoto);
        btnTakePhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                launchCamera(view);
            }
        });
        submitBtn.setOnClickListener(view -> {
            String description = editTextDescription.getText().toString();
            String location = editTextLocation.getText().toString();
            String title = editTextTitle.getText().toString();
            if (description.length() > 0 && location.length() > 0 && title.length() > 0 && photoFile != null)
                submitWorkOrder(description, location, title);
        });
    }

    public void submitWorkOrder(String description, String location, String title) {

        Tenant tenant = (Tenant) role;
        WorkOrder workOrder = new WorkOrder();
        workOrder.setTitle(title);
        workOrder.setDescription(description);
        workOrder.setLocation(location);
        workOrder.setTenant(tenant);
        workOrder.setLandlord(tenant.getLandlord());
        workOrder.setStatus(false);
        workOrder.setAttachment(new ParseFile(photoFile));
        workOrder.setQuotes(new ArrayList<>());
        workOrder.setRating(0);
        workOrder.saveInBackground();

        MainActivity.bottomNavigationView.setSelectedItemId(R.id.action_home);
        finish();
        return;
    }

    public void launchCamera(View view) {
        // Create Intent to take a picture and return control to the calling application
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Create a File reference for future access
        photoFile = getPhotoFileUri(photoFileName);

        // Wrap File object into a content provider
        Uri fileProvider = FileProvider.getUriForFile(getApplicationContext(), "com.codepath.fileprovider.MaintenanceApp", photoFile);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, fileProvider);

        // If you call startActivityForResult() using an intent that no app can handle, your app will crash.
        // So as long as the result is not null, it's safe to use the intent.
        if (intent.resolveActivity(getApplicationContext().getPackageManager()) != null) {
            // Start the image capture intent to take photo
            startActivityForResult(intent, CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE);
        }
    }

    public File getPhotoFileUri(String fileName) {
        // Get safe storage directory for photos
        // Use `getExternalFilesDir` on Context to access package-specific directories.
        // This way, we don't need to request external read/write runtime permissions.
        File mediaStorageDir = new File(getApplicationContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES), TAG);

        // Create the storage directory if it does not exist
        if (!mediaStorageDir.exists() && !mediaStorageDir.mkdirs()){
            Log.d(TAG, "failed to create directory");
        }

        // Return the file target for the photo based on filename
        return new File(mediaStorageDir.getPath() + File.separator + fileName);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                // By this point we have the camera photo on disk
                Bitmap takenImage = BitmapFactory.decodeFile(photoFile.getAbsolutePath());
                // Load the taken image into a preview
                ivWorkOrderImage.setImageBitmap(takenImage);
                ivWorkOrderImage.setMaxHeight(200);
                ivWorkOrderImage.setMaxWidth(150);


            } else { // Result was a failure
                Toast.makeText(getApplicationContext(), "Picture wasn't taken!", Toast.LENGTH_SHORT).show();
            }
        }
    }


}
