package comp5216.sydney.edu.fridgebutler.Recipe.View;

import static comp5216.sydney.edu.fridgebutler.Recipe.Controller.RecipeApi.BASE_URL;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.ArrayList;
import java.util.List;

import comp5216.sydney.edu.fridgebutler.Adapter.RecipeAdapter;
import comp5216.sydney.edu.fridgebutler.R;
import comp5216.sydney.edu.fridgebutler.Recipe.Controller.RecipeApi;
import comp5216.sydney.edu.fridgebutler.Recipe.Model.Recipe;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Suggest recipe based on the list of item that user inputs
 */
public class RecipeRecommendation extends AppCompatActivity {
    private String TAG = this.getClass().getName();

    ListView recipeListView;
    ArrayAdapter < Recipe > recipeAdapter;
    List < Recipe > recipeList;
    ArrayList < String > ingredientsInput;

    static Gson gson = new GsonBuilder().setLenient().create();
    static Retrofit retrofit = new Retrofit.Builder().baseUrl(BASE_URL).addConverterFactory(GsonConverterFactory.create(gson)).build();
    static final RecipeApi recipeApi = retrofit.create(RecipeApi.class);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe_recommendation);

        recipeList = new ArrayList < Recipe > ();
        ingredientsInput = new ArrayList < String > ();

        //set Listview and Adapter
        recipeListView = findViewById(R.id.recipeList);
        recipeAdapter = new RecipeAdapter(this, recipeList);
        recipeListView.setAdapter(recipeAdapter);

        Log.d(TAG, "Check extras received" + String.valueOf(ingredientsInput.size()));
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            ingredientsInput = extras.getStringArrayList("ingredientList");
            Log.d(TAG, "Check extras received" + String.valueOf(ingredientsInput.size()));
        }
        getRecipesID(ingredientsInput);
    }

    //Get recipe ID based on user input
    private void getRecipesID(ArrayList < String > input) {
        String dataInput = "";
        if (input.size() == 1) {
            dataInput = input.get(0);
        } else {
            for (int i = 0; i < input.size(); i++) {
                dataInput += input.get(i) + ",";
            }
//            if (input.size() > 2) {
//                dataInput += input.get(input.size() - 1);
//            }
        }

        Log.d(TAG, "Check input " + dataInput);

        Call < List < Recipe >> call = recipeApi.getRecipesID(dataInput, recipeApi.API_KEY_HEADER);
        call.enqueue(new Callback < List < Recipe >> () {
            @Override
            public void onResponse(Call < List < Recipe >> call, Response < List < Recipe >> response) {
                recipeList = response.body();

                ArrayList < String > recipeIdList = new ArrayList < > ();
                Log.d(TAG, "Check recipe retrieved " + recipeList.size());
                for (int x = 0; x < recipeList.size(); x++) {
                    recipeIdList.add(recipeList.get(x).getId());
                    Log.d(TAG, "Check recipe component" + recipeList.get(x).getTitle() + " " + recipeList.get(x).getImage());
                }

                recipeAdapter = new RecipeAdapter(getApplicationContext(), recipeList);
                recipeListView.setAdapter(recipeAdapter);

                setupListViewListener(recipeIdList, input);
            }
            @Override
            public void onFailure(Call < List < Recipe >> call, Throwable t) {
                Log.d(TAG, "Error fetching recipe");
            }
        });
    }

    //once user click on a recipe, navigate user to RecipeSelected activity
    //send recipe id with intent to launch new activity
    private void setupListViewListener(ArrayList < String > RecipeIdList, ArrayList < String > input) {
        recipeListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView < ? > adapterView, View view, int position, long rowID) {
                Intent launchRecipe = new Intent(RecipeRecommendation.this, RecipeSelected.class);
                launchRecipe.putExtra("recipeId", RecipeIdList.get(position));
                launchRecipe.putStringArrayListExtra("ingredient", input);
                startActivity(launchRecipe);
            }
        });
    }

}