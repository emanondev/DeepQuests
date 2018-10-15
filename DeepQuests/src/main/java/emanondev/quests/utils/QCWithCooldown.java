package emanondev.quests.utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;
import emanondev.quests.configuration.ConfigSection;
import emanondev.quests.data.CooldownData;
import emanondev.quests.mission.MissionDisplayInfo;
import emanondev.quests.newgui.button.AButton;
import emanondev.quests.newgui.button.BackButton;
import emanondev.quests.newgui.button.StaticButton;
import emanondev.quests.newgui.button.StaticFlagButton;
import emanondev.quests.newgui.button.StringListEditorButton;
import emanondev.quests.newgui.gui.Gui;
import emanondev.quests.newgui.gui.MapGui;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ComponentBuilder;

public abstract class QCWithCooldown extends QCWithWorlds {
	private final CooldownData cooldownData;

	public QCWithCooldown(ConfigSection m, Savable parent) {
		super(m, parent);
		this.cooldownData = new CooldownData(m,this,getDefaultCooldownMinutes(),getDefaultCooldownUse());
	}

	
	protected abstract boolean getDefaultCooldownUse();

	protected abstract long getDefaultCooldownMinutes();

	
	
	public CooldownData getCooldownData() {
		return cooldownData;
	}

	

	public abstract DisplayStateInfo getDisplayInfo();

	private final static BaseComponent[] changeItemDesc = new ComponentBuilder(
			ChatColor.GOLD + "Click to set the item in your main hand").create();

	protected class QCWithCooldownEditor extends QCWithWorldsEditor {

		public QCWithCooldownEditor(String title, Player p, Gui previusHolder) {
			super(title, p, previusHolder);
			putButton(8, cooldownData.getCooldownEditorButton(this));
			putButton(15, new DisplayEditorButton());
			putButton(17, cooldownData.getCooldownTogglerButton(this));
		}

		private class DisplayEditorButton extends AButton {
			private ItemStack item = new ItemBuilder(Material.PAPER).setGuiProperty().build();

			public DisplayEditorButton() {
				super(QCWithCooldownEditor.this);
				Utils.updateDescription(item, Arrays.asList("&6&lFull Display Editor"), getTargetPlayer(), true);
			}

			@Override
			public ItemStack getItem() {
				return item;
			}

			@Override
			public boolean update() {
				return false;
			}

			@Override
			public void onClick(Player clicker, ClickType click) {
				clicker.openInventory(new DisplaySelector().getInventory());

			}

			private class DisplaySelector extends MapGui {

				public DisplaySelector() {
					super("&8Status Display Editor", 6, DisplayEditorButton.this.getTargetPlayer(),
							QCWithCooldownEditor.this);
					for (int i = 0; i < DisplayState.values().length; i++) {
						this.putButton(i, new DisplayButton(DisplayState.values()[i]));
						this.putButton(i + 9, new StateLoreEditorButton(DisplayState.values()[i]));
						this.putButton(i + 18, new ItemStackEditorButton(DisplayState.values()[i]));
						this.putButton(i + 27, new HiddenFlagButton(DisplayState.values()[i]));
						this.putButton(53, new BackButton(this));
						updateInventory();
					}
				}

				private class StateLoreEditorButton extends StringListEditorButton {
					private DisplayState state;

					public StateLoreEditorButton(DisplayState state) {
						super("DisplayTextEditor for " + state.toString(),
								new ItemBuilder(Material.PAPER).setGuiProperty().build(), DisplaySelector.this);
						this.state = state;
						update();
					}

					@Override
					public List<String> getButtonDescription() {
						return null;
					}

					@Override
					public List<String> getCurrentList() {
						if (state == null)
							return new ArrayList<String>();
						if (getDisplayInfo() instanceof MissionDisplayInfo)
							return ((MissionDisplayInfo) getDisplayInfo()).getRawDescription(state);
						return getDisplayInfo().getDescription(state);
					}

					@Override
					public boolean onStringListChange(List<String> list) {
						getDisplayInfo().setDescription(state, list);
						return true;
					}

				}

				private class DisplayButton extends StaticButton {

					public DisplayButton(DisplayState state) {
						super(Utils.setDescription(new ItemBuilder(Material.THIN_GLASS).setGuiProperty().build(),
								Arrays.asList("&6&lStatus: &6" + state.toString(), "&6" + state.getDescription(),
										"&6Click below buttons to edit:",
										"&6Display Text, Display Item, Hidden status"),
								DisplaySelector.this.getTargetPlayer(), true), DisplaySelector.this);
					}

					@Override
					public void onClick(Player clicker, ClickType click) {
					}

				}

				private class HiddenFlagButton extends StaticFlagButton {
					private final DisplayState state;

					public HiddenFlagButton(DisplayState state) {
						super(Utils.setDescription(
								new ItemBuilder(Material.WOOL).setGuiProperty().setDamage(5).build(),
								Arrays.asList("&6This status is &anot hidden"), DisplaySelector.this.getTargetPlayer(),
								true),
								Utils.setDescription(
										new ItemBuilder(Material.WOOL).setGuiProperty().setDamage(14)
												.build(),
										Arrays.asList("&6This status is &chidden"),
										DisplaySelector.this.getTargetPlayer(), true),
								DisplaySelector.this);
						this.state = state;
					}

					@Override
					public boolean getCurrentValue() {
						if (state == null)
							return false;
						return getDisplayInfo().isHidden(state);
					}

					@Override
					public boolean onValueChangeRequest(boolean value) {
						getDisplayInfo().setHide(state, value);
						return true;
					}

				}

				private class ItemStackEditorButton extends emanondev.quests.newgui.button.ItemEditorButton {
					private final DisplayState state;

					public ItemStackEditorButton(DisplayState state) {
						super(DisplaySelector.this);
						this.state = state;
					}

					@Override
					public ItemStack getCurrentItem() {
						if (state == null)
							return null;
						return getDisplayInfo().getItem(state);
					}

					@Override
					public void onReicevedItem(ItemStack item) {
						getDisplayInfo().setItem(state, item);
						getParent().updateInventory();
					}

					@Override
					public List<String> getButtonDescription() {
						return null;
					}

					@Override
					public boolean update() {
						ItemStack current = getCurrentItem();
						if (current == null || current.getType() == Material.AIR)
							this.item = new ItemBuilder(Material.BARRIER).setGuiProperty().build();
						else
							this.item = current;
						return true;
					}

					@Override
					public void onClick(Player clicker, ClickType click) {
						this.requestItem(clicker, changeItemDesc);

					}
				}
			}
		}
	}
}
