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