name := "the_gardener"
maintainer := "florian.fauvarque@gmail.com"

val jdkVersion = "1.8"
scalaVersion := "2.12.8"

lazy val root = (project in file(".")).enablePlugins(PlayScala)


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
  caffeine,
  "ch.qos.logback" % "logback-access" % "1.2.3",
  "net.logstash.logback" % "logstash-logback-encoder" % "6.1",
  "com.typesafe.play" %% "play-json" % "2.7.4",
  "org.julienrf" %% "play-json-derived-codecs" % "6.0.0",
  "io.cucumber" % "gherkin" % "5.1.0",
  "com.typesafe.play" %% "anorm" % "2.5.3",
  "mysql" % "mysql-connector-java" % "5.1.30",
  "org.eclipse.jgit" % "org.eclipse.jgit" % "5.1.2.201810061102-r",
  "io.swagger" %% "swagger-play2" % "1.7.1",
  "com.h2database" % "h2" % "1.4.199",
  "com.jsuereth" %% "scala-arm" % "2.0",
  "commons-io" % "commons-io" % "2.6",
  "net.ruippeixotog" %% "scala-scraper" % "2.1.0" % Test,
  "io.cucumber" %% "cucumber-scala" % "2.0.1" % Test,
  "io.cucumber" % "cucumber-junit" % "2.4.0" % Test,
  "io.cucumber" % "cucumber-picocontainer" % "2.4.0" % Test,
  "org.scalatestplus.play" %% "scalatestplus-play" % "4.0.3" % Test,
  "org.mockito" % "mockito-all" % "1.10.19" % Test
)

scapegoatVersion in ThisBuild := "1.3.8"
scapegoatMaxErrors := 10000
scapegoatDisabledInspections := Seq("FinalModifierOnCaseClass", "PreferSeqEmpty", "PreferSetEmpty", "CatchException")
scapegoatIgnoredFiles := Seq(".*/*Routes.scala")
scalacOptions in Scapegoat += "-P:scapegoat:overrideLevels:TraversableHead=Warning:OptionGet=Warning"

evictionWarningOptions in update := EvictionWarningOptions.empty

routesGenerator := InjectedRoutesGenerator
