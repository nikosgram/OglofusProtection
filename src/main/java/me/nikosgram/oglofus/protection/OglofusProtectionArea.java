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

import com.sk89q.worldedit.Vector;
import com.sk89q.worldguard.protection.flags.*;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.managers.storage.StorageException;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import me.nikosgram.oglofus.protection.api.InviteSystem;
import me.nikosgram.oglofus.protection.api.ProtectionArea;
import me.nikosgram.oglofus.protection.api.ProtectionRank;
import me.nikosgram.oglofus.protection.api.ProtectionSystem;
import me.nikosgram.oglofus.protection.api.event.protection.KickMemberEvent;
import me.nikosgram.oglofus.protection.api.exception.member.InviteYourSelfException;
import me.nikosgram.oglofus.protection.api.exception.member.KickYourSelfException;
import me.nikosgram.oglofus.protection.api.exception.member.MemberNotExistsException;
import me.nikosgram.oglofus.protection.api.exception.player.AccessException;
import me.nikosgram.oglofus.protection.api.exception.player.PlayerNotExistsException;
import me.nikosgram.oglofus.protection.api.exception.player.PlayerOfflineException;
import me.nikosgram.oglofus.protection.api.exception.protection.ProtectionRegionNullableException;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Collection;
import java.util.UUID;

import static com.sk89q.worldguard.bukkit.WorldGuardPlugin.inst;
import static me.nikosgram.oglofus.protection.OglofusProtection.*;

public class OglofusProtectionArea implements ProtectionArea
{
    public UUID                      regionUID;
    public OglofusProtectionLocation location;

    public OglofusProtectionArea() { }

    public OglofusProtectionArea( ProtectedRegion region, Location location )
    {
        regionUID = UUID.fromString( region.getId() );
        this.location = new OglofusProtectionLocation( location );
    }

    public OglofusProtectionArea( UUID regionUID, Location location )
    {
        this.regionUID = regionUID;
        this.location = new OglofusProtectionLocation( location );
    }

    public OglofusProtectionArea( UUID regionUID, int x, int y, int z, UUID worldUID )
    {
        this.regionUID = regionUID;
        location = new OglofusProtectionLocation( x, y, z, worldUID );
    }

    @Override
    public UUID getUuid()
    {
        return regionUID;
    }

    @Override
    public ProtectedRegion getRegion()
    {
        return getRegionManager().getRegion( regionUID.toString() );
    }

    @Override
    public RegionManager getRegionManager()
    {
        return inst().getRegionManager( getWorld() );
    }

    @Override
    public World getWorld()
    {
        return location.getWorld();
    }

    @Override
    public Location getLocation()
    {
        return location.toLocation();
    }

    @Override
    public Collection< OfflinePlayer > getOwners()
    {
        Collection< OfflinePlayer > owners = new ArrayList< OfflinePlayer >();
        for ( UUID uuid : getOwnersUuid() )
        {
            OfflinePlayer owner;
            if ( ( owner = Bukkit.getOfflinePlayer( uuid ) ) != null )
            {
                owners.add( owner );
            }
        }
        return owners;
    }

    @Override
    public Collection< Player > getOnlineOwners()
    {
        Collection< Player > owners = new ArrayList< Player >();
        for ( UUID uuid : getOwnersUuid() )
        {
            Player owner;
            if ( ( owner = Bukkit.getPlayer( uuid ) ) != null )
            {
                owners.add( owner );
            }
        }
        return owners;
    }

    @Override
    public Collection< UUID > getOwnersUuid()
    {
        return getRegion().getOwners().getUniqueIds();
    }

    @Override
    public Collection< OfflinePlayer > getMembers()
    {
        Collection< OfflinePlayer > members = new ArrayList< OfflinePlayer >();
        for ( UUID uuid : getMembersUuid() )
        {
            OfflinePlayer member;
            if ( ( member = Bukkit.getOfflinePlayer( uuid ) ) != null )
            {
                members.add( member );
            }
        }
        return members;
    }

    @Override
    public Collection< Player > getOnlineMembers()
    {
        Collection< Player > members = new ArrayList< Player >();
        for ( UUID uuid : getMembersUuid() )
        {
            Player member;
            if ( ( member = Bukkit.getPlayer( uuid ) ) != null )
            {
                members.add( member );
            }
        }
        return members;
    }

    @Override
    public Collection< UUID > getMembersUuid()
    {
        return getRegion().getMembers().getUniqueIds();
    }

    @Override
    public Collection< OfflinePlayer > getPlayers()
    {
        Collection< OfflinePlayer > players = new ArrayList< OfflinePlayer >();
        for ( UUID uuid : getPlayersUuid() )
        {
            OfflinePlayer player;
            if ( ( player = Bukkit.getOfflinePlayer( uuid ) ) != null )
            {
                players.add( player );
            }
        }
        return players;
    }

    @Override
    public Collection< Player > getOnlinePlayers()
    {
        Collection< Player > players = new ArrayList< Player >();
        for ( UUID uuid : getPlayersUuid() )
        {
            Player player;
            if ( ( player = Bukkit.getPlayer( uuid ) ) != null )
            {
                players.add( player );
            }
        }
        return players;
    }

    @Override
    public Collection< UUID > getPlayersUuid()
    {
        Collection< UUID > returned = new ArrayList< UUID >( getOwnersUuid() );
        returned.addAll( getMembersUuid() );
        return returned;
    }

    @Override
    @Deprecated
    public boolean isOwner( String target )
    {
        return isOwner( notNull( Bukkit.getOfflinePlayer( notNull( target ) ), "The player '" + target + "' doesn't exists." ) );
    }

    @Override
    public boolean isOwner( OfflinePlayer target )
    {
        return getOwnersUuid().contains( notNull( target ).getUniqueId() );
    }

    @Override
    public boolean isOwner( UUID target )
    {
        return getOwnersUuid().contains( notNull( target ) );
    }

    @Override
    @Deprecated
    public boolean isMember( String target )
    {
        return isMember( notNull( Bukkit.getOfflinePlayer( notNull( target ) ), "The player '" + target + "' doesn't exists." ) );
    }

    @Override
    public boolean isMember( OfflinePlayer target )
    {
        return getMembersUuid().contains( notNull( target ).getUniqueId() );
    }

    @Override
    public boolean isMember( UUID target )
    {
        return getMembersUuid().contains( notNull( target ) );
    }

    @Override
    public boolean hasOwnerAccess( Player target )
    {
        return notNull( target ).isOp() || target.hasPermission( "oglofus.protection.bypass" ) || isOwner( target );
    }

    @Override
    public boolean hasMemberAccess( Player target )
    {
        return notNull( target ).isOp() || target.hasPermission( "oglofus.protection.bypass" ) || isMember( target );
    }

    @Override
    public Collection< Block > getBlocks()
    {
        Collection< Block > blocks = new ArrayList< Block >();
        for ( Location location : getBlocksLocations() )
        {
            blocks.add( location.getBlock() );
        }
        return blocks;
    }

    @Override
    public Collection< Entity > getEntities()
    {
        Collection< Entity > entities = new ArrayList< Entity >();
        for ( Location location : getBlocksLocations() )
        {
            for ( Entity entity : location.getChunk().getEntities() )
            {
                if ( entity.getLocation().lengthSquared() == location.lengthSquared() )
                {
                    entities.add( entity );
                }
            }
        }
        return entities;
    }

    @Override
    public Collection< Location > getBlocksLocations()
    {
        Collection< Location > returned = new ArrayList< Location >();

        ProtectedRegion region     = getRegion();
        Vector          min_vector = region.getMinimumPoint();
        Vector          max_vector = region.getMaximumPoint();

        for ( double location_x = min_vector.getX(); location_x <= max_vector.getX(); location_x++ )
        {
            for ( double location_y = min_vector.getY(); location_y <= max_vector.getY(); location_y++ )
            {
                for ( double location_z = min_vector.getZ(); location_z <= max_vector.getZ(); location_z++ )
                {
                    returned.add( new Location( getWorld(), location_x, location_y, location_z ) );
                }
            }
        }

        return returned;
    }

    @Override
    @Deprecated
    public Player getMember( String target )
    {
        return getMember( notNull( Bukkit.getPlayer( notNull( target ) ), "The player '" + target + "' doesn't exists." ).getUniqueId() );
    }

    @Override
    @Deprecated
    public OfflinePlayer getOfflineMember( String target )
    {
        return getOfflineMember( notNull( Bukkit.getOfflinePlayer( notNull( target ) ), "The player '" + target + "' doesn't exists." ).getUniqueId() );
    }

    @Override
    public Player getMember( UUID target )
    {
        OfflinePlayer member = getOfflineMember( notNull( target ) );
        if ( member == null )
        {
            return null;
        }
        return member.getPlayer();
    }

    @Override
    public OfflinePlayer getOfflineMember( UUID target )
    {
        if ( getMembersUuid().contains( notNull( target ) ) )
        {
            return Bukkit.getOfflinePlayer( target );
        }
        return null;
    }

    @Override
    public ProtectionRank getRank( String target )
    {
        return isOwner( notNull( target ) ) ? ProtectionRank.Owner : ( isMember( target ) ? ProtectionRank.Member : ProtectionRank.None );
    }

    @Override
    public ProtectionRank getRank( Player target )
    {
        return isOwner( notNull( target ) ) ? ProtectionRank.Owner : ( isMember( target ) ? ProtectionRank.Member : ProtectionRank.None );
    }

    @Override
    public ProtectionRank getRank( UUID target )
    {
        return isOwner( notNull( target ) ) ? ProtectionRank.Owner : ( isMember( target ) ? ProtectionRank.Member : ProtectionRank.None );
    }

    @Override
    public void broadcast( String message )
    {
        broadcast( message, ProtectionRank.None );
    }

    @Override
    public void broadcast( String message, ProtectionRank rank )
    {
        notNull( message );
        switch ( notNull( rank ) )
        {
            case Member:
            {
                for ( Player player : getOnlineMembers() )
                {
                    player.sendRawMessage( reformMessage( message ) );
                }
                break;
            }
            case Owner:
            {
                for ( Player player : getOnlineOwners() )
                {
                    player.sendRawMessage( reformMessage( message ) );
                }
                break;
            }
            case None:
            {
                for ( Player player : getOnlinePlayers() )
                {
                    player.sendRawMessage( reformMessage( message ) );
                }
                break;
            }
        }
    }

    @Override
    public void reFlag()
    {
        ProtectedRegion region = getRegion();
        if ( region == null )
        {
            getPlugin().getLogger().warning( ChatColor.RED + "The ProtectedRegion not exists. '" + regionUID + "'." );
            ProtectionSystem.removeProtectionArena( this );
            getPlugin().getLogger().warning( ChatColor.RED + "The ProtectionArea '" + regionUID + "' deleted." );
            throw new ProtectionRegionNullableException();
        }

        for ( Flag< ? > flag : region.getFlags().keySet() )
        {
            region.setFlag( flag, null );
        }

        for ( String key : getConfiguration().protectionFlags.keySet() )
        {
            Flag flag = ( Flag ) DefaultFlag.fuzzyMatchFlag( key );
            Object value = getConfiguration().protectionFlags.get( key );

            if ( flag instanceof StateFlag )
            {
                value = StateFlag.State.valueOf( getConfiguration().protectionFlags.get( key ).toString() );

            } else
                if ( flag instanceof BooleanFlag )
                {
                    value = Boolean.valueOf( getConfiguration().protectionFlags.get( key ).toString() );
                } else
                    if ( flag instanceof IntegerFlag )
                    {
                        value = Integer.valueOf( getConfiguration().protectionFlags.get( key ).toString() );
                    } else
                        if ( flag instanceof DoubleFlag )
                        {
                            value = Double.valueOf( getConfiguration().protectionFlags.get( key ).toString() );
                        }
            region.setFlag( flag, value );
        }

        try
        {
            getRegionManager().saveChanges();
        } catch ( StorageException e )
        {
            e.printStackTrace();
        }
    }

    @Override
    @Deprecated
    public void invite( CommandSender sender, String target )
    {
        Player player;
        if ( ( player = Bukkit.getPlayer( notNull( target ) ) ) == null )
        {
            throw new PlayerOfflineException( getLanguage().playerOfflineException.getMessage(), target );
        }
        invite( sender, player );
    }

    @Override
    public void invite( CommandSender sender, UUID target )
    {
        Player player;
        if ( ( player = Bukkit.getPlayer( notNull( target ) ) ) == null )
        {
            throw new PlayerOfflineException( target.toString(), getLanguage().playerOfflineException.getMessage() );
        }
        invite( sender, player );
    }

    @Override
    public void invite( CommandSender sender, Player target )
    {
        if ( sender instanceof Player )
        {
            if ( !hasOwnerAccess( ( Player ) sender ) )
            {
                throw new AccessException( getLanguage().accessException.getMessage() );
            }
            if ( sender.getName().equals( target.getName() ) )
            {
                throw new InviteYourSelfException( getLanguage().inviteYourSelfException.getMessage() );
            }
        }
        invite( target );
    }

    @Override
    @Deprecated
    public void invite( String target )
    {
        Player player;
        if ( ( player = Bukkit.getPlayer( notNull( target ) ) ) == null )
        {
            throw new PlayerOfflineException( getLanguage().playerOfflineException.getMessage(), target );
        }
        invite( player );
    }

    @Override
    public void invite( UUID target )
    {
        Player player;
        if ( ( player = Bukkit.getPlayer( notNull( target ) ) ) == null )
        {
            throw new PlayerOfflineException( getLanguage().playerOfflineException.getMessage(), target.toString() );
        }
        invite( player );
    }

    @Override
    public void invite( Player target )
    {
        InviteSystem.invite( target, this );
    }

    @Override
    @Deprecated
    public void kick( CommandSender sender, String target )
    {
        OfflinePlayer player;
        if ( ( player = Bukkit.getOfflinePlayer( notNull( target ) ) ) == null )
        {
            throw new PlayerNotExistsException( getLanguage().playerNotExistsException.getMessage(), target );
        }
        kick( sender, player );
    }

    @Override
    public void kick( CommandSender sender, UUID target )
    {
        OfflinePlayer player;
        if ( ( player = Bukkit.getOfflinePlayer( notNull( target ) ) ) == null )
        {
            throw new PlayerNotExistsException( getLanguage().playerNotExistsException.getMessage(), target.toString() );
        }
        kick( sender, player );
    }

    @Override
    public void kick( CommandSender sender, OfflinePlayer target )
    {
        if ( sender instanceof Player )
        {
            if ( !hasOwnerAccess( ( Player ) sender ) )
            {
                throw new AccessException( getLanguage().accessException.getMessage() );
            }
            if ( ( ( Player ) sender ).getUniqueId().equals( target.getUniqueId() ) )
            {
                throw new KickYourSelfException( getLanguage().kickYourSelfException.getMessage() );
            }
        }
        kick( target );
    }

    @Override
    @Deprecated
    public void kick( String target )
    {
        OfflinePlayer player;
        if ( ( player = Bukkit.getOfflinePlayer( notNull( target ) ) ) == null )
        {
            throw new PlayerNotExistsException( getLanguage().playerNotExistsException.getMessage(), target );
        }
        kick( player );
    }

    @Override
    public void kick( UUID target )
    {
        OfflinePlayer player;
        if ( ( player = Bukkit.getOfflinePlayer( notNull( target ) ) ) == null )
        {
            throw new PlayerNotExistsException( getLanguage().playerNotExistsException.getMessage(), target.toString() );
        }
        kick( player );
    }

    @Override
    public void kick( OfflinePlayer target )
    {
        if ( !isMember( notNull( target ) ) )
        {
            throw new MemberNotExistsException( getLanguage().memberNotExistsException.getMessage(), target.getName() );
        }
        getRegion().getMembers().removePlayer( target.getUniqueId() );
        Bukkit.getPluginManager().callEvent( new KickMemberEvent( this, target ) );
        try
        {
            getRegionManager().saveChanges();
        } catch ( StorageException e )
        {
            e.printStackTrace();
        }
    }
}
