package at.haha007.edenlib.database;

import java.sql.DriverManager;
import java.sql.SQLException;

public class MySqlDatabase extends SqlDatabase {


    private final String host, username, password, database, datasource;
    private final boolean useSSL;

    public MySqlDatabase(String host, String username, String password, String database, String datasource, boolean useSSL) {
        this.host = host;
        this.username = username;
        this.password = password;
        this.database = database;
        this.useSSL = useSSL;
        this.datasource = datasource;
    }

    @Override
    public void connect() throws SQLException {
        try {
            Class.forName(datasource);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        connection = DriverManager.getConnection(String.format("jdbc:mysql://%s/%s?user=%s&password=%s&useSSL=%b&autoReconnect=yes", host, database, username, password, useSSL));
    }

}
