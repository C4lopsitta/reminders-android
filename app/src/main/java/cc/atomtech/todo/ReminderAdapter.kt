package cc.atomtech.todo

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.FrameLayout
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat.startActivity
import androidx.recyclerview.widget.RecyclerView


class ReminderAdapter(private val dataSet: List<Reminder>, private val pkgContext: Context): RecyclerView.Adapter<ReminderAdapter.ViewHolder>() {
    private var dataList: MutableList<Reminder> = dataSet as MutableList<Reminder>

    class ViewHolder (view: View): RecyclerView.ViewHolder(view) {
        val body: TextView
        val checkbox: CheckBox
        val frame: FrameLayout

        init {
            body = view.findViewById(R.id.reminder_view_body)
            checkbox = view.findViewById(R.id.reminder_check)
            frame = view.findViewById(R.id.reminder)
        }
    }

    override fun getItemCount(): Int = dataSet.size

    @SuppressLint("NotifyDataSetChanged")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.body.text = dataSet[position].body
        holder.checkbox.isChecked = dataList[position].isCompleted

        holder.checkbox.setOnClickListener {
            reminderDao.updateIsCompleted(!dataList[position].isCompleted, dataList[position].id)
            dataList[position].isCompleted = !dataList[position].isCompleted
        }
//        holder.delete.setOnClickListener {
//            reminderDao.deleteReminder(dataList[position])
//            notifyItemRemoved(position)
//            dataList.removeAt(position)
//            notifyDataSetChanged()
//        }
        holder.body.setOnClickListener {
            val i = Intent(pkgContext, ShowReminder::class.java)
            i.putExtra("body", dataList[position].body)
            i.putExtra("isCompleted", dataList[position].isCompleted)
            i.putExtra("reminderID", dataList[position].id)
            //TODO: Add date and time (and notification settings)
            pkgContext.startActivity(i)
        }
        holder.body.setOnLongClickListener {
            val toast = Toast.makeText(pkgContext, "To be implemented press and hold menu", Toast.LENGTH_SHORT)
            toast.show()
            true
        }
        holder.frame.setOnLongClickListener {
            val toast = Toast.makeText(pkgContext, "To be implemented press and hold menu", Toast.LENGTH_SHORT)
            toast.show()
            true
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.reminder_row, parent, false)
        return ViewHolder(view)
    }
}
