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

import com.flowpowered.math.vector.Vector3d;
import com.google.common.base.Optional;
import lombok.Getter;
import me.nikosgram.oglofus.protection.api.region.ProtectionLocation;
import me.nikosgram.oglofus.utils.OglofusUtils;
import org.spongepowered.api.block.BlockState;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;
import org.spongepowered.api.world.extent.Extent;

import java.util.UUID;

public class OglofusProtectionLocation implements ProtectionLocation
{
    private final OglofusSponge sponge;
    @Getter
    private final UUID          world;
    @Getter
    private       int           x;
    @Getter
    private       int           y;
    @Getter
    private       int           z;

    protected OglofusProtectionLocation( OglofusSponge sponge, ProtectionLocation location )
    {
        this.sponge = sponge;
        this.world = location.getWorld();
        this.x = location.getX();
        this.y = location.getY();
        this.z = location.getZ();
    }

    protected OglofusProtectionLocation( OglofusSponge sponge, UUID world, int x, int y, int z )
    {
        this.sponge = sponge;
        this.world = world;
        this.x = x;
        this.y = y;
        this.z = z;
    }

    @Override
    public String getWorldName()
    {
        return getWorldAs( World.class ).get().getName();
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
        if ( OglofusUtils.equalClass( tClass, World.class ) )
        {
            return Optional.of( ( T ) sponge.getServer().getWorld( this.world ) );
        } else
            if ( OglofusUtils.equalClass( tClass, Extent.class ) )
            {
                getLocationAs( Location.class ).get().getExtent();
            }
        return Optional.absent();
    }

    @Override
    public < T > Optional< T > getLocationAs( Class< T > tClass )
    {
        Location location = new Location( getWorldAs( World.class ).get(), this.x, this.y, this.z );
        if ( OglofusUtils.equalClass( tClass, Location.class ) )
        {
            return Optional.of( ( T ) location );
        } else
            if ( OglofusUtils.equalClass( tClass, BlockState.class ) )
            {
                return Optional.of( ( T ) location.getState() );
            } else
                if ( OglofusUtils.equalClass( tClass, Vector3d.class ) )
                {
                    return Optional.of( ( T ) location.getPosition() );
                }
        return Optional.absent();
    }

    @Override
    public boolean equals( Object o )
    {
        if ( this == o ) return true;
        if ( o == null || getClass() != o.getClass() ) return false;

        OglofusProtectionLocation that = ( OglofusProtectionLocation ) o;

        if ( this.x != that.x ) return false;
        if ( this.y != that.y ) return false;
        if ( this.z != that.z ) return false;
        if ( this.sponge != null ? !this.sponge.equals( that.sponge ) : that.sponge != null ) return false;
        return !( this.world != null ? !this.world.equals( that.world ) : that.world != null );
    }

    @Override
    public int hashCode()
    {
        int result = this.sponge != null ? this.sponge.hashCode() : 0;
        result = 31 * result + ( this.world != null ? this.world.hashCode() : 0 );
        result = 31 * result + x;
        result = 31 * result + y;
        result = 31 * result + z;
        return result;
    }

    @Override
    public String toString()
    {
        return "ProtectionLocation{" +
                ", world=" + this.world +
                ", x=" + this.x +
                ", y=" + this.y +
                ", z=" + this.z +
                '}';
    }
}
