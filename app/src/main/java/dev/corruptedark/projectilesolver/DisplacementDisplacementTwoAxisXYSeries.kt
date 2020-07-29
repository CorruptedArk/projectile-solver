/**
 *
Projectile Solver is an Android application to solve projectile systems with constant acceleration.
In order to solve any system, 3 of 5 known variables are required.
However, this program does not care which 3 are provided.
    Copyright (C) 2020  Noah Stanford <noahstandingford@gmail.com>

    Projectile Solver is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    Projectile Solver is distributed in the hope that it will be useful and educational,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with this program.  If not, see <https://www.gnu.org/licenses/>.
*/

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