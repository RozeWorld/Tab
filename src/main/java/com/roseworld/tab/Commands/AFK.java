package com.roseworld.tab.Commands;

import com.mojang.brigadier.Command;
import com.roseworld.tab.AFKstatus;
import com.roseworld.tab.RankHandler;
import io.papermc.paper.command.brigadier.Commands;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.entity.Player;

public class AFK{
    private Player player;

    public void register(Commands cm){
        cm.register(Commands.literal("afk")
                .requires(context -> {
                    if(context.getSender() instanceof Player sender){
                        player = sender;
                        return true;
                    }
                    return false;
                })
                .executes(context -> {
                    RankHandler.setStatusAFK(player);
                    AFKstatus.setAFK(player);
                    return Command.SINGLE_SUCCESS;
                })
                .build(), "Changes your status to AFK");
    }
}
