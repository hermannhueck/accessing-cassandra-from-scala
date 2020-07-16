package com.datastax.workshop.scala

import java.time.Instant
import java.util.UUID

import org.slf4j.LoggerFactory

import com.datastax.oss.driver.api.core.CqlSession
import com.datastax.oss.driver.api.core.cql._
import com.datastax.oss.driver.api.core.uuid.Uuids

import DataModelConstants._

/**
  * CRUD Operations on a journey.
  *  Interaction with Cassandra.
  *
  * Inject Session in constructor.
  *
  * @author Developer Advocates Team
  * @param cqlSession
  * cassandra session
  */
class JourneyRepository(cqlSession: CqlSession) {

  /** Logger for the class. */
  private val LOGGER = LoggerFactory.getLogger(classOf[JourneyRepository])

  private val SOLUTION_INSERT       =
    "INSERT INTO spacecraft_journey_catalog (spacecraft_name, journey_id, active, summary) VALUES(?,?,?,?)"
  private val SOLUTION_TAKEOFF      =
    "UPDATE spacecraft_journey_catalog SET active=true, start=? WHERE spacecraft_name=? AND journey_id=?"
  private val SOLUTION_LANDING      =
    "UPDATE spacecraft_journey_catalog SET active=false, end=? WHERE spacecraft_name=? AND journey_id=?"
  private val SOLUTION_READ_JOURNEY =
    "SELECT * FROM spacecraft_journey_catalog WHERE spacecraft_name=? AND journey_id=?"

  createSchema(cqlSession)

  /**
    * Create the statement to create a new journey.
    *
    * Tips:
    *  - For now we focus on creation, launching the spacecraft will come later
    *  - A journey is active only after launch
    *  - JourneyId is a TimeUUID not a default UUID
    *
    * Check result with
    * select journey_id, spacecraft_name,summary,start,end,active from killrvideo.spacecraft_journey_catalog;
    */
  def create(spacecraft: String, journeySummary: String): UUID = {
    val journeyId = Uuids.timeBased
    cqlSession.execute(
      SimpleStatement
        .builder(SOLUTION_INSERT)
        .addPositionalValue(spacecraft)
        .addPositionalValue(journeyId)
        .addPositionalValue(false)
        .addPositionalValue(journeySummary)
        .build
    )
    LOGGER.info("Journey with id '{}' has been inserted in DB", journeyId)
    journeyId
  }

  /**
    * Create the statement to takeoff the spacecraft.
    *
    * Tips:
    *  - We need the full primnary key in where clause (journey, spacecraft)
    *  - We need to update both the active and start date
    *
    * Check result with
    * select journey_id, spacecraft_name,summary,start,end,active from killrvideo.spacecraft_journey_catalog;
    */
  def takeoff(journeyId: UUID, spacecraft: String): ResultSet = {
    cqlSession.execute(
      SimpleStatement
        .builder(SOLUTION_TAKEOFF)
        .addPositionalValue(Instant.now)
        .addPositionalValue(spacecraft)
        .addPositionalValue(journeyId)
        .build
    )
  }

  /**
    * Save a few readings
    *
    * Tips:
    *  - We would like a batch statement (edge computing)
    *  - We need to have all table recording the same reading_time
    *  - Timestamps are Instant in the Java world
    *
    * Check result with
    * select journey_id, spacecraft_name,summary,start,end,active from killrvideo.spacecraft_journey_catalog;
    */
  def log(
      journeyId: UUID,
      spacecraft: String,
      speed: Double,
      pressure: Double,
      temperature: Double,
      x: Double,
      y: Double,
      z: Double,
      readTime: Instant
  ): ResultSet = {
    val bb          = new BatchStatementBuilder(BatchType.LOGGED)
    bb.addStatement(
      SimpleStatement
        .builder(
          "INSERT INTO spacecraft_speed_over_time (" + "spacecraft_name,journey_id," + "speed,reading_time,speed_unit) " + "VALUES (?,?,?,?,?)"
        )
        .addPositionalValue(spacecraft)
        .addPositionalValue(journeyId)
        .addPositionalValue(speed)
        .addPositionalValue(readTime)
        .addPositionalValue("km/hour")
        .build
    )
    bb.addStatement(
      SimpleStatement
        .builder(
          "INSERT INTO spacecraft_temperature_over_time (" + "spacecraft_name,journey_id," + "temperature,reading_time,temperature_unit) " + "VALUES (?,?,?,?,?)"
        )
        .addPositionalValue(spacecraft)
        .addPositionalValue(journeyId)
        .addPositionalValue(temperature)
        .addPositionalValue(readTime)
        .addPositionalValue("K")
        .build
    )
    bb.addStatement(
      SimpleStatement
        .builder(
          "INSERT INTO spacecraft_pressure_over_time (" + "spacecraft_name,journey_id," + "pressure,reading_time,pressure_unit) " + "VALUES (?,?,?,?,?)"
        )
        .addPositionalValue(spacecraft)
        .addPositionalValue(journeyId)
        .addPositionalValue(pressure)
        .addPositionalValue(readTime)
        .addPositionalValue("Pa")
        .build
    )
    // UDT
    val udtlocation = cqlSession
      .getMetadata
      .getKeyspace(cqlSession.getKeyspace.get)
      .flatMap((ks) => ks.getUserDefinedType("location_udt"))
      .orElseThrow(() => new IllegalArgumentException("Missing UDT 'location_udt'"))
    val location    = udtlocation.newValue
    location.setDouble("x_coordinate", x)
    location.setDouble("y_coordinate", y)
    location.setDouble("z_coordinate", z)
    bb.addStatement(
      SimpleStatement
        .builder(
          "INSERT INTO spacecraft_location_over_time (" + "spacecraft_name,journey_id," + "location,reading_time,location_unit) " + "VALUES (?,?,?,?,?)"
        )
        .addPositionalValue(spacecraft)
        .addPositionalValue(journeyId)
        .addPositionalValue(location)
        .addPositionalValue(readTime)
        .addPositionalValue("AU")
        .build
    )
    cqlSession.execute(bb.build)
  }

  def landing(journeyId: UUID, spacecraft: String): ResultSet = {
    cqlSession.execute(
      SimpleStatement
        .builder(SOLUTION_LANDING)
        .addPositionalValue(Instant.now)
        .addPositionalValue(spacecraft)
        .addPositionalValue(journeyId)
        .build
    )
  }

  def delete(journeyId: UUID, spacecraft: String): ResultSet = {
    val bb = new BatchStatementBuilder(BatchType.LOGGED)
    // Delete the journey
    bb.addStatements(
      SimpleStatement
        .builder("DELETE FROM spacecraft_journey_catalog " + "WHERE spacecraft_name=? AND journey_id=?")
        .addPositionalValue(spacecraft)
        .addPositionalValue(journeyId)
        .build
    )
    // Delete all relevant metrics per partition
    bb.addStatement(
      SimpleStatement
        .builder("DELETE FROM spacecraft_speed_over_time (" + "WHERE spacecraft_name=? AND journey_id=?")
        .addPositionalValue(spacecraft)
        .addPositionalValue(journeyId)
        .build
    )
    bb.addStatement(
      SimpleStatement
        .builder("DELETE FROM spacecraft_location_over_time (" + "WHERE spacecraft_name=? AND journey_id=?")
        .addPositionalValue(spacecraft)
        .addPositionalValue(journeyId)
        .build
    )
    bb.addStatement(
      SimpleStatement
        .builder("DELETE FROM spacecraft_pressure_over_time (" + "WHERE spacecraft_name=? AND journey_id=?")
        .addPositionalValue(spacecraft)
        .addPositionalValue(journeyId)
        .build
    )
    bb.addStatement(
      SimpleStatement
        .builder("DELETE FROM spacecraft_temperature_over_time (" + "WHERE spacecraft_name=? AND journey_id=?")
        .addPositionalValue(spacecraft)
        .addPositionalValue(journeyId)
        .build
    )
    cqlSession.execute(bb.build)
  }

  def find(journeyId: UUID, spacecraft: String): Option[Journey] = {
    val rs = cqlSession.execute(
      SimpleStatement
        .builder(SOLUTION_READ_JOURNEY)
        .addPositionalValue(spacecraft)
        .addPositionalValue(journeyId)
        .build
    )

    Option(rs.one()).map(Journey.apply)
  }
}
