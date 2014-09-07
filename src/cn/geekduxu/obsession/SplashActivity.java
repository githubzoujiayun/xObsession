package cn.geekduxu.obsession;

import java.io.File;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.os.SystemClock;
import android.widget.Toast;

public class SplashActivity extends Activity {

    @SuppressLint("ShowToast") @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        
        File root = new File(Environment.getExternalStorageDirectory(),"obsession");
        if(!root.exists()){
        	if (!root.mkdirs()){
        		Toast.makeText(SplashActivity.this, "�Ҳ����ڴ濨�����Ժ����ԡ�", Toast.LENGTH_LONG);
        		finish();
        	}
        }
        
        new Thread(){
        	public void run() {
        		SystemClock.sleep(1750);
        		Intent intent = new Intent(SplashActivity.this, MainActivity.class);
        		startActivity(intent);
        		finish();
        	}
        }.start();
    }

}
