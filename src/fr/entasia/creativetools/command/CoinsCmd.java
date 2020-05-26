package fr.entasia.creativetools.command;

import fr.entasia.creativetools.Main;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.sql.ResultSet;
import java.sql.SQLException;

public class CoinsCmd implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if(!(sender instanceof Player))return false;
		try{
			if (args.length == 0) {
				Player p = (Player) sender;
				ResultSet rs = Main.sqlConnection.fastSelectUnsafe("SELECT crea_money FROM playerdata.stats WHERE uuid=?",p.getUniqueId());
				if(!rs.next())throw new SQLException("no ResultSet");
				int money = rs.getInt(1);
				p.sendMessage("§eCoins §8» §7Vous avez " + money + " coins");
			}else if (args.length == 1) {
				Player p = Bukkit.getPlayer(args[0]);
				if (p == null) sender.sendMessage("§c" + args[0] + " n'est pas connecté ou n'existe pas !");
				else {
					ResultSet rs = Main.sqlConnection.fastSelectUnsafe("SELECT crea_money FROM playerdata.stats WHERE uuid=?",p.getUniqueId());
					if(!rs.next())throw new SQLException("no ResultSet");
					int money = rs.getInt(1);
					p.sendMessage("§eCoins §8» §7" + args[0] + " a " + money + " coins");
				}
			}else sender.sendMessage("§cSyntaxe : /coins [joueur]");
		}catch(SQLException e){
			e.printStackTrace();
			Main.sqlConnection.broadcastError();
			sender.sendMessage(" §cUne erreur interne est survenue ! Préviens un membre du Staff !");
		}
		return false;
	}

}
