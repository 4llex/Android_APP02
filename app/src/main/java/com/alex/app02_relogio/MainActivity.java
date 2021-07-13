package com.alex.app02_relogio;

import androidx.appcompat.app.AppCompatActivity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.BatteryManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.SystemClock;
import android.widget.TextView;

import java.util.Calendar;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private ViewHolder mViewHolder = new ViewHolder();
    private Runnable mRunnable;
    // Lida com a thread
    //private Handler mHandler = new Handler(Looper.getMainLooper());


    private BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            int level = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, 0);
            mViewHolder.textBattery.setText(String.format("%s%%", level));
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        this.mViewHolder.textHourMinute = findViewById(R.id.text_hour_minute);
        this.mViewHolder.textSeconds = findViewById(R.id.text_seconds);
        this.mViewHolder.textBattery = findViewById(R.id.textBattery);

        this.registerReceiver(this.mReceiver, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));

        this.startClock();

    }

    /**
     * Inicializa o relógio de cabeceira
     */
    private void startClock() {

        // Inicializa o calendário
        final Calendar calendar = Calendar.getInstance();

        // Lida com a thread
        final Handler mHandler = new Handler(Looper.getMainLooper());

        // Inicializa thread
        this.mRunnable = new Runnable() {
            @Override
            public void run() {

                // Obtém o tempo do celular e atribui ao calendário criado
                calendar.setTimeInMillis(System.currentTimeMillis());

                int hour = calendar.get(Calendar.HOUR_OF_DAY);
                int minutes = calendar.get(Calendar.MINUTE);
                int seconds = calendar.get(Calendar.SECOND);

                // Converte e atribui
                mViewHolder.textHourMinute.setText(String.format(Locale.getDefault(), "%02d:%02d", hour, minutes));
                mViewHolder.textSeconds.setText(String.format(Locale.getDefault(), "%02d", seconds));


                // Cálculo de quando a thread vai rodar novamente
                long now = SystemClock.elapsedRealtime();
                long next = now + (1000 - (now % 1000));
                mHandler.postAtTime(mRunnable, next);
            }
        };

        // Faz com que a thread rode pela primeira vez
        this.mRunnable.run();
    }

    private static class ViewHolder {
        TextView textHourMinute;
        TextView textSeconds;
        TextView textBattery;
    }

}