// Comment to get more information during initialization
logLevel := Level.Warn

// Use the Play sbt plugin for Play projects
addSbtPlugin("com.typesafe.play" % "sbt-plugin" % "2.8.19")
//addSbtPlugin("org.scoverage" % "sbt-scoverage" % "1.6.1")  // Broken from 2.12.13 and upwards (https://github.com/scoverage/sbt-scoverage/issues/319)
addSbtPlugin("org.scalastyle" %% "scalastyle-sbt-plugin" % "1.0.0")
//addSbtPlugin("com.sksamuel.scapegoat" %% "sbt-scapegoat" % "1.1.0")

addSbtPlugin("com.github.dwickern" % "sbt-swagger-play" % "0.5.0")

addSbtPlugin("se.marcuslonnberg" % "sbt-docker" % "1.10.0")

addSbtPlugin("ch.epfl.scala" % "sbt-scalafix" % "0.12.0")

ThisBuild / evictionErrorLevel := Level.Info
