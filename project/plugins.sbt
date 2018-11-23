// Comment to get more information during initialization
logLevel := Level.Warn

// Use the Play sbt plugin for Play projects
addSbtPlugin("com.typesafe.play" % "sbt-plugin" % "2.6.20")
addSbtPlugin("com.github.sbt" % "sbt-jacoco" % "3.1.0")

addSbtPlugin("com.kelkoo.common.sbt" % "sbt-angular-cli" % "0.1.6")

evictionWarningOptions in update := EvictionWarningOptions.empty