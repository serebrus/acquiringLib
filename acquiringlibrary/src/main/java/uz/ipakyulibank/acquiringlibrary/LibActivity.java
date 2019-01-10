package uz.ipakyulibank.acquiringlibrary;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.whiteelephant.monthpicker.MonthPickerDialog;

import java.text.NumberFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Random;

public class LibActivity extends AppCompatActivity implements AsyncResponse, DataExchangeWithServer {
    protected LibActivity slf;
    protected String server_url = "https://wi.ipakyulibank.uz/acquiring/hJaAGAA/Uz5QszX1kA9J6C6A7UtYScICvmVZ/mobile/";
    protected String logo_url = server_url + "logos/";

    protected String genID;
    protected String sessionName;
    protected String app_key;
    protected String transactionID;
    protected String amount;
    protected Double real_amount;
    protected String terminal_num;
    protected String lang;
    protected String url_success;
    protected String url_fail;
    protected String url_redirect;

    protected static int MAX_LENGTH = 16;
    protected boolean goBack = true;
    protected Button btn;
    protected Button btn_pay;
    protected EditText sc;

    protected boolean flgForExit_ptp = false;
    protected HashMap<String, String> banks = new HashMap<>();
    protected HashMap<String, Integer> banks_logo = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lib);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        slf = this;
        final String[] card_data = new String[1];
        final String[] c_m = new String[1];
        final int[] c_y = new int[1];
        final EditText ce = findViewById(R.id.card_exp);

        banks.put("14", "IPAK YO`LI BANKI");
        banks.put("03", "O`ZSANOATQURILISHBANK");
        banks.put("33", "IPOTEKA BANK");
        banks.put("53", "Infin BANK");
        banks.put("56", "Hi-Tech Bank");
        banks.put("06", "XALQ BANKI");

        banks_logo.put("14", R.drawable.b14);
        banks_logo.put("03", R.drawable.b03);
        banks_logo.put("33", R.drawable.b33);
        banks_logo.put("56", R.drawable.b56);

        Intent incom = getIntent();
        if (incom.hasExtra("app_key") && incom.hasExtra("transactionID") && incom.hasExtra("amount")) {
            app_key = incom.getStringExtra("app_key");
            transactionID = incom.getStringExtra("transactionID");
            amount = incom.getStringExtra("amount");
            real_amount = Double.parseDouble(amount);
            terminal_num = incom.getStringExtra("terminal_num");
            lang = incom.getStringExtra("lang");
            url_success = incom.getStringExtra("url_success");
            url_fail = incom.getStringExtra("url_fail");
            url_redirect = incom.getStringExtra("url_redirect");

            new DownloadImageTask((ImageView) findViewById(R.id.imgLogo)).execute(logo_url + app_key + ".png");

            TextView tv = findViewById(R.id.textView);
            String pre_num = NumberFormat.getCurrencyInstance().format((real_amount/100));
            String num = pre_num.replaceAll("[а-яёА-ЯЁ]+\\.", "Сум");
            tv.setText(num);

            Calendar today = Calendar.getInstance();
            final MonthPickerDialog.Builder builder = new MonthPickerDialog.Builder(LibActivity.this, new MonthPickerDialog.OnDateSetListener() {
                @Override
                public void onDateSet(int selectedMonth, int selectedYear) {

                }
            }, today.get(Calendar.YEAR), today.get(Calendar.MONTH));
            builder.setActivatedMonth(Calendar.JULY)
                    .setMinYear(1990)
                    .setActivatedYear(2017)
                    .setMaxYear(2030)
                    .setMinMonth(Calendar.FEBRUARY)
                    .setTitle("Срок карты")
                    .setMonthRange(Calendar.FEBRUARY, Calendar.NOVEMBER)
                    // .setMaxMonth(Calendar.OCTOBER)
                    // .setYearRange(1890, 1890)
                    // .setMonthAndYearRange(Calendar.FEBRUARY, Calendar.OCTOBER, 1890, 1890)
                    //.showMonthOnly()
                    // .showYearOnly()
                    .setOnMonthChangedListener(new MonthPickerDialog.OnMonthChangedListener() {
                        @Override
                        public void onMonthChanged(int selectedMonth) {
                            int m = selectedMonth + 1;
                            c_m[0] = m < 10 ? "0" + m : m + "";
                            card_data[0] = c_y[0] + "/" + c_m[0];
                            ce.setText(card_data[0]);
                        }
                    })
                    .setOnYearChangedListener(new MonthPickerDialog.OnYearChangedListener() {
                        @Override
                        public void onYearChanged(int selectedYear) {
                            if (selectedYear >= 2000) {
                                c_y[0] = selectedYear - 2000;
                            } else {
                                c_y[0] = selectedYear - 1900;
                            }
                            card_data[0] = c_y[0] + "/" + c_m[0];
                            ce.setText(card_data[0]);
                        }
                    })
                /*.build()
                .show()*/;

            final EditText cn = findViewById(R.id.card_num);
            cn.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void afterTextChanged(Editable editable) {
                    String txt = cn.getText().toString();
                    if (txt.trim().length() == 19) {
                        InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                        if (imm != null) {
                            imm.hideSoftInputFromWindow(cn.getWindowToken(), 0);
                        }

                        builder.build().show();
                    }

                    if (editable.toString().length() == 4 || editable.toString().length() == 9 || editable.toString().length() == 14) {
                        String new_cn = editable.toString() + " ";
                        cn.setText(new_cn);
                        cn.setSelection(editable.toString().length() + 1);
                    } else if (editable.toString().length() == 7) {
                        String part = editable.toString().substring(editable.toString().lastIndexOf(" ") + 1);
                        if (banks_logo.containsKey(part)) {
                            ImageView bank_logo = findViewById(R.id.idBankLogo);
                            bank_logo.setImageResource(banks_logo.get(part));
                        }
                    }
                }
            });

            ce.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void afterTextChanged(Editable editable) {
                    String txt = ce.getText().toString();
                    if (txt.trim().length() == 6) {
                        InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                        if (imm != null) {
                            imm.hideSoftInputFromWindow(ce.getWindowToken(), 0);
                        }
                    }
                }
            });

            btn = findViewById(R.id.btn_get_otp);
            btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (cn.getText().toString().length() == 19) {
                        String iv = random();
                        String SecretKey = random();
                        try {
                            String plainText = cn.getText().toString() + ":" + ce.getText().toString();
                            MCrypt _crypt = new MCrypt(iv, SecretKey);
                            String encrypted = MCrypt.bytesToHex(_crypt.encrypt(plainText));
                            String decrypted = new String(_crypt.decrypt(encrypted));
                            //Toast.makeText(slf, " Ключ: " + iv, Toast.LENGTH_LONG).show();

                            HashMap<String, String> postData = new HashMap<>();
                            postData.put("step", "card_info");
                            postData.put("app_key", app_key);
                            postData.put("transactionID", transactionID);
                            postData.put("amount", amount);
                            postData.put("terminal_num", terminal_num);
                            postData.put("lang", lang);
                            postData.put("url_redirect", url_redirect);
                            postData.put("url_success", url_success);
                            postData.put("url_fail", url_fail);
                            postData.put("iv", iv + "");
                            postData.put("sec_key", SecretKey);
                            postData.put("card_data", encrypted + "");

                            sendDataToServer(LibActivity.this, server_url, postData);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    } else {
                        Toast.makeText(slf, "Введите номер карты " , Toast.LENGTH_LONG).show();
                    }
                }
            });

            btn_pay = findViewById(R.id.btn_pay);
            btn_pay.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    sc = findViewById(R.id.sec_code);
                    String sec_code = sc.getText().toString();
                    if (sec_code.length() == 6) {
                        btn_pay.setEnabled(false);
                        HashMap<String, String> postData = new HashMap<>();
                        postData.put("step", "pay");
                        postData.put("app_key", app_key);
                        postData.put("transactionID", transactionID);
                        postData.put("amount", amount);
                        postData.put("terminal_num", terminal_num);
                        postData.put("lang", lang);
                        postData.put("url_redirect", url_redirect);
                        postData.put("url_success", url_success);
                        postData.put("url_fail", url_fail);
                        postData.put("sec_code", sec_code);
                        postData.put("genID", genID);
                        postData.put("sessionName", sessionName);

                        sendDataToServer(LibActivity.this, server_url, postData);
                    } else {
                        Toast.makeText(slf, "Введите код операции корректно" , Toast.LENGTH_LONG).show();
                    }
                }
            });
        } else {
            Toast.makeText(slf, "Необходимые параметры не указаны" , Toast.LENGTH_LONG).show();
            closeWnd(Activity.RESULT_CANCELED, "1");
        }
    }

    @Override
    public void processFinish(String output) {
        String[] resp = output.split(":{3}");
        switch (resp[0]) {
            case "sys":
                break;
            case "error":
                goBack = true;
                //closeWnd(Activity.RESULT_CANCELED, "1");
                break;
            case "card_info":
                goBack = false;
                btn.setEnabled(false);
                EditText cn = findViewById(R.id.card_num);
                cn.setEnabled(false);
                btn_pay.setVisibility(View.VISIBLE);
                sc = findViewById(R.id.sec_code);
                sc.setVisibility(View.VISIBLE);

                if (resp.length > 2 && resp[2]!=null) {
                    TextView card_fio = findViewById(R.id.card_fio);
                    card_fio.setText(resp[2]);
                }

                if (resp.length > 3 && resp[3]!=null) {
                    genID = resp[3];
                }

                if (resp.length > 4 && resp[4]!=null) {
                    sessionName = resp[4];
                    Toast.makeText(this, sessionName , Toast.LENGTH_LONG).show();
                }

                getSecCodeBySMS();
                break;
            case "incorrect_sec_code":
                sc = findViewById(R.id.sec_code);
                sc.setText("");
                //sc.getText().clear();
                sc.setSelection(0);
                btn_pay.setEnabled(true);
                break;
            case "success":
                goBack = true;
                sc.setEnabled(false);
                closeWnd(Activity.RESULT_OK, "0");
                break;
            case "pay_error":
                goBack = true;
                closeWnd(Activity.RESULT_CANCELED, "2");
                break;
        }
        Toast.makeText(this, resp[1] , Toast.LENGTH_LONG).show();
        /*
        TextView tv = findViewById(R.id.textView);
        tv.setHeight(1400);
        tv.setTextSize(14);
        tv.setText(output);
        */
    }

    @Override
    public void sendDataToServer(AsyncResponse delegate, String sURL, HashMap<String, String> params) {
        ConnectivityManager conMgr = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
        if (conMgr != null) {
            if ( conMgr.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED
                    || conMgr.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED ) {
                // notify user you are online
                PostResponseAsyncTask getOTPTask = new PostResponseAsyncTask(delegate, params);
                getOTPTask.execute(sURL);
            }
            else if ( conMgr.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.DISCONNECTED
                    || conMgr.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.DISCONNECTED) {
                // notify user you are not online
                Toast.makeText(this, "Проверьте подключение к интернету " , Toast.LENGTH_LONG).show();
            }
        }
    }

    public static String random() {
        String DATA = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ";
        Random RANDOM = new Random();
        StringBuilder sb = new StringBuilder(MAX_LENGTH);
        for (int i = 0; i < MAX_LENGTH; i++) {
            sb.append(DATA.charAt(RANDOM.nextInt(DATA.length())));
        }

        return sb.toString();
    }

    @Override
    public void onBackPressed() {
        if (goBack) {
            super.onBackPressed();
        }
    }

    private void getSecCodeBySMS() {
        final IncomingSms incomingSms = new IncomingSms();
        final Handler handler = new Handler();
        final Runnable r = new Runnable() {
            public void run() {
                if (flgForExit_ptp) {
                    return;
                }

                checkIncomingSMS(incomingSms, sc);
                handler.postDelayed(this, 1000);
            }
        };
        handler.postDelayed(r, 1000);
    }

    private void checkIncomingSMS(IncomingSms incomingSms, EditText edt) {
        String res_num = incomingSms.getSenderNum();
        String res_msg = incomingSms.getMessage();

        if (res_num.equals("6900")) {
            if (res_msg.startsWith("Kod podtverjdeniya tranzaksii")) {
                String[] arr_res = res_msg.split(":");
                String result = arr_res[1].trim();

                edt.setText(result);
                flgForExit_ptp = true;
            }
        }
    }

    private void closeWnd(int resultCode, String data) {
        Intent intent = new Intent();
        intent.putExtra("result", data);
        setResult(resultCode, intent);
        finish();

        /*
        Class cl = getParent().getClass();
        Intent intent = new Intent(LibActivity.this, cl);
        startActivity(intent);
        finish();

        return resultCode;
        */
    }
}
