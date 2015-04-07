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

import me.nikosgram.oglofus.protection.api.InviteSystem;
import me.nikosgram.oglofus.protection.api.ProtectionArea;
import me.nikosgram.oglofus.protection.api.ProtectionSystem;
import me.nikosgram.oglofus.protection.api.event.protection.*;
import me.nikosgram.oglofus.protection.api.exception.player.AccessException;
import me.nikosgram.oglofus.protection.api.exception.player.MaxProtectionAreasException;
import me.nikosgram.oglofus.protection.api.exception.protection.*;
import me.nikosgram.oglofus.protection.api.exception.protection.world.ProtectionWorldException;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static me.nikosgram.oglofus.protection.OglofusProtection.*;

public class OglofusListener implements Listener
{
    @EventHandler( priority = EventPriority.MONITOR )
    public void createProtection( BlockPlaceEvent event )
    {
        if ( event.isCancelled() )
        {
            return;
        }
        if ( !event.canBuild() )
        {
            return;
        }
        try
        {
            ProtectionSystem.createProtectionArea( event.getBlock().getLocation(), event.getPlayer() );
        } catch ( ProtectionWorldException | ProtectionLoreException | ProtectionBlockNullableException | ProtectionBlockNotEqualWithPlacedException ignored )
        {
        } catch ( MaxProtectionAreasException e )
        {
            getLanguage().protectionAreaMaxException.sendMessage( event.getPlayer() );
        } catch ( ProtectionRadiusIsZeroException e )
        {
            event.getPlayer().sendRawMessage( reformMessage( "&cError 0x0R1" ) );
            e.printStackTrace();
        } catch ( ProtectionAreaPlaceException e )
        {
            event.getPlayer().sendRawMessage( getLanguage().protectionAreaPlaceException.getMessage() );
            event.setCancelled( true );
        }
    }

    @EventHandler( priority = EventPriority.MONITOR )
    public void destroyProtection( BlockBreakEvent event )
    {
        if ( event.isCancelled() )
        {
            return;
        }
        try
        {
            ProtectionSystem.deleteProtectionRegion( event.getBlock().getLocation(), event.getPlayer() );

            event.getBlock().setType( Material.AIR );
            if ( !event.getPlayer().getGameMode().equals( GameMode.CREATIVE ) )
            {
                ItemStack stack = new ItemStack( Material.SPONGE );
                ItemMeta item = Bukkit.getItemFactory().getItemMeta( getConfiguration().getProtectionBlock() );
                List< String > meta_data = new ArrayList< String >();
                meta_data.add( 0, getConfiguration().protectionMetaData );
                item.setLore( meta_data );
                stack.setItemMeta( item );
                event.getPlayer().getWorld().dropItemNaturally( event.getPlayer().getLocation(), stack );
            }
            event.setCancelled( true );
        } catch ( ProtectionBlockNotEqualWithPlacedException | ProtectionRegionNullableException | ProtectionAreaNotExistsException | ProtectionWorldException ignored )
        {
        } catch ( ProtectionBlockNullableException e )
        {
            e.printStackTrace();
        } catch ( AccessException e )
        {
            getLanguage().protectionAreaNotAccess.sendMessage( event.getPlayer() );
            event.setCancelled( true );
        }
    }

    @EventHandler( priority = EventPriority.MONITOR )
    public void destroyInvitation( PlayerQuitEvent event )
    {
        InviteSystem.cancel( event.getPlayer() );
    }

    @EventHandler( priority = EventPriority.LOWEST )
    public void onProtectionPlace( ProtectionPlaceEvent event )
    {
        if ( !getConfiguration().onPlaceEffect.enabled )
        {
            return;
        }

        Effect effect;
        if ( ( effect = getConfiguration().onPlaceEffect.getEffect() ) == null )
        {
            return;
        }

        for ( Location location : event.getProtectionArea().getBlocksLocations() )
        {
            if ( !location.getBlock().getType().equals( Material.AIR ) )
            {
                continue;
            }
            if ( location.getWorld().getBlockAt( location.getBlockX(), location.getBlockY() - 1, location.getBlockZ() ).getType().equals( Material.AIR ) )
            {
                continue;
            }
            location.getWorld().playEffect( location, effect, 1 );
        }
    }

    @EventHandler( priority = EventPriority.LOWEST )
    public void onProtectionBreak( PreProtectionBreakEvent event )
    {
        if ( event.isCancelled() )
        {
            return;
        }
        if ( !getConfiguration().onBreakEffect.enabled )
        {
            return;
        }

        Effect effect;
        if ( ( effect = getConfiguration().onBreakEffect.getEffect() ) == null )
        {
            return;
        }

        for ( Location location : event.getProtectionArea().getBlocksLocations() )
        {
            if ( !location.getBlock().getType().equals( Material.AIR ) )
            {
                continue;
            }
            if ( location.getWorld().getBlockAt( location.getBlockX(), location.getBlockY() - 1, location.getBlockZ() ).getType().equals( Material.AIR ) )
            {
                continue;
            }
            location.getWorld().playEffect( location, effect, 1 );
        }
    }

    @EventHandler( priority = EventPriority.LOWEST )
    public void onKickMember( KickMemberEvent event )
    {
        ProtectionArea area = event.getProtectionArea();

        Map< String, String > messages = new HashMap< String, String >();
        messages.put( "player", event.getTarget().getName() );
        messages.put( "id", area.getUuid().toString() );

        area.broadcast( getLanguage().protectionMemberKickMessage.getMessage( messages ) );
        Player target;
        if ( ( target = event.getTarget().getPlayer() ) != null )
        {
            target.sendRawMessage( getLanguage().protectionTargetKickMessage.getMessage( messages ) );
        }
        Bukkit.getConsoleSender().sendRawMessage( getLanguage().protectionMemberKickMessage.getMessage( messages ) );
    }

    @EventHandler( priority = EventPriority.LOWEST )
    public void onInvitePlayer( InvitePlayerEvent event )
    {
        if ( event.isCancelled() )
        {
            return;
        }
        ProtectionArea area = event.getProtectionArea();

        Map< String, String > messages = new HashMap< String, String >();
        messages.put( "player", event.getTarget().getDisplayName() );
        messages.put( "id", area.getUuid().toString() );

        area.broadcast( getLanguage().protectionMemberInviteMessage.getMessage( messages ) );

        event.getTarget().sendRawMessage( getLanguage().protectionTargetInviteMessage.getMessage( messages ) );

        TextComponent message = new TextComponent( "If you want to join " );
        message.setColor( net.md_5.bungee.api.ChatColor.GREEN );
        TextComponent clickable = new TextComponent( "click here" );
        clickable.setColor( net.md_5.bungee.api.ChatColor.RED );
        clickable.setClickEvent( new ClickEvent( ClickEvent.Action.RUN_COMMAND, "/protection accept" ) );
        clickable.setHoverEvent( new HoverEvent( HoverEvent.Action.SHOW_TEXT, new ComponentBuilder( "Click here to join!" ).color( net.md_5.bungee.api.ChatColor.GRAY ).create() ) );
        message.addExtra( clickable );
        message.addExtra( " or type /protection accept" );
        event.getTarget().spigot().sendMessage( message );

        Bukkit.getConsoleSender().sendRawMessage( getLanguage().protectionMemberInviteMessage.getMessage( messages ) );
    }

    @EventHandler( priority = EventPriority.LOWEST )
    public void onJoinMember( JoinMemberEvent event )
    {
        if ( event.isCancelled() )
        {
            return;
        }
        ProtectionArea area = event.getProtectionArea();

        Map< String, String > messages = new HashMap< String, String >();
        messages.put( "player", event.getTarget().getDisplayName() );
        messages.put( "id", area.getUuid().toString() );

        for ( Player member : area.getOnlinePlayers() )
        {
            if ( member.getUniqueId().equals( event.getTarget().getUniqueId() ) )
            {
                continue;
            }
            member.sendRawMessage( getLanguage().protectionMemberJoinMessage.getMessage( messages ) );
        }
        event.getTarget().sendRawMessage( getLanguage().protectionTargetJoinMessage.getMessage( messages ) );
        Bukkit.getConsoleSender().sendRawMessage( getLanguage().protectionMemberJoinMessage.getMessage( messages ) );
    }
}
