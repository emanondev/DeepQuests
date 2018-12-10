package emanondev.quests.data;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.inventory.ItemStack;

import emanondev.quests.configuration.ConfigSection;
import emanondev.quests.newgui.button.Button;
import emanondev.quests.newgui.button.SelectOneElementButton;
import emanondev.quests.newgui.gui.Gui;
import emanondev.quests.task.Task;
import emanondev.quests.utils.ItemBuilder;
import emanondev.quests.Quests;

public class BossBarData extends QCData {
	private final static String PATH_COLOR = "bossbar.color";
	private final static String PATH_STYLE = "bossbar.style";
	
	
	private BarStyle style;
	private BarColor color;

	public BossBarData(ConfigSection section, Task parent) {
		super(section, parent);
		try {
			style = BarStyle.valueOf(getSection().getString(PATH_STYLE));
			if (style==null)
				throw new NullPointerException();
		} catch (Exception e) {
			style = Quests.get().getBossBarManager().getTaskStyle(parent);
		}
		try {
			color = BarColor.valueOf(getSection().getString(PATH_COLOR));
			if (color==null)
				throw new NullPointerException();
		} catch (Exception e) {
			color = Quests.get().getBossBarManager().getTaskColor(parent);
		}
	}

	public BarStyle getStyle() {
		return style;
	}

	public BarColor getColor() {
		return color;
	}
	
	public boolean setStyle(BarStyle style) {
		if (style == null || style == this.style)
			return false;
		this.style = style;
		getSection().set(PATH_STYLE,style.toString());
		setDirty(true);
		return true;
	}

	public boolean setColor(BarColor color) {
		if (color == null || color == this.color)
			return false;
		this.color = color;
		getSection().set(PATH_COLOR,color.toString());
		setDirty(true);
		return true;
	}
	
	public Button getStyleSelectorButton(Gui gui) {
		return new StyleSelectorButton(gui);
	}
	public Button getColorSelectorButton(Gui gui) {
		return new ColorSelectorButton(gui);
	}
	private class StyleSelectorButton extends SelectOneElementButton<BarStyle> {

		public StyleSelectorButton( Gui parent) {
			super("&8Style Selector", new ItemBuilder(Material.ARMOR_STAND).setGuiProperty().build(), parent, 
					Arrays.asList(BarStyle.values()), false, true, false);
		}

		@Override
		public List<String> getButtonDescription() {
			ArrayList<String> desc = new ArrayList<String>();
			desc.add("&6&lStyle Selector");
			desc.add("&6Click to edit");
			desc.add("&6Current Style: &e"+style.toString());
			return desc;
		}

		@Override
		public List<String> getElementDescription(BarStyle element) {
			ArrayList<String> desc = new ArrayList<String>();
			desc.add("&6Style: &e"+element.toString());
			desc.add("&6Click to select");
			return desc;
		}

		@Override
		public ItemStack getElementItem(BarStyle element) {
			return new ItemBuilder(Material.ORANGE_WOOL).setGuiProperty().build();
		}

		@Override
		public void onElementSelectRequest(BarStyle element) {
			if (setStyle(element)) {
				getParent().updateInventory();
				getTargetPlayer().openInventory(getParent().getInventory());
			}
		}
		
	}
	private class ColorSelectorButton extends SelectOneElementButton<BarColor> {

		public ColorSelectorButton( Gui parent) {
			super("&8Color Selector", new ItemBuilder(Material.ARMOR_STAND).setGuiProperty().build(), parent, 
					Arrays.asList(BarColor.values()), false, true, false);
		}

		@Override
		public List<String> getButtonDescription() {
			ArrayList<String> desc = new ArrayList<String>();
			desc.add("&6&lColor Selector");
			desc.add("&6Click to edit");
			desc.add("&6Current Color: &e"+color.toString());
			return desc;
		}

		@Override
		public List<String> getElementDescription(BarColor element) {
			ArrayList<String> desc = new ArrayList<String>();
			desc.add("&6Color: &e"+element.toString());
			desc.add("&6Click to select");
			return desc;
		}

		@Override
		public ItemStack getElementItem(BarColor element) {
			return new ItemBuilder(Material.ORANGE_WOOL).setGuiProperty().build();
		}

		@Override
		public void onElementSelectRequest(BarColor element) {
			if (setColor(element)) {
				getParent().updateInventory();
				getTargetPlayer().openInventory(getParent().getInventory());
			}
		}
		
	}
	
	
}
