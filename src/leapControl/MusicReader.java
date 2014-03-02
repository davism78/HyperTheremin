package leapControl;

import static leapControl.MusicRecorder.ENTRY_SIZE;
import graphics.GraphicsUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.ByteBuffer;

public class MusicReader {
	
	private String filename;
	private FileInputStream input;
	private ByteBuffer currentData;
	
	public MusicReader(String filename) {
		this.filename = filename;
		restart();
	}
	
	public void restart() {
		try {
			input = new FileInputStream(new File(filename));
			currentData = null;
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Retrieves the next set of data in the file
	 * @return True if the is data in the file. 
	 *         False if no data was received.
	 */
	public boolean next() {
		try {
			byte[] bytes = new byte[ENTRY_SIZE];
			int bytesRead = 0;
			while(bytesRead != ENTRY_SIZE) {
				int bRead = input.read(bytes);
				if(bRead == -1) {
					currentData = null;
					return false;
				} else {
					bytesRead += bRead;
				}
			}
			currentData = ByteBuffer.wrap(bytes);
			return true;
		} catch (IOException e) {
			e.printStackTrace();
		}
		return false;
	}
	
	/**
	 * Gets the pitch attribute of the current piece of data.
	 * 
	 */
	public double getPitch() {
		if(currentData == null) {
			throw new IllegalStateException("trying to get pitch without getting next data.");
		}
		return currentData.getDouble();
	}
	
	/**
	 * Gets the volume attribute of the current piece of data.
	 * 
	 */
	public double getVolume() {
		if(currentData == null) {
			throw new IllegalStateException("trying to get volume without getting next data.");
		}
		return currentData.getDouble(8);
	}
	
	public static void main(String[] args) {
		MusicReader reader = new MusicReader(GraphicsUtils.DEFAULT_RECORD_FILE);
		while(reader.next()) {
			double pitch = reader.getPitch();
			double volume = reader.getVolume();
			System.out.println("Pitch: " + pitch + "Volume: " + volume);
		}
	}
}
