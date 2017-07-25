package `in`.nowke.expensa.models

import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey
import android.arch.persistence.room.ColumnInfo
import android.text.format.DateUtils
import java.util.*

/**
 * User model
 */
@Entity(tableName = "user")
class User {
    @PrimaryKey(autoGenerate = true)
    var id: Int = 0
    var name: String = ""
}