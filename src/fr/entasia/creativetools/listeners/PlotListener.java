package fr.entasia.creativetools.listeners;

import com.google.common.eventbus.Subscribe;
import com.plotsquared.core.events.PlayerEnterPlotEvent;
import com.plotsquared.core.events.PlayerLeavePlotEvent;
import fr.entasia.creativetools.Main;
import fr.entasia.creativetools.utils.CreaPlayer;
import org.bukkit.entity.Player;

public class PlotListener {


	@Subscribe
	public void onPlotEnter(PlayerEnterPlotEvent e) {
		Player p = (Player) e.getPlotPlayer().getPlatformPlayer();
		CreaPlayer cp = Main.getCreaPlayer(p);
		cp.sb.setPlot(e.getPlot());
	}

	@Subscribe
	public void onPlotQuit(PlayerLeavePlotEvent e) {
		Player p = (Player) e.getPlotPlayer().getPlatformPlayer();
		CreaPlayer cp = Main.getCreaPlayer(p);
		cp.sb.setPlot(null);
	}
}
