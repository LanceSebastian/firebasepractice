package uk.ac.aber.dcs.firestorepractise

data class Post(
    val title: String = "",
    val content: String = "",
    val timestamp: Long = System.currentTimeMillis()
)
