package me.chrommob.cheques;

import co.aikar.commands.BaseCommand;
import co.aikar.commands.annotation.CommandAlias;
import co.aikar.commands.annotation.CommandCompletion;
import co.aikar.commands.annotation.CommandPermission;
import co.aikar.commands.annotation.Subcommand;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

@CommandAlias("cheques|seky")
public class Commands extends BaseCommand {
    @CommandPermission("cheques.admin")
    @Subcommand("get")
    @CommandCompletion("@range:0-100")
    public void onGetCommand(CommandSender sender, double amount) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("§cTento příkaz můžeš použít pouze jako hráč.");
            return;
        }
        Player player = (Player) sender;
        player.getInventory().addItem(Cheques.getInstance().getChequesManager().getChequeItem(amount));
    }

    @CommandPermission("cheques.verify")
    @Subcommand("verify")
    public void onVerifyCommand(CommandSender sender) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("Tento příkaz můžeš použít pouze jako hráč.");
            return;
        }
        sender.sendMessage("Začínám ověřovat šeky v tvé ruce...");
        Player player = (Player) sender;
        ItemStack toVerify = player.getInventory().getItemInMainHand();
        if (toVerify == null) {
            player.sendMessage("V ruce nemáš žádný předmět.");
            return;
        }
        if (toVerify.getType() != Material.PAPER) {
            player.sendMessage("V ruce nemáš papír.");
            return;
        }
        if (!Cheques.getInstance().getChequesManager().isChequeValid(toVerify)) {
            player.sendMessage("Tento šek není platný.");
            player.sendMessage("Byl už vybrán hráčem: " + Cheques.getInstance().getChequesManager().getChequeClaimer(toVerify));
            return;
        }
        player.sendMessage("Tento šek je platný.");
    }

    @CommandPermission("cheques.admin")
    @Subcommand("getAll")
    public void onGetAllCommand(CommandSender sender) {
        sender.sendMessage("Začínám vypisovat cenu všech šeků...");
        sender.sendMessage("Celková cena všech šeků: " +Cheques.getInstance().getChequesManager().getDatabaseManager().getTotalAmountDistributed());
    }

    @CommandPermission("cheques.admin")
    @Subcommand("getClaimed")
    public void onGetClaimedCommand(CommandSender sender) {
        sender.sendMessage("Začínám vypisovat cenu všech vybraných šeků...");
        sender.sendMessage("Celková cena všech vybraných šeků: " +Cheques.getInstance().getChequesManager().getDatabaseManager().getTotalAmountClaimed());
    }

    @CommandPermission("cheques.admin")
    @Subcommand("getUnclaimed")
    public void onGetUnclaimedCommand(CommandSender sender) {
        sender.sendMessage("Začínám vypisovat cenu všech nevybraných šeků...");
        sender.sendMessage("Celková cena všech nevybraných šeků: " +Cheques.getInstance().getChequesManager().getDatabaseManager().getTotalAmountNotClaimed());
    }
}
