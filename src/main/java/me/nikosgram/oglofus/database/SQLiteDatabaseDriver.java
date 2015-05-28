package me.nikosgram.oglofus.database;

import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class SQLiteDatabaseDriver implements DatabaseDriver
{
    private Path database;
    private Connection connection = null;

    public SQLiteDatabaseDriver( Path database )
    {
        this.database = database;
    }

    @Override
    public Connection openConnection()
    {
        try
        {
            Class.forName( "org.sqlite.JDBC" );

            if ( Files.notExists( database.getParent() ) )
            {
                try
                {
                    Files.createDirectories( database.getParent() );
                } catch ( Exception e )
                {
                    throw new RuntimeException( e );
                }
            }

            if ( Files.notExists( database ) )
            {
                try
                {
                    Files.createFile( database );
                } catch ( Exception e )
                {
                    throw new RuntimeException( e );
                }
            }

            this.connection = DriverManager.getConnection( String.format( "jdbc:sqlite:%s", database ) );
        } catch ( Exception e )
        {
            throw new RuntimeException( e );
        }
        return null;
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
            return !this.connection.isClosed();
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
        return "SQLite";
    }
}
