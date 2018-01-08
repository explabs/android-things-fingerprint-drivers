package space.huttka.androidthings.driver.r301t;

/**
 * @author leon0399
 */

public class R301tPacket {

    public static final char FINGERPRINT_STARTCODE = 0xEF01;

    public static final int FINGERPRINT_ADDER = 0xFFFFFFFF;

    public static final byte FINGERPRINT_COMMANDPACKET = 0x1;
    public static final byte FINGERPRINT_DATAPACKET = 0x2;
    public static final byte FINGERPRINT_ACKPACKET = 0x7;
    public static final byte FINGERPRINT_ENDDATAPACKET = 0x8;

    public static final byte FINGERPRINT_OK = 0x00;
    public static final byte FINGERPRINT_PACKETRECIEVEERR = 0x01;
    public static final byte FINGERPRINT_NOFINGER = 0x02;
    public static final byte FINGERPRINT_IMAGEFAIL = 0x03;
    public static final byte FINGERPRINT_IMAGEMESS = 0x06;
    public static final byte FINGERPRINT_FEATUREFAIL = 0x07;
    public static final byte FINGERPRINT_NOMATCH = 0x08;
    public static final byte FINGERPRINT_NOTFOUND = 0x09;
    public static final byte FINGERPRINT_ENROLLMISMATCH = 0x0A;
    public static final byte FINGERPRINT_BADLOCATION = 0x0B;
    public static final byte FINGERPRINT_DBRANGEFAIL = 0x0C;
    public static final byte FINGERPRINT_UPLOADFEATUREFAIL = 0x0D;
    public static final byte FINGERPRINT_PACKETRESPONSEFAIL = 0x0E;
    public static final byte FINGERPRINT_UPLOADFAIL = 0x0F;
    public static final byte FINGERPRINT_DELETEFAIL = 0x10;
    public static final byte FINGERPRINT_DBCLEARFAIL = 0x11;
    public static final byte FINGERPRINT_PASSFAIL = 0x13;
    public static final byte FINGERPRINT_INVALIDIMAGE = 0x15;
    public static final byte FINGERPRINT_FLASHERR = 0x18;
    public static final byte FINGERPRINT_INVALIDREG = 0x1A;
    public static final byte FINGERPRINT_ADDRCODE = 0x20;
    public static final byte FINGERPRINT_PASSVERIFY = 0x21;

    public static final int FINGERPRINT_TIMEOUT = 0xFF;
    public static final int FINGERPRINT_BADPACKET = 0xFE;

    public static final byte FINGERPRINT_GETIMAGE = 0x01;
    public static final byte FINGERPRINT_IMAGE2TZ = 0x02;
    public static final byte FINGERPRINT_REGMODEL = 0x05;
    public static final byte FINGERPRINT_STORE = 0x06;
    public static final byte FINGERPRINT_LOAD = 0x07;
    public static final byte FINGERPRINT_UPLOAD = 0x08;
    public static final byte FINGERPRINT_DELETE = 0x0C;
    public static final byte FINGERPRINT_EMPTY = 0x0D;
    public static final byte FINGERPRINT_SETPASSWORD = 0x12;
    public static final byte FINGERPRINT_VERIFYPASSWORD = 0x13;
    public static final byte FINGERPRINT_HISPEEDSEARCH = 0x1B;
    public static final byte FINGERPRINT_TEMPLATECOUNT = 0x1D;

    /**
     * "Header" in Data package format
     */
    public char start_code;

    /**
     * "Adder" in Data package format
     */
    public int address;

    /**
     * "Package identifier" in Data package format
     */
    public byte type;

    /**
     * "Package length" in Data package format
     */
    public char length;

    /**
     * "Package contents" in Data package format
     */
    public byte data[] = new byte[64];

    public R301tPacket(byte type, char length, byte[] data) {
        this.start_code = FINGERPRINT_STARTCODE;
        this.address = FINGERPRINT_ADDER;
        this.type = type;
        this.length = length;

        if(length < 64)
            System.arraycopy(data, 0, this.data, 0, length);
        else
            System.arraycopy(data, 0, this.data, 0, 64);
    }


}
