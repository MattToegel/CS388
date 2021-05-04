package njit.mt.whackaquack.ui.shop;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import njit.mt.whackaquack.data.DataStoreDataSource;
import njit.mt.whackaquack.data.DataStoreRepository;
import njit.mt.whackaquack.data.LoginDataSource;
import njit.mt.whackaquack.data.LoginRepository;

/**
 * ViewModel provider factory to instantiate LoginViewModel.
 * Required given LoginViewModel has a non-empty constructor
 */
public class ShopViewModelFactory implements ViewModelProvider.Factory {
    @NonNull
    private final Application application;


    public ShopViewModelFactory(@NonNull Application application) {
        this.application = application;
    }
    @NonNull
    @Override
    @SuppressWarnings("unchecked")
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if (modelClass.isAssignableFrom(ShopViewModel.class)) {
            return (T) new ShopViewModel(
                    LoginRepository.getInstance(new LoginDataSource(), application),
                    DataStoreRepository.getInstance(new DataStoreDataSource(), application));
        } else {
            throw new IllegalArgumentException("Unknown ViewModel class");
        }
    }
}