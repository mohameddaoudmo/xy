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
import androidx.compose.foundation.*
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.unit.IntOffset

import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun DragAndDropCirclesApps() {
    // Constants for sizing
    val circleSize = 50.dp
    val targetSize = 54.dp

    // Define circle colors to match HTML
    val circleColors = listOf(
        Color(0xFFFF5733), // Circle 1 - Orange-red
        Color(0xFF33A8FF), // Circle 2 - Blue
        Color(0xFF33FF57)  // Circle 3 - Green
    )
        // Define drop target positions to match HTML
    val dropTargets = listOf(
        TargetPosition(id = 1, topPos = 60.dp, leftPos = 45.dp),
        TargetPosition(id = 2, topPos = 30.dp, leftPos = 72.dp),
        TargetPosition(id = 3, topPos = 15.dp, leftPos = 21.dp)
    )

    // State for circle positions
    class CirclePosition {
        var isDropped by mutableStateOf(false)
        var targetId by mutableStateOf<Int?>(null)
    }

    // Initialize circle positions
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
                .zIndex(1f) // Key fix: Ensure source container renders above image
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
                    val density = LocalDensity.current
                    if (!circlePositions[i].isDropped) {
                        DraggableCircle(
                            id = i + 1,
                            color = circleColors[i],
                            size = circleSize,
                            onDrop = { x, y ->
                                // Check if dropped on a target
                                for (target in dropTargets) {
                                    // Calculate target center position in the image container
                                    val targetCenterX = with(density) {
                                        imageContainerBounds.left + target.leftPos.toPx()
                                    }
                                    val targetCenterY = with(density) {
                                        imageContainerBounds.top + target.topPos.toPx()
                                    }

                                    // Check if drop position is within target bounds
                                    val distance = kotlin.math.sqrt(
                                        (x - targetCenterX).pow(2) +
                                                (y - targetCenterY).pow(2)
                                    )

                                    if (distance < with(density) { (targetSize/2).toPx() }) {
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
                Box(
                    modifier = Modifier
                        .size(targetSize)
                        .offset(
                            x = target.leftPos - targetSize / 2,
                            y = target.topPos - targetSize / 2
                        )
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

// Data class for target positions
data class TargetPosition(
    val id: Int,
    val topPos: Dp,
    val leftPos: Dp
)

// Extension function for power calculation
private fun Float.pow(exponent: Int): Float {
    var result = 1f
    repeat(exponent) { result *= this }
    return result
}