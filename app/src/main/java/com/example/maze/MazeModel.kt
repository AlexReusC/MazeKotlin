package com.example.maze

class MazeModel {
    private lateinit var mazeMap : Array<IntArray>

    init {
        mazeMap = arrayOf(
            intArrayOf(1,1,1,1,1,1,1,1,1),
            intArrayOf(1,0,1,1,1,1,0,0,1),
            intArrayOf(0,0,0,0,0,0,0,0,1),
            intArrayOf(1,1,1,0,0,1,0,0,1),
            intArrayOf(1,1,0,0,0,1,1,0,1),
            intArrayOf(1,1,0,0,0,0,0,0,1),
            intArrayOf(1,1,0,0,0,0,1,1,1),
            intArrayOf(1,0,0,0,0,0,0,0,1),
            intArrayOf(1,1,0,1,1,0,1,1,1),
            intArrayOf(1,0,0,0,0,0,0,0,1),
            intArrayOf(1,1,0,1,1,0,1,1,1),
            intArrayOf(1,0,0,0,0,0,0,0,1),
            intArrayOf(1,1,0,1,1,0,1,1,1),
            intArrayOf(1,0,0,0,0,0,0,0,1),
            intArrayOf(1,1,1,1,1,1,1,1,1),

            )
    }

    fun getMazeMap() : Array<IntArray>{
        return mazeMap
    }
}