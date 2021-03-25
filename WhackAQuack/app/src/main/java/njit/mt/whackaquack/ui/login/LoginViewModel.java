package njit.mt.whackaquack.ui.login;

import androidx.annotation.RequiresApi;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import android.os.Build;
import android.util.Log;
import android.util.Patterns;

import njit.mt.whackaquack.data.LoginRepository;
import njit.mt.whackaquack.data.Result;
import njit.mt.whackaquack.data.model.LoggedInUser;
import njit.mt.whackaquack.R;

public class LoginViewModel extends ViewModel {

    private MutableLiveData<LoginFormState> loginFormState = new MutableLiveData<>();
    private MutableLiveData<LoginResult> loginResult = new MutableLiveData<>();
    private LoginRepository authRepository;
    LoginViewModel(LoginRepository authRepository) {
        this.authRepository = authRepository;
    }

    LiveData<LoginFormState> getLoginFormState() {
        return loginFormState;
    }

    LiveData<LoginResult> getLoginResult() {
        return loginResult;
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public void login(String username, String password) {
        // can be launched in a separate asynchronous job

        //in this example this login function will handle both registration and login
        //you can change it to have separate views for login/register if you wish
        authRepository.login(username, password, (Result<LoggedInUser> success)->{
            //success callback
            Log.v(this.getClass().getSimpleName(), "login success");
            LoggedInUser data = ((Result.Success<LoggedInUser>) success).getData();
            loginResult.setValue(new LoginResult(new LoggedInUserView(data.getDisplayName())));
        }, (Result.Error error)->{
            //error callback
            Log.v(this.getClass().getSimpleName(), error.getError().getMessage());
            loginResult.setValue(new LoginResult(R.string.login_failed));
        });
    }

    public void loginDataChanged(String username, String password) {
        if (!isUserNameValid(username)) {
            loginFormState.setValue(new LoginFormState(R.string.invalid_username, null));
        } else if (!isPasswordValid(password)) {
            loginFormState.setValue(new LoginFormState(null, R.string.invalid_password));
        } else {
            loginFormState.setValue(new LoginFormState(true));
        }
    }

    // A placeholder username validation check
    private boolean isUserNameValid(String username) {
        if (username == null) {
            return false;
        }
        if (username.contains("@")) {
            return Patterns.EMAIL_ADDRESS.matcher(username).matches();
        } else {
            return !username.trim().isEmpty();
        }
    }

    // A placeholder password validation check
    private boolean isPasswordValid(String password) {
        return password != null && password.trim().length() > 7;
    }
}

