package com.uw.android310.lesson6.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
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

    private View mRootView;

    @Bind(R.id.button_uploaded_images)
    Button mUploadedImages;

    @Bind(R.id.button_upload_images)
    Button mUploadImages;

    @Bind(R.id.button_logout)
    Button mLogout;

    public LandingPageActivityFragment() {
    }

    public View getView() {
        return mRootView;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_landing_page, container, false);
        mRootView = rootView;
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

        mLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "clicked on logout...");
                mUser = "";
                Intent intent = new Intent(getActivity(), LoginActivity.class);
                startActivity(intent);

                Snackbar.make(getView(), "Logging off", Snackbar.LENGTH_SHORT).show();
            }
        });

        return rootView;
    }
}
