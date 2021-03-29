package njit.mt.followme;

import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;

import njit.mt.followme.db.entity.ScoreEntity;


public class ScoreListAdapter extends ListAdapter<ScoreEntity, ScoreViewHolder> {

    public ScoreListAdapter(@NonNull DiffUtil.ItemCallback<ScoreEntity> diffCallback) {
        super(diffCallback);
    }

    @Override
    public ScoreViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return ScoreViewHolder.create(parent);
    }

    @Override
    public void onBindViewHolder(ScoreViewHolder holder, int position) {
        ScoreEntity current = getItem(position);
        holder.bind(current.name, current.score, current.created);
    }

    static class UserDiff extends DiffUtil.ItemCallback<ScoreEntity> {

        @Override
        public boolean areItemsTheSame(@NonNull ScoreEntity oldItem, @NonNull ScoreEntity newItem) {
            return oldItem == newItem;
        }

        @Override
        public boolean areContentsTheSame(@NonNull ScoreEntity oldItem, @NonNull ScoreEntity newItem) {
            return oldItem.getData().equals(newItem.getData());
        }
    }
}
