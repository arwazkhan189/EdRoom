package tech.edroomdevs.edroom.daos

import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import tech.edroomdevs.edroom.model.User

class UserDao {
    private val db = FirebaseFirestore.getInstance()
    val usersCollection = db.collection("Users")

    fun addUser(user: User?) {
        user?.let {
            GlobalScope.launch(Dispatchers.IO) {
                usersCollection.document(user.uid!!).set(it)
            }
        }
    }

    /*fun getUserById(uId: String): Task<DocumentSnapshot> {
        return usersCollection.document(uId).get()
    }*/

}