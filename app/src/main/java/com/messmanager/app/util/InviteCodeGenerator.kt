package com.messmanager.app.util

import java.security.SecureRandom

object InviteCodeGenerator {
    private const val CHARS = "ABCDEFGHJKLMNPQRSTUVWXYZ23456789" // Excluded easily confused chars: I, O, 0, 1
    private val random = SecureRandom()

    fun generateCode(length: Int = Constants.INVITE_CODE_LENGTH): String {
        return (1..length)
            .map { CHARS[random.nextInt(CHARS.length)] }
            .joinToString("")
    }
}
