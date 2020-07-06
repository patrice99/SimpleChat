package com.example.simplechat;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.parse.LogInCallback;
import com.parse.Parse;
import com.parse.ParseAnonymousUtils;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseUser;
import com.parse.SaveCallback;

public class ChatActivity extends AppCompatActivity {
    static final String TAG = ChatActivity.class.getSimpleName();
    static final String USER_ID_KEY = "userId";
    static final String BODY_KEY = "body";

    EditText etMessage;
    Button btSend;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        //User login
        if (ParseUser.getCurrentUser() != null) { //Either start with an existing user
            startWithCurrentUser();
        } else { //if not login with an anonymous user 
            login();
        }
    }


    // get the user ID from the cached currentUser object
    void startWithCurrentUser() {
        setupMessagePosting();

    }

    void setupMessagePosting() {
        //fnd the text field and button
        etMessage = findViewById(R.id.etMessage);
        btSend = findViewById(R.id.btSend);

        //When send button is clicked create message object on Parse
        btSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String data = etMessage.getText().toString();

                //Using new 'Message' Parsed-back model now
                Message message = new Message();
                message.setBody(data);
                message.setUserId(ParseUser.getCurrentUser().getObjectId());
                message.saveInBackground(new SaveCallback() {
                    @Override
                    public void done(ParseException e) {
                        if (e == null) { //no error
                            Toast.makeText(ChatActivity.this, "Successfully created message on Parse", Toast.LENGTH_SHORT).show();
                        } else {
                            Log.e(TAG, "Failed to show message", e);
                        }
                    }
                });
                etMessage.setText(null);
            }
        });

    }

    void login(){
        ParseAnonymousUtils.logIn(new LogInCallback() {
            @Override
            public void done(ParseUser user, ParseException e) {
                if (e != null) {
                    Log.e(TAG, "Anonymous login failed: " + e);
                } else {
                    startWithCurrentUser();
                }
            }
        });

    }
    
    
}