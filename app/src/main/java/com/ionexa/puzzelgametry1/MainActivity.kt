package com.ionexa.puzzelgametry1

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ionexa.puzzelgametry1.ui.theme.PuzzelGameTry1Theme
import kotlinx.coroutines.delay
import kotlin.random.Random

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            PuzzelGameTry1Theme {
                LazyVerticalGridDemo()
            }
        }
    }
}

@Composable
fun LazyVerticalGridDemo() {
    var firstClick by remember { mutableStateOf(false) }
    var secondClick by remember { mutableStateOf(false) }
    var firstState by remember { mutableStateOf(0) }
    var secondState by remember { mutableStateOf(11) }
    var firstIndex by remember { mutableStateOf(100) }
    var secondIndex by remember { mutableStateOf(100) }

    val imagedata = initdata()
    var tilesData by remember {
        mutableStateOf(generateTilesData(imagedata))
    }

    LaunchedEffect(firstClick, secondClick) {
        if (firstIndex != 100 && secondIndex != 100) {
            if (firstIndex != secondIndex) {
                if (firstState != 100 && secondState != 100) {
                    if (firstState == secondState) {
                        val newTilesData = tilesData.toMutableList()
                        newTilesData[firstIndex] = null
                        newTilesData[secondIndex] = null
                        tilesData = newTilesData.filterNotNull()
                        secondClick = false
                        firstClick = false
                        firstIndex = 100
                        secondIndex = 100
                    } else {
                        if (firstClick && secondClick) {
                            // Reset both clicks if they are not equal
                            firstClick = false
                            secondClick = false
                        }
                    }
                }
            }
        }

        // Handle timeout logic for firstClick
        if (firstClick) {
            delay(2500)
            if (!secondClick) {
                // Do nothing, waiting for second click to be true
                firstClick = false
                firstState = 100
                firstIndex = 100
            }
        }
        if (secondClick) {
            delay(2500)
            if (!firstClick) {
                // Do nothing, waiting for second click to be true
                secondClick = false
                secondState = 100
                secondIndex = 100
            }
        }

        // Handle timeout logic for secondClick

    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(top = 40.dp)
    ) {
        Text(text = "First click: $firstClick, State: $firstState, Index: $firstIndex")
        Text(text = "Second click: $secondClick, State: $secondState, Index: $secondIndex")
        if (tilesData.size >= 1)
            LazyVerticalGrid(
                columns = GridCells.Adaptive(80.dp),
                contentPadding = PaddingValues(
                    start = 12.dp,
                    top = 16.dp,
                    end = 12.dp,
                    bottom = 16.dp
                ),
                content = {
                    items(tilesData.size) { index ->
                        val tile = tilesData[index]
                        if (tile != null) {
                            FlipAnimation(
                                data = tile,
                                index = index,
                                firstClick = firstClick,
                                secondClick = secondClick,
                                firstState = firstState,
                                secondState = secondState,
                                setFirstState = { firstState = it },
                                setSecondState = { secondState = it },
                                setFirstClick = { firstClick = it },
                                setSecondClick = { secondClick = it },
                                setFirstIndex = { firstIndex = it },
                                setSecondIndex = { secondIndex = it }
                            )
                        }
                    }
                }
            ) else {
            Image(painter = painterResource(id = R.drawable.youwin), contentDescription = "you win")
        }
    }
}


@Composable
fun FlipAnimation(
    data: ImageData,
    index: Int,
    firstClick: Boolean,
    secondClick: Boolean,
    firstState: Int,
    secondState: Int,
    setFirstState: (value: Int) -> Unit,
    setSecondState: (value: Int) -> Unit,
    setFirstClick: (value: Boolean) -> Unit,
    setSecondClick: (value: Boolean) -> Unit,
    setFirstIndex: (Int) -> Unit,
    setSecondIndex: (Int) -> Unit
) {
    val scaleX = remember { Animatable(1f) }
    var isFlipped by remember { mutableStateOf(false) }
var test=false
    LaunchedEffect(isFlipped) {
        if (isFlipped) {
            scaleX.animateTo(
                targetValue = -1f,
                animationSpec = tween(durationMillis = 300)
            )
            if (!firstClick && !secondClick) {
                setFirstClick(true)
                setFirstState(data.uniqueNumber)
                setFirstIndex(index)
            } else if (firstClick && !secondClick) {
                setSecondClick(true)
                setSecondState(data.uniqueNumber)
                setSecondIndex(index)
            }

            delay(1000)
            scaleX.animateTo(
                targetValue = 1f,
                animationSpec = tween(durationMillis = 300)
            )
            isFlipped = false

            if (firstClick && secondClick) {
                setFirstClick(false)
                setFirstState(100)
                setFirstIndex(100)
                setSecondClick(false)
                setSecondState(100)
                setSecondIndex(100)
            }
        }
    }

    Box(
        modifier = Modifier
            .size(100.dp)
            .graphicsLayer(scaleX = scaleX.value),
        contentAlignment = Alignment.Center
    ) {
        Card(
            colors = CardDefaults.cardColors(containerColor = Color.White),
            modifier = Modifier
                .padding(4.dp)
                .fillMaxWidth(),
            elevation = CardDefaults.elevatedCardElevation(12.dp),
            onClick = { isFlipped = true }
        ) {
            AnimatedVisibility(visible = isFlipped, enter = fadeIn(), exit = fadeOut()) {
                Image(
                    painter = painterResource(id = data.image),
                    modifier = Modifier.fillMaxSize(),
                    contentDescription = ""
                )
            }
            AnimatedVisibility(visible = !isFlipped, enter = fadeIn(), exit = fadeOut()) {
                Image(
                    painter = painterResource(id = if(test)data.image else R.drawable.covertiles),
                    modifier = Modifier.fillMaxSize(),
                    contentDescription = ""
                )
            }
        }
    }
}

fun generateUniqueRandomNumbers(range: Int): List<Int> {
    return (0 until range).toList().shuffled(Random)
}

fun generateTilesData(imagedata: List<ImageData>): List<ImageData?> {
    val d = generateUniqueRandomNumbers(24)
    val tilesData = mutableListOf<ImageData?>()
    d.forEach {
        val index = it % imagedata.size
        tilesData.add(imagedata[index])
    }
    return tilesData
}

fun initdata(): List<ImageData> {
    return listOf(
        ImageData(image = R.drawable.emojie1, 1),
        ImageData(image = R.drawable.emojie2, 2),
        ImageData(image = R.drawable.emojie3, 3),
        ImageData(image = R.drawable.emojie4, 4),
        ImageData(image = R.drawable.emojie5, 5),
        ImageData(image = R.drawable.emojie6, 6),
        ImageData(image = R.drawable.emojie7, 7),
        ImageData(image = R.drawable.emojie8, 8),
        ImageData(image = R.drawable.emojie9, 9),
        ImageData(image = R.drawable.emojie10, 10),
        ImageData(image = R.drawable.emojie11, 11),
        ImageData(image = R.drawable.emojie12, 12)
    )
}

data class ImageData(val image: Int, val uniqueNumber: Int)
