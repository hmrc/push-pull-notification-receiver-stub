import play.core.PlayVersion
import sbt._

object AppDependencies {

  val compile: Seq[ModuleID] = Seq(
    "uk.gov.hmrc"       %% "bootstrap-backend-play-30" % "9.11.0",
    "uk.gov.hmrc.mongo" %% "hmrc-mongo-play-30"        % "2.5.0",
    "org.typelevel"     %% "cats-core"                 % "2.13.0"
  )

  val test: Seq[ModuleID] = Seq(
    "org.scalacheck"        %% "scalacheck"              % "1.18.1",
    "org.mockito"            % "mockito-core"            % "5.16.1",
    "org.scalatestplus"     %% "mockito-5-12"            % "3.2.19.0",
    "org.scalacheck"        %% "scalacheck"              % "1.18.1",
    "org.scalatestplus"     %% "scalacheck-1-18"         % "3.2.19.0",
    "uk.gov.hmrc"           %% "bootstrap-test-play-30"  % "9.11.0",
    "org.scalatestplus"     %% "scalacheck-1-18"         % "3.2.19.0",
    "uk.gov.hmrc.mongo"     %% "hmrc-mongo-test-play-30" % "2.5.0",
    "com.vladsch.flexmark"   % "flexmark-all"            % "0.64.8",
    "com.github.tomakehurst" % "wiremock-standalone"     % "3.0.1"
  ).map(_ % Test)
}
