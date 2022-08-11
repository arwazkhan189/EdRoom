package tech.edroomdevs.edroom.model

class User(
    val uid: String? = null,
    val fullName: String? = null,
    val department: String? = null,
    val semester: String? = null,
    val rollNumber: String? = null,
    val mobileNumber: String? = null,
    val userEmailId: String? = null,
    val attendanceMap: HashMap<String, Int>? = null
)