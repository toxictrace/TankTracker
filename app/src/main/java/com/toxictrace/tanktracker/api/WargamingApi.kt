package com.toxictrace.tanktracker.api

import com.google.gson.annotations.SerializedName
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

// ── Account Search ───────────────────────────────────────────────────────────

data class SearchResponse(
    val status: String,
    val data: List<PlayerSearchItem>?
)

data class PlayerSearchItem(
    @SerializedName("account_id") val accountId: Long,
    val nickname: String
)

// ── Account Info ─────────────────────────────────────────────────────────────

data class AccountInfoResponse(
    val status: String,
    val data: Map<String, AccountData?>?
)

data class AccountData(
    val nickname: String,
    @SerializedName("account_id") val accountId: Long,
    val statistics: Statistics?,
    @SerializedName("global_rating") val globalRating: Int,
    @SerializedName("created_at") val createdAt: Long,
    @SerializedName("last_battle_time") val lastBattleTime: Long
)

data class Statistics(
    val all: BattleStats?
)

data class BattleStats(
    val battles: Int = 0,
    val wins: Int = 0,
    val losses: Int = 0,
    val frags: Int = 0,
    @SerializedName("survived_battles") val survivedBattles: Int = 0,
    val hits: Int = 0,
    val shots: Int = 0,
    @SerializedName("damage_dealt") val damageDealt: Long = 0L,
    @SerializedName("damage_assisted_radio") val damageAssistedRadio: Long = 0L,
    @SerializedName("damage_assisted_track") val damageAssistedTrack: Long = 0L,
    @SerializedName("damage_blocked_by_armour") val damageBlockedByArmour: Long = 0L,
    @SerializedName("spotted") val spotted: Int = 0,
    val xp: Long = 0L,
    @SerializedName("battle_avg_xp") val battleAvgXp: Int = 0,
    @SerializedName("max_damage") val maxDamage: Int = 0,
    @SerializedName("max_frags") val maxFrags: Int = 0,
    val draws: Int = 0
)

// ── Clan ─────────────────────────────────────────────────────────────────────

data class PlayerClanResponse(
    val status: String,
    val data: Map<String, PlayerClanData?>?
)

data class PlayerClanData(
    @SerializedName("clan_id") val clanId: Long?,
    val clan: ClanShortData?
)

data class ClanShortData(
    val tag: String,
    val name: String
)

// ── Tank Stats ────────────────────────────────────────────────────────────────

data class TanksStatsResponse(
    val status: String,
    val data: Map<String, List<TankStatItem>?>?
)

data class TankStatItem(
    @SerializedName("tank_id") val tankId: Long,
    val all: TankBattleStats?,
    @SerializedName("mark_of_mastery") val markOfMastery: Int = 0
)

data class TankBattleStats(
    val battles: Int = 0,
    val wins: Int = 0,
    @SerializedName("damage_dealt") val damageDealt: Long = 0L,
    @SerializedName("damage_assisted_radio") val damageAssistedRadio: Long = 0L,
    @SerializedName("damage_assisted_track") val damageAssistedTrack: Long = 0L,
    @SerializedName("damage_blocked_by_armour") val damageBlockedByArmour: Long = 0L,
    val frags: Int = 0,
    @SerializedName("survived_battles") val survivedBattles: Int = 0,
    val hits: Int = 0,
    val shots: Int = 0,
    val spotted: Int = 0,
    val xp: Long = 0L
)

// ── Vehicle Encyclopedia ──────────────────────────────────────────────────────

data class VehicleInfoResponse(
    val status: String,
    val data: Map<String, VehicleData?>?
)

data class VehicleData(
    @SerializedName("tank_id") val tankId: Long,
    val name: String,
    val nation: String,
    val tier: Int,
    val type: String,
    @SerializedName("is_premium") val isPremium: Boolean = false,
    val images: VehicleImages?
)

data class VehicleImages(
    @SerializedName("big_icon") val bigIcon: String?,
    @SerializedName("small_icon") val smallIcon: String?
)

// ── API Interface ─────────────────────────────────────────────────────────────

interface WargamingApi {

    @GET("wot/account/list/")
    suspend fun searchPlayers(
        @Query("application_id") appId: String,
        @Query("search") search: String,
        @Query("type") type: String = "startswith",
        @Query("limit") limit: Int = 10
    ): SearchResponse

    @GET("wot/account/info/")
    suspend fun getAccountInfo(
        @Query("application_id") appId: String,
        @Query("account_id") accountId: Long,
        @Query("fields") fields: String = "nickname,account_id,global_rating,created_at,last_battle_time,statistics.all"
    ): AccountInfoResponse

    @GET("wot/clans/accountinfo/")
    suspend fun getPlayerClan(
        @Query("application_id") appId: String,
        @Query("account_id") accountId: Long,
        @Query("fields") fields: String = "clan_id,clan.tag,clan.name"
    ): PlayerClanResponse

    @GET("wot/tanks/stats/")
    suspend fun getTankStats(
        @Query("application_id") appId: String,
        @Query("account_id") accountId: Long,
        @Query("fields") fields: String = "tank_id,all,mark_of_mastery"
    ): TanksStatsResponse

    @GET("wot/encyclopedia/vehicles/")
    suspend fun getVehicleInfo(
        @Query("application_id") appId: String,
        @Query("tank_id") tankIds: String,
        @Query("fields") fields: String = "tank_id,name,nation,tier,type,is_premium,images.big_icon,images.small_icon"
    ): VehicleInfoResponse
}

// ── Singleton ─────────────────────────────────────────────────────────────────

object WgApiClient {
    const val APP_ID = "f21d8f6446559bf7c2bb4f61003e9d28"
    private const val BASE_URL = "https://api.worldoftanks.eu/"

    val api: WargamingApi by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(WargamingApi::class.java)
    }
}
