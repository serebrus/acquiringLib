package uz.ipakyulibank.acquiringlibrary;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.telephony.SmsMessage;
import android.util.Log;

/**
 * Created by adminn on 29.01.2016.
 */
public class IncomingSms extends BroadcastReceiver {
    // Get the object of SmsManager
    final SmsManager sms = SmsManager.getDefault();

    public static String senderNum = "empty";
    public static String message = "empty";

    public void onReceive(Context context, Intent intent) {

        // Retrieves a map of extended data from the intent.
        final Bundle bundle = intent.getExtras();

        try {
            if (bundle != null) {
                final Object[] pdusObj = (Object[]) bundle.get("pdus");

                for (int i = 0; i < pdusObj.length; i++) {

                    SmsMessage currentMessage = SmsMessage.createFromPdu((byte[]) pdusObj[i]);
                    String phoneNumber = currentMessage.getDisplayOriginatingAddress();

                    senderNum  = phoneNumber;
                    message = currentMessage.getDisplayMessageBody();

                    //Log.i("SmsReceiver", "senderNum: " + senderNum + "; message: " + message);

                    /* Show Alert
                    int duration = Toast.LENGTH_LONG;
                    Toast toast = Toast.makeText(context,
                            "senderNum: "+ senderNum + ", message: " + message, duration);
                    toast.show();
                    */

                } // end for loop
            } // bundle is null

        } catch (Exception e) {
            Log.e("SmsReceiver", "Exception smsReceiver" +e);
        }
    }

    public String getSenderNum() {
        return senderNum;
    }

    public String getMessage() {
        return message;
    }

    public void setSenderNum() {
        senderNum = "empty";
    }

    public void setMessage() {
        message = "empty";
    }

}
