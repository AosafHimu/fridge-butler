package comp5216.sydney.edu.fridgebutler.Login;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import comp5216.sydney.edu.fridgebutler.R;


public class Setting extends AppCompatActivity {
    FirebaseAuth firebaseAuth;
    FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        firebaseAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
    }

    public void changePassword(View view) {
        EditText resetEmail = new EditText(view.getContext());
        AlertDialog.Builder resetDialog = new AlertDialog.Builder(view.getContext());
        resetDialog.setTitle("Do you want to reset your password?");
        resetDialog.setMessage("Enter your email to receive reset password link.");
        resetDialog.setView(resetEmail);

        resetDialog.setPositiveButton("No", (dialogInterface, i) -> {
        }).setNegativeButton("Yes", (dialogInterface, i) -> {
            String email = resetEmail.getText().toString().trim();
            firebaseAuth.sendPasswordResetEmail(email).addOnSuccessListener(unused -> Toast.makeText(Setting.this, "Link sent to your email.", Toast.LENGTH_SHORT).show())
                    .addOnFailureListener(e -> Toast.makeText(Setting.this, e.getMessage(), Toast.LENGTH_LONG).show());

        });

        resetDialog.create().show();
    }


}