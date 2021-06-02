import play.sbt.routes.RoutesKeys
import uk.gov.hmrc.DefaultBuildSettings
import uk.gov.hmrc.sbtdistributables.SbtDistributablesPlugin
import scoverage.ScoverageKeys

val appName = "push-pull-notification-receiver-stub"

lazy val microservice = Project(appName, file("."))
  .enablePlugins(play.sbt.PlayScala, SbtAutoBuildPlugin, SbtGitVersioning, SbtDistributablesPlugin)
  .disablePlugins(
    JUnitXmlReportPlugin
  ) //Required to prevent https://github.com/scalatest/scalatest/issues/1427
  .configs(IntegrationTest)
  .settings(SbtDistributablesPlugin.publishingSettings)
  .settings(DefaultBuildSettings.integrationTestSettings())
  .settings(inConfig(Test)(testReportSettings))
  .settings(inConfig(IntegrationTest)(testReportSettings))
  .settings(inConfig(IntegrationTest)(ScalafmtPlugin.scalafmtConfigSettings))
  .settings(scalacSettings)
  .settings(scoverageSettings)
  .settings(
    majorVersion := 0,
    scalaVersion := "2.12.13",
    resolvers += Resolver.jcenterRepo,
    libraryDependencies ++= AppDependencies.compile ++ AppDependencies.test,
    RoutesKeys.routesImport ++= Seq("models._")
  )

lazy val scalacSettings = Def.settings(
  scalacOptions += "-Wconf:src=routes/.*:silent",
  scalacOptions ~= { opts => opts.filterNot(Set("-Xlint")) },
  scalacOptions ~= { opts => opts.filterNot(_.startsWith("-Ywarn-unused")) },
  scalacOptions += "-Ywarn-unused:imports"
)

// Scoverage exclusions and minimums
lazy val scoverageSettings = Def.settings(
  ScoverageKeys.coverageMinimumStmtTotal := 85,
  ScoverageKeys.coverageFailOnMinimum := true,
  ScoverageKeys.coverageHighlighting := true,
  ScoverageKeys.coverageExcludedFiles := Seq(
    "<empty>",
    ".*javascript.*",
    ".*Routes.*"
  ).mkString(";"),
  ScoverageKeys.coverageExcludedPackages := Seq(
    """uk\.gov\.hmrc\.BuildInfo*""",
    """.*\.Routes""",
    """.*\.RoutesPrefix""",
    """.*\.Reverse[^.]*""",
    """config\.*"""
  ).mkString(";")
)

// Disable test reports outside of CI
lazy val testReportSettings = Def.settings(
  testOptions ~= { opts =>
    if (sys.env.get("JENKINS_HOME").nonEmpty) {
      opts
    } else {
      Seq.empty
    }
  }
)
