package my.framework.util;

public class Updator<S, T> {
	
	private long lastUpdatedAt = -1;
	private T object;
	private Runner<S, T> runner;
	private Updatable source;
		
	public Updator(Updatable source, Runner<S, T> runner) {
		this.source = source;
		this.runner = runner;
		runner.setSource((S)source);
	}
	
	public void clear() {
	    object = null;
	    lastUpdatedAt = -1;
	}
	
	public T get() {
		if (source.updatedSince(lastUpdatedAt)) {
			// synchronized source to prevent concurrent modification
			synchronized (source) {				
				object = runner.run();
				lastUpdatedAt = source.updatedAt();
			}
		}
		return object;
	}
	
	public static abstract class Runner<S, T> {
		
		protected S source;
		
		public abstract T run();
		
		public void setSource(S source) {
			this.source = source;
		}
		
	}

}
