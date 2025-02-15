package com.example.to_do.model

import android.os.Parcel
import android.os.Parcelable

class TaskModel(
    var TaskId: String = "",
    var TaskName: String = "",
    var TaskDate: String = "",
    var TaskDescription: String = "",
    var TaskPriority: String = "", // New: Task priority (Low, Medium, High)
    var TaskCategory: String = "", // New: Task category (Work, Personal, etc.)

) : Parcelable {

    constructor(parcel: Parcel) : this(
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: "",

    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(TaskId)
        parcel.writeString(TaskName)
        parcel.writeString(TaskDate)
        parcel.writeString(TaskDescription)
        parcel.writeString(TaskPriority)
        parcel.writeString(TaskCategory)


    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<TaskModel> {
        override fun createFromParcel(parcel: Parcel): TaskModel {
            return TaskModel(parcel)
        }

        override fun newArray(size: Int): Array<TaskModel?> {
            return arrayOfNulls(size)
        }
    }
}