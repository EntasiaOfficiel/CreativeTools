package fr.entasia.creativetools.listeners;

import com.plotsquared.core.events.PlayerEnterPlotEvent;
import com.plotsquared.core.events.PlayerLeavePlotEvent;
import org.bukkit.event.EventHandler;

public class PlotListener {


	@EventHandler
	public void onPlotEnter(PlayerEnterPlotEvent e) {
		System.out.println(e.getPlotPlayer().getPlatformPlayer());
//		System.out.println("plot enter event for "+e.getPlotPlayer().getPlatformPlayer().getName());
//		CreaPlayer cp = Main.playerCache.get(e.getPlayer().getUniqueId());
//		if(cp!=null){
//			cp.sb.setPlot(e.getPlot());
//		}
	}

	@EventHandler
	public void onPlotQuit(PlayerLeavePlotEvent e) {
//		System.out.println("plot leave event for "+e.getPlayer().getName());
//		CreaPlayer cp = Main.playerCache.get(e.getPlayer().getUniqueId());
//		if(cp!=null){
//			cp.sb.setPlot(null);
//		}
	}
}
