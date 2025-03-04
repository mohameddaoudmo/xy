package com.semicolon.myapplicationtest

import HalfCircleProgressIndicatorWithPercentage
import HalfCircleThreeSectionChart
import HalfCircleThreeSectionChartWithPercentage
import ThreeSectionHalfCircleProgressIndicator
import android.annotation.SuppressLint
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.semicolon.myapplicationtest.ui.theme.MyApplicationtestTheme
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape

import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.toSize
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape

import androidx.compose.runtime.*

import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlin.math.roundToInt
import androidx.compose.ui.composed
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInRoot
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import com.semicolon.myapplicationtest.news.CoordinatedDragAndDrop
import com.semicolon.myapplicationtest.ss.DragAndDropCirclesWithPrePositioning
import com.semicolon.myapplicationtest.videoorImage.MediaDropSystem
import com.semicolon.syaqa.componat.DragAndDropCirclesApps
import com.semicolon.syaqa.componat.DragAndDropCirclesAppss
import com.semicolon.syaqa.component.DragAndDropCirclesImproved
import kotlin.math.pow
import kotlin.math.roundToInt
import kotlin.math.sqrt

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationtestTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Column(modifier = Modifier.fillMaxSize().padding(innerPadding), verticalArrangement = Arrangement.Center, horizontalAlignment = Alignment.CenterHorizontally) {

                        val apiCoordinates = listOf(
                            60 to 45,  // 60% from top, 45% from left
                            30 to 72,  // 30% from top, 72% from left
                            15 to 21   // 15% from top, 21% from left
                        )
                        val videoUri = "android.resource://${packageName}/${R.raw.traffic}"

                        MediaDropSystem(    apiCoordinates = listOf(
                            Pair(60, 45),  // (topPercent, leftPercent) for target 1
                            Pair(30, 72),  // (topPercent, leftPercent) for target 2
                            Pair(15, 21)   // (topPercent, leftPercent) for target 3
                        ),isVideo = true, videoUri =videoUri)
                }}
            }
        }
    }
}@Composable
fun ChartScreen() {
    HalfCircleThreeSectionChart(
        sectionValues = listOf(30f, 50f, 20f),
        sectionColors = listOf(Color.Red, Color.Green, Color.Blue),
        modifier = Modifier.size(120.dp),
        strokeWidth = 12.dp
    )
}

@Composable
fun ExampleScreen() {
    HalfCircleThreeSectionChartWithPercentage(
        sectionValues = listOf(30f, 50f, 20f),
        sectionColors = listOf(Color.Red, Color.Green, Color.Blue),
        modifier = Modifier.size(120.dp),
        strokeWidth = 12.dp
    )
}


@Composable
fun DragAndDropCirclesApp() {
    val circleRadius = 25.dp
    val circleSize = circleRadius * 2
    val dropTargetSize = circleSize + 4.dp

    // Define the circles with their colors
    val circleColors = listOf(
        Color(0xFFFF5733), // Orange-red
        Color(0xFF33A8FF), // Blue
        Color(0xFF33FF57)  // Green
    )

    // Define the drop target positions
    val dropTargets = listOf(
        DropTarget(id = 1, xPos = 100.dp, yPos = 50.dp),
        DropTarget(id = 2, xPos = 300.dp, yPos = 150.dp),
        DropTarget(id = 3, xPos = 150.dp, yPos = 200.dp)
    )

    // Track which circle is currently being dragged
    var draggedCircleId by remember { mutableStateOf<Int?>(null) }

    // Track absolute screen positions for dragging
    var dragOffset by remember { mutableStateOf(Offset.Zero) }

    // Store imageContainer position
    var imageContainerPosition by remember { mutableStateOf(Offset.Zero) }

    // Track the circles' positions and states
    val circles = remember {
        List(3) { idx ->
            mutableStateOf(CircleState(id = idx + 1, color = circleColors[idx]))
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(30.dp)
    ) {
        Text(
            text = "Drag the circles to their matching positions",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center
        )

        // Source container for circles
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0xFFF0F0F0))
                .padding(20.dp),
            contentAlignment = Alignment.Center
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(30.dp),
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(vertical = 10.dp)
            ) {
                // Display only circles that aren't placed on a target
                circles.forEachIndexed { index, circleState ->
                    if (!circleState.value.isPlaced && draggedCircleId != circleState.value.id) {
                        val density = LocalDensity.current

                        Box(

                            modifier = Modifier
                                .size(circleSize)
                                .shadow(4.dp, CircleShape)
                                .clip(CircleShape)
                                .background(circleState.value.color)
                                .pointerInput(circleState.value.id) {
                                    detectDragGestures(
                                        onDragStart = { offset ->
                                            draggedCircleId = circleState.value.id
                                            dragOffset = offset
                                        },
                                        onDrag = { change, dragAmount ->
                                            change.consume()
                                            dragOffset += dragAmount
                                        },
                                        onDragEnd = {
                                            // Check if dropped on a target
                                            var placed = false

                                            // Calculate relative position to the image container
                                            val relativeX = dragOffset.x - imageContainerPosition.x
                                            val relativeY = dragOffset.y - imageContainerPosition.y

                                            dropTargets.forEach { target ->
                                                // Convert target position to pixels for comparison
                                                val targetX = with(density) { target.xPos.toPx() }
                                                val targetY = with(density) { target.yPos.toPx() }

                                                // Calculate distance between drag position and target
                                                val distance = sqrt(
                                                    (relativeX - targetX).pow(2) +
                                                            (relativeY - targetY).pow(2)
                                                )

                                                // If close enough to target, place the circle
                                                if (distance < with(density) { dropTargetSize.toPx() / 2 }) {
                                                    // Remove any existing circle from this target
                                                    circles.forEach { circle ->
                                                        if (circle.value.isPlaced &&
                                                            circle.value.placedTargetId == target.id) {
                                                            circle.value = circle.value.copy(
                                                                isPlaced = false,
                                                                placedTargetId = null
                                                            )
                                                        }
                                                    }

                                                    // Place current circle
                                                    circleState.value = circleState.value.copy(
                                                        isPlaced = true,
                                                        placedTargetId = target.id
                                                    )
                                                    placed = true
                                                }
                                            }

                                            draggedCircleId = null
                                            dragOffset = Offset.Zero
                                        },
                                        onDragCancel = {
                                            draggedCircleId = null
                                            dragOffset = Offset.Zero
                                        }
                                    )
                                },
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = circleState.value.id.toString(),
                                color = Color.White,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }

        // Target image with drop positions
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(300.dp)
                .background(Color(0xFFE0E0E0))
                .clip(RoundedCornerShape(8.dp))
                .onGloballyPositioned { coordinates ->
                    // Store the container position for accurate drop calculation
                    imageContainerPosition = coordinates.positionInRoot()
                }
        ) {
            // Background image
            Image(
                painter = painterResource(id = R.drawable.ic_launcher_background),
                contentDescription = "Background image",
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )

            // Drop targets
            dropTargets.forEach { target ->
                Box(
                    modifier = Modifier
                        .size(dropTargetSize)
                        .offset(x = target.xPos - dropTargetSize/2, y = target.yPos - dropTargetSize/2)
                        .border(
                            width = 2.dp,
                            color = Color(0xFF666666),
                            shape = CircleShape
                        )
                        .background(Color(0x4DFFFFFF), CircleShape)
                ) {
                    // Show placed circle if exists
                    val placedCircle = circles.find {
                        it.value.isPlaced && it.value.placedTargetId == target.id
                    }

                    placedCircle?.let { circleState ->
                        Box(
                            modifier = Modifier
                                .size(circleSize)
                                .align(Alignment.Center)
                                .shadow(4.dp, CircleShape)
                                .clip(CircleShape)
                                .background(circleState.value.color),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = circleState.value.id.toString(),
                                color = Color.White,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }

        // Dragged circle overlay (appears above everything else)
        draggedCircleId?.let { id ->
            val circleState = circles.find { it.value.id == id }?.value ?: return@let

            Box(
                modifier = Modifier
                    .offset {
                        IntOffset(
                            dragOffset.x.roundToInt(),
                            dragOffset.y.roundToInt()
                        )
                    }
                    .size(circleSize)
                    .shadow(8.dp, CircleShape)
                    .clip(CircleShape)
                    .background(circleState.color.copy(alpha = 0.8f))
                    .zIndex(10f),  // Ensure it's above everything else
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = id.toString(),
                    color = Color.White,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        // Reset button
        Button(
            onClick = {
                // Reset all circles to their initial state
                circles.forEach { circle ->
                    circle.value = circle.value.copy(
                        isPlaced = false,
                        placedTargetId = null
                    )
                }
            },
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4A90E2)),
            modifier = Modifier.padding(top = 20.dp)
        ) {
            Text(
                text = "Reset All",
                color = Color.White,
                fontSize = 16.sp,
                modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
            )
        }
    }
}

// Data classes
data class CircleState(
    val id: Int,
    val color: Color,
    val isPlaced: Boolean = false,
    val placedTargetId: Int? = null
)

data class DropTarget(
    val id: Int,
    val xPos: androidx.compose.ui.unit.Dp,
    val yPos: androidx.compose.ui.unit.Dp
)