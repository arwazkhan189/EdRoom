package tech.edroomdevs.edroom.adapter

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import tech.edroomdevs.edroom.R
import tech.edroomdevs.edroom.model.Resource

class ResourceRecyclerAdapter(
    private val context: Context,
    private var resourceList: ArrayList<Resource>
) : RecyclerView.Adapter<ResourceRecyclerAdapter.ResourceViewHolder>() {

    class ResourceViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val recyclerResourceRow: LinearLayout = view.findViewById(R.id.recyclerResourceRow)
        val tvResourceName: TextView = view.findViewById(R.id.tvResourceName)
        val tvResourceCategory: TextView = view.findViewById(R.id.tvResourceCategory)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ResourceViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.recycler_resource_single_row, parent, false)
        return ResourceViewHolder(view)
    }

    override fun onBindViewHolder(holder: ResourceViewHolder, position: Int) {
        val resource: Resource = resourceList[position]
        holder.tvResourceName.text = resource.resourceName
        holder.tvResourceCategory.text = resource.resourceCategory
        holder.recyclerResourceRow.setOnClickListener {
            val openURL = Intent(Intent.ACTION_VIEW)
            openURL.data = Uri.parse(resource.resourceLink)
            context.startActivity(openURL)
        }
    }

    override fun getItemCount(): Int {
        return resourceList.size
    }

}