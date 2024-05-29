package net.derpy.item_vacuums.block.entity;

import net.derpy.item_vacuums.ItemVacuumsMod;
import net.derpy.item_vacuums.block.ModBlocks;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModBlockEntities {
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES =
            DeferredRegister.create(ForgeRegistries.BLOCK_ENTITY_TYPES, ItemVacuumsMod.MOD_ID);

    public static final RegistryObject<BlockEntityType<WoodenVacuumBlockEntity>> WOODEN_VACUUM_BE =
            BLOCK_ENTITIES.register("wooden_vacuum_be", () ->
                    BlockEntityType.Builder.of(WoodenVacuumBlockEntity::new,
                            ModBlocks.WOODEN_VACUUM.get()).build(null));

    public static void register(IEventBus eventBus){
        BLOCK_ENTITIES.register(eventBus);
    }
}
