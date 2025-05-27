import android.content.Context
import com.google.android.gms.auth.api.signin.*
import com.google.android.gms.fitness.Fitness
import com.google.android.gms.fitness.data.DataType
import com.google.android.gms.fitness.request.DataReadRequest
import com.google.android.gms.tasks.Tasks
import java.util.concurrent.TimeUnit
import java.util.Calendar

class GoogleFitManager(private val context: Context) {

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

        val buckets = response.buckets
        var totalSteps = 0
        for (bucket in buckets) {
            val dataSets = bucket.dataSets
            for (dataSet in dataSets) {
                for (dp in dataSet.dataPoints) {
                    totalSteps += dp.getValue(DataType.TYPE_STEP_COUNT_DELTA.fields[0]).asInt()
                }
            }
        }

        return totalSteps
    }

    // Similarly, you can add readHeartRate() and readSleepData() methods here
}

}