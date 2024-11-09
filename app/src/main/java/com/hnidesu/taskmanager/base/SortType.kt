package com.hnidesu.taskmanager.base


enum class SortType {
    CreationAsc,
    CreationDesc,
    ModifiedAsc,
    ModifiedDesc,
    DeadlineAsc,
    DeadlineDesc;

    companion object  {
        fun reverse(sortType: SortType): SortType {
            val ordinal = sortType.ordinal
            return if (ordinal % 2 == 0) {
                entries[ordinal + 1]
            } else entries[ordinal - 1]
        }
    }


}
