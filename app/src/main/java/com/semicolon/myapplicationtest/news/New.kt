package com.semicolon.syaqa.component

import android.util.Log
import androidx.compose.foundation.*
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
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
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import com.semicolon.myapplicationtest.R
import kotlin.math.pow
import kotlin.math.sqrt
@Composable
fun DragAndDropCirclesImproved(apiCoordinates: List<Pair<Int, Int>>) {
    // Constants for sizing
    val circleSize = 50.dp
    val targetSize = 54.dp

    // Define circle colors
    val circleColors = listOf(
        Color(0xFFFF5733), // Circle 1 - Orange-red
        Color(0xFF33A8FF), // Circle 2 - Blue
        Color(0xFF33FF57)  // Circle 3 - Green
    )

    // State for image dimensions
    var imageWidth by remember { mutableStateOf(1f) }
    var imageHeight by remember { mutableStateOf(1f) }

    // Create target positions based on percentages
    val dropTargets = remember(imageWidth, imageHeight) {
        apiCoordinates.mapIndexed { index, (topPercent, leftPercent) ->
            TargetPosition(
                id = index + 1,
                leftPos = (imageWidth * leftPercent) / 100,
                topPos = (imageHeight * topPercent) / 100
            )
        }
    }

    // State for circle positions
    val circlePositions = remember { List(3) { CirclePosition() } }

    // Track image container bounds
    var imageContainerBounds by remember { mutableStateOf(androidx.compose.ui.geometry.Rect.Zero) }

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
                .zIndex(1f) // Ensure source container renders above image
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
                for (i in 0 until 3) {
                    val density =LocalDensity.current
                    if (!circlePositions[i].isDropped) {
                        DraggableCircle(
                            id = i + 1,
                            color = circleColors[i],
                            size = circleSize,
                            onDrop = { x, y ->
                                Log.d("DragDrop", "Drop at x=$x, y=$y")
                                // Check if dropped on a target
                                for (target in dropTargets) {
                                    // Calculate target center position in absolute coordinates
                                    val targetCenterX = imageContainerBounds.left + target.leftPos
                                    val targetCenterY = imageContainerBounds.top + target.topPos

                                    Log.d("DragDrop", "Target ${target.id} at x=$targetCenterX, y=$targetCenterY")

                                    // Calculate distance between drop point and target center
                                    val distance = kotlin.math.sqrt(
                                        (x - targetCenterX).pow(2) +
                                                (y - targetCenterY).pow(2)
                                    )

                                    // Get target radius in pixels
                                    val targetRadius = with(density) { (targetSize / 2).toPx() }

                                    Log.d("DragDrop", "Distance: $distance, Target radius: $targetRadius")

                                    // Check if drop is within target bounds with more tolerance
                                    if (distance < targetRadius * 1.5f) { // Added tolerance factor
                                        Log.d("DragDrop", "Drop detected on target ${target.id}")

                                        // Remove any existing circle from this target
                                        for (j in 0 until 3) {
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
                    imageWidth = coordinates.size.width.toFloat()
                    imageHeight = coordinates.size.height.toFloat()
                    imageContainerBounds = coordinates.boundsInRoot()
                    Log.d("DragDrop", "Image bounds: $imageContainerBounds")
                    Log.d("DragDrop", "Image width: $imageWidth, height: $imageHeight")
                }
        ) {
            // Background image
            Image(
                painter = painterResource(id = R.drawable.backgg),
                contentDescription = "Background image",
                contentScale = ContentScale.FillBounds,
                modifier = Modifier.fillMaxSize()
            )

            // Drop targets
            dropTargets.forEach { target ->
                Box(
                    modifier = Modifier
                        .size(targetSize)
                        .offset {
                            // Center the target around its position point
                            IntOffset(
                                target.leftPos.toInt() - (targetSize / 2).toPx().toInt(),
                                target.topPos.toInt() - (targetSize / 2).toPx().toInt()
                            )
                        }
                        .border(
                            width = 2.dp,
                            color = Color.Gray,
                            shape = CircleShape
                        )
                        .background(Color(0x4DFFFFFF), CircleShape)
                ) {
                    // Find if there's a circle on this target
                    for (i in 0 until 3) {
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

        // Reset button
        Button(
            onClick = {
                // Reset all circles
                for (i in 0 until 3) {
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
    }
}

@Composable
fun DraggableCircle(
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
                    onDragStart = {
                        isDragging = true
                        Log.d("DragDrop", "Started dragging circle $id")
                    },
                    onDrag = { change, dragAmount ->
                        change.consume()
                        dragOffset += dragAmount
                    },
                    onDragEnd = {
                        val dropX = originalPosition.x + dragOffset.x
                        val dropY = originalPosition.y + dragOffset.y
                        Log.d("DragDrop", "Ending drag of circle $id at position $dropX, $dropY")
                        onDrop(dropX, dropY)
                        isDragging = false
                        dragOffset = Offset.Zero
                    },
                    onDragCancel = {
                        isDragging = false
                        dragOffset = Offset.Zero
                        Log.d("DragDrop", "Drag cancelled for circle $id")
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

// Data class for target positions
data class TargetPosition(
    val id: Int,
    val leftPos: Float,
    val topPos: Float
)

// Circle position state class
class CirclePosition {
    var isDropped by mutableStateOf(false)
    var targetId by mutableStateOf<Int?>(null)
}

// Extension function for power calculation
private fun Float.pow(exponent: Int): Float {
    var result = 1f
    repeat(exponent) { result *= this }
    return result
}