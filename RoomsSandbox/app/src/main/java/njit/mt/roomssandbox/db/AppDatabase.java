package njit.mt.roomssandbox.db;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.sqlite.db.SupportSQLiteDatabase;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import njit.mt.roomssandbox.db.dao.UserDao;
import njit.mt.roomssandbox.db.entity.UserEntity;

@Database(entities = {UserEntity.class}, version = 3, exportSchema = false)
public abstract class AppDatabase extends RoomDatabase {
    public abstract UserDao userDao();
    private static volatile AppDatabase INSTANCE;
    private static final int NUMBER_OF_THREADS = 4;
    private static final String database_name = "database-name";
    static final ExecutorService databaseWriteExecutor =
            Executors.newFixedThreadPool(NUMBER_OF_THREADS);
    static AppDatabase getDatabase(final Context context) {
        if (INSTANCE == null) {
            synchronized (AppDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                            AppDatabase.class, database_name)
                            .addCallback(sRoomDatabaseCallback)//determines how the data initializes
                            .fallbackToDestructiveMigration() //destroys DB on schema change
                            .build();
                }
            }
        }
        return INSTANCE;
    }
    private static RoomDatabase.Callback sRoomDatabaseCallback = new RoomDatabase.Callback() {
        @Override
        public void onCreate(@NonNull SupportSQLiteDatabase db) {
            super.onCreate(db);

            // If you want to keep data through app restarts,
            // comment out the following block
            databaseWriteExecutor.execute(() -> {
                // Populate the database in the background.
                // If you want to start with more words, just add them.
                UserDao dao = INSTANCE.userDao();
                dao.deleteAll();

                UserEntity user = new UserEntity("Bob", "Smith");
                dao.insert(user);
                user = new UserEntity("Joe", "Jones");
                dao.insert(user);
                Log.v("test", "inserted test users");
            });
        }
    };
}
