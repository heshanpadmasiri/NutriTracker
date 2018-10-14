package com.app.nutritracker.nutritracker;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.IdpResponse;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.fitness.FitnessOptions;
import com.google.android.gms.fitness.data.DataType;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Arrays;
import java.util.List;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener,DietFragment.OnFragmentInteractionListener,GraphFragment.OnFragmentInteractionListener,SettingsFragment.OnFragmentInteractionListener {

    private static final int RC_SIGN_IN = 1;
    private static final int GOOGLE_FIT_PERMISSION_CODE = 2;

    private FitnessOptions mFitnessOptions;



    private FirebaseAuth mAuth;
    private TextView txtView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);



        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        mAuth = FirebaseAuth.getInstance();
        //txtView = findViewById(R.id.txtView);
        android.app.FragmentManager fragmentManager=getFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.content_frame,new DietFragment()).commit();

        mFitnessOptions = FitnessOptions.builder()
                .addDataType(DataType.TYPE_STEP_COUNT_DELTA, FitnessOptions.ACCESS_READ)
                .addDataType(DataType.TYPE_STEP_COUNT_CUMULATIVE, FitnessOptions.ACCESS_READ)
                .addDataType(DataType.AGGREGATE_STEP_COUNT_DELTA, FitnessOptions.ACCESS_READ)
                .addDataType(DataType.AGGREGATE_CALORIES_EXPENDED, FitnessOptions.ACCESS_READ)
                .build();

    }

    protected void startAuthentication(){
        List<AuthUI.IdpConfig> providers = Arrays.asList(
                new AuthUI.IdpConfig.GoogleBuilder().build()
        );
        startActivityForResult(
                AuthUI.getInstance()
                        .createSignInIntentBuilder()
                        .setAvailableProviders(providers)
                        .build(),
                RC_SIGN_IN);
    }

    protected void getSensorPermission(){
        if (!GoogleSignIn.hasPermissions(GoogleSignIn.getLastSignedInAccount(this), mFitnessOptions)){
            GoogleSignIn.requestPermissions(
                    this,
                    GOOGLE_FIT_PERMISSION_CODE,
                    GoogleSignIn.getLastSignedInAccount(this),
                    mFitnessOptions

            );
        } else {
            accessGoogleFit();
        }
    }

    private void onUserLogin(){
        // use this to pull data from users Google profile

        NavigationView navigationView = findViewById(R.id.nav_view);
        View headerView = navigationView.getHeaderView(0);


        FirebaseUser user = AuthenticationService.getUser();
        LayoutInflater inflater = (LayoutInflater)this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.nav_header_main, null);
        TextView userName = headerView.findViewById(R.id.userName);
        if (user.getDisplayName() != null){
            userName.setText(user.getDisplayName());
        }
        if (user.getPhotoUrl() != null){
            ImageView userImage = headerView.findViewById(R.id.userPic);
            new DownloadImageTask(userImage).execute(user.getPhotoUrl().toString());
        }
    }



    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser user = mAuth.getCurrentUser();
        if (user == null){
            // user haven't logged in
            startAuthentication();
        } else {
            getSensorPermission();
            onUserLogin();
        }
    }

    private void accessGoogleFit() {

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN){
            IdpResponse response = IdpResponse.fromResultIntent(data);
            if (resultCode == RESULT_OK){
                onUserLogin();
            } else {
                // login failed
                txtView.setText("login failed");
            }
        } else if (requestCode == GOOGLE_FIT_PERMISSION_CODE){
            if (resultCode == RESULT_OK){
                // fit permission granted
                // todo: get user step count data
                accessGoogleFit();
            } else {
                // fit permission rejected
            }
        }
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();



        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        android.app.FragmentManager fragmentManager=getFragmentManager();
        int id = item.getItemId();

        if (id == R.id.nav_diet) {
            fragmentManager.beginTransaction().replace(R.id.content_frame,new DietFragment()).commit();
        } else if (id == R.id.nav_graph) {
            fragmentManager.beginTransaction().replace(R.id.content_frame,new GraphFragment()).commit();
        } else if (id == R.id.nav_settings) {
            fragmentManager.beginTransaction().replace(R.id.content_frame,new SettingsFragment()).commit();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }
}


