package gameboy;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

//TO DO:
//setCartridgeType() -> needs MBCmanager
//Memory Bios Logo thingy
//Figure out how to do logo checksum

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
	private String title;
	private String locale;
	private String romSize;
	private int romBank;
	private String ramSize;
	private int expectedHeaderChecksum;
	private int headerChecksum;
	private int cartridgelogoChecksum;
	private int bootromlogoChecksum;
	
	public Cartridge(String path) {
		loadRom(path);
		System.out.println("Loading rom from " + path);
	}
	
	private void loadRom(String filePath) {
		try {
			byte[] tempRom = Files.readAllBytes(new File(filePath).toPath());
			rom = new int[tempRom.length];
			for (int i = 0; i < tempRom.length; i++) {
				rom[i] = tempRom[i] & 0xff;
			}
			setTitle();
			setLocale();
			setRamSize();
			setRomSize();
			//set the cartridge type
			verifyHeaderChecksum();
			//verify the logo checksum
		} catch (IOException e) {
			System.out.println("Exception: " + e);
			//Ask to put correct rom path when inputting rom path is implemented
		}
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
	
	private void setRamSize() {
		switch (rom[RAM_SIZE]) {
		case 0x00:
			this.ramSize = "None";
			break;
			
		case 0x01:
			this.ramSize = "2KB";
			break;
			
		case 0x02:
			this.ramSize = "8KB";
			break;
			
		case 0x03:
			this.ramSize = "32KB";
			break;
			
		case 0x04:
			this.ramSize = "128KB";
			break;
			
		case 0x05:
			this.ramSize = "64KB";
			break;
		}
	}
	
	private int getRamSize() {
		switch (ramSize) {
        case "None":
            return 0;
        case "2KB":
            return 2048;
        case "8KB":
            return 8192;
        case "32KB":
            return 8192 * 4; // 8KB * 4
        case "64KB":
            return 8192 * 8; // 8KB * 8
        case "128KB":
            return 8192 * 16; // 8KB * 16
        default:
            return 0;
		}
	}
	
	private void setRomSize() {
		switch (rom[ROM_SIZE]) {
		case 0x00:
			this.romSize = "32 KiB";
			this.romBank = 2;
			break;
			
		case 0x01:
			this.romSize = "64 KiB";
			this.romBank = 4;
			break;

		case 0x02:
			this.romSize = "128 KiB";
			this.romBank = 8;
			break;
			
		case 0x03:
			this.romSize = "256 KiB";
			this.romBank = 16;
			break;
			
		case 0x04:
			this.romSize = "512 KiB";
			this.romBank = 32;
			break;
			
		case 0x05:
			this.romSize = "1 MiB";
			this.romBank = 64;
			break;
			
		case 0x06:
			this.romSize = "2 MiB";
			this.romBank = 128;
			break;
			
		case 0x07:
			this.romSize = "4 MiB";
			this.romBank = 256;
			break;
			
		case 0x08:
			this.romSize = "8 MiB";
			this.romBank = 512;
			break;
		}
	}
	
	private int getRomSize() {
		switch (romSize) {
        case "32 KiB":
            return 8192 * 4; // 8KB * 4
        case "64 KiB":
            return 8192 * 8; // 8KB * 8
        case "128 KiB":
            return 8192 * 16; // 8KB * 16
        case "256 KiB":
            return 8192 * 32; // 8KB * 32
        case "512 KiB":
            return 8192 * 64; // 8KB * 64
        case "1 MiB":
            return 8192 * 128; // 8KB * 128
        case "2 MiB":
            return 8192 * 256; // 8KB * 256
        case "4 MiB":
            return 8192 * 512; // 8KB * 512
        case "8 MiB":
            return 8192 * 1024; // 8KB * 1024
        default:
            return 0;
		}
	}
	
	private void setTitle() {
		StringBuilder sb = new StringBuilder();
		
		for (int i = TITLE_START; i < TITLE_END; i++) {
			if (this.rom[i] == 0) {
				continue;
			}
			
			sb.append(i);
		}
		
		this.title = sb.toString();
	}
	
	private String getTitle() {
		return this.title;
	}

	private void verifyHeaderChecksum() {
		expectedHeaderChecksum = rom[HEADER_CHECKSUM_EXPECTED];
		headerChecksum = 0;
		for (int i = HEADER_CHECKSUM_START; i <= HEADER_CHECKSUM_END; i++) {
			headerChecksum = headerChecksum - rom[i] - 1;
		}
		headerChecksum &= 0xff;
	}
	
	public void printRomData() {
		System.out.print(
		//Title
		"Title:" + title + "\n" +
		//Rom Size
		"Rom Size: " + romSize + "\n" +
		//Ram Size
		"Ram Size: " + ramSize + "\n" +
		//Locale
		"Locale: " + locale + "\n" +
		//Header Checksum pass or fail
		"Header Checksums: ( " + headerChecksum + " == " + expectedHeaderChecksum + " ) is " +
        (headerChecksum == expectedHeaderChecksum) + " )\n"
		//Logo Checksum pass or fail
		);
	}
}
