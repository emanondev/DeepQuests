package emanondev.quests.newgui.button;

import emanondev.quests.newgui.gui.Gui;
import emanondev.quests.utils.QuestComponent;

public abstract class QCButton<T extends QuestComponent> extends AButton implements Comparable<QCButton<T>>{

	private T qc;
	
	public QCButton(Gui parent,T qc) {
		super(parent);
		this.qc = qc;
	}
	
	public T getQuestComponent() {
		return qc;
	}

	@Override
	public int compareTo(QCButton<T> o) {
		if (qc!=null)
			return qc.compareTo(o.qc);
		if (o!= null && o.qc!=null)
			return o.qc.compareTo(qc);
		return 0;
	}
	

}
