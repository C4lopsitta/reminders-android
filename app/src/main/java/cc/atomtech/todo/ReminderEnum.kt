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

   companion object {
      public fun getEnumFromChipId(id: Int?): Filters {
         return when(id) {
            R.id.showall -> Filters.ALL;
            R.id.showcompleted -> Filters.COMPLETED;
            R.id.showtocomplete -> Filters.UNCOMPLETED;
            R.id.showwithnotification -> Filters.WITH_NOTIFICATION;
            R.id.showwithoutnotification -> Filters.WITHOUT_NOTIFICATION;
            else -> {return Filters.ALL}
         }
      }

      public fun getEnumFromOrdinal(ordinal: Int): Filters {
         return when(ordinal) {
            0 -> Filters.ALL;
            1 -> Filters.COMPLETED;
            2 -> Filters.UNCOMPLETED;
            3 -> Filters.WITH_NOTIFICATION;
            4 -> Filters.WITHOUT_NOTIFICATION;
            else -> {return Filters.ALL}
         }
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