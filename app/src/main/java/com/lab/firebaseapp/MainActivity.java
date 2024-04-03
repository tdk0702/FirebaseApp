package com.lab.firebaseapp;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MainActivity extends AppCompatActivity {

    private TextView txtDetails,tvTittle;
    private EditText inputName, inputN1, inputN2;
    private Button btnSave,btnGet;
    private DatabaseReference mFirebaseDatabase;
    private FirebaseDatabase mFirebaseInstance;

    private String userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        txtDetails = (TextView) findViewById(R.id.tvGet);
        tvTittle = findViewById(R.id.tvInput);
        inputN1 = (EditText) findViewById(R.id.etNum1);
        inputN2 = (EditText) findViewById(R.id.etNum2);
        inputName = (EditText) findViewById(R.id.etName);
        btnSave = (Button) findViewById(R.id.btnCal);
        btnGet = findViewById(R.id.btnGet);

        mFirebaseInstance = FirebaseDatabase.getInstance("https://firedata-app-tdk-default-rtdb.asia-southeast1.firebasedatabase.app/");

        mFirebaseDatabase = mFirebaseInstance.getReference("users");

        mFirebaseInstance.getReference("title").setValue("App");

        // app_title change listener
        mFirebaseInstance.getReference("title").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.e("TAG", "App title updated");
                String appTitle = dataSnapshot.getValue(String.class);
                tvTittle.setText(appTitle);
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.e("TAG", "Failed to read app title value.", error.toException());
            }
        });

        btnGet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                userId = inputName.getText().toString();
                mFirebaseDatabase.child(userId).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange( DataSnapshot datasnapshot) {
                        Log.e("TAG", "App title updated");
                        User user = datasnapshot.getValue(User.class);
                        txtDetails.setText(user.Out());
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }
        });
        // Save / update the user
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (inputN1.getText()==null || inputN2.getText()==null || inputName.getText() == null){
                    Log.e("TAG", "Data empty!");
                }
                String name = inputName.getText().toString();
                int n1 = 0;
                int n2 = 0;
                try{
                    n1 = Integer.parseInt(inputN1.getText().toString());
                    n2 = Integer.parseInt(inputN2.getText().toString());
                }
                catch(NumberFormatException e){
                    n1 = -2004;
                    n2 = -2004;
                }
                if (TextUtils.isEmpty(userId)) {
                    createUser(name, n1, n2);
                } else {
                    updateUser(name, n1, n2);
                }
            }
        });
    }
    private void createUser(String name, int n1, int n2) {
        if (TextUtils.isEmpty(userId) || userId != name)
            userId = name;
            //userId = mFirebaseDatabase.push().getKey().toString();
        mFirebaseDatabase.push().setValue(userId);
        User user = new User(name, n1, n2);

        mFirebaseDatabase.child(userId).setValue(user);

        addUserChangeListener();
    }
    private void addUserChangeListener() {
        // User data change listener
        mFirebaseDatabase.child(userId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);

                // Check for null
                if (user == null) {
                    Log.e("TAG", "User data is null!");
                    return;
                }

                Log.e("TAG", "User data is changed!" + user.Out());

                // Display newly updated name and email
                txtDetails.setText(user.Out());

                // clear edit text
                inputN1.setText("");
                inputN2.setText("");
                inputName.setText("");
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.e("TAG", "Failed to read user", error.toException());
            }
        });
    }

    private void updateUser(String name, int n1,int n2) {
        User user = new User(name,n1,n2);
        mFirebaseDatabase.child(userId).setValue(user);
        addUserChangeListener();
    }
}