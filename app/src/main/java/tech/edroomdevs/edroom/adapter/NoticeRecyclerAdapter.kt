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
import tech.edroomdevs.edroom.activity.noticeboard.NoticeDetailsActivity
import tech.edroomdevs.edroom.model.Notice

class NoticeRecyclerAdapter(
    private val context: Context,
    private var noticeList: ArrayList<Notice>
) : RecyclerView.Adapter<NoticeRecyclerAdapter.NoticeViewHolder>() {

    class NoticeViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val imgNoticeImage: ImageView = view.findViewById(R.id.imgNoticeImage)
        val tvNoticeTitle: TextView = view.findViewById(R.id.tvNoticeTitle)
        val tvNoticeDate: TextView = view.findViewById(R.id.tvNoticeDate)
        val tvNoticeDesc: TextView = view.findViewById(R.id.tvNoticeDesc)
        val btnReadDetails: com.google.android.material.button.MaterialButton =
            view.findViewById(R.id.btnReadDetails)
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
        holder.tvNoticeTitle.text = notice.noticeTitle
        holder.tvNoticeDesc.text = notice.noticeDescription
        holder.tvNoticeDate.text = notice.noticeDate
        Picasso.get().load(notice.noticeImageUrl).error(R.drawable.notice_image)
            .into(holder.imgNoticeImage)
        holder.btnReadDetails.setOnClickListener {
            val intent = Intent(context, NoticeDetailsActivity::class.java)
            intent.putExtra("noticeId", notice.noticeId)
            intent.putExtra("noticeTitle", notice.noticeTitle)
            intent.putExtra("noticeDescription", notice.noticeDescription)
            intent.putExtra("noticeImageUrl", notice.noticeImageUrl)
            intent.putExtra("noticeByTeacher", notice.noticeByTeacher)
            intent.putExtra("noticeImageTitle", notice.noticeImageTitle)
            intent.putExtra("noticeDate", notice.noticeDate)
            context.startActivity(intent)
        }
    }

    override fun getItemCount(): Int {
        return noticeList.size
    }
}