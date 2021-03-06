package ak.miniproject3;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class EventCreate extends AppCompatActivity implements View.OnClickListener, DatePickerDialog.OnDateSetListener {
    private FirebaseAuth mAuth;
    private EditText title;
    private EditText description;
    private ImageView image;
    private Uri downloadUrl;
    private Button submit;
    private Button date;
    private DatabaseReference mRef;
    private DatabaseReference userRef;
    private StorageReference imagesRef;
    private FirebaseUser currentUser;
    private boolean dateSelected = false;
    private int month = -1;
    private int day = -1;
    private int year = -1;
    private boolean imageSet = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_create);
        title = findViewById(R.id.eventName);
        description = findViewById(R.id.eventDescription);
        image  = findViewById(R.id.eventImage);
        image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivityForResult(
                        new Intent(
                                Intent.ACTION_PICK,
                                android.provider.MediaStore.Images.Media.INTERNAL_CONTENT_URI),
                        1);
            }
        });
        mRef = FirebaseDatabase.getInstance().getReference("Events");
        userRef = FirebaseDatabase.getInstance().getReference("Confirm");
        mAuth = FirebaseAuth.getInstance();
        imagesRef = FirebaseStorage.getInstance().getReference("Pictures");
        date = findViewById(R.id.setDate);
        date.setOnClickListener(this);

        currentUser = mAuth.getCurrentUser();
        submit = findViewById(R.id.submit);
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (title.getText().toString().isEmpty()) {
                    Toast.makeText(EventCreate.this, "What's the name", Toast.LENGTH_LONG).show();
                } else {
                    upload();
                }
            }
        });


    }

    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.setDate:
                Calendar calendar = Calendar.getInstance();
                int year = calendar.get(Calendar.YEAR);
                int month = calendar.get(Calendar.MONTH);
                int day = calendar.get(Calendar.DAY_OF_MONTH);
                DatePickerDialog dialog = new DatePickerDialog(
                        EventCreate.this,
                        R.style.Theme_AppCompat_Light_Dialog_Alert,
                        this,
                        year, month, day);
                dialog.show();
                break;
        }
    }

    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK && requestCode == 1) {
            Uri selectedImage = data.getData();
            Bitmap bitmap;
            try {
                bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), selectedImage);
                image.setImageBitmap(bitmap);
                imageSet = true;

            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }


    }


    private void upload() {
        final String eventName = title.getText().toString();
        final String eventDescription = description.getText().toString();
        final String userEmail = currentUser.getEmail();
        final String userID =  currentUser.getUid();
        final String eventID = mRef.push().getKey();
        Bitmap bitmap = ((BitmapDrawable) image.getDrawable()).getBitmap();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG,100, baos);
        byte[] data = baos.toByteArray();
        final UploadTask uploadTask = imagesRef.child(eventID).putBytes(data);
        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(EventCreate.this, "Unable to upload", Toast.LENGTH_LONG).show();
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                //do nothing.
            }
        });
        final Task<Uri> urlTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
            @Override
            public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                if (!task.isSuccessful()) {
                    throw task.getException();
                }
                Log.i("TAG", imagesRef.getDownloadUrl().toString());
                return imagesRef.getDownloadUrl();
            }
        }).addOnCompleteListener(new OnCompleteListener<Uri>() {
            @Override
            public void onComplete(@NonNull Task<Uri> Task) {
                if (Task.isSuccessful()) {
                    downloadUrl = Task.getResult();
                    Date current = new Date();
                    int rsvp = 1;
                    String confirm;
                    if (month < 10) {
                        if (day < 10) {
                            confirm = "0" + month +"/0"+day +  "/" + year;
                        } else {
                            confirm = "0" + month+"/" +day + "/" + year;
                        }
                    } else {
                        if (day < 10) {
                            confirm = month + "/" + "0" + day + "/" + year;
                        } else {
                            confirm = month+"/" + day + "/" + year;
                        }
                    }
                    Event event = new Event(eventID, userEmail, downloadUrl.toString(), eventName, rsvp, eventDescription, confirm, current.getTime());
                    mRef.child(eventID).setValue(event);
                    ArrayList<String> users = new ArrayList<>();
                    users.add(eventID);
                    userRef.child(userID).setValue(users);
                    startActivity(new Intent(EventCreate.this, EventsDisplay.class));
                } else {
                    Toast.makeText(EventCreate.this, "Not uploaded", Toast.LENGTH_LONG).show();
                }
            }
        });

    }

    private void setEditingEnabled(boolean enabled) {

    }

    private void writeEvent(String userID, String email, String title, String description, String imageURL) {
        String key = mRef.child("events").push().getKey();
        Map<String, Object> eventValues = new HashMap<>();
        eventValues.put("email", email);
        eventValues.put("title", title);
        eventValues.put("description", description);
        eventValues.put("imageURL", imageURL);
        Map<String, Object> childUpdates = new HashMap<>();
        childUpdates.put("/events/" + key, eventValues);
        mRef.updateChildren(childUpdates);
    }

    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        String date = Integer.toString(month + 1) + "/" + Integer.toString(dayOfMonth) + "/" + Integer.toString(year);
        this.year = year;
        this.month = month + 1;
        this.day = dayOfMonth;
        dateSelected = true;
    }
}
