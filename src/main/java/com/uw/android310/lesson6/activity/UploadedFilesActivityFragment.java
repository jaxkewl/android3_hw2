package com.uw.android310.lesson6.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.uw.android310.lesson6.R;
import com.uw.android310.lesson6.model.ImageD;
import com.uw.android310.lesson6.model.ImageDelete;
import com.uw.android310.lesson6.service.ImageDeleteService;
import com.uw.android310.lesson6.util.Constants;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import butterknife.Bind;
import butterknife.ButterKnife;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * A placeholder fragment containing a simple view.
 */
public class UploadedFilesActivityFragment extends Fragment {
    HashMap<String, String> mDeleteUrlHashMap = new HashMap<String, String>();

    public static final String TAG = UploadedFilesActivityFragment.class.getSimpleName();

    private ArrayList<String> mUploadedImages = new ArrayList<String>();

    private String mUser;

    private View mView;

    @Bind(R.id.list_view_uploaded_images)
    ListView mUploadedImagesListView;

    /**
     * Delete object containing deletehash and meta data
     */
    private ImageDelete mDelete;

    public UploadedFilesActivityFragment() {
    }


    private void displayAllSharedPref() {
        Log.d(TAG, "displayAllSharedPref called");

        SharedPreferences sharedPref = getActivity().getSharedPreferences(Constants.sharedPrefName, getActivity().MODE_PRIVATE);

        Map<String, ?> keys = sharedPref.getAll();

        for (Map.Entry<String, ?> entry : keys.entrySet()) {
            Log.d(TAG, "***************** ***************** " + entry.getKey() + ": " + entry.getValue().toString());
        }
    }

    //for each unique key, generate a delete Url
    private void populateDeleteUrlHashmap() {
        mDeleteUrlHashMap = new HashMap<String, String>();

    }

    private void deleteKeyFromSharedPref(String key) {
        Log.d(TAG, "deleteKeyFromSharedPref " + key);
        SharedPreferences sharedPref = getActivity().getSharedPreferences(Constants.sharedPrefName, getActivity().MODE_PRIVATE);

        Map<String, ?> keys = sharedPref.getAll();

        for (Map.Entry<String, ?> entry : keys.entrySet()) {
            Log.d(TAG, "***************** ***************** " + entry.getKey() + ": " + entry.getValue().toString());

            if (entry.getKey().contains(key)) {
                Log.d(TAG, "deleting key " + entry.getKey());
                sharedPref.edit().remove(entry.getKey()).commit();
            }
            else if (entry.getValue().toString().contains(key)) {
                Log.d(TAG, "deleting key because value contains what we are looking for " + entry.getKey());
                sharedPref.edit().remove(entry.getKey()).commit();  //GOTCHA: call commit right after removing key
            }

        }
        displayAllSharedPref();
    }


    private String getFileKey(String url) {
        String uploadKey = "";
        Pattern expression = Pattern.compile("\\.com\\/(\\w+)\\.jpg");
        Matcher matcher = expression.matcher(url);
        while (matcher.find()) {
            Log.d(TAG, "captureDeletehash: " + matcher.group());
            Log.d(TAG, "found upload key of: " + matcher.group(1));
            uploadKey = matcher.group(1);
            break;
        }
        return uploadKey;
    }

    //this method will use the URL in the argument to return the matching delete hash
    //that was saved off when the image was initially uploaded anonymously
    private String captureDeletehash(String url) {
        String deletehash = "";

        String uploadKey = getFileKey(url);

        //if we found an upload key find the deletehash that matches this upload key
        if (uploadKey.equals("")) {
            return "";
        }

        SharedPreferences sharedPref = getActivity().getSharedPreferences(Constants.sharedPrefName, getActivity().MODE_PRIVATE);

        Map<String, ?> keys = sharedPref.getAll();

        mUploadedImages = new ArrayList<String>();

        //if the shred pref contains the upload key and ends with delete, then we found the deletehash
        for (Map.Entry<String, ?> entry : keys.entrySet()) {
            if (entry.getKey().contains(uploadKey) && (entry.getKey().endsWith("delete"))) {
                Log.d(TAG, "***************** found delete hash ***************** " + entry.getKey() + ": " + entry.getValue().toString());
                return entry.getValue().toString();
            }
        }

        return deletehash;
    }

    private void captureAllUploadedImages() {
        Log.d(TAG, "captureAllUploadedImages called");

        SharedPreferences sharedPref = getActivity().getSharedPreferences(Constants.sharedPrefName, getActivity().MODE_PRIVATE);

        Map<String, ?> keys = sharedPref.getAll();

        mUploadedImages = new ArrayList<String>();

        for (Map.Entry<String, ?> entry : keys.entrySet()) {
            if (entry.getKey().startsWith(mUser + "_image") && !(entry.getKey().endsWith("delete"))) {
                mUploadedImages.add(entry.getValue().toString());
                Log.d(TAG, "***************** ***************** " + entry.getKey() + ": " + entry.getValue().toString());
            }
        }
    }


    private void displayAllUploadedImages() {
        Log.d(TAG, "displayAllUploadedImages called...");
        for (String str : mUploadedImages) {
            Log.d(TAG, "***************** ***************** " + str);
        }
    }

    private void init(View rootView) {

        //set the user name
        mUser = getActivity().getIntent().getStringExtra(Constants.userName);
        Log.d(TAG, "setting user name to: " + mUser);

        captureAllUploadedImages();

        displayAllSharedPref();
        displayAllUploadedImages();


    }


    private void confirmDelete(final String deletehash, final String fileKey) {
        Log.d(TAG, "confirmDelete called with " + deletehash + " " + fileKey);

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage(R.string.dialog_message)
                .setTitle(R.string.dialog_title);

        // Add the buttons
        builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                Snackbar.make(mView, "Deleting image with deletehash " + deletehash, Snackbar.LENGTH_LONG).show();
                deleteImage(deletehash);
                deleteKeyFromSharedPref(fileKey);

                //reload the activity
                getActivity().finish();
                startActivity(getActivity().getIntent());
            }
        });
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User cancelled the dialog

                //reload the activity
                getActivity().finish();
                startActivity(getActivity().getIntent());
            }
        });

        // Create the AlertDialog
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_uploaded_files, container, false);
        mView = rootView;
        ButterKnife.bind(this, rootView);

        init(rootView);


        ImageListAdapter imageListAdapter = new ImageListAdapter(getActivity(), mUploadedImages);
        mUploadedImagesListView.setAdapter(imageListAdapter);

        mUploadedImagesListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Log.d(TAG, "onItemClick " + mUploadedImages.size() + " " + i + " " + l);

                final String fileKey = getFileKey(mUploadedImages.get(i));
                final String deletehash = captureDeletehash(mUploadedImages.get(i));

                Log.d(TAG, "deletehash and filekey " + deletehash + " " + fileKey);

                //don't delete right away, confirm with user first
                confirmDelete(deletehash, fileKey);
            }
        });

        return rootView;
    }

    private void createDelete(String deletehash) {
        mDelete = new ImageDelete();
        mDelete.deletehash = deletehash;
    }

    public void deleteImage(String deletehash) {
        Log.d(TAG, "deleteImage clicked " + deletehash);

        if (null == deletehash) {
            return;
        }

        // Wrap the chosen image in an upload object (to be sent to API).
        createDelete(deletehash);

        // Initiate delete
        new ImageDeleteService(getActivity()).execute(mDelete, new UiCallback());

    }

    private class UiCallback implements Callback<ImageD> {

        @Override
        public void success(ImageD imageResponse, Response response) {
            Log.d(TAG, "success called... this is a callback after deleting an image ");
            Snackbar.make(mView, "delete successful " + response.toString(), Snackbar.LENGTH_LONG).show();
/*
            NotificationCompat.Builder builder =
                    new NotificationCompat.Builder(getContext())
                            .setSmallIcon(R.drawable.notification_template_icon_bg)
                            .setContentTitle("Image Uploaded to Imgur")
                            .setContentText("Delete URL: " + deleteUrl);

            // Create the notification
            Notification basicNotification = builder.build();

            mNotificationManager.notify(UPLOAD_NOTIFICATION_ID, basicNotification);
*/


        }

        @Override
        public void failure(RetrofitError error) {
            //Assume we have no connection, since error is null
            if (error == null) {
                Snackbar.make(getActivity().findViewById(R.id.rootView), "No internet connection", Snackbar.LENGTH_SHORT).show();
            }

            Snackbar.make(mView, "failure to delete " + error.toString(), Snackbar.LENGTH_LONG).show();
            Log.d(TAG, "failure to delete... " + error.toString());

/*            NotificationCompat.Builder builder =
                    new NotificationCompat.Builder(getContext())
                            .setSmallIcon(R.drawable.notification_template_icon_bg)
                            .setContentTitle("Image Failed to upload to Imgur")
                            .setContentText("Try again");

            // Create the notification
            Notification basicNotification = builder.build();

            mNotificationManager.notify(UPLOAD_NOTIFICATION_ID, basicNotification);*/
        }
    }

}
