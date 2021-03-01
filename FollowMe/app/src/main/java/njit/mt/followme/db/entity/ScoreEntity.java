package njit.mt.followme.db.entity;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverters;

import java.text.SimpleDateFormat;
import java.util.Date;

import njit.mt.followme.db.DateConverter;

@Entity(tableName = "scores")
@TypeConverters(DateConverter.class)
public class ScoreEntity {
    @PrimaryKey(autoGenerate = true)
    public int id;

    @ColumnInfo(name = "name")
    public String name;

    @ColumnInfo(name = "score")
    public int score;

    @ColumnInfo(name = "created")
    public String created;//https://medium.com/androiddevelopers/room-time-2b4cf9672b98

    public String getData(){
        return name + score + created;
    }

    public ScoreEntity(@NonNull String name, @NonNull int score) {
        this.name = name;
        this.score = score;
        //todo may be eligible to put in a function
        String formattedDate = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
        String formattedTime = new SimpleDateFormat("HH:mm").format(new Date());
        String dt = String.format("%s %s", formattedDate, formattedTime);
        this.created = dt;
    }
}
