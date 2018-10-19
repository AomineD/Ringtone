package com.colvengames.downloadmp3.ui;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.colvengames.downloadmp3.R;
import com.colvengames.downloadmp3.manager.PrefManager;

public class RequestCodeActivity extends AppCompatActivity {

    private Button VerifyBtn;
    private Button Get_code;
    PrefManager prefManager;
    private EditText input;
    private LoginActivity loginActivity;
public static final String key_lest = "kskdas";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_request_code);
        prefManager = new PrefManager(getApplicationContext());


        input = findViewById(R.id.input_code);
        VerifyBtn = findViewById(R.id.verify_btn);
        Get_code = findViewById(R.id.get_code);
        loginActivity = LoginActivity.staticClass;

        VerifyBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (input.getText().toString().equals("0034118010")) {
                    Log.e("MAIN", "onClick: "+input.getText().toString());
                    SharedPreferences.Editor editor = SplashActivity.sharedPreferences.edit();


                    editor.putInt(key_lest, 1);
                    editor.commit();
                    editor.putInt(IntroActivity.key_st, 1);
                    editor.commit();
                    loginActivity.ActivateInDataBase(loginActivity.emailActual);
                    Toast.makeText(getApplicationContext(), "Activado con éxito!", Toast.LENGTH_SHORT).show();
                    enter();
                } else {
                    Log.e("MAIN", "onClick: " + input.getText().toString());
                    Toast.makeText(getApplicationContext(), "Código incorrecto", Toast.LENGTH_SHORT).show();
                }
            }
        });

        Get_code.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PackageManager packageManager = getPackageManager();
                String packname = "com.facebook.orca";
                Intent nulable = packageManager.getLaunchIntentForPackage(packname);


                if (nulable == null) {

                    Intent kk = new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + packname));
                    startActivity(kk);
                } else {
                    Intent pak = new Intent(Intent.ACTION_VIEW, Uri.parse("http://m.me/averrepelis"));

                    //pak.setType("text/plain");
                    //pak.putExtra(Intent.EXTRA_TEXT, "Quiero mi code");
                    pak.setPackage(packname);

                    startActivity(pak);
                }
            }
        });

    }




    public void enter(){
        Log.e("MAIN", "enter: LISTO");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(RequestCodeActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(RequestCodeActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                Intent intent = new Intent(RequestCodeActivity.this,PermissionActivity.class);
                Log.e("MAIN", "enter: code is = "+SplashActivity.sharedPreferences.getInt(key_lest, 0));
                startActivity(intent);
                overridePendingTransition(R.anim.enter, R.anim.exit);
                finish();
            }else{
                Intent intent = new Intent(RequestCodeActivity.this,MainActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.enter, R.anim.exit);
                finish();
            }
        }else{
            Intent intent = new Intent(RequestCodeActivity.this,MainActivity.class);
            startActivity(intent);
            overridePendingTransition(R.anim.enter, R.anim.exit);
            finish();
        }
    }
}
