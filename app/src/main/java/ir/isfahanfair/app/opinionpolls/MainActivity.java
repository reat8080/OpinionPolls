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
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.media.MediaPlayer;
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

        database = this.openOrCreateDatabase("opinion", MODE_PRIVATE, null);
        database.execSQL("CREATE TABLE IF NOT EXISTS results (poll_cat_name VARCHAR,ans1 int(1), ans2 int(1) ," +
                " ans3 int(1),ans4 int(1),ans5 int(1),ans6 int(1),ans7 int(1),ans8 int(1),ans9 int(1),ans10 int(1),creat DATETIME DEFAULT CURRENT_TIMESTAMP)");

        LocalBroadcastManager.getInstance(getApplicationContext())
                .registerReceiver(mBroadcastReceiver,
                        new IntentFilter(MyService.MY_SERVICE_MESSAGE));

    }

    public void opinionHandler (final int i){
        final MediaPlayer mp = MediaPlayer.create(this, R.raw.iphone_unlock);
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
                mp.start();
            }
        });
        soso.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                happy.setAlpha(0.3f);
                angry.setAlpha(0.3f);
                soso.setAlpha(1f);
                answers [i] = 2;
                mp.start();
            }
        });
        happy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                angry.setAlpha(0.3f);
                soso.setAlpha(0.3f);
                happy.setAlpha(1f);
                answers [i] = 3;
                mp.start();
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
        if (answers[1] == 0) {
            Toast.makeText(this, "لطفا به سوالات پاسخ دهید.", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "نظرات شما با موفقیت ثبت شد. سپاس بابت وقتتان.", Toast.LENGTH_SHORT).show();
            if (!NetworkHelper.hasNetworkAccess(this)) {
                storeInDatabase();
            } else {
                storeInServer();
            }
            startActivity(new Intent(this, MainActivity.class));
        }
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

        Cursor c = database.rawQuery("SELECT * FROM results", null);
        int pcnIn = c.getColumnIndex("poll_cat_name");
        int creatIn = c.getColumnIndex("creat");
        int ansq1In = c.getColumnIndex("ans1");
        int ansq2In = c.getColumnIndex("ans2");
        int ansq3In = c.getColumnIndex("ans3");
        int ansq4In = c.getColumnIndex("ans4");
        int ansq5In = c.getColumnIndex("ans5");
        int ansq6In = c.getColumnIndex("ans6");
        int ansq7In = c.getColumnIndex("ans7");
        int ansq8In = c.getColumnIndex("ans8");
        int ansq9In = c.getColumnIndex("ans9");
        int ansq10In = c.getColumnIndex("ans10");

        c.moveToFirst();
        int conter = c.getCount();

        if (c.getCount()  == 0){

        } else {
            while ( conter != 0 ) {
                String registerUrl = "http://comp.isfahanregister.com/app_api/insert?pcn=" +
                        c.getString(pcnIn)+"&ansq1="+c.getString(ansq1In)+"&ansq2="+c.getString(ansq2In)+"&ansq3="+c.getString(ansq3In)+
                        "&ansq4="+c.getString(ansq4In)+"&ansq5="+c.getString(ansq5In)+"&ansq6="+c.getString(ansq6In) +
                        "&ansq7="+c.getString(ansq7In)+"&ansq8="+c.getString(ansq8In)+"&ansq9="+c.getString(ansq9In)+
                        "&ansq10="+c.getString(ansq10In)+"creat"+c.getString(creatIn);
                Intent intent = new Intent(this, MyService.class);
                intent.setData(Uri.parse(registerUrl));
                startService(intent);
                conter--;
                c.moveToNext();
            }
            database.execSQL("DROP TABLE IF EXISTS results");
        }
        String registerUrl = "http://comp.isfahanregister.com/app_api/insert?pcn=" +
                poll_cat_name+"&ansq1="+answers[1]+"&ansq2="+answers[2]+"&ansq3="+answers[3]+"&ansq4="+answers[4]+
                "&ansq5="+answers[5]+"&ansq6="+answers[6]+"&ansq7="+answers[7]+"&ansq8="+answers[8]+
                "&ansq9="+answers[9]+"&ansq10="+answers[10]+"creat"+System.currentTimeMillis();
        Intent intent = new Intent(this, MyService.class);
        intent.setData(Uri.parse(registerUrl));
        startService(intent);

    }

    private void storeInDatabase() {

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
