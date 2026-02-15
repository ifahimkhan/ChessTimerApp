package com.fahim.chesstimer.ui.component

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.Easing
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntSize
import kotlin.math.abs
import kotlin.math.floor
import kotlin.math.roundToInt

/**
 * Animated counter with a vertical scrolling odometer effect.
 * Each digit scrolls independently through intermediate values.
 *
 * When the counter increases, digits roll upward; when it decreases, they roll downward.
 * Handles multi-digit transitions (e.g. 99 -> 100) by keying each digit column
 * to its positional significance (ones, tens, hundreds, etc.).
 *
 * @param targetValue The number to display.
 * @param modifier Modifier applied to the root Row.
 * @param style TextStyle for each digit.
 * @param color Text color for all digits.
 * @param animationDurationMs Duration of the scroll animation per digit change.
 * @param easing Easing curve applied to the scroll animation.
 * @param prefix Optional text rendered before the digits (e.g. "$").
 * @param suffix Optional text rendered after the digits (e.g. " pts").
 * @param minDigits Minimum number of digits displayed (zero-padded on the left).
 */
@Composable
fun AnimatedCounter(
    targetValue: Int,
    modifier: Modifier = Modifier,
    style: TextStyle = MaterialTheme.typography.displayLarge,
    color: Color = Color.Unspecified,
    animationDurationMs: Int = 400,
    easing: Easing = FastOutSlowInEasing,
    prefix: String = "",
    suffix: String = "",
    minDigits: Int = 1
) {
    val isNegative = targetValue < 0
    val absValue = abs(targetValue)
    val digitString = absValue.toString().padStart(minDigits, '0')

    // Direction tracking: read the old value during composition, update afterwards.
    var previousValue by remember { mutableIntStateOf(targetValue) }
    val isIncrementing = targetValue >= previousValue
    SideEffect { previousValue = targetValue }

    // Pre-measure digit dimensions so every column has a uniform size.
    val textMeasurer = rememberTextMeasurer()
    val digitSize = remember(style) {
        var maxW = 0
        var maxH = 0
        for (d in 0..9) {
            val result = textMeasurer.measure("$d", style)
            if (result.size.width > maxW) maxW = result.size.width
            if (result.size.height > maxH) maxH = result.size.height
        }
        IntSize(maxW, maxH)
    }

    val density = LocalDensity.current
    val digitHeightDp = with(density) { digitSize.height.toDp() }
    val digitMinWidthDp = with(density) { digitSize.width.toDp() }

    Row(modifier = modifier, verticalAlignment = Alignment.CenterVertically) {
        if (isNegative) {
            Text("-", style = style, color = color)
        }
        if (prefix.isNotEmpty()) {
            Text(prefix, style = style, color = color)
        }

        // Key each column by its positional significance so animation state
        // survives digit-count changes (e.g. the ones column keeps its
        // Animatable when the value goes from 99 to 100).
        digitString.forEachIndexed { index, char ->
            val positionFromRight = digitString.length - 1 - index
            key(positionFromRight) {
                OdometerDigit(
                    digit = char.digitToInt(),
                    isIncrementing = isIncrementing,
                    digitHeightPx = digitSize.height,
                    animationDurationMs = animationDurationMs,
                    easing = easing,
                    style = style,
                    color = color,
                    modifier = Modifier
                        .height(digitHeightDp)
                        .widthIn(min = digitMinWidthDp)
                )
            }
        }

        if (suffix.isNotEmpty()) {
            Text(suffix, style = style, color = color)
        }
    }
}

/**
 * Single digit column that scrolls vertically through 0-9.
 *
 * Maintains a cumulative scroll position so wrapping transitions
 * (e.g. 9 -> 0 on increment) scroll naturally in the expected direction.
 * Uses [graphicsLayer] for the per-frame offset so only integer-boundary
 * crossings trigger recomposition.
 */
@Composable
private fun OdometerDigit(
    digit: Int,
    isIncrementing: Boolean,
    digitHeightPx: Int,
    animationDurationMs: Int,
    easing: Easing,
    style: TextStyle,
    color: Color,
    modifier: Modifier = Modifier
) {
    val position = remember { Animatable(digit.toFloat()) }

    LaunchedEffect(digit) {
        val lastTarget = position.targetValue.roundToInt()
        val lastDigit = lastTarget.mod(10)
        if (lastDigit == digit) return@LaunchedEffect

        // Pick the shortest path around the 0-9 ring;
        // use the overall counter direction as a tiebreaker.
        val forwardDiff = (digit - lastDigit + 10).mod(10)
        val backwardDiff = (lastDigit - digit + 10).mod(10)

        val newTarget = when {
            forwardDiff < backwardDiff -> lastTarget + forwardDiff
            forwardDiff > backwardDiff -> lastTarget - backwardDiff
            isIncrementing             -> lastTarget + forwardDiff
            else                       -> lastTarget - backwardDiff
        }

        position.animateTo(
            targetValue = newTarget.toFloat(),
            animationSpec = tween(durationMillis = animationDurationMs, easing = easing)
        )
    }

    // Recompose only when the set of visible digits changes (integer crossing),
    // not on every animation frame.
    val intPosition by remember { derivedStateOf { floor(position.value).toInt() } }

    Box(modifier = modifier.clipToBounds(), contentAlignment = Alignment.Center) {
        for (i in (intPosition - 1)..(intPosition + 2)) {
            key(i) {
                val displayDigit = i.mod(10)
                Text(
                    text = "$displayDigit",
                    style = style,
                    color = color,
                    modifier = Modifier.graphicsLayer {
                        translationY = (i.toFloat() - position.value) * digitHeightPx
                    }
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun AnimatedCounterPreview() {
    MaterialTheme {
        AnimatedCounter(targetValue = 42, minDigits = 3)
    }
}
