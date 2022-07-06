package com.example.mypractice.commons.util;

import org.springframework.stereotype.Component;

/**
 * 雪花id生成器
 *
 * @author somebody
 */
@Component
public class SnowflakeIdGenerator {

    // ==============================Fields===========================================

    /**
     * 序列号所占位数、位移、掩码/极大值
     * 用MASK是为了不用求余而完成1到1023的循环，见nextId()
     * MASK的结构：1(符号位) 0……0(51个0) 1……1(12个1)
     */
    private static final long SEQUENCE_BITS = 12L;
    private static final long SEQUENCE_SHIFT = 0L;
    private static final long SEQUENCE_MASK = ~(-1L << SEQUENCE_BITS);

    /**
     * 机器的序号，占5位
     */
    private static final long WORKER_ID_BITS = 5L;
    private static final long WORKER_ID_SHIFT = SEQUENCE_BITS;
    private static final long WORKER_ID_MASK = ~(-1L << WORKER_ID_BITS);

    /**
     * 数据中心序号，占5位
     */
    private static final long DATA_CENTER_ID_BITS = 5L;
    private static final long DATA_CENTER_ID_SHIFT = SEQUENCE_BITS + WORKER_ID_BITS;
    private static final long DATA_CENTER_ID_MASK = ~(-1L << DATA_CENTER_ID_BITS);

    /**
     * 时间戳，占41位
     */
    private static final long TIME_STAMP_BITS = 41L;
    private static final long TIME_STAMP_LEFT_SHIFT = SEQUENCE_BITS + WORKER_ID_BITS + DATA_CENTER_ID_BITS;
    private static final long TIME_STAMP_MASK = ~(-1L << TIME_STAMP_BITS);

    /**
     * 开始时间截 (2022-06-26)
     */
    private static final long EPOCH = 1656248869000L;


    private long sequence = 0L;
    private long workerId;
    private long dataCenterId;

    /**
     * 上次生成 ID 的时间截，初始化为-1，防止第一次生成ID时序列化无法取0
     */
    private long lastTimestamp = -1L;

    //==============================Constructors=====================================

    public SnowflakeIdGenerator() {
        this(0, 0);
    }

    /**
     * 构造函数
     *
     * @param workerId     工作ID (0~31)
     * @param dataCenterId 数据中心 ID (0~31)
     */
    public SnowflakeIdGenerator(long workerId, long dataCenterId) {
        if (workerId > WORKER_ID_MASK || workerId < 0) {
            throw new IllegalArgumentException(String.format("workerId can't be greater than %d or less than 0", WORKER_ID_MASK));
        }

        if (dataCenterId > DATA_CENTER_ID_MASK || dataCenterId < 0) {
            throw new IllegalArgumentException(String.format("dataCenterId can't be greater than %d or less than 0", DATA_CENTER_ID_MASK));
        }

        this.workerId = workerId;
        this.dataCenterId = dataCenterId;
    }

    // ============================== Methods ==========================================

    /**
     * 获得下一个 ID (该方法是线程安全的，synchronized)
     */
    public synchronized long nextId() {
        long timestamp = timeGen();

        // 如果当前时间小于上一次 ID 生成的时间戳，说明系统时钟回退过，这个时候应当等待。
        // 出现这种现象是因为系统的时间被回拨，或出现闰秒现象。
        if (timestamp < lastTimestamp) {
            timestamp = tilNextMillis(timestamp, lastTimestamp);
        }

        // 如果是同一时间生成的，则进行毫秒内序列
        if (lastTimestamp == timestamp) {
            // 相同毫秒内，序列号自增
            // Mask最后12位全1，其他的非符号位全0，则从1到1023和它做与运算都得到自己，一旦到达1024，又变回0
            sequence = (sequence + 1) & SEQUENCE_MASK;
            // 毫秒内序列溢出，即，同一毫秒的序列数已经达到最大
            if (sequence == 0) {
                // 阻塞到下一个毫秒,获得新的时间戳
                timestamp = tilNextMillis(lastTimestamp);
            }
        }
        // 时间戳改变，毫秒内序列重置
        else {
            sequence = 0L;
        }

        // 将当前生成的时间戳记录为『上次时间戳』。『下次』生成时间戳时要用到。
        lastTimestamp = timestamp;

        // 移位并通过或运算拼到一起组成 64 位的 ID
        // 时间戳部分
        return ((timestamp - EPOCH) << TIME_STAMP_LEFT_SHIFT)
                // 数据中心部分
                | (dataCenterId << DATA_CENTER_ID_SHIFT)
                // 机器标识部分
                | (workerId << WORKER_ID_SHIFT)
                // 序列号部分
                | sequence;
    }

    /**
     * 阻塞到下一个毫秒，直到获得新的时间戳
     *
     * @param lastTimestamp 上次生成ID的时间截
     * @return 当前时间戳
     */
    protected long tilNextMillis(long lastTimestamp) {
        long timestamp = timeGen();
        while (timestamp <= lastTimestamp) {
            timestamp = timeGen();
        }
        return timestamp;
    }

    /**
     * 阻塞到下一个毫秒，直到获得新的时间戳
     *
     * @param timestamp     当前时间戳
     * @param lastTimestamp 上次生成ID的时间截
     * @return 当前时间戳
     */
    protected long tilNextMillis(long timestamp, long lastTimestamp) {
        while (timestamp <= lastTimestamp) {
            timestamp = timeGen();
        }
        return timestamp;
    }

    /**
     * 返回以毫秒为单位的当前时间
     *
     * @return 当前时间(毫秒)
     */
    protected long timeGen() {
        return System.currentTimeMillis();
    }
}