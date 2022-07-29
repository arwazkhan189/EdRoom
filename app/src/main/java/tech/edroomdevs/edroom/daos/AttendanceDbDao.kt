package tech.edroomdevs.edroom.daos


import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class AttendanceDbDao {
    val db = FirebaseFirestore.getInstance()
    val attendanceCollection = db.collection("AttendanceDB")

    fun addAttendance(
        subject: String,
        tempDateList: String,
        presentStudentRollNumberList: ArrayList<String>
    ) {
        GlobalScope.launch {
            val dateList = hashMapOf(tempDateList to presentStudentRollNumberList)
            attendanceCollection.document(subject).set(dateList, SetOptions.merge())
        }
    }

}

