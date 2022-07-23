package tech.edroomdevs.edroom.daos

import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import tech.edroomdevs.edroom.model.Doubt

class DoubtDao {
    private val db = FirebaseFirestore.getInstance()
    val doubtCollection = db.collection("Doubts")

    fun addDoubtByUser(
        doubtId: String,
        doubtTitle: String,
        doubtCreatedBy: String,
        doubtTime: Long,
        doubtSubject: String,
        doubtChapter: String,
        doubtDesc: String,
        doubtImageUrl: String,
        userId: String,
        doubtImageTitle: String,
        answersArray: ArrayList<String>?
    ) {
        GlobalScope.launch {
            val doubt = Doubt(
                doubtId,
                doubtTitle,
                doubtCreatedBy,
                doubtTime,
                doubtSubject,
                doubtChapter,
                doubtDesc,
                doubtImageUrl,
                userId,
                doubtImageTitle,
                answersArray
            )
            doubtCollection.document(doubtId).set(doubt)
        }
    }
}

