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

package me.nikosgram.oglofus.protection.sponge;

import lombok.Getter;
import me.nikosgram.oglofus.protection.api.action.ActionResponse;
import me.nikosgram.oglofus.protection.api.region.ProtectionRegion;
import me.nikosgram.oglofus.protection.api.region.ProtectionStaff;
import me.nikosgram.oglofus.protection.api.region.ProtectionVector;
import org.spongepowered.api.entity.player.Player;
import org.spongepowered.api.util.command.CommandSource;

import java.util.UUID;

public class OglofusProtectionRegion implements ProtectionRegion
{
    @Getter
    private final UUID             uuid;
    private final OglofusSponge    sponge;
    @Getter
    private final ProtectionStaff  protectionStaff;
    @Getter
    private final ProtectionVector protectionVector;
    @Getter
    private       String           name;

    protected OglofusProtectionRegion( UUID uuid, OglofusSponge sponge )
    {
        this.uuid = uuid;
        this.sponge = sponge;
        this.name = this.sponge.getConnector().getString(
                "oglofus_regions", "uuid", this.uuid.toString(), "name"
        ).get();
        this.protectionStaff = new OglofusProtectionStaff( this.uuid, this.sponge );
        this.protectionVector = new OglofusProtectionVector( this.uuid, this.sponge );
    }

    /**
     * Change the region's name.
     *
     * @param name the new name.
     * @return the response.
     */
    @Override
    public ActionResponse changeName( String name )
    {
        if ( name.length() > 36 )
        {
            return ActionResponse.Failure.setMessage( "length" );
        }
        if ( this.sponge.getConnector().exists( "oglofus_regions", "name", name ) )
        {
            return ActionResponse.Failure.setMessage( "exists" );
        }
        this.name = name;
        this.sponge.getConnector().update( "oglofus_regions", "uuid", this.uuid.toString(), "name", this.name );
        return ActionResponse.Successful.setMessage( this.name );
    }

    /**
     * Change the region's name.
     *
     * @param sender who want to change the name.
     * @param name   the new name.
     * @return the response.
     */
    @Override
    public ActionResponse changeName( Object sender, String name )
    {
        if ( sender instanceof CommandSource )
        {
            if ( sender instanceof Player )
            {
                if ( getProtectionStaff().hasOwnerAccess( ( ( Player ) sender ).getUniqueId() ) )
                {
                    return changeName( name );
                }
                return ActionResponse.Failure.setMessage( "access" );
            }
            if ( ( ( CommandSource ) sender ).hasPermission( "oglofus.protection.bypass" ) )
            {
                return this.changeName( name );
            }
            return ActionResponse.Failure.setMessage( "access" );
        }
        return ActionResponse.Failure.setMessage( "object" );
    }
}
