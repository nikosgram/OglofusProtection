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
import me.nikosgram.oglofus.protection.api.region.ProtectionLocation;
import me.nikosgram.oglofus.protection.api.region.ProtectionRegion;
import ninja.leaping.configurate.ConfigurationNode;
import ninja.leaping.configurate.commented.CommentedConfigurationNode;
import ninja.leaping.configurate.loader.ConfigurationLoader;
import org.slf4j.Logger;
import org.spongepowered.api.Game;
import org.spongepowered.api.Server;
import org.spongepowered.api.event.Subscribe;
import org.spongepowered.api.event.entity.player.PlayerBreakBlockEvent;
import org.spongepowered.api.event.entity.player.PlayerInteractBlockEvent;
import org.spongepowered.api.event.entity.player.PlayerPlaceBlockEvent;
import org.spongepowered.api.event.state.PreInitializationEvent;
import org.spongepowered.api.event.state.ServerStoppedEvent;
import org.spongepowered.api.plugin.Plugin;
import org.spongepowered.api.service.config.DefaultConfig;
import org.spongepowered.api.world.World;

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
    private Server                                            server;
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
    @Getter
    private InvitationManager                                 invitationManager;

    public OglofusSponge()
    {
        OglofusProtection.invoke( this );
    }

    @Subscribe
    public void onPreInitialization( PreInitializationEvent event )
    {
        this.server = this.game.getServer();
        try
        {
            if ( !this.configFile.exists() )
            {
                Files.createFile( this.configFile.toPath() );
                this.config = this.configManager.load();

                this.config.getNode( "ConfigVersion" ).setValue( 1 );

                this.config.getNode( "database", "type" ).setValue( "mysql" );
                this.config.getNode( "database", "host" ).setValue( "localhost" );
                this.config.getNode( "database", "port" ).setValue( 3306 );
                this.config.getNode( "database", "user" ).setValue( "root" );
                this.config.getNode( "database", "pass" ).setValue( "password" );
                this.config.getNode( "database", "data" ).setValue( "database" );

                this.configManager.save( config );
                this.logger.info(
                        "Created default configuration, " +
                                "OglofusProtection will not run until you have edited this file!"
                );
            }
        } catch ( IOException exception )
        {
            this.logger.error( "Couldn't create default configuration file!" );
        }

        if ( this.config.getNode( "database", "type" ).getString().equalsIgnoreCase( "sqlite" ) )
        {
            this.connector = new DatabaseConnector(
                    new SQLiteDatabaseDriver(
                            Paths.get(
                                    this.config.getNode(
                                            "database", "host"
                                    ).getString()
                            )
                    )
            );
        } else
        {
            this.connector = new DatabaseConnector(
                    new MySQLDatabaseDriver(
                            this.config.getNode( "database", "user" ).getString(),
                            this.config.getNode( "database", "data" ).getString(),
                            this.config.getNode( "database", "pass" ).getString(),
                            this.config.getNode( "database", "host" ).getString(),
                            this.config.getNode( "database", "port" ).getInt()
                    )
            );
        }

        this.connector.openConnection();

        if ( this.connector.checkConnection() )
        {
            this.regionManager = new OglofusRegionManager( this );
            this.invitationManager = new OglofusInvitationManager( this );
        }
    }

    @Subscribe
    public void onServerStopped( ServerStoppedEvent event )
    {
        this.connector.closeConnection();
    }

    @Subscribe
    public void security( PlayerInteractBlockEvent event )
    {
        ProtectionLocation location = new OglofusProtectionLocation(
                this,
                ( ( World ) event.getBlock().getExtent() ).getUniqueId(),
                event.getBlock().getBlockX(),
                event.getBlock().getBlockY(),
                event.getBlock().getBlockZ()
        );
        ProtectionRegion region;
        if ( ( region = getRegionManager().getRegion( location ).orNull() ) != null )
        {
            if ( !region.getProtectionStaff().hasMemberAccess( event.getEntity().getUniqueId() ) )
            {
                event.setCancelled( true );
            } else
            {
                if ( region.getProtectionVector().getBlockLocation().equals( location ) )
                {
                    if ( !region.getProtectionStaff().hasOwnerAccess( event.getEntity().getUniqueId() ) )
                    {
                        event.setCancelled( true );
                    }
                }
            }
        }
    }

    @Subscribe
    public void security( PlayerPlaceBlockEvent event )
    {
        ProtectionLocation location = new OglofusProtectionLocation(
                this,
                ( ( World ) event.getBlock().getExtent() ).getUniqueId(),
                event.getBlock().getBlockX(),
                event.getBlock().getBlockY(),
                event.getBlock().getBlockZ()
        );
        ProtectionRegion region;
        if ( ( region = getRegionManager().getRegion( location ).orNull() ) != null )
        {
            if ( !region.getProtectionStaff().hasMemberAccess( event.getEntity().getUniqueId() ) )
            {
                event.setCancelled( true );
            } else
            {
                if ( region.getProtectionVector().getBlockLocation().equals( location ) )
                {
                    if ( !region.getProtectionStaff().hasOwnerAccess( event.getEntity().getUniqueId() ) )
                    {
                        event.setCancelled( true );
                    }
                }
            }
        }
    }

    @Subscribe
    public void security( PlayerBreakBlockEvent event )
    {
        ProtectionLocation location = new OglofusProtectionLocation(
                this,
                ( ( World ) event.getBlock().getExtent() ).getUniqueId(),
                event.getBlock().getBlockX(),
                event.getBlock().getBlockY(),
                event.getBlock().getBlockZ()
        );
        ProtectionRegion region;
        if ( ( region = getRegionManager().getRegion( location ).orNull() ) != null )
        {
            if ( !region.getProtectionStaff().hasMemberAccess( event.getEntity().getUniqueId() ) )
            {
                event.setCancelled( true );
            } else
            {
                if ( region.getProtectionVector().getBlockLocation().equals( location ) )
                {
                    if ( !region.getProtectionStaff().hasOwnerAccess( event.getEntity().getUniqueId() ) )
                    {
                        event.setCancelled( true );
                    }
                }
            }
        }
    }
}
