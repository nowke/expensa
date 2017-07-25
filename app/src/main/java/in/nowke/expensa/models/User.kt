package `in`.nowke.expensa.models

import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey

/**
 * User model
 */
@Entity(tableName = "user")
class User {
    @PrimaryKey(autoGenerate = true)
    var id: Int = 0
    var name: String = ""
}