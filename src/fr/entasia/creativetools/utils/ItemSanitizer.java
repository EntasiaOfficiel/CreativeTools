package fr.entasia.creativetools.utils;

import fr.entasia.apis.nbt.ItemNBT;
import fr.entasia.apis.nbt.NBTComponent;
import fr.entasia.apis.nbt.NBTManager;
import fr.entasia.apis.utils.ReflectionUtils;
import net.minecraft.server.v1_15_R1.NBTBase;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_15_R1.inventory.CraftMetaBanner;
import org.bukkit.craftbukkit.v1_15_R1.inventory.CraftMetaBook;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.*;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Map;

public class ItemSanitizer {

	public static Field unhandledTags;
	public static Method applyToItem;

	public static Class<?> CraftMetaEnchantedBook;
	public static Field bannerPatterns;

	static {
		try {
			Class<?> CraftMetaItem = ReflectionUtils.getOBCClass("inventory.CraftMetaItem");
			unhandledTags = CraftMetaItem.getDeclaredField("unhandledTags");
			unhandledTags.setAccessible(true);
			applyToItem = CraftMetaItem.getDeclaredMethod("applyToItem", NBTManager.TagCompoundClass);
			applyToItem.setAccessible(true);

			CraftMetaEnchantedBook = ReflectionUtils.getOBCClass("inventory.CraftMetaItem");

			Class<?> CraftMetaBanner = org.bukkit.craftbukkit.v1_15_R1.inventory.CraftMetaBanner.class;
			bannerPatterns = CraftMetaBanner.getDeclaredField("patterns");
			bannerPatterns.setAccessible(true);

		} catch (ReflectiveOperationException e) {
			e.printStackTrace();
		}
	}


	public static void sanitiseItem(ItemStack item){
		try{
			if(item==null)return;
			ItemMeta meta = item.getItemMeta();
			if (meta == null)return;
			Map<String, NBTBase> map = (Map<String, NBTBase>) unhandledTags.get(meta);
			map.clear();

			for(Map.Entry<Enchantment, Integer> e : meta.getEnchants().entrySet()) {
				meta.addEnchant(e.getKey(), e.getValue(), false);
			}
			meta.setAttributeModifiers(null);

			if(!sanitiseMeta(meta)){
				meta = Bukkit.getItemFactory().getItemMeta(Material.STONE);
			}


			NBTComponent nbt = new NBTComponent();
			applyToItem.invoke(meta, nbt.getRawNBT()); // ca applie bien la method child, et pas celle de CraftMetaItem....
			ItemNBT.setNBT(item, nbt);
		}catch(ReflectiveOperationException e){
			if(item==null)return;
			item.setType(Material.AIR);
			item.setItemMeta(null);
		}
	}


	public static boolean sanitiseMeta(ItemMeta meta){
		if (meta instanceof CraftMetaBanner) {
			CraftMetaBanner cmeta = (CraftMetaBanner) meta;
			if (cmeta.numberOfPatterns() > 32) {
				cmeta.setPatterns(new ArrayList<>());
			}
		} else if (meta instanceof CraftMetaBook) {
			CraftMetaBook cmeta = (CraftMetaBook) meta;
			if (cmeta.getPageCount() > 100) {
				cmeta.pages.subList(0, 100);
			}
		} else if (meta instanceof EnchantmentStorageMeta) {
			EnchantmentStorageMeta cmeta = (EnchantmentStorageMeta)meta;
			for(Map.Entry<Enchantment, Integer> e : cmeta.getStoredEnchants().entrySet()) {
				cmeta.addStoredEnchant(e.getKey(), e.getValue(), false);
			}
		} else if (meta instanceof SkullMeta) {
			// bon de base
		} else if (meta instanceof MapMeta) {
			// bon de base
		} else if (meta instanceof PotionMeta) {
			PotionMeta cmeta = (PotionMeta)meta;
			cmeta.clearCustomEffects();
		} else if (meta instanceof FireworkMeta) {
			FireworkMeta cmeta = (FireworkMeta)meta;
			if(cmeta.getPower()>3)cmeta.setPower(1);
			if(cmeta.getEffectsSize()>3)cmeta.clearEffects();
		} else return false;
		return true;
	}

}
