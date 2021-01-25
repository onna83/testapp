package com.vision.x;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class LoginActivity extends AppCompatActivity {
    private EditText pNumber, code, pName;
    String verification_id;
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks Callback;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        FirebaseApp.initializeApp(this);
        CheckLogIn();   //Check if user already logged in
        pNumber = findViewById(R.id.PhoneNumber);
        pName = findViewById(R.id.Name);
        code = findViewById(R.id.Code);
        final Button verify = findViewById(R.id.Verify);
        verify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(verification_id!=null){
                    VerifyWithCode();
                }
                else {
                    startPhoneNumberVerification();
                }
            }
        });

        Callback = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            @Override
            public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {
                LogInWithPhoneCredentials(phoneAuthCredential);
            }

            @Override
            public void onVerificationFailed(@NonNull FirebaseException e) { }

            //Saving received code
            @Override
            public void onCodeSent(@NonNull String verificationId, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                super.onCodeSent(verificationId, forceResendingToken);
                verification_id = verificationId;
                verify.setText("Verify");
            }
        };
    }




    private void LogInWithPhoneCredentials(PhoneAuthCredential phoneAuthCredential) {
        FirebaseAuth.getInstance().signInWithCredential(phoneAuthCredential).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                    if(user !=null){
                        final DatabaseReference UserDB= FirebaseDatabase.getInstance().getReference().child("user").child(user.getUid()); //get current user uid from DB
                        UserDB.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                if(!dataSnapshot.exists()){
                                    Map<String, Object> usrMap = new HashMap<>(); //saving user info against userId in database
                                    usrMap.put("name",pName.getText().toString());
                                    usrMap.put("phone",user.getPhoneNumber());
                                    UserDB.updateChildren(usrMap);//update database info under user tree
                                }
                                CheckLogIn(); //Check if user loggedIn
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });
                    }
                }
            }
        });
    }

    //Check if user already logged in
    private void CheckLogIn() {
        FirebaseUser user=FirebaseAuth.getInstance().getCurrentUser();
        if(user != null){
            startActivity(new Intent(getApplicationContext(), MainHomeScreenActivity.class)); //Go to home screen activity
            finish();
        }
    }


    private void VerifyWithCode(){
        PhoneAuthCredential credential= PhoneAuthProvider.getCredential(verification_id, code.getText().toString());
        LogInWithPhoneCredentials(credential);
    }


    private void startPhoneNumberVerification() {
        PhoneAuthProvider.getInstance().verifyPhoneNumber(pNumber.getText().toString(), 60, TimeUnit.SECONDS, this, Callback);
    }
}
