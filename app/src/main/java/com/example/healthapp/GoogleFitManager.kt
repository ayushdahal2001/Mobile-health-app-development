package com.example.healthmonitoring_app.android

import android.content.Context
import android.os.Parcel
import android.os.Parcelable
import com.google.android.gms.auth.api.signin.*
import com.google.android.gms.fitness.Fitness
import com.google.android.gms.fitness.data.DataType
import com.google.android.gms.fitness.request.DataReadRequest
import com.google.android.gms.tasks.Tasks
import java.util.*
import java.util.concurrent.TimeUnit

class GoogleFitManager(private val context: Context) : Parcelable {

    constructor(parcel: Parcel) : this(TODO("context")) {
    }

    private fun getGoogleAccount(): GoogleSignInAccount? {
        return GoogleSignIn.getAccountForExtension(context, fitnessOptions())
    }

    private fun fitnessOptions(): FitnessOptions {
        return FitnessOptions.builder()
            .addDataType(DataType.TYPE_STEP_COUNT_DELTA, FitnessOptions.ACCESS_READ)
            .addDataType(DataType.TYPE_HEART_RATE_BPM, FitnessOptions.ACCESS_READ)
            .addDataType(DataType.TYPE_SLEEP_SEGMENT, FitnessOptions.ACCESS_READ)
            .build()
    }

    fun readStepCount(): Int {
        val account = getGoogleAccount() ?: return -1

        val cal = Calendar.getInstance()
        val endTime = cal.timeInMillis
        cal.add(Calendar.DAY_OF_YEAR, -1)
        val startTime = cal.timeInMillis

        val request = DataReadRequest.Builder()
            .aggregate(DataType.TYPE_STEP_COUNT_DELTA)
            .setTimeRange(startTime, endTime, TimeUnit.MILLISECONDS)
            .bucketByTime(1, TimeUnit.DAYS)
            .build()

        val response = Tasks.await(
            Fitness.getHistoryClient(context, account).readData(request)
        )

        var totalSteps = 0
        for (bucket in response.buckets) {
            for (dataSet in bucket.dataSets) {
                for (dp in dataSet.dataPoints) {
                    totalSteps += dp.getValue(DataType.TYPE_STEP_COUNT_DELTA.fields[0]).asInt()
                }
            }
        }

        return totalSteps
    }

    fun readHeartRate(): Float {
        val account = getGoogleAccount() ?: return -1f

        val cal = Calendar.getInstance()
        val endTime = cal.timeInMillis
        cal.add(Calendar.DAY_OF_YEAR, -1)
        val startTime = cal.timeInMillis

        val request = DataReadRequest.Builder()
            .read(DataType.TYPE_HEART_RATE_BPM)
            .setTimeRange(startTime, endTime, TimeUnit.MILLISECONDS)
            .build()

        val response = Tasks.await(
            Fitness.getHistoryClient(context, account).readData(request)
        )

        var totalBpm = 0f
        var count = 0

        for (dataSet in response.dataSets) {
            for (dp in dataSet.dataPoints) {
                val bpm = dp.getValue(DataType.TYPE_HEART_RATE_BPM.fields[0]).asFloat()
                totalBpm += bpm
                count++
            }
        }

        return if (count > 0) totalBpm / count else 0f
    }

    fun readSleepHours(): Float {
        val account = getGoogleAccount() ?: return 0f

        val cal = Calendar.getInstance()
        val endTime = cal.timeInMillis
        cal.add(Calendar.DAY_OF_YEAR, -1)
        val startTime = cal.timeInMillis

        val request = DataReadRequest.Builder()
            .read(DataType.TYPE_SLEEP_SEGMENT)
            .setTimeRange(startTime, endTime, TimeUnit.MILLISECONDS)
            .build()

        val response = Tasks.await(
            Fitness.getHistoryClient(context, account).readData(request)
        )

        var totalSleepMillis = 0L

        for (dataSet in response.dataSets) {
            for (dp in dataSet.dataPoints) {
                val sleepStart = dp.getStartTime(TimeUnit.MILLISECONDS)
                val sleepEnd = dp.getEndTime(TimeUnit.MILLISECONDS)
                totalSleepMillis += (sleepEnd - sleepStart)
            }
        }

        return totalSleepMillis / (1000f * 60f * 60f) // Convert ms to hours
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {

    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<GoogleFitManager> {
        override fun createFromParcel(parcel: Parcel): GoogleFitManager {
            return GoogleFitManager(parcel)
        }

        override fun newArray(size: Int): Array<GoogleFitManager?> {
            return arrayOfNulls(size)
        }
    }
}
