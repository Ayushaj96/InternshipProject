package com.example.vasu.aismap;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.payumoney.core.PayUmoneyConfig;
import com.payumoney.core.PayUmoneyConstants;
import com.payumoney.core.PayUmoneySdkInitializer;
import com.payumoney.core.entity.TransactionResponse;
import com.payumoney.sdkui.ui.utils.PayUmoneyFlowManager;
import com.payumoney.sdkui.ui.utils.ResultModel;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashMap;

public class CartActivity extends AppCompatActivity {

    String fullName,mobile,emailId,username,address,machine_serial_no,access,status,company1,company2,type;
    int company1quantity , company2quantity ;

    Spinner companySpinner ;

    SharedPreferences sharedPreferences ;

    ImageButton ibIncQuantity , ibDecQuantity ;
    TextView tvCompany,tvType,tvQuantity,tvCost,tvTotalCost ;
    Button btnPay ;

    String productinfo = "" ;
    int cost = 0 , totalCost = 0;
    int chosenCompany = 0 ;

    CardView cardDetails ;

    String merchantId = "5932019" , merchantKey = "EFtpMftw" , merchantSalt = "m4cHTWKqoL"  ;
    private PayUmoneySdkInitializer.PaymentParam mPaymentParams;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);

        sharedPreferences=getApplicationContext().getSharedPreferences("MyInfo", MODE_PRIVATE);

        cardDetails = findViewById(R.id.cardView) ;

        companySpinner = findViewById(R.id.comapnySpinner);
        ibIncQuantity = findViewById(R.id.ibIncQuantity);
        ibDecQuantity = findViewById(R.id.ibDecQuantity);

        tvCompany = findViewById(R.id.tvCompany);
        tvType = findViewById(R.id.tvType);
        tvQuantity = findViewById(R.id.tvQuantity);
        tvCost = findViewById(R.id.tvCost);
        tvTotalCost = findViewById(R.id.tvTotalCost);

        btnPay = findViewById(R.id.btnPay);

        Intent i = getIntent();
        fullName = sharedPreferences.getString("FullName" , "") ;
        emailId = sharedPreferences.getString("Email" , "");
        mobile = sharedPreferences.getString("Mobile" , "");
        username = sharedPreferences.getString("Username" , "");

        machine_serial_no = i.getStringExtra("serialno");
        company1 = i.getStringExtra("company1");
        company1quantity = i.getIntExtra("company1quantity" , 0) ;
        company2 = i.getStringExtra("company2");
        company2quantity = i.getIntExtra("company2quantity" , 0) ;

        ArrayList<String> companies = new ArrayList<>();
        companies.add("Select a Company") ;
        companies.add(company1) ;
        companies.add(company2) ;

        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, companies);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        companySpinner.setAdapter(spinnerAdapter);

       companySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
           @Override
           public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
               String temp = ""+adapterView.getItemAtPosition(i) ;
                if (!temp.equalsIgnoreCase("Select a Company")){
                    productinfo = temp ;
                    totalCost = 0 ;
                    if (i == 1){
                        chosenCompany = 1 ;
                    }else if(i == 2){
                        chosenCompany = 2 ;
                    }
                    setValues(temp);
                }else {
                    productinfo = "" ;
                    if (cardDetails.getVisibility() == View.VISIBLE){
                        cardDetails.setVisibility(View.INVISIBLE);
                    }
                }

           }

           @Override
           public void onNothingSelected(AdapterView<?> adapterView) {

           }
       });

       ibIncQuantity.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View view) {
               int temp = Integer.parseInt(tvQuantity.getText().toString());
               if (temp < (chosenCompany == 1 ? company1quantity : company2quantity)){
                   temp += 1 ;
                   totalCost = temp*cost ;
                   tvQuantity.setText(""+temp);
                   tvTotalCost.setText("Total Cost : " + totalCost+"");
               }else {
                   Toast.makeText(CartActivity.this, "Cannot go grater than quantity", Toast.LENGTH_SHORT).show();
               }
           }
       });

        ibDecQuantity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int temp = Integer.parseInt(tvQuantity.getText().toString());
                if (temp > 0){
                    temp -= 1 ;
                    totalCost = temp*cost ;
                    tvQuantity.setText(""+temp);
                    tvTotalCost.setText("Total Cost : " + totalCost+"");
                }else {
                    Toast.makeText(CartActivity.this, "Cannot go less than 0", Toast.LENGTH_SHORT).show();
                }

            }
        });

        btnPay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (totalCost > 0){
                    launchPayUMoneyFlow();
                }else {
                    Toast.makeText(CartActivity.this, "Total Cost is 0", Toast.LENGTH_SHORT).show();
                }
            }
        });


    }

    public void setValues(String company){

        if (cardDetails.getVisibility() == View.INVISIBLE){
            cardDetails.setVisibility(View.VISIBLE);
        }

        tvCompany.setText(company.substring(0,company.indexOf("-")));

        if (company.contains("hd")){
            tvType.setText("Heavy Days");
            tvCost.setText("Rs. 10 per product");
            cost = 10 ;
        }else {
            tvType.setText("Light Days");
            tvCost.setText("Rs. 5 per product");
            cost = 5 ;
        }

        tvTotalCost.setText("0");
        tvQuantity.setText("0");
        totalCost = 0 ;

    }

    private void launchPayUMoneyFlow() {

        PayUmoneyConfig payUmoneyConfig = PayUmoneyConfig.getInstance();

        payUmoneyConfig.setDoneButtonText("Pay Now");
        payUmoneyConfig.setPayUmoneyActivityTitle("Payment Activity");
        PayUmoneySdkInitializer.PaymentParam.Builder builder = new PayUmoneySdkInitializer.PaymentParam.Builder();

        double amount = (double) 1 ;
        String txnId = System.currentTimeMillis() + "";
        String phone = mobile;
        String productName = productinfo;
        String firstName = fullName;
        String email = emailId;
        String udf1 = "";
        String udf2 = "";
        String udf3 = "";
        String udf4 = "";
        String udf5 = "";
        String udf6 = "";
        String udf7 = "";
        String udf8 = "";
        String udf9 = "";
        String udf10 = "";

        builder.setAmount(amount)
                .setTxnId(txnId)
                .setPhone(phone)
                .setProductName(productName)
                .setFirstName(firstName)
                .setEmail(email)
                .setsUrl("https://www.payumoney.com/mobileapp/payumoney/success.php")
                .setfUrl("https://www.payumoney.com/mobileapp/payumoney/failure.php")
                .setUdf1(udf1)
                .setUdf2(udf2)
                .setUdf3(udf3)
                .setUdf4(udf4)
                .setUdf5(udf5)
                .setUdf6(udf6)
                .setUdf7(udf7)
                .setUdf8(udf8)
                .setUdf9(udf9)
                .setUdf10(udf10)
                .setIsDebug(false)
                .setKey(merchantKey)
                .setMerchantId(merchantId);

        try {
            mPaymentParams = builder.build();

            mPaymentParams = calculateServerSideHashAndInitiatePayment1(mPaymentParams);

            PayUmoneyFlowManager.startPayUMoneyFlow(mPaymentParams,CartActivity.this, R.style.AppTheme_default, false);


        } catch (Exception e) {
            // some exception occurred
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    public static String hashCal(String str) {
        byte[] hashseq = str.getBytes();
        StringBuilder hexString = new StringBuilder();
        try {
            MessageDigest algorithm = MessageDigest.getInstance("SHA-512");
            algorithm.reset();
            algorithm.update(hashseq);
            byte messageDigest[] = algorithm.digest();
            for (byte aMessageDigest : messageDigest) {
                String hex = Integer.toHexString(0xFF & aMessageDigest);
                if (hex.length() == 1) {
                    hexString.append("0");
                }
                hexString.append(hex);
            }
        } catch (NoSuchAlgorithmException ignored) {
        }
        return hexString.toString();
    }

    private PayUmoneySdkInitializer.PaymentParam calculateServerSideHashAndInitiatePayment1(final PayUmoneySdkInitializer.PaymentParam paymentParam) {

        StringBuilder stringBuilder = new StringBuilder();
        HashMap<String, String> params = paymentParam.getParams();
        stringBuilder.append(params.get(PayUmoneyConstants.KEY) + "|");
        stringBuilder.append(params.get(PayUmoneyConstants.TXNID) + "|");
        stringBuilder.append(params.get(PayUmoneyConstants.AMOUNT) + "|");
        stringBuilder.append(params.get(PayUmoneyConstants.PRODUCT_INFO) + "|");
        stringBuilder.append(params.get(PayUmoneyConstants.FIRSTNAME) + "|");
        stringBuilder.append(params.get(PayUmoneyConstants.EMAIL) + "|");
        stringBuilder.append(params.get(PayUmoneyConstants.UDF1) + "|");
        stringBuilder.append(params.get(PayUmoneyConstants.UDF2) + "|");
        stringBuilder.append(params.get(PayUmoneyConstants.UDF3) + "|");
        stringBuilder.append(params.get(PayUmoneyConstants.UDF4) + "|");
        stringBuilder.append(params.get(PayUmoneyConstants.UDF5) + "||||||");

        stringBuilder.append(merchantSalt);

        String hash = hashCal(stringBuilder.toString());
        paymentParam.setMerchantHash(hash);

        return paymentParam;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result Code is -1 send from Payumoney activity
        Log.d("TRANSACTION", "request code " + requestCode + " resultcode " + resultCode);
        if (requestCode == PayUmoneyFlowManager.REQUEST_CODE_PAYMENT && resultCode == RESULT_OK && data !=
                null) {
            TransactionResponse transactionResponse = data.getParcelableExtra(PayUmoneyFlowManager
                    .INTENT_EXTRA_TRANSACTION_RESPONSE);

            ResultModel resultModel = data.getParcelableExtra(PayUmoneyFlowManager.ARG_RESULT);

            // Check which object is non-null
            if (transactionResponse != null && transactionResponse.getPayuResponse() != null) {
                if (transactionResponse.getTransactionStatus().equals(TransactionResponse.TransactionStatus.SUCCESSFUL)) {
                    Log.i("TRANSACTION" , "Success");
                    Toast.makeText(this, "Transaction Success", Toast.LENGTH_SHORT).show();
                } else {
                    Log.i("TRANSACTION" , "Failure");
                    Toast.makeText(this, "Transaction Failed", Toast.LENGTH_SHORT).show();
                }

                // Response from Payumoney
                String payuResponse = transactionResponse.getPayuResponse();

                // Response from SURl and FURL
                String merchantResponse = transactionResponse.getTransactionDetails();

                new AlertDialog.Builder(this)
                        .setCancelable(false)
                        .setMessage("Payu's Data : " + payuResponse + "\n\n\n Merchant's Data: " + merchantResponse)
                        .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                dialog.dismiss();
                            }
                        }).show();

            } else if (resultModel != null && resultModel.getError() != null) {
                Log.d("TRANSACTION", "Error response : " + resultModel.getError().getTransactionResponse());
            } else {
                Log.d("TRANSACTION", "Both objects are null!");
            }
        }
    }


}
