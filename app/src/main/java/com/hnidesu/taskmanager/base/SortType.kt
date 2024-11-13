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
            return when(sortType) {
                CreationAsc -> CreationDesc
                CreationDesc -> CreationAsc
                ModifiedAsc -> ModifiedDesc
                ModifiedDesc -> ModifiedAsc
                DeadlineAsc -> DeadlineDesc
                DeadlineDesc -> DeadlineAsc
            }
        }
    }


}
