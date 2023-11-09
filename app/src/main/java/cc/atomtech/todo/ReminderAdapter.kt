package cc.atomtech.todo

import android.annotation.SuppressLint
import android.content.ClipData
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.FrameLayout
import android.widget.PopupMenu
import android.widget.TextView
import androidx.core.content.ContextCompat.*
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar


class ReminderAdapter(private val dataSet: List<Reminder>, private val pkgContext: Context): RecyclerView.Adapter<ReminderAdapter.ViewHolder>() {
    private var dataList: MutableList<Reminder> = dataSet as MutableList<Reminder>

    class ViewHolder (view: View): RecyclerView.ViewHolder(view) {
        val title: TextView
        val checkbox: CheckBox
        val frame: FrameLayout

        init {
            title = view.findViewById(R.id.reminder_view_title)
            checkbox = view.findViewById(R.id.reminder_check)
            frame = view.findViewById(R.id.reminder)
        }
    }

    override fun getItemCount(): Int = dataSet.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.title.text = dataSet[position].title
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

        holder.title.setOnClickListener {
            val i = Intent(pkgContext, ShowReminder::class.java)
            i.putExtra("title", dataList[position].title)
            i.putExtra("body", dataList[position].body)
            i.putExtra("isCompleted", dataList[position].isCompleted)
            i.putExtra("reminderID", dataList[position].id)
            i.putExtra("getNotification", dataList[position].getNotification)
            //TODO: Add date and time (and notification settings)
            pkgContext.startActivity(i)
        }
        holder.title.setOnLongClickListener {
            showPopup(holder.title, dataList[position].title, position)
            true
        }
        holder.frame.setOnLongClickListener {
            showPopup(holder.frame, dataList[position].title, position)
            true
        }
    }
    @SuppressLint("NotifyDataSetChanged")
    private fun showPopup(view: View, body: String?, position: Int){
        val popup = PopupMenu(pkgContext, view)
        popup.inflate(R.menu.hold_menu)
        popup.setOnMenuItemClickListener(PopupMenu.OnMenuItemClickListener { item: MenuItem ->
            when (item.itemId) {
                R.id.clipboard_copy -> {
                    var clip: String = ""
                    if(!body.isNullOrEmpty())
                        clip = String.format("[UNIX : ${dataList[position].notificationTimestamp}] ${dataList[position].title} ${dataList[position].body}")
                    clipboard.setPrimaryClip(ClipData.newPlainText(clip, clip))
                    Snackbar.make(view, pkgContext.getString(R.string.copy_copied), Snackbar.LENGTH_SHORT).show()
                }
                R.id.hold_delete -> {
                    Snackbar.make(view, pkgContext.getString(R.string.snack_deleted), Snackbar.LENGTH_SHORT).setAction(pkgContext.getString(R.string.snack_undo)){
                    }.addCallback(object: Snackbar.Callback() {
                        override fun onDismissed(transientBottomBar: Snackbar?, event: Int) {
                            if(event == DISMISS_EVENT_SWIPE || event == DISMISS_EVENT_TIMEOUT){
                                reminderDao.deleteReminder(dataList[position])
                                notifyItemRemoved(position)
                                dataList.removeAt(position)
                                notifyDataSetChanged()
                            }
                        }
                    }).show()
                }
                R.id.share -> {
                    var shareText: String = String.format("${pkgContext.getString(R.string.share_header)}" +
                          "\nTIMESTAMP NOTIFICATION: ${dataList[position].notificationTimestamp}\n" +
                          "${dataList[position].title}\n${dataList[position].body}");

                    val sendIntent: Intent = Intent().apply {
                        action = Intent.ACTION_SEND
                        putExtra(Intent.EXTRA_TEXT, shareText)
                        type = "text/plain"
                    }
                    val shareIntent = Intent.createChooser(sendIntent, "Reminder");
                    pkgContext.startActivity(shareIntent);
                }
            }
            true
        })
        popup.show()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.reminder_row, parent, false)
        return ViewHolder(view)
    }
}
