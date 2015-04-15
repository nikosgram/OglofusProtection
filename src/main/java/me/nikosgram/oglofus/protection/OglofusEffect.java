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

import static me.nikosgram.oglofus.protection.OglofusProtection.notNull;

public class OglofusEffect
{
    public boolean enabled = true;
    public String  effect  = "FLAME";

    public OglofusEffect( String effect, boolean enabled )
    {
        this.effect = effect;
        this.enabled = enabled;
    }

    public Effect getEffect()
    {
        return Effect.valueOf( effect );
    }

    public void playEffect( Location location, int var )
    {
        if ( enabled )
        {
            notNull( location ).getWorld().playEffect( location, getEffect(), var );
        }
    }
}
