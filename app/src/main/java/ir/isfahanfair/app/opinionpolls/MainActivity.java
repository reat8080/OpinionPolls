package ir.isfahanfair.app.opinionpolls;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import ir.isfahanfair.app.opinionpolls.services.MyService;
import ir.isfahanfair.app.opinionpolls.util.NetworkHelper;
import uk.co.chrisjenx.calligraphy.CalligraphyConfig;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public class MainActivity extends AppCompatActivity {

    int [] answers = new int[11] ;
    Dialog phoneRegisterDialog , proposRegisterDialog;
    String phoneNumber , proposValue , poll_cat_name = "تست نظر سنجی اپ";
    SQLiteDatabase database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        CalligraphyConfig.initDefault(new CalligraphyConfig.Builder()
                .setDefaultFontPath("font/yekan.ttf")
                .setFontAttrId(R.attr.fontPath)
                .build());
        setContentView(R.layout.activity_main);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);

        for (int i = 1 ; i < 11 ; i++) {
            opinionHandler (i);
        }

        LocalBroadcastManager.getInstance(getApplicationContext())
                .registerReceiver(mBroadcastReceiver,
                        new IntentFilter(MyService.MY_SERVICE_MESSAGE));

    }

    public void opinionHandler (final int i){
        String angryId = "quest"+i+"angry";
        int angryID = getResources().getIdentifier(angryId, "id", getPackageName());
        final ImageButton angry = findViewById(angryID) ;
        String sosoId = "quest"+i+"soso";
        int sosoID = getResources().getIdentifier(sosoId, "id", getPackageName());
        final ImageButton soso = findViewById(sosoID) ;
        String happyId = "quest"+i+"happy";
        int happyID = getResources().getIdentifier(happyId, "id", getPackageName());
        final ImageButton happy = findViewById(happyID) ;
        angry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                soso.setAlpha(0.3f);
                happy.setAlpha(0.3f);
                angry.setAlpha(1f);
                answers [i] = 1;
            }
        });
        soso.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                happy.setAlpha(0.3f);
                angry.setAlpha(0.3f);
                soso.setAlpha(1f);
                answers [i] = 2;
            }
        });
        happy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                angry.setAlpha(0.3f);
                soso.setAlpha(0.3f);
                happy.setAlpha(1f);
                answers [i] = 3;
            }
        });
    }

    @Override
    protected void attachBaseContext(Context newBase){
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    private BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String message =
                    intent.getStringExtra(MyService.MY_SERVICE_PAYLOAD);
            Log.i("codejson" , message);
            Toast.makeText(MainActivity.this, "keke", Toast.LENGTH_SHORT).show();
            //int verificationCode =  getUserCode(message);
        }
    };

//    public int getUserCode(String userCodeJson) {
//        JSONObject json = null;
//        try {
//            json = new JSONObject(userCodeJson);
//            Toast.makeText(this, json.getString("registercode") , Toast.LENGTH_SHORT).show();
//            return Integer.valueOf( json.getString("registercode"));
//        } catch (JSONException e) {
//            e.printStackTrace();
//            return 0;
//        }
//    }

    public void submitButtonHandler(View view) {
        Toast.makeText(this, "نظرات شما با موفقیت ثبت شد. سپاس بابت وقتتان.", Toast.LENGTH_SHORT).show();
        if (!NetworkHelper.hasNetworkAccess(this)) {
            storeInDatabase();
        } else {
            storeInServer();
        }
        //startActivity(new Intent(this , MainActivity.class));
    }

    public void phoneRegisterHandler(View view) {
        phoneRegisterDialog = new Dialog(this);
        phoneRegisterDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        phoneRegisterDialog.setContentView(R.layout.phoneregister_dialog);
        phoneRegisterDialog.show();
    }

    public void proposRegisterHandler(View view) {
        proposRegisterDialog = new Dialog(this);
        proposRegisterDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        proposRegisterDialog.setContentView(R.layout.proposregister_dialog);
        proposRegisterDialog.show();
    }

    public void phoneRegisterSubmitHandler(View view) {

        EditText phone =  phoneRegisterDialog.findViewById(R.id.phoneNumber);
        if (phone.length() == 0)
            Toast.makeText(this, "شماره همراه خود را وارد نمایید.", Toast.LENGTH_SHORT).show();
        else {
            phoneNumber = phone.getText().toString();
            Toast.makeText(this, "شماره همراه شما با موفقیت ثبت شد.", Toast.LENGTH_SHORT).show();
            phoneRegisterDialog.hide();
        }
    }

    public void cancelPhoneHandler(View view) {
        phoneRegisterDialog.hide();
    }

    public void proposRegisterSubmitHandler(View view) {
        EditText propos = proposRegisterDialog.findViewById(R.id.proposText);
        if (propos.length() == 0) {
            Toast.makeText(this, "لطفا متن مورد نظر خود را وارد نمایید.", Toast.LENGTH_SHORT).show();
        } else {
            proposValue = propos.getText().toString();
            Toast.makeText(this, "نظر شما با موفقیت ثبت شد.", Toast.LENGTH_SHORT).show();
            proposRegisterDialog.hide();
        }
    }

    public void cancelProposHandler(View view) {
        proposRegisterDialog.hide();
    }

    public void isfahanfairHandler(View view) {
        Dialog isfahanfairDialog = new Dialog(this);
        isfahanfairDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        isfahanfairDialog.setContentView(R.layout.isfahanfair_dialog);
        isfahanfairDialog.show();
    }

    private void storeInServer() {
//        String registerUrl = "http://admin:1234@comp.isfahanregister.com/app_api/insert?pcn=test" +
//                "&ansq1=2&ansq2=2&ansq3=3&ansq4=3" +
//                "&ansq5=3&ansq6=3&ansq7=3&ansq8=3&ansq9=3&ansq10=3";
//        Intent intent = new Intent(this, MyService.class);
//        intent.setData(Uri.parse(registerUrl));
//        startService(intent);
        String registerUrl = "http://admin:1234@comp.isfahanregister.com/app_api/insert?pcn=test&ansq1=2&ansq2=2&ansq3=3&ansq4=3&ansq5=3&ansq6=3&ansq7=3&ansq8=3&ansq9=3&ansq10=3" ;
        Intent intent2 = new Intent(this, MyService.class);
        intent2.setData(Uri.parse(registerUrl));
        startService(intent2);
        Toast.makeText(this, "we are here", Toast.LENGTH_SHORT).show();

    }

    private void storeInDatabase() {
        database = this.openOrCreateDatabase("opinion", MODE_PRIVATE, null);
        database.execSQL("CREATE TABLE IF NOT EXISTS results (poll_cat_name VARCHAR,ans1 int(1), ans2 int(1) ," +
                " ans3 int(1),ans4 int(1),ans5 int(1),ans6 int(1),ans7 int(1),ans8 int(1),ans9 int(1),ans10 int(1),creat DATETIME DEFAULT CURRENT_TIMESTAMP)");

        ContentValues requestValue = new ContentValues();
        requestValue.put("poll_cat_name" , poll_cat_name);
        requestValue.put("ans1" , answers[1]);
        requestValue.put("ans2" , answers[2]);
        requestValue.put("ans3" , answers[3]);
        requestValue.put("ans4" , answers[4]);
        requestValue.put("ans5" , answers[5]);
        requestValue.put("ans6" , answers[6]);
        requestValue.put("ans7" , answers[7]);
        requestValue.put("ans8" , answers[8]);
        requestValue.put("ans9" , answers[9]);
        requestValue.put("ans10" , answers[10]);

        database.insert("results" , null , requestValue);
    }


}
