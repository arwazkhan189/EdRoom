package tech.edroomdevs.edroom.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import tech.edroomdevs.edroom.R
import tech.edroomdevs.edroom.model.User

class StudentListRecyclerAdapter(
    options: FirestoreRecyclerOptions<User>,
    private val listener: IStudentListRecyclerAdapter
) :
    FirestoreRecyclerAdapter<User, StudentListRecyclerAdapter.StudentListViewHolder>(options) {

    class StudentListViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvStudentName: TextView = view.findViewById(R.id.tvStudentName)
        val tvStudentRollNumber: TextView = view.findViewById(R.id.tvStudentRollNumber)
        val radioPresent: RadioButton = view.findViewById(R.id.radioPresent)
        val radioAbsent: RadioButton = view.findViewById(R.id.radioAbsent)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StudentListViewHolder {
        val studentListViewHolder = StudentListViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.recycler_attendance_student_single_row, parent, false)
        )
        studentListViewHolder.radioPresent.setOnClickListener {
            listener.onPresentClick(
                snapshots.getSnapshot(studentListViewHolder.adapterPosition).id,
                studentListViewHolder.tvStudentRollNumber.text.toString()
            )
        }
        studentListViewHolder.radioAbsent.setOnClickListener {
            listener.onAbsentClick(
                snapshots.getSnapshot(studentListViewHolder.adapterPosition).id,
                studentListViewHolder.tvStudentRollNumber.text.toString()
            )
        }
        return studentListViewHolder
    }

    override fun onBindViewHolder(
        holder: StudentListViewHolder,
        position: Int,
        model: User
    ) {
        holder.tvStudentName.text = model.fullName
        holder.tvStudentRollNumber.text = model.rollNumber
    }

}

interface IStudentListRecyclerAdapter {
    fun onPresentClick(id: String, rollNumber: String)
    fun onAbsentClick(id: String, rollNumber: String)
}
