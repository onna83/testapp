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

public class ChatListAdapter extends RecyclerView.Adapter<ChatListAdapter.ChatListViewHolder> {

    ArrayList<ChatObj> ChatList;
    public ChatListAdapter(ArrayList<ChatObj> ChatList){
        this.ChatList = ChatList;
    }

    @NonNull
    @Override
    public ChatListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View layoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.chat,null , false);
        RecyclerView.LayoutParams lp = new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutView.setLayoutParams(lp);
        ChatListViewHolder recyclerView = new ChatListViewHolder(layoutView);
        return recyclerView;
    }

    //Populate recycler view as its scrolled with more items
    @Override
    public void onBindViewHolder(@NonNull final ChatListViewHolder holder, final int position) {
        holder._Title.setText(ChatList.get(position).getChatID());
        holder._Layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent =new Intent(v.getContext(), ChatActivity.class);
                Bundle bundle = new Bundle();
                bundle.putString("chatID",ChatList.get(holder.getAdapterPosition()).getChatID());
                intent.putExtras(bundle);
                v.getContext().startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return ChatList.size();
    }


    public class  ChatListViewHolder extends RecyclerView.ViewHolder{
        public TextView _Title;
        public LinearLayout _Layout;
        public ChatListViewHolder(View view){
            super(view);
            _Title =  view.findViewById(R.id.title);
            _Layout = view.findViewById(R.id.layout);
        }
    }
}
