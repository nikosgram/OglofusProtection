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

package me.nikosgram.oglofus.protection.api.event.protection;

import me.nikosgram.oglofus.protection.api.ProtectionArea;
import me.nikosgram.oglofus.protection.api.event.ProtectionEvent;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;

public class ProtectionPlaceEvent extends ProtectionEvent
{
    private static final HandlerList handlers = new HandlerList();

    private final Player   placer;
    private final Location blockLocation;
    private       String   message;

    public ProtectionPlaceEvent( ProtectionArea protectionArea, Player placer, Location blockLocation, String message )
    {
        super( protectionArea );
        this.placer = placer;
        this.blockLocation = blockLocation;
        this.message = message;
    }

    public static HandlerList getHandlerList()
    {
        return handlers;
    }

    @Override
    public HandlerList getHandlers()
    {
        return handlers;
    }

    public Player getPlacer()
    {
        return placer;
    }

    public Location getBlockLocation()
    {
        return blockLocation;
    }

    public String getMessage()
    {
        return message;
    }

    public void setMessage( String message )
    {
        this.message = message;
    }
}