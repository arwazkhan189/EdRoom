package tech.edroomdevs.edroom.model

class Doubt(
    val doubtId: String? = null,
    val doubtTitle: String? = null,
    val doubtCreatedBy: String? = null,
    val doubtTime: Long = 0L,
    val doubtSubject: String? = null,
    val doubtChapter: String? = null,
    val doubtDesc: String? = null,
    val doubtImageUrl: String? = null,
    val userId: String? = null,
    val doubtImageTitle: String? = null,
    val answersArray: ArrayList<String>? = null
)