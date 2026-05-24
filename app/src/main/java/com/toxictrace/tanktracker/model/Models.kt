package com.toxictrace.tanktracker.model

enum class TankClass { HEAVY, MEDIUM, LIGHT, TANK_DESTROYER, SPG }

enum class MasteryBadge { NONE, THIRD, SECOND, FIRST, ACE }

enum class BattleResult { VICTORY, DEFEAT, DRAW }

data class TankInfo(
    val id: Long,
    val name: String,
    val nation: String,
    val tier: Int,
    val tankClass: TankClass,
    val battles: Int,
    val winRate: Double,
    val avgDamage: Int,
    val avgXP: Int,
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
    val wn8Value: Int,
    val winRatePct: Double,
    val battlesPlayed: Int,
    val avgDamage: Int,
    val kdRatio: Double,
    val survivalRate: Double,
    val accuracyPct: Double,
    val avgXP: Int,
    val tanks: List<TankInfo>
)

object WN8Ratings {
    fun getRatingLabel(wn8: Int): String = when {
        wn8 >= 2900 -> "Super Unicum"
        wn8 >= 2450 -> "Unicum"
        wn8 >= 2000 -> "Great"
        wn8 >= 1600 -> "Very Good"
        wn8 >= 1200 -> "Above Average"
        wn8 >= 900  -> "Average"
        else        -> "Below Average"
    }
}

// Map WG API type string → TankClass
fun String.toTankClass(): TankClass = when (this) {
    "heavyTank"     -> TankClass.HEAVY
    "mediumTank"    -> TankClass.MEDIUM
    "lightTank"     -> TankClass.LIGHT
    "AT-SPG"        -> TankClass.TANK_DESTROYER
    "SPG"           -> TankClass.SPG
    else            -> TankClass.MEDIUM
}

// Map mastery int (0–4) → MasteryBadge
fun Int.toMasteryBadge(): MasteryBadge = when (this) {
    4    -> MasteryBadge.ACE
    3    -> MasteryBadge.FIRST
    2    -> MasteryBadge.SECOND
    1    -> MasteryBadge.THIRD
    else -> MasteryBadge.NONE
}
