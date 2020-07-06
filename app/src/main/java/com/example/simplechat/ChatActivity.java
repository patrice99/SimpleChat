package com.example.simplechat;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.LogInCallback;
import com.parse.Parse;
import com.parse.ParseAnonymousUtils;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.util.ArrayList;
import java.util.List;

public class ChatActivity extends AppCompatActivity {
    static final String TAG = ChatActivity.class.getSimpleName();
    static final String USER_ID_KEY = "userId";
    static final String BODY_KEY = "body";
    static final int MAX_CHAT_MESSAGES_TO_SHOW = 50;

    EditText etMessage;
    Button btSend;

    RecyclerView rvChat;
    ArrayList<Message> mMessages;
    ChatAdapter mAdapter;
    //Keep track of the initial load to scroll to the bottom of ListView
    boolean mFirstLoad;

    // Create a handler which can run code periodically . This is primitive polling
    static final int POLL_INTERVAL = 1000; // milliseconds
    Handler myHandler = new android.os.Handler();
    Runnable mRefreshMessagesRunnable = new Runnable() {
        @Override
        public void run() {
            refreshMessages();
            myHandler.postDelayed(this, POLL_INTERVAL);
        }
    };


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

        myHandler.postDelayed(mRefreshMessagesRunnable, POLL_INTERVAL);
    }


    // get the user ID from the cached currentUser object
    void startWithCurrentUser() {
        setupMessagePosting();

    }

    //Setup message field and posting
    void setupMessagePosting() {
        //fnd the text field and button
        etMessage = (EditText) findViewById(R.id.etMessage);
        btSend = (Button) findViewById(R.id.btSend);
        rvChat = (RecyclerView) findViewById(R.id.rvChat);
        mMessages = new ArrayList<>();
        mFirstLoad = true;
        final String userId = ParseUser.getCurrentUser().getObjectId();
        mAdapter = new ChatAdapter(ChatActivity.this, userId, mMessages);
        rvChat.setAdapter(mAdapter);

        //associate the layout manager with the Recycler View
        final LinearLayoutManager linearLayoutManager = new LinearLayoutManager(ChatActivity.this);
        linearLayoutManager.setReverseLayout(true);
        rvChat.setLayoutManager(linearLayoutManager);

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

    //Query messages from Parse so we can load them into the chat adapter
    void refreshMessages(){
        //Construct query to execute
        ParseQuery<Message> query = ParseQuery.getQuery(Message.class);

        //Configure limit and sort order
        query.setLimit(MAX_CHAT_MESSAGES_TO_SHOW);

        //get the latest 50 messages, order will show up newest to oldest of this group
        query.orderByDescending("createdAt");

        //Execute query to fetch all messages from Parse asynchronously
        //This is equivalent to a SELECT query with SQL

        query.findInBackground(new FindCallback<Message>() {
            @Override
            public void done(List<Message> messages, ParseException e) {
                if (e == null) {
                    mMessages.clear();
                    mMessages.addAll(messages);
                    mAdapter.notifyDataSetChanged(); //update adapter
                    //scroll to the bottom of the list on initial load
                    if (mFirstLoad){
                        rvChat.scrollToPosition(0);
                        mFirstLoad = false;
                    } else {
                        Log.e("message", "Error loading messages", e);
                    }
                }
            }
        });
    }
    
    
}