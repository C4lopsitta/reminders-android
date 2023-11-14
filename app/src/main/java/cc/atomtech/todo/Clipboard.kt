package cc.atomtech.todo

import android.content.ClipData
import android.content.ClipboardManager

class Clipboard {
   companion object {
      private var clipboard: ClipboardManager? = null;
      private final const val clipboardReminderLabel = "Reminder";
      public fun instantiate(systemService: ClipboardManager) {
         clipboard = systemService;
      }

      public fun copy(clip: String) {
         if(clipboard == null) return;
         clipboard!!.setPrimaryClip(ClipData.newPlainText(clipboardReminderLabel, clip));
      }

   }
}