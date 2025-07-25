package com.roseworld.tab;

import com.rosekingdom.rosekingdom.RoseKingdom;
import com.roseworld.tab.Kingdoms.Kingdom;
import com.roseworld.tab.Kingdoms.KingdomHandler;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.permissions.PermissionAttachment;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitScheduler;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.ScoreboardManager;
import org.bukkit.scoreboard.Team;

import static com.rosekingdom.rosekingdom.Core.Database.Main_Statements.UserStatement.getRank;

public class TabList {
    static ScoreboardManager manager = Bukkit.getScoreboardManager();
    static Scoreboard board = manager.getNewScoreboard();
    public static Scoreboard getBoard(){
        return board;
    }

    public static void join(Player player){
        String rankName = getRank(player.getUniqueId());
        Rank rank = Rank.valueOf(rankName);
        RankColor color = RankColor.valueOf(rankName);
        Kingdom kingdom = KingdomHandler.getKingdom(player);
        if(kingdom != null){
            kingdom.joinKingdom(player);
            if(!kingdom.getOnlinePlayers().isEmpty()){
                kingdom.createSeparator();
            }
        }else{
            for(Team ranks : RankHandler.getBaseRanks()){
                if(ranks.getName().contains(rankName)){
                    player.playerListName(Component.text(rank.prefix).append(Component.text(player.getName(), TextColor.fromHexString(color.color))));
                    player.displayName(Component.text(rank.prefix).append(Component.text(player.getName(), TextColor.fromHexString(color.color))));
                    ranks.prefix(Component.text(rank.prefix));
                    ranks.addPlayer(player);
                    RankHandler.setPlayerRank(player, ranks);
                    break;
                }
            }
        }

        refreshScoreboard();

        //Permissions
        PermissionAttachment attachment = player.addAttachment(JavaPlugin.getPlugin(Tab.class));
        attachment.setPermission("rk." + rank, true);
        player.updateCommands();
    }

    public static void display(Player player){
        player.sendPlayerListHeader(Component.text("\u00a7f\uef05\uDAFF\uDFFF\uef06\uDAFF\uDFFF\uef07\uDAFF\uDFFE\uef08\n\n"));
        updatePlayerCount();
    }

    public static void updatePlayerCount(){
        BukkitScheduler scheduler = Bukkit.getScheduler();
        scheduler.scheduleSyncDelayedTask(JavaPlugin.getPlugin(RoseKingdom.class), () -> {
            for(Player on : Bukkit.getServer().getOnlinePlayers()){
                on.sendPlayerListFooter(Component.text("\nOnline Players: ", TextColor.fromHexString("#FFB415"))
                        .append(Component.text(Bukkit.getOnlinePlayers().size(),TextColor.fromHexString("#2eff31"))));
            }
        }, 10);
    }

    public static void refreshScoreboard(){
        for(Player online : Bukkit.getOnlinePlayers()){
            online.setScoreboard(board);
        }
    }
}
