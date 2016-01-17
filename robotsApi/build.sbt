name := "sprayApiExample"

version := "1.0"

scalaVersion := "2.11.6"

libraryDependencies ++= {
  val akkaV = "2.3.9"
  val sprayV = "1.3.3"
  Seq(
    "io.spray"            %%  "spray-can"     % sprayV,
    "io.spray"            %%  "spray-servlet" % sprayV,
    "io.spray"            %%  "spray-routing" % sprayV,
    "io.spray"            %%  "spray-json"    % "1.3.1", //has not been updated yet
    "com.typesafe.akka"   %%  "akka-actor"    % akkaV,
    "joda-time" % "joda-time" % "2.9.1"
  )
}

//This adds tomcat dependencies, you can also use jetty()
tomcat()
