package com.example.vasu.aismap;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.payumoney.core.PayUmoneySdkInitializer;
import com.payumoney.core.entity.TransactionResponse;
import com.payumoney.sdkui.ui.utils.PayUmoneyFlowManager;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class PaymentActivty extends AppCompatActivity {

    Button btnPay ;

    // merchant key , salt , auth error
    String merchantId = "3432323243" , merchantKey = "234327461" , merchantSalt = "3H632G" , merchantAuthError = "" ;
    String transactionId = "12345" , amount = "1" , productinfo = "Product" , firstname = "Salman" , email = "aisindia@gmail.com" ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment_activty);

        btnPay =  (Button) findViewById(R.id.btnPay) ;

        String hashSequence = merchantKey+"|"+transactionId+"|"+amount+"|"+productinfo+"|"+firstname+"|"+
                email+"|"+"Machine"+"|"+"AIS"+"|"+"||||||"+merchantSalt;
        String serverCalculatedHash= hashCal("SHA-512", hashSequence);
        Toast.makeText(this, ""+serverCalculatedHash, Toast.LENGTH_SHORT).show();

        PayUmoneySdkInitializer.PaymentParam.Builder builder = new PayUmoneySdkInitializer.PaymentParam.Builder();
        builder.setAmount(Double.parseDouble(amount))                          // Payment amount
                .setTxnId(transactionId)                                             // Transaction ID
                .setPhone("+918447135908")                                           // User Phone number
                .setProductName(productinfo)                   // Product Name or description
                .setFirstName(firstname)                              // User First name
                .setEmail(email)
                .setsUrl("https://www.google.com/")
                .setfUrl("https://www.facebook.com/")
                .setUdf1("Machine")
                .setUdf2("AIS")
                .setIsDebug(true)                              // Integration environment - true (Debug)/ false(Production)
                .setKey(merchantKey)                        // Merchant key
                .setMerchantId(merchantId);

        PayUmoneySdkInitializer.PaymentParam paymentParam = null;
        try {
            paymentParam = builder.build();
            paymentParam.setMerchantHash(serverCalculatedHash);
        }catch (Exception e){
            Toast.makeText(this, ""+e, Toast.LENGTH_SHORT).show();
        }
        final PayUmoneySdkInitializer.PaymentParam finalPaymentParam = paymentParam;
        btnPay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (finalPaymentParam != null) {
                    PayUmoneyFlowManager.startPayUMoneyFlow(finalPaymentParam, PaymentActivty.this, R.style.AppTheme_default, false);
                }
                }
        });


    }

    public static String hashCal(String type, String hashString) {
        StringBuilder hash = new StringBuilder();
        MessageDigest messageDigest = null;
        try {
            messageDigest = MessageDigest.getInstance(type);
            messageDigest.update(hashString.getBytes());
            byte[] mdbytes = messageDigest.digest();
            for (byte hashByte : mdbytes) {
                hash.append(Integer.toString((hashByte & 0xff) + 0x100, 16).substring(1));
            }
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return hash.toString();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d("MainActivity", "request code " + requestCode + " resultcode " + resultCode);
        if (requestCode == PayUmoneyFlowManager.REQUEST_CODE_PAYMENT && resultCode == RESULT_OK && data != null) {
            TransactionResponse transactionResponse = data.getParcelableExtra(PayUmoneyFlowManager.INTENT_EXTRA_TRANSACTION_RESPONSE);
            if (transactionResponse != null && transactionResponse.getPayuResponse() != null) {
                if(transactionResponse.getTransactionStatus().equals(TransactionResponse.TransactionStatus.SUCCESSFUL)){
                    Toast.makeText(this, "Transaction Success", Toast.LENGTH_SHORT).show();
                    //Success Transaction
                } else{
                    Toast.makeText(this, "Transaction Failed", Toast.LENGTH_SHORT).show();
                    //Failure Transaction
                }
                String payuResponse = transactionResponse.getPayuResponse() ;
                String merchantResponse = transactionResponse.getTransactionDetails();
                Toast.makeText(this, "PayuResponse " + payuResponse, Toast.LENGTH_SHORT).show();
                Toast.makeText(this, "merchantResponse " + merchantResponse, Toast.LENGTH_SHORT).show();
            }else {
                Log.d("PAYMENT", "Both objects are null!");
            }
        }
    }

}
