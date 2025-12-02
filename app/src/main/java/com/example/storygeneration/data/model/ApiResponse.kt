package com.example.storygeneration.data.model

data class ApiResponse(
    val code: Int,
    val msg: String,
    val data: StoryData,
    val time: Long
)

data class StoryData(
    val shotList: List<Shot>
)

data class Shot(
    val scence_title: String,
    val prompt: String,
    val narration: String,
    val bgm_suggestion: String
)