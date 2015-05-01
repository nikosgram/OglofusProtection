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

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;

import java.util.UUID;

public class OglofusProtectionLocation
{
    public int x, y, z;
    public UUID world;

    public OglofusProtectionLocation() {}

    public OglofusProtectionLocation( Location location )
    {
        x = location.getBlockX();
        y = location.getBlockY();
        z = location.getBlockZ();
        world = location.getWorld().getUID();
    }

    public OglofusProtectionLocation( int x, int y, int z, UUID world )
    {
        this.x = x;
        this.y = y;
        this.z = z;
        this.world = world;
    }

    public int getX()
    {
        return x;
    }

    public int getY()
    {
        return y;
    }

    public int getZ()
    {
        return z;
    }

    public World getWorld()
    {
        return Bukkit.getWorld( world );
    }

    public Location toLocation()
    {
        return new Location( getWorld(), x, y, z );
    }

    @Override
    public String toString()
    {
        return "OglofusProtectionLocation{" +
                "x=" + x +
                ", y=" + y +
                ", z=" + z +
                ", world=" + world +
                '}';
    }
}
