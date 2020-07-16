package com.datastax.workshop.scala

import com.datastax.oss.driver.api.core.`type`.DataTypes._
import com.datastax.oss.driver.api.querybuilder.SchemaBuilder._

import com.datastax.oss.driver.api.core.CqlSession
import com.datastax.oss.driver.api.core.cql.SimpleStatement
import com.datastax.oss.driver.api.core.metadata.schema.ClusteringOrder
import com.datastax.oss.driver.api.querybuilder.QueryBuilder

/**
  * A good practice is to group all constants related to the Cassandra
  * data model to an interface to be used in classes.
  *
  * -> Removing a column will you a compilatinon error
  * -> Renaming a column at a single place
  * -> Easy to be used with QueryBuilder
  *
  * @author Cedrick LUNVEN (@clunven)
  */
object DataModelConstants {

  /** Constants for table todo_tasks */
  val TABLE_JOURNEY           = "spacecraft_journey_catalog"
  val JOURNEY_SPACECRAFT_NAME = "spacecraft_name"
  val JOURNEY_ID              = "journey_id"
  val JOURNEY_START           = "start"
  val JOURNEY_END             = "end"
  val JOURNEY_ACTIVE          = "active"
  val JOURNEY_SUMMARY         = "summary"

  /**
    *  CREATE TABLE IF NOT EXISTS spacecraft_journey_catalog (
    *    spacecraft_name text,
    *    journey_id      timeuuid,
    *    start           timestamp,
    *    end             timestamp,
    *    active          boolean,
    *    summary         text,
    *    PRIMARY KEY ((spacecraft_name), journey_id))
    *  WITH CLUSTERING ORDER BY (journey_id desc)
    */
  val STMT_CREATE_TABLE_JOURNEY: SimpleStatement =
    createTable(TABLE_JOURNEY)
    .ifNotExists()
    .withPartitionKey(JOURNEY_SPACECRAFT_NAME, TEXT)
    .withClusteringColumn(JOURNEY_ID, TIMEUUID)
    .withColumn(JOURNEY_START, TIMESTAMP)
    .withColumn(JOURNEY_END, TIMESTAMP)
    .withColumn(JOURNEY_ACTIVE, BOOLEAN)
    .withColumn(JOURNEY_SUMMARY, TEXT)
    .withClusteringOrder(JOURNEY_ID, ClusteringOrder.DESC)
    .build()

  /**
    * - SPEED -
    *
    *  CREATE TABLE IF NOT EXISTS spacecraft_speed_over_time (
    *     spacecraft_name text,
    *     journey_id timeuuid,
    *     speed double,
    *     reading_time timestamp,
    *     speed_unit text,
    *     PRIMARY KEY ((spacecraft_name, journey_id), reading_time)
    * ) WITH CLUSTERING ORDER BY (reading_time DESC)
    */
  val TABLE_METRIC_SPEED                              = "spacecraft_speed_over_time"
  val SPEED_SPACECRAFT_NAME                           = "spacecraft_name"
  val SPEED_JOURNEY_ID                                = "journey_id"
  val SPEED_SPEED                                     = "speed"
  val SPEED_TIME                                      = "reading_time"
  val SPEED_UNIT                                      = "speed_unit"
  val STMT_CREATE_TABLE_METRIC_SPEED: SimpleStatement = createTable(TABLE_METRIC_SPEED)
    .ifNotExists()
    .withPartitionKey(SPEED_SPACECRAFT_NAME, TEXT)
    .withPartitionKey(SPEED_JOURNEY_ID, TIMEUUID)
    .withClusteringColumn(SPEED_TIME, TIMESTAMP)
    .withColumn(SPEED_SPEED, DOUBLE)
    .withColumn(SPEED_UNIT, TEXT)
    .withClusteringOrder(SPEED_TIME, ClusteringOrder.DESC)
    .build()

  /**
    * - TEMPERATURE -
    *
    * CREATE TABLE IF NOT EXISTS spacecraft_temperature_over_time (
    *   spacecraft_name text,
    *   journey_id timeuuid,
    *   temperature double,
    *   temperature_unit text,
    *   reading_time timestamp,
    *   PRIMARY KEY ((spacecraft_name, journey_id), reading_time)
    * ) WITH CLUSTERING ORDER BY (reading_time DESC)
    */
  val TABLE_METRIC_TEMPERATURE                              = "spacecraft_temperature_over_time"
  val TEMPE_SPACECRAFT_NAME                                 = "spacecraft_name"
  val TEMPE_JOURNEY_ID                                      = "journey_id"
  val TEMPE_VALUE                                           = "temperature"
  val TEMPE_TIME                                            = "reading_time"
  val TEMPE_UNIT                                            = "temperature_unit"
  val STMT_CREATE_TABLE_METRIC_TEMPERATURE: SimpleStatement = createTable(TABLE_METRIC_TEMPERATURE)
    .ifNotExists()
    .withPartitionKey(TEMPE_SPACECRAFT_NAME, TEXT)
    .withPartitionKey(TEMPE_JOURNEY_ID, TIMEUUID)
    .withClusteringColumn(TEMPE_TIME, TIMESTAMP)
    .withColumn(TEMPE_VALUE, DOUBLE)
    .withColumn(TEMPE_UNIT, TEXT)
    .withClusteringOrder(TEMPE_TIME, ClusteringOrder.DESC)
    .build()

  /**
    * - PRESSURE -
    *
    * CREATE TABLE IF NOT EXISTS spacecraft_pressure_over_time (
    *    spacecraft_name text,
    *    journey_id timeuuid,
    *    pressure double,
    *    pressure_unit text,
    *    reading_time timestamp,
    *    PRIMARY KEY ((spacecraft_name, journey_id), reading_time)
    * ) WITH CLUSTERING ORDER BY (reading_time DESC)
    */
  val TABLE_METRIC_PRESSURE                              = "spacecraft_pressure_over_time"
  val PRESSURE_SPACECRAFT_NAME                           = "spacecraft_name"
  val PRESSURE_JOURNEY_ID                                = "journey_id"
  val PRESSURE_VALUE                                     = "pressure"
  val PRESSURE_TIME                                      = "reading_time"
  val PRESSURE_UNIT                                      = "pressure_unit"
  val STMT_CREATE_TABLE_METRIC_PRESSURE: SimpleStatement = createTable(TABLE_METRIC_PRESSURE)
    .ifNotExists()
    .withPartitionKey(PRESSURE_SPACECRAFT_NAME, TEXT)
    .withPartitionKey(PRESSURE_JOURNEY_ID, TIMEUUID)
    .withClusteringColumn(PRESSURE_TIME, TIMESTAMP)
    .withColumn(PRESSURE_VALUE, DOUBLE)
    .withColumn(PRESSURE_UNIT, TEXT)
    .withClusteringOrder(PRESSURE_TIME, ClusteringOrder.DESC)
    .build()

  /**
    * UDT.
    *
    * CREATE TYPE IF NOT EXISTS location_udt (
    *   x_coordinate double,
    *   y_coordinate double,
    *   z_coordinate double
    * )
    */
  val UDT_LOCATION                              = "location_udt"
  val LOCATION_X                                = "x_coordinate"
  val LOCATION_Y                                = "y_coordinate"
  val LOCATION_Z                                = "z_coordinate"
  val STMT_CREATE_UDT_LOCATION: SimpleStatement = createType(UDT_LOCATION)
    .ifNotExists()
    .withField(LOCATION_X, DOUBLE)
    .withField(LOCATION_Y, DOUBLE)
    .withField(LOCATION_Z, DOUBLE)
    .build()

  /**
    * CREATE TABLE IF NOT EXISTS spacecraft_location_over_time (
    *    spacecraft_name text,
    *    journey_id timeuuid,
    *    location frozen<location_udt>,
    *    location_unit text,
    *    reading_time timestamp,
    *    PRIMARY KEY ((spacecraft_name, journey_id), reading_time)
    * ) WITH CLUSTERING ORDER BY (reading_time DESC)
    */
  val TABLE_METRIC_LOCATION                              = "spacecraft_location_over_time"
  val LOCATION_SPACECRAFT_NAME                           = "spacecraft_name"
  val LOCATION_JOURNEY_ID                                = "journey_id"
  val LOCATION_VALUE                                     = "location"
  val LOCATION_TIME                                      = "reading_time"
  val LOCATION_UNIT                                      = "location_unit"
  val STMT_CREATE_TABLE_METRIC_LOCATION: SimpleStatement = createTable(TABLE_METRIC_LOCATION)
    .ifNotExists()
    .withPartitionKey(LOCATION_SPACECRAFT_NAME, TEXT)
    .withPartitionKey(LOCATION_JOURNEY_ID, TIMEUUID)
    .withClusteringColumn(LOCATION_TIME, TIMESTAMP)
    .withColumn(LOCATION_VALUE, udt(UDT_LOCATION, true))
    .withColumn(LOCATION_UNIT, TEXT)
    .withClusteringOrder(LOCATION_TIME, ClusteringOrder.DESC)
    .build()

  /**
    * Utility class.
    *
    * @param cqlSession
    *      cassandra session
    */
  def createSchema(cqlSession: CqlSession): Unit = {
    cqlSession.execute(STMT_CREATE_TABLE_JOURNEY)
    cqlSession.execute(STMT_CREATE_TABLE_METRIC_SPEED)
    cqlSession.execute(STMT_CREATE_TABLE_METRIC_PRESSURE)
    cqlSession.execute(STMT_CREATE_TABLE_METRIC_TEMPERATURE)
    cqlSession.execute(STMT_CREATE_UDT_LOCATION)
    cqlSession.execute(STMT_CREATE_TABLE_METRIC_LOCATION)
    ()
  }

  def truncateTables(cqlSession: CqlSession): Unit = {
    cqlSession.execute(QueryBuilder.truncate(TABLE_JOURNEY).build())
    cqlSession.execute(QueryBuilder.truncate(TABLE_METRIC_LOCATION).build())
    cqlSession.execute(QueryBuilder.truncate(TABLE_METRIC_PRESSURE).build())
    cqlSession.execute(QueryBuilder.truncate(TABLE_METRIC_SPEED).build())
    cqlSession.execute(QueryBuilder.truncate(TABLE_METRIC_TEMPERATURE).build())
    ()
  }
}
