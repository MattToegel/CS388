package com.ethereallab.tapitscores

import android.app.Application
import com.ethereallab.tapitscores.database.AppDatabase

class TapGameApplication : Application() {
    val database: AppDatabase by lazy { AppDatabase.getDatabase(this) }
}