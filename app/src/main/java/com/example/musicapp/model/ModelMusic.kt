package com.example.musicapp.model

import java.io.Serializable

class ModelMusic : Serializable {
    var strId: String? = null

    @JvmField
    var strTitle: String? = null

    @JvmField
    var strBand: String? = null

    @JvmField
    var cover: String? = null

}