package com.dsapps2018.dota2guessthesound.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.dsapps2018.dota2guessthesound.presentation.ui.screens.fastfinger.FastFingerScreen
import com.dsapps2018.dota2guessthesound.presentation.ui.screens.fastfinger.PickTimeScreen
import com.dsapps2018.dota2guessthesound.presentation.ui.screens.home.HomeScreen
import com.dsapps2018.dota2guessthesound.presentation.ui.screens.invoker.InvokerExplanationScreen
import com.dsapps2018.dota2guessthesound.presentation.ui.screens.invoker.InvokerScreen
import com.dsapps2018.dota2guessthesound.presentation.ui.screens.changelog.ChangelogScreen
import com.dsapps2018.dota2guessthesound.presentation.ui.screens.options.AttributionScreen
import com.dsapps2018.dota2guessthesound.presentation.ui.screens.options.OptionsScreen
import com.dsapps2018.dota2guessthesound.presentation.ui.screens.options.PrivacyScreen
import com.dsapps2018.dota2guessthesound.presentation.ui.screens.playagain.PlayAgainFastFingerScreen
import com.dsapps2018.dota2guessthesound.presentation.ui.screens.playagain.PlayAgainInvokerScreen
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
                },
                onFastFingerClicked = {
                    navController.navigate(PickTimeDestination)
                },
                onInvokerClicked = {
                    navController.navigate(InvokerExplanationDestination)
                },
                onOptionsClicked = {
                    navController.navigate(OptionsDestination)
                }
            )
        }

        composable<QuizModeDestination> {
            QuizScreen(
                onPlayAgain = { score, answeredAll ->
                    navController.navigate(route = PlayAgainDestination(score, answeredAll)) {
                        popUpTo<HomeDestination> {
                            inclusive = false
                            saveState = false
                        }
                        restoreState = false
                    }
                }
            )
        }

        composable<FastFingerModeDestination> { backStackEntry ->
            val fastFingerDestination: FastFingerModeDestination = backStackEntry.toRoute()

            FastFingerScreen(
                initialTime = fastFingerDestination.time,
                onPlayAgain = { scoreGuessed, scoreTotal, time, answeredAll ->
                    navController.navigate(
                        route = PlayAgainFastFingerDestination(
                            scoreGuessed,
                            scoreTotal,
                            time,
                            answeredAll
                        )
                    ) {
                        popUpTo<HomeDestination> {
                            inclusive = false
                            saveState = false
                        }
                        restoreState = false
                    }
                })
        }

        composable<PickTimeDestination> {
            PickTimeScreen(
                onPlayClicked = { time ->
                    navController.navigate(route = FastFingerModeDestination(time)) {
                        popUpTo<HomeDestination> {
                            inclusive = false
                            saveState = false
                        }
                        restoreState = false
                    }
                }
            )
        }

        composable<InvokerExplanationDestination> {
            InvokerExplanationScreen {
                navController.navigate(route = InvokerDestination) {
                    popUpTo<HomeDestination> {
                        inclusive = false
                        saveState = false
                    }
                    restoreState = false
                }
            }
        }

        composable<InvokerDestination> {
            InvokerScreen { score ->
                navController.navigate(route = PlayAgainInvokerDestination(score)) {
                    popUpTo<HomeDestination> {
                        inclusive = false
                        saveState = false
                    }
                    restoreState = false
                }
            }
        }

        composable<ForceUpdateDestination> {
            ForceUpdateScreen()
        }

        composable<PlayAgainDestination> { backStackEntry ->
            val playAgainDestination: PlayAgainDestination = backStackEntry.toRoute()

            PlayAgainScreen(
                score = playAgainDestination.score,
                answeredAll = playAgainDestination.answeredAll,
                onPlayAgain = {
                    navController.navigate(route = QuizModeDestination) {
                        popUpTo<HomeDestination> {
                            inclusive = false
                            saveState = false
                        }
                        restoreState = false
                    }
                })
        }

        composable<PlayAgainFastFingerDestination> { backStackEntry ->
            val playAgainFastFingerDestination: PlayAgainFastFingerDestination =
                backStackEntry.toRoute()

            PlayAgainFastFingerScreen(
                scoreGuessed = playAgainFastFingerDestination.scoreGuessed,
                scoreTotal = playAgainFastFingerDestination.scoreTotal,
                answeredAll = playAgainFastFingerDestination.answeredAll,
                time = playAgainFastFingerDestination.time,
                onPlayAgain = {
                    navController.navigate(route = PickTimeDestination) {
                        popUpTo<HomeDestination> {
                            inclusive = false
                            saveState = false
                        }
                        restoreState = false
                    }
                })
        }

        composable<PlayAgainInvokerDestination> { backStackEntry ->
            val playAgainInvokerDestination: PlayAgainInvokerDestination = backStackEntry.toRoute()

            PlayAgainInvokerScreen(
                score = playAgainInvokerDestination.score,
                onPlayAgain = {
                    navController.navigate(route = HomeDestination) {
                        popUpTo<HomeDestination> {
                            inclusive = false
                            saveState = false
                        }
                        restoreState = false
                    }
                })
        }

        composable<OptionsDestination> {
            OptionsScreen(
                onPrivacyPolicyClick = {
                    navController.navigate(route = PrivacyDestination)
                },
                onChangelogClick = {
                    navController.navigate(route = ChangelogDestination)
                },
                onAttributionsClicked = {
                    navController.navigate(route = AttributionDestination)
                }
            )
        }

        composable<PrivacyDestination> {
            PrivacyScreen()
        }

        composable<ChangelogDestination> {
            ChangelogScreen()
        }

        composable<AttributionDestination> {
            AttributionScreen()
        }
    }
}
