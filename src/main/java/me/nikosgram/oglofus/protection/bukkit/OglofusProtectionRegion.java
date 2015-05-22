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

package me.nikosgram.oglofus.protection.bukkit;

import lombok.Getter;
import me.nikosgram.oglofus.protection.api.action.ActionResponse;
import me.nikosgram.oglofus.protection.api.region.ProtectionRegion;
import me.nikosgram.oglofus.protection.api.region.ProtectionStaff;
import me.nikosgram.oglofus.protection.api.region.ProtectionVector;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.UUID;

public class OglofusProtectionRegion implements ProtectionRegion
{
    @Getter
    private final UUID             uuid;
    private final OglofusBukkit    bukkit;
    @Getter
    private final ProtectionStaff  protectionStaff;
    @Getter
    private final ProtectionVector protectionVector;
    @Getter
    private       String           name;

    protected OglofusProtectionRegion( UUID uuid, OglofusBukkit bukkit )
    {
        this.uuid = uuid;
        this.bukkit = bukkit;
        this.name = this.bukkit.getConnector().getString(
                "oglofus_regions", "uuid", this.uuid.toString(), "name"
        ).get();
        this.protectionStaff = null;
        this.protectionVector = new OglofusProtectionVector( this.uuid, this.bukkit );
    }

    @Override
    public ActionResponse changeName( String name )
    {
        if ( name.length() > 36 )
        {
            return ActionResponse.Failure.setMessage( "length" );
        }
        if ( this.bukkit.getConnector().exists( "oglofus_regions", "name", name ) )
        {
            return ActionResponse.Failure.setMessage( "exists" );
        }
        this.name = name;
        this.bukkit.getConnector().update( "oglofus_regions", "uuid", this.uuid.toString(), "name", this.name );
        return ActionResponse.Successful.setMessage( this.name );
    }

    @Override
    public ActionResponse changeName( Object sender, String name )
    {
        if ( sender instanceof CommandSender )
        {
            if ( sender instanceof Player )
            {
                if ( getProtectionStaff().hasOwnerAccess( ( ( Player ) sender ).getUniqueId() ) )
                {
                    return changeName( name );
                }
                return ActionResponse.Failure.setMessage( "access" );
            }
            if ( ( ( CommandSender ) sender ).hasPermission( "oglofus.protection.bypass" ) )
            {
                return this.changeName( name );
            }
            return ActionResponse.Failure.setMessage( "access" );
        }
        return ActionResponse.Failure.setMessage( "object" );
    }
}
