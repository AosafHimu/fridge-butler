package comp5216.sydney.edu.fridgebutler.Login;


import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import com.google.firebase.auth.FirebaseAuth;

import comp5216.sydney.edu.fridgebutler.EditItem.MainActivity;
import comp5216.sydney.edu.fridgebutler.R;

public class Login extends AppCompatActivity {
    EditText emailInput, passwordInput;
    Button loginButton;
    FirebaseAuth firebaseAuth;
    TextView registerUser, forgotPass;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        emailInput = findViewById(R.id.emailAddress);
        passwordInput = findViewById(R.id.Password);
        loginButton = findViewById(R.id.login);
        registerUser = findViewById(R.id.registerHere);
        forgotPass = findViewById(R.id.forgotPass);

        firebaseAuth = FirebaseAuth.getInstance();

    }

    public void setLoginButton(View view){
        String email = emailInput.getText().toString().trim();
        String password = passwordInput.getText().toString().trim();

        if(TextUtils.isEmpty(email)){
            emailInput.setError("Please enter your email");
            return;
        }
        if(TextUtils.isEmpty(password)){
            passwordInput.setError("Please enter your password");
            return;
        }
        if(password.length() < 5){
            passwordInput.setError("Password must have at least 6 characters.");
            return;
        }

        firebaseAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(task -> {
            if(task.isSuccessful()){
                Toast.makeText(Login.this, "Welcome", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(getApplicationContext(), MainActivity.class));
            }
            else{
                Toast.makeText(Login.this, "Error!" + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void setRegisterUser(View view){
        startActivity(new Intent(getApplicationContext(), Register.class));
    }

    public void resetPassword(View view){
        EditText resetEmail = new EditText(view.getContext());
        AlertDialog.Builder resetDialog = new AlertDialog.Builder(view.getContext());
        resetDialog.setTitle("Do you want to reset your password?");
        resetDialog.setMessage("Enter your email to receive reset password link.");
        resetDialog.setView(resetEmail);

        resetDialog.setPositiveButton("No", (dialogInterface, i) -> {
        }).setNegativeButton("Yes", (dialogInterface, i) -> {
            String email  = resetEmail.getText().toString().trim();
            firebaseAuth.sendPasswordResetEmail(email).addOnSuccessListener(unused -> Toast.makeText(Login.this, "Link sent to your email.", Toast.LENGTH_SHORT).show())
                    .addOnFailureListener(e -> Toast.makeText(Login.this,  e.getMessage(), Toast.LENGTH_LONG).show());

        });
        resetDialog.create().show();
    }
}