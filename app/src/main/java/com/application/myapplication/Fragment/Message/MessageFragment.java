package com.application.myapplication.Fragment.Message;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;

import com.application.myapplication.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttMessage;

import info.mqtt.android.service.Ack;
import info.mqtt.android.service.MqttAndroidClient;

public class MessageFragment extends Fragment {

    MqttAndroidClient mqttAndroidClient;
    Context context;
    String serverUri = "tcp://35.239.121.30:1883";
    String clientId = "SMART_HOME";
    EditText sendMessage;
    String message, sendText;
    TextView sendTxt;

    FloatingActionButton btnSend;
    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        this.context = requireContext();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_message, container, false);
        sendMessage = view.findViewById(R.id.et_send_message);
//        sendMessage.requestFocus();
//        InputMethodManager imm = (InputMethodManager) requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
//        imm.showSoftInput(sendMessage, InputMethodManager.SHOW_IMPLICIT);
        requireActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE | WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);

        // Set a focus change listener for the EditText
        sendMessage.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    // Scroll the EditText into view when it gains focus
                    sendMessage.post(new Runnable() {
                        @Override
                        public void run() {
                            sendMessage.requestFocus();
                            InputMethodManager imm = (InputMethodManager) requireContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                            imm.showSoftInput(sendMessage, InputMethodManager.SHOW_IMPLICIT);
                        }
                    });
                }
            }
        });
        btnSend = view.findViewById(R.id.floating_btn);
        sendTxt = view.findViewById(R.id.txt_sender);
        mqttAndroidClient = new MqttAndroidClient(context, serverUri, clientId, Ack.AUTO_ACK);
        connectToMQTTBroker();

        return view;
    }

    public void connectToMQTTBroker() {
        String username = "duy";
        String password = "duy";
        MqttConnectOptions mqttConnectOptions = new MqttConnectOptions();
        mqttConnectOptions.setAutomaticReconnect(true);
        mqttConnectOptions.setCleanSession(true);
        mqttConnectOptions.setUserName(username);
        mqttConnectOptions.setPassword(password.toCharArray());
        IMqttToken token = mqttAndroidClient.connect(mqttConnectOptions);
        token.setActionCallback(new IMqttActionListener() {
            @Override
            public void onSuccess(IMqttToken asyncActionToken) {
                Log.d("MQTT", "Connect to Broker Successfully");
                btnSend.setOnClickListener(view ->{
                    message = sendMessage.getText().toString();
                    publishMessage("SEND", message, 0);
                    sendTxt.setText(message);
                });

            }

            @Override
            public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                Log.d("MQTT", "Connect Failed");
            }
        });
    }

    public void publishMessage(String topic, String payload, int qos) {
        MqttMessage message = new MqttMessage(payload.getBytes());
        message.setQos(qos);
        mqttAndroidClient.publish(topic, message);
        Log.d("MQTT", "Topic: " + topic + ", Message: " + message);
    }
}