package by.niaprauski.designsystem.ui.modifier

import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.clipRect
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import kotlin.math.cos
import kotlin.math.sin
import kotlin.random.Random

fun Modifier.mysteriousBackground(
    color: Color,
    strokeWidth: Dp = 2.dp,
): Modifier = this.drawWithCache {
    val strokeWidthPx = strokeWidth.toPx()
    val width = size.width
    val height = size.height

    val cache = CelestialAstrolabePattern.getOrBuildCache(
        width = width,
        height = height,
        color = color,
        strokeWidthPx = strokeWidthPx,
    )

    onDrawBehind {
        clipRect {
            cache.draw(
                drawScope = this,
            )
        }
    }
}

private class CirclePrimitive(
    val center: Offset,
    val radius: Float,
    val color: Color,
    val stroke: Stroke,
    val isFilled: Boolean = false,
)

private class LinePrimitive(
    val start: Offset,
    val end: Offset,
    val color: Color,
    val strokeWidthPx: Float,
)

private class AstrolabeCache(
    val circles: List<CirclePrimitive>,
    val lines: List<LinePrimitive>,
) {
    fun draw(drawScope: DrawScope) {

        for (c in circles) {
            if (c.isFilled) {
                drawScope.drawCircle(
                    color = c.color,
                    radius = c.radius,
                    center = c.center,
                )
            } else {
                drawScope.drawCircle(
                    color = c.color,
                    radius = c.radius,
                    center = c.center,
                    style = c.stroke,
                )
            }
        }

        for (l in lines) {
            drawScope.drawLine(
                color = l.color,
                start = l.start,
                end = l.end,
                strokeWidth = l.strokeWidthPx,
            )
        }
    }
}

private object CelestialAstrolabePattern {
    private val starRaysCount: Int = Random.nextInt(10, 16)
    private val seed: Float = Random.nextFloat() * 360f

    private var cachedWidth: Float = -1f
    private var cachedHeight: Float = -1f
    private var cachedColor: Color = Color.Unspecified
    private var cachedStrokeWidthPx: Float = -1f
    private var lastCache: AstrolabeCache? = null

    fun getOrBuildCache(
        width: Float,
        height: Float,
        color: Color,
        strokeWidthPx: Float,
    ): AstrolabeCache {
        val existing = lastCache
        if (existing != null &&
            cachedWidth == width &&
            cachedHeight == height &&
            cachedColor == color &&
            cachedStrokeWidthPx == strokeWidthPx
        ) {
            return existing
        }

        val newCache = buildCache(
            width = width,
            height = height,
            color = color,
            strokeWidthPx = strokeWidthPx,
        )

        cachedWidth = width
        cachedHeight = height
        cachedColor = color
        cachedStrokeWidthPx = strokeWidthPx
        lastCache = newCache

        return newCache
    }

    private fun buildCache(
        width: Float,
        height: Float,
        color: Color,
        strokeWidthPx: Float,
    ): AstrolabeCache {
        val circles = mutableListOf<CirclePrimitive>()
        val lines = mutableListOf<LinePrimitive>()

        val rand = Random(seed.toInt())

        val maxRadius = maxOf(width, height) * (0.45f + rand.nextFloat() * 0.5f)
        val center = Offset(
            x = width * (0.15f + rand.nextFloat() * 0.7f),
            y = height * (0.15f + rand.nextFloat() * 0.7f),
        )

        val ringCount = rand.nextInt(3, 6)
        for (i in 1..ringCount) {
            val radius = maxRadius * (i.toFloat() / ringCount)
            circles.add(
                CirclePrimitive(
                    center = center,
                    radius = radius,
                    color = color.copy(alpha = color.alpha * 0.5f),
                    stroke = Stroke(strokeWidthPx),
                ),
            )
        }

        val offsetCenter = Offset(
            x = center.x + maxRadius * 0.25f,
            y = center.y - maxRadius * 0.2f,
        )
        circles.add(
            CirclePrimitive(
                center = offsetCenter,
                radius = maxRadius * 0.75f,
                color = color.copy(alpha = color.alpha * 0.35f),
                stroke = Stroke(strokeWidthPx * 0.8f),
            ),
        )

        val angleStep = (2 * Math.PI / starRaysCount).toFloat()
        val intersectionPoints = mutableListOf<Offset>()

        for (i in 0 until starRaysCount) {
            val angle = i * angleStep + Math.toRadians(seed.toDouble()).toFloat()
            val outerPoint = Offset(
                x = center.x + maxRadius * cos(angle.toDouble()).toFloat(),
                y = center.y + maxRadius * sin(angle.toDouble()).toFloat(),
            )

            lines.add(
                LinePrimitive(
                    start = center,
                    end = outerPoint,
                    color = color.copy(alpha = color.alpha * 0.3f),
                    strokeWidthPx = strokeWidthPx * 0.7f,
                ),
            )

            intersectionPoints.add(outerPoint)
        }

        val step = starRaysCount / 3
        for (i in intersectionPoints.indices) {
            val nextIndex = (i + step) % intersectionPoints.size
            lines.add(
                LinePrimitive(
                    start = intersectionPoints[i],
                    end = intersectionPoints[nextIndex],
                    color = color.copy(alpha = color.alpha * 0.25f),
                    strokeWidthPx = strokeWidthPx * 0.6f,
                ),
            )
        }

        intersectionPoints.forEach { point ->
            circles.add(
                CirclePrimitive(
                    center = point,
                    radius = strokeWidthPx * 2.2f,
                    color = color,
                    stroke = Stroke(0f),
                    isFilled = true,
                ),
            )
        }

        return AstrolabeCache(
            circles = circles,
            lines = lines,
        )
    }
}