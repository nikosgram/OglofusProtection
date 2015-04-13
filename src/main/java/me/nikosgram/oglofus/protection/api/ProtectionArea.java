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
    UUID getUuid();

    ProtectedRegion getRegion();

    RegionManager getRegionManager();

    World getWorld();

    Location getLocation();

    Collection< OfflinePlayer > getOwners();

    Collection< Player > getOnlineOwners();

    Collection< UUID > getOwnersUuid();

    Collection< OfflinePlayer > getMembers();

    Collection< Player > getOnlineMembers();

    Collection< UUID > getMembersUuid();

    Collection< OfflinePlayer > getPlayers();

    Collection< Player > getOnlinePlayers();

    Collection< Player > getOnlinePlayers( ProtectionRank rank );

    Collection< UUID > getPlayersUuid();

    @Deprecated
    boolean isOwner( String target );

    boolean isOwner( OfflinePlayer target );

    boolean isOwner( UUID target );

    @Deprecated
    boolean isMember( String target );

    boolean isMember( OfflinePlayer target );

    boolean isMember( UUID target );

    boolean hasOwnerAccess( Player target );

    boolean hasMemberAccess( Player target );

    Collection< Block > getBlocks();

    Collection< Entity > getEntities();

    Collection< Location > getBlocksLocations();

    @Deprecated
    Player getMember( String username );

    @Deprecated
    OfflinePlayer getOfflineMember( String username );

    Player getMember( UUID uuid );

    OfflinePlayer getOfflineMember( UUID uuid );

    @Deprecated
    ProtectionRank getRank( String target );

    ProtectionRank getRank( Player target );

    ProtectionRank getRank( UUID target );

    void broadcast( String message );

    void broadcast( String message, ProtectionRank rank );

    void reFlag();

    @Deprecated
    void invite( CommandSender sender, String target );

    void invite( CommandSender sender, Player target );

    void invite( CommandSender sender, UUID target );

    @Deprecated
    void invite( String target );

    void invite( Player target );

    void invite( UUID target );

    @Deprecated
    void kick( CommandSender sender, String target );

    void kick( CommandSender sender, OfflinePlayer target );

    void kick( CommandSender sender, UUID target );

    @Deprecated
    void kick( String target );

    void kick( OfflinePlayer target );

    void kick( UUID target );
}
