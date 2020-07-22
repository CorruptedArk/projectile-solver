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
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.EditText
import android.widget.NumberPicker
import android.widget.Toast
import androidx.core.view.children
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity()
{
    private val accelSolver = ConstantAccelSolver()

    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val actionBar = supportActionBar
        actionBar!!.subtitle = "1D Solver"
        actionBar!!.setBackgroundDrawable(ColorDrawable(Color.parseColor("#FF232323")))
        window.statusBarColor = Color.parseColor("#ff151515")
        precisionPicker.minValue = 1
        precisionPicker.maxValue = Int.MAX_VALUE
        precisionPicker.wrapSelectorWheel = false

        window.navigationBarColor = Color.parseColor("#ff151515")
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean
    {
        menuInflater.inflate(R.menu.main_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId)
        {
            R.id.aboutItem -> {openAboutActivity(); return true}
            R.id.twoDimensionsItem -> {openTwoDimensionActivity(); return true}
            else -> return super.onOptionsItemSelected(item)
        }

    }

    private fun openTwoDimensionActivity()
    {
        val intent = Intent(this, TwoDimensionsActivity::class.java)
        startActivity(intent)
    }

    private fun openAboutActivity()
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

        val values = mutableListOf(initVelocityString, finalVelocityString, displacementString, accelerationString, timeString)

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

            accelSolver.findAllValues(initVelocity, finalVelocity, displacement, acceleration, time)

            initVelBox.setText(formatOutputString(accelSolver.getInitVelocities()))
            finalVelBox.setText(formatOutputString(accelSolver.getFinalVelocities()))
            displacementBox.setText(formatOutputString(listOf(accelSolver.getDisplacement())))
            accelerationBox.setText(formatOutputString(listOf(accelSolver.getAcceleration())))
            timeBox.setText(formatOutputString(accelSolver.getTimes()))
        }
    }

    private fun formatOutputString(values: List<Double>): String
    {
        var multiFormat = "%." + precisionPicker.value + "f"

        for (i in 1 until values.size)
        {
            multiFormat += ", %." + precisionPicker.value + "f"
        }

        return String.format(multiFormat, *values.toTypedArray())
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

    fun clear(view: View)
    {
        clearEditTextRecursive(view.rootView)
    }

    private fun clearEditTextRecursive(view: View)
    {
        when (view)
        {
            is NumberPicker -> {
                return
            }
            is EditText -> {
                view.text.clear()
            }
            is ViewGroup -> {
                for (child in view.children) {
                    clearEditTextRecursive(child)
                }
            }
        }
    }

}
