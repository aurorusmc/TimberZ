package com.zetaplugins.timberz.bootstrap;

import com.zetaplugins.timberz.TimberZ;
import io.papermc.paper.plugin.bootstrap.BootstrapContext;
import io.papermc.paper.plugin.bootstrap.PluginBootstrap;
import io.papermc.paper.plugin.bootstrap.PluginProviderContext;
import io.papermc.paper.registry.data.EnchantmentRegistryEntry;
import io.papermc.paper.registry.event.RegistryEvents;
import io.papermc.paper.registry.keys.EnchantmentKeys;
import io.papermc.paper.registry.keys.tags.ItemTypeTagKeys;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.text.Component;
import org.bukkit.inventory.EquipmentSlotGroup;
import org.bukkit.plugin.java.JavaPlugin;

public final class TimberZBootstrap implements PluginBootstrap {
    @Override
    public void bootstrap(BootstrapContext context) {
        context.getLifecycleManager().registerEventHandler(RegistryEvents.ENCHANTMENT.freeze().newHandler(event -> {
            event.registry().register(
                    EnchantmentKeys.create(Key.key("timberz:timber")),
                    b -> {
                        b
                                .maxLevel(1)
                                .weight(10)
                                .anvilCost(1)
                                .minimumCost(EnchantmentRegistryEntry.EnchantmentCost.of(1, 1))
                                .maximumCost(EnchantmentRegistryEntry.EnchantmentCost.of(50, 1))
                                .activeSlots(EquipmentSlotGroup.ANY)
                                .supportedItems(event.getOrCreateTag(ItemTypeTagKeys.AXES))
                                .description(Component.text("Timber"));
                    }
            );
        }));
    }

    @Override
    public JavaPlugin createPlugin(PluginProviderContext context) {
        return new TimberZ();
    }
}
