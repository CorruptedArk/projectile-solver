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
import kotlinx.android.synthetic.main.activity_main.*
import kotlin.math.abs


class MainActivity : AppCompatActivity()
{
    private val accelSolver = ConstantAccelSolver()
    private var solved = false

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

        domainRadioGroup.check(R.id.timeDomainButton)
        rangeRadioGroup.check(R.id.displacementRangeButton)

        domainRadioGroup.setOnCheckedChangeListener { _, _ ->
            if (solved) {
                updatePlot()
            }
        }

        rangeRadioGroup.setOnCheckedChangeListener { _, _ ->
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
        return when(item.itemId) {
            R.id.aboutItem -> {openAboutActivity(); true
            }
            R.id.twoDimensionsItem -> {openTwoDimensionActivity(); true
            }
            else -> super.onOptionsItemSelected(item)
        }

    }


    /**
     * Starts an instance of TwoDimensionsActivity
     */
    private fun openTwoDimensionActivity()
    {
        val intent = Intent(this, TwoDimensionsActivity::class.java)
        startActivity(intent)
    }

    /**
     * Starts an instance of AboutActivity
     */
    private fun openAboutActivity()
    {
        val intent = Intent(this, AboutActivity::class.java)
        startActivity(intent)
    }

    /**
     * Solves the projectile system represented by the values in the text fields and then fills in the missing values in the UI.
     *
     * @param [view] The required view parameter to use in a view's onClick. Unused by this function.
     */
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

            solved = true
            updatePlot()
        }
    }

    /**
     * Redraws [oneDimensionPlot] with the with a value graph selected by [domainRadioGroup] and [rangeRadioGroup].
     * All graphs are modeled by [accelSolver] and the window bounds are set by [domainMinBox] and [domainMaxBox] along with the min and max range values of each series.
     */
    private fun updatePlot()
    {
        oneDimensionPlot.clear()
        val minDomain = domainMinBox.text.toString().toDoubleOrNull() ?: -1.0
        val maxDomain = domainMaxBox.text.toString().toDoubleOrNull() ?: 1.0

        val formatter = LineAndPointFormatter(Color.parseColor("#00A2FF"), null, null, null)
        formatter.interpolationParams = CatmullRomInterpolator.Params(10, CatmullRomInterpolator.Type.Centripetal)

        /*val pointLabelFormatter = PointLabelFormatter(Color.WHITE)
        val pointFormatter = LineAndPointFormatter(null, Color.WHITE, null, pointLabelFormatter)*/
        when(listOf(domainRadioGroup.checkedRadioButtonId, rangeRadioGroup.checkedRadioButtonId))
        {
            listOf(R.id.timeDomainButton, R.id.displacementRangeButton) ->
            {
                oneDimensionPlot.addSeries(formatter,accelSolver.getDisplacementTimeXYSeries(minDomain, maxDomain, 100, "Δx vs t"))
                oneDimensionPlot.domainTitle.text = "Domain t"
                oneDimensionPlot.rangeTitle.text = "Range Δx"


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

                oneDimensionPlot.addSeries(formatter, SimpleXYSeries( samplesList,  samplesList, "Δx vs Δx"))
                oneDimensionPlot.domainTitle.text = "Domain Δx"
                oneDimensionPlot.rangeTitle.text = "Range Δx"


                /*val pointSeries = SimpleXYSeries(mutableListOf(accelSolver.getDisplacement()), mutableListOf(accelSolver.getDisplacement()), "")

                oneDimensionPlot.addSeries(pointSeries, pointFormatter)*/
            }
            listOf(R.id.timeDomainButton, R.id.velocityRangeButton) ->
            {
                oneDimensionPlot.addSeries(formatter, accelSolver.getVelocityTimeXYSeries(minDomain, maxDomain, 100, "V vs t"))
                oneDimensionPlot.domainTitle.text = "Domain t"
                oneDimensionPlot.rangeTitle.text = "Range V"

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
                oneDimensionPlot.addSeries(formatter, seriesArray[0])
                oneDimensionPlot.addSeries (formatter2, seriesArray.last())
                oneDimensionPlot.domainTitle.text = "Domain Δx"
                oneDimensionPlot.rangeTitle.text = "Range V"

                val xList = mutableListOf<Double>()
                for (velocity in accelSolver.getFinalVelocities())
                    xList.add(accelSolver.getDisplacement())

                /*val pointSeries = SimpleXYSeries(xList, accelSolver.getFinalVelocities(), "")

                oneDimensionPlot.addSeries(pointSeries, pointFormatter)*/
            }
            else -> oneDimensionPlot.addSeries(formatter, SimpleXYSeries(""))
        }

        oneDimensionPlot.redraw()
    }

    /**
     * Creates a formatted String for outputting solved values.
     * Precision is set by [precisionPicker].
     * @param [values] The list of Double values to formatted
     * @return String of formatted values
     */
    private fun formatOutputString(values: List<Double>): String
    {
        var multiFormat = "%." + precisionPicker.value + "f"

        for (i in 1 until values.size)
        {
            multiFormat += ", %." + precisionPicker.value + "f"
        }

        return String.format(multiFormat, *values.toTypedArray())
    }

    /**
     * Checks if all arguments passed are null.
     *
     * @param [items] A vararg containing all arguments passed.
     * @return true if all are null, false otherwise
     */
    private fun areAllNull(vararg items: Any?): Boolean
    {
        var areNull = true
        for (item in items)
        {
            if (item != null)
            {
                areNull = false
                break
            }
        }

        return areNull
    }

    /**
     * Returns all EditText fields and [oneDimensionPlot] to their default states.
     *
     * @param [view] The required view parameter to use in a view's onClick. Unused by this function.
     */
    fun clear(view: View)
    {
        clearEditTextRecursive(view.rootView)

        solved = false
        domainMinBox.setText("-1")
        domainMaxBox.setText("1")
        oneDimensionPlot.domainTitle.text = ""
        oneDimensionPlot.rangeTitle.text = ""
        oneDimensionPlot.clear()
        oneDimensionPlot.redraw()
    }

    /**
     * Recursively clears text from [view] and its children if it is an EditText or EditText subclass.
     * Deliberately leaves NumberPicker views alone.
     *
     * @param [view] The root view of the view tree to be cleared.
     */
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
