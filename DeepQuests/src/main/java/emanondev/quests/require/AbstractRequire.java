package emanondev.quests.require;

import java.util.ArrayList;
import java.util.HashMap;

import org.bukkit.Material;
import org.bukkit.configuration.MemorySection;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

import emanondev.quests.gui.CustomButton;
import emanondev.quests.gui.CustomGui;
import emanondev.quests.gui.EditorButtonFactory;
import emanondev.quests.gui.RequireGui;
import emanondev.quests.gui.button.TextEditorButton;
import emanondev.quests.utils.Savable;
import emanondev.quests.utils.StringUtils;
import emanondev.quests.utils.YmlLoadableWithCooldown;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ComponentBuilder;

public abstract class AbstractRequire {
	private static final String PATH_DESCRIPTION = "description";
	private final String nameID;
	private final MemorySection section;
	private final YmlLoadableWithCooldown parent;
	private final HashMap<Integer,EditorButtonFactory> tools = new HashMap<Integer,EditorButtonFactory>();
	private String description; 
	public AbstractRequire(MemorySection section,YmlLoadableWithCooldown parent) {
		if (section==null || parent==null)
			throw new NullPointerException();
		this.nameID = loadName(section).toLowerCase();
		this.section = section;
		this.parent = parent;
		this.description = section.getString(PATH_DESCRIPTION);
		this.addToEditor(0, new DescriptionEditorButtonFactory());
	}
	public String getNameID() {
		return nameID;
	}
	/**
	 * @return the unique name
	 */
	private String loadName(MemorySection m) {
		String name = m.getName();
		if (name==null||name.isEmpty())
			throw new NullPointerException();
		return name;
	}
	public YmlLoadableWithCooldown getParent() {
		return parent;
	}
	protected MemorySection getSection() {
		return section;
	}
	public void openEditorGui(Player p){
		openEditorGui(p,null);
	}
	public void openEditorGui(Player p,CustomGui previusHolder){
		p.openInventory(new RequireGui(p,this,previusHolder,tools,"&9Require &8("+getKey()+")").getInventory());
	}
	public abstract String getKey();
	public void addToEditor(int slot,EditorButtonFactory item) {
		if (item!=null)
			tools.put(slot,item);
	}
	
	public String getDescription() {
		return description;
	}
	public boolean setDescription(String desc) {
		if (desc!=null && desc.equals(description))
			return false;
		if (desc==null && description==null)
			return false;
		this.description = desc;
		section.set(PATH_DESCRIPTION,description);
		if (parent instanceof Savable)
			((Savable) parent).setDirty(true);
		return true;
	}
	private class DescriptionEditorButtonFactory implements EditorButtonFactory {
		private class DescriptionEditorButton extends TextEditorButton {
			private ItemStack item = new ItemStack(Material.NAME_TAG);
			public DescriptionEditorButton(CustomGui parent) {
				super(parent);
				update();
			}
			@Override
			public ItemStack getItem() {
				return item;
			}
			public void update() {
				ArrayList<String> desc = new ArrayList<String>();
				desc.add("&6&lRequire Description Editor");
				desc.add("&6Click to edit");
				desc.add("&7Current value:");
				if (description!=null)
					desc.add("&7'&f"+description+"&7'");
				else
					desc.add("&7no description is set");
				desc.add("");
				desc.add("&7Represent the description of the Require");
				StringUtils.setDescription(item, desc);
			}
			@Override
			public void onClick(Player clicker, ClickType click) {
				this.requestText(clicker, StringUtils.revertColors(description), changeDescriptionHelp);
			}
			@Override
			public void onReicevedText(String text) {
				if (text == null)
					text = "";
				if (setDescription(text)) {
					update();
					getParent().reloadInventory();
				}
				else
					getOwner().sendMessage(StringUtils.fixColorsAndHolders(
							"&cSelected description was not a valid description"));
			}
		}
		@Override
		public CustomButton getCustomButton(CustomGui parent) {
			return new DescriptionEditorButton(parent);
		}
	}
	
	private static final BaseComponent[] changeDescriptionHelp = new ComponentBuilder(
			ChatColor.GOLD+"Click suggest the command and the old description\n\n"+
			ChatColor.GOLD+"Change override old description with new description\n"+
			ChatColor.YELLOW+"/questtext <new description>\n\n"
			).create();
}
