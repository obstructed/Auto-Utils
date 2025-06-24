package com.sami.autils.commands;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.util.ChatComponentText;

import java.io.*;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class AutoBoop extends CommandBase {
    private static final List<String> boopList = new ArrayList<>();
    private static final File configFile = new File(Minecraft.getMinecraft().mcDataDir, "autils/autoboop.json");
    private static final Gson gson = new Gson();

    static {
        loadList();
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
            Minecraft.getMinecraft().displayGuiScreen(new GuiAutoBoopConfig());
            return;
        }

        String sub = args[0].toLowerCase();

        switch (sub) {
            case "add":
                if (args.length < 2) {
                    sender.addChatMessage(new ChatComponentText("Usage: /autoboop add <player>"));
                    return;
                }
                if (!boopList.contains(args[1])) {
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
                if (boopList.remove(args[1])) {
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

    public static class GuiAutoBoopConfig extends GuiScreen {
        @Override
        public void drawScreen(int mouseX, int mouseY, float partialTicks) {
            drawDefaultBackground();
            drawCenteredString(fontRendererObj, "AutoBoop Config", width / 2, height / 2 - 10, 0xFFFFFF);
            super.drawScreen(mouseX, mouseY, partialTicks);
        }

        @Override
        public boolean doesGuiPauseGame() {
            return false;
        }
    }
}