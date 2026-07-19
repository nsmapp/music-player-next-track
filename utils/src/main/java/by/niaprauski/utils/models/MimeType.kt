package by.niaprauski.utils.models

enum class MimeType(val type: String){
    PLS("audio/x-scpls"),
    M3U("audio/x-mpegurl"),
    M3U8("application/vnd.apple.mpegurl"),

    MPEG("audio/mpeg"),
    OGG("audio/ogg"),
    OPUS("audio/opus"),
    AAC("audio/aac"),
    FLAC("audio/flac"),
    WAV("audio/wav"),
    M4A("audio/mp4"),
}
