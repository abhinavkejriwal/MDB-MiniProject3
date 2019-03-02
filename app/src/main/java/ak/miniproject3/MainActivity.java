package ak.miniproject3;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private EditText myEmail;
    private EditText myPassword;
    private TextView mySignup;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);




        //Set Button
        Button myLoginButton = findViewById(R.id.loginButton);
        mAuth = FirebaseAuth.getInstance();
        myEmail = findViewById(R.id.emailText);
        myPassword = findViewById(R.id.passwordText);

        myLoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signIn(myEmail.getText().toString(), myPassword.getText().toString());
            }
        });

        mySignup = findViewById(R.id.signUp);
        mySignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, SignUp.class));
            }
        });



    }

    @Override
    public void onStart() {
        super.onStart();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            startActivity(new Intent(MainActivity.this, EventsDisplay.class));
        }
    }

    public void signIn(String email, String password) {
        mAuth.signInWithEmailAndPassword(email, password).
                addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    Intent intent = new Intent(MainActivity.this, EventsDisplay.class);
                    startActivity(intent);
                } else {
                    Toast.makeText(MainActivity.this, "Nah. Something went wrong.", Toast.LENGTH_LONG).show();
                }
            }
        });
/*                addOnCompleteListener(this, (task) -> {
                    if (task.isSuccessful()) {
                        Intent myIntent = new Intent(MainActivity.this, EventsDisplay.class);
                        startActivity(myIntent);
                    }
                })*/
    }

    //Q: Why did this not work.
/*    mAuth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
        @Override
        public void onComplete(@NonNull Task<AuthResult> task) {
            if (task.isSuccessful()) {
                // Sign in success, update UI with the signed-in user's information
                Log.d(TAG, "signInWithEmail:success");
                FirebaseUser user = mAuth.getCurrentUser();
                updateUI(user);
            } else {
                // If sign in fails, display a message to the user.
                Log.w(TAG, "signInWithEmail:failure", task.getException());
                Toast.makeText(EmailPasswordActivity.this, "Authentication failed.",
                        Toast.LENGTH_SHORT).show();
                updateUI(null);
            }

            // ...
        }
    });*/
}
