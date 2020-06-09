package com.jianbao.jamboble.data

import java.util.*

data class QnUser(
    var user_id: String,
    /**
     * 男: male
     * 女:female
     */
    var gender: String,
    var height: Int,
    var birthday: Date
)