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

class EditAttendanceRecyclerAdapter(
    options: FirestoreRecyclerOptions<User>,
    private val presentStudentRollNumberList: ArrayList<String>,
    private val listener: IEditAttendanceRecyclerAdapter
) : FirestoreRecyclerAdapter<User, EditAttendanceRecyclerAdapter.EditAttendanceViewHolder>(options) {

    class EditAttendanceViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvStudentName: TextView = view.findViewById(R.id.tvStudentName)
        val tvStudentRollNumber: TextView = view.findViewById(R.id.tvStudentRollNumber)
        val radioPresent: RadioButton = view.findViewById(R.id.radioPresent)
        val radioAbsent: RadioButton = view.findViewById(R.id.radioAbsent)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EditAttendanceViewHolder {
        val editAttendanceViewHolder = EditAttendanceViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.recycler_attendance_student_single_row, parent, false)
        )
        editAttendanceViewHolder.radioPresent.setOnClickListener {
            listener.onPresentClick(
                snapshots.getSnapshot(editAttendanceViewHolder.adapterPosition).id,
                editAttendanceViewHolder.tvStudentRollNumber.text.toString(),
                editAttendanceViewHolder.tvStudentName.text.toString()
            )
        }
        editAttendanceViewHolder.radioAbsent.setOnClickListener {
            listener.onAbsentClick(
                snapshots.getSnapshot(editAttendanceViewHolder.adapterPosition).id,
                editAttendanceViewHolder.tvStudentRollNumber.text.toString(),
                editAttendanceViewHolder.tvStudentName.text.toString()
            )
        }
        return editAttendanceViewHolder
    }

    override fun onBindViewHolder(holder: EditAttendanceViewHolder, position: Int, model: User) {
        holder.tvStudentName.text = model.fullName
        holder.tvStudentRollNumber.text = model.rollNumber
        if (presentStudentRollNumberList.contains(model.rollNumber)) {
            holder.radioPresent.isChecked = true
        } else {
            holder.radioAbsent.isChecked = true
        }
    }
}

interface IEditAttendanceRecyclerAdapter {
    fun onPresentClick(id: String, rollNumber: String, fullName: String)
    fun onAbsentClick(id: String, rollNumber: String, fullName: String)
}
