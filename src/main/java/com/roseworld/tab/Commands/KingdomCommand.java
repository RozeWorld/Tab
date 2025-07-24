package com.roseworld.tab.Commands;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.rosekingdom.rosekingdom.Core.Utils.Message;
import com.roseworld.tab.Kingdoms.Kingdom;
import com.roseworld.tab.Kingdoms.KingdomHandler;
import io.papermc.paper.command.brigadier.Commands;
import io.papermc.paper.command.brigadier.argument.ArgumentTypes;
import io.papermc.paper.command.brigadier.argument.resolvers.selector.PlayerSelectorArgumentResolver;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.List;

public class KingdomCommand {
    private Player player;

    public void register(Commands cm){
        cm.register(Commands.literal("kingdom")
                .requires(source -> {
                    if(source.getExecutor() instanceof Player sender){
                        player = sender;
                        return true;
                    }
                    return false;
                })
                .then(Commands.literal("create")
                        .then(Commands.argument("name", StringArgumentType.string())
                                .executes(context -> {
                                    String name = StringArgumentType.getString(context, "name");
                                    if(KingdomHandler.getKingdoms().size()+1 > 10){
                                        player.sendMessage(Message.Warning("Unable to create a kingdom cause the number of kingdoms exceeds the limit!"));
                                    }
                                    if(!KingdomHandler.isInKingdom(player)){
                                        Kingdom kingdom = new Kingdom(name, player);
                                        kingdom.createSeparator();
                                        kingdom.joinKingdom(player);
                                    }else{
                                        player.sendMessage(Message.Info("You are already in a kingdom!"));
                                        player.sendMessage(Message.Warning("You need to abandon your kingdom to create a new one."));
                                    }
                                    return Command.SINGLE_SUCCESS;
                                })))
                .then(Commands.literal("join")
                        .then(Commands.argument("kingdom", StringArgumentType.string())
                                .suggests((context, builder) -> {
                                    KingdomHandler.getKingdoms().forEach(kingdom -> builder.suggest(kingdom.getName()));
                                    return builder.buildFuture();
                                })
                                .executes(context -> {
                                    String name = StringArgumentType.getString(context, "kingdom");
                                    if (!KingdomHandler.isInKingdom(player)) {
                                        for (Kingdom kingdom : KingdomHandler.getKingdoms()) {
                                            if (kingdom.getName().equals(name))
                                                if (kingdom.isPublic()) {
                                                    kingdom.joinKingdom(player);
                                                    return Command.SINGLE_SUCCESS;
                                                } else {
                                                    player.sendMessage(Message.Info("This kingdom is not public."));
                                                    return Command.SINGLE_SUCCESS;
                                                }
                                        }
                                        player.sendMessage(Message.Warning("There is not such kingdom!"));
                                    }else{
                                        player.sendMessage(Message.Info("You are already in a kingdom!"));
                                        player.sendMessage(Message.Warning("You need to abandon your kingdom to join a new one."));
                                    }
                                    return Command.SINGLE_SUCCESS;
                                })))
                .then(Commands.literal("leave")
                        .executes(context -> {
                            Kingdom kingdom = KingdomHandler.getKingdom(player);
                            if(kingdom == null) {
                                player.sendMessage(Message.Info("You are not in a kingdom!"));
                                return Command.SINGLE_SUCCESS;
                            }
                            player.sendMessage(Component.text("You left ").append(Component.text(kingdom.getName()).append(Component.text("!"))).color(TextColor.fromHexString("#5ae630")));
                            kingdom.leaveKingdom(player);
                            return Command.SINGLE_SUCCESS;
                        }))
                .then(Commands.literal("delete")
                        .executes(context -> {
                            Kingdom kingdom = KingdomHandler.getKingdom(player);
                            if(kingdom == null) {
                                player.sendMessage(Message.Info("You are not in a kingdom!"));
                                return Command.SINGLE_SUCCESS;
                            }
                            if(kingdom.getOwner().equals(player.getUniqueId())) {
                                kingdom.deleteKingdom();
                                player.sendMessage(Message.Warning(kingdom.getName() + " has been deleted!"));
                                return Command.SINGLE_SUCCESS;
                            }else{
                                player.sendMessage(Message.Warning("You don't have a permission to run this action!"));
                            }
                            return Command.SINGLE_SUCCESS;
                        }))
                .then(Commands.literal("public")
                        .executes(context -> {
                            Kingdom kingdom = KingdomHandler.getKingdom(player);
                            if(kingdom == null) {
                                player.sendMessage(Message.Info("You are not in a kingdom!"));
                                return Command.SINGLE_SUCCESS;
                            }
                            if(kingdom.isPublic()){
                                player.sendMessage(Message.Info(kingdom.getName() + " is now public!"));
                            }else {
                                player.sendMessage(Message.Info(kingdom.getName() + " is now private!"));
                            }
                            return Command.SINGLE_SUCCESS;
                        })
                        .then(Commands.argument("state", BoolArgumentType.bool())
                                .executes(context -> {
                                    boolean state = BoolArgumentType.getBool(context, "state");
                                    Kingdom kingdom = KingdomHandler.getKingdom(player);
                                    if(kingdom == null) {
                                        player.sendMessage(Message.Info("You are not in a kingdom!"));
                                        return Command.SINGLE_SUCCESS;
                                    }
                                    if(kingdom.getOwner().equals(player.getUniqueId())){
                                        kingdom.setPublic(state);
                                    }else{
                                        player.sendMessage(Message.Warning("You don't have permissions to change the kingdom!"));
                                    }
                                    return Command.SINGLE_SUCCESS;
                                })))
                .then(Commands.literal("rename")
                        .then(Commands.argument("name", StringArgumentType.string()))
                        .executes(context -> {
                            String newName = StringArgumentType.getString(context, "name");
                            Kingdom kingdom = KingdomHandler.getKingdom(player);
                            if(kingdom == null) {
                                player.sendMessage(Message.Info("You are not in a kingdom!"));
                                return Command.SINGLE_SUCCESS;
                            }
                            if(kingdom.getOwner().equals(player.getUniqueId())){
                                kingdom.setName(newName);
                            }else{
                                player.sendMessage(Message.Warning("You don't have permissions to change the kingdom!"));
                            }
                            return Command.SINGLE_SUCCESS;
                        }))
                .then(Commands.literal("invite")
                        .then(Commands.argument("player", ArgumentTypes.players())
                                .executes(context -> {
                                    Kingdom kingdom = KingdomHandler.getKingdom(player);
                                    if(kingdom == null){
                                        player.sendMessage(Message.Warning("You're not in a kingdom!"));
                                        return Command.SINGLE_SUCCESS;
                                    }
                                    String invite = kingdom.createInvite();
                                    Player target = context.getArgument("player", PlayerSelectorArgumentResolver.class).resolve(context.getSource()).getFirst();
                                    target.sendMessage(Component.text("You are invited to join ", TextColor.fromHexString("#5ae630"))
                                            .append(Component.text(kingdom.getName(), TextColor.fromHexString("#5ae630")))
                                            .append(Component.text(" kingdom! ", TextColor.fromHexString("#5ae630")))
                                            .append(Component.text("[Join]", TextColor.fromHexString("#e3af20"))
                                                    .hoverEvent(HoverEvent.showText(Component.text("Click to join!\n", TextColor.fromHexString("#e3af20"))
                                                            .append(Component.text("Invite Code: ", TextColor.fromHexString("#555555")))
                                                            .append(Component.text(invite, TextColor.fromHexString("#AAAAAA")))))
                                                    .clickEvent(ClickEvent.runCommand("/kingdom " + invite))));
                                    player.sendMessage(Component.text("Invite sent to ").append(Component.text(target.getName())).color(TextColor.fromHexString("#e3af20")));
                                    return Command.SINGLE_SUCCESS;
                                })))
                .then(Commands.literal("chat")
                        .executes(context -> {
                            Kingdom kingdom = KingdomHandler.getKingdom(player);
                            if(kingdom != null){
                                if(!kingdom.getInChat().contains(player.getUniqueId())) {
                                    kingdom.joinChat(player);
                                    KingdomHandler.addKingdomChatter(player, kingdom);
                                    player.sendMessage(Component.text("Switched to the kingdom's chat!", TextColor.fromHexString("#5ae630")));
                                    return Command.SINGLE_SUCCESS;
                                }
                                kingdom.leaveChat(player);
                                com.rosekingdom.rosekingdom.Tab.Kingdoms.KingdomHandler.removeKingdomChatter(player);
                                player.sendMessage(Component.text("Switched to the general chat!", TextColor.fromHexString("#5ae630")));
                            }else {
                                player.sendMessage(Message.Warning("You're not in a kingdom!"));
                            }
                            return Command.SINGLE_SUCCESS;
                        }))
                .build(), "Kingdom creation", List.of("kd"));
    }
}
