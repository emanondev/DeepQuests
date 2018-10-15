package emanondev.quests.data;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang.exception.ExceptionUtils;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.event.entity.CreatureSpawnEvent.SpawnReason;
import org.bukkit.inventory.ItemStack;
import emanondev.quests.Quests;
import emanondev.quests.SpawnReasonTracker;
import emanondev.quests.configuration.ConfigSection;
import emanondev.quests.newgui.button.Button;
import emanondev.quests.newgui.button.CollectionElementsSelectorButton;
import emanondev.quests.newgui.button.EnumSetSelectorButton;
import emanondev.quests.newgui.button.StaticFlagButton;
import emanondev.quests.newgui.gui.Gui;
import emanondev.quests.task.Task;
import emanondev.quests.utils.ItemBuilder;
import emanondev.quests.utils.StringUtils;
import emanondev.quests.utils.Utils;

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
	private final ConfigSection section;

	public EntityTaskInfo(ConfigSection m, Task parent) {
		this.parent = parent;
		this.section = m;
		List<String> list = m.getStringList(PATH_ENTITY);
		if (list != null)
			for (String value : list) {
				try {
					entity.add(EntityType.valueOf(value.toUpperCase()));
				} catch (Exception e) {
					Quests.getLogger("errors")
							.log("error on Path " + m.getCurrentPath() + "." + m.getName() + " " + e.getMessage());
					Quests.getLogger("errors").log(ExceptionUtils.getStackTrace(e));
				}
			}
		if (entity.isEmpty())
			entityWhitelist = false;
		else
			entityWhitelist = m.getBoolean(PATH_ENTITY_AS_WHITELIST, true);

		entityName = m.getString(PATH_ENTITY_NAME, null);

		list = m.getStringList(PATH_ENTITY_SPAWNREASON);
		if (list != null)
			for (String value : list) {
				try {
					spawnReasons.add(SpawnReason.valueOf(value.toUpperCase()));
				} catch (Exception e) {
					Quests.getLogger("errors")
							.log("error on Path " + m.getCurrentPath() + "." + m.getName() + " " + e.getMessage());
					Quests.getLogger("errors").log(ExceptionUtils.getStackTrace(e));
				}
			}
		if (spawnReasons.isEmpty())
			spawnReasonsWhitelist = false;
		else
			spawnReasonsWhitelist = m.getBoolean(PATH_ENTITY_SPAWNREASON_AS_WHITELIST, false);

		ignoreNPC = m.getBoolean(PATH_IGNORE_NPC, true);
	}

	public boolean isEntityTypeListWhitelist() {
		return entityWhitelist;
	}

	public boolean setEntityTypeListWhitelist(boolean value) {
		if (value == entityWhitelist)
			return false;
		entityWhitelist = value;
		section.set(PATH_ENTITY_AS_WHITELIST, entityWhitelist);
		parent.setDirty(true);
		return true;
	}

	public Set<EntityType> getListedEntityType() {
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
		section.set(PATH_ENTITY, list);
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
		section.set(PATH_ENTITY, list);
		parent.setDirty(true);
		return true;
	}

	private boolean checkEntityType(Entity e) {
		if (entityWhitelist) {
			if (!entity.contains(e.getType()))
				return false;
		} else {
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
		} else {
			if (spawnReasons.contains(reason))
				return false;
		}
		return true;
	}

	private boolean checkEntityName(Entity e) {
		if (entityName == null)
			return true;

		return entityName.equals(e.getName());
	}

	private boolean checkNPC(Entity e) {
		if (ignoreNPC && e.hasMetadata("NPC"))
			return false;
		return true;
	}

	public boolean isValidEntity(Entity e) {
		return checkEntityType(e) && checkSpawnReason(e) && checkEntityName(e) && checkNPC(e);
	}

	public Button getEntityTypeSelectorButton(Gui parent) {
		return new EntityTypeSelectorButton(parent);
	}

	private class EntityTypeSelectorButton extends CollectionElementsSelectorButton<EntityType> {

		public EntityTypeSelectorButton(Gui parent) {
			super("&6&lEntityType Selector",
					new ItemBuilder(Material.SKULL_ITEM).setGuiProperty().build(), 
					parent, ALLOWED_ENTITY_TYPES, true);
		}

		@Override
		public List<String> getButtonDescription() {
			ArrayList<String> desc = new ArrayList<String>();
			desc.add("&6&lEntity Type Selector");
			desc.add("&6Click to edit");
			if (entity.isEmpty())
				desc.add("&7No entity restrictions are set");
			else {
				if (!isEntityTypeListWhitelist()) {
					desc.add("&7All listed entity are &cdisabled");
					for (EntityType type : entity)
						desc.add(" &7- &c" + type.toString());
				} else {
					desc.add("&7All listed entity are &aenabled");
					for (EntityType type : entity)
						desc.add(" &7- &a" + type.toString());
				}
			}
			return desc;
		}

		@Override
		public List<String> getElementDescription(EntityType type) {
			ArrayList<String> desc = new ArrayList<String>();
			desc.add("&6Entity Type: '&e" + type.toString() + "&6'");
			if (entity.contains(type)) {
				if (!isEntityTypeListWhitelist()) {
					desc.add(StringUtils.fixColorsAndHolders("&7This type is &cUnallowed"));
				} else {
					desc.add(StringUtils.fixColorsAndHolders("&7This type is &aAllowed"));
				}
				desc.add(StringUtils.fixColorsAndHolders("&7(selected)"));
			} else {
				if (isEntityTypeListWhitelist()) {
					desc.add(StringUtils.fixColorsAndHolders("&7This type is &cUnallowed"));
				} else {
					desc.add(StringUtils.fixColorsAndHolders("&7This type is &aAllowed"));
				}
				desc.add(StringUtils.fixColorsAndHolders("&7(unselected)"));
			}
			return desc;
		}

		@Override
		public ItemStack getElementItem(EntityType type) {
			return getGuiItem(type);
		}

		@Override
		public boolean getIsWhitelist() {
			return isEntityTypeListWhitelist();
		}

		@Override
		public boolean onToggleElementRequest(EntityType element) {
			if (currentCollectionContains(element))
				return removeEntityTypeFromList(element);
			return addEntityTypeToList(element);
		}

		@Override
		public boolean onWhiteListChangeRequest(boolean isWhitelist) {
			return setEntityTypeListWhitelist(isWhitelist);
		}

		@Override
		public boolean currentCollectionContains(EntityType element) {
			return getListedEntityType().contains(element);
		}

	}

	private final static List<EntityType> ALLOWED_ENTITY_TYPES = loadAllowedTypes();

	private static List<EntityType> loadAllowedTypes() {
		ArrayList<EntityType> list = new ArrayList<EntityType>();
		try {
			list.add(EntityType.PLAYER);
			list.add(EntityType.ZOMBIE);
			list.add(EntityType.ZOMBIE_VILLAGER);// 1.8
			list.add(EntityType.HUSK);// 1.10
			list.add(EntityType.GIANT);
			list.add(EntityType.SKELETON);
			list.add(EntityType.STRAY);// 1.10
			list.add(EntityType.WITCH);
			list.add(EntityType.SPIDER);
			list.add(EntityType.CAVE_SPIDER);
			list.add(EntityType.CREEPER);
			list.add(EntityType.SILVERFISH);
			list.add(EntityType.GUARDIAN);//1.8
			list.add(EntityType.ELDER_GUARDIAN);// 1.8
			
			list.add(EntityType.VINDICATOR);// 1.11
			list.add(EntityType.VEX);// 1.11
			list.add(EntityType.EVOKER);// 1.11
			list.add(EntityType.ILLUSIONER);// 1.12
			
			list.add(EntityType.SLIME);
			list.add(EntityType.MAGMA_CUBE);
			list.add(EntityType.PIG_ZOMBIE);
			list.add(EntityType.BLAZE);
			list.add(EntityType.GHAST);
			list.add(EntityType.WITHER_SKELETON);
			list.add(EntityType.WITHER);
			
			list.add(EntityType.ENDERMAN);
			list.add(EntityType.ENDERMITE);
			list.add(EntityType.SHULKER);// 1.9
			list.add(EntityType.ENDER_DRAGON);
			list.add(EntityType.VILLAGER);
			list.add(EntityType.IRON_GOLEM);
			list.add(EntityType.SNOWMAN);
			list.add(EntityType.HORSE);
			list.add(EntityType.DONKEY);// 1.8
			list.add(EntityType.MULE);// 1.8
			list.add(EntityType.SKELETON_HORSE);// 1.8
			list.add(EntityType.ZOMBIE_HORSE);// 1.8
			list.add(EntityType.PIG);
			list.add(EntityType.CHICKEN);
			list.add(EntityType.SHEEP);
			list.add(EntityType.COW);
			list.add(EntityType.MUSHROOM_COW);
			list.add(EntityType.WOLF);
			list.add(EntityType.OCELOT);
			list.add(EntityType.RABBIT);
			list.add(EntityType.LLAMA);// 1.11
			list.add(EntityType.POLAR_BEAR);// 1.10
			list.add(EntityType.SQUID);
			list.add(EntityType.PARROT);// 1.12
			list.add(EntityType.BAT);
			// 1.13
			/*
			 * list.add(TURTLE); list.add(PHANTOM); list.add(COD); list.add(SALMON);
			 * list.add(PUFFER_FISH); list.add(TROPICAL_FISH); list.add(DROWNED);
			 * list.add(DOLPHIN);
			 */
		} catch (Exception e) {
		}
		return list;
	}

	private static ItemStack getGuiItem(EntityType type) {
		try {
			switch (type) {
			case ZOMBIE:
				return new ItemBuilder(Material.ROTTEN_FLESH).setGuiProperty().build();
			case WITCH:
				return new ItemBuilder(Material.GLASS_BOTTLE).setGuiProperty().build();
			case SPIDER:
			case CAVE_SPIDER:
				return new ItemBuilder(Material.SPIDER_EYE).setGuiProperty().build();
			case SLIME:
				return new ItemBuilder(Material.SLIME_BALL).setGuiProperty().build();
			case SKELETON:
				return new ItemBuilder(Material.BONE).setGuiProperty().build();
			case SILVERFISH:
				return new ItemBuilder(Material.SMOOTH_BRICK).setGuiProperty().build();
			case GIANT:
				return new ItemBuilder(Material.BONE_BLOCK).setGuiProperty().build();
			case ENDERMITE:
				return new ItemBuilder(Material.ENDER_PEARL).setAmount(2).setGuiProperty().build();
			case ENDERMAN:
				return new ItemBuilder(Material.ENDER_PEARL).setGuiProperty().build();
			case CREEPER:
				return new ItemBuilder(Material.SULPHUR).setGuiProperty().build();
			case WOLF:
			case VILLAGER:
				return new ItemBuilder(Material.EMERALD).setGuiProperty().build();
			case SNOWMAN:
				return new ItemBuilder(Material.SNOW_BALL).setGuiProperty().build();
			case IRON_GOLEM:
				return new ItemBuilder(Material.IRON_BLOCK).setGuiProperty().build();
			case WITHER:
				return new ItemBuilder(Material.NETHER_STAR).setGuiProperty().build();
			case ENDER_DRAGON:
				return new ItemBuilder(Material.DRAGON_EGG).setGuiProperty().build();
			case SQUID:
				return new ItemBuilder(Material.INK_SACK).setGuiProperty().build();
			case GUARDIAN:
				return new ItemBuilder(Material.PRISMARINE_SHARD).setGuiProperty().build();
			case BAT:
				return new ItemBuilder(Material.STONE).setGuiProperty().build();
			case OCELOT:
				return new ItemBuilder(Material.RAW_FISH).setGuiProperty().build();
			case PIG:
				return new ItemBuilder(Material.PORK).setGuiProperty().build();
			case SHEEP:
				return new ItemBuilder(Material.WOOL).setGuiProperty().build();
			case RABBIT:
				return new ItemBuilder(Material.RABBIT_FOOT).setGuiProperty().build();
			case MUSHROOM_COW:
				return new ItemBuilder(Material.RED_MUSHROOM).setGuiProperty().build();
			case COW:
				return new ItemBuilder(Material.MILK_BUCKET).setGuiProperty().build();
			case CHICKEN:
				return new ItemBuilder(Material.RAW_CHICKEN).setGuiProperty().build();
			case PLAYER:
				return new ItemBuilder(Material.SKULL_ITEM).setDamage(3).setGuiProperty().build();
			case BLAZE:
				return new ItemBuilder(Material.BLAZE_ROD).setGuiProperty().build();
			case PIG_ZOMBIE:
				return new ItemBuilder(Material.GOLD_NUGGET).setGuiProperty().build();
			case MAGMA_CUBE:
				return new ItemBuilder(Material.MAGMA_CREAM).setGuiProperty().build();
			case GHAST:
				return new ItemBuilder(Material.GHAST_TEAR).setGuiProperty().build();
			case WITHER_SKELETON:
				return new ItemBuilder(Material.NETHER_BRICK).setGuiProperty().build();
			// 1.8

			case HORSE:
				return new ItemBuilder(Material.SADDLE).setGuiProperty().build();
			case DONKEY:
				return new ItemBuilder(Material.SADDLE).setAmount(2).setGuiProperty().build();
			case MULE:
				return new ItemBuilder(Material.SADDLE).setAmount(3).setGuiProperty().build();
			case SKELETON_HORSE:
				return new ItemBuilder(Material.SADDLE).setAmount(4).setGuiProperty().build();
			case ZOMBIE_HORSE:
				return new ItemBuilder(Material.SADDLE).setAmount(5).setGuiProperty().build();
			case ELDER_GUARDIAN:
				return new ItemBuilder(Material.PRISMARINE_CRYSTALS).setGuiProperty().build();
			case ZOMBIE_VILLAGER:
				return new ItemBuilder(Material.ROTTEN_FLESH).setAmount(2).setGuiProperty().build();
			// 1.9
			case SHULKER:
				return new ItemBuilder(Material.PURPLE_SHULKER_BOX).setGuiProperty().build();
			// 1.10
			case POLAR_BEAR:
				return new ItemBuilder(Material.SNOW_BLOCK).setGuiProperty().build();
			case HUSK:
				return new ItemBuilder(Material.ROTTEN_FLESH).setAmount(3).setGuiProperty().build();
			case STRAY:
				return new ItemBuilder(Material.BONE).setAmount(2).setGuiProperty().build();
			// 1.11
			case LLAMA:
				return new ItemBuilder(Material.CARPET).setGuiProperty().build();
			case EVOKER:
				return new ItemBuilder(Material.MONSTER_EGG).setGuiProperty().build();
			case VEX:
				return new ItemBuilder(Material.IRON_SWORD).setGuiProperty().build();
			case VINDICATOR:
				return new ItemBuilder(Material.IRON_AXE).setGuiProperty().build();
			// 1.12
			case PARROT:
				return new ItemBuilder(Material.NOTE_BLOCK).setGuiProperty().build();
			case ILLUSIONER:
				return new ItemBuilder(Material.LINGERING_POTION).setGuiProperty().build();
			// 1.13
			/*
			 * case TURTLE: case PHANTOM: case COD: case SALMON: case PUFFER_FISH: case
			 * TROPICAL_FISH: case DROWNED: case DOLPHIN: return 11; }
			 */
			default:
			}
		} catch (Exception e) {
		}
		return new ItemBuilder(Material.BARRIER).setGuiProperty().build();

	}

	public boolean setSpawnReasonListWhitelist(boolean value) {
		if (value == spawnReasonsWhitelist)
			return false;
		spawnReasonsWhitelist = value;
		section.set(PATH_ENTITY_SPAWNREASON_AS_WHITELIST, spawnReasonsWhitelist);
		parent.setDirty(true);
		return true;
	}

	public Set<SpawnReason> getListedSpawnReason() {
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
		section.set(PATH_ENTITY_SPAWNREASON, list);
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
		section.set(PATH_ENTITY_SPAWNREASON, list);
		parent.setDirty(true);
		return true;
	}

	public Button getSpawnReasonSelectorButton(Gui parent) {
		return new SpawnReasonSelectorButton(parent);
	}

	private class SpawnReasonSelectorButton extends EnumSetSelectorButton<SpawnReason> {

		public SpawnReasonSelectorButton(Gui parent) {
			super(SpawnReason.class, "&6&lSpawn Reasons Selector",
					new ItemBuilder(Material.MONSTER_EGG).setGuiProperty().build(), parent, null, true);
		}

		@Override
		public List<String> getButtonDescription() {
			ArrayList<String> desc = new ArrayList<String>();
			desc.add("&6&lSpawn Reasons Selector");
			desc.add("&6Click to edit");
			if (spawnReasons.isEmpty())
				desc.add("&7No Spawn Reasons restrictions are set");
			else {
				if (!isSpawnReasonListWhitelist()) {
					desc.add("&7All listed Spawn Reasons are &cdisabled");
					for (SpawnReason type : spawnReasons)
						desc.add(" &7- &c" + type.toString());
				} else {
					desc.add("&7All listed Spawn Reasons are &aenabled");
					for (SpawnReason type : spawnReasons)
						desc.add(" &7- &a" + type.toString());
				}
			}
			return desc;
		}

		@Override
		public List<String> getElementDescription(SpawnReason type) {
			ArrayList<String> desc = new ArrayList<String>();
			desc.add("&6Spawn Reason: '&e" + type.toString() + "&6'");
			if (spawnReasons.contains(type)) {
				if (!isSpawnReasonListWhitelist()) {
					desc.add("&7This type is &cUnallowed");
				} else {
					desc.add("&7This type is &aAllowed");
				}
				desc.add("&7(select)");
			} else {
				if (isSpawnReasonListWhitelist()) {
					desc.add("&7This type is &cUnallowed");
				} else {
					desc.add("&7This type is &aAllowed");
				}
				desc.add("&7(not selected)");
			}
			desc.addAll(getDescription(type));
			return desc;
		}

		@Override
		public ItemStack getElementItem(SpawnReason element) {
			return new ItemBuilder(Material.MONSTER_EGG).setGuiProperty().build();
		}

		@Override
		public boolean getIsWhitelist() {
			return isSpawnReasonListWhitelist();
		}

		@Override
		public boolean onToggleElementRequest(SpawnReason element) {
			if (currentCollectionContains(element))
				return removeSpawnReasonFromList(element);
			return addSpawnReasonToList(element);
		}

		@Override
		public boolean onWhiteListChangeRequest(boolean isWhitelist) {
			return setSpawnReasonListWhitelist(isWhitelist);
		}

		@Override
		public boolean currentCollectionContains(SpawnReason element) {
			return getListedSpawnReason().contains(element);
		}

	}

	private static ArrayList<String> getDescription(SpawnReason reason) {
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
		section.set(PATH_IGNORE_NPC, ignoreNPC);
		parent.setDirty(true);
		return true;
	}

	public boolean areCitizensNPCIgnored() {
		return ignoreNPC;
	}

	public Button getIgnoreCitizenButton(Gui parent) {
		return new IgnoreCitizenTargetsButton(parent);
	}

	private class IgnoreCitizenTargetsButton extends StaticFlagButton {

		public IgnoreCitizenTargetsButton(Gui parent) {
			super(Utils.setDescription(new ItemBuilder(Material.SKULL_ITEM).setDamage(3).build(),
					Arrays.asList("&6&lCitizen Npc Flag", "&6Click to toggle",
							"&7Now Citizen NPC &cwon't count &7as valid Targets"),
					null, true),
					Utils.setDescription(new ItemBuilder(Material.SKULL_ITEM).setDamage(3).build(),
							Arrays.asList("&6&lCitizen Npc Flag", "&6Click to toggle",
									"&7Now Citizen NPC &acount &7as valid Targets"),
							null, true),
					parent);
		}

		@Override
		public boolean getCurrentValue() {
			return areCitizensNPCIgnored();
		}

		@Override
		public boolean onValueChangeRequest(boolean value) {
			return setIgnoreCitizenNPC(!areCitizensNPCIgnored());
		}
	}

}
