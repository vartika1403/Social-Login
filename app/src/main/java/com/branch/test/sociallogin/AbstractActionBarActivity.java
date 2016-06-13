package com.branch.test.sociallogin;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import org.xml.sax.ErrorHandler;

public abstract class AbstractActionBarActivity extends ActionBarActivity implements ErrorHandler {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // handleUncaughtExceptions();
    }


    protected void hideKeyboardIfVisible() {
        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager inputManager = (InputMethodManager) this.getSystemService(Context.INPUT_METHOD_SERVICE);
            inputManager.hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }

    protected boolean isKeyboardVisible(){
        View view = this.getCurrentFocus();
        if(view!=null) {
            InputMethodManager imm = (InputMethodManager) this
                    .getSystemService(Context.INPUT_METHOD_SERVICE);

            if (imm.isAcceptingText()) {
                return true;
            } else {
                return false;
            }
        }
        return false;
    }

    @Override
    protected void onResume(){
        super.onResume();
        hideKeyboardIfVisible();
    }
}
