package in.moon.messenger.activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import in.moon.messenger.R;

public class StartPage extends AppCompatActivity {

    private Button registrationButton;
    private Button loginButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.start_page);

        registrationButton = (Button) findViewById(R.id.start_reg_btn);
        loginButton = (Button) findViewById(R.id.start_login_btn);

        //opens registration page
        registrationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent reg_intent = new Intent(StartPage.this, RegistrationPage.class);
                startActivity(reg_intent);
            }
        });

        //opens login page
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent login_intent = new Intent(StartPage.this, LoginPage.class);
                startActivity(login_intent);

            }
        });

    }
}
