package com.craftingapps.status.saver.models

import android.net.Uri
import android.os.Parcel
import android.os.Parcelable


class StatusModel : Parcelable {



    var isVideo: Boolean = false
        get() = field
        set(value) {
            field = value
        }
    var name: String = "defaultValue"
        get() = field
        set(value) {
            field = value
        }
    var path: String = "defaultValue"
        get() = field
        set(value) {
            field = value
        }
    var filename: String = "defaultValue"
        get() = field
        set(value) {
            field = value
        }
    var uri: Parcelable? = null
        get() = field
        set(value) {
            field = value
        }

    constructor() {}


    //********** Describes the kinds of Special Objects contained in this Parcelable Instance's marshaled representation *********//

    override fun describeContents(): Int {
        return 0
    }


    //********** Writes the values to the Parcel *********//

    override fun writeToParcel(parcel_out: Parcel, flags: Int) {
        parcel_out.writeString(name)
        parcel_out.writeString(path)
        parcel_out.writeString(filename)
        parcel_out.writeByte((if (isVideo) 1 else 0).toByte())
        parcel_out.writeParcelable(uri, flags)
    }


    //********** Retrieves the values from the Parcel *********//

    constructor(parcel_in: Parcel) {
        this.name = parcel_in.readString()
        this.path = parcel_in.readString()
        this.filename = parcel_in.readString()
        isVideo = parcel_in.readByte().toInt() != 0
        this.uri = parcel_in.readParcelable(Uri::class.java.classLoader)
    }

    companion object CREATOR : Parcelable.Creator<StatusModel> {
        override fun createFromParcel(parcel: Parcel): StatusModel {
            return StatusModel(parcel)
        }

        override fun newArray(size: Int): Array<StatusModel?> {
            return arrayOfNulls(size)
        }
    }


}
