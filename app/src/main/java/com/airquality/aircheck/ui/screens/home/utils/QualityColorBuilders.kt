package com.airquality.aircheck.ui.screens.home.utils

import com.airquality.aircheck.R

data class QualityColorModel(
    val imageColor: Int,
    val image: Int
)

object QualityColorBuilders {

    fun getQualityColorModel(value: Double): QualityColorModel {
        return when {
            value <= 50 -> goodQualityBuilder()
            value <= 100 -> moderateQualityBuilder()
            value <= 150 -> unhealthySensibleQualityBuilder()
            value <= 200 -> unhealthyQualityBuilder()
            value <= 300 -> veryUnhealthyQualityBuilder()
            value > 300 -> hazardousQualityBuilder()
            else -> defaultQualityBuilder()
        }
    }

    private fun defaultQualityBuilder(): QualityColorModel {
        return QualityColorModel(
            imageColor = R.color.defaultColor,
            image = R.drawable.gris
        )
    }

    private fun goodQualityBuilder(): QualityColorModel {
        return QualityColorModel(
            imageColor = R.color.goodColor,
            image = R.drawable.verde
        )
    }

    private fun moderateQualityBuilder(): QualityColorModel {
        return QualityColorModel(
            imageColor = R.color.moderateColor,
            image = R.drawable.amarillo
        )
    }

    private fun unhealthySensibleQualityBuilder(): QualityColorModel {
        return QualityColorModel(
            imageColor = R.color.unhealthySensibleColor,
            image = R.drawable.naranja
        )
    }

    private fun unhealthyQualityBuilder(): QualityColorModel {
        return QualityColorModel(
            imageColor = R.color.unhealthyColor,
            image = R.drawable.rojo
        )
    }

    private fun veryUnhealthyQualityBuilder(): QualityColorModel {
        return QualityColorModel(
            imageColor = R.color.veryUnhealthyColor,
            image = R.drawable.morado
        )
    }

    private fun hazardousQualityBuilder(): QualityColorModel {
        return QualityColorModel(
            imageColor = R.color.hazardousColor,
            image = R.drawable.granate
        )
    }
}