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

import static me.nikosgram.oglofus.protection.OglofusProtection.notNull;

@Configuration( "config" )
public class OglofusConfiguration
{
    public OglofusEffect         onBreakEffect      = new OglofusEffect( "FLAME", true );
    public OglofusEffect         onPlaceEffect      = new OglofusEffect( "CLOUD", true );
    public String                protectionBlock    = "SPONGE";
    public Map< String, Object > protectionFlags    = new HashMap< String, Object >();
    public OglofusLimits         protectionLimits   = new OglofusLimits();
    public int                   protectionRadius   = 6;
    public boolean               protectionVert     = false;
    public List< String >        protectionWorlds   = new ArrayList< String >();
    public String                protectionMetaData = "metadata";
    public long                  autoReloadDelay    = 300000;
    public long                  autoCancelDelay    = 60000;

    public Material getProtectionBlock()
    {
        return Material.matchMaterial( protectionBlock );
    }

    public boolean allowWorld( World world )
    {
        return allowWorld( notNull( world ).getName() );
    }

    public boolean allowWorld( String world )
    {
        return protectionWorlds.contains( notNull( world ) );
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
