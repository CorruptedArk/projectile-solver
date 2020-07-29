package dev.corruptedark.projectilesolver

import com.androidplot.xy.SimpleXYSeries
import kotlin.math.abs
import kotlin.math.pow
import kotlin.math.sqrt

class VelocityDisplacementTwoAxisXYSeries (acceleration1: Double, acceleration2: Double, initialVelocity1: Double, initialVelocity2: Double, minDisplacement: Double, maxDisplacement: Double, positiveSeries: Boolean, size: Int, title: String) : SimpleXYSeries(title)
{

    init {

        val increment = abs(maxDisplacement - minDisplacement) / size

        for (i in 0 until size)
        {
            val xValue = i * increment + minDisplacement

            var xSqrt = sqrt(abs(initialVelocity1.pow(2) + 2 * acceleration1 * xValue))

            if (!positiveSeries)
                xSqrt = -xSqrt

            val yValue = (acceleration2 / acceleration1) * (-initialVelocity1 + xSqrt) + initialVelocity2

            addLast(xValue, yValue)
        }

    }

}