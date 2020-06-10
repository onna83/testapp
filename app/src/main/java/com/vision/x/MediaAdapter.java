package com.vision.x;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

public class MediaAdapter extends RecyclerView.Adapter<MediaAdapter.Media_View_Holder> {
    ArrayList<String> mediaList;
    Context context;
    public MediaAdapter(Context context, ArrayList<String> mediaList){
        this.context=context;
        this.mediaList=mediaList;
    }

    @NonNull
    @Override
    public Media_View_Holder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View layoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.media,null,false);
        Media_View_Holder mediaViewHolder=new Media_View_Holder(layoutView);
        return mediaViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull Media_View_Holder holder, int position) {
        Glide.with(context).load(Uri.parse(mediaList.get(position))).into(holder.Media);
    }

    @Override
    public int getItemCount() {
        return mediaList.size();
    }

    public class Media_View_Holder extends RecyclerView.ViewHolder{

        ImageView Media;
        public Media_View_Holder(@NonNull View itemView) {
            super(itemView);
            Media=itemView.findViewById(R.id.media);
        }
    }
}
