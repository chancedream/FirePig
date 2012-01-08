package my.framework.util;

import java.io.File;

public class FileObserver extends Thread {

	private static final long DELAY = 60000;
		
	private File file;
	private Handler handler;
	
	public FileObserver(File file, Handler handler) {
		this.file = file;
		this.handler = handler;
		setName("file-observer");
		setDaemon(true);
	}
	
	@Override
	public void run() {
		long lastModified = 0;
		while (true) {
			if (file.exists() && file.lastModified() > lastModified) {
				lastModified = file.lastModified();
				handler.handle(file);
			}
			try {
				Thread.sleep(DELAY);
			} catch (InterruptedException e) {}
		}
	}
	
	public interface Handler {
		
		void handle(File file);
		
	}

}
