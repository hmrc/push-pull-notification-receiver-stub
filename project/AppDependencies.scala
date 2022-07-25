import play.core.PlayVersion
import sbt._

object AppDependencies {

  val compile = Seq(
    "uk.gov.hmrc"       %% "bootstrap-backend-play-28" % "5.24.0",
    "uk.gov.hmrc.mongo" %% "hmrc-mongo-play-28"        % "0.68.0",
    "org.typelevel"     %% "cats-core"                 % "2.6.1"
  )

  val test = Seq(
    "uk.gov.hmrc"            %% "bootstrap-test-play-28"  % "5.3.0"             % Test,
    "org.scalatest"          %% "scalatest"               % "3.2.9"             % Test,
    "com.typesafe.play"      %% "play-test"               % PlayVersion.current % Test,
    "org.mockito"            %% "mockito-scala-scalatest" % "1.16.37"           % Test,
    "org.scalatestplus"      %% "scalacheck-1-15"         % "3.2.9.0"           % Test,
    "uk.gov.hmrc.mongo"      %% "hmrc-mongo-test-play-28" % "0.50.0"            % "test, it",
    "com.vladsch.flexmark"    % "flexmark-all"            % "0.36.8"            % "test, it",
    "org.scalatestplus.play" %% "scalatestplus-play"      % "5.1.0"             % "test, it",
    "com.github.tomakehurst"  % "wiremock-standalone"     % "2.27.2"            % IntegrationTest
  )
}
