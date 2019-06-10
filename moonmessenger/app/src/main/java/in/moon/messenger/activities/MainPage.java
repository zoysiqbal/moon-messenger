package in.moon.messenger.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;

import in.moon.messenger.R;
import in.moon.messenger.adapters.SectionsPagerAdapter;

public class MainPage extends AppCompatActivity {

    private FirebaseAuth auth;
    private Toolbar toolbar;

    private ViewPager viewPager;
    private SectionsPagerAdapter sectionsPagerAdapter;

    private DatabaseReference databaseReference;

    private TabLayout tabLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_page);

        auth = FirebaseAuth.getInstance();

        toolbar = (Toolbar) findViewById(R.id.main_page_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("moon messenger");

        if (auth.getCurrentUser() != null) {
            databaseReference = FirebaseDatabase.getInstance().getReference().child("users").child(auth.getCurrentUser().getUid());
        }

        //creating tabs in the view pager
        viewPager = (ViewPager) findViewById(R.id.main_tabPager);
        sectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        //set adapter for view pager
        viewPager.setAdapter(sectionsPagerAdapter);

        //includes tab layout with view pager
        tabLayout = (TabLayout) findViewById(R.id.main_tabs);
        tabLayout.setupWithViewPager(viewPager);
    }

    @Override
    public void onStart() {
        super.onStart();
        //checks if user is logged in
        FirebaseUser currentUser = auth.getCurrentUser();

        //check user status
        if (currentUser == null) {
            //if user is signed in, send to the start page (where chats are displayed)
            sendToStart();

        } else {
            databaseReference.child("online").setValue("true");
        }
    }


    @Override
    protected void onStop() {
        super.onStop();

        FirebaseUser currentUser = auth.getCurrentUser();
        if (currentUser != null) {
            databaseReference.child("online").setValue(ServerValue.TIMESTAMP);
        }
    }

    //if user is signed in, send to the start page (where chats are displayed)
    private void sendToStart() {

        Intent startIntent = new Intent(MainPage.this, StartPage.class);
        startActivity(startIntent);
        finish();

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);

        //enables menu bar to work
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);

        //if user selects this button it will log the user out
        if (item.getItemId() == R.id.main_logout_btn) {

            databaseReference.child("online").setValue(ServerValue.TIMESTAMP);

            FirebaseAuth.getInstance().signOut();
            sendToStart();

        }

        if (item.getItemId() == R.id.main_settings_btn) {

            //sends user to account settings page once button is clicked
            Intent settingsIntent = new Intent(MainPage.this, UserSettings.class);
            startActivity(settingsIntent);

        }

        return true;
    }
}
