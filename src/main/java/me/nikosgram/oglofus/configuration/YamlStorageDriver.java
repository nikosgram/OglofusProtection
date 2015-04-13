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

package me.nikosgram.oglofus.configuration;

import org.yaml.snakeyaml.Yaml;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Need preview!
 *
 * @param <T>
 */
@Deprecated
public class YamlStorageDriver< T > implements StorageDriver< T >
{
    private static final Yaml YAML = new Yaml();

    protected final ConfigurationDriver< T > driver;
    protected final Configuration            configuration;
    private final   Path                     path;

    private long modified = 0L;

    protected YamlStorageDriver( ConfigurationDriver< T > driver )
    {
        this.driver = driver;
        configuration = driver.configuration.getAnnotation( Configuration.class );
        path = Paths.get( driver.workDirectory.toString() + "/" + configuration.value() + ".yml" );
    }

    public boolean create()
    {
        Path parent = path.getParent();
        if ( !Files.exists( parent ) )
        {
            try
            {
                Files.createDirectories( parent );
            } catch ( IOException e )
            {
                e.printStackTrace();
            }
        }
        if ( !Files.exists( path ) )
        {
            try
            {
                Files.createFile( path );
            } catch ( IOException e )
            {
                e.printStackTrace();
            }
        }
        return Files.exists( path );
    }

    public void save()
    {
        if ( create() )
        {
            try ( OutputStreamWriter writer = new OutputStreamWriter( new FileOutputStream( path.toFile() ), Charset.forName( "UTF-8" ) ) )
            {
                YAML.dump( driver.model, writer );
            } catch ( IOException e )
            {
                e.printStackTrace();
            }
            try
            {
                modified = Files.getLastModifiedTime( path ).toMillis();
            } catch ( IOException e )
            {
                e.printStackTrace();
            }
        }
    }

    public void load()
    {
        if ( create() )
        {
            try
            {
                if ( Files.getLastModifiedTime( path ).toMillis() == modified )
                {
                    return;
                }
            } catch ( IOException e )
            {
                e.printStackTrace();
            }
            try ( InputStreamReader reader = new InputStreamReader( new FileInputStream( path.toFile() ), Charset.forName( "UTF-8" ) ) )
            {
                YAML.loadAs( reader, driver.configuration );
            } catch ( IOException e )
            {
                e.printStackTrace();
            }
            if ( driver.model == null )
            {
                try
                {
                    driver.model = driver.configuration.newInstance();
                    save();
                } catch ( InstantiationException | IllegalAccessException e )
                {
                    e.printStackTrace();
                }
            }
        }
    }
}