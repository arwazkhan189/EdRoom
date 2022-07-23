package tech.edroomdevs.edroom.adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import tech.edroomdevs.edroom.R
import tech.edroomdevs.edroom.activity.doubtcorner.DoubtDetailsActivity
import tech.edroomdevs.edroom.model.Doubt
import tech.edroomdevs.edroom.util.DateTime

class DoubtRecyclerAdapter(
    options: FirestoreRecyclerOptions<Doubt>,
    private val context: Context
) :
    FirestoreRecyclerAdapter<Doubt, DoubtRecyclerAdapter.DoubtViewHolder>(options) {

    class DoubtViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvDoubtTitle: TextView = view.findViewById(R.id.tvDoubtTitle)
        val tvDoubtCreatedBy: TextView = view.findViewById(R.id.tvDoubtCreatedBy)
        val tvDoubtTime: TextView = view.findViewById(R.id.tvDoubtTime)
        val tvDoubtSubject: TextView = view.findViewById(R.id.tvDoubtSubject)
        val tvDoubtChapter: TextView = view.findViewById(R.id.tvDoubtChapter)
        val tvDoubtDesc: TextView = view.findViewById(R.id.tvDoubtDesc)
        val tvDoubtAnswerCount: TextView = view.findViewById(R.id.tvDoubtAnswerCount)
        val btnDoubtDetails: com.google.android.material.button.MaterialButton =
            view.findViewById(R.id.btnDoubtDetails)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DoubtViewHolder {
        return DoubtViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.recycler_doubt_single_row, parent, false)
        )
    }

    override fun onBindViewHolder(holder: DoubtViewHolder, position: Int, model: Doubt) {
        val dateTime = DateTime.getTimeAgo(model.doubtTime)
        holder.tvDoubtTitle.text = model.doubtTitle
        holder.tvDoubtCreatedBy.text = model.doubtCreatedBy
        holder.tvDoubtTime.text = dateTime
        holder.tvDoubtSubject.text = model.doubtSubject
        holder.tvDoubtChapter.text = model.doubtChapter
        holder.tvDoubtDesc.text = model.doubtDesc
        if (model.answersArray == null)
            holder.tvDoubtAnswerCount.text = "0"
        else
            holder.tvDoubtAnswerCount.text = model.answersArray.size.toString()

        holder.btnDoubtDetails.setOnClickListener {
            val intent = Intent(context, DoubtDetailsActivity::class.java)
            intent.putExtra("doubtId", model.doubtId)
            intent.putExtra("doubtTitle", model.doubtTitle)
            intent.putExtra("doubtCreatedBy", model.doubtCreatedBy)
            intent.putExtra("doubtTime", dateTime)
            intent.putExtra("doubtSubject", model.doubtSubject)
            intent.putExtra("doubtChapter", model.doubtChapter)
            intent.putExtra("doubtDesc", model.doubtDesc)
            intent.putExtra("doubtImageUrl", model.doubtImageUrl)
            intent.putExtra("userId", model.userId)
            intent.putExtra("doubtImageTitle", model.doubtImageTitle)
            context.startActivity(intent)
        }
    }

}


