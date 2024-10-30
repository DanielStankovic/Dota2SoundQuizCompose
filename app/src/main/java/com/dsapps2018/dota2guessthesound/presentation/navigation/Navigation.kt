package com.dsapps2018.dota2guessthesound.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.dsapps2018.dota2guessthesound.presentation.ui.screens.home.HomeScreen
import com.dsapps2018.dota2guessthesound.presentation.ui.screens.playagain.PlayAgainScreen
import com.dsapps2018.dota2guessthesound.presentation.ui.screens.quiz.QuizScreen
import com.dsapps2018.dota2guessthesound.presentation.ui.screens.syncscreen.SyncScreen
import com.dsapps2018.dota2guessthesound.presentation.ui.screens.syncscreen.forceupdate.ForceUpdateScreen


@Composable
fun HomeNavGraph(navController: NavHostController = rememberNavController()) {
    NavHost(navController = navController, startDestination = SyncDestination) {

        composable<SyncDestination> {
            SyncScreen(
                onUpdateRequired = {
                    navController.navigate(ForceUpdateDestination)
                },
                onSyncFinished = {
                    navController.navigate(HomeDestination) {
                        popUpTo<SyncDestination> {
                            inclusive = true
                            saveState = false
                        }
                        restoreState = false
                    }
                }
            )
        }

        composable<LoginDestination> {

        }

        composable<SignUpDestination> {

        }

        composable<HomeDestination> {
            HomeScreen(
                onQuizClicked = {
                    navController.navigate(QuizModeDestination)
                }
            )
        }

        composable<QuizModeDestination> {
            QuizScreen(
                onPlayAgain = { score, answeredAll ->
                    navController.navigate(route = PlayAgainDestination(score, answeredAll)){
                        popUpTo<HomeDestination> {
                            inclusive = false
                            saveState = false
                        }
                        restoreState = false
                    }
                }
            )
        }

        composable<FastFingerModeDestination> {

        }

        composable<InvokerDestination> {

        }

        composable<ForceUpdateDestination> {
            ForceUpdateScreen()
        }

        composable<PlayAgainDestination> { backStackEntry ->
            val playAgainDestination: PlayAgainDestination = backStackEntry.toRoute()

            PlayAgainScreen(score = playAgainDestination.score, answeredAll = playAgainDestination.answeredAll, onPlayAgain = {
                navController.navigate(route = QuizModeDestination){
                    popUpTo<HomeDestination> {
                        inclusive = false
                        saveState = false
                    }
                    restoreState = false
                }
            })
        }
    }
}
