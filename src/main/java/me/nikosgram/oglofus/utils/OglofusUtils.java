/*
 * Copyright 2014-2015 Nikos Grammatikos
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://raw.githubusercontent.com/nikosgram13/OglofusProtection/master/LICENSE
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package me.nikosgram.oglofus.utils;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

public class OglofusUtils
{
    public static < T > T notNull( T object )
    {
        return notNull( object, "The validated object is null" );
    }

    public static < T > T notNull( T object, String message )
    {
        if ( object == null )
        {
            throw new IllegalArgumentException( message );
        }
        return object;
    }

    public static < T > List< T > page( int page, int size, List< T > list )
    {
        List< T > returned = new ArrayList< T >();
        if ( ( size * page ) > list.size() )
        {
            return returned;
        }
        int end = ( size * page ) + size;
        if ( ( size * page ) + size > list.size() )
        {
            end = list.size();
        }
        for ( int i = ( size * page ); i < end; i++ )
        {
            returned.add( list.get( i ) );
        }
        return returned;
    }

    public static String notEmpty( String string )
    {
        return notEmpty( string, "The validated string is empty" );
    }

    public static String notEmpty( String string, String message )
    {
        if ( string == null || string.length() == 0 || string.trim().length() == 0 )
        {
            throw new IllegalArgumentException( message );
        }
        return string;
    }

    public static boolean equalClass( Class< ? > aClass, Class< ? > bClass )
    {
        return aClass == bClass || aClass.isAssignableFrom( bClass );
    }

    public static < T > T newInstance( Class< T > tClass, Object... initargs )
    {
        try
        {
            return tClass.getConstructor().newInstance( initargs );
        } catch ( InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e )
        {
            throw new RuntimeException( e );
        }
    }

    public static String capitalizeMessage( String message )
    {
        return message.substring( 0, 1 ) + message.substring( 1 ).toLowerCase();
    }
}