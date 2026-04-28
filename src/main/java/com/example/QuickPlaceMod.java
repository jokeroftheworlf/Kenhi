package com.example;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import org.lwjgl.glfw.GLFW;

public class QuickPlaceMod implements ClientModInitializer {

    private static KeyBinding key;

    // 0 = idle, 1 = slot3/place, 2 = slot2/place
    private static int state = 0;

    @Override
    public void onInitializeClient() {

        key = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.quickplace.trigger",
                InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_GRAVE_ACCENT, // ~ key
                "category.quickplace"
        ));

        ClientTickEvents.END_CLIENT_TICK.register(client -> {

            if (client.player == null || client.interactionManager == null) return;

            // START trigger
            if (key.wasPressed() && state == 0) {
                state = 1;
                return;
            }

            // STEP 1: slot 3 + place
            if (state == 1) {
                doPlace(client, 2); // slot index 2 = hotbar slot 3
                state = 2;
                return;
            }

            // STEP 2: slot 2 + place
            if (state == 2) {
                doPlace(client, 1); // slot index 1 = hotbar slot 2
                state = 0;
            }
        });
    }

    private void doPlace(MinecraftClient client, int slot) {

        if (client.player == null || client.interactionManager == null) return;

        // switch hotbar slot
        client.player.getInventory().selectedSlot = slot;

        // get crosshair target
        HitResult hit = client.crosshairTarget;
        if (!(hit instanceof BlockHitResult blockHit)) return;

        // place block
        client.interactionManager.interactBlock(
                client.player,
                Hand.MAIN_HAND,
                blockHit
        );
    }
}
