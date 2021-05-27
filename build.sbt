import uk.gov.hmrc.DefaultBuildSettings.integrationTestSettings
import uk.gov.hmrc.sbtdistributables.SbtDistributablesPlugin.publishingSettings

val appName = "push-pull-notification-receiver-stub"

lazy val microservice = Project(appName, file("."))
  .enablePlugins(play.sbt.PlayScala, SbtAutoBuildPlugin, SbtGitVersioning, SbtDistributablesPlugin)
  .configs(IntegrationTest)
  .settings(publishingSettings: _*)
  .settings(integrationTestSettings(): _*)
  .settings(inConfig(IntegrationTest)(ScalafmtPlugin.scalafmtConfigSettings))
  .settings(
    majorVersion := 0,
    scalaVersion := "2.12.13",
    resolvers += Resolver.jcenterRepo,
    libraryDependencies ++= AppDependencies.compile ++ AppDependencies.test,
    scalacOptions += "-Wconf:src=routes/.*:silent",
    scalacOptions ~= { opts => opts.filterNot(Set("-Xlint")) },
    scalacOptions ~= { opts => opts.filterNot(_.startsWith("-Ywarn-unused")) }
  )
