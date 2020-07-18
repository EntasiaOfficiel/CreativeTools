package fr.entasia.creativetools;

import com.intellectualcrafters.plot.api.PlotAPI;
import com.intellectualcrafters.plot.object.Plot;
import com.intellectualcrafters.plot.object.PlotId;
import fr.entasia.apis.sql.SQLConnection;
import fr.entasia.creativetools.command.*;
import fr.entasia.creativetools.listeners.Basic;
import fr.entasia.creativetools.listeners.Protection;
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

			getServer().getPluginManager().registerEvents(new Basic(), this);
			getServer().getPluginManager().registerEvents(new Protection(), this);
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
			if(p.getId().x==plid.x&&p.getId().y==plid.y)return p;
		}
		return null;
	}

	public static void tpSpawn(Player p){
		p.teleport(spawn);
		p.getInventory().clear();

	}
}
