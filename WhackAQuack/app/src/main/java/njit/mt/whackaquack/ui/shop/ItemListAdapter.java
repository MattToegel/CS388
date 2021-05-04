package njit.mt.whackaquack.ui.shop;

import android.util.Log;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;

import java.util.function.Consumer;

import njit.mt.whackaquack.data.model.Item;
import njit.mt.whackaquack.data.model.ScoreData;
import njit.mt.whackaquack.ui.history.HistoryViewHolder;

public class ItemListAdapter extends ListAdapter<Item, ItemViewHolder> {
    Consumer<Item> purchaseCallback;
    public ItemListAdapter(@NonNull DiffUtil.ItemCallback<Item> diffCallback, Consumer<Item> purchaseCallback) {
        super(diffCallback);
        this.purchaseCallback = purchaseCallback;
    }

    @Override
    public ItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return ItemViewHolder.create(parent);
    }

    @Override
    public void onBindViewHolder(ItemViewHolder holder, int position) {
        Item current = getItem(position);
        Log.v("ItemListAdapter", ""+current.getId());
        holder.bind(current.getName(), current.getCost(), current.getId(), purchaseCallback);
    }

    static class ItemDiff extends DiffUtil.ItemCallback<Item> {

        @Override
        public boolean areItemsTheSame(@NonNull Item oldItem, @NonNull Item newItem) {
            return oldItem == newItem;
        }

        @Override
        public boolean areContentsTheSame(@NonNull Item oldItem, @NonNull Item newItem) {
            return oldItem.getId() == newItem.getId();
        }
    }
}