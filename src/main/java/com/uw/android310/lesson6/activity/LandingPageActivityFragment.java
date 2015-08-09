package com.uw.android310.lesson6.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.uw.android310.lesson6.R;
import com.uw.android310.lesson6.util.Constants;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * A placeholder fragment containing a simple view.
 */
public class LandingPageActivityFragment extends Fragment {

    public static final String TAG = LandingPageActivity.class.getSimpleName();

    private String mUser;

    @Bind(R.id.button_uploaded_images)
    Button mUploadedImages;

    @Bind(R.id.button_upload_images)
    Button mUploadImages;

    public LandingPageActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_landing_page, container, false);

        ButterKnife.bind(this, rootView);

        //set the user name
        mUser = getActivity().getIntent().getStringExtra(Constants.userName);
        Log.d(TAG, "setting user name to: " + mUser);

        mUploadedImages.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "uploaded images button clicked");

                Intent intent = new Intent(getActivity(), UploadedFilesActivity.class);
                intent.putExtra(Constants.userName, mUser);
                startActivity(intent);
            }
        });

        mUploadImages.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "upload images button clicked");

                Intent intent = new Intent(getActivity(), MainActivity.class);
                //Intent intent = new Intent(getActivity(), UploadedFilesActivity.class);
                intent.putExtra(Constants.userName, mUser);
                startActivity(intent);

            }
        });

        return rootView;
    }
}
