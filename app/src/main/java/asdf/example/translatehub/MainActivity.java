package asdf.example.translatehub;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import com.example.translatehub.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.button.MaterialButton;
import com.google.mlkit.common.model.DownloadConditions;
import com.google.mlkit.nl.translate.TranslateLanguage;
import com.google.mlkit.nl.translate.Translation;
import com.google.mlkit.nl.translate.Translator;
import com.google.mlkit.nl.translate.TranslatorOptions;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

public class MainActivity extends AppCompatActivity {

    private EditText sourceLanguageEt;
    private TextView destinationLanguageTv;
    private MaterialButton sourceLanguageChooseBtn;
    private MaterialButton destinationLanguageChooseBtn;
    private MaterialButton translateBtn;

    private TranslatorOptions translatorOptions;


    private Translator translator;

    private ProgressDialog progressDialog;

    private ArrayList<ModelLanguage> languageArrayList;

    private static final String TAG = "MAIN_TAG";

    private String sourceLanguageCode = "en";
    private String sourceLanguageTitle = "English";
    private String destinationLanguageCode = "ur";
    private String destinationLanguageTitle = "Urdu";






    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Objects.requireNonNull(getSupportActionBar()).setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.background)));


        sourceLanguageEt = findViewById(R.id.sourceLanguageEt);
        destinationLanguageTv = findViewById(R.id.destinationLanguageTv);
        sourceLanguageChooseBtn = findViewById(R.id.sourceLanguageChooseBtn);
        destinationLanguageChooseBtn = findViewById(R.id.destinationLanguageChooseBtn);
        translateBtn = findViewById(R.id.translateBtn);

        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Please Wait");
        progressDialog.setCanceledOnTouchOutside(false);

        loadAvailablelanguages();

        sourceLanguageChooseBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sourceLanguageChoose();

            }
        });
        destinationLanguageChooseBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                destinationLanguageChoose();

            }
        });
        translateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                validateData();

            }
        });


    }
    private String sourceLanguageText = "";

    private void validateData() {
        sourceLanguageText = sourceLanguageEt.getText().toString().trim();

        Log.d(TAG, "validateData: sourceLanguageText: "+sourceLanguageText);

        if (sourceLanguageText.isEmpty()){
            Toast.makeText(this, "Enter Text To Translate", Toast.LENGTH_SHORT).show();
        }
        else {
            startTranslations();
        }
    }

    private void startTranslations() {

        progressDialog.setMessage("Processing Language...");
        progressDialog.show();

        translatorOptions = new TranslatorOptions.Builder()
                .setSourceLanguage(sourceLanguageCode)
                .setTargetLanguage(destinationLanguageCode)
                .build();
        translator = Translation.getClient(translatorOptions);

        DownloadConditions downloadConditions = new DownloadConditions.Builder()
                .requireWifi()
                .build();

        translator.downloadModelIfNeeded(downloadConditions)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {

                        Log.d(TAG, "onSuccess: Model ready, Starting translate...");
                        progressDialog.setMessage("Translating...");


                        translator.translate(sourceLanguageText)
                                .addOnSuccessListener(new OnSuccessListener<String>() {
                                    @Override
                                    public void onSuccess(String translatedText) {

                                        Log.d(TAG, "onSuccess: translatedText: "+translatedText);
                                        progressDialog.dismiss();

                                        destinationLanguageTv.setText(translatedText);
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {

                                        progressDialog.dismiss();
                                        Log.d(TAG, "onFailure: ", e);
                                        Toast.makeText(MainActivity.this, "Failed To Translated due to "+e.getMessage(), Toast.LENGTH_SHORT).show();

                                    }
                                });

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                        progressDialog.dismiss();
                        Log.d(TAG, "onFailure: ", e);
                        Toast.makeText(MainActivity.this, "Failed to ready model due to "+e.getMessage(), Toast.LENGTH_SHORT).show();

                    }
                });
    }

    private void sourceLanguageChoose(){

        PopupMenu popupMenu = new PopupMenu(this, sourceLanguageChooseBtn);

        for (int i=0; i<languageArrayList.size(); i++) {

            popupMenu.getMenu().add(Menu.NONE, i, i, languageArrayList.get(i).LanguageTitle);
        }

        popupMenu.show();

        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                int position = item.getItemId();


                sourceLanguageCode = languageArrayList.get(position).LanguageCode;
                sourceLanguageTitle = languageArrayList.get(position).LanguageTitle;

                sourceLanguageChooseBtn.setText(sourceLanguageTitle);
                sourceLanguageEt.setHint("Enter "+sourceLanguageTitle);

                Log.d(TAG, "onMenuItemClick: sourceLanguageCode: "+ sourceLanguageCode);
                Log.d(TAG, "onMenuItemClick: sourceLanguageTitle: "+ sourceLanguageTitle);

                return false;
            }
        });
    }

    private void destinationLanguageChoose(){

        PopupMenu popupMenu = new PopupMenu(this, destinationLanguageChooseBtn);


        for (int i=0; i<languageArrayList.size(); i++){

            popupMenu.getMenu().add(Menu.NONE, i, i, languageArrayList.get(i).getLanguageTitle());
        }

        popupMenu.show();

        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {

                int position = item.getItemId();

                destinationLanguageCode = languageArrayList.get(position).LanguageCode;
                destinationLanguageTitle = languageArrayList.get(position).LanguageTitle;


                destinationLanguageChooseBtn.setText(destinationLanguageTitle);

                Log.d(TAG, "onMenuItemClick: destinationLanguageCode: "+destinationLanguageCode);
                Log.d(TAG, "onMenuItemClick: destinationLanguageTitle: "+destinationLanguageTitle);




                return false;
            }
        });
    }

    private void loadAvailablelanguages() {

        languageArrayList = new ArrayList<>();

        List<String> languageCodeList = TranslateLanguage.getAllLanguages();

        for (String languageCode: languageCodeList){
            String languageTitle = new Locale(languageCode).getDisplayLanguage();
            Log.d(TAG, "loadAvailablelanguages: languageCode: "+languageCode);
            Log.d(TAG, "loadAvailablelanguages: languageTitle: "+languageTitle);

            ModelLanguage modelLanguage = new ModelLanguage(languageCode, languageTitle);
            languageArrayList.add(modelLanguage);

        }

    }
}