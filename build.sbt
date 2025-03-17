import uk.gov.hmrc.DefaultBuildSettings
import play.sbt.routes.RoutesKeys


val appName = "push-pull-notification-receiver-stub"

ThisBuild / majorVersion := 0
ThisBuild / scalaVersion := "3.6.4"

lazy val microservice = Project(appName, file("."))
  .enablePlugins(play.sbt.PlayScala, SbtDistributablesPlugin)
  .settings(
    PlayKeys.playDefaultPort := 10202,
    libraryDependencies ++= AppDependencies.compile ++ AppDependencies.test,
    scalacOptions += "-Wconf:src=routes/.*:s",
    scalacOptions += "-Wconf:msg=Flag.*repeatedly:s",
    scalacOptions += "-Xfatal-warnings",
      RoutesKeys.routesImport ++= Seq("models._"),
    scalacOptions := scalacOptions.value.map {
      case "-Ykind-projector" => "-Xkind-projector"
      case option             => option
    }
  )
  .settings(resolvers += Resolver.jcenterRepo)
  .settings(CodeCoverageSettings.settings*)
  .settings(inThisBuild(buildSettings))

lazy val it = project
  .enablePlugins(PlayScala)
  .dependsOn(microservice % "test->test") // the "test->test" allows reusing test code and test dependencies
  .settings(DefaultBuildSettings.itSettings())
  .settings(
    libraryDependencies ++= AppDependencies.test
  )
  .settings(
    scalacOptions += "-Wconf:msg=Flag.*repeatedly:s",
    scalacOptions := scalacOptions.value.map {
      case "-Ykind-projector" => "-Xkind-projector"
      case option             => option
    }
  )

  .settings(CodeCoverageSettings.settings*)

lazy val buildSettings = Def.settings(
  scalafmtOnCompile := true
)
