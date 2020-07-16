package com.datastax.workshop.scala

import java.util.UUID

import org.junit.jupiter.api._
import org.junit.platform.runner.JUnitPlatform
import org.junit.runner.RunWith

import org.slf4j.LoggerFactory

import com.datastax.oss.driver.api.core.CqlSession
import com.datastax.oss.driver.api.core.cql._

@RunWith(classOf[JUnitPlatform])
object Ex09_Query4c_ReadMetrics {

  private val LOGGER                 = LoggerFactory.getLogger("Exercise4")
  private var cqlSession: CqlSession = _

  @BeforeAll def initConnection(): Unit =
    cqlSession = createCqlSession(LOGGER)

  @AfterAll def closeConnectionToCassandra(): Unit =
    closeCqlSession(cqlSession, LOGGER)
}

@RunWith(classOf[JUnitPlatform])
class Ex09_Query4c_ReadMetrics {

  import Ex09_Query4c_ReadMetrics._

  @Test
  def read_a_dimension(): Unit = {

    val stmt = SimpleStatement
      .builder("select * from spacecraft_speed_over_time where spacecraft_name=? AND journey_id=?")
      .addPositionalValue(Ex04_Query5b_TakeOff.SPACECRAFT)
      .addPositionalValue(UUID.fromString(Ex04_Query5b_TakeOff.JOURNEY_ID))
      .build

    val rs     = cqlSession.execute(stmt)
    // we fetch everything
    var offset = 0
    import scala.jdk.CollectionConverters._
    rs.all().asScala.foreach { row =>
      LOGGER.info(
        "idx:{}, time={}, value={}", {
          offset += 1; offset
        },
        row.getInstant("reading_time"),
        row.getDouble("speed")
      )
    }
    LOGGER.info("SUCCESS")
  }
}
