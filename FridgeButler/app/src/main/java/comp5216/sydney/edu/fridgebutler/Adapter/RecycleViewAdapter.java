package comp5216.sydney.edu.fridgebutler.Adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;
import comp5216.sydney.edu.fridgebutler.R;

/***************************************************************************************
 *    Title:  Android Developers Guide
 *    Author: n/A
 *    Date:  2021-10-12
 *    Code version: N/A
 *    Availability: https://developer.android.com/guide/topics/ui/layout/recyclerview
 *
 ***************************************************************************************/

/**
 * Custom Adapter for recycle view in RecipeSelected class
 *
 */
public class RecycleViewAdapter extends RecyclerView.Adapter < RecycleViewAdapter.ViewHolder > {

    private List < String > recipeInfo;

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView textView;

        public ViewHolder(View view) {
            super(view);
            textView = (TextView) view.findViewById(R.id.textView);
        }

        public TextView getTextView() {
            return textView;
        }

    }

    /**
     * Initialize the information to the Adapter.
     *
     * @param recipeInfo List<String> containing the data to populate views to be used
     * by RecyclerView.
     */
    public RecycleViewAdapter(List < String > recipeInfo) {
        this.recipeInfo = recipeInfo;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup v, int viewType) {
        // Create a new view, which defines the UI of the list item
        View view = LayoutInflater.from(v.getContext())
                .inflate(R.layout.text_row_item, v, false);

        return new ViewHolder(view);
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(ViewHolder viewHolder, final int position) {

        // Get element from your dataset at this position and replace the
        // contents of the view with that element
        viewHolder.getTextView().setText(recipeInfo.get(position));
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return recipeInfo.size();
    }
}