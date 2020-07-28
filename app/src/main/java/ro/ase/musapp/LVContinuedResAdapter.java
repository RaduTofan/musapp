package ro.ase.musapp;

import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.FileProvider;

import com.mongodb.Block;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.gridfs.GridFSBucket;
import com.mongodb.client.gridfs.GridFSBuckets;
import com.mongodb.client.gridfs.model.GridFSFile;
import com.mongodb.client.gridfs.model.GridFSUploadOptions;

import org.bson.Document;
import org.bson.types.ObjectId;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

public class LVContinuedResAdapter extends ArrayAdapter<Song> {

    private int resourceID;
    private Context theContext;
    public MediaPlayer player;
    private View v;
    private boolean songIsPlaying=false;
    private ImageView viewOfPreviouslyClickedItem=null;
    private Runnable runnable;
    private Handler handler;

    MongoDatabase database = FormularActivity.mobileClient.getDatabase("MusappDB");

    GridFSBucket gridFSBucket = GridFSBuckets.create(database, "songsMIDI");

    public LVContinuedResAdapter(@NonNull Context context, int resource, @NonNull List<Song> objects) {
        super(context, resource, objects);
        resourceID=resource;
        theContext=context;
        handler = new Handler();
    }



    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        Song song = getItem(position);
        LayoutInflater inflater = LayoutInflater.from(getContext());
        v = inflater.inflate(resourceID,null);
        final String songDirectory=song.getDirectory();

        TextView tv = v.findViewById(R.id.id_textView_SongName);
        tv.setText(song.getName());



        final ImageView imageViewPlay = v.findViewById(R.id.imageViewPlay);

        imageViewPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(songIsPlaying==false){
                    viewOfPreviouslyClickedItem=v.findViewById(R.id.imageViewPlay);
                    songIsPlaying=true;
                    imageViewPlay.setImageResource(R.drawable.pause);
                    play(songDirectory);
                }else{
                    viewOfPreviouslyClickedItem.setImageResource(R.drawable.play);
                    songIsPlaying=false;
                    stopPlayer();
                    imageViewPlay.setImageResource(R.drawable.play);
                    if(viewOfPreviouslyClickedItem!=imageViewPlay){
                        viewOfPreviouslyClickedItem=v.findViewById(R.id.imageViewPlay);
                        songIsPlaying=true;
                        imageViewPlay.setImageResource(R.drawable.pause);
                        play(songDirectory);
                    }
                }

            }
        });

        ImageView imageViewSave=v.findViewById(R.id.imageViewSave);

        imageViewSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                try {
                    InputStream streamToUploadFrom = new FileInputStream(new File(song.getDirectory()));
                    GridFSUploadOptions options = new GridFSUploadOptions()
                            .chunkSizeBytes(255000)
                            .metadata(new Document());

                    ObjectId fileId = gridFSBucket.uploadFromStream(song.getName(), streamToUploadFrom, options);

                    gridFSBucket.find().forEach(
                            new Block<GridFSFile>() {
                                public void apply(final GridFSFile gridFSFile) {
                                    if(gridFSFile.getFilename().equals(song.getName())){
                                        imageViewSave.setImageResource(R.drawable.check);
                                        Toast.makeText(theContext, "Saved", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                    imageViewSave.setClickable(false);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

        ImageView imageViewShare=v.findViewById(R.id.imageViewShare);

        imageViewShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stopPlayer();
                File songFile = new File(songDirectory);

                Uri path = FileProvider.getUriForFile(theContext,"ro.ase.musapp.fileprovider",songFile);

                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_SEND);
                intent.putExtra(Intent.EXTRA_STREAM,path);
                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                intent.setType("audio/midi");

                theContext.startActivity(Intent.createChooser(intent,"Choose an app: "));
            }
        });


        ListViewWithContinuedResults.seekBarResults.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser) {
                    player.seekTo(progress);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });


        return v;

    }

    public void play(String songPath){
        if(player==null){
            player=MediaPlayer.create(theContext, Uri.parse(songPath));
            player.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    viewOfPreviouslyClickedItem.setImageResource(R.drawable.play);
                    songIsPlaying=false;
                    stopPlayer();
                    ListViewWithContinuedResults.seekBarResults.setProgress(0);
                }
            });
        }
        player.start();
        ListViewWithContinuedResults.seekBarResults.setEnabled(true);
        ListViewWithContinuedResults.seekBarResults.setMax(player.getDuration());
        ListViewWithContinuedResults.textViewEndTime.setText(createTimerLabel(ListViewWithContinuedResults.seekBarResults.getMax()));
        changeSeekBar();
    }

    public void stopPlayer(){
        if(player!=null){
            player.release();
            player=null; //releases the memory
            ListViewWithContinuedResults.seekBarResults.setEnabled(false);

        }
    }

    private void changeSeekBar() {
        if (player != null) {
            ListViewWithContinuedResults.seekBarResults.setProgress(player.getCurrentPosition());

            if (player.isPlaying()) {
                runnable = new Runnable() {
                    @Override
                    public void run() {
                        changeSeekBar();
                        ListViewWithContinuedResults.textViewStartTime.setText(createTimerLabel(ListViewWithContinuedResults.seekBarResults.getProgress()));
                    }
                };
                handler.postDelayed(runnable, 1000);
            }
        }


    }

    private String createTimerLabel(int duration) {
        String timerLabel = "";
        int min = duration / 1000 / 60;
        int sec = duration / 1000 % 60;

        timerLabel+=min+":";
        if(sec<10){
            timerLabel+="0";
        }
        timerLabel+=sec;

        return timerLabel;
    }
}
