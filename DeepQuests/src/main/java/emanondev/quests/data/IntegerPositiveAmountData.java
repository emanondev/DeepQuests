package emanondev.quests.data;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Material;

import emanondev.quests.configuration.ConfigSection;
import emanondev.quests.newgui.button.AmountSelectorButton;
import emanondev.quests.newgui.button.Button;
import emanondev.quests.newgui.gui.Gui;
import emanondev.quests.utils.ItemBuilder;
import emanondev.quests.utils.QuestComponent;

public class IntegerPositiveAmountData extends QCData{
	private final static String PATH_AMOUNT = "amount";
	
	private int amount;

	public IntegerPositiveAmountData(ConfigSection section, QuestComponent parent) {
		super(section, parent);
		this.amount = section.getInt(PATH_AMOUNT, 1);
	}
	
	public int getAmount() {
		return amount;
	}
	public boolean setAmount(int amount) {
		if (amount <= 0)
			amount = 1;
		if (this.amount == amount)
			return false;
		getSection().set(PATH_AMOUNT,amount);
		this.amount = amount;
		return true;
	}
	
	public Button getAmountEditorButton(Gui gui) {
		return new PositiveAmountEditorButton(gui);
	}
	
	private class PositiveAmountEditorButton extends AmountSelectorButton {

		public PositiveAmountEditorButton(Gui parent) {
			super("&8Amount Editor", new ItemBuilder(Material.REPEATER).setGuiProperty().build(), parent);
		}

		@Override
		public List<String> getButtonDescription() {
			List<String> desc = new ArrayList<String>();
			desc.add("&6&lAmount Editor");
			desc.add("&6Click to edit");
			desc.add("&7Amount is &e" + getAmount());
			return desc;
		}

		@Override
		public long getCurrentAmount() {
			return amount;
		}

		@Override
		public boolean onAmountChangeRequest(long i) {
			return setAmount((int) i);
		}
		
	}

}
