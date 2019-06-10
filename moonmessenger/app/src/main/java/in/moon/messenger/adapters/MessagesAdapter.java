package in.moon.messenger.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import in.moon.messenger.R;
import in.moon.messenger.utils.TimeStamp;
import in.moon.messenger.utils.Messages;

public class MessagesAdapter extends RecyclerView.Adapter<MessagesAdapter.MessageViewHolder> {

    private final Context context;
    private List<Messages> msgList;
    private DatabaseReference userDatabase;

    public MessagesAdapter(Context context, List<Messages> msgList) {
        this.context = context;
        this.msgList = msgList;
    }

    @Override
    public MessageViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.message_single_layout, parent, false);

        return new MessageViewHolder(v);

    }

    public class MessageViewHolder extends RecyclerView.ViewHolder {

        public TextView message, time;
        public CircleImageView userPic;
        public TextView username;

        public MessageViewHolder(View view) {
            super(view);
            //intialises the layout views
            message = view.findViewById(R.id.messageText);
            userPic = view.findViewById(R.id.profilePic);
            username = view.findViewById(R.id.userName);
            time = view.findViewById(R.id.time);

        }
    }

    @Override
    public void onBindViewHolder(final MessageViewHolder viewHolder, int i) {

        Messages messages = msgList.get(i);

        String fromUser = messages.getFrom();
        String messageType = messages.getType();

        //initialising users from firebase
        userDatabase = FirebaseDatabase.getInstance().getReference().child("users").child(fromUser);

        //getting username from ID
        userDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                //get name
                String name = dataSnapshot.child("name").getValue().toString();
                viewHolder.username.setText(name);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        viewHolder.message.setText(messages.getMessage());
        TimeStamp timeStamp = new TimeStamp();
        String ago = timeStamp.getTimeAgo(messages.getTime(), context);
        viewHolder.time.setText(ago);
    }

    @Override
    public int getItemCount() {
        return msgList.size();
    }

}
