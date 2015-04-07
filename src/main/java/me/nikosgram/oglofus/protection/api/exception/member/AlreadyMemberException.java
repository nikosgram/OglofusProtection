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

package me.nikosgram.oglofus.protection.api.exception.member;

import me.nikosgram.oglofus.protection.api.exception.protection.ProtectionException;

public class AlreadyMemberException extends ProtectionException
{
    public AlreadyMemberException( String player )
    {
        super( "The player '" + player + "' is already member." );
    }

    public AlreadyMemberException( String message, String player )
    {
        super( message.replace( "{player}", player ) );
    }
}