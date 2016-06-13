package com.branch.test.sociallogin;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.CallbackManager;
import com.facebook.FacebookSdk;
import com.facebook.login.LoginManager;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.GoogleApiClient;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.ViewById;

import java.util.Arrays;

@EFragment(R.layout.login_fragment)
public class LoginFragment extends BaseLoginFragment {

    /** The Constant ARG_PARAM1. */
    protected static final String ARG_PARAM1 = "param1";

    /** The Constant ARG_PARAM2. */
    protected static final String ARG_PARAM2 = "param2";

    /** The view. */
    protected static View view;

    // TODO: Rename and change types of parameters
    /** The m param1. */
    protected String mParam1;

    /** The m param2. */
    protected String mParam2;

    /**
     * The instance.
     */
    private LoginFragment instance;

    private OnFragmentInteractionListener mListener;

    /**
     * The pd.
     */
    private ProgressDialog pd;


    @ViewById(R.id.login_page)
    LinearLayout login_page;
    @ViewById(R.id.google_login_button)
    LinearLayout google_login;
    @ViewById(R.id.facebook_login_button)
    LinearLayout facebook_login;

    @ViewById(R.id.fb_button_text)
    TextView fbButtonText;

    /**
     * Instantiates a new centers fragment.
     */
    public LoginFragment() {
        // Required empty public constructor
    }


    /**
     * On facebook button Click.
     */
    @Click(R.id.google_login_button)
    public void onGoogleButtonClick() {
        getIdToken();
    }

    @Click(R.id.facebook_login_button)
    public void onFacebookButtonOnClick() {
        Log.e("fb login", "button clicked");
        LoginManager.getInstance().logInWithReadPermissions(this, Arrays.asList("public_profile", "email", "user_friends"));

    }


    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment CantersFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static LoginFragment_ newInstance(String param1, int screenType) {
        LoginFragment_ fragment = new LoginFragment_();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putInt(ARG_PARAM2, screenType);
        fragment.setArguments(args);
        return fragment;
    }


    @AfterViews
    void updateView() {
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
        }
        instance = this;

        Token token = new Token();
        Toast.makeText(getContext(), token.getTokenId(),
                Toast.LENGTH_SHORT).show();
        // Views

        // For sample only: make sure there is a valid server client ID.
        validateServerClientID();

        // [START configure_signin]
        // Request only the user's ID token, which can be used to identify the
        // user securely to your backend. This will contain the user's basic
        // profile (name, profile picture URL, etc) so you should not need to
        // make an additional call to personalize your application.
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.server_client_id))
                .requestEmail()
                .build();
        // [END configure_signin]

        // Build GoogleAPIClient with the Google Sign-In API and the above options.
        mGoogleApiClient = new GoogleApiClient.Builder(getContext())
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();

        FacebookSdk.sdkInitialize(getActivity().getApplicationContext());
        callbackManager = CallbackManager.Factory.create();
        instantiateFacebookLoginProcess();

        checkIfSignup();
    }


    private void checkIfSignup() {
        //TODO: check if already signup from shared preference / db
    }

    private void postLoginProcess() {
        if (mListener != null) {
            mListener.loggedIn();
        }
    }


    /**
     * On attach.
     *
     * @param activity the activity
     */
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            if (mGoogleApiClient != null)
                mGoogleApiClient.connect();
            mListener = (OnFragmentInteractionListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    /**
     * On detach.
     */
    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
        callbackManager = null;
        mGoogleApiClient = null;
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    public interface OnFragmentInteractionListener {

        public void loggedIn();

    }

}
