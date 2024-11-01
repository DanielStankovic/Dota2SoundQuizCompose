package com.dsapps2018.dota2guessthesound.data.repository

import android.content.Context
import android.os.Environment
import androidx.core.content.ContextCompat
import androidx.core.os.EnvironmentCompat
import com.dsapps2018.dota2guessthesound.BuildConfig
import com.dsapps2018.dota2guessthesound.data.api.response.CasterDto
import com.dsapps2018.dota2guessthesound.data.api.response.CasterTypeDto
import com.dsapps2018.dota2guessthesound.data.api.response.ConfigDto
import com.dsapps2018.dota2guessthesound.data.api.response.SoundDto
import com.dsapps2018.dota2guessthesound.data.dao.CasterDao
import com.dsapps2018.dota2guessthesound.data.dao.CasterTypeDao
import com.dsapps2018.dota2guessthesound.data.dao.SoundDao
import com.dsapps2018.dota2guessthesound.data.db.entity.CasterEntity
import com.dsapps2018.dota2guessthesound.data.db.entity.CasterTypeEntity
import com.dsapps2018.dota2guessthesound.data.db.entity.SoundEntity
import com.dsapps2018.dota2guessthesound.data.util.Constants
import com.dsapps2018.dota2guessthesound.data.util.getInitialModifiedDate
import dagger.hilt.android.qualifiers.ApplicationContext
import io.github.jan.supabase.postgrest.Postgrest
import io.github.jan.supabase.postgrest.query.Columns
import io.github.jan.supabase.postgrest.query.Order
import io.github.jan.supabase.storage.Storage
import io.github.jan.supabase.storage.downloadPublicTo
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import java.io.File
import javax.inject.Inject

class SyncRepository @Inject constructor(
    @ApplicationContext private val applicationContext: Context,
    private val postgrest: Postgrest,
    private val storage: Storage,
    private val casterTypeDao: CasterTypeDao,
    private val casterDao: CasterDao,
    private val soundDao: SoundDao
) {

    suspend fun syncRemoteConfig(): ConfigDto {
        return try {

            postgrest.from(Constants.TABLE_CONFIG).select(
                Columns.raw(
                    """
            forced_version: data->forced_version,
            recommended_version: data->recommended_version
            """
                        .trimIndent()
                )
            ).decodeSingle<ConfigDto>()

        } catch (e: Exception) {
            throw e
        }
    }

    suspend fun syncCasterType() {
        try {
            val modifiedDate = casterTypeDao.getModifiedDate() ?: getInitialModifiedDate()
            val casterTypeList = postgrest
                .from(Constants.TABLE_CASTER_TYPE)
                .select(columns = Columns.list("id", "type", "code", "modified_at", "active")) {
                    filter {
                        gt("modified_at", modifiedDate)

                    }
                    order("modified_at", Order.ASCENDING)
                }.decodeList<CasterTypeDto>().map { x ->
                    CasterTypeEntity(x.id, x.type, x.code, x.modifiedAt, x.isActive)
                }

            casterTypeDao.deleteAll(casterTypeList)
            casterTypeDao.insertAll(casterTypeList.filter { x -> x.isActive })
        } catch (e: Exception) {
            throw e
        }
    }

    suspend fun syncCaster() {
        try {
            val modifiedDate = casterDao.getModifiedDate() ?: getInitialModifiedDate()
            val casterList = postgrest
                .from(Constants.TABLE_CASTERS)
                .select(
                    columns = Columns.list(
                        "id",
                        "name",
                        "caster_type_id",
                        "modified_at",
                        "active"
                    )
                ) {
                    filter {
                        gt("modified_at", modifiedDate)
                    }
                    order("modified_at", Order.ASCENDING)
                }.decodeList<CasterDto>().map { x ->
                    CasterEntity(x.id, x.name, x.casterTypeId, x.modifiedAt, x.isActive)
                }

            casterDao.deleteAll(casterList)
            casterDao.insertAll(casterList.filter { x -> x.isActive })
        } catch (e: Exception) {
            throw e
        }
    }

    fun syncSound(): Flow<Pair<Float, String>> {
        return flow {
            try {
                val modifiedDate = if(!BuildConfig.DEBUG) soundDao.getModifiedDate() ?: getInitialModifiedDate()
                else{
                    //Ovde za slucaj testa mozemo da fetchujemo samo neke i ovo samo u slucaju debuga. Koristim
                    //ovaj uslov za debug cisto kao osiguranje da ne ode ovaj kod na produkciju
                    soundDao.getModifiedDate() ?: getInitialModifiedDate()
//                    "2024-10-29 17:38:45.593298" //Ovo je 5 modified_at iz baze na serveru sortirano DESC, tako da ce da vrati samo 5 zvuka uvek na svez sync
                }
                val soundList = postgrest
                    .from(Constants.TABLE_SOUNDS)
                    .select(
                        columns = Columns.list(
                            "id",
                            "spell_name",
                            "sound_file_name",
                            "sound_file_link",
                            "caster_id",
                            "modified_at",
                            "active"
                        )
                    ) {
                        filter {
                            gt("modified_at", modifiedDate)
                        }
                        order("modified_at", Order.ASCENDING)
                    }.decodeList<SoundDto>().map { x ->
                        SoundEntity(
                            x.id,
                            x.spellName,
                            x.soundFileName,
                            x.soundFileLink,
                            x.modifiedAt,
                            x.casterId,
                            x.isActive
                        )
                    }

            soundDao.deleteAll(soundList)
//            val a = storage.from("sounds").list("spell_sounds"){
//                limit = 1000
//            }
//            val b = a
                val directory = getAppExternalStorage()
                directory?.let { dir ->
                    val filteredList = soundList.filter { x -> x.isActive }
                    val progressPortion = 1f / filteredList.size
                    filteredList.forEachIndexed {index, sound ->
                        val file = File(dir, sound.soundFileName)
                        storage.from("sounds").downloadPublicTo(
                            "spell_sounds/${sound.soundFileName}",
                            file
                        )
                        sound.soundFileLink = file.path
                        soundDao.insert(sound)
                        emit(progressPortion * (index + 1) to sound.spellName)
                    }
                }

//            soundDao.insertAll(soundList.filter { x -> x.isActive })
            } catch (e: Exception) {
                throw e
            }
        }
    }

    private fun getAppExternalStorage(): File? {

        //This is the list of all the external storages on the device.
        //We will first try to download data to the first one, the primary one, and if that is not successful
        //because of memory problems, we will try the next one.
        val externalStorageVolumes: Array<out File> =
            ContextCompat.getExternalFilesDirs(applicationContext, null)
        var isMediaMounted = false
        var directoryCreated = false
        var directory: File? = null
        externalStorageVolumes.forEachIndexed { index, storage ->
            //Index 0 is the primary storage
            if (EnvironmentCompat.getStorageState(storage) != Environment.MEDIA_MOUNTED) {
                return@forEachIndexed
            }
            //Ovo znaci da je mounted i da mozemo dalje da downloadujemo zvukove
            isMediaMounted = true
            val ringtonesDirectory = File(storage, Environment.DIRECTORY_RINGTONES)
            if (!ringtonesDirectory.exists()) {
                if (!ringtonesDirectory.mkdirs()) {
                    return@forEachIndexed
                }
            }
            directoryCreated = true
            directory = ringtonesDirectory
        }

        if (!isMediaMounted) throw Exception("Error - Can not download sounds to device storage")
        if (!directoryCreated) throw Exception("Error - Can not create download directory")
        return directory
    }
}