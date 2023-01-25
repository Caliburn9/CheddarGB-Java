package gameboy;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

public class Cartridge {
	private final int LOGO_START = 0x104;
	private final int LOGO_END = 0x133;
	private final int TITLE_START = 0x134;
	private final int TITLE_END = 0x143;
	private final int ROM_SIZE = 0x148;
	private final int RAM_SIZE = 0x149;
	private final int LOCALE = 0x14a;
	private final int HEADER_CHECKSUM_START = 0x134;
	private final int HEADER_CHECKSUM_END = 0x14c;
	private final int HEADER_CHECKSUM_EXPECTED = 0x14d;
	
	private int[] rom;
	private String locale;
	
	public Cartridge(String path) {
		loadRom(path);
		System.out.println("Loading rom from " + path);
	}
	
	private void setLocale() {
		int localeVal = rom[LOCALE];
				
		if (localeVal == 0x00) {
			locale = "Japanese";
		} else if (localeVal == 0x01) {
			locale = "Global";
		} else {
			locale = "Unknown";
		}
	}
	
	private String getLocale() {
		return this.locale;
	}
	
	private void loadRom(String filePath) {
		try {
			byte[] tempRom = Files.readAllBytes(new File(filePath).toPath());
			rom = new int[tempRom.length];
			for (int i = 0; i < tempRom.length; i++) {
				rom[i] = tempRom[i] & 0xff;
			}
			//set the title
			setLocale();
			//set the ram size
			//set the rom size
			//set the cartridge type
			//verify the header checksum
			//verify the logo checksum
		} catch (IOException e) {
			System.out.println("Exception: " + e);
			//Ask to put correct rom path when inputting rom path is implemented
		}
	}
}
