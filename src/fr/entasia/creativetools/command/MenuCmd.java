package fr.entasia.creativetools.command;

import fr.entasia.creativetools.utils.InvsManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class MenuCmd implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command arg1, String arg2, String[] args) {
		if(!(sender instanceof Player)) return false;
		InvsManager.plotListOpen((Player)sender);
		return true;
	}

}
