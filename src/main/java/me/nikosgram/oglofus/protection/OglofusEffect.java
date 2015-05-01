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

import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.Material;

import static me.nikosgram.oglofus.protection.OglofusPlugin.notNull;

public class OglofusEffect
{
    public boolean enabled       = true;
    public String  effect        = "FLAME";
    public int     data          = 0;
    public int     visibleRadius = 10;

    public OglofusEffect( String effect, boolean enabled )
    {
        this.effect = effect;
        this.enabled = enabled;
    }

    public OglofusEffect( String effect, boolean enabled, int data, int visibleRadius )
    {
        this.enabled = enabled;
        this.effect = effect;
        this.data = data;
        this.visibleRadius = visibleRadius;
    }

    public Effect getEffect()
    {
        return Effect.valueOf( effect );
    }

    public void playEffect( Location location )
    {
        if ( enabled )
        {
            notNull( location ).getWorld().playEffect( location, notNull( getEffect() ), data, visibleRadius );
        }
    }

    public void playEffect( Location minLocation, Location maxLocation )
    {
        playEffect( minLocation, maxLocation, Material.AIR );
    }

    public void playEffect( Location minLocation, Location maxLocation, Material material )
    {
        if ( !enabled ) return;
        for ( double location_x = notNull( minLocation ).getX(); location_x <= notNull( maxLocation ).getX(); location_x++ )
        {
            for ( double location_y = minLocation.getY(); location_y <= maxLocation.getY(); location_y++ )
            {
                for ( double location_z = minLocation.getZ(); location_z <= maxLocation.getZ(); location_z++ )
                {
                    Location location = new Location( minLocation.getWorld(), location_x, location_y, location_z );
                    if ( location.getBlock().getType().equals( notNull( material ) ) )
                    {
                        playEffect( location );
                    }
                }
            }
        }
    }

    @Override
    public String toString()
    {
        return "OglofusEffect{" +
                "enabled=" + enabled +
                ", effect='" + effect + '\'' +
                '}';
    }
}
