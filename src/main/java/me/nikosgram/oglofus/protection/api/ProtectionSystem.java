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

import com.sk89q.worldedit.BlockVector;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.domains.DefaultDomain;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.managers.storage.StorageException;
import com.sk89q.worldguard.protection.regions.ProtectedCuboidRegion;
import me.nikosgram.oglofus.configuration.ConfigurationDriver;
import me.nikosgram.oglofus.protection.OglofusProtection;
import me.nikosgram.oglofus.protection.OglofusProtectionArea;
import me.nikosgram.oglofus.protection.OglofusProtections;
import me.nikosgram.oglofus.protection.api.event.player.PreProtectionPlaceEvent;
import me.nikosgram.oglofus.protection.api.event.protection.PreProtectionBreakEvent;
import me.nikosgram.oglofus.protection.api.event.protection.ProtectionBreakEvent;
import me.nikosgram.oglofus.protection.api.event.protection.ProtectionPlaceEvent;
import me.nikosgram.oglofus.protection.api.exception.player.AccessException;
import me.nikosgram.oglofus.protection.api.exception.player.MaxProtectionAreasException;
import me.nikosgram.oglofus.protection.api.exception.protection.*;
import me.nikosgram.oglofus.protection.api.exception.protection.world.ProtectionWorldException;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.inventory.meta.ItemMeta;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.UUID;

import static com.sk89q.worldguard.bukkit.WGBukkit.getRegionManager;
import static me.nikosgram.oglofus.protection.OglofusProtection.*;

public final class ProtectionSystem
{
    private static ConfigurationDriver< OglofusProtections > configurationSystem = null;

    public static void invoke( OglofusProtection plugin )
    {
        if ( configurationSystem != null )
        {
            throw new RuntimeException( "You don't have access to do that." );
        }
        configurationSystem = new ConfigurationDriver< OglofusProtections >( OglofusProtections.class, plugin.getDataFolder().toPath() ).load();
    }

    public static Collection< ProtectionArea > getProtectionAreas()
    {
        Collection< ProtectionArea > areas = new ArrayList< ProtectionArea >();
        for ( ProtectionArea block : configurationSystem.getModel().map.values() )
        {
            areas.add( block );
        }
        return areas;
    }

    public static ProtectionArea getProtectionArea( UUID uuid )
    {
        for ( ProtectionArea area : configurationSystem.getModel().map.values() )
        {
            if ( area.getUuid().equals( uuid ) )
            {
                return area;
            }
        }
        return null;
    }

    public static ProtectionArea getProtectionArea( Location location )
    {
        for ( ProtectionArea area : configurationSystem.getModel().map.values() )
        {
            if ( !area.getWorld().getName().equals( notNull( location ).getWorld().getName() ) )
            {
                continue;
            }
            if ( area.getLocation().lengthSquared() == location.lengthSquared() )
            {
                return area;
            }
        }
        return null;
    }

    public static void createProtectionArea( Location location, @Nullable Player player )
    {
        if ( !getConfiguration().allowWorld( notNull( location ).getWorld() ) )
        {
            throw new ProtectionWorldException();
        }
        if ( player != null )
        {
            ItemMeta meta = player.getItemInHand().getItemMeta();
            if ( !meta.hasLore() )
            {
                throw new ProtectionLoreException();
            }
            boolean exists = false;
            for ( String lore : meta.getLore() )
            {
                if ( lore.equalsIgnoreCase( getConfiguration().protectionMetaData ) )
                {
                    exists = true;
                    break;
                }
            }
            if ( !exists )
            {
                throw new ProtectionLoreException();
            }
        }
        Material material = getConfiguration().getProtectionBlock();
        if ( material == null )
        {
            throw new ProtectionBlockNullableException();
        }
        if ( !location.getBlock().getType().equals( material ) )
        {
            throw new ProtectionBlockNotEqualWithPlacedException();
        }
        if ( player != null )
        {
            if ( !canBuildProtectionArea( player ) )
            {
                throw new MaxProtectionAreasException( getLanguage().protectionAreaMaxException.getMessage() );
            }
        }
        int radius;
        if ( ( radius = getConfiguration().protectionRadius ) < 1 )
        {
            throw new ProtectionRadiusIsZeroException();
        }

        PreProtectionPlaceEvent preProtectionPlaceEvent = new PreProtectionPlaceEvent( player, location );
        Bukkit.getPluginManager().callEvent( preProtectionPlaceEvent );

        if ( preProtectionPlaceEvent.isCancelled() )
        {
            return;
        }

        int block_x = location.getBlockX(), block_y = location.getBlockY(), block_z = location.getBlockZ();

        BlockVector vector_min = new BlockVector( block_x + radius, block_y + radius, block_z + radius );
        BlockVector vector_max = new BlockVector( block_x - radius, block_y - radius, block_z - radius );

        UUID uuid = generateDatabaseUUID();

        ProtectedCuboidRegion protectedRegion = new ProtectedCuboidRegion( uuid.toString(), vector_min, vector_max );

        if ( getRegionManager( location.getWorld() ).getApplicableRegions( protectedRegion ).size() > 0 )
        {
            throw new ProtectionAreaPlaceException( getLanguage().protectionAreaPlaceException.getMessage() );
        }

        if ( player != null )
        {
            DefaultDomain owner = new DefaultDomain();
            owner.addPlayer( WorldGuardPlugin.inst().wrapPlayer( player ) );
            protectedRegion.setOwners( owner );
        }

        OglofusProtectionArea area = new OglofusProtectionArea( protectedRegion, location );

        importProtectionArea( area );

        RegionManager manager = area.getRegionManager();
        manager.addRegion( protectedRegion );

        area.reFlag();

        try
        {
            manager.saveChanges();
        } catch ( StorageException e )
        {
            e.printStackTrace();
        }

        String message = getLanguage().protectionAreaCreated.getMessage( new String[]{ "id" }, new String[]{ uuid.toString() } );

        ProtectionPlaceEvent protectionPlaceEvent = new ProtectionPlaceEvent( area, player, location, message );
        Bukkit.getPluginManager().callEvent( protectionPlaceEvent );

        if ( !protectionPlaceEvent.getMessage().equals( message ) )
        {
            message = protectionPlaceEvent.getMessage();
        }

        if ( player != null )
        {
            player.sendRawMessage( message );
        }
        Bukkit.getConsoleSender().sendRawMessage( message );
    }

    public static void deleteProtectionRegion( ProtectionArea area, @Nullable Player player )
    {
        if ( notNull( area ).getRegion() == null )
        {
            getPlugin().getLogger().warning( ChatColor.RED + "The ProtectedRegion not exists. '" + area.getUuid() + "'." );
            configurationSystem.getModel().map.remove( area.getUuid() );
            getPlugin().getLogger().warning( ChatColor.RED + "The ProtectionArea '" + area.getUuid() + "' deleted." );
            throw new ProtectionRegionNullableException();
        }
        if ( player != null )
        {
            if ( !area.hasOwnerAccess( player ) )
            {
                throw new AccessException( getLanguage().accessException.getMessage() );
            }
        }
        PreProtectionBreakEvent preProtectionBreakEvent = new PreProtectionBreakEvent( area, player );

        Bukkit.getPluginManager().callEvent( preProtectionBreakEvent );
        if ( preProtectionBreakEvent.isCancelled() )
        {
            return;
        }

        RegionManager manager = getRegionManager( area.getWorld() );
        manager.removeRegion( area.getUuid().toString() );
        try
        {
            manager.saveChanges();
        } catch ( StorageException e )
        {
            e.printStackTrace();
        }
        configurationSystem.getModel().map.remove( area.getUuid() );

        String message = getLanguage().protectionAreaDeleted.getMessage( new String[]{ "id" }, new String[]{ area.getUuid().toString() } );

        ProtectionBreakEvent protectionBreakEvent = new ProtectionBreakEvent( area, player, message );
        Bukkit.getPluginManager().callEvent( protectionBreakEvent );

        if ( !protectionBreakEvent.getMessage().equals( message ) )
        {
            message = protectionBreakEvent.getMessage();
        }
        if ( player != null )
        {
            player.sendRawMessage( message );
        }
        Bukkit.getConsoleSender().sendRawMessage( message );
    }

    public static void deleteProtectionRegion( Location location, @Nullable Player player )
    {
        if ( !getConfiguration().allowWorld( notNull( location ).getWorld() ) )
        {
            throw new ProtectionWorldException();
        }
        Material material = getConfiguration().getProtectionBlock();
        if ( material == null )
        {
            throw new ProtectionBlockNullableException();
        }
        if ( !location.getBlock().getType().equals( getConfiguration().getProtectionBlock() ) )
        {
            throw new ProtectionBlockNotEqualWithPlacedException();
        }
        ProtectionArea area;
        if ( ( area = getProtectionArea( location ) ) == null )
        {
            throw new ProtectionAreaNotExistsException( "&cNo protection areas." );
        }
        deleteProtectionRegion( area, player );
    }

    protected static void importProtectionArea( ProtectionArea area )
    {
        if ( configurationSystem.getModel().map.containsKey( notNull( area ).getUuid() ) )
        {
            return;
        }
        configurationSystem.getModel().map.put( area.getUuid(), ( OglofusProtectionArea ) area );
    }

    public static void removeProtectionArena( ProtectionArea area )
    {
        if ( !configurationSystem.getModel().map.containsKey( notNull( area ).getUuid() ) )
        {
            return;
        }
        configurationSystem.getModel().map.remove( area.getUuid() );
    }

    protected static UUID generateDatabaseUUID()
    {
        UUID uuid = UUID.randomUUID();
        for ( ProtectionArea area : getProtectionAreas() )
        {
            if ( area.getUuid().equals( uuid ) )
            {
                uuid = generateDatabaseUUID();
            }
        }
        return uuid;
    }

    public static boolean canBuildProtectionArea( Player target )
    {
        if ( notNull( target ).isOp() )
        {
            return true;
        }
        Integer max_protections = null;
        for ( String key : getConfiguration().protectionLimits.other.keySet() )
        {
            if ( target.hasPermission( "me.nikosgram.protection.limit." + key ) )
            {
                max_protections = getConfiguration().protectionLimits.other.get( key );
            }
        }
        if ( max_protections == null )
        {
            max_protections = getConfiguration().protectionLimits.defaultLimit;
        }
        return getRegionManager( target.getWorld() ).getRegionCountOfPlayer( WorldGuardPlugin.inst().wrapPlayer( target ) ) < max_protections;
    }

    public static void saveChanges()
    {
        getPlugin().getLogger().info( "The system saving the regions..." );
        for ( World world : getConfiguration().getWorlds() )
        {
            try
            {
                WorldGuardPlugin.inst().getRegionManager( world ).saveChanges();
            } catch ( StorageException e )
            {
                e.printStackTrace();
            }
        }
        for ( ProtectionArea area : getProtectionAreas() )
        {
            area.reFlag();
        }
        configurationSystem.save();
        getPlugin().getLogger().info( "Saving completed!" );
    }
}
