package com.sami.autils.commands;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import net.minecraft.client.Minecraft;
import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.util.ChatComponentText;
import net.minecraftforge.client.event.ClientChatReceivedEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.io.*;
import java.lang.reflect.Type;
import java.util.*;

public class AutoParty extends CommandBase {
    private static final List<String> partyList = new ArrayList<>();
    private static final File configFile = new File(Minecraft.getMinecraft().mcDataDir, "autils/autoparty.json");
    private static final Gson gson = new Gson();
    private static final long PARTY_TIMEOUT = 120_000;
    private static final long DELAY_MS = 750;

    private static long lastPartyTime = 0L;

    static {
        loadList();
        MinecraftForge.EVENT_BUS.register(new AutoParty());
    }

    @Override
    public String getCommandName() {
        return "autoparty";
    }

    @Override
    public String getCommandUsage(ICommandSender sender) {
        return "/autoparty <add/remove/list/clear> [player]";
    }

    @Override
    public void processCommand(ICommandSender sender, String[] args) {
        if (args.length == 0) {
            sender.addChatMessage(new ChatComponentText("Usage: /autoparty <add/remove/list/clear> [player]"));
            return;
        }

        String sub = args[0].toLowerCase();

        switch (sub) {
            case "add":
                if (args.length < 2) {
                    sender.addChatMessage(new ChatComponentText("Usage: /autoparty add <player>"));
                    return;
                }
                String nameToAdd = args[1].toLowerCase();
                if (!partyList.stream().anyMatch(p -> p.equalsIgnoreCase(nameToAdd))) {
                    partyList.add(args[1]);
                    saveList();
                    sender.addChatMessage(new ChatComponentText("Added " + args[1] + " to AutoParty list."));
                } else {
                    sender.addChatMessage(new ChatComponentText(args[1] + " is already in the list."));
                }
                break;

            case "remove":
                if (args.length < 2) {
                    sender.addChatMessage(new ChatComponentText("Usage: /autoparty remove <player>"));
                    return;
                }
                String nameToRemove = args[1].toLowerCase();
                boolean removed = partyList.removeIf(p -> p.equalsIgnoreCase(nameToRemove));
                if (removed) {
                    saveList();
                    sender.addChatMessage(new ChatComponentText("Removed " + args[1] + " from AutoParty list."));
                } else {
                    sender.addChatMessage(new ChatComponentText(args[1] + " was not in the list."));
                }
                break;

            case "list":
                if (partyList.isEmpty()) {
                    sender.addChatMessage(new ChatComponentText("AutoParty list is empty."));
                } else {
                    sender.addChatMessage(new ChatComponentText("AutoParty List:"));
                    for (String player : partyList) {
                        sender.addChatMessage(new ChatComponentText(" - " + player));
                    }
                }
                break;

            case "clear":
                partyList.clear();
                saveList();
                sender.addChatMessage(new ChatComponentText("Cleared AutoParty list."));
                break;

            default:
                sender.addChatMessage(new ChatComponentText("Unknown subcommand. Use /autoparty <add/remove/list/clear>"));
                break;
        }
    }

    @Override
    public int getRequiredPermissionLevel() {
        return 0;
    }

    @SubscribeEvent
    public void onChat(ClientChatReceivedEvent event) {
        String message = event.message.getUnformattedText();
        if (message.startsWith("Friend > ") && message.endsWith(" joined.")) {
            String name = message.substring(9, message.length() - 8).trim();
            boolean match = partyList.stream().anyMatch(p -> p.equalsIgnoreCase(name));
            if (!match) return;

            long now = System.currentTimeMillis();

            if (now - lastPartyTime >= PARTY_TIMEOUT) {
                sendChat("/party " + name);
                lastPartyTime = now;
            }
        }
    }

    private void sendChat(String message) {
        new Thread(() -> {
            try {
                Thread.sleep(DELAY_MS);
            } catch (InterruptedException ignored) {}
            Minecraft.getMinecraft().thePlayer.sendChatMessage(message);
        }).start();
    }

    private static void saveList() {
        try {
            configFile.getParentFile().mkdirs();
            try (Writer writer = new FileWriter(configFile)) {
                gson.toJson(partyList, writer);
            }
        } catch (IOException e) {
            System.err.println("[AutoParty] Failed to save autoparty list: " + e.getMessage());
        }
    }

    private static void loadList() {
        if (!configFile.exists()) return;
        try (Reader reader = new FileReader(configFile)) {
            Type type = new TypeToken<List<String>>() {}.getType();
            List<String> loaded = gson.fromJson(reader, type);
            if (loaded != null) partyList.addAll(loaded);
        } catch (IOException e) {
            System.err.println("[AutoParty] Failed to load autoparty list: " + e.getMessage());
        }
    }
}
