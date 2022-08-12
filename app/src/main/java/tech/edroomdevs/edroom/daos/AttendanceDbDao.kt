package tech.edroomdevs.edroom.daos


import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class AttendanceDbDao {
    val db = FirebaseFirestore.getInstance()
    val attendanceCollection = db.collection("AttendanceDB")
    private val userCollection = db.collection("Users")
    private val recentAttendanceCollection = db.collection("RecentAttendance")

    fun addAttendance(
        subject: String,
        tempDateList: String,
        presentStudentRollNumberList: ArrayList<String>,
        incrementBy: Number
    ) {
        GlobalScope.launch {
            val dateList = hashMapOf(tempDateList to presentStudentRollNumberList)
            attendanceCollection.document(subject).set(dateList, SetOptions.merge())
            if (incrementBy == 1)
                attendanceCollection.document(subject)
                    .update("Total${subject}", FieldValue.increment(1))
        }
    }

    fun incrementAttendanceValue(
        userIdList: ArrayList<String>,
        subject: String,
        incrementBy: Number
    ) {
        GlobalScope.launch {
            for (userId in userIdList) {
                if (incrementBy == 1) {
                    userCollection.document(userId)
                        .update("attendanceMap.${subject}", FieldValue.increment(100))
                } else {
                    userCollection.document(userId)
                        .update("attendanceMap.${subject}", FieldValue.increment(-100))
                }
            }
        }
    }

    fun addRecentAttendance(
        branch: String,
        semester: String,
        teacher: String,
        subject: String
    ) {
        GlobalScope.launch {
            val recentAttendanceMap =
                hashMapOf("recentTeacher" to teacher, "recentSubject" to subject)
            recentAttendanceCollection.document(branch + semester).set(recentAttendanceMap)
        }
    }

}

