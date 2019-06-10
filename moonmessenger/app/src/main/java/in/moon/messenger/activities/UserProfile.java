package in.moon.messenger.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import in.moon.messenger.R;

public class UserProfile extends AppCompatActivity {

    private ImageView profileImage;
    private TextView profileName;
    private DatabaseReference userDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_profile);

        final String user_id = getIntent().getStringExtra("user_id");
        final String user_name = getIntent().getStringExtra("user_name");

        userDatabase = FirebaseDatabase.getInstance().getReference().child("users").child(user_id);

        profileImage = findViewById(R.id.profile_image);
        profileName = findViewById(R.id.profile_displayName);

        Button msg = findViewById(R.id.msg);

        profileName.setText(user_name);

        msg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent profileIntent = new Intent(UserProfile.this, MessagingPage.class);
                profileIntent.putExtra("user_id", user_id);
                profileIntent.putExtra("user_name", user_name);
                startActivity(profileIntent);
            }
        });
    }

}
