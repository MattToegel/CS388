package njit.mt.whackaquack.ui.history;

import android.util.Log;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;

import njit.mt.whackaquack.data.model.ScoreData;

public class HistoryListAdapter extends ListAdapter<ScoreData, HistoryViewHolder> {

    public HistoryListAdapter(@NonNull DiffUtil.ItemCallback<ScoreData> diffCallback) {
        super(diffCallback);
    }

    @Override
    public HistoryViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return HistoryViewHolder.create(parent);
    }

    @Override
    public void onBindViewHolder(HistoryViewHolder holder, int position) {
        ScoreData current = getItem(position);
        Log.v("HistoryListAdapter", current.getId());
        holder.bind(current.getUid(), current.getScore(), current.getCreated());
    }

    static class ScoreDiff extends DiffUtil.ItemCallback<ScoreData> {

        @Override
        public boolean areItemsTheSame(@NonNull ScoreData oldItem, @NonNull ScoreData newItem) {
            return oldItem == newItem;
        }

        @Override
        public boolean areContentsTheSame(@NonNull ScoreData oldItem, @NonNull ScoreData newItem) {
            return oldItem.getId().equals(newItem.getId());
        }
    }
}