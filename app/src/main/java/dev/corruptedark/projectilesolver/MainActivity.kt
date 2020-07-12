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

import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity()
{
    private val accelSolver = ConstantAccelSolver()

    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val actionBar = supportActionBar
        actionBar!!.subtitle = "Main"
        actionBar!!.setBackgroundDrawable(ColorDrawable(Color.parseColor("#FF232323")))
        window.statusBarColor = Color.parseColor("#ff151515")
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean
    {
        menuInflater.inflate(R.menu.menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId)
        {
            R.id.aboutItem -> {openAboutActivity(); return true}
            else -> return super.onOptionsItemSelected(item)
        }

    }

    fun openAboutActivity()
    {
        val intent = Intent(this, AboutActivity::class.java)
        startActivity(intent)
    }

    fun solveSystem(view: View)
    {
        val initVelocityString = initVelBox.text.toString()
        val finalVelocityString = finalVelBox.text.toString()
        val displacementString = displacementBox.text.toString()
        val accelerationString = accelerationBox.text.toString()
        val timeString = timeBox.text.toString()

        var values = mutableListOf(initVelocityString, finalVelocityString, displacementString, accelerationString, timeString)

        var emptyCount = 0
        for (value in values)
        {
            if (value.toDoubleOrNull() == null)
                emptyCount++
        }

        if (emptyCount > 2)
        {
            Toast.makeText(this, "Error: Not enough values", Toast.LENGTH_SHORT).show()
        }
        else
        {
            val initVelocity = initVelocityString.toDoubleOrNull()
            val finalVelocity = finalVelocityString.toDoubleOrNull()
            val displacement = displacementString.toDoubleOrNull()
            val acceleration = accelerationString.toDoubleOrNull()
            val time = timeString.toDoubleOrNull()

            when {
                areAllNull(listOf(finalVelocity, displacement)) -> accelSolver.findFinalVelAndDisplacement(initVelocity!!, acceleration!!, time!!)
                areAllNull(listOf(initVelocity, displacement)) -> accelSolver.findInitVelAndDisplacement(finalVelocity!!, acceleration!!, time!!)
                areAllNull(listOf(acceleration, displacement)) -> accelSolver.findAccelerationAndDisplacement(initVelocity!!, finalVelocity!!, time!!)
                areAllNull(listOf(time, displacement)) -> accelSolver.findTimeAndDisplacement(initVelocity!!, finalVelocity!!, acceleration!!)
                areAllNull(listOf(initVelocity, acceleration)) -> accelSolver.findInitVelAndAcceleration(finalVelocity!!, displacement!!, time!!)
                areAllNull(listOf(finalVelocity, acceleration)) -> accelSolver.findFinalVelAndAcceleration(initVelocity!!, displacement!!, time!!)
                areAllNull(listOf(time, acceleration)) -> accelSolver.findTimeAndAcceleration(initVelocity!!, finalVelocity!!, displacement!!)
                areAllNull(listOf(initVelocity, time)) -> accelSolver.findInitVelAndTime(finalVelocity!!, displacement!!, acceleration!!)
                areAllNull(listOf(finalVelocity, time)) -> accelSolver.findFinalVelAndTime(initVelocity!!, displacement!!, acceleration!!)
                areAllNull(listOf(initVelocity, finalVelocity)) -> accelSolver.findInitVelAndFinalVel(displacement!!, acceleration!!, time!!)
            }

            val initVelocities = accelSolver.getInitVelocities()
            if (initVelocities.size > 1)
                initVelBox.setText(String.format("%.2f, %.2f", initVelocities[0], initVelocities[1]))
            else
                initVelBox.setText(String.format("%.2f", initVelocities[0]))

            val finalVelocities = accelSolver.getFinalVelocities()
            if (finalVelocities.size > 1)
                finalVelBox.setText(String.format("%.2f, %.2f", finalVelocities[0], finalVelocities[1]))
            else
                finalVelBox.setText(String.format("%.2f", finalVelocities[0]))

            displacementBox.setText(String.format("%.2f",accelSolver.getDisplacement()))

            accelerationBox.setText(String.format("%.2f", accelSolver.getAcceleration()))

            val times = accelSolver.getTimes()

            if(times.size > 1)
                timeBox.setText(String.format("%.2f, %.2f", times[0], times[1]))
            else
                timeBox.setText(String.format("%.2f", times[0]))
        }
    }

    private fun areAllNull(values: List<Any?>): Boolean
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

    fun clear(view: View)
    {
        initVelBox.text.clear()
        finalVelBox.text.clear()
        displacementBox.text.clear()
        accelerationBox.text.clear()
        timeBox.text.clear()
    }

}
