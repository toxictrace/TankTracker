package com.toxictrace.tanktracker.model

data class SessionBattle(
    val tankName: String,
    val tankTier: Int,
    val isWin: Boolean,
    val damage: Int,
    val frags: Int,
    val survived: Boolean,
    val xp: Int,
    val timestamp: Long = System.currentTimeMillis()
)

data class SessionStats(
    val battleCount: Int = 0,
    val wins: Int = 0,
    val totalDamage: Long = 0L,
    val totalFrags: Int = 0,
    val survivedCount: Int = 0,
    val totalXP: Long = 0L,
    val startTime: Long = System.currentTimeMillis(),
    val battleHistory: List<SessionBattle> = emptyList()
) {
    val winRate: Double
        get() = if (battleCount > 0) (wins.toDouble() / battleCount * 100) else 0.0
    val avgDamage: Int
        get() = if (battleCount > 0) (totalDamage / battleCount).toInt() else 0
    val avgFrags: Double
        get() = if (battleCount > 0) totalFrags.toDouble() / battleCount else 0.0
    val survivalRate: Double
        get() = if (battleCount > 0) survivedCount.toDouble() / battleCount * 100 else 0.0
    val avgXP: Int
        get() = if (battleCount > 0) (totalXP / battleCount).toInt() else 0
    val sessionDurationMs: Long
        get() = System.currentTimeMillis() - startTime
    val bestBattle: SessionBattle?
        get() = battleHistory.maxByOrNull { it.damage }
    val worstBattle: SessionBattle?
        get() = battleHistory.minByOrNull { it.damage }
    val winStreak: Int
        get() {
            var streak = 0
            for (b in battleHistory.reversed()) { if (b.isWin) streak++ else break }
            return streak
        }
    val loseStreak: Int
        get() {
            var streak = 0
            for (b in battleHistory.reversed()) { if (!b.isWin) streak++ else break }
            return streak
        }
}
