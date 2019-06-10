package in.moon.messenger.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import in.moon.messenger.R;
import in.moon.messenger.activities.MessagingPage;
import in.moon.messenger.utils.UserTiming;

public class MessagesFragment extends Fragment {

    private RecyclerView recyclerView;
    private DatabaseReference chatsDatabase;
    private DatabaseReference messagesDatabase;
    private DatabaseReference usersDatabase;
    private FirebaseAuth auth;
    private String currentUserID;
    private View mainView;

    public MessagesFragment() {
        //empty constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        mainView = inflater.inflate(R.layout.messages_fragment, container, false);

        recyclerView = (RecyclerView) mainView.findViewById(R.id.conv_list);
        auth = FirebaseAuth.getInstance();

        currentUserID = auth.getCurrentUser().getUid();

        chatsDatabase = FirebaseDatabase.getInstance().getReference().child("chat").child(currentUserID);

        chatsDatabase.keepSynced(true);
        usersDatabase = FirebaseDatabase.getInstance().getReference().child("users");
        messagesDatabase = FirebaseDatabase.getInstance().getReference().child("messages").child(currentUserID);
        usersDatabase.keepSynced(true);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(linearLayoutManager);

        //inflate the layout
        return mainView;
    }


    @Override
    public void onStart() {
        super.onStart();

        //retrieves list of chats from database
        Query chatQuery = chatsDatabase.orderByChild("timestamp");

        FirebaseRecyclerAdapter<UserTiming, ConversationViewHolder> firebaseConvAdapter = new FirebaseRecyclerAdapter<UserTiming, ConversationViewHolder>(
                UserTiming.class,
                R.layout.users_layout,
                ConversationViewHolder.class,
                chatQuery
        ) {
            @Override
            protected void populateViewHolder(final ConversationViewHolder convViewHolder, final UserTiming conversation, int i) {

                final String listUserID = getRef(i).getKey();

                Query lastMessageQuery = messagesDatabase.child(listUserID).limitToLast(1);
                lastMessageQuery.addChildEventListener(new ChildEventListener() {
                    @Override
                    public void onChildAdded(DataSnapshot dataSnapshot, String s) {

                        String data = dataSnapshot.child("message").getValue().toString();

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

                //retrieves user data
                usersDatabase.child(listUserID).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        final String userName = dataSnapshot.child("name").getValue().toString();

                        convViewHolder.usernameView.setText(userName);

                        convViewHolder.view.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {

                                //starts messaging activity with this intent
                                Intent chatIntent = new Intent(getContext(), MessagingPage.class);
                                chatIntent.putExtra("user_id", listUserID);
                                chatIntent.putExtra("user_name", userName);
                                startActivity(chatIntent);

                            }
                        });

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

            }
        };

        //sets adapter
        recyclerView.setAdapter(firebaseConvAdapter);
    }

    public static class ConversationViewHolder extends RecyclerView.ViewHolder {
        View view;
        TextView usernameView;

        public ConversationViewHolder(View itemView) {
            super(itemView);
            //initialize views
            view = itemView;
            usernameView = view.findViewById(R.id.user_single_name);
        }
    }
}
