package comp5216.sydney.edu.fridgebutler.Recipe.View;

import static comp5216.sydney.edu.fridgebutler.Recipe.Controller.RecipeApi.BASE_URL;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import comp5216.sydney.edu.fridgebutler.Adapter.RecycleViewAdapter;
import comp5216.sydney.edu.fridgebutler.R;
import comp5216.sydney.edu.fridgebutler.Recipe.Controller.RecipeApi;
import comp5216.sydney.edu.fridgebutler.Recipe.Model.Ingredient;
import comp5216.sydney.edu.fridgebutler.Recipe.Model.Recipe;
import comp5216.sydney.edu.fridgebutler.Recipe.Model.Step;
import comp5216.sydney.edu.fridgebutler.ShoppingList.ShoppingList;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/***************************************************************************************
 *    Title:  FridgePal
 *    Author: amailk
 *    Date:  2019-08-02
 *    Code version: N/A
 *    Availability: https://github.com/amailk/fridge-pal-android
 *
 ***************************************************************************************/

/**
 * Get recipe information from recipe ID retrieved from intent
 */
public class RecipeSelected extends AppCompatActivity {
    ImageView addToShop;

    String TAG = this.getClass().getSimpleName();
    static Gson gson = new GsonBuilder().setLenient().create();
    static Retrofit retrofit = new Retrofit.Builder().baseUrl(BASE_URL).addConverterFactory(GsonConverterFactory.create(gson)).build();
    static final RecipeApi recipeApi = retrofit.create(RecipeApi.class);

    TextView title;
    List < String > alreadyHave;
    List < String > cookingInstruction;
    ArrayList < String > ingredientList;
    RecycleViewAdapter recycleViewAdapter;

    public FirebaseAuth firebaseAuth;
    public FirebaseFirestore db;
    public String user_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe_selected);

        firebaseAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        user_id = firebaseAuth.getCurrentUser().getUid();

        title = findViewById(R.id.name);
        addToShop = findViewById(R.id.add);

        alreadyHave = new ArrayList < > ();
        ingredientList = new ArrayList < > ();
        cookingInstruction = new ArrayList < > ();
        cookingInstruction.add("INGREDIENTS");

        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.cookingInstruction);
        recycleViewAdapter = new RecycleViewAdapter(cookingInstruction);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(recycleViewAdapter);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            String recipeId = extras.getString("recipeId");
            alreadyHave = extras.getStringArrayList("ingredient");
            fetchRecipeInstruction(recipeId, alreadyHave);
        }
    }

    /**
     * Get recipe instruction from recipe id
     */
    public void fetchRecipeInstruction(String id, List < String > alreadyHave) {
        Call < Recipe > recipeCall = recipeApi.getRecipeDetail(id, recipeApi.API_KEY_HEADER);
        recipeCall.enqueue(new Callback < Recipe > () {
            @Override
            public void onResponse(Call < Recipe > call, Response < Recipe > response) {
                Recipe fetchedRecipe = response.body();
                title.setText(fetchedRecipe.getTitle());
                for (Ingredient item: fetchedRecipe.getExtendedIngredients()) {
                    cookingInstruction.add(item.getOriginal());
                    ingredientList.add(item.getAmount() + " " + item.getUnit() + " " + item.getName());
                    Log.d(TAG, "Ingredients to shop" + item.getAmount() + " " + item.getUnit() + " " + item.getName());
                }
                Log.d(TAG, "Number of ingredients fetched" + cookingInstruction.size());
                cookingInstruction.add("INSTRUCTIONS");
                recycleViewAdapter.notifyDataSetChanged();

                if (!fetchedRecipe.getAnalyzedInstructions().isEmpty()) {
                    for (Step step: fetchedRecipe.getAnalyzedInstructions().get(0).getSteps()) {
                        Log.d(TAG, "Check cooking instructions: " + step.getNumber() + " " + step.getStep());
                        cookingInstruction.add(step.getNumber() + ". " + step.getStep());
                        recycleViewAdapter.notifyDataSetChanged();
                    }
                }

                for (int i = 0; i < ingredientList.size(); i++) {
                    for (int z = 0; z < alreadyHave.size(); z++) {
                        if (ingredientList.get(i).toLowerCase().contains(alreadyHave.get(z).toLowerCase())) {
                            ingredientList.remove(ingredientList.get(i));
                        }
                    }
                }
                setAddToShop();
            }

            @Override
            public void onFailure(Call < Recipe > call, Throwable t) {
                Log.d(TAG, "Error showing recipe");
            }
        });

    }

    /**
     * Add ingredients in the recipe that user don't have to a shopping list
     */
    public void setAddToShop() {
        addToShop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(RecipeSelected.this);
                builder.setTitle("Do you want to add the recipe to your shopping list?")
                        .setPositiveButton("Cancel", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialogInterface, int i) {

                            }
                        })
                        .setNegativeButton("Yes", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialogInterface, int i) {
                                Intent intent = new Intent(getApplicationContext(), ShoppingList.class);
                                CollectionReference df = db.collection("shopping").document(user_id).collection("IngredientList");
                                for (int x = 0; x < ingredientList.size(); x++) {
                                    Map < String, Object > shop = new HashMap < > ();
                                    shop.put("item", ingredientList.get(x));
                                    df.add(shop);
                                }
                                startActivity(intent);
                            }
                        });

                builder.create().show();
            }
        });
    }
}