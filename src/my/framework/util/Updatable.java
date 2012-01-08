package my.framework.util;

/**
 * <tt>Updatable</tt>接口用于跟踪对象的内部状态是否更新。
 * @author gmice
 */
public interface Updatable {
	
	/**
	 * 返回对象最近一次更新时间。
	 * @return 对象最近一次更新时间
	 */
	long updatedAt();
	
	/**
	 * 返回对象自指定时间后，是否又有更新。
	 * @param lastUpdatedAt 指定时间，通常是之前从<tt>updatedAt()</tt>获取的时间 
	 * @return true 当又有更新
	 */
	boolean updatedSince(long lastUpdatedAt);

}
