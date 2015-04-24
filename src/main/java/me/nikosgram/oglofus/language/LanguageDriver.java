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

import me.nikosgram.oglofus.configuration.ConfigurationDriver;
import me.nikosgram.oglofus.configuration.ConfigurationType;

import java.nio.file.Path;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class LanguageDriver< T >
{
    protected final Class< T >               configuration;
    protected final Path                     workDirectory;
    protected final ConfigurationDriver< T > defaultLanguage;
    protected final Map< Language, ConfigurationDriver< T > > driverMap = new HashMap< Language, ConfigurationDriver< T > >();

    public LanguageDriver( Class< T > configuration, Path workDirectory )
    {
        this.configuration = configuration;
        this.workDirectory = workDirectory;
        defaultLanguage = new ConfigurationDriver< T >( configuration, workDirectory, ConfigurationType.Json, "language" );
    }

    public T getModel( Language language )
    {
        return getModel( language, false );
    }

    private T getModel( Language language, boolean second )
    {
        if ( !driverMap.containsKey( language ) )
        {
            if ( !second )
            {
                return load().save().getModel( language, true );
            }
            return defaultLanguage.getModel();
        }
        return driverMap.get( language ).getModel();
    }

    public LanguageDriver< T > load()
    {
        defaultLanguage.load();
        for ( String file : workDirectory.toFile().list() )
        {
            if ( !file.startsWith( "language_" ) )
            {
                continue;
            }
            Language language = Language.getLanguage( file.replace( "language_", "" ).replace( ".json", "" ) );
            if ( language == null )
            {
                continue;
            }
            if ( !driverMap.containsKey( language ) || driverMap.get( language ) == null )
            {
                driverMap.put( language, new ConfigurationDriver< T >( configuration, workDirectory, ConfigurationType.Json, "language_" + language.getId() ) );
            }
            driverMap.get( language ).load();
        }
        return this;
    }

    public T getModel()
    {
        return defaultLanguage.getModel();
    }

    public Collection< Language > getLanguages()
    {
        return driverMap.keySet();
    }

    public LanguageDriver< T > save()
    {
        defaultLanguage.save();
        for ( ConfigurationDriver< T > driver : driverMap.values() )
        {
            driver.save();
        }
        return this;
    }
}
