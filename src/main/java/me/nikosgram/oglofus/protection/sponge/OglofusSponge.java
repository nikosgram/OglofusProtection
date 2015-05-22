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

package me.nikosgram.oglofus.protection.sponge;

import com.google.inject.Inject;
import lombok.Getter;
import me.nikosgram.oglofus.database.DatabaseConnector;
import me.nikosgram.oglofus.database.MySQLDatabaseDriver;
import me.nikosgram.oglofus.database.SQLiteDatabaseDriver;
import me.nikosgram.oglofus.protection.api.OglofusProtection;
import me.nikosgram.oglofus.protection.api.manager.InvitationManager;
import me.nikosgram.oglofus.protection.api.manager.RegionManager;
import me.nikosgram.oglofus.protection.api.plugin.ProtectionPlugin;
import ninja.leaping.configurate.ConfigurationNode;
import ninja.leaping.configurate.commented.CommentedConfigurationNode;
import ninja.leaping.configurate.loader.ConfigurationLoader;
import org.slf4j.Logger;
import org.spongepowered.api.Game;
import org.spongepowered.api.event.Subscribe;
import org.spongepowered.api.event.state.PreInitializationEvent;
import org.spongepowered.api.plugin.Plugin;
import org.spongepowered.api.service.config.DefaultConfig;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

@Plugin( id = "OglofusProtection", name = "OglofusProtection", version = "2.0.1-R0.1-SNAPSHOT" )
public class OglofusSponge implements ProtectionPlugin
{
    @Getter
    @Inject
    private Game                                              game;
    @Getter
    @Inject
    private Logger                                            logger;
    @Getter
    @Inject
    @DefaultConfig( sharedRoot = true )
    private File                                              configFile;
    @Getter
    @Inject
    @DefaultConfig( sharedRoot = true )
    private ConfigurationLoader< CommentedConfigurationNode > configManager;
    @Getter
    private ConfigurationNode                                 config;
    @Getter
    private DatabaseConnector                                 connector;
    @Getter
    private RegionManager                                     regionManager;

    public OglofusSponge()
    {
        OglofusProtection.invoke( this );
    }

    @Subscribe
    public void onPreInitialization( PreInitializationEvent event )
    {
        try
        {
            if ( !configFile.exists() )
            {
                Files.createFile( configFile.toPath() );
                config = configManager.load();

                config.getNode( "ConfigVersion" ).setValue( 1 );

                config.getNode( "database", "type" ).setValue( "mysql" );
                config.getNode( "database", "host" ).setValue( "localhost" );
                config.getNode( "database", "port" ).setValue( 3306 );
                config.getNode( "database", "user" ).setValue( "root" );
                config.getNode( "database", "pass" ).setValue( "password" );
                config.getNode( "database", "data" ).setValue( "database" );

                configManager.save( config );
                logger.info(
                        "Created default configuration, " +
                                "OglofusProtection will not run until you have edited this file!"
                );
            }
        } catch ( IOException exception )
        {
            logger.error( "Couldn't create default configuration file!" );
        }

        if ( config.getNode( "database", "type" ).getString().equalsIgnoreCase( "sqlite" ) )
        {
            connector = new DatabaseConnector(
                    new SQLiteDatabaseDriver(
                            Paths.get(
                                    config.getNode(
                                            "database", "host"
                                    ).getString()
                            )
                    )
            );
        } else
        {
            connector = new DatabaseConnector(
                    new MySQLDatabaseDriver(
                            config.getNode( "database", "user" ).getString(),
                            config.getNode( "database", "data" ).getString(),
                            config.getNode( "database", "pass" ).getString(),
                            config.getNode( "database", "host" ).getString(),
                            config.getNode( "database", "port" ).getInt()
                    )
            );
        }

        if ( connector.checkConnection() )
        {
            regionManager = new OglofusRegionManager( this );
        }
    }

    @Override
    public InvitationManager getInvitationManager()
    {
        return null;
    }
}
