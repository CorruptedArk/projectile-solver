package dev.corruptedark.projectilesolver

import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.*
import android.widget.*
import androidx.core.view.children
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

    var solved = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_two_dimensions)

        val actionBar = supportActionBar
        actionBar!!.subtitle = "2D Solver"
        actionBar!!.setBackgroundDrawable(ColorDrawable(Color.parseColor("#FF232323")))
        window.statusBarColor = Color.parseColor("#ff151515")
        precisionPicker.minValue = 1
        precisionPicker.maxValue = Int.MAX_VALUE
        precisionPicker.wrapSelectorWheel = false

        solved = false
    }

    fun onCheckBoxClicked(view: View)
    {
        if (view is CheckBox) {
            val checked = view.isChecked

            when (view.id) {
                R.id.initVelAngleCheck -> {
                    if (checked) {
                        initVelAngleBox.visibility = View.VISIBLE
                        initVelDegree.visibility = View.VISIBLE
                        initVelYLabel.visibility = View.GONE
                        initVelYBox.visibility = View.GONE
                        initVelXLabel.text = "Initial velocity V₀ = "
                    } else {
                        initVelAngleBox.visibility = View.GONE
                        initVelDegree.visibility = View.GONE
                        initVelYLabel.visibility = View.VISIBLE
                        initVelYBox.visibility = View.VISIBLE
                        initVelXLabel.text = "Initial velocity ˣV₀ = "
                    }
                }
                R.id.finalVelAngleCheck -> {
                    if (checked) {
                        finalVelAngleBox.visibility = View.VISIBLE
                        finalVelDegree.visibility = View.VISIBLE
                        finalVelYLabel.visibility = View.GONE
                        finalVelYBox.visibility = View.GONE
                        finalVelXLabel.text = "Final velocity V₁ = "
                    } else {
                        finalVelAngleBox.visibility = View.GONE
                        finalVelDegree.visibility = View.GONE
                        finalVelYLabel.visibility = View.VISIBLE
                        finalVelYBox.visibility = View.VISIBLE
                        finalVelXLabel.text = "Final velocity ˣV₁ = "
                    }

                }
                R.id.displacementAngleCheck -> {
                    if (checked) {
                        displacementAngleBox.visibility = View.VISIBLE
                        displacementDegree.visibility = View.VISIBLE
                        displacementYLabel.visibility = View.GONE
                        displacementYBox.visibility = View.GONE
                        displacementXLabel.text = "Displacement Δp = "
                    } else {
                        displacementAngleBox.visibility = View.GONE
                        displacementDegree.visibility = View.GONE
                        displacementYLabel.visibility = View.VISIBLE
                        displacementYBox.visibility = View.VISIBLE
                        displacementXLabel.text = "Displacement Δx = "
                    }
                }
                R.id.accelerationAngleCheck -> {
                    if (checked) {
                        accelerationAngleBox.visibility = View.VISIBLE
                        accelerationDegree.visibility = View.VISIBLE
                        accelerationYLabel.visibility = View.GONE
                        accelerationYBox.visibility = View.GONE
                        accelerationXLabel.text = "Acceleration α = "
                    } else {
                        accelerationAngleBox.visibility = View.GONE
                        accelerationDegree.visibility = View.GONE
                        accelerationYLabel.visibility = View.VISIBLE
                        accelerationYBox.visibility = View.VISIBLE
                        accelerationXLabel.text = "Acceleration αˣ = "
                    }
                }
            }

            if (solved) {
                if (initVelAngleCheck.isChecked) {
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

                    initVelAngleBox.setText(formatOutputString(angles.distinct()))
                    initVelXBox.setText(formatOutputString(velocities.distinct()))
                } else {
                    initVelXBox.setText(formatOutputString(xAccelSolver.getInitVelocities()))
                    initVelYBox.setText(formatOutputString(yAccelSolver.getInitVelocities()))
                }

                if (finalVelAngleCheck.isChecked) {
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

                    finalVelAngleBox.setText(formatOutputString(angles.distinct()))
                    finalVelXBox.setText(formatOutputString(velocities.distinct()))
                } else {
                    finalVelXBox.setText(formatOutputString(xAccelSolver.getFinalVelocities()))
                    finalVelYBox.setText(formatOutputString(yAccelSolver.getFinalVelocities()))
                }


                if (displacementAngleCheck.isChecked) {
                    val xDisplacement = xAccelSolver.getDisplacement()
                    val yDisplacement = yAccelSolver.getDisplacement()
                    val displacement =
                        sqrt(xDisplacement * xDisplacement + yDisplacement * yDisplacement)
                    var angle = atan2(yDisplacement, xDisplacement)
                    if (angle < 0.0) {
                        angle += 2 * PI
                    }

                    displacementAngleBox.setText(formatOutputString(listOf(RAD_TO_DEG * angle)))
                    displacementXBox.setText(formatOutputString(listOf(displacement)))
                } else {
                    displacementXBox.setText(formatOutputString(listOf(xAccelSolver.getDisplacement())))
                    displacementYBox.setText(formatOutputString(listOf(yAccelSolver.getDisplacement())))
                }

                if (accelerationAngleCheck.isChecked) {
                    val xAcceleration = xAccelSolver.getAcceleration()
                    val yAcceleration = yAccelSolver.getAcceleration()
                    val acceleration =
                        sqrt(xAcceleration * xAcceleration + yAcceleration * yAcceleration)
                    var angle = atan2(yAcceleration, xAcceleration)
                    if (angle < 0.0) {
                        angle += 2 * PI
                    }

                    accelerationAngleBox.setText(formatOutputString(listOf(RAD_TO_DEG * angle)))
                    accelerationXBox.setText(formatOutputString(listOf(acceleration)))
                } else {
                    accelerationXBox.setText(formatOutputString(listOf(xAccelSolver.getAcceleration())))
                    accelerationYBox.setText(formatOutputString(listOf(yAccelSolver.getAcceleration())))
                }

                val times = mutableListOf<Double>()

                times.addAll(xAccelSolver.getTimes())
                times.addAll(yAccelSolver.getTimes())

                timeBox.setText(formatOutputString(times.distinct()))
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

        var initVelocityX: Double?
        var initVelocityY: Double?
        var finalVelocityX: Double?
        var finalVelocityY: Double?
        var displacementX: Double?
        var displacementY: Double?
        var accelerationX: Double?
        var accelerationY: Double?
        var time: Double?


        //Start collecting the values from the boxes
        if (initVelAngleCheck.isChecked)
        {
            val initVelAngle = initVelAngleBox.text.toString().toDoubleOrNull()
            val initVelocity = initVelXBox.text.toString().toDoubleOrNull()

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
            initVelocityX = initVelXBox.text.toString().toDoubleOrNull()
            initVelocityY = initVelYBox.text.toString().toDoubleOrNull()
        }

        if (finalVelAngleCheck.isChecked)
        {
            val finalVelAngle = finalVelAngleBox.text.toString().toDoubleOrNull()
            val finalVelocity = finalVelXBox.text.toString().toDoubleOrNull()

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
            finalVelocityX = finalVelXBox.text.toString().toDoubleOrNull()
            finalVelocityY = finalVelYBox.text.toString().toDoubleOrNull()
        }

        if (displacementAngleCheck.isChecked)
        {
            val displacementAngle = displacementAngleBox.text.toString().toDoubleOrNull()
            val displacement = displacementXBox.text.toString().toDoubleOrNull()

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
            displacementX = displacementXBox.text.toString().toDoubleOrNull()
            displacementY = displacementYBox.text.toString().toDoubleOrNull()
        }

        if (accelerationAngleCheck.isChecked)
        {
            val accelerationAngle = accelerationAngleBox.text.toString().toDoubleOrNull()
            val acceleration = accelerationXBox.text.toString().toDoubleOrNull()

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
            accelerationX = accelerationXBox.text.toString().toDoubleOrNull()
            accelerationY = accelerationYBox.text.toString().toDoubleOrNull()
        }

        time = timeBox.text.toString().toDoubleOrNull()
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
        //TODO: Format and display output
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

                        if (initVelAngleCheck.isChecked)
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

                            initVelAngleBox.setText(formatOutputString(angles.distinct()))
                            initVelXBox.setText(formatOutputString(velocities.distinct()))
                        }
                        else
                        {
                            initVelXBox.setText(formatOutputString(xAccelSolver.getInitVelocities()))
                            initVelYBox.setText(formatOutputString(yAccelSolver.getInitVelocities()))
                        }

                        if (finalVelAngleCheck.isChecked)
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

                            finalVelAngleBox.setText(formatOutputString(angles.distinct()))
                            finalVelXBox.setText(formatOutputString(velocities.distinct()))
                        }
                        else
                        {
                            finalVelXBox.setText(formatOutputString(xAccelSolver.getFinalVelocities()))
                            finalVelYBox.setText(formatOutputString(yAccelSolver.getFinalVelocities()))
                        }


                        if (displacementAngleCheck.isChecked)
                        {
                            val xDisplacement = xAccelSolver.getDisplacement()
                            val yDisplacement = yAccelSolver.getDisplacement()
                            val displacement = sqrt(xDisplacement * xDisplacement + yDisplacement * yDisplacement)
                            var angle = atan2(yDisplacement,xDisplacement)
                            if(angle < 0.0)
                            {
                                angle += 2 * PI
                            }

                            displacementAngleBox.setText(formatOutputString(listOf(RAD_TO_DEG * angle)))
                            displacementXBox.setText(formatOutputString(listOf(displacement)))
                        }
                        else
                        {
                            displacementXBox.setText(formatOutputString(listOf(xAccelSolver.getDisplacement())))
                            displacementYBox.setText(formatOutputString(listOf(yAccelSolver.getDisplacement())))
                        }

                        if (accelerationAngleCheck.isChecked)
                        {
                            val xAcceleration = xAccelSolver.getAcceleration()
                            val yAcceleration = yAccelSolver.getAcceleration()
                            val acceleration = sqrt(xAcceleration * xAcceleration + yAcceleration * yAcceleration)
                            var angle = atan2(yAcceleration,xAcceleration)
                            if(angle < 0.0)
                            {
                                angle += 2 * PI
                            }

                            accelerationAngleBox.setText(formatOutputString(listOf(RAD_TO_DEG * angle)))
                            accelerationXBox.setText(formatOutputString(listOf(acceleration)))
                        }
                        else
                        {
                            accelerationXBox.setText(formatOutputString(listOf(xAccelSolver.getAcceleration())))
                            accelerationYBox.setText(formatOutputString(listOf(yAccelSolver.getAcceleration())))
                        }

                        val times = mutableListOf<Double>()

                        times.addAll(xAccelSolver.getTimes())
                        times.addAll(yAccelSolver.getTimes())

                        timeBox.setText(formatOutputString(times.distinct()))
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

                        if (initVelAngleCheck.isChecked)
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

                            initVelAngleBox.setText(formatOutputString(angles.distinct()))
                            initVelXBox.setText(formatOutputString(velocities.distinct()))
                        }
                        else
                        {
                            initVelXBox.setText(formatOutputString(xAccelSolver.getInitVelocities()))
                            initVelYBox.setText(formatOutputString(yAccelSolver.getInitVelocities()))
                        }

                        if (finalVelAngleCheck.isChecked)
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

                            finalVelAngleBox.setText(formatOutputString(angles.distinct()))
                            finalVelXBox.setText(formatOutputString(velocities.distinct()))
                        }
                        else
                        {
                            finalVelXBox.setText(formatOutputString(xAccelSolver.getFinalVelocities()))
                            finalVelYBox.setText(formatOutputString(yAccelSolver.getFinalVelocities()))
                        }


                        if (displacementAngleCheck.isChecked)
                        {
                            val xDisplacement = xAccelSolver.getDisplacement()
                            val yDisplacement = yAccelSolver.getDisplacement()
                            val displacement = sqrt(xDisplacement * xDisplacement + yDisplacement * yDisplacement)
                            var angle = atan2(yDisplacement,xDisplacement)
                            if(angle < 0.0)
                            {
                                angle += 2 * PI
                            }

                            displacementAngleBox.setText(formatOutputString(listOf(RAD_TO_DEG * angle)))
                            displacementXBox.setText(formatOutputString(listOf(displacement)))
                        }
                        else
                        {
                            displacementXBox.setText(formatOutputString(listOf(xAccelSolver.getDisplacement())))
                            displacementYBox.setText(formatOutputString(listOf(yAccelSolver.getDisplacement())))
                        }

                        if (accelerationAngleCheck.isChecked)
                        {
                            val xAcceleration = xAccelSolver.getAcceleration()
                            val yAcceleration = yAccelSolver.getAcceleration()
                            val acceleration = sqrt(xAcceleration * xAcceleration + yAcceleration * yAcceleration)
                            var angle = atan2(yAcceleration,xAcceleration)
                            if(angle < 0.0)
                            {
                                angle += 2 * PI
                            }

                            accelerationAngleBox.setText(formatOutputString(listOf(RAD_TO_DEG * angle)))
                            accelerationXBox.setText(formatOutputString(listOf(acceleration)))
                        }
                        else
                        {
                            accelerationXBox.setText(formatOutputString(listOf(xAccelSolver.getAcceleration())))
                            accelerationYBox.setText(formatOutputString(listOf(yAccelSolver.getAcceleration())))
                        }

                        val times = mutableListOf<Double>()

                        times.addAll(xAccelSolver.getTimes())
                        times.addAll(yAccelSolver.getTimes())

                        timeBox.setText(formatOutputString(times.distinct()))
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
                if (initVelAngleCheck.isChecked)
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

                    initVelAngleBox.setText(formatOutputString(angles.distinct()))
                    initVelXBox.setText(formatOutputString(velocities.distinct()))
                }
                else
                {
                    initVelXBox.setText(formatOutputString(xAccelSolver.getInitVelocities()))
                    initVelYBox.setText(formatOutputString(yAccelSolver.getInitVelocities()))
                }

                if (finalVelAngleCheck.isChecked)
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

                    finalVelAngleBox.setText(formatOutputString(angles.distinct()))
                    finalVelXBox.setText(formatOutputString(velocities.distinct()))
                }
                else
                {
                    finalVelXBox.setText(formatOutputString(xAccelSolver.getFinalVelocities()))
                    finalVelYBox.setText(formatOutputString(yAccelSolver.getFinalVelocities()))
                }


                if (displacementAngleCheck.isChecked)
                {
                    val xDisplacement = xAccelSolver.getDisplacement()
                    val yDisplacement = yAccelSolver.getDisplacement()
                    val displacement = sqrt(xDisplacement * xDisplacement + yDisplacement * yDisplacement)
                    var angle = atan2(yDisplacement,xDisplacement)
                    if(angle < 0.0)
                    {
                        angle += 2 * PI
                    }

                    displacementAngleBox.setText(formatOutputString(listOf(RAD_TO_DEG * angle)))
                    displacementXBox.setText(formatOutputString(listOf(displacement)))
                }
                else
                {
                    displacementXBox.setText(formatOutputString(listOf(xAccelSolver.getDisplacement())))
                    displacementYBox.setText(formatOutputString(listOf(yAccelSolver.getDisplacement())))
                }

                if (accelerationAngleCheck.isChecked)
                {
                    val xAcceleration = xAccelSolver.getAcceleration()
                    val yAcceleration = yAccelSolver.getAcceleration()
                    val acceleration = sqrt(xAcceleration * xAcceleration + yAcceleration * yAcceleration)
                    var angle = atan2(yAcceleration,xAcceleration)
                    if(angle < 0.0)
                    {
                        angle += 2 * PI
                    }

                    accelerationAngleBox.setText(formatOutputString(listOf(RAD_TO_DEG * angle)))
                    accelerationXBox.setText(formatOutputString(listOf(acceleration)))
                }
                else
                {
                    accelerationXBox.setText(formatOutputString(listOf(xAccelSolver.getAcceleration())))
                    accelerationYBox.setText(formatOutputString(listOf(yAccelSolver.getAcceleration())))
                }

                val times = mutableListOf<Double>()

                times.addAll(xAccelSolver.getTimes())
                times.addAll(yAccelSolver.getTimes())

                timeBox.setText(formatOutputString(times.distinct()))

                solved = true
            }
        }
        //End finding correct strategy and solve or print error
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

    fun clear(view: View)
    {
        clearEditTextRecursive(view.rootView)
        solved = false
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
