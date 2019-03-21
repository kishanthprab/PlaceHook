package com.example.kishanthprab.placehook;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.button.MaterialButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.kishanthprab.placehook.DataObjects.User;
import com.example.kishanthprab.placehook.Utility.FireAuthUtil;
import com.example.kishanthprab.placehook.Utility.Functions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.rengwuxian.materialedittext.MaterialAutoCompleteTextView;
import com.rengwuxian.materialedittext.MaterialEditText;

public class RegisterActivity extends AppCompatActivity {

    final static String TAG = "RegisterActivity";

    MaterialEditText edtEmail, edtPassword, edtCPassword, edtName;
    TextView txt_signin;
    MaterialButton btn_register;

    RelativeLayout RegrootLayout;

    AlertDialog alertDialog;

    FirebaseAuth FireAuth;
    FirebaseAuth.AuthStateListener FireAuthStateListener;
    FirebaseDatabase FireDB;
    DatabaseReference UsersRef;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        //initialize firebase
        FireAuth = FirebaseAuth.getInstance();
        FireAuthStateListener = FireAuthUtil.SettingFirebaseAuthStateListener();

        FireDB = FirebaseDatabase.getInstance();
        UsersRef = FireDB.getReference("Users");

        RegrootLayout = (RelativeLayout) findViewById(R.id.regrootLayout);
        btn_register = (MaterialButton) findViewById(R.id.btn_register);
        edtName = (MaterialEditText) findViewById(R.id.edtName);
        edtEmail = (MaterialEditText) findViewById(R.id.edtEmail);
        edtPassword = (MaterialEditText) findViewById(R.id.edtPassword);
        edtCPassword = (MaterialEditText) findViewById(R.id.edtCPassword);
        txt_signin = (TextView) findViewById(R.id.txt_signin);


        //alertdialog initiation
        alertDialog = Functions.spotsDialog(RegisterActivity.this);


        btn_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "clicked");



                if (validateSignup()) {
                    alertDialog.setTitle("PLACE HOOK");
                    alertDialog.setMessage("Creating account...");
                    alertDialog.show();

                    CreateUser();

                    Log.d(TAG, "okay " + validateSignup());

                } else {

                    Log.d(TAG, "error");
                }
            }
        });


        txt_signin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(), LoginActivity.class);
                startActivity(i);

            }
        });

    }

    private boolean validateSignup() {

        if (TextUtils.isEmpty(edtEmail.getText().toString())) {

            Snackbar.make(RegrootLayout, "Please enter email address", Snackbar.LENGTH_SHORT).show();
            return false;
        }

        if (TextUtils.isEmpty(edtPassword.getText().toString())) {

            Snackbar.make(RegrootLayout, "Please enter password", Snackbar.LENGTH_SHORT).show();
            return false;
        }

        if (TextUtils.isEmpty(edtCPassword.getText().toString())) {

            Snackbar.make(RegrootLayout, "Please Enter password", Snackbar.LENGTH_SHORT).show();
            return false;
        }


        if (TextUtils.isEmpty(edtName.getText().toString())) {

            Snackbar.make(RegrootLayout, "Please enter your name", Snackbar.LENGTH_SHORT).show();
            return false;
        }

        if (!edtPassword.getText().toString().equals(edtCPassword.getText().toString())) {

            Snackbar.make(RegrootLayout, "Password not matching", Snackbar.LENGTH_SHORT).show();
            return false;
        }

        if (edtPassword.getText().toString().length() < 6) {

            Snackbar.make(RegrootLayout, "Password is too short", Snackbar.LENGTH_SHORT).show();
            return false;
        }
        return true;

    }


    private void CreateUser() {


        FireAuth.createUserWithEmailAndPassword(edtEmail.getText().toString(), edtPassword.getText().toString())
                .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                    @Override
                    public void onSuccess(AuthResult authResult) {


                        User userObj = new User(
                                edtEmail.getText().toString(),
                                edtPassword.getText().toString(),
                                edtName.getText().toString());

                        UsersRef.child(FirebaseAuth.getInstance().getCurrentUser().getUid()).setValue(userObj).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {

                                alertDialog.dismiss();
                                Snackbar.make(RegrootLayout, "Account created successfully", Snackbar.LENGTH_SHORT).show();
                                startActivity(new Intent(getApplicationContext(), DashboardActivity.class));
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                alertDialog.dismiss();

                                Snackbar.make(RegrootLayout, "Creating account failed " + e, Snackbar.LENGTH_SHORT).show();
                            }
                        });


                        // UsersRef.child()
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        alertDialog.dismiss();
                        Log.d(TAG, "sign up failed" + e);
                        Snackbar.make(RegrootLayout, "Failed " + e, Snackbar.LENGTH_SHORT).show();
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

}
