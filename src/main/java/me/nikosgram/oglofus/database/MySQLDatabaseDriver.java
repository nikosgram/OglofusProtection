package me.nikosgram.oglofus.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class MySQLDatabaseDriver implements DatabaseDriver
{
    private String  username;
    private String  database;
    private String  password;
    private String  hostname;
    private Integer port;
    private Connection connection = null;

    public MySQLDatabaseDriver( String username, String database, String password, String hostname, Integer port )
    {
        this.username = username;
        this.database = database;
        this.password = password;
        this.hostname = hostname;
        this.port = port;
    }

    @Override
    public Connection openConnection()
    {
        try
        {
            Class.forName( "com.mysql.jdbc.Driver" );
            this.connection = DriverManager.getConnection(
                    "jdbc:mysql://" +
                            this.hostname +
                            ":" +
                            this.port +
                            "/" +
                            this.database, this.username, this.password
            );
        } catch ( Exception e )
        {
            throw new RuntimeException( e );
        }
        return this.connection;
    }

    @Override
    public Boolean checkConnection()
    {
        if ( connection == null )
        {
            return false;
        }
        try
        {
            return !connection.isClosed();
        } catch ( SQLException e )
        {
            throw new RuntimeException( e );
        }
    }

    @Override
    public void closeConnection()
    {
        if ( checkConnection() )
        {
            try
            {
                this.connection.close();
            } catch ( Exception e )
            {
                throw new RuntimeException( e );
            }
        }
    }

    @Override
    public Connection getConnection()
    {
        return connection;
    }

    @Override
    public String name()
    {
        return "MySQL";
    }
}
