package org.maxur.ddd.commons.domain;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;
import java.net.NetworkInterface;
import java.nio.BufferUnderflowException;
import java.nio.ByteBuffer;
import java.security.SecureRandom;
import java.util.Date;
import java.util.Enumeration;
import java.util.concurrent.atomic.AtomicInteger;

import static com.google.common.base.Strings.isNullOrEmpty;

/**
 * <p>A globally unique identifier for objects.</p>
 * <p>
 * <p>Consists of 12 bytes, divided as follows:</p>
 * <table border="1">
 * <caption>ObjectID layout</caption>
 * <tr>
 * <td>0</td><td>1</td><td>2</td><td>3</td><td>4</td><td>5</td><td>6</td><td>7</td><td>8</td><td>9</td><td>10</td><td>11</td>
 * </tr>
 * <tr>
 * <td colspan="4">time</td><td colspan="3">machine</td> <td colspan="2">pid</td><td colspan="3">inc</td>
 * </tr>
 * </table>
 * <p>
 * <p>Instances of this class are immutable.</p>
 *
 * @mongodb.driver.manual core/object-id ObjectId
 */
public final class Id<T> implements Comparable<Id>, Serializable {

    private static final long serialVersionUID = 3670079982654483072L;

    private static Logger logger = LoggerFactory.getLogger(Id.class);

    private static final int LOW_ORDER_THREE_BYTES = 0x00ffffff;

    private static final int MACHINE_IDENTIFIER;

    private static final short PROCESS_IDENTIFIER;

    private static final AtomicInteger NEXT_COUNTER = new AtomicInteger(new SecureRandom().nextInt());

    private static final char[] HEX_CHARS = new char[]{
            '0', '1', '2', '3', '4', '5', '6', '7',
            '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'};

    private final int timestamp;
    private final int machineIdentifier;
    private final short processIdentifier;
    private final int counter;

    /**
     * Gets a new object id.
     *
     * @return the new id
     */
    public static <S> Id<S> get() {
        return new Id<>();
    }

    /**
     * Checks if a string could be an {@code Id}.
     *
     * @param hexString a potential Id as a String.
     * @return whether the string could be an object id
     * @throws IllegalArgumentException if hexString is null
     */
    public static boolean isValid(final String hexString) {
        if (hexString == null) {
            throw new IllegalArgumentException();
        }

        int len = hexString.length();
        if (len != 24) {
            return false;
        }

        for (int i = 0; i < len; i++) {
            char c = hexString.charAt(i);
            if (c >= '0' && c <= '9') {
                continue;
            }
            if (c >= 'a' && c <= 'f') {
                continue;
            }
            if (c >= 'A' && c <= 'F') {
                continue;
            }

            return false;
        }

        return true;
    }

    /**
     * Gets the generated machine identifier.
     *
     * @return an int representing the machine identifier
     */
    public static int getGeneratedMachineIdentifier() {
        return MACHINE_IDENTIFIER;
    }

    /**
     * Gets the generated process identifier.
     *
     * @return the process id
     */
    public static int getGeneratedProcessIdentifier() {
        return PROCESS_IDENTIFIER;
    }

    /**
     * Gets the current value of the auto-incrementing counter.
     *
     * @return the current counter value.
     */
    public static int getCurrentCounter() {
        return NEXT_COUNTER.get();
    }

    /**
     * <p>Creates an Id using time, machine and inc values.  The Java driver used to create all ObjectIds this way, but it does not
     * match the <a href="http://docs.mongodb.org/manual/reference/object-id/">Id specification</a>, which requires four values, not
     * three. This major release of the Java driver conforms to the specification, but still supports clients that are relying on the
     * behavior of the previous major release by providing this explicit factory method that takes three parameters instead of four.</p>
     * <p>
     * <p>Ordinary users of the driver will not need this method.  It's only for those that have written there own BSON decoders.</p>
     * <p>
     * <p>NOTE: This will not break any application that use ObjectIds.  The 12-byte representation will be round-trippable from old to new
     * driver releases.</p>
     *
     * @param time    time in seconds
     * @param machine machine ID
     * @param inc     incremental value
     * @return a new {@code Id} created from the given values
     * @since 2.12.0
     */
    public static <S> Id<S> createFromLegacyFormat(final int time, final int machine, final int inc) {
        return new Id<>(time, machine, inc);
    }

    /**
     * Create a new object id.
     */
    public Id() {
        this(new Date());
    }

    /**
     * Constructs a new instance using the given date.
     *
     * @param date the date
     */
    public Id(final Date date) {
        this(dateToTimestampSeconds(date), MACHINE_IDENTIFIER, PROCESS_IDENTIFIER, NEXT_COUNTER.getAndIncrement(), false);
    }

    /**
     * Constructs a new instances using the given date and counter.
     *
     * @param date    the date
     * @param counter the counter
     * @throws IllegalArgumentException if the high order byte of counter is not zero
     */
    public Id(final Date date, final int counter) {
        this(date, MACHINE_IDENTIFIER, PROCESS_IDENTIFIER, counter);
    }

    /**
     * Constructs a new instances using the given date, machine identifier, process identifier, and counter.
     *
     * @param date              the date
     * @param machineIdentifier the machine identifier
     * @param processIdentifier the process identifier
     * @param counter           the counter
     * @throws IllegalArgumentException if the high order byte of machineIdentifier or counter is not zero
     */
    public Id(final Date date, final int machineIdentifier, final short processIdentifier, final int counter) {
        this(dateToTimestampSeconds(date), machineIdentifier, processIdentifier, counter);
    }

    /**
     * Creates an Id using the given time, machine identifier, process identifier, and counter.
     *
     * @param timestamp         the time in seconds
     * @param machineIdentifier the machine identifier
     * @param processIdentifier the process identifier
     * @param counter           the counter
     * @throws IllegalArgumentException if the high order byte of machineIdentifier or counter is not zero
     */
    public Id(final int timestamp, final int machineIdentifier, final short processIdentifier, final int counter) {
        this(timestamp, machineIdentifier, processIdentifier, counter, true);
    }

    private Id(final int timestamp, final int machineIdentifier, final short processIdentifier, final int counter,
               final boolean checkCounter) {
        if ((machineIdentifier & 0xff000000) != 0) {
            throw new IllegalArgumentException("The machine identifier must be between 0 and 16777215 (it must fit in three bytes).");
        }
        if (checkCounter && ((counter & 0xff000000) != 0)) {
            throw new IllegalArgumentException("The counter must be between 0 and 16777215 (it must fit in three bytes).");
        }
        this.timestamp = timestamp;
        this.machineIdentifier = machineIdentifier;
        this.processIdentifier = processIdentifier;
        this.counter = counter & LOW_ORDER_THREE_BYTES;
    }

    /**
     * Constructs a new instance from a 24-byte hexadecimal string representation.
     *
     * @param hexString the string to convert
     * @throws IllegalArgumentException if the string is not a valid hex string representation of an Id
     */
    public Id(final String hexString) {
        this(parseHexString(hexString));
    }

    /**
     * Constructs a new instance from the given byte array
     *
     * @param bytes the byte array
     * @throws IllegalArgumentException if array is null or not of length 12
     */
    public Id(final byte[] bytes) {
        if (bytes == null) {
            throw new IllegalArgumentException();
        }
        if (bytes.length != 12) {
            throw new IllegalArgumentException("need 12 bytes");
        }

        timestamp = makeInt(bytes[0], bytes[1], bytes[2], bytes[3]);
        machineIdentifier = makeInt((byte) 0, bytes[4], bytes[5], bytes[6]);
        processIdentifier = (short) makeInt((byte) 0, (byte) 0, bytes[7], bytes[8]);
        counter = makeInt((byte) 0, bytes[9], bytes[10], bytes[11]);
    }

    /**
     * Creates an Id
     *
     * @param timestamp                   time in seconds
     * @param machineAndProcessIdentifier machine and process identifier
     * @param counter                     incremental value
     */
    Id(final int timestamp, final int machineAndProcessIdentifier, final int counter) {
        this(legacyToBytes(timestamp, machineAndProcessIdentifier, counter));
    }

    public static <S> Id<S> id(String id) throws BusinessException {
        return new Id<>(checkId(id));
    }

    private static String checkId(String id) throws BusinessException {
        if (isNullOrEmpty(id)) {
            throw new BusinessException("Identifier must not be empty");
        }
        if (!isValid(id)) {
            throw new BusinessException("Identifier %s is invalid", id);
        }
        return id;
    }

    private static byte[] legacyToBytes(final int timestamp, final int machineAndProcessIdentifier, final int counter) {
        byte[] bytes = new byte[12];
        bytes[0] = int3(timestamp);
        bytes[1] = int2(timestamp);
        bytes[2] = int1(timestamp);
        bytes[3] = int0(timestamp);
        bytes[4] = int3(machineAndProcessIdentifier);
        bytes[5] = int2(machineAndProcessIdentifier);
        bytes[6] = int1(machineAndProcessIdentifier);
        bytes[7] = int0(machineAndProcessIdentifier);
        bytes[8] = int3(counter);
        bytes[9] = int2(counter);
        bytes[10] = int1(counter);
        bytes[11] = int0(counter);
        return bytes;
    }

    /**
     * Convert to a byte array.  Note that the numbers are stored in big-endian order.
     *
     * @return the byte array
     */
    public byte[] toByteArray() {
        byte[] bytes = new byte[12];
        bytes[0] = int3(timestamp);
        bytes[1] = int2(timestamp);
        bytes[2] = int1(timestamp);
        bytes[3] = int0(timestamp);
        bytes[4] = int2(machineIdentifier);
        bytes[5] = int1(machineIdentifier);
        bytes[6] = int0(machineIdentifier);
        bytes[7] = short1(processIdentifier);
        bytes[8] = short0(processIdentifier);
        bytes[9] = int2(counter);
        bytes[10] = int1(counter);
        bytes[11] = int0(counter);
        return bytes;
    }

    /**
     * Gets the timestamp (number of seconds since the Unix epoch).
     *
     * @return the timestamp
     */
    public int getTimestamp() {
        return timestamp;
    }

    /**
     * Gets the machine identifier.
     *
     * @return the machine identifier
     */
    public int getMachineIdentifier() {
        return machineIdentifier;
    }

    /**
     * Gets the process identifier.
     *
     * @return the process identifier
     */
    public short getProcessIdentifier() {
        return processIdentifier;
    }

    /**
     * Gets the counter.
     *
     * @return the counter
     */
    public int getCounter() {
        return counter;
    }

    /**
     * Gets the timestamp as a {@code Date} instance.
     *
     * @return the Date
     */
    public Date getDate() {
        return new Date(timestamp * 1000L);
    }

    /**
     * Converts this instance into a 24-byte hexadecimal string representation.
     *
     * @return a string representation of the Id in hexadecimal format
     */
    public String asString() {
        char[] chars = new char[24];
        int i = 0;
        for (byte b : toByteArray()) {
            chars[i++] = HEX_CHARS[b >> 4 & 0xF];
            chars[i++] = HEX_CHARS[b & 0xF];
        }
        return new String(chars);
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Id id = (Id) o;
        return counter == id.counter &&
                machineIdentifier == id.machineIdentifier &&
                processIdentifier == id.processIdentifier &&
                timestamp == id.timestamp;

    }

    @Override
    public int hashCode() {
        int result = timestamp;
        result = 31 * result + machineIdentifier;
        result = 31 * result + (int) processIdentifier;
        result = 31 * result + counter;
        return result;
    }

    @Override
    public int compareTo(final Id other) {
        if (other == null) {
            throw new NullPointerException();
        }

        byte[] byteArray = toByteArray();
        byte[] otherByteArray = other.toByteArray();
        for (int i = 0; i < 12; i++) {
            if (byteArray[i] != otherByteArray[i]) {
                return ((byteArray[i] & 0xff) < (otherByteArray[i] & 0xff)) ? -1 : 1;
            }
        }
        return 0;
    }

    @Override
    public String toString() {
        return asString();
    }

    // Deprecated methods

    /**
     * Gets the time of this ID, in seconds.
     *
     * @return the time component of this ID in seconds
     * @deprecated Use #getTimestamp instead
     */
    @Deprecated
    public int getTimeSecond() {
        return timestamp;
    }

    /**
     * Gets the time of this instance, in milliseconds.
     *
     * @return the time component of this ID in milliseconds
     * @deprecated Use #getDate instead
     */
    @Deprecated
    public long getTime() {
        return timestamp * 1000L;
    }

    /**
     * @return a string representation of the Id in hexadecimal format
     * @see Id#asString()
     * @deprecated use {@link #asString()}
     */
    @Deprecated
    public String toStringMongod() {
        return asString();
    }

    static {
        try {
            MACHINE_IDENTIFIER = createMachineIdentifier();
            PROCESS_IDENTIFIER = createProcessIdentifier();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private static int createMachineIdentifier() {
        // build a 2-byte machine piece based on NICs info
        int machinePiece;
        try {
            StringBuilder sb = new StringBuilder();
            Enumeration<NetworkInterface> e = NetworkInterface.getNetworkInterfaces();
            while (e.hasMoreElements()) {
                NetworkInterface ni = e.nextElement();
                sb.append(ni.toString());
                byte[] mac = ni.getHardwareAddress();
                if (mac != null) {
                    ByteBuffer bb = ByteBuffer.wrap(mac);
                    try {
                        sb.append(bb.getChar());
                        sb.append(bb.getChar());
                        sb.append(bb.getChar());
                    } catch (BufferUnderflowException shortHardwareAddressException) {
                        /*
                        * NOPMD
                        * mac with less than 6 bytes. continue
                        */
                    }
                }
            }
            machinePiece = sb.toString().hashCode();
        } catch (Throwable t) {
            // exception sometimes happens with IBM JVM, use random
            machinePiece = (new SecureRandom().nextInt());
            logger.warn("Failed to get machine identifier from network interface, using random number instead", t);
        }
        machinePiece = machinePiece & LOW_ORDER_THREE_BYTES;
        return machinePiece;
    }

    // Creates the process identifier.  This does not have to be unique per class loader because
    // NEXT_COUNTER will provide the uniqueness.
    private static short createProcessIdentifier() {
        short processId;
        try {
            String processName = java.lang.management.ManagementFactory.getRuntimeMXBean().getName();
            if (processName.contains("@")) {
                processId = (short) Integer.parseInt(processName.substring(0, processName.indexOf('@')));
            } else {
                processId = (short) java.lang.management.ManagementFactory.getRuntimeMXBean().getName().hashCode();
            }

        } catch (Throwable t) {
            processId = (short) new SecureRandom().nextInt();
            logger.warn("Failed to get process identifier from JMX, using random number instead", t);
        }

        return processId;
    }

    private static byte[] parseHexString(final String s) {
        if (!isValid(s)) {
            throw new IllegalArgumentException("invalid hexadecimal representation of an Id: [" + s + "]");
        }

        byte[] b = new byte[12];
        for (int i = 0; i < b.length; i++) {
            b[i] = (byte) Integer.parseInt(s.substring(i * 2, i * 2 + 2), 16);
        }
        return b;
    }

    private static int dateToTimestampSeconds(final Date time) {
        return (int) (time.getTime() / 1000);
    }

    // Big-Endian helpers, in this class because all other BSON numbers are little-endian

    private static int makeInt(final byte b3, final byte b2, final byte b1, final byte b0) {
        // CHECKSTYLE:OFF
        return (((b3) << 24) |
                ((b2 & 0xff) << 16) |
                ((b1 & 0xff) << 8) |
                ((b0 & 0xff)));
        // CHECKSTYLE:ON
    }

    private static byte int3(final int x) {
        return (byte) (x >> 24);
    }

    private static byte int2(final int x) {
        return (byte) (x >> 16);
    }

    private static byte int1(final int x) {
        return (byte) (x >> 8);
    }

    private static byte int0(final int x) {
        return (byte) (x);
    }

    private static byte short1(final short x) {
        return (byte) (x >> 8);
    }

    private static byte short0(final short x) {
        return (byte) (x);
    }
}

