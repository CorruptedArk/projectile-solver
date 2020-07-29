package dev.corruptedark.projectilesolver

import com.androidplot.xy.SimpleXYSeries
import kotlin.math.abs
import kotlin.math.sqrt

class VelocityDisplacementXYSeries(acceleration: Double, initialVelocity: Double, minDisplacement: Double, maxDisplacement: Double, positiveSeries: Boolean, size: Int, title: String) : SimpleXYSeries(title)
{

    init {
        val increment = abs(maxDisplacement - minDisplacement) / size

        for (i in 0 until size)
        {
            val xValue = i * increment + minDisplacement
            val yValue = sqrt(abs(initialVelocity * initialVelocity + 2 * acceleration * xValue))

            if(positiveSeries)
                addLast(xValue, yValue)
            else
                addLast(xValue, -yValue)
        }
    }

}