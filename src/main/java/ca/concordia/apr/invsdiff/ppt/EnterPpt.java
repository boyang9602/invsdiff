package ca.concordia.apr.invsdiff.ppt;

public class EnterPpt extends ChildPpt {
	private String name;
	public EnterPpt(ParentPpt parent, String name) {
		super(parent);
		this.name = name;
	}
	public EnterPpt(ParentPpt parent, String name, String condition) {
		super(parent);
		this.name = name;
		this.condition = condition;
	}
	public String getName() {
		return name;
	}
	public final String getRawName() {
		if (this.condition != null) {
			return parent.getName() + "." + name + ":::ENTER" + ";" + condition;
		}
		return parent.getName() + "." + name + ":::ENTER";
	}
}
