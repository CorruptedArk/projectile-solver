package dev.corruptedark.projectilesolver

import com.androidplot.xy.SimpleXYSeries
import kotlin.math.abs
import kotlin.math.pow
import kotlin.math.sqrt

class DisplacementDisplacementTwoAxisXYSeries(acceleration1: Double, acceleration2: Double, initialVelocity1: Double, initialVelocity2: Double, minDomain: Double, maxDomain: Double, positiveSeries: Boolean, size: Int, title: String) : SimpleXYSeries(title)
{

    init {

        val increment = abs(maxDomain - minDomain) / size

        for (i in 0 until size)
        {
            val xValue = i * increment + minDomain

            var xSqrt = sqrt(abs(initialVelocity1.pow(2) + 2 * acceleration1 * xValue))

            if (!positiveSeries)
                xSqrt = -xSqrt

            val yValue: Double

            yValue = if(acceleration1 == 0.0)
                (acceleration2 * xValue.pow(2)) / (2 * initialVelocity1.pow(2)) + initialVelocity2 * xValue / initialVelocity1
            else
                (acceleration2 / acceleration1.pow(2)) * (initialVelocity1 + acceleration1 * xValue + initialVelocity1 * xSqrt ) - (initialVelocity2 / acceleration1) * (initialVelocity1 + xSqrt)

            addLast(xValue, yValue)
        }

    }

}