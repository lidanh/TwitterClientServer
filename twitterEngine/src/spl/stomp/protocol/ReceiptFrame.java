package spl.stomp.protocol;

/**
 * @name Receipt Frame
 * @author  Lidan Hifi
 * @author  Ken Saggy
 * 
 * RECEIPT stomp frame
 */
class ReceiptFrame extends StompFrame {
    public ReceiptFrame(String receiptId) {
        super(StompCommand.RECEIPT);
        addFrameHeader("receipt-id", receiptId);
    }
}
