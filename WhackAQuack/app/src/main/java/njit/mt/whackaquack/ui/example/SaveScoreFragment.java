package njit.mt.whackaquack.ui.example;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import njit.mt.whackaquack.R;

public class SaveScoreFragment extends Fragment {

    private SaveScoreViewModel saveScoreViewModel;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        saveScoreViewModel =

        new ViewModelProvider(this, new SaveScoreViewModelFactory(getActivity().getApplication()))
                .get(SaveScoreViewModel.class);
        View root = inflater.inflate(R.layout.fragment_save_score, container, false);


        final TextView scoreView = root.findViewById(R.id.editScoreField);


        final Button btn = root.findViewById(R.id.saveScoreButton);
        btn.setOnClickListener((view)->{
            saveScoreViewModel.saveScore(scoreView.getText().toString());
        });
        return root;
    }

}