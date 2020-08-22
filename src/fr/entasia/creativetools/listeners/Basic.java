package fr.entasia.creativetools.listeners;

import com.plotsquared.bukkit.events.PlayerEnterPlotEvent;
import com.plotsquared.bukkit.events.PlayerLeavePlotEvent;
import fr.entasia.creativetools.Main;
import fr.entasia.creativetools.utils.CreaPlayer;
import fr.entasia.creativetools.utils.SBManager;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.weather.WeatherChangeEvent;

public class Basic implements Listener {

	@EventHandler
	public void onJoin(PlayerJoinEvent e) {
		e.getPlayer().teleport(Main.spawn);
		CreaPlayer cp = new CreaPlayer(e.getPlayer());
		cp.sb = new SBManager(cp);
		Main.playerCache.put(e.getPlayer().getUniqueId(), cp);
	}

	@EventHandler
	public void onQuit(PlayerQuitEvent e) {
		Main.playerCache.remove(e.getPlayer().getUniqueId());
	}

	@EventHandler
	public void onWeather(WeatherChangeEvent e) {
		e.setCancelled(e.toWeatherState()); // en gros si c'est true c'est la pluie // ca marche fait pas chier // bonjour
	}

	@EventHandler
	public void onPlotEnter(PlayerEnterPlotEvent e) {
		CreaPlayer cp = Main.playerCache.get(e.getPlayer().getUniqueId());
		if(cp!=null){
			cp.sb.setPlot(e.getPlot());
		}
	}

	@EventHandler
	public void onPlotQuit(PlayerLeavePlotEvent e) {
		CreaPlayer cp = Main.playerCache.get(e.getPlayer().getUniqueId());
		if(cp!=null){
			cp.sb.setPlot(null);
		}
	}

	@EventHandler
	public void onWorldChange(PlayerChangedWorldEvent e) {
		Player p = e.getPlayer();
		CreaPlayer cp = Main.playerCache.get(p.getUniqueId());
		if(cp!=null){
			if(p.getWorld()==Main.world){
				p.setGameMode(GameMode.CREATIVE);
				cp.sb.softSet();
				cp.sb.register();
			}else{
				cp.sb.clear();
			}
		}
	}
}