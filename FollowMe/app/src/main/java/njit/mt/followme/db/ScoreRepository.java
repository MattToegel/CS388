package njit.mt.followme.db;

import android.app.Application;

import androidx.lifecycle.LiveData;

import java.util.List;

import njit.mt.followme.db.dao.ScoreDao;
import njit.mt.followme.db.entity.ScoreEntity;


public class ScoreRepository {

    private ScoreDao scoreDao;
    private LiveData<List<ScoreEntity>> top10;

    // Note that in order to unit test the WordRepository, you have to remove the Application
    // dependency. This adds complexity and much more code, and this sample is not about testing.
    // See the BasicSample in the android-architecture-components repository at
    // https://github.com/googlesamples
    public ScoreRepository(Application application) {
        AppDatabase db = AppDatabase.getDatabase(application);
        scoreDao = db.scoreDao();
        top10 = scoreDao.getTop10();
    }

    // Room executes all queries on a separate thread.
    // Observed LiveData will notify the observer when the data has changed.
    public LiveData<List<ScoreEntity>> getTop10() {
        return top10;
    }

    // You must call this on a non-UI thread or your app will throw an exception. Room ensures
    // that you're not doing any long running operations on the main thread, blocking the UI.
    public void insert(ScoreEntity score) {
        AppDatabase.databaseWriteExecutor.execute(() -> {
            scoreDao.insert(score);
        });
    }
}