package njit.mt.roomssandbox;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

public class UserViewHolder extends RecyclerView.ViewHolder {
    private final TextView firstNameView, lastNameView;

    private UserViewHolder(View itemView) {
        super(itemView);
        firstNameView = itemView.findViewById(R.id.firstNameView);
        lastNameView = itemView.findViewById(R.id.lastNameview);
    }

    public void bind(String firstName, String lastName) {
        firstNameView.setText(firstName);
        lastNameView.setText(lastName);
    }

    static UserViewHolder create(ViewGroup parent) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.recyclerview_item, parent, false);
        return new UserViewHolder(view);
    }
}