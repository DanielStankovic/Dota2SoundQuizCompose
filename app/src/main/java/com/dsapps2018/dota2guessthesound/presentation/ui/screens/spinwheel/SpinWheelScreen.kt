package com.dsapps2018.dota2guessthesound.presentation.ui.screens.spinwheel

import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Scaffold
import androidx.compose.material3.ScaffoldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.paint
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.dsapps2018.dota2guessthesound.R
import com.dsapps2018.dota2guessthesound.data.util.toDp
import com.dsapps2018.dota2guessthesound.presentation.ui.composables.spin_wheel_compose.SpinWheel
import com.dsapps2018.dota2guessthesound.presentation.ui.composables.spin_wheel_compose.SpinWheelDefaults
import com.dsapps2018.dota2guessthesound.presentation.ui.composables.spin_wheel_compose.state.rememberSpinWheelState
import kotlinx.coroutines.launch

@Composable
fun SpinWheelScreen(modifier: Modifier = Modifier) {
    var index by remember {
        mutableIntStateOf(0)
    }
    Scaffold(
        contentWindowInsets = ScaffoldDefaults.contentWindowInsets.only(WindowInsetsSides.Bottom),
        content = { padding ->
            Box(
                modifier
                    .fillMaxSize()
                    .padding(
                        top = padding.calculateTopPadding(),
                        bottom = padding.calculateBottomPadding()
                    )
                    .paint(
                        painterResource(id = R.drawable.home_background),
                        contentScale = ContentScale.Crop
                    ),
                contentAlignment = Alignment.Center

            ) {

                val textList by remember {
                    mutableStateOf(
                        listOf(
                            "Pie 1",
                            "Pie 2",
                            "Pie 3",
                            "Pie 4",
                            "Pie 5",
                            "Pie 6",
                            "Pie 7",
                            "Pie 8"
                        )
                    )
                }

                val state = rememberSpinWheelState(
                    resultDegree = 52f
                )
                val scope = rememberCoroutineScope()

                SpinWheel(
                    state = state,
                    dimensions = SpinWheelDefaults.spinWheelDimensions(
                        spinWheelSize = 350.toDp(), frameWidth = 15.toDp(), selectorWidth = 10.toDp()
                    ),
                    onClick = {
                        scope.launch {
                            state.animate { pieIndex ->
                                index = pieIndex
                            }
                        }
                    },
                ) { pieIndex ->
//                    Text(text = textList[pieIndex], modifier = Modifier, fontSize = 14.sp, lineHeight = 12.sp, textAlign = TextAlign.Center)
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        if(pieIndex in 0..3){
                            Text("100", fontSize = 12.sp, lineHeight = 10.sp)
                        }else{
                            Image(painterResource(R.drawable.coin_blank), contentDescription = null, modifier = Modifier.size(20.dp))
                        }

                    }
                }

                Text(
                    "Spin index : $index",
                    color = Color.White,
                    fontSize = 22.sp,
                    modifier = Modifier.align(
                        Alignment.BottomCenter
                    )
                )
            }
        }
    )
}