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

import com.sk89q.worldguard.LocalPlayer;
import me.nikosgram.oglofus.protection.api.MessageLang;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Map;

import static me.nikosgram.oglofus.protection.OglofusProtection.*;

public class OglofusMessage
{
    public String english = "";
    public String greek   = null;

    public OglofusMessage( String message )
    {
        this.english = message;
    }

    public OglofusMessage( String english, String greek )
    {
        this.english = english;
        this.greek = greek;
    }

    public String getEnglish()
    {
        return english;
    }

    public void setEnglish( String english )
    {
        this.english = english;
    }

    public String getGreek()
    {
        return greek;
    }

    public void setGreek( String greek )
    {
        this.greek = greek;
    }

    public String getMessage()
    {
        return getMessage( MessageLang.English );
    }

    public String getMessage( MessageLang lang )
    {
        switch ( lang )
        {
            case English:
                return reformMessage( english );
            case Greek:
                if ( greek != null )
                {
                    return reformMessage( greek );
                }
            default:
                return getMessage();
        }
    }

    public String getMessage( Map< String, String > values )
    {
        return reformMessage( getMessage(), values );
    }

    public String getMessage( Map< String, String > values, MessageLang lang )
    {
        return reformMessage( getMessage( lang ), values );
    }

    public String getMessage( String[] keys, String[] values )
    {
        return reformMessage( getMessage(), keys, values );
    }

    public String getMessage( String[] keys, String[] values, MessageLang lang )
    {
        return reformMessage( getMessage( lang ), keys, values );
    }

    public void sendMessage( Player target )
    {
        notNull( target ).sendRawMessage( getMessage( getLanguage( target ) ) );
    }

    public void sendMessage( Player target, Map< String, String > values )
    {
        notNull( target ).sendRawMessage( getMessage( values, getLanguage( target ) ) );
    }

    public void sendMessage( Player target, String[] keys, String[] values )
    {
        notNull( target ).sendRawMessage( getMessage( keys, values, getLanguage( target ) ) );
    }

    public void sendMessage( LocalPlayer target )
    {
        sendMessage( Bukkit.getPlayer( target.getUniqueId() ) );
    }

    public void sendMessage( LocalPlayer target, Map< String, String > values )
    {
        sendMessage( Bukkit.getPlayer( target.getUniqueId() ), values );
    }

    public void sendMessage( LocalPlayer target, String[] keys, String[] values )
    {
        sendMessage( Bukkit.getPlayer( target.getUniqueId() ), keys, values );
    }

    public void sendMessage( CommandSender target )
    {
        if ( target instanceof Player )
        {
            sendMessage( ( Player ) target );
            return;
        }
        notNull( target ).sendMessage( getMessage() );
    }

    public void sendMessage( CommandSender target, Map< String, String > values )
    {
        if ( target instanceof Player )
        {
            sendMessage( ( Player ) target, values );
            return;
        }
        notNull( target ).sendMessage( getMessage( values ) );
    }

    public void sendMessage( CommandSender target, String[] keys, String[] values )
    {
        if ( target instanceof Player )
        {
            sendMessage( ( Player ) target, keys, values );
            return;
        }
        notNull( target ).sendMessage( getMessage( keys, values ) );
    }

    @Override
    public String toString()
    {
        return getMessage();
    }
}
