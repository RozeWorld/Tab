package com.roseworld.tab.Kingdoms;

import com.rosekingdom.rosekingdom.Core.NPCs.NPC;
import com.rosekingdom.rosekingdom.Core.Utils.Message;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.util.*;

public class KingdomHandler {
    private static final List<Kingdom> kingdomList = new ArrayList<>();
    public static Map<Kingdom, String> invites = new HashMap<>();
    public static Map<UUID, Kingdom> inChats = new HashMap<>();
    public static Map<Kingdom, NPC> separators = new HashMap<>();

    public static List<Kingdom> getKingdoms(){
        return kingdomList;
    }

    public static List<NPC> getSeparators(){
        List<NPC> npcs = new ArrayList<>();
        for(Kingdom kingdom : kingdomList){
            if(!kingdom.getHasSeparatorOn()){
                npcs.add(separators.get(kingdom));
            }
        }
        return npcs;
    }

    public static void addKingdom(Kingdom kingdom){
        kingdomList.add(kingdom);
        separators.put(kingdom, kingdom.getSeparator());
    }

    public static void removeKingdom(Kingdom kingdom) {
        kingdomList.remove(kingdom);
        int order = 1;
        for(Kingdom k : getKingdoms()){
            k.deleteSeparator();
            k.deleteRanks();
            k.setKingdomNumber(order);
            k.createSeparator();
            k.createRanks();
            for(Player member : k.getOnlinePlayers()){
                k.joinKingdom(member);
            }
            order++;
        }
    }

    public static void addKingdomChatter(Player player, Kingdom kingdom){
        inChats.put(player.getUniqueId(), kingdom);
    }

    public static void removeKingdomChatter(Player player){
        inChats.remove(player.getUniqueId());
    }

    public static Set<UUID> getKingdomChatters(){
        return inChats.keySet();
    }

    public static Kingdom getChatterKingdom(Player player){
        return inChats.get(player.getUniqueId());
    }

    public static Collection<String> getInvites(){
        return invites.values();
    }

    public static void addInvite(Kingdom kingdom, String invite){
        invites.put(kingdom, invite);
    }

    public static void removeInvite(Kingdom kingdom, String invite){
        invites.remove(kingdom, invite);
    }

    public static void acceptInvite(String invite, Player player){
        if(isInKingdom(player)){
            player.sendMessage(Message.Warning("You cannot join this kingdom because you're already in a kingdom!"));
            return;
        }
        for(Kingdom kingdom : invites.keySet()){
            if(invites.get(kingdom).equals(invite)){
                kingdom.joinKingdom(player);
                kingdom.deleteInvite(invite);
                removeInvite(kingdom, invite);
            }
        }
    }

    public static boolean isInKingdom(Player player){
        return getKingdom(player) != null;
    }

    public static Kingdom getKingdom(OfflinePlayer player){
        for(Kingdom kingdom : getKingdoms()) {
            for (UUID members : kingdom.getMembers()) {
                if (members.equals(player.getUniqueId())) {
                    return kingdom;
                }
            }
        }
        return null;
    }
    public static Kingdom getKingdom(String name){
        for(Kingdom kingdom : getKingdoms()){
            if(kingdom.name.equals(name)){
                return kingdom;
            }
        }
        return null;
    }

    public static void lastOnline(Kingdom kingdom){
        if(kingdom.getOnlinePlayers().size()<=1){
            kingdom.deleteSeparator();
        }
    }

    public static void saveKingdoms(){
        for(Kingdom kingdom : getKingdoms()){
            kingdom.save();
        }
    }

    public static void removeKingdomChatters(Kingdom kingdom) {
        for(UUID chatter : kingdom.getInChat()){
            inChats.remove(chatter);
        }
    }
}
