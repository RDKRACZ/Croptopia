package com.epherical.croptopia.items;

import com.epherical.croptopia.CroptopiaMod;
import com.epherical.croptopia.register.Content;
import net.fabricmc.fabric.api.loot.v1.FabricLootPoolBuilder;
import net.fabricmc.fabric.api.loot.v1.event.LootTableLoadingCallback;
import net.fabricmc.fabric.mixin.loot.table.LootSupplierBuilderHooks;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.entries.LootTableReference;
import net.minecraft.world.level.storage.loot.functions.SetItemCountFunction;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;
import net.minecraft.world.level.storage.loot.providers.number.UniformGenerator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;


public class CropLootTableModifier {
    private static final Logger LOGGER = LoggerFactory.getLogger(CropLootTableModifier.class);

    public static void init() {
        LootTableLoadingCallback.EVENT.register((resourceManager, lootManager, id, fabricLootSupplierBuilder, lootTableSetter) -> {
            if (id.getNamespace().equalsIgnoreCase("minecraft")) {
                String path = id.getPath();
                switch (path) {
                    case "entities/cod", "entities/salmon", "entities/tropical_fish" -> {
                        FabricLootPoolBuilder builder = FabricLootPoolBuilder.builder();
                        builder.withEntry(LootItem.lootTableItem(Content.ROE).build());
                        fabricLootSupplierBuilder.withPool(builder.build());
                    }
                    case "gameplay/fishing/fish" -> {
                        List<LootPool> pools = ((LootSupplierBuilderHooks) fabricLootSupplierBuilder).getPools();
                        if (pools.isEmpty()) {
                            LOGGER.warn("Can not inject into gameplay/fishing/fish as it is empty");
                        } else {
                            FabricLootPoolBuilder builder = FabricLootPoolBuilder.of(pools.get(0));
                            builder.withEntry(LootTableReference.lootTableReference(new ResourceLocation("croptopia", "gameplay/fishing/fish"))
                                    .setWeight(30).build());
                            pools.set(0, builder.build());
                        }
                    }
                    case "entities/squid" -> {
                        FabricLootPoolBuilder builder = FabricLootPoolBuilder.builder();
                        builder.withEntry(LootItem.lootTableItem(Content.CALAMARI).build());
                        fabricLootSupplierBuilder.withPool(builder.build());
                    }
                    case "entities/glow_squid" -> {
                        FabricLootPoolBuilder builder = FabricLootPoolBuilder.builder();
                        builder.withEntry(LootItem.lootTableItem(Content.GLOWING_CALAMARI).build());
                        fabricLootSupplierBuilder.withPool(builder.build());
                    }
                    case "chests/spawn_bonus_chest" -> {
                        FabricLootPoolBuilder builder = FabricLootPoolBuilder.builder();
                        builder.setRolls(ConstantValue.exactly(1));
                        builder.setBonusRolls(ConstantValue.exactly(0));
                        for (SeedItem seed : CroptopiaMod.seeds) {
                            builder.withEntry(
                                    LootItem.lootTableItem(seed)
                                            .setWeight(5)
                                            .apply(SetItemCountFunction.setCount(UniformGenerator.between(3, 8), false)).build()
                            );
                        }
                        fabricLootSupplierBuilder.withPool(builder);
                    }
                }
            }
        });
    }
}