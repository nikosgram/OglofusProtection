package me.nikosgram.oglofus.database;

import com.google.common.base.Optional;
import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.EncoderException;
import org.apache.commons.codec.StringDecoder;
import org.apache.commons.codec.StringEncoder;
import org.apache.commons.codec.net.URLCodec;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.*;

public class DatabaseConnector
{
    private static final StringEncoder encoder = new URLCodec();
    private static final StringDecoder decoder = new URLCodec();

    private DatabaseDriver driver = null;

    public DatabaseConnector( DatabaseDriver driver )
    {
        this.driver = driver;
    }

    public static String reformedListToString( List< String > list, Boolean encode )
    {
        List< String > returned = new ArrayList< String >();
        for ( String s : list )
        {
            returned.add( encode ? encodeString( s ) : s.trim() );
        }
        String Returned = "StartAT*" + returned.toString() + "*EndAT";
        return Returned.replaceFirst( "StartAT\\*\\[", "" ).replaceFirst( "\\]\\*EndAT", "" );
    }

    public static String reformedListToString( Collection< String > list, Boolean encode )
    {
        List< String > returned = new ArrayList< String >();
        Collections.addAll( returned, list.toArray( new String[ list.size() ] ) );
        return reformedListToString( returned, encode );
    }


    public static String reformedListToString( String[] list, Boolean encode )
    {
        List< String > returned = new ArrayList< String >();
        Collections.addAll( returned, list );
        return reformedListToString( returned, encode );
    }

    public static String reformedListToString( List< String > list )
    {
        return reformedListToString( list, true );
    }

    public static String reformedListToString( String[] list )
    {
        List< String > StringList = new ArrayList< String >();
        Collections.addAll( StringList, list );
        return reformedListToString( StringList, true );
    }

    public static String reformedListToString( Collection< String > list )
    {
        List< String > returned = new ArrayList< String >();
        Collections.addAll( returned, list.toArray( new String[ list.size() ] ) );
        return reformedListToString( returned, true );
    }

    public static String encodeString( String s )
    {
        s = s.trim();
        if ( ( s.startsWith( "'" ) ) && ( s.endsWith( "'" ) ) )
        {
            try
            {
                return "'" + encoder.encode( s ) + "'";
            } catch ( EncoderException e )
            {
                throw new RuntimeException( e );
            }

        } else
        {
            try
            {
                return "'" + encoder.encode( s ) + "'";
            } catch ( EncoderException e )
            {
                throw new RuntimeException( e );
            }
        }
    }

    public static String decodeString( String s )
    {
        s = s.trim();
        try
        {
            return decoder.decode( s );
        } catch ( DecoderException e )
        {
            throw new RuntimeException( e );
        }
    }

    public static String reformMessage( String Message )
    {
        if ( !Message.endsWith( ";" ) )
        {
            Message = Message + ";";
        }
        return Message;
    }

    public Connection openConnection()
    {
        if ( driver == null )
        {
            return null;
        }
        driver.openConnection();
        if ( checkConnection() )
        {
            return driver.getConnection();
        }
        return null;
    }

    public Boolean checkConnection()
    {
        if ( driver == null )
        {
            return false;
        }
        return driver.checkConnection();
    }

    public void closeConnection()
    {
        if ( driver == null )
        {
            return;
        }
        if ( !checkConnection() )
        {
            return;
        }
        driver.closeConnection();
    }

    public Connection getConnection()
    {
        if ( driver == null )
        {
            return null;
        }
        return driver.getConnection();
    }

    public String name()
    {
        if ( driver == null )
        {
            return "";
        }
        return driver.name();
    }

    public void execute( String message )
    {
        if ( checkConnection() )
        {
            Statement statement = getStatement();
            try
            {
                statement.execute( reformMessage( message ) );
            } catch ( SQLException e )
            {
                throw new RuntimeException( e );
            } finally
            {
                closeStatement( statement );
            }
        }
    }

    public Optional< Object > getObject( String message, String wanted )
    {
        Object returned = null;
        if ( checkConnection() )
        {
            ResultSet result = getResultSet( reformMessage( message ) );
            try
            {
                if ( result.next() )
                {
                    try
                    {
                        returned = result.getObject( wanted );
                    } catch ( SQLException ignored ) {}
                }
            } catch ( SQLException e )
            {
                throw new RuntimeException( e );
            } finally
            {
                closeResultSet( result );
            }
        }
        return Optional.fromNullable( returned );
    }

    public Optional< String > getString( String message, String wanted )
    {
        String returned = null;
        if ( checkConnection() )
        {
            ResultSet result = getResultSet( reformMessage( message ) );
            try
            {
                if ( result.next() )
                {
                    try
                    {
                        returned = result.getString( wanted );
                    } catch ( SQLException ignored ) {}
                }
            } catch ( SQLException e )
            {
                throw new RuntimeException( e );
            } finally
            {
                closeResultSet( result );
            }
        }
        return Optional.fromNullable( returned );
    }

    public Optional< Integer > getInteger( String message, String wanted )
    {
        Integer returned = null;
        if ( checkConnection() )
        {
            ResultSet result = getResultSet( reformMessage( message ) );
            try
            {
                if ( result.next() )
                {
                    try
                    {
                        returned = result.getInt( wanted );
                    } catch ( SQLException ignored ) {}
                }
            } catch ( SQLException e )
            {
                throw new RuntimeException( e );
            } finally
            {
                closeResultSet( result );
            }
        }
        return Optional.fromNullable( returned );
    }

    public Optional< Boolean > getBoolean( String message, String wanted )
    {
        Boolean returned = null;
        if ( checkConnection() )
        {
            ResultSet result = getResultSet( reformMessage( message ) );
            try
            {
                if ( result.next() )
                {
                    try
                    {
                        returned = result.getBoolean( wanted );
                    } catch ( SQLException ignored ) {}
                }
            } catch ( SQLException e )
            {
                throw new RuntimeException( e );
            } finally
            {
                closeResultSet( result );
            }
        }
        return Optional.fromNullable( returned );
    }

    public Boolean exists( String message )
    {
        Boolean returned = false;
        if ( checkConnection() )
        {
            ResultSet result = getResultSet( reformMessage( message ) );
            try
            {
                returned = result.next();
            } catch ( SQLException e )
            {
                throw new RuntimeException( e );
            } finally
            {
                closeResultSet( result );
            }
        }
        return returned;
    }

    public List< Object > getObjectList( String message, String wanted )
    {
        List< Object > objects = new ArrayList< Object >();
        if ( checkConnection() )
        {
            ResultSet result = getResultSet( reformMessage( message ) );
            try
            {
                while ( result.next() )
                {
                    try
                    {
                        objects.add( result.getObject( wanted ) );
                    } catch ( SQLException ignored ) {}
                }
            } catch ( SQLException e )
            {
                throw new RuntimeException( e );
            } finally
            {
                closeResultSet( result );
            }
        }
        return objects;
    }

    public List< String > getStringList( String message, String wanted )
    {
        List< String > objects = new ArrayList< String >();
        if ( checkConnection() )
        {
            ResultSet result = getResultSet( reformMessage( message ) );
            try
            {
                while ( result.next() )
                {
                    try
                    {
                        objects.add( result.getString( wanted ) );
                    } catch ( SQLException ignored ) {}
                }
            } catch ( SQLException e )
            {
                throw new RuntimeException( e );
            } finally
            {
                closeResultSet( result );
            }
        }
        return objects;
    }

    public List< Integer > getIntegerList( String message, String wanted )
    {
        List< Integer > objects = new ArrayList< Integer >();
        if ( checkConnection() )
        {
            ResultSet result = getResultSet( reformMessage( message ) );
            try
            {
                while ( result.next() )
                {
                    try
                    {
                        objects.add( result.getInt( wanted ) );
                    } catch ( SQLException ignored ) {}
                }
            } catch ( SQLException e )
            {
                throw new RuntimeException( e );
            } finally
            {
                closeResultSet( result );
            }
        }
        return objects;
    }

    public List< Boolean > getBooleanList( String message, String wanted )
    {
        List< Boolean > objects = new ArrayList< Boolean >();
        if ( checkConnection() )
        {
            ResultSet result = getResultSet( reformMessage( message ) );
            try
            {
                while ( result.next() )
                {
                    try
                    {
                        objects.add( result.getBoolean( wanted ) );
                    } catch ( SQLException ignored ) {}
                }
            } catch ( SQLException e )
            {
                throw new RuntimeException( e );
            } finally
            {
                closeResultSet( result );
            }
        }
        return objects;
    }

    public Map< String, Object > getObjectMap( String message, String[] wanted )
    {
        Map< String, Object > returned = new HashMap< String, Object >();
        if ( checkConnection() )
        {
            ResultSet result = getResultSet( reformMessage( message ) );
            try
            {
                while ( result.next() )
                {
                    for ( String want : wanted )
                    {
                        try
                        {
                            returned.put( want, result.getObject( want ) );
                        } catch ( SQLException ignored ) {}
                    }
                }
            } catch ( SQLException e )
            {
                throw new RuntimeException( e );
            } finally { closeResultSet( result ); }
        }
        return returned;
    }

    public Map< String, String > getStringMap( String message, String[] wanted )
    {
        Map< String, String > returned = new HashMap< String, String >();
        if ( checkConnection() )
        {
            ResultSet result = getResultSet( reformMessage( message ) );
            try
            {
                while ( result.next() )
                {
                    for ( String want : wanted )
                    {
                        try
                        {
                            returned.put( want, result.getString( want ) );
                        } catch ( SQLException ignored ) {}
                    }
                }
            } catch ( SQLException e )
            {
                throw new RuntimeException( e );
            } finally { closeResultSet( result ); }
        }
        return returned;
    }

    public Map< String, Integer > getIntegerMap( String message, String[] wanted )
    {
        Map< String, Integer > returned = new HashMap< String, Integer >();
        if ( checkConnection() )
        {
            ResultSet result = getResultSet( reformMessage( message ) );
            try
            {
                while ( result.next() )
                {
                    for ( String want : wanted )
                    {
                        try
                        {
                            returned.put( want, result.getInt( want ) );
                        } catch ( SQLException ignored ) {}
                    }
                }
            } catch ( SQLException e )
            {
                throw new RuntimeException( e );
            } finally { closeResultSet( result ); }
        }
        return returned;
    }

    public Map< String, Boolean > getBooleanMap( String message, String[] wanted )
    {
        Map< String, Boolean > returned = new HashMap< String, Boolean >();
        if ( checkConnection() )
        {
            ResultSet result = getResultSet( reformMessage( message ) );
            try
            {
                while ( result.next() )
                {
                    for ( String want : wanted )
                    {
                        try
                        {
                            returned.put( want, result.getBoolean( want ) );
                        } catch ( SQLException ignored ) {}
                    }
                }
            } catch ( SQLException e )
            {
                throw new RuntimeException( e );
            } finally { closeResultSet( result ); }
        }
        return returned;
    }

    public Boolean existsInside( String message, String wanted, String type )
    {
        for ( String s : getStringList( message, wanted ) )
        {
            if ( s.equals( type ) )
            {
                return true;
            }
        }
        return false;
    }

    public Statement getStatement()
    {
        if ( checkConnection() )
        {
            try
            {
                return getConnection().createStatement();
            } catch ( SQLException e )
            {
                throw new RuntimeException( e );
            }
        }
        return null;
    }

    public ResultSet getResultSet( String message )
    {
        if ( checkConnection() )
        {
            Statement statement = getStatement();
            if ( statement == null )
            {
                return null;
            }
            try
            {
                return getStatement().executeQuery( message );
            } catch ( SQLException e )
            {
                closeStatement( statement );
                throw new RuntimeException( e );
            }
        }
        return null;
    }

    public void closeStatement( Statement statement )
    {
        try
        {
            statement.close();
        } catch ( SQLException e )
        {
            throw new RuntimeException( e );
        }
    }

    public void closeResultSet( ResultSet result )
    {
        try
        {
            closeStatement( result.getStatement() );
        } catch ( SQLException e )
        {
            throw new RuntimeException( e );
        }
        try
        {
            result.close();
        } catch ( SQLException e )
        {
            throw new RuntimeException( e );
        }
    }

    public Boolean exists( String table, String where, String like )
    {
        Boolean returned = false;
        if ( checkConnection() )
        {
            ResultSet result = getResultSet(
                    reformMessage(
                            "SELECT * FROM " +
                                    table +
                                    " WHERE " +
                                    where +
                                    " LIKE " +
                                    encodeString( like )
                    )
            );
            try
            {
                returned = result.next();
            } catch ( SQLException e )
            {
                throw new RuntimeException( e );
            } finally { closeResultSet( result ); }
        }
        return returned;
    }

    public Optional< Object > getObject( String table, String where, String like, String wanted )
    {
        return getObject( "SELECT * FROM " + table + " WHERE " + where + " LIKE " + encodeString( like ), wanted );
    }

    public Optional< String > getString( String table, String where, String like, String wanted )
    {
        return getString( "SELECT * FROM " + table + " WHERE " + where + " LIKE " + encodeString( like ), wanted );
    }

    public Optional< Integer > getInteger( String table, String where, String like, String wanted )
    {
        return getInteger( "SELECT * FROM " + table + " WHERE " + where + " LIKE " + encodeString( like ), wanted );
    }

    public Optional< Boolean > getBoolean( String table, String where, String like, String wanted )
    {
        return getBoolean( "SELECT * FROM " + table + " WHERE " + where + " LIKE " + encodeString( like ), wanted );
    }

    public Map< String, Object > getObjectMap( String table, String where, String like, String[] wanted )
    {
        return getObjectMap( "SELECT * FROM " + table + " WHERE " + where + " LIKE " + encodeString( like ), wanted );
    }

    public Map< String, String > getStringMap( String table, String where, String like, String[] wanted )
    {
        return getStringMap( "SELECT * FROM " + table + " WHERE " + where + " LIKE " + encodeString( like ), wanted );
    }

    public Map< String, Integer > getIntegerMap( String table, String where, String like, String[] wanted )
    {
        return getIntegerMap( "SELECT * FROM " + table + " WHERE " + where + " LIKE " + encodeString( like ), wanted );
    }

    public Map< String, Boolean > getBooleanMap( String table, String where, String like, String[] wanted )
    {
        return getBooleanMap( "SELECT * FROM " + table + " WHERE " + where + " LIKE " + encodeString( like ), wanted );
    }

    public List< Object > getObjectList( String table, String where, String like, String wanted )
    {
        return getObjectList( "SELECT * FROM " + table + " WHERE " + where + " LIKE " + encodeString( like ), wanted );
    }

    public List< String > getStringList( String table, String where, String like, String wanted )
    {
        return getStringList( "SELECT * FROM " + table + " WHERE " + where + " LIKE " + encodeString( like ), wanted );
    }

    public List< Integer > getIntegerList( String table, String where, String like, String wanted )
    {
        return getIntegerList( "SELECT * FROM " + table + " WHERE " + where + " LIKE " + encodeString( like ), wanted );
    }

    public List< Boolean > getBooleanList( String table, String where, String like, String wanted )
    {
        return getBooleanList( "SELECT * FROM " + table + " WHERE " + where + " LIKE " + encodeString( like ), wanted );
    }

    public void createTable( String table, String... values )
    {
        execute( "CREATE TABLE IF NOT EXISTS " + table + " (" + reformedListToString( values, false ) + ")" );
    }

    public void insert( String table, Map< String, String > values )
    {
        execute(
                "INSERT INTO " +
                        table +
                        " (" +
                        reformedListToString( values.keySet(), false ) +
                        ") VALUES (" +
                        reformedListToString( values.values() ) +
                        ")"
        );
    }

    public void update( String table, String where, String like, String change, String to )
    {
        execute(
                "UPDATE " +
                        table +
                        " SET " +
                        change +
                        " = " +
                        encodeString( to ) +
                        " WHERE " +
                        where +
                        " LIKE " +
                        encodeString( like )
        );
    }

    public void update( String table, String where, String like, Map< String, String > values )
    {
        List< String > settable = new ArrayList< String >();
        for ( String change : values.keySet() )
        {
            settable.add( change + "=" + encodeString( values.get( change ) ) );
        }
        execute(
                "UPDATE " +
                        table +
                        " SET " +
                        reformedListToString( settable, false ) +
                        " WHERE " +
                        where +
                        " LIKE " +
                        encodeString( like )
        );
    }
}
