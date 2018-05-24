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
import emanondev.quests.gui.CustomGui;
import emanondev.quests.gui.CustomButton;
import emanondev.quests.gui.CustomMultiPageGui;
import emanondev.quests.gui.EditorGui;
import emanondev.quests.gui.EditorButtonFactory;
import emanondev.quests.hooks.Hooks;
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
	public EditorButtonFactory getEntityTypeEditorButtonFactory(){
		return new EditEntityTypeFactory();
	}
	
	private class EditEntityTypeFactory implements EditorButtonFactory {
		private class EditEntityTypeButton extends CustomButton {
			private ItemStack item = new ItemStack(Material.SKULL_ITEM);
			public EditEntityTypeButton(CustomGui parent) {
				super(parent);
				update();
			}
			@Override
			public ItemStack getItem() {
				return item;
			}
			public void update() {
				ItemMeta meta = item.getItemMeta();
				meta.setDisplayName(StringUtils.fixColorsAndHolders("&6&lEntity Type Selector"));
				ArrayList<String> lore = new ArrayList<String>();
				lore.add("&6Click to edit");
				if (entity.isEmpty())
					lore.add("&7No entity restrictions are set");
				else {
					if (!isEntityTypeListWhitelist()) {
						lore.add("&7All listed entity are &cdisabled");
						for (EntityType type : entity)
							lore.add(" &7- &c"+type.toString());
					}
					else {
						lore.add("&7All listed entity are &aenabled");
						for (EntityType type : entity)
							lore.add(" &7- &a"+type.toString());
					}
				}
				meta.setLore(StringUtils.fixColorsAndHolders(lore));
				item.setItemMeta(meta);
			}
			@Override
			public void onClick(Player clicker, ClickType click) {
				clicker.openInventory(new EntityTypeEditorGui(clicker,(EditorGui) getParent()).getInventory());
			}
			private class EntityTypeEditorGui extends CustomMultiPageGui<CustomButton> {
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
				private WhitelistButton whitelistButton = new WhitelistButton(this);
				@Override
				public void update() {
					whitelistButton.update();
					super.update();
				}
				private class WhitelistButton extends CustomButton {
					public WhitelistButton(EntityTypeEditorGui parent) {
						super(parent);
						ItemMeta meta = item.getItemMeta();
						meta.setDisplayName(StringUtils.fixColorsAndHolders("&6Click to revert valid types"));
						item.setItemMeta(meta);
						update();
					}
					@Override 
					public void update() {
						if (!isEntityTypeListWhitelist()) {
							this.item.setDurability((short) 15);
							ItemMeta meta = item.getItemMeta();
							ArrayList<String> lore = new ArrayList<String>();
							lore.add(StringUtils.fixColorsAndHolders("&7(Now Blacklist)"));
							meta.setLore(lore);
							item.setItemMeta(meta);
						}
						else {
							this.item.setDurability((short) 0);
							ItemMeta meta = item.getItemMeta();
							ArrayList<String> lore = new ArrayList<String>();
							lore.add(StringUtils.fixColorsAndHolders("&7(Now Whitelist)"));
							meta.setLore(lore);
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
				private class EntityTypeButton extends CustomButton {
					private ItemStack item = new ItemStack(Material.WOOL);
					private final EntityType type;
					public EntityTypeButton(EntityTypeEditorGui parent,EntityType type) {
						super(parent);
						this.type = type;
						item.setDurability(getWoolColor(type));
						ItemMeta meta = item.getItemMeta();
						meta.setDisplayName(StringUtils.fixColorsAndHolders("&6Entity Type: '&e"+type.toString()+"&6'"));
						meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
						item.setItemMeta(meta);
						update();
					}
					public void update() {
						ItemMeta meta = this.item.getItemMeta();
						ArrayList<String> lore = new ArrayList<String>();
						if (entity.contains(type)) {
							if (!isEntityTypeListWhitelist()) {
								lore.add(StringUtils.fixColorsAndHolders("&7This type is &cUnallowed"));
								meta.removeEnchant(Enchantment.DURABILITY);
							}
							else {
								lore.add(StringUtils.fixColorsAndHolders("&7This type is &aAllowed"));
								meta.addEnchant(Enchantment.DURABILITY, 1, true);
							}
							lore.add(StringUtils.fixColorsAndHolders("&7(selected)"));
						}
						else {
							if (isEntityTypeListWhitelist()) {
								lore.add(StringUtils.fixColorsAndHolders("&7This type is &cUnallowed"));
								meta.removeEnchant(Enchantment.DURABILITY);
							}
							else {
								lore.add(StringUtils.fixColorsAndHolders("&7This type is &aAllowed"));
								meta.addEnchant(Enchantment.DURABILITY, 1, true);
							}
							lore.add(StringUtils.fixColorsAndHolders("&7(unselected)"));
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
			
		}
		@Override
		public EditEntityTypeButton getCustomButton(CustomGui parent) {
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
	
	
	public boolean setSpawnReasonListWhitelist(boolean value) {
		if (value==spawnReasonsWhitelist)
			return false;
		spawnReasonsWhitelist = value;
		section.set(PATH_ENTITY_SPAWNREASON_AS_WHITELIST,spawnReasonsWhitelist);
		parent.setDirty(true);
		return true;
	}
	public Set<SpawnReason> getListedSpawnReason(){
		return Collections.unmodifiableSet(spawnReasons);
	}
	
	private boolean isSpawnReasonListWhitelist() {
		return spawnReasonsWhitelist;
	}
	public boolean addSpawnReasonToList(SpawnReason type) {
		if (spawnReasons.contains(type))
			return false;
		spawnReasons.add(type);
		ArrayList<String> list = new ArrayList<String>();
		for (SpawnReason spawnType : spawnReasons) {
			list.add(spawnType.toString());
		}
		section.set(PATH_ENTITY_SPAWNREASON,list);
		parent.setDirty(true);
		return true;
	}
	
	public boolean removeSpawnReasonFromList(SpawnReason type) {
		if (!spawnReasons.contains(type))
			return false;
		spawnReasons.remove(type);
		ArrayList<String> list = new ArrayList<String>();
		for (SpawnReason spawnType : spawnReasons) {
			list.add(spawnType.toString());
		}
		section.set(PATH_ENTITY_SPAWNREASON,list);
		parent.setDirty(true);
		return true;
	}
	
	public EditorButtonFactory getSpawnReasonEditorButtonFactory(){
		return new EditSpawnReasonFactory();
	}
	
	private class EditSpawnReasonFactory implements EditorButtonFactory {
		private class EditSpawnReasonButton extends CustomButton {
			private ItemStack item = new ItemStack(Material.MONSTER_EGG);
			public EditSpawnReasonButton(CustomGui parent) {
				super(parent);
				update();
			}
			@Override
			public ItemStack getItem() {
				return item;
			}
			public void update() {
				ItemMeta meta = item.getItemMeta();
				meta.setDisplayName(StringUtils.fixColorsAndHolders("&6&lSpawn Reasons Selector"));
				ArrayList<String> lore = new ArrayList<String>();
				lore.add("&6Click to edit");
				if (spawnReasons.isEmpty())
					lore.add("&7No Spawn Reasons restrictions are set");
				else {
					if (!isSpawnReasonListWhitelist()) {
						lore.add("&7All listed Spawn Reasons are &cdisabled");
						for (SpawnReason type : spawnReasons)
							lore.add(" &7- &c"+type.toString());
					}
					else {
						lore.add("&7All listed Spawn Reasons are &aenabled");
						for (SpawnReason type : spawnReasons)
							lore.add(" &7- &a"+type.toString());
					}
				}
				meta.setLore(StringUtils.fixColorsAndHolders(lore));
				item.setItemMeta(meta);
			}
			@Override
			public void onClick(Player clicker, ClickType click) {
				clicker.openInventory(new SpawnReasonEditorGui(clicker,(EditorGui) getParent()).getInventory());
			}
			private class SpawnReasonEditorGui extends CustomMultiPageGui<CustomButton> {
				public SpawnReasonEditorGui(Player p, EditorGui previusHolder) {
					super(p,previusHolder, 6,1);
					for (SpawnReason type : SpawnReason.values()) {
						addButton(new SpawnReasonButton(this,type));
					}
					this.setFromEndCloseButtonPosition(8);
					this.setTitle(null,StringUtils.fixColorsAndHolders("&8Spawn Reason Selector"));
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
				private WhitelistButton whitelistButton = new WhitelistButton(this);
				@Override
				public void update() {
					whitelistButton.update();
					super.update();
				}
				private class WhitelistButton extends CustomButton {
					public WhitelistButton(SpawnReasonEditorGui parent) {
						super(parent);
						ItemMeta meta = item.getItemMeta();
						meta.setDisplayName(StringUtils.fixColorsAndHolders("&6Click to Revert valid Spawn Reasons"));
						item.setItemMeta(meta);
						
						update();
					}
					@Override 
					public void update() {
						if (!isSpawnReasonListWhitelist()) {
							this.item.setDurability((short) 15);
							ItemMeta meta = item.getItemMeta();
							ArrayList<String> lore = new ArrayList<String>();
							lore.add(StringUtils.fixColorsAndHolders("&7(Now Blacklist)"));
							meta.setLore(lore);	
							item.setItemMeta(meta);
						}
						else {
							this.item.setDurability((short) 0);
							ItemMeta meta = item.getItemMeta();
							ArrayList<String> lore = new ArrayList<String>();
							lore.add(StringUtils.fixColorsAndHolders("&7(Now Whitelist)"));
							meta.setLore(lore);	
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
						setSpawnReasonListWhitelist(!isSpawnReasonListWhitelist());
						update();
						getParent().update();
					}
				}
				private class SpawnReasonButton extends CustomButton {
					private ItemStack item = new ItemStack(Material.WOOL);
					private final SpawnReason type;
					public SpawnReasonButton(SpawnReasonEditorGui parent,SpawnReason type) {
						super(parent);
						this.type = type;
						//item.setDurability(getWoolColor(type));
						ItemMeta meta = item.getItemMeta();
						meta.setDisplayName(StringUtils.fixColorsAndHolders("&6Spawn Reason: '&e"+type.toString()+"&6'"));
						meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
						item.setItemMeta(meta);
						update();
					}
					public void update() {
						ItemMeta meta = this.item.getItemMeta();
						ArrayList<String> lore = new ArrayList<String>();
						if (spawnReasons.contains(type)) {
							if (!isSpawnReasonListWhitelist()) {
								lore.add(StringUtils.fixColorsAndHolders("&7This type is &cUnallowed"));
								meta.removeEnchant(Enchantment.DURABILITY);
							}
							else {
								lore.add(StringUtils.fixColorsAndHolders("&7This type is &aAllowed"));
								meta.addEnchant(Enchantment.DURABILITY, 1, true);
							}
							lore.add(StringUtils.fixColorsAndHolders("&7(select)"));
						}
						else {
							if (isSpawnReasonListWhitelist()) {
								lore.add(StringUtils.fixColorsAndHolders("&7This type is &cUnallowed"));
								meta.removeEnchant(Enchantment.DURABILITY);
							}
							else {
								lore.add(StringUtils.fixColorsAndHolders("&7This type is &aAllowed"));
								meta.addEnchant(Enchantment.DURABILITY, 1, true);
							}
							lore.add(StringUtils.fixColorsAndHolders("&7(not selected)"));
						}
						lore.addAll(StringUtils.fixColorsAndHolders(getDescription(type)));
						meta.setLore(lore);
						item.setItemMeta(meta);
					}
					
					@Override
					public ItemStack getItem() {
						return item;
					}

					@Override
					public void onClick(Player clicker, ClickType click) {
						if (spawnReasons.contains(type))
							removeSpawnReasonFromList(type);
						else
							addSpawnReasonToList(type);
						update();
						getParent().reloadInventory();
					}
					
				}
			}
		}
		@Override
		public EditSpawnReasonButton getCustomButton(CustomGui parent) {
			return new EditSpawnReasonButton(parent);
		}
	}
	
	
	private static ArrayList<String> getDescription(SpawnReason reason){
		ArrayList<String> desc = new ArrayList<String>();
		desc.add("");
		switch (reason) {
		case BREEDING:
			desc.add("&7When an animal breeds to create a child");
			break;
		case BUILD_IRONGOLEM:
			desc.add("&7When an iron golem is spawned by being built");
			break;
		case BUILD_SNOWMAN:
			desc.add("&7When a snowman is spawned by being built");
			break;
		case BUILD_WITHER:
			desc.add("&7When a wither boss is spawned by being built");
			break;
		case CHUNK_GEN:
			desc.add("&7When a creature spawns due to chunk generation");
			break;
		case CURED:
			desc.add("&7When a villager is cured from infection");
			break;
		case CUSTOM:
			desc.add("&7When a creature is spawned by plugins");
			break;
		case DEFAULT:
			desc.add("&7When an entity is missing a SpawnReason");
			break;
		case DISPENSE_EGG:
			desc.add("&7When a creature is spawned");
			desc.add("&7by a dispenser dispensing an egg");
			break;
		case EGG:
			desc.add("&7When a creature spawns from an egg");
			break;
		case ENDER_PEARL:
			desc.add("&7When an entity is spawned as");
			desc.add("&7a result of ender pearl usage");
			break;
		case INFECTION:
			desc.add("&7When a zombie infects a villager");
			break;
		case JOCKEY:
			desc.add("&7When an entity spawns as");
			desc.add("&7a jockey of another entity");
			desc.add("&7(mostly spider jockeys)");
			break;
		case LIGHTNING:
			desc.add("&7When a creature spawns because");
			desc.add("&7of a lightning strike");
			break;
		case MOUNT:
			desc.add("&7When an entity spawns as");
			desc.add("&7a mount of another entity");
			desc.add("&7(mostly chicken jockeys)");
			break;
		case NATURAL:
			desc.add("&7When something spawns from natural means");
			break;
		case NETHER_PORTAL:
			desc.add("&7When a creature is spawned by nether portal");
			break;
		case OCELOT_BABY:
			desc.add("&7When an ocelot has a baby");
			desc.add("&7spawned along with them");
			break;
		case REINFORCEMENTS:
			desc.add("&7When an entity calls for reinforcements");
			break;
		case SHOULDER_ENTITY:
			desc.add("&7When an entity is spawned as a");
			desc.add("&7result of the entity it is being");
			desc.add("&7perched on jumping or being damaged");
			break;
		case SILVERFISH_BLOCK:
			desc.add("&7When a silverfish spawns from a block");
			break;
		case SLIME_SPLIT:
			desc.add("&7When a slime splits");
			break;
		case SPAWNER:
			desc.add("&7When a creature spawns from a spawner");
			break;
		case SPAWNER_EGG:
			desc.add("&7When a creature spawns from a Spawner Egg");
			break;
		case TRAP:
			desc.add("&7When an entity spawns as");
			desc.add("&7a trap for players approaching");
			break;
		case VILLAGE_DEFENSE:
			desc.add("&7When an iron golem is spawned to defend a village");
			break;
		case VILLAGE_INVASION:
			desc.add("&7When a zombie is spawned to invade a village");
			break;
		}
		return desc;
	}
	
	
	
	public boolean setIgnoreCitizenNPC(boolean value) {
		if (value == ignoreNPC)
			return false;
		ignoreNPC = value;
		section.set(PATH_IGNORE_NPC,ignoreNPC);
		parent.setDirty(true);
		return true;
	}
	
	public EditorButtonFactory getIgnoreCitizenNPCEditorButtonFactory(){
		return new EditIgnoreCitizenNPCFactory();
	}
	public boolean areCitizensNPCIgnored() {
		return ignoreNPC;
	}
	
	private class EditIgnoreCitizenNPCFactory implements EditorButtonFactory {
		private class EditIgnoreCitizenNPCButton extends CustomButton {
			private ItemStack item = new ItemStack(Material.SKULL_ITEM);
			public EditIgnoreCitizenNPCButton(CustomGui parent) {
				super(parent);
				item.setDurability((short) 3);
				update();
			}
			@Override
			public ItemStack getItem() {
				return item;
			}
			public void update() {
				ItemMeta meta = item.getItemMeta();
				meta.setDisplayName(StringUtils.fixColorsAndHolders("&6&lCitizen Npc Flag"));
				ArrayList<String> lore = new ArrayList<String>();
				lore.add("&6Click to toggle");
				if (!ignoreNPC) {
					lore.add("&7Now Citizen NPC &cwon't count &7as valid Targets");
				}
				else {
					lore.add("&7Now Citizen NPC &acount &7as valid Targets");
				}
				meta.setLore(StringUtils.fixColorsAndHolders(lore));
				item.setItemMeta(meta);
			}
			@Override
			public void onClick(Player clicker, ClickType click) {
				setIgnoreCitizenNPC(!areCitizensNPCIgnored());
				update();
				getParent().reloadInventory();
			}
		}
		@Override
		public EditIgnoreCitizenNPCButton getCustomButton(CustomGui parent) {
			if (Hooks.isCitizenEnabled())
				return new EditIgnoreCitizenNPCButton(parent);
			return null;
		}
	}
	
}
