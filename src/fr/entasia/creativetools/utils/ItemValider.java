package fr.entasia.creativetools.utils;

import fr.entasia.apis.nbt.ItemNBT;
import fr.entasia.apis.nbt.NBTComponent;
import fr.entasia.apis.utils.ReflectionUtils;
import net.minecraft.server.v1_14_R1.NBTBase;
import net.minecraft.server.v1_14_R1.NBTList;
import net.minecraft.server.v1_14_R1.NBTTagCompound;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Map;

public class ItemValider {

	public static Field unhandledTags;

	static{
		try{
			Class<?> CraftMetaItem = ReflectionUtils.getOBCClass("inventory.CraftMetaItem");
			unhandledTags = CraftMetaItem.getDeclaredField("unhandledTags");
			unhandledTags.setAccessible(true);
		}catch(ReflectiveOperationException e){
			e.printStackTrace();
		}
	}


	public static boolean validateItem(ItemStack item){
		try{
			ItemMeta meta = item.getItemMeta();
			Map<String, NBTBase> map = (Map<String, NBTBase>)unhandledTags.get(meta);
			for(Map.Entry<String, NBTBase> e : map.entrySet()){
				System.out.println(e.getKey());
			}
			map.clear();

			Method m = meta.getClass().getDeclaredMethod("applyToItem", NBTTagCompound.class);
			m.setAccessible(true);
			NBTComponent nbt = new NBTComponent();
			m.invoke(meta, nbt.getRawNBT());

//			recur((NBTTagCompound)nbt.getRawNBT(), 1); // security

			ItemNBT.setNBT(item, nbt);

			return true;
		} catch(ReflectiveOperationException e){
			e.printStackTrace();
			return false;
		}
	}

	private static final int MAX_DEPTH = 4;

	private static void recur(NBTTagCompound nbt, int lvl){
		if(lvl==MAX_DEPTH)nbt.map.clear();
		else{
			for(NBTBase l : nbt.map.values()){
				apply(l, lvl+1);
			}
		}
	}

	private static void recur(NBTList<?> nbt, int lvl){
		if(lvl==MAX_DEPTH)nbt.clear();
		else{
			for(NBTBase l : nbt){
				apply(l, lvl+1);
			}
		}
	}

	private static void apply(NBTBase l, int lvl) {
		if (l instanceof NBTList) {
			recur((NBTList<?>) l, lvl);
		} else if (l instanceof NBTTagCompound) {
			recur((NBTTagCompound) l, lvl);
		}
	}

}
