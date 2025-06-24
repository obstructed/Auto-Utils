package com.sami.autils.commands;

import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandSender;
import net.minecraft.util.ChatComponentText;

public class AutoUtilsCommand extends CommandBase {
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
        sender.addChatMessage(new ChatComponentText("§bAuto Utils Help Menu:"));
        sender.addChatMessage(new ChatComponentText("§7/autoboop <add|remove|list|clear> [player] - Manage AutoBoop list"));
        sender.addChatMessage(new ChatComponentText("§7/autoparty <add|remove|list|clear> [player] - Manage AutoParty list"));
    }

    @Override
    public int getRequiredPermissionLevel() {
        return 0;
    }
}
