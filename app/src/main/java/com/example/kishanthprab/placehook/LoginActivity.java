package com.example.kishanthprab.placehook;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.button.MaterialButton;
import android.support.design.widget.Snackbar;
import android.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.kishanthprab.placehook.Utility.FireAuthUtil;
import com.example.kishanthprab.placehook.Utility.Functions;
import com.example.kishanthprab.placehook.Utility.KeyboardUtils;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.rengwuxian.materialedittext.MaterialEditText;

import dmax.dialog.SpotsDialog;

public class LoginActivity extends AppCompatActivity {

    final static String TAG = "LoginActivity";

    MaterialEditText edt_LoginEmail, edt_LoginPassword;
    TextView txt_Forsignup;
    MaterialButton btn_Signin;

    RelativeLayout LoginRootLayout;

    AlertDialog alertDialog;

    FirebaseAuth FireAuth;
    FirebaseAuth.AuthStateListener FireAuthStateListener;
    FirebaseDatabase FireDB;
    DatabaseReference UsersRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);


        //initialize firebase
        FireAuth = FirebaseAuth.getInstance();
        FireAuthStateListener = FireAuthUtil.SettingFirebaseAuthStateListener();

        FireDB = FirebaseDatabase.getInstance();
        UsersRef = FireDB.getReference("Users");

        LoginRootLayout = (RelativeLayout) findViewById(R.id.LoginRootLayout);
        btn_Signin = (MaterialButton) findViewById(R.id.btn_signin);
        edt_LoginEmail = (MaterialEditText) findViewById(R.id.edt_loginEmail);
        edt_LoginPassword = (MaterialEditText) findViewById(R.id.edt_LoginPassword);
        txt_Forsignup = (TextView) findViewById(R.id.txt_ForSignup);


        alertDialog = Functions.spotsDialog(LoginActivity.this);

        txt_Forsignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent i = new Intent(getApplicationContext(), RegisterActivity.class);
                startActivity(i);


            }
        });



        btn_Signin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                if (validateLogin()) {
                    alertDialog.setTitle("Sign in");
                    alertDialog.setMessage("Authenticating user..");
                    // alertDialog.setIcon(R.drawable.ic_launcher_background);
                    alertDialog.show();

                    //KeyboardUtils.hideKeyboard(LoginActivity.this);
                    SignInUser(edt_LoginEmail.getText().toString(), edt_LoginPassword.getText().toString());


                }


            }
        });

    }


    private boolean validateLogin() {
        KeyboardUtils.hideKeyboard(LoginActivity.this);

        if (TextUtils.isEmpty(edt_LoginEmail.getText().toString())) {

            Snackbar.make(LoginRootLayout, "Please enter email address", Snackbar.LENGTH_SHORT).show();
            return false;
        }

        if (TextUtils.isEmpty(edt_LoginPassword.getText().toString())) {

            Snackbar.make(LoginRootLayout, "Please enter password", Snackbar.LENGTH_SHORT).show();
            return false;
        }


        return true;

    }

    private void SignInUser(String email, String password) {



        FireAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithEmail:success");
                            FirebaseUser user = FireAuth.getCurrentUser();

                            alertDialog.dismiss();
                            Toast.makeText(getApplicationContext(), "Login successful", Toast.LENGTH_LONG).show();
                            Snackbar.make(LoginRootLayout, "Login successful", Snackbar.LENGTH_LONG).show();

                            startActivity(new Intent(getApplicationContext(), DashboardActivity.class));


                            //finish();

                        } else {
                            // If sign in fails, display a message to the user.
                            alertDialog.dismiss();
                            Log.d(TAG, "signInWithEmail:failure", task.getException());
                           // Toast.makeText(getApplicationContext(), "Login Failed", Toast.LENGTH_SHORT).show();
                            Snackbar.make(LoginRootLayout, "Failed : " + task.getException().getLocalizedMessage(), Snackbar.LENGTH_LONG).show();

                        }


                        // ...
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                alertDialog.dismiss();
               // Log.d(TAG, "signInWithEmail:failure", e);
                Snackbar.make(LoginRootLayout, "Failed " + e.getLocalizedMessage(), Snackbar.LENGTH_SHORT).show();
                //Toast.makeText(getApplicationContext(), "Failed " + e, Toast.LENGTH_LONG).show();
            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();
        FireAuth.addAuthStateListener(FireAuthStateListener);

        FirebaseUser CurrentUser = FireAuthUtil.getmUser();
        //after this it should go to the


    }

    @Override
    protected void onStop() {
        super.onStop();
        if (FireAuthStateListener != null) {
            FireAuth.removeAuthStateListener(FireAuthStateListener);
        }
    }


    public static Context getContext(){

        return getContext().getApplicationContext();
    }


    public AlertDialog showDialog(){

        AlertDialog alertDialog = new SpotsDialog.Builder()
                .setContext(getApplicationContext())
                .build();

        return alertDialog;

    }
}
