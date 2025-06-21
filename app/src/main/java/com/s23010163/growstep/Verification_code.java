package com.s23010163.growstep;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class Verification_code extends Activity {

    EditText code1, code2, code3, code4;
    Button verifyButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activty_verification_code); // Matches the XML filename

        code1 = findViewById(R.id.code1);
        code2 = findViewById(R.id.code2);
        code3 = findViewById(R.id.code3);
        code4 = findViewById(R.id.code4);
        verifyButton = findViewById(R.id.verifyButton);

        verifyButton.setOnClickListener(v -> {
            String fullCode = code1.getText().toString() +
                    code2.getText().toString() +
                    code3.getText().toString() +
                    code4.getText().toString();

            if (TextUtils.isEmpty(fullCode) || fullCode.length() < 4) {
                Toast.makeText(this, "Please enter full 4-digit code", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Code Verified: " + fullCode, Toast.LENGTH_SHORT).show();

                // Navigate to HomeActivity
                Intent intent = new Intent(this, CreatePassword.class);
                startActivity(intent);
                finish();
            }
        });
    }
}
