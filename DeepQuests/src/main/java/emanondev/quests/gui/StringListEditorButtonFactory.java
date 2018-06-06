package emanondev.quests.gui;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;

import emanondev.quests.utils.StringUtils;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;

public abstract class StringListEditorButtonFactory implements EditorButtonFactory {

	private final ArrayList<String> buttonDescription;

	private final String title;

	private final Material material;

	private final short durability;

	public StringListEditorButtonFactory(List<String> buttonDescription, String title, Material material) {
		this(buttonDescription, title, material, 0);
	}

	/**
	 * 
	 * @param buttonDescription
	 * @param title
	 */
	public StringListEditorButtonFactory(List<String> buttonDescription, String title, Material material,
			int durability) {

		if (buttonDescription == null)
			this.buttonDescription = new ArrayList<String>();
		else
			this.buttonDescription = StringUtils.fixColorsAndHolders(new ArrayList<String>(buttonDescription));

		if (title == null)
			this.title = "";
		else
			this.title = StringUtils.fixColorsAndHolders(title);

		if (material == null)
			this.material = Material.NAME_TAG;
		else
			this.material = material;

		this.durability = (short) Math.max(0, durability);
	}

	@Override
	public CustomButton getCustomButton(CustomGui parent) {
		return new StringListEditorButton(parent);
	}

	protected abstract List<String> getStringList();

	private ArrayList<String> getSafeStringList() {
		List<String> rawList = getStringList();
		ArrayList<String> list;
		if (rawList == null)
			list = new ArrayList<String>();
		else {
			list = new ArrayList<String>(rawList);
			for (int i = 0; i < list.size(); i++) {
				list.set(i, StringUtils.revertColors(list.get(i)));
			}
		}
		return list;
	}

	/**
	 * called when the original ArrayList to edit is changed
	 */
	protected abstract void onChange(ArrayList<String> newList);

	private class StringListEditorButton extends CustomButton {
		private ItemStack item = new ItemStack(material);

		public StringListEditorButton(CustomGui parent) {
			super(parent);
			if (durability != 0)
				item.setDurability((short) durability);
			update();
		}

		public void update() {
			ArrayList<String> desc = new ArrayList<String>(buttonDescription);
			ArrayList<String> current = getSafeStringList();
			if (current.isEmpty())
				desc.add("&7 - No value is set -");
			else
				desc.addAll(getSafeStringList());
			StringUtils.setDescription(item, desc);
		}

		@Override
		public ItemStack getItem() {
			return item;
		}

		@Override
		public void onClick(Player clicker, ClickType click) {
			clicker.openInventory(new StringListEditorGui(clicker).getInventory());
		}

		private class StringListEditorGui extends CustomLinkedGui<CustomButton> {
			public StringListEditorGui(Player p) {
				super(p, StringListEditorButton.this.getParent(), 6);
				this.setFromEndCloseButtonPosition(8);
				this.setTitle(null, StringUtils.convertText(p, title));
				this.addButton(11, new NoColorPreviewButton());
				this.addButton(29, new PreviewButton());
				this.addButton(15, new AddLineButton());
				this.addButton(24, new SetLineButton());
				this.addButton(33, new RemoveLineButton());
				this.reloadInventory();
			}

			private class NoColorPreviewButton extends CustomButton {
				private ItemStack item = new ItemStack(Material.IRON_BLOCK);

				public NoColorPreviewButton() {
					super(StringListEditorGui.this);
					update();
				}

				@Override
				public ItemStack getItem() {
					return item;
				}

				@Override
				public void onClick(Player clicker, ClickType click) {
				}

				public void update() {
					ArrayList<String> desc = getSafeStringList();
					if (desc.isEmpty())
						desc.add("&7 - No value is set -");
					for(int i = 0; i<desc.size();i++)
						desc.set(i,ChatColor.WHITE+desc.get(i));
					StringUtils.setDescriptionNoColors(item, desc);
				}
			}

			private class PreviewButton extends CustomButton {
				private ItemStack item = new ItemStack(Material.DIAMOND_BLOCK);

				public PreviewButton() {
					super(StringListEditorGui.this);
					update();
				}

				@Override
				public ItemStack getItem() {
					return item;
				}

				@Override
				public void onClick(Player clicker, ClickType click) {
				}

				public void update() {
					ArrayList<String> desc = getSafeStringList();
					if (desc.isEmpty())
						desc.add("&7 - No value is set -");
					for(int i = 0; i<desc.size();i++)
						desc.set(i,ChatColor.WHITE+desc.get(i));
					StringUtils.setDescription(item, desc);
				}
			}

			private class AddLineButton extends TextEditorButton {
				private ItemStack item = new ItemStack(Material.WOOL);

				public AddLineButton() {
					super(StringListEditorGui.this);
					item.setDurability((short) 5);
					ArrayList<String> desc = new ArrayList<String>();
					desc.add("&a&lAdd &6&la New Line");
					StringUtils.setDescription(item, desc);
				}

				@Override
				public void onReicevedText(String text) {
					ArrayList<String> list = getSafeStringList();
					if (text==null)
						text = "";
					list.add(text);
					onChange(list);
					getParent().update();
				}

				@Override
				public ItemStack getItem() {
					return item;
				}

				@Override
				public void onClick(Player clicker, ClickType click) {
					this.requestText(clicker, addLineChatText);
				}
			}

			private class RemoveLineButton extends TextEditorButton {
				private ItemStack item = new ItemStack(Material.WOOL);

				public RemoveLineButton() {
					super(StringListEditorGui.this);
					item.setDurability((short) 14);
					ArrayList<String> desc = new ArrayList<String>();
					desc.add("&c&lRemove a &6&lLine");
					StringUtils.setDescription(item, desc);
				}

				@Override
				public void onReicevedText(String text) {
					if (text == null)
						return;
					ArrayList<String> list;
					try {
						int line = Integer.valueOf(text) - 1;
						list = getSafeStringList();
						list.remove(line);
					} catch (NumberFormatException e) {
						getParent().getPlayer().sendMessage(ChatColor.RED + "Not a Number");
						return;
					} catch (IndexOutOfBoundsException e) {
						getParent().getPlayer().sendMessage(ChatColor.RED + "Invalid number");
						return;
					}
					onChange(list);
					getParent().update();
				}

				@Override
				public ItemStack getItem() {
					return item;
				}

				@Override
				public void onClick(Player clicker, ClickType click) {
					ComponentBuilder comp = new ComponentBuilder(ChatColor.GOLD + "****************************\n");
					ArrayList<String> list = getSafeStringList();
					for (int i = 0; i < list.size(); i++)
						comp.append(ChatColor.GOLD + "  Click here to remove line " + (i + 1) + "  \n").event(
								new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/questtext " + (i + 1))).event(
										new HoverEvent(HoverEvent.Action.SHOW_TEXT,
												new ComponentBuilder(StringUtils.fixColorsAndHolders(list.get(i)) + "\n"
														+ ChatColor.RED + "Warning: deleting can't be undone")
																.create()));
					comp.append("").append(ChatColor.GOLD + "    Click here to go back    \n")
							.event(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/questtext"))
							.event(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
									new ComponentBuilder(ChatColor.GOLD + "Reopen the Gui").create()))
							.append(ChatColor.GOLD + "****************************");
					this.requestText(clicker, comp.create());
				}
			}

			private class SetLineButton extends TextEditorButton {
				private ItemStack item = new ItemStack(Material.WOOL);

				public SetLineButton() {
					super(StringListEditorGui.this);
					item.setDurability((short) 11);
					ArrayList<String> desc = new ArrayList<String>();
					desc.add("&9&lSet &6&lLine");
					StringUtils.setDescription(item, desc);
				}

				@Override
				public void onReicevedText(String text) {
					if (text == null)
						return;
					ArrayList<String> list;
					try {
						String rawNumber = text.split(" ")[0];
						text = text.replaceFirst(rawNumber, "");
						if (text.startsWith(" "))
							text = text.replaceFirst(" ", "");
						int line = Integer.valueOf(rawNumber) - 1;
						list = getSafeStringList();
						if (list.get(line)!=null && list.get(line).equals(text)) {
							getParent().getPlayer().sendMessage(ChatColor.RED + "Text didn't change");
							return;
						}
						list.set(line, text);
					} catch (NumberFormatException e) {
						getParent().getPlayer().sendMessage(ChatColor.RED + "Not a Number");
						return;
					} catch (IndexOutOfBoundsException e) {
						getParent().getPlayer().sendMessage(ChatColor.RED + "Invalid number");
						return;
					} catch (Exception e) {
						getParent().getPlayer().sendMessage(ChatColor.RED + "Invalid input");
						return;
					}
					onChange(list);
					getParent().update();
				}

				@Override
				public ItemStack getItem() {
					return item;
				}

				@Override
				public void onClick(Player clicker, ClickType click) {
					ComponentBuilder comp = new ComponentBuilder(ChatColor.GOLD + "****************************\n");
					ArrayList<String> list = getSafeStringList();
					for (int i = 0; i < list.size(); i++)
						comp.append(ChatColor.GOLD + "  Click here to set line " + (i + 1) + "  \n")
								.event(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND,
										"/questtext " + (i + 1) + " " + list.get(i)))
								.event(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
										new ComponentBuilder(StringUtils.fixColorsAndHolders(list.get(i)) + "\n"
												+ ChatColor.RED + "Warning: this action will replace previus line")
														.create()));
					comp.append("").append(ChatColor.GOLD + "    Click here to go back    \n")
							.event(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/questtext"))
							.event(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
									new ComponentBuilder(ChatColor.GOLD + "Reopen the Gui").create()))
							.append(ChatColor.GOLD + "****************************");
					this.requestText(clicker, comp.create());
				}
			}
		}
	}

	private final static BaseComponent[] addLineChatText = new ComponentBuilder(
			ChatColor.GOLD + "****************************\n" + ChatColor.GOLD + "   Click Me to add a new Line   \n"
					+ ChatColor.GOLD + "****************************")
							.event(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/questtext "))
							.event(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
									new ComponentBuilder(ChatColor.GOLD + "Write the text to add on the new line"
											+ ChatColor.GOLD + "Tip: use '&' for chat formats" + ChatColor.YELLOW
											+ "/questtext <new line>").create()))
							.create();
}
