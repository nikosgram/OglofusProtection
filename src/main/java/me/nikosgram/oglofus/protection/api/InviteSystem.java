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

import com.sk89q.worldguard.protection.managers.storage.StorageException;
import me.nikosgram.oglofus.protection.OglofusPlugin;
import me.nikosgram.oglofus.protection.api.event.protection.InvitePlayerEvent;
import me.nikosgram.oglofus.protection.api.event.protection.JoinMemberEvent;
import me.nikosgram.oglofus.protection.api.exception.member.AlreadyMemberException;
import me.nikosgram.oglofus.protection.api.exception.player.NoInvitesException;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.*;

import static me.nikosgram.oglofus.protection.OglofusPlugin.*;
import static me.nikosgram.oglofus.protection.api.ProtectionSystem.getProtectionArea;

public final class InviteSystem
{
    private final static Map< UUID, List< UUID > > MAP = new HashMap< UUID, List< UUID > >();

    public static void invite( final Player target, final ProtectionArea area )
    {
        if ( notNull( area ).isMember( notNull( target ) ) )
        {
            throw new AlreadyMemberException( target.getName(), getLanguage().getModel().alreadyMemberException );
        }
        if ( !MAP.containsKey( area.getUuid() ) )
        {
            MAP.put( area.getUuid(), new ArrayList< UUID >() );
        }
        cancel( target );
        if ( MAP.get( area.getUuid() ).contains( target.getUniqueId() ) )
        {
            return;
        }
        InvitePlayerEvent event = new InvitePlayerEvent( area, target );
        Bukkit.getPluginManager().callEvent( event );
        if ( event.isCancelled() )
        {
            return;
        }
        MAP.get( area.getUuid() ).add( target.getUniqueId() );
        Bukkit.getScheduler().runTaskLater( OglofusPlugin.getPlugin(), new Runnable()
        {
            @Override
            public void run()
            {
                cancel( target, area );
            }
        }, getConfiguration().autoCancelDelay );
    }

    public static void accept( Player target )
    {
        ProtectionArea area = null;
        for ( UUID uuid : MAP.keySet() )
        {
            if ( MAP.get( uuid ).contains( notNull( target ).getUniqueId() ) )
            {
                area = getProtectionArea( uuid );
            }
        }
        if ( area == null )
        {
            throw new NoInvitesException( getLanguage().getModel( getLanguage( target ) ).noInvitesException );
        }
        JoinMemberEvent event = new JoinMemberEvent( area, target );
        Bukkit.getPluginManager().callEvent( event );
        if ( event.isCancelled() )
        {
            return;
        }
        cancel( target, area );
        area.getRegion().getMembers().addPlayer( target.getUniqueId() );
        try
        {
            area.getRegionManager().saveChanges();
        } catch ( StorageException e )
        {
            e.printStackTrace();
        }
    }

    public static void cancel( Player target )
    {
        for ( List< UUID > uuidList : MAP.values() )
        {
            if ( !uuidList.contains( notNull( target ).getUniqueId() ) )
            {
                continue;
            }
            uuidList.remove( target.getUniqueId() );
        }
    }

    public static void cancel( Player target, ProtectionArea area )
    {
        if ( !MAP.containsKey( notNull( area ).getUuid() ) )
        {
            return;
        }
        if ( !MAP.get( area.getUuid() ).contains( notNull( target ).getUniqueId() ) )
        {
            return;
        }
        MAP.get( area.getUuid() ).remove( target.getUniqueId() );
        if ( MAP.get( area.getUuid() ).isEmpty() )
        {
            MAP.remove( area.getUuid() );
        }
    }
}
