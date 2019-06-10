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
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import de.hdodenhof.circleimageview.CircleImageView;
import in.moon.messenger.R;
import in.moon.messenger.activities.UserProfile;
import in.moon.messenger.utils.Users;


/**
 * A simple {@link Fragment} subclass.
 */
public class UsersFragment extends Fragment {

    private RecyclerView userList;
    private DatabaseReference databaseReference;
    private FirebaseAuth auth;
    private String currentUserID;
    private View mainView;

    public UsersFragment() {
        //empty constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        mainView = inflater.inflate(R.layout.users_fragment, container, false);

        userList = (RecyclerView) mainView.findViewById(R.id.friends_list);
        auth = FirebaseAuth.getInstance();

        currentUserID = auth.getCurrentUser().getUid();

        databaseReference = FirebaseDatabase.getInstance().getReference().child("users");
        databaseReference.keepSynced(true);


        userList.setHasFixedSize(true);
        userList.setLayoutManager(new LinearLayoutManager(getContext()));

        //inflates the layout
        return mainView;
    }


    @Override
    public void onStart() {
        super.onStart();

        //retrieves the users list
        final FirebaseRecyclerAdapter<Users, UserViewHolder> firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<Users, UserViewHolder>(
                Users.class,
                R.layout.users_layout,
                UserViewHolder.class,
                databaseReference

        ) {
            @Override
            protected void populateViewHolder(UserViewHolder userViewHolder, final Users users, int position) {

                //sets data on recyclerview item
                userViewHolder.usernameView.setText(users.getName());

                final String userID = getRef(position).getKey();

                userViewHolder.view.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        //start another activity on recycler item click
                        Intent profileIntent = new Intent(getActivity(), UserProfile.class);
                        profileIntent.putExtra("user_id", userID);
                        profileIntent.putExtra("user_name", users.getName());
                        startActivity(profileIntent);

                    }
                });

            }
        };


        userList.setAdapter(firebaseRecyclerAdapter);

    }

    //creates view holder
    public static class UserViewHolder extends RecyclerView.ViewHolder {

        View view;
        TextView usernameView;
        CircleImageView userImageView;

        public UserViewHolder(View itemView) {
            super(itemView);

            view = itemView;
            usernameView = (TextView) view.findViewById(R.id.user_single_name);
            userImageView = (CircleImageView) view.findViewById(R.id.user_single_image);
        }
    }
}
