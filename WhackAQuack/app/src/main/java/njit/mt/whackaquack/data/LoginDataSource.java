package njit.mt.whackaquack.data;

import android.os.Build;
import android.util.Log;

import androidx.annotation.RequiresApi;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;

import java.util.function.Consumer;

import njit.mt.whackaquack.data.model.LoggedInUser;

/**
 * Class that handles authentication w/ login credentials and retrieves user information.
 */
public class LoginDataSource {
    private LoggedInUser user;
    private FirebaseAuth mAuth;
    private static final String TAG = "LoginDataSource";

    public LoginDataSource() {
        mAuth = FirebaseAuth.getInstance();
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public void login(String username, String password, Consumer<Result<LoggedInUser>> success, Consumer<Result.Error> error) {
        mAuth.signInWithEmailAndPassword(username, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        // Sign in success, update UI with the signed-in user's information
                        Log.d(TAG, "signInWithEmail:success");
                        FirebaseUser fbUser = mAuth.getCurrentUser();
                        //Note: this is just an example, if you're doing FB likely you'll want to
                        //replace the whole user object
                        user = new LoggedInUser(
                                fbUser.getUid(), fbUser.getDisplayName());
                        success.accept(new Result.Success<>(user));
                    } else {
                        // If sign in fails, display a message to the user.
                        Log.w(TAG, "signInWithEmail:failure", task.getException());
                        String errorCode = "unhandled";

                        if (task.getException().getMessage().contains("There is no user record")) {
                            errorCode = "auth/user-not-found";//keep it consistent with the expected
                            //message
                        }
                        error.accept(new Result.Error(errorCode, new Exception(task.getException().getMessage())));

                    }
                });
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public void register(String email, String username, String password, Consumer<Result<LoggedInUser>> success, Consumer<Result.Error> error) {
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        // Sign in success, update UI with the signed-in user's information
                        Log.d(TAG, "createUserWithEmail:success");
                        FirebaseUser fbUser = mAuth.getCurrentUser();
                        //Note: this is just an example, if you're doing FB likely you'll want to
                        //replace the whole user object

                        UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                                .setDisplayName(username)

                                .build();

                        fbUser.updateProfile(profileUpdates)
                                .addOnCompleteListener(task1 -> {
                                    if (task1.isSuccessful()) {
                                        Log.d(TAG, "User profile updated.");
                                        user = new LoggedInUser(
                                                fbUser.getUid(), username);
                                        success.accept(new Result.Success<>(user));
                                    }
                                });
                    } else {
                        // If sign in fails, display a message to the user.
                        Log.w(TAG, "createUserWithEmail:failure", task.getException());
                        error.accept(new Result.Error("login-failed", new Exception(task.getException().getMessage())));
                    }
                });
    }

    public void logout() {
        // TODO: revoke authentication
    }
}