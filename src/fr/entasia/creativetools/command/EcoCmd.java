package fr.entasia.creativetools.command;

import fr.entasia.creativetools.Main;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.sql.SQLException;

public class EcoCmd implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if(!(sender instanceof Player))return false;
		Player p = (Player) sender;
		if (p.hasPermission("entasia.eco")) {
			if (args.length == 3) {
				Player target = Bukkit.getPlayer(args[1]);
				if(target==null)p.sendMessage("§cLe joueur "+args[1]+" n'est pas connecté ou n'existe pas !");
				else {
					int coins;
					try {
						coins = Integer.parseInt(args[2]);
					} catch (NumberFormatException e) {
						p.sendMessage("§cLe nombre " + args[2] + " est invalide !");
						return true;
					}
					try {
						if (args[0].equalsIgnoreCase("give")) {
							Main.sqlConnection.fastUpdateUnsafe("update playerdata.stats set crea_money=crea_money+? WHERE uuid=?", coins, target.getUniqueId());
							p.sendMessage("§eEco §8» §7Tu as ajouté " + args[2] + " à la monnaie de " + args[1] + " !");
						} else if (args[0].equalsIgnoreCase("set")) {
							Main.sqlConnection.fastUpdateUnsafe("update playerdata.stats set crea_money=? WHERE uuid=?", coins, target.getUniqueId());
							p.sendMessage("§eEco §8» §7La monnaie de " + args[1] + " à été définie à " + args[2] + " !");
						}
					} catch (SQLException e) {
						e.printStackTrace();
						Main.sqlConnection.broadcastError();
						p.sendMessage("§cUne erreur interne est survenue !");
						return true;
					}
				}
			}else p.sendMessage("§7Syntaxe : /eco <give/set> <player> <number>");
		}
		return true;
	}

}
