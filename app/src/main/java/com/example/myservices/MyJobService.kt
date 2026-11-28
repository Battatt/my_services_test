package com.example.myservices

import android.app.job.JobParameters
import android.app.job.JobService
import android.content.Intent
import android.os.PersistableBundle
import android.util.Log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.time.Duration.Companion.milliseconds

private const val TAG = ".MyJobService"


class MyJobService : JobService() {
    private val coroutineScope = CoroutineScope(Dispatchers.Main)

    override fun onCreate() {
        super.onCreate()
        log("onCreate")
    }


    override fun onDestroy() {
        super.onDestroy()
        coroutineScope.cancel()
        log("onDestroy")
    }


    override fun onStartJob(params: JobParameters?): Boolean {
        log("onStartJob")
        coroutineScope.launch {
            var workItem = params?.dequeueWork()
            while (workItem != null) {
                val page = workItem.intent?.getIntExtra(KEY_PAGE, 0) ?: 0

                for (i in 0 until 5) {
                    delay(1000.milliseconds)
                    log("Timer $i page: $page")
                }

                params?.completeWork(workItem)
                workItem = params?.dequeueWork()
            }

            jobFinished(params, false)
        }
        return true // Is job still working? ^
    }

    override fun onStopJob(p0: JobParameters?): Boolean {  // stopped by OS
        log("onStopJob")
        return true  // reschedule
    }

    private fun log(message: String) {
        Log.d(TAG, message)
    }

    companion object {
        const val JOB_ID = 111

        private const val KEY_PAGE = "page"

        fun newIntent(page: Int): Intent {
            return Intent().apply {
                putExtra(KEY_PAGE, page)
            }
        }
    }
}