package njit.mt.whackaquack.ui.stats;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import njit.mt.whackaquack.R;

public class StatsFragment extends Fragment {

    private StatsViewModel statsViewModel;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        statsViewModel =

                new ViewModelProvider(this, new StatsViewModelFactory(getActivity().getApplication()))
                        .get(StatsViewModel.class);
        View root = inflater.inflate(R.layout.fragment_stats, container, false);
        //TODO fetch stats if not available (or periodically update)
        //final TextView textView = root.findViewById(R.id.text_save_score);
        final TextView lvlView = root.findViewById(R.id.level);
        final TextView xpView = root.findViewById(R.id.experience);
        final TextView pointsView = root.findViewById(R.id.points);
        final TextView playsView = root.findViewById(R.id.plays);
        statsViewModel.getLastStats().observe(getViewLifecycleOwner(), stats -> {
            ///stats stuff
            if(stats != null){
                lvlView.setText("Lvl: "+stats.getLevel());
                xpView.setText("XP: "+stats.getExperience());
                pointsView.setText("Bills: "+stats.getPoints());
                int plays = 0;
                if(stats.getPlays().containsKey(getActivity().getPackageName()+".test")){
                    plays = stats.getPlays().get(getActivity().getPackageName()+".test");
                }
                playsView.setText(String.format("You played %s time(s)", plays));
            }
        });
        return root;
    }
}