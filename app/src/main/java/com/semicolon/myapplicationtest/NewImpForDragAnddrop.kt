package com.semicolon.syaqa.componat

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.boundsInRoot
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import com.semicolon.myapplicationtest.R
import androidx.compose.ui.unit.IntOffset
import kotlin.math.roundToInt
import kotlin.math.sqrt

@Composable
fun DragAndDropCirclesAppss() {
    // Constants for sizing
    val circleSize = 50.dp
    val targetSize = 54.dp

    val density = LocalDensity.current
    val targetSizePx = with(density) { targetSize.toPx() }
    val circleSizePx = with(density) { circleSize.toPx() }

    // Define circle colors
    val circleColors = listOf(
        Color(0xFFFF5733), // Circle 1 - Orange-red
        Color(0xFF33A8FF), // Circle 2 - Blue
        Color(0xFF33FF57)  // Circle 3 - Green
    )

    // State to track the image dimensions
    var imageWidth by remember { mutableStateOf(0f) }
    var imageHeight by remember { mutableStateOf(0f) }

    // In a real app, these would come from your API
    // These values are percentages (0-100) of the image dimensions
    val dropTargetPercentages = listOf(
        TargetPercentagePosition(id = 1, xPercent = 45f, yPercent = 20f),
        TargetPercentagePosition(id = 2, xPercent = 72f, yPercent = 10f),
        TargetPercentagePosition(id = 3, xPercent = 21f, yPercent = 5f)
    )

    // Debug - log state
    var isDebugMode by remember { mutableStateOf(false) }
    var debugMessage by remember { mutableStateOf("") }

    // Track image container bounds
    var imageContainerBounds by remember { mutableStateOf(androidx.compose.ui.geometry.Rect.Zero) }

    // Calculated drop targets based on image dimensions
    val dropTargets = remember(imageWidth, imageHeight) {
        dropTargetPercentages.map { targetPercent ->
            val xPos = (targetPercent.xPercent / 100f) * imageWidth
            val yPos = (targetPercent.yPercent / 100f) * imageHeight

            TargetPositions(
                id = targetPercent.id,
                xPos = xPos,
                yPos = yPos
            )
        }
    }

    // State for circle positions
    class CirclePosition {
        var isDropped by mutableStateOf(false)
        var targetId by mutableStateOf<Int?>(null)
    }

    // Initialize circle positions
    val circlePositions = remember { List(3) { CirclePosition() } }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(30.dp)
    ) {
        // Title text
        Text(
            text = "Drag the circles to their matching positions",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center
        )

        // Source circles container with higher z-index
        Box(
            modifier = Modifier
                .zIndex(1f)
                .fillMaxWidth()
                .background(Color(0xFFF0F0F0), RoundedCornerShape(8.dp))
                .padding(20.dp),
            contentAlignment = Alignment.Center
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(30.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Only show circles that haven't been dropped
                for (i in 0..2) {
                    if (!circlePositions[i].isDropped) {
                        DraggableCircles(
                            id = i + 1,
                            color = circleColors[i],
                            size = circleSize,
                            onDrop = { x, y ->
                                // Check if dropped on a target
                                for (target in dropTargets) {
                                    // Calculate target center position in the image container
                                    val targetCenterX = imageContainerBounds.left + target.xPos
                                    val targetCenterY = imageContainerBounds.top + target.yPos

                                    // Distance between drop point and target center
                                    val distance = sqrt(
                                        (x - targetCenterX) * (x - targetCenterX) +
                                                (y - targetCenterY) * (y - targetCenterY)
                                    )

                                    // For debugging
                                    if (isDebugMode) {
                                        debugMessage = "Drop: ($x, $y), Target: ($targetCenterX, $targetCenterY), Distance: $distance, Threshold: ${targetSizePx * 0.75f}"
                                    }

                                    // More lenient threshold for dropping - use 75% of target size as threshold
                                    val dropThreshold = targetSizePx * 0.75f
                                    if (distance < dropThreshold) {
                                        // Remove any existing circle from this target
                                        for (j in 0..2) {
                                            if (circlePositions[j].targetId == target.id) {
                                                circlePositions[j].isDropped = false
                                                circlePositions[j].targetId = null
                                            }
                                        }

                                        // Set this circle as dropped to this target
                                        circlePositions[i].isDropped = true
                                        circlePositions[i].targetId = target.id
                                        break
                                    }
                                }
                            }
                        )
                    }
                }
            }
        }

        // Target image with drop positions
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(300.dp)
                .background(Color(0xFFE0E0E0), RoundedCornerShape(8.dp))
                .clip(RoundedCornerShape(8.dp))
                .onGloballyPositioned { coordinates ->
                    imageContainerBounds = coordinates.boundsInRoot()
                    imageWidth = coordinates.size.width.toFloat()
                    imageHeight = coordinates.size.height.toFloat()
                }
        ) {
            // Background image
            Image(
                painter = painterResource(id = R.drawable.backgg),
                contentDescription = "Background image",
                contentScale = ContentScale.FillWidth,
                modifier = Modifier.fillMaxSize()
            )

            // Drop targets
            dropTargets.forEach { target ->
                val halfTargetSize = targetSizePx / 2

                Box(
                    modifier = Modifier
                        .size(targetSize)
                        .offset {
                            IntOffset(
                                (target.xPos - halfTargetSize).roundToInt(),
                                (target.yPos - halfTargetSize).roundToInt()
                            )
                        }
                        .border(
                            width = 2.dp,
                            color = Color(0xFF666666),
                            shape = CircleShape
                        )
                        .background(Color(0x4DFFFFFF), CircleShape)
                ) {
                    // Find if there's a circle on this target
                    for (i in 0..2) {
                        if (circlePositions[i].isDropped && circlePositions[i].targetId == target.id) {
                            // Show the dropped circle
                            Box(
                                modifier = Modifier
                                    .size(circleSize)
                                    .align(Alignment.Center)
                                    .clip(CircleShape)
                                    .background(circleColors[i]),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "${i + 1}",
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                            break
                        }
                    }
                }
            }
        }

        // Debug text if in debug mode
        if (isDebugMode && debugMessage.isNotEmpty()) {
            Text(
                text = debugMessage,
                fontSize = 12.sp,
                color = Color.Gray,
                modifier = Modifier.padding(top = 8.dp)
            )
        }

        // Reset button
        Button(
            onClick = {
                // Reset all circles
                for (i in 0..2) {
                    circlePositions[i].isDropped = false
                    circlePositions[i].targetId = null
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

        // Toggle debug mode button (you can remove this in production)
        Button(
            onClick = { isDebugMode = !isDebugMode },
            colors = ButtonDefaults.buttonColors(
                containerColor = if (isDebugMode) Color(0xFF4CAF50) else Color(0xFF9E9E9E)
            ),
            modifier = Modifier.padding(top = 8.dp)
        ) {
            Text(
                text = if (isDebugMode) "Debug Mode: ON" else "Debug Mode: OFF",
                color = Color.White,
                fontSize = 14.sp
            )
        }
    }
}

@Composable
fun DraggableCircles(
    id: Int,
    color: Color,
    size: Dp,
    onDrop: (Float, Float) -> Unit
) {
    var isDragging by remember { mutableStateOf(false) }
    var dragOffset by remember { mutableStateOf(Offset.Zero) }
    var originalPosition by remember { mutableStateOf(Offset.Zero) }

    val opacity by remember { derivedStateOf { if (isDragging) 0.5f else 1.0f } }

    Box(
        modifier = Modifier
            .size(size)
            .onGloballyPositioned { coordinates ->
                if (!isDragging) {
                    originalPosition = coordinates.boundsInRoot().center
                }
            }
            .offset {
                if (isDragging) {
                    IntOffset(dragOffset.x.toInt(), dragOffset.y.toInt())
                } else {
                    IntOffset(0, 0)
                }
            }
            .clip(CircleShape)
            .background(color.copy(alpha = opacity))
            .pointerInput(id) {
                detectDragGestures(
                    onDragStart = { isDragging = true },
                    onDrag = { change, dragAmount ->
                        change.consume()
                        dragOffset += dragAmount
                    },
                    onDragEnd = {
                        val dropX = originalPosition.x + dragOffset.x
                        val dropY = originalPosition.y + dragOffset.y
                        onDrop(dropX, dropY)
                        isDragging = false
                        dragOffset = Offset.Zero
                    },
                    onDragCancel = {
                        isDragging = false
                        dragOffset = Offset.Zero
                    }
                )
            }
            .zIndex(if (isDragging) 10f else 1f),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "$id",
            color = Color.White,
            fontWeight = FontWeight.Bold
        )
    }
}

// Data class for target positions with actual coordinates
data class TargetPositions(
    val id: Int,
    val xPos: Float,  // Actual x position in pixels
    val yPos: Float   // Actual y position in pixels
)

// Data class for target positions with percentage values
data class TargetPercentagePosition(
    val id: Int,
    val xPercent: Float,  // x position as percentage of image width (0-100)
    val yPercent: Float   // y position as percentage of image height (0-100)
)