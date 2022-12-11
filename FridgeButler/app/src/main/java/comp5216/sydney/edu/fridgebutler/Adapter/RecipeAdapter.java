package comp5216.sydney.edu.fridgebutler.Adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import comp5216.sydney.edu.fridgebutler.R;
import comp5216.sydney.edu.fridgebutler.Recipe.Model.Recipe;

/**
 * Custom array adapter for listing recipe in RecipeRecommendation
 */
public class RecipeAdapter extends ArrayAdapter < Recipe > {

    //Constructor
    public RecipeAdapter(@NonNull Context context, List < Recipe > recipeList) {
        super(context, 0, recipeList);
    }

    //Populating individual recipe to View
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        View currentItemView = convertView;

        if (currentItemView == null) {
            currentItemView = LayoutInflater.from(getContext()).inflate(R.layout.recipe_adapter_view, parent, false);
        }

        Recipe recipePosition = getItem(position);

        //Load image to ImageView using Picasso
        String imageURL = recipePosition.getImage();
        Log.d(RecipeAdapter.class.getSimpleName(), imageURL);

        ImageView recipeImageView = currentItemView.findViewById(R.id.recipePic);
        assert recipePosition != null;

        ExecutorService executor = Executors.newSingleThreadExecutor();
        Handler handler = new Handler(Looper.getMainLooper());
        executor.execute(new Runnable() {
            @Override
            public void run() {
                URL url = null;
                try {
                    url = new URL(imageURL);
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                }
                Bitmap bitmap = null;
                try {
                     bitmap =  BitmapFactory.decodeStream(url.openConnection().getInputStream());
                } catch (IOException e) {
                    e.printStackTrace();
                }

                Bitmap finalBitmap = bitmap;
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        recipeImageView.setImageBitmap(finalBitmap);
                    }
                });

            }
        });


        Picasso.with(this.getContext()).load(imageURL).into(recipeImageView);

        TextView recipeName = currentItemView.findViewById(R.id.recipeName);
        recipeName.setText(recipePosition.getTitle());

        return currentItemView;
    }
}