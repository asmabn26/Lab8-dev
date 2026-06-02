package com.example.asyncthreadmonitor;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    private TextView txtStatus;
    private TextView txtProgressPercent;
    private ProgressBar progressBar;
    private ImageView imgPreview;

    private Handler uiHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        txtStatus = findViewById(R.id.txtStatus);
        txtProgressPercent = findViewById(R.id.txtProgressPercent);
        progressBar = findViewById(R.id.progressBar);
        imgPreview = findViewById(R.id.imgPreview);

        Button btnThread = findViewById(R.id.btnThread);
        Button btnAsync = findViewById(R.id.btnAsync);
        Button btnToast = findViewById(R.id.btnToast);

        uiHandler = new Handler(Looper.getMainLooper());

        btnToast.setOnClickListener(v ->
                Toast.makeText(this, "L'interface répond toujours", Toast.LENGTH_SHORT).show()
        );

        btnThread.setOnClickListener(v -> chargerImageEnArrierePlan());

        btnAsync.setOnClickListener(v -> new CalculProgressifTask().execute());
    }

    private void chargerImageEnArrierePlan() {
        progressBar.setVisibility(View.VISIBLE);
        progressBar.setProgress(0);
        txtProgressPercent.setText("0 %");
        txtStatus.setText(getString(R.string.status_loading_image));

        Thread worker = new Thread(() -> {
            try {
                Thread.sleep(1500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            uiHandler.post(() -> {
                imgPreview.setImageResource(R.mipmap.ic_launcher);
                progressBar.setProgress(100);
                txtProgressPercent.setText("100 %");
                progressBar.setVisibility(View.INVISIBLE);
                txtStatus.setText(getString(R.string.status_image_loaded));
            });
        });

        worker.start();
    }

    private class CalculProgressifTask extends AsyncTask<Void, Integer, Long> {

        @Override
        protected void onPreExecute() {
            progressBar.setVisibility(View.VISIBLE);
            progressBar.setProgress(0);
            txtProgressPercent.setText("0 %");
            txtStatus.setText(getString(R.string.status_calculating));
        }

        @Override
        protected Long doInBackground(Void... voids) {
            long resultat = 0;

            for (int progression = 1; progression <= 100; progression++) {

                for (int i = 0; i < 250000; i++) {
                    resultat += (progression * i) % 9;
                }

                publishProgress(progression);
            }

            return resultat;
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            int progress = values[0];
            progressBar.setProgress(progress);
            txtProgressPercent.setText(progress + " %");
        }

        @Override
        protected void onPostExecute(Long resultat) {
            progressBar.setVisibility(View.INVISIBLE);
            txtStatus.setText(getString(R.string.status_calc_finished, resultat));
        }
    }
}