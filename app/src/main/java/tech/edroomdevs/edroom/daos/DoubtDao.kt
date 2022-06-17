package tech.edroomdevs.edroom.daos

import com.google.android.gms.tasks.Task
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import tech.edroomdevs.edroom.model.Doubt
import tech.edroomdevs.edroom.model.User

class DoubtDao {
    private val db = FirebaseFirestore.getInstance()
    private val auth = Firebase.auth
    val doubtCollection = db.collection("Doubts")

    fun addDoubtByUser(
        doubtTitle: String,
        doubtCreatedBy: String,
        doubtTime: Long,
        doubtSubject: String,
        doubtChapter: String,
        doubtDesc: String
    ) {

        val currentUserId = auth.currentUser!!.uid
        GlobalScope.launch {
            val userDao = UserDao()
            userDao.getUserById(currentUserId).await().toObject(User::class.java)!!
            val doubt = Doubt(
                currentUserId,
                doubtTitle,
                doubtCreatedBy,
                doubtTime,
                doubtSubject,
                doubtChapter,
                doubtDesc
            )
            doubtCollection.document().set(doubt)
        }
    }

    private fun getDoubtById(doubtId: String): Task<DocumentSnapshot> {
        return doubtCollection.document(doubtId).get()
    }

}

