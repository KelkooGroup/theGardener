name := "the_gardener"
maintainer := "florian.fauvarque@gmail.com"

val jdkVersion = "11"
scalaVersion := "2.13.10"

lazy val root = (project in file(".")).enablePlugins(PlayScala).settings(
  swaggerPlayValidate := false
)

// specify the source and target jdk for Java compiler
javacOptions ++= Seq("-source", jdkVersion, "-target", jdkVersion)

// glorious Scala 2.13 options from https://nathankleyn.com/2019/05/13/recommended-scalac-flags-for-2-13/
scalacOptions ++= Seq(
  "-deprecation", // Emit warning and location for usages of deprecated APIs.
  "-explaintypes", // Explain type errors in more detail.
  "-feature", // Emit warning and location for usages of features that should be imported explicitly.
  "-language:existentials", // Existential types (besides wildcard types) can be written and inferred
  "-language:experimental.macros", // Allow macro definition (besides implementation and application)
  "-language:higherKinds", // Allow higher-kinded types
  "-language:implicitConversions", // Allow definition of implicit functions called views
  "-unchecked", // Enable additional warnings where generated code depends on assumptions.
  "-Xcheckinit", // Wrap field accessors to throw an exception on uninitialized access.
  "-Xlint:adapted-args", // Warn if an argument list is modified to match the receiver.
  "-Xlint:constant", // Evaluation of a constant arithmetic expression results in an error.
  "-Xlint:delayedinit-select", // Selecting member of DelayedInit.
  "-Xlint:doc-detached", // A Scaladoc comment appears to be detached from its element.
  "-Xlint:inaccessible", // Warn about inaccessible types in method signatures.
  "-Xlint:infer-any", // Warn when a type argument is inferred to be `Any`.
  "-Xlint:missing-interpolator", // A string literal appears to be missing an interpolator id.
  "-Xlint:nullary-unit", // Warn when nullary methods return Unit.
  "-Xlint:option-implicit", // Option.apply used implicit view.
  "-Xlint:package-object-classes", // Class or object defined in package object.
  "-Xlint:poly-implicit-overload", // Parameterized overloaded implicit methods are not visible as view bounds.
  "-Xlint:private-shadow", // A private field (or class parameter) shadows a superclass field.
  "-Xlint:stars-align", // Pattern sequence wildcard must align with sequence component.
  "-Xlint:type-parameter-shadow", // A local type parameter shadows a type already in scope.
  "-Ywarn-dead-code", // Warn when dead code is identified.
  "-Ywarn-extra-implicit", // Warn when more than one implicit parameter section is defined.
  "-Ywarn-numeric-widen", // Warn when numerics are widened.
  "-Ywarn-unused:implicits", // Warn if an implicit parameter is unused.
  "-Ywarn-unused:imports", // Warn if an import selector is not referenced.
  "-Ywarn-unused:locals", // Warn if a local definition is unused.
  "-Ywarn-unused:nowarn", // checks that every @nowarn annotation suppresses at least one warning
  "-Ywarn-unused:params", // Warn if a value parameter is unused.
  "-Ywarn-unused:patvars", // Warn if a variable bound in a pattern is unused.
  "-Ywarn-unused:privates", // Warn if a private member is unused.
  "-Ywarn-value-discard", // Warn when non-Unit expression results are unused.
  "-Ybackend-parallelism", "8", // Enable paralellisation — change to desired number!
  "-Ycache-plugin-class-loader:last-modified", // Enables caching of classloaders for compiler plugins
  "-Ycache-macro-class-loader:last-modified", // and macro definitions. This can lead to performance improvements.
)

scalacOptions ++= {
  if (insideCI.value) Seq("-Wconf:any:error", "-Xfatal-warnings")
  else Seq("-Wconf:any:warning")
}
// Ignore everything in generated files (from Play routes)
// See https://github.com/scala/bug/issues/12631 for context of why rootdir is necessary
scalacOptions ++= Seq("-rootdir", ".", "-Wconf:src=target/.*:silent")

// Add option to enable anorm stack traces
javaOptions += "-Dscala.control.noTraceSuppression=true"

//*** dist packaging
// do not generage API documentation when using dist task
Compile / doc / sources := Seq.empty
Compile / packageDoc / publishArtifact := false


//Removing the top level directory
topLevelDirectory := None

val silencerVersion = "1.5.0"
val cucumberVersion = "7.14.0"
val jacksonVersion = "2.14.2"

libraryDependencies ++= Seq(
  ws,
  filters,
  guice,
  evolutions,
  jdbc,
  caffeine,
  "ch.qos.logback" % "logback-access" % "1.2.12",
  "net.logstash.logback" % "logstash-logback-encoder" % "7.3",
  "com.typesafe.play" %% "play-json" % "2.9.4",
  "org.julienrf" %% "play-json-derived-codecs" % "10.1.0",
  "io.cucumber" % "gherkin" % "26.0.3",
  "org.playframework.anorm" %% "anorm" % "2.7.0",
  "org.mariadb.jdbc" % "mariadb-java-client" % "3.1.3",
  "org.eclipse.jgit" % "org.eclipse.jgit" % "6.5.0.202303070854-r",
  // Swagger
  "io.swagger" % "swagger-annotations" % "1.6.10", // do not upgrade beyond 1.x because of sbt-swagger-play compatibility
  "org.webjars" % "swagger-ui" % "4.18.2",

  "com.h2database" % "h2" % "1.4.199",
  "commons-io" % "commons-io" % "2.11.0",
  "com.outr" %% "lucene4s" % "1.11.1",

  "net.ruippeixotog" %% "scala-scraper" % "3.1.0" % Test,

  // Cucumber
  "io.cucumber" %% "cucumber-scala" % "8.14.2" % Test,
  "io.cucumber" % "cucumber-junit" % cucumberVersion % Test,
  "io.cucumber" % "cucumber-picocontainer" % cucumberVersion % Test,

  "com.fasterxml.jackson.module" %% "jackson-module-scala" % jacksonVersion % Test,
  "org.scalatestplus.play" %% "scalatestplus-play" % "5.1.0" % Test,
  "org.scalatestplus" %% "mockito-3-12" % "3.2.10.0" % Test,
)

dependencyOverrides ++= Seq(
  // Jackson conflict between logback and swagger
  "com.fasterxml.jackson.core" % "jackson-core" % jacksonVersion,
  "com.fasterxml.jackson.core" % "jackson-databind" % jacksonVersion,
  "com.fasterxml.jackson.module" %% "jackson-module-scala" % jacksonVersion,
)

ThisBuild / evictionErrorLevel := Level.Info

routesGenerator := InjectedRoutesGenerator

// Enabling Docker plugin
enablePlugins(sbtdocker.DockerPlugin, JavaAppPackaging)

// Dockerfile definition
docker / dockerfile := {
  val appDir: File = stage.value
  val defaultConfFile: File = new File("./docker/application.conf")
  val defaultLogBackFile: File = new File("./local-conf/logback.xml")
  val targetDir = "/app"
  val confDir = "/app-conf"
  val gitDataDir = "/git-data"
  val embeddedDbDir = "/data"

  new Dockerfile {
    from("openjdk:11-jre")
    runRaw(s"mkdir $confDir && mkdir $gitDataDir && mkdir $embeddedDbDir")
    expose(9000)
    entryPoint(s"$targetDir/bin/${executableScriptName.value}")
    cmd(s"-Dconfig.file=$confDir/application.conf")
    copy(defaultConfFile, s"$confDir/", chown = "daemon:daemon")
    copy(defaultLogBackFile, s"$confDir/", chown = "daemon:daemon")
    copy(appDir, targetDir, chown = "daemon:daemon")
  }
}

// Images definitions to build and push
val dockerOrganization = "kelkoogroup"
val dockerImage = "thegardener"
docker / imageNames := Seq(
  ImageName(s"$dockerOrganization/$dockerImage:latest"),
  ImageName(s"$dockerOrganization/$dockerImage:${version.value}")
)
