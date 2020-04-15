package utils

object StringUtils {

  def removeFirstSlash(str: String): String = {
    str.headOption match {
      case Some('/') => str.tail
      case _ => str
    }
  }

}
