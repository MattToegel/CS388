package njit.mt.whackaquack.ui.data;

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

public class DataStoreFragment extends Fragment {

    private DataStoreViewModel viewModel;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        viewModel =

                new ViewModelProvider(this, new DataStoreViewModelFactory(getActivity().getApplication()))
                        .get(DataStoreViewModel.class);
        View root = inflater.inflate(R.layout.fragment_datastore, container, false);


        final TextView keyView = root.findViewById(R.id.editKeyField);
        final TextView valueView = root.findViewById(R.id.editValueField);

        final TextView respView = root.findViewById(R.id.textJSON);
        final TextView errorView = root.findViewById(R.id.textError);
        viewModel.getJSON().observe(getViewLifecycleOwner(), (String j) -> {
            respView.setText(j);
        });
        viewModel.getError().observe(getViewLifecycleOwner(), (String j) -> {
            errorView.setText(j);
        });

        final Button setBtn = root.findViewById(R.id.setDataButton);
        final Button getBtn = root.findViewById(R.id.getDataButton);

        setBtn.setOnClickListener((view) -> {
            viewModel.setData(keyView.getText().toString(), valueView.getText().toString());
        });
        getBtn.setOnClickListener((view) -> {
            viewModel.getData(keyView.getText().toString());
        });
        return root;
    }
}