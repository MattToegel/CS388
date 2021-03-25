package njit.mt.roomssandbox.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

/**
 * Used for ViewModelProvider to map our UserViewModel (since we need to pass arguments; there's no default constructor)
 */
public class UserViewModelFactory extends ViewModelProvider.NewInstanceFactory {

    @NonNull
    private final Application application;


    public UserViewModelFactory(@NonNull Application application) {
        this.application = application;
    }

    @NonNull
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if (modelClass == UserViewModel.class) {
            return (T) new UserViewModel(application);
        }
        return null;
    }
}