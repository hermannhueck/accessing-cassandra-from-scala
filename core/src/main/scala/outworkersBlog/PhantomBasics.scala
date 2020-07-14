package outworkersBlog

import scala.concurrent._
import scala.concurrent.duration._
import scala.util.chaining._

import com.outworkers.phantom.dsl._
import java.{util => ju}
import org.joda.time.DateTime

/*
  Source inspired by Flavian Alexandru' blog post at:
  https://medium.com/outworkers/a-series-of-phantom-part1-getting-started-with-phantom-1014787bc550
 */
object PhantomBasics extends hutil.App {

  // @annotation.nowarn("cat=unused-privates")
  object Defaults {
    val keyspace                                        = "whatever"
    // private val connectorEmbedded: CassandraConnection  = ContactPoint.embedded.keySpace("whatever")
    // val hosts                                           = Seq("10.10.5.20", "1.1.1.1")
    private val hosts                                   = Seq("localhost")
    private val connectorLocalhost: CassandraConnection = ContactPoints(hosts).keySpace(keyspace)
    val connector                                       = connectorLocalhost
  }

  case class User(
      id: UUID,
      email: String,
      name: String,
      registrationDate: DateTime
  )

  abstract class Users extends Table[Users, User] {

    object id               extends UUIDColumn with PartitionKey
    object email            extends StringColumn
    object name             extends StringColumn
    object registrationDate extends DateTimeColumn

    // def insertRecordNotWorking(user: User): Future[ResultSet] = {
    //   store(user)
    //     .consistencyLevel_=(ConsistencyLevel.ALL)
    //     .future()
    // }

    // workaround with insert
    @annotation.nowarn("cat=deprecation")
    def insertRecord(user: User): Future[ResultSet] =
      insert
        .value(_.id, user.id)
        .value(_.email, user.email)
        .value(_.name, user.name)
        .value(_.registrationDate, user.registrationDate)
        .consistencyLevel_=(ConsistencyLevel.ALL)
        .future()

    def getById(id: UUID): Future[Option[User]] =
      select
        .where(_.id eqs id)
        .one()

    def deleteById(id: UUID): Future[ResultSet] =
      delete()
        .where(_.id eqs id)
        .consistencyLevel_=(ConsistencyLevel.ONE)
        .future()
  }

  class MyDatabase(override val connector: CassandraConnection) extends Database[MyDatabase](connector) {
    object users extends Users with connector.Connector
  }

  object MyDatabase extends MyDatabase(Defaults.connector)

  implicit val ec: ExecutionContext = ExecutionContext.global

  // sys.addShutdownHook {
  //   println("=====>>> Shutting down MyDatabase")
  //   Await.ready(MyDatabase.truncateAsync(), 20.seconds)
  //   MyDatabase.shutdown()
  //   Thread.sleep(10000L)
  //   println("=====>>> Shutdown COMPLETE")
  // }

  try {
    println("=====>>> Setting up CassandraConnection ...")
    Await.ready(MyDatabase.createAsync(), 20.seconds)
    Await.ready(MyDatabase.truncateAsync(), 20.seconds)
    println("=====>>> CassandraConnection Setup COMPLETE")

    val id      = ju.UUID.randomUUID()
    val johnDoe = User(id, "john@doe.com", "John Doe", DateTime.now())
    val created = Await.result(MyDatabase.users.insertRecord(johnDoe), 5.seconds)
    created pipe { r => println(s"----->>> $r") }

    val retrieved = Await.result(MyDatabase.users.getById(id), 5.seconds)
    retrieved pipe { r => println(s"----->>> $r") }

    val deleted = Await.result(MyDatabase.users.deleteById(id), 5.seconds)
    deleted pipe { r => println(s"----->>> $r") }
  } finally {
    println("=====>>> Shutting down CassandraConnection ...")
    MyDatabase.truncate()
    MyDatabase.shutdown()
    // Thread.sleep(10000L)
    println("=====>>> CassandraConnection Shutdown COMPLETE")
  }
}
