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

public abstract class BlockChecherV1_13 implements BlockChecker {
	private final BlocksTaskInfo parent;
	
	BlockChecherV1_13(List<String> list, BlocksTaskInfo parent) {
		this.parent = parent;
		boolean dirty = false;
		if (list != null) {
			for (String block : list) {
				if (block == null || block.isEmpty())
					continue;
				block = block.toUpperCase();
				Material mat = null;
				if (block.contains(":")) 
					block = block.split(":")[0];
					/*
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
				} else {*/
				try {
					mat = Material.valueOf(block.split(":")[0]);
				} catch (Exception e) {
					new IllegalArgumentException("Can't recognize '" + block + "' Material").printStackTrace();
					dirty = true;
					continue;
				}
				if (mat!=null)
					blockSet.add(mat);
			}
		}
		if (dirty) {
			List<String> mylist = new ArrayList<String>();
			for (Material mat:blockSet) {
				mylist.add(mat.toString());
			}
			this.parent.setBlocksList(mylist);
		}
	}

	//private final HashSet<String> blockSet = new HashSet<String>();
	private final HashSet<Material> blockSet = new HashSet<Material>();

	@SuppressWarnings("deprecation")
	@Override
	public boolean isValidBlock(Block block) {
		if (blockSet.isEmpty())
			return true;
		if (hasMaterial(block.getType()))
			return true;
		return false;
	}

	/*
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
	}*/

	@Override
	public Button getBlockSelectorButton(Gui parent) {
		return new BlockSelectorButton(parent);
	}

	private class BlockSelectorButton extends CollectionElementsSelectorButton<Material> {

		public BlockSelectorButton(Gui parent) {
			super("&8Block Selector", new ItemBuilder(Material.STONE).setGuiProperty().build(), parent,
					getAllowedMaterials(), false);
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
				for (Material block : blockSet)
					desc.add(" &7- &a" + block.toString());
			}
			return desc;
		}

		@Override
		public List<String> getElementDescription(Material element) {
			List<String> desc = new ArrayList<String>();
			if (hasMaterial(element))
				desc.add(" &7- &a" + element.toString());
			else
				desc.add(" &7- &c" + element.toString());
			
			return desc;
		}

		@Override
		public ItemStack getElementItem(Material element) {
			return getDisplayItem(element);
		}

		@Override
		public boolean getIsWhitelist() {
			return true;
		}

		@Override
		public boolean onToggleElementRequest(Material element) {
			if (!hasMaterial(element))
				return addMaterial(element);
			return removeMaterial(element);
		}

		@Override
		public boolean onWhiteListChangeRequest(boolean isWhitelist) {
			return false;
		}

		@Override
		public boolean currentCollectionContains(Material element) {
			return this.hasMaterial(element);
		}

	}
	private static boolean isMaterialAllowed(Material mat) {
		return mat.isBlock();
	}
	public List<Material> getAllowedMaterials() {
		List<Material> myList = new ArrayList<Material>();
		for (Material mat:Material.values())
			if (isMaterialAllowed(mat))
				myList.add(mat);
		return myList;
	}
	public ItemStack getDisplayItem(Material mat) {
		if (mat.isItem())
			return new ItemBuilder(mat).setGuiProperty().build();
		return new ItemBuilder(Material.STONE).setGuiProperty().build();
	}
	/*
	private static final List<BlockTypeContainer> allowedMaterials = new ArrayList<BlockTypeContainer>(
			getMaterialButtons());

	private static void addUpTo(List<BlockTypeContainer> list, Material mat, int max) {
		for (int i = 0; i < max; i++)
			list.add(new BlockTypeContainer(mat, (byte) i));
	}

	private static void addAll(List<BlockTypeContainer> list, Material... mats) {
		for (Material mat : mats)
			list.add(new BlockTypeContainer(mat));
	}*/

}
