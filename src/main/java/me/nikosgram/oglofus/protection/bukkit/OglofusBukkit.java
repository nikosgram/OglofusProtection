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
import org.bukkit.plugin.java.JavaPlugin;

import java.nio.file.Paths;

public class OglofusBukkit extends JavaPlugin implements ProtectionPlugin
{
    @Getter
    private DatabaseConnector connector;
    @Getter
    private RegionManager     regionManager;


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

        if ( connector.checkConnection() )
        {
            regionManager = new OglofusRegionManager( this );
        }
    }

    @Override
    public void onEnable()
    {
        super.onEnable();
    }

    @Override
    public void onDisable()
    {
        super.onDisable();
    }

    @Override
    public InvitationManager getInvitationManager()
    {
        return null;
    }
}
