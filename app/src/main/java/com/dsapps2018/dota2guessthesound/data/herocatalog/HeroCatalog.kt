package com.dsapps2018.dota2guessthesound.data.herocatalog

import android.content.Context
import androidx.annotation.DrawableRes
import com.dsapps2018.dota2guessthesound.R
import com.dsapps2018.dota2guessthesound.data.dao.CasterDao
import com.dsapps2018.dota2guessthesound.data.dao.SoundDao
import com.dsapps2018.dota2guessthesound.data.model.SoundModel
import com.dsapps2018.dota2guessthesound.data.util.SoundPlayback
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

data class CatalogHero(
    val id: Int,
    val name: String,
    @DrawableRes val portraitResId: Int,
)

/**
 * Hero Catalog: local Heroes and their Sounds for inspection / listen.
 * Not a playable mode.
 */
class HeroCatalog @Inject constructor(
    @ApplicationContext private val context: Context,
    private val casterDao: CasterDao,
    private val soundDao: SoundDao,
    private val soundPlayback: SoundPlayback,
) {

    suspend fun heroes(): List<CatalogHero> {
        val resources = context.resources
        val packageName = context.packageName
        return casterDao.getActiveCastersOfType(HERO_TYPE)
            .map { caster ->
                val drawableName =
                    "hero_${caster.name.lowercase().replace("'s", "s").replace("-", "")}"
                val portraitResId = resources.getIdentifier(
                    drawableName,
                    "drawable",
                    packageName,
                ).takeIf { it != 0 } ?: R.drawable.hero_question_mark
                CatalogHero(
                    id = caster.id,
                    name = caster.name,
                    portraitResId = portraitResId,
                )
            }
    }

    suspend fun sounds(heroId: Int): List<SoundModel> =
        soundDao.getSoundsForCaster(heroId)

    fun play(sound: SoundModel) = soundPlayback.play(sound)

    fun stop() = soundPlayback.stop()

    companion object {
        const val HERO_TYPE = "HERO"
    }
}
