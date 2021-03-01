package njit.mt.followme.db;

import androidx.room.TypeConverter;

import java.util.Date;

public class DateConverter {
    @TypeConverter
    public Date toDate(long timestamp){
        return (timestamp==-1L)?null:new Date(timestamp);
    }

    @TypeConverter
    public long toTimestamp(Date date){
        return date.getTime();
    }
}