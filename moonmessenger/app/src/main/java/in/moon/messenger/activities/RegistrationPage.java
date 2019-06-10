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
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;

import java.util.HashMap;

import in.moon.messenger.R;

public class RegistrationPage extends AppCompatActivity {

    private TextInputLayout username;
    private TextInputLayout emailAddress;
    private TextInputLayout password;
    private Button registerButton;
    private Toolbar toolbar;
    private DatabaseReference databaseReference;

    //creates progress dialog
    private ProgressDialog registrationDialog;

    //authenticates connection to firebase
    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.registration_page);

        //sets toolbar
        toolbar = findViewById(R.id.register_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("create account");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        registrationDialog = new ProgressDialog(this);

        //authenticates connection to firebase
        auth = FirebaseAuth.getInstance();

        username = findViewById(R.id.register_display_name);
        emailAddress = findViewById(R.id.register_email);
        password = findViewById(R.id.reg_password);
        registerButton = findViewById(R.id.reg_create_btn);

        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String user_name = username.getEditText().getText().toString();
                String user_email = emailAddress.getEditText().getText().toString();
                String user_password = RegistrationPage.this.password.getEditText().getText().toString();

                if (!TextUtils.isEmpty(user_name) || !TextUtils.isEmpty(user_email) || !TextUtils.isEmpty(user_password)) {
                    //show progress dialog
                    registrationDialog.setMessage("please wait whilst we register your account");
                    registrationDialog.show();

                    registerNewUser(user_name, user_email, user_password);

                }
            }
        });
    }

    //retrieves data from firebase
    private void registerNewUser(final String userName, String emailAddress, String password) {

        auth.createUserWithEmailAndPassword(emailAddress, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {

                if (task.isSuccessful()) {

                    //retrieves user ID to store in realtime database
                    FirebaseUser current_user = FirebaseAuth.getInstance().getCurrentUser();
                    String uid = current_user.getUid();

                    //writes a message to the database (adds child and user ID and sets values)
                    databaseReference = FirebaseDatabase.getInstance().getReference().child("users").child(uid);

                    String deviceToken = FirebaseInstanceId.getInstance().getToken();

                    //hash map stores values in the database children
                    HashMap<String, String> userHashMap = new HashMap<>();
                    userHashMap.put("name", userName);
                    userHashMap.put("image", "default");
                    userHashMap.put("device_token", deviceToken);

                    databaseReference.setValue(userHashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {

                            if (task.isSuccessful()) {

                                //dismiss the error message
                                registrationDialog.dismiss();

                                //if user successfully logs in
                                Intent mainIntent = new Intent(RegistrationPage.this, MainPage.class);
                                mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(mainIntent);
                                finish();

                            }

                        }
                    });


                } else {

                    registrationDialog.hide();
                    Toast.makeText(RegistrationPage.this, "registration unsuccessful; please try again", Toast.LENGTH_LONG).show();

                }

            }
        });

    }
}
