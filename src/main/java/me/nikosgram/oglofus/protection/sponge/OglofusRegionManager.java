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

import me.nikosgram.oglofus.protection.api.action.ActionResponse;
import me.nikosgram.oglofus.protection.api.manager.RegionManager;
import me.nikosgram.oglofus.protection.api.region.ProtectionLocation;
import me.nikosgram.oglofus.protection.api.region.ProtectionRegion;
import me.nikosgram.oglofus.protection.api.region.ProtectionVector;

import java.util.*;

public class OglofusRegionManager implements RegionManager
{
    private final OglofusSponge sponge;
    private final Map< UUID, ProtectionRegion > map = new HashMap< UUID, ProtectionRegion >();

    protected OglofusRegionManager( OglofusSponge sponge )
    {
        this.sponge = sponge;
        sponge.getConnector().createTable(
                "oglofus_regions",
                "id int identity(1,1) primary key",
                "uuid varchar(36)",
                "name varchar(32)",
                "owner varchar(36)",
                "created date"
        );
        sponge.getConnector().createTable(
                "oglofus_vectors",
                "id int identity(1,1) primary key",
                "uuid varchar(36)",
                "radius tinyint",
                "x int",
                "y int",
                "z int",
                "world varchar(36)"
        );
        sponge.getConnector().createTable(
                "oglofus_staff",
                "id int identity(1,1) primary key",
                "uuid varchar(36)",
                "player varchar(36)",
                "rank varchar(10)"
        );
        for ( String uid : sponge.getConnector().getStringList( "select uuid from oglofus_regions", "uuid" ) )
        {
            UUID uuid = UUID.fromString( uid );
            map.put( uuid, new OglofusProtectionRegion( uuid, sponge ) );
        }
    }

    @Override
    public Optional< ProtectionRegion > getRegion( UUID target )
    {
        if ( map.containsKey( target ) )
        {
            return Optional.of( map.get( target ) );
        }
        return Optional.empty();
    }

    @Override
    public Optional< ProtectionRegion > getRegion( String target )
    {
        for ( ProtectionRegion region : getRegions() )
        {
            if ( region.getName().equalsIgnoreCase( target ) )
            {
                return Optional.of( region );
            }
        }
        for ( ProtectionRegion region : getRegions() )
        {
            if ( region.getName().toLowerCase().startsWith( target.toLowerCase() ) )
            {
                return Optional.of( region );
            }
        }
        return Optional.empty();
    }

    @Override
    public Optional< ProtectionRegion > getRegion( ProtectionLocation location )
    {
        for ( ProtectionRegion region : getRegions() )
        {
            ProtectionVector vector = region.getProtectionVector();
            if ( !location.getWorld().equals( vector.getBlockLocation().getWorld() ) )
            {
                continue;
            }
            if ( Math.abs( vector.getBlockLocation().getX() - location.getX() ) <= vector.getRadius() )
            {
                if ( Math.abs( vector.getBlockLocation().getY() - location.getY() ) <= vector.getRadius() )
                {
                    if ( Math.abs( vector.getBlockLocation().getZ() - location.getZ() ) <= vector.getRadius() )
                    {
                        return Optional.of( region );
                    }
                }
            }
        }
        return Optional.empty();
    }

    @Override
    public Collection< ProtectionRegion > getRegions()
    {
        return map.values();
    }

    @Override
    public ActionResponse createProtectionArea( ProtectionLocation location, UUID sender )
    {
        return null;
    }

    @Override
    public ActionResponse deleteProtectionArea( ProtectionRegion area, UUID sender )
    {
        return null;
    }
}
