package at.haha007.edenlib.database;

import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class MySqlDatabase extends SqlDatabase {


	private final String host, username, password, database;
	private final boolean useSSL;

	public MySqlDatabase(String host, String username, String password, String database, boolean useSSL) {
		this.host = host;
		this.username = username;
		this.password = password;
		this.database = database;
		this.useSSL = useSSL;
	}

	@Override
	public PreparedStatement prepareStatement(String statement) throws SQLException {
		return connection.prepareStatement(statement);
	}

	@Override
	public void connect() throws SQLException {
		try {
			Class.forName("com.mysql.jdbc.Driver").newInstance();
		} catch (InstantiationException | IllegalAccessException | ClassNotFoundException e) {
			e.printStackTrace();
		}
		connection = DriverManager.getConnection(String.format("jdbc:mysql://%s/%s?user=%s&password=%s&useSSL=%b&autoReconnect=yes", host, database, username, password, useSSL));
	}

}
