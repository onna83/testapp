package com.vision.x;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class MainHomeScreenActivity extends AppCompatActivity {
    private RecyclerView Chat_List;  //All chats appear here using this recycler view
    private RecyclerView.Adapter Chat_List_Adapter;
    private RecyclerView.LayoutManager ChatListLayoutManager;

    ArrayList<ChatObj> chatList = new ArrayList<ChatObj>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_home_screen);
        Fresco.initialize(this);
        Button Logout =findViewById(R.id.logout);
        final Button FindUser =findViewById(R.id.findUser);

        //Go to userList Activity
        FindUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), FindUserActivity.class));
            }
        });

        //Logout button which logs out of the application to the login screen
        Logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance().signOut();
                Intent intent = new Intent(getApplicationContext(), LoginActivity.class);//change activities
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP); //Kill Activity on top, return to previous activity
                startActivity(intent);
                finish();//remove from starting again
                return;
            }
        });

        getContactsPermissions();
        StartRecyclerView(); //Starts recycler view to populate chat list
        getChatList();

    }


    //Gets perms from users to view contacts
    private void getContactsPermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestPermissions(new String[]{Manifest.permission.WRITE_CONTACTS, Manifest.permission.READ_CONTACTS}, 1);
        }
    }
    private void StartRecyclerView() {
        Chat_List = findViewById(R.id.chats);  //chatlist id from the layout
        Chat_List.setNestedScrollingEnabled(false); //self explanatory
        Chat_List.setHasFixedSize(false);   //self explanatory
        ChatListLayoutManager = new LinearLayoutManager(getApplicationContext(), RecyclerView.VERTICAL , false);
        Chat_List.setLayoutManager(ChatListLayoutManager); //set the layout of the contents in recycler view
        Chat_List_Adapter = new ChatListAdapter(chatList);
        Chat_List.setAdapter(Chat_List_Adapter);
    }

    private void getChatList(){
        DatabaseReference chatDB = FirebaseDatabase.getInstance().getReference().child("user").child(FirebaseAuth.getInstance().getUid()).child("chat");
        chatDB.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    for (DataSnapshot childSnapshot : dataSnapshot.getChildren()){
                        ChatObj chat = new ChatObj(childSnapshot.getKey());
                        boolean flag=false;
                        for(ChatObj i : chatList){
                            if(i.getChatID().equals(chat.getChatID())){
                                flag=true;
                            }
                            else if(flag){
                                continue;
                            }
                        }
                        chatList.add(chat);
                        Chat_List_Adapter.notifyDataSetChanged();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

}

