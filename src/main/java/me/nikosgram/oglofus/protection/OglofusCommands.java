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

import com.sk89q.minecraft.util.commands.*;
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
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.*;

import static com.sk89q.worldguard.bukkit.WGBukkit.getRegionManager;
import static me.nikosgram.oglofus.protection.OglofusProtection.*;

public class OglofusCommands
{
    @Command( aliases = { "version" }, desc = "Get the Oglofus-Protection version", max = 0 )
    @CommandPermissions( { "oglofus.protection.command.version" } )
    @Logging( Logging.LogMode.ALL )
    @Console
    public static void version( CommandContext context, CommandSender sender ) throws org.bukkit.command.CommandException
    {
        sender.sendMessage( ChatColor.YELLOW + getPlugin().getDescription().getName() + " " + getPlugin().getDescription().getVersion() );
        new OglofusMessage( "&eAuthor: niksogram13", "&eΔημιουργός: nikosgram13" ).sendMessage( sender );
    }

    @Command( aliases = { "reload" }, desc = "Reload the Oglofus-Protection", max = 0 )
    @CommandPermissions( { "oglofus.protection.command.reload" } )
    @Logging( Logging.LogMode.ALL )
    @Console
    public static void reload( CommandContext context, CommandSender sender ) throws org.bukkit.command.CommandException
    {
        new OglofusMessage( "&eThe system reloading...", "&eΤο σύστημα διαβάζει ξανά τα αρχεία διαμόρφωσης..." ).sendMessage( sender );
        configurationAction( OglofusProtection.ConfigurationAction.RELOAD );
        new OglofusMessage( "&aReloading completed!", "&aΗ διαδικασία ολοκληρώθηκε!" ).sendMessage( sender );
    }

    @Command( aliases = { "save" }, desc = "Force save the protection regions", max = 0 )
    @CommandPermissions( { "oglofus.protection.command.save" } )
    @Logging( Logging.LogMode.ALL )
    @Console
    public static void save( CommandContext context, CommandSender sender ) throws org.bukkit.command.CommandException
    {
        new OglofusMessage( "&eThe system saving the regions...", "&eΤο σύστημα αποθηκεύει όλες τις προστατευόμενες περιοχές..." ).sendMessage( sender );
        ProtectionSystem.saveChanges();
        new OglofusMessage( "&aSaving completed!", "&aΗ διαδικασία ολοκληρώθηκε!" ).sendMessage( sender );
    }

    @Command( aliases = { "give" }, usage = "<amount> [<player>]", desc = "Giving to you some protection blocks!", min = 0, max = 2 )
    @CommandPermissions( { "oglofus.protection.command.give" } )
    @Logging( Logging.LogMode.ALL )
    @Console
    public static void give( CommandContext context, CommandSender sender ) throws org.bukkit.command.CommandException
    {
        Material       material = getConfiguration().getProtectionBlock();
        ItemMeta       itemMeta = Bukkit.getItemFactory().getItemMeta( material );
        List< String > lore     = new ArrayList<>();

        lore.add( 0, getConfiguration().protectionMetaData );

        itemMeta.setLore( lore );
        ItemStack stack;
        if ( context.argsLength() >= 1 )
        {
            stack = new ItemStack( material, context.getInteger( 0 ) );
        } else
        {
            stack = new ItemStack( material );
        }
        stack.setItemMeta( itemMeta );

        Player player;
        if ( context.argsLength() >= 2 )
        {
            if ( ( player = Bukkit.getPlayer( context.getString( 1 ) ) ) == null )
            {
                getLanguage().playerOfflineException.sendMessage( sender, new String[]{ "player" }, new String[]{ context.getString( 1 ) } );
                return;
            }
        } else
        {
            if ( !( sender instanceof Player ) )
            {
                sender.sendMessage( ChatColor.RED + "Cannot execute that command, I don't know who you are!" );
                return;
            }
            player = ( Player ) sender;
        }
        player.getInventory().addItem( stack );
    }

    @Command( aliases = { "invite" }, usage = "<player> [<id>]", desc = "Invite a player at your region!", min = 1, max = 2 )
    @CommandPermissions( { "oglofus.protection.command.invite" } )
    @Logging( Logging.LogMode.ALL )
    @Console
    public static void invite( CommandContext context, CommandSender sender ) throws org.bukkit.command.CommandException
    {
        if ( context.argsLength() < 2 )
        {
            if ( !( sender instanceof Player ) )
            {
                sender.sendMessage( ChatColor.RED + "Cannot execute that command, I don't know who you are!" );
                sender.sendMessage( ChatColor.AQUA + "You can use /protection invite <username> <id>" );
                return;
            }
            Player player = ( Player ) sender;
            if ( !getConfiguration().allowWorld( player.getWorld() ) )
            {
                getLanguage().noProtectionArea.sendMessage( player );
                return;
            }
            List< String > local_regions;
            if ( ( local_regions = getRegionManager( player.getWorld() ).getApplicableRegionsIDs( WorldGuardPlugin.inst().wrapPlayer( player ).getPosition() ) ).size() < 1 )
            {
                getLanguage().noAreas.sendMessage( player );
                return;
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
                return;
            }
            ProtectionArea area;
            if ( ( area = ProtectionSystem.getProtectionArea( uuid ) ) != null )
            {
                Map< String, String > values = new HashMap<>();
                values.put( "player", context.getString( 0 ) );
                try
                {
                    area.invite( sender, context.getString( 0 ) );
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
                return;
            }
            getLanguage().noProtectionArea.sendMessage( ( ( Player ) sender ) );
            return;
        }
        if ( sender instanceof Player )
        {
            throw new org.bukkit.command.CommandException();
        }
        ProtectionArea area;
        if ( ( area = ProtectionSystem.getProtectionArea( UUID.fromString( context.getString( 1 ) ) ) ) != null )
        {
            try
            {
                area.invite( sender, context.getString( 0 ) );
            } catch ( AlreadyMemberException | PlayerOfflineException | AccessException | InviteYourSelfException e )
            {
                sender.sendMessage( e.getMessage() );
            }
            return;
        }

        getLanguage().protectionAreaNotExists.sendMessage( sender, new String[]{ "id" }, new String[]{ context.getString( 1 ) } );
    }

    @Command( aliases = { "info" }, usage = "[<id>]", desc = "Get info from a Protection Area", max = 1 )
    @CommandPermissions( { "oglofus.protection.command.info" } )
    @Logging( Logging.LogMode.ALL )
    @Console
    public static void info( CommandContext context, CommandSender sender ) throws org.bukkit.command.CommandException
    {
        if ( context.argsLength() < 1 )
        {
            if ( !( sender instanceof Player ) )
            {
                sender.sendMessage( ChatColor.RED + "Cannot execute that command, I don't know who you are!" );
                sender.sendMessage( ChatColor.AQUA + "You can use /protection info <id>" );
                return;
            }
            Player player = ( Player ) sender;
            if ( !getConfiguration().allowWorld( player.getWorld() ) )
            {
                getLanguage().noProtectionArea.sendMessage( player );
                return;
            }
            List< String > local_regions;
            if ( ( local_regions = getRegionManager( player.getWorld() ).getApplicableRegionsIDs( WorldGuardPlugin.inst().wrapPlayer( player ).getPosition() ) ).size() < 1 )
            {
                getLanguage().noAreas.sendMessage( player );
                return;
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
                return;
            }
            ProtectionArea area;
            if ( ( area = ProtectionSystem.getProtectionArea( uuid ) ) != null )
            {
                ProtectedRegion region = area.getRegion();

                Map< String, String > values = new HashMap< String, String >();

                values.put( "n", "\n" );
                values.put( "id", area.getUuid().toString() );
                List< String > members = new ArrayList< String >();
                for ( OfflinePlayer member : area.getMembers() )
                {
                    members.add( member.getName() );
                }
                List< String > owners = new ArrayList< String >();
                for ( OfflinePlayer owner : area.getOwners() )
                {
                    members.add( owner.getName() );
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
                return;
            }
            getLanguage().noProtectionArea.sendMessage( player );
            return;
        }
        if ( sender instanceof Player )
        {
            throw new org.bukkit.command.CommandException();
        }
        ProtectionArea area;
        if ( ( area = ProtectionSystem.getProtectionArea( UUID.fromString( context.getString( 0 ) ) ) ) != null )
        {
            ProtectedRegion region = area.getRegion();
            Map< String, String > values = new HashMap< String, String >();

            values.put( "n", "\n" );
            values.put( "id", area.getUuid().toString() );
            List< String > members = new ArrayList< String >();
            for ( OfflinePlayer member : area.getMembers() )
            {
                members.add( member.getName() );
            }
            List< String > owners = new ArrayList< String >();
            for ( OfflinePlayer owner : area.getOwners() )
            {
                members.add( owner.getName() );
            }
            values.put( "owners", StringUtils.join( owners, ", " ) );
            values.put( "members", StringUtils.join( members, ", " ) );
            values.put( "location", area.getLocation().getX() + "," + area.getLocation().getBlockY() + "," + area.getLocation().getBlockZ() );
            values.put( "vector", region.getMinimumPoint().toString() + ' ' + region.getMaximumPoint().toString() );

            getLanguage().ownerInfo.sendMessage( sender, values );
            return;
        }

        getLanguage().protectionAreaNotExists.sendMessage( sender, new String[]{ "id" }, new String[]{ context.getString( 0 ) } );
    }

    @Command( aliases = { "kick" }, usage = "<player> [<id>]", desc = "Kick a player at your Protection Area", min = 1, max = 2 )
    @CommandPermissions( { "oglofus.protection.command.kick" } )
    @Logging( Logging.LogMode.ALL )
    @Console
    public static void kick( CommandContext context, CommandSender sender ) throws org.bukkit.command.CommandException
    {
        if ( context.argsLength() < 2 )
        {
            if ( !( sender instanceof Player ) )
            {
                sender.sendMessage( ChatColor.RED + "Cannot execute that command, I don't know who you are!" );
                sender.sendMessage( ChatColor.AQUA + "You can use /protection invite <username> <id>" );
                return;
            }
            Player player = ( Player ) sender;
            if ( !getConfiguration().allowWorld( player.getWorld() ) )
            {
                getLanguage().noProtectionArea.sendMessage( player );
                return;
            }
            List< String > local_regions;
            if ( ( local_regions = getRegionManager( player.getWorld() ).getApplicableRegionsIDs( WorldGuardPlugin.inst().wrapPlayer( player ).getPosition() ) ).size() < 1 )
            {
                getLanguage().noAreas.sendMessage( player );
                return;
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
                return;
            }
            ProtectionArea area;
            if ( ( area = ProtectionSystem.getProtectionArea( uuid ) ) != null )
            {
                Map< String, String > values = new HashMap<>();
                values.put( "player", context.getString( 0 ) );
                try
                {
                    area.kick( player, context.getString( 0 ) );
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
                return;
            }
            getLanguage().noProtectionArea.sendMessage( player );
            return;
        }
        if ( sender instanceof Player )
        {
            throw new org.bukkit.command.CommandException();
        }
        ProtectionArea area;
        if ( ( area = ProtectionSystem.getProtectionArea( UUID.fromString( context.getString( 1 ) ) ) ) != null )
        {
            try
            {
                area.kick( sender, context.getString( 0 ) );
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
            return;
        }
        getLanguage().protectionAreaNotExists.sendMessage( sender, new String[]{ "{id}" }, new String[]{ context.getString( 1 ) } );
    }

    @Command( aliases = { "accept" }, desc = "Accept a protection area invite", max = 0 )
    @CommandPermissions( { "oglofus.protection.command.accept" } )
    @Logging( Logging.LogMode.ALL )
    public static void accept( CommandContext context, CommandSender sender ) throws org.bukkit.command.CommandException
    {
        if ( !( sender instanceof Player ) )
        {
            sender.sendMessage( ChatColor.RED + "Cannot execute that command, I don't know who you are!" );
            return;
        }

        try
        {
            InviteSystem.accept( ( Player ) sender );
        } catch ( NoInvitesException e )
        {
            getLanguage().noInvitesException.sendMessage( ( Player ) sender );
        }
    }
}
