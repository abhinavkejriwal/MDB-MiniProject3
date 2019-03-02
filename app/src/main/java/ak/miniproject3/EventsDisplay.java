package ak.miniproject3;

import android.app.DownloadManager;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class EventsDisplay extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private FloatingActionButton fABcreate;
    private FirebaseDatabase mydata;
    private RecyclerView myRV;
    private RecyclerView.LayoutManager myLM;
    private RecyclerView.Adapter myRVadapter;
    private ArrayList<Event> myEventsList;
    private EventsAdapter myEventsAdapter;

    private String myUSER;

    private DatabaseReference mydatabase;
    private Query events;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_events_display);

        FirebaseDatabase.getInstance().setPersistenceEnabled(true);

        mAuth = FirebaseAuth.getInstance();

        FirebaseUser currUser = mAuth.getCurrentUser();

        fABcreate = findViewById(R.id.fAB);
        fABcreate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(EventsDisplay.this, EventCreate.class));
            }
        });


        mydata = FirebaseDatabase.getInstance();
        myUSER = mAuth.getCurrentUser().getUid();
        myEventsList = new ArrayList<Event>();

        myRV = findViewById(R.id.myRecyclerView);
        myLM = new LinearLayoutManager(this);
        myRV.setLayoutManager(myLM);

        myEventsAdapter = new EventsAdapter(myEventsList);
        myRV.setAdapter(myEventsAdapter);
        fetchData();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.events_display, menu);
        final MenuItem Item = menu.findItem(R.id.Out);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        mAuth.signOut();
        EventsDisplay.this.startActivity(new Intent(EventsDisplay.this, MainActivity.class));
        return true;
    }


    private void fetchData() {
        events = mydata.getReference("events").orderByChild("time");
        events.keepSynced(true);
        events.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                myEventsList.clear();
                for (DataSnapshot snapshot: dataSnapshot.getChildren()) {
                    String id = snapshot.child("id").getValue().toString();
                    String email = snapshot.child("email").getValue().toString();
                    String imageURL = snapshot.child("imageURL").getValue().toString();
                    String eventName = snapshot.child("eventName").getValue().toString();
                    int numInterested = Integer.valueOf(snapshot.child("numberInterested").getValue().toString());
                    String description = snapshot.child("eventDescription").getValue().toString();
                    String date = snapshot.child("date").getValue().toString();
                    Long timestamp = Long.valueOf(snapshot.child("time").getValue().toString());
                    myEventsList.add(0, new Event(id, email, imageURL, eventName, numInterested, description, date, timestamp));
                }
                myEventsAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.d("TAG", "Couldn't retrieve events");
            }
        });


    }
}
