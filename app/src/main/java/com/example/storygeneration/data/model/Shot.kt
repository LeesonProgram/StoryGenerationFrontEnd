package com.example.storygeneration.data.model

data class Shot(
    val id: String,
    val prompt: String,
    val narration: String,
    val imageUrl: String,
    val transition: Transition = Transition.Crossfade
)

enum class Transition {
    Ken_Burns, Crossfade, Volume_Mix
}