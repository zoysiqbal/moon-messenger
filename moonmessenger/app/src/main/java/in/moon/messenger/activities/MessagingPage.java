package in.moon.messenger.activities;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;
import in.moon.messenger.R;
import in.moon.messenger.adapters.MessagesAdapter;
import in.moon.messenger.utils.TimeStamp;
import in.moon.messenger.utils.Messages;

public class MessagingPage extends AppCompatActivity {

    private String user;
    private Toolbar toolbar;
    private DatabaseReference databaseReference;
    private TextView titleTextView;
    private TextView lastSeenTextView;
    private CircleImageView userProfileImage;
    private FirebaseAuth auth;
    private String userID;
    private ImageButton sendButton;
    private EditText messageInput;
    private RecyclerView messageList;

    private final List<Messages> messagesList = new ArrayList<>();
    private LinearLayoutManager manager;
    private MessagesAdapter messagesAdapter;

    //total number of messages to display in window
    private static final int TOTAL_ITEMS_TO_LOAD = 10;
    private int currentPage = 1;

    //messages positioning
    private int itemPos = 0;

    private String lastKey = "";
    private String previousKey = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_messaging_page);

        //sets toolbar
        toolbar = findViewById(R.id.chat_app_bar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowCustomEnabled(true);

        //retrieves reference from database
        databaseReference = FirebaseDatabase.getInstance().getReference();
        auth = FirebaseAuth.getInstance();
        userID = auth.getCurrentUser().getUid();

        //retrieves data from previous
        user = getIntent().getStringExtra("user_id");
        String userName = getIntent().getStringExtra("user_name");

        LayoutInflater inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View action_bar_view = inflater.inflate(R.layout.username_bar, null);

        getSupportActionBar().setCustomView(action_bar_view);

        //initialise views
        titleTextView = (TextView) findViewById(R.id.custom_bar_title);
        lastSeenTextView = (TextView) findViewById(R.id.custom_bar_seen);
        userProfileImage = (CircleImageView) findViewById(R.id.custom_bar_image);
        sendButton = (ImageButton) findViewById(R.id.chat_send_btn);
        messageInput = (EditText) findViewById(R.id.chat_message_view);
        messagesAdapter = new MessagesAdapter(this, messagesList);
        messageList = (RecyclerView) findViewById(R.id.messages_list);
        manager = new LinearLayoutManager(this);

        messageList.setHasFixedSize(true);
        messageList.setLayoutManager(manager);
        messageList.setAdapter(messagesAdapter);
        databaseReference.child("chat").child(userID).child(user).child("seen").setValue(true);

        loadAllMessages();

        titleTextView.setText(userName);

        //retrieves data from firebase database
        databaseReference.child("users").child(user).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                //checks online status
                String onlineStatus = dataSnapshot.child("online").getValue().toString();
                if (onlineStatus.equals("true")) {
                    lastSeenTextView.setText("online");
                } else {
                    TimeStamp timeStamp = new TimeStamp();
                    long lastTime = Long.parseLong(onlineStatus);
                    String lastSeenTime = timeStamp.getTimeAgo(lastTime, getApplicationContext());
                    lastSeenTextView.setText(lastSeenTime);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


        //retrieves data from firebase database
        databaseReference.child("chat").child(userID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                if (!dataSnapshot.hasChild(user)) {
                    //updating value on firebase
                    Map chatHashMap = new HashMap();
                    chatHashMap.put("seen", false);
                    chatHashMap.put("timestamp", ServerValue.TIMESTAMP);

                    Map mainChatMap = new HashMap();
                    mainChatMap.put("chat/" + userID + "/" + user, chatHashMap);
                    mainChatMap.put("chat/" + user + "/" + userID, chatHashMap);

                    //retrieves response from firebase after updating
                    databaseReference.updateChildren(mainChatMap, new DatabaseReference.CompletionListener() {
                        @Override
                        public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {

                            if (databaseError != null) {
                                Log.d(MessagingPage.class.getSimpleName(), databaseError.getMessage());
                            }
                        }
                    });
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        //sends message to user
        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendMessage();
            }
        });

    }

    private void loadAllMessages() {
        DatabaseReference messageRef = databaseReference.child("messages").child(userID).child(user);
        Query messageQuery = messageRef.limitToLast(currentPage * TOTAL_ITEMS_TO_LOAD);

        messageQuery.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {

                Messages message = dataSnapshot.getValue(Messages.class);

                itemPos++;

                if (itemPos == 1) {

                    String messageKey = dataSnapshot.getKey();

                    lastKey = messageKey;
                    previousKey = messageKey;

                }

                messagesList.add(message);
                messagesAdapter.notifyDataSetChanged();

                messageList.scrollToPosition(messagesList.size() - 1);


            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    //sends message to user
    private void sendMessage() {

        String userMessage = messageInput.getText().toString();

        if (!TextUtils.isEmpty(userMessage)) {

            String currentUserRef = "messages/" + userID + "/" + user;
            String chatUserRef = "messages/" + user + "/" + userID;

            DatabaseReference user_message_push = databaseReference.child("messages")
                    .child(userID).child(user).push();

            String push_id = user_message_push.getKey();

            //creating a new row entry in firebase database
            Map messageHashMap = new HashMap();
            messageHashMap.put("message", userMessage);
            messageHashMap.put("seen", false);
            messageHashMap.put("type", "text");
            messageHashMap.put("time", ServerValue.TIMESTAMP);
            messageHashMap.put("from", userID);

            Map mainMessageMap = new HashMap();
            mainMessageMap.put(currentUserRef + "/" + push_id, messageHashMap);
            mainMessageMap.put(chatUserRef + "/" + push_id, messageHashMap);

            messageInput.setText("");

            databaseReference.child("chat").child(userID).child(user).child("seen").setValue(true);
            databaseReference.child("chat").child(userID).child(user).child("timestamp").setValue(ServerValue.TIMESTAMP);

            databaseReference.child("chat").child(user).child(userID).child("seen").setValue(false);
            databaseReference.child("chat").child(user).child(userID).child("timestamp").setValue(ServerValue.TIMESTAMP);

            databaseReference.updateChildren(mainMessageMap, new DatabaseReference.CompletionListener() {
                @Override
                public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                    if (databaseError != null) {
                        Log.d(MessagingPage.class.getSimpleName(), databaseError.getMessage());
                    }
                }
            });
        }
    }
}
