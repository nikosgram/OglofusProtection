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
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Map;

import static me.nikosgram.oglofus.protection.OglofusProtection.notNull;
import static me.nikosgram.oglofus.protection.OglofusProtection.reformMessage;

public class OglofusMessage
{
    public String message = "";

    public OglofusMessage() {}

    public OglofusMessage( String message )
    {
        this.message = message;
    }

    public String getMessage()
    {
        return reformMessage( message );
    }

    public String getMessage( Map< String, String > values )
    {
        return reformMessage( message, values );
    }

    public String getMessage( String[] keys, String[] values )
    {
        return reformMessage( message, keys, values );
    }

    public void sendMessage( Player sender )
    {
        notNull( sender ).sendRawMessage( getMessage() );
    }

    public void sendMessage( Player sender, Map< String, String > values )
    {
        notNull( sender ).sendRawMessage( getMessage( values ) );
    }

    public void sendMessage( Player sender, String[] keys, String[] values )
    {
        notNull( sender ).sendRawMessage( getMessage( keys, values ) );
    }

    public void sendMessage( LocalPlayer sender )
    {
        notNull( sender ).printRaw( getMessage() );
    }

    public void sendMessage( LocalPlayer sender, Map< String, String > values )
    {
        notNull( sender ).printRaw( getMessage( values ) );
    }

    public void sendMessage( LocalPlayer sender, String[] keys, String[] values )
    {
        notNull( sender ).printRaw( getMessage( keys, values ) );
    }

    public void sendMessage( CommandSender sender )
    {
        notNull( sender ).sendMessage( getMessage() );
    }

    public void sendMessage( CommandSender sender, Map< String, String > values )
    {
        notNull( sender ).sendMessage( getMessage( values ) );
    }

    public void sendMessage( CommandSender sender, String[] keys, String[] values )
    {
        notNull( sender ).sendMessage( getMessage( keys, values ) );
    }

    @Override
    public String toString()
    {
        return message;
    }
}
