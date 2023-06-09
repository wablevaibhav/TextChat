package com.example.textchatapp.Fragments;


import android.annotation.SuppressLint;
import android.content.ContentResolver;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.textchatapp.Adapter.UserAdapter;
import com.example.textchatapp.Model.User;
import com.example.textchatapp.Model.UserObject;
import com.example.textchatapp.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;


public class UsersFragment extends Fragment {

    private RecyclerView recyclerView;

    private UserAdapter userAdapter;
    private List<User> mUsers;
    Cursor cursor;
    String phone;


    ArrayList<UserObject> userList, contactList;

    @SuppressLint("MissingInflatedId")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_users, container, false);

        recyclerView = view.findViewById(R.id.RVUsers);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        mUsers = new ArrayList<>();

        contactList = new ArrayList<>();
        userList = new ArrayList<>();

        readUsers();

        return view;
    }


    @SuppressLint("Range")
    public void readUsers() {

        ContentResolver cr = requireActivity().getContentResolver();

        cursor = cr
                .query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, null, null, null);

        while (cursor.moveToNext()) {
            @SuppressLint("Range")
            String name = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));

            phone = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));


            if (phone.length() <= 10) {
                continue;
            }

            if (!String.valueOf(phone.charAt(0)).equals("+")) {
                phone = "+88" + phone;
            }
            phone = phone.replace(" ", "");
            phone = phone.replace("-", "");
            phone = phone.replace("(", "");
            phone = phone.replace(")", "");

            final UserObject mContact = new UserObject("", name, phone);
            contactList.add(mContact);


            final FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
            final DatabaseReference mUserDB = FirebaseDatabase.getInstance().getReference().child("Users");
            Query query = mUserDB.orderByChild("phone").equalTo(mContact.getPhone());
            query.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                    if (dataSnapshot.exists()) {
                        mUsers.clear();
                        for (DataSnapshot childSnapshot : dataSnapshot.getChildren()) {
                            User user = childSnapshot.getValue(User.class);
                            if(!Objects.requireNonNull(user).getId().equals(Objects.requireNonNull(firebaseUser).getUid())) {
                                mUsers.add(user);
                            }

                        }
                        userAdapter = new UserAdapter(getContext(), mUsers, false);
                        recyclerView.setAdapter(userAdapter);


                    }
                }


                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });

        }

        cursor.close();
    }



}