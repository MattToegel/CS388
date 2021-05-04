package njit.mt.whackaquack.ui.shop;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.function.Consumer;

import njit.mt.whackaquack.R;
import njit.mt.whackaquack.data.model.Item;

public class ItemViewHolder extends RecyclerView.ViewHolder {
    private final TextView nameView, costView;
    private final Button button;
    private final static String TAG = "ItemViewholder";

    private ItemViewHolder(View itemView) {
        super(itemView);
        nameView = itemView.findViewById(R.id.nameView);
        costView = itemView.findViewById(R.id.costView);
        button = itemView.findViewById(R.id.button);
    }

    public void bind(String name, int cost, int id, Consumer<Item> callback) {
        nameView.setText(name);
        costView.setText("" + cost);
        button.setText("Buy ("+cost + ")");
        button.setOnClickListener((view)->{
            Item item = new Item();
            item.setCost(cost);
            item.setId(id);
            item.setName(name);
            if(callback != null) {
                callback.accept(item);
            }
        });
    }

    static ItemViewHolder create(ViewGroup parent) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.shop_item, parent, false);
        return new ItemViewHolder(view);
    }
}
