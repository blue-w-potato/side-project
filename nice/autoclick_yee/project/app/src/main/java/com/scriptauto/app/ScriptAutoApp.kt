package com.scriptauto.app

import android.app.Application
import androidx.room.Room
import com.scriptauto.app.data.AppDatabase
import com.scriptauto.app.data.ScriptRepository

class ScriptAutoApp : Application() {

    lateinit var repository: ScriptRepository
        private set

    override fun onCreate() {
        super.onCreate()
        val db = Room.databaseBuilder(this, AppDatabase::class.java, "script_auto.db").build()
        repository = ScriptRepository(db.scriptDao())
    }
}
