package com.vision.x;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RadioButton;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ChatActivity extends AppCompatActivity {
    private RecyclerView Chat,Media;
    private RecyclerView.Adapter ChatAdapter,MediaAdapter;
    private RecyclerView.LayoutManager ChatLayoutManager,MediaLayoutManager;
    int imgIntent=1;
    ArrayList<MessageObj> messages;
    ArrayList<String> mediaURIList=new ArrayList<>();
    ArrayList<String> mediaIDs=new ArrayList<>();
    int mediaIterator =0;
    EditText Message;
    String chatID;
    CheckBox Translate;
    DatabaseReference DBmessage;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        chatID= getIntent().getExtras().getString("chatID");
        Button addMedia =findViewById(R.id.addMedia);
        Button Send = findViewById(R.id.send);
        Translate= findViewById(R.id.translate);

        DBmessage=  FirebaseDatabase.getInstance().getReference().child("chat").child(chatID);
        Send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SEND_MESSAGE();
            }
        });
        addMedia.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GALLERY();
            }
        });
        StartMessageRecyclerView();
        StartMediaRecyclerView();
        getTextDB();
    }

    public boolean flag=true;





    private void GALLERY() {
        Intent intent =new Intent();
        intent.setType("image/*");
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE,true);
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent,"Select Media"), imgIntent);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode==RESULT_OK){
            if(requestCode == imgIntent) {
                if (data.getClipData() == null) {
                    mediaURIList.add(data.getData().toString());
                } else {
                    for (int i = 0; i < data.getClipData().getItemCount(); i++) {
                        mediaURIList.add(data.getClipData().getItemAt(i).getUri().toString());
                    }
                }
                MediaAdapter.notifyDataSetChanged();
            }
        }
    }

    private void getTextDB() {
        DBmessage.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                if(dataSnapshot.exists()){
                    String text = "";
                    String morseTemp = "";
                    String Morse = "";
                    String CreatorID="";

                    ArrayList<String> mediaURL_list=new ArrayList<>();
                    if(dataSnapshot.child("text").getValue() !=null){
                        //text= dataSnapshot.child("text").getValue().toString();

                        if(flag){
                            morseTemp=dataSnapshot.child("text").getValue().toString();
                            Morse = TranslateText(morseTemp);
                            text=Morse;
                        }
                        else{
                            text= dataSnapshot.child("text").getValue().toString();
                        }
                    }
                    if(dataSnapshot.child("creator").getValue() !=null){
                        CreatorID= dataSnapshot.child("creator").getValue().toString();
                    }
                    if(dataSnapshot.child("media").getChildrenCount()>0){
                        for(DataSnapshot mediaSnapshot: dataSnapshot.child("media").getChildren()){
                            mediaURL_list.add(mediaSnapshot.getValue().toString());
                        }
                    }
                    MessageObj _Message = new MessageObj(dataSnapshot.getKey(),CreatorID,text,mediaURL_list);
                    messages.add(_Message);
                    ChatLayoutManager.scrollToPosition(messages.size()-1);
                    ChatAdapter.notifyDataSetChanged();

                }
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private String TranslateText(String morseTemp) {
        String Morse=morseTemp;
        char[] englishText = { 'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l',
                'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x',
                'y', 'z', '1', '2', '3', '4', '5', '6', '7', '8', '9', '0',
                ',', '.', '?' };

        String[] morseCode = { ".-", "-...", "-.-.", "-..", ".", "..-.", "--.", "....", "..",
                ".---", "-.-", ".-..", "--", "-.", "---", ".---.", "--.-", ".-.",
                "...", "-", "..-", "...-", ".--", "-..-", "-.--", "--..", ".----",
                "..---", "...--", "....-", ".....", "-....", "--...", "---..", "----.",
                "-----", "--..--", ".-.-.-", "..--.." };
        Morse=Morse.toLowerCase();
        char[] msgText = Morse.toCharArray();
        String msgText_in_morse = " ";
        for (int i = 0; i < msgText.length; i++){
            for (int j = 0; j < englishText.length; j++){

                if (msgText[i]==englishText[j]){
                    msgText_in_morse = msgText_in_morse + morseCode[j] + " ";
                }
            }
        }

        return msgText_in_morse;
    }

    private void SEND_MESSAGE() {

        Message = findViewById(R.id.messageL);
        String msgID=DBmessage.push().getKey();
        final DatabaseReference newMessage = DBmessage.child(msgID);
        final Map newMessageMap = new HashMap();
        newMessageMap.put("creator", FirebaseAuth.getInstance().getUid());
        if(!Message.getText().toString().isEmpty()) {
            newMessageMap.put("text", Message.getText().toString());
        }
            if(!mediaURIList.isEmpty()){
                for(String mURI:mediaURIList){
                    String mediaID=newMessage.child("media").push().getKey();
                    mediaIDs.add(mediaID);
                    final StorageReference filePath =FirebaseStorage.getInstance().getReference().child("chat").child(chatID).child(msgID).child(mediaID);
                    UploadTask uploadTask = filePath.putFile(Uri.parse(mURI));
                    uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            filePath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    newMessageMap.put("/media/"+ mediaIDs.get(mediaIterator)+"/",uri.toString());
                                    mediaIterator++;
                                    if(mediaIterator==mediaURIList.size()){
                                        newMessage.updateChildren(newMessageMap);
                                        Message.setText(null);
                                        mediaURIList.clear();
                                        mediaIDs.clear();
                                        MediaAdapter.notifyDataSetChanged();
                                    }
                                }
                            });
                        }
                    });
                }
            }
            else {
                if(!Message.getText().toString().isEmpty()){
                    newMessage.updateChildren(newMessageMap);
                    Message.setText(null);
                    mediaURIList.clear();
                    mediaIDs.clear();
                    MediaAdapter.notifyDataSetChanged();
                }
            }
        }




    private void StartMessageRecyclerView() {
        messages= new ArrayList<>();
        Chat= findViewById(R.id.messages);
        Chat.setNestedScrollingEnabled(false);
        Chat.setHasFixedSize(false);

        ChatLayoutManager = new LinearLayoutManager(getApplicationContext(), RecyclerView.VERTICAL , false);
        Chat.setLayoutManager(ChatLayoutManager);
        ChatAdapter = new ChatAdapter(messages);
        Chat.setAdapter(ChatAdapter);
    }

    private void StartMediaRecyclerView() {
        mediaURIList= new ArrayList<>();
        Media= findViewById(R.id.mediaList);
        Media.setNestedScrollingEnabled(false);
        Media.setHasFixedSize(false);

        MediaLayoutManager = new LinearLayoutManager(getApplicationContext(), RecyclerView.HORIZONTAL , false);
        Media.setLayoutManager(MediaLayoutManager);
        MediaAdapter = new MediaAdapter(getApplicationContext(),mediaURIList);
        Media.setAdapter(MediaAdapter);
    }

}
