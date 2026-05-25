package com.toxictrace.tanktracker.model

enum class TankClass { HEAVY, MEDIUM, LIGHT, TANK_DESTROYER, SPG }
enum class MasteryBadge { NONE, THIRD, SECOND, FIRST, ACE }
enum class StatPeriod { ALL, RECENT }

data class TankInfo(
    val id: Long,
    val name: String,
    val nation: String,
    val tier: Int,
    val tankClass: TankClass,
    val isPremium: Boolean = false,
    val battles: Int,
    val winRate: Double,
    val avgDamage: Int,
    val avgAssist: Int,
    val avgBlocked: Int,
    val avgSpotted: Double,
    val avgFrags: Double,
    val avgXP: Int,
    val survivalRate: Double,
    val accuracy: Double,
    val marksOfExcellence: Int,
    val masteryBadge: MasteryBadge,
    val imageUrl: String? = null
)

data class PlayerProfile(
    val accountId: Long,
    val nickname: String,
    val clanTag: String,
    val clanName: String,
    val globalRating: Int,
    val createdAt: Long,
    val lastBattleTime: Long,
    // Core stats
    val wn8Value: Int,
    val winRatePct: Double,
    val battlesPlayed: Int,
    val avgDamage: Int,
    val avgAssist: Int,
    val avgBlocked: Int,
    val avgSpotted: Double,
    val avgFrags: Double,
    val kdRatio: Double,
    val survivalRate: Double,
    val accuracyPct: Double,
    val avgXP: Int,
    val avgTier: Double,
    val maxDamage: Int,
    val maxFrags: Int,
    // Tanks
    val tanks: List<TankInfo>
)

object WN8Colors {
    fun getLabel(wn8: Int): String = when {
        wn8 >= 2900 -> "Super Unicum"
        wn8 >= 2450 -> "Unicum"
        wn8 >= 2000 -> "Great"
        wn8 >= 1600 -> "Very Good"
        wn8 >= 1200 -> "Above Average"
        wn8 >= 900  -> "Average"
        else        -> "Below Average"
    }
}

fun String.toTankClass(): TankClass = when (this) {
    "heavyTank"  -> TankClass.HEAVY
    "mediumTank" -> TankClass.MEDIUM
    "lightTank"  -> TankClass.LIGHT
    "AT-SPG"     -> TankClass.TANK_DESTROYER
    "SPG"        -> TankClass.SPG
    else         -> TankClass.MEDIUM
}

fun Int.toMasteryBadge(): MasteryBadge = when (this) {
    4    -> MasteryBadge.ACE
    3    -> MasteryBadge.FIRST
    2    -> MasteryBadge.SECOND
    1    -> MasteryBadge.THIRD
    else -> MasteryBadge.NONE
}
