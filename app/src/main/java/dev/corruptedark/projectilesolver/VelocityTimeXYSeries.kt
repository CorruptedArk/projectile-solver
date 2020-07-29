package dev.corruptedark.projectilesolver

import com.androidplot.xy.SimpleXYSeries
import kotlin.math.abs

class VelocityTimeXYSeries(acceleration: Double, initialVelocity: Double, minTime: Double, maxTime: Double, size: Int, title: String) : SimpleXYSeries(title)
{
    init {
        val increment = abs(maxTime - minTime) / size

        for (i in 0 until size)
        {
            val xValue = i * increment + minTime
            val yValue = acceleration * xValue + initialVelocity
            addLast(xValue, yValue)
        }
    }
}