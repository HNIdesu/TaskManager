package com.hnidesu.taskmanager.util

import java.nio.charset.StandardCharsets
import java.security.MessageDigest
import java.util.zip.CRC32

object HashUtil{

    fun md5Digest(text: String): String {
        val md5 = MessageDigest.getInstance("MD5")
        md5.update(text.toByteArray(StandardCharsets.UTF_8))
        val sb = StringBuilder()
        for (b in md5.digest()) {
            var hex = String.format("%X", arrayOf(b))
            if (hex.length == 1) {
                hex = "0$hex"
            }
            sb.append(hex)
        }
        return sb.toString()
    }

    fun crc32Digest(text: String): Long {
        return CRC32().let {
            it.update(text.toByteArray(StandardCharsets.UTF_8))
            it.value
        }

    }

}
