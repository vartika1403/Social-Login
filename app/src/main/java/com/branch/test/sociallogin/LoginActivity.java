package com.branch.test.sociallogin;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Toast;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;


/*
 * Created by shubham on 25/8/15.
 */
@EActivity(R.layout.activity_login)
public class LoginActivity extends AbstractActionBarActivity implements LoginFragment.OnFragmentInteractionListener{


    private Context context;
    @ViewById(R.id.toolbar_actionbar)
    public Toolbar toolbar;


    @AfterViews
    void updateUI() {
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        getSupportFragmentManager().beginTransaction()
                .add(R.id.loginContainer, new LoginFragment_(), "loginFragment")
                .commit();


    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (savedInstanceState == null) {
            Bundle extras = getIntent().getExtras();
        }

        context = this;

    }


    @Override
    public void onBackPressed() {

            super.onBackPressed();
    }

    @Override
    public void warning(SAXParseException exception) throws SAXException {

    }

    @Override
    public void error(SAXParseException exception) throws SAXException {

    }

    @Override
    public void fatalError(SAXParseException exception) throws SAXException {

    }

    @Override
    public void loggedIn() {

    }

}
