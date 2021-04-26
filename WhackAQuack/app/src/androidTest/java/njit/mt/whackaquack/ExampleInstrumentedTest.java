package njit.mt.whackaquack;

import android.content.Context;
import android.util.Log;

import androidx.test.core.app.ApplicationProvider;
import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;
import java.util.List;

import njit.mt.whackaquack.data.DataStoreDataSource;
import njit.mt.whackaquack.data.Result;
import njit.mt.whackaquack.data.ScoreDataSource;
import njit.mt.whackaquack.data.model.Data;
import njit.mt.whackaquack.data.model.ScoreData;

import static org.junit.Assert.*;

/**
 * Instrumented test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class ExampleInstrumentedTest {
    private Context context = ApplicationProvider.getApplicationContext();
    static Data d = new Data(null, null);
    static String key = "0Qs9mdULIyVXfGkESkr0";
    @Test
    public void useAppContext() {
        // Context of the app under test.
        Context appContext = InstrumentationRegistry.getInstrumentation().getTargetContext();
        assertEquals("Packages is expected package", "njit.mt.whackaquack", appContext.getPackageName());
        System.out.println("Doesn't work");
    }
    @Test
    public void saveGenericData() throws JSONException {
        DataStoreDataSource ds = new DataStoreDataSource();

        d = new Data(key, new JSONObject("{test:'test2'}"));
        d.setAppKey("njit.mt.test");//context.getPackageName());
        d.setUid("mt85");
        Data rd = new Data(null,null);
        final Object sync = new Object();
        ds.setData(d, (Result<Data> success)->{

            Data dr = ((Result.Success<Data>) success).getData();
            rd.setValue(dr.getValue());
            key = dr.getKey();
            synchronized (sync){
                sync.notify();
            }
        }, (Result.Error error)->{
            System.out.println( error.getError().getMessage());

            assertFalse("Error should not occur",true);
            synchronized (sync){
                sync.notify();
            }
        });

        synchronized (sync){
            try {
                sync.wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        assertEquals("Fetched data matches saved data", d.getValue().toString(), rd.getValue().toString());

    }
    @Test
    public void getGenericData() throws JSONException {
        DataStoreDataSource ds = new DataStoreDataSource();
        final Object sync = new Object();
        d.setKey(key);
        //System.out.print(d.getValue().toString());
        d.setUid("mt85");
        d.setValue(new JSONObject("{test:'test2'}"));
        d.setAppKey("njit.mt.test");//context.getPackageName());
        Data rd = new Data(null,null);
        ds.getData(d,(Result<Data> success)->{
            Data d = ((Result.Success<Data>) success).getData();
            rd.setValue(d.getValue());
            synchronized (sync){
                sync.notify();
            }

        }, (Result.Error error)->{
            System.out.println( error.getCode());
            assertFalse("Error should not occur" + error.toString(),true);
            synchronized (sync){
                sync.notify();
            }
        });
        synchronized (sync){
            try {
                sync.wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        assertEquals("Fetched data matches saved data", d.getValue().toString(), rd.getValue().toString());
    }
    @Test
    public void topScore_isCorrect(){
        ScoreDataSource ds = new ScoreDataSource();
        System.out.println(context.getPackageName());
        ScoreData sd = new ScoreData(context.getPackageName() + ".test", null, -1, null, null);

        List<ScoreData> rd = new ArrayList<>();
        final Object sync = new Object();
        ds.getScores(sd, (Result<List<ScoreData>> result) -> {
            List<ScoreData> s = ((Result.Success<List<ScoreData>>) result).getData();
            rd.addAll(s);
            synchronized (sync){
                sync.notify();
            }

        }, (Result.Error err) -> {
            //TODO do something with error
            Log.e("ScoreRepository", err.getCode() + " - " + err.getError().getMessage());
            synchronized (sync){
                sync.notify();
            }
        });
        synchronized (sync){
            try {
                sync.wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        assertTrue("Scores are invalid", rd.get(0).getScore() >= rd.get(1).getScore());
    }
}