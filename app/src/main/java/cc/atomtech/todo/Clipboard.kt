package cc.atomtech.todo

import android.content.ClipData
import android.content.ClipboardManager

class Clipboard {
   companion object {
      private var clipboard: ClipboardManager? = null;
      private val clipboardReminderLabel = "Reminder";
      public fun instantiate(systemService: ClipboardManager) {
         clipboard = systemService;
      }

      public fun copy(clip: String) {
         clipboard?.setPrimaryClip(ClipData.newPlainText(clipboardReminderLabel, clip));
      }

   }
}