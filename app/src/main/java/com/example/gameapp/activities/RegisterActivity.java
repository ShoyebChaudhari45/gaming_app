package com.example.gameapp.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.gameapp.R;
import com.example.gameapp.api.ApiClient;
import com.example.gameapp.api.ApiService;
import com.example.gameapp.models.request.RegisterRequest;
import com.example.gameapp.models.response.RegisterResponse;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputLayout;

import java.util.regex.Pattern;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import android.text.InputType;


public class RegisterActivity extends AppCompatActivity {

    private long lastBackPressedTime = 0;
    private static final String TAG = "REGISTER_API";

    private EditText edtName, edtMobile, edtEmail, edtPassword;
    private RadioGroup rgUserType;
    private MaterialButton btnSignup;
    private TextView txtLogin;
    private View progressContainer;

    private TextInputLayout tilName, tilMobile, tilEmail, tilPassword;

    // 🔐 Password rule
    private static final Pattern PASSWORD_PATTERN =
            Pattern.compile("^(?=.*[A-Z])(?=.*[a-z])(?=.*\\d)(?=.*[@#$%^&+=!]).{6,}$");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        edtName = findViewById(R.id.edtName);
        edtMobile = findViewById(R.id.edtMobile);
        edtEmail = findViewById(R.id.edtEmail);
        edtPassword = findViewById(R.id.edtPassword);
        rgUserType = findViewById(R.id.rgUserType);
        btnSignup = findViewById(R.id.btnSignup);
        txtLogin = findViewById(R.id.txtLogin);
        progressContainer = findViewById(R.id.progressContainer);

        // 🔥 Get TextInputLayouts WITHOUT changing XML
        tilName = (TextInputLayout) edtName.getParent().getParent();
        tilMobile = (TextInputLayout) edtMobile.getParent().getParent();
        tilEmail = (TextInputLayout) edtEmail.getParent().getParent();
        tilPassword = findViewById(R.id.tilPassword);

        btnSignup.setOnClickListener(v -> registerUser());

        txtLogin.setOnClickListener(v ->
                startActivity(new Intent(RegisterActivity.this, LoginActivity.class))
        );
    }

    // ================= REGISTER =================
    private void registerUser() {

        clearErrors();

        String name = edtName.getText().toString().trim();
        String mobile = edtMobile.getText().toString().trim();
        String email = edtEmail.getText().toString().trim();
        String password = edtPassword.getText().toString().trim();

        // 👤 Name
        if (name.isEmpty()) {
            tilName.setError("Please enter your name");
            edtName.requestFocus();
            return;
        }

        // 📱 Mobile
        if (!mobile.matches("^[6-9]\\d{9}$")) {
            tilMobile.setError("Enter valid 10-digit mobile number");
            edtMobile.requestFocus();
            return;
        }

        // 📧 Email
        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            tilEmail.setError("Enter a valid email address");
            edtEmail.requestFocus();
            return;
        }

        // 🔐 Password
        // 🔐 Password validation with AUTO VISIBILITY
        if (!PASSWORD_PATTERN.matcher(password).matches()) {

            // 👁 Password visible when error
            edtPassword.setInputType(
                    InputType.TYPE_CLASS_TEXT |
                            InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
            );
            edtPassword.setSelection(edtPassword.length());

            // 🔴 SET REAL ERROR
            tilPassword.setError(
                    "Must contain uppercase, lowercase, number & special character"
            );

            edtPassword.requestFocus();
            return;

        } else {

            // 🔒 Hide password when valid
            edtPassword.setInputType(
                    InputType.TYPE_CLASS_TEXT |
                            InputType.TYPE_TEXT_VARIATION_PASSWORD
            );
            edtPassword.setSelection(edtPassword.length());

            // ✅ Clear error
            tilPassword.setError(null);
        }



        // 👥 User type
        if (rgUserType.getCheckedRadioButtonId() == -1) {
            Toast.makeText(this, "Please select user type", Toast.LENGTH_SHORT).show();
            return;
        }

        showLoader(true);

        RegisterRequest request = new RegisterRequest();
        request.name = name;
        request.mobile_no = mobile;
        request.email = email;
        request.password = password;
        request.status = "active";

        int selectedId = rgUserType.getCheckedRadioButtonId();
        request.user_type = (selectedId == R.id.rbEmployee) ? "employee" : "customer";

        ApiService api = ApiClient.getClient().create(ApiService.class);
        api.register(request).enqueue(new Callback<RegisterResponse>() {

            @Override
            public void onResponse(Call<RegisterResponse> call,
                                   Response<RegisterResponse> response) {

                showLoader(false);
                Log.d(TAG, "HTTP Code: " + response.code());

                if (response.isSuccessful()) {
                    toast("Registration successful");
                    startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
                    finish();
                    return;
                }

                toast("Registration failed");
            }

            @Override
            public void onFailure(Call<RegisterResponse> call, Throwable t) {
                showLoader(false);
                toast("Network error. Please try again");
            }
        });
    }

    // ================= HELPERS =================
    private void clearErrors() {
        tilName.setError(null);
        tilMobile.setError(null);
        tilEmail.setError(null);
        tilPassword.setError(null);
    }

    private void showLoader(boolean show) {
        progressContainer.setVisibility(show ? View.VISIBLE : View.GONE);
        btnSignup.setEnabled(!show);
    }

    private void toast(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_LONG).show();
    }
    @Override
    public void onBackPressed() {
        long currentTime = System.currentTimeMillis();
        if (currentTime - lastBackPressedTime < 2000) {
            finish();
        } else {
            lastBackPressedTime = currentTime;
            Toast.makeText(this, "Press back again to exit", Toast.LENGTH_SHORT).show();
        }
    }
}
