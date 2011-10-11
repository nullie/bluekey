package org.nullie.bluekey;

public enum Command {
	LOCK ("L"),
	UNLOCK ("U");
	
	public final byte[] code;
	
	Command(String code) {
		this.code = code.getBytes();
	}
}
