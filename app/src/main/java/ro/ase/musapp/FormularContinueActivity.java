package ro.ase.musapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Toast;

import com.leff.midi.MidiFile;
import com.leff.midi.MidiTrack;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.FileAsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.mongodb.Block;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.gridfs.GridFSBucket;
import com.mongodb.client.gridfs.GridFSBuckets;
import com.mongodb.client.gridfs.model.GridFSFile;
import com.mongodb.stitch.android.core.Stitch;
import com.mongodb.stitch.android.core.StitchAppClient;
import com.mongodb.stitch.android.services.mongodb.local.LocalMongoDbService;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.entity.StringEntity;

public class FormularContinueActivity extends AppCompatActivity {

    private final String adressWithoutPort = "18.217.62.152";
    private final String ipAddress = "http://" + adressWithoutPort + ":8080/";
    private final String ipAddressContinue = ipAddress + "continue";
    private final String ipAddresStoreSample = ipAddress + "/storesample";
    private int connectionTimeout = 30000;

    private AlertDialog.Builder alertDialogProecessing = null;
    public static Activity FormularContinueActivity;

    private LinearLayout linearLayoutImprov = null;
    private LinearLayout linearLayoutElse = null;
    private String stringModel = null;

    private int clickedChordsImprov = 1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_formular_continue);
        FormularContinueActivity = this;

        ImageView ivLibrary = findViewById(R.id.idImgViewLibraryContinue);
        ivLibrary.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent it = new Intent(FormularContinueActivity.this, MyLibraryActivity.class);
                startActivity(it);
                finish();
            }
        });


        ImageView ivCreate = findViewById(R.id.idImgViewCreateContinue);
        ivCreate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent it = new Intent(FormularContinueActivity.this, FormularActivity.class);
                startActivity(it);
                finish();
            }
        });


        linearLayoutElse = findViewById(R.id.LinearLayoutElse_continue);
        Spinner spinnerModel = findViewById(R.id.spinner_model_continue);


        String[] tempArrayPrimerForSpinner = getResources().getStringArray(R.array.Primers);
        ArrayList<String> stringArrayListPrimersForSpinner = new ArrayList<>();
        for (String string : tempArrayPrimerForSpinner) {
            stringArrayListPrimersForSpinner.add(string);
        }
        String[] tempArrayChordsForSpinner = getResources().getStringArray(R.array.Chords);
        ArrayList<String> stringArrayListChordsForSpinner = new ArrayList<>();
        for (String string : tempArrayChordsForSpinner) {
            stringArrayListChordsForSpinner.add(string);
        }

        String[] tempArrayConfigsForSpinnerModelIsMelody = getResources().getStringArray(R.array.ModelIsMelodyRNN);
        ArrayList<String> stringArrayListConfigsForSpinnerModelIsMelody = new ArrayList<>();
        for (String string : tempArrayConfigsForSpinnerModelIsMelody) {
            stringArrayListConfigsForSpinnerModelIsMelody.add(string);
        }
        String[] tempArrayConfigsForSpinnerModelIsPerformance = getResources().getStringArray(R.array.ModelIsPerfromance);
        ArrayList<String> stringArrayListConfigsForSpinnerModelIsPerformance = new ArrayList<>();
        for (String string : tempArrayConfigsForSpinnerModelIsPerformance) {
            stringArrayListConfigsForSpinnerModelIsPerformance.add(string);
        }


        SpinnerFormularAdapter adapterModelIsMelody = new SpinnerFormularAdapter(this,
                R.layout.custom_spinner_formular, R.id.customSpinnerTextView, stringArrayListConfigsForSpinnerModelIsMelody);
        SpinnerFormularAdapter adapterModelIsPerformance = new SpinnerFormularAdapter(this,
                R.layout.custom_spinner_formular, R.id.customSpinnerTextView, stringArrayListConfigsForSpinnerModelIsPerformance);
        Spinner spinnerConfig = findViewById(R.id.spinner_config_continue);


        spinnerModel.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                stringModel = spinnerModel.getSelectedItem().toString();

                if (stringModel.contains("MelodyRNN")) {
                    (findViewById(R.id.idTextViewConfig_continue)).setVisibility(View.VISIBLE);
                    spinnerConfig.setAdapter(adapterModelIsMelody);
                    spinnerConfig.setVisibility(View.VISIBLE);

                } else if (stringModel.contains("PerformanceRNN")) {
                    (findViewById(R.id.idTextViewConfig_continue)).setVisibility(View.VISIBLE);
                    spinnerConfig.setAdapter(adapterModelIsPerformance);
                    spinnerConfig.setVisibility(View.VISIBLE);
                } else {
                    (findViewById(R.id.idTextViewConfig_continue)).setVisibility(View.GONE);
                    spinnerConfig.setVisibility(View.GONE);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


    }


    public void magicContinue(View view) throws UnsupportedEncodingException {
        alertDialogProecessing = new AlertDialog.Builder(ro.ase.musapp.FormularContinueActivity.this);
        View customView = LayoutInflater.from(ro.ase.musapp.FormularContinueActivity.this).inflate(R.layout.alert_dialog_progress_bar, null);
        alertDialogProecessing.setCancelable(false);
        alertDialogProecessing.setView(customView);
        alertDialogProecessing.create();
        alertDialogProecessing.show();

        ConstraintLayout constraintLayout = customView.findViewById(R.id.id_AlertDialog);
        AnimationDrawable animationDrawable = (AnimationDrawable) constraintLayout.getBackground();
        animationDrawable.setEnterFadeDuration(2000);
        animationDrawable.setExitFadeDuration(2000);
        animationDrawable.start();


        int nrSteps = 0;
        String backingChordsImprov = "xd";
        int nrOutputs;


        if (stringModel.contains("Performance")) {
            if (((Spinner) findViewById(R.id.spinner_stepsElse_continue)).getSelectedItem().toString().contains("Medium")) {
                nrSteps = 9000;
            } else if (((Spinner) findViewById(R.id.spinner_stepsElse_continue)).getSelectedItem().toString().contains("Long")) {
                nrSteps = 10500;
            } else {
                nrSteps = 6500;
            }
        } else if (stringModel.contains("PianoRoll")) {
            if (((Spinner) findViewById(R.id.spinner_stepsElse_continue)).getSelectedItem().toString().contains("Medium")) {
                nrSteps = 1350;
            } else if (((Spinner) findViewById(R.id.spinner_stepsElse_continue)).getSelectedItem().toString().contains("Long")) {
                nrSteps = 1600;
            } else {
                nrSteps = 1152;
            }
        } else {
            if (((Spinner) findViewById(R.id.spinner_stepsElse_continue)).getSelectedItem().toString().contains("Medium")) {
                nrSteps = 750;
            } else if (((Spinner) findViewById(R.id.spinner_stepsElse_continue)).getSelectedItem().toString().contains("Long")) {
                nrSteps = 900;
            } else {
                nrSteps = 576;
            }
        }

        nrOutputs = Integer.parseInt(((Spinner) findViewById(R.id.spinner_NrOutPutElse_continue)).getSelectedItem().toString());


        if (stringModel.contains("MelodyRNN")) {
            stringModel = ((Spinner) findViewById(R.id.spinner_config_continue)).getSelectedItem().toString();
            if (stringModel.contains("Basic")) {
                stringModel = "basic_rnn";
            } else if (stringModel.contains("Mono")) {
                stringModel = "mono_rnn";
            } else if (stringModel.contains("Lookback")) {
                stringModel = "lookback_rnn";
            } else if (stringModel.contains("Attention")) {
                stringModel = "attention_rnn";
            }
        } else if (stringModel.contains("DrumKit")) {
            stringModel = "drum_kit";

        } else if (stringModel.contains("ImprovRNN")) {
            stringModel = "chord_pitches_improv";

        } else if (stringModel.contains("PerformanceRNN")) {
            stringModel = ((Spinner) findViewById(R.id.spinner_config_continue)).getSelectedItem().toString();
            if (stringModel.contains("Performance")) {
                stringModel = "performance";
            } else if (stringModel.contains("Dynamics")) {
                stringModel = "performance_with_dynamics";
            } else if (stringModel.contains("Modulo")) {
                stringModel = "performance_with_dynamics_and_modulo_encoding";
            } else if (stringModel.contains("Density")) {
                stringModel = "density_conditioned_performance_with_dynamics";
            } else if (stringModel.contains("Pitch")) {
                stringModel = "pitch_conditioned_performance_with_dynamics";
            } else if (stringModel.contains("Multiconditioned")) {
                stringModel = "multiconditioned_performance_with_dynamics";
            }

        } else if (stringModel.contains("PolyphonyRNN")) {
            stringModel = "polyphony";

        } else if (stringModel.contains("PianoRollRNN")) {
            stringModel = "pianoroll";
        } else if (stringModel.contains("Auto")) {
            stringModel = "chord_pitches_improv";
        }


        File fileZipContinued = new File("/data/data/ro.ase.musapp/files/songscontinued.zip");
        JSONObject jsonObject = new JSONObject();
        try {
            Intent it = getIntent();
            jsonObject.put("filename", it.getStringExtra("fileName"));
            jsonObject.put("model", stringModel);
            jsonObject.put("nr_steps", nrSteps);
            jsonObject.put("first_note", "");

            jsonObject.put("backing_chords", backingChordsImprov);
            jsonObject.put("outputs", nrOutputs);


            Log.d("JSON", jsonObject.toString());

        } catch (JSONException e) {
            e.printStackTrace();
        }

        if (nrOutputs > 5) {
            connectionTimeout = 90000;
        }

        RequestParams params = new RequestParams();
        try {
            Intent it = getIntent();
            String pathOfSongToContinueFrom = it.getStringExtra("song");
            File songToContinueFrom = new File(pathOfSongToContinueFrom);
            params.put("midfile", songToContinueFrom);

        } catch (IOException e) {
            e.printStackTrace();
        }
        StringEntity se = new StringEntity(jsonObject.toString());

        Context context = this;

        AsyncHttpClient clientMidi = new AsyncHttpClient();
        clientMidi.setTimeout(connectionTimeout);
        clientMidi.setMaxRetriesAndTimeout(1, connectionTimeout);
        clientMidi.post(ipAddresStoreSample, params, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {


                new InteractWithServerContinueTrack(ro.ase.musapp.FormularContinueActivity.this).execute(ipAddressContinue, jsonObject.toString());


            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                Toast.makeText(context, "Failed to connect to the server", Toast.LENGTH_SHORT).show();
                finish();
            }
        });


        stringModel = ((Spinner) (findViewById(R.id.spinner_model_continue))).getSelectedItem().toString();

    }


}
