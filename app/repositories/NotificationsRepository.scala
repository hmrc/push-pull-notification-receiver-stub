/*
 * Copyright 2022 HM Revenue & Customs
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

package repositories

import models.BoxId
import models.Notification
import models.formats.MongoFormats
import org.bson.codecs.configuration.CodecRegistries
import org.mongodb.scala.MongoClient
import org.mongodb.scala.MongoCollection
import org.mongodb.scala.bson.BsonDocument
import org.mongodb.scala.model.Filters
import org.mongodb.scala.model.IndexModel
import org.mongodb.scala.model.IndexOptions
import org.mongodb.scala.model.Indexes
import uk.gov.hmrc.mongo.MongoComponent
import uk.gov.hmrc.mongo.play.json.Codecs
import uk.gov.hmrc.mongo.play.json.CollectionFactory
import uk.gov.hmrc.mongo.play.json.PlayMongoRepository

import javax.inject.Inject
import javax.inject.Singleton
import scala.concurrent.ExecutionContext
import scala.concurrent.Future

@Singleton
class NotificationsRepository @Inject() (mongo: MongoComponent)(implicit ec: ExecutionContext)
    extends PlayMongoRepository[Notification](
      mongoComponent = mongo,
      collectionName = "notifications",
      domainFormat = MongoFormats.notificationFormat,
      replaceIndexes = true,
      indexes = Seq(
        IndexModel(
          Indexes.ascending("notificationId"),
          IndexOptions().background(false).unique(true)
        ),
        IndexModel(Indexes.ascending("boxId"))
      )
    ) {

  override lazy val collection: MongoCollection[Notification] =
    CollectionFactory
      .collection(mongo.database, collectionName, domainFormat)
      .withCodecRegistry(
        CodecRegistries.fromRegistries(
          CodecRegistries.fromCodecs(
            Codecs.playFormatCodec(domainFormat),
            Codecs.playFormatCodec(MongoFormats.jsonNotificationFormat),
            Codecs.playFormatCodec(MongoFormats.xmlNotificationFormat)
          ),
          MongoClient.DEFAULT_CODEC_REGISTRY
        )
      )

  def find(boxId: BoxId): Future[Seq[Notification]] =
    collection.find(Filters.eq("boxId", Codecs.toBson(boxId.value))).toFuture

  def insert(notification: Notification): Future[Unit] =
    collection.insertOne(notification).toFuture.map(_ => ())

  def deleteAll(): Future[Unit] =
    collection.deleteMany(BsonDocument()).toFuture.map(_ => ())
}
