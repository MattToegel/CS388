package njit.mt.whackaquack.data;

import android.os.Build;

import androidx.annotation.RequiresApi;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import java.util.function.Consumer;

import njit.mt.whackaquack.data.model.LoggedInUser;

/**
 * Class that requests authentication and user information from the remote data source and
 * maintains an in-memory cache of login status and user credentials information.
 */
public class LoginRepository {

    private static volatile LoginRepository instance;

    private final LoginDataSource dataSource;

    // If user credentials will be cached in local storage, it is recommended it be encrypted
    // @see https://developer.android.com/training/articles/keystore
    private LoggedInUser user = null;
    //NOTE: not necessarily the best way to do this
    private MutableLiveData<Boolean> isLoggedIn;
    // private constructor : singleton access
    private LoginRepository(LoginDataSource dataSource) {
        this.dataSource = dataSource;
        if(isLoggedIn == null){
            isLoggedIn = new MutableLiveData<>();
        }
        isLoggedIn.setValue(user != null);
    }

    public static LoginRepository getInstance(LoginDataSource dataSource) {
        if (instance == null) {
            instance = new LoginRepository(dataSource);
        }
        return instance;
    }
    //NOTE: not necessarily the best way to do this
    public LiveData<Boolean> isLoggedIn(){
        if(isLoggedIn == null){
            isLoggedIn = new MutableLiveData<>();
        }
        return isLoggedIn;
    }
    /*public boolean isLoggedIn() {
        return user != null;
    }*/

    public void logout() {
        user = null;
        isLoggedIn.setValue(false);
        dataSource.logout();
    }
    public LoggedInUser getUser(){
        return this.user;
    }
    private void setLoggedInUser(LoggedInUser user) {
        this.user = user;
        isLoggedIn.setValue(user != null);
        // If user credentials will be cached in local storage, it is recommended it be encrypted
        // @see https://developer.android.com/training/articles/keystore
    }
    public void updateProfile(String username, String email, String phoneNumber, String password,Consumer<Result<LoggedInUser>> success, Consumer<Result.Error> error){
        dataSource.updateProfile(this.user.getUserId(), username, email, phoneNumber, password, success, error);
    }
    @RequiresApi(api = Build.VERSION_CODES.N)
    public void login(String username, String password, Consumer<Result<LoggedInUser>> success, Consumer<Result.Error> error) {
        // handle login
        dataSource.login(username, password,
                (Result<LoggedInUser> result) -> {
                    setLoggedInUser(((Result.Success<LoggedInUser>) result).getData());
                    success.accept(result);
                },
                (Result.Error err) -> {
                    //if user is not found, attempt to register; otherwise return error
                    if (err.getCode().equals("auth/user-not-found")) {
                        register(username, username.split("@")[0], password, success, error);
                    } else {
                        error.accept(err);
                    }
                });
    }

    public void register(String email, String username, String password, Consumer<Result<LoggedInUser>> success, Consumer<Result.Error> error) {
        dataSource.register(email, username, password, (Result<LoggedInUser> result) -> {
                    setLoggedInUser(((Result.Success<LoggedInUser>) result).getData());
                    success.accept(result);
                },
                (Result.Error err) -> {
                    error.accept(err);
                });
    }
}