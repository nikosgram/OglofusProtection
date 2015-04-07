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

@Configuration( "language" )
public class OglofusLanguage
{
    public OglofusMessage accessException               = new OglofusMessage( "&cYou don't have access to do that." );
    public OglofusMessage alreadyMemberException        = new OglofusMessage( "&cThe player '{player}' is already member." );
    public OglofusMessage inviteYourSelfException       = new OglofusMessage( "&cYou can't invite you." );
    public OglofusMessage kickYourSelfException         = new OglofusMessage( "&cYou can't kick you." );
    public OglofusMessage memberInfo                    = new OglofusMessage( "&eRegion &c{id}{n}&eOwners: &c{owners}{n}&eMembers: &c{members}" );
    public OglofusMessage memberNotExistsException      = new OglofusMessage( "&cThe player '{player}' is not member at your Area." );
    public OglofusMessage noAreas                       = new OglofusMessage( "&cNo regions here." );
    public OglofusMessage noInvitesException            = new OglofusMessage( "&cYou don't have invites..." );
    public OglofusMessage noProtectionArea              = new OglofusMessage( "&cNo protection areas here." );
    public OglofusMessage ownerInfo                     = new OglofusMessage( "&eRegion &e{id}{n}&eOwners: &c{owners}{n}&eMembers: &c{members}{n}&eBlock Location: &c{location}{n}&eRegion Vector: &c{vector}" );
    public OglofusMessage playerNotExistsException      = new OglofusMessage( "&cThe player '{player}' doesn't exists." );
    public OglofusMessage playerOfflineException        = new OglofusMessage( "&cThe player '{player}' is not Online." );
    public OglofusMessage protectionAreaCreated         = new OglofusMessage( "&aThe region '{id}' created!" );
    public OglofusMessage protectionAreaDeleted         = new OglofusMessage( "&aThe Protection Area with the ID '{id}' deleted!" );
    public OglofusMessage protectionAreaMaxException    = new OglofusMessage( "&cYou can't build more Protection Areas." );
    public OglofusMessage protectionAreaNotAccess       = new OglofusMessage( "&cYou don't have access here." );
    public OglofusMessage protectionAreaNotExists       = new OglofusMessage( "&cProtection Area with the ID '{id}' doesn't exists." );
    public OglofusMessage protectionAreaPlaceException  = new OglofusMessage( "&cYou can't put your protection block here." );
    public OglofusMessage protectionMemberInviteMessage = new OglofusMessage( "&aThe player '{player}' invited to the Protection Area with ID '{id}'" );
    public OglofusMessage protectionMemberJoinMessage   = new OglofusMessage( "&aThe player '{player}' has join to the Protection Area with ID '{id}'" );
    public OglofusMessage protectionMemberKickMessage   = new OglofusMessage( "&aThe player '{player}' kicked from Protection Area with ID '{id}'" );
    public OglofusMessage protectionTargetInviteMessage = new OglofusMessage( "&aYou have a invite request to join at the Protection Area with ID '{id}'." );
    public OglofusMessage protectionTargetJoinMessage   = new OglofusMessage( "&aYou have join to the Protection Area with ID '{id}'" );
    public OglofusMessage protectionTargetKickMessage   = new OglofusMessage( "&aYou kicked from the Protection Area with ID '{id}'" );
}
