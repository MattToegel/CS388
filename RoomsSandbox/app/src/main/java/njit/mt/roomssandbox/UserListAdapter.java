package njit.mt.roomssandbox;

import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;

import njit.mt.roomssandbox.db.entity.UserEntity;

public class UserListAdapter extends ListAdapter<UserEntity, UserViewHolder> {

    public UserListAdapter(@NonNull DiffUtil.ItemCallback<UserEntity> diffCallback) {
        super(diffCallback);
    }

    @Override
    public UserViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return UserViewHolder.create(parent);
    }

    @Override
    public void onBindViewHolder(UserViewHolder holder, int position) {
        UserEntity current = getItem(position);
        holder.bind(current.firstName, current.lastName);
    }

    static class UserDiff extends DiffUtil.ItemCallback<UserEntity> {

        @Override
        public boolean areItemsTheSame(@NonNull UserEntity oldItem, @NonNull UserEntity newItem) {
            return oldItem == newItem;
        }

        @Override
        public boolean areContentsTheSame(@NonNull UserEntity oldItem, @NonNull UserEntity newItem) {
            return oldItem.getName().equals(newItem.getName());
        }
    }
}
