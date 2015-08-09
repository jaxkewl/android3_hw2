package com.uw.android310.lesson6.service;


import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import com.uw.android310.lesson6.api.ImgurRestApi;

import com.uw.android310.lesson6.model.ImageD;
import com.uw.android310.lesson6.model.ImageDelete;
import com.uw.android310.lesson6.util.Constants;
import com.uw.android310.lesson6.util.NetworkUtils;

import java.lang.ref.WeakReference;

import retrofit.Callback;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class ImageDeleteService extends Service {
    public final static String TAG = ImageDeleteService.class.getSimpleName();

    private WeakReference<Context> mContext;

    public ImageDeleteService(Context context) {
        this.mContext = new WeakReference<>(context);
    }


    public void execute(ImageDelete deleteImage, final Callback<ImageD> callback) {

        Log.d(TAG, "********* execute called... " + deleteImage);

        // Check network connection before making a call
        if (!NetworkUtils.isConnected(mContext.get())) {
            callback.failure(null);
            return;
        }

        RestAdapter restAdapter = buildRestAdapter();

        restAdapter.create(ImgurRestApi.class).deleteImage(
                Constants.getClientAuth(),                  // Client auth
                deleteImage.deletehash,    //delete hash of image, generated when image was uploaded anonymously
                new Callback<ImageD>() {
                    @Override
                    public void success(ImageD imageResponse, Response response) {
                        Log.d(TAG, "******** success called...");

                        if (callback != null) {
                            callback.success(imageResponse, response);
                        }

                        /**
                         * TODO: Notify image was NOT deleted successfully
                         */
                        if (response == null) {
                            Log.d(TAG, "Image failed to delete successfully.");
                        }
                        /**
                         * TODO: Notify image was deleted successfully
                         */
                        if (imageResponse.success) {
                            Log.d(TAG, "Image was deleted successfully: " + imageResponse.toString());
                            //Log.d(TAG, "image uploaded to: " + imageResponse.getLink());


                        }
                    }

                    @Override
                    public void failure(RetrofitError error) {
                        if (callback != null) {
                            callback.failure(error);
                        }
                        /**
                         * TODO: Notify image was NOT deleted successfully
                         */
                    }
                });
    }

    private RestAdapter buildRestAdapter() {
        RestAdapter imgurAdapter = new RestAdapter.Builder()
                .setLogLevel(RestAdapter.LogLevel.FULL)
                .setEndpoint(ImgurRestApi.server)
                .build();

        /*
        Set rest adapter logging if we're already logging
        */
        if (Constants.LOGGING) {
            imgurAdapter.setLogLevel(RestAdapter.LogLevel.FULL);
        }
        return imgurAdapter;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
