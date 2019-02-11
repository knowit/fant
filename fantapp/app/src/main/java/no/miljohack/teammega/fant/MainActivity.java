package no.miljohack.teammega.fant;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

public class MainActivity extends AppCompatActivity {

    public static String KEY = "";

    BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        startService(new Intent(this, HostCardEmulatorService.class));
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        final Button button = findViewById(R.id.button);
        final ImageView imageView = findViewById(R.id.generer);
        final ImageView imageView1 = findViewById(R.id.godkjent);
        final ImageView imageView2 = findViewById(R.id.leser);
        final ImageView imageView3 = findViewById(R.id.error);
        final TextView textView = findViewById(R.id.hjelpetekst);

        IntentFilter filter = new IntentFilter();
        filter.addAction("open_complete");
        filter.addAction("no_access");
        filter.addAction("no_code");

        broadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String action = intent.getAction();
                assert action != null;
                switch (action) {
                    case "open_complete":
                        imageView2.setVisibility(View.INVISIBLE);
                        imageView1.setVisibility(View.VISIBLE);
                        textView.setText("Godkjent");
                        textView.setTextColor(Color.parseColor("#06872B"));
                        KEY = "";
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                finish();
                                startActivity(getIntent());
                            }
                        }, 10000);

                        break;
                    case "no_access":
                        imageView2.setVisibility(View.INVISIBLE);
                        imageView3.setVisibility(View.VISIBLE);
                        textView.setText("Ingen tilgang");
                        textView.setTextColor(Color.parseColor("#D55555"));

                        break;
                    case "no_code":
                        imageView.setVisibility(View.INVISIBLE);
                        imageView3.setVisibility(View.VISIBLE);
                        textView.setVisibility(View.VISIBLE);
                        textView.setText("Generer kode først");
                        textView.setTextColor(Color.parseColor("#D55555"));
                        break;
                }
            }
        };
        registerReceiver(broadcastReceiver, filter);
        textView.setVisibility(View.INVISIBLE);
        imageView1.setVisibility(View.INVISIBLE);
        imageView2.setVisibility(View.INVISIBLE);
        imageView3.setVisibility(View.INVISIBLE);

        final Runnable r = new Runnable() {
            @Override
            public void run() {
                imageView1.setVisibility(View.INVISIBLE);
                imageView3.setVisibility(View.INVISIBLE);
                imageView2.setVisibility(View.VISIBLE);
                textView.setText("Legg mobilen inntil leseren");
                textView.setTextColor(Color.BLACK);
            }
        };
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
                String url = "https://5qulm7recj.execute-api.eu-west-2.amazonaws.com/dev/nfc";
                StringRequest request = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            KEY = new JSONObject(response).getJSONObject("nfc").get("key").toString();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        Intent intent = new Intent("new_key");
                        intent.putExtra("key", KEY);
                        sendBroadcast(intent);
                        button.setVisibility(View.INVISIBLE);
                        textView.setVisibility(View.VISIBLE);
                        textView.setText("Kode generert");
                        textView.setTextColor(Color.parseColor("#06872B"));
                        imageView.setVisibility(View.INVISIBLE);
                        imageView3.setVisibility(View.INVISIBLE);
                        imageView1.setVisibility(View.VISIBLE);
                        imageView1.postDelayed(r, 1000);

                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        imageView.setImageResource(R.drawable.ic_error);
                    }
                });
                queue.add(request);

            }
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unregisterReceiver(broadcastReceiver);
    }
}
