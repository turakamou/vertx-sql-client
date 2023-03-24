/*
 * Copyright (c) 2011-2021 Contributors to the Eclipse Foundation
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0, or the Apache License, Version 2.0
 * which is available at https://www.apache.org/licenses/LICENSE-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0 OR Apache-2.0
 */
package io.vertx.oracleclient.impl.commands;

import io.vertx.core.Context;
import io.vertx.core.Future;
import io.vertx.core.impl.ContextInternal;
import io.vertx.core.json.JsonArray;
import io.vertx.oracleclient.OraclePrepareOptions;
import io.vertx.sqlclient.impl.PreparedStatement;
import oracle.jdbc.OracleConnection;

import java.sql.SQLException;
import java.sql.Statement;

import static io.vertx.oracleclient.impl.Helper.executeBlocking;

public class PrepareStatementCommand extends AbstractCommand<PreparedStatement> {

  private final OraclePrepareOptions options;
  private final String sql;

  public PrepareStatementCommand(OraclePrepareOptions options, String sql) {
    this.options = options;
    this.sql = sql;
  }

  @Override
  public Future<PreparedStatement> execute(OracleConnection conn, ContextInternal context) {
    boolean autoGeneratedKeys = options == null || options.isAutoGeneratedKeys();
    boolean autoGeneratedIndexes = options != null && options.getAutoGeneratedKeysIndexes() != null;

    if (autoGeneratedKeys && !autoGeneratedIndexes) {
      return prepareReturningKey(conn, context);
    } else if (autoGeneratedIndexes) {
      return prepareWithAutoGeneratedIndexes(conn, context);
    } else {
      return prepare(conn, context);
    }
  }

  private Future<PreparedStatement> prepareWithAutoGeneratedIndexes(OracleConnection conn, Context context) {
    return executeBlocking(context, () -> {
      // convert json array to int or string array
      JsonArray indexes = options.getAutoGeneratedKeysIndexes();
      if (indexes.getValue(0) instanceof Number) {
        int[] keys = new int[indexes.size()];
        for (int i = 0; i < keys.length; i++) {
          keys[i] = indexes.getInteger(i);
        }
        try (java.sql.PreparedStatement statement = conn.prepareStatement(sql, keys)) {
          return new OraclePreparedStatement(sql, statement);
        }
      }
      if (indexes.getValue(0) instanceof String) {
        String[] keys = new String[indexes.size()];
        for (int i = 0; i < keys.length; i++) {
          keys[i] = indexes.getString(i);
        }
        try (java.sql.PreparedStatement statement = conn.prepareStatement(sql, keys)) {
          return new OraclePreparedStatement(sql, statement);
        }
      }
      throw new SQLException("Invalid type of index, only [int, String] allowed");
    });
  }

  private Future<PreparedStatement> prepareReturningKey(OracleConnection connection, ContextInternal context) {
    return executeBlocking(context, () -> {
      try (java.sql.PreparedStatement statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
        return new OraclePreparedStatement(sql, statement);
      }
    });
  }

  private Future<PreparedStatement> prepare(OracleConnection connection, Context context) {
    return executeBlocking(context, () -> {
      try (java.sql.PreparedStatement statement = connection.prepareStatement(sql)) {
        return new OraclePreparedStatement(sql, statement);
      }
    });
  }
}
