package cc.atomtech.todo

enum class Filters {
   ALL,
   COMPLETED,
   UNCOMPLETED,
   WITH_NOTIFICATION,
   WITHOUT_NOTIFICATION;

   public fun getChipId(): Int {
      return when(this) {
         Filters.ALL -> R.id.showall;
         Filters.COMPLETED -> R.id.showcompleted;
         Filters.UNCOMPLETED -> R.id.showtocomplete;
         Filters.WITH_NOTIFICATION -> R.id.showwithnotification;
         Filters.WITHOUT_NOTIFICATION -> R.id.showwithoutnotification;
      }
   }
}

enum class SortingMethods {
   BY_NOTIFICATION,
   BY_CREATION,
   BY_TITLE,
   BY_NOTIFICATION_REVERSED,
   BY_CREATION_REVERSED,
   BY_TITLE_REVERSED
}