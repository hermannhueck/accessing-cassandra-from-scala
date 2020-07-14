package cassandra.embedded

import scala.concurrent._
import scala.concurrent.duration._
import org.cassandraunit.utils._

object Start extends hutil.App {

  implicit val ec: ExecutionContext = ExecutionContext.global

  Await.result(
    Future(EmbeddedCassandraServerHelper.startEmbeddedCassandra()),
    60.seconds
  )

}
