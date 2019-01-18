name := "the_gardener"

val pom = xml.XML.load("pom.xml")

version := { pom \  "version" text}

val jdkVersion = "1.8"
scalaVersion := "2.12.7"

lazy val root = (project in file(".")).enablePlugins(PlayScala).enablePlugins(SbtWeb)


// specify the source and target jdk for Java compiler
javacOptions ++= Seq("-source", jdkVersion, "-target", jdkVersion)

scalacOptions ++= Seq("-feature", "-deprecation", "-Xfatal-warnings")

// Add option to enable anorm stack traces
javaOptions += "-Dscala.control.noTraceSuppression=true"

// add directory for test configuration files
unmanagedClasspath in Test += baseDirectory.value / "local-conf"
unmanagedClasspath in Runtime += baseDirectory.value / "local-conf"

//*** dist packaging
// do not generage API documentation when using dist task
sources in(Compile, doc) := Seq.empty
publishArtifact in(Compile, packageDoc) := false


//Removing the top level directory
topLevelDirectory := None

libraryDependencies ++= Seq(
  ws,
  filters,
  guice,
  evolutions,
  jdbc,
  cacheApi,
  "com.kelkoo.common" %% "playScalaCommon" % "2.1.2",
  "ch.qos.logback" % "logback-access" % "1.2.3",
  "net.logstash.logback" % "logstash-logback-encoder" % "4.11",
  "com.typesafe.play" %% "play-json" % "2.6.10",
  "org.julienrf" %% "play-json-derived-codecs" % "4.0.1",
  "io.cucumber" % "gherkin" % "5.0.0",
  "com.typesafe.play" %% "anorm" % "2.5.3",
  "mysql" % "mysql-connector-java" % "5.1.30",
  "org.eclipse.jgit" % "org.eclipse.jgit" % "5.1.2.201810061102-r",
  "io.swagger" %% "swagger-play2" % "1.6.0",
  "com.h2database" % "h2" % "1.4.197",
  "com.jsuereth" %% "scala-arm" % "2.0",
  "net.ruippeixotog" %% "scala-scraper" % "2.1.0" % Test,
  "io.cucumber" %% "cucumber-scala" % "2.0.1" % Test,
  "io.cucumber" % "cucumber-junit" % "2.0.1" % Test,
  "io.cucumber" % "cucumber-picocontainer" % "2.0.1" % Test,
  "org.scalatestplus.play" %% "scalatestplus-play" % "3.1.2" % Test,
  "org.mockito" % "mockito-all" % "1.10.19" % Test
)

scapegoatVersion in ThisBuild := "1.3.8"
scapegoatMaxErrors := 10000
scapegoatDisabledInspections := Seq("FinalModifierOnCaseClass", "PreferSeqEmpty", "PreferSetEmpty", "CatchException")
scapegoatIgnoredFiles := Seq(".*/*Routes.scala")
scalacOptions in Scapegoat += "-P:scapegoat:overrideLevels:TraversableHead=Warning:OptionGet=Warning"

evictionWarningOptions in update := EvictionWarningOptions.empty

routesGenerator := InjectedRoutesGenerator

JsEngineKeys.npmNodeModules in Assets := Nil

JsEngineKeys.npmNodeModules in TestAssets := Nil
