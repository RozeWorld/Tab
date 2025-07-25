package com.roseworld.tab;

import com.rosekingdom.rosekingdom.Core.Utils.Message;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Team;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.roseworld.tab.TabList.board;
import static com.roseworld.tab.TabList.getBoard;

public class RankHandler {
    private static final List<Team> baseRanks = new ArrayList<>();
    private static final Map<Player, Team> playerRanks = new HashMap<>();
    private static final Map<Player, Team> isAFK = new HashMap<>();
    private static final Map<String, Integer> rankNumbers = new HashMap<>();
    public static int getRankNumber(String rank){
        return rankNumbers.get(rank);
    }
    public static int getRankNumber(Rank rank){
        return rankNumbers.get(rank.name());
    }
    public static List<Team> getBaseRanks(){
        return baseRanks;
    }
    public static void setPlayerRank(Player player, Team team){
        playerRanks.put(player, team);
    }

    public static Team getPlayerRank(Player player){
        return playerRanks.get(player);
    }

    public static void registerBaseRanks(){
        baseRanks.add(board.registerNewTeam("1001"+Rank.OWNER.name()));
        baseRanks.add(board.registerNewTeam("1002"+Rank.ADMIN.name()));
        baseRanks.add(board.registerNewTeam("1003"+Rank.MOD.name()));
        baseRanks.add(board.registerNewTeam("1004"+Rank.ARTIST.name()));
        baseRanks.add(board.registerNewTeam("1005"+Rank.DEFAULT.name()));
        int number = 1;
        for(Rank rank : Rank.values()){
            rankNumbers.put(rank.name(), number);
            number++;
        }
    }

    public static void setStatusAFK(Player player) {
        if(isAFK.containsKey(player)) return;
        Team base = playerRanks.get(player);
        Team team = getBoard().getTeam(base.getName() + "_AFK");
        if(team == null){
            team = board.registerNewTeam(base.getName() + "_AFK");
            team.prefix(base.prefix());
            team.suffix(Component.text("\uDB00\uDC03\uEa06"));
        }
        player.playerListName(player.playerListName().append(Component.text("\uDB00\uDC03\uEa06")));
        player.displayName(player.displayName().append(Component.text("\uDB00\uDC03\uEa06")));
        //delete the sleep ignore
        player.setSleepingIgnored(true);
        team.addPlayer(player);
        isAFK.put(player, team);
        player.sendMessage(Component.text("You are now AFK!", TextColor.fromHexString("#167ac7")));
        Message.Console(Component.text(player.getName() + " is now AFK!", TextColor.fromHexString("#167ac7")));
    }

    public static void removeStatusAFK(Player player) {
        if(isAFK.containsKey(player)) {
            Team team = isAFK.get(player);
            team.removePlayer(player);
            if(team.getEntries().isEmpty()){
                team.unregister();
            }
            TabList.join(player);
            //delete the sleep ignore
            player.setSleepingIgnored(false);
            isAFK.remove(player);
        }
    }
}
