/*
 * Copyright 2023 HM Revenue & Customs
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package base

import com.github.tomakehurst.wiremock.WireMockServer
import com.github.tomakehurst.wiremock.core.WireMockConfiguration
import org.scalatest.{BeforeAndAfterAll, BeforeAndAfterEach, Suite}
import org.scalatestplus.play.{BaseOneServerPerSuite, FakeApplicationFactory}
import play.api.Application
import play.api.inject.guice.{GuiceApplicationBuilder, GuiceableModule}

trait WiremockSuite extends BeforeAndAfterAll with BeforeAndAfterEach {
  this: Suite & BaseOneServerPerSuite & FakeApplicationFactory =>

  protected val server: WireMockServer = new WireMockServer(
    WireMockConfiguration.wireMockConfig().dynamicPort()
  )

  protected def portConfigKeys: Seq[String]

  override def fakeApplication(): Application =
    new GuiceApplicationBuilder()
      .configure(portConfigKeys.map(key => key -> server.port().toString) *)
      .overrides(bindings *)
      .build()

  protected def bindings: Seq[GuiceableModule] = Seq.empty

  override protected def beforeAll(): Unit = {
    server.start()
    app
    super.beforeAll()
  }

  override protected def beforeEach(): Unit = {
    server.resetAll()
    app
    super.beforeEach()
  }

  override protected def afterAll(): Unit = {
    super.afterAll()
    server.stop()
  }
}
