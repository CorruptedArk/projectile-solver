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

import com.androidplot.xy.XYSeries

class ConstantAccelSolver {

    //Ext suffix stands for extraneous
    private var initVelocity = 0.0; private var initVelocityExt = 0.0
    private var finalVelocity = 0.0; private var finalVelocityExt = 0.0
    private var displacement = 0.0
    private var acceleration = 0.0
    private var time = 0.0; private var timeExt = 0.0

    private var hasInitVelExt = false
    private var hasFinalVelExt = false
    private var hasTimeExt = false

    fun getDisplacementDisplacementTwoAxisXYSeries(solver2: ConstantAccelSolver, minDomain: Double, maxDomain: Double, size: Int, title: String): Array<XYSeries>
    {
        return arrayOf(DisplacementDisplacementTwoAxisXYSeries(this.acceleration, solver2.acceleration, this.initVelocity, solver2.initVelocity, minDomain, maxDomain, true, size, title),
            DisplacementDisplacementTwoAxisXYSeries(this.acceleration, solver2.acceleration, this.initVelocity, solver2.initVelocity, minDomain, maxDomain, false, size, title))
    }

    fun getVelocityDisplacementTwoAxisXYSeries(solver2: ConstantAccelSolver, minDomain: Double, maxDomain: Double, size: Int, title: String): Array<XYSeries>
    {
        return  arrayOf(DisplacementDisplacementTwoAxisXYSeries(this.acceleration, solver2.acceleration, this.initVelocity, solver2.initVelocity, minDomain, maxDomain, true, size, title),
            DisplacementDisplacementTwoAxisXYSeries(this.acceleration, solver2.acceleration, this.initVelocity, solver2.initVelocity, minDomain, maxDomain, false, size, title))
    }

    fun getDisplacementTimeXYSeries(minTime: Double, maxTime: Double, size: Int, title: String): DisplacementTimeXYSeries
    {
        return DisplacementTimeXYSeries(acceleration, initVelocity, minTime, maxTime, size, title)
    }

    fun getVelocityTimeXYSeries(minTime: Double, maxTime: Double, size: Int, title: String): VelocityTimeXYSeries
    {
        return VelocityTimeXYSeries(acceleration, initVelocity, minTime, maxTime, size, title)
    }

    fun getVelocityDisplacementXYSeries(minDisplacement: Double, maxDisplacement: Double, size: Int, title: String): Array<XYSeries>
    {
        return arrayOf(VelocityDisplacementXYSeries(acceleration, initVelocity, minDisplacement, maxDisplacement, true, size, "+$title"),
            VelocityDisplacementXYSeries(acceleration, initVelocity, minDisplacement, maxDisplacement, false, size, "-$title"))
    }

    fun getInitVelocities(): MutableList<Double>
    {
        var velocities = mutableListOf(initVelocity)
        if (hasInitVelExt){
            velocities.add(initVelocityExt)
        }

        return velocities
    }

    fun getFinalVelocities(): MutableList<Double>
    {
        var velocities = mutableListOf(finalVelocity)
        if (hasFinalVelExt){
            velocities.add(finalVelocityExt)
        }

        return velocities
    }

    fun getDisplacement(): Double
    {
        return displacement
    }

    fun getAcceleration(): Double
    {
        return acceleration
    }

    fun getTimes(): MutableList<Double>
    {
        var times = mutableListOf(time)

        if (hasTimeExt)
        {
            times.add(timeExt)
        }

        return times
    }

    private fun areAllNull(vararg values: Any?): Boolean
    {
        var areNull = true
        for (value in values)
        {
            if (value != null)
            {
                areNull = false
                break
            }
        }

        return areNull
    }

    fun findAllValues(initVelocity: Double?, finalVelocity: Double?, displacement: Double?, acceleration: Double?, time: Double?)
    {
        when {
            areAllNull(finalVelocity, displacement) -> findFinalVelAndDisplacement(initVelocity!!, acceleration!!, time!!)
            areAllNull(initVelocity, displacement) -> findInitVelAndDisplacement(finalVelocity!!, acceleration!!, time!!)
            areAllNull(acceleration, displacement) -> findAccelerationAndDisplacement(initVelocity!!, finalVelocity!!, time!!)
            areAllNull(time, displacement) -> findTimeAndDisplacement(initVelocity!!, finalVelocity!!, acceleration!!)
            areAllNull(initVelocity, acceleration) -> findInitVelAndAcceleration(finalVelocity!!, displacement!!, time!!)
            areAllNull(finalVelocity, acceleration) -> findFinalVelAndAcceleration(initVelocity!!, displacement!!, time!!)
            areAllNull(time, acceleration) -> findTimeAndAcceleration(initVelocity!!, finalVelocity!!, displacement!!)
            areAllNull(initVelocity, time) -> findInitVelAndTime(finalVelocity!!, displacement!!, acceleration!!)
            areAllNull(finalVelocity, time) -> findFinalVelAndTime(initVelocity!!, displacement!!, acceleration!!)
            areAllNull(initVelocity, finalVelocity) -> findInitVelAndFinalVel(displacement!!, acceleration!!, time!!)
        }
    }

    private fun findFinalVelAndDisplacement(initVelocity: Double, acceleration: Double, duration: Double)
    {
        this.hasInitVelExt = false
        this.hasFinalVelExt = false
        this.hasTimeExt = false

        this.initVelocity = initVelocity
        this.acceleration = acceleration
        this.time = duration
        this.finalVelocity = initVelocity + acceleration * duration
        this.displacement = initVelocity * duration + .5 * acceleration * Math.pow(duration, 2.0)
    }

    private fun findInitVelAndDisplacement(finalVelocity: Double, acceleration: Double, duration: Double)
    {
        this.hasInitVelExt = false
        this.hasFinalVelExt = false
        this.hasTimeExt = false

        this.finalVelocity = finalVelocity
        this.acceleration = acceleration
        this.time = duration
        this.initVelocity = finalVelocity - acceleration * duration
        this.displacement = finalVelocity * duration - .5 * acceleration * Math.pow(duration, 2.0)
    }

    private fun findAccelerationAndDisplacement(initVelocity: Double, finalVelocity: Double, duration: Double)
    {
        this.hasInitVelExt = false
        this.hasFinalVelExt = false
        this.hasTimeExt = false

        this.initVelocity = initVelocity
        this.finalVelocity = finalVelocity
        this.time = duration
        this.displacement = .5 * (finalVelocity + initVelocity) * duration
        this.acceleration = (finalVelocity - initVelocity) / duration
    }

    private fun findTimeAndDisplacement(initVelocity: Double, finalVelocity: Double, acceleration: Double)
    {
        this.hasInitVelExt = false
        this.hasFinalVelExt = false
        this.hasTimeExt = false

        this.initVelocity = initVelocity
        this.finalVelocity = finalVelocity
        this.acceleration = acceleration
        this.time = (finalVelocity - initVelocity) / acceleration
        this.displacement = (Math.pow(finalVelocity, 2.0) - Math.pow(initVelocity, 2.0)) / (2.0*acceleration)
    }

    private fun findInitVelAndAcceleration(finalVelocity: Double, displacement: Double, duration: Double)
    {
        this.hasInitVelExt = false
        this.hasFinalVelExt = false
        this.hasTimeExt = false

        this.finalVelocity = finalVelocity
        this.displacement = displacement
        this.time = duration
        this.initVelocity = 2.0 * displacement / duration - finalVelocity
        this.acceleration = 2.0 * (finalVelocity * duration - displacement) / Math.pow(duration, 2.0)
    }

    private fun findFinalVelAndAcceleration(initVelocity: Double, displacement: Double, duration: Double)
    {
        this.hasInitVelExt = false
        this.hasFinalVelExt = false
        this.hasTimeExt = false

        this.initVelocity = initVelocity
        this.displacement = displacement
        this.time = duration
        this.finalVelocity = 2.0 * displacement / duration - initVelocity
        this.acceleration = 2.0 * (displacement - initVelocity * duration) / Math.pow(duration, 2.0)
    }

    private fun findTimeAndAcceleration(initVelocity: Double, finalVelocity: Double, displacement: Double)
    {
        this.hasInitVelExt = false
        this.hasFinalVelExt = false
        this.hasTimeExt = false

        this.initVelocity = initVelocity
        this.finalVelocity = finalVelocity
        this.displacement = displacement
        this.acceleration = (Math.pow(finalVelocity, 2.0) - Math.pow(initVelocity, 2.0)) / (2.0 * displacement)
        this.time = 2.0 * displacement / (finalVelocity + initVelocity)
    }

    private fun findInitVelAndTime(finalVelocity: Double, displacement: Double, acceleration: Double)
    {
        this.hasInitVelExt = true
        this.hasFinalVelExt = false
        this.hasTimeExt = true

        this.finalVelocity = finalVelocity
        this.displacement = displacement
        this.acceleration = acceleration

        this.initVelocityExt = Math.sqrt(Math.pow(finalVelocity,2.0) - 2.0 * acceleration * displacement)
        this.initVelocity = -this.initVelocityExt

        if ((finalVelocity + this.initVelocityExt).equals(0.0))
        {
            this.timeExt = (finalVelocity - this.initVelocityExt) / acceleration
        }
        else
        {
            this.timeExt = 2.0 * displacement / (finalVelocity + this.initVelocityExt)
        }

        if ((finalVelocity + initVelocity).equals(0.0))
        {
            this.time = (finalVelocity - this.initVelocity) / acceleration
        }
        else
        {
            this.time = 2.0 * displacement / (finalVelocity + this.initVelocity)
        }
    }

    private fun findFinalVelAndTime(initVelocity: Double, displacement: Double, acceleration: Double)
    {
        this.hasInitVelExt = false
        this.hasFinalVelExt = true
        this.hasTimeExt = true

        this.initVelocity = initVelocity
        this.displacement = displacement
        this.acceleration = acceleration

        this.finalVelocityExt = Math.sqrt(Math.pow(initVelocity, 2.0) + 2.0 * acceleration * displacement)
        this.finalVelocity = -this.finalVelocityExt

        if ((this.finalVelocityExt + initVelocity).equals(0.0))
        {
            this.timeExt = (this.finalVelocityExt - initVelocity) / acceleration
        }
        else
        {
            this.timeExt = 2.0 * displacement / (this.finalVelocityExt + initVelocity)
        }

        if ((this.finalVelocity + initVelocity).equals(0.0))
        {
            this.time = (this.finalVelocity - initVelocity) / acceleration
        }
        else
        {
            this.time = 2.0 * displacement / (this.finalVelocity + initVelocity)
        }
    }

    private fun findInitVelAndFinalVel(displacement: Double, acceleration: Double, duration: Double)
    {
        this.hasInitVelExt = false
        this.hasFinalVelExt = false
        this.hasTimeExt = false

        this.displacement = displacement
        this.acceleration = acceleration
        this.time = duration
        this.initVelocity = (displacement - .5 * acceleration * Math.pow(duration, 2.0)) / duration
        this.finalVelocity = (displacement + .5 * acceleration * Math.pow(duration, 2.0)) / duration
    }
}