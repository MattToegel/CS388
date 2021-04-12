package njit.mt.whackaquack.ui.history;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import njit.mt.whackaquack.R;

public class HistoryViewHolder extends RecyclerView.ViewHolder {
    private final TextView nameView, scoreView, dateView;
    private final static String TAG = "ScoreViewHolder";

    private HistoryViewHolder(View itemView) {
        super(itemView);
        nameView = itemView.findViewById(R.id.nameView);
        scoreView = itemView.findViewById(R.id.scoreView);
        dateView = itemView.findViewById(R.id.dateView);
    }

    public void bind(String name, int score, String date) {
        Log.v(TAG, "name " + name);
        Log.v(TAG, "Score " + score);
        Log.v(TAG, "Created " + date);
        nameView.setText(name);
        scoreView.setText("" + score);
        dateView.setText(date);
    }

    static HistoryViewHolder create(ViewGroup parent) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.scoreview_item, parent, false);
        return new HistoryViewHolder(view);
    }
}
