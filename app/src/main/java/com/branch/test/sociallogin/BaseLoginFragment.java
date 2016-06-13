package com.branch.test.sociallogin;

import android.app.Activity;
import android.content.Intent;
import android.content.IntentSender;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.Profile;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.google.android.gms.auth.GoogleAuthException;
import com.google.android.gms.auth.GoogleAuthUtil;
import com.google.android.gms.auth.UserRecoverableAuthException;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;

import org.json.JSONObject;

import java.io.IOException;


public class BaseLoginFragment extends Fragment implements GoogleApiClient.OnConnectionFailedListener{
    //    google+ signIn constants
    private static final String TAG = "IdTokenActivity";
    private static final int RC_GET_TOKEN = 9002;
    private static final int REQ_SIGN_IN_REQUIRED = 55664;
    private String accessToken=null;
    protected GoogleApiClient mGoogleApiClient;
    private TextView mIdTokenTextView;
//    end of google+ SignIn constants
    /**
     * The Constant GOOGLE.
     */
    private static final String GOOGLE = "2";

    /** The callback manager. */
    protected CallbackManager callbackManager;
    /**
     * The Constant FACEBOOK.
     */
    private static final String FACEBOOK = "1";

    private String userEmailFromPhone;
    private boolean isDataOk = true;

    /** The m sign in OnClicked. */
    private boolean mSignInOnClicked;
    /* Request code used to invoke sign in user interactions. */
    /**
     * The Constant RC_SIGN_IN.
     */
    private static final int RC_SIGN_IN = 0;
    private int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;


     /* A flag indicating that a PendingIntent is in progress and prevents
     * us from starting further intents.
     */
    /**
     * The m intent in progress.
     */
    private boolean mIntentInProgress;


    //  start of google signin code
    protected void validateServerClientID() {
        String serverClientId = getString(R.string.server_client_id);
        String suffix = ".apps.googleusercontent.com";
        if (!serverClientId.trim().endsWith(suffix)) {
            String message = "Invalid server client ID in strings.xml, must end with " + suffix;

            Log.w(TAG, message);
            Toast.makeText(getActivity(), message, Toast.LENGTH_LONG).show();
        }
    }

    protected void getIdToken() {
        // Show an account picker to let the user choose a Google account from the device.
        // If the GoogleSignInOptions only asks for IDToken and/or profile and/or email then no
        // consent screen will be shown here.
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(signInIntent, RC_GET_TOKEN);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        try{
            callbackManager.onActivityResult(requestCode, resultCode, data);
        }catch (Exception e){
            e.printStackTrace();
        }

        if (requestCode == RC_GET_TOKEN) {
            // [START get_id_token]
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            handleSignInResult(result);
            Log.d(TAG, "onActivityResult:GET_TOKEN:success:" + result.getStatus().isSuccess());
            Toast.makeText(getContext(),""+result.getStatus().isSuccess(),Toast.LENGTH_SHORT).show();
            if (result.isSuccess()) {
                GoogleSignInAccount acct = result.getSignInAccount();
                String idToken = acct.getIdToken();
                Token token = new Token();
                token.setSignedIn(result.getStatus().isSuccess());
                // Show signed-in UI.
                Log.d(TAG, "idToken:" + idToken);
                // TODO(user): send token to server and validate server-side
                doGoogleNetworkCall(idToken);

            } else {
                Toast.makeText(getActivity(), "Error signing In", Toast.LENGTH_SHORT).show();
            }
            // [END get_id_token]
        }
    }


    private class RetrieveTokenTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {
            String accountName = params[0];
            String scopes = "oauth2:profile email";
            try {
                accessToken = GoogleAuthUtil.getToken(getContext(), accountName, scopes);
            } catch (IOException e) {
                Log.e(TAG, e.getMessage());
            } catch (UserRecoverableAuthException e) {
                startActivityForResult(e.getIntent(), REQ_SIGN_IN_REQUIRED);
            } catch (GoogleAuthException e) {
                Log.e(TAG, e.getMessage());
            }
            return accessToken;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
        }
    }


    private void handleSignInResult(GoogleSignInResult result) {
        Log.d(TAG, "handleSignInResult:" + result.isSuccess());
        if (result.isSuccess()) {
            // Signed in successfully, show authenticated UI.
            GoogleSignInAccount acct = result.getSignInAccount();
            Log.d(TAG, "SignInName:" + acct.getDisplayName());
            Log.d(TAG, "SignInId:" + acct.getId());
            Log.d(TAG, "SignInName:" + acct.getEmail());
            Toast.makeText(getContext(),acct.getEmail(),
                    Toast.LENGTH_SHORT).show();
            new RetrieveTokenTask().execute(acct.getEmail());

        } else {
            // Signed out, show unauthenticated UI.
            Toast.makeText(getContext(), "Error signing In", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        toastAppropriateError(connectionResult.getErrorCode());
        if (!mIntentInProgress) {
            if (mSignInOnClicked && connectionResult.hasResolution()) {
                try {
                    connectionResult.startResolutionForResult((Activity) getActivity(), RC_SIGN_IN);
                    mIntentInProgress = true;
                } catch (IntentSender.SendIntentException e) {
                    mIntentInProgress = false;
                    mGoogleApiClient.connect();
                }
            }
        }
    }

//    end of google sign in code

    private void toastAppropriateError(int errorCode) {
        switch (errorCode){
            case ConnectionResult.SERVICE_VERSION_UPDATE_REQUIRED:
                toastError(getString(R.string.SERVICE_VERSION_UPDATE_REQUIRED));
                break;
            case ConnectionResult.SERVICE_DISABLED:
                toastError(getString(R.string.SERVICE_DISABLED));
                break;
            case ConnectionResult.SERVICE_INVALID:
                toastError(getString(R.string.SERVICE_INVALID));
                break;
            case ConnectionResult.SERVICE_MISSING:
                toastError(getString(R.string.SERVICE_MISSING));
                break;
        }
    }

    /**
     * Toast error.
     *
     * @param string the string
     */
    private void toastError(String string) {
        Toast.makeText(getActivity(), string, Toast.LENGTH_LONG).show();
    }


    private void doGoogleNetworkCall(String idtoken){
        //TODO: save this google id token in shared preference / db
    }

    /**
     * Instantiate facebook login process.
     */
    protected void instantiateFacebookLoginProcess() {
        LoginManager.getInstance().registerCallback(callbackManager,
                new FacebookCallback<LoginResult>() {
                    @Override
                    public void onSuccess(LoginResult loginResult) {
                        Toast.makeText(getActivity().getApplicationContext(), "Success", Toast.LENGTH_LONG).show();
                        Profile profile = Profile.getCurrentProfile();
                        getAccessTokenForFacebook();
                        mapSocialAuthRequest(loginResult.getAccessToken().getToken(), FACEBOOK);

                    }

                    @Override
                    public void onCancel() {
                        Log.e("fbLogin", "cancelled");
                    }

                    @Override
                    public void onError(FacebookException exception) {
                        Toast.makeText(getActivity().getApplicationContext(), "Error", Toast.LENGTH_LONG).show();
                    }

                });

    }

    /**
     * Gets the access token for facebook.
     *
     * @return the access token for facebook
     */
    private void getAccessTokenForFacebook() {
        GraphRequest request = GraphRequest.newMeRequest(
                AccessToken.getCurrentAccessToken(),
                new GraphRequest.GraphJSONObjectCallback() {
                    @Override
                    public void onCompleted(JSONObject jsonObject, GraphResponse response) {
//                        Toast.makeText(getActivity().getApplicationContext(), "  ", Toast.LENGTH_LONG).show();
                    }
                });
        Bundle parameters = new Bundle();
        parameters.putString("fields", "id,name,link,cover,email");
        request.setParameters(parameters);
        request.executeAsync();
    }

    public boolean checkPlayServices() {
        int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(getActivity());
        if (resultCode != ConnectionResult.SUCCESS) {
            if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
                GooglePlayServicesUtil.getErrorDialog(resultCode, getActivity(),
                        PLAY_SERVICES_RESOLUTION_REQUEST).show();
            } else {
                Log.i("ZZZ", "This device is not supported.");

            }
            return false;
        }
        return true;
    }

    /**
     * Map social auth request.
     *TODO:add check for google sign in
     * @param response the response
     * @param provider the provider
     */
    private void mapSocialAuthRequest(String response, final String provider) {
        Log.e("token_fb", response);

        //TODO: save this fb auth token in shared preference / db
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        if (mGoogleApiClient != null)
            mGoogleApiClient.connect();

    }
    @Override
    public void onDetach() {
        super.onDetach();
        callbackManager = null;
        mGoogleApiClient = null;
    }


    @Override
    public void onDestroy() {
        callbackManager = null;
        mGoogleApiClient = null;
        super.onDestroy();
    }

    @Override
    public void onDestroyView() {
        callbackManager = null;
        mGoogleApiClient = null;
        super.onDestroyView();
    }
}
