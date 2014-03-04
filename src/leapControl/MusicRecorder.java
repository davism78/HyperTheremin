package leapControl;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;

public class MusicRecorder {
	private FileOutputStream output;

	public static final int ENTRY_SIZE = 16;

	public MusicRecorder(String fileName) {
		try {
			output = new FileOutputStream(new File(fileName));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Writes the given pitch/volume pair to the file specified
	 * upon construction.
	 */
	public void record(double pitch, double volume) {
		try {
			/*
			 * Format is ENTRY_SIZE bytes per pair.
			 * pitch,volume
			 * 
			 */
			ByteBuffer bytes = ByteBuffer.allocate(ENTRY_SIZE);
			bytes.putDouble(pitch).putDouble(volume);
			output.write(bytes.array());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void cleanup() {
		try {
			output.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
