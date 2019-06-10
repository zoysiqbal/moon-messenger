package in.moon.messenger.activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import de.hdodenhof.circleimageview.CircleImageView;
import in.moon.messenger.R;

public class UserSettings extends AppCompatActivity {

    private DatabaseReference userDatabase;
    private FirebaseUser currentUser;
    private CircleImageView displayImage;
    private TextView username;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_settings);

        displayImage = (CircleImageView) findViewById(R.id.settings_image);
        username = (TextView) findViewById(R.id.settings_name);

        currentUser = FirebaseAuth.getInstance().getCurrentUser();

        String current_uid = currentUser.getUid();

        userDatabase = FirebaseDatabase.getInstance().getReference().child("users").child(current_uid);
        userDatabase.keepSynced(true);

        //check user data listener
        userDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                String name = dataSnapshot.child("name").getValue().toString();

                username.setText(name);

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

}
