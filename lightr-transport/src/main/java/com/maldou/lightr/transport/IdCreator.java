package com.maldou.lightr.transport;

import java.util.Calendar;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class IdCreator {
	private ReadWriteLock lock = new ReentrantReadWriteLock();
	private Calendar calendar;
	private long pretime; // 已经出现的时间
	private int preSeq; // 时间第一次使用时的序列号
	private int currentSeq = 0; // 当前序列号
	private int maxSeq = 0x200; // 1s内生成的最大id数

	private IdCreator() {
		calendar = Calendar.getInstance();
	}
	private static IdCreator instance = new IdCreator();
	public static IdCreator getInstance() {
		return instance;
	}

	/*
	 * 产生id serveid 为服务器编号id slat 为参数
	 */
	public long create(long serveid) {
		long sid = calcServer(serveid);
		long ct = 0;
		int seq = 0;
		lock.writeLock().lock();
		try {
			Calendar d = this.getCurrentTimeMillis();
			ct = getCurrentTime(d);
			if (ct == pretime) {
				seq = this.nextSeq();
				if (seq == preSeq) { // 已经有个的序列号
					ct = tailTime(ct);
				}
			} else if (ct > pretime) {
				seq = this.nextSeq();
				preSeq = seq;
			} else { // TODO 需不需要向前加时间，有待确定
			}
			pretime = ct;
		} finally {
			lock.writeLock().unlock();
		}
		ct <<= Constants.SEQ_L;
		ct |= seq;
//		ct <<= Constants.SERVCE_L;
//		ct |= sid;
		return ct;
	}

	private long calcServer(long serverid) {
		return serverid & Constants.SERVERRANGER;
	}

	private long tailTime(final long time) {
		Calendar d = this.getCurrentTimeMillis();
		long ct = getCurrentTime(d);
		while (ct <= time) {
			d = this.getCurrentTimeMillis();
			ct = this.getCurrentTime(d);
		}
		return ct;
	}

	private int nextSeq() {
		currentSeq++;
		if (currentSeq >= maxSeq) {
			currentSeq = 1;
		}
		return currentSeq;
	}

	public int getMaxSeq() {
		return maxSeq;
	}

	/*
*
*/
	private long getCurrentTime(Calendar calendar) {
		long t = 0l | (this.getYear(calendar) - Constants.FIXYEAR);
		t <<= getMonthMove();
		t = t | this.getMonth(calendar);
		t <<= this.getDayMove();
		t = t | this.getDay(calendar);
		t <<= this.getHourMove();
		t = t | this.getHour(calendar);
		t <<= this.getMinuteMove();
		t = t | this.getMinute(calendar);
		t <<= this.getSecondMove();
		t = t | this.getSecond(calendar);
		return t;
	}

	private int getMonthMove() {
		return Constants.MONTH_L;
	}

	private int getDayMove() {
		return Constants.DAY_L;
	}

	private int getHourMove() {
		return Constants.HOUR_L;
	}

	private int getMinuteMove() {
		return Constants.MINUTE_L;
	}

	private int getSecondMove() {
		return Constants.SECOND_L;
	}

	private int getYearMove() {
		return Constants.YEAR_L;
	}

	private Calendar getCurrentTimeMillis() {
		calendar.setTimeInMillis(System.currentTimeMillis());
		return calendar;
	}

	private int getYear(Calendar calendar) {
		return calendar.get(Calendar.YEAR);
	}

	private int getMonth(Calendar calendar) {
		return calendar.get(Calendar.MONTH);
	}

	private int getDay(Calendar calendar) {
		return calendar.get(Calendar.DAY_OF_MONTH);
	}

	private int getHour(Calendar calendar) {
		return calendar.get(Calendar.HOUR_OF_DAY);
	}

	private int getMinute(Calendar calendar) {
		return calendar.get(Calendar.MINUTE);
	}

	private int getSecond(Calendar calendar) {
		return calendar.get(Calendar.SECOND);
	}

	private Calendar getTimebyId_(long id) {
		Calendar cal = Calendar.getInstance();
		long second = id >>> this.getSecondMove();
		cal.set(Calendar.SECOND, (int) (id - (second << this.getSecondMove())));
		long minute = second >>> this.getMinuteMove();
		cal.set(Calendar.MINUTE,
				(int) (second - (minute << this.getMinuteMove())));
		long hour = minute >>> this.getHourMove();
		cal.set(Calendar.HOUR_OF_DAY,
				(int) (minute - (hour << this.getHourMove())));

		long day = hour >>> this.getDayMove();
		cal.set(Calendar.DAY_OF_MONTH,
				(int) (hour - (day << this.getDayMove())));
		long month = day >>> this.getMonthMove();
		cal.set(Calendar.MONTH, (int) (day - (month << this.getMonthMove())));
		long year = month >>> this.getYearMove();
		cal.set(Calendar.YEAR, (int) (month - (year << this.getYearMove()))
				+ Constants.FIXYEAR);
		return cal;
	}

	/*
	 * 根据id取出产生id的时间 id
	 */
	public Calendar getTimebyId(long id) {
		return getTimebyId_(id >>> (Constants.SEQ_L));
	}
	
	public long getSequnce(long id) {
		return id & 0x0f;
	}
	
	public static void main(String[] args) {
		IdCreator creator = new IdCreator();
		long id = (creator.create(10L));
		System.out.println(id);
		System.out.println(creator.getTimebyId(id).getTime());
		System.out.println(creator.getSequnce(id));
	}
}