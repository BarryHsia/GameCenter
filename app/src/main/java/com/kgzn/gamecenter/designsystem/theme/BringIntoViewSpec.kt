package com.kgzn.gamecenter.designsystem.theme

import androidx.compose.foundation.gestures.BringIntoViewSpec
import kotlin.math.abs

val AppBringIntoViewSpec = object : BringIntoViewSpec {
    val parentFraction = 0.2f
    val childFraction = 0f

    override fun calculateScrollDistance(offset: Float, size: Float, containerSize: Float): Float {
        val leadingEdgeOfItemRequestingFocus = offset
        val trailingEdgeOfItemRequestingFocus = offset + size

        val sizeOfItemRequestingFocus =
            abs(trailingEdgeOfItemRequestingFocus - leadingEdgeOfItemRequestingFocus)
        val childSmallerThanParent = sizeOfItemRequestingFocus <= containerSize
        val initialTargetForLeadingEdge =
            parentFraction * containerSize - (childFraction * sizeOfItemRequestingFocus)
        val spaceAvailableToShowItem = containerSize - initialTargetForLeadingEdge

        val targetForLeadingEdge =
            if (
                childSmallerThanParent && spaceAvailableToShowItem < sizeOfItemRequestingFocus
            ) {
                containerSize - sizeOfItemRequestingFocus
            } else {
                initialTargetForLeadingEdge
            }

        return leadingEdgeOfItemRequestingFocus - targetForLeadingEdge
    }
}