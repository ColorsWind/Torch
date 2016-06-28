package net.minecraft.server;

import javax.annotation.Nullable;
// CraftBukkit start
import java.util.List;
import org.bukkit.Location;

import org.bukkit.craftbukkit.entity.CraftHumanEntity;
import org.bukkit.entity.HumanEntity;
// CraftBukkit end

public class InventoryLargeChest implements ITileInventory {

    private String a;
    public ITileInventory left;
    public ITileInventory right;

    // CraftBukkit start - add fields and methods
    public List<HumanEntity> transaction = new java.util.ArrayList<HumanEntity>();

    public ItemStack[] getContents() {
        ItemStack[] result = new ItemStack[this.getSize()];
        for (int i = 0; i < result.length; i++) {
            result[i] = this.getItem(i);
        }
        return result;
    }

    public void onOpen(CraftHumanEntity who) {
        this.left.onOpen(who);
        this.right.onOpen(who);
        transaction.add(who);
    }

    public void onClose(CraftHumanEntity who) {
        this.left.onClose(who);
        this.right.onClose(who);
        transaction.remove(who);
    }

    public List<HumanEntity> getViewers() {
        return transaction;
    }

    public org.bukkit.inventory.InventoryHolder getOwner() {
        return null; // This method won't be called since CraftInventoryDoubleChest doesn't defer to here
    }

    public void setMaxStackSize(int size) {
        this.left.setMaxStackSize(size);
        this.right.setMaxStackSize(size);
    }

    @Override
    public Location getLocation() {
        return left.getLocation(); // TODO: right?
    }
    // CraftBukkit end

    public InventoryLargeChest(String s, ITileInventory itileinventory, ITileInventory itileinventory1) {
        this.a = s;
        if (itileinventory == null) {
            itileinventory = itileinventory1;
        }

        if (itileinventory1 == null) {
            itileinventory1 = itileinventory;
        }

        this.left = itileinventory;
        this.right = itileinventory1;
        if (itileinventory.x_()) {
            itileinventory1.a(itileinventory.y_());
        } else if (itileinventory1.x_()) {
            itileinventory.a(itileinventory1.y_());
        }

    }

    public int getSize() {
        return this.left.getSize() + this.right.getSize();
    }

    public boolean a(IInventory iinventory) {
        return this.left == iinventory || this.right == iinventory;
    }

    public String getName() {
        return this.left.hasCustomName() ? this.left.getName() : (this.right.hasCustomName() ? this.right.getName() : this.a);
    }

    public boolean hasCustomName() {
        return this.left.hasCustomName() || this.right.hasCustomName();
    }

    public IChatBaseComponent getScoreboardDisplayName() {
        return (IChatBaseComponent) (this.hasCustomName() ? new ChatComponentText(this.getName()) : new ChatMessage(this.getName(), new Object[0]));
    }

    @Nullable
    public ItemStack getItem(int i) {
        return i >= this.left.getSize() ? this.right.getItem(i - this.left.getSize()) : this.left.getItem(i);
    }

    @Nullable
    public ItemStack splitStack(int i, int j) {
        return i >= this.left.getSize() ? this.right.splitStack(i - this.left.getSize(), j) : this.left.splitStack(i, j);
    }

    @Nullable
    public ItemStack splitWithoutUpdate(int i) {
        return i >= this.left.getSize() ? this.right.splitWithoutUpdate(i - this.left.getSize()) : this.left.splitWithoutUpdate(i);
    }

    public void setItem(int i, @Nullable ItemStack itemstack) {
        if (i >= this.left.getSize()) {
            this.right.setItem(i - this.left.getSize(), itemstack);
        } else {
            this.left.setItem(i, itemstack);
        }

    }

    public int getMaxStackSize() {
        return Math.min(this.left.getMaxStackSize(), this.right.getMaxStackSize()); // CraftBukkit - check both sides
    }

    public void update() {
        this.left.update();
        this.right.update();
    }

    public boolean a(EntityHuman entityhuman) {
        return this.left.a(entityhuman) && this.right.a(entityhuman);
    }

    public void startOpen(EntityHuman entityhuman) {
        this.left.startOpen(entityhuman);
        this.right.startOpen(entityhuman);
    }

    public void closeContainer(EntityHuman entityhuman) {
        this.left.closeContainer(entityhuman);
        this.right.closeContainer(entityhuman);
    }

    public boolean b(int i, ItemStack itemstack) {
        return true;
    }

    public int getProperty(int i) {
        return 0;
    }

    public void setProperty(int i, int j) {}

    public int g() {
        return 0;
    }

    public boolean x_() {
        return this.left.x_() || this.right.x_();
    }

    public void a(ChestLock chestlock) {
        this.left.a(chestlock);
        this.right.a(chestlock);
    }

    public ChestLock y_() {
        return this.left.y_();
    }

    public String getContainerName() {
        return this.left.getContainerName();
    }

    public Container createContainer(PlayerInventory playerinventory, EntityHuman entityhuman) {
        return new ContainerChest(playerinventory, this, entityhuman);
    }

    public void l() {
        this.left.l();
        this.right.l();
    }
}
