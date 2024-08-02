package com.example.chandeoapp;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Date;

import de.hdodenhof.circleimageview.CircleImageView;

public class chatwindo extends AppCompatActivity {
    String reciverimg, reciverUid, reciverName, SenderUID;
    CircleImageView profile;
    TextView reciverNName;
    CardView sendbtn;
    EditText textmsg;
    FirebaseAuth firebaseAuth;
    FirebaseDatabase database;
    public static String senderImg;
    public static String reciverIImg;
    String senderRoom, reciverRoom;
    RecyclerView messageAdpter;
    ArrayList<msgModelclass> messagesArrayList;
    messagesAdpter mmessagesAdpter;
    void mappingData(){
        getSupportActionBar().hide();
        messagesArrayList = new ArrayList<>();
        messageAdpter = findViewById(R.id.msgadpter);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setStackFromEnd(true);
        messageAdpter.setLayoutManager(linearLayoutManager);
        reciverName = getIntent().getStringExtra("nameeee");
        reciverimg = getIntent().getStringExtra("reciverImg");
        sendbtn = findViewById(R.id.sendbtnn);
        textmsg = findViewById(R.id.textmsg);

        profile = findViewById(R.id.profileimgg);
        reciverNName = findViewById(R.id.recivername);

        Picasso.get().load(reciverimg).into(profile);
    }
    void fireBaseInitial(){
        reciverNName.setText("" + reciverName);
        FirebaseApp.initializeApp(this);
        database = FirebaseDatabase.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();
        SenderUID = firebaseAuth.getUid();
        reciverUid = getIntent().getStringExtra("uid");
        if (SenderUID.compareTo(reciverUid) < 0) {
            senderRoom = SenderUID + reciverUid;
        } else {
            senderRoom = reciverUid + SenderUID;
        }

        Log.d("123", "fireBaseInitial: "+senderRoom);
        reciverRoom = reciverUid + SenderUID;
    }
    void eventInitial(){
        DatabaseReference reference = database.getReference().child("user").child(firebaseAuth.getUid());
        DatabaseReference chatreference = database.getReference().child("chats").child(senderRoom).child("messages");
        chatreference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                messagesArrayList.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    msgModelclass messages = dataSnapshot.getValue(msgModelclass.class);
                    assert messages != null;
                    messages.setId(dataSnapshot.getKey());
                    messagesArrayList.add(messages);
                }
                mmessagesAdpter = new messagesAdpter(chatwindo.this, messagesArrayList,senderRoom);
                messageAdpter.setAdapter(mmessagesAdpter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                senderImg = snapshot.child("profilepic").getValue().toString();
                reciverIImg = reciverimg;
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        sendbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Get the message text
                String message = textmsg.getText().toString();

                // Check if the message is empty
                if (message.isEmpty()) {
                    Toast.makeText(chatwindo.this, "Enter The Message First", Toast.LENGTH_SHORT).show();
                    return;
                }
                sendMessage(message);
            }
        });
    }
    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chatwindo);
        mappingData();
        fireBaseInitial();
        eventInitial();
    }
    void sendMessage(String message){
        // Clear the message input field
        textmsg.setText("");

        // Get the current date and time
        Date date = new Date();

        // Create a message object
        msgModelclass messagess = new msgModelclass(message, SenderUID, date.getTime());

        // Initialize the Firebase database
        database = FirebaseDatabase.getInstance();

        // Reference to the sender's room
        DatabaseReference senderReference = database.getReference().child("chats").child(senderRoom).child("messages").push();

        // Set the message to the sender's room
        senderReference.setValue(messagess).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    // Message added to the sender's room
                } else {
                    // Handle the error
                    Toast.makeText(chatwindo.this, "Failed to send message", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    protected void onResume() {
        getMessage();
        super.onResume();
    }

    void getMessage(){
        DatabaseReference messagesRef = database.getReference().child("chats").child(senderRoom).child("messages");
// Thêm một ValueEventListener để lắng nghe sự thay đổi của tin nhắn
        messagesRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // Xóa danh sách tin nhắn cũ
                messagesArrayList.clear();

                // Duyệt qua tất cả các tin nhắn và thêm vào danh sách
                for (DataSnapshot messageSnapshot : dataSnapshot.getChildren()) {
                    msgModelclass message = messageSnapshot.getValue(msgModelclass.class);
                    message.setId(messageSnapshot.getKey());
                    messagesArrayList.add(message);
                }
                Log.d("123", messagesArrayList.size()+"");
                // Cập nhật Adapter
                mmessagesAdpter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Xử lý lỗi
                Log.w(TAG, "loadMessages:onCancelled", databaseError.toException());
            }
        });
    }
}