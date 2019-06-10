package in.moon.messenger.activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;

import in.moon.messenger.R;

public class LoginPage extends AppCompatActivity {

    private Toolbar toolbar;
    private TextInputLayout email;
    private TextInputLayout pass;
    private Button loginButton;
    private ProgressDialog loginDialog;
    private FirebaseAuth auth;
    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_page);

        //firebase is initialised
        auth = FirebaseAuth.getInstance();

        init();

        //click on login
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //retrieves values of input fields
                String emailAddress = email.getEditText().getText().toString();
                String password = pass.getEditText().getText().toString();

                //checks if values are empty
                if (!TextUtils.isEmpty(emailAddress) || !TextUtils.isEmpty(password)) {

                    //notifies the user that they are being logged in
                    loginDialog.setMessage("please wait whilst we log you in");
                    loginDialog.show();

                    //starts the login
                    loginUser(emailAddress, password);
                }
            }
        });
    }

    //initialising variables
    private void init() {
        toolbar = findViewById(R.id.login_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("login");

        loginDialog = new ProgressDialog(this);

        //adds user information into database
        databaseReference = FirebaseDatabase.getInstance().getReference().child("users");
        email = findViewById(R.id.login_email);
        pass = findViewById(R.id.login_password);
        loginButton = findViewById(R.id.login_btn);
    }


    //making login call
    private void loginUser(String emailAddress, String password) {


        auth.signInWithEmailAndPassword(emailAddress, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {

                //checks if user is signed in or not
                if (task.isSuccessful()) {
                    loginDialog.dismiss();

                    //creates current user session
                    String current_user_id = auth.getCurrentUser().getUid();
                    String deviceToken = FirebaseInstanceId.getInstance().getToken();

                    databaseReference.child(current_user_id).child("device_token").setValue(deviceToken).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Intent mainIntent = new Intent(LoginPage.this, MainPage.class);
                            mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(mainIntent);
                            finish();
                        }
                    });

                } else {

                    loginDialog.hide();
                    String task_result = task.getException().getMessage().toString();

                    //error message
                    Toast.makeText(LoginPage.this, "error: " + task_result, Toast.LENGTH_LONG).show();
                }

            }
        });
    }
}
