package com.uw.android310.lesson6.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.uw.android310.lesson6.R;
import com.uw.android310.lesson6.util.Constants;

import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * A placeholder fragment containing a simple view.
 */
public class LoginActivityFragment extends Fragment {

    public final static String TAG = LoginActivity.class.getSimpleName();

    @Bind(R.id.button_login)
    Button mLoginButton;

    @Bind(R.id.button_create_user)
    Button mCreateNewButton;

    @Bind(R.id.edit_text_email)
    EditText mEditTextEmailAddr;

    @Bind(R.id.edit_text_password)
    EditText mEditTextPassword;


    public LoginActivityFragment() {
    }

    private void init(final View rootView) {

        displayAllSharedPref();

        //set on click listeners for the buttons
        mCreateNewButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = mEditTextEmailAddr.getText().toString();
                String pass = mEditTextPassword.getText().toString();

                handleCreateNewUser();
                Snackbar.make(rootView, "You clicked on add new user " + email + " " + pass, Snackbar.LENGTH_SHORT).show();
                displayAllSharedPref();
            }
        });


        mLoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String email = mEditTextEmailAddr.getText().toString();
                String pass = mEditTextPassword.getText().toString();

                boolean loginValidated = validateLogin(email, pass);
                Snackbar.make(rootView, "You clicked on login " + email + " " + pass, Snackbar.LENGTH_SHORT).show();

                if (loginValidated) {
                    Intent intent = new Intent(getActivity(), LandingPageActivity.class);
                    //Intent intent = new Intent(getActivity(), UploadedFilesActivity.class);
                    intent.putExtra(Constants.userName, email);
                    startActivity(intent);
                }


            }
        });
    }


    private boolean isValidEmail(String email) {

        boolean emailValid = !Patterns.EMAIL_ADDRESS.matcher(mEditTextEmailAddr.getText()).matches();

        //all email addresses contain an '@' and a '.'
        if (email.contains("@") && email.contains(".")) {
            return true;
        } else return false;
    }

    private boolean validCredentials(String email, String pass) {

        boolean validated = true;

        if (!isValidEmail(email)) {
            mEditTextEmailAddr.setError("Need a valid email");
            validated = false;
        }

        if (mEditTextPassword.getText().toString().trim().equals("")) {
            mEditTextPassword.setError("Need a valid password");
            validated = false;
        }

        if (!validated) return false;

        return validated;
    }


    /*this method will check if the email given is part of the shared pref and if it is
    it will check the password*/
    private boolean validateLogin(String email, String pass) {
        Log.d(TAG, "validateLogin called with " + email + " and " + pass);

        if (!validCredentials(email, pass)) return false;

        //determine if email address is in shared pref
        SharedPreferences sharedPref = getActivity().getSharedPreferences(Constants.sharedPrefName, getActivity().MODE_PRIVATE);
        if (sharedPref.contains(email)) {
            //email address exists, check password
            Log.d(TAG, "email address found in shared pref");
            if (sharedPref.getString(email, null).equals(pass)) {
                Log.d(TAG, "password matches!");
                return true;
            } else {
                Log.d(TAG, "passwords do not match, you entered: " + pass + ", but actual password is: " + sharedPref.getString(email, null));
            }
        }
        return false;
    }


    private void displayAllSharedPref() {
        Log.d(TAG, "displayAllSharedPref called");

        SharedPreferences sharedPref = getActivity().getSharedPreferences(Constants.sharedPrefName, getActivity().MODE_PRIVATE);

        Map<String, ?> keys = sharedPref.getAll();

        for (Map.Entry<String, ?> entry : keys.entrySet()) {
            Log.d(TAG, "***************** ***************** " + entry.getKey() + ": " + entry.getValue().toString());
        }
    }

    private void validateCredentials() {
        Log.d(TAG, "validateCredentials called");
    }

    private void handleCreateNewUser() {
        Log.d(TAG, "handleCreateNewUser called");


        //store all email address as lowercase
        String email = mEditTextEmailAddr.getText().toString().trim().toLowerCase();
        String pass = mEditTextPassword.getText().toString();

        //determine if email address is already in shared pref
        SharedPreferences sharedPref = getActivity().getSharedPreferences(Constants.sharedPrefName, getActivity().MODE_PRIVATE);
        if (sharedPref.contains(email)) {
            //email address exists, check password
            mEditTextEmailAddr.setError("Email already exists, please login");
            return;
        }

        boolean validated = validCredentials(email, pass);

        if (validated) {
            SharedPreferences.Editor editor = sharedPref.edit();
            editor.putString(email, pass);
            editor.commit();
        }
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_login, container, false);

        ButterKnife.bind(this, rootView);

        init(rootView);
        return rootView;
    }
}
