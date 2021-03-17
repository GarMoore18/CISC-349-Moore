package com.example.cameraupload;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.orhanobut.logger.AndroidLogAdapter;
import com.orhanobut.logger.Logger;

import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.ByteArrayOutputStream;
import java.lang.reflect.Method;

public class MainActivity extends AppCompatActivity {
    private Context context;
    static final String TAG = "MainActivity";
    static final int REQUEST_IMAGE_CAPTURE = 1;

    private Bitmap bitmap;
    private ImageView imageView;
    private TextView result;
    private Button capture;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        context = this;

        imageView = findViewById(R.id.image_from_camera);
        result = findViewById(R.id.result);
        capture = findViewById(R.id.take_image_from_camera);

        Logger.addLogAdapter(new AndroidLogAdapter());

        capture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Logger.d("Clicked capture button");

                Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

                startActivityForResult(cameraIntent, REQUEST_IMAGE_CAPTURE);
            }

        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == REQUEST_IMAGE_CAPTURE) {
            Bitmap image = (Bitmap) data.getExtras().get("data");
            imageView.setImageBitmap(image);
            uploadToServer(encodeToBase64(image, Bitmap.CompressFormat.PNG, 100));
        }
    }

    public static String encodeToBase64(Bitmap image, Bitmap.CompressFormat compressFormat, int quality) {
        ByteArrayOutputStream byteArrayOS = new ByteArrayOutputStream();
        image.compress(compressFormat, quality, byteArrayOS);
        return Base64.encodeToString(byteArrayOS.toByteArray(), Base64.DEFAULT);
    }

    private void uploadToServer(final String image) {
        RequestQueue requestQueue = Volley.newRequestQueue(context);

        JSONObject json = new JSONObject();
        try {
            json.put("Info", "Hello from Garrett");
            json.put("image", image);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        Logger.d(image);

        final String URL = "http://10.0.0.133/image";

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, URL, json,
                new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                Logger.d("We Are in VOLLEY");
            }
            }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                Logger.e(error.getMessage());
            }
        });
        requestQueue.add(jsonObjectRequest);


        //requestQueue.add(stringRequestCombined);
    }

}