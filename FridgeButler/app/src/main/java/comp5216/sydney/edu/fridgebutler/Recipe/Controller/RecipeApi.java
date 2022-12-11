package comp5216.sydney.edu.fridgebutler.Recipe.Controller;

import java.util.List;

import comp5216.sydney.edu.fridgebutler.Recipe.Model.Recipe;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

/**
 * Interface for call query to use Spoonacular API
 */
public interface RecipeApi {
    static String API_KEY_HEADER = "b7df7df29e8047609d946939003f9162";
    static String BASE_URL = "https://api.spoonacular.com/";

    //Query get recipes by ingredients
    @GET("recipes/findByIngredients?number=6")
    Call < List < Recipe >> getRecipesID(@Query("ingredients") String ingredients,
                                         @Query("apiKey") String api);

    //Query to get recipe information using recipe ID
    @GET("recipes/{id}/information")
    Call < Recipe > getRecipeDetail(@Path("id") String recipeId, @Query("apiKey") String api);

}