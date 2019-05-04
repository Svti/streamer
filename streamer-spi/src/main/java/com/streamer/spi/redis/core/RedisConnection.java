package com.streamer.spi.redis.core;

import java.io.IOException;
import java.sql.Array;
import java.sql.Blob;
import java.sql.CallableStatement;
import java.sql.Clob;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.NClob;
import java.sql.PreparedStatement;
import java.sql.SQLClientInfoException;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;
import java.sql.SQLWarning;
import java.sql.SQLXML;
import java.sql.Savepoint;
import java.sql.Statement;
import java.sql.Struct;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.Executor;

import org.apache.commons.lang3.StringUtils;

public class RedisConnection implements java.sql.Connection {

    public static final String PROPERTY_DB = "database";
    public static final String PROPERTY_PASSWORD = "password";

    private RedisIO io = null;
    private boolean isClosed = true;
    private boolean autoCommit = true;

    public RedisConnection(final RedisIO io, final Properties info) throws SQLException {

        if (io == null) {
            throw new RuntimeException("Null RedisIO handler.");
        }

        this.io = io;
        isClosed = false;

        if (info != null) {

            if (StringUtils.isNotEmpty(info.getProperty(PROPERTY_PASSWORD))) {
                try {
                    RedisCommandProcessor.runCommand(this,
                            RedisCommand.AUTH.toString() + " " + info.getProperty(PROPERTY_PASSWORD));
                } catch (Exception e) {
                    throw new SQLException("Could not authenticate with Redis.", e);
                }
            }

            if (StringUtils.isNotEmpty(info.getProperty(PROPERTY_DB))) {
                try {
                    RedisCommandProcessor.runCommand(this,
                            RedisCommand.SELECT.toString() + " " + info.getProperty(PROPERTY_DB));
                } catch (Exception e) {
                    throw new SQLException("Could not select database with Redis.", e);
                }
            }
        }
    }

    @Override
    public void clearWarnings() throws SQLException {
        throw new SQLFeatureNotSupportedException("clearWarnings");
    }

    /**
     * Issues a QUIT command to Redis server and closes any socket opened. No
     * operations should be done in the connection object after call this
     * method.
     */
    @Override
    public void close() throws SQLException {
        isClosed = true;
        try {
            io.sendRaw(RedisCommand.QUIT.toString() + "\r\n");
            io.close();
        } catch (IOException | RedisResultException e) {
            throw new SQLException(e);
        }
    }

    /**
     * Send a SAVE command to Redis server.
     */
    @Override
    public void commit() throws SQLException {
        checkConnection();
        try {
            io.sendRaw(RedisCommand.SAVE.toString() + "\r\n");
        } catch (IOException | RedisResultException e) {
            throw new SQLException(e);
        }
    }

    @Override
    public Array createArrayOf(final String typeName, final Object[] elements) throws SQLException {
        throw new SQLFeatureNotSupportedException("createArrayOf");
    }

    @Override
    public Blob createBlob() throws SQLException {
        throw new SQLFeatureNotSupportedException("createBlob");
    }

    @Override
    public Clob createClob() throws SQLException {
        throw new SQLFeatureNotSupportedException("createClob");
    }

    @Override
    public NClob createNClob() throws SQLException {
        throw new SQLFeatureNotSupportedException("createNClob");
    }

    @Override
    public SQLXML createSQLXML() throws SQLException {
        throw new SQLFeatureNotSupportedException("createSQLXML");
    }

    @Override
    public Statement createStatement() throws SQLException {
        return new RedisStatement(this);
    }

    @Override
    public Statement createStatement(final int resultSetType, final int resultSetConcurrency) throws SQLException {
        throw new SQLFeatureNotSupportedException("createStatement");
    }

    @Override
    public Statement createStatement(final int resultSetType, final int resultSetConcurrency,
            final int resultSetHoldability) throws SQLException {
        throw new SQLFeatureNotSupportedException("createStatement");
    }

    @Override
    public Struct createStruct(final String typeName, final Object[] attributes) throws SQLException {
        throw new SQLFeatureNotSupportedException("createStruct");
    }

    @Override
    public boolean getAutoCommit() throws SQLException {
        return autoCommit;
    }

    @Override
    public String getCatalog() throws SQLException {
        checkConnection(); // as API spec says throw exception if conn is
                           // closed.
        return null;
    }

    @Override
    public Properties getClientInfo() throws SQLException {
        throw new SQLFeatureNotSupportedException("getClientInfo");
    }

    @Override
    public String getClientInfo(final String name) throws SQLException {
        throw new SQLFeatureNotSupportedException("getClientInfo");
    }

    @Override
    public int getHoldability() throws SQLException {
        throw new SQLFeatureNotSupportedException("getHoldability");
    }

    @Override
    public DatabaseMetaData getMetaData() throws SQLException {
        return new RedisDatabaseMetaData();
    }

    @Override
    public int getTransactionIsolation() throws SQLException {
        return Connection.TRANSACTION_NONE;
    }

    @Override
    public Map<String, Class<?>> getTypeMap() throws SQLException {
        throw new SQLFeatureNotSupportedException("getTypeMap");
    }

    @Override
    public SQLWarning getWarnings() throws SQLException {
        return null;
    }

    @Override
    public boolean isClosed() throws SQLException {
        return isClosed;
    }

    @Override
    public boolean isReadOnly() throws SQLException {
        return false;
    }

    @Override
    public boolean isValid(final int timeout) throws SQLException {
        return isClosed;
    }

    @Override
    public String nativeSQL(final String sql) throws SQLException {
        throw new SQLFeatureNotSupportedException("nativeSQL");
    }

    @Override
    public CallableStatement prepareCall(final String sql) throws SQLException {
        return prepareCall(sql, 0, 0, 0);
    }

    @Override
    public CallableStatement prepareCall(final String sql, final int resultSetType, final int resultSetConcurrency)
            throws SQLException {
        return prepareCall(sql, 0, 0, 0);
    }

    @Override
    public CallableStatement prepareCall(String sql, int resultSetType, int resultSetConcurrency,
            int resultSetHoldability) throws SQLException {
        throw new SQLFeatureNotSupportedException("prepareCall");
    }

    @Override
    public PreparedStatement prepareStatement(final String sql) throws SQLException {
        return new RedisPreparedStatement(sql, this);
    }

    @Override
    public PreparedStatement prepareStatement(final String sql, final int autoGeneratedKeys) throws SQLException {
        throw new SQLFeatureNotSupportedException("prepareStatement");
    }

    @Override
    public PreparedStatement prepareStatement(final String sql, final int[] columnIndexes) throws SQLException {
        throw new SQLFeatureNotSupportedException("prepareStatement");
    }

    @Override
    public PreparedStatement prepareStatement(final String sql, final String[] columnNames) throws SQLException {
        throw new SQLFeatureNotSupportedException("prepareStatement");
    }

    @Override
    public PreparedStatement prepareStatement(final String sql, final int resultSetType, final int resultSetConcurrency)
            throws SQLException {
        throw new SQLFeatureNotSupportedException("prepareStatement");
    }

    @Override
    public PreparedStatement prepareStatement(final String sql, final int resultSetType, final int resultSetConcurrency,
            final int resultSetHoldability) throws SQLException {
        throw new SQLFeatureNotSupportedException("prepareStatement");
    }

    @Override
    public void releaseSavepoint(final Savepoint savepoint) throws SQLException {
        throw new SQLFeatureNotSupportedException("releaseSavepoin");
    }

    @Override
    public void rollback() throws SQLException {
        rollback(null);
    }

    @Override
    public void rollback(final Savepoint savepoint) throws SQLException {
        throw new SQLFeatureNotSupportedException("rollback");
    }

    @Override
    public void setAutoCommit(final boolean autoCommit) throws SQLException {
        this.autoCommit = autoCommit;
    }

    @Override
    public void setCatalog(final String catalog) throws SQLException {
        throw new SQLFeatureNotSupportedException("setCatalog");
    }

    @Override
    public void setClientInfo(final Properties properties) throws SQLClientInfoException {
    }

    @Override
    public void setClientInfo(final String name, final String value) throws SQLClientInfoException {
    }

    @Override
    public void setHoldability(final int holdability) throws SQLException {
        throw new SQLFeatureNotSupportedException("setHoldability");
    }

    @Override
    public void setReadOnly(final boolean readOnly) throws SQLException {
        throw new SQLFeatureNotSupportedException("setReadOnly");
    }

    @Override
    public Savepoint setSavepoint() throws SQLException {
        throw new SQLFeatureNotSupportedException("setSavepoint");
    }

    @Override
    public Savepoint setSavepoint(final String name) throws SQLException {
        throw new SQLFeatureNotSupportedException("setSavepoint");
    }

    @Override
    public void setTransactionIsolation(final int level) throws SQLException {
        throw new SQLFeatureNotSupportedException("setSavepoint");
    }

    @Override
    public void setTypeMap(final Map<String, Class<?>> map) throws SQLException {
    }

    @Override
    public void setSchema(final String schema) throws SQLException {
        throw new SQLFeatureNotSupportedException("setSchema");
    }

    @Override
    public String getSchema() throws SQLException {
        throw new SQLFeatureNotSupportedException("getSchema");
    }

    @Override
    public void abort(final Executor executor) throws SQLException {
        throw new SQLFeatureNotSupportedException("abort");
    }

    @Override
    public void setNetworkTimeout(final Executor executor, final int milliseconds) throws SQLException {
        throw new SQLFeatureNotSupportedException("setNetworkTimeout");
    }

    @Override
    public int getNetworkTimeout() throws SQLException {
        throw new SQLFeatureNotSupportedException("getNetworkTimeout");
    }

    @Override
    public boolean isWrapperFor(Class<?> arg0) throws SQLException {
        throw new SQLFeatureNotSupportedException("isWrapperFor");
    }

    @Override
    public <T> T unwrap(Class<T> arg0) throws SQLException {
        throw new SQLFeatureNotSupportedException("unwrap");
    }

    protected Object msgToServer(String redisMsg) throws SQLException {
        checkConnection();
        try {
            return io.sendRaw(redisMsg);
        } catch (IOException | RedisResultException e) {
            isClosed = true;
            throw new SQLException(e.getMessage());
        }
    }

    private void checkConnection() throws SQLException {
        if (isClosed()) {
            throw new SQLException("Connection with Redis is closed");
        }
    }

}
