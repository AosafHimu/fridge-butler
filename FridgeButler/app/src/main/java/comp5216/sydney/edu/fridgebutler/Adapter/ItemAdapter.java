package comp5216.sydney.edu.fridgebutler.Adapter;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import java.util.ArrayList;

import comp5216.sydney.edu.fridgebutler.R;

/**
 * Custom adapter for List View on Main Activity
 * to show the users' items and theirs expiry date
 */

public class ItemAdapter extends ArrayAdapter < Item > {
    ArrayList < Item > listItem;

    //Constructor
    public ItemAdapter(@NonNull Context context, ArrayList < Item > arrayList) {
        super(context, 0, arrayList);
        listItem = arrayList;
    }

    //Populating item data to its own view
    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        View itemView = convertView;

        if (itemView == null) {
            itemView = LayoutInflater.from(getContext()).inflate(R.layout.adapter_listview, parent, false);
        }
        Item currentItem = getItem(position);

        TextView itemName = itemView.findViewById(R.id.itemName);
        itemName.setText(currentItem.getName());

        TextView expiry = itemView.findViewById(R.id.expiryDate);
        expiry.setText(currentItem.getExpiryDate());

        return itemView;
    }

}