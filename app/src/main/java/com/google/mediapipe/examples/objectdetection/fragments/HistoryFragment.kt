package com.google.mediapipe.examples.objectdetection.fragments

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.mediapipe.examples.objectdetection.DetectionHistoryStore
import com.google.mediapipe.examples.objectdetection.DetectionRecord
import com.google.mediapipe.examples.objectdetection.R
import com.google.mediapipe.examples.objectdetection.databinding.FragmentHistoryBinding
import java.io.File
import java.text.DateFormat
import java.util.Date

class HistoryFragment : Fragment() {
    private var binding: FragmentHistoryBinding? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, state: Bundle?): View {
        binding = FragmentHistoryBinding.inflate(inflater, container, false)
        return binding!!.root
    }

    override fun onViewCreated(view: View, state: Bundle?) {
        val records = DetectionHistoryStore.load(requireContext())
        binding!!.emptyHistory.visibility = if (records.isEmpty()) View.VISIBLE else View.GONE
        binding!!.historyList.apply {
            visibility = if (records.isEmpty()) View.GONE else View.VISIBLE
            layoutManager = LinearLayoutManager(requireContext())
            adapter = HistoryAdapter(records, ::share)
        }
    }

    private fun share(record: DetectionRecord) {
        val raw = Uri.parse(record.uri)
        val uri = if (raw.scheme == "file") FileProvider.getUriForFile(
            requireContext(), "${requireContext().packageName}.files", File(raw.path!!)
        ) else raw
        val intent = Intent(Intent.ACTION_SEND).apply {
            type = if (record.type == "video") "video/*" else "image/*"
            putExtra(Intent.EXTRA_STREAM, uri)
            putExtra(Intent.EXTRA_TEXT, "Detecciones: ${DetectionHistoryStore.summary(record.counts)}")
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
        startActivity(Intent.createChooser(intent, "Compartir detección"))
    }

    override fun onDestroyView() { binding = null; super.onDestroyView() }
}

private class HistoryAdapter(
    private val records: List<DetectionRecord>,
    private val onShare: (DetectionRecord) -> Unit
) : RecyclerView.Adapter<HistoryAdapter.Holder>() {
    class Holder(view: View) : RecyclerView.ViewHolder(view) {
        val title: TextView = view.findViewById(R.id.historyTitle)
        val date: TextView = view.findViewById(R.id.historyDate)
        val objects: TextView = view.findViewById(R.id.historyObjects)
        val share: Button = view.findViewById(R.id.shareHistory)
    }
    override fun onCreateViewHolder(parent: ViewGroup, type: Int) = Holder(
        LayoutInflater.from(parent.context).inflate(R.layout.item_history, parent, false)
    )
    override fun getItemCount() = records.size
    override fun onBindViewHolder(holder: Holder, position: Int) {
        val item = records[position]
        holder.title.text = if (item.type == "video") "Video analizado" else "Fotografía analizada"
        holder.date.text = DateFormat.getDateTimeInstance().format(Date(item.createdAt))
        holder.objects.text = DetectionHistoryStore.summary(item.counts)
        holder.share.setOnClickListener { onShare(item) }
    }
}
