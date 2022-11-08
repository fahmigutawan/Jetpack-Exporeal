package com.bcc.exporeal.ui.style

import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.sp
import com.bcc.exporeal.R

object AppType {
    fun h1() = TextStyle(
        fontFamily = FontFamily(Font(R.font.worksans_semibold)),
        fontSize = 24.sp
    )

    fun h2() = TextStyle(
        fontFamily = FontFamily(Font(R.font.worksans_semibold)),
        fontSize = 20.sp
    )

    fun h3() = TextStyle(
        fontFamily = FontFamily(Font(R.font.worksans_semibold)),
        fontSize = 18.sp
    )

    fun h4() = TextStyle(
        fontFamily = FontFamily(Font(R.font.worksans_semibold)),
        fontSize = 16.sp
    )

    fun h5() = TextStyle(
        fontFamily = FontFamily(Font(R.font.worksans_semibold)),
        fontSize = 14.sp
    )

    fun body1() = TextStyle(
        fontFamily = FontFamily(Font(R.font.worksans_regular)),
        fontSize = 16.sp
    )

    fun body2() = TextStyle(
        fontFamily = FontFamily(Font(R.font.worksans_regular)),
        fontSize = 14.sp
    )

    fun body3() = TextStyle(
        fontFamily = FontFamily(Font(R.font.worksans_regular)),
        fontSize = 12.sp
    )

    fun body1Semibold() = TextStyle(
        fontFamily = FontFamily(Font(R.font.worksans_semibold)),
        fontSize = 16.sp
    )

    fun body2Semibold() = TextStyle(
        fontFamily = FontFamily(Font(R.font.worksans_semibold)),
        fontSize = 14.sp
    )

    fun body3Semibold() = TextStyle(
        fontFamily = FontFamily(Font(R.font.worksans_semibold)),
        fontSize = 12.sp
    )

    fun buttonNormal() = TextStyle(
        fontFamily = FontFamily(Font(R.font.worksans_semibold)),
        fontSize = 14.sp
    )

    fun buttonSmall() = TextStyle(
        fontFamily = FontFamily(Font(R.font.worksans_semibold)),
        fontSize = 12.sp
    )

    fun fieldLabel() = TextStyle(
        fontFamily = FontFamily(Font(R.font.worksans_regular)),
        fontSize = 14.sp
    )

    fun fieldPlaceholder() = TextStyle(
        fontFamily = FontFamily(Font(R.font.worksans_regular)),
        fontSize = 14.sp
    )

    fun bottomMenu() = TextStyle(
        fontFamily = FontFamily(Font(R.font.worksans_regular)),
        fontSize = 12.sp
    )
}