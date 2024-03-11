package com.oriensolutions.firebaseauthwithphone;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;

import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity {

    public static final String TAG = MainActivity.class.getName();

    private FirebaseAuth firebaseAuth;

    private String mverificationId;
    private PhoneAuthProvider.ForceResendingToken resendingToken;

    private PhoneAuthProvider.OnVerificationStateChangedCallbacks callbacks;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        firebaseAuth = FirebaseAuth.getInstance();

        EditText phoneText = findViewById(R.id.editTextPhone);
        EditText otpText = findViewById(R.id.editTextNumber);

        findViewById(R.id.btnSend).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                signInWithPhone(phoneText.getText().toString());
            }
        });

        findViewById(R.id.btnVerify).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                verifyOtp(otpText.getText().toString());
            }
        });

        callbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            @Override
            public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {
                Log.i(TAG, "onVerificationCompleted: " + phoneAuthCredential);
            }

            @Override
            public void onVerificationFailed(@NonNull FirebaseException e) {
                Log.i(TAG, "onVerificationFailed: " + e.getMessage());

            }

            @Override
            public void onCodeSent(@NonNull String verificationId, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                Log.i(TAG,"onCodeSent"+verificationId);
                Toast.makeText(MainActivity.this,"OTP Code sent Your phone",Toast.LENGTH_LONG).show();

                mverificationId = verificationId;
                resendingToken = forceResendingToken;
            }
        };

    }

    private  void verifyOtp(String otp){
        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(mverificationId, otp);
        signInWithPhoneAuth(credential);
    }

    private void signInWithPhone(String phoneNumber) {
        PhoneAuthOptions options = PhoneAuthOptions.newBuilder(firebaseAuth)
                .setPhoneNumber("+94" + phoneNumber)
                .setTimeout(60L, TimeUnit.SECONDS)
                .setActivity(this)
                .setCallbacks(callbacks)
                .build();

        PhoneAuthProvider.verifyPhoneNumber(options);
    }

    private void signInWithPhoneAuth(PhoneAuthCredential credential){
        firebaseAuth.signInWithCredential(credential)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            FirebaseUser user = task.getResult().getUser();
                            updateUi(user);
                        }
                    }
                });
    }

    private void updateUi(FirebaseUser user){
        if(user != null){
            Toast.makeText(MainActivity.this,"Phone Sign In Working",Toast.LENGTH_LONG).show();
        }
    }

}