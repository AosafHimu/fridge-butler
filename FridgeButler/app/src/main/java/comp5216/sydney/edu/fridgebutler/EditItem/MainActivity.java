package comp5216.sydney.edu.fridgebutler.EditItem;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;

import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.navigation.NavigationView;

import android.content.Intent;
import android.util.Log;
import android.widget.ListView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.Source;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;

import comp5216.sydney.edu.fridgebutler.Login.Login;
import comp5216.sydney.edu.fridgebutler.Map.MapsActivity;
import comp5216.sydney.edu.fridgebutler.Adapter.DataCallBack;
import comp5216.sydney.edu.fridgebutler.Adapter.Item;
import comp5216.sydney.edu.fridgebutler.Adapter.ItemAdapter;
import comp5216.sydney.edu.fridgebutler.Map.UpItem;
import comp5216.sydney.edu.fridgebutler.R;
import comp5216.sydney.edu.fridgebutler.Recipe.View.RecipeRecommendation;
import comp5216.sydney.edu.fridgebutler.Login.Setting;
import comp5216.sydney.edu.fridgebutler.ShoppingList.ShoppingList;
import comp5216.sydney.edu.fridgebutler.Login.ShowProfile;

/**
 * Main activity sets navigation drawer,
 * retrieve users' item from firebase
 */
public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    int REQUEST_OPEN_EDITLIST = 101;
    int REQUEST_EDIT_ITEM = 102;

    public DrawerLayout drawerLayout;
    public ActionBarDrawerToggle actionBarDrawerToggle;
    public NavigationView navigationView;
    public FirebaseAuth firebaseAuth;
    public FirebaseFirestore db;
    public String user_id;

    ArrayList < Item > expiredFood;
    ArrayList < Item > itemList;
    ItemAdapter adapter;
    ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // initialize user ID and firebase instance
        firebaseAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        user_id = firebaseAuth.getCurrentUser().getUid();

        // initialize drawer layout
        drawerLayout = findViewById(R.id.my_drawer_layout);
        actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, R.string.nav_open, R.string.nav_close);

        drawerLayout.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //initialize navigatiton view
        navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        expiredFood = new ArrayList < > ();
        itemList = new ArrayList < Item > ();
        adapter = new ItemAdapter(this, itemList);

        //load data from firebase using interface for data callback
        loadData(new DataCallBack() {
            @Override
            public void onComplete(ArrayList < Item > item) {
                adapter.notifyDataSetChanged();
                setupListViewListener();
            }
        });

        listView = findViewById(R.id.list_item);
        listView.setAdapter(adapter);

    }

    //set action on list view.
    //user can edit item if they click on it and delete if they long click
    private void setupListViewListener() {
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView < ? > adapterView, View view, int i, long l) {
                Intent intent = new Intent(MainActivity.this, EditItem.class);
                intent.putExtra("itemName", itemList.get(i).getName());
                intent.putExtra("expiryDate", itemList.get(i).getExpiryDate());
                intent.putExtra("docRef", itemList.get(i).getDocRef());
                startActivityForResult(intent, REQUEST_EDIT_ITEM);
            }
        });

        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView < ? > adapterView, View view, int position, long rowID) {
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                builder.setTitle(R.string.dialog_delete_title)
                        .setMessage(R.string.dialog_delete_msg)
                        .setPositiveButton(R.string.cancel, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialogInterface, int i) {}
                        })
                        .setNegativeButton(R.string.delete, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialogInterface, int i) {
                                String docRef = itemList.get(position).getDocRef();
                                DocumentReference docToDelete = db.collection("ingredients").document(user_id).collection("IngredientList").document(docRef);
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
                                adapter.notifyDataSetChanged();

                            }
                        });
                builder.create().show();
                return true;
            }
        });
    }

    //Set action on selecting item on menu
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (actionBarDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    //navigate to different activity once user select item on menu
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.nav_logout) {
            firebaseAuth.signOut();
            startActivity(new Intent(getApplicationContext(), Login.class));
        } else if (id == R.id.nav_account) {
            startActivity(new Intent(getApplicationContext(), ShowProfile.class));
        } else if (id == R.id.nav_settings) {
            startActivity(new Intent(getApplicationContext(), Setting.class));
        } else if (id == R.id.nav_edit_item) {
            //chnage here
            Intent intent = new Intent(getApplicationContext(), AddItem.class);
            startActivityForResult(intent, REQUEST_OPEN_EDITLIST);
        } else if (id == R.id.nav_map) {
            startActivity(new Intent(getApplicationContext(), MapsActivity.class));
        } else if (id == R.id.nav_recommend) {
            Intent intent = new Intent(getApplicationContext(), RecipeRecommendation.class);
            ArrayList < String > ingredientList = new ArrayList < > ();
            for (int i = 0; i < itemList.size(); i++) {
                ingredientList.add(itemList.get(i).getName());
            }
            Log.d(TAG, "Check extras sent" + String.valueOf(ingredientList.size()));
            intent.putExtra("ingredientList", ingredientList);
            startActivity(intent);

        } else if (id == R.id.nav_offer) {
            startActivity(new Intent(getApplicationContext(), UpItem.class));
        } else if (id == R.id.nav_shopping) {
            startActivity(new Intent(getApplicationContext(), ShoppingList.class));
        }
        drawerLayout.closeDrawers();
        return true;
    }

    //Activity result to get data from intent
    @SuppressLint("MissingSuperCall")
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_OPEN_EDITLIST) {
            if (resultCode == RESULT_OK) {
                String editItemName = data.getExtras().getString("ItemName");
                String editItemTime = data.getExtras().getString("ItemTime");
                String editDocID = data.getExtras().getString("DocID");
                Item item = new Item(editItemName, editItemTime, editDocID);
                itemList.add(item);

                Collections.sort(itemList, new Comparator<Item>() {
                    public int compare(Item o1, Item o2) {
                        if(o1.getExpiryDate().equals("OVERDUE")){
                            return -1;
                        }
                        else if(o2.getExpiryDate().equals("OVERDUE")){
                            return 1;
                        }
                        return o1.getExpiryDate().compareTo(o2.getExpiryDate());
                    }
                });
                adapter.notifyDataSetChanged();
            }
        } else if (requestCode == REQUEST_EDIT_ITEM) {
            if (resultCode == RESULT_OK) {
                String editItemName = data.getExtras().getString("ItemName");
                String editItemTime = data.getExtras().getString("ItemTime");
                String editDocID = data.getExtras().getString("DocID");
                Item item = new Item(editItemName, editItemTime, editDocID);
                Log.d(TAG, "check edit intent: " + item.getName());
                for (int i = 0; i < itemList.size(); i++) {
                    if (itemList.get(i).getDocRef().equals(item.getDocRef())) {
                        itemList.set(i, item);
                    }
                }

                Collections.sort(itemList, new Comparator<Item>() {
                    public int compare(Item o1, Item o2) {
                        if(o1.getExpiryDate().equals("OVERDUE")){
                            return -1;
                        }
                        else if(o2.getExpiryDate().equals("OVERDUE")){
                            return 1;
                        }
                        return o1.getExpiryDate().compareTo(o2.getExpiryDate());
                    }
                });
                adapter.notifyDataSetChanged();
            }
        }

    }

    //load data from firebase
    public void loadData(DataCallBack dataCallBack) {
        CollectionReference collectionRef = db.collection("ingredients").document(user_id).collection("IngredientList");
        Source source = Source.DEFAULT;
        collectionRef.get(source).addOnCompleteListener(new OnCompleteListener < QuerySnapshot > () {
            @Override
            public void onComplete(@NonNull Task < QuerySnapshot > task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document: task.getResult()) {
                        // Convert deadline string to int array
                        String deadline = document.get("expiryDate").toString();
                        int[] dateArray = Arrays.stream(deadline.split("/"))
                                .mapToInt(Integer::parseInt)
                                .toArray();

                        // Calculate remaining days
                        int day = dateArray[0];
                        int month = dateArray[1];
                        int year = dateArray[2];

                        // Calculate remaining days
                        Calendar userDeadline = Calendar.getInstance();
                        userDeadline.set(year, month - 1, day);
                        Long diff = userDeadline.getTimeInMillis() - System.currentTimeMillis();
                        int days = (int)(diff / 86400000);
                        diff -= days * 86400000;
                        String remainingDays = String.format("%d days left", days);

                        if (userDeadline.getTimeInMillis() - System.currentTimeMillis() <= 0) {
                            remainingDays = "OVERDUE";
                        }

                        Item item = new Item(document.get("Name").toString(), remainingDays, document.getId());
                        itemList.add(item);

                        Collections.sort(itemList, new Comparator<Item>() {
                            public int compare(Item o1, Item o2) {
                                if(o1.getExpiryDate().equals("OVERDUE")){
                                    return -1;
                                }
                                else if(o2.getExpiryDate().equals("OVERDUE")){
                                    return 1;
                                }
                                return o1.getExpiryDate().compareTo(o2.getExpiryDate());
                            }
                        });
                    }
                    dataCallBack.onComplete(itemList);
                    checkExpiry();
                } else {
                    Log.d(TAG, "Error getting documents: ", task.getException());
                }
            }
        });
    }

    // Check status of added food, suggest options for expired food
    public void checkExpiry() {
        ArrayList < String > ingredientList = new ArrayList < > ();
        int flag = 0; // flag to signal if there is food that will expire in 1 day
        for (int i = 0; i < itemList.size(); i++) {
            if (itemList.get(i).getExpiryDate().equals("OVERDUE")) {
                if (!expiredFood.contains(itemList.get(i))) {
                    expiredFood.add(itemList.get(i));
                }
            }

            if (flag == 0) {
                if (itemList.get(i).getExpiryDate().charAt(0) == '1') {
                    // If the food will expire in 1 day, suggest the user to get recipe
                    flag = 1;
                    ingredientList.add(itemList.get(i).getName());
                }
            }
        }

        // Ask user if want to remove all the expired food
        if (expiredFood.size() != 0) {
            AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
            builder.setTitle("Expired food detected")
                    .setMessage("Do you want to remove all the expired food?")
                    .setPositiveButton("Cancel", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialogInterface, int i) {

                        }
                    })
                    .setNegativeButton("Yes", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialogInterface, int i) {
                            for (int a = 0; a < expiredFood.size(); a++) {
                                String docRef = expiredFood.get(a).getDocRef();
                                DocumentReference docToDelete = db.collection("ingredients").document(user_id).collection("IngredientList").document(docRef);
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
                            }

                            itemList.removeAll(expiredFood);
                            expiredFood.clear();
                            adapter.notifyDataSetChanged();
                        }
                    });

            builder.create().show();
        }

        // Ask user to handle food which will expire in 1 day
        if (flag == 1) {
            AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
            builder.setTitle("Note: food will expire in 1 day")
                    .setMessage("There are food that will expire in 1 day, do you want to check recipe?")
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialogInterface, int i) {
                            // Navigate to recipe
                            Intent intent = new Intent(getApplicationContext(), RecipeRecommendation.class);
                            intent.putExtra("ingredientList", ingredientList);
                            Log.d(TAG, "Check ingredient List" + ingredientList.size());
                            startActivity(intent);
                        }
                    })
                    .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialogInterface, int i) {
                            // User cancelled the dialog
                            // Nothing happens
                        }
                    });

            builder.create().show();
        }
    }

}