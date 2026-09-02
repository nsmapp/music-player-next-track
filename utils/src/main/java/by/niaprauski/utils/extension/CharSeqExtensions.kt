package by.niaprauski.utils.extension

import java.nio.charset.Charset

fun CharSequence?.orDefault(default: String): String =
    if (this.isNullOrEmpty()) default else this.toString()

fun CharSequence?.ifNullOrEmpty(defaultValue: () -> CharSequence?): CharSequence? {
    return if (this.isNullOrEmpty()) defaultValue() else this
}

fun String.convertToInt() = this.filter { it.isDigit() }.toInt()


fun CharSequence?.fixOldEncoding(): String? {
    if (this.isNullOrBlank()) return this?.toString()
    val name = this.toString()

    if (name.any { it in '\u0400'..'\u04FF' }) return name

    return try {
        val bytes = name.toByteArray(Charsets.ISO_8859_1)

        val utf8 = String(bytes, Charsets.UTF_8)
        if (!utf8.contains('\uFFFD') && utf8.any { it.code > 127 }) return utf8

        val win1251 = String(bytes, Charset.forName("Windows-1251"))

        if (win1251.any { it in '\u0400'..'\u04FF' }) {
            val hasControlChars = name.any { it.code in 128..159 }
            if (hasControlChars || !win1251.isMixedLatinCyrillic()) {
                return win1251
            }
        }

        name
    } catch (e: Exception) {
        name
    }
}

private fun String.isMixedLatinCyrillic(): Boolean {
    var hasLat = false
    var hasCyr = false
    for (c in this) {
        if (c in 'a'..'z' || c in 'A'..'Z') hasLat = true
        else if (c in '\u0400'..'\u04FF') hasCyr = true
        else if (!c.isLetter()) {
            if (hasLat && hasCyr) return true
            hasLat = false
            hasCyr = false
        }
        if (hasLat && hasCyr) return true
    }
    return hasLat && hasCyr
}

