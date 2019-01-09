package uz.ipakyulibank.acquiringapplication;

import android.content.Intent;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import uz.ipakyulibank.acquiringlibrary.LibActivity;

public class FirstActivity extends AppCompatActivity {
    protected FirstActivity slf;

    protected String app_key = "00HA45EH59GF66VR50UK61ER10KY32DF";
    protected String transactionID = "77072";
    protected String amount;
    protected String terminal_num = "1";
    protected String lang = "ru";
    protected String url_success = "http://apteka.getmuzon.com/api/web/v1/payments/success/67";
    protected String url_fail = "http://apteka.getmuzon.com/api/web/v1/payments/fail/67";
    protected String url_redirect = "http://apteka-express.uz";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_first);

        slf = this;

        Button btn = findViewById(R.id.send_data);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TextInputEditText txt = findViewById(R.id.id_edit);
                amount = txt.getText().toString();

                Intent data = new Intent(slf, LibActivity.class);
                data.putExtra("app_key", app_key);
                data.putExtra("transactionID", transactionID);
                data.putExtra("amount", amount);
                data.putExtra("terminal_num", terminal_num);
                data.putExtra("lang", lang);
                data.putExtra("url_success", url_success);
                data.putExtra("url_fail", url_fail);
                data.putExtra("url_redirect", url_redirect);
                startActivity(data);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        /*
        if (requestCode == SUCCESS) {
            if (resultCode == RESULT_OK) {}
        }
        */
        Toast.makeText(this, "Ответ: " + resultCode + data.getStringExtra("result"), Toast.LENGTH_LONG).show();
    }
}
