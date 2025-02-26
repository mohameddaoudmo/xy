package com.semicolon.myapplicationtest

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.text.font.FontWeight
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
fun DragAndDropCirclesAppsss(apiCoordinates: List<Pair<Int, Int>>) {
    val circleSize = 50.dp
    val targetSize = 54.dp

    val circleColors = listOf(
        Color(0xFFFF5733), // Orange-red
        Color(0xFF33A8FF), // Blue
        Color(0xFF33FF57)  // Green
    )

    var imageWidth by remember { mutableStateOf(1f) }
    var imageHeight by remember { mutableStateOf(1f) }

    val dropTargets = remember(imageWidth, imageHeight) {
        apiCoordinates.mapIndexed { index, (topPercent, leftPercent) ->
            TargetPosition(
                id = index + 1,
                leftPos = (imageWidth * leftPercent) / 100,
                topPos = (imageHeight * topPercent) / 100
            )
        }
    }

    val circlePositions = remember { List(3) { CirclePosition() } }

    var imageContainerBounds by remember { mutableStateOf(androidx.compose.ui.geometry.Rect.Zero) }

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

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0xFFF0F0F0), RoundedCornerShape(8.dp))
                .padding(20.dp),
            contentAlignment = Alignment.Center
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(30.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                for (i in 0 until 3) {
                    val density = LocalDensity.current
                    if (!circlePositions[i].isDropped) {
                        DraggableCircle(
                            id = i + 1,
                            color = circleColors[i],
                            size = circleSize,
                            onDrop = { x, y ->
                                for (target in dropTargets) {
                                    val targetCenterX = with(density) {
                                        imageContainerBounds.left + target.leftPos
                                    }
                                    val targetCenterY = with(density) {
                                        imageContainerBounds.top + target.topPos
                                    }

                                    val distance = kotlin.math.sqrt(
                                        (x - targetCenterX).pow(2) +
                                                (y - targetCenterY).pow(2)
                                    )

                                    if (distance < with(density) { (targetSize / 2).toPx() }) {
                                        for (j in 0 until 3) {
                                            if (circlePositions[j].targetId == target.id) {
                                                circlePositions[j].isDropped = false
                                                circlePositions[j].targetId = null
                                            }
                                        }

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
                }
        ) {
            Image(
                painter = painterResource(id = R.drawable.backgg),
                contentDescription = "Background image",
                contentScale = ContentScale.FillBounds,
                modifier = Modifier.fillMaxSize()
            )

            dropTargets.forEach { target ->
                Box(
                    modifier = Modifier
                        .size(targetSize)
                        .offset { IntOffset(target.leftPos.toInt(), target.topPos.toInt()) }
                        .border(2.dp, Color.Gray, CircleShape)
                        .background(Color(0x4DFFFFFF), CircleShape)
                ) {
                    for (i in 0 until 3) {
                        if (circlePositions[i].isDropped && circlePositions[i].targetId == target.id) {
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

        Button(
            onClick = {
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

data class TargetPosition(
    val id: Int,
    val leftPos: Float,
    val topPos: Float
)

class CirclePosition {
    var isDropped by mutableStateOf(false)
    var targetId by mutableStateOf<Int?>(null)
}

private fun Float.pow(exponent: Int): Float {
    var result = 1f
    repeat(exponent) { result *= this }
    return result
}
