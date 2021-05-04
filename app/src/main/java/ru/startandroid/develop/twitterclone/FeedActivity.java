package ru.startandroid.develop.twitterclone;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class FeedActivity extends AppCompatActivity {

    ListView feedList;

    DatabaseReference ref;
    FirebaseAuth auth;
    ArrayAdapter<Tweet> tweetAdapter;
    ArrayList<Tweet> tweetList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feed);

        feedList = findViewById(R.id.feed_list);

        ref = FirebaseDatabase.getInstance().getReference();
        auth = FirebaseAuth.getInstance();

        tweetList = new ArrayList<>();
        tweetAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, tweetList);
        feedList.setAdapter(tweetAdapter);

        setTitle("Feed");


        ref.child("Users").child(auth.getCurrentUser().getUid()).child("Following").get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                for (DataSnapshot snap : task.getResult().getChildren()) {

                    ref.child("Users").get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DataSnapshot> task) {

                            for (DataSnapshot data : task.getResult().getChildren()) {

                                User user = data.getValue(User.class);

                                if (snap.getKey().equals(user.getUsername()) && (boolean) snap.getValue()) {

                                    Log.i("lol", snap.getKey() + " : " + user.getUsername() + " : " + (boolean) snap.getValue());

                                    for (DataSnapshot snapshot : data.child("Tweets").getChildren()) {

                                        Tweet tweet = snapshot.getValue(Tweet.class);

                                        tweet.setOwner(user.getUsername());

                                        tweetList.add(tweet);
                                    }
                                    tweetAdapter.notifyDataSetChanged();
                                }
                            }
                        }
                    });
                }
            }
        });
    }
}