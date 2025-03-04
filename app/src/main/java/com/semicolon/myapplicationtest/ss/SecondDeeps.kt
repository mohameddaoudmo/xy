package com.semicolon.myapplicationtest.ss



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
fun DragAndDropCirclesWithPrePositioning(apiCoordinates: List<Pair<Int, Int>>) {
    val circleSize = 50.dp
    val targetSize = 54.dp

    val circleColors = listOf(
        Color(0xFFFF5733),
        Color(0xFF33A8FF),
        Color(0xFF33FF57)
    )

    var imageWidth by remember { mutableStateOf(1f) }
    var imageHeight by remember { mutableStateOf(1f) }
    var imageContainerBounds by remember { mutableStateOf(androidx.compose.ui.geometry.Rect.Zero) }

    val dropTargets = remember(imageWidth, imageHeight) {
        apiCoordinates.mapIndexed { index, (topPercent, leftPercent) ->
            TargetPosition(
                id = index + 1,
                leftPos = (imageWidth * leftPercent) / 100,
                topPos = (imageHeight * topPercent) / 100
            )
        }
    }

    val circlePositions = remember {
        dropTargets.map { target ->
            CirclePosition(
                isDropped = true,
                targetId = target.id,
                leftPos = target.leftPos,
                topPos = target.topPos
            )
        }
    }

    Column(
        modifier = Modifier.fillMaxSize().padding(20.dp),
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

            dropTargets.forEachIndexed { index, target ->
                Box(
                    modifier = Modifier
                        .size(targetSize)
                        .offset {
                            IntOffset(
                                target.leftPos.toInt() - (targetSize / 2).toPx().toInt(),
                                target.topPos.toInt() - (targetSize / 2).toPx().toInt()
                            )
                        }
                        .border(2.dp, Color.Gray, CircleShape)
                        .background(Color(0x4DFFFFFF), CircleShape)
                )
            }

            circlePositions.forEachIndexed { index, position ->
                Box(
                    modifier = Modifier
                        .size(circleSize)
                        .offset {
                            IntOffset(
                                position.leftPos.toInt() - (circleSize / 2).toPx().toInt(),
                                position.topPos.toInt() - (circleSize / 2).toPx().toInt()
                            )
                        }
                        .clip(CircleShape)
                        .background(circleColors[index]),
                    contentAlignment = Alignment.Center
                ) {
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

data class TargetPosition(val id: Int, val leftPos: Float, val topPos: Float)

data class CirclePosition(
    var isDropped: Boolean,
    var targetId: Int?,
    var leftPos: Float,
    var topPos: Float
)


private fun Float.pow(exponent: Int): Float {
    var result = 1f
    repeat(exponent) { result *= this }
    return result
}