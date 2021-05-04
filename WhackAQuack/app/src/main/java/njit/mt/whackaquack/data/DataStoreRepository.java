package njit.mt.whackaquack.data;

import android.content.Context;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;
import java.util.function.Consumer;

import njit.mt.whackaquack.data.model.Data;
import njit.mt.whackaquack.data.model.ScoreData;

/**
 * Class that requests authentication and user information from the remote data source and
 * maintains an in-memory cache of login status and user credentials information.
 */
public class DataStoreRepository {

    private static volatile DataStoreRepository instance;

    private final DataStoreDataSource dataSource;
    private MutableLiveData<Data> lastData = new MutableLiveData<>();

    public LiveData<Data> getLastData() {
        return lastData;
    }

    // If user credentials will be cached in local storage, it is recommended it be encrypted
    // @see https://developer.android.com/training/articles/keystore
    //NOTE: not necessarily the best way to do this
    private MutableLiveData<List<ScoreData>> scores = new MutableLiveData<List<ScoreData>>();

    public void setData(String appKey, String key, String value, String uid, Consumer<Result<Data>> success, Consumer<Result.Error> error) {
        Data d = null;
        try {
            d = new Data(key, new JSONObject(value));
            d.setUid(uid);
            d.setAppKey(appKey);
            Log.v("setting data", d.toString());
            dataSource.setData(d, (Result<Data> s) -> {
                Data rd = ((Result.Success<Data>) s).getData();
                lastData.setValue(rd);
                success.accept(s);
            }, error);
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    public void getData(String appKey, String key, String uid, Consumer<Result<Data>> success, Consumer<Result.Error> error) {
        Data d = new Data(key, null);
        d.setUid(uid);
        d.setAppKey(appKey);
        dataSource.getData(d, (Result<Data> s) -> {
            Data rd = ((Result.Success<Data>) s).getData();
            lastData.setValue(rd);
            success.accept(s);
        }, error);

    }


    // private constructor : singleton access
    private DataStoreRepository(DataStoreDataSource dataSource) {
        this.dataSource = dataSource;
    }

    public static DataStoreRepository getInstance(DataStoreDataSource dataSource, Context context) {
        if (instance == null) {
            instance = new DataStoreRepository(dataSource);
        }
        return instance;
    }

}