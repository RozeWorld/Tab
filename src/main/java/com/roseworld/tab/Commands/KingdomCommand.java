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
                .then(Commands.argument("kingdom", StringArgumentType.string())
                        .executes(context -> {
                            String invite = StringArgumentType.getString(context, "kingdom");
                            if(KingdomHandler.getInvites().contains(invite)){
                                KingdomHandler.acceptInvite(invite, player);
                                return Command.SINGLE_SUCCESS;
                            }
                            player.sendMessage(Message.Info("Invalid invite code!"));
                            return Command.SINGLE_SUCCESS;
                        }))
                .then(Commands.literal("create")
                        .requires(source -> {
                            if(source.getExecutor() instanceof Player king) {
                                Kingdom kingdom = KingdomHandler.getKingdom(king);
                                return kingdom == null;
                            }
                            return false;
                        })
                        .then(Commands.argument("name", StringArgumentType.string())
                                .executes(context -> {
                                    String name = StringArgumentType.getString(context, "name");
                                    if(KingdomHandler.getKingdoms().size()+1 > 10){
                                        player.sendMessage(Message.Warning("Unable to create a kingdom cause the number of kingdoms exceeds the limit!"));
                                    }
                                    if(!KingdomHandler.isInKingdom(player)){
                                        Kingdom newKingdom = new Kingdom(name, player);
                                        newKingdom.createSeparator();
                                        newKingdom.joinKingdom(player);
                                        player.updateCommands();
                                    }else{
                                        player.sendMessage(Message.Info("You are already in a kingdom!"));
                                        player.sendMessage(Message.Warning("You need to abandon your kingdom to create a new one."));
                                    }
                                    return Command.SINGLE_SUCCESS;
                                })))
                .then(Commands.literal("join")
                        .requires(source -> {
                            if(source.getExecutor() instanceof Player king) {
                                Kingdom kingdom = KingdomHandler.getKingdom(king);
                                return kingdom == null;
                            }
                            return false;
                        })
                        .then(Commands.argument("kingdom", StringArgumentType.string())
                                .suggests((context, builder) -> {
                                    KingdomHandler.getKingdoms().forEach(kingdom -> {
                                        if(kingdom.isPublic()) builder.suggest(kingdom.getName());
                                    });
                                    return builder.buildFuture();
                                })
                                .executes(context -> {
                                    String name = StringArgumentType.getString(context, "kingdom");
                                    for (Kingdom kingdom : KingdomHandler.getKingdoms()) {
                                        if (kingdom.getName().equals(name))
                                            if (kingdom.isPublic()) {
                                                kingdom.joinKingdom(player);
                                                player.updateCommands();
                                                return Command.SINGLE_SUCCESS;
                                            } else {
                                                player.sendMessage(Message.Info("This kingdom is not public."));
                                                return Command.SINGLE_SUCCESS;
                                            }
                                    }
                                    player.sendMessage(Message.Warning("There is not such kingdom!"));
                                    return Command.SINGLE_SUCCESS;
                                })))
                .then(Commands.literal("leave")
                        .requires(source -> {
                            if(source.getExecutor() instanceof Player king) {
                                Kingdom kingdom = KingdomHandler.getKingdom(king);
                                return kingdom != null;
                            }
                            return false;
                        })
                        .executes(context -> {
                            Kingdom kingdom = KingdomHandler.getKingdom(player);
                            if(kingdom == null) return Command.SINGLE_SUCCESS;
                            kingdom.leaveKingdom(player);
                            player.updateCommands();
                            return Command.SINGLE_SUCCESS;
                        }))
                .then(Commands.literal("delete")
                        .requires(source -> {
                            if(source.getExecutor() instanceof Player king) {
                                Kingdom kingdom = KingdomHandler.getKingdom(king);
                                return kingdom != null && kingdom.getOwner().equals(player.getUniqueId());
                            }
                            return false;
                        })
                        .executes(context -> {
                            Kingdom kingdom = KingdomHandler.getKingdom(player);
                            if(kingdom != null && kingdom.getOwner().equals(player.getUniqueId())) {
                                kingdom.deleteKingdom();
                                player.sendMessage(Message.Warning(kingdom.getName() + " has been deleted!"));
                                player.updateCommands();
                                return Command.SINGLE_SUCCESS;
                            }else{
                                player.sendMessage(Message.Warning("You don't have a permission to run this action!"));
                            }
                            return Command.SINGLE_SUCCESS;
                        }))
                .then(Commands.literal("public")
                        .requires(source -> {
                            if(source.getExecutor() instanceof Player king) {
                                Kingdom kingdom = KingdomHandler.getKingdom(king);
                                return kingdom != null;
                            }
                            return false;
                        })
                        .executes(context -> {
                            Kingdom kingdom = KingdomHandler.getKingdom(player);
                            if(kingdom != null) {
                                if(kingdom.isPublic()){
                                    player.sendMessage(Message.Info(kingdom.getName() + " is public!"));
                                }else {
                                    player.sendMessage(Message.Info(kingdom.getName() + " is private!"));
                                }
                            }
                            return Command.SINGLE_SUCCESS;
                        })
                        .then(Commands.argument("state", BoolArgumentType.bool())
                                .requires(source -> {
                                    if(source.getExecutor() instanceof Player king) {
                                        Kingdom kingdom = KingdomHandler.getKingdom(king);
                                        return kingdom != null && kingdom.getOwner().equals(player.getUniqueId());
                                    }
                                    return false;
                                })
                                .executes(context -> {
                                    boolean state = BoolArgumentType.getBool(context, "state");
                                    Kingdom kingdom = KingdomHandler.getKingdom(player);
                                    if(kingdom != null && kingdom.getOwner().equals(player.getUniqueId())){
                                        kingdom.setPublic(state);
                                        if(state){
                                            player.sendMessage(Message.Info(kingdom.getName() + " is now public!"));
                                        }else{
                                            player.sendMessage(Message.Info(kingdom.getName() + " is now invite-only!"));
                                        }
                                    }
                                    return Command.SINGLE_SUCCESS;
                                })))
                .then(Commands.literal("rename")
                        .requires(source -> {
                            if(source.getExecutor() instanceof Player king) {
                                Kingdom kingdom = KingdomHandler.getKingdom(king);
                                return kingdom != null && kingdom.getOwner().equals(player.getUniqueId());
                            }
                            return false;
                        })
                        .then(Commands.argument("name", StringArgumentType.string())
                                .executes(context -> {
                                    String newName = StringArgumentType.getString(context, "name");
                                    Kingdom kingdom = KingdomHandler.getKingdom(player);
                                    if(kingdom != null && kingdom.getOwner().equals(player.getUniqueId())){
                                        kingdom.setName(newName);
                                    }
                                    return Command.SINGLE_SUCCESS;
                                })))
                .then(Commands.literal("invite")
                        .requires(source -> {
                            if(source.getExecutor() instanceof Player king) {
                                Kingdom kingdom = KingdomHandler.getKingdom(king);
                                return kingdom != null;
                            }
                            return false;
                        })
                        .then(Commands.argument("player", ArgumentTypes.player())
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
                        .requires(source -> {
                            if(source.getExecutor() instanceof Player king) {
                                Kingdom kingdom = KingdomHandler.getKingdom(king);
                                return kingdom != null;
                            }
                            return false;
                        })
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
                                KingdomHandler.removeKingdomChatter(player);
                                player.sendMessage(Component.text("Switched to the general chat!", TextColor.fromHexString("#5ae630")));
                            }
                            return Command.SINGLE_SUCCESS;
                        }))
                .build(), "Kingdom creation", List.of("kd"));
    }
}
