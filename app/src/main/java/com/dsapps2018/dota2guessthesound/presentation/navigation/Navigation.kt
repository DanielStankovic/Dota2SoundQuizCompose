package com.dsapps2018.dota2guessthesound.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.dsapps2018.dota2guessthesound.presentation.ui.screens.changelog.ChangelogScreen
import com.dsapps2018.dota2guessthesound.presentation.ui.screens.faq.FaqScreen
import com.dsapps2018.dota2guessthesound.presentation.ui.screens.fastfinger.FastFingerScreen
import com.dsapps2018.dota2guessthesound.presentation.ui.screens.fastfinger.PickTimeScreen
import com.dsapps2018.dota2guessthesound.presentation.ui.screens.home.HomeScreen
import com.dsapps2018.dota2guessthesound.presentation.ui.screens.invoker.InvokerExplanationScreen
import com.dsapps2018.dota2guessthesound.presentation.ui.screens.invoker.InvokerScreen
import com.dsapps2018.dota2guessthesound.presentation.ui.screens.leaderboard.LeaderboardScreen
import com.dsapps2018.dota2guessthesound.presentation.ui.screens.leaderboard.leaderboardhistory.LeaderboardHistoryScreen
import com.dsapps2018.dota2guessthesound.presentation.ui.screens.options.AttributionScreen
import com.dsapps2018.dota2guessthesound.presentation.ui.screens.options.OptionsScreen
import com.dsapps2018.dota2guessthesound.presentation.ui.screens.options.PrivacyScreen
import com.dsapps2018.dota2guessthesound.presentation.ui.screens.playagain.PlayAgainFastFingerScreen
import com.dsapps2018.dota2guessthesound.presentation.ui.screens.playagain.PlayAgainInvokerScreen
import com.dsapps2018.dota2guessthesound.presentation.ui.screens.playagain.PlayAgainScreen
import com.dsapps2018.dota2guessthesound.presentation.ui.screens.profile.ProfileScreen
import com.dsapps2018.dota2guessthesound.presentation.ui.screens.quiz.QuizScreen
import com.dsapps2018.dota2guessthesound.presentation.ui.screens.rewards.RewardsScreen
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
                },
                onProfileClicked = {
                    navController.navigate(ProfileDestination)
                },
                onQuestionClicked = {
                    navController.navigate(FaqDestination)
                },
                onLeaderboardClicked = {
                    navController.navigate(LeaderboardDestination(null))
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
                    navController.popBackStack(route = HomeDestination, false)
                })
        }

        composable<OptionsDestination> {
            OptionsScreen(
                onProfileClick = {
                    navController.navigate(route = ProfileDestination)
                },
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

        composable<ProfileDestination> {
            ProfileScreen()
        }

        composable<LeaderboardDestination> { backStackEntry ->

            val leaderboardDestination: LeaderboardDestination = backStackEntry.toRoute()

            LeaderboardScreen(
                isHistory = leaderboardDestination.leaderboardId != null,
                onHistoryClicked = {
                    navController.navigate(route = LeaderboardHistoryDestination)
                },
                onCheckRewardClicked = { leaderboardId, month ->
                    navController.navigate(route = RewardsDestination(leaderboardId, month))
                },
                onQuestionClicked = {
                    navController.navigate(route = FaqDestination)
                })
        }

        composable<RewardsDestination> { backStackEntry ->
            val rewardsDestination: RewardsDestination = backStackEntry.toRoute()
            RewardsScreen(
                month = rewardsDestination.month
            )
        }

        composable<FaqDestination> {
            FaqScreen()
        }

        composable<LeaderboardHistoryDestination> {
            LeaderboardHistoryScreen(
                onLeaderboardClicked = { leaderboardId ->
                    navController.navigate(LeaderboardDestination(leaderboardId))
                }
            )
        }
    }
}
