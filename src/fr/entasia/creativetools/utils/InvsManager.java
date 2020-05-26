package fr.entasia.creativetools.utils;

import com.boydti.fawe.FaweCache;
import com.boydti.fawe.util.EditSessionBuilder;
import com.intellectualcrafters.plot.object.Plot;
import com.intellectualcrafters.plot.object.PlotId;
import com.intellectualcrafters.plot.object.RegionWrapper;
import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.Vector;
import com.sk89q.worldedit.blocks.BaseBlock;
import com.sk89q.worldedit.regions.CuboidRegion;
import fr.entasia.apis.menus.MenuClickEvent;
import fr.entasia.apis.menus.MenuCreator;
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
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Set;
import java.util.UUID;

public class InvsManager {


	// ####################
	// ---------- Plot List Menu ----------
	// ####################

	public static MenuCreator plotListMenu = new MenuCreator(null, null) {
		@Override
		public void onMenuClick(MenuClickEvent e) {
			if(e.item.hasItemMeta()&&e.item.getItemMeta().hasDisplayName()) {
				if (e.item.getLore() != null && e.item.getLore().size() != 0) {
					String a = e.item.getLore().get(0).substring(7);
					Plot plot = Main.getPlot(PlotId.fromString(a));
					if (plot == null || !plot.isAdded(e.player.getUniqueId())) e.player.sendMessage("§cUne erreur s'est produite !");
					else plotGestionOpen(e.player, plot);
				}
			}
		}
	};

	public static void plotListOpen(Player p){

		Inventory inv = plotListMenu.createInv(6, "§aPlots :");

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
							com.intellectualcrafters.plot.object.Location truc = plot.getHome();
							e.player.teleport(new Location(Main.world, truc.getX(), truc.getY(), truc.getZ(), truc.getYaw(), truc.getPitch()));
							break;
						case BANNER:
							plotTeamOpen(e.player, plot);
							break;
						case SAPLING:
							choseBiomeOpen(e.player, plot);
							break;
						case SAND:
							changeFloorOpen(e.player, plot);
							break;
						case BOOK_AND_QUILL:
							plotListOpen(e.player);
							break;
					}
				}
			}
		}
	};

	public static void placeSkullAsync(Inventory inv, int slot, ItemStack item){
		new BukkitRunnable() {
			@Override
			public void run() {
				inv.setItem(slot, item);
			}
		}.runTaskAsynchronously(Main.main);
	}

	public static void plotGestionOpen(Player p, Plot plot){

		Inventory inv = plotGestionMenu.createInv(3, "§aGestion du Plot :", plot);

		ItemStack item = new ItemStack(Material.ENDER_PEARL, 1);
		ItemMeta meta = item.getItemMeta();
		meta.setDisplayName("§7Se téléporter à ce plot");
		item.setItemMeta(meta);
		inv.setItem(10, item);


		item = new ItemStack(Material.BANNER, 1, (short)3);
		meta = item.getItemMeta();
		meta.setDisplayName("§7Voir les membres de la team");
		item.setItemMeta(meta);
		inv.setItem(11, item);

		item = new ItemStack(Material.SAPLING);
		meta = item.getItemMeta();
		meta.setDisplayName("§7Changer de biome");
		item.setItemMeta(meta);
		inv.setItem(12, item);

		item = new ItemStack(Material.SAND);
		meta = item.getItemMeta();
		meta.setDisplayName("§7Changer le sol du Plot");
		ArrayList<String> list = new ArrayList<>();
		list.add("§4§lEN BETA :");
		list.add("§cCette opération va totalement remplacer le sol de votre plot ( couche de surface seulement )");
		list.add("§cy compris les blocks que vous aurez posé vous-même !");
		list.add("§cA ne donc pas utiliser sur un plot dont la surface est un pixel-art, par exemple");
		meta.setLore(list);
		item.setItemMeta(meta);
		inv.setItem(13, item);

		item = new ItemStack(Material.BOOK_AND_QUILL, 1);
		meta = item.getItemMeta();
		meta.setDisplayName("§cRetour au menu précédent");
		item.setItemMeta(meta);
		inv.setItem(26, item);

		p.openInventory(inv);
	}


	// ####################
	// ---------- Plot Team Menu ----------
	// ####################

	public static MenuCreator plotTeamMenu = new MenuCreator(null, null) {
		@Override
		public void onMenuClick(MenuClickEvent e) {
			if (e.item.hasItemMeta() && e.item.getItemMeta().hasDisplayName()) {
				Plot plot = (Plot) e.data;
				if (plot == null || !plot.isAdded(e.player.getUniqueId()))
					e.player.sendMessage("§cUne erreur s'est produite !");
				else if (e.item.getType() == Material.BOOK_AND_QUILL) plotGestionOpen(e.player, plot);
			}
		}
	};

	public static void plotTeamOpen(Player p, Plot plot) {

		Set<UUID> owners = plot.getOwners();
		Set<UUID> members = plot.getTrusted();
		Set<UUID> coops = plot.getMembers();
		Inventory inv = plotTeamMenu.createInv( (owners.size()+members.size()+coops.size())/9+1, "§7Membres de la team", plot);
		ItemStack item = new ItemStack(Material.SKULL_ITEM, 1, (short)3);
		SkullMeta meta = (SkullMeta)item.getItemMeta();
		OfflinePlayer lp;
		int i = 0;
		boolean played;
		ArrayList<String> array = new ArrayList<>();
		for (UUID uuid : owners) {
			array.clear();
			array.add("§6Rang : §cOwner");
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
			inv.setItem(i, item);
			if(played){
				meta.setOwningPlayer(lp);
				item.setItemMeta(meta);
				placeSkullAsync(inv, i, item.clone());
			}
			i++;
		}

		for (UUID uuid : members) {
			array.clear();
			array.add("§6Rang : §aMembre");
			lp = Bukkit.getOfflinePlayer(uuid);
			played = lp.hasPlayedBefore();
			if (played) {
				meta.setDisplayName("§a" + lp.getName());
				meta.setOwningPlayer(null);
			} else{
				meta.setDisplayName("§3Joueur inconnu !");
				array.add("§9Ce joueur n'a pas été trouvé dans nos bases de données ! Cela peut être réglé à sa reconnexion");
			}

			meta.setLore(array);
			item.setItemMeta(meta);
			inv.setItem(i, item);
			if(played){
				meta.setOwningPlayer(lp);
				item.setItemMeta(meta);
				placeSkullAsync(inv, i, item.clone());
			}
			i++;
		}

		for (UUID uuid : coops) {
			array.clear();
			array.add("§6Rang : §eCOOP");
			lp = Bukkit.getOfflinePlayer(uuid);
			played = lp.hasPlayedBefore();
			if (played) {
				meta.setDisplayName("§e" + lp.getName());
				meta.setOwningPlayer(null);
			} else{
				meta.setDisplayName("§3Joueur inconnu !");
				array.add("§9Ce joueur n'a pas été trouvé dans nos bases de données ! Cela peut être réglé à sa reconnexion");
			}

			meta.setLore(array);
			item.setItemMeta(meta);
			inv.setItem(i, item);
			if(played){
				meta.setOwningPlayer(lp);
				item.setItemMeta(meta);
				placeSkullAsync(inv, i, item.clone());
			}
			i++;
		}
		item = new ItemStack(Material.BOOK_AND_QUILL, 1);
		ItemMeta rmeta = item.getItemMeta();
		rmeta.setDisplayName("§cRetour au menu précédent");
		item.setItemMeta(rmeta);
		inv.setItem(inv.getSize()-1, item);
		p.openInventory(inv);
	}


	// ####################
	// ---------- Plot Biome Menu ----------
	// ####################

	public static MenuCreator plotBiomeMenu = new MenuCreator(null, null) {
		@Override
		public void onMenuClick(MenuClickEvent e) {
			if(e.item.hasItemMeta()&&e.item.getItemMeta().hasDisplayName()){
				Plot plot = (Plot)e.data;
				if(plot==null||!plot.isAdded(e.player.getUniqueId()))e.player.sendMessage("§cUne erreur s'est produite !");
				else {
					if(e.item.getType() == Material.BOOK_AND_QUILL) plotGestionOpen(e.player, plot);
					else {
						String type;
						switch (e.item.getType()) {
							case GRASS:
								type = "PLAINS";
								break;
							case DIRT:
								type = "TAIGA";
								break;
							case LOG_2:
								type = "SAVANNA";
								break;
							case VINE:
								type = "JUNGLE";
								break;
							case ICE:
								type = "ICE_FLATS";
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
		}
	};

	public static void choseBiomeOpen(Player p, Plot plot) {

		Inventory inv = plotBiomeMenu.createInv(3, "§7Choisir un biome", plot);

		ItemStack item = new ItemStack(Material.GRASS);
		ItemMeta meta = item.getItemMeta();
		meta.setDisplayName("§aPlaine");
		item.setItemMeta(meta);
		inv.setItem(10, item);

		item = new ItemStack(Material.DIRT, 1, (short)2);
		meta = item.getItemMeta();
		meta.setDisplayName("§6Taiga");
		item.setItemMeta(meta);
		inv.setItem(11, item);

		item = new ItemStack(Material.LOG_2);
		meta = item.getItemMeta();
		meta.setDisplayName("§aPlaine");
		item.setItemMeta(meta);
		inv.setItem(12, item);

		item = new ItemStack(Material.VINE);
		meta = item.getItemMeta();
		meta.setDisplayName("§aPlaine");
		item.setItemMeta(meta);
		inv.setItem(13, item);

		item = new ItemStack(Material.ICE);
		meta = item.getItemMeta();
		meta.setDisplayName("§aPlaine");
		item.setItemMeta(meta);
		inv.setItem(14, item);

		item = new ItemStack(Material.BOOK_AND_QUILL, 1);
		meta = item.getItemMeta();
		meta.setDisplayName("§cRetour au menu précédent");
		item.setItemMeta(meta);
		inv.setItem(26, item);
		p.openInventory(inv);
	}


	// ####################
	// ---------- Plot Change Floor Menu ----------
	// ####################

	public static MenuCreator plotFloorMenu = new MenuCreator(null, new int[]{11}) {

		@Override
		public void onMenuClick(MenuClickEvent e) {
			Plot plot = (Plot)e.data;
			System.out.println(plot==null);
			if(plot==null||!plot.isAdded(e.player.getUniqueId()))e.player.sendMessage("§cUne erreur s'est produite !");
			else {
				if(e.item.getType() == Material.BOOK_AND_QUILL) plotGestionOpen(e.player, plot);
				if(e.item.getType() == Material.WOOL) {
					ItemStack selected = e.inv.getItem(11);
					if(selected==null)e.player.sendMessage("§cMet un block dans le slot vide du menu !");
					else{
						EditSession editSession = new EditSessionBuilder(Main.world.getName()).fastmode(true).allowedRegionsEverywhere().build();
						BaseBlock bb = FaweCache.getBlock(
								selected.getTypeId(),
								selected.getDurability());
						for(RegionWrapper rw : plot.getRegions()){
							System.out.println(rw.minX+":"+rw.minZ+" --> "+rw.maxX+":"+rw.maxZ);
							editSession.setBlocks(new CuboidRegion(new Vector(rw.minX, 65, rw.minZ), new Vector(rw.maxX, 65, rw.maxZ)),
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
//			System.out.println("bb");
//			System.out.println(e.item.getType());
//			System.out.println(e.item.getType().isSolid());
//			System.out.println(e.item.getType().isBlock());
			return !e.item.getType().isBlock();
		}
	};

	public static void changeFloorOpen(Player p, Plot plot) {

		Inventory inv = plotFloorMenu.createInv(5, "§7Changer le sol du plot  §c(Béta)", plot);

		ItemStack item = new ItemStack(Material.STAINED_GLASS_PANE, 1, (short) 3);
		for(int i : new int[]{1,2,3,10,12,19,20,21}) inv.setItem(i, item);

		item = new ItemStack(Material.WOOL, 1, (short)5);
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


		item = new ItemStack(Material.BOOK_AND_QUILL, 1);
		meta = item.getItemMeta();
		meta.setDisplayName("§cRetour au menu précédent");
		item.setItemMeta(meta);
		inv.setItem(44, item);
		p.openInventory(inv);
	}
}
