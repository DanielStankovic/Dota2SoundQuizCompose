package com.dsapps2018.dota2guessthesound.data.analytics

import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.logEvent
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Product analytics for Firebase / GA4. Callers use intent methods; event names
 * and parameter limits live here. Catalog: docs/analytics-events.md.
 */
@Singleton
class ProductAnalytics @Inject constructor(
    private val firebaseAnalytics: FirebaseAnalytics,
) {

    fun selectMode(mode: AnalyticsMode) {
        firebaseAnalytics.logEvent(FirebaseAnalytics.Event.SELECT_CONTENT) {
            param(FirebaseAnalytics.Param.CONTENT_TYPE, CONTENT_TYPE_MODE)
            param(PARAM_CONTENT_ID, mode.id)
        }
    }

    fun journeyGated() {
        firebaseAnalytics.logEvent(EVENT_JOURNEY_GATED) {}
    }

    fun login() {
        firebaseAnalytics.logEvent(FirebaseAnalytics.Event.LOGIN) {
            param(FirebaseAnalytics.Param.METHOD, METHOD_GOOGLE)
        }
    }

    fun signUp() {
        firebaseAnalytics.logEvent(FirebaseAnalytics.Event.SIGN_UP) {
            param(FirebaseAnalytics.Param.METHOD, METHOD_GOOGLE)
        }
    }

    fun modeStart(mode: AnalyticsMode) {
        firebaseAnalytics.logEvent(EVENT_MODE_START) {
            param(PARAM_MODE, mode.id)
        }
    }

    fun levelStart(
        level: Int,
        firstClear: Boolean,
        replay: Boolean,
        affixNames: List<String>,
    ) {
        val affixes = joinAffixNames(affixNames)
        firebaseAnalytics.logEvent(FirebaseAnalytics.Event.LEVEL_START) {
            param(FirebaseAnalytics.Param.LEVEL_NAME, level.toString())
            param(PARAM_MODE, AnalyticsMode.Journey.id)
            param(PARAM_JOURNEY_LEVEL, level.toLong())
            param(PARAM_FIRST_CLEAR, boolParam(firstClear))
            param(PARAM_REPLAY, boolParam(replay))
            param(PARAM_AFFIXES, affixes)
            param(PARAM_AFFIX_COUNT, affixNames.size.toLong())
        }
    }

    fun levelEnd(
        level: Int,
        success: Boolean,
        firstClear: Boolean,
        replay: Boolean,
        affixNames: List<String>,
        failReason: String?,
        extraLifeUsed: Boolean,
        gateOffer: String,
        clearTimeMs: Long?,
    ) {
        val affixes = joinAffixNames(affixNames)
        firebaseAnalytics.logEvent(FirebaseAnalytics.Event.LEVEL_END) {
            param(FirebaseAnalytics.Param.LEVEL_NAME, level.toString())
            param(FirebaseAnalytics.Param.SUCCESS, if (success) 1L else 0L)
            param(PARAM_MODE, AnalyticsMode.Journey.id)
            param(PARAM_JOURNEY_LEVEL, level.toLong())
            param(PARAM_FIRST_CLEAR, boolParam(firstClear))
            param(PARAM_REPLAY, boolParam(replay))
            param(PARAM_AFFIXES, affixes)
            param(PARAM_AFFIX_COUNT, affixNames.size.toLong())
            param(PARAM_EXTRA_LIFE_USED, boolParam(extraLifeUsed))
            param(PARAM_GATE_OFFER, gateOffer)
            if (!success && failReason != null) {
                param(PARAM_FAIL_REASON, failReason)
            }
            if (success && clearTimeMs != null) {
                param(PARAM_CLEAR_TIME_MS, clearTimeMs)
            }
        }
    }

    fun postScore(mode: AnalyticsMode, score: Double) {
        firebaseAnalytics.logEvent(FirebaseAnalytics.Event.POST_SCORE) {
            param(FirebaseAnalytics.Param.SCORE, score)
            param(PARAM_MODE, mode.id)
        }
    }

    fun earnCoins(amount: Long, source: String) {
        if (amount <= 0L) return
        firebaseAnalytics.logEvent(FirebaseAnalytics.Event.EARN_VIRTUAL_CURRENCY) {
            param(FirebaseAnalytics.Param.VIRTUAL_CURRENCY_NAME, CURRENCY_COINS)
            param(FirebaseAnalytics.Param.VALUE, amount.toDouble())
            param(PARAM_SOURCE, source)
        }
    }

    fun spendCoins(amount: Long, itemName: String) {
        if (amount <= 0L) return
        firebaseAnalytics.logEvent(FirebaseAnalytics.Event.SPEND_VIRTUAL_CURRENCY) {
            param(FirebaseAnalytics.Param.VIRTUAL_CURRENCY_NAME, CURRENCY_COINS)
            param(FirebaseAnalytics.Param.VALUE, amount.toDouble())
            param(FirebaseAnalytics.Param.ITEM_NAME, itemName)
        }
    }

    fun extraLifeOffer(
        mode: AnalyticsMode,
        gateOffer: String,
        journeyLevel: Int? = null,
        affixNames: List<String> = emptyList(),
    ) {
        logExtraLife(EVENT_EXTRA_LIFE_OFFER, mode, gateOffer, journeyLevel, affixNames)
    }

    fun extraLifeRecover(
        mode: AnalyticsMode,
        gateOffer: String,
        journeyLevel: Int? = null,
        affixNames: List<String> = emptyList(),
    ) {
        logExtraLife(EVENT_EXTRA_LIFE_RECOVER, mode, gateOffer, journeyLevel, affixNames)
    }

    fun extraLifeDecline(
        mode: AnalyticsMode,
        gateOffer: String,
        journeyLevel: Int? = null,
        affixNames: List<String> = emptyList(),
    ) {
        logExtraLife(EVENT_EXTRA_LIFE_DECLINE, mode, gateOffer, journeyLevel, affixNames)
    }

    fun setSignedIn(signedIn: Boolean) {
        firebaseAnalytics.setUserProperty(USER_SIGNED_IN, boolParam(signedIn))
    }

    fun setJourneyLevel(level: Int) {
        firebaseAnalytics.setUserProperty(USER_JOURNEY_LEVEL, level.toString())
    }

    private fun logExtraLife(
        event: String,
        mode: AnalyticsMode,
        gateOffer: String,
        journeyLevel: Int?,
        affixNames: List<String>,
    ) {
        firebaseAnalytics.logEvent(event) {
            param(PARAM_MODE, mode.id)
            param(PARAM_GATE_OFFER, gateOffer)
            if (journeyLevel != null) {
                param(PARAM_JOURNEY_LEVEL, journeyLevel.toLong())
                param(PARAM_AFFIXES, joinAffixNames(affixNames))
            }
        }
    }

    companion object {
        const val CONTENT_TYPE_MODE = "mode"
        const val METHOD_GOOGLE = "Google"
        const val CURRENCY_COINS = "coins"

        const val EVENT_MODE_START = "mode_start"
        const val EVENT_JOURNEY_GATED = "journey_gated"
        const val EVENT_EXTRA_LIFE_OFFER = "extra_life_offer"
        const val EVENT_EXTRA_LIFE_RECOVER = "extra_life_recover"
        const val EVENT_EXTRA_LIFE_DECLINE = "extra_life_decline"

        const val PARAM_CONTENT_ID = "content_id"
        const val PARAM_MODE = "mode"
        const val PARAM_JOURNEY_LEVEL = "journey_level"
        const val PARAM_FIRST_CLEAR = "first_clear"
        const val PARAM_REPLAY = "replay"
        const val PARAM_AFFIXES = "affixes"
        const val PARAM_AFFIX_COUNT = "affix_count"
        const val PARAM_FAIL_REASON = "fail_reason"
        const val PARAM_EXTRA_LIFE_USED = "extra_life_used"
        const val PARAM_GATE_OFFER = "gate_offer"
        const val PARAM_CLEAR_TIME_MS = "clear_time_ms"
        const val PARAM_SOURCE = "source"

        const val FAIL_HEARTS = "hearts"
        const val FAIL_RACE_TIMEOUT = "race_timeout"
        const val FAIL_SUDDEN_DEATH = "sudden_death"
        const val FAIL_GATE_DECLINED = "gate_declined"
        const val FAIL_QUIT = "quit"

        const val GATE_HEART = "heart"
        const val GATE_TIME = "time"
        const val GATE_NONE = "none"

        const val SOURCE_JOURNEY_FIRST_CLEAR = "journey_first_clear"
        const val SOURCE_QUIZ = "quiz"
        const val SOURCE_FF_30 = "ff_30"
        const val SOURCE_FF_60 = "ff_60"
        const val SOURCE_FF_90 = "ff_90"
        const val SOURCE_INVOKER_AD_GRANT = "invoker_ad_grant"

        const val ITEM_INVOKER_ENTRY = "invoker_entry"

        const val USER_SIGNED_IN = "signed_in"
        const val USER_JOURNEY_LEVEL = "journey_level"

        private const val MAX_PARAM_VALUE = 100

        private fun joinAffixNames(names: List<String>): String {
            if (names.isEmpty()) return ""
            val joined = names.joinToString(",")
            if (joined.length <= MAX_PARAM_VALUE) return joined
            val cut = joined.take(MAX_PARAM_VALUE)
            val lastComma = cut.lastIndexOf(',')
            return if (lastComma > 0) cut.substring(0, lastComma) else cut
        }

        private fun boolParam(value: Boolean): String = if (value) "true" else "false"

        fun fastFingerCoinSource(timeSeconds: Int): String = when (timeSeconds) {
            60 -> SOURCE_FF_60
            90 -> SOURCE_FF_90
            else -> SOURCE_FF_30
        }
    }
}
