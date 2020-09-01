package fr.entasia.creativetools.utils;

import com.boydti.fawe.FaweAPI;
import com.boydti.fawe.util.EditSessionBuilder;
import com.plotsquared.core.plot.Plot;
import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.math.BlockVector3Imp;
import com.sk89q.worldedit.regions.CuboidRegion;
import com.sk89q.worldedit.regions.Region;
import com.sk89q.worldedit.world.biome.BiomeType;
import com.sk89q.worldedit.world.biome.BiomeTypes;
import com.sk89q.worldedit.world.block.BaseBlock;
import com.sk89q.worldedit.world.block.BlockType;
import com.sk89q.worldedit.world.block.BlockTypes;
import fr.entasia.apis.menus.MenuClickEvent;
import fr.entasia.apis.menus.MenuCreator;
import fr.entasia.apis.other.ItemBuilder;
import fr.entasia.apis.utils.ItemUtils;
import fr.entasia.creativetools.Main;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.*;

public class InvsManager {


	// ####################
	// ---------- Plot List Menu ----------
	// ####################

	public static MenuCreator plotListMenu = new MenuCreator(null, null) {
		@Override
		public void onMenuClick(MenuClickEvent e) {
			HashMap<Integer, Plot> link = (HashMap<Integer, Plot>) e.data;
			if (link == null) return;
			Plot plot = link.get(e.slot);
			if (plot == null) e.player.sendMessage("§cUne erreur s'est produite !");
			else if (plot.isAdded(e.player.getUniqueId())) plotGestionOpen(e.player, plot);
			else e.player.sendMessage("§cTu n'es plus membre de ce plot !");
		}
	};

	public static void plotListOpen(Player p){

		HashMap<Integer, Plot> link = new HashMap<>();
		Inventory inv = plotListMenu.createInv(6, "§aPlots :", link);

		ArrayList<Plot> member = new ArrayList<>();
		ArrayList<Plot> owner = new ArrayList<>();
		UUID uuid = p.getUniqueId();
		for(Plot pl : Main.plotApi.getAllPlots()){
			if(pl.hasOwner()&&pl.isBasePlot()){
				if(pl.isOwner(uuid))owner.add(pl);
				else if(pl.isAdded(uuid))member.add(pl);
			}
		}



		int slot = 10;
		int n=1;
		ItemStack item = new ItemStack(Material.DIAMOND_PICKAXE);
		ItemMeta meta = item.getItemMeta();
		for(Plot plot : owner){
			meta.setDisplayName("§7Plot n°"+n);
			meta.setLore(Collections.singletonList("§eID : "+plot.getId().toString()));
			item.setItemMeta(meta);
			inv.setItem(slot, item);
			link.put(slot, plot);

			if(slot%7==0)slot+=3;
			else slot++;
			n++;
		}
		slot = ((slot/9)+1)*9+1;
		item = new ItemStack(Material.MAGMA_CREAM);
		meta = item.getItemMeta();
		OfflinePlayer t;
		String ts;
		for(Plot plot : member) {
			t = Bukkit.getOfflinePlayer(plot.getOwners().iterator().next());
			if(t==null||t.getName()==null)ts="§8Joueur Inconnu";
			else ts = t.getName();
			meta.setDisplayName("§7Plot de "+ts);
			meta.setLore(Collections.singletonList("§eID : "+plot.getId().toString()));
			item.setItemMeta(meta);
			inv.setItem(slot, item);
			link.put(slot, plot);

			if (slot % 7 == 0) slot += 3;
			else slot++;
			n++;
		}

		p.openInventory(inv);
	}


	// ####################
	// ---------- Plot Gestion Menu ----------
	// ####################


	public static MenuCreator plotGestionMenu = new MenuCreator(null, null) {
		@Override
		public void onMenuClick(MenuClickEvent e) {


			if(e.item.hasItemMeta()&&e.item.getItemMeta().hasDisplayName()){
				Plot plot = (Plot)e.data;
				if(plot==null||!plot.isAdded(e.player.getUniqueId()))e.player.sendMessage("§cUne erreur s'est produite !");
				else{
					switch(e.item.getType()){
						case ENDER_PEARL:
							plot.getHome(faweLoc ->
									e.player.teleport(new Location(Main.world, faweLoc.getX(), faweLoc.getY(), faweLoc.getZ(), faweLoc.getYaw(), faweLoc.getPitch())));
							break;
						case LIGHT_BLUE_BANNER:
							plotTeamOpen(e.player, plot);
							break;
						case OAK_SAPLING:
							choseBiomeOpen(e.player, plot);
							break;
						case SAND:
							changeFloorOpen(e.player, plot);
							break;
						case WRITABLE_BOOK:
							plotListOpen(e.player);
							break;
					}
				}
			}
		}
	};

	public static void plotGestionOpen(Player p, Plot plot){

		Inventory inv = plotGestionMenu.createInv(3, "§aGestion du Plot :", plot);

		ItemStack item = new ItemBuilder(Material.ENDER_PEARL).name("§7Se téléporter à ce plot").build();
		inv.setItem(10, item);


		item = new ItemBuilder(Material.LIGHT_BLUE_BANNER).name("§7Voir les membres de la team").build();
		inv.setItem(11, item);

		item = new ItemBuilder(Material.OAK_SAPLING).name("§7Changer de biome").build();
		inv.setItem(12, item);

		item = new ItemBuilder(Material.SAND).name("§7Changer le sol du Plot").lore(
			"§4§lEN BETA :",
			"§cCette opération va totalement remplacer le sol de votre plot ( couche de surface seulement )",
			"§cy compris les blocks que vous aurez posé vous-même !",
			"§cA ne donc pas utiliser sur un plot dont la surface est un pixel-art, par exemple"
		).build();
		inv.setItem(13, item);

		item = new ItemBuilder(Material.WRITABLE_BOOK, 1).name("§cRetour au menu précédent").build();
		inv.setItem(26, item);

		p.openInventory(inv);
	}


	// ####################
	// ---------- Plot Team Menu ----------
	// ####################

	public static MenuCreator plotTeamMenu = new MenuCreator() {
		@Override
		public void onMenuClick(MenuClickEvent e) {
			Plot plot = (Plot) e.data;
			if (plot == null){
				e.player.sendMessage("§cUne erreur s'est produite !");
				return;
			}
			if (e.item.getType() == Material.WRITABLE_BOOK) plotGestionOpen(e.player, plot);
			else{
				if(plot.isAdded(e.player.getUniqueId())){
					e.player.sendMessage("§cPas encore implémenté !");
				}else{
					e.player.sendMessage("§cTu n'es plus membre de cette île !");
				}
			}
		}
	};

	public static void plotTeamOpen(Player p, Plot plot) {

		HashMap<String, Set<UUID>> map = new HashMap<>();
		map.put("§cChef", plot.getOwners());
		map.put("§aMembre", plot.getTrusted());
		map.put("§eCOOP", plot.getMembers());
		Inventory inv = plotTeamMenu.createInv( map.size()/9+1, "§7Membres de la team", plot);
		ItemStack item = new ItemStack(Material.PLAYER_HEAD);
		SkullMeta meta = (SkullMeta)item.getItemMeta();
		OfflinePlayer lp;
		int i = 0;
		boolean played;
		ArrayList<String> array = new ArrayList<>();
		for(Map.Entry<String, Set<UUID>> e : map.entrySet()){
			for (UUID uuid : e.getValue()) {
				array.clear();
				array.add("§6Rang : "+e.getKey());
				lp = Bukkit.getOfflinePlayer(uuid);
				played = lp.hasPlayedBefore();
				if (played) {
					meta.setDisplayName("§c" + lp.getName());
					meta.setOwningPlayer(null);
				} else{
					meta.setDisplayName("§3Joueur inconnu !");
					array.add("§9Ce joueur n'a pas été trouvé dans nos bases de données ! Cela peut être réglé à sa reconnexion");
				}

				meta.setLore(array);
				item.setItemMeta(meta);
				ItemUtils.placeSkullAsync(inv, i, item, lp, Main.main);
				i++;
			}
		}

		item = new ItemBuilder(Material.WRITABLE_BOOK).name("§cRetour au menu précédent").build();
		inv.setItem(inv.getSize()-1, item);
		p.openInventory(inv);
	}


	// ####################
	// ---------- Plot Biome Menu ----------
	// ####################

	public static MenuCreator plotBiomeMenu = new MenuCreator() {
		@Override
		public void onMenuClick(MenuClickEvent e) {
			Plot plot = (Plot) e.data;
			if (plot == null || !plot.isAdded(e.player.getUniqueId()))
				e.player.sendMessage("§cUne erreur s'est produite !");
			else {
				if (e.item.getType() == Material.WRITABLE_BOOK) plotGestionOpen(e.player, plot);
				else {
					BiomeType type;
					switch (e.item.getType()) {
						case GRASS:
							type = BiomeTypes.PLAINS;
							break;
						case DIRT:
							type = BiomeTypes.TAIGA;
							break;
						case ACACIA_LOG:
							type = BiomeTypes.SAVANNA;
							break;
						case BROWN_MUSHROOM:
							type = BiomeTypes.SWAMP;
							break;
						case VINE:
							type = BiomeTypes.JUNGLE;
							break;
						case ICE:
							type = BiomeTypes.ICE_SPIKES;
							break;
						default:
							e.player.sendMessage("§cCe biome n'a pas été implémenté dans le Créatif ! Contacte un membre du Staff");
							return;
					}
					e.player.closeInventory();
					e.player.teleport(Main.spawn);
					e.player.sendMessage("§6Nous t'avons téléporté au spawn afin d'actualiser le plot, tu peux y retourner dès maintenant !");
					plot.setBiome(type, null);
				}

			}
		}
	};

	public static void choseBiomeOpen(Player p, Plot plot) {

		Inventory inv = plotBiomeMenu.createInv(3, "§7Choisir un biome", plot);

		inv.setItem(10, new ItemBuilder(Material.GRASS).name("§aPlaine").build());
		inv.setItem(11, new ItemBuilder(Material.DIRT).damage(2).name("§6Taiga").build());
		inv.setItem(12, new ItemBuilder(Material.ACACIA_LOG).name("§cSavanne").build());
		inv.setItem(13, new ItemBuilder(Material.BROWN_MUSHROOM).name("§2Marais").build());
		inv.setItem(14, new ItemBuilder(Material.VINE).name("§aJungle").build());
		inv.setItem(15, new ItemBuilder(Material.ICE).name("§2Neige").build());

		inv.setItem(26, new ItemBuilder(Material.WRITABLE_BOOK).name("§cRetour au menu précédent").build());

		p.openInventory(inv);
	}


	// ####################
	// ---------- Plot Change Floor Menu ----------
	// ####################

	public static MenuCreator plotFloorMenu = new MenuCreator(null, new int[]{11}) {

		@Override
		public void onMenuClick(MenuClickEvent e) {
			Plot plot = (Plot)e.data;
			if(plot==null||!plot.isAdded(e.player.getUniqueId()))e.player.sendMessage("§cUne erreur s'est produite !");
			else {
				if(e.item.getType() == Material.WRITABLE_BOOK) plotGestionOpen(e.player, plot);
				if(e.item.getType() == Material.LIME_WOOL) {
					ItemStack selected = e.inv.getItem(11);
					if(selected==null)e.player.sendMessage("§cMet un block dans le slot vide du menu !");
					else{
						EditSession editSession = new EditSessionBuilder(FaweAPI.getWorld(Main.world.getName())).fastmode(true).allowedRegionsEverywhere().build();
						BlockType bt = BlockTypes.get(selected.getType().toString());
						if(bt==null){
							e.player.sendMessage("§cUne erreur s'est produite lors de l'identification du block ! Contacte un membre du Staff");
							return;
						}
						BaseBlock bb = new BaseBlock(bt.getDefaultState());
						for(CuboidRegion rg : plot.getRegions()){
							editSession.setBlocks(
									(Region) new CuboidRegion(BlockVector3Imp.at(rg.getMinimumX(),
											65, rg.getMinimumZ()),
											BlockVector3Imp.at(rg.getMaximumX(), 65, rg.getMaximumZ())),
									bb);
						}
						e.player.closeInventory();
						e.player.teleport(Main.spawn);
						e.player.sendMessage("§6Nous t'avons téléporté au spawn afin d'actualiser le plot, tu peux y retourner dès maintenant !");
						editSession.flushQueue();
					}
				}
			}
		}

		@Override
		public boolean onFreeSlotClick(MenuClickEvent e) {
			return !e.item.getType().isBlock();
		}
	};

	public static void changeFloorOpen(Player p, Plot plot) {

		Inventory inv = plotFloorMenu.createInv(5, "§7Changer le sol du plot  §c(Béta)", plot);

		ItemStack item = new ItemStack(Material.GLASS_PANE);
		for(int i : new int[]{1,2,3,10,12,19,20,21}) inv.setItem(i, item);

		item = new ItemStack(Material.LIME_WOOL);
		ItemMeta meta = item.getItemMeta();
		meta.setDisplayName("§aValider");
		item.setItemMeta(meta);
		inv.setItem(14, item);

		item = new ItemStack(Material.BOOK);
		meta = item.getItemMeta();
		meta.setDisplayName("§3Informations :");
		meta.setLore(Collections.singletonList("§bMettez dans le slot entouré le block que vous voulez définir en sol de votre plot !"));
		item.setItemMeta(meta);
		inv.setItem(16, item);


		item = new ItemStack(Material.WRITABLE_BOOK);
		meta = item.getItemMeta();
		meta.setDisplayName("§cRetour au menu précédent");
		item.setItemMeta(meta);
		inv.setItem(44, item);
		p.openInventory(inv);
	}
}
