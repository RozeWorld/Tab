package com.roseworld.tab.Commands;

import com.rosekingdom.rosekingdom.Core.CommandManager.CommandRK;
import com.rosekingdom.rosekingdom.Core.Database.Main_Statements.UserStatement;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Ranking{

//    public Ranking(){
//        setName("rank");
//        setArgumentRequirement(true);
//    }
//
//
//
//
//
//
//
//
//
//
//
////    ============================================
//
////    THIS SHOULD BE IN THE MOD TOOLS PLUGIN
//
////    ============================================
//
//
//
//
//
//
//
//
//
//
//
//    @Override
//    public void execute(CommandSender sender, String[] args) {
//        Player player;
//        player = Bukkit.getPlayer(args[0]);
//        if(player == null){
//            sender.sendMessage(Component.text("There is no such player!"));
//            return;
//        }
//        UUID uuid = player.getUniqueId();
//        Rank rank = Rank.valueOf(args[1].toUpperCase());
//        UserStatement.setRank(uuid.toString(), rank.name());
//        Tab.join(player);
//    }
//
//    @Override
//    public List<String> tabComplete(CommandSender sender, String[] args) {
//        if(args.length == 1){
//            List<String> players = new ArrayList<>();
//            for(Player player : Bukkit.getOnlinePlayers()){
//                players.add(player.getName());
//            }
//            return players;
//        }
//        if(args.length == 2){
//            List<String> ranks = new ArrayList<>();
//            ranks.add("owner");
//            ranks.add("admin");
//            ranks.add("mod");
//            ranks.add("artist");
//            ranks.add("default");
//            return ranks;
//        }
//        return null;
//    }
}
