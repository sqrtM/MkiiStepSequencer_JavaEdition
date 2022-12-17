package org.mason.MkIISeq;

public abstract class MessageBuilder {

    enum PadStatus {
        INACTIVE_OFF,
        INACTIVE_ON,
        ACTIVE_ON,
        ACTIVE_OFF
    }

    private final byte[] mkiiDefaultSysexMessage = {
            (byte) 0xF0, 0x00, 0x20, 0x6B, 0x7F, 0x42, 0x02, 0x00, 0x10, 0x70, 0x14, (byte) 0xF7
    };

    private final int PAD_ADDRESS = 9;
    private final int PAD_COLOR = 10;

    void selectMessageType(PadStatus status, int currentAddress) {
        byte[] outgoingMessage = mkiiDefaultSysexMessage.clone();
        switch (status) {
            case INACTIVE_OFF -> {

            }
            case INACTIVE_ON -> {

            }
            case ACTIVE_OFF -> {

            }
            case ACTIVE_ON -> {

            }
            default -> throw new IllegalStateException("Unexpected value: " + currentAddress);
        }
    }
}
