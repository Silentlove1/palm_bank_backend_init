package com.life.bank.palm.common.utils;

/**
 * 雪花算法ID生成器
 */
public class SnowflakeIdGenerator {

    private static final long START_TIMESTAMP = 1609459200000L; // 2021-01-01

    private static final long SEQUENCE_BIT = 12;
    private static final long MACHINE_BIT = 5;
    private static final long DATACENTER_BIT = 5;

    private static final long MAX_DATACENTER_NUM = ~(-1L << DATACENTER_BIT);
    private static final long MAX_MACHINE_NUM = ~(-1L << MACHINE_BIT);
    private static final long MAX_SEQUENCE = ~(-1L << SEQUENCE_BIT);

    private static final long MACHINE_LEFT = SEQUENCE_BIT;
    private static final long DATACENTER_LEFT = SEQUENCE_BIT + MACHINE_BIT;
    private static final long TIMESTAMP_LEFT = DATACENTER_LEFT + DATACENTER_BIT;

    private long datacenterId = 1;
    private long machineId = 1;
    private long sequence = 0L;
    private long lastTimestamp = -1L;

    private static SnowflakeIdGenerator instance = new SnowflakeIdGenerator();

    public static SnowflakeIdGenerator getInstance() {
        return instance;
    }

    public synchronized long nextId() {
        long currTimestamp = getNewTimestamp();

        if (currTimestamp < lastTimestamp) {
            throw new RuntimeException("时钟回拨，拒绝生成ID");
        }

        if (currTimestamp == lastTimestamp) {
            sequence = (sequence + 1) & MAX_SEQUENCE;
            if (sequence == 0L) {
                currTimestamp = getNextMill();
            }
        } else {
            sequence = 0L;
        }

        lastTimestamp = currTimestamp;

        return (currTimestamp - START_TIMESTAMP) << TIMESTAMP_LEFT
                | datacenterId << DATACENTER_LEFT
                | machineId << MACHINE_LEFT
                | sequence;
    }

    private long getNextMill() {
        long mill = getNewTimestamp();
        while (mill <= lastTimestamp) {
            mill = getNewTimestamp();
        }
        return mill;
    }

    private long getNewTimestamp() {
        return System.currentTimeMillis();
    }
}