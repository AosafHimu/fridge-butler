package comp5216.sydney.edu.fridgebutler.ShoppingList;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

import comp5216.sydney.edu.fridgebutler.Adapter.Item;
import comp5216.sydney.edu.fridgebutler.Map.UpItem;
import comp5216.sydney.edu.fridgebutler.R;

public class ShoppingList extends AppCompatActivity {
    ImageView addCart;
    FirebaseAuth firebaseAuth;
    FirebaseFirestore db;
    String user_id;

    ListView shopping;
    ArrayAdapter < String > adapter;
    ArrayList < String > shoppingList;
    ArrayList < Item > itemList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shopping_list);

        firebaseAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        user_id = firebaseAuth.getCurrentUser().getUid();

        shoppingList = new ArrayList < > ();
        itemList = new ArrayList < > ();

        adapter = new ArrayAdapter < String > (this, R.layout.shopping_list_item, R.id.textView, shoppingList);
        addCart = findViewById(R.id.addCart);
        shopping = findViewById(R.id.shop);
        shopping.setAdapter(adapter);

        CollectionReference collectionRef = db.collection("shopping").document(user_id).collection("IngredientList");
        collectionRef.get().addOnCompleteListener(new OnCompleteListener < QuerySnapshot > () {
            @Override
            public void onComplete(@NonNull Task < QuerySnapshot > task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document: task.getResult()) {
                        Log.d("TAG", "check item in shopping list " + document.get("item").toString());
                        itemList.add(new Item(document.get("item").toString(), document.getId()));
                        shoppingList.add(document.get("item").toString());
                        adapter.notifyDataSetChanged();
                    }
                    onClickListerner();
                } else {
                    Log.d(TAG, "Error getting documents: ", task.getException());
                }
            }
        });

    }

    public void onClickListerner() {
        shopping.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView < ? > adapterView, View view, int position, long rowID) {
                AlertDialog.Builder builder = new AlertDialog.Builder(ShoppingList.this);
                builder.setTitle("Do you want to delete this item?")
                        .setPositiveButton(R.string.cancel, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialogInterface, int i) {}
                        })
                        .setNegativeButton(R.string.delete, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialogInterface, int i) {
                                String docRef = itemList.get(position).getDocRef();
                                DocumentReference docToDelete = db.collection("shopping").document(user_id).collection("IngredientList").document(docRef);
                                docToDelete.delete().addOnSuccessListener(new OnSuccessListener < Void > () {
                                    @Override
                                    public void onSuccess(Void unused) {
                                        Log.d(TAG, "DocumentSnapshot successfully deleted!");
                                    }
                                })
                                        .addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                Log.w(TAG, "Error deleting document", e);
                                            }
                                        });
                                itemList.remove(position);
                                shoppingList.remove(position);
                                adapter.notifyDataSetChanged();
                            }
                        });
                builder.create().show();
                return true;
            }
        });

        addCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(ShoppingList.this);
                builder.setTitle("Do you want to upload your shopping list")
                        .setMessage("If you need your grocery delivered, others can contact to help you")
                        .setPositiveButton("Cancel", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialogInterface, int i) {

                            }
                        })
                        .setNegativeButton("Yes", new DialogInterface.OnClickListener() {
                            @RequiresApi(api = Build.VERSION_CODES.O)
                            public void onClick(DialogInterface dialogInterface, int i) {
                                String toBuy = String.join("\n", shoppingList);
                                Intent intent = new Intent(getApplicationContext(), UpItem.class);
                                intent.putExtra("shoppingList", toBuy);
                                startActivity(intent);
                            }
                        });

                builder.create().show();
            }
        });

    }
}