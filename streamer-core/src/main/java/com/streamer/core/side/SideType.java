package com.streamer.core.side;

public enum SideType {

	UNKNOWN(""), JDBC("jdbc");

	private String name;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	SideType(String name) {
		this.name = name;
	}

	public static SideType nameOf(String name) {
		SideType current = SideType.UNKNOWN;
		for (SideType type : values()) {
			if (type.getName().equalsIgnoreCase(name)) {
				current = type;
			}
		}
		return current;
	}
}
