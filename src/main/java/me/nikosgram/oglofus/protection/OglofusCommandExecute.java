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

import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import me.nikosgram.oglofus.protection.api.InviteSystem;
import me.nikosgram.oglofus.protection.api.ProtectionArea;
import me.nikosgram.oglofus.protection.api.ProtectionSystem;
import me.nikosgram.oglofus.protection.api.exception.member.AlreadyMemberException;
import me.nikosgram.oglofus.protection.api.exception.member.InviteYourSelfException;
import me.nikosgram.oglofus.protection.api.exception.member.KickYourSelfException;
import me.nikosgram.oglofus.protection.api.exception.member.MemberNotExistsException;
import me.nikosgram.oglofus.protection.api.exception.player.AccessException;
import me.nikosgram.oglofus.protection.api.exception.player.NoInvitesException;
import me.nikosgram.oglofus.protection.api.exception.player.PlayerNotExistsException;
import me.nikosgram.oglofus.protection.api.exception.player.PlayerOfflineException;
import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.*;

import static com.sk89q.worldguard.bukkit.WGBukkit.getRegionManager;
import static me.nikosgram.oglofus.protection.OglofusProtection.*;

public class OglofusCommandExecute implements CommandExecutor, TabCompleter
{
    private final String[] commands = new String[]{ "help", "version", "reload", "save", "give", "invite", "info", "kick", "accept" };

    protected OglofusCommandExecute() {}

    @Override
    public boolean onCommand( CommandSender sender, Command cmd, String s, String[] args )
    {
        if ( !sender.hasPermission( cmd.getPermission() ) )
        {
            sendMessage( sender, "accessException" );
            return true;
        }
        if ( args.length > 0 )
        {
            switch ( args[ 0 ].toLowerCase() )
            {
                case "version":
                {
                    if ( !sender.hasPermission( cmd.getPermission() + "." + args[ 0 ].toLowerCase() ) )
                    {
                        sendMessage( sender, "accessException" );
                        break;
                    }
                    sender.sendMessage( ChatColor.YELLOW + getPlugin().getDescription().getName() + " " + getPlugin().getDescription().getVersion() );
                    sender.sendMessage( reformMessage( getLanguage().getModel( getLanguage( sender ) ).authorMessage + "nikosgram13" ) );
                    break;
                }
                case "reload":
                {
                    if ( !sender.hasPermission( cmd.getPermission() + "." + args[ 0 ].toLowerCase() ) )
                    {
                        sendMessage( sender, "accessException" );
                        break;
                    }
                    sendMessage( sender, "forceReloadMessage" );
                    configurationAction( OglofusProtection.ConfigurationAction.RELOAD );
                    sendMessage( sender, "reloadCompletedMessage" );
                    break;
                }
                case "save":
                {
                    if ( !sender.hasPermission( cmd.getPermission() + "." + args[ 0 ].toLowerCase() ) )
                    {
                        sendMessage( sender, "accessException" );
                        break;
                    }

                    sendMessage( sender, "forceRegionSaveMessage" );
                    ProtectionSystem.saveChanges();
                    sendMessage( sender, "saveCompletedMessage" );
                    break;
                }
                case "give":
                {
                    if ( !sender.hasPermission( cmd.getPermission() + "." + args[ 0 ].toLowerCase() ) )
                    {
                        sendMessage( sender, "accessException" );
                        break;
                    }
                    Material material = getConfiguration().getProtectionBlock();
                    ItemMeta itemMeta = Bukkit.getItemFactory().getItemMeta( material );
                    List< String > lore = new ArrayList<>();

                    lore.add( 0, getConfiguration().protectionMetaData );

                    itemMeta.setLore( lore );
                    ItemStack stack;
                    if ( args.length >= 2 )
                    {
                        try
                        {
                            stack = new ItemStack( material, Integer.valueOf( args[ 1 ] ) );
                        } catch ( NumberFormatException e )
                        {
                            sender.sendMessage( ChatColor.RED + "Number expected, string received instead." );
                            break;
                        }
                    } else
                    {
                        stack = new ItemStack( material );
                    }
                    stack.setItemMeta( itemMeta );

                    Player player;
                    if ( args.length >= 3 )
                    {
                        if ( ( player = Bukkit.getPlayer( args[ 2 ] ) ) == null )
                        {
                            sendMessage( sender, "playerOfflineException", new String[]{ "player" }, new String[]{ args[ 2 ] } );
                            break;
                        }
                    } else
                    {
                        if ( !( sender instanceof Player ) )
                        {
                            sender.sendMessage( ChatColor.RED + "Cannot execute that command, I don't know who you are!" );
                            break;
                        }
                        player = ( Player ) sender;
                    }
                    player.getInventory().addItem( stack );
                    break;
                }
                case "invite":
                {
                    if ( !sender.hasPermission( cmd.getPermission() + "." + args[ 0 ].toLowerCase() ) )
                    {
                        sendMessage( sender, "accessException" );
                        break;
                    }
                    if ( args.length < 3 )
                    {
                        if ( !( sender instanceof Player ) )
                        {
                            sender.sendMessage( ChatColor.RED + "Cannot execute that command, I don't know who you are!" );
                            sender.sendMessage( ChatColor.AQUA + "You can use /" + s + " invite <username> <id>" );
                            break;
                        }
                        Player player = ( Player ) sender;
                        if ( !getConfiguration().allowWorld( player.getWorld() ) )
                        {
                            sendMessage( sender, "noProtectionArea" );
                            break;
                        }
                        List< String > local_regions;
                        if ( ( local_regions = getRegionManager( player.getWorld() ).getApplicableRegionsIDs( WorldGuardPlugin.inst().wrapPlayer( player ).getPosition() ) ).size() < 1 )
                        {
                            sendMessage( sender, "noAreas" );
                            break;
                        }
                        UUID uuid = null;
                        for ( String regions : local_regions )
                        {
                            try
                            {
                                uuid = UUID.fromString( regions );
                                break;
                            } catch ( IllegalArgumentException ignored ) {}
                        }
                        if ( uuid == null )
                        {
                            sendMessage( sender, "noProtectionArea" );
                            break;
                        }
                        ProtectionArea area;
                        if ( ( area = ProtectionSystem.getProtectionArea( uuid ) ) != null )
                        {
                            Map< String, String > values = new HashMap<>();
                            values.put( "player", args[ 1 ] );
                            try
                            {
                                area.invite( sender, args[ 1 ] );
                            } catch ( AlreadyMemberException e )
                            {
                                sendMessage( sender, "alreadyMemberException", values );
                            } catch ( PlayerOfflineException e )
                            {
                                sendMessage( sender, "playerOfflineException", values );
                            } catch ( AccessException e )
                            {
                                sendMessage( sender, "accessException" );
                            } catch ( InviteYourSelfException e )
                            {
                                sendMessage( sender, "inviteYourSelfException" );
                            }
                            break;
                        }
                        sendMessage( sender, "noProtectionArea" );
                        break;
                    }
                    if ( sender instanceof Player )
                    {
                        break;
                    }
                    ProtectionArea area;
                    if ( ( area = ProtectionSystem.getProtectionArea( UUID.fromString( args[ 2 ] ) ) ) != null )
                    {
                        try
                        {
                            area.invite( sender, args[ 2 ] );
                        } catch ( AlreadyMemberException | PlayerOfflineException | AccessException | InviteYourSelfException e )
                        {
                            sender.sendMessage( reformMessage( e.getMessage() ) );
                        }
                        break;
                    }

                    sendMessage( sender, "protectionAreaNotExists", new String[]{ "id" }, new String[]{ args[ 2 ] } );
                    break;
                }
                case "info":
                {
                    if ( !sender.hasPermission( cmd.getPermission() + "." + args[ 0 ].toLowerCase() ) )
                    {
                        sendMessage( sender, "accessException" );
                        break;
                    }
                    if ( args.length < 2 )
                    {
                        if ( !( sender instanceof Player ) )
                        {
                            sender.sendMessage( ChatColor.RED + "Cannot execute that command, I don't know who you are!" );
                            sender.sendMessage( ChatColor.AQUA + "You can use /" + s + " info <id>" );
                            break;
                        }
                        Player player = ( Player ) sender;
                        if ( !getConfiguration().allowWorld( player.getWorld() ) )
                        {
                            sendMessage( sender, "noProtectionArea" );
                            break;
                        }
                        List< String > local_regions;
                        if ( ( local_regions = getRegionManager( player.getWorld() ).getApplicableRegionsIDs( WorldGuardPlugin.inst().wrapPlayer( player ).getPosition() ) ).size() < 1 )
                        {
                            sendMessage( sender, "noAreas" );
                            break;
                        }
                        UUID uuid = null;
                        for ( String regions : local_regions )
                        {
                            try
                            {
                                uuid = UUID.fromString( regions );
                                break;
                            } catch ( IllegalArgumentException ignored ) {}
                        }
                        if ( uuid == null )
                        {
                            sendMessage( sender, "noProtectionArea" );
                            break;
                        }
                        ProtectionArea area;
                        if ( ( area = ProtectionSystem.getProtectionArea( uuid ) ) != null )
                        {
                            ProtectedRegion region = area.getRegion();

                            Map< String, String > values = new HashMap< String, String >();

                            values.put( "n", "\n" );
                            values.put( "id", area.getUuid().toString() );
                            List< String > owners = new ArrayList< String >();
                            for ( OfflinePlayer owner : area.getOwners() )
                            {
                                owners.add( owner.getName() );
                            }
                            List< String > members = new ArrayList< String >();
                            for ( OfflinePlayer member : area.getMembers() )
                            {
                                members.add( member.getName() );
                            }
                            values.put( "owners", StringUtils.join( owners, ", " ) );
                            values.put( "members", StringUtils.join( members, ", " ) );
                            values.put( "location", area.getLocation().getX() + "," + area.getLocation().getBlockY() + "," + area.getLocation().getBlockZ() );
                            values.put( "vector", region.getMinimumPoint().toString() + ' ' + region.getMaximumPoint().toString() );

                            switch ( area.getRank( ( ( Player ) sender ) ) )
                            {
                                case Owner:
                                    sendMessage( sender, "ownerInfo", values );
                                    break;
                                case Member:
                                    sendMessage( sender, "memberInfo", values );
                                    break;
                                case None:
                                default:
                                    sendMessage( sender, "protectionAreaNotAccess" );
                                    break;
                            }
                            break;
                        }
                        sendMessage( sender, "noProtectionArea" );
                        break;
                    }
                    if ( sender instanceof Player )
                    {
                        break;
                    }
                    UUID uuid;
                    try
                    {
                        uuid = UUID.fromString( args[ 1 ] );
                    } catch ( IllegalArgumentException e )
                    {
                        sender.sendMessage( ChatColor.RED + "Uuid expected, string received instead." );
                        break;
                    }
                    ProtectionArea area;
                    if ( ( area = ProtectionSystem.getProtectionArea( uuid ) ) != null )
                    {
                        ProtectedRegion region = area.getRegion();
                        Map< String, String > values = new HashMap< String, String >();

                        values.put( "n", "\n" );
                        values.put( "id", area.getUuid().toString() );
                        List< String > owners = new ArrayList< String >();
                        for ( OfflinePlayer owner : area.getOwners() )
                        {
                            owners.add( owner.getName() );
                        }
                        List< String > members = new ArrayList< String >();
                        for ( OfflinePlayer member : area.getMembers() )
                        {
                            members.add( member.getName() );
                        }
                        values.put( "owners", StringUtils.join( owners, ", " ) );
                        values.put( "members", StringUtils.join( members, ", " ) );
                        values.put( "location", area.getLocation().getX() + "," + area.getLocation().getBlockY() + "," + area.getLocation().getBlockZ() );
                        values.put( "vector", region.getMinimumPoint().toString() + ' ' + region.getMaximumPoint().toString() );

                        sendMessage( sender, "ownerInfo", values );
                        break;
                    }

                    sendMessage( sender, "protectionAreaNotExists", new String[]{ "id" }, new String[]{ args[ 1 ] } );
                }
                case "kick":
                {
                    if ( !sender.hasPermission( cmd.getPermission() + "." + args[ 0 ].toLowerCase() ) )
                    {
                        sendMessage( sender, "accessException" );
                        break;
                    }
                    if ( args.length < 3 )
                    {
                        if ( !( sender instanceof Player ) )
                        {
                            sender.sendMessage( ChatColor.RED + "Cannot execute that command, I don't know who you are!" );
                            sender.sendMessage( ChatColor.AQUA + "You can use /" + s + " invite <username> <id>" );
                            break;
                        }
                        Player player = ( Player ) sender;
                        if ( !getConfiguration().allowWorld( player.getWorld() ) )
                        {
                            sendMessage( sender, "noProtectionArea" );
                            break;
                        }
                        List< String > local_regions;
                        if ( ( local_regions = getRegionManager( player.getWorld() ).getApplicableRegionsIDs( WorldGuardPlugin.inst().wrapPlayer( player ).getPosition() ) ).size() < 1 )
                        {
                            sendMessage( sender, "noAreas" );
                            break;
                        }
                        UUID uuid = null;
                        for ( String regions : local_regions )
                        {
                            try
                            {
                                uuid = UUID.fromString( regions );
                                break;
                            } catch ( IllegalArgumentException ignored ) {}
                        }
                        if ( uuid == null )
                        {
                            sendMessage( sender, "noProtectionArea" );
                            break;
                        }
                        ProtectionArea area;
                        if ( ( area = ProtectionSystem.getProtectionArea( uuid ) ) != null )
                        {
                            Map< String, String > values = new HashMap<>();
                            values.put( "player", args[ 1 ] );
                            try
                            {
                                area.kick( player, args[ 1 ] );
                            } catch ( AccessException e )
                            {
                                sendMessage( sender, "accessException" );
                            } catch ( PlayerNotExistsException e )
                            {
                                sendMessage( sender, "playerNotExistsException", values );
                            } catch ( MemberNotExistsException e )
                            {
                                sendMessage( sender, "memberNotExistsException", values );
                            } catch ( KickYourSelfException e )
                            {
                                sendMessage( sender, "kickYourSelfException" );
                            }
                            break;
                        }
                        sendMessage( sender, "noProtectionArea" );
                        break;
                    }
                    if ( sender instanceof Player )
                    {
                        break;
                    }
                    UUID uuid;
                    try
                    {
                        uuid = UUID.fromString( args[ 1 ] );
                    } catch ( IllegalArgumentException e )
                    {
                        sender.sendMessage( ChatColor.RED + "Uuid expected, string received instead." );
                        break;
                    }
                    ProtectionArea area;
                    if ( ( area = ProtectionSystem.getProtectionArea( uuid ) ) != null )
                    {
                        try
                        {
                            area.kick( sender, args[ 2 ] );
                        } catch ( AccessException e )
                        {
                            sendMessage( sender, "accessException" );
                        } catch ( PlayerNotExistsException e )
                        {
                            sendMessage( sender, "playerNotExistsException" );
                        } catch ( MemberNotExistsException e )
                        {
                            sendMessage( sender, "memberNotExistsException" );
                        } catch ( KickYourSelfException e )
                        {
                            sendMessage( sender, "kickYourSelfException" );
                        }
                        break;
                    }
                    sendMessage( sender, "protectionAreaNotExists", new String[]{ "{id}" }, new String[]{ args[ 2 ] } );
                    break;
                }
                case "accept":
                {
                    if ( !sender.hasPermission( cmd.getPermission() + "." + args[ 0 ].toLowerCase() ) )
                    {
                        sendMessage( sender, "accessException" );
                        break;
                    }
                    if ( !( sender instanceof Player ) )
                    {
                        sender.sendMessage( ChatColor.RED + "Cannot execute that command, I don't know who you are!" );
                        break;
                    }

                    try
                    {
                        InviteSystem.accept( ( Player ) sender );
                    } catch ( NoInvitesException e )
                    {
                        sendMessage( sender, "noInvitesException" );
                    }
                    break;
                }
                case "help":
                {
                    if ( !sender.hasPermission( cmd.getPermission() + "." + args[ 0 ].toLowerCase() ) )
                    {
                        sendMessage( sender, "accessException" );
                        break;
                    }
                    if ( args.length == 1 )
                    {
                        sender.sendMessage( ChatColor.YELLOW + "Commands: <" + getPlugin().getDescription().getName() + " " + getPlugin().getDescription().getVersion() + '>' );
                        for ( String command : commands )
                        {
                            if ( command.equalsIgnoreCase( args[ 0 ] ) )
                            {
                                continue;
                            }
                            if ( sender.hasPermission( cmd.getPermission() + "." + command ) )
                            {
                                sender.sendMessage( ChatColor.GREEN + "Command <" + command + "> " + ChatColor.RED + "/" + s + " help " + command );
                            }
                        }
                        break;
                    }
                    if ( args.length > 1 )
                    {
                        switch ( args[ 1 ].toLowerCase() )
                        {
                            case "version":
                            {
                                if ( !sender.hasPermission( cmd.getPermission() + "." + args[ 1 ].toLowerCase() ) )
                                {
                                    sendMessage( sender, "accessException" );
                                    break;
                                }
                                sendMessage( sender, "versionCommandDescription" );
                                sender.sendMessage( reformMessage( getLanguage().getModel( getLanguage( sender ) ).usageCommandExample + s + " " + args[ 1 ].toLowerCase() ) );
                                break;
                            }
                            case "reload":
                            {
                                if ( !sender.hasPermission( cmd.getPermission() + "." + args[ 1 ].toLowerCase() ) )
                                {
                                    sendMessage( sender, "accessException" );
                                    break;
                                }
                                sendMessage( sender, "reloadCommandDescription" );
                                sender.sendMessage( reformMessage( getLanguage().getModel( getLanguage( sender ) ).usageCommandExample + s + " " + args[ 1 ].toLowerCase() ) );
                                break;
                            }
                            case "save":
                            {
                                if ( !sender.hasPermission( cmd.getPermission() + "." + args[ 1 ].toLowerCase() ) )
                                {
                                    sendMessage( sender, "accessException" );
                                    break;
                                }
                                sendMessage( sender, "saveCommandDescription" );
                                sender.sendMessage( reformMessage( getLanguage().getModel( getLanguage( sender ) ).usageCommandExample + s + " " + args[ 1 ].toLowerCase() ) );
                                break;
                            }
                            case "give":
                            {
                                if ( !sender.hasPermission( cmd.getPermission() + "." + args[ 1 ].toLowerCase() ) )
                                {
                                    sendMessage( sender, "accessException" );
                                    break;
                                }
                                sendMessage( sender, "giveCommandDescription" );
                                sender.sendMessage( reformMessage( getLanguage().getModel( getLanguage( sender ) ).usageCommandExample + s + " " + args[ 1 ].toLowerCase() + " <amount> [<player>]" ) );
                                break;
                            }
                            case "invite":
                            {
                                if ( !sender.hasPermission( cmd.getPermission() + "." + args[ 1 ].toLowerCase() ) )
                                {
                                    sendMessage( sender, "accessException" );
                                    break;
                                }
                                sendMessage( sender, "inviteCommandDescription" );
                                sender.sendMessage( reformMessage( getLanguage().getModel( getLanguage( sender ) ).usageCommandExample + s + " " + args[ 1 ].toLowerCase() + " <player> [<id>]" ) );
                                break;
                            }
                            case "info":
                            {
                                if ( !sender.hasPermission( cmd.getPermission() + "." + args[ 1 ].toLowerCase() ) )
                                {
                                    sendMessage( sender, "accessException" );
                                    break;
                                }
                                sendMessage( sender, "infoCommandDescription" );
                                sender.sendMessage( reformMessage( getLanguage().getModel( getLanguage( sender ) ).usageCommandExample + s + " " + args[ 1 ].toLowerCase() + " [<id>]" ) );
                                break;
                            }
                            case "kick":
                            {
                                if ( !sender.hasPermission( cmd.getPermission() + "." + args[ 1 ].toLowerCase() ) )
                                {
                                    sendMessage( sender, "accessException" );
                                    break;
                                }
                                sendMessage( sender, "kickCommandDescription" );
                                sender.sendMessage( reformMessage( getLanguage().getModel( getLanguage( sender ) ).usageCommandExample + s + " " + args[ 1 ].toLowerCase() + " <player> [<id>]" ) );
                                break;
                            }
                            case "accept":
                            {
                                if ( !sender.hasPermission( cmd.getPermission() + "." + args[ 1 ].toLowerCase() ) )
                                {
                                    sendMessage( sender, "accessException" );
                                    break;
                                }
                                sendMessage( sender, "acceptCommandDescription" );
                                sender.sendMessage( reformMessage( getLanguage().getModel( getLanguage( sender ) ).usageCommandExample + s + " " + args[ 1 ].toLowerCase() ) );
                                break;
                            }
                            default:
                            {
                                sender.sendMessage( ChatColor.YELLOW + "Commands: <" + getPlugin().getDescription().getName() + " " + getPlugin().getDescription().getVersion() + '>' );
                                for ( String command : commands )
                                {
                                    if ( command.equalsIgnoreCase( args[ 0 ] ) )
                                    {
                                        continue;
                                    }
                                    if ( sender.hasPermission( cmd.getPermission() + "." + command ) )
                                    {
                                        sender.sendMessage( ChatColor.GREEN + "Command <" + command + "> " + ChatColor.RED + "/" + s + " help " + command );
                                    }
                                }
                                break;
                            }
                        }
                    }
                    break;
                }
                default:
                {
                    List< String > command_list = new ArrayList<>();
                    for ( String command : commands )
                    {
                        if ( sender.hasPermission( cmd.getPermission() + "." + command ) )
                        {
                            command_list.add( command );
                        }
                    }
                    sender.sendMessage( ChatColor.RED + "/" + s + " <" + StringUtils.join( command_list, "|" ) + '>' );
                    break;
                }
            }
            return true;
        }
        List< String > command_list = new ArrayList<>();
        for ( String command : commands )
        {
            if ( sender.hasPermission( cmd.getPermission() + "." + command ) )
            {
                command_list.add( command );
            }
        }
        sender.sendMessage( ChatColor.RED + "/" + s + " <" + StringUtils.join( command_list, "|" ) + '>' );
        return true;
    }

    @Override
    public List< String > onTabComplete( CommandSender sender, Command cmd, String s, String[] args )
    {
        if ( cmd.testPermissionSilent( sender ) )
        {
            switch ( args.length )
            {
                case 1:
                {
                    List< String > returned = new ArrayList<>();

                    for ( String command : commands )
                    {
                        if ( command.startsWith( args[ 0 ].toLowerCase() ) )
                        {
                            if ( sender.hasPermission( cmd.getPermission() + "." + command ) )
                            {
                                returned.add( command );
                            }
                        }
                    }

                    return returned;
                }
                case 2:
                {
                    if ( !sender.hasPermission( cmd.getPermission() + "." + args[ 0 ].toLowerCase() ) )
                    {
                        break;
                    }
                    List< String > returned = new ArrayList<>();

                    switch ( args[ 0 ] )
                    {
                        case "help":
                        {
                            for ( String command : commands )
                            {
                                if ( command.equalsIgnoreCase( args[ 0 ] ) )
                                {
                                    continue;
                                }
                                if ( !sender.hasPermission( cmd.getPermission() + "." + command ) )
                                {
                                    continue;
                                }
                                if ( command.startsWith( args[ 1 ].toLowerCase() ) )
                                {
                                    returned.add( command );
                                }
                            }
                        }
                        case "invite":
                        {
                            for ( Player player : Bukkit.getOnlinePlayers() )
                            {
                                if ( player.getName().equals( sender.getName() ) )
                                {
                                    continue;
                                }
                                if ( player.getName().startsWith( args[ 1 ].toLowerCase() ) )
                                {
                                    returned.add( player.getName() );
                                }
                            }
                        }
                        case "kick":
                        {
                            for ( OfflinePlayer player : Bukkit.getOfflinePlayers() )
                            {
                                if ( player.getName().equals( sender.getName() ) )
                                {
                                    continue;
                                }
                                if ( player.getName().startsWith( args[ 1 ].toLowerCase() ) )
                                {
                                    returned.add( player.getName() );
                                }
                            }
                        }
                        case "info":
                        {
                            if ( sender instanceof Player )
                            {
                                break;
                            }
                            for ( ProtectionArea area : ProtectionSystem.getProtectionAreas() )
                            {
                                returned.add( area.getUuid().toString() );
                            }
                        }
                    }

                    return returned;
                }
                case 3:
                {
                    if ( !sender.hasPermission( cmd.getPermission() + "." + args[ 0 ].toLowerCase() ) )
                    {
                        break;
                    }
                    List< String > returned = new ArrayList<>();

                    switch ( args[ 0 ] )
                    {
                        case "give":
                        {
                            for ( Player player : Bukkit.getOnlinePlayers() )
                            {
                                if ( player.getName().startsWith( args[ 2 ].toLowerCase() ) )
                                {
                                    returned.add( player.getName() );
                                }
                            }
                        }
                        case "kick":
                        case "invite":
                        {
                            if ( sender instanceof Player )
                            {
                                break;
                            }
                            for ( ProtectionArea area : ProtectionSystem.getProtectionAreas() )
                            {
                                if ( area.getUuid().toString().startsWith( args[ 2 ] ) )
                                {
                                    returned.add( area.getUuid().toString() );
                                }
                            }
                        }
                    }

                    return returned;
                }
            }
        }
        return new ArrayList<>();
    }
}
