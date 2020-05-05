package utils

object DurationUtil {

  def durationFromMillisToHumanReadable(duration: Long): String = {
    val milliseconds = duration % 1000L
    val seconds = (duration / 1000L) % 60L
    val minutes = (duration / (1000L * 60L)) % 60L
    val hours = (duration / (1000L * 3600L)) % 24L
    val days = (duration / (1000L * 86400L)) % 7L
    val weeks = (duration / (1000L * 604800L)) % 4L
    val months = (duration / (1000L * 2592000L)) % 52L
    val years = (duration / (1000L * 31556952L)) % 10L
    val decades = (duration / (1000L * 31556952L * 10L)) % 10L
    val centuries = (duration / (1000L * 31556952L * 100L)) % 100L
    val millenniums = (duration / (1000L * 31556952L * 1000L)) % 1000L
    val megaannums = duration / (1000L * 31556952L * 1000000L)

    val sb = new scala.collection.mutable.StringBuilder()

    if (megaannums > 0) sb.append(megaannums + " megaannums ")
    if (millenniums > 0) sb.append(millenniums + " millenniums ")
    if (centuries > 0) sb.append(centuries + " centuries ")
    if (decades > 0) sb.append(decades + " decades ")
    if (years > 0) sb.append(years + " years ")
    if (months > 0) sb.append(months + " months ")
    if (weeks > 0) sb.append(weeks + " weeks ")
    if (days > 0) sb.append(days + " days ")
    if (hours > 0) sb.append(hours + " hours ")
    if (minutes > 0) sb.append(minutes + " minutes ")
    if (seconds > 0) sb.append(seconds + " seconds ")
    if (minutes < 1 && hours < 1 && days < 1) {
      if (sb.nonEmpty) sb.append(" ")
      sb.append(milliseconds + " milliseconds")
    }
    sb.toString().trim
  }
}
