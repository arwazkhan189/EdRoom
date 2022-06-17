package tech.edroomdevs.edroom.adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import tech.edroomdevs.edroom.R
import tech.edroomdevs.edroom.activity.NoticeDetailsActivity
import tech.edroomdevs.edroom.model.Notice

class NoticeRecyclerAdapter(
    private val context: Context,
    private var noticeList: ArrayList<Notice>
) : RecyclerView.Adapter<NoticeRecyclerAdapter.NoticeViewHolder>() {

    class NoticeViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvNoticeTitle: TextView = view.findViewById(R.id.tvNoticeTitle)
        val tvNoticeShortDesc: TextView = view.findViewById(R.id.tvNoticeShortDesc)
        val imgNoticeImage: ImageView = view.findViewById(R.id.imgNoticeImage)
        val btnReadDetails: com.google.android.material.button.MaterialButton =
            view.findViewById(R.id.btnReadDetails)
        //val tvNoticeDesc: TextView = view.findViewById(R.id.tvNoticeDesc)
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): NoticeViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.recycler_notice_single_row, parent, false)
        return NoticeViewHolder(view)
    }

    override fun onBindViewHolder(holder: NoticeViewHolder, position: Int) {
        val notice: Notice = noticeList[position]
        holder.tvNoticeTitle.text = notice.title
        holder.tvNoticeShortDesc.text = notice.short_description
        Picasso.get().load(notice.image).error(R.drawable.current_notice)
            .into(holder.imgNoticeImage)
        holder.btnReadDetails.setOnClickListener {
            val intent = Intent(context, NoticeDetailsActivity::class.java)
            intent.putExtra("title", notice.title)
            intent.putExtra("description", notice.description)
            intent.putExtra("image", notice.image)
            context.startActivity(intent)
        }
    }

    override fun getItemCount(): Int {
        return noticeList.size
    }
}