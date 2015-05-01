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

import me.nikosgram.oglofus.configuration.Configuration;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.World;

import java.util.*;

@Configuration( "config" )
public class OglofusConfiguration
{
    public OglofusEffect         onBreakEffect      = new OglofusEffect( "FLAME", true, 20, 10 );
    public OglofusEffect         onPlaceEffect      = new OglofusEffect( "CLOUD", true, 20, 10 );
    public String                protectionBlock    = "SPONGE";
    public Map< String, Object > protectionFlags    = new HashMap< String, Object >();
    public OglofusLimits         protectionLimits   = new OglofusLimits();
    public int                   protectionRadius   = 6;
    public boolean               protectionVert     = false;
    public List< String >        protectionWorlds   = new ArrayList< String >();
    public String                protectionMetaData = "metadata";
    public long                  autoReloadDelay    = 300000;
    public long                  autoCancelDelay    = 60000;
    public OglofusEffect         wallEffect         = new OglofusEffect( "HAPPY_VILLAGER", true, 0, 10 );

    public Material getProtectionBlock()
    {
        return Material.matchMaterial( protectionBlock );
    }

    public boolean allowWorld( World target )
    {
        for ( World world : getWorlds() )
        {
            if ( world.getUID().equals( target.getUID() ) )
            {
                return true;
            }
        }
        return false;
    }

    public boolean allowWorld( String target )
    {
        for ( World world : getWorlds() )
        {
            if ( world.getName().equalsIgnoreCase( target ) )
            {
                return true;
            }
        }
        return false;
    }

    public Collection< World > getWorlds()
    {
        Collection< World > worlds = new ArrayList< World >();
        for ( String world : protectionWorlds )
        {
            World w;
            if ( ( w = Bukkit.getWorld( world ) ) == null )
            {
                continue;
            }
            worlds.add( w );
        }
        return worlds;
    }
}
