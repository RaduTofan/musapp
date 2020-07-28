package ro.ase.musapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.media.Image;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.leff.midi.MidiFile;
import com.leff.midi.MidiTrack;
import com.leff.midi.event.MidiEvent;
import com.leff.midi.event.NoteOn;
import com.leff.midi.event.meta.Tempo;
import com.leff.midi.event.meta.TimeSignature;
import com.leff.midi.examples.EventPrinter;
import com.leff.midi.util.MidiProcessor;
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

public class FormularActivity extends AppCompatActivity {

    private final String adressWithoutPort = "18.217.62.152";
    private final String ipAddress = "http://" + adressWithoutPort + ":8080/";

    private AlertDialog.Builder alertDialogProecessing = null;
    public static Activity mainActivity;
    public MediaPlayer player;

    public static StitchAppClient client =
            Stitch.initializeDefaultAppClient("ro.ase.musapp");

    public static MongoClient mobileClient =
            client.getServiceClient(LocalMongoDbService.clientFactory);

    MongoDatabase database = mobileClient.getDatabase("MusappDB");
    GridFSBucket gridFSBucket = GridFSBuckets.create(database, "songsMIDI");


    private LinearLayout linearLayoutImprov=null;
    private LinearLayout linearLayoutElse=null;
    private String stringModel=null;


    private int clickedPrimersElse=1;
    private int clickedChordsImprov=1;
    private int clickedPrimersImprov=1;
    private List<Spinner> spinnersNoteElse=new ArrayList<>();
    private List<Spinner> spinnersChordImprov=new ArrayList<>();
    private List<Spinner> spinnersNoteImprov=new ArrayList<>();



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mainActivity = this;



        ImageView ivLibrary=findViewById(R.id.idImgViewLibraryCreateSongMain);
        ivLibrary.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent it = new Intent(FormularActivity.this, MyLibraryActivity.class);
                startActivity(it);
                finish();
            }
        });




        linearLayoutImprov=findViewById(R.id.LinearLayoutImprov);
        linearLayoutElse=findViewById(R.id.LinearLayoutElse);
        Spinner spinnerModel = findViewById(R.id.spinner_model);



        Spinner spinnerChordImprov1=findViewById(R.id.spinner_ChordsImprov1);
        spinnersChordImprov.add(spinnerChordImprov1);
        Spinner spinnerNoteElse1=findViewById(R.id.spinner_NoteElse1);
        spinnersNoteElse.add(spinnerNoteElse1);
        Spinner spinnerNoteImprov1=findViewById(R.id.spinner_NoteImprov1);
        spinnersNoteImprov.add(spinnerNoteImprov1);
        Spinner spinnerChordImprov2=findViewById(R.id.spinner_ChordsImprov2);
        spinnersChordImprov.add(spinnerChordImprov2);
        Spinner spinnerNoteElse2=findViewById(R.id.spinner_NoteElse2);
        spinnersNoteElse.add(spinnerNoteElse2);
        Spinner spinnerNoteImprov2=findViewById(R.id.spinner_NoteImprov2);
        spinnersNoteImprov.add(spinnerNoteImprov2);
        Spinner spinnerChordImprov3=findViewById(R.id.spinner_ChordsImprov3);
        spinnersChordImprov.add(spinnerChordImprov3);
        Spinner spinnerNoteElse3=findViewById(R.id.spinner_NoteElse3);
        spinnersNoteElse.add(spinnerNoteElse3);
        Spinner spinnerNoteImprov3=findViewById(R.id.spinner_NoteImprov3);
        spinnersNoteImprov.add(spinnerNoteImprov3);
        Spinner spinnerChordImprov4=findViewById(R.id.spinner_ChordsImprov4);
        spinnersChordImprov.add(spinnerChordImprov4);
        Spinner spinnerNoteElse4=findViewById(R.id.spinner_NoteElse4);
        spinnersNoteElse.add(spinnerNoteElse4);
        Spinner spinnerNoteImprov4=findViewById(R.id.spinner_NoteImprov4);
        spinnersNoteImprov.add(spinnerNoteImprov4);
        Spinner spinnerChordImprov5=findViewById(R.id.spinner_ChordsImprov5);
        spinnersChordImprov.add(spinnerChordImprov5);
        Spinner spinnerNoteElse5=findViewById(R.id.spinner_NoteElse5);
        spinnersNoteElse.add(spinnerNoteElse5);
        Spinner spinnerNoteImprov5=findViewById(R.id.spinner_NoteImprov5);
        spinnersNoteImprov.add(spinnerNoteImprov5);

        ImageButton addPrimersElse=findViewById(R.id.imageAddPrimersElse);
        ImageButton addChordsImprov=findViewById(R.id.imageAddBackingChords);
        ImageButton addPrimersImprov=findViewById(R.id.imageAddPrimersImprov);


        String[] tempArrayPrimerForSpinner=getResources().getStringArray(R.array.Primers);
        ArrayList<String> stringArrayListPrimersForSpinner=new ArrayList<>();
        for (String string:tempArrayPrimerForSpinner) {
            stringArrayListPrimersForSpinner.add(string);
        }
        String[] tempArrayChordsForSpinner=getResources().getStringArray(R.array.Chords);
        ArrayList<String> stringArrayListChordsForSpinner=new ArrayList<>();
        for (String string:tempArrayChordsForSpinner) {
            stringArrayListChordsForSpinner.add(string);
        }

        String[] tempArrayConfigsForSpinnerModelIsMelody=getResources().getStringArray(R.array.ModelIsMelodyRNN);
        ArrayList<String> stringArrayListConfigsForSpinnerModelIsMelody=new ArrayList<>();
        for (String string:tempArrayConfigsForSpinnerModelIsMelody) {
            stringArrayListConfigsForSpinnerModelIsMelody.add(string);
        }
        String[] tempArrayConfigsForSpinnerModelIsPerformance=getResources().getStringArray(R.array.ModelIsPerfromance);
        ArrayList<String> stringArrayListConfigsForSpinnerModelIsPerformance=new ArrayList<>();
        for (String string:tempArrayConfigsForSpinnerModelIsPerformance) {
            stringArrayListConfigsForSpinnerModelIsPerformance.add(string);
        }

        SpinnerFormularAdapter adapter=new SpinnerFormularAdapter(this,
                R.layout.custom_spinner_formular,R.id.customSpinnerTextView,stringArrayListPrimersForSpinner);
        SpinnerFormularAdapter adapterChords=new SpinnerFormularAdapter(this,
                R.layout.custom_spinner_formular,R.id.customSpinnerTextView,stringArrayListChordsForSpinner);
        spinnerChordImprov1.setAdapter(adapterChords);
        spinnerNoteElse1.setAdapter(adapter);
        spinnerNoteImprov1.setAdapter(adapter);
        spinnerChordImprov2.setAdapter(adapterChords);
        spinnerNoteElse2.setAdapter(adapter);
        spinnerNoteImprov2.setAdapter(adapter);
        spinnerChordImprov3.setAdapter(adapterChords);
        spinnerNoteElse3.setAdapter(adapter);
        spinnerNoteImprov3.setAdapter(adapter);
        spinnerChordImprov4.setAdapter(adapterChords);
        spinnerNoteElse4.setAdapter(adapter);
        spinnerNoteImprov4.setAdapter(adapter);
        spinnerChordImprov5.setAdapter(adapterChords);
        spinnerNoteElse5.setAdapter(adapter);
        spinnerNoteImprov5.setAdapter(adapter);

        SpinnerFormularAdapter adapterModelIsMelody=new SpinnerFormularAdapter(this,
                R.layout.custom_spinner_formular,R.id.customSpinnerTextView,stringArrayListConfigsForSpinnerModelIsMelody);
        SpinnerFormularAdapter adapterModelIsPerformance=new SpinnerFormularAdapter(this,
                R.layout.custom_spinner_formular,R.id.customSpinnerTextView,stringArrayListConfigsForSpinnerModelIsPerformance);
        Spinner spinnerConfig=findViewById(R.id.spinner_config);


        spinnerModel.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                stringModel=spinnerModel.getSelectedItem().toString();
                if(stringModel.contains("Auto") || stringModel.contains("ImprovRNN")){
                    linearLayoutImprov.setVisibility(View.VISIBLE);
                    linearLayoutElse.setVisibility(View.GONE);
                }else{
                    linearLayoutImprov.setVisibility(View.GONE);
                    linearLayoutElse.setVisibility(View.VISIBLE);
                }
                if(stringModel.contains("MelodyRNN")){
                    (findViewById(R.id.idTextViewConfig)).setVisibility(View.VISIBLE);
                    spinnerConfig.setAdapter(adapterModelIsMelody);
                    spinnerConfig.setVisibility(View.VISIBLE);

                }else if (stringModel.contains("PerformanceRNN")){
                    (findViewById(R.id.idTextViewConfig)).setVisibility(View.VISIBLE);
                    spinnerConfig.setAdapter(adapterModelIsPerformance);
                    spinnerConfig.setVisibility(View.VISIBLE);
                }else{
                    (findViewById(R.id.idTextViewConfig)).setVisibility(View.GONE);
                    spinnerConfig.setVisibility(View.GONE);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        addPrimersElse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(clickedPrimersElse<5){
                    spinnersNoteElse.get(clickedPrimersElse).setVisibility(View.VISIBLE);
                    clickedPrimersElse++;

                }
            }
        });

        addPrimersImprov.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(clickedPrimersImprov<5){
                    spinnersNoteImprov.get(clickedPrimersImprov).setVisibility(View.VISIBLE);
                    clickedPrimersImprov++;
                }
            }
        });

        addChordsImprov.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(clickedChordsImprov<5){
                    spinnersChordImprov.get(clickedChordsImprov).setVisibility(View.VISIBLE);
                    clickedChordsImprov++;

                }
            }
        });


    }


    public void magic(View view) {
        alertDialogProecessing = new AlertDialog.Builder(FormularActivity.this);
        View customView = LayoutInflater.from(FormularActivity.this).inflate(R.layout.alert_dialog_progress_bar, null);
        alertDialogProecessing.setCancelable(false);
        alertDialogProecessing.setView(customView);
        alertDialogProecessing.create();
        alertDialogProecessing.show();


        ConstraintLayout constraintLayout= customView.findViewById(R.id.id_AlertDialog);
        AnimationDrawable animationDrawable=(AnimationDrawable)constraintLayout.getBackground();
        animationDrawable.setEnterFadeDuration(2000);
        animationDrawable.setExitFadeDuration(2000);
        animationDrawable.start();

        int[] primerMelody;
        int nrSteps=0;
        String backingChordsImprov="";
        int nrOutputs;


        if(stringModel.contains("Auto") || stringModel.contains("ImprovRNN")){
            primerMelody=new int[clickedPrimersImprov];
            for(int ind=0;ind<clickedPrimersImprov;ind++) {
                if (spinnersNoteImprov.get(ind).getSelectedItem().toString().contains("Auto")) {
                    primerMelody[ind]=60;
                } else {
                    primerMelody[ind]=Integer.parseInt(spinnersNoteImprov.get(ind).getSelectedItem().toString());
                }
            }

            for(int i=0;i<clickedChordsImprov;i++) {
                if (spinnersChordImprov.get(i).getSelectedItem().toString().contains("Auto")) {
                    int AutoChords=new Random().nextInt(2);
                    switch (AutoChords) {
                        case 0:
                            backingChordsImprov="C G Am F C G Am F";i=5;break;
                        case 1:
                            backingChordsImprov="Am Dm G C F Bdim E E";i=5;break;
                    }
                } else {
                    backingChordsImprov+=spinnersChordImprov.get(i).getSelectedItem().toString()+" ";

                }
            }
            nrOutputs=Integer.parseInt(((Spinner)findViewById(R.id.spinner_NrOutPutImprov)).getSelectedItem().toString());

        }else{
            if(stringModel.contains("Performance")){
                if(((Spinner)findViewById(R.id.spinner_stepsElse)).getSelectedItem().toString().contains("Medium")){
                    nrSteps=3200;
                }else if(((Spinner)findViewById(R.id.spinner_stepsElse)).getSelectedItem().toString().contains("Long")){
                    nrSteps=6000;
                }else{
                    nrSteps=1600;
                }
            }else if(stringModel.contains("PianoRoll")){
                if(((Spinner)findViewById(R.id.spinner_stepsElse)).getSelectedItem().toString().contains("Medium")){
                    nrSteps=512;
                }else if(((Spinner)findViewById(R.id.spinner_stepsElse)).getSelectedItem().toString().contains("Long")){
                    nrSteps=1024;
                }else{
                    nrSteps=256;
                }
            }else{
                if(((Spinner)findViewById(R.id.spinner_stepsElse)).getSelectedItem().toString().contains("Medium")){
                    nrSteps=256;
                }else if(((Spinner)findViewById(R.id.spinner_stepsElse)).getSelectedItem().toString().contains("Long")){
                    nrSteps=512;
                }else{
                    nrSteps=128;
                }
            }

            primerMelody=new int[clickedPrimersElse];
            for(int ind=0;ind<clickedPrimersElse;ind++) {
                if (spinnersNoteElse.get(ind).getSelectedItem().toString().contains("Auto")) {
                    if(stringModel.contains("DrumKit")){
                        primerMelody[ind]=0;
                    }else{
                    primerMelody[ind]=60;
                    }
                } else {
                    primerMelody[ind]=Integer.parseInt(spinnersNoteElse.get(ind).getSelectedItem().toString());
                }
            }

            nrOutputs=Integer.parseInt(((Spinner)findViewById(R.id.spinner_NrOutPutElse)).getSelectedItem().toString());

        }

        if(stringModel.contains("MelodyRNN")){
            stringModel=((Spinner)findViewById(R.id.spinner_config)).getSelectedItem().toString();
            if(stringModel.contains("Basic")){
                stringModel="basic_rnn";
            }else if(stringModel.contains("Mono")){
                stringModel="mono_rnn";
            }else if(stringModel.contains("Lookback")){
                stringModel="lookback_rnn";
            }else if(stringModel.contains("Attention")){
                stringModel="attention_rnn";
            }
        }else if(stringModel.contains("DrumKit")){
            stringModel="drum_kit";

        }else if(stringModel.contains("ImprovRNN")){
            stringModel="chord_pitches_improv";

        }else if(stringModel.contains("PerformanceRNN")){
            stringModel=((Spinner)findViewById(R.id.spinner_config)).getSelectedItem().toString();
            if(stringModel.contains("Performance")){
                stringModel="performance";
            }else if(stringModel.contains("Dynamics")){
                stringModel="performance_with_dynamics";
            }else if(stringModel.contains("Modulo")){
                stringModel="performance_with_dynamics_and_modulo_encoding";
            }else if(stringModel.contains("Density")){
                stringModel="density_conditioned_performance_with_dynamics";
            }else if(stringModel.contains("Pitch")){
            stringModel="pitch_conditioned_performance_with_dynamics";
            }else if(stringModel.contains("Multiconditioned")){
            stringModel="multiconditioned_performance_with_dynamics";
            }

        }else if(stringModel.contains("PolyphonyRNN")){
            stringModel="polyphony";

        }else if(stringModel.contains("PianoRollRNN")){
            stringModel="pianoroll";
        }
        else if(stringModel.contains("Auto")){
            stringModel="chord_pitches_improv";
        }

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("model", stringModel);
            jsonObject.put("nr_steps", nrSteps);
            JSONArray array = new JSONArray();
            for (int i = 0; i < primerMelody.length; i++) {
                array.put(primerMelody[i]);
            }
            jsonObject.put("first_note", array);
            jsonObject.put("backing_chords",backingChordsImprov);
            jsonObject.put("outputs", nrOutputs);

            try {
                FileOutputStream fos = openFileOutput("songs.zip", MODE_PRIVATE);
                fos.close();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

            new InteractWithServer(FormularActivity.this).execute(ipAddress, jsonObject.toString());

        } catch (JSONException e) {
            e.printStackTrace();
        }

        stringModel=((Spinner)(findViewById(R.id.spinner_model))).getSelectedItem().toString();

    }





    public void playMelodyImprov(View view) {

        MidiTrack noteTrack = new MidiTrack();


        int j=1;
        for(int i=0;i<clickedPrimersImprov;i++){
            if(spinnersNoteImprov.get(i).getSelectedItem().toString().contains("Auto")){
                noteTrack.insertNote(0, 60, 100, 240 * (i + j), 15);
                j++;
            }else {
                noteTrack.insertNote(0, Integer.parseInt(spinnersNoteImprov.get(i).getSelectedItem().toString()), 100, 240 * (i + j), 15);
                j++;
            }

        }


        List<MidiTrack> tracks = new ArrayList<MidiTrack>();
        tracks.add(noteTrack);
        MidiFile midi = new MidiFile(MidiFile.DEFAULT_RESOLUTION, (ArrayList<MidiTrack>) tracks);

        File output = new File("/data/data/ro.ase.musapp/melodyimprov.mid");
        try
        {
            midi.writeToFile(output);
        }
        catch(IOException e)
        {
            System.err.println(e);
        }

        play("/data/data/ro.ase.musapp/melodyimprov.mid");
        ((ImageView)findViewById(R.id.playMelodyImprov)).setImageResource(R.drawable.pause);


    }



    public void playMelodyElse(View view) {
        MidiTrack noteTrack = new MidiTrack();


        int j=1;
        for(int i=0;i<clickedPrimersElse;i++){
            if(spinnersNoteElse.get(i).getSelectedItem().toString().contains("Auto")){
                noteTrack.insertNote(0, 60, 100, 240 * (i + j), 15);
                j++;
            }else {
                noteTrack.insertNote(0, Integer.parseInt(spinnersNoteElse.get(i).getSelectedItem().toString()), 100, 240 * (i + j), 15);
                j++;
            }
        }


        List<MidiTrack> tracks = new ArrayList<MidiTrack>();
        tracks.add(noteTrack);
        MidiFile midi = new MidiFile(MidiFile.DEFAULT_RESOLUTION, (ArrayList<MidiTrack>) tracks);

        File output = new File("/data/data/ro.ase.musapp/melodyelse.mid");
        try
        {
            midi.writeToFile(output);
        }
        catch(IOException e)
        {
            System.err.println(e);
        }

        play("/data/data/ro.ase.musapp/melodyelse.mid");
        ((ImageView)findViewById(R.id.playMelodyElse)).setImageResource(R.drawable.pause);

    }

    public void play(String songPath){
        if(player==null){
            player= MediaPlayer.create(this, Uri.parse(songPath));
            player.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    ((ImageView)findViewById(R.id.playMelodyImprov)).setImageResource(R.drawable.play);
                    ((ImageView)findViewById(R.id.playMelodyElse)).setImageResource(R.drawable.play);
                    stopPlayer();
                }
            });
        }
        player.start();
    }

    public void stopPlayer(){
        if(player!=null){
            player.release();
            player=null; //releases the memory

        }
    }


}
