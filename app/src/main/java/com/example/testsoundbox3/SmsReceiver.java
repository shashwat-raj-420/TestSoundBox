package com.example.testsoundbox3;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsMessage;
import android.util.Log;
import android.widget.Toast;

import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;



public class SmsReceiver extends BroadcastReceiver {

    private static SmsListener mListener;
    private String bankName;
    Boolean b=false;


    @Override
    public void onReceive(Context context, Intent intent) {
        Bundle data = intent.getExtras();
        Object[] pdus = (Object[]) data.get("pdus");
        for (Object o : pdus) {
            SmsMessage smsMessage = SmsMessage.createFromPdu((byte[]) o);
            String sender = smsMessage.getDisplayOriginatingAddress();

            String messageBody = smsMessage.getDisplayMessageBody();
            messageBody = messageBody.toLowerCase(Locale.ROOT);

            Log.d("data4", "data 4");

            if (messageBody.contains("credited")){
                int rupeesInWord = rupees_in_word(messageBody);
                if (rupeesInWord==1) {
                    received_money(messageBody, sender, 1);
                }else if (rupeesInWord==2) {
                    Log.d("data6", "data 6");
                    received_money(messageBody, sender, 2);
                }else {
                    received_money(messageBody,sender,0);
                }

            }

        }
    }
    public int rupees_in_word(String message){
        if (message.contains("credited") && message.contains("inr")){
            return 2;
        } else if (message.contains("credited") && message.contains("rs.")){
            Log.d("data9", "data 9");
            return 1;
        } else {
            return 0;
        }
    }

    public void received_money(String messageBody , String sender , int rupee_sign){
        String tempMessage = messageBody.replaceAll(" ","");
        String message = tempMessage.replaceAll(",","");
        if (rupee_sign == 1) {
            Pattern pattern = Pattern.compile("rs.(\\d+(\\.\\d+)?)");
            Matcher matcher = pattern.matcher(message);
            if (matcher.find()) {
                Log.d("data10", "data 10");
                String match = matcher.group(1); // Extract the matched group
                double moneyInRupees = Double.parseDouble(match);
                Log.d("data11", "received ₹" + Double.toString(moneyInRupees) + " from " +sender);
                mListener.messageReceived("received ₹" + Double.toString(moneyInRupees), sender);
            }
        }
        else if (rupee_sign == 2) {
            Log.d("data7", "data 7");
            Pattern pattern = Pattern.compile("inr(\\d+(\\.\\d+)?)");
            Matcher matcher = pattern.matcher(message);
            if (matcher.find()) {
                Log.d("data8", "data 8");
                String match = matcher.group(1); // Extract the matched group
                double moneyInRupees = Double.parseDouble(match);
                mListener.messageReceived("received ₹" + Double.toString(moneyInRupees), sender);
            }
        }
        else {
            Pattern pattern = Pattern.compile("₹(\\d+(\\.\\d+)?)");
            Matcher matcher = pattern.matcher(message);
            if (matcher.find()) {
                String match = matcher.group(1); // Extract the matched group
                double moneyInRupees = Double.parseDouble(match);
                mListener.messageReceived("received ₹" + Double.toString(moneyInRupees), sender);
            }
        }
    }

    public static void bindListener(SmsListener listener) {
        mListener = listener;
    }
}