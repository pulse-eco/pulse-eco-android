package com.netcetera.skopjepulse.pulseappbar

import com.netcetera.skopjepulse.base.model.MeasurementType

/**
 * Model used for each of the tabs in [MeasurementTypeTabBarView].
 */
data class MeasurementTypeTab(val measurementType: MeasurementType, val title : String)