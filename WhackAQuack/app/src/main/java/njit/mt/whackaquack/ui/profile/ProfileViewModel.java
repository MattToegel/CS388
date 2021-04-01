package njit.mt.whackaquack.ui.profile;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import njit.mt.whackaquack.data.LoginRepository;
import njit.mt.whackaquack.data.Result;
import njit.mt.whackaquack.data.model.LoggedInUser;

public class ProfileViewModel extends ViewModel {
    LoginRepository loginRepository;

    private MutableLiveData<String> displayName = new MutableLiveData<>();
    private MutableLiveData<String> phoneNumber = new MutableLiveData<>();
    private MutableLiveData<String> email = new MutableLiveData<>();

    public ProfileViewModel(LoginRepository loginRepository) {
        this.loginRepository = loginRepository;
        LoggedInUser user = this.loginRepository.getUser();
        displayName.setValue(user.getDisplayName());
        email.setValue((user.getEmail()));
        phoneNumber.setValue(user.getPhoneNumber());
    }


    public LiveData<String> getDisplayName(){return displayName;}

    public LiveData<String> getPhoneNumber(){return phoneNumber;}

    public LiveData<String> getEmail(){return email;}

    public void saveChanges(String displayName, String email, String phoneNumber){
        loginRepository.updateProfile(displayName, email, phoneNumber, null, (user)->{
            Log.v("ProfileViewModel", "saved profile");
            LoggedInUser data = ((Result.Success<LoggedInUser>) user).getData();
            this.displayName.setValue(data.getDisplayName());
            this.email.setValue(data.getEmail());
            this.phoneNumber.setValue(data.getPhoneNumber());
        },(error)->{
            Log.e("ProfileViewModel", "error saving profile");
        });
    }
}