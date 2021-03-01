package njit.mt.followme.db.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

public class ScoreViewModelFactory extends ViewModelProvider.NewInstanceFactory {

    @NonNull
    private final Application application;


    public ScoreViewModelFactory(@NonNull Application application) {
        this.application = application;
    }

    @NonNull
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if (modelClass == ScoreViewModel.class) {
            return (T) new ScoreViewModel(application);
        }
        return null;
    }
}