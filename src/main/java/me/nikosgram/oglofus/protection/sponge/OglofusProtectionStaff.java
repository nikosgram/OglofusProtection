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

import com.google.common.base.Optional;
import lombok.Getter;
import me.nikosgram.oglofus.protection.api.action.ActionResponse;
import me.nikosgram.oglofus.protection.api.region.ProtectionRank;
import me.nikosgram.oglofus.protection.api.region.ProtectionStaff;
import me.nikosgram.oglofus.utils.OglofusUtils;
import org.spongepowered.api.entity.player.Player;
import org.spongepowered.api.entity.player.User;
import org.spongepowered.api.service.user.UserStorage;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.Texts;

import java.util.*;

public class OglofusProtectionStaff implements ProtectionStaff
{
    private final Map< UUID, ProtectionRank > staff = new HashMap< UUID, ProtectionRank >();
    @Getter
    private final UUID          owner;
    private final UUID          uuid;
    private final OglofusSponge sponge;

    protected OglofusProtectionStaff( UUID uuid, OglofusSponge sponge )
    {
        this.uuid = uuid;
        this.sponge = sponge;
        this.owner = UUID.fromString(
                this.sponge.getConnector().getString(
                        "oglofus_regions", "uuid", uuid.toString(), "owner"
                ).get()
        );
        Map< String, String > staff = this.sponge.getConnector().getStringMap(
                "oglofus_regions", "uuid", uuid.toString(), new String[]{ "player", "rank" }
        );
        for ( String uid : staff.keySet() )
        {
            this.staff.put( UUID.fromString( uid ), ProtectionRank.valueOf( staff.get( uid ) ) );
        }
    }

    /**
     * Get the region's owner.
     *
     * @param tClass get owner as who?
     * @return the owner.
     */
    @Override
    public < T > Optional< T > getOwnerAs( Class< T > tClass )
    {
        if ( OglofusUtils.equalClass( tClass, Player.class ) )
        {
            return ( Optional< T > ) sponge.getServer().getPlayer( owner );
        } else
            if ( OglofusUtils.equalClass( tClass, User.class ) )
            {
                UserStorage storage;
                if ( ( storage = sponge.getGame().getServiceManager().provide( UserStorage.class ).orNull() ) != null )
                {
                    return ( Optional< T > ) storage.get( owner ).orNull();
                }
            }
        return Optional.absent();
    }

    /**
     * Get the officers.
     *
     * @param tClass get officers as who?
     * @return the members.
     */
    @Override
    public < T > Collection< T > getOfficersAs( Class< T > tClass )
    {
        List< T > returned = new ArrayList< T >();
        if ( OglofusUtils.equalClass( tClass, Player.class ) )
        {
            for ( UUID uuid : getOfficers() )
            {
                Player player;
                if ( ( player = sponge.getServer().getPlayer( uuid ).orNull() ) != null )
                {
                    returned.add( ( T ) player );
                }
            }
        } else
            if ( OglofusUtils.equalClass( tClass, User.class ) )
            {
                UserStorage storage;
                if ( ( storage = sponge.getGame().getServiceManager().provide( UserStorage.class ).orNull() ) != null )
                {
                    for ( UUID uuid : getOfficers() )
                    {
                        User player;
                        if ( ( player = storage.get( uuid ).orNull() ) != null )
                        {
                            returned.add( ( T ) player );
                        }
                    }
                }
            }
        return returned;
    }

    /**
     * Get the officers ids.
     *
     * @return the members as {@link UUID}.
     */
    @Override
    public Collection< UUID > getOfficers()
    {
        List< UUID > returned = new ArrayList< UUID >();
        for ( UUID uuid : staff.keySet() )
        {
            if ( staff.get( uuid ).equals( ProtectionRank.Officer ) )
            {
                returned.add( uuid );
            }
        }
        return returned;
    }

    /**
     * Get the members.
     *
     * @param tClass get members as who?
     * @return the members.
     */
    @Override
    public < T > Collection< T > getMembersAs( Class< T > tClass )
    {
        List< T > returned = new ArrayList< T >();
        if ( OglofusUtils.equalClass( tClass, Player.class ) )
        {
            for ( UUID uuid : getMembers() )
            {
                Player player;
                if ( ( player = sponge.getServer().getPlayer( uuid ).orNull() ) != null )
                {
                    returned.add( ( T ) player );
                }
            }
        } else
            if ( OglofusUtils.equalClass( tClass, User.class ) )
            {
                UserStorage storage;
                if ( ( storage = sponge.getGame().getServiceManager().provide( UserStorage.class ).orNull() ) != null )
                {
                    for ( UUID uuid : getMembers() )
                    {
                        User player;
                        if ( ( player = storage.get( uuid ).orNull() ) != null )
                        {
                            returned.add( ( T ) player );
                        }
                    }
                }
            }
        return returned;
    }

    /**
     * Get the members ids.
     *
     * @return the members as {@link UUID}.
     */
    @Override
    public Collection< UUID > getMembers()
    {
        List< UUID > returned = new ArrayList< UUID >();
        for ( UUID uuid : staff.keySet() )
        {
            if ( staff.get( uuid ).equals( ProtectionRank.Member ) )
            {
                returned.add( uuid );
            }
        }
        return returned;
    }

    /**
     * Get the staff.
     *
     * @param tClass get staff as who?
     * @return the staff.
     */
    @Override
    public < T > Collection< T > getStaffAs( Class< T > tClass )
    {
        List< T > returned = new ArrayList< T >();
        if ( OglofusUtils.equalClass( tClass, Player.class ) )
        {
            for ( UUID uuid : staff.keySet() )
            {
                Player player;
                if ( ( player = sponge.getServer().getPlayer( uuid ).orNull() ) != null )
                {
                    returned.add( ( T ) player );
                }
            }
        } else
            if ( OglofusUtils.equalClass( tClass, User.class ) )
            {
                UserStorage storage;
                if ( ( storage = sponge.getGame().getServiceManager().provide( UserStorage.class ).orNull() ) != null )
                {
                    for ( UUID uuid : staff.keySet() )
                    {
                        User player;
                        if ( ( player = storage.get( uuid ).orNull() ) != null )
                        {
                            returned.add( ( T ) player );
                        }
                    }
                }
            }
        return returned;
    }

    /**
     * Get the staff ids.
     *
     * @return staff as {@link UUID}.
     */
    @Override
    public Collection< UUID > getStaff()
    {
        return staff.keySet();
    }

    /**
     * Check if a player is owner.
     *
     * @param target the {@link UUID}
     * @return true if the player is owner
     */
    @Override
    public boolean isOwner( UUID target )
    {
        return target.equals( owner );
    }

    /**
     * Check if a player is officer.
     *
     * @param target the {@link UUID}
     * @return true if the player is officer
     */
    @Override
    public boolean isOfficer( UUID target )
    {
        return staff.containsKey( target ) && staff.get( target ).equals( ProtectionRank.Officer );
    }

    /**
     * Check if a player is owner.
     *
     * @param target the {@link UUID}
     * @return true if the player is member
     */
    @Override
    public boolean isMember( UUID target )
    {
        return staff.containsKey( target ) && staff.get( target ).equals( ProtectionRank.Member );
    }

    /**
     * Check if a player is staff.
     *
     * @param target the {@link UUID}
     * @return true if the player is staff
     */
    @Override
    public boolean isStaff( UUID target )
    {
        return staff.containsKey( target );
    }

    /**
     * Check if a player has owner access.
     *
     * @param target the {@link UUID}
     * @return true if the player has owner access
     */
    @Override
    public boolean hasOwnerAccess( UUID target )
    {
        if ( target.equals( owner ) )
        {
            return true;
        }
        Player player;
        return ( player = sponge.getServer().getPlayer( target ).orNull() ) != null &&
                player.hasPermission( "oglofus.protection.bypass.owner" );
    }

    /**
     * Check if a player has officer access.
     *
     * @param target the {@link UUID}
     * @return true if the player has officer access
     */
    @Override
    public boolean hasOfficerAccess( UUID target )
    {
        if ( staff.containsKey( target ) && staff.get( target ).equals( ProtectionRank.Officer ) )
        {
            return true;
        }
        Player player;
        return ( player = sponge.getServer().getPlayer( target ).orNull() ) != null &&
                player.hasPermission( "oglofus.protection.bypass.officer" );
    }

    /**
     * Check if a player has member access.
     *
     * @param target the {@link UUID}
     * @return true if the player has member access
     */
    @Override
    public boolean hasMemberAccess( UUID target )
    {
        if ( staff.containsKey( target ) && staff.get( target ).equals( ProtectionRank.Member ) )
        {
            return true;
        }
        Player player;
        return ( player = sponge.getServer().getPlayer( target ).orNull() ) != null &&
                player.hasPermission( "oglofus.protection.bypass.member" );
    }

    /**
     * Get the rank from player
     *
     * @param target the {@link UUID}
     * @return the rank
     */
    @Override
    public ProtectionRank getRank( UUID target )
    {
        return staff.containsKey( target ) ? staff.get( target ) : ProtectionRank.None;
    }

    /**
     * Broadcast, to protection area's members, a message
     *
     * @param message the message
     */
    @Override
    public void broadcast( String message )
    {
        broadcastRaw( Texts.of( message ) );
    }

    /**
     * Broadcast, to protection area's members with rank, a message
     *
     * @param message the message
     * @param rank    who you want to display the message
     */
    @Override
    public void broadcast( String message, ProtectionRank rank )
    {
        broadcastRaw( Texts.of( message ), rank );
    }

    /**
     * Broadcast, to protection area's members, a message
     *
     * @param message the message as raw
     */
    @Override
    public void broadcastRaw( Object message )
    {
        if ( message instanceof Text )
        {
            for ( Player player : getStaffAs( Player.class ) )
            {
                player.sendMessage( ( Text ) message );
            }
        }
    }

    /**
     * Broadcast, to protection area's members with rank, a message
     *
     * @param message the message as raw
     * @param rank    who you want to display the message
     */
    @Override
    public void broadcastRaw( Object message, ProtectionRank rank )
    {
        if ( message instanceof Text )
        {
            switch ( rank )
            {
                case Member:
                    for ( Player player : getMembersAs( Player.class ) )
                    {
                        player.sendMessage( ( Text ) message );
                    }
                    break;
                case Officer:
                    for ( Player player : getOfficersAs( Player.class ) )
                    {
                        player.sendMessage( ( Text ) message );
                    }
                    break;
                case Owner:
                    Player player;
                    if ( ( player = getOwnerAs( Player.class ).orNull() ) != null )
                    {
                        player.sendMessage( ( Text ) message );
                    }
                    break;
            }
        }
    }

    /**
     * Reflag this region.
     *
     * @return the response.
     */
    @Override
    public ActionResponse reFlag()
    {
        //TODO: make it.
        return null;
    }

    /**
     * Invite a player to join at this area
     *
     * @param sender who want to invite the player
     * @param target the player
     * @return the response.
     */
    @Override
    public ActionResponse invite( UUID sender, UUID target )
    {
        return null;
    }

    /**
     * Invite a player to join at this area
     *
     * @param target the player
     * @return the response.
     */
    @Override
    public ActionResponse invite( UUID target )
    {
        return null;
    }

    /**
     * Kick a player to join at this area
     *
     * @param sender who want to kick the player
     * @param target the player
     * @return the response.
     */
    @Override
    public ActionResponse kick( UUID sender, UUID target )
    {
        return null;
    }

    /**
     * Kick a player to join at this area
     *
     * @param target the player
     * @return the response.
     */
    @Override
    public ActionResponse kick( UUID target )
    {
        return null;
    }

    /**
     * Promote a Member to Officer in this region.
     *
     * @param sender who want to promote the player
     * @param target the player
     * @return the response.
     */
    @Override
    public ActionResponse promote( UUID sender, UUID target )
    {
        return null;
    }

    /**
     * Promote a member to Officer in this region.
     *
     * @param target the player
     * @return the response.
     */
    @Override
    public ActionResponse promote( UUID target )
    {
        return null;
    }

    /**
     * Demote a Officer to Member in this region.
     *
     * @param sender who want to demote the player
     * @param target the player
     * @return the response.
     */
    @Override
    public ActionResponse demote( UUID sender, UUID target )
    {
        return null;
    }

    /**
     * Demote a Officer to Member in this region.
     *
     * @param target the player
     * @return the response.
     */
    @Override
    public ActionResponse demote( UUID target )
    {
        return null;
    }

    /**
     * Change the rank from a player in this region.
     *
     * @param sender who want to change the rank from the player
     * @param target the player
     * @param rank   the rank
     * @return the response.
     */
    @Override
    public ActionResponse changeRank( UUID sender, UUID target, ProtectionRank rank )
    {
        return null;
    }

    /**
     * Change the rank from a player in this region.
     *
     * @param target the player
     * @param rank   the rank
     * @return the response.
     */
    @Override
    public ActionResponse changeRank( UUID target, ProtectionRank rank )
    {
        return null;
    }
}
