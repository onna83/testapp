package com.vision.x;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Arrays;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.os.Vibrator;

import com.stfalcon.frescoimageviewer.ImageViewer;

import static androidx.core.content.ContextCompat.getSystemService;

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
        holder._Layout.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                String text=messages.get(position).getText().toString();
                String Morse_Code=TranslateText(text);
                int len=text.length();

                long[] pattern = new long[1000];
                for (int i = 0; i < 1000; i++) {pattern[i]=0;}
                /*Pattern*/

                char[] MorseCodeButCharArray=Morse_Code.toCharArray();
                int j=1;
                for (int i = 0; i < len; i++) {
                    if (MorseCodeButCharArray[i]=='.'){
                        pattern[j]=200;
                        if(MorseCodeButCharArray[i+1]!=' '&& i<len ){
                            j++;
                            pattern[j]=100;
                        }
                    }
                    else if(MorseCodeButCharArray[i]=='-'){
                        pattern[j]=350;
                        if(MorseCodeButCharArray[i+1]!=' '&& i<len ){
                            j++;
                            pattern[j]=100;
                        }
                    }
                    else if(MorseCodeButCharArray[i]==' '){
                        pattern[j]=275;
                    }

                }
                //Toast.makeText(v.getContext(), String.valueOf(len), Toast.LENGTH_SHORT).show();
                //long[] pattern=new long[text.length()];
                Vibrator V= (Vibrator) v.getContext().getSystemService(Context.VIBRATOR_SERVICE);
                V.vibrate(pattern, -1);
                return false;
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

    @Override
    public int getItemCount() {
        return messages.size();
    }


    public class  ChatViewHolder extends RecyclerView.ViewHolder{
        TextView _Message,_Sender;
        Button viewMedia;
        //Button _Translate;
        public LinearLayout _Layout;
        public ChatViewHolder(View view){
            super(view);
            _Message=view.findViewById(R.id.message);
            _Sender=view.findViewById(R.id.sender);
            _Layout = view.findViewById(R.id.messageLayout);
            viewMedia=view.findViewById(R.id.viewMedia);
           // _Translate=view.findViewById(R.id.translate);
        }
    }
}
