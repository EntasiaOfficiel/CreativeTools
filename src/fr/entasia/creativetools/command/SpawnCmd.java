package fr.entasia.creativetools.command;

import fr.entasia.creativetools.Main;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class SpawnCmd implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if(!(sender instanceof Player))return false;
		((Player) sender).teleport(Main.spawn);
		sender.sendMessage("§6Téléportation au spawn réussie !");
		return false;
	}

}
