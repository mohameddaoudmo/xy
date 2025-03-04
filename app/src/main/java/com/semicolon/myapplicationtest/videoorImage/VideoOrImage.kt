package com.semicolon.myapplicationtest.videoorImage
import android.util.Log
import androidx.annotation.OptIn
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
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.boundsInRoot
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.zIndex
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.AspectRatioFrameLayout
import androidx.media3.ui.PlayerView
import com.semicolon.myapplicationtest.R
import kotlin.math.pow
import kotlin.math.sqrt

@OptIn(UnstableApi::class)
@Composable
fun MediaDropSystem(
    apiCoordinates: List<Pair<Int, Int>>,
    isVideo: Boolean = false,
    imageResourceId: Int = R.drawable.backgg,
    videoUri: String = ""
) {
    // Constants for sizing
    val circleSize = 50.dp
    val targetSize = 54.dp

    // Define circle colors
    val circleColors = listOf(
        Color(0xFFFF5733), // Circle 1 - Orange-red
        Color(0xFF33A8FF), // Circle 2 - Blue
        Color(0xFF33FF57)  // Circle 3 - Green
    )

    // State for media container dimensions
    var mediaWidth by remember { mutableStateOf(1f) }
    var mediaHeight by remember { mutableStateOf(1f) }

    // State for circle positions
    val circlePositions = remember { List(3) { CirclePosition() } }

    // Track media container bounds
    var mediaContainerBounds by remember { mutableStateOf(androidx.compose.ui.geometry.Rect.Zero) }

    // Video player state if needed
    var videoController by remember { mutableStateOf<ExoPlayer?>(null) }
    val context = LocalContext.current

    // Initialize video player if needed
    LaunchedEffect(isVideo, videoUri) {
        if (isVideo && videoUri.isNotEmpty()) {
            videoController = ExoPlayer.Builder(context).build().apply {
                setMediaItem(MediaItem.fromUri(videoUri))
                prepare()
             playWhenReady = true
                repeatMode = Player.REPEAT_MODE_ONE
            }
        } else {
            videoController?.release()
            videoController = null
        }
    }

    // Clean up video player
    DisposableEffect(Unit) {
        onDispose {
            videoController?.release()
            videoController = null
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(30.dp)
    ) {
        // Title text
        Text(
            text = "Drag the circles to their matching positions on ${if (isVideo) "video" else "image"}",
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
                val density = LocalDensity.current
                // Only show circles that haven't been dropped
                for (i in 0 until 3) {
                    if (!circlePositions[i].isDropped) {
                        DraggableCircleFixed(
                            id = i + 1,
                            color = circleColors[i],
                            size = circleSize,
                            mediaContainerBounds = mediaContainerBounds,
                            onDrop = { x, y ->
                                // Convert the drop coordinates to be relative to the media container
                                val relativeX = x - mediaContainerBounds.left
                                val relativeY = y - mediaContainerBounds.top

                                Log.d("MediaDrop", "Absolute drop at x=$x, y=$y")
                                Log.d("MediaDrop", "Relative drop at x=$relativeX, y=$relativeY")

                                // Create target positions based on percentages
                                val dropTargets = apiCoordinates.mapIndexed { index, (topPercent, leftPercent) ->
                                    // Calculate target position in pixels (same coordinate system as the drop)
                                    val targetX = (mediaWidth * leftPercent) / 100
                                    val targetY = (mediaHeight * topPercent) / 100

                                    Log.d("MediaDrop", "Target ${index+1} at x=$targetX, y=$targetY (from $leftPercent%, $topPercent%)")

                                    Triple(index + 1, targetX, targetY)
                                }

                                // Check if dropped on a target
                                for ((targetId, targetX, targetY) in dropTargets) {
                                    // Calculate distance between relative drop point and target center
                                    val distance = kotlin.math.sqrt(
                                        (relativeX - targetX).pow(2) +
                                                (relativeY - targetY).pow(2)
                                    )

                                    // Get target radius in pixels
                                    val targetRadius = with(density) { (targetSize / 2).toPx() }

                                    Log.d("MediaDrop", "Distance to target $targetId: $distance, Target radius: $targetRadius")

                                    // Check if drop is within target bounds with tolerance
                                    if (distance < targetRadius * 2.5f) {
                                        Log.d("MediaDrop", "Drop detected on target $targetId")

                                        // Remove any existing circle from this target
                                        for (j in 0 until 3) {
                                            if (circlePositions[j].targetId == targetId) {
                                                circlePositions[j].isDropped = false
                                                circlePositions[j].targetId = null
                                            }
                                        }

                                        // Set this circle as dropped to this target
                                        circlePositions[i].isDropped = true
                                        circlePositions[i].targetId = targetId
                                        break
                                    }
                                }
                            }
                        )
                    }
                }
            }
        }

        // Target media container with drop positions
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(300.dp)
                .background(Color(0xFFE0E0E0), RoundedCornerShape(8.dp))
                .clip(RoundedCornerShape(8.dp))
                .onGloballyPositioned { coordinates ->
                    mediaWidth = coordinates.size.width.toFloat()
                    mediaHeight = coordinates.size.height.toFloat()
                    mediaContainerBounds = coordinates.boundsInRoot()
                    Log.d("MediaDrop", "Media bounds: $mediaContainerBounds")
                    Log.d("MediaDrop", "Media width: $mediaWidth, height: $mediaHeight")
                }
        ) {
            // Display either background image or video
            if (isVideo && videoController != null) {
                // Video background
                AndroidView(
                    factory = { ctx ->
                        PlayerView(ctx).apply {
                            player = videoController
                            useController = false

                            resizeMode = AspectRatioFrameLayout.RESIZE_MODE_FILL
                        }
                    },
                    modifier = Modifier.fillMaxSize()
                )
            } else {
                // Image background
                Image(
                    painter = painterResource(id = imageResourceId),
                    contentDescription = "Background image",
                    contentScale = ContentScale.FillBounds,
                    modifier = Modifier.fillMaxSize()
                )
            }

            // Draw target positions and dropped circles
            apiCoordinates.forEachIndexed { index, (topPercent, leftPercent) ->
                val targetId = index + 1

                // Calculate position in pixels
                val targetX = (mediaWidth * leftPercent) / 100
                val targetY = (mediaHeight * topPercent) / 100

                // Draw target circle
                Box(
                    modifier = Modifier
                        .size(targetSize)
                        .offset {
                            IntOffset(
                                targetX.toInt() - (targetSize / 2).toPx().toInt(),
                                targetY.toInt() - (targetSize / 2).toPx().toInt()
                            )
                        }

                        .background(Color(0xFF54BFFF).copy(0.35f), CircleShape)
                )

                // Place any dropped circles at their target positions
                for (i in 0 until 3) {
                    if (circlePositions[i].isDropped && circlePositions[i].targetId == targetId) {
                        Box(
                            modifier = Modifier
                                .size(circleSize)
                                .offset {
                                    IntOffset(
                                        targetX.toInt() - (circleSize / 2).toPx().toInt(),
                                        targetY.toInt() - (circleSize / 2).toPx().toInt()
                                    )
                                }
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

// Modified draggable circle with renamed parameter
@Composable
fun DraggableCircleFixed(
    id: Int,
    color: Color,
    size: Dp,
    mediaContainerBounds: Rect,
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
            .background(Color.White.copy(alpha = opacity))
            .border(BorderStroke(1.dp,Color(0xFF1BA9FF)), CircleShape)
            .pointerInput(id) {
                detectDragGestures(
                    onDragStart = {
                        isDragging = true
                        Log.d("MediaDrop", "Started dragging circle $id")
                    },
                    onDrag = { change, dragAmount ->
                        change.consume()
                        dragOffset += dragAmount
                    },
                    onDragEnd = {
                        val dropX = originalPosition.x + dragOffset.x
                        val dropY = originalPosition.y + dragOffset.y
                        Log.d("MediaDrop", "Ending drag of circle $id at absolute position $dropX, $dropY")
                        onDrop(dropX, dropY)
                        isDragging = false
                        dragOffset = Offset.Zero
                    },
                    onDragCancel = {
                        isDragging = false
                        dragOffset = Offset.Zero
                        Log.d("MediaDrop", "Drag cancelled for circle $id")
                    }
                )
            }
            .zIndex(if (isDragging) 10f else 1f),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "$id",
            color = Color(0xFF1BA9FF),
            fontWeight = FontWeight.Bold
        )
    }
}

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