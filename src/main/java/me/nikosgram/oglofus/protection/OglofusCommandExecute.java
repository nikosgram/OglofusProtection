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
            getLanguage().accessException.sendMessage( sender );
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
                        getLanguage().accessException.sendMessage( sender );
                        break;
                    }
                    sender.sendMessage( ChatColor.YELLOW + getPlugin().getDescription().getName() + " " + getPlugin().getDescription().getVersion() );
                    new OglofusMessage( "&eAuthor: niksogram13", "&eΔημιουργός: nikosgram13" ).sendMessage( sender );
                    break;
                }
                case "reload":
                {
                    if ( !sender.hasPermission( cmd.getPermission() + "." + args[ 0 ].toLowerCase() ) )
                    {
                        getLanguage().accessException.sendMessage( sender );
                        break;
                    }
                    new OglofusMessage( "&eThe system reloading...", "&eΤο σύστημα διαβάζει ξανά τα αρχεία διαμόρφωσης..." ).sendMessage( sender );
                    configurationAction( OglofusProtection.ConfigurationAction.RELOAD );
                    new OglofusMessage( "&aReloading completed!", "&aΗ διαδικασία ολοκληρώθηκε!" ).sendMessage( sender );
                    break;
                }
                case "save":
                {
                    if ( !sender.hasPermission( cmd.getPermission() + "." + args[ 0 ].toLowerCase() ) )
                    {
                        getLanguage().accessException.sendMessage( sender );
                        break;
                    }
                    new OglofusMessage( "&eThe system saving the regions...", "&eΤο σύστημα αποθηκεύει όλες τις προστατευόμενες περιοχές..." ).sendMessage( sender );
                    ProtectionSystem.saveChanges();
                    new OglofusMessage( "&aSaving completed!", "&aΗ διαδικασία ολοκληρώθηκε!" ).sendMessage( sender );
                    break;
                }
                case "give":
                {
                    if ( !sender.hasPermission( cmd.getPermission() + "." + args[ 0 ].toLowerCase() ) )
                    {
                        getLanguage().accessException.sendMessage( sender );
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
                            getLanguage().playerOfflineException.sendMessage( sender, new String[]{ "player" }, new String[]{ args[ 2 ] } );
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
                        getLanguage().accessException.sendMessage( sender );
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
                            getLanguage().noProtectionArea.sendMessage( player );
                            break;
                        }
                        List< String > local_regions;
                        if ( ( local_regions = getRegionManager( player.getWorld() ).getApplicableRegionsIDs( WorldGuardPlugin.inst().wrapPlayer( player ).getPosition() ) ).size() < 1 )
                        {
                            getLanguage().noAreas.sendMessage( player );
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
                            getLanguage().noProtectionArea.sendMessage( player );
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
                                getLanguage().alreadyMemberException.sendMessage( player, values );
                            } catch ( PlayerOfflineException e )
                            {
                                getLanguage().playerOfflineException.sendMessage( player, values );
                            } catch ( AccessException e )
                            {
                                getLanguage().accessException.sendMessage( player );
                            } catch ( InviteYourSelfException e )
                            {
                                getLanguage().inviteYourSelfException.sendMessage( player );
                            }
                            break;
                        }
                        getLanguage().noProtectionArea.sendMessage( ( ( Player ) sender ) );
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
                            sender.sendMessage( e.getMessage() );
                        }
                        break;
                    }

                    getLanguage().protectionAreaNotExists.sendMessage( sender, new String[]{ "id" }, new String[]{ args[ 2 ] } );
                    break;
                }
                case "info":
                {
                    if ( !sender.hasPermission( cmd.getPermission() + "." + args[ 0 ].toLowerCase() ) )
                    {
                        getLanguage().accessException.sendMessage( sender );
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
                            getLanguage().noProtectionArea.sendMessage( player );
                            break;
                        }
                        List< String > local_regions;
                        if ( ( local_regions = getRegionManager( player.getWorld() ).getApplicableRegionsIDs( WorldGuardPlugin.inst().wrapPlayer( player ).getPosition() ) ).size() < 1 )
                        {
                            getLanguage().noAreas.sendMessage( player );
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
                            getLanguage().noProtectionArea.sendMessage( player );
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
                                    getLanguage().ownerInfo.sendMessage( player, values );
                                    break;
                                case Member:
                                    getLanguage().memberInfo.sendMessage( player, values );
                                    break;
                                case None:
                                default:
                                    getLanguage().protectionAreaNotAccess.sendMessage( player );
                                    break;
                            }
                            break;
                        }
                        getLanguage().noProtectionArea.sendMessage( player );
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

                        getLanguage().ownerInfo.sendMessage( sender, values );
                        break;
                    }

                    getLanguage().protectionAreaNotExists.sendMessage( sender, new String[]{ "id" }, new String[]{ args[ 1 ] } );
                }
                case "kick":
                {
                    if ( !sender.hasPermission( cmd.getPermission() + "." + args[ 0 ].toLowerCase() ) )
                    {
                        getLanguage().accessException.sendMessage( sender );
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
                            getLanguage().noProtectionArea.sendMessage( player );
                            break;
                        }
                        List< String > local_regions;
                        if ( ( local_regions = getRegionManager( player.getWorld() ).getApplicableRegionsIDs( WorldGuardPlugin.inst().wrapPlayer( player ).getPosition() ) ).size() < 1 )
                        {
                            getLanguage().noAreas.sendMessage( player );
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
                            getLanguage().noProtectionArea.sendMessage( player );
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
                                getLanguage().accessException.sendMessage( player );
                            } catch ( PlayerNotExistsException e )
                            {
                                getLanguage().playerNotExistsException.sendMessage( player, values );
                            } catch ( MemberNotExistsException e )
                            {
                                getLanguage().memberNotExistsException.sendMessage( player, values );
                            } catch ( KickYourSelfException e )
                            {
                                getLanguage().kickYourSelfException.sendMessage( player );
                            }
                            break;
                        }
                        getLanguage().noProtectionArea.sendMessage( player );
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
                            getLanguage().accessException.sendMessage( sender );
                        } catch ( PlayerNotExistsException e )
                        {
                            getLanguage().playerNotExistsException.sendMessage( sender );
                        } catch ( MemberNotExistsException e )
                        {
                            getLanguage().memberNotExistsException.sendMessage( sender );
                        } catch ( KickYourSelfException e )
                        {
                            getLanguage().kickYourSelfException.sendMessage( sender );
                        }
                        break;
                    }
                    getLanguage().protectionAreaNotExists.sendMessage( sender, new String[]{ "{id}" }, new String[]{ args[ 2 ] } );
                    break;
                }
                case "accept":
                {
                    if ( !sender.hasPermission( cmd.getPermission() + "." + args[ 0 ].toLowerCase() ) )
                    {
                        getLanguage().accessException.sendMessage( sender );
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
                        getLanguage().noInvitesException.sendMessage( ( Player ) sender );
                    }
                    break;
                }
                case "help":
                {
                    if ( !sender.hasPermission( cmd.getPermission() + "." + args[ 0 ].toLowerCase() ) )
                    {
                        language.getModel().accessException.sendMessage( sender );
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
                                    getLanguage().accessException.sendMessage( sender );
                                    break;
                                }
                                new OglofusMessage( "&aDescription: Get the OglofusProtection version", "&aΠεριγραφή: Εμφάνισε την εκδοχή του OglofusProtection" ).sendMessage( sender );
                                new OglofusMessage( "&aUsage: /" + s + " " + args[ 1 ].toLowerCase(), "&aΧρήση: /" + s + " " + args[ 1 ].toLowerCase() ).sendMessage( sender );
                                break;
                            }
                            case "reload":
                            {
                                if ( !sender.hasPermission( cmd.getPermission() + "." + args[ 1 ].toLowerCase() ) )
                                {
                                    getLanguage().accessException.sendMessage( sender );
                                    break;
                                }
                                new OglofusMessage( "&aDescription: Reload the OglofusProtection", "&aΠεριγραφή: Ανάγκασε το σύστημα να διαβάζει ξανά όλα τα αρχεία διαμόρφωσης" ).sendMessage( sender );
                                new OglofusMessage( "&aUsage: /" + s + " " + args[ 1 ].toLowerCase(), "&aΧρήση: /" + s + " " + args[ 1 ].toLowerCase() ).sendMessage( sender );
                                break;
                            }
                            case "save":
                            {
                                if ( !sender.hasPermission( cmd.getPermission() + "." + args[ 1 ].toLowerCase() ) )
                                {
                                    getLanguage().accessException.sendMessage( sender );
                                    break;
                                }
                                new OglofusMessage( "&aDescription: Reload the OglofusProtection", "&aΠεριγραφή: Ανάγκασε το σύστημα να αποθηκεύσει όλες τις προστατευόμενες περιοχές" ).sendMessage( sender );
                                new OglofusMessage( "&aUsage: /" + s + " " + args[ 1 ].toLowerCase(), "&aΧρήση: /" + s + " " + args[ 1 ].toLowerCase() ).sendMessage( sender );
                                break;
                            }
                            case "give":
                            {
                                if ( !sender.hasPermission( cmd.getPermission() + "." + args[ 1 ].toLowerCase() ) )
                                {
                                    getLanguage().accessException.sendMessage( sender );
                                    break;
                                }
                                new OglofusMessage( "&aDescription: Giving to you or somebody some protection blocks", "&aΠεριγραφή: Δώσε σε εσένα ή σε κάποιον άλλον μερικά protection blocks" ).sendMessage( sender );
                                new OglofusMessage( "&aUsage: /" + s + " " + args[ 1 ].toLowerCase() + " <amount> [<player>]", "&aΧρήση: /" + s + " " + args[ 1 ].toLowerCase() + " <ποσό> [<παίκτης>]" ).sendMessage( sender );
                                break;
                            }
                            case "invite":
                            {
                                if ( !sender.hasPermission( cmd.getPermission() + "." + args[ 1 ].toLowerCase() ) )
                                {
                                    getLanguage().accessException.sendMessage( sender );
                                    break;
                                }
                                new OglofusMessage( "&aDescription: Invite a player at your region", "&aΠεριγραφή: Προσκάλεσε κάποιον παίκτη στην περιοχή σου" ).sendMessage( sender );
                                new OglofusMessage( "&aUsage: /" + s + " " + args[ 1 ].toLowerCase() + " <player> [<id>]", "&aΧρήση: /" + s + " " + args[ 1 ].toLowerCase() + " <παίκτης> [<id>]" ).sendMessage( sender );
                                break;
                            }
                            case "info":
                            {
                                if ( !sender.hasPermission( cmd.getPermission() + "." + args[ 1 ].toLowerCase() ) )
                                {
                                    getLanguage().accessException.sendMessage( sender );
                                    break;
                                }
                                new OglofusMessage( "&aDescription: Displaying the information in your region", "&aΠεριγραφή: Εμφάνιση των πληροφοριών της περιοχή σου" ).sendMessage( sender );
                                new OglofusMessage( "&aUsage: /" + s + " " + args[ 1 ].toLowerCase() + " [<id>]", "&aΧρήση: /" + s + " " + args[ 1 ].toLowerCase() + " [<id>]" ).sendMessage( sender );
                                break;
                            }
                            case "kick":
                            {
                                if ( !sender.hasPermission( cmd.getPermission() + "." + args[ 1 ].toLowerCase() ) )
                                {
                                    getLanguage().accessException.sendMessage( sender );
                                    break;
                                }
                                new OglofusMessage( "&aDescription: Banish a player from your region", "&aΠεριγραφή: Διώξε έναν παίκτη από την περιοχή σου" ).sendMessage( sender );
                                new OglofusMessage( "&aUsage: /" + s + " " + args[ 1 ].toLowerCase() + " <player> [<id>]", "&aΧρήση: /" + s + " " + args[ 1 ].toLowerCase() + " <παίκτης> [<id>]" ).sendMessage( sender );
                                break;
                            }
                            case "accept":
                            {
                                if ( !sender.hasPermission( cmd.getPermission() + "." + args[ 1 ].toLowerCase() ) )
                                {
                                    getLanguage().accessException.sendMessage( sender );
                                    break;
                                }
                                new OglofusMessage( "&aDescription: Accept a request to add in other region", "&aΠεριγραφή: Αποδέξου ένα αίτημα προσθήκης στην περιοχή κάποιου" ).sendMessage( sender );
                                new OglofusMessage( "&aUsage: /" + s + " " + args[ 1 ].toLowerCase(), "&aΧρήση: /" + s + " " + args[ 1 ].toLowerCase() ).sendMessage( sender );
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
