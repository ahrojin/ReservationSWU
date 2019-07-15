package com.examplecho2.reservationswu;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.GoogleAuthProvider;

public class MainActivity extends AppCompatActivity {

    // 구글 로그인 클라이언트 제어자
    private GoogleSignInClient mGoogleSignInClient;
    // Firebase  인증객체
    private FirebaseAuth mFirebaseAuth = FirebaseAuth.getInstance();
    // TAG
    private final String TAG = getClass().getSimpleName();



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        findViewById(R.id.btnGoogleSignIn).setOnClickListener(mClicks);

        // 구글 로그인 객체 선언
        GoogleSignInOptions googleSignInOptions = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                //.requestIdToken(getString(R.id.default_we))
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, googleSignInOptions);

    }
// end onCreate

    private View.OnClickListener mClicks = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            switch (view.getId()){
                case R.id.btnGoogleSignIn:
                    googleSignIn();
                    break;
            }
        }
    };

    // 구글 로그인 처리
    private void googleSignIn(){
        Intent i = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(i, 1004);
    } // end googleSignIn

    // Firebase 회원가입하면서 로그인까지 되는 것
    private void firebaseAuthWithGoogle(GoogleSignInAccount account){
        AuthCredential credential = GoogleAuthProvider.getCredential(account.getIdToken(), null);
        mFirebaseAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            // Firebase 로그인 성공
                            Log.d(TAG, " >> Firebase 로그인 성공");

                        }
                        else{
                            // 로그인 실패
                            Log.e(TAG, " >> 인증 실패"+task.getException());
                        }
                    }
                });
    } // end firebaseAuthWithGoogle

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        //구글 로그인 버튼 응답
        if(requestCode == 1004){
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try{
                // 구글 로그인 성공
                GoogleSignInAccount account = task.getResult(ApiException.class);
                Toast.makeText(getBaseContext(), "구글 로그인에 성공 하였습니다.", Toast.LENGTH_LONG).show();

                // Firebase 로그인 인증하러 가기
                firebaseAuthWithGoogle(account);
            }catch(ApiException e){
                e.printStackTrace();
            }// try...catch
        } // if...else
    } // end onActivityResult


} // end class
