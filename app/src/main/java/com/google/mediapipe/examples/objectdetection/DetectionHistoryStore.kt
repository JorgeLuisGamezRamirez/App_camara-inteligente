package com.google.mediapipe.examples.objectdetection

import android.content.Context
import com.google.mediapipe.tasks.vision.objectdetector.ObjectDetectorResult
import org.json.JSONArray
import org.json.JSONObject

data class DetectionRecord(
    val id: Long,
    val type: String,
    val uri: String,
    val counts: Map<String, Int>,
    val createdAt: Long = System.currentTimeMillis()
)

object DetectionHistoryStore {
    private const val PREFS = "detection_history"
    private const val KEY = "records"

    fun counts(result: ObjectDetectorResult): Map<String, Int> =
        result.detections().mapNotNull { it.categories().firstOrNull()?.categoryName() }
            .groupingBy { it }.eachCount()

    fun summary(counts: Map<String, Int>): String =
        if (counts.isEmpty()) "Sin objetos" else counts.entries.joinToString(" · ") { "${it.key}: ${it.value}" }

    fun add(context: Context, record: DetectionRecord) {
        val records = load(context).toMutableList().apply { add(0, record) }.take(100)
        val json = JSONArray()
        records.forEach { item ->
            json.put(JSONObject().apply {
                put("id", item.id)
                put("type", item.type)
                put("uri", item.uri)
                put("createdAt", item.createdAt)
                put("counts", JSONObject(item.counts))
            })
        }
        context.getSharedPreferences(PREFS, Context.MODE_PRIVATE).edit().putString(KEY, json.toString()).apply()
    }

    fun load(context: Context): List<DetectionRecord> = runCatching {
        val array = JSONArray(context.getSharedPreferences(PREFS, Context.MODE_PRIVATE).getString(KEY, "[]"))
        (0 until array.length()).map { index ->
            val item = array.getJSONObject(index)
            val countJson = item.getJSONObject("counts")
            val counts = countJson.keys().asSequence().associateWith { countJson.getInt(it) }
            DetectionRecord(item.getLong("id"), item.getString("type"), item.getString("uri"), counts, item.getLong("createdAt"))
        }
    }.getOrDefault(emptyList())
}
