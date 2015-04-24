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

package me.nikosgram.oglofus.language;

public enum Language
{
    English( "en" ),
    Greek( "el" ),
    Armenian( "hy" ),
    Dutch( "nl" ),
    Esperanto( "eo" ),
    French( "fr" ),
    Georgian( "ka" ),
    German( "de" ),
    Italian( "it" ),
    Japanese( "ja" ),
    Persian( "fa" ),
    Polish( "pl" ),
    Portuguese( "pt" ),
    Russian( "ru" ),
    Spanish( "es" ),
    Swedish( "sv" ),
    Turkish( "tr" );

    private final String id;

    Language( String id )
    {
        this.id = id;
    }

    public static Language getLanguage( String id )
    {
        for ( Language language : values() )
        {
            if ( language.id.equalsIgnoreCase( id ) )
            {
                return language;
            }
        }
        return null;
    }

    public String getId()
    {
        return id;
    }

    @Override
    public String toString()
    {
        return id;
    }
}
