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

class NewAttendanceRecyclerAdapter(
    options: FirestoreRecyclerOptions<User>,
    private val listener: INewAttendanceRecyclerAdapter
) :
    FirestoreRecyclerAdapter<User, NewAttendanceRecyclerAdapter.NewAttendanceViewHolder>(options) {

    class NewAttendanceViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvStudentName: TextView = view.findViewById(R.id.tvStudentName)
        val tvStudentRollNumber: TextView = view.findViewById(R.id.tvStudentRollNumber)
        val radioPresent: RadioButton = view.findViewById(R.id.radioPresent)
        val radioAbsent: RadioButton = view.findViewById(R.id.radioAbsent)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NewAttendanceViewHolder {
        val newAttendanceViewHolder = NewAttendanceViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.recycler_attendance_student_single_row, parent, false)
        )
        newAttendanceViewHolder.radioPresent.setOnClickListener {
            listener.onPresentClick(
                snapshots.getSnapshot(newAttendanceViewHolder.adapterPosition).id,
                newAttendanceViewHolder.tvStudentRollNumber.text.toString()
            )
        }
        newAttendanceViewHolder.radioAbsent.setOnClickListener {
            listener.onAbsentClick(
                snapshots.getSnapshot(newAttendanceViewHolder.adapterPosition).id,
                newAttendanceViewHolder.tvStudentRollNumber.text.toString()
            )
        }
        return newAttendanceViewHolder
    }

    override fun onBindViewHolder(
        holder: NewAttendanceViewHolder,
        position: Int,
        model: User
    ) {
        holder.tvStudentName.text = model.fullName
        holder.tvStudentRollNumber.text = model.rollNumber
    }

}

interface INewAttendanceRecyclerAdapter {
    fun onPresentClick(id: String, rollNumber: String)
    fun onAbsentClick(id: String, rollNumber: String)
}
