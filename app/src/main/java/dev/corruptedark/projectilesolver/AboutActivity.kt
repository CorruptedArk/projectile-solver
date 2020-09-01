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

import dev.corruptedark.projectilesolver.BuildConfig
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.method.LinkMovementMethod
import android.text.method.MovementMethod
import androidx.compose.foundation.Image
import androidx.compose.foundation.ScrollableColumn
import androidx.compose.foundation.Text
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.Colors
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.LayoutModifier
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.setContent
import androidx.compose.ui.res.imageResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.AnnotatedString
import kotlinx.android.synthetic.main.activity_about.*
import androidx.compose.ui.graphics.Color as ComposeColor

class AboutActivity : AppCompatActivity() {

    //private lateinit var binding: ActivityAboutBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        //binding = ActivityAboutBinding.inflate(layoutInflater)
        //setContentView(binding.root)
        setContent {
            AboutLayout()
        }

        val actionBar = supportActionBar
        actionBar!!.subtitle = "About"
        actionBar.setDisplayHomeAsUpEnabled(true)
        actionBar.setBackgroundDrawable(ColorDrawable(Color.parseColor("#FF232323")))
        window.statusBarColor = Color.parseColor("#ff151515")
        window.navigationBarColor = Color.parseColor("#ff151515")

    }

    @Composable
    private fun AboutLayout(
        backgroundColor: ComposeColor = ComposeColor(0xff494949)
    )
    {
        Surface(color = backgroundColor) {
            ScrollableColumn(Modifier.fillMaxSize()) {
                Image(vectorResource(R.drawable.ic_icon), Modifier.fillMaxWidth())
                Text(getString(R.string.github_link), color = ComposeColor.White)
                Text(AnnotatedString(getString(R.string.about_info, BuildConfig.VERSION_NAME)), color = ComposeColor.White)
            }
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
}

