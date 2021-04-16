package njit.mt.whackaquack.data;

import android.content.Context;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import java.util.List;

import njit.mt.whackaquack.data.model.ScoreData;
import njit.mt.whackaquack.data.model.Stats;

/**
 * Class that requests authentication and user information from the remote data source and
 * maintains an in-memory cache of login status and user credentials information.
 */
public class ScoreRepository {

    private static volatile ScoreRepository instance;

    private final ScoreDataSource dataSource;
    // If user credentials will be cached in local storage, it is recommended it be encrypted
    // @see https://developer.android.com/training/articles/keystore
    //NOTE: not necessarily the best way to do this
    private MutableLiveData<List<ScoreData>> scores = new MutableLiveData<List<ScoreData>>();

    public void queryScore(String appKey) {
        queryScore(appKey, null);
    }

    public void queryScore(String appKey, String uid) {
        queryScores(appKey, uid, uid);
    }

    public void queryScores(String appKey, String uid, String startsAfter) {
        ScoreData sd = new ScoreData(appKey, uid, -1, null, startsAfter);
        dataSource.getScores(sd, (Result<List<ScoreData>> result) -> {
            List<ScoreData> s = ((Result.Success<List<ScoreData>>) result).getData();
            scores.setValue(s);
            //scores = new MutableLiveData<List<ScoreData>>(s);

        }, (Result.Error err) -> {
            //TODO do something with error
            Log.e("ScoreRepository", err.getCode() + " - " + err.getError().getMessage());
        });
    }

    public LiveData<List<ScoreData>> getScores() {
        return scores;
    }

    public void saveScore(ScoreData score) {//TODO: maybe want to pass function here in the future
        dataSource.saveScore(score, (Result<Stats> result) -> {
                    Stats sd = ((Result.Success<Stats>) result).getData();
                    Log.v("Saved score result", sd.toString());
                },
                (Result.Error err) -> {
                    Log.e("ScoreRepository", err.getCode() + " - " + err.getError().getMessage());
                });
    }

    // private constructor : singleton access
    private ScoreRepository(ScoreDataSource dataSource) {
        this.dataSource = dataSource;
    }

    public static ScoreRepository getInstance(ScoreDataSource dataSource, Context context) {
        if (instance == null) {
            instance = new ScoreRepository(dataSource);
        }
        return instance;
    }

}