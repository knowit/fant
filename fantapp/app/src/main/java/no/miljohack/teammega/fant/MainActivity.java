package no.miljohack.teammega.fant;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        startService(new Intent(this, HostCardEmulatorService.class));
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        final Button button = findViewById(R.id.button);
        final TextView mTextView = findViewById(R.id.mTextView);
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
                        System.out.println(KEY);
                        mTextView.setText("Successfully collected key");
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        mTextView.setText("Couldn't get key");
                    }
                });
                queue.add(request);
            }
        });


    }
}
