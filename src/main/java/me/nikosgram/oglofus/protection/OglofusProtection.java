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

import com.sk89q.bukkit.util.CommandsManagerRegistration;
import com.sk89q.minecraft.util.commands.*;
import me.nikosgram.oglofus.configuration.ConfigurationDriver;
import me.nikosgram.oglofus.protection.api.ProtectionSystem;
import net.md_5.bungee.api.ChatColor;
import org.apache.commons.lang.text.StrSubstitutor;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.permissions.Permission;
import org.bukkit.permissions.PermissionDefault;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitScheduler;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.BlockingDeque;

public class OglofusProtection extends JavaPlugin
{
    protected static CommandsManager< CommandSender >            commandsManager = null;
    protected static ConfigurationDriver< OglofusConfiguration > configuration   = null;
    protected static ConfigurationDriver< OglofusLanguage >      language        = null;

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
                plugin.getLogger().info( org.bukkit.ChatColor.YELLOW + "Configuration loading..." );
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
                plugin.getLogger().info( org.bukkit.ChatColor.GREEN + "Loading completed!" );
                break;
            }
            case SAVE:
            {
                plugin.getLogger().info( org.bukkit.ChatColor.YELLOW + "Configuration saving..." );
                notNull( configuration, "The configurations must not be null. Load the Configuration first!" ).save();
                notNull( language, "The language must not be null. Load the Configuration first!" ).save();
                plugin.getLogger().info( org.bukkit.ChatColor.GREEN + "Saving completed!" );
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

        commandsManager = new CommandsManager< CommandSender >()
        {
            @Override
            public boolean hasPermission( CommandSender sender, String s )
            {
                return sender.hasPermission( s );
            }
        };

        /**
         * Registering commands...
         */
        CommandsManagerRegistration registration = new CommandsManagerRegistration( this, commandsManager );
        registration.register( OglofusCommand.class );

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

    @Override
    public boolean onCommand( CommandSender sender, Command cmd, String label, String[] args )
    {
        try
        {
            commandsManager.execute( cmd.getName(), args, sender, sender );
        } catch ( CommandPermissionsException e )
        {
            sender.sendMessage( org.bukkit.ChatColor.RED + "You don't have permission." );
        } catch ( MissingNestedCommandException e )
        {
            sender.sendMessage( org.bukkit.ChatColor.RED + e.getUsage() );
        } catch ( CommandUsageException e )
        {
            sender.sendMessage( org.bukkit.ChatColor.RED + e.getMessage() );
            sender.sendMessage( org.bukkit.ChatColor.RED + e.getUsage() );
        } catch ( WrappedCommandException e )
        {
            if ( e.getCause() instanceof NumberFormatException )
            {
                sender.sendMessage( org.bukkit.ChatColor.RED + "Number expected, string received instead." );
            } else
            {
                sender.sendMessage( org.bukkit.ChatColor.RED + "An error has occurred. See console." );
                e.printStackTrace();
            }
        } catch ( CommandException e )
        {
            sender.sendMessage( org.bukkit.ChatColor.RED + e.getMessage() );
        }

        return true;
    }

    public enum ConfigurationAction
    {
        SAVE,
        LOAD,
        RELOAD
    }
}
