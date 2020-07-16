package com.datastax.workshop.scala

import java.time.Instant
import java.util.UUID

import org.junit.jupiter.api._
import org.junit.platform.runner.JUnitPlatform
import org.junit.runner.RunWith

import org.slf4j.LoggerFactory

import com.datastax.oss.driver.api.core.CqlSession

@RunWith(classOf[JUnitPlatform])
object Ex05_Query5c_Travel {

  private val LOGGER                         = LoggerFactory.getLogger("Exercise3")
  private var cqlSession: CqlSession         = _
  private var journeyRepo: JourneyRepository = _

  @BeforeAll def initConnection(): Unit = {
    cqlSession = createCqlSession(LOGGER)
    journeyRepo = new JourneyRepository(cqlSession)
  }

  @AfterAll def closeConnectionToCassandra(): Unit =
    closeCqlSession(cqlSession, LOGGER)
}

@RunWith(classOf[JUnitPlatform])
class Ex05_Query5c_Travel {

  import Ex05_Query5c_Travel._

  @Test
  @throws[InterruptedException]
  def save_readings(): Unit = {
    for (i <- 0 until 50) {
      val speed       = 300 + i + Math.random * 10
      val pressure    = Math.random * 20
      val temperature = Math.random * 300
      val x           = 13 + i
      val y           = 14 + i
      val z           = 36 + i
      val readingTime = Instant.now
      journeyRepo
        .log(
          UUID.fromString(Ex04_Query5b_TakeOff.JOURNEY_ID),
          Ex04_Query5b_TakeOff.SPACECRAFT,
          speed,
          pressure,
          temperature,
          x.toDouble,
          y.toDouble,
          z.toDouble,
          readingTime
        )
      Thread.sleep(200)
      LOGGER.info("{}/50 - travelling..", i)
    }
    LOGGER.info("Reading saved", Ex04_Query5b_TakeOff.JOURNEY_ID)
    LOGGER.info("SUCCESS")

    /* select * from spacecraft_speed_over_time
     * where spacecraft_name='DragonCrew,SpaceX'
     * AND journey_id=b7fdf670-c5b8-11ea-9d41-49528c2e2634;
     */
  }
}
