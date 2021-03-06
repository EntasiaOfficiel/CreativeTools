package fr.entasia.creativetools.listeners;

import fr.entasia.creativetools.Main;
import fr.entasia.creativetools.utils.ItemSanitizer;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.ExplosionPrimeEvent;
import org.bukkit.event.inventory.InventoryCreativeEvent;

public class ProtectListener implements Listener {

	@EventHandler
	public void onJoin(BlockPlaceEvent e) {
		if(e.getPlayer().getWorld()==Main.buildworld){
			if(!e.getPlayer().hasPermission("build.buildmap"))e.setCancelled(true);
		}
	}

	@EventHandler
	public void onJoin(BlockBreakEvent e) {
		if(e.getPlayer().getWorld()==Main.buildworld){
			if(!e.getPlayer().hasPermission("build.buildmap"))e.setCancelled(true);
		}
	}

	@EventHandler
	public void onQuit(ExplosionPrimeEvent e) {
		if(e.getEntity().getWorld()==Main.buildworld){
			e.setCancelled(true);
		}
	}

	@EventHandler
	public void a(InventoryCreativeEvent e) {
		ItemSanitizer.sanitiseItem(e.getCursor());
	}
}