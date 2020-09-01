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
import android.graphics.PathEffect
import android.graphics.drawable.ColorDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.core.view.children
import com.androidplot.util.PixelUtils
import com.androidplot.xy.*
import dev.corruptedark.projectilesolver.databinding.ActivityMainBinding
import kotlinx.android.synthetic.main.activity_main.*
import kotlin.math.abs


class MainActivity : AppCompatActivity()
{
    private val accelSolver = ConstantAccelSolver()
    private var solved = false
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?)
    {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val actionBar = supportActionBar
        actionBar!!.subtitle = "1D Solver"
        actionBar!!.setBackgroundDrawable(ColorDrawable(Color.parseColor("#FF232323")))
        window.statusBarColor = Color.parseColor("#ff151515")
        binding.precisionPicker.minValue = 1
        binding.precisionPicker.maxValue = Int.MAX_VALUE
        binding.precisionPicker.wrapSelectorWheel = false

        window.navigationBarColor = Color.parseColor("#ff151515")

        binding.domainRadioGroup.check(R.id.timeDomainButton)
        binding.rangeRadioGroup.check(R.id.displacementRangeButton)

        binding.domainRadioGroup.setOnCheckedChangeListener { _, _ ->
            if (solved) {
                updatePlot()
            }
        }

        binding.rangeRadioGroup.setOnCheckedChangeListener { _, _ ->
            if (solved) {
                updatePlot()
            }
        }
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
        val initVelocityString = binding.initVelBox.text.toString()
        val finalVelocityString = binding.finalVelBox.text.toString()
        val displacementString = binding.displacementBox.text.toString()
        val accelerationString = binding.accelerationBox.text.toString()
        val timeString = binding.timeBox.text.toString()

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

            binding.initVelBox.setText(formatOutputString(accelSolver.getInitVelocities()))
            binding.finalVelBox.setText(formatOutputString(accelSolver.getFinalVelocities()))
            binding.displacementBox.setText(formatOutputString(listOf(accelSolver.getDisplacement())))
            binding.accelerationBox.setText(formatOutputString(listOf(accelSolver.getAcceleration())))
            binding.timeBox.setText(formatOutputString(accelSolver.getTimes()))

            solved = true
            updatePlot()
        }
    }

    private fun updatePlot()
    {
        binding.oneDimensionPlot.clear()
        val minDomain = binding.domainMinBox.text.toString().toDoubleOrNull() ?: -1.0
        val maxDomain = binding.domainMaxBox.text.toString().toDoubleOrNull() ?: 1.0

        val formatter = LineAndPointFormatter(Color.parseColor("#00A2FF"), null, null, null)
        formatter.interpolationParams = CatmullRomInterpolator.Params(10, CatmullRomInterpolator.Type.Centripetal)

        /*val pointLabelFormatter = PointLabelFormatter(Color.WHITE)
        val pointFormatter = LineAndPointFormatter(null, Color.WHITE, null, pointLabelFormatter)*/
        when(listOf(binding.domainRadioGroup.checkedRadioButtonId, binding.rangeRadioGroup.checkedRadioButtonId))
        {
            listOf(R.id.timeDomainButton, R.id.displacementRangeButton) ->
            {
                binding.oneDimensionPlot.addSeries(formatter,accelSolver.getDisplacementTimeXYSeries(minDomain, maxDomain, 100, "Δx vs t"))
                binding.oneDimensionPlot.domainTitle.text = "Domain t"
                binding.oneDimensionPlot.rangeTitle.text = "Range Δx"


                val yList = mutableListOf<Double>()
                for (time in accelSolver.getTimes())
                    yList.add(accelSolver.getDisplacement())

                /*val pointSeries = SimpleXYSeries(accelSolver.getTimes(), yList, "")

                oneDimensionPlot.addSeries(pointSeries, pointFormatter)*/
            }
            listOf(R.id.displacementDomainButton, R.id.displacementRangeButton) ->
            {
                val samplesList = mutableListOf<Double>()
                val size = 4
                for (i in 0..size)
                   samplesList.add(minDomain + i.toDouble()/size.toDouble()*abs(maxDomain - minDomain))

                binding.oneDimensionPlot.addSeries(formatter, SimpleXYSeries( samplesList,  samplesList, "Δx vs Δx"))
                binding.oneDimensionPlot.domainTitle.text = "Domain Δx"
                binding.oneDimensionPlot.rangeTitle.text = "Range Δx"


                /*val pointSeries = SimpleXYSeries(mutableListOf(accelSolver.getDisplacement()), mutableListOf(accelSolver.getDisplacement()), "")

                oneDimensionPlot.addSeries(pointSeries, pointFormatter)*/
            }
            listOf(R.id.timeDomainButton, R.id.velocityRangeButton) ->
            {
                binding.oneDimensionPlot.addSeries(formatter, accelSolver.getVelocityTimeXYSeries(minDomain, maxDomain, 100, "V vs t"))
                binding.oneDimensionPlot.domainTitle.text = "Domain t"
                binding.oneDimensionPlot.rangeTitle.text = "Range V"

                /*val pointSeries = SimpleXYSeries("")

                for (time in accelSolver.getTimes())
                    for (velocity in accelSolver.getFinalVelocities())
                        pointSeries.addLast(time, velocity)

                oneDimensionPlot.addSeries(pointSeries, pointFormatter)*/
            }
            listOf(R.id.displacementDomainButton, R.id.velocityRangeButton) ->
            {
                val formatter2 = LineAndPointFormatter(Color.MAGENTA, null, null, null)
                formatter2.interpolationParams = CatmullRomInterpolator.Params(10, CatmullRomInterpolator.Type.Centripetal)

                val seriesArray = accelSolver.getVelocityDisplacementXYSeries(minDomain, maxDomain, 100, "V vs Δx")
                binding.oneDimensionPlot.addSeries(formatter, seriesArray[0])
                binding.oneDimensionPlot.addSeries (formatter2, seriesArray.last())
                binding.oneDimensionPlot.domainTitle.text = "Domain Δx"
                binding.oneDimensionPlot.rangeTitle.text = "Range V"

                val xList = mutableListOf<Double>()
                for (velocity in accelSolver.getFinalVelocities())
                    xList.add(accelSolver.getDisplacement())

                /*val pointSeries = SimpleXYSeries(xList, accelSolver.getFinalVelocities(), "")

                oneDimensionPlot.addSeries(pointSeries, pointFormatter)*/
            }
            else -> binding.oneDimensionPlot.addSeries(formatter, SimpleXYSeries(""))
        }

        binding.oneDimensionPlot.redraw()
    }


    private fun formatOutputString(values: List<Double>): String
    {
        var multiFormat = "%." + binding.precisionPicker.value + "f"

        for (i in 1 until values.size)
        {
            multiFormat += ", %." + binding.precisionPicker.value + "f"
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

        solved = false
        binding.domainMinBox.setText("-1")
        binding.domainMaxBox.setText("1")
        binding.oneDimensionPlot.domainTitle.text = ""
        binding.oneDimensionPlot.rangeTitle.text = ""
        binding.oneDimensionPlot.clear()
        binding.oneDimensionPlot.redraw()
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
