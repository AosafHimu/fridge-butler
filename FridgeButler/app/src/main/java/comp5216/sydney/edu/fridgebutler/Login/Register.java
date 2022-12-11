package comp5216.sydney.edu.fridgebutler.Login;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import java.util.HashMap;
import java.util.Map;

import comp5216.sydney.edu.fridgebutler.EditItem.MainActivity;
import comp5216.sydney.edu.fridgebutler.R;


public class Register extends AppCompatActivity {
    String TAG = this.getClass().getSimpleName();
    EditText nameInput, passwordInput, emailInput;
    Button registerButton;
    TextView logIn;
    FirebaseAuth firebaseAuth;
    FirebaseFirestore db;
    String user_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        nameInput = findViewById(R.id.Name);
        emailInput = findViewById(R.id.emailAddress);
        passwordInput = findViewById(R.id.Password);
        registerButton = findViewById(R.id.Register);
        logIn = findViewById(R.id.login);

        firebaseAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
    }

    public void setRegisterButton(View view){
        String username = nameInput.getText().toString().trim();
        String email = emailInput.getText().toString().trim();
        String password = passwordInput.getText().toString().trim();

        if(TextUtils.isEmpty(username)){
            nameInput.setError("Please enter your full name");
        }
        if(TextUtils.isEmpty(email)){
            emailInput.setError("Please enter your email");
            return;
        }
        if(TextUtils.isEmpty(password)){
            passwordInput.setError("Please enter your password");
            return;
        }
        if(password.length() < 6){
            passwordInput.setError("Password must have at least 6 characters.");
            return;
        }

        firebaseAuth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                Map<String, Object> user = new HashMap<>();
                user.put("FullName", username);
                user.put("Email", email);

                if(task.isSuccessful()){
                    Toast.makeText(Register.this, "User Created", Toast.LENGTH_SHORT).show();
                    Log.d(TAG, " User Registered");
                    user_id = firebaseAuth.getCurrentUser().getUid();
                    DocumentReference documentReference = db.collection("users").document(user_id);
                    documentReference.set(user).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Log.d(TAG, "onSuccess: user Profile is created for "+ user_id);
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.d(TAG, "onFailure: " + e.toString());
                            Toast.makeText(Register.this, "Error" + task.getException().getMessage(),Toast.LENGTH_SHORT).show();
                        }
                    });
                    startActivity(new Intent(getApplicationContext(), MainActivity.class));
                }
                else{
                    Toast.makeText(Register.this, "Error! " + task.getException().getMessage(),Toast.LENGTH_SHORT).show();
                    Log.d(TAG, "Cannot register");
                }
            }
        });
    }

    public void setLogIn(View view){
        startActivity(new Intent(getApplicationContext(), Login.class));
    }



}