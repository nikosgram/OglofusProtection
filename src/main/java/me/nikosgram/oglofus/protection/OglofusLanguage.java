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
    public OglofusMessage accessException               = new OglofusMessage( "&cYou don't have access to do that.", "&cΔεν έχεις την άδεια να το κάνεις αυτό." );
    public OglofusMessage alreadyMemberException        = new OglofusMessage( "&cThe player '{player}' is already member.", "&cΟ παίκτης '{player}' είναι ήδη member" );
    public OglofusMessage inviteYourSelfException       = new OglofusMessage( "&cYou can't invite you.", "&cΔεν μπορείς να προσκαλέσεις τον εαυτό σου." );
    public OglofusMessage kickYourSelfException         = new OglofusMessage( "&cYou can't kick you.", "&cΔεν μπορείς να διώξεις τον εαυτό σου." );
    public OglofusMessage memberInfo                    = new OglofusMessage( "&eRegion &c{id}{n}&eOwners: &c{owners}{n}&eMembers: &c{members}" );
    public OglofusMessage memberNotExistsException      = new OglofusMessage( "&cThe player '{player}' is not member at your Area.", "&cΟ player '{player}' δεν είναι member στην Area σας." );
    public OglofusMessage noAreas                       = new OglofusMessage( "&cNo regions here.", "&cΔεν υπάρχουν regions εδώ." );
    public OglofusMessage noInvitesException            = new OglofusMessage( "&cYou don't have invites...", "&cΔεν έχεις προσκλήσεις..." );
    public OglofusMessage noProtectionArea              = new OglofusMessage( "&cNo protection areas here.", "&cΔεν υπάρχουν προστατευόμενες περιοχές εδώ." );
    public OglofusMessage ownerInfo                     = new OglofusMessage( "&eRegion &e{id}{n}&eOwners: &c{owners}{n}&eMembers: &c{members}{n}&eBlock Location: &c{location}{n}&eRegion Vector: &c{vector}" );
    public OglofusMessage playerNotExistsException      = new OglofusMessage( "&cThe player '{player}' doesn't exists.", "&cΟ παίκτης '{player}' δεν υπάρχει." );
    public OglofusMessage playerOfflineException        = new OglofusMessage( "&cThe player '{player}' is not Online.", "&cΟ παίκτης '{player}' δεν είναι online." );
    public OglofusMessage protectionAreaCreated         = new OglofusMessage( "&aThe region '{id}' created!", "&aΤο region '{id}' δημιουργήθηκε!" );
    public OglofusMessage protectionAreaDeleted         = new OglofusMessage( "&cThe Protection Area with the ID '{id}' deleted!", "&cΤο region με το ID '{id}' διαγράφτικε!" );
    public OglofusMessage protectionAreaMaxException    = new OglofusMessage( "&cYou can't build more Protection Areas.", "&cΔεν μπορείς να προσθέσεις και άλλες προστατευόμενες περιοχές." );
    public OglofusMessage protectionAreaNotAccess       = new OglofusMessage( "&cYou don't have access here.", "&cΔεν έχεις δικαιώματα σε αυτή την περιοχή." );
    public OglofusMessage protectionAreaNotExists       = new OglofusMessage( "&cProtection Area with the ID '{id}' doesn't exists." /*This is running only from the console.*/ );
    public OglofusMessage protectionAreaPlaceException  = new OglofusMessage( "&cYou can't put your protection block here.", "&cΔεν μπορείς να τοποθετήσεις εδώ το protection block." );
    public OglofusMessage protectionMemberInviteMessage = new OglofusMessage( "&aThe player '{player}' invited to the Protection Area with ID '{id}'", "&aΟ παίκτης '{player}' προσκλήθηκε στην προστατευόμενη περιοχή με ID '{id}'." );
    public OglofusMessage protectionMemberJoinMessage   = new OglofusMessage( "&aThe player '{player}' has join to the Protection Area with ID '{id}'", "Ο παίκτης '{player}' εισήλθε στην προστατευόμενη περιοχή με ID '{id}'." );
    public OglofusMessage protectionMemberKickMessage   = new OglofusMessage( "&aThe player '{player}' kicked from Protection Area with ID '{id}'", "Ο παίκτης '{player}' αφαιρέθηκε από την προστατευόμενη περιοχή με ID '{id}'." );
    public OglofusMessage protectionTargetInviteMessage = new OglofusMessage( "&aYou have a invite request to join at the Protection Area with ID '{id}'.", "&aΣε έστειλαν πρόσκληση να εισέλθετε στην προστατευόμενη περιοχή με ID '{id}'" );
    public OglofusMessage protectionTargetJoinMessage   = new OglofusMessage( "&aYou have join to the Protection Area with ID '{id}'", "&aΜόλις εισήλθες στην προστατευόμενη περιοχή με ID '{id}'" );
    public OglofusMessage protectionTargetKickMessage   = new OglofusMessage( "&aYou kicked from the Protection Area with ID '{id}'", "&aΜόλις σε αφαίρεσαν από την προστατευόμενη περιοχή με ID '{id}'" );
}
