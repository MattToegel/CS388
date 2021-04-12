package njit.mt.whackaquack.ui.example;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import njit.mt.whackaquack.data.LoginDataSource;
import njit.mt.whackaquack.data.LoginRepository;
import njit.mt.whackaquack.data.ScoreDataSource;
import njit.mt.whackaquack.data.ScoreRepository;

/**
 * ViewModel provider factory to instantiate LoginViewModel.
 * Required given LoginViewModel has a non-empty constructor
 */
public class SaveScoreViewModelFactory implements ViewModelProvider.Factory {
    @NonNull
    private final Application application;


    public SaveScoreViewModelFactory(@NonNull Application application) {
        this.application = application;
    }
    @NonNull
    @Override
    @SuppressWarnings("unchecked")
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if (modelClass.isAssignableFrom(SaveScoreViewModel.class)) {
            return (T) new SaveScoreViewModel(ScoreRepository.getInstance(new ScoreDataSource(), application),LoginRepository.getInstance(new LoginDataSource(), application));
        } else {
            throw new IllegalArgumentException("Unknown ViewModel class");
        }
    }
}