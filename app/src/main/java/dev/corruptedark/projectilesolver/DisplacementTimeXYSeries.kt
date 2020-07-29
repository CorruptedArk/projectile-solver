package dev.corruptedark.projectilesolver

import com.androidplot.xy.SimpleXYSeries
import kotlin.math.abs

class DisplacementTimeXYSeries(acceleration: Double, initialVelocity: Double, minTime: Double, maxTime: Double, size: Int, title: String) :
    SimpleXYSeries(title) {


    init {
        val increment = abs(maxTime - minTime) / size

        for (i in 0 until size)
        {
            val xValue = i * increment + minTime
            val yValue = acceleration * xValue * xValue / 2.0 + initialVelocity * xValue
            addLast(xValue, yValue)
        }
    }


}