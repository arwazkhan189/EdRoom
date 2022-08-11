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
    private var totalClassSubject: Int = 0
    private var totalClass: ArrayList<Int> = arrayListOf()

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

    fun getTotalNumberOfClassInSubject(subject: String): Int {
        attendanceCollection.document(subject).get().addOnSuccessListener {
            if (it.get("Total${subject}") != 0) {
                totalClassSubject = it.get("Total${subject}").toString().toInt()
            }
        }
        return totalClassSubject
    }
//
//    fun getTotalNumberOfClass(subjectList: ArrayList<String>): Int {
//        for (subject in subjectList) {
//            attendanceCollection.document(subject).get().addOnSuccessListener {
//                if (it.get("Total${subject}") != 0) {
//                    totalClass.add(it.get("Total${subject}").toString().toInt())
//                }
//            }
//        }
//        return totalClass[0]
//    }

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

}

