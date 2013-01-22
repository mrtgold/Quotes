package com.gec.rq;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Messenger;
import android.text.style.QuoteSpan;
import android.view.View.OnClickListener;
import android.view.View;
import android.widget.Toast;
import android.widget.Button;
import android.widget.EditText;
import android.content.Context;

public class MainActivity extends Activity {
    DBAdapter db = new DBAdapter(this);
    EditText quote;

    private Handler handler = new Handler() {
        public void handleMessage(Message message) {
            Object path = message.obj;
            if (message.arg1 == RESULT_OK && path != null) {
                Toast.makeText(MainActivity.this,
                        "Downloaded" + path.toString(), Toast.LENGTH_LONG)
                        .show();
            } else {
                Toast.makeText(MainActivity.this, "Download failed.",
                        Toast.LENGTH_LONG).show();
            }

        }

        ;
    };

    private OnClickListener mAddListener = new OnClickListener() {
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.go:
                    AddQuote();
                    break;

                case R.id.genRan:
                    GetQuote();
                    break;

                case R.id.getFile:
                    StartGetFile();
                    break;

            }
        }
    };

    private void GetQuote() {
        db.open();
        try {
            String quote = "";
            quote = db.getRandomEntry();

            Context context = getApplicationContext();
            CharSequence text = quote;
            int duration = Toast.LENGTH_LONG;
            Toast toast = Toast.makeText(context, text, duration);
            toast.show();
        } catch (Exception ex) {
            Context context = getApplicationContext();
            CharSequence text = "OnClick::genRan\n" + ex.toString();
            int duration = Toast.LENGTH_LONG;
            Toast toast = Toast.makeText(context, text, duration);
            toast.show();
        }
        db.close();
    }

    private void AddQuote() {
        db.open();
        long id = 0;

        try {
            quote = (EditText) findViewById(R.id.Quote);
            db.insertQuote(quote.getText().toString());
            id = db.getAllEntries();

            Context context = getApplicationContext();
            CharSequence text = "The quote '" + quote.getText() + "' was added successfully!\n" +
                    "Quotes Total = " + id;
            int duration = Toast.LENGTH_LONG;
            Toast toast = Toast.makeText(context, text, duration);
            toast.show();
            quote.setText("");
        } catch (Exception ex) {
            Context context = getApplicationContext();
            CharSequence text = "OnClick:\n" + ex.toString() + " ID = " + id;
            int duration = Toast.LENGTH_LONG;
            Toast toast = Toast.makeText(context, text, duration);
            toast.show();
        }
        db.close();
    }

    private void StartGetFile() {
        String address = "http://wtfismyip.com/text.html";
        Intent intent = new Intent(this, QuotesUpdateService.class);
        // Create a new Messenger for the communication back
        Messenger messenger = new Messenger(handler);
        intent.putExtra("MESSENGER", messenger);
        intent.setData(Uri.parse(address));
        intent.putExtra("urlpath", address);
        startService(intent);
    }

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        Button setButton = (Button) findViewById(R.id.go);
        Button getButton = (Button) findViewById(R.id.genRan);
        Button getFileButton = (Button) findViewById(R.id.getFile);

        setButton.setOnClickListener(mAddListener);
        getButton.setOnClickListener(mAddListener);
        getFileButton.setOnClickListener(mAddListener);
    }

}
