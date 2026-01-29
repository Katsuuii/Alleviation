//package Controller;
//
//import OrderReceiptLogic.Receipt;
//import javafx.fxml.FXML;
//import javafx.scene.control.TextArea;
//
//public class ReceiptController {
//
//    @FXML
//    private TextArea receiptArea;
//
//    private Receipt receipt;
//
//    public void setReceipt(Receipt receipt) {
//        this.receipt = receipt;
//        showReceipt();
//    }
//
//    private void showReceipt() {
//        if (receipt != null) {
//            receiptArea.setText(receipt.toPrintableText());
//        }
//    }
//}
