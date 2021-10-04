package com.example.maze

import android.util.DisplayMetrics
import kotlin.math.*
import kotlin.properties.Delegates

class MazeModel(metrics: DisplayMetrics) {
    private lateinit var mazeMap : Array<IntArray>
    private var screenHeight by Delegates.notNull<Float>()
    private var screenWidth by Delegates.notNull<Float>()
    private val widthSquare : Float
    private val radiusBall : Float
    private var xAxisBall : Float
    private var yAxisBall : Float
    private var xVelBall : Float
    private var yVelBall : Float
    private var xAreaTL : Int
    private var yAreaTL : Int
    private var xAreaBR : Int
    private var yAreaBR : Int

    init {
        //dimensions screen
        screenWidth = metrics.widthPixels.toFloat()
        screenHeight = metrics.heightPixels.toFloat()

        //map of the maze
        //TODO: create an enum with the differents maps
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

        //dimensions square and ball
        widthSquare = this.screenWidth / this.mazeMap[0].size
        radiusBall = this.widthSquare / 2

        //position and velocity of ball (x,y)
        xAxisBall = 36f
        yAxisBall = this.getRadiusBall() * 5
        xVelBall = 0f
        yVelBall = 0f

        //highlighted cells
        xAreaTL = -1
        yAreaTL = -1
        xAreaBR = -1
        yAreaBR = -1
    }

    fun getMazeMap() : Array<IntArray>{
        return mazeMap
    }

    fun getWidth() : Float {
        return this.screenWidth
    }

    fun getCellsHighlighted(): List<Int> {
        return listOf<Int>(xAreaTL, yAreaTL, xAreaBR, yAreaBR)
    }

    fun getWidthSquare() : Float {
        return this.widthSquare
    }

    fun getRadiusBall() : Float {
        return this.radiusBall
    }

    fun getXVel() : Float {
        return xVelBall
    }

    fun getYVel() : Float {
        return yVelBall
    }

    fun getXBall() : Float {
        return xAxisBall
    }

    fun getYBall() : Float {
        return yAxisBall
    }

    fun setVelocity(xVelBall : Float, yVelBall : Float){
        this.xVelBall = xVelBall
        this.yVelBall = yVelBall
    }

    fun setPosition(xVelBall : Float, yVelBall : Float){
        //calculate potential pos with current pos + velocity
        var xPotentialPos = xAxisBall + xVelBall
        var yPotentialPos = yAxisBall + yVelBall

        //calculate current and target cells (discreet number instead of continous)
        val xCurrentCell : Int = (floor(xAxisBall / this.widthSquare)).toInt()
        val yCurrentCell : Int = (floor(yAxisBall / this.widthSquare)).toInt()
        val xTargetCell : Int = (xPotentialPos / this.widthSquare).toInt()
        val yTargetCell : Int = (yPotentialPos / this.widthSquare).toInt()

        //calculate area where a possible collision could occur
        //it is considered that min is 0 and max are the dimensions of the map
        xAreaTL = max(min(xCurrentCell, xTargetCell) - 1, 0)
        yAreaTL = max(min(yCurrentCell, yTargetCell) - 1, 0)
        xAreaBR  = min(max(xCurrentCell, xTargetCell) + 2, this.mazeMap[0].size)
        yAreaBR = min(max(yCurrentCell, yTargetCell) + 2, this.mazeMap.size)

        //we determine if there is obstacles and, if so, we calculate with clamping
        //the nearest point, if the distance from the nearest point to the radius is less
        //than the radius it means that a collision occurred so we move the object in the
        //opposite direction * the overlap
        for(i in yAreaTL until yAreaBR){
            for(j in xAreaTL until xAreaBR){
                if(mazeMap[i][j] == 1){
                    val xNearestPoint = max(j*this.widthSquare, min(xPotentialPos, (j+1)*this.widthSquare))
                    val yNearestPoint = max(i*this.widthSquare, min(yPotentialPos, (i+1)*this.widthSquare))

                    val xRayToNearest : Float = xNearestPoint - xPotentialPos
                    val yRayToNearest : Float = yNearestPoint - yPotentialPos
                    var overlap : Float = this.radiusBall - sqrt(xRayToNearest.pow(2) + yRayToNearest.pow(2))

                    //for example 1/0
                    if(overlap.isNaN()) overlap = 0f

                    if(overlap > 0){
                        val normRayToNearest : Float = sqrt(xRayToNearest.pow(2) + yRayToNearest.pow(2))
                        xPotentialPos = xPotentialPos - ((xRayToNearest / normRayToNearest) * overlap)
                        yPotentialPos = yPotentialPos - ((yRayToNearest / normRayToNearest) * overlap)
                    }
                }
            }
        }

        //we change the pos of the ball
        xAxisBall = xPotentialPos
        yAxisBall = yPotentialPos


        //left-limit of the map
        if (xAxisBall < this.getRadiusBall()) { //left
            xAxisBall = this.getRadiusBall()
        }
    }
}