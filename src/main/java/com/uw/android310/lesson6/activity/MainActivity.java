package com.uw.android310.lesson6.activity;

import android.app.Notification;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.widget.EditText;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;
import com.uw.android310.lesson6.R;
import com.uw.android310.lesson6.model.Image;
import com.uw.android310.lesson6.model.ImageUpload;
import com.uw.android310.lesson6.service.ImageUploadService;
import com.uw.android310.lesson6.util.Constants;
import com.uw.android310.lesson6.util.DocumentUtils;
import com.uw.android310.lesson6.util.IntentUtils;

import java.io.File;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;


public class MainActivity extends AppCompatActivity {

    public static final String TAG = MainActivity.class.getSimpleName();

    private String mUser;

    @Bind(R.id.imageview)
    ImageView mUploadImage;

    @Bind(R.id.editText_upload_title)
    EditText mUploadTitle;

    @Bind(R.id.editText_upload_desc)
    EditText mUploadDesc;

    @Bind(R.id.toolbar)
    Toolbar mToolbar;

    // Use NotificationCompat for backwards compatibility
    private NotificationManagerCompat mNotificationManager;

    // Unique (within app) ID for email Notification
    public static final int UPLOAD_NOTIFICATION_ID = 1;

    /**
     * Upload object containing image and meta data
     */
    private ImageUpload mUpload;

    /**
     * Chosen file from intent
     */
    private File mChosenFile;

    public Context getContext() {
        return (Context)this;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ButterKnife.bind(this);

        setSupportActionBar(mToolbar);

        mNotificationManager = NotificationManagerCompat.from(this);

        //set the user name
        mUser = getIntent().getStringExtra(Constants.userName);
        Log.d(TAG, "setting user name to: " + mUser);
    }

    @OnClick(R.id.imageview)
    public void onChooseImage() {
        Log.d(TAG, "image selected...");

        mUploadDesc.clearFocus();
        mUploadTitle.clearFocus();
        IntentUtils.chooseFileIntent(this);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        Log.d(TAG, "onActivityResult called ");


        if (requestCode != IntentUtils.FILE_PICK) {
            return;
        }

        if (resultCode != RESULT_OK) {
            return;
        }

        Uri returnUri = data.getData();
        String filePath = DocumentUtils.getPath(this, returnUri);

        //Safety check to prevent null pointer exception
        if (filePath == null || filePath.isEmpty()) {
            return;
        }

        mChosenFile = new File(filePath);

        Picasso.with(getBaseContext())
                .load(mChosenFile)
                .placeholder(R.drawable.ic_photo_library_black)
                .fit()
                .into(mUploadImage);

    }

    @OnClick(R.id.fab)
    public void uploadImage() {
        Log.d(TAG, "Floating action button clicked, uploading image");

        if (mChosenFile == null) {
            return;
        }

        // Wrap the chosen image in an upload object (to be sent to API).
        createUpload(mChosenFile);

        // Initiate upload
        new ImageUploadService(this).execute(mUpload, new UiCallback());
    }

    private void clearInput() {
        mUploadTitle.setText("");
        mUploadDesc.clearFocus();
        mUploadDesc.setText("");
        mUploadTitle.clearFocus();
        mUploadImage.setImageResource(R.drawable.ic_photo_library_black);
    }

    private void createUpload(File image) {
        mUpload = new ImageUpload();
        mUpload.image = image;
        mUpload.title = mUploadTitle.getText().toString();
        mUpload.description = mUploadDesc.getText().toString();
    }


    private void displayAllSharedPref() {
        Log.d(TAG, "displayAllSharedPref called");

        SharedPreferences sharedPref = getSharedPreferences(Constants.sharedPrefName, MODE_PRIVATE);

        Map<String, ?> keys = sharedPref.getAll();

        for (Map.Entry<String, ?> entry : keys.entrySet()) {
            Log.d(TAG, "***************** ***************** " + entry.getKey() + ": " + entry.getValue().toString());
        }
    }

    private void saveDeleteUrl(String deletehash, String imageId) {
        Log.d(TAG, "saveDeleteUrl called... " + deletehash + " " + imageId);

        SharedPreferences sharedPref = getSharedPreferences(Constants.sharedPrefName, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString(mUser + "_image " + imageId + "_delete", deletehash);
        editor.commit();

        displayAllSharedPref();
    }

    private void saveUploadedImage(String imageId, String imageUrl) {
        Log.d(TAG, "saveUploadedImage called... " + imageId + " " + imageUrl);

        SharedPreferences sharedPref = getSharedPreferences(Constants.sharedPrefName, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString(mUser + "_image " + imageId, imageUrl);
        editor.commit();

        displayAllSharedPref();
    }

    private class UiCallback implements Callback<Image> {

        @Override
        public void success(Image imageResponse, Response response) {
            //String deleteUrl = "https://api.imgur.com/3/image/" + imageResponse.getDeletehash();
            Log.d(TAG, "success called... this is a callback after uploading an image " + imageResponse.getId() + " " + imageResponse.getLink() + " deletehash: " + imageResponse.getDeletehash());

            saveDeleteUrl(imageResponse.getDeletehash(), imageResponse.getId());
            saveUploadedImage(imageResponse.getId(), imageResponse.getLink());

            NotificationCompat.Builder builder =
                    new NotificationCompat.Builder(getContext())
                            .setSmallIcon(R.drawable.notification_template_icon_bg)
                            .setContentTitle("Image Uploaded to Imgur")
                            .setContentText("Delete Hash: " + imageResponse.getDeletehash());

            // Create the notification
            Notification basicNotification = builder.build();

            mNotificationManager.notify(UPLOAD_NOTIFICATION_ID, basicNotification);

            // Reset the fields
            clearInput();
        }

        @Override
        public void failure(RetrofitError error) {
            //Assume we have no connection, since error is null
            if (error == null) {
                Snackbar.make(findViewById(R.id.rootView), "No internet connection", Snackbar.LENGTH_SHORT).show();
            }

            NotificationCompat.Builder builder =
                    new NotificationCompat.Builder(getContext())
                            .setSmallIcon(R.drawable.notification_template_icon_bg)
                            .setContentTitle("Image Failed to upload to Imgur")
                            .setContentText("Try again");

            // Create the notification
            Notification basicNotification = builder.build();

            mNotificationManager.notify(UPLOAD_NOTIFICATION_ID, basicNotification);
        }
    }
}
