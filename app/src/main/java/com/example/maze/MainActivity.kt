package com.example.maze

import android.content.Context
import android.content.res.Resources
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.DisplayMetrics
import android.view.WindowManager
import java.io.Console
import kotlin.properties.Delegates

class MainActivity : AppCompatActivity() {
    private var screenHeight by Delegates.notNull<Int>()
    private var screenWidth by Delegates.notNull<Int>()
    private lateinit var maze : MazeModel


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // get screen dimensions
        val metrics : DisplayMetrics = this.resources.displayMetrics
        screenWidth = metrics.widthPixels
        screenHeight = metrics.heightPixels

        //get mazeModel obj
        maze = MazeModel()





    }
}