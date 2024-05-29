package net.derpy.item_vacuums.block.entity;

import com.mojang.serialization.Decoder;
import net.derpy.item_vacuums.item.ModItems;
import net.derpy.item_vacuums.screen.WoodenVacuumMenu;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.Containers;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class WoodenVacuumBlockEntity extends BlockEntity implements MenuProvider {
    private final ItemStackHandler itemHandler = new ItemStackHandler(16);

    private static final int FILTER_SLOT = 0;
    private static final int LAST_SLOT = 15;

    private LazyOptional<IItemHandler> lazyItemHandler = LazyOptional.empty();

    public WoodenVacuumBlockEntity(BlockPos pPos, BlockState pBlockState) {
        super(ModBlockEntities.WOODEN_VACUUM_BE.get(), pPos, pBlockState);
        this.data = new ContainerData() {
            @Override
            public int get(int i) {
                return switch (i){
                    case 0 -> WoodenVacuumBlockEntity.this.progress;
                    case 1 -> WoodenVacuumBlockEntity.this.maxProgress;
                    default -> 0;
                };
            }

            @Override
            public void set(int i, int i1) {
                switch (i){
                    case 0 -> WoodenVacuumBlockEntity.this.progress = i1;
                    case 1 -> WoodenVacuumBlockEntity.this.maxProgress = i1;
                }
            }

            @Override
            public int getCount() {
                return 2;
            }
        };
    }

    protected final ContainerData data;
    private int progress = 0;
    private int maxProgress = 78;

    @Override
    public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
        if(cap == ForgeCapabilities.ITEM_HANDLER){
            return lazyItemHandler.cast();
        }
        return super.getCapability(cap, side);
    }

    @Override
    public void onLoad() {
        super.onLoad();

        lazyItemHandler = LazyOptional.of(() -> itemHandler);
    }

    @Override
    public void invalidateCaps() {
        super.invalidateCaps();
        lazyItemHandler.invalidate();
    }

    public void drops(){
        SimpleContainer inventory = new SimpleContainer(itemHandler.getSlots());
        for(int i = 0; i < itemHandler.getSlots(); i++){
            inventory.setItem(i, itemHandler.getStackInSlot(i));
        }
        Containers.dropContents(this.level, this.worldPosition, inventory);
    }

    @Override
    public Component getDisplayName() {
        return Component.translatable("block.item_vacuums.wooden_vacuum");
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int i, Inventory inventory, Player player) {
        return new WoodenVacuumMenu(i, inventory, this, this.data);
    }

    @Override
    protected void saveAdditional(CompoundTag pTag) {
        pTag.put("inventory", itemHandler.serializeNBT());
        pTag.putInt("wooden_vacuum.progress", progress);
        super.saveAdditional(pTag);
    }

    @Override
    public void load(CompoundTag pTag) {
        super.load(pTag);
        itemHandler.deserializeNBT(pTag.getCompound("inventory"));
        progress = pTag.getInt("wooden_vacuum.progress");
    }

    public void tick(Level pLevel, BlockPos pPos, BlockState pState){
        if(hasRecipe()){
            increaseCraftingProgress();
            setChanged(pLevel, pPos, pState);
            
            if(hasProgressFinished()){
                craftItem();
                resetProgress();
            }
        } else {
            resetProgress();
        }
    }

    private void resetProgress() {
        progress = 0;
    }

    private void craftItem() {
        ItemStack result = new ItemStack(ModItems.SAPPHIRE.get(), 1);
        for (int i = 1; i <= LAST_SLOT; i++){
            if (canInsertItemIntoOutputSlot(result.getItem(), i) && canInsertAmountIntoOutputSlot(result.getCount(), i)){
                this.itemHandler.setStackInSlot(i, new ItemStack(result.getItem(), this.itemHandler.getStackInSlot(i).getCount() + result.getCount()));
                break;
            }
        }
    }

    private boolean hasProgressFinished() {
        return progress >= maxProgress;
    }

    private void increaseCraftingProgress() {
        progress++;
    }

    private boolean hasRecipe() {
        boolean hasCraftingItem = this.itemHandler.getStackInSlot(FILTER_SLOT).getItem() == ModItems.RAW_SAPPHIRE.get();
        ItemStack result = new ItemStack(ModItems.SAPPHIRE.get());

        boolean canInsert = false;
        for(int j = 0; j <= LAST_SLOT; j++){
            if(canInsertAmountIntoOutputSlot(result.getCount(), j) && canInsertItemIntoOutputSlot(result.getItem(), j)){
                canInsert = true;
                break;
            }
        }

        return hasCraftingItem && canInsert;
    }

    private boolean canInsertItemIntoOutputSlot(Item item, int slot) {

        return this.itemHandler.getStackInSlot(LAST_SLOT).isEmpty() || this.itemHandler.getStackInSlot(slot).is(item);
    }

    private boolean canInsertAmountIntoOutputSlot(int count, int slot) {
        return this.itemHandler.getStackInSlot(slot).getCount() + count <= this.itemHandler.getStackInSlot(slot).getMaxStackSize();
    }


}
