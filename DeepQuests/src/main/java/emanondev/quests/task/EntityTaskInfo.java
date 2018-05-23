package emanondev.quests.task;

import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang.exception.ExceptionUtils;
import org.bukkit.Material;
import org.bukkit.configuration.MemorySection;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.CreatureSpawnEvent.SpawnReason;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import emanondev.quests.Quests;
import emanondev.quests.SpawnReasonTracker;
import emanondev.quests.gui.CustomGuiHolder;
import emanondev.quests.gui.CustomGuiItem;
import emanondev.quests.gui.CustomMultiPageGuiHolder;
import emanondev.quests.gui.EditorGui;
import emanondev.quests.gui.EditorGuiItemFactory;
import emanondev.quests.utils.MemoryUtils;
import emanondev.quests.utils.StringUtils;

public class EntityTaskInfo {
	public final static String PATH_ENTITY = "entity";
	public final static String PATH_ENTITY_AS_WHITELIST = "entity-is-whitelist";
	public final static String PATH_ENTITY_NAME = "entity-name";
	public final static String PATH_IGNORE_NPC = "ignore-npc";
	public final static String PATH_ENTITY_SPAWNREASON = "spawn-reason.list";
	public final static String PATH_ENTITY_SPAWNREASON_AS_WHITELIST = "spawn-reason.is-whitelist";
	private final EnumSet<EntityType> entity = EnumSet.noneOf(EntityType.class);
	private final EnumSet<SpawnReason> spawnReasons = EnumSet.noneOf(SpawnReason.class);
	private boolean spawnReasonsWhitelist;
	private boolean entityWhitelist;
	private boolean ignoreNPC;
	private String entityName;
	private final Task parent;
	private final MemorySection section;
	public EntityTaskInfo(MemorySection m,Task parent) {
		this.parent = parent;
		this.section = m;
		List<String> list = MemoryUtils.getStringList(m, PATH_ENTITY);
		if (list!=null)
			for (String value : list) {
				try {
					entity.add(EntityType.valueOf(value.toUpperCase()));
				} catch (Exception e) {
					Quests.getLogger("errors").log("error on Path "+m.getCurrentPath()+"."+m.getName()
						+" "+e.getMessage());
					Quests.getLogger("errors").log(ExceptionUtils.getStackTrace(e));
				}
			}
		if (entity.isEmpty())
			entityWhitelist = false;
		else
			entityWhitelist = m.getBoolean(PATH_ENTITY_AS_WHITELIST,true);
		
		entityName = m.getString(PATH_ENTITY_NAME,null);
		
		
		list = MemoryUtils.getStringList(m, PATH_ENTITY_SPAWNREASON);
		if (list!=null)
			for (String value : list) {
				try {
					spawnReasons.add(SpawnReason.valueOf(value.toUpperCase()));
				} catch (Exception e) {
					Quests.getLogger("errors").log("error on Path "+m.getCurrentPath()+"."+m.getName()
						+" "+e.getMessage());
					Quests.getLogger("errors").log(ExceptionUtils.getStackTrace(e));
				}
			}
		if (spawnReasons.isEmpty())
			spawnReasonsWhitelist = false;
		else
			spawnReasonsWhitelist = m.getBoolean(PATH_ENTITY_SPAWNREASON_AS_WHITELIST,false);
		
		
		ignoreNPC = m.getBoolean(PATH_IGNORE_NPC,false);
	}
	public boolean isEntityTypeListWhitelist() {
		return entityWhitelist;
	}
	public boolean setEntityTypeListWhitelist(boolean value) {
		if (value==entityWhitelist)
			return false;
		entityWhitelist = value;
		section.set(PATH_ENTITY_AS_WHITELIST,entityWhitelist);
		parent.setDirty(true);
		return true;
	}
	public Set<EntityType> getListedEntityType(){
		return Collections.unmodifiableSet(entity);
	}
	public boolean addEntityTypeToList(EntityType type) {
		if (entity.contains(type))
			return false;
		entity.add(type);
		ArrayList<String> list = new ArrayList<String>();
		for (EntityType entityType : entity) {
			list.add(entityType.toString());
		}
		section.set(PATH_ENTITY,list);
		parent.setDirty(true);
		return true;
	}
	
	public boolean removeEntityTypeFromList(EntityType type) {
		if (!entity.contains(type))
			return false;
		entity.remove(type);
		ArrayList<String> list = new ArrayList<String>();
		for (EntityType entityType : entity) {
			list.add(entityType.toString());
		}
		section.set(PATH_ENTITY,list);
		parent.setDirty(true);
		return true;
	}
	
	private boolean checkEntityType(Entity e) {
		if (entityWhitelist) {
			if (!entity.contains(e.getType()))
				return false;
		}
		else {
			if (entity.contains(e.getType()))
				return false;
		}
		return true;
	}
	private boolean checkSpawnReason(Entity e) {
		SpawnReason reason = SpawnReasonTracker.getSpawnReason(e);
		if (spawnReasonsWhitelist) {
			if (!spawnReasons.contains(reason))
				return false;
		}
		else {
			if (spawnReasons.contains(reason))
				return false;
		}
		return true;
	}
	
	private boolean checkEntityName(Entity e) {
		if (entityName==null)
			return true;
		
		return entityName.equals(e.getName());
	}
	private boolean checkNPC(Entity e) {
		if (e.hasMetadata("NPC")&&ignoreNPC)
			return false;
		return true;
	}

	public boolean isValidEntity(Entity e) {
		return checkEntityType(e)&&checkSpawnReason(e)&&checkEntityName(e)&&checkNPC(e);
	}
	@SuppressWarnings("rawtypes")
	public EditorGuiItemFactory getEntityTypeEditorButton(){
		return new EditEntityTypeFactory();
	}
	
	private class EditEntityTypeFactory<T extends AbstractTask> implements EditorGuiItemFactory {
		private class EditEntityTypeButton extends CustomGuiItem {
			private ItemStack item = new ItemStack(Material.SKULL_ITEM);
			public EditEntityTypeButton(CustomGuiHolder parent) {
				super(parent);
				update();
			}
			@Override
			public ItemStack getItem() {
				return item;
			}
			public void update() {
				ItemMeta meta = item.getItemMeta();
				meta.setDisplayName(StringUtils.fixColorsAndHolders("&6&lEntity Type editor"));
				ArrayList<String> lore = new ArrayList<String>();
				lore.add("&6Click to edit");
				if (getListedEntityType().isEmpty())
					lore.add("&aNo entity restrictions are set");
				else {
					if (!isEntityTypeListWhitelist()) {
						lore.add("&6All listed entity are &cdisabled");
						for (EntityType type : getListedEntityType())
							lore.add(" &6- &c"+type.toString());
					}
					else {
						lore.add("&6All listed entity are &aenabled");
						for (EntityType type : getListedEntityType())
							lore.add(" &6- &a"+type.toString());
					}
				}
				meta.setLore(StringUtils.fixColorsAndHolders(lore));
				item.setItemMeta(meta);
			}
			@Override
			public void onClick(Player clicker, ClickType click) {
				clicker.openInventory(new EntityTypeEditorGui(clicker,(EditorGui) getParent()).getInventory());
			}
		}
		@Override
		public EditEntityTypeButton getCustomGuiItem(CustomGuiHolder parent) {
			return new EditEntityTypeButton(parent);
		}
	}
	
	private final static EnumSet<EntityType> ALLOWED_ENTITY_TYPES = loadAllowedTypes();
	private static EnumSet<EntityType> loadAllowedTypes(){
		EnumSet<EntityType> list = EnumSet.noneOf(EntityType.class);
		try {
			list.add(EntityType.ZOMBIE);
			list.add(EntityType.WOLF);
			list.add(EntityType.WITCH);
			list.add(EntityType.WITHER);
			list.add(EntityType.VILLAGER);
			list.add(EntityType.SQUID);
			list.add(EntityType.SPIDER);
			list.add(EntityType.SNOWMAN);
			list.add(EntityType.SLIME);
			list.add(EntityType.SKELETON);
			list.add(EntityType.SILVERFISH);
			list.add(EntityType.SHEEP);
			list.add(EntityType.RABBIT);
			list.add(EntityType.PLAYER);
			list.add(EntityType.BAT);
			list.add(EntityType.PIG);
			list.add(EntityType.PIG_ZOMBIE);
			list.add(EntityType.OCELOT);
			list.add(EntityType.MUSHROOM_COW);
			list.add(EntityType.COW);
			list.add(EntityType.MAGMA_CUBE);
			list.add(EntityType.IRON_GOLEM);
			list.add(EntityType.HORSE);
			list.add(EntityType.GUARDIAN);
			list.add(EntityType.GIANT);
			list.add(EntityType.GHAST);
			list.add(EntityType.ENDERMITE);
			list.add(EntityType.ENDERMAN);
			list.add(EntityType.ENDER_DRAGON);
			list.add(EntityType.WITHER_SKELETON);
			list.add(EntityType.CREEPER);
			list.add(EntityType.CAVE_SPIDER);
			list.add(EntityType.CHICKEN);
			list.add(EntityType.BLAZE);
			//1.8

			list.add(EntityType.DONKEY);
			list.add(EntityType.ELDER_GUARDIAN);
			list.add(EntityType.MULE);
			list.add(EntityType.SKELETON_HORSE);
			list.add(EntityType.ZOMBIE_HORSE);
			list.add(EntityType.ZOMBIE_VILLAGER);
			//1.9
			list.add(EntityType.SHULKER);
			//1.10
			list.add(EntityType.POLAR_BEAR);
			list.add(EntityType.HUSK);
			list.add(EntityType.STRAY);
			//1.11
			list.add(EntityType.LLAMA);
			list.add(EntityType.EVOKER);
			list.add(EntityType.VEX);
			list.add(EntityType.VINDICATOR);
			//1.12
			list.add(EntityType.PARROT);
			list.add(EntityType.ILLUSIONER);
			//1.13
			/*
			list.add(TURTLE);
			list.add(PHANTOM);
			list.add(COD);
			list.add(SALMON);
			list.add(PUFFER_FISH);
			list.add(TROPICAL_FISH);
			list.add(DROWNED);
			list.add(DOLPHIN);
			*/
		} catch (Exception e) {}
		return list;
	}
	private static short getWoolColor(EntityType type) {
		try {
		switch (type) {
		case ZOMBIE:
		case WITCH:
		case SPIDER:
		case SLIME:
		case SKELETON:
		case SILVERFISH:
		case GIANT:
		case ENDERMITE:
		case ENDERMAN:
		case CREEPER:
		case CAVE_SPIDER:
			return 14;
		case WOLF:
		case VILLAGER:
		case SNOWMAN:
		case IRON_GOLEM:
			return 0;
		case WITHER:
		case ENDER_DRAGON:
			return 15;
		case SQUID:
		case GUARDIAN:
			return 11;
		case BAT:
		case OCELOT:
		case PIG:
		case SHEEP:
		case RABBIT:
		case MUSHROOM_COW:
		case COW:
		case HORSE:
		case CHICKEN:
			return 5;
		case PLAYER:
			return 4;
		case BLAZE:
		case PIG_ZOMBIE:
		case MAGMA_CUBE:
		case GHAST:
		case WITHER_SKELETON:
			return 1;
		//1.8

		case DONKEY:
		case MULE:
		case SKELETON_HORSE:
		case ZOMBIE_HORSE:
			return 5;
		case ELDER_GUARDIAN:
			return 11;
		case ZOMBIE_VILLAGER:
			return 14;
		//1.9
		case SHULKER:
			return 14;
		//1.10
		case POLAR_BEAR:
			return 5;
		case HUSK:
		case STRAY:
			return 14;
		//1.11
		case LLAMA:
			return 5;
		case EVOKER:
		case VEX:
		case VINDICATOR:
			return 14;
		//1.12
		case PARROT:
			return 5;
		case ILLUSIONER:
			return 14;
		//1.13
		/*
		case TURTLE:
		case PHANTOM:
		case COD:
		case SALMON:
		case PUFFER_FISH:
		case TROPICAL_FISH:
		case DROWNED:
		case DOLPHIN:
			return 11;
		}*/
			default:
		}
		}catch (Exception e) {}
		return 0;
		
		
	}
	
	private class EntityTypeEditorGui extends CustomMultiPageGuiHolder<EntityTypeButton> {
		public EntityTypeEditorGui(Player p, EditorGui previusHolder) {
			super(p,previusHolder, 6,1);
			for (EntityType type : ALLOWED_ENTITY_TYPES) {
				addButton(new EntityTypeButton(this,type));
			}
			this.setFromEndCloseButtonPosition(8);
			reloadInventory();
		}
		public void reloadInventory() {
			getInventory().setItem(size()-1,whitelistButton.getItem());
			super.reloadInventory();
		}
		@Override
		public void onSlotClick(Player clicker,int slot,ClickType click) {
			if (slot==size()-1) {
				whitelistButton.onClick(clicker,click);
				return;
			}
			super.onSlotClick(clicker, slot, click);
		}
		private WhiteListButton whitelistButton = new WhiteListButton(this);
		@Override
		public void update() {
			whitelistButton.update();
			super.update();
		}
		private class WhiteListButton extends CustomGuiItem {
			public WhiteListButton(EntityTypeEditorGui parent) {
				super(parent);
				update();
			}
			@Override 
			public void update() {
				if (!isEntityTypeListWhitelist()) {
					this.item.setDurability((short) 15);
					ItemMeta meta = item.getItemMeta();
					meta.setDisplayName(StringUtils.fixColorsAndHolders("&6Selected EntityType are on a &lBlackList"));
					item.setItemMeta(meta);
				}
				else {
					this.item.setDurability((short) 0);
					ItemMeta meta = item.getItemMeta();
					meta.setDisplayName(StringUtils.fixColorsAndHolders("&6Selected EntityType are on a &lWhiteList"));
					item.setItemMeta(meta);
				}
			}
			private ItemStack item = new ItemStack(Material.WOOL);

			@Override
			public ItemStack getItem() {
				return item;
			}

			@Override
			public void onClick(Player clicker, ClickType click) {
				setEntityTypeListWhitelist(!isEntityTypeListWhitelist());
				update();
				getParent().update();
			}
		}
	}
	
	private class EntityTypeButton extends CustomGuiItem {
		private ItemStack item = new ItemStack(Material.WOOL);
		private final EntityType type;
		public EntityTypeButton(EntityTypeEditorGui parent,EntityType type) {
			super(parent);
			this.type = type;
			item.setDurability(getWoolColor(type));
			ItemMeta meta = item.getItemMeta();
			meta.setDisplayName(StringUtils.fixColorsAndHolders("&6Entity Type: '&e&l"+type.toString()+"&6'"));
			meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
			item.setItemMeta(meta);
			update();
		}
		public void update() {
			ItemMeta meta = this.item.getItemMeta();
			ArrayList<String> lore = new ArrayList<String>();
			if (entity.contains(type)) {
				if (!isEntityTypeListWhitelist()) {
					lore.add(StringUtils.fixColorsAndHolders("&6This type is &cBlackListed"));
					meta.removeEnchant(Enchantment.DURABILITY);
				}
				else {
					lore.add(StringUtils.fixColorsAndHolders("&6This type is &aWhiteListed"));
					meta.addEnchant(Enchantment.DURABILITY, 1, true);
				}
				lore.add(StringUtils.fixColorsAndHolders("&7(list contains this type)"));
			}
			else {
				if (isEntityTypeListWhitelist()) {
					lore.add(StringUtils.fixColorsAndHolders("&6This type is &cBlackListed"));
					meta.removeEnchant(Enchantment.DURABILITY);
				}
				else {
					lore.add(StringUtils.fixColorsAndHolders("&6This type is &aWhiteListed"));
					meta.addEnchant(Enchantment.DURABILITY, 1, true);
				}
				lore.add(StringUtils.fixColorsAndHolders("&7(list don't contains this type)"));
			}
			meta.setLore(lore);
			item.setItemMeta(meta);
		}
		
		@Override
		public ItemStack getItem() {
			return item;
		}

		@Override
		public void onClick(Player clicker, ClickType click) {
			if (entity.contains(type))
				removeEntityTypeFromList(type);
			else
				addEntityTypeToList(type);
			update();
			getParent().reloadInventory();
		}
		
	}
	
}
