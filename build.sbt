name := "sprayApiExample"

version := "1.0"

scalaVersion := "2.11.2"

libraryDependencies ++= {
  val akkaV = "2.3.6"
  val sprayV = "1.3.2"
  Seq(
    "io.spray"            %%  "spray-can"     % sprayV,
    "io.spray"            %%  "spray-routing" % sprayV,
    "io.spray"            %%  "spray-json"    % "1.3.1", //has not been updated to 1.3.2 yet
    "com.typesafe.akka"   %%  "akka-actor"    % akkaV
  )
}
