package com.kgzn.gamecenter.data.remote.request

import java.lang.reflect.Field
import java.lang.reflect.Modifier
import java.security.MessageDigest

interface Token {

    companion object {
        private const val SECRET_KEY = "a4ff9083802144edb96dc6f38cdb6330"
    }

    val token: String

    fun token(): String {
        var targetClass: Class<*>? = javaClass
        val fields: MutableList<Field> = mutableListOf()
        while (targetClass != null && targetClass != Any::class.java) {
            fields.addAll(targetClass.declaredFields)
            targetClass = targetClass.superclass
        }

        return fields.filterNot { field ->
            Modifier.isStatic(field.modifiers) || field.name == "token"
        }.sortedBy { it.name }.map { field ->
            field.isAccessible = true
            field[this]?.let { "${field.name}=$it" } ?: ""
        }.filter { it.isNotBlank() }.joinToString("&").let {
            calculateMd5(it + SECRET_KEY)
        }
    }

    private fun calculateMd5(input: String): String {
        val md = MessageDigest.getInstance("MD5")
        md.update(input.toByteArray())
        val digest = md.digest()
        val hexString = StringBuilder()

        for (byte in digest) {
            // 转换为两位十六进制数，不足两位补0
            hexString.append(String.format("%02x", byte.toInt() and 0xFF))
        }
        return hexString.toString()
    }
}