package ak.miniproject3;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class SignUp extends AppCompatActivity {
    private DatabaseReference mDatabase;
    private FirebaseAuth mAuth;
    private EditText password;
    private EditText email;
    private TextView login;
    private EditText passwordConfirm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        mAuth = FirebaseAuth.getInstance();
        final Button signup = findViewById(R.id.signUp);
        password = findViewById(R.id.yourPassword);
        passwordConfirm = findViewById(R.id.yourPasswordAgain);
        email = findViewById(R.id.yourEmail);
        mDatabase = FirebaseDatabase.getInstance().getReference();
        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String pass = password.getText().toString();
                String pass2 = passwordConfirm.getText().toString();
                signUp(email.getText().toString(), password.getText().toString());
            }
        });


    }


    public void signUp(String email, String pass) {
        mAuth.createUserWithEmailAndPassword(email, pass).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful())
                startActivity(new Intent(SignUp.this, EventsDisplay.class));
                else {
                    Toast.makeText(SignUp.this, "Authentication Failed.", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }
}
