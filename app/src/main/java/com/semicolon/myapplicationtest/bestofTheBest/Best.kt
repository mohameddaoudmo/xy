package com.semicolon.myapplicationtest.bestofTheBest

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
fun DragAndDropCirclesWithFreePositioningmmm(apiCoordinates: List<Pair<Int, Int>> = emptyList()) {
    // Constants for sizing
    val circleSize = 50.dp

    // Define circle colors
    val circleColors = listOf(
        Color(0xFFFF5733), // Circle 1 - Orange-red
        Color(0xFF33A8FF), // Circle 2 - Blue
        Color(0xFF33FF57)  // Circle 3 - Green
    )

    // State for image dimensions
    var imageWidth by remember { mutableStateOf(1f) }
    var imageHeight by remember { mutableStateOf(1f) }

    // Track image container bounds
    var imageContainerBounds by remember { mutableStateOf(androidx.compose.ui.geometry.Rect.Zero) }

    // State for each circle's position (relative to the image)
    class CirclePositionData {
        var isDropped by mutableStateOf(false)
        var positionX by mutableStateOf(0f)
        var positionY by mutableStateOf(0f)
    }

    val circlePositions = remember { List(3) { CirclePositionData() } }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(30.dp)
    ) {
        // Title text
        Text(
            text = "Drag the circles to any position on the image",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center
        )

        // Source circles container
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
                for (i in 0 until 3) {
                    if (!circlePositions[i].isDropped) {
                        DraggableCircleaa(
                            id = i + 1,
                            color = circleColors[i],
                            size = circleSize,
                            onDrop = { x, y ->
                                Log.d("DragDrop", "Drop at x=$x, y=$y")

                                // Check if drop is within the image bounds
                                if (x >= imageContainerBounds.left &&
                                    x <= imageContainerBounds.right &&
                                    y >= imageContainerBounds.top &&
                                    y <= imageContainerBounds.bottom) {

                                    // Calculate position relative to the image container
                                    val relativeX = x - imageContainerBounds.left
                                    val relativeY = y - imageContainerBounds.top

                                    Log.d("DragDrop", "Relative position: x=$relativeX, y=$relativeY")

                                    // Store the position and mark as dropped
                                    circlePositions[i].isDropped = true
                                    circlePositions[i].positionX = relativeX
                                    circlePositions[i].positionY = relativeY
                                }
                            }
                        )
                    }
                }
            }
        }

        // Target image
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

            // Draw dropped circles at their exact dropped positions
            circlePositions.forEachIndexed { index, position ->
                if (position.isDropped ) {
                    Box(
                        modifier = Modifier
                            .size(circleSize)
                            .offset {
                                IntOffset(
                                    position.positionX.toInt() - (circleSize / 2).toPx().toInt(),
                                    position.positionY.toInt() - (circleSize / 2).toPx().toInt()
                                )
                            }
                            .clip(CircleShape)
                            .background(
                                if (position.isDropped) circleColors[index]
                                else circleColors[index].copy(alpha = 0.5f)
                            )
                            .border(
                                width = 2.dp,
                                color = if (position.isDropped) Color.Transparent
                                else circleColors[index],
                                shape = CircleShape
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        if (position.isDropped) {
                            Text(
                                text = "${index + 1}",
                                color = Color.White,
                                fontWeight = FontWeight.Bold
                            )
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
fun DraggableCircleaa(
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