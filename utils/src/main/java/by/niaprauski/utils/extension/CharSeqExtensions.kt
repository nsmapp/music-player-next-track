package by.niaprauski.utils.extension

import java.nio.charset.Charset

fun CharSequence?.orDefault(default: String): String =
    if (this.isNullOrEmpty()) default else this.toString()

fun CharSequence?.ifNullOrEmpty(defaultValue: () -> CharSequence?): CharSequence? {
    return if (this.isNullOrEmpty()) defaultValue() else this
}

fun String.convertToInt() = this.filter { it.isDigit() }.toInt()

//TODO support other encodings(language)
fun CharSequence?.fixOldEncoding(): CharSequence? {
    if (this.isNullOrBlank()) return this

    return try {
        val s = this.toString()
        val bytes = s.toByteArray(Charsets.ISO_8859_1)
        val decoded = String(bytes, Charset.forName("Windows-1251"))
        if (decoded.any { it in '\u0400'..'\u04FF' }) decoded else this
    } catch (e: Exception) {
        this
    }
}