package com.js.nowakelock.data.sp

import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences
import android.os.FileObserver
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.edit
import com.js.nowakelock.BasicApp
import com.js.nowakelock.BuildConfig
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.io.File


class SP {
    companion object {
        private val preferencesFileName = BuildConfig.APPLICATION_ID + "_preferences"

        private var pref: SharedPreferences = BasicApp.context.createDeviceProtectedStorageContext()
            .getSharedPreferences(preferencesFileName, AppCompatActivity.MODE_PRIVATE)

        private var instance = SP()
        fun getInstance() = instance

        @SuppressLint("SetWorldReadable", "SetWorldWritable")
        fun fixPermissionsAsync(context: Context) {
            GlobalScope.launch(Dispatchers.IO) {
                try {
                    Thread.sleep(500)
                } catch (t: Throwable) {
                }
                val pkgFolder =
                    context.createDeviceProtectedStorageContext().filesDir.parentFile
                if (pkgFolder.exists()) {
                    pkgFolder.setExecutable(true, false)
                    pkgFolder.setReadable(true, false)
                    //pkgFolder.setWritable(true, false);
                    val sharedPrefsFolder =
                        File(pkgFolder.absolutePath + "/shared_prefs")
                    if (sharedPrefsFolder.exists()) {
                        sharedPrefsFolder.setExecutable(true, false)
                        sharedPrefsFolder.setReadable(true, false)
                        //sharedPrefsFolder.setWritable(true, false);
                        val f =
                            File(sharedPrefsFolder.absolutePath + "/" + preferencesFileName + ".xml")
                        if (f.exists()) {
                            f.setReadable(true, false)
                            f.setExecutable(true, false)
                            //f.setWritable(true, false);
                        }
                    }
                }
            }
        }
    }

    fun fixPermissionsAsync() = Companion.fixPermissionsAsync(BasicApp.context)

    fun getFlag(wN: String): Boolean {
        val tmp = pref.getBoolean(wN, true)
        fixPermissionsAsync()
        return tmp
    }

    fun setFlag(wN: String, flag: Boolean) {
        pref.edit(commit = true) {
            putBoolean(wN, flag)
        }
        fixPermissionsAsync()
    }

    fun startFileObserver() {
        val mFileObserver = object : FileObserver(
            BasicApp.context.createDeviceProtectedStorageContext().dataDir
                .toString() + "/shared_prefs",
            ATTRIB or CLOSE_WRITE
        ) {
            override fun onEvent(event: Int, path: String?) {
                fixPermissionsAsync()
            }
        }
        mFileObserver.startWatching()
    }

}