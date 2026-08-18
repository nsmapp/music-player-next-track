package by.niaprauski.utils.extension

import java.nio.charset.Charset
import java.nio.charset.StandardCharsets

fun CharSequence?.orDefault(default: String): String =
    if (this.isNullOrEmpty()) default else this.toString()

fun CharSequence?.ifNullOrEmpty(defaultValue: () -> CharSequence?): CharSequence? {
    return if (this.isNullOrEmpty()) defaultValue() else this
}

fun String.convertToInt() = this.filter { it.isDigit() }.toInt()


fun CharSequence?.fixOldEncoding(): String? {
    if (this.isNullOrBlank()) return this?.toString()
    val name = this.toString()

    if (name.any { it.code > 255 && it != '\uFFFD' }) return name
    if (name.all { it.code < 128 }) return name

    return try {
        val bytes = name.toByteArray(StandardCharsets.ISO_8859_1)

        val decodedUtf8 = String(bytes, StandardCharsets.UTF_8)
        if (
            !decodedUtf8.contains('\uFFFD')
            && decodedUtf8.any { it.code > 127 }
        ) return decodedUtf8

        val charsets = listOf("Windows-1251", "Windows-1250", "KOI8-R")
        for (name in charsets) {
            try {
                val decoded = String(bytes, Charset.forName(name))
                if (decoded.count { it.isLetter() } >= (name.length * 0.5)) {
                    return decoded
                }
            } catch (e: Exception) {
            }
        }
        name
    } catch (e: Exception) {
        name
    }
}