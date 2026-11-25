package pl.autofinder;

import com.mojang.blaze3d.platform.InputConstants;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import org.lwjgl.glfw.GLFW;

import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class AnarchiaAutoFinderClient implements ClientModInitializer {
    private static final Set<String> TARGETS = new LinkedHashSet<>();
    private static final Map<String, Long> NEXT_TPA_TICK = new ConcurrentHashMap<>();

    private static KeyMapping listKey;
    private static long ticks = 0;

    // 40 ticków = około 2 sekundy przy 20 TPS.
    private static final long TPA_INTERVAL_TICKS = 40;

    @Override
    public void onInitializeClient() {
        KeyMapping.Category category = KeyMapping.Category.register(
                Identifier.fromNamespaceAndPath("anarchia_autofinder", "main")
        );

        listKey = KeyBindingHelper.registerKeyBinding(new KeyMapping(
                "key.anarchia_autofinder.open_list",
                InputConstants.Type.KEYSYM,
                GLFW.GLFW_KEY_6,
                category
        ));

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            while (listKey.consumeClick()) {
                if (client.player == null) return;

                if (client.screen instanceof TargetsScreen) {
                    client.setScreen(null);
                } else {
                    client.setScreen(new TargetsScreen());
                }
            }

            if (client.player == null || client.level == null || client.getConnection() == null) {
                return;
            }

            ticks++;

            // Skanuj co pół sekundy.
            if (ticks % 10 == 0) {
                scanPlayers(client);
            }

            // Ponawiaj /tpa osobno dla każdego wykrytego gracza co 2 sekundy.
            for (String name : Set.copyOf(TARGETS)) {
                long next = NEXT_TPA_TICK.getOrDefault(name, 0L);
                if (ticks >= next) {
                    client.getConnection().sendCommand("tpa " + name);
                    NEXT_TPA_TICK.put(name, ticks + TPA_INTERVAL_TICKS);
                }
            }
        });
    }

    private static void scanPlayers(Minecraft client) {
        Set<String> seenNow = new LinkedHashSet<>();

        for (Player other : client.level.players()) {
            if (other == client.player || other.isRemoved()) continue;

            // Ograniczenie do graczy w zasięgu 64 bloków.
            if (other.distanceToSqr(client.player) > 64.0 * 64.0) continue;

            if (hasElytra(other) || hasNetheriteArmor(other)) {
                seenNow.add(other.getName().getString());
            }
        }

        // Usuń graczy, którzy zniknęli z zasięgu lub nie spełniają już warunku.
        TARGETS.retainAll(seenNow);
        NEXT_TPA_TICK.keySet().retainAll(seenNow);
        TARGETS.addAll(seenNow);
    }

    private static boolean hasElytra(Player player) {
        ItemStack chest = player.getItemBySlot(EquipmentSlot.CHEST);
        return chest.is(Items.ELYTRA);
    }

    private static boolean hasNetheriteArmor(Player player) {
        return player.getItemBySlot(EquipmentSlot.HEAD).is(Items.NETHERITE_HELMET)
                || player.getItemBySlot(EquipmentSlot.CHEST).is(Items.NETHERITE_CHESTPLATE)
                || player.getItemBySlot(EquipmentSlot.LEGS).is(Items.NETHERITE_LEGGINGS)
                || player.getItemBySlot(EquipmentSlot.FEET).is(Items.NETHERITE_BOOTS);
    }

    static Set<String> getTargetsSnapshot() {
        return new LinkedHashSet<>(TARGETS);
    }

    static long getIntervalSeconds() {
        return TPA_INTERVAL_TICKS / 20;
    }
}
