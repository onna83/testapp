package com.vision.x;

import android.database.Cursor;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.telephony.TelephonyManager;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.vision.x.Extras.CountryCodePrefix;

import java.util.ArrayList;

public class FindUserActivity extends AppCompatActivity {
    private RecyclerView User_List;
    private RecyclerView.Adapter User_List_Adapter;
    private RecyclerView.LayoutManager UserListLayoutManager;

    ArrayList<UserObj> userList, contactList;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_find_user);

        contactList = new ArrayList<>();
        userList = new ArrayList<>();
        StartRecyclerView();
        getContactList();
    }



    private void getContactList(){
        String CountryPrefix= getCountryCode();
        Cursor contacts =  getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,null,null,null,null);
        while(contacts.moveToNext()){
            String Name = contacts.getString(contacts.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
            String Phone = contacts.getString(contacts.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
            Phone = Phone.replace(" ", "");
            Phone = Phone.replace("-", "");
            Phone = Phone.replace("(", "");
            Phone = Phone.replace(")", "");
            if(!String.valueOf(Phone.charAt(0)).equals("+")){
                Phone=CountryPrefix+Phone;
            }
            UserObj Contacts = new UserObj(Name,Phone,"");
            contactList.add(Contacts);
            ShowUserInfo(Contacts);
        }
    }

    private void ShowUserInfo(final UserObj contacts) {
        DatabaseReference userDB = FirebaseDatabase.getInstance().getReference().child("user");
        Query query = userDB.orderByChild("phone").equalTo(contacts.getPhone());
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()){
                    String phone = "";
                    String name = "";

                    for(DataSnapshot childSnapshots : dataSnapshot.getChildren()){
                        if(childSnapshots.child("name").getValue()!=null)
                            name = childSnapshots.child("name").getValue().toString();

                        if(childSnapshots.child("phone").getValue()!=null)
                            phone = childSnapshots.child("phone").getValue().toString();

                        UserObj User = new UserObj(name,phone,childSnapshots.getKey());
                        if(name.equals(phone)){
                            for(UserObj i: contactList){
                                if(i.getPhone().equals(User.getPhone())){
                                    User.setName(i.getName());
                                }
                            }
                        }

                        userList.add(User);
                        User_List_Adapter.notifyDataSetChanged();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private String getCountryCode(){
        String CC = null ;
        TelephonyManager telephonyManager = (TelephonyManager) getApplicationContext().getSystemService(getApplicationContext().TELEPHONY_SERVICE);
        if(telephonyManager.getNetworkCountryIso()!=null){
            if(!telephonyManager.getNetworkCountryIso().toString().equals("")){
                CC = telephonyManager.getNetworkCountryIso().toString();
            }
        }
        return CountryCodePrefix.getPhone(CC);
    }

    private void StartRecyclerView() {
        User_List = findViewById(R.id.userList);
        User_List.setNestedScrollingEnabled(false);
        User_List.setHasFixedSize(false);
        UserListLayoutManager = new LinearLayoutManager(getApplicationContext(), RecyclerView.VERTICAL , false);
        User_List.setLayoutManager(UserListLayoutManager);
        User_List_Adapter = new UserListAdapter(userList);
        User_List.setAdapter(User_List_Adapter);
    }
}
