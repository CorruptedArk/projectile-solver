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
import android.view.*
import android.widget.*
import androidx.core.view.children
import com.androidplot.xy.CatmullRomInterpolator
import com.androidplot.xy.LineAndPointFormatter
import com.androidplot.xy.SimpleXYSeries
import com.androidplot.xy.XYSeries
import dev.corruptedark.projectilesolver.databinding.ActivityTwoDimensionsBinding
import kotlinx.android.synthetic.main.activity_main.precisionPicker
import kotlinx.android.synthetic.main.activity_main.timeBox
import kotlinx.android.synthetic.main.activity_two_dimensions.*
import java.lang.Math.pow
import kotlin.math.*

const val DEG_TO_RAD = PI / 180
const val RAD_TO_DEG = 180 / PI

class TwoDimensionsActivity : AppCompatActivity() {

    private val xAccelSolver = ConstantAccelSolver()
    private val yAccelSolver = ConstantAccelSolver()
    private lateinit var binding: ActivityTwoDimensionsBinding

    private var solved = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTwoDimensionsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val actionBar = supportActionBar
        actionBar!!.subtitle = "2D Solver"
        actionBar!!.setBackgroundDrawable(ColorDrawable(Color.parseColor("#FF232323")))
        window.statusBarColor = Color.parseColor("#ff151515")
        binding.precisionPicker.minValue = 1
        binding.precisionPicker.maxValue = Int.MAX_VALUE
        binding.precisionPicker.wrapSelectorWheel = false

        window.navigationBarColor = Color.parseColor("#ff151515")

        solved = false

        binding.domainRadioGroup.check(R.id.timeDomainButton)
        binding.rangeRadioGroup.check(R.id.xDisplacementRangeButton)

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

    fun onCheckBoxClicked(view: View)
    {
        if (view is CheckBox) {
            val checked = view.isChecked

            when (view.id) {
                R.id.initVelAngleCheck -> {
                    if (checked) {
                        binding.initVelAngleBox.visibility = View.VISIBLE
                        binding.initVelDegree.visibility = View.VISIBLE
                        binding.initVelYLabel.visibility = View.GONE
                        binding.initVelYBox.visibility = View.GONE
                        binding.initVelXLabel.text = "Initial velocity V₀ = "
                    } else {
                        binding.initVelAngleBox.visibility = View.GONE
                        binding.initVelDegree.visibility = View.GONE
                        binding.initVelYLabel.visibility = View.VISIBLE
                        binding.initVelYBox.visibility = View.VISIBLE
                        binding.initVelXLabel.text = "Initial velocity ˣV₀ = "
                    }
                }
                R.id.finalVelAngleCheck -> {
                    if (checked) {
                        binding.finalVelAngleBox.visibility = View.VISIBLE
                        binding.finalVelDegree.visibility = View.VISIBLE
                        binding.finalVelYLabel.visibility = View.GONE
                        binding.finalVelYBox.visibility = View.GONE
                        binding.finalVelXLabel.text = "Final velocity V₁ = "
                    } else {
                        binding.finalVelAngleBox.visibility = View.GONE
                        binding.finalVelDegree.visibility = View.GONE
                        binding.finalVelYLabel.visibility = View.VISIBLE
                        binding.finalVelYBox.visibility = View.VISIBLE
                        binding.finalVelXLabel.text = "Final velocity ˣV₁ = "
                    }

                }
                R.id.displacementAngleCheck -> {
                    if (checked) {
                        binding.displacementAngleBox.visibility = View.VISIBLE
                        binding.displacementDegree.visibility = View.VISIBLE
                        binding.displacementYLabel.visibility = View.GONE
                        binding.displacementYBox.visibility = View.GONE
                        binding.displacementXLabel.text = "Displacement Δp = "
                    } else {
                        binding.displacementAngleBox.visibility = View.GONE
                        binding.displacementDegree.visibility = View.GONE
                        binding.displacementYLabel.visibility = View.VISIBLE
                        binding.displacementYBox.visibility = View.VISIBLE
                        binding.displacementXLabel.text = "Displacement Δx = "
                    }
                }
                R.id.accelerationAngleCheck -> {
                    if (checked) {
                        binding.accelerationAngleBox.visibility = View.VISIBLE
                        binding.accelerationDegree.visibility = View.VISIBLE
                        binding.accelerationYLabel.visibility = View.GONE
                        binding.accelerationYBox.visibility = View.GONE
                        binding.accelerationXLabel.text = "Acceleration α = "
                    } else {
                        binding.accelerationAngleBox.visibility = View.GONE
                        binding.accelerationDegree.visibility = View.GONE
                        binding.accelerationYLabel.visibility = View.VISIBLE
                        binding.accelerationYBox.visibility = View.VISIBLE
                        binding.accelerationXLabel.text = "Acceleration αˣ = "
                    }
                }
            }

            if (solved) {
                if (binding.initVelAngleCheck.isChecked) {
                    val angles = mutableListOf<Double>()
                    val velocities = mutableListOf<Double>()
                    for (xInitVelocity in xAccelSolver.getInitVelocities()) {
                        for (yInitVelocity in yAccelSolver.getInitVelocities()) {
                            var angle = atan2(yInitVelocity, xInitVelocity)
                            if (angle < 0.0) {
                                angle += 2 * PI
                            }
                            angles.add(RAD_TO_DEG * angle)
                            velocities.add(sqrt(yInitVelocity * yInitVelocity + xInitVelocity * xInitVelocity))
                        }
                    }

                    binding.initVelAngleBox.setText(formatOutputString(angles.distinct()))
                    binding.initVelXBox.setText(formatOutputString(velocities.distinct()))
                } else {
                    binding.initVelXBox.setText(formatOutputString(xAccelSolver.getInitVelocities()))
                    binding.initVelYBox.setText(formatOutputString(yAccelSolver.getInitVelocities()))
                }

                if (binding.finalVelAngleCheck.isChecked) {
                    val angles = mutableListOf<Double>()
                    val velocities = mutableListOf<Double>()
                    for (xFinalVelocity in xAccelSolver.getFinalVelocities()) {
                        for (yFinalVelocity in yAccelSolver.getFinalVelocities()) {
                            var angle = atan2(yFinalVelocity, xFinalVelocity)
                            if (angle < 0.0) {
                                angle += 2 * PI
                            }
                            angles.add(RAD_TO_DEG * angle)
                            velocities.add(sqrt(yFinalVelocity * yFinalVelocity + xFinalVelocity * xFinalVelocity))
                        }
                    }

                    binding.finalVelAngleBox.setText(formatOutputString(angles.distinct()))
                    binding.finalVelXBox.setText(formatOutputString(velocities.distinct()))
                } else {
                    binding.finalVelXBox.setText(formatOutputString(xAccelSolver.getFinalVelocities()))
                    binding.finalVelYBox.setText(formatOutputString(yAccelSolver.getFinalVelocities()))
                }


                if (binding.displacementAngleCheck.isChecked) {
                    val xDisplacement = xAccelSolver.getDisplacement()
                    val yDisplacement = yAccelSolver.getDisplacement()
                    val displacement =
                        sqrt(xDisplacement * xDisplacement + yDisplacement * yDisplacement)
                    var angle = atan2(yDisplacement, xDisplacement)
                    if (angle < 0.0) {
                        angle += 2 * PI
                    }

                    binding.displacementAngleBox.setText(formatOutputString(listOf(RAD_TO_DEG * angle)))
                    binding.displacementXBox.setText(formatOutputString(listOf(displacement)))
                } else {
                    binding.displacementXBox.setText(formatOutputString(listOf(xAccelSolver.getDisplacement())))
                    binding.displacementYBox.setText(formatOutputString(listOf(yAccelSolver.getDisplacement())))
                }

                if (binding.accelerationAngleCheck.isChecked) {
                    val xAcceleration = xAccelSolver.getAcceleration()
                    val yAcceleration = yAccelSolver.getAcceleration()
                    val acceleration =
                        sqrt(xAcceleration * xAcceleration + yAcceleration * yAcceleration)
                    var angle = atan2(yAcceleration, xAcceleration)
                    if (angle < 0.0) {
                        angle += 2 * PI
                    }

                    binding.accelerationAngleBox.setText(formatOutputString(listOf(RAD_TO_DEG * angle)))
                    binding.accelerationXBox.setText(formatOutputString(listOf(acceleration)))
                } else {
                    binding.accelerationXBox.setText(formatOutputString(listOf(xAccelSolver.getAcceleration())))
                    binding.accelerationYBox.setText(formatOutputString(listOf(yAccelSolver.getAcceleration())))
                }

                val times = mutableListOf<Double>()

                times.addAll(xAccelSolver.getTimes())
                times.addAll(yAccelSolver.getTimes())

                binding.timeBox.setText(formatOutputString(times.distinct()))
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.two_dimension_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId)
        {
            R.id.aboutItem2D -> {openAboutActivity(); return true}
            R.id.oneDimensionItem -> {onBackPressed(); return true}
            else -> return super.onOptionsItemSelected(item)
        }

    }

    private fun openAboutActivity()
    {
        val intent = Intent(this, AboutActivity::class.java)
        startActivity(intent)
    }

    private fun anyAreNull(vararg values: Any?): Boolean
    {
        var areNull = false
        for (value in values)
        {
            if (value == null)
            {
                areNull = true
                break
            }
        }

        return areNull
    }

    fun solveSystem(view: View)
    {

        val initVelocityX: Double?
        val initVelocityY: Double?
        val finalVelocityX: Double?
        val finalVelocityY: Double?
        val displacementX: Double?
        val displacementY: Double?
        val accelerationX: Double?
        val accelerationY: Double?
        var time: Double?


        //Start collecting the values from the boxes
        if (binding.initVelAngleCheck.isChecked)
        {
            val initVelAngle = binding.initVelAngleBox.text.toString().toDoubleOrNull()
            val initVelocity = binding.initVelXBox.text.toString().toDoubleOrNull()

            if(anyAreNull(initVelAngle, initVelocity))
            {
                initVelocityX = null
                initVelocityY = null
            }
            else
            {
                initVelocityX = initVelocity!! * cos(initVelAngle!! * DEG_TO_RAD)
                initVelocityY = initVelocity!! * sin(initVelAngle!! * DEG_TO_RAD)
            }
        }
        else
        {
            initVelocityX = binding.initVelXBox.text.toString().toDoubleOrNull()
            initVelocityY = binding.initVelYBox.text.toString().toDoubleOrNull()
        }

        if (binding.finalVelAngleCheck.isChecked)
        {
            val finalVelAngle = binding.finalVelAngleBox.text.toString().toDoubleOrNull()
            val finalVelocity = binding.finalVelXBox.text.toString().toDoubleOrNull()

            if(anyAreNull(finalVelAngle, finalVelocity))
            {
                finalVelocityX = null
                finalVelocityY = null
            }
            else
            {
                finalVelocityX = finalVelocity!! * cos(finalVelAngle!! * DEG_TO_RAD)
                finalVelocityY = finalVelocity!! * sin(finalVelAngle!! * DEG_TO_RAD)
            }
        }
        else
        {
            finalVelocityX = binding.finalVelXBox.text.toString().toDoubleOrNull()
            finalVelocityY = binding.finalVelYBox.text.toString().toDoubleOrNull()
        }

        if (binding.displacementAngleCheck.isChecked)
        {
            val displacementAngle = binding.displacementAngleBox.text.toString().toDoubleOrNull()
            val displacement = binding.displacementXBox.text.toString().toDoubleOrNull()

            if(anyAreNull(displacementAngle, displacement))
            {
                displacementX = null
                displacementY = null
            }
            else
            {
                displacementX = displacement!! * cos(displacementAngle!! * DEG_TO_RAD)
                displacementY = displacement!! * sin(displacementAngle!! * DEG_TO_RAD)
            }
        }
        else
        {
            displacementX = binding.displacementXBox.text.toString().toDoubleOrNull()
            displacementY = binding.displacementYBox.text.toString().toDoubleOrNull()
        }

        if (binding.accelerationAngleCheck.isChecked)
        {
            val accelerationAngle = binding.accelerationAngleBox.text.toString().toDoubleOrNull()
            val acceleration = binding.accelerationXBox.text.toString().toDoubleOrNull()

            if(anyAreNull(accelerationAngle, acceleration))
            {
                accelerationX = null
                accelerationY = null
            }
            else
            {
                accelerationX = acceleration!! * cos(accelerationAngle!! * DEG_TO_RAD)
                accelerationY = acceleration!! * sin(accelerationAngle!! * DEG_TO_RAD)
            }
        }
        else
        {
            accelerationX = binding.accelerationXBox.text.toString().toDoubleOrNull()
            accelerationY = binding.accelerationYBox.text.toString().toDoubleOrNull()
        }

        time = binding.timeBox.text.toString().toDoubleOrNull()
        //End collecting of values from boxes

        var xValues = listOf(initVelocityX, finalVelocityX, displacementX, accelerationX, time)
        var yValues = listOf(initVelocityY, finalVelocityY, displacementY, accelerationY, time)

        //Start counting how much is missing
        var xEmptyCount = 0
        for (xValue in xValues)
        {
            if (xValue == null)
                xEmptyCount++
        }

        var yEmptyCount = 0
        for (yValue in yValues)
        {
            if (yValue == null)
                yEmptyCount++
        }
        //End counting how much is missing

        //Start finding correct strategy and solve or print error
        when
        {
            xEmptyCount > 2 && yEmptyCount > 2 -> {
                Toast.makeText(this, "Error: Not enough x or y values", Toast.LENGTH_SHORT).show()
            }
            xEmptyCount > 2 -> {
                //Solve y values
                yAccelSolver.findAllValues(initVelocityY, finalVelocityY, displacementY, accelerationY, time)

                val times = yAccelSolver.getTimes()

                //Try solving X values with time
                if (times.size == 1)
                {
                    time = times[0]
                    xValues = listOf(initVelocityX, finalVelocityX, displacementX, accelerationX, time)

                    xEmptyCount = 0
                    for (xValue in xValues)
                    {
                        if (xValue == null)
                            xEmptyCount++
                    }

                    if (xEmptyCount > 2)
                    {
                        Toast.makeText(this, "Not enough x values", Toast.LENGTH_SHORT).show()
                    }
                    else
                    {
                        //Solve x values
                        xAccelSolver.findAllValues(initVelocityX, finalVelocityX, displacementX, accelerationX, time)

                        if (binding.initVelAngleCheck.isChecked)
                        {
                            val angles = mutableListOf<Double>()
                            val velocities = mutableListOf<Double>()
                            for (xInitVelocity in xAccelSolver.getInitVelocities())
                            {
                                for (yInitVelocity in yAccelSolver.getInitVelocities())
                                {
                                    var angle = atan2(yInitVelocity,xInitVelocity)
                                    if(angle < 0.0)
                                    {
                                        angle += 2*PI
                                    }
                                    angles.add(RAD_TO_DEG * angle)
                                    velocities.add(sqrt(yInitVelocity * yInitVelocity + xInitVelocity * xInitVelocity))
                                }
                            }

                            binding.initVelAngleBox.setText(formatOutputString(angles.distinct()))
                            binding.initVelXBox.setText(formatOutputString(velocities.distinct()))
                        }
                        else
                        {
                            binding.initVelXBox.setText(formatOutputString(xAccelSolver.getInitVelocities()))
                            binding.initVelYBox.setText(formatOutputString(yAccelSolver.getInitVelocities()))
                        }

                        if (binding.finalVelAngleCheck.isChecked)
                        {
                            val angles = mutableListOf<Double>()
                            val velocities = mutableListOf<Double>()
                            for (xFinalVelocity in xAccelSolver.getFinalVelocities())
                            {
                                for (yFinalVelocity in yAccelSolver.getFinalVelocities())
                                {
                                    var angle = atan2(yFinalVelocity,xFinalVelocity)
                                    if(angle < 0.0)
                                    {
                                        angle += 2*PI
                                    }
                                    angles.add(RAD_TO_DEG * angle)
                                    velocities.add(sqrt(yFinalVelocity * yFinalVelocity + xFinalVelocity * xFinalVelocity))
                                }
                            }

                            binding.finalVelAngleBox.setText(formatOutputString(angles.distinct()))
                            binding.finalVelXBox.setText(formatOutputString(velocities.distinct()))
                        }
                        else
                        {
                            binding.finalVelXBox.setText(formatOutputString(xAccelSolver.getFinalVelocities()))
                            binding.finalVelYBox.setText(formatOutputString(yAccelSolver.getFinalVelocities()))
                        }


                        if (binding.displacementAngleCheck.isChecked)
                        {
                            val xDisplacement = xAccelSolver.getDisplacement()
                            val yDisplacement = yAccelSolver.getDisplacement()
                            val displacement = sqrt(xDisplacement * xDisplacement + yDisplacement * yDisplacement)
                            var angle = atan2(yDisplacement,xDisplacement)
                            if(angle < 0.0)
                            {
                                angle += 2 * PI
                            }

                            binding.displacementAngleBox.setText(formatOutputString(listOf(RAD_TO_DEG * angle)))
                            binding.displacementXBox.setText(formatOutputString(listOf(displacement)))
                        }
                        else
                        {
                            binding.displacementXBox.setText(formatOutputString(listOf(xAccelSolver.getDisplacement())))
                            binding.displacementYBox.setText(formatOutputString(listOf(yAccelSolver.getDisplacement())))
                        }

                        if (binding.accelerationAngleCheck.isChecked)
                        {
                            val xAcceleration = xAccelSolver.getAcceleration()
                            val yAcceleration = yAccelSolver.getAcceleration()
                            val acceleration = sqrt(xAcceleration * xAcceleration + yAcceleration * yAcceleration)
                            var angle = atan2(yAcceleration,xAcceleration)
                            if(angle < 0.0)
                            {
                                angle += 2 * PI
                            }

                            binding.accelerationAngleBox.setText(formatOutputString(listOf(RAD_TO_DEG * angle)))
                            binding.accelerationXBox.setText(formatOutputString(listOf(acceleration)))
                        }
                        else
                        {
                            binding.accelerationXBox.setText(formatOutputString(listOf(xAccelSolver.getAcceleration())))
                            binding.accelerationYBox.setText(formatOutputString(listOf(yAccelSolver.getAcceleration())))
                        }

                        val times = mutableListOf<Double>()

                        times.addAll(xAccelSolver.getTimes())
                        times.addAll(yAccelSolver.getTimes())

                        binding.timeBox.setText(formatOutputString(times.distinct()))
                    }
                }
                else
                {
                    Toast.makeText(this, "Not enough x values", Toast.LENGTH_SHORT).show()
                }

                solved = true
            }
            yEmptyCount > 2 -> {
                //Solve x values
                xAccelSolver.findAllValues(initVelocityX, finalVelocityX, displacementX, accelerationX, time)

                val times = xAccelSolver.getTimes()

                //Try solving y values with time
                if (times.size == 1)
                {
                    time = times[0]
                    yValues = listOf(initVelocityY, finalVelocityY, displacementY, accelerationY, time)

                    yEmptyCount = 0
                    for (yValue in yValues)
                    {
                        if (yValue == null)
                            yEmptyCount++
                    }

                    if (yEmptyCount > 2)
                    {
                        Toast.makeText(this, "Not enough y values", Toast.LENGTH_SHORT).show()
                    }
                    else
                    {
                        //Solve y values
                        yAccelSolver.findAllValues(initVelocityY, finalVelocityY, displacementY, accelerationY, time)

                        if (binding.initVelAngleCheck.isChecked)
                        {
                            val angles = mutableListOf<Double>()
                            val velocities = mutableListOf<Double>()
                            for (xInitVelocity in xAccelSolver.getInitVelocities())
                            {
                                for (yInitVelocity in yAccelSolver.getInitVelocities())
                                {
                                    var angle = atan2(yInitVelocity,xInitVelocity)
                                    if(angle < 0.0)
                                    {
                                        angle += 2*PI
                                    }
                                    angles.add(RAD_TO_DEG * angle)
                                    velocities.add(sqrt(yInitVelocity * yInitVelocity + xInitVelocity * xInitVelocity))
                                }
                            }

                            binding.initVelAngleBox.setText(formatOutputString(angles.distinct()))
                            binding.initVelXBox.setText(formatOutputString(velocities.distinct()))
                        }
                        else
                        {
                            binding.initVelXBox.setText(formatOutputString(xAccelSolver.getInitVelocities()))
                            binding.initVelYBox.setText(formatOutputString(yAccelSolver.getInitVelocities()))
                        }

                        if (binding.finalVelAngleCheck.isChecked)
                        {
                            val angles = mutableListOf<Double>()
                            val velocities = mutableListOf<Double>()
                            for (xFinalVelocity in xAccelSolver.getFinalVelocities())
                            {
                                for (yFinalVelocity in yAccelSolver.getFinalVelocities())
                                {
                                    var angle = atan2(yFinalVelocity,xFinalVelocity)
                                    if(angle < 0.0)
                                    {
                                        angle += 2*PI
                                    }
                                    angles.add(RAD_TO_DEG * angle)
                                    velocities.add(sqrt(yFinalVelocity * yFinalVelocity + xFinalVelocity * xFinalVelocity))
                                }
                            }

                            binding.finalVelAngleBox.setText(formatOutputString(angles.distinct()))
                            binding.finalVelXBox.setText(formatOutputString(velocities.distinct()))
                        }
                        else
                        {
                            binding.finalVelXBox.setText(formatOutputString(xAccelSolver.getFinalVelocities()))
                            binding.finalVelYBox.setText(formatOutputString(yAccelSolver.getFinalVelocities()))
                        }


                        if (binding.displacementAngleCheck.isChecked)
                        {
                            val xDisplacement = xAccelSolver.getDisplacement()
                            val yDisplacement = yAccelSolver.getDisplacement()
                            val displacement = sqrt(xDisplacement * xDisplacement + yDisplacement * yDisplacement)
                            var angle = atan2(yDisplacement,xDisplacement)
                            if(angle < 0.0)
                            {
                                angle += 2 * PI
                            }

                            binding.displacementAngleBox.setText(formatOutputString(listOf(RAD_TO_DEG * angle)))
                            binding.displacementXBox.setText(formatOutputString(listOf(displacement)))
                        }
                        else
                        {
                            binding.displacementXBox.setText(formatOutputString(listOf(xAccelSolver.getDisplacement())))
                            binding.displacementYBox.setText(formatOutputString(listOf(yAccelSolver.getDisplacement())))
                        }

                        if (binding.accelerationAngleCheck.isChecked)
                        {
                            val xAcceleration = xAccelSolver.getAcceleration()
                            val yAcceleration = yAccelSolver.getAcceleration()
                            val acceleration = sqrt(xAcceleration * xAcceleration + yAcceleration * yAcceleration)
                            var angle = atan2(yAcceleration,xAcceleration)
                            if(angle < 0.0)
                            {
                                angle += 2 * PI
                            }

                            binding.accelerationAngleBox.setText(formatOutputString(listOf(RAD_TO_DEG * angle)))
                            binding.accelerationXBox.setText(formatOutputString(listOf(acceleration)))
                        }
                        else
                        {
                            binding.accelerationXBox.setText(formatOutputString(listOf(xAccelSolver.getAcceleration())))
                            binding.accelerationYBox.setText(formatOutputString(listOf(yAccelSolver.getAcceleration())))
                        }

                        val times = mutableListOf<Double>()

                        times.addAll(xAccelSolver.getTimes())
                        times.addAll(yAccelSolver.getTimes())

                        binding.timeBox.setText(formatOutputString(times.distinct()))
                    }
                }
                else
                {
                    Toast.makeText(this, "Not enough y values", Toast.LENGTH_SHORT).show()
                }

                solved = true
            }
            else -> {
                //Solve x values
                xAccelSolver.findAllValues(initVelocityX, finalVelocityX, displacementX, accelerationX, time)

                //Solve y values
                yAccelSolver.findAllValues(initVelocityY, finalVelocityY, displacementY, accelerationY, time)

                //Format and output results
                if (binding.initVelAngleCheck.isChecked)
                {
                    val angles = mutableListOf<Double>()
                    val velocities = mutableListOf<Double>()
                    for (xInitVelocity in xAccelSolver.getInitVelocities())
                    {
                        for (yInitVelocity in yAccelSolver.getInitVelocities())
                        {
                            var angle = atan2(yInitVelocity,xInitVelocity)
                            if(angle < 0.0)
                            {
                                angle += 2*PI
                            }
                            angles.add(RAD_TO_DEG * angle)
                            velocities.add(sqrt(yInitVelocity * yInitVelocity + xInitVelocity * xInitVelocity))
                        }
                    }

                    binding.initVelAngleBox.setText(formatOutputString(angles.distinct()))
                    binding.initVelXBox.setText(formatOutputString(velocities.distinct()))
                }
                else
                {
                    binding.initVelXBox.setText(formatOutputString(xAccelSolver.getInitVelocities()))
                    binding.initVelYBox.setText(formatOutputString(yAccelSolver.getInitVelocities()))
                }

                if (binding.finalVelAngleCheck.isChecked)
                {
                    val angles = mutableListOf<Double>()
                    val velocities = mutableListOf<Double>()
                    for (xFinalVelocity in xAccelSolver.getFinalVelocities())
                    {
                        for (yFinalVelocity in yAccelSolver.getFinalVelocities())
                        {
                            var angle = atan2(yFinalVelocity,xFinalVelocity)
                            if(angle < 0.0)
                            {
                                angle += 2*PI
                            }
                            angles.add(RAD_TO_DEG * angle)
                            velocities.add(sqrt(yFinalVelocity * yFinalVelocity + xFinalVelocity * xFinalVelocity))
                        }
                    }

                    binding.finalVelAngleBox.setText(formatOutputString(angles.distinct()))
                    binding.finalVelXBox.setText(formatOutputString(velocities.distinct()))
                }
                else
                {
                    binding.finalVelXBox.setText(formatOutputString(xAccelSolver.getFinalVelocities()))
                    binding.finalVelYBox.setText(formatOutputString(yAccelSolver.getFinalVelocities()))
                }


                if (binding.displacementAngleCheck.isChecked)
                {
                    val xDisplacement = xAccelSolver.getDisplacement()
                    val yDisplacement = yAccelSolver.getDisplacement()
                    val displacement = sqrt(xDisplacement * xDisplacement + yDisplacement * yDisplacement)
                    var angle = atan2(yDisplacement,xDisplacement)
                    if(angle < 0.0)
                    {
                        angle += 2 * PI
                    }

                    binding.displacementAngleBox.setText(formatOutputString(listOf(RAD_TO_DEG * angle)))
                    binding.displacementXBox.setText(formatOutputString(listOf(displacement)))
                }
                else
                {
                    binding.displacementXBox.setText(formatOutputString(listOf(xAccelSolver.getDisplacement())))
                    binding.displacementYBox.setText(formatOutputString(listOf(yAccelSolver.getDisplacement())))
                }

                if (binding.accelerationAngleCheck.isChecked)
                {
                    val xAcceleration = xAccelSolver.getAcceleration()
                    val yAcceleration = yAccelSolver.getAcceleration()
                    val acceleration = sqrt(xAcceleration * xAcceleration + yAcceleration * yAcceleration)
                    var angle = atan2(yAcceleration,xAcceleration)
                    if(angle < 0.0)
                    {
                        angle += 2 * PI
                    }

                    binding.accelerationAngleBox.setText(formatOutputString(listOf(RAD_TO_DEG * angle)))
                    binding.accelerationXBox.setText(formatOutputString(listOf(acceleration)))
                }
                else
                {
                    binding.accelerationXBox.setText(formatOutputString(listOf(xAccelSolver.getAcceleration())))
                    binding.accelerationYBox.setText(formatOutputString(listOf(yAccelSolver.getAcceleration())))
                }

                val times = mutableListOf<Double>()

                times.addAll(xAccelSolver.getTimes())
                times.addAll(yAccelSolver.getTimes())

                binding.timeBox.setText(formatOutputString(times.distinct()))

                solved = true
            }
        }
        //End finding correct strategy and solve or print error
        if (solved)
            updatePlot()
    }

    private fun updatePlot()
    {
        binding.twoDimensionPlot.clear()
        val minDomain = binding.domainMinBox.text.toString().toDoubleOrNull() ?: -1.0
        val maxDomain = binding.domainMaxBox.text.toString().toDoubleOrNull() ?: 1.0

        val formatter = LineAndPointFormatter(Color.parseColor("#00A2FF"), null, null, null)
        formatter.interpolationParams = CatmullRomInterpolator.Params(10, CatmullRomInterpolator.Type.Centripetal)


        when(listOf(binding.domainRadioGroup.checkedRadioButtonId, binding.rangeRadioGroup.checkedRadioButtonId))
        {
            listOf(R.id.timeDomainButton, R.id.xDisplacementRangeButton) ->
            {
                binding.twoDimensionPlot.addSeries(formatter, xAccelSolver.getDisplacementTimeXYSeries(minDomain, maxDomain, 100, "Δx vs t"))
                binding.twoDimensionPlot.domainTitle.text = "Domain t"
                binding.twoDimensionPlot.rangeTitle.text = "Range Δx"
            }
            listOf(R.id.timeDomainButton, R.id.yDisplacementRangeButton) ->
            {
                binding.twoDimensionPlot.addSeries(formatter, yAccelSolver.getDisplacementTimeXYSeries(minDomain, maxDomain, 100, "Δy vs t"))
                binding.twoDimensionPlot.domainTitle.text = "Domain t"
                binding.twoDimensionPlot.rangeTitle.text = "Range Δy"
            }
            listOf(R.id.xDisplacementDomainButton, R.id.xDisplacementRangeButton) ->
            {
                val samplesList = mutableListOf<Double>()
                val size = 4
                for (i in 0..size)
                    samplesList.add(minDomain + i.toDouble()/size.toDouble()*abs(maxDomain - minDomain))

                binding.twoDimensionPlot.addSeries(formatter, SimpleXYSeries(samplesList, samplesList, "Δx vs Δx"))
                binding.twoDimensionPlot.domainTitle.text = "Domain Δx"
                binding.twoDimensionPlot.rangeTitle.text = "Range Δx"
            }
            listOf(R.id.yDisplacementDomainButton, R.id.yDisplacementRangeButton) ->
            {
                val samplesList = mutableListOf<Double>()
                val size = 4
                for (i in 0..size)
                    samplesList.add(minDomain + i.toDouble()/size.toDouble()*abs(maxDomain - minDomain))

                binding.twoDimensionPlot.addSeries(formatter, SimpleXYSeries(samplesList, samplesList, "Δy vs Δy"))
                binding.twoDimensionPlot.domainTitle.text = "Domain Δy"
                binding.twoDimensionPlot.rangeTitle.text = "Range Δy"
            }
            listOf(R.id.timeDomainButton, R.id.xVelocityRangeButton) ->
            {
                binding.twoDimensionPlot.addSeries(formatter, xAccelSolver.getVelocityTimeXYSeries(minDomain, maxDomain, 100, "ˣV vs t"))
                binding.twoDimensionPlot.domainTitle.text = "Domain t"
                binding.twoDimensionPlot.rangeTitle.text = "Range ˣV"
            }
            listOf(R.id.timeDomainButton, R.id.yVelocityRangeButton) ->
            {
                binding.twoDimensionPlot.addSeries(formatter, yAccelSolver.getVelocityTimeXYSeries(minDomain, maxDomain, 100, "ʸV vs t"))
                binding.twoDimensionPlot.domainTitle.text = "Domain t"
                binding.twoDimensionPlot.rangeTitle.text = "Range ʸV"
            }
            listOf(R.id.xDisplacementDomainButton, R.id.xVelocityRangeButton) ->
            {
                val formatter2 = LineAndPointFormatter(Color.MAGENTA, null, null, null)
                formatter2.interpolationParams = CatmullRomInterpolator.Params(10, CatmullRomInterpolator.Type.Centripetal)

                val seriesArray = xAccelSolver.getVelocityDisplacementXYSeries(minDomain, maxDomain, 100, "ˣV vs Δx")
                binding.twoDimensionPlot.addSeries(formatter, seriesArray[0])
                binding.twoDimensionPlot.addSeries (formatter2, seriesArray.last())
                binding.twoDimensionPlot.domainTitle.text = "Domain Δx"
                binding.twoDimensionPlot.rangeTitle.text = "Range ˣV"
            }
            listOf(R.id.yDisplacementDomainButton, R.id.yVelocityRangeButton) ->
            {
                val formatter2 = LineAndPointFormatter(Color.MAGENTA, null, null, null)
                formatter2.interpolationParams = CatmullRomInterpolator.Params(10, CatmullRomInterpolator.Type.Centripetal)

                val seriesArray = yAccelSolver.getVelocityDisplacementXYSeries(minDomain, maxDomain, 100, "ʸV vs Δy")
                binding.twoDimensionPlot.addSeries(formatter, seriesArray[0])
                binding.twoDimensionPlot.addSeries (formatter2, seriesArray.last())
                binding.twoDimensionPlot.domainTitle.text = "Domain Δy"
                binding.twoDimensionPlot.rangeTitle.text = "Range ʸV"
            }
            listOf(R.id.xDisplacementDomainButton, R.id.yDisplacementRangeButton) ->
            {
                val formatter2 = LineAndPointFormatter(Color.MAGENTA, null, null, null)
                formatter2.interpolationParams = CatmullRomInterpolator.Params(10, CatmullRomInterpolator.Type.Centripetal)

                val seriesArray = xAccelSolver.getDisplacementDisplacementTwoAxisXYSeries(yAccelSolver, minDomain, maxDomain, 100, "Δy vs Δx")
                binding.twoDimensionPlot.addSeries(formatter, seriesArray[0])
                binding.twoDimensionPlot.addSeries (formatter2, seriesArray.last())
                binding.twoDimensionPlot.domainTitle.text = "Domain Δx"
                binding.twoDimensionPlot.rangeTitle.text = "Range Δy"
            }
            listOf(R.id.yDisplacementDomainButton, R.id.xDisplacementRangeButton) ->
            {
                val formatter2 = LineAndPointFormatter(Color.MAGENTA, null, null, null)
                formatter2.interpolationParams = CatmullRomInterpolator.Params(10, CatmullRomInterpolator.Type.Centripetal)

                val seriesArray = yAccelSolver.getDisplacementDisplacementTwoAxisXYSeries(xAccelSolver, minDomain, maxDomain, 100, "Δx vs Δy")
                binding.twoDimensionPlot.addSeries(formatter, seriesArray[0])
                binding.twoDimensionPlot.addSeries (formatter2, seriesArray.last())
                binding.twoDimensionPlot.domainTitle.text = "Domain Δy"
                binding.twoDimensionPlot.rangeTitle.text = "Range Δx"
            }
            listOf(R.id.xDisplacementDomainButton, R.id.yVelocityRangeButton) ->
            {
                val formatter2 = LineAndPointFormatter(Color.MAGENTA, null, null, null)
                formatter2.interpolationParams = CatmullRomInterpolator.Params(10, CatmullRomInterpolator.Type.Centripetal)

                val seriesArray = xAccelSolver.getVelocityDisplacementTwoAxisXYSeries(yAccelSolver, minDomain, maxDomain, 100, "ʸV vs Δx")
                binding.twoDimensionPlot.addSeries(formatter, seriesArray[0])
                binding.twoDimensionPlot.addSeries (formatter2, seriesArray.last())
                binding.twoDimensionPlot.domainTitle.text = "Domain Δx"
                binding.twoDimensionPlot.rangeTitle.text = "Range ʸV"
            }
            listOf(R.id.yDisplacementDomainButton, R.id.xVelocityRangeButton) ->
            {
                val formatter2 = LineAndPointFormatter(Color.MAGENTA, null, null, null)
                formatter2.interpolationParams = CatmullRomInterpolator.Params(10, CatmullRomInterpolator.Type.Centripetal)

                val seriesArray = yAccelSolver.getVelocityDisplacementTwoAxisXYSeries(xAccelSolver, minDomain, maxDomain, 100, "ˣV vs Δy")
                binding.twoDimensionPlot.addSeries(formatter, seriesArray[0])
                binding.twoDimensionPlot.addSeries (formatter2, seriesArray.last())
                binding.twoDimensionPlot.domainTitle.text = "Domain Δy"
                binding.twoDimensionPlot.rangeTitle.text = "Range ˣV"
            }
            else -> binding.twoDimensionPlot.addSeries(formatter, SimpleXYSeries(""))
        }

        binding.twoDimensionPlot.redraw()
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

    fun clear(view: View)
    {
        clearEditTextRecursive(view.rootView)
        solved = false

        binding.domainMinBox.setText("-1")
        binding.domainMaxBox.setText("1")
        binding.twoDimensionPlot.domainTitle.text = ""
        binding.twoDimensionPlot.rangeTitle.text = ""
        binding.twoDimensionPlot.clear()
        binding.twoDimensionPlot.redraw()
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
