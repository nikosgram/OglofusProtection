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

package me.nikosgram.oglofus.protection;

import me.nikosgram.oglofus.configuration.ConfigurationDriver;
import me.nikosgram.oglofus.protection.api.MessageLang;
import me.nikosgram.oglofus.protection.api.ProtectionSystem;
import net.md_5.bungee.api.ChatColor;
import org.apache.commons.lang.text.StrSubstitutor;
import org.bukkit.Bukkit;
import org.bukkit.command.PluginCommand;
import org.bukkit.entity.Player;
import org.bukkit.permissions.Permission;
import org.bukkit.permissions.PermissionDefault;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitScheduler;

import java.util.HashMap;
import java.util.Map;

public class OglofusProtection extends JavaPlugin
{
    protected static ConfigurationDriver< OglofusConfiguration > configuration = null;
    protected static ConfigurationDriver< OglofusLanguage >      language      = null;

    protected static OglofusProtection plugin;

    public OglofusProtection()
    {
        plugin = this;
    }

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

    public static String notEmpty( String string )
    {
        return notEmpty( string, "The validated string is empty" );
    }

    public static String notEmpty( String string, String message )
    {
        if ( string == null || string.length() == 0 )
        {
            throw new IllegalArgumentException( message );
        }
        return string;
    }

    public static String reformMessage( String message )
    {
        return reformMessage( message, null );
    }

    public static String reformMessage( String message, String[] keys, String[] values )
    {
        Map< String, String > value_map = new HashMap< String, String >();
        for ( int i = 0; i < notNull( keys ).length; i++ )
        {
            value_map.put( keys[ i ], notNull( values )[ i ] );
        }
        return reformMessage( message, value_map );
    }

    public static String reformMessage( String message, Map< String, String > values )
    {
        return ChatColor.translateAlternateColorCodes( '&', StrSubstitutor.replace( notEmpty( notNull( message ) ), ( values == null ? new HashMap< String, String >() : values ), "{", "}" ) );
    }

    public static MessageLang getLanguage( Player p )
    {
        String locale = p.spigot().getLocale();
        switch ( locale.substring( 0, 2 ).toLowerCase() )
        {
            case "el":
                return MessageLang.Greek;
            default:
                return MessageLang.English;
        }
    }

    public static OglofusConfiguration getConfiguration()
    {
        return configuration.getModel();
    }

    public static OglofusLanguage getLanguage()
    {
        return language.getModel();
    }

    public static void configurationAction( ConfigurationAction action )
    {
        switch ( notNull( action, "The action must not be null!" ) )
        {
            case LOAD:
            {
                log( ChatColor.YELLOW + "Configuration loading..." );
                if ( configuration == null )
                {
                    configuration = new ConfigurationDriver< OglofusConfiguration >( OglofusConfiguration.class, plugin.getDataFolder().toPath() ).load();
                } else
                {
                    configuration.load();
                }
                if ( getConfiguration().protectionFlags.isEmpty() )
                {
                    getConfiguration().protectionFlags.put( "greeting", "Hello World :D" );
                }
                if ( getConfiguration().protectionLimits.other.isEmpty() )
                {
                    getConfiguration().protectionLimits.other.put( "vip", 5 );
                }
                for ( Permission permission : Bukkit.getPluginManager().getPermissions() )
                {
                    if ( permission.getName().startsWith( "oglofus.protection.limit." ) )
                    {
                        if ( getConfiguration().protectionLimits.other.keySet().contains( permission.getName().replaceFirst( "oglofus.protection.limit.", "" ) ) )
                        {
                            continue;
                        }
                        Bukkit.getPluginManager().removePermission( permission );
                    }
                }
                for ( String limit : getConfiguration().protectionLimits.other.keySet() )
                {
                    boolean exists = false;
                    for ( Permission permission : Bukkit.getPluginManager().getPermissions() )
                    {
                        if ( !permission.getName().startsWith( "oglofus.protection.limit." ) )
                        {
                            continue;
                        }
                        if ( permission.getName().equals( "oglofus.protection.limit." + limit ) )
                        {
                            exists = true;
                            break;
                        }
                    }
                    if ( exists )
                    {
                        continue;
                    }
                    Bukkit.getPluginManager().addPermission( new Permission( "oglofus.protection.limit." + limit, PermissionDefault.OP ) );
                }
                if ( getConfiguration().protectionWorlds.isEmpty() )
                {
                    getConfiguration().protectionWorlds.add( "world" );
                }
                if ( language == null )
                {
                    language = new ConfigurationDriver< OglofusLanguage >( OglofusLanguage.class, plugin.getDataFolder().toPath() ).load();
                } else
                {
                    language.load();
                }
                log( ChatColor.GREEN + "Loading completed!" );
                break;
            }
            case SAVE:
            {
                log( ChatColor.YELLOW + "Configuration saving..." );
                notNull( configuration, "The configurations must not be null. Load the Configuration first!" ).save();
                notNull( language, "The language must not be null. Load the Configuration first!" ).save();
                log( ChatColor.GREEN + "Saving completed!" );
                break;
            }
            case RELOAD:
            {
                configurationAction( ConfigurationAction.LOAD );
                configurationAction( ConfigurationAction.SAVE );
                break;
            }
        }
    }

    public static OglofusProtection getPlugin()
    {
        return plugin;
    }

    public static void log( String... messages )
    {
        for ( String message : messages )
        {
            Bukkit.getConsoleSender().sendMessage( ChatColor.RESET + "[" + ChatColor.AQUA + plugin.getDescription().getName() + ChatColor.RESET + "] " + message );
        }
    }

    @Override
    public void onLoad()
    {
        ProtectionSystem.invoke( plugin );
    }

    @Override
    public void onDisable()
    {
        ProtectionSystem.saveChanges();
    }

    @Override
    public void onEnable()
    {
        configurationAction( ConfigurationAction.RELOAD );

        PluginManager manager = getServer().getPluginManager();

        if ( manager.getPlugin( "WorldEdit" ) == null )
        {
            Bukkit.getScheduler().runTaskLater( this, new Runnable()
            {
                @Override
                public void run()
                {
                    Bukkit.getPluginManager().disablePlugin( plugin );
                }
            }, 20L );
            throw new NullPointerException( "I can't find the WorldEdit plugin, what happens?" );
        }
        if ( manager.getPlugin( "WorldGuard" ) == null )
        {
            Bukkit.getScheduler().runTaskLater( this, new Runnable()
            {
                @Override
                public void run()
                {
                    getServer().getPluginManager().disablePlugin( plugin );
                }
            }, 20L );
            throw new NullPointerException( "I can't find the WorldGuard plugin, what happens?" );
        }

        /**
         * Default Permissions:
         */
        manager.addPermission( new Permission( "oglofus.protection.command", PermissionDefault.TRUE ) );
        manager.addPermission( new Permission( "oglofus.protection.command.help", PermissionDefault.TRUE ) );
        manager.addPermission( new Permission( "oglofus.protection.command.accept", PermissionDefault.TRUE ) );
        manager.addPermission( new Permission( "oglofus.protection.command.info", PermissionDefault.TRUE ) );
        manager.addPermission( new Permission( "oglofus.protection.command.invite", PermissionDefault.TRUE ) );
        manager.addPermission( new Permission( "oglofus.protection.command.kick", PermissionDefault.TRUE ) );
        manager.addPermission( new Permission( "oglofus.protection.command.version", PermissionDefault.TRUE ) );

        /**
         * OP Permissions:
         */
        manager.addPermission( new Permission( "oglofus.protection.command.give", PermissionDefault.OP ) );
        manager.addPermission( new Permission( "oglofus.protection.command.debug", PermissionDefault.OP ) );
        manager.addPermission( new Permission( "oglofus.protection.command.save", PermissionDefault.OP ) );
        manager.addPermission( new Permission( "oglofus.protection.command.reload", PermissionDefault.OP ) );
        manager.addPermission( new Permission( "oglofus.protection.bypass", PermissionDefault.OP ) );

        /**
         * Registering commands...
         */
        OglofusCommandExecute oglofusCommandExecute = new OglofusCommandExecute();

        PluginCommand command = Bukkit.getPluginCommand( "protection" );
        command.setExecutor( oglofusCommandExecute );
        command.setTabCompleter( oglofusCommandExecute );

        /**
         * Registering listeners...
         */
        manager.registerEvents( new OglofusListener(), this );

        /**
         * Registering schedulers...
         */
        BukkitScheduler scheduler = Bukkit.getScheduler();
        scheduler.runTaskTimer( this, new Runnable()
        {
            @Override
            public void run()
            {
                ProtectionSystem.saveChanges();
            }
        }, getConfiguration().autoReloadDelay, getConfiguration().autoReloadDelay );
    }

    public enum ConfigurationAction
    {
        SAVE,
        LOAD,
        RELOAD
    }
}
