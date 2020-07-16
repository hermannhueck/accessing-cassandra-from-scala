package com.datastax.workshop.scala

import org.junit.jupiter.api._
import org.junit.platform.runner.JUnitPlatform
import org.junit.runner.RunWith

import org.slf4j.LoggerFactory

import com.datastax.oss.driver.api.core.CqlSession
import com.datastax.oss.driver.api.core.cql._

@RunWith(classOf[JUnitPlatform])
object Ex07_Query4a_ListJourneys {

  private val LOGGER                 = LoggerFactory.getLogger("Exercise4")
  private var cqlSession: CqlSession = _

  @BeforeAll def initConnection(): Unit =
    cqlSession = createCqlSession(LOGGER)

  @AfterAll def closeConnectionToCassandra(): Unit =
    closeCqlSession(cqlSession, LOGGER)
}

@RunWith(classOf[JUnitPlatform])
class Ex07_Query4a_ListJourneys {

  import Ex07_Query4a_ListJourneys._

  /*
   * select * from spacecraft_journey_catalog
   * WHERE journey_id=47b04070-c4fb-11ea-babd-17b91da87c10
   * AND spacecraft_name='DragonCrew,SpaceX';
   */
  @Test
  def listJourneys(): Unit = {

    val stmt = SimpleStatement
      .builder("select * from spacecraft_journey_catalog where spacecraft_name=?")
      .addPositionalValue(Ex04_Query5b_TakeOff.SPACECRAFT)
      .build

    val rs = cqlSession.execute(stmt)
    import scala.jdk.CollectionConverters._
    rs.all().asScala.foreach { row =>
      LOGGER.info("- Journey: {} Summary: {}", row.getUuid("journey_id"), row.getString("summary"))
    }
    LOGGER.info("SUCCESS")
  }
}
