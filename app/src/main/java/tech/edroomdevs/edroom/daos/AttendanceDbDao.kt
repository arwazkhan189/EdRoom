package tech.edroomdevs.edroom.daos


import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class AttendanceDbDao {
    val db = FirebaseFirestore.getInstance()
    val attendanceCollection = db.collection("AttendanceDB")

    fun addAttendance(subject: String, tempDateList: String) {
        GlobalScope.launch {
            val dateList = hashMapOf(tempDateList to arrayListOf<String>())
            attendanceCollection.document(subject).set(dateList, SetOptions.merge())
        }
    }

    fun markAttendancePresent(subject: String, date: String, rollNumber: String) {
        GlobalScope.launch {
            attendanceCollection.document(subject).update(date, FieldValue.arrayUnion(rollNumber))
        }
    }

    fun markAttendanceAbsent(subject: String, date: String, rollNumber: String) {
        GlobalScope.launch {
            attendanceCollection.document(subject).update(date, FieldValue.arrayRemove(rollNumber))
        }
    }
}
