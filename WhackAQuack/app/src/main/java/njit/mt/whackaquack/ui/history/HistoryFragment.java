package njit.mt.whackaquack.ui.history;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import njit.mt.whackaquack.R;

public class HistoryFragment extends Fragment {

    private HistoryViewModel historyViewModel;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_history, container, false);

        RecyclerView recyclerView = root.findViewById(R.id.recyclerview);
        final HistoryListAdapter adapter = new HistoryListAdapter(new HistoryListAdapter.UserDiff());
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(root.getContext()));
        //Since our model has a 1 parameter constructor we need to use a factory to map it
        historyViewModel = new ViewModelProvider(this, new HistoryViewModelFactory(getActivity().getApplication())).get(HistoryViewModel.class);
        historyViewModel.getScoreHistory().observe(getViewLifecycleOwner(), scores -> {
            // Update the cached copy of the words in the adapter.
            adapter.submitList(scores);
        });
        historyViewModel.queryScore("njit.mt.test");

        /*final TextView textView = root.findViewById(R.id.text_history);
        historyViewModel.getText().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                textView.setText(s);
            }
        });*/
        return root;
    }
}