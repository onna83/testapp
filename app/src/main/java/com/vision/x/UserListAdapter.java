package com.vision.x;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

public class UserListAdapter extends RecyclerView.Adapter<UserListAdapter.UserListViewHolder> {

    ArrayList<UserObj> userList;

    public UserListAdapter(ArrayList<UserObj> userList){
        this.userList = userList;
    }

    @NonNull
    @Override
    public UserListAdapter.UserListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View layoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_user,null , false);
        RecyclerView.LayoutParams lp = new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutView.setLayoutParams(lp);
        UserListViewHolder recyclerView = new UserListViewHolder(layoutView);
        return recyclerView;
    }

    @Override
    public void onBindViewHolder(@NonNull UserListAdapter.UserListViewHolder holder, final int position) {
        holder._Name.setText(userList.get(position).getName());
        holder._Phone.setText(userList.get(position).getPhone());
        holder._Layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String chatID = FirebaseDatabase.getInstance().getReference().child("chat").push().getKey();
                FirebaseDatabase.getInstance().getReference().child("user").child(FirebaseAuth.getInstance().getUid()).child("chat").child(chatID).setValue(true);
                FirebaseDatabase.getInstance().getReference().child("user").child(userList.get(position).getUserID()).child("chat").child(chatID).setValue(true);
                Intent intent =new Intent(v.getContext(), MainHomeScreenActivity.class);
                v.getContext().startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return userList.size();
    }


    public class  UserListViewHolder extends RecyclerView.ViewHolder{
        public TextView _Name, _Phone;
        public LinearLayout _Layout;
        public UserListViewHolder(View view){
            super(view);
            _Name =  view.findViewById(R.id.name);
            _Phone =  view.findViewById(R.id.phone);
            _Layout = view.findViewById(R.id.layout);
        }
    }
}
