package tech.edroomdevs.edroom.adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import tech.edroomdevs.edroom.R
import tech.edroomdevs.edroom.activity.doubtcorner.AnswerDetailsActivity
import tech.edroomdevs.edroom.model.Answer
import tech.edroomdevs.edroom.util.DateTime


class AnswerRecyclerAdapter(
    options: FirestoreRecyclerOptions<Answer>,
    private val context: Context
) : FirestoreRecyclerAdapter<Answer, AnswerRecyclerAdapter.AnswerViewHolder>(
    options
) {

    class AnswerViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvAnswerByName: TextView = view.findViewById(R.id.tvAnswerByName)
        val tvAnswerTime: TextView = view.findViewById(R.id.tvAnswerTime)
        val tvAnswerDesc: TextView = view.findViewById(R.id.tvAnswerDesc)
        val rlAnswer: RelativeLayout = view.findViewById(R.id.rlAnswer)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AnswerViewHolder {
        return AnswerViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.recycler_answer_single_row, parent, false)
        )
    }

    override fun onBindViewHolder(holder: AnswerViewHolder, position: Int, model: Answer) {
        val answerTime = DateTime.getTimeAgo(model.answerTime)
        holder.tvAnswerByName.text = model.answerByName
        holder.tvAnswerTime.text = answerTime
        holder.tvAnswerDesc.text = model.answerDesc
        holder.rlAnswer.setOnClickListener {
            val intent = Intent(context, AnswerDetailsActivity::class.java)
            intent.putExtra("answerId", model.answerId)
            intent.putExtra("answerByName", model.answerByName)
            intent.putExtra("answerDesc", model.answerDesc)
            intent.putExtra("answerImageUrl", model.answerImageUrl)
            intent.putExtra("answerTime", answerTime)
            intent.putExtra("answerImageTitle", model.answerImageTitle)
            intent.putExtra("answeredDoubtId", model.answeredDoubtId)
            intent.putExtra("answeredUserId", model.answeredUserId)
            context.startActivity(intent)
        }
    }

}