package tech.edroomdevs.edroom.daos

import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import tech.edroomdevs.edroom.model.Answer


class AnswerDao {

    private val db = FirebaseFirestore.getInstance()
    private val doubtCollection = db.collection("Doubts")
    val answerCollection = db.collection("Answers")

    fun addAnswerByUser(
        answerId: String,
        answerByName: String,
        answerDesc: String,
        answerImageUrl: String,
        answerTime: Long,
        answerImageTitle: String,
        answeredDoubtId: String,
        answeredUserId: String
    ) {
        GlobalScope.launch {
            val answer = Answer(
                answerId,
                answerByName,
                answerDesc,
                answerImageUrl,
                answerTime,
                answerImageTitle,
                answeredDoubtId,
                answeredUserId
            )
            answerCollection.document(answerId).set(answer)
            doubtCollection.document(answeredDoubtId)
                .update("answersArray", FieldValue.arrayUnion(answerId))
        }
    }

}