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

package me.nikosgram.oglofus.protection.api;

import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import java.util.Collection;
import java.util.UUID;

public interface ProtectionArea
{
    /**
     * Get the id from the ProtectionArea.
     *
     * @return the id
     */
    UUID getUuid();

    /**
     * Get this ProtectionArea as {@link ProtectedRegion}
     *
     * @return the region
     */
    ProtectedRegion getRegion();

    /**
     * Get the ProtectionArea's {@link RegionManager}
     *
     * @return the RegionManager
     */
    RegionManager getRegionManager();

    /**
     * Get the ProtectionArea's {@link World}
     *
     * @return the world
     */
    World getWorld();

    /**
     * Get the location from ProtectionBlock
     *
     * @return the ProtectionBlock's location
     */
    Location getLocation();

    /**
     * Get the owners as {@link OfflinePlayer}s
     *
     * @return the owners
     */
    Collection< OfflinePlayer > getOwners();

    /**
     * Get the online owners
     *
     * @return the online owners
     */
    Collection< Player > getOnlineOwners();

    /**
     * Get the owners ids
     *
     * @return owners as {@link UUID}
     */
    Collection< UUID > getOwnersUuid();

    /**
     * Get the members as {@link OfflinePlayer}s
     *
     * @return the members
     */
    Collection< OfflinePlayer > getMembers();

    /**
     * Get the online members
     *
     * @return the online members
     */
    Collection< Player > getOnlineMembers();

    /**
     * Get the members ids
     *
     * @return the members as {@link UUID}
     */
    Collection< UUID > getMembersUuid();

    /**
     * Get all staff of the ProtectionArea (Owners and Members) as {@link OfflinePlayer}
     *
     * @return the staff
     */
    Collection< OfflinePlayer > getPlayers();

    /**
     * Get the online staff
     *
     * @return the online staff
     */
    Collection< Player > getOnlinePlayers();

    /**
     * Get the online staff with {@link ProtectionRank}
     *
     * @param rank {@link ProtectionRank}
     * @return the online staff
     */
    Collection< Player > getOnlinePlayers( ProtectionRank rank );

    /**
     * Get the staff's ids
     *
     * @return the staff as {@link UUID}
     */
    Collection< UUID > getPlayersUuid();

    /**
     * Check if a player is owner.
     *
     * @param target the player's username
     * @return true if the player is owner
     */
    @Deprecated
    boolean isOwner( String target );

    /**
     * Check if a player is owner.
     *
     * @param target the {@link OfflinePlayer}
     * @return true if the player is owner
     */
    boolean isOwner( OfflinePlayer target );

    /**
     * Check if a player is owner.
     *
     * @param target the {@link UUID}
     * @return true if the player is owner
     */
    boolean isOwner( UUID target );

    /**
     * Check if a player is member.
     *
     * @param target the player's username
     * @return true if the player is member
     */
    @Deprecated
    boolean isMember( String target );

    /**
     * Check if a player is owner.
     *
     * @param target the {@link OfflinePlayer}
     * @return true if the player is owner
     */
    boolean isMember( OfflinePlayer target );

    /**
     * Check if a player is owner.
     *
     * @param target the {@link UUID}
     * @return true if the player is owner
     */
    boolean isMember( UUID target );

    /**
     * Check if a player has owner access.
     *
     * @param target the {@link OfflinePlayer}
     * @return true if the player has owner access
     */
    boolean hasOwnerAccess( OfflinePlayer target );

    /**
     * Check if a player has member access.
     *
     * @param target the {@link OfflinePlayer}
     * @return true if the player has member access
     */
    boolean hasMemberAccess( OfflinePlayer target );

    /**
     * Check if a player has owner access.
     *
     * @param target the {@link UUID}
     * @return true if the player has owner access
     */
    boolean hasOwnerAccess( UUID target );

    /**
     * Check if a player has member access.
     *
     * @param target the {@link UUID}
     * @return true if the player has member access
     */
    boolean hasMemberAccess( UUID target );

    /**
     * Get all the blocks inside the {@link ProtectionArea}.
     *
     * @return the blocks
     */
    Collection< Block > getBlocks();

    /**
     * Get all the entities inside the {@link ProtectionArea}.
     *
     * @return the entities
     */
    Collection< Entity > getEntities();

    /**
     * Get all the locations inside the {@link ProtectionArea}.
     *
     * @return the locations
     */
    Collection< Location > getBlocksLocations();

    /**
     * Get member as {@link Player}
     *
     * @param username the member's username
     * @return if isn't members the player returned null else the player
     */
    @Deprecated
    Player getMember( String username );

    /**
     * Get member as {@link OfflinePlayer}
     *
     * @param username the member's username
     * @return if isn't members the player returned null else the player
     */
    @Deprecated
    OfflinePlayer getOfflineMember( String username );

    /**
     * Get member as {@link Player}
     *
     * @param uuid the member's uuid
     * @return if isn't members the player returned null else the player
     */
    Player getMember( UUID uuid );

    /**
     * Get member as {@link OfflinePlayer}
     *
     * @param uuid the member's uuid
     * @return if isn't members the player returned null else the player
     */
    OfflinePlayer getOfflineMember( UUID uuid );

    /**
     * Get the rank from {@link Player}
     *
     * @param target the player's username
     * @return the rank
     */
    @Deprecated
    ProtectionRank getRank( String target );

    /**
     * Get the rank from {@link OfflinePlayer}
     *
     * @param target the offline player
     * @return the rank
     */
    ProtectionRank getRank( OfflinePlayer target );

    /**
     * Get the rank from {@link Player}
     *
     * @param target the {@link UUID}
     * @return the rank
     */
    ProtectionRank getRank( UUID target );

    /**
     * Broadcast, to protection area's members, a message
     *
     * @param message the message
     */
    void broadcast( String message );

    /**
     * Broadcast, to protection area's members with rank, a message
     *
     * @param message the message
     * @param rank    who you want to display the message
     */
    void broadcast( String message, ProtectionRank rank );

    /**
     * Reflag this {@link ProtectionArea}
     */
    void reFlag();

    /**
     * Invite a player to join at this area
     *
     * @param sender who want to invite the player
     * @param target the player
     */
    @Deprecated
    void invite( CommandSender sender, String target );

    /**
     * Invite a player to join at this area
     *
     * @param sender who want to invite the player
     * @param target the player
     */
    void invite( CommandSender sender, OfflinePlayer target );

    /**
     * Invite a player to join at this area
     *
     * @param sender who want to invite the player
     * @param target the player
     */
    void invite( CommandSender sender, UUID target );

    /**
     * Invite a player to join at this area
     *
     * @param target the player
     */
    @Deprecated
    void invite( String target );

    /**
     * Invite a player to join at this area
     *
     * @param target the player
     */
    void invite( OfflinePlayer target );

    /**
     * Invite a player to join at this area
     *
     * @param target the player
     */
    void invite( UUID target );

    /**
     * Kick a player to join at this area
     *
     * @param sender who want to kick the player
     * @param target the player
     */
    @Deprecated
    void kick( CommandSender sender, String target );

    /**
     * Kick a player to join at this area
     *
     * @param sender who want to kick the player
     * @param target the player
     */
    void kick( CommandSender sender, OfflinePlayer target );

    /**
     * Kick a player to join at this area
     *
     * @param sender who want to kick the player
     * @param target the player
     */
    void kick( CommandSender sender, UUID target );

    /**
     * Kick a player to join at this area
     *
     * @param target the player
     */
    @Deprecated
    void kick( String target );

    /**
     * Kick a player to join at this area
     *
     * @param target the player
     */
    void kick( OfflinePlayer target );

    /**
     * Kick a player to join at this area
     *
     * @param target the player
     */
    void kick( UUID target );
}
