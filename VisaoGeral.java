package com.example.tasktide;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import android.Manifest;


public class VisaoGeral extends AppCompatActivity {

    private static final int PICK_IMAGE_REQUEST = 1;
    private static final int PERMISSION_REQUEST_CODE = 2;
    private ImageView imgBanner;
    private CheckBox chbMaisDeUmDiaEvento;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_visao_geral);

        imgBanner = findViewById(R.id.imgBanner);
        ImageView btnMudarBanner = findViewById(R.id.imgbtnMudarBanner);
        ImageButton imgbtnCriarCronograma = findViewById(R.id.imgbtnCriarCronograma);
        chbMaisDeUmDiaEvento = findViewById(R.id.chbMaisDeUmDiaEvento);

        // Solicitar permissões se necessário
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, PERMISSION_REQUEST_CODE);
        }

        btnMudarBanner.setOnClickListener(v -> openImageChooser());

        imgbtnCriarCronograma.setOnClickListener(v -> showCreateScheduleDialog());

        chbMaisDeUmDiaEvento.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                showConfirmationDialog();
            }
        });
    }

    private void openImageChooser() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    private void showCreateScheduleDialog() {
        // Inflar o layout do diálogo personalizado
        LayoutInflater inflater = LayoutInflater.from(this);
        View dialogView = inflater.inflate(R.layout.dialog_criar_cronograma, null);

        EditText edtHorario = dialogView.findViewById(R.id.edtHorario);
        EditText edtNomeAtividade = dialogView.findViewById(R.id.edtNomeAtividade);

        // Construir o diálogo
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        dialogBuilder.setView(dialogView);
        dialogBuilder.setTitle("Adicionar atividade ao cronograma");
        dialogBuilder.setPositiveButton("Salvar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Salvar os dados inseridos pelo usuário
                String horario = edtHorario.getText().toString();
                String nomeAtividade = edtNomeAtividade.getText().toString();
                // Você pode adicionar aqui a lógica para salvar esses dados onde precisar
                Log.d("VisaoGeral", "Horário: " + horario + ", Nome da Atividade: " + nomeAtividade);
            }
        });
        dialogBuilder.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        // Mostrar o diálogo
        AlertDialog dialog = dialogBuilder.create();
        dialog.show();
    }

    private void showConfirmationDialog() {
        new AlertDialog.Builder(this)
                .setTitle("Confirmação")
                .setMessage("Tem certeza de que o evento terá mais de um dia? Você precisará informar as datas do evento.")
                .setPositiveButton("Prosseguir", (dialog, which) -> showDateInputDialog())
                .setNegativeButton("Cancelar", (dialog, which) -> {
                    chbMaisDeUmDiaEvento.setChecked(false); // Desmarcar o CheckBox se o usuário cancelar
                    dialog.dismiss();
                })
                .show();
    }

    private void showDateInputDialog() {
        LayoutInflater inflater = LayoutInflater.from(this);
        View dialogView = inflater.inflate(R.layout.dialog_input_datas, null);
        LinearLayout dateContainer = dialogView.findViewById(R.id.dateContainer);
        Button btnAddDate = dialogView.findViewById(R.id.btnAddDate);

        // Adicionar o primeiro campo de data
        addDateField(dateContainer);

        btnAddDate.setOnClickListener(v -> addDateField(dateContainer));

        new AlertDialog.Builder(this)
                .setTitle("Informe as datas")
                .setView(dialogView)
                .setPositiveButton("Salvar", (dialog, which) -> saveDates(dateContainer))
                .setNegativeButton("Cancelar", (dialog, which) -> dialog.dismiss())
                .show();
    }

    private void addDateField(LinearLayout container) {
        LayoutInflater inflater = LayoutInflater.from(this);
        View dateFieldView = inflater.inflate(R.layout.date_field, container, false);
        container.addView(dateFieldView);
    }

    private void saveDates(LinearLayout container) {
        // Percorrer todos os campos de data e salvar os valores
        for (int i = 0; i < container.getChildCount(); i++) {
            View view = container.getChildAt(i);
            if (view instanceof LinearLayout) {
                EditText edtDate = view.findViewById(R.id.edtDate);
                String date = edtDate.getText().toString();
                // Adicionar lógica para salvar a data
                // Exemplo de log
                Log.d("VisaoGeral", "Data: " + date);
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            Uri imageUri = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), imageUri);
                imgBanner.setImageBitmap(bitmap);
            } catch (Exception e) {
                e.printStackTrace();
                Log.e("VisaoGeral", "Erro ao carregar a imagem: " + e.getMessage());
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permissão concedida
            } else {
                // Permissão negada
                Log.e("VisaoGeral", "Permissão para acessar armazenamento negada.");
            }
        }
    }



}
