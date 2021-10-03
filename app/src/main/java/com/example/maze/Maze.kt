package com.example.maze

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.util.AttributeSet
import android.util.DisplayMetrics
import android.view.View
import androidx.annotation.Nullable
import kotlin.math.*
import kotlin.properties.Delegates

class Maze(context: Context, @Nullable attributeSet: AttributeSet) : View(context), SensorEventListener {
    private var screenHeight by Delegates.notNull<Float>()
    private var screenWidth by Delegates.notNull<Float>()
    private var maze : MazeModel
    private var mazeMap : Array<IntArray>
    private var paintSquare = Paint()
    private var paintBall = Paint()
    private var paintLine = Paint()
    private var widthSquare : Float
    private var yAxisBall : Float
    private var xAxisBall : Float
    private var yVelBall : Float
    private var xVelBall : Float
    private var radiusBall : Float
    private var xAreaTL : Int
    private var yAreaTL : Int
    private var xAreaBR : Int
    private var yAreaBR : Int


    init {
        // get screen dimensions
        val metrics : DisplayMetrics = this.resources.displayMetrics
        screenWidth = metrics.widthPixels.toFloat()
        screenHeight = metrics.heightPixels.toFloat()

        //get mazeModel obj
        maze = MazeModel()

        //map
        mazeMap = maze.getMazeMap()
        widthSquare = screenWidth / mazeMap[0].size

        //ball
        radiusBall = widthSquare / 2
        xAxisBall = 36f
        yAxisBall = radiusBall * 5
        xVelBall = 0f
        yVelBall = 0f
        val smAdministrator = context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
        val snsRotation = smAdministrator.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
        smAdministrator.registerListener(this, snsRotation, SensorManager.SENSOR_DELAY_FASTEST)

        //cellsHighlighted
        xAreaTL = -1
        yAreaTL = -1
        xAreaBR = -1
        yAreaBR = -1

        //paint objects
        paintLine.setColor(Color.GRAY)
        paintLine.setStrokeWidth(3f)

    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        for(i in mazeMap.indices){
            for(j in mazeMap[0].indices){
                if(mazeMap[i][j] == 1){
                    val(left, top, right, bottom) = arrayOf(j*widthSquare, i*widthSquare, (j+1)*widthSquare, (i+1)*widthSquare)
                    // fill
                    paintSquare.setAlpha(255)
                    paintSquare.setStyle(Paint.Style.FILL);
                    paintSquare.setColor(Color.BLACK);
                    canvas.drawRect(left, top, right, bottom, paintSquare)
                    // border
                    paintSquare.setStyle(Paint.Style.STROKE);
                    paintSquare.setStrokeWidth(3f)
                    paintSquare.setColor(Color.GRAY);
                    canvas.drawRect(left, top, right, bottom, paintSquare)

                    canvas.drawLine(left, top, right, bottom, paintLine)
                    canvas.drawLine(left, bottom, right, top, paintLine)
                }
            }
        }

        //ball.draw(canvas)
        paintBall.color = Color.RED
        canvas.drawCircle(xAxisBall, yAxisBall, radiusBall, paintBall)

        //if(xAreaTL != -1){
            //paintSquare.setStyle(Paint.Style.FILL);
            //paintSquare.setColor(Color.GREEN);
            //paintSquare.setAlpha(200)
            //canvas.drawRect(xAreaTL*widthSquare, yAreaTL*widthSquare, (xAreaBR + 1)*widthSquare, (yAreaBR + 1)*widthSquare, paintSquare)
            //canvas.drawRect(xAreaTL*widthSquare, yAreaTL*widthSquare, (10)*widthSquare, (10)*widthSquare, paintSquare)

        //}

        //unit vector
        if(abs(xVelBall)*abs(yVelBall) > 0){
            val xNorm = xVelBall / sqrt(xVelBall.pow(2) + yVelBall.pow(2))
            val yNorm = yVelBall / sqrt(xVelBall.pow(2) + yVelBall.pow(2))
            canvas.drawLine(xAxisBall, yAxisBall, xAxisBall + xNorm * radiusBall, yAxisBall + yNorm * radiusBall,paintLine)
        }
        invalidate()

    }

    override fun onSensorChanged(change: SensorEvent?) {
        xVelBall = change!!.values[0] * -1f
        yVelBall = change!!.values[1]
        var xPotentialPos = xAxisBall + xVelBall
        var yPotentialPos = yAxisBall + yVelBall

        val xCurrentCell : Int = (floor(xAxisBall / widthSquare)).toInt()
        val yCurrentCell : Int = (floor(yAxisBall / widthSquare)).toInt()
        val xTargetCell : Int = (xPotentialPos / widthSquare).toInt()
        val yTargetCell : Int = (yPotentialPos / widthSquare).toInt()




        xAreaTL = max(min(xCurrentCell, xTargetCell) - 1, 0)
        yAreaTL = max(min(yCurrentCell, yTargetCell) - 1, 0)
        xAreaBR  = min(max(xCurrentCell, xTargetCell) + 2, mazeMap[0].size)
        yAreaBR = min(max(yCurrentCell, yTargetCell) + 2, mazeMap[0].size)

        for(i in yAreaTL until yAreaBR){
            for(j in xAreaTL until xAreaBR){
                if(mazeMap[i][j] == 1){
                    val xNearestPoint = max(j*widthSquare, min(xPotentialPos, (j+1)*widthSquare))
                    val yNearestPoint = max(i*widthSquare, min(yPotentialPos, (i+1)*widthSquare))

                    val xRayToNearest : Float = xNearestPoint - xPotentialPos
                    val yRayToNearest : Float = yNearestPoint - yPotentialPos
                    var overlap : Float = radiusBall - sqrt(xRayToNearest.pow(2) + yRayToNearest.pow(2))

                    if(overlap.isNaN()) overlap = 0f

                    if(overlap > 0){
                        println(overlap)
                        val normRayToNearest : Float = sqrt(xRayToNearest.pow(2) + yRayToNearest.pow(2))
                        xPotentialPos = xPotentialPos - ((xRayToNearest / normRayToNearest) * overlap)
                        yPotentialPos = yPotentialPos - ((yRayToNearest / normRayToNearest) * overlap)
                    }
                }
            }
        }


        xAxisBall = xPotentialPos
        yAxisBall = yPotentialPos


        if (xAxisBall < radiusBall) { //left
            xAxisBall = radiusBall
        } else if (xAxisBall > screenWidth) { //right
            xAxisBall = (screenWidth)
        }
        if (yAxisBall < 0 ) { //top
            yAxisBall = 0f
        } else if (yAxisBall > screenHeight) { //bottom
            yAxisBall = screenHeight
        }

    }

    override fun onAccuracyChanged(p0: Sensor?, p1: Int) { }

}