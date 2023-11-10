package com.example.lab;

import android.app.Activity;
import android.util.Log;
import android.widget.Toast;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

public class ApiHelper {
    Activity ctx;

    public ApiHelper(Activity ctx) {
        this.ctx = ctx;
    }

    public void on_ready(String res) {
        // Ваш код обработки ответа
    }

    public void on_error(int responseCode) {
        // Ваш код обработки ошибки
    }

    public class NetOp implements Runnable {
        public String req;
        public String body;
        public String response; // Добавляем поле для хранения ответа

        public void run() {
            try {
                int responseCode = http_get(req, body);
                ctx.runOnUiThread(new Runnable() {
                    public void run() {
                        if (responseCode == HttpURLConnection.HTTP_OK) {
                            on_ready(response);
                        } else {
                            on_error(responseCode);
                        }
                    }
                });
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }

        int http_get(String req, String body) throws IOException {
            URL url = new URL(req);
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            con.setRequestProperty("Content-Type", "application/json");
            con.setRequestProperty("accept", "application/json");
            con.setRequestMethod("POST");

            con.setDoOutput(true);
            BufferedOutputStream out = new BufferedOutputStream(con.getOutputStream());
            out.write(body.getBytes());
            out.flush();

            int responseCode = con.getResponseCode();

            if (responseCode == HttpURLConnection.HTTP_OK) {
                BufferedInputStream inp = new BufferedInputStream(con.getInputStream());

                byte[] buf = new byte[512];
                StringBuilder res = new StringBuilder();

                while (true) {
                    int num = inp.read(buf);
                    if (num < 0) break;

                    res.append(new String(buf, 0, num));
                }

                response = res.toString();
            }

            con.disconnect();
            return responseCode;
        }

        public String getResponse() {
            return response;
        }
    }

    public void send(String req, String body) {
        NetOp nop = new NetOp();
        nop.body = body;
        nop.req = req;

        Thread th = new Thread(nop);
        th.start();
    }

    public String getResponse() {
        return new NetOp().getResponse();
    }
}