package me.chrommob.cheques;

import de.tr7zw.changeme.nbtapi.NBTItem;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class ChequesManager {
    private final Economy economy;
    private final DatabaseManager databaseManager = new DatabaseManager();

    public ChequesManager() {
        this.economy = Cheques.getInstance().getServer().getServicesManager().getRegistration(Economy.class).getProvider();
    }


    public ItemStack getChequeItem(double amount) {
        ItemStack cheque = new ItemStack(Material.PAPER);
        cheque.setAmount(1);
        ItemMeta itemMeta = cheque.getItemMeta();
        itemMeta.setDisplayName("Šek");
        List<String> lore = new ArrayList<>();
        lore.add("Částka: " + amount + "₾OG");
        itemMeta.setLore(lore);
        cheque.setItemMeta(itemMeta);
        NBTItem nbtItem = new NBTItem(cheque);
        nbtItem.addCompound("cheque");
        nbtItem.setDouble("cheque.amount", amount);
        nbtItem.setString("cheque.id", UUID.randomUUID().toString());
        databaseManager.addCheque(nbtItem.getString("cheque.id"), amount);
        return nbtItem.getItem();
    }

    public boolean isChequeValid(ItemStack cheque) {
        NBTItem nbtItem = new NBTItem(cheque);
        if (!nbtItem.hasCustomNbtData()) return false;
        if (!nbtItem.hasKey("cheque")) return false;
        if (!nbtItem.hasKey("cheque.id")) return false;
        if (!nbtItem.hasKey("cheque.amount")) return false;
        return databaseManager.isChequeValid(nbtItem.getString("cheque.id"), nbtItem.getDouble("cheque.amount"));
    }

    public void addMoney(Player player, double amount, String uuid) {
        economy.depositPlayer(player, amount);
        databaseManager.setClaimed(uuid, player);
    }

    public String getChequeClaimer(ItemStack toVerify) {
        NBTItem nbtItem = new NBTItem(toVerify);
        return databaseManager.getChequeClaimer(nbtItem.getString("cheque.id"));
    }

    public DatabaseManager getDatabaseManager() {
        return databaseManager;
    }
}
