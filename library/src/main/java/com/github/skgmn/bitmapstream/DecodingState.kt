package com.github.skgmn.bitmapstream

internal open class DecodingState {
    open fun setPhase(phase: Int) = Unit

    companion object {
        const val PHASE_METADATA = 0
        const val PHASE_BITMAP = 1
        const val PHASE_COMLETE = 2
    }
}