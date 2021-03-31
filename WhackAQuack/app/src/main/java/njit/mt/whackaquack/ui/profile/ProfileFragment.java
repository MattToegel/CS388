package njit.mt.whackaquack.ui.profile;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import njit.mt.whackaquack.R;
import njit.mt.whackaquack.ui.login.LoginViewModel;
import njit.mt.whackaquack.ui.login.LoginViewModelFactory;

public class ProfileFragment extends Fragment {

    private ProfileViewModel profileViewModel;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        profileViewModel =

        new ViewModelProvider(this, new ProfileViewModelFactory())
                .get(ProfileViewModel.class);
        View root = inflater.inflate(R.layout.fragment_profile, container, false);


        final TextView emailView = root.findViewById(R.id.editTextTextEmailAddress);
        profileViewModel.getEmail().observe(getViewLifecycleOwner(), s -> emailView.setText(s));

        final TextView usernameView = root.findViewById(R.id.editTextTextPersonName);
        profileViewModel.getDisplayName().observe(getViewLifecycleOwner(), s -> usernameView.setText(s));

        final TextView phoneView = root.findViewById(R.id.editTextPhone);
        profileViewModel.getPhoneNumber().observe(getViewLifecycleOwner(), s -> phoneView.setText(s));

        final Button btn = root.findViewById(R.id.button);
        btn.setOnClickListener((view)->{
            profileViewModel.saveChanges();
        });
        return root;
    }

}