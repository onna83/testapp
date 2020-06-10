package com.vision.x;

import java.util.ArrayList;

public class MessageObj {
    String msgID,text,senderID;
    ArrayList<String> mediaURL_list;
    public MessageObj(String msgID, String senderID,String text,ArrayList mediaURL_list){
        this.msgID=msgID;
        this.senderID=senderID;
        this.text=text;
        this.mediaURL_list=mediaURL_list;
    }

    public String getMsgID() {
        return msgID;
    }

    public String getSenderID() {
        return senderID;
    }

    public String getText() {
        return text;
    }

    public ArrayList<String> getMediaURL_list() {
        return mediaURL_list;
    }
}
