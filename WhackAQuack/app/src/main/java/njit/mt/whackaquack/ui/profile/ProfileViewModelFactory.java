package njit.mt.whackaquack.ui.profile;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import njit.mt.whackaquack.data.LoginDataSource;
import njit.mt.whackaquack.data.LoginRepository;

/**
 * ViewModel provider factory to instantiate LoginViewModel.
 * Required given LoginViewModel has a non-empty constructor
 */
public class ProfileViewModelFactory implements ViewModelProvider.Factory {
    @NonNull
    private final Application application;


    public ProfileViewModelFactory(@NonNull Application application) {
        this.application = application;
    }
    @NonNull
    @Override
    @SuppressWarnings("unchecked")
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if (modelClass.isAssignableFrom(ProfileViewModel.class)) {
            return (T) new ProfileViewModel(LoginRepository.getInstance(new LoginDataSource(), application));
        } else {
            throw new IllegalArgumentException("Unknown ViewModel class");
        }
    }
}