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

package me.nikosgram.oglofus.protection.bukkit;

import com.google.common.base.Optional;
import lombok.Getter;
import me.nikosgram.oglofus.protection.api.region.ProtectionLocation;

import java.util.UUID;

public class OglofusProtectionLocation implements ProtectionLocation
{
    private final OglofusBukkit bukkit;
    @Getter
    private final UUID          world;
    @Getter
    private       int           x;
    @Getter
    private       int           y;
    @Getter
    private       int           z;

    protected OglofusProtectionLocation( OglofusBukkit bukkit, ProtectionLocation location )
    {
        this.bukkit = bukkit;
        this.world = location.getWorld();
        this.x = location.getX();
        this.y = location.getY();
        this.z = location.getZ();
    }

    protected OglofusProtectionLocation( OglofusBukkit bukkit, UUID world, int x, int y, int z )
    {
        this.bukkit = bukkit;
        this.world = world;
        this.x = x;
        this.y = y;
        this.z = z;
    }

    @Override
    public String getWorldName()
    {
        return bukkit.getServer().getWorld( world ).getName();
    }

    @Override
    public ProtectionLocation add( int x, int y, int z )
    {
        this.x += x;
        this.y += y;
        this.z += z;
        return this;
    }

    @Override
    public < T > Optional< T > getWorldAs( Class< T > tClass )
    {
        return Optional.absent();
    }

    @Override
    public < T > Optional< T > getLocationAs( Class< T > tClass )
    {
        return Optional.absent();
    }

    @Override
    public boolean equals( Object o )
    {
        if ( this == o ) return true;
        if ( o == null || getClass() != o.getClass() ) return false;

        OglofusProtectionLocation that = ( OglofusProtectionLocation ) o;

        if ( x != that.x ) return false;
        if ( y != that.y ) return false;
        if ( z != that.z ) return false;
        if ( bukkit != null ? !bukkit.equals( that.bukkit ) : that.bukkit != null ) return false;
        return !( world != null ? !world.equals( that.world ) : that.world != null );

    }

    @Override
    public int hashCode()
    {
        int result = bukkit != null ? bukkit.hashCode() : 0;
        result = 31 * result + ( world != null ? world.hashCode() : 0 );
        result = 31 * result + x;
        result = 31 * result + y;
        result = 31 * result + z;
        return result;
    }

    @Override
    public String toString()
    {
        return "ProtectionLocation{" +
                ", world=" + world +
                ", x=" + x +
                ", y=" + y +
                ", z=" + z +
                '}';
    }
}
