package njit.mt.roomssandbox.db.entity;
import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class UserEntity {
    @PrimaryKey(autoGenerate = true)
    public int uid;

    @ColumnInfo(name = "first_name")
    public String firstName;

    @ColumnInfo(name = "last_name")
    public String lastName;

    public String getName() {
        return lastName + ", " + firstName;
    }

    public UserEntity(@NonNull String firstName, @NonNull String lastName) {

        this.firstName = firstName;
        this.lastName = lastName;
    }
}
