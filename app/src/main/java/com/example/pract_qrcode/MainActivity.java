package com.example.pract_qrcode;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;

public class MainActivity extends AppCompatActivity {

    ImageView imageView;
    Button button;
    Button button2;
    EditText editText;
    String EditTextValue;
    Thread thread;
    ProgressBar progressBar;
    private MainAsyncTask mainAsyncTask;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        progressBar = findViewById(R.id.am_progress);
        imageView = findViewById(R.id.imageView);
        editText = findViewById(R.id.editText);
        button = findViewById(R.id.button);
        button2 = findViewById(R.id.button2);

        button2.setOnClickListener(view -> {
            Intent intent = new Intent(getApplicationContext(), CamActivity.class);
            startActivity(intent);
        });

        button.setOnClickListener(view1 -> {
            EditTextValue = editText.getText().toString();

            if (mainAsyncTask == null) {
                mainAsyncTask = new MainAsyncTask();
                mainAsyncTask.execute();
            } else {
                if (mainAsyncTask.getStatus() != AsyncTask.Status.FINISHED) {
                    mainAsyncTask.cancel(true);
                    mainAsyncTask = new MainAsyncTask();
                    mainAsyncTask.execute();
                } else {
                    mainAsyncTask = new MainAsyncTask();
                    mainAsyncTask.execute();
                }
            }
        });


    }


    class MainAsyncTask extends AsyncTask<Void, Void, Bitmap> {

        //выолняется в главном потоке перед выполнением задачи рабочего потока
        @Override
        protected void onPreExecute() {
            imageView.setVisibility(View.INVISIBLE);
            progressBar.setVisibility(View.VISIBLE);
        }

        //Можно вызвать из метода doInBackground чтобы отобразить некий прогресс операции
        @Override
        protected void onProgressUpdate(Void... values) {
            super.onProgressUpdate(values);
        }

        @Override
        protected void onCancelled() {
        }

        @Override
        protected void onCancelled(Bitmap s) {
        }

        //в этом методе выполняется операция в рабочем потоке(рабочий - не главный)
        @Override
        protected Bitmap doInBackground(Void... voids) {
            QRCodeWriter writer = new QRCodeWriter();
            Bitmap bmp = null;

            try {
                BitMatrix bitMatrix = writer.encode(EditTextValue, BarcodeFormat.QR_CODE, 512, 512);
                int width = bitMatrix.getWidth();
                int height = bitMatrix.getHeight();
                bmp = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565);
                for (int x = 0; x < width; x++) {
                    for (int y = 0; y < height; y++) {
                        bmp.setPixel(x, y, bitMatrix.get(x, y) ? getResources().getColor(R.color.QRCodeBlackColor) : getResources().getColor(R.color.QRCodeWhiteColor));
                    }
                }

            } catch (WriterException e) {
                e.printStackTrace();
            }
            return bmp;
        }

        //выполняется в главном потоке после выполнения операции в рабочем потоке
        //как правило использует входное значение некоторого типа
        @Override
        protected void onPostExecute(Bitmap bmp) {
            ((ImageView) findViewById(R.id.imageView)).setImageBitmap(bmp);

            progressBar.setVisibility(View.INVISIBLE);
            imageView.setVisibility(View.VISIBLE);
        }
    }
}


