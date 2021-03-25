package njit.mt.followme;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

public class ScoreViewHolder extends RecyclerView.ViewHolder {
    private final TextView nameView, scoreView, dateView;
    private final static String TAG = "ScoreViewHolder";

    private ScoreViewHolder(View itemView) {
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
        scoreView.setText(""+score);
        dateView.setText(date);
    }

    static ScoreViewHolder create(ViewGroup parent) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.scoreview_item, parent, false);
        return new ScoreViewHolder(view);
    }
}