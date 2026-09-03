package com.dsapps2018.dota2guessthesound.data.auth

import com.dsapps2018.dota2guessthesound.data.repository.LeaderboardRepository
import com.dsapps2018.dota2guessthesound.data.repository.PlayerProgressRepository
import com.dsapps2018.dota2guessthesound.data.util.Constants
import io.github.jan.supabase.auth.Auth
import io.github.jan.supabase.auth.providers.Google
import io.github.jan.supabase.auth.providers.builtin.IDToken
import io.github.jan.supabase.postgrest.Postgrest
import io.github.jan.supabase.postgrest.query.Columns
import io.github.jan.supabase.postgrest.query.Count
import java.security.MessageDigest
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Deep Auth module: sign-in / sign-out orchestration with Player Progress bootstrap
 * and leaderboard attach. See docs/adr/0014-auth-session.md.
 */
@Singleton
class AuthSession @Inject constructor(
    private val auth: Auth,
    private val postgrest: Postgrest,
    private val playerProgressRepository: PlayerProgressRepository,
    private val leaderboardRepository: LeaderboardRepository,
) {
    val sessionStatus = auth.sessionStatus

    fun noncePair(): Pair<String, String> {
        val rawNonce = UUID.randomUUID().toString()
        val bytes = rawNonce.toByteArray()
        val md = MessageDigest.getInstance("SHA-256")
        val digest = md.digest(bytes)
        return rawNonce to digest.fold("") { str, it -> str + "%02x".format(it) }
    }

    /**
     * Google ID token sign-in, then create-or-sync Player Progress and attach leaderboard.
     */
    suspend fun signInWithGoogle(googleIdToken: String, rawNonce: String) {
        auth.signInWith(IDToken) {
            idToken = googleIdToken
            provider = Google
            nonce = rawNonce
        }

        auth.currentUserOrNull()?.id?.let { id ->
            if (userDataExists(id)) {
                playerProgressRepository.sync()
            } else {
                playerProgressRepository.createServerUserData(id)
                playerProgressRepository.sync()
            }
            leaderboardRepository.updateUserIdAndSendData(id)
        }
    }

    /** Sync Player Progress, sign out of Auth, reset local progress. */
    suspend fun signOut() {
        playerProgressRepository.sync()
        auth.signOut()
        playerProgressRepository.resetLocalUserData()
    }

    suspend fun syncProgress() {
        playerProgressRepository.sync()
    }

    private suspend fun userDataExists(userId: String): Boolean {
        val count = postgrest.from(Constants.TABLE_GAME_DATA).select(
            columns = Columns.list("user_id")
        ) {
            filter {
                eq("user_id", userId)
            }
            count(Count.EXACT)
        }.countOrNull()
        return !(count == null || count <= 0)
    }
}
