package njit.mt.whackaquack.ui.stats;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import njit.mt.whackaquack.data.ScoreDataSource;
import njit.mt.whackaquack.data.ScoreRepository;
public class StatsViewModelFactory implements ViewModelProvider.Factory {
    @NonNull
    private final Application application;


    public StatsViewModelFactory(@NonNull Application application) {
        this.application = application;
    }
    @NonNull
    @Override
    @SuppressWarnings("unchecked")
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if (modelClass.isAssignableFrom(StatsViewModel.class)) {
            return (T) new StatsViewModel(ScoreRepository.getInstance(new ScoreDataSource(), application));
        } else {
            throw new IllegalArgumentException("Unknown ViewModel class");
        }
    }
}