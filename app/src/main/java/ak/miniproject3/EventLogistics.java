package ak.miniproject3;

import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class EventLogistics extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private FloatingActionButton createEvent;
    private FirebaseDatabase database;
/*    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager layoutManager;
    private RecyclerView.Adapter mAdapter;
    private ArrayList<Event> eventsList;
    private EventsAdapter eventsAdapter;
    private String userID;*/
    private DatabaseReference refEvents;
    private Query events;
    private ImageView imageView;
    private TextView description;
    private TextView date;
    private TextView number;
    private TextView name;
    private CheckBox checkBox;
    private String eventName;
    private String eventDescription;
    private String eventDate;
    private String imageURL;
    private String numberAttending;
    private boolean attending;
    private ArrayList<String> thoseAttendingEvents = new ArrayList<>();
    DatabaseReference rsvp;
    private boolean check;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_logistics);
        Bundle b = getIntent().getExtras();
        final String id = b.getString("EventID");
        database.setPersistenceEnabled(true);
        database = FirebaseDatabase.getInstance();
        mAuth = FirebaseAuth.getInstance();

        rsvp = database.getReference().child("Confirm").child(mAuth.getCurrentUser().getUid());
        rsvp.keepSynced(true);
        rsvp.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() != null) {
                    thoseAttendingEvents= new ArrayList<>();
                    for (DataSnapshot event: dataSnapshot.getChildren()) {
                        thoseAttendingEvents.add(event.getValue().toString());
                    }
                }
                if (thoseAttendingEvents.contains(id) && thoseAttendingEvents != null) {
                    check = true;
                }
                if (check) {
                    checkBox.setChecked(true);
                }

            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        checkBox = findViewById(R.id.prob);


        imageView = findViewById(R.id.pic2);
        description = findViewById(R.id.about);
        date = findViewById(R.id.date);
        //number = findViewById(R.id.number);
        name = findViewById(R.id.name);
        checkBox = findViewById(R.id.prob);
        checkBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!checkBox.isChecked()) {
                    numberAttending = String.valueOf(Integer.parseInt(numberAttending) - 1);
                    number.setText("Confirm: " + numberAttending);
                    thoseAttendingEvents.remove(id);
                    refEvents.child("numberInterested").setValue(Integer.valueOf(numberAttending));
                    rsvp.setValue(thoseAttendingEvents);
                } else {

                    numberAttending = String.valueOf(Integer.parseInt(numberAttending) + 1);
                    number.setText("Confirm: " + numberAttending);
                    thoseAttendingEvents.add(id);
                    refEvents.child("numberInterested").setValue(Integer.valueOf(numberAttending));
                    rsvp.setValue(thoseAttendingEvents);
                }
            }});

        refEvents = database.getReference("events").child(id);
        refEvents.keepSynced(true);
        refEvents.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnap) {
                imageURL = dataSnap.child("imageURL").getValue().toString();
                numberAttending = dataSnap.child("numberInterested").getValue().toString();
                eventName = dataSnap.child("eventName").getValue().toString();
                eventDate = dataSnap.child("date").getValue().toString();
                eventDescription = dataSnap.child("eventDescription").getValue().toString();
                name.setText(eventName);
                date.setText(eventDate);
                number.setText("Confirm: " + numberAttending);
                description.setText(eventDescription);
                Glide.with(imageView.getContext()).load(imageURL).centerCrop().into(imageView);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.d("TAG", "Nah.");
            }
        });

        refEvents.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                numberAttending = dataSnapshot.child("ThoseInterested").getValue().toString();
                number.setText("Confirm: " +numberAttending);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                // do nothing
            }
        });
    }
}
