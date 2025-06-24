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

public class AutoBoop extends CommandBase {
    private static final List<String> boopList = new ArrayList<>();
    private static final File configFile = new File(Minecraft.getMinecraft().mcDataDir, "autils/autoboop.json");
    private static final Gson gson = new Gson();
    private static final int MAX_BOOO_LENGTH = 16;
    private static final long BOOP_TIMEOUT = 120_000;
    private static final long DELAY_MS = 500;

    private static long lastBoopTime = 0L;
    private static long lastBoopAttemptTime = 0L;
    private static int boopIndex = 0;

    static {
        loadList();
        MinecraftForge.EVENT_BUS.register(new AutoBoop());
    }

    @Override
    public String getCommandName() {
        return "autoboop";
    }

    @Override
    public String getCommandUsage(ICommandSender sender) {
        return "/autoboop <add|remove|list|clear> [player]";
    }

    @Override
    public void processCommand(ICommandSender sender, String[] args) {
        if (args.length == 0) {
            sender.addChatMessage(new ChatComponentText("Usage: /autoboop <add|remove|list|clear> [player]"));
            return;
        }

        String sub = args[0].toLowerCase();

        switch (sub) {
            case "add":
                if (args.length < 2) {
                    sender.addChatMessage(new ChatComponentText("Usage: /autoboop add <player>"));
                    return;
                }
                String nameToAdd = args[1].toLowerCase();
                if (!boopList.stream().anyMatch(p -> p.equalsIgnoreCase(nameToAdd))) {
                    boopList.add(args[1]);
                    saveList();
                    sender.addChatMessage(new ChatComponentText("Added " + args[1] + " to AutoBoop list."));
                } else {
                    sender.addChatMessage(new ChatComponentText(args[1] + " is already in the list."));
                }
                break;

            case "remove":
                if (args.length < 2) {
                    sender.addChatMessage(new ChatComponentText("Usage: /autoboop remove <player>"));
                    return;
                }
                String nameToRemove = args[1].toLowerCase();
                boolean removed = boopList.removeIf(p -> p.equalsIgnoreCase(nameToRemove));
                if (removed) {
                    saveList();
                    sender.addChatMessage(new ChatComponentText("Removed " + args[1] + " from AutoBoop list."));
                } else {
                    sender.addChatMessage(new ChatComponentText(args[1] + " was not in the list."));
                }
                break;

            case "list":
                if (boopList.isEmpty()) {
                    sender.addChatMessage(new ChatComponentText("AutoBoop list is empty."));
                } else {
                    sender.addChatMessage(new ChatComponentText("AutoBoop List:"));
                    for (String player : boopList) {
                        sender.addChatMessage(new ChatComponentText(" - " + player));
                    }
                }
                break;

            case "clear":
                boopList.clear();
                saveList();
                sender.addChatMessage(new ChatComponentText("Cleared AutoBoop list."));
                break;

            default:
                sender.addChatMessage(new ChatComponentText("Unknown subcommand. Use /autoboop <add|remove|list|clear>"));
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
            boolean match = boopList.stream().anyMatch(p -> p.equalsIgnoreCase(name));
            if (!match) return;

            long now = System.currentTimeMillis();

            if (now - lastBoopTime >= BOOP_TIMEOUT) {
                sendChat("/boop " + name);
                boopIndex = 0;
                lastBoopTime = now;
            } else {
                if (now - lastBoopAttemptTime >= BOOP_TIMEOUT) {
                    boopIndex = 0;
                }

                String msg = generateBoopMessage();
                sendChat("/w " + name + " " + msg);
                boopIndex++;
            }

            lastBoopAttemptTime = now;
        }
    }

    private String generateBoopMessage() {
        int oCount = 2 + (boopIndex * 2);
        if (oCount > MAX_BOOO_LENGTH) oCount = MAX_BOOO_LENGTH;
        StringBuilder sb = new StringBuilder("B");
        for (int i = 0; i < oCount; i++) sb.append("o");
        sb.append("p!");
        return sb.toString();
    }

    private void sendChat(String message) {
        new Thread(() -> {
            try {
                Thread.sleep(DELAY_MS);
            } catch (InterruptedException ignored) {}
            Minecraft.getMinecraft().thePlayer.sendChatMessage(message);
        }).start();
    }

    public static List<String> getBoopList() {
        return boopList;
    }

    private static void saveList() {
        try {
            configFile.getParentFile().mkdirs();
            try (Writer writer = new FileWriter(configFile)) {
                gson.toJson(boopList, writer);
            }
        } catch (IOException e) {
            System.err.println("[AutoBoop] Failed to save autoboop list: " + e.getMessage());
        }
    }

    private static void loadList() {
        if (!configFile.exists()) return;
        try (Reader reader = new FileReader(configFile)) {
            Type type = new TypeToken<List<String>>() {}.getType();
            List<String> loaded = gson.fromJson(reader, type);
            if (loaded != null) boopList.addAll(loaded);
        } catch (IOException e) {
            System.err.println("[AutoBoop] Failed to load autoboop list: " + e.getMessage());
        }
    }
}
