package me.chrommob.cheques;

import de.tr7zw.changeme.nbtapi.NBTItem;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;

public class EventListener implements Listener {
    @EventHandler
    public void onRightClick(PlayerInteractEvent event) {
        if (event.getItem() == null) {
            return;
        }
        if (event.getItem().getType() != Material.PAPER) {
            return;
        }
        if (!Cheques.getInstance().getChequesManager().isChequeValid(event.getItem())) {
            String chequeClaimer = Cheques.getInstance().getChequesManager().getChequeClaimer(event.getItem());
            if (chequeClaimer != null) {
                event.getPlayer().sendMessage(ChatColor.BOLD + "" + ChatColor.RED + "Tento šek již byl vyměněn za peníze!");
                event.getPlayer().sendMessage(ChatColor.BOLD + "" + ChatColor.RED + "Byl vyplacen hráči: " + ChatColor.RESET + chequeClaimer);
            }
            return;
        }
        event.setCancelled(true);
        NBTItem nbtItem = new NBTItem(event.getItem());
        double amount = nbtItem.getDouble("cheque.amount");
        String uuid = nbtItem.getString("cheque.id");
        event.getPlayer().getInventory().remove(event.getItem());
        Cheques.getInstance().getChequesManager().addMoney(event.getPlayer(), amount, uuid);
        event.getPlayer().sendMessage(ChatColor.BOLD + "" + ChatColor.GREEN + "Bylo vám připsáno " + amount + "₾OG!");
    }
}
