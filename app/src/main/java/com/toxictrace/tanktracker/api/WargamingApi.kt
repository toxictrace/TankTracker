package com.toxictrace.tanktracker.api

import com.google.gson.annotations.SerializedName
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

// ── Response models ──────────────────────────────────────────────────────────

data class SearchResponse(
    val status: String,
    val data: List<PlayerSearchItem>?
)

data class PlayerSearchItem(
    @SerializedName("account_id") val accountId: Long,
    val nickname: String
)

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
    @SerializedName("last_battle_time") val lastBattleTime: Long,
    @SerializedName("clan_id") val clanId: Long?
)

data class Statistics(
    val all: BattleStats?
)

data class BattleStats(
    val battles: Int,
    val wins: Int,
    val losses: Int,
    val frags: Int,
    val survived_battles: Int,
    val hits: Int,
    val shots: Int,
    @SerializedName("damage_dealt") val damageDealt: Long,
    val xp: Long
)

data class ClanInfoResponse(
    val status: String,
    val data: Map<String, ClanData?>?
)

data class ClanData(
    val tag: String,
    val name: String
)

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

data class TanksStatsResponse(
    val status: String,
    val data: Map<String, List<TankStatItem>?>?
)

data class TankStatItem(
    @SerializedName("tank_id") val tankId: Long,
    val all: TankBattleStats?,
    @SerializedName("mark_of_mastery") val markOfMastery: Int,
    @SerializedName("marks_on_gun") val marksOnGun: Int?
)

data class TankBattleStats(
    val battles: Int,
    val wins: Int,
    @SerializedName("damage_dealt") val damageDealt: Long,
    val frags: Int,
    val survived: Int,
    val hits: Int,
    val shots: Int,
    val xp: Long
)

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
    val images: VehicleImages?
)

data class VehicleImages(
    @SerializedName("big_icon") val bigIcon: String?
)

// ── Retrofit interface ───────────────────────────────────────────────────────

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
        @Query("fields") fields: String = "nickname,account_id,global_rating,statistics.all"
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
        @Query("fields") fields: String = "tank_id,all,mark_of_mastery,marks_on_gun"
    ): TanksStatsResponse

    @GET("wot/encyclopedia/vehicles/")
    suspend fun getVehicleInfo(
        @Query("application_id") appId: String,
        @Query("tank_id") tankIds: String,
        @Query("fields") fields: String = "tank_id,name,nation,tier,type,images.big_icon"
    ): VehicleInfoResponse
}

// ── Singleton ────────────────────────────────────────────────────────────────

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
