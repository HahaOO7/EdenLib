package at.haha007.edenlib.database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

abstract public class SqlDatabase {
	protected Connection connection;

	 public PreparedStatement prepareStatement(String statement) throws SQLException {
	 	return connection.prepareStatement(statement);
	 }

	abstract public void connect() throws SQLException;

	public void disconnect() throws SQLException {
		if (isConnected()) connection.close();
	}

	public boolean isConnected() throws SQLException {
		return connection != null && !connection.isClosed();
	}

	public Connection getConnection() {
		return connection;
	}
}
