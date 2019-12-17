package ca.concordia.apr.invsdiff.ppt;

public class ExitPpt extends ChildPpt {
	private String name;
	public ExitPpt(ParentPpt parent, String name) {
		super(parent);
		this.name = name;
	}
	public ExitPpt(ParentPpt parent, String name, String condition) {
		super(parent);
		this.name = name;
		this.condition = condition;
	}
	public String getName() {
		return name;
	}
	public final String getRawName() {
		if (this.condition != null) {
			return parent.getName() + "." + name + ":::EXIT" + ";" + condition;
		}
		return parent.getName() + "." + name + ":::EXIT";
	}
}
