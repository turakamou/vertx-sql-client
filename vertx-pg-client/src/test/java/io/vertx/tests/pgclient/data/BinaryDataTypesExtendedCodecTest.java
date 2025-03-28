package io.vertx.tests.pgclient.data;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

import io.vertx.core.internal.buffer.BufferInternal;
import io.vertx.pgclient.PgConnection;
import io.vertx.tests.sqlclient.ColumnChecker;
import io.vertx.sqlclient.Row;
import io.vertx.sqlclient.Tuple;
import io.vertx.core.buffer.Buffer;
import io.vertx.ext.unit.Async;
import io.vertx.ext.unit.TestContext;
import org.junit.Test;

import java.util.Random;

public class BinaryDataTypesExtendedCodecTest extends ExtendedQueryDataTypeCodecTestBase {
  @Test
  public void testBytea(TestContext ctx) {
    Random r = new Random();
    int len = 2048;
    byte[] bytes = new byte[len];
    r.nextBytes(bytes);
    Async async = ctx.async();
    PgConnection.connect(vertx, options).onComplete(ctx.asyncAssertSuccess(conn -> {
      conn.prepare("SELECT $1::BYTEA \"Bytea\"").onComplete(
        ctx.asyncAssertSuccess(p -> {
          p.query()
            .execute(Tuple.of(Buffer.buffer(bytes)))
            .onComplete(ctx.asyncAssertSuccess(result -> {
            ColumnChecker.checkColumn(0, "Bytea")
              .returns(Tuple::getValue, Row::getValue, Buffer.buffer(bytes))
              .<Buffer>returns(Tuple::getBuffer, Row::getBuffer, buffer -> {
                assertFalse(((BufferInternal)buffer).getByteBuf().isDirect());
                assertEquals(Buffer.buffer(bytes), buffer);
              })
              .forRow(result.iterator().next());
            async.complete();
          }));
        }));
    }));
  }

  @Test
  public void testBufferArray(TestContext ctx) {
    Random r = new Random();
    int len = 2048;
    byte[] bytes = new byte[len];
    r.nextBytes(bytes);
    Async async = ctx.async();
    PgConnection.connect(vertx, options).onComplete(ctx.asyncAssertSuccess(conn -> {
      conn.prepare("SELECT ARRAY[$1::BYTEA] \"Bytea\"").onComplete(
        ctx.asyncAssertSuccess(p -> {
          p.query().execute(Tuple.of(Buffer.buffer(bytes))).onComplete(ctx.asyncAssertSuccess(result -> {
            ColumnChecker.checkColumn(0, "Bytea")
              .returns(Tuple::getValue, Row::getValue, new Buffer[]{Buffer.buffer(bytes)})
              .returns(Tuple::getArrayOfBuffers, Row::getArrayOfBuffers, new Buffer[]{Buffer.buffer(bytes)})
              .forRow(result.iterator().next());
            async.complete();
          }));
        }));
    }));
  }
}
