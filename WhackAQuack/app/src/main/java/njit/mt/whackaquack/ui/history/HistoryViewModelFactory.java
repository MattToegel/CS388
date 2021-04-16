package njit.mt.whackaquack.ui.history;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import njit.mt.whackaquack.data.LoginDataSource;
import njit.mt.whackaquack.data.LoginRepository;
import njit.mt.whackaquack.data.ScoreDataSource;
import njit.mt.whackaquack.data.ScoreRepository;
import njit.mt.whackaquack.ui.history.HistoryViewModel;
import njit.mt.whackaquack.ui.profile.ProfileViewModel;

/**
 * ViewModel provider factory to instantiate LoginViewModel.
 * Required given LoginViewModel has a non-empty constructor
 */
public class HistoryViewModelFactory implements ViewModelProvider.Factory {
    @NonNull
    private final Application application;


    public HistoryViewModelFactory(@NonNull Application application) {
        this.application = application;
    }
    @NonNull
    @Override
    @SuppressWarnings("unchecked")
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if (modelClass.isAssignableFrom(HistoryViewModel.class)) {
            return (T) new HistoryViewModel(ScoreRepository.getInstance(new ScoreDataSource(), application));
        } else {
            throw new IllegalArgumentException("Unknown ViewModel class");
        }
    }
}