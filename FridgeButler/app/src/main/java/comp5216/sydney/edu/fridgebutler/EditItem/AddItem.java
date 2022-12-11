package comp5216.sydney.edu.fridgebutler.EditItem;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import comp5216.sydney.edu.fridgebutler.R;

/**
 * User input item name and expiry date
 * This class takes user input and stores in firebase
 */
public class AddItem extends AppCompatActivity {
    private DatePicker datepicker;
    private EditText addItem;
    private Button saveButton;
    private TextView autoSuggest;

    private String user_id;
    private FirebaseFirestore db;
    private FirebaseAuth firebaseAuth;
    private String remainingDays;
    private String documentID;

    private String radioButton;
    private String times;

    private static final String[] category = new String[] {
            "Fruits",
            "Vegetables",
            "Meat",
            "Seafood",
            "Dairy"
    };
    private static final String[] time = new String[] {
            "5 days",
            "3 days",
            "4 days",
            "2 days",
            "7 days"
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_item);

        datepicker = findViewById(R.id.datePicker1);
        addItem = findViewById(R.id.editItem);
        saveButton = findViewById(R.id.saveItem);

        db = FirebaseFirestore.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();
        user_id = firebaseAuth.getCurrentUser().getUid();

        autoSuggest = findViewById(R.id.suggestion);

        autoSuggest();
        setSaveButton();

    }

    //Handling and send data to firebase once user clicked save button
    public void setSaveButton() {
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String itemName = addItem.getText().toString();

                int day = datepicker.getDayOfMonth();
                int month = datepicker.getMonth() + 1;
                int year = datepicker.getYear();
                String expiry = day + "/" + month + "/" + year;

                Calendar userDeadline = Calendar.getInstance();
                userDeadline.set(year, month - 1, day);
                Long diff = userDeadline.getTimeInMillis() - System.currentTimeMillis();
                int days = (int)(diff / 86400000);
                diff -= days * 86400000;
                remainingDays = String.format("%d days left", days);

                if (userDeadline.getTimeInMillis() - System.currentTimeMillis() <= 0) {
                    remainingDays = "OVERDUE";
                }

                Map < String, Object > itemsList = new HashMap < > ();
                itemsList.put("expiryDate", expiry);
                itemsList.put("Name", itemName);

                CollectionReference df = db.collection("ingredients")
                        .document(user_id).collection("IngredientList");
                df.add(itemsList)
                        .addOnSuccessListener(new OnSuccessListener < DocumentReference > () {
                            @Override
                            public void onSuccess(DocumentReference documentReference) {
                                Log.d(TAG, "Add new Item: " + documentReference.getId());
                                documentID = documentReference.getId();
                                // Prepare data intent for sending it back
                                Intent data = new Intent();

                                // Pass relevant data back as a result
                                data.putExtra("ItemName", itemName);
                                data.putExtra("ItemTime", remainingDays);
                                data.putExtra("DocID", documentID);

                                // Activity finishes OK, return the data
                                setResult(RESULT_OK, data);
                                finish();
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.w(TAG, "Error adding document", e);
                            }
                        });
            }
        });
    }

    //set auto suggestion if user don't know the exact expiry date
    public void autoSuggest() {
        autoSuggest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                radioButton = category[0];
                AlertDialog.Builder builder = new AlertDialog.Builder(AddItem.this);
                builder.setTitle("Choice the category of your food");
                builder.setSingleChoiceItems(category, 0, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        radioButton = category[which];
                        times = time[which];

                    }
                });
                builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Snackbar.make(view, "Better consume before " + times + " after purchase", Snackbar.LENGTH_SHORT).show();
                    }
                });
                builder.setNegativeButton("Cancel", null);
                builder.create().show();
            }
        });
    }

}