package fr.entasia.creativetools.command;

import fr.entasia.creativetools.Main;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CreaToolsCmd implements CommandExecutor {



	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if(!(sender instanceof Player))return false;
		if(sender.hasPermission("plugin.creativetools")) {
			if (args.length == 0) sender.sendMessage("§cMet un argument !");
			else if(args[0].equalsIgnoreCase("test")){
			}else if(args[0].equalsIgnoreCase("setspawn")){
				Location l = ((Player) sender).getLocation().getBlock().getLocation();
				Main.world = l.getWorld();
				Main.spawn = new Location(Main.world, l.getX()+0.5, l.getY()+0.2, l.getZ()+0.5);
				Main.main.getConfig().set("spawn.x", l.getX());
				Main.main.getConfig().set("spawn.y", l.getY());
				Main.main.getConfig().set("spawn.z", l.getZ());
				Main.main.getConfig().set("world", l.getWorld().toString());
				Main.main.saveConfig();
				sender.sendMessage("§bSpawn redéfini avec succès !");
			}else sender.sendMessage("§cArgument inconnu !");
		}else sender.sendMessage("§cTu n'as pas la permission d'utiliser cette commande !");
		return true;
	}

}
