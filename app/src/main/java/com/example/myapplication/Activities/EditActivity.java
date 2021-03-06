package com.example.myapplication.Activities;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.myapplication.R;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.firestore.FirebaseFirestore;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class EditActivity extends AppCompatActivity {

    TextInputLayout tilUsername, tilPassword, tilConfirm;
    EditText etUsername, etPassword, etConfirm;
    Button btnConfirm, btnCancel;
    ImageView ivImage;
    FirebaseAuth auth;
    FirebaseUser user;
    ProgressDialog progressDialog;
    Context context;

    private static int REQUEST_CODE = 53;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        context = this;

        auth = FirebaseAuth.getInstance();
        user = auth.getCurrentUser();

        tilUsername = findViewById(R.id.register_til_username);
        tilPassword = findViewById(R.id.edit_profile_til_password);
        tilConfirm = findViewById(R.id.edit_profile_til_cpassword);

        etUsername = findViewById(R.id.edit_profile_et_name);
        etPassword = findViewById(R.id.edit_profile_et_password);
        etConfirm = findViewById(R.id.edit_profile_et_cpassword);

        etUsername.setText(user.getDisplayName());

        btnConfirm = findViewById(R.id.edit_profile_btn_confirm);
        btnCancel = findViewById(R.id.edit_profile_btn_cancel);

        ivImage = findViewById(R.id.edit_profile_image);
        Glide.with(this).load(user.getPhotoUrl()).placeholder(R.drawable.profile_icon).into(ivImage);
        ivImage.setOnClickListener(imageListener);

        btnConfirm.setOnClickListener(confirmListener);
        btnCancel.setOnClickListener(cancelListener);

    }
    private View.OnClickListener confirmListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            String newUsername = etUsername.getText().toString();
            String newPassword = etPassword.getText().toString();
            String confirm = etConfirm.getText().toString();

            String oldPassword = "password"; //ganti

            boolean valid = true, updatePassword = false;

            //Nyalain disini (Progress bar)
            progressDialog = new ProgressDialog(EditActivity.this);
            //Show Dialog
            progressDialog.show();
            //Set Content View
            progressDialog.setContentView(R.layout.progress_dialog);
            //Set Tapping Out False
            progressDialog.setCancelable(false);
            //Set Transparent Background
            progressDialog.getWindow().setBackgroundDrawableResource(
                    android.R.color.transparent
            );

            if(!newPassword.isEmpty()){
                if(newPassword.length()<8){
                    tilPassword.setError("New Password must be 8 digits or more");
                } else {
                    tilPassword.setError(null);
                    updatePassword = true;
                }

                if(confirm.isEmpty()) {
                    tilConfirm.setError("Confirmation Password must be filled");
                    return;
                } else if(!confirm.equals(oldPassword)){
                    tilConfirm.setError("Wrong Password");
                    return;
                } else {
                    if(updatePassword){
                        user.updatePassword(newPassword);
                    }
                    tilConfirm.setError(null);
                }

            } else {
                tilPassword.setError(null);
            }


            //Matiin progress bar


            UserProfileChangeRequest profileChangeRequest = new UserProfileChangeRequest.Builder()
                    .setDisplayName(newUsername)
                    .build();
            user.updateProfile(profileChangeRequest).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()) {
                        progressDialog.dismiss();
                        finish();
                    }
                }
            });
        }
    };

    private View.OnClickListener imageListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent intent = new Intent();
            intent.setType("image/*");
            intent.setAction(Intent.ACTION_GET_CONTENT);
            startActivityForResult(Intent.createChooser(intent, "Select Picture"), REQUEST_CODE);
        }
    };

    private View.OnClickListener cancelListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            finish();
        }
    };

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            progressDialog = new ProgressDialog(EditActivity.this);
            //Show Dialog
            progressDialog.show();
            //Set Content View
            progressDialog.setContentView(R.layout.progress_dialog);
            //Set Tapping Out False
            progressDialog.setCancelable(false);
            //Set Transparent Background
            progressDialog.getWindow().setBackgroundDrawableResource(
                    android.R.color.transparent
            );
            Uri selected = data.getData();
            final Bitmap bitmap;
            try {
                bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), selected);

                UserProfileChangeRequest profileChangeRequest = new UserProfileChangeRequest.Builder()
                        .setPhotoUri(selected)
                        .build();
                user.updateProfile(profileChangeRequest).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Glide.with(context).load(user.getPhotoUrl()).placeholder(R.drawable.profile_icon).into(ivImage);
                        }
                        progressDialog.dismiss();
                    }
                });

            } catch (Exception e) {
                progressDialog.dismiss();
            }
        }

    }
}