package com.example;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;

import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;

import org.lwjgl.glfw.GLFW;

public class QuickPlaceMod implements ClientModInitializer {

    private static KeyBinding toggleKey;
    private static boolean enabled = false;

    @Override
    public void onInitializeClient() {

        toggleKey = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.quickplace.toggle",
                InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_GRAVE_ACCENT,
                "category.quickplace"
        ));

        ClientTickEvents.END_CLIENT_TICK.register(client -> {

            if (toggleKey.wasPressed()) {
                enabled = !enabled;
            }

            if (!enabled) return;

            run(client);
        });
    }

    private void run(MinecraftClient client) {
        if (client.player == null || client.interactionManager == null) return;

        HitResult hit = client.crosshairTarget;
        if (!(hit instanceof BlockHitResult blockHit)) return;

        // slot 3 (index 2)
        client.player.getInventory().selectedSlot = 2;
        client.interactionManager.interactBlock(client.player, Hand.MAIN_HAND, blockHit);

        // slot 2 (index 1)
        client.player.getInventory().selectedSlot = 1;
        client.interactionManager.interactBlock(client.player, Hand.MAIN_HAND, blockHit);
    }
}
