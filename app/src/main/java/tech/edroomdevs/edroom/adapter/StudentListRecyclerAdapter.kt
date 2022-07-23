package tech.edroomdevs.edroom.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import tech.edroomdevs.edroom.R
import tech.edroomdevs.edroom.model.StudentList

class StudentListRecyclerAdapter(
    options: FirestoreRecyclerOptions<StudentList>,
    private val context: Context
) :
    FirestoreRecyclerAdapter<StudentList, StudentListRecyclerAdapter.StudentListViewHolder>(options) {

    class StudentListViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvStudentName: TextView = view.findViewById(R.id.tvStudentName)
        val tvStudentRollNumber: TextView = view.findViewById(R.id.tvStudentRollNumber)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StudentListViewHolder {
        return StudentListViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.recycler_attendance_student_single_row, parent, false)
        )
    }

    override fun onBindViewHolder(
        holder: StudentListViewHolder,
        position: Int,
        model: StudentList
    ) {
        holder.tvStudentName.text = model.fullName
        holder.tvStudentRollNumber.text = model.rollNumber
    }

}
