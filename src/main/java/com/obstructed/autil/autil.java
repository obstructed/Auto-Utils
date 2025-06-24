package com.sami.autils;

import net.minecraftforge.client.event.ClientChatReceivedEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraft.client.Minecraft;

@Mod(modid = autil.MODID, name = autil.NAME, version = autil.VERSION, clientSideOnly = true)
public class autil {
    public static final String MODID = "autils";
    public static final String NAME = "Auto Utils Mod";
    public static final String VERSION = "1.0";

    @Mod.EventHandler
    public void init(FMLInitializationEvent event) {
        MinecraftForge.EVENT_BUS.register(this);
        System.out.println(NAME + " loaded.");
    }

    @SubscribeEvent
    public void onChat(ClientChatReceivedEvent event) {
        String message = event.message.getUnformattedText();

        if (message.startsWith("Friend > ") && message.endsWith(" joined.")) {
            String stripped = message.substring(9, message.length() - 8).trim();
            Minecraft.getMinecraft().thePlayer.sendChatMessage("/boop " + stripped);
        }
    }
}