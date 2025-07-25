package com.roseworld.tab;

import com.rosekingdom.rosekingdom.Core.Database.Main_Statements.UserStatement;
import com.rosekingdom.rosekingdom.Core.NPCs.NPC;
import com.rosekingdom.rosekingdom.Core.NPCs.NPCHandler;
import com.rosekingdom.rosekingdom.Core.Utils.Message;
import com.roseworld.tab.Commands.CommandManager;
import com.roseworld.tab.Kingdoms.Kingdom;
import com.roseworld.tab.Kingdoms.KingdomHandler;
import com.roseworld.tab.Statements.KingdomStatement;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.Bukkit;
import org.bukkit.ServerLinks;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;

public final class Tab extends JavaPlugin implements Listener {

    @Override
    public void onEnable() {
        new CommandManager(this);

        AFKstatus.check(this);
        RankHandler.registerBaseRanks();
        KingdomStatement.loadKingdoms();

        UserStatement.setRank("96022bb0-c25b-45da-8537-f323edbba03a", Rank.OWNER.name());

        getServer().getPluginManager().registerEvents(this, this);
        getServer().getPluginManager().registerEvents(new AFKstatus(), this);

        ServerLinks();
    }

    @Override
    public void onDisable() {
        this.getLogger().info("Tab is shutting down...");

        KingdomHandler.saveKingdoms();
    }

    @SuppressWarnings({"Experimental", "UnstableApiUsage"})
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onJoin(PlayerJoinEvent e){
        Player player = e.getPlayer();

        TabList.display(player);

        TabList.join(player);
        Kingdom kingdom = KingdomHandler.getKingdom(player);
        if(kingdom != null && kingdom.getInChat().contains(player.getUniqueId())){
            player.sendMessage(Component.text("You are currently chatting with " + kingdom.getName() + "'s members.", TextColor.fromHexString("#5ae630")));
        }

        List<NPC> npcList = NPCHandler.getNPCs();
        npcList.removeAll(KingdomHandler.getSeparators());
        for(NPC npc : npcList){
            npc.spawn();
        }
    }

    @EventHandler
    public void onLeaveEvent(PlayerQuitEvent e){
        Player player = e.getPlayer();
        TabList.updatePlayerCount();

        if(KingdomHandler.isInKingdom(player)){
            Kingdom kingdom = KingdomHandler.getKingdom(player);
            if(kingdom == null) {
                Message.Console("Couldn't fetch kingdom!");
                return;
            }
            KingdomHandler.lastOnline(kingdom);
        }
    }

    @SuppressWarnings({"Experimental", "UnstableApiUsage"})
    private void ServerLinks(){
        ServerLinks serverLinks = Bukkit.getServerLinks();
        try {
            serverLinks.addLink(Component.text("Discord"), new URI("https://discord.gg/WCPcF8zbus"));
            serverLinks.addLink(ServerLinks.Type.REPORT_BUG, URI.create("https://github.com/RozeWorld/RoseKingdom/issues"));
            serverLinks.addLink(ServerLinks.Type.FEEDBACK, URI.create("https://github.com/RozeWorld/RoseKingdom/issues"));
        } catch (URISyntaxException e) {
            Message.Exception("A link is not working", e);
        }
    }
}
