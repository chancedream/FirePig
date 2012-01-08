package my.framework.util;

import java.util.ArrayList;
import java.util.List;

/**
 * <tt>FutureList</tt>用于表示一个增长中的<tt>List</tt>。
 * 和普通的<tt>List</tt>的区别在于，当尝试获取一个下标越界的元素时，
 * 如果<tt>FutureList</tt>还在增长中，则不会抛出<tt>IndexOutOfBoundsException</tt>。
 * 当前线程将被挂起而等待<tt>FutureList</tt>增长。
 * 
 * @author gmice 
 */
public class FutureList<E> implements Updatable {
	
	private boolean isDone;
	private List<E> list;
	private long updateAt;
	
	/**
	 * 创建一个空的<tt>FutureList</tt>，初始状态为未完成
	 */
	public FutureList() {
		this(false);
	}
	
	/**
	 * 创建一个空的<tt>FutureList</tt>，并设置初始状态是否为已完成
	 * @param isDone 初始状态，true表示已完成
	 */
	public FutureList(boolean isDone) {
		this.list = new ArrayList<E>();
		this.isDone = isDone;
	}
	
	/**
	 * 添加指定元素至<tt>FutureList</tt>尾。
	 * 只能在<tt>FutureList</tt>状态为未完成时调用此方法。
	 * 若先前有一或多个线程因尝试获取此元素而被阻止，那么它们将会在元素添加后被全部唤醒。
	 * @param object 待添加的元素
	 */
	public synchronized void add(E object) {
		if (isDone) throw new RuntimeException("Unable to add elements when FutureList is done");
		list.add(object);
		updated();
		notifyAll();
	}
	
	/**
	 * 添加多个指定元素至<tt>FutureList</tt>尾。
	 * 只能在<tt>FutureList</tt>状态为未完成时调用此方法。
	 * 若先前有一或多个线程因尝试获取这些元素之一而被阻止，那么它们将会在元素添加后被全部唤醒。
	 * @param objects 待添加的多个元素，添加顺序和传入参数顺序相同
	 */
	public synchronized void add(E... objects) {
		if (isDone) throw new RuntimeException("Unable to add elements when FutureList is done");
		for (E object : objects) list.add(object);
		updated();
		notifyAll();
	}
	
	public synchronized void awaitTermination() throws InterruptedException {
		while (!isDone) wait();
	}
	
	/**
	 * 删除<tt>List</tt>的所有元素，同时将<tt>FutureList</tt>状态设为未完成。
	 */
	public synchronized void clear() {
		clear(false);
	}
	
	public synchronized void clear(boolean isDone) {
		if (!this.isDone) throw new RuntimeException("Unable to clear FutureList which hasn't done");
		this.isDone = isDone;
		list.clear();
		updated();
	}
	
	/**
	 * 将<tt>FutureList</tt>状态设为已完成，同时唤醒所有仍然被阻止的尝试获取元素的线程。
	 * 这些线程将会得到<tt>IndexOutOfBoundsException</tt>。
	 */
	public synchronized void done() {
		isDone = true;		
		notifyAll();
	}
	
	/**
	 * 获取指定下标的<tt>FutureList</tt>元素。
	 * 如果下标大于或等于当前<tt>FutureList</tt>的大小，线程将会被挂起以等待<tt>FutureList</tt>增长到能获取该元素。 
	 * @param index 待返回元素的下标
	 * @return 指定下标的元素
	 * @throws InterruptedException 如果当<tt>FutureList</tt>完成增长（状态被设为已完成）时，下标依然是越界的，则会抛出<tt>IndexOutOfBoundsException</tt>
	 */
	public synchronized E get(int index) throws InterruptedException {
		while (true) {
			if (index < list.size()) return list.get(index);
			if (isDone) throw new IndexOutOfBoundsException();
			wait();
		}		
	}
	
	/**
	 * 获取<tt>FutureList</tt>尾的元素。
	 * @return 线程将会被挂起直到<tt>FutureList</tt>增长完成，然后返回最后一个元素。如果当增长完成时<tt>FutureList</tt>的元素个数为0，则返回<tt>null</tt>
	 * @throws InterruptedException
	 */
	public synchronized E getLast() throws InterruptedException {
		while (!isDone) wait();
		if (list.isEmpty()) return null;
		return list.get(list.size() - 1);
	}
	
	/**
	 * 返回<tt>FutureList</tt>的状态。
	 * @return true 当<tt>FutureList</tt>状态为已完成
	 */
	public boolean isDone() {
		return isDone;
	}
	
	/**
	 * 返回<tt>FutureList</tt>的<tt>Iterator</tt>。
	 * @return <tt>FutureList</tt>的<tt>Iterator</tt>
	 */
	public FutureIterator<E> iterator() {		
		return new FutureIterator<E>(this);
	}
	
	/**
	 * 返回当前<tt>FutureList</tt>的元素个数。
	 * @return 当前<tt>FutureList</tt>的元素个数
	 */
	public synchronized int size() {
		return list.size();
	}

	@Override
	public long updatedAt() {
		return updateAt;
	}

	@Override
	public boolean updatedSince(long lastUpdatedAt) {
		return updateAt > lastUpdatedAt;
	}
	
	private void updated() {
		updateAt = System.nanoTime();
	}

}
