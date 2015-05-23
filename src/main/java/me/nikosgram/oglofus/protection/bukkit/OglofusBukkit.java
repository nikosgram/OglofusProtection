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

package me.nikosgram.oglofus.protection.bukkit;

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
import me.nikosgram.oglofus.protection.bukkit.handler.WorldGuardHandler;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.nio.file.Paths;

public class OglofusBukkit extends JavaPlugin implements ProtectionPlugin, Listener
{
    @Getter
    private DatabaseConnector connector;
    @Getter
    private RegionManager     regionManager;
    @Getter
    private InvitationManager invitationManager;

    public OglofusBukkit()
    {
        OglofusProtection.invoke( this );
    }

    @Override
    public void onLoad()
    {
        saveDefaultConfig();
        getConfig().options().copyDefaults( true );

        if ( getConfig().getString( "database.type" ).equalsIgnoreCase( "sqlite" ) )
        {
            connector = new DatabaseConnector(
                    new SQLiteDatabaseDriver(
                            Paths.get(
                                    getConfig().getString(
                                            "database.host"
                                    )
                            )
                    )
            );
        } else
        {
            connector = new DatabaseConnector(
                    new MySQLDatabaseDriver(
                            getConfig().getString( "database.user" ),
                            getConfig().getString( "database.data" ),
                            getConfig().getString( "database.pass" ),
                            getConfig().getString( "database.host" ),
                            getConfig().getInt( "database.port" )
                    )
            );
        }

        connector.openConnection();

        if ( connector.checkConnection() )
        {
            regionManager = new OglofusRegionManager( this );
        }
    }

    @Override
    public void onEnable()
    {
        if ( getServer().getPluginManager().getPlugin( "WorldGuard" ) != null )
        {
            regionManager.registerHandler( new WorldGuardHandler() );
        }
        getServer().getPluginManager().registerEvents( this, this );
    }

    @Override
    public void onDisable()
    {
        connector.closeConnection();
    }

    @EventHandler
    public void security( PlayerInteractEvent event )
    {
        if ( event.getAction().equals( Action.PHYSICAL ) ||
                event.getAction().equals( Action.RIGHT_CLICK_AIR ) ||
                event.getAction().equals( Action.LEFT_CLICK_AIR ) )
        {
            return;
        }
        ProtectionLocation location = new OglofusProtectionLocation(
                this,
                event.getClickedBlock().getWorld().getUID(),
                event.getClickedBlock().getX(),
                event.getClickedBlock().getY(),
                event.getClickedBlock().getZ()
        );
        ProtectionRegion region;
        if ( ( region = getRegionManager().getRegion( location ).orNull() ) != null )
        {
            if ( !region.getProtectionStaff().hasMemberAccess( event.getPlayer().getUniqueId() ) )
            {
                event.setCancelled( true );
            } else
            {
                if ( region.getProtectionVector().getBlockLocation().equals( location ) )
                {
                    if ( !region.getProtectionStaff().hasOwnerAccess( event.getPlayer().getUniqueId() ) )
                    {
                        event.setCancelled( true );
                    }
                }
            }
        }
    }

    @EventHandler
    public void security( BlockPlaceEvent event )
    {
        ProtectionLocation location = new OglofusProtectionLocation(
                this,
                event.getBlock().getWorld().getUID(),
                event.getBlock().getX(),
                event.getBlock().getY(),
                event.getBlock().getZ()
        );
        ProtectionRegion region;
        if ( ( region = getRegionManager().getRegion( location ).orNull() ) != null )
        {
            if ( !region.getProtectionStaff().hasMemberAccess( event.getPlayer().getUniqueId() ) )
            {
                event.setCancelled( true );
            } else
            {
                if ( region.getProtectionVector().getBlockLocation().equals( location ) )
                {
                    if ( !region.getProtectionStaff().hasOwnerAccess( event.getPlayer().getUniqueId() ) )
                    {
                        event.setCancelled( true );
                    }
                }
            }
        }
    }

    @EventHandler
    public void security( BlockBreakEvent event )
    {
        ProtectionLocation location = new OglofusProtectionLocation(
                this,
                event.getBlock().getWorld().getUID(),
                event.getBlock().getX(),
                event.getBlock().getY(),
                event.getBlock().getZ()
        );
        ProtectionRegion region;
        if ( ( region = getRegionManager().getRegion( location ).orNull() ) != null )
        {
            if ( !region.getProtectionStaff().hasMemberAccess( event.getPlayer().getUniqueId() ) )
            {
                event.setCancelled( true );
            } else
            {
                if ( region.getProtectionVector().getBlockLocation().equals( location ) )
                {
                    if ( !region.getProtectionStaff().hasOwnerAccess( event.getPlayer().getUniqueId() ) )
                    {
                        event.setCancelled( true );
                    }
                }
            }
        }
    }
}
