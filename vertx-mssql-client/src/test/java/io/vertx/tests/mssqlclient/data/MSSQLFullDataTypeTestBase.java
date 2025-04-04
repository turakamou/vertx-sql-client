/*
 * Copyright (c) 2011-2022 Contributors to the Eclipse Foundation
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0, or the Apache License, Version 2.0
 * which is available at https://www.apache.org/licenses/LICENSE-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0 OR Apache-2.0
 */

package io.vertx.tests.mssqlclient.data;

import io.vertx.core.buffer.Buffer;
import io.vertx.ext.unit.TestContext;
import io.vertx.tests.sqlclient.ColumnChecker;
import io.vertx.sqlclient.Row;
import io.vertx.sqlclient.Tuple;
import org.junit.Test;

import java.math.BigDecimal;
import java.time.*;
import java.util.UUID;
import java.util.function.Consumer;

public abstract class MSSQLFullDataTypeTestBase extends MSSQLDataTypeTestBase {

  @Test
  public void testDecodeAllColumns(TestContext ctx) {
    testDecodeNotNullValue(ctx, "*", row -> {
      ctx.assertEquals((short) 127, row.getValue("test_tinyint"));
      ctx.assertEquals((short) 32767, row.getValue("test_smallint"));
      ctx.assertEquals(2147483647, row.getValue("test_int"));
      ctx.assertEquals(9223372036854775807L, row.getValue("test_bigint"));
      ctx.assertEquals((float) 3.40282E38, row.getValue("test_float_4"));
      ctx.assertEquals(1.7976931348623157E308, row.getValue("test_float_8"));
      ctx.assertEquals(new BigDecimal("999.99"), row.getValue("test_numeric"));
      ctx.assertEquals(new BigDecimal("12345"), row.getValue("test_decimal"));
      ctx.assertEquals(true, row.getValue("test_boolean"));
      ctx.assertEquals("testchar", row.getValue("test_char"));
      ctx.assertEquals("testvarchar", row.getValue("test_varchar"));
      ctx.assertEquals("testvarcharmax", row.getValue("test_varchar_max"));
      ctx.assertEquals("testtext", row.getValue("test_text"));
      ctx.assertEquals("testntext", row.getValue("test_ntext"));
      ctx.assertEquals(LocalDate.of(2019, 1, 1), row.getValue("test_date"));
      ctx.assertEquals(LocalTime.of(18, 45, 2), row.getValue("test_time"));
      ctx.assertEquals(LocalDateTime.of(2019, 1, 1, 18, 45, 0), row.getValue("test_smalldatetime"));
      ctx.assertEquals(LocalDateTime.of(2019, 1, 1, 18, 45, 2), row.getValue("test_datetime"));
      ctx.assertEquals(LocalDateTime.of(2019, 1, 1, 18, 45, 2), row.getValue("test_datetime2"));
      ctx.assertEquals(LocalDateTime.of(2019, 1, 1, 18, 45, 2).atOffset(ZoneOffset.ofHoursMinutes(-3, -15)), row.getValue("test_datetimeoffset"));
      ctx.assertEquals(Buffer.buffer("hello world").appendBytes(new byte[20 - "hello world".length()]), row.getValue("test_binary"));
      ctx.assertEquals(Buffer.buffer("big apple"), row.getValue("test_varbinary"));
      ctx.assertEquals(Buffer.buffer("venice of the north"), row.getValue("test_varbinary_max"));
      ctx.assertEquals(Buffer.buffer("paris of the west"), row.getValue("test_image"));
      ctx.assertEquals(new BigDecimal("12.3456"), row.getValue("test_money"));
      ctx.assertEquals(new BigDecimal("12.34"), row.getValue("test_smallmoney"));
    });
  }

  @Test
  public void testDecodeTinyInt(TestContext ctx) {
    testDecodeNotNullValue(ctx, "test_tinyint", row -> {
      checkNumber(row, "test_tinyint", (short) 127);
    });
  }

  @Test
  public void testDecodeSmallIntInt(TestContext ctx) {
    testDecodeNotNullValue(ctx, "test_smallint", row -> {
      checkNumber(row, "test_smallint", (short) 32767);
    });
  }

  @Test
  public void testDecodeInt(TestContext ctx) {
    testDecodeNotNullValue(ctx, "test_int", row -> {
      checkNumber(row, "test_int", 2147483647);
    });
  }

  @Test
  public void testDecodeBigInt(TestContext ctx) {
    testDecodeNotNullValue(ctx, "test_bigint", row -> {
      checkNumber(row, "test_bigint", 9223372036854775807L);
    });
  }

  @Test
  public void testDecodeFloat4(TestContext ctx) {
    testDecodeNotNullValue(ctx, "test_float_4", row -> {
      checkNumber(row, "test_float_4", (float) 3.40282E38);
    });
  }

  @Test
  public void testDecodeFloat8(TestContext ctx) {
    testDecodeNotNullValue(ctx, "test_float_8", row -> {
      checkNumber(row, "test_float_8", 1.7976931348623157E308);
    });
  }

  @Test
  public void testDecodeNumeric(TestContext ctx) {
    testDecodeNotNullValue(ctx, "test_numeric", row -> {
      checkNumber(row, "test_numeric", new BigDecimal("999.99"));
    });
  }


  @Test
  public void testDecodeUuid(TestContext ctx) {
    testDecodeNotNullValue(ctx, "test_uuid", row -> {
      ColumnChecker.checkColumn(0, "test_uuid")
        .returns(Tuple::getValue, Row::getValue, UUID.fromString("e2d1f163-40a7-480b-b1a6-07faaef8e01b"))
        .returns(Tuple::getUUID, Row::getUUID, UUID.fromString("e2d1f163-40a7-480b-b1a6-07faaef8e01b"))
        .forRow(row);
    });
  }

  @Test
  public void testDecodeDecimal(TestContext ctx) {
    testDecodeNotNullValue(ctx, "test_decimal", row -> {
      checkNumber(row, "test_decimal", new BigDecimal("12345"));
    });
  }

  @Test
  public void testDecodeBit(TestContext ctx) {
    testDecodeNotNullValue(ctx, "test_boolean", row -> {
      ColumnChecker.checkColumn(0, "test_boolean")
        .returns(Tuple::getValue, Row::getValue, true)
        .returns(Tuple::getBoolean, Row::getBoolean, true)
        .returns(Boolean.class, true)
        .forRow(row);
    });
  }

  @Test
  public void testDecodeChar(TestContext ctx) {
    testDecodeNotNullValue(ctx, "test_char", row -> {
      ColumnChecker.checkColumn(0, "test_char")
        .returns(Tuple::getValue, Row::getValue, "testchar")
        .returns(Tuple::getString, Row::getString, "testchar")
        .returns(String.class, "testchar")
        .forRow(row);
    });
  }

  @Test
  public void testDecodeVarChar(TestContext ctx) {
    testDecodeNotNullValue(ctx, "test_varchar", row -> {
      ColumnChecker.checkColumn(0, "test_varchar")
        .returns(Tuple::getValue, Row::getValue, "testvarchar")
        .returns(Tuple::getString, Row::getString, "testvarchar")
        .returns(String.class, "testvarchar")
        .forRow(row);
    });
  }

  @Test
  public void testDecodeDate(TestContext ctx) {
    testDecodeNotNullValue(ctx, "test_date", row -> {
      ColumnChecker.checkColumn(0, "test_date")
        .returns(Tuple::getValue, Row::getValue, LocalDate.of(2019, 1, 1))
        .returns(Tuple::getLocalDate, Row::getLocalDate, LocalDate.of(2019, 1, 1))
        .returns(LocalDate.class, LocalDate.of(2019, 1, 1))
        .forRow(row);
    });
  }

  @Test
  public void testDecodeTime(TestContext ctx) {
    testDecodeNotNullValue(ctx, "test_time", row -> {
      ColumnChecker.checkColumn(0, "test_time")
        .returns(Tuple::getValue, Row::getValue, LocalTime.of(18, 45, 2))
        .returns(Tuple::getLocalTime, Row::getLocalTime, LocalTime.of(18, 45, 2))
        .returns(LocalTime.class, LocalTime.of(18, 45, 2))
        .forRow(row);
    });
  }

  @Test
  public void testDecodeSmallDateTime(TestContext ctx) {
    testDecodeNotNullValue(ctx, "test_smalldatetime", row -> {
      ColumnChecker.checkColumn(0, "test_smalldatetime")
        .returns(Tuple::getValue, Row::getValue, LocalDateTime.of(2019, 1, 1, 18, 45, 0))
        .returns(Tuple::getLocalDateTime, Row::getLocalDateTime, LocalDateTime.of(2019, 1, 1, 18, 45, 0))
        .returns(Tuple::getLocalDate, Row::getLocalDate, LocalDate.of(2019, 1, 1))
        .returns(Tuple::getLocalTime, Row::getLocalTime, LocalTime.of(18, 45, 0))
        .returns(LocalDateTime.class, LocalDateTime.of(2019, 1, 1, 18, 45, 0))
        .forRow(row);
    });
  }

  @Test
  public void testDecodeDateTime(TestContext ctx) {
    testDecodeNotNullValue(ctx, "test_datetime", row -> {
      ColumnChecker.checkColumn(0, "test_datetime")
        .returns(Tuple::getValue, Row::getValue, LocalDateTime.of(2019, 1, 1, 18, 45, 2))
        .returns(Tuple::getLocalDateTime, Row::getLocalDateTime, LocalDateTime.of(2019, 1, 1, 18, 45, 2))
        .returns(Tuple::getLocalDate, Row::getLocalDate, LocalDate.of(2019, 1, 1))
        .returns(Tuple::getLocalTime, Row::getLocalTime, LocalTime.of(18, 45, 2))
        .returns(LocalDateTime.class, LocalDateTime.of(2019, 1, 1, 18, 45, 2))
        .forRow(row);
    });
  }

  @Test
  public void testDecodeDateTime2(TestContext ctx) {
    testDecodeNotNullValue(ctx, "test_datetime2", row -> {
      ColumnChecker.checkColumn(0, "test_datetime2")
        .returns(Tuple::getValue, Row::getValue, LocalDateTime.of(2019, 1, 1, 18, 45, 2))
        .returns(Tuple::getLocalDateTime, Row::getLocalDateTime, LocalDateTime.of(2019, 1, 1, 18, 45, 2))
        .returns(Tuple::getLocalDate, Row::getLocalDate, LocalDate.of(2019, 1, 1))
        .returns(Tuple::getLocalTime, Row::getLocalTime, LocalTime.of(18, 45, 2))
        .returns(LocalDateTime.class, LocalDateTime.of(2019, 1, 1, 18, 45, 2))
        .forRow(row);
    });
  }

  @Test
  public void testDecodeOffsetDateTime(TestContext ctx) {
    testDecodeNotNullValue(ctx, "test_datetimeoffset", row -> {
      ColumnChecker.checkColumn(0, "test_datetimeoffset")
        .returns(Tuple::getValue, Row::getValue, LocalDateTime.of(2019, 1, 1, 18, 45, 2).atOffset(ZoneOffset.ofHoursMinutes(-3, -15)))
        .returns(Tuple::getOffsetDateTime, Row::getOffsetDateTime, LocalDateTime.of(2019, 1, 1, 18, 45, 2).atOffset(ZoneOffset.ofHoursMinutes(-3, -15)))
        .returns(Tuple::getLocalDateTime, Row::getLocalDateTime, LocalDateTime.of(2019, 1, 1, 18, 45, 2))
        .returns(Tuple::getLocalDate, Row::getLocalDate, LocalDate.of(2019, 1, 1))
        .returns(Tuple::getLocalTime, Row::getLocalTime, LocalTime.of(18, 45, 2))
        .returns(OffsetDateTime.class, LocalDateTime.of(2019, 1, 1, 18, 45, 2).atOffset(ZoneOffset.ofHoursMinutes(-3, -15)))
        .forRow(row);
    });
  }

  @Test
  public void testDecodeBinary(TestContext ctx) {
    String str = "hello world";
    Buffer expected = Buffer.buffer(str).appendBytes(new byte[20 - str.length()]);
    testDecodeNotNullValue(ctx, "test_binary", row -> {
      ColumnChecker.checkColumn(0, "test_binary")
        .returns(Tuple::getValue, Row::getValue, expected)
        .returns(Tuple::getBuffer, Row::getBuffer, expected)
        .returns(Buffer.class, expected)
        .forRow(row);
    });
  }

  @Test
  public void testDecodeVarBinary(TestContext ctx) {
    testDecodeNotNullValue(ctx, "test_varbinary", row -> {
      ColumnChecker.checkColumn(0, "test_varbinary")
        .returns(Tuple::getValue, Row::getValue, Buffer.buffer("big apple"))
        .returns(Tuple::getBuffer, Row::getBuffer, Buffer.buffer("big apple"))
        .returns(Buffer.class, Buffer.buffer("big apple"))
        .forRow(row);
    });
  }

  @Test
  public void testDecodeMoney(TestContext ctx) {
    testDecodeNotNullValue(ctx, "test_money", row -> {
      checkNumber(row, "test_money", new BigDecimal("12.3456"));
    });
  }

  @Test
  public void testDecodeSmallMoney(TestContext ctx) {
    testDecodeNotNullValue(ctx, "test_smallmoney", row -> {
      checkNumber(row, "test_smallmoney", new BigDecimal("12.34"));
    });
  }

  protected abstract void testDecodeValue(TestContext ctx, boolean isNull, String columnName, Consumer<Row> checker);

  private void testDecodeNotNullValue(TestContext ctx, String columnName, Consumer<Row> checker) {
    testDecodeValue(ctx, false, columnName, checker);
  }

  protected static void checkNumber(Row row, String columnName, Number value) {
    ColumnChecker.checkColumn(0, columnName)
      .returns(Tuple::getValue, Row::getValue, value)
      .returns(Tuple::getShort, Row::getShort, value.shortValue())
      .returns(Tuple::getInteger, Row::getInteger, value.intValue())
      .returns(Tuple::getLong, Row::getLong, value.longValue())
      .returns(Tuple::getFloat, Row::getFloat, value.floatValue())
      .returns(Tuple::getDouble, Row::getDouble, value.doubleValue())
      .returns(Tuple::getBigDecimal, Row::getBigDecimal, new BigDecimal(value.toString()))
      .returns(Byte.class, value.byteValue())
      .returns(BigDecimal.class, new BigDecimal(value.toString()))
      .forRow(row);
  }
}
