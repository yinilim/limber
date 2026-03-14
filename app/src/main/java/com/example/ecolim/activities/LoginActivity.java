package com.example.ecolim.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.CheckBox;
import android.widget.Toast;

import androidx.biometric.BiometricManager;
import androidx.biometric.BiometricPrompt;
import androidx.core.content.ContextCompat;

import com.example.ecolim.R;
import com.example.ecolim.database.EcolimDbHelper;
import com.example.ecolim.models.Usuario;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.util.concurrent.Executor;

public class LoginActivity extends BaseActivity {

    private static final String PREFS_NAME  = "ecolim_prefs";
    private static final String KEY_USER_ID = "usuario_id";

    private TextInputLayout   layoutEmail, layoutPassword;
    private TextInputEditText edtEmail, edtPassword;
    private MaterialButton    btnIngresar, btnHuella;
    private CheckBox          chkRecordar;
    private EcolimDbHelper    db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        db             = EcolimDbHelper.getInstance(this);
        layoutEmail    = findViewById(R.id.layout_email);
        layoutPassword = findViewById(R.id.layout_password);
        edtEmail       = findViewById(R.id.edt_email);
        edtPassword    = findViewById(R.id.edt_password);
        btnIngresar    = findViewById(R.id.btn_ingresar);
        btnHuella      = findViewById(R.id.btn_huella);
        chkRecordar    = findViewById(R.id.chk_recordar);

        btnIngresar.setOnClickListener(v -> intentarLogin());
        btnHuella.setOnClickListener(v -> mostrarAutenticacionHuella());

        // Mostrar botón huella solo si hay sesión guardada
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        long usuarioGuardado = prefs.getLong(KEY_USER_ID, -1);
        btnHuella.setVisibility(usuarioGuardado != -1
                ? android.view.View.VISIBLE
                : android.view.View.GONE);
    }

    private void intentarLogin() {
        String email    = txt(edtEmail);
        String password = txt(edtPassword);

        layoutEmail.setError(null);
        layoutPassword.setError(null);

        if (email.isEmpty()) {
            layoutEmail.setError("Ingrese su correo");
            edtEmail.requestFocus();
            return;
        }
        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            layoutEmail.setError("Correo no válido");
            edtEmail.requestFocus();
            return;
        }
        if (password.isEmpty()) {
            layoutPassword.setError("Ingrese su contraseña");
            edtPassword.requestFocus();
            return;
        }
        if (password.length() < 6) {
            layoutPassword.setError("Mínimo 6 caracteres");
            edtPassword.requestFocus();
            return;
        }

        btnIngresar.setEnabled(false);
        btnIngresar.setText("Verificando...");

        Usuario usuario = db.loginUsuario(email, password);

        if (usuario != null) {
            if (chkRecordar.isChecked()) {
                getSharedPreferences(PREFS_NAME, MODE_PRIVATE)
                        .edit()
                        .putLong(KEY_USER_ID, usuario.getId())
                        .apply();
            }

            Toast.makeText(this,
                    "¡Bienvenido, " + usuario.getNombre() + "!",
                    Toast.LENGTH_SHORT).show();

            Intent intent = new Intent(this, MainActivity.class);
            intent.putExtra("usuario_id", usuario.getId());
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);

        } else {
            layoutPassword.setError("Correo o contraseña incorrectos");
            btnIngresar.setEnabled(true);
            btnIngresar.setText("Ingresar");
        }
    }

    private void mostrarAutenticacionHuella() {
        BiometricManager biometricManager = BiometricManager.from(this);

        if (biometricManager.canAuthenticate(BiometricManager.Authenticators.BIOMETRIC_STRONG)
                != BiometricManager.BIOMETRIC_SUCCESS) {
            Toast.makeText(this,
                    "Este dispositivo no soporta huella digital",
                    Toast.LENGTH_SHORT).show();
            return;
        }

        Executor executor = ContextCompat.getMainExecutor(this);

        BiometricPrompt biometricPrompt = new BiometricPrompt(this, executor,
                new BiometricPrompt.AuthenticationCallback() {

                    @Override
                    public void onAuthenticationSucceeded(BiometricPrompt.AuthenticationResult result) {
                        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
                        long userId = prefs.getLong(KEY_USER_ID, -1);

                        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                        intent.putExtra("usuario_id", userId);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                    }

                    @Override
                    public void onAuthenticationError(int errorCode, CharSequence errString) {
                        Toast.makeText(getApplicationContext(),
                                "Error: " + errString, Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onAuthenticationFailed() {
                        Toast.makeText(getApplicationContext(),
                                "Huella no reconocida", Toast.LENGTH_SHORT).show();
                    }
                });

        BiometricPrompt.PromptInfo promptInfo = new BiometricPrompt.PromptInfo.Builder()
                .setTitle("Ecolim")
                .setSubtitle("Usa tu huella para ingresar")
                .setNegativeButtonText("Cancelar")
                .build();

        biometricPrompt.authenticate(promptInfo);
    }

    private String txt(TextInputEditText e) {
        return e.getText() == null ? "" : e.getText().toString().trim();
    }
}