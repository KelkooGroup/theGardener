name := "the_gardener"
maintainer := "florian.fauvarque@gmail.com"

val jdkVersion = "1.8"
scalaVersion := "2.12.8"

lazy val root = (project in file(".")).enablePlugins(PlayScala)


// specify the source and target jdk for Java compiler
javacOptions ++= Seq("-source", jdkVersion, "-target", jdkVersion)

scalacOptions ++= Seq(
  "-deprecation",                      // Emit warning and location for usages of deprecated APIs.
  "-encoding", "utf-8",                // Specify character encoding used by source files.
  "-explaintypes",                     // Explain type errors in more detail.
  "-feature",                          // Emit warning and location for usages of features that should be imported explicitly.
  "-language:existentials",            // Existential types (besides wildcard types) can be written and inferred
  "-language:experimental.macros",     // Allow macro definition (besides implementation and application)
  "-language:higherKinds",             // Allow higher-kinded types
  "-language:implicitConversions",     // Allow definition of implicit functions called views
  "-unchecked",                        // Enable additional warnings where generated code depends on assumptions.
  "-Xcheckinit",                       // Wrap field accessors to throw an exception on uninitialized access.
  "-Xfatal-warnings",                  // Fail the compilation if there are any warnings.
  "-Xfuture",                          // Turn on future language features.
  "-Xlint:adapted-args",               // Warn if an argument list is modified to match the receiver.
  "-Xlint:by-name-right-associative",  // By-name parameter of right associative operator.
  "-Xlint:constant",                   // Evaluation of a constant arithmetic expression results in an error.
  "-Xlint:delayedinit-select",         // Selecting member of DelayedInit.
  "-Xlint:doc-detached",               // A Scaladoc comment appears to be detached from its element.
  "-Xlint:inaccessible",               // Warn about inaccessible types in method signatures.
  "-Xlint:infer-any",                  // Warn when a type argument is inferred to be `Any`.
  "-Xlint:missing-interpolator",       // A string literal appears to be missing an interpolator id.
  "-Xlint:nullary-override",           // Warn when non-nullary `def f()' overrides nullary `def f'.
  "-Xlint:nullary-unit",               // Warn when nullary methods return Unit.
  "-Xlint:option-implicit",            // Option.apply used implicit view.
  "-Xlint:package-object-classes",     // Class or object defined in package object.
  "-Xlint:poly-implicit-overload",     // Parameterized overloaded implicit methods are not visible as view bounds.
  "-Xlint:private-shadow",             // A private field (or class parameter) shadows a superclass field.
  "-Xlint:stars-align",                // Pattern sequence wildcard must align with sequence component.
  "-Xlint:type-parameter-shadow",      // A local type parameter shadows a type already in scope.
  "-Xlint:unsound-match",              // Pattern match may not be typesafe.
  "-Yno-adapted-args",                 // Do not adapt an argument list (either by inserting () or creating a tuple) to match the receiver.
  "-Ypartial-unification",             // Enable partial unification in type constructor inference
  "-Ywarn-dead-code",                  // Warn when dead code is identified.
  "-Ywarn-extra-implicit",             // Warn when more than one implicit parameter section is defined.
  "-Ywarn-inaccessible",               // Warn about inaccessible types in method signatures.
  "-Ywarn-infer-any",                  // Warn when a type argument is inferred to be `Any`.
  "-Ywarn-nullary-override",           // Warn when non-nullary `def f()' overrides nullary `def f'.
  "-Ywarn-nullary-unit",               // Warn when nullary methods return Unit.
  "-Ywarn-numeric-widen",              // Warn when numerics are widened.
  "-Ywarn-unused:implicits",           // Warn if an implicit parameter is unused.
  "-Ywarn-unused:imports",             // Warn if an import selector is not referenced.
  "-Ywarn-unused:locals",              // Warn if a local definition is unused.
  "-Ywarn-unused:params",              // Warn if a value parameter is unused.
  "-Ywarn-unused:patvars",             // Warn if a variable bound in a pattern is unused.
  "-Ywarn-unused:privates",            // Warn if a private member is unused.
  "-Ywarn-value-discard"               // Warn when non-Unit expression results are unused.
)

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

val silencerVersion = "1.4.4"
libraryDependencies ++= Seq(
  ws,
  filters,
  guice,
  evolutions,
  jdbc,
  caffeine,
  "ch.qos.logback" % "logback-access" % "1.2.3",
  "net.logstash.logback" % "logstash-logback-encoder" % "6.3",
  "com.typesafe.play" %% "play-json" % "2.7.4",
  "org.julienrf" %% "play-json-derived-codecs" % "7.0.0",
  "io.cucumber" % "gherkin" % "9.0.0",
  "com.typesafe.play" %% "anorm" % "2.5.3",
  "mysql" % "mysql-connector-java" % "8.0.18",
  "org.eclipse.jgit" % "org.eclipse.jgit" % "5.6.0.201912101111-r",
  "io.swagger" %% "swagger-play2" % "1.7.1",
  "com.h2database" % "h2" % "1.4.199",
  "com.jsuereth" %% "scala-arm" % "2.0",
  "commons-io" % "commons-io" % "2.6",
  "net.ruippeixotog" %% "scala-scraper" % "2.2.0" % Test,
  "io.cucumber" %% "cucumber-scala" % "2.0.1" % Test,
  "io.cucumber" % "cucumber-junit" % "2.4.0" % Test,
  "io.cucumber" % "cucumber-picocontainer" % "2.4.0" % Test,
  "org.scalatestplus.play" %% "scalatestplus-play" % "4.0.3" % Test,
  "org.mockito" % "mockito-all" % "1.10.19" % Test,
  
   compilerPlugin("com.github.ghik" % "silencer-plugin" % silencerVersion cross CrossVersion.full),
  "com.github.ghik" % "silencer-lib" % silencerVersion % Provided cross CrossVersion.full
)

scapegoatVersion in ThisBuild := "1.3.8"
scapegoatMaxErrors := 10000
scapegoatDisabledInspections := Seq("FinalModifierOnCaseClass", "PreferSeqEmpty", "PreferSetEmpty", "CatchException")
scapegoatIgnoredFiles := Seq(".*/*Routes.scala")
scalacOptions in Scapegoat += "-P:scapegoat:overrideLevels:TraversableHead=Warning:OptionGet=Warning"

evictionWarningOptions in update := EvictionWarningOptions.empty

routesGenerator := InjectedRoutesGenerator
