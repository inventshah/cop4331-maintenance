package com.example.maintenanceapp.Fragments;

import static com.example.maintenanceapp.NewWorkOrderActivity.PICK_PHOTO_CODE;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ImageDecoder;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.appcompat.widget.Toolbar;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.maintenanceapp.LoginActivity;
import com.example.maintenanceapp.R;
import com.example.maintenanceapp.RegisterLandlordActivity;
import com.parse.GetCallback;
import com.parse.GetDataCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import org.w3c.dom.Text;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import models.Handyman;
import models.Landlord;
import models.Tenant;


public class ProfileFragment extends Fragment {

    ParseObject role;
    private TextView tvFullName;
    private TextView tvUsername;
    private TextView tvEmail;
    private TextView tvPoints;
    private ImageView ivProfilePicture;
    private TextView tvLandlordKey;
    ImageButton btnCopyLandlordKey;
    Spinner spProfileOptions;
    private SwipeRefreshLayout swipeContainer;

    public static final String TAG = "ProfileFragment";
    public ProfileFragment() {
        // Required empty public constructor
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        role = (ParseObject) this.getActivity().getIntent().getExtras().get("role");
        Toolbar toolbar = (Toolbar) view.findViewById(R.id.profile_toolbar);
        ((AppCompatActivity)getActivity()).setSupportActionBar(toolbar);
        tvUsername = view.findViewById(R.id.tvProfileUsername);
        tvEmail = view.findViewById(R.id.tvEmail);
        tvFullName = view.findViewById(R.id.tvNameOfUser);
        tvPoints = view.findViewById(R.id.tvPoints);
        ivProfilePicture = view.findViewById(R.id.ivProfilePicture);
        btnCopyLandlordKey = view.findViewById(R.id.btnCopyLandlordKey);
        spProfileOptions = view.findViewById(R.id.spProfileOptions);
        tvLandlordKey = view.findViewById(R.id.tvLandlordKey);
        swipeContainer = view.findViewById(R.id.swipeContainerProfile);
        tvUsername.setText("@" +ParseUser.getCurrentUser().getUsername());
        tvEmail.setText(ParseUser.getCurrentUser().getEmail());
        tvFullName.setText(ParseUser.getCurrentUser().getString("name"));
        tvPoints.setText("Points: "+(Math.round(role.getDouble("points")*100.0)/100.0));
        tvEmail.setText(ParseUser.getCurrentUser().getString("email"));

        ivProfilePicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onPickPhoto(v);
            }
        });
        // Set refresh listener
        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                ParseFile profilePicture = ParseUser.getCurrentUser().getParseFile("profilePic");
                if (profilePicture != null) {
                    profilePicture.getDataInBackground(new GetDataCallback() {
                        public void done(byte[] data, ParseException e) {
                            if (e == null) {
                                Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
                                if (bitmap != null)
                                    Glide.with(getContext()).load(bitmap).apply(RequestOptions.circleCropTransform()).into(ivProfilePicture);
                            }
                            else Log.d(TAG, "ParseFile ParseException: " + e.toString());
                        }
                    });
                }
                else Log.d(TAG, "ParseFile is null");
                swipeContainer.setRefreshing(false);
            }
        });


        if (role instanceof Landlord)
        {
            btnCopyLandlordKey.setVisibility(View.VISIBLE);
            tvLandlordKey.setVisibility(View.VISIBLE);
            tvLandlordKey.setText(((Landlord) role).getLandLordKey().toString());
            btnCopyLandlordKey.setOnClickListener(v -> {

                ClipboardManager clipboard = (ClipboardManager)getContext().getSystemService(getContext().CLIPBOARD_SERVICE);
                ClipData clip = ClipData.newPlainText("", tvLandlordKey.getText().toString());
                clipboard.setPrimaryClip(clip);
                Toast.makeText(getContext(), "Copied to clipboard!", Toast.LENGTH_LONG).show();
            });
        }

        // Create and set array adapters for each spinner
        ArrayAdapter<String> profileOptionsAdapter = new ArrayAdapter<>(getContext(),
                android.R.layout.simple_spinner_dropdown_item,
                getResources().getStringArray(role instanceof Handyman ? R.array.postOptionsHM : R.array.postOptions));

        spProfileOptions.setAdapter(profileOptionsAdapter);
        spProfileOptions.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener()
        {
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id)
            {
                String selectedItem = parent.getItemAtPosition(position).toString();
                if (selectedItem.equals("Log out"))
                {
                    ParseUser.logOut();
                    if (ParseUser.getCurrentUser() != null)
                        Log.e("Logout Error", "logout err");

                    Intent intent = new Intent(getContext(), LoginActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                }
                else if (selectedItem.equals("Register Landlord")) {
                    Intent intent = new Intent(getContext(), RegisterLandlordActivity.class);
                    intent.putExtra("role", role);
                    startActivity(intent);
                }
            } // to close the onItemSelected
            public void onNothingSelected(AdapterView<?> parent) { }
        });

        // Set Profile picture
        ParseFile profilePicture = ParseUser.getCurrentUser().getParseFile("profilePic");
        if (profilePicture != null) {
            profilePicture.getDataInBackground(new GetDataCallback() {
                public void done(byte[] data, ParseException e) {
                    if (e == null) {
                        Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
                        if (bitmap != null)
                            Glide.with(getContext()).load(bitmap).apply(RequestOptions.circleCropTransform()).into(ivProfilePicture);
                    }
                    else Log.d(TAG, "ParseFile ParseException: " + e.toString());
                }
            });
        }
        else Log.d(TAG, "ParseFile is null");
    }

    // Trigger gallery selection for a photo
    public void onPickPhoto(View view) {
        // Create intent for picking a photo from the gallery
        Intent intent = new Intent(Intent.ACTION_PICK,
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

        startActivityForResult(intent, PICK_PHOTO_CODE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if ((data != null) && requestCode == PICK_PHOTO_CODE) {
            Uri photoUri = data.getData();

            // Load the image located at photoUri into selectedImage
            Bitmap selectedImage = loadFromUri(photoUri);

            Glide.with(this).load(selectedImage).apply(RequestOptions.circleCropTransform()).into(ivProfilePicture);
            // Convert bitmap to image
            try {
                convertBitmapToFile(selectedImage);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void convertBitmapToFile(Bitmap bitmap) throws Exception {
        // Convert bitmap to byte array
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 0, bos);
        byte[] bitmapdata = bos.toByteArray();

        ParseFile file = new ParseFile("profilePic", bitmapdata);
        file.saveInBackground(new SaveCallback() {
            public void done(ParseException e) {
                if (null == e)
                    saveProfilePicture(ParseUser.getCurrentUser(), file);
            }
        });
    }

    public Bitmap loadFromUri(Uri photoUri) {
        Bitmap image = null;
        try {
            // Check version of Android on device
            if(Build.VERSION.SDK_INT > 27){
                // On newer versions of Android, use the new decodeBitmap method
                ImageDecoder.Source source = ImageDecoder.createSource(getContext().getContentResolver(), photoUri);
                image = ImageDecoder.decodeBitmap(source);
            } else {
                // Support older versions of Android by using getBitmap
                image = MediaStore.Images.Media.getBitmap(getContext().getContentResolver(), photoUri);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return image;
    }

    public void saveProfilePicture(ParseUser user, ParseFile file) {
        if (file == null) {
            Log.i(TAG, "photofile is null");
            return;
        }
        user.put("profilePic", file);
        user.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                Log.i(TAG, "saving user profile picture");
            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_profile, container, false);
    }
}