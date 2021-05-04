package ru.startandroid.develop.twitterclone;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckedTextView;
import android.widget.EditText;
import android.widget.ListView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.EventListener;
import java.util.HashMap;
import java.util.Map;


public class MainActivity extends AppCompatActivity {

    DatabaseReference ref;
    FirebaseAuth auth;

    ListView listView;
    ArrayAdapter<User> adapter;
    ArrayList<User> users;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        super.onOptionsItemSelected(item);

        switch (item.getItemId()) {

            case R.id.addTweet: {

                AlertDialog.Builder builder = new AlertDialog.Builder(this);

                final EditText textTweet = new EditText(this);
                textTweet.setHint("Tweet");
                builder.setView(textTweet);

                builder.setIcon(android.R.drawable.ic_dialog_email);
                builder.setTitle("Add a tweet");
                builder.setMessage("What is on your mind?");
                builder.setPositiveButton("Sent", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm");
                        Date date = new Date();
                        String time = formatter.format(date);

                        Map<String, Object> map = new HashMap<>();
                        map.put("time", time);
                        map.put("content", textTweet.getText().toString());

                        ref.child("Users").child(auth.getCurrentUser().getUid()).child("Tweets").push().updateChildren(map);

                        dialogInterface.cancel();
                    }

                });
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                        dialogInterface.cancel();

                    }
                });
                builder.show();

                break;
            }
            case R.id.showFeed: {

                Intent intent = new Intent(MainActivity.this, FeedActivity.class);

                startActivity(intent);

                break;
            }
            case R.id.logout: {

                auth.signOut();

                startActivity(new Intent(MainActivity.this, StartActivity.class));

                finish();

                break;
            }


        }


        return true;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setTitle("User List");

        ref = FirebaseDatabase.getInstance().getReference();
        auth = FirebaseAuth.getInstance();

        listView = findViewById(R.id.list_view);
        listView.setChoiceMode(AbsListView.CHOICE_MODE_MULTIPLE);
        users = new ArrayList<>();
        adapter = new ArrayAdapter<User>(this, android.R.layout.simple_list_item_checked, users);
        listView.setAdapter(adapter);

        ref.child("Users").child(auth.getCurrentUser().getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                int countFollowing = 0;

                for (DataSnapshot data : snapshot.child("Following").getChildren()) {

                    boolean following = (boolean) data.getValue();

                    if (!following) {
                        countFollowing++;
                    }
                }

                if (countFollowing == snapshot.child("Following").getChildrenCount()) {
                    ref.child("Users").child(auth.getCurrentUser().getUid()).child("noFollowing").setValue(true);
                } else {
                    ref.child("Users").child(auth.getCurrentUser().getUid()).child("noFollowing").setValue(false);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        ref.child("Users").get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {

                for (DataSnapshot data : task.getResult().getChildren()) {

                    User user = data.getValue(User.class);

                    if (!data.getKey().equals(auth.getCurrentUser().getUid())) {

                        users.add(user);

                    } else {

                        int i = 0;

                        for (DataSnapshot dataSnapshot : data.child("Following").getChildren()) {
                            listView.setItemChecked(i, (boolean) dataSnapshot.getValue());
                            i++;
                        }

                    }

                }

                adapter.notifyDataSetChanged();

            }
        });

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                String username = users.get(position).getUsername();
                CheckedTextView v = (CheckedTextView) view;
                boolean currentCheck = v.isChecked();
                Map<String, Object> map = new HashMap<>();
                map.put(username, currentCheck);
                ref.child("Users").child(auth.getCurrentUser().getUid()).child("Following").updateChildren(map);
            }
        });

    }
}