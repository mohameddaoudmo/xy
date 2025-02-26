import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun ThreeSectionHalfCircleProgressIndicator(
    progress: Float,
    maxProgress: Float = 100f,
    section1Color: Color = Color(0xFF4CAF50), // Green
    section2Color: Color = Color(0xFFFFC107), // Yellow
    section3Color: Color = Color(0xFFF44336), // Red
    backgroundColor: Color =  Color(0xFFF44336),
    strokeWidth: Float = 8f, // Thinner stroke
    animationDuration: Int = 1000,
    showPercentage: Boolean = true,
    // Define where each section ends (as a percentage of the max progress)
    section1End: Float = 33.33f,
    section2End: Float = 66.66f
) {
    // Ensure progress is between 0 and maxProgress
    val normalizedProgress = progress.coerceIn(0f, maxProgress)
    val progressPercentage = (normalizedProgress / maxProgress) * 100

    // Animate the progress
    var animationPlayed by remember { mutableStateOf(false) }
    val currentProgress = animateFloatAsState(
        targetValue = if (animationPlayed) normalizedProgress else 0f,
        animationSpec = tween(animationDuration),
        label = "progressAnimation"
    )

    LaunchedEffect(key1 = true) {
        animationPlayed = true
    }

    // Calculate angles for the sections
    val section1Angle = (section1End / 100f) * 180f
    val section2Angle = ((section2End - section1End) / 100f) * 180f
    val section3Angle = ((100f - section2End) / 100f) * 180f

    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .width(140.dp)
            .height(60.dp) // Much smaller height
            .padding(4.dp) // Smaller padding
    ) {
        Canvas(
            modifier = Modifier
                .fillMaxWidth()
                .height(60.dp) // Much smaller canvas height
        ) {
            // Background arc (180 degrees - half circle)
            drawArc(
                color = backgroundColor,
                startAngle = 180f,
                sweepAngle = 180f,
                useCenter = false,
                topLeft = Offset(0f, 0f),
                size = Size(size.width, size.height * 2),
                style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
            )

            // Calculate how much of each section to show based on current progress
            val currentAngle = (currentProgress.value / maxProgress) * 180f

            // Section 1 (0% to section1End%)
            val section1SweepAngle = minOf(currentAngle, section1Angle)
            if (section1SweepAngle > 0) {
                drawArc(
                    color = section1Color,
                    startAngle = 180f,
                    sweepAngle = section1SweepAngle,
                    useCenter = false,
                    topLeft = Offset(0f, 0f),
                    size = Size(size.width, size.height * 2),
                    style = Stroke(width = strokeWidth, cap = StrokeCap.Butt)
                )
            }

            // Section 2 (section1End% to section2End%)
            if (currentAngle > section1Angle) {
                val section2SweepAngle = minOf(currentAngle - section1Angle, section2Angle)
                drawArc(
                    color = section2Color,
                    startAngle = 180f + section1Angle,
                    sweepAngle = section2SweepAngle,
                    useCenter = false,
                    topLeft = Offset(0f, 0f),
                    size = Size(size.width, size.height * 2),
                    style = Stroke(width = strokeWidth, cap = StrokeCap.Butt)
                )
            }

            // Section 3 (section2End% to 100%)
            if (currentAngle > section1Angle + section2Angle) {
                val section3SweepAngle = minOf(currentAngle - section1Angle - section2Angle, section3Angle)
                drawArc(
                    color = section3Color,
                    startAngle = 180f + section1Angle + section2Angle,
                    sweepAngle = section3SweepAngle,
                    useCenter = false,
                    topLeft = Offset(0f, 0f),
                    size = Size(size.width, size.height * 2),
                    style = Stroke(width = strokeWidth, cap = StrokeCap.Butt)
                )
            }

            // Draw section dividers (small gaps or lines)
            if (section1Angle > 0 && section1Angle < 180f) {
                drawLine(
                    color = backgroundColor,
                    start = calculatePointOnArc(
                        center = Offset(size.width / 2, size.height),
                        radius = size.width / 2 - strokeWidth / 2,
                        angleInDegrees = 180f + section1Angle
                    ),
                    end = calculatePointOnArc(
                        center = Offset(size.width / 2, size.height),
                        radius = size.width / 2 + strokeWidth / 2,
                        angleInDegrees = 180f + section1Angle
                    ),
                    strokeWidth = 1f // Thinner divider
                )
            }

            if (section2End > 0 && section2End < 100f) {
                drawLine(
                    color = backgroundColor,
                    start = calculatePointOnArc(
                        center = Offset(size.width / 2, size.height),
                        radius = size.width / 2 - strokeWidth / 2,
                        angleInDegrees = 180f + section1Angle + section2Angle
                    ),
                    end = calculatePointOnArc(
                        center = Offset(size.width / 2, size.height),
                        radius = size.width / 2 + strokeWidth / 2,
                        angleInDegrees = 180f + section1Angle + section2Angle
                    ),
                    strokeWidth = 1f // Thinner divider
                )
            }
        }

        if (showPercentage) {
            Text(
                text = "${progressPercentage.toInt()}%",
                fontWeight = FontWeight.Bold,
                fontSize = 12.sp, // Much smaller text
                color = Color.Black,
                modifier = Modifier.align(Alignment.Center)
            )
        }
    }
}

// Helper function to calculate points on the arc
private fun calculatePointOnArc(center: Offset, radius: Float, angleInDegrees: Float): Offset {
    val angleInRadians = Math.toRadians(angleInDegrees.toDouble())
    val x = center.x + radius * kotlin.math.cos(angleInRadians)
    val y = center.y + radius * kotlin.math.sin(angleInRadians)
    return Offset(x.toFloat(), y.toFloat())
}

@Composable
fun HalfCircleProgressIndicatorWithPercentage(
    progress: Float, // Expected value between 0.0 and 1.0
    modifier: Modifier = Modifier.size(200.dp),
    progressColor: Color = Color.Green,
    backgroundColor: Color = Color.LightGray,
    strokeWidth: Dp = 12.dp,
    percentageTextColor: Color = Color.Black,
    textStyle: TextStyle = TextStyle(fontSize = 24.sp, fontWeight = FontWeight.Bold)
) {
    Box(contentAlignment = Alignment.Center, modifier = modifier) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val strokePx = strokeWidth.toPx()
            // Draw the background half‑circle arc
            drawArc(
                color = backgroundColor,
                startAngle = 180f,
                sweepAngle = 180f,
                useCenter = false,
                style = Stroke(width = strokePx, cap = StrokeCap.Round)
            )
            // Draw the progress arc (its sweep is based on the progress value)
            drawArc(
                color = progressColor,
                startAngle = 180f,
                sweepAngle = 180f * progress,
                useCenter = false,
                style = Stroke(width = strokePx, cap = StrokeCap.Round)
            )
        }
        // Overlay the percentage text at the center
        Text(
            text = "${(progress * 100).toInt()}%",
            style = textStyle,
            color = percentageTextColor
        )
    }
}


@Composable
fun HalfCircleThreeSectionChart(
    sectionValues: List<Float>, // e.g., listOf(30f, 50f, 20f)
    sectionColors: List<Color>, // e.g., listOf(Color.Red, Color.Green, Color.Blue)
    modifier: Modifier = Modifier,
    strokeWidth: Dp = 8.dp,
    backgroundColor: Color = Color.LightGray
) {
    // Ensure we have exactly three sections
    require(sectionValues.size == 3 && sectionColors.size == 3) {
        "Provide exactly three values and three colors"
    }

    Canvas(modifier = modifier) {
        // Use the smallest dimension to form a square for our arc's bounding box
        val diameter = size.minDimension
        val strokePx = strokeWidth.toPx()

        // Define the bounding rectangle for the arcs (inset by half the stroke)
        val arcRect = Rect(
            offset = Offset(strokePx / 2, strokePx / 2),
            size = Size(diameter - strokePx, diameter - strokePx)
        )

        // First, draw the background half-circle
        drawArc(
            color = backgroundColor,
            startAngle = 180f,
            sweepAngle = 180f,
            useCenter = false,
            topLeft = arcRect.topLeft,
            size = arcRect.size,
            style = Stroke(width = strokePx, cap = StrokeCap.Butt)
        )

        // Calculate total so that each section is proportional to the whole
        val total = sectionValues.sum()
        var currentStartAngle = 180f

        sectionValues.forEachIndexed { index, value ->
            // Calculate the sweep angle for this section (relative to 180°)
            val sweepAngle = 180f * (value / total)
            drawArc(
                color = sectionColors[index],
                startAngle = currentStartAngle,
                sweepAngle = sweepAngle,
                useCenter = false,
                topLeft = arcRect.topLeft,
                size = arcRect.size,
                style = Stroke(width = strokePx, cap = StrokeCap.Butt)
            )
            currentStartAngle += sweepAngle
        }
    }
}



@Preview
@Composable
fun ThreeSectionHalfCircleProgressIndicatorPreview() {
    ThreeSectionHalfCircleProgressIndicator(
        progress = 75f,
        section1Color = Color(0xFF4CAF50), // Green
        section2Color = Color(0xFFFFC107), // Yellow
        section3Color = Color(0xFFF44336)  // Red
    )
}

@Composable
fun HalfCircleThreeSectionChartWithPercentage(
    sectionValues: List<Float>, // e.g., listOf(30f, 50f, 20f) where the first value is the "completed" portion
    sectionColors: List<Color>, // e.g., listOf(Color.Red, Color.Green, Color.Blue)
    modifier: Modifier = Modifier,
    strokeWidth: Dp = 8.dp,
    backgroundColor: Color = Color.LightGray,
    percentageTextColor: Color = Color.White,
    textStyle: TextStyle = TextStyle(fontSize = 14.sp, fontWeight = FontWeight.Bold)
) {
    // Ensure we have exactly three sections
    require(sectionValues.size == 3 && sectionColors.size == 3) {
        "Provide exactly three values and three colors"
    }

    // Calculate total and then determine the percentage for the first section
    val total = sectionValues.sum()
    val percentage = if (total > 0f) (sectionValues[0] / total * 100).toInt() else 0

    Box(modifier = modifier, contentAlignment = Alignment.Center) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            // Use the smallest dimension to form a square for our arc's bounding box
            val diameter = size.minDimension
            val strokePx = strokeWidth.toPx()

            // Define the bounding rectangle for the arcs (inset by half the stroke)
            val arcRect = Rect(
                offset = Offset(strokePx / 2, strokePx / 2),
                size = Size(diameter - strokePx, diameter - strokePx)
            )

            // Draw the background half‑circle (full 180°)
            drawArc(
                color = backgroundColor,
                startAngle = 180f,
                sweepAngle = 180f,
                useCenter = false,
                topLeft = arcRect.topLeft,
                size = arcRect.size,
                style = Stroke(width = strokePx, cap = StrokeCap.Butt)
            )

            // Draw each section proportionally
            var currentStartAngle = 180f
            sectionValues.forEachIndexed { index, value ->
                // Calculate the sweep angle for this section (relative to 180°)
                val sweepAngle = 180f * (value / total)
                drawArc(
                    color = sectionColors[index],
                    startAngle = currentStartAngle,
                    sweepAngle = sweepAngle,
                    useCenter = false,
                    topLeft = arcRect.topLeft,
                    size = arcRect.size,
                    style = Stroke(width = strokePx, cap = StrokeCap.Butt)
                )
                currentStartAngle += sweepAngle
            }
        }
        // Overlay the percentage text at the center of the half‑circle
        Text(
            text = "$percentage%",
            style = textStyle,
            color = percentageTextColor
        )
    }
}
