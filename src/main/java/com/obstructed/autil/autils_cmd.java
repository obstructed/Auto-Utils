package com.sami.autils.commands;

import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.util.ChatComponentText;

public class autils_cmd extends CommandBase {
    @Override
    public String getCommandName() {
        return "autils";
    }

    @Override
    public String getCommandUsage(ICommandSender sender) {
        return "/autils";
    }

    @Override
    public void processCommand(ICommandSender sender, String[] args) {
        sender.addChatMessage(new ChatComponentText("=============================\n\n"));
        sender.addChatMessage(new ChatComponentText("Auto Utils Help Menu:"));
        sender.addChatMessage(new ChatComponentText("autoboop <add|remove|list|clear> [player] - Manage AutoBoop list"));
        sender.addChatMessage(new ChatComponentText("autoparty <add|remove|list|clear> [player] - Manage AutoParty list"));
        sender.addChatMessage(new ChatComponentText("autils <command> - Show this help menu"));
        sender.addChatMessage(new ChatComponentText("\n\n============================="));
    }

    @Override
    public int getRequiredPermissionLevel() {
        return 0;
    }
}
