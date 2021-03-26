package njit.mt.whackaquack.data;

import android.os.Build;

import androidx.annotation.RequiresApi;

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

    // private constructor : singleton access
    private LoginRepository(LoginDataSource dataSource) {
        this.dataSource = dataSource;
    }

    public static LoginRepository getInstance(LoginDataSource dataSource) {
        if (instance == null) {
            instance = new LoginRepository(dataSource);
        }
        return instance;
    }

    public boolean isLoggedIn() {
        return user != null;
    }

    public void logout() {
        user = null;
        dataSource.logout();
    }

    private void setLoggedInUser(LoggedInUser user) {
        this.user = user;
        // If user credentials will be cached in local storage, it is recommended it be encrypted
        // @see https://developer.android.com/training/articles/keystore
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public void login(String username, String password, Consumer<Result<LoggedInUser>> success, Consumer<Result.Error> error) {
        // handle login
        /*.login(username, password,
                (Result<LoggedInUser> result) -> {
                    setLoggedInUser(((Result.Success<LoggedInUser>) result).getData());
                    success.accept(result);
                },
                (Result.Error err) -> {
                    // error.accept(((Result.Success<LoggedInUser>) error));
                    error.accept(err);
                });*/
        dataSource.register(username, username.split("@")[0], password, (Result<LoggedInUser> result) -> {
                    setLoggedInUser(((Result.Success<LoggedInUser>) result).getData());
                    success.accept(result);
                },
                (Result.Error err) -> {
                    // error.accept(((Result.Success<LoggedInUser>) error));
                    error.accept(err);
                });
        /*Result<LoggedInUser> result = dataSource.login(username, password);
        if (result instanceof Result.Success) {
            setLoggedInUser(((Result.Success<LoggedInUser>) result).getData());
        }
        else{
            return register(username, username.split("@")[0], password);
        }
        return result;*/
    }
}