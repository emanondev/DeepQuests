package emanondev.quests.task;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import emanondev.quests.gui.CustomButton;
import emanondev.quests.gui.CustomGui;
import emanondev.quests.gui.CustomMultiPageGui;
import emanondev.quests.gui.EditorButtonFactory;
import emanondev.quests.utils.StringUtils;

public class BlockCheckerV1_12 implements BlockChecker{
	private final BlocksTaskInfo parent;
	BlockCheckerV1_12(List<String> list,BlocksTaskInfo parent){
		this.parent = parent;
		boolean dirty = false;
		if (list!=null) {
			for (String block : list) {
				if (block == null || block.isEmpty())
					continue;
				block = block.toUpperCase();
				if (block.contains(":")) {
					try {
						if (block.split(":").length>2) {
							new IllegalArgumentException("Invalid text for Material type '"+block+"'").printStackTrace();
							dirty = true;
							continue;
						}
						Material.valueOf(block.split(":")[0]);
					} catch (Exception e) {
						new IllegalArgumentException("Can't recognize '"+block+"' Material").printStackTrace();
						dirty = true;
						continue;
					}
					try {
						if (Integer.valueOf(block.split(":")[1])>=16) {
							new IllegalArgumentException("Invalid Data on Material '"+block+"'").printStackTrace();
							dirty = true;
							continue;
						}
					} catch (Exception e) {
						new IllegalArgumentException("Invalid Data on Material '"+block+"'").printStackTrace();
						dirty = true;
						continue;
					}
				}
				else {
					try {
						Material.valueOf(block.split(":")[0]);
					} catch (Exception e) {
						new IllegalArgumentException("Can't recognize '"+block+"' Material").printStackTrace();
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
		if (hasBlock(block.getType().toString()+":"+block.getData()))
			return true;
		return false;
	}
	private boolean hasBlock(String block) {
		return blockSet.contains(block);
	}
	
	
	private boolean addBlock(ArrayList<BlockType> block) {
		if (block == null || block.isEmpty())
			return false;
		boolean dirty = false;
		for (BlockType type : block) {
			if (type !=null)
				dirty =  blockSet.add(type.toString()) || dirty;
		}
		if (dirty)
			parent.setBlocksList(new ArrayList<String>(blockSet));
		return dirty;
	}
	private boolean removeBlock(ArrayList<BlockType> block) {
		if (block == null || block.isEmpty())
			return false;
		boolean dirty = false;
		for (BlockType type : block) {
			if (type !=null)
				dirty = blockSet.remove(type.toString()) || dirty;
		}
		if (dirty)
			parent.setBlocksList(new ArrayList<String>(blockSet));
		return dirty;
	}
	@Override
	public EditorButtonFactory getBlocksSelectorButtonFactory() {
		return new BlockSelectorButtonFactory();
	}
	private class BlockSelectorButtonFactory implements EditorButtonFactory {
		private class BlockSelectorButton extends CustomButton {
			private ItemStack item = new ItemStack(Material.STONE);
			public BlockSelectorButton(CustomGui parent) {
				super(parent);
				update();
			}
			public void update() {
				ArrayList<String> desc = new ArrayList<String>();
				desc.add("&6&lBlock Type Selector");
				desc.add("&6Click to edit");
				if (blockSet.isEmpty())
					desc.add("&7No blocks restrictions are set");
				else {
					desc.add("&7All listed blocks are &aAllowed");
					for (String block : blockSet)
						desc.add(" &7- &a"+block);
				}
				StringUtils.setDescription(item, desc);
			}
			@Override
			public ItemStack getItem() {
				return item;
			}

			@Override
			public void onClick(Player clicker, ClickType click) {
				clicker.openInventory(new BlockSelectorGui(clicker).getInventory());
			}
			private class BlockSelectorGui extends CustomMultiPageGui<MaterialButton> {
				public BlockSelectorGui(Player p) {
					super(p, BlockSelectorButton.this.getParent(), 6, 1);
					this.setFromEndCloseButtonPosition(8);
					this.setTitle(null,StringUtils.fixColorsAndHolders("&8Block Selector"));
					addUpTo(Material.STONE,7);
					this.addButton(new MaterialButton(this,Material.GRASS));
					addUpTo(Material.DIRT,3);
					this.addButton(new MaterialButton(this,Material.COBBLESTONE));
					addUpTo(Material.WOOD,6);
					addUpTo(Material.SAPLING,6);
					this.addButton(new MaterialButton(this,Material.BEDROCK));
					addUpTo(Material.SAND,2);
					this.addButton(new MaterialButton(this,Material.GRAVEL));
					this.addButton(new MaterialButton(this,Material.GOLD_ORE));
					this.addButton(new MaterialButton(this,Material.IRON_ORE));
					this.addButton(new MaterialButton(this,Material.COAL_ORE));
					this.addButton(new MaterialButton(this,Material.LOG,
							new BlockType(Material.LOG,(byte) 0),
							new BlockType(Material.LOG,(byte) 4),
							new BlockType(Material.LOG,(byte) 8),
							new BlockType(Material.LOG,(byte) 12)));
					this.addButton(new MaterialButton(this,Material.LOG,(byte) 1,
							new BlockType(Material.LOG,(byte) 1),
							new BlockType(Material.LOG,(byte) 5),
							new BlockType(Material.LOG,(byte) 9),
							new BlockType(Material.LOG,(byte) 13)));
					this.addButton(new MaterialButton(this,Material.LOG,(byte) 2,
							new BlockType(Material.LOG,(byte) 2),
							new BlockType(Material.LOG,(byte) 6),
							new BlockType(Material.LOG,(byte) 10),
							new BlockType(Material.LOG,(byte) 14)));
					this.addButton(new MaterialButton(this,Material.LOG,(byte) 3,
							new BlockType(Material.LOG,(byte) 3),
							new BlockType(Material.LOG,(byte) 7),
							new BlockType(Material.LOG,(byte) 11),
							new BlockType(Material.LOG,(byte) 15)));
					this.addButton(new MaterialButton(this,Material.LOG_2,
							new BlockType(Material.LOG_2,(byte) 0),
							new BlockType(Material.LOG_2,(byte) 4),
							new BlockType(Material.LOG_2,(byte) 8),
							new BlockType(Material.LOG_2,(byte) 12)));
					this.addButton(new MaterialButton(this,Material.LOG_2,(byte) 1,
							new BlockType(Material.LOG_2,(byte) 1),
							new BlockType(Material.LOG_2,(byte) 5),
							new BlockType(Material.LOG_2,(byte) 9),
							new BlockType(Material.LOG_2,(byte) 13)));
					this.addButton(new MaterialButton(this,Material.LEAVES,
							new BlockType(Material.LEAVES,(byte) 0),
							new BlockType(Material.LEAVES,(byte) 4),
							new BlockType(Material.LEAVES,(byte) 8),
							new BlockType(Material.LEAVES,(byte) 12)));
					this.addButton(new MaterialButton(this,Material.LEAVES,(byte) 1,
							new BlockType(Material.LEAVES,(byte) 1),
							new BlockType(Material.LEAVES,(byte) 5),
							new BlockType(Material.LEAVES,(byte) 9),
							new BlockType(Material.LEAVES,(byte) 13)));
					this.addButton(new MaterialButton(this,Material.LEAVES,(byte) 2,
							new BlockType(Material.LEAVES,(byte) 2),
							new BlockType(Material.LEAVES,(byte) 6),
							new BlockType(Material.LEAVES,(byte) 10),
							new BlockType(Material.LEAVES,(byte) 14)));
					this.addButton(new MaterialButton(this,Material.LEAVES,(byte) 3,
							new BlockType(Material.LEAVES,(byte) 3),
							new BlockType(Material.LEAVES,(byte) 7),
							new BlockType(Material.LEAVES,(byte) 11),
							new BlockType(Material.LEAVES,(byte) 15)));
					this.addButton(new MaterialButton(this,Material.LEAVES_2,
							new BlockType(Material.LEAVES_2,(byte) 0),
							new BlockType(Material.LEAVES_2,(byte) 4),
							new BlockType(Material.LEAVES_2,(byte) 8),
							new BlockType(Material.LEAVES_2,(byte) 12)));
					this.addButton(new MaterialButton(this,Material.LEAVES_2,(byte) 1,
							new BlockType(Material.LEAVES_2,(byte) 1),
							new BlockType(Material.LEAVES_2,(byte) 5),
							new BlockType(Material.LEAVES_2,(byte) 9),
							new BlockType(Material.LEAVES_2,(byte) 13)));
					this.addButton(new MaterialButton(this,Material.SPONGE));
					this.addButton(new MaterialButton(this,Material.SPONGE,(byte) 1));
					this.addButton(new MaterialButton(this,Material.GLASS));
					this.addButton(new MaterialButton(this,Material.LAPIS_ORE));
					this.addButton(new MaterialButton(this,Material.LAPIS_BLOCK));
					this.addButton(new MaterialButton(this,Material.DISPENSER));
					this.addButton(new MaterialButton(this,Material.SANDSTONE));
					this.addButton(new MaterialButton(this,Material.SANDSTONE,(byte) 1));
					this.addButton(new MaterialButton(this,Material.SANDSTONE,(byte) 2));
					this.addButton(new MaterialButton(this,Material.NOTE_BLOCK));
					this.addButton(new MaterialButton(this,Material.BED,
							new BlockType(Material.BED_BLOCK,null)));
					this.addButton(new MaterialButton(this,Material.POWERED_RAIL));
					this.addButton(new MaterialButton(this,Material.DETECTOR_RAIL));
					this.addButton(new MaterialButton(this,Material.PISTON_STICKY_BASE));
					this.addButton(new MaterialButton(this,Material.WEB,(byte) 0));
					addUpTo(Material.LONG_GRASS,3);
					this.addButton(new MaterialButton(this,Material.DEAD_BUSH));
					this.addButton(new MaterialButton(this,Material.PISTON_BASE));
					addUpTo(Material.WOOL,16);
					this.addButton(new MaterialButton(this,Material.YELLOW_FLOWER));
					addUpTo(Material.RED_ROSE,9);
					this.addButton(new MaterialButton(this,Material.BROWN_MUSHROOM));
					this.addButton(new MaterialButton(this,Material.RED_MUSHROOM));
					this.addButton(new MaterialButton(this,Material.GOLD_BLOCK));
					this.addButton(new MaterialButton(this,Material.IRON_BLOCK));
					this.addButton(new MaterialButton(this,Material.BRICK));
					this.addButton(new MaterialButton(this,Material.TNT));
					this.addButton(new MaterialButton(this,Material.BOOKSHELF));
					this.addButton(new MaterialButton(this,Material.MOSSY_COBBLESTONE));
					this.addButton(new MaterialButton(this,Material.OBSIDIAN));
					this.addButton(new MaterialButton(this,Material.TORCH));
					this.addButton(new MaterialButton(this,Material.MOB_SPAWNER));
					this.addButton(new MaterialButton(this,Material.CHEST));
					this.addButton(new MaterialButton(this,Material.REDSTONE,
							new BlockType(Material.REDSTONE_WIRE,null)));
					this.addButton(new MaterialButton(this,Material.DIAMOND_ORE));
					this.addButton(new MaterialButton(this,Material.DIAMOND_BLOCK));
					this.addButton(new MaterialButton(this,Material.WORKBENCH));
					this.addButton(new MaterialButton(this,Material.WHEAT,
							new BlockType(Material.CROPS,null)));
					this.addButton(new MaterialButton(this,Material.FURNACE,
							new BlockType(Material.FURNACE,null),
							new BlockType(Material.BURNING_FURNACE,null)));
					this.addButton(new MaterialButton(this,Material.SIGN,
							new BlockType(Material.SIGN_POST,null),
							new BlockType(Material.WALL_SIGN,null)));
					this.addButton(new MaterialButton(this,Material.WOOD_DOOR,
							new BlockType(Material.WOODEN_DOOR,null)));
					this.addButton(new MaterialButton(this,Material.LADDER));
					this.addButton(new MaterialButton(this,Material.RAILS));
					this.addButton(new MaterialButton(this,Material.LEVER));
					this.addButton(new MaterialButton(this,Material.STONE_PLATE));
					this.addButton(new MaterialButton(this,Material.IRON_DOOR,
							new BlockType(Material.IRON_DOOR_BLOCK,null)));
					this.addButton(new MaterialButton(this,Material.WOOD_PLATE));
					this.addButton(new MaterialButton(this,Material.REDSTONE_ORE,
							new BlockType(Material.REDSTONE_ORE,null),
							new BlockType(Material.GLOWING_REDSTONE_ORE,null)));
					this.addButton(new MaterialButton(this,Material.REDSTONE_TORCH_ON,
							new BlockType(Material.REDSTONE_TORCH_ON,null),
							new BlockType(Material.REDSTONE_TORCH_OFF,null)));
					this.addButton(new MaterialButton(this,Material.STONE_BUTTON));
					this.addButton(new MaterialButton(this,Material.SNOW));
					this.addButton(new MaterialButton(this,Material.ICE));
					this.addButton(new MaterialButton(this,Material.SNOW_BLOCK));
					this.addButton(new MaterialButton(this,Material.CACTUS));
					this.addButton(new MaterialButton(this,Material.CLAY));
					this.addButton(new MaterialButton(this,Material.SUGAR_CANE,
							new BlockType(Material.SUGAR_CANE_BLOCK,null)));
					this.addButton(new MaterialButton(this,Material.JUKEBOX));
					this.addButton(new MaterialButton(this,Material.FENCE));
					this.addButton(new MaterialButton(this,Material.PUMPKIN));
					this.addButton(new MaterialButton(this,Material.NETHERRACK));
					this.addButton(new MaterialButton(this,Material.SOUL_SAND));
					this.addButton(new MaterialButton(this,Material.GLOWSTONE));
					this.addButton(new MaterialButton(this,Material.JACK_O_LANTERN));
					this.addButton(new MaterialButton(this,Material.CAKE,
							new BlockType(Material.CAKE_BLOCK,null)));
					addUpTo(Material.STAINED_GLASS,16);
					this.addButton(new MaterialButton(this,Material.TRAP_DOOR));
					addUpTo(Material.MONSTER_EGGS,6);
					this.addButton(new MaterialButton(this,Material.SMOOTH_BRICK));
					this.addButton(new MaterialButton(this,Material.HUGE_MUSHROOM_1));
					this.addButton(new MaterialButton(this,Material.HUGE_MUSHROOM_2));
					this.addButton(new MaterialButton(this,Material.IRON_FENCE));
					this.addButton(new MaterialButton(this,Material.THIN_GLASS));
					this.addButton(new MaterialButton(this,Material.MELON_BLOCK));
					this.addButton(new MaterialButton(this,Material.MELON_SEEDS,
							new BlockType(Material.MELON_STEM,null)));
					this.addButton(new MaterialButton(this,Material.VINE));
					this.addButton(new MaterialButton(this,Material.MYCEL));
					this.addButton(new MaterialButton(this,Material.WATER_LILY));
					this.addButton(new MaterialButton(this,Material.NETHER_BRICK));
					this.addButton(new MaterialButton(this,Material.NETHER_FENCE));
					this.addButton(new MaterialButton(this,Material.NETHER_WART_BLOCK,
							new BlockType(Material.NETHER_WARTS,null)));
					this.addButton(new MaterialButton(this,Material.ENCHANTMENT_TABLE));
					this.addButton(new MaterialButton(this,Material.BREWING_STAND_ITEM,
							new BlockType(Material.BREWING_STAND,null)));
					this.addButton(new MaterialButton(this,Material.CAULDRON_ITEM,
							new BlockType(Material.CAULDRON,null)));
					this.addButton(new MaterialButton(this,Material.ENDER_PORTAL_FRAME));
					this.addButton(new MaterialButton(this,Material.ENDER_STONE));
					this.addButton(new MaterialButton(this,Material.DRAGON_EGG));
					this.addButton(new MaterialButton(this,Material.REDSTONE_LAMP_OFF,
							new BlockType(Material.REDSTONE_LAMP_OFF,null),
							new BlockType(Material.REDSTONE_LAMP_ON,null)));
					this.addButton(new MaterialButton(this,Material.INK_SACK,(byte) 3,
							new BlockType(Material.COCOA,null)));
					this.addButton(new MaterialButton(this,Material.EMERALD_ORE));
					this.addButton(new MaterialButton(this,Material.ENDER_CHEST));
					this.addButton(new MaterialButton(this,Material.TRIPWIRE_HOOK));
					this.addButton(new MaterialButton(this,Material.EMERALD_BLOCK));
					this.addButton(new MaterialButton(this,Material.BEACON));
					this.addButton(new MaterialButton(this,Material.COBBLE_WALL));
					this.addButton(new MaterialButton(this,Material.FLOWER_POT_ITEM,
							new BlockType(Material.FLOWER_POT,null)));
					this.addButton(new MaterialButton(this,Material.CARROT_ITEM,
							new BlockType(Material.CARROT,null)));
					this.addButton(new MaterialButton(this,Material.POTATO_ITEM,
							new BlockType(Material.POTATO,null)));
					this.addButton(new MaterialButton(this,Material.SKULL_ITEM,(byte) 0,
							new BlockType(Material.SKULL,null)));
					this.addButton(new MaterialButton(this,Material.SKULL_ITEM,(byte) 1,
							new BlockType(Material.SKULL,null)));
					this.addButton(new MaterialButton(this,Material.SKULL_ITEM,(byte) 2,
							new BlockType(Material.SKULL,null)));
					this.addButton(new MaterialButton(this,Material.SKULL_ITEM,(byte) 3,
							new BlockType(Material.SKULL,null)));
					this.addButton(new MaterialButton(this,Material.SKULL_ITEM,(byte) 4,
							new BlockType(Material.SKULL,null)));
					this.addButton(new MaterialButton(this,Material.SKULL_ITEM,(byte) 5,
							new BlockType(Material.SKULL,null)));
					this.addButton(new MaterialButton(this,Material.ANVIL));
					this.addButton(new MaterialButton(this,Material.TRAPPED_CHEST));
					this.addButton(new MaterialButton(this,Material.GOLD_PLATE));
					this.addButton(new MaterialButton(this,Material.IRON_PLATE));
					this.addButton(new MaterialButton(this,Material.REDSTONE_BLOCK));
					this.addButton(new MaterialButton(this,Material.QUARTZ_ORE));
					this.addButton(new MaterialButton(this,Material.HOPPER));
					this.addButton(new MaterialButton(this,Material.QUARTZ_BLOCK,(byte) 0));
					this.addButton(new MaterialButton(this,Material.QUARTZ_BLOCK,(byte) 1));
					this.addButton(new MaterialButton(this,Material.QUARTZ_BLOCK,(byte) 2,
							new BlockType(Material.QUARTZ_BLOCK,(byte) 2),
							new BlockType(Material.QUARTZ_BLOCK,(byte) 3),
							new BlockType(Material.QUARTZ_BLOCK,(byte) 4)));
					this.addButton(new MaterialButton(this,Material.ACTIVATOR_RAIL));
					this.addButton(new MaterialButton(this,Material.DROPPER));
					addUpTo(Material.STAINED_CLAY,16);
					addUpTo(Material.STAINED_GLASS_PANE,16);
					this.addButton(new MaterialButton(this,Material.SLIME_BLOCK));
					this.addButton(new MaterialButton(this,Material.BARRIER));
					this.addButton(new MaterialButton(this,Material.IRON_TRAPDOOR));
					addUpTo(Material.PRISMARINE,3);
					this.addButton(new MaterialButton(this,Material.SEA_LANTERN));
					this.addButton(new MaterialButton(this,Material.HAY_BLOCK));
					addUpTo(Material.CARPET,16);
					this.addButton(new MaterialButton(this,Material.HARD_CLAY));
					this.addButton(new MaterialButton(this,Material.COAL_BLOCK));
					this.addButton(new MaterialButton(this,Material.PACKED_ICE));
					addUpTo(Material.DOUBLE_PLANT,6);
					this.addButton(new MaterialButton(this,Material.BANNER,
							new BlockType(Material.WALL_BANNER ,null),
							new BlockType(Material.STANDING_BANNER ,null)));
					addUpTo(Material.RED_SANDSTONE,3);
					this.addButton(new MaterialButton(this,Material.SPRUCE_FENCE));
					this.addButton(new MaterialButton(this,Material.BIRCH_FENCE));
					this.addButton(new MaterialButton(this,Material.PACKED_ICE));
					this.addButton(new MaterialButton(this,Material.DARK_OAK_FENCE));
					this.addButton(new MaterialButton(this,Material.ACACIA_FENCE));
					this.addButton(new MaterialButton(this,Material.SPRUCE_DOOR_ITEM,
							new BlockType(Material.SPRUCE_DOOR,null)));
					this.addButton(new MaterialButton(this,Material.BIRCH_DOOR_ITEM,
							new BlockType(Material.BIRCH_DOOR,null)));
					this.addButton(new MaterialButton(this,Material.JUNGLE_DOOR_ITEM,
							new BlockType(Material.JUNGLE_DOOR,null)));
					this.addButton(new MaterialButton(this,Material.ACACIA_DOOR_ITEM,
							new BlockType(Material.ACACIA_DOOR,null)));
					this.addButton(new MaterialButton(this,Material.DARK_OAK_DOOR_ITEM,
							new BlockType(Material.DARK_OAK_DOOR,null)));
					this.addButton(new MaterialButton(this,Material.END_ROD));
					this.addButton(new MaterialButton(this,Material.CHORUS_PLANT));
					this.addButton(new MaterialButton(this,Material.CHORUS_FLOWER));
					this.addButton(new MaterialButton(this,Material.PURPUR_BLOCK));
					this.addButton(new MaterialButton(this,Material.PURPUR_PILLAR));
					this.addButton(new MaterialButton(this,Material.END_BRICKS));
					this.addButton(new MaterialButton(this,Material.BEETROOT,
							new BlockType(Material.BEETROOT_BLOCK,null)));
					this.addButton(new MaterialButton(this,Material.MAGMA));
					this.addButton(new MaterialButton(this,Material.RED_NETHER_BRICK));
					this.addButton(new MaterialButton(this,Material.BONE_BLOCK));
					this.addButton(new MaterialButton(this,Material.OBSERVER));
					this.addButton(new MaterialButton(this,Material.WHITE_SHULKER_BOX));
					this.addButton(new MaterialButton(this,Material.ORANGE_SHULKER_BOX));
					this.addButton(new MaterialButton(this,Material.MAGENTA_SHULKER_BOX));
					this.addButton(new MaterialButton(this,Material.LIGHT_BLUE_SHULKER_BOX));
					this.addButton(new MaterialButton(this,Material.YELLOW_SHULKER_BOX));
					this.addButton(new MaterialButton(this,Material.LIME_SHULKER_BOX));
					this.addButton(new MaterialButton(this,Material.PINK_SHULKER_BOX));
					this.addButton(new MaterialButton(this,Material.GRAY_SHULKER_BOX));
					this.addButton(new MaterialButton(this,Material.SILVER_SHULKER_BOX));
					this.addButton(new MaterialButton(this,Material.CYAN_SHULKER_BOX));
					this.addButton(new MaterialButton(this,Material.PURPLE_SHULKER_BOX));
					this.addButton(new MaterialButton(this,Material.BLUE_SHULKER_BOX));
					this.addButton(new MaterialButton(this,Material.BROWN_SHULKER_BOX));
					this.addButton(new MaterialButton(this,Material.GREEN_SHULKER_BOX));
					this.addButton(new MaterialButton(this,Material.RED_SHULKER_BOX));
					this.addButton(new MaterialButton(this,Material.BLACK_SHULKER_BOX));
					this.addButton(new MaterialButton(this,Material.WHITE_GLAZED_TERRACOTTA));
					this.addButton(new MaterialButton(this,Material.ORANGE_GLAZED_TERRACOTTA));
					this.addButton(new MaterialButton(this,Material.MAGENTA_GLAZED_TERRACOTTA));
					this.addButton(new MaterialButton(this,Material.LIGHT_BLUE_GLAZED_TERRACOTTA));
					this.addButton(new MaterialButton(this,Material.YELLOW_GLAZED_TERRACOTTA));
					this.addButton(new MaterialButton(this,Material.LIME_GLAZED_TERRACOTTA));
					this.addButton(new MaterialButton(this,Material.PINK_GLAZED_TERRACOTTA));
					this.addButton(new MaterialButton(this,Material.GRAY_GLAZED_TERRACOTTA));
					this.addButton(new MaterialButton(this,Material.SILVER_GLAZED_TERRACOTTA));
					this.addButton(new MaterialButton(this,Material.CYAN_GLAZED_TERRACOTTA));
					this.addButton(new MaterialButton(this,Material.PURPLE_GLAZED_TERRACOTTA));
					this.addButton(new MaterialButton(this,Material.BLUE_GLAZED_TERRACOTTA));
					this.addButton(new MaterialButton(this,Material.BROWN_GLAZED_TERRACOTTA));
					this.addButton(new MaterialButton(this,Material.GREEN_GLAZED_TERRACOTTA));
					this.addButton(new MaterialButton(this,Material.RED_GLAZED_TERRACOTTA));
					this.addButton(new MaterialButton(this,Material.BLACK_GLAZED_TERRACOTTA));
					addUpTo(Material.CONCRETE,16);
					addUpTo(Material.CONCRETE_POWDER,16);
					reloadInventory();
				}
				
				private void addUpTo(Material mat, int max) {
					for (int i = 0; i < max ; i++)
						this.addButton(new MaterialButton(this,mat,(byte) i));
				}
				
			}
		}
		
		@Override
		public CustomButton getCustomButton(CustomGui parent) {
			return new BlockSelectorButton(parent);
		}
	}
	
	private class MaterialButton extends CustomButton {
		private ItemStack item;
		private ArrayList<BlockType> blocks = new ArrayList<BlockType>();
		public MaterialButton(CustomGui parent,Material material,BlockType... blocks) {
			this(parent,material,null,blocks);
		}
		public MaterialButton(CustomGui parent,Material material,Byte data,BlockType... blocks) {
			super(parent);
			item = new ItemStack(material);
			if (data!=null)
				item.setDurability(data);
			
			if (blocks == null || blocks.length==0)
				this.blocks.add(new BlockType(material,data));
			else {
				for (BlockType block : blocks) {
					if (block != null)
						this.blocks.add(block);
				}
			}
			update();
		}
		public void update(){
			ItemMeta meta = item.getItemMeta();
			ArrayList<String> lore = new ArrayList<String>();
			for (BlockType type : blocks) {
				if (BlockCheckerV1_12.this.hasBlock(type.toString()))
					lore.add(" &7- &a"+type.toString());
				else
					lore.add(" &7- &c"+type.toString());
			}
			meta.setLore(StringUtils.fixColorsAndHolders(lore));
			item.setItemMeta(meta);
		}
		@Override
		public ItemStack getItem() {
			return item;
		}
		@Override
		public void onClick(Player clicker, ClickType click) {
			if (!hasBlock(blocks.get(0).toString())) {
				if (BlockCheckerV1_12.this.addBlock(blocks)) {
					update();
					getParent().reloadInventory();
				}
			}
			else {
				if (BlockCheckerV1_12.this.removeBlock(blocks)) {
					update();
					getParent().reloadInventory();
				}
			}
				
		}
	}
}
class BlockType {
	private final Material material;
	private final Byte data;
	BlockType(Material type,Byte data){
		if (type == null)
			throw new NullPointerException();
		this.material = type;
		this.data = data;
	}
	public String toString() {
		if (data == null)
			return material.toString();
		else
			return material.toString()+":"+data;
	}
	public Byte getData() {
		return data;
	}
	public Material getType() {
		return material;
	}
}