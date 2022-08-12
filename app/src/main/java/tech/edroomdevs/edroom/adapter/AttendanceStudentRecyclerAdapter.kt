package tech.edroomdevs.edroom.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import tech.edroomdevs.edroom.R

class AttendanceStudentRecyclerAdapter(
    private val subjectList: ArrayList<String>,
    private val percentList: ArrayList<Int>,
    private val totalSubjectClassList: ArrayList<Int>
) : RecyclerView.Adapter<AttendanceStudentRecyclerAdapter.AttendanceStudentViewHolder>() {

    class AttendanceStudentViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvSubject: TextView = view.findViewById(R.id.tvSubject)
        val tvSubjectAttendancePercent: TextView =
            view.findViewById(R.id.tvSubjectAttendancePercent)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AttendanceStudentViewHolder {
        return AttendanceStudentViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.recycler_student_attendance_percent_single_row, parent, false)
        )
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: AttendanceStudentViewHolder, position: Int) {
        holder.tvSubject.text = subjectList[position]
        holder.tvSubjectAttendancePercent.text =
            "${percentList[position] / totalSubjectClassList[position]} %"
    }

    override fun getItemCount(): Int {
        return subjectList.size
    }


}