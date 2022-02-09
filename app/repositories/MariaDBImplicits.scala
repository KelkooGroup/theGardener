package repositories

import anorm.{Column, MetaDataItem, TypeDoesNotMatch}

import java.time.Instant

object MariaDBImplicits {
  // MariaDB driver returns java BigInt instead of Long for the millis column, so the default Instant mapper does not work
  implicit def columnToInstant: Column[Instant] = Column.columnToLong.map(Instant.ofEpochMilli)

  // Starting with v3, MariaDB driver returns Byte instead of Boolean for boolean columns
  implicit def columnToBoolean: Column[Boolean] =
    Column.nonNull { (value, meta) =>
      val MetaDataItem(qualified, _, _) = meta
      value match {
        case bool: Boolean => Right(bool)
        case bit: Byte if bit == 1 => Right(true)
        case bit: Byte if bit == 0 => Right(false)
        case bit: Byte => Left(TypeDoesNotMatch(s"Cannot convert $bit to Boolean for column $qualified"))
        case _ => Left(TypeDoesNotMatch(s"Cannot convert $value: ${value.asInstanceOf[AnyRef].getClass} to Boolean for column $qualified"))
      }
    }


}
