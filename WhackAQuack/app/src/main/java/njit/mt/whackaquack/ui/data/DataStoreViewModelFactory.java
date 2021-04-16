package njit.mt.whackaquack.ui.data;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import njit.mt.whackaquack.data.DataStoreDataSource;
import njit.mt.whackaquack.data.DataStoreRepository;
import njit.mt.whackaquack.data.LoginDataSource;
import njit.mt.whackaquack.data.LoginRepository;
import njit.mt.whackaquack.ui.example.SaveScoreViewModel;

public class DataStoreViewModelFactory implements ViewModelProvider.Factory {
    @NonNull
    private final Application application;


    public DataStoreViewModelFactory(@NonNull Application application) {
        this.application = application;
    }
    @NonNull
    @Override
    @SuppressWarnings("unchecked")
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if (modelClass.isAssignableFrom(DataStoreViewModel.class)) {
            return (T) new DataStoreViewModel(DataStoreRepository.getInstance(new DataStoreDataSource(), application), LoginRepository.getInstance(new LoginDataSource(), application));
        } else {
            throw new IllegalArgumentException("Unknown ViewModel class");
        }
    }
}