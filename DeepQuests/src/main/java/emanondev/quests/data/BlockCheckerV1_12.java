package emanondev.quests.data;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.inventory.ItemStack;
import emanondev.quests.newgui.button.Button;
import emanondev.quests.newgui.button.CollectionElementsSelectorButton;
import emanondev.quests.newgui.gui.Gui;
import emanondev.quests.utils.ItemBuilder;

public class BlockCheckerV1_12 implements BlockChecker {
	private final BlocksTaskInfo parent;

	BlockCheckerV1_12(List<String> list, BlocksTaskInfo parent) {
		this.parent = parent;
		boolean dirty = false;
		if (list != null) {
			for (String block : list) {
				if (block == null || block.isEmpty())
					continue;
				block = block.toUpperCase();
				if (block.contains(":")) {
					try {
						if (block.split(":").length > 2) {
							new IllegalArgumentException("Invalid text for Material type '" + block + "'")
									.printStackTrace();
							dirty = true;
							continue;
						}
						Material.valueOf(block.split(":")[0]);
					} catch (Exception e) {
						new IllegalArgumentException("Can't recognize '" + block + "' Material").printStackTrace();
						dirty = true;
						continue;
					}
					try {
						if (Integer.valueOf(block.split(":")[1]) >= 16) {
							new IllegalArgumentException("Invalid Data on Material '" + block + "'").printStackTrace();
							dirty = true;
							continue;
						}
					} catch (Exception e) {
						new IllegalArgumentException("Invalid Data on Material '" + block + "'").printStackTrace();
						dirty = true;
						continue;
					}
				} else {
					try {
						Material.valueOf(block.split(":")[0]);
					} catch (Exception e) {
						new IllegalArgumentException("Can't recognize '" + block + "' Material").printStackTrace();
						dirty = true;
						continue;
					}
				}
				blockSet.add(block);
			}
		}
		if (dirty)
			this.parent.setBlocksList(new ArrayList<String>(blockSet));
	}

	private final HashSet<String> blockSet = new HashSet<String>();

	@SuppressWarnings("deprecation")
	@Override
	public boolean isValidBlock(Block block) {
		if (blockSet.isEmpty())
			return true;
		if (hasBlock(block.getType().toString()))
			return true;
		if (hasBlock(block.getType().toString() + ":" + block.getData()))
			return true;
		return false;
	}

	private boolean hasBlock(String block) {
		return blockSet.contains(block);
	}

	private boolean addBlock(List<BlockType> block) {
		if (block == null || block.isEmpty())
			return false;
		boolean dirty = false;
		for (BlockType type : block) {
			if (type != null)
				if (blockSet.add(type.toString()))
					dirty = true;
		}
		if (dirty)
			parent.setBlocksList(new ArrayList<String>(blockSet));
		return dirty;
	}

	private boolean removeBlock(List<BlockType> block) {
		if (block == null || block.isEmpty())
			return false;
		boolean dirty = false;
		for (BlockType type : block) {
			if (type != null)
				if (blockSet.remove(type.toString()))
					dirty = true;
		}
		if (dirty)
			parent.setBlocksList(new ArrayList<String>(blockSet));
		return dirty;
	}

	@Override
	public Button getBlockSelectorButton(Gui parent) {
		return new BlockSelectorButton(parent);
	}

	private class BlockSelectorButton extends CollectionElementsSelectorButton<BlockTypeContainer> {

		public BlockSelectorButton(Gui parent) {
			super("&8Block Selector", new ItemBuilder(Material.STONE).setGuiProperty().build(), parent,
					allowedMaterials, false);
		}

		@Override
		public List<String> getButtonDescription() {
			List<String> desc = new ArrayList<String>();
			desc.add("&6&lBlock Type Selector");
			desc.add("&6Click to edit");
			if (blockSet.isEmpty())
				desc.add("&7No blocks restrictions are set");
			else {
				desc.add("&7All listed blocks are &aAllowed");
				for (String block : blockSet)
					desc.add(" &7- &a" + block);
			}
			return desc;
		}

		@Override
		public List<String> getElementDescription(BlockTypeContainer element) {
			List<String> desc = new ArrayList<String>();
			for (BlockType type : element.getBlockTypeList()) {
				if (BlockCheckerV1_12.this.hasBlock(type.toString()))
					desc.add(" &7- &a" + type.toString());
				else
					desc.add(" &7- &c" + type.toString());
			}
			return desc;
		}

		@Override
		public ItemStack getElementItem(BlockTypeContainer element) {
			return element.getDisplayItem();
		}

		@Override
		public boolean getIsWhitelist() {
			return true;
		}

		@Override
		public boolean onToggleElementRequest(BlockTypeContainer element) {
			if (!hasBlock(element.getBlockTypeList().get(0).toString()))
				return addBlock(element.getBlockTypeList());
			return removeBlock(element.getBlockTypeList());
		}

		@Override
		public boolean onWhiteListChangeRequest(boolean isWhitelist) {
			return false;
		}

		@Override
		public boolean currentCollectionContains(BlockTypeContainer element) {
			return BlockCheckerV1_12.this.hasBlock(element.getBlockTypeList().get(0).toString());
		}

	}

	private static final List<BlockTypeContainer> allowedMaterials = new ArrayList<BlockTypeContainer>(
			getMaterialButtons());

	private static void addUpTo(List<BlockTypeContainer> list, Material mat, int max) {
		for (int i = 0; i < max; i++)
			list.add(new BlockTypeContainer(mat, (byte) i));
	}

	private static void addAll(List<BlockTypeContainer> list, Material... mats) {
		for (Material mat : mats)
			list.add(new BlockTypeContainer(mat));
	}

	private static final List<BlockTypeContainer> getMaterialButtons() {
		List<BlockTypeContainer> list = new ArrayList<BlockTypeContainer>();
		addUpTo(list, Material.STONE, 7);
		addAll(list, Material.GRASS);
		addUpTo(list, Material.DIRT, 3);
		addAll(list, Material.COBBLESTONE);
		addUpTo(list, Material.WOOD, 6);
		addUpTo(list, Material.SAPLING, 6);
		addAll(list, Material.BEDROCK);
		addUpTo(list, Material.SAND, 2);
		addAll(list, Material.GRAVEL, Material.GOLD_ORE, Material.IRON_ORE, Material.COAL_ORE);
		list.add(new BlockTypeContainer(Material.LOG, new BlockType(Material.LOG, (byte) 0),
				new BlockType(Material.LOG, (byte) 4), new BlockType(Material.LOG, (byte) 8),
				new BlockType(Material.LOG, (byte) 12)));
		list.add(new BlockTypeContainer(Material.LOG, (byte) 1, new BlockType(Material.LOG, (byte) 1),
				new BlockType(Material.LOG, (byte) 5), new BlockType(Material.LOG, (byte) 9),
				new BlockType(Material.LOG, (byte) 13)));
		list.add(new BlockTypeContainer(Material.LOG, (byte) 2, new BlockType(Material.LOG, (byte) 2),
				new BlockType(Material.LOG, (byte) 6), new BlockType(Material.LOG, (byte) 10),
				new BlockType(Material.LOG, (byte) 14)));
		list.add(new BlockTypeContainer(Material.LOG, (byte) 3, new BlockType(Material.LOG, (byte) 3),
				new BlockType(Material.LOG, (byte) 7), new BlockType(Material.LOG, (byte) 11),
				new BlockType(Material.LOG, (byte) 15)));
		list.add(new BlockTypeContainer(Material.LOG_2, new BlockType(Material.LOG_2, (byte) 0),
				new BlockType(Material.LOG_2, (byte) 4), new BlockType(Material.LOG_2, (byte) 8),
				new BlockType(Material.LOG_2, (byte) 12)));
		list.add(new BlockTypeContainer(Material.LOG_2, (byte) 1, new BlockType(Material.LOG_2, (byte) 1),
				new BlockType(Material.LOG_2, (byte) 5), new BlockType(Material.LOG_2, (byte) 9),
				new BlockType(Material.LOG_2, (byte) 13)));
		list.add(new BlockTypeContainer(Material.LEAVES, new BlockType(Material.LEAVES, (byte) 0),
				new BlockType(Material.LEAVES, (byte) 4), new BlockType(Material.LEAVES, (byte) 8),
				new BlockType(Material.LEAVES, (byte) 12)));
		list.add(new BlockTypeContainer(Material.LEAVES, (byte) 1, new BlockType(Material.LEAVES, (byte) 1),
				new BlockType(Material.LEAVES, (byte) 5), new BlockType(Material.LEAVES, (byte) 9),
				new BlockType(Material.LEAVES, (byte) 13)));
		list.add(new BlockTypeContainer(Material.LEAVES, (byte) 2, new BlockType(Material.LEAVES, (byte) 2),
				new BlockType(Material.LEAVES, (byte) 6), new BlockType(Material.LEAVES, (byte) 10),
				new BlockType(Material.LEAVES, (byte) 14)));
		list.add(new BlockTypeContainer(Material.LEAVES, (byte) 3, new BlockType(Material.LEAVES, (byte) 3),
				new BlockType(Material.LEAVES, (byte) 7), new BlockType(Material.LEAVES, (byte) 11),
				new BlockType(Material.LEAVES, (byte) 15)));
		list.add(new BlockTypeContainer(Material.LEAVES_2, new BlockType(Material.LEAVES_2, (byte) 0),
				new BlockType(Material.LEAVES_2, (byte) 4), new BlockType(Material.LEAVES_2, (byte) 8),
				new BlockType(Material.LEAVES_2, (byte) 12)));
		list.add(new BlockTypeContainer(Material.LEAVES_2, (byte) 1, new BlockType(Material.LEAVES_2, (byte) 1),
				new BlockType(Material.LEAVES_2, (byte) 5), new BlockType(Material.LEAVES_2, (byte) 9),
				new BlockType(Material.LEAVES_2, (byte) 13)));
		addAll(list, Material.SPONGE);
		list.add(new BlockTypeContainer(Material.SPONGE, (byte) 1));
		addAll(list, Material.GLASS, Material.LAPIS_ORE, Material.LAPIS_BLOCK, Material.DISPENSER);
		addUpTo(list, Material.SANDSTONE, 2);
		addAll(list, Material.NOTE_BLOCK);
		list.add(new BlockTypeContainer(Material.BED, new BlockType(Material.BED_BLOCK, null)));
		addAll(list, Material.POWERED_RAIL, Material.DETECTOR_RAIL, Material.PISTON_STICKY_BASE, Material.WEB);
		addUpTo(list, Material.LONG_GRASS, 3);
		addAll(list, Material.DEAD_BUSH, Material.PISTON_BASE);
		addUpTo(list, Material.WOOL, 16);
		addAll(list, Material.YELLOW_FLOWER);
		addUpTo(list, Material.RED_ROSE, 9);
		addAll(list, Material.BROWN_MUSHROOM, Material.RED_MUSHROOM, Material.GOLD_BLOCK, Material.IRON_BLOCK,
				Material.BRICK, Material.TNT, Material.BOOKSHELF, Material.MOSSY_COBBLESTONE, Material.OBSIDIAN,
				Material.TORCH, Material.MOB_SPAWNER, Material.CHEST);
		list.add(new BlockTypeContainer(Material.REDSTONE, new BlockType(Material.REDSTONE_WIRE, null)));
		addAll(list, Material.DIAMOND_ORE, Material.DIAMOND_BLOCK, Material.WORKBENCH);
		list.add(new BlockTypeContainer(Material.WHEAT, new BlockType(Material.CROPS, null)));
		list.add(new BlockTypeContainer(Material.FURNACE, new BlockType(Material.FURNACE, null),
				new BlockType(Material.BURNING_FURNACE, null)));
		list.add(new BlockTypeContainer(Material.SIGN, new BlockType(Material.SIGN_POST, null),
				new BlockType(Material.WALL_SIGN, null)));
		list.add(new BlockTypeContainer(Material.WOOD_DOOR, new BlockType(Material.WOODEN_DOOR, null)));
		addAll(list, Material.LADDER, Material.RAILS, Material.LEVER, Material.STONE_PLATE);
		list.add(new BlockTypeContainer(Material.IRON_DOOR, new BlockType(Material.IRON_DOOR_BLOCK, null)));
		addAll(list, Material.WOOD_PLATE);
		list.add(new BlockTypeContainer(Material.REDSTONE_ORE, new BlockType(Material.REDSTONE_ORE, null),
				new BlockType(Material.GLOWING_REDSTONE_ORE, null)));
		list.add(new BlockTypeContainer(Material.REDSTONE_TORCH_ON, new BlockType(Material.REDSTONE_TORCH_ON, null),
				new BlockType(Material.REDSTONE_TORCH_OFF, null)));
		addAll(list, Material.STONE_BUTTON, Material.SNOW, Material.ICE, Material.SNOW_BLOCK, Material.CACTUS,
				Material.CLAY);
		list.add(new BlockTypeContainer(Material.SUGAR_CANE, new BlockType(Material.SUGAR_CANE_BLOCK, null)));
		addAll(list, Material.JUKEBOX, Material.FENCE, Material.PUMPKIN, Material.NETHERRACK, Material.SOUL_SAND,
				Material.GLOWSTONE, Material.JACK_O_LANTERN);
		list.add(new BlockTypeContainer(Material.CAKE, new BlockType(Material.CAKE_BLOCK, null)));
		addUpTo(list, Material.STAINED_GLASS, 16);
		addAll(list, Material.TRAP_DOOR);
		addUpTo(list, Material.MONSTER_EGGS, 6);
		addAll(list, Material.SMOOTH_BRICK, Material.HUGE_MUSHROOM_1, Material.HUGE_MUSHROOM_2, Material.IRON_FENCE,
				Material.THIN_GLASS, Material.MELON_BLOCK);
		list.add(new BlockTypeContainer(Material.MELON_SEEDS, new BlockType(Material.MELON_STEM, null)));
		addAll(list, Material.VINE, Material.MYCEL, Material.WATER_LILY, Material.NETHER_BRICK, Material.NETHER_FENCE);
		list.add(new BlockTypeContainer(Material.NETHER_WART_BLOCK, new BlockType(Material.NETHER_WARTS, null)));
		addAll(list, Material.ENCHANTMENT_TABLE);
		list.add(new BlockTypeContainer(Material.BREWING_STAND_ITEM, new BlockType(Material.BREWING_STAND, null)));
		list.add(new BlockTypeContainer(Material.CAULDRON_ITEM, new BlockType(Material.CAULDRON, null)));
		addAll(list, Material.ENDER_PORTAL_FRAME, Material.ENDER_STONE, Material.DRAGON_EGG);
		list.add(new BlockTypeContainer(Material.REDSTONE_LAMP_OFF, new BlockType(Material.REDSTONE_LAMP_OFF, null),
				new BlockType(Material.REDSTONE_LAMP_ON, null)));
		list.add(new BlockTypeContainer(Material.INK_SACK, (byte) 3, new BlockType(Material.COCOA, null)));
		addAll(list, Material.EMERALD_ORE, Material.ENDER_CHEST, Material.TRIPWIRE_HOOK, Material.EMERALD_BLOCK,
				Material.BEACON, Material.COBBLE_WALL);
		list.add(new BlockTypeContainer(Material.FLOWER_POT_ITEM, new BlockType(Material.FLOWER_POT, null)));
		list.add(new BlockTypeContainer(Material.CARROT_ITEM, new BlockType(Material.CARROT, null)));
		list.add(new BlockTypeContainer(Material.POTATO_ITEM, new BlockType(Material.POTATO, null)));
		list.add(new BlockTypeContainer(Material.SKULL_ITEM, (byte) 0, new BlockType(Material.SKULL, null)));
		list.add(new BlockTypeContainer(Material.SKULL_ITEM, (byte) 1, new BlockType(Material.SKULL, null)));
		list.add(new BlockTypeContainer(Material.SKULL_ITEM, (byte) 2, new BlockType(Material.SKULL, null)));
		list.add(new BlockTypeContainer(Material.SKULL_ITEM, (byte) 3, new BlockType(Material.SKULL, null)));
		list.add(new BlockTypeContainer(Material.SKULL_ITEM, (byte) 4, new BlockType(Material.SKULL, null)));
		list.add(new BlockTypeContainer(Material.SKULL_ITEM, (byte) 5, new BlockType(Material.SKULL, null)));
		addAll(list, Material.ANVIL, Material.TRAPPED_CHEST, Material.GOLD_PLATE, Material.IRON_PLATE,
				Material.REDSTONE_BLOCK, Material.QUARTZ_ORE, Material.HOPPER);
		list.add(new BlockTypeContainer(Material.QUARTZ_BLOCK, (byte) 0));
		list.add(new BlockTypeContainer(Material.QUARTZ_BLOCK, (byte) 1));
		list.add(new BlockTypeContainer(Material.QUARTZ_BLOCK, (byte) 2, new BlockType(Material.QUARTZ_BLOCK, (byte) 2),
				new BlockType(Material.QUARTZ_BLOCK, (byte) 3), new BlockType(Material.QUARTZ_BLOCK, (byte) 4)));
		addAll(list, Material.ACTIVATOR_RAIL, Material.DROPPER);
		addUpTo(list, Material.STAINED_CLAY, 16);
		addUpTo(list, Material.STAINED_GLASS_PANE, 16);
		addAll(list, Material.SLIME_BLOCK, Material.BARRIER, Material.IRON_TRAPDOOR);
		addUpTo(list, Material.PRISMARINE, 3);
		addAll(list, Material.SEA_LANTERN, Material.HAY_BLOCK);
		addUpTo(list, Material.CARPET, 16);
		addAll(list, Material.HARD_CLAY, Material.COAL_BLOCK, Material.PACKED_ICE);
		addUpTo(list, Material.DOUBLE_PLANT, 6);
		list.add(new BlockTypeContainer(Material.BANNER, new BlockType(Material.WALL_BANNER, null),
				new BlockType(Material.STANDING_BANNER, null)));
		addUpTo(list, Material.RED_SANDSTONE, 3);
		addAll(list, Material.SPRUCE_FENCE, Material.BIRCH_FENCE, Material.PACKED_ICE, Material.DARK_OAK_FENCE,
				Material.ACACIA_FENCE);
		list.add(new BlockTypeContainer(Material.SPRUCE_DOOR_ITEM, new BlockType(Material.SPRUCE_DOOR, null)));
		list.add(new BlockTypeContainer(Material.BIRCH_DOOR_ITEM, new BlockType(Material.BIRCH_DOOR, null)));
		list.add(new BlockTypeContainer(Material.JUNGLE_DOOR_ITEM, new BlockType(Material.JUNGLE_DOOR, null)));
		list.add(new BlockTypeContainer(Material.ACACIA_DOOR_ITEM, new BlockType(Material.ACACIA_DOOR, null)));
		list.add(new BlockTypeContainer(Material.DARK_OAK_DOOR_ITEM, new BlockType(Material.DARK_OAK_DOOR, null)));
		addAll(list, Material.END_ROD, Material.CHORUS_PLANT, Material.CHORUS_FLOWER, Material.PURPUR_BLOCK,
				Material.PURPUR_PILLAR, Material.END_BRICKS);
		list.add(new BlockTypeContainer(Material.BEETROOT, new BlockType(Material.BEETROOT_BLOCK, null)));
		addAll(list, Material.MAGMA, Material.RED_NETHER_BRICK, Material.BONE_BLOCK, Material.OBSERVER,
				Material.WHITE_SHULKER_BOX, Material.ORANGE_SHULKER_BOX, Material.MAGENTA_SHULKER_BOX,
				Material.LIGHT_BLUE_SHULKER_BOX, Material.YELLOW_SHULKER_BOX, Material.LIME_SHULKER_BOX,
				Material.PINK_SHULKER_BOX, Material.GRAY_SHULKER_BOX, Material.SILVER_SHULKER_BOX,
				Material.CYAN_SHULKER_BOX, Material.PURPLE_SHULKER_BOX, Material.BLUE_SHULKER_BOX,
				Material.BROWN_SHULKER_BOX, Material.GREEN_SHULKER_BOX, Material.RED_SHULKER_BOX,
				Material.BLACK_SHULKER_BOX, Material.WHITE_GLAZED_TERRACOTTA, Material.ORANGE_GLAZED_TERRACOTTA,
				Material.MAGENTA_GLAZED_TERRACOTTA, Material.LIGHT_BLUE_GLAZED_TERRACOTTA,
				Material.YELLOW_GLAZED_TERRACOTTA, Material.LIME_GLAZED_TERRACOTTA, Material.PINK_GLAZED_TERRACOTTA,
				Material.GRAY_GLAZED_TERRACOTTA, Material.SILVER_GLAZED_TERRACOTTA, Material.CYAN_GLAZED_TERRACOTTA,
				Material.PURPLE_GLAZED_TERRACOTTA, Material.BLUE_GLAZED_TERRACOTTA, Material.BROWN_GLAZED_TERRACOTTA,
				Material.GREEN_GLAZED_TERRACOTTA, Material.RED_GLAZED_TERRACOTTA, Material.BLACK_GLAZED_TERRACOTTA);
		addUpTo(list, Material.CONCRETE, 16);
		addUpTo(list, Material.CONCRETE_POWDER, 16);
		return list;
	}
}

class BlockType {
	private final Material material;
	private final Byte data;

	BlockType(Material type, Byte data) {
		if (type == null)
			throw new NullPointerException();
		this.material = type;
		this.data = data;
	}

	public String toString() {
		if (data == null)
			return material.toString();
		else
			return material.toString() + ":" + data;
	}

	public Byte getData() {
		return data;
	}

	public Material getType() {
		return material;
	}
}

class BlockTypeContainer {
	private ItemStack displayItem;
	private List<BlockType> types = new ArrayList<BlockType>();

	public ItemStack getDisplayItem() {
		return displayItem;
	}

	public List<BlockType> getBlockTypeList() {
		return types;
	}

	BlockTypeContainer(Material material) {
		this(material, (Byte) null);
	}

	BlockTypeContainer(Material material, Byte data) {
		this(material,data,new BlockType(material, (Byte) data));
	}

	BlockTypeContainer(Material material, BlockType... blockTypes) {
		this(material, null, blockTypes);
	}

	BlockTypeContainer(Material material, Byte data, BlockType... blockTypes) {
		if (blockTypes == null || blockTypes.length == 0)
			throw new NullPointerException();
		for (BlockType type : blockTypes)
			types.add(type);
		if (data==null)
			displayItem = new ItemBuilder(material).setGuiProperty().build();
		else
			displayItem = new ItemBuilder(material).setGuiProperty().setDamage(data).build();
	}
}