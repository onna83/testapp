package com.vision.x;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.stfalcon.frescoimageviewer.ImageViewer;

public class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.ChatViewHolder> {

    ArrayList<MessageObj> messages=new ArrayList<>();

    public ChatAdapter(ArrayList<MessageObj> messages){
        this.messages = messages;
    }

    @NonNull
    @Override
    public ChatViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View layoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.activity_message,null , false);
        RecyclerView.LayoutParams lp = new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutView.setLayoutParams(lp);
        ChatViewHolder recyclerView = new ChatViewHolder(layoutView);
        return recyclerView;
    }

    @Override
    public void onBindViewHolder(@NonNull final ChatAdapter.ChatViewHolder holder, final int position) {
        holder._Message.setText(messages.get(position).getText());
        holder._Sender.setText(messages.get(position).getSenderID());
        if(messages.get(holder.getAdapterPosition()).getMediaURL_list().isEmpty()){
            holder.viewMedia.setVisibility(View.GONE);
        }
        holder.viewMedia.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new ImageViewer.Builder(v.getContext(), messages.get(holder.getAdapterPosition()).getMediaURL_list())
                        .setStartPosition(0)
                        .show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return messages.size();
    }


    public class  ChatViewHolder extends RecyclerView.ViewHolder{
        TextView _Message,_Sender;
        Button viewMedia;
        public LinearLayout _Layout;
        public ChatViewHolder(View view){
            super(view);
            _Message=view.findViewById(R.id.message);
            _Sender=view.findViewById(R.id.sender);
            _Layout = view.findViewById(R.id.layout);
            viewMedia=view.findViewById(R.id.viewMedia);
        }
    }
}
