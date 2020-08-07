package fr.entasia.creativetools;

import com.plotsquared.core.api.PlotAPI;
import com.plotsquared.core.plot.Plot;
import com.plotsquared.core.plot.PlotId;
import fr.entasia.apis.sql.SQLConnection;
import fr.entasia.creativetools.command.*;
import fr.entasia.creativetools.listeners.BasicListener;
import fr.entasia.creativetools.listeners.PlotListener;
import fr.entasia.creativetools.listeners.ProtectListener;
import fr.entasia.creativetools.utils.CreaPlayer;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.UUID;


public class Main extends JavaPlugin {

	public static Main main;
	public static SQLConnection sqlConnection;
	public static PlotAPI plotApi;
	public static HashMap<UUID, CreaPlayer> playerCache = new HashMap<>();

	public static World world;
	public static World buildworld;
	public static Location spawn;

	@Override
	public void onEnable() {
		try {
			main = this;
			plotApi = new PlotAPI();
			plotApi.registerListener(new PlotListener());

			getLogger().info("Plugin CreativeTools activé");
			saveDefaultConfig();
			loadConfig();

			if(getConfig().getBoolean("dev", false)) sqlConnection = new SQLConnection("root");
			else sqlConnection = new SQLConnection("creatif");

			getCommand("creatools").setExecutor(new CreaToolsCmd());
			getCommand("money").setExecutor(new CoinsCmd());
			getCommand("eco").setExecutor(new EcoCmd());
			getCommand("spawn").setExecutor(new SpawnCmd());
			getCommand("cosmetiques").setExecutor(new CosmCmd());
			getCommand("menu").setExecutor(new MenuCmd());

			getServer().getPluginManager().registerEvents(new BasicListener(), this);
			getServer().getPluginManager().registerEvents(new ProtectListener(), this);
		} catch (Exception e) {
			e.printStackTrace();
			getLogger().severe("Erreur lors du chargement du plugin ! ARRET DU SERVEUR");
			getServer().shutdown();
		}
	}

	@Override
	public void onDisable() {
		getLogger().severe("Plugin CreativeTools désactivé");
	}

	public boolean loadConfig() throws Exception {
		main.reloadConfig();
		world = Bukkit.getServer().getWorlds().get(0);
		buildworld = Bukkit.getServer().getWorld(getConfig().getString("buildworld"));
		spawn = new Location(world, parseLoc("x") + 0.5, parseLoc("y") + 0.2, parseLoc("z") + 0.5);
		return true;
	}

	private static double parseLoc(String a) {
		return main.getConfig().getInt("spawn." + a);
	}

	public static Plot getPlot(PlotId plid){
		for(Plot p : plotApi.getAllPlots()){
			if(p.getId().getX()==plid.getX()&&p.getId().getY()==plid.getY())return p;
		}
		return null;
	}

	public static void tpSpawn(Player p){
		p.teleport(spawn);
		p.getInventory().clear();

	}
}
