package ro.ase.musapp;

import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.FileProvider;

import com.mongodb.client.MongoDatabase;
import com.mongodb.client.gridfs.GridFSBucket;
import com.mongodb.client.gridfs.GridFSBuckets;

import java.io.File;
import java.net.URI;
import java.util.List;

public class LVLibraryAdapter extends ArrayAdapter<Song> {

    private int resourceID;
    private Context theContext;
    private View v;
    private List<Song> listSongs;
    public MediaPlayer player;
    private ImageView viewOfPreviouslyClickedItem = null;
    private boolean songIsPlaying = false;
    private Runnable runnable;
    private Handler handler;

    MongoDatabase database = FormularActivity.mobileClient.getDatabase("MusappDB");

    GridFSBucket gridFSBucket = GridFSBuckets.create(database, "songsMIDI");

    public LVLibraryAdapter(@NonNull Context context, int resource, @NonNull List<Song> objects) {
        super(context, resource, objects);
        resourceID = resource;
        theContext = context;
        listSongs = objects;
        handler = new Handler();
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        Song song = getItem(position);
        LayoutInflater inflater = LayoutInflater.from(getContext());
        v = inflater.inflate(resourceID, null);
        final String songDirectory = "/data/data/ro.ase.musapp/files/myLibrary/" + song.getName();

        TextView tv = v.findViewById(R.id.id_textView_SongName_LIBRARY);
        tv.setText(song.getName());

        ImageView imageViewDelete = v.findViewById(R.id.imageViewDelete_LIBRARY);

        imageViewDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stopPlayer();
                File currentFile = new File(songDirectory);
                currentFile.delete();
                gridFSBucket.delete(song.getSongObjectID());
                listSongs.remove(song);
                notifyDataSetChanged();
                if (listSongs.size() <= 0) {
                    MyLibraryActivity.myLibraryActivity.recreate();
                    tv.setVisibility(View.VISIBLE);
                }
            }
        });

        ImageView imageViewPlayLibrary = v.findViewById(R.id.imageViewPlay_LIBRARY);
        imageViewPlayLibrary.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (songIsPlaying == false) {
                    viewOfPreviouslyClickedItem = v.findViewById(R.id.imageViewPlay_LIBRARY);
                    songIsPlaying = true;
                    imageViewPlayLibrary.setImageResource(R.drawable.pause);
                    play(songDirectory);
                } else {
                    viewOfPreviouslyClickedItem.setImageResource(R.drawable.play);
                    songIsPlaying = false;
                    stopPlayer();
                    imageViewPlayLibrary.setImageResource(R.drawable.play);
                    if (viewOfPreviouslyClickedItem != imageViewPlayLibrary) {
                        viewOfPreviouslyClickedItem = v.findViewById(R.id.imageViewPlay_LIBRARY);
                        songIsPlaying = true;
                        imageViewPlayLibrary.setImageResource(R.drawable.pause);
                        play(songDirectory);
                    }
                }
            }
        });


        ImageView imageViewShareLibrary = v.findViewById(R.id.imageViewShare_LIBRARY);
        imageViewShareLibrary.setOnClickListener(new View.OnClickListener() {
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

        ImageView imageViewEditLibrary = v.findViewById(R.id.imageViewEdit_LIBRARY);
        imageViewEditLibrary.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stopPlayer();
                Intent it = new Intent(theContext, EditPageActivity.class);
                it.putExtra("song", song.getName());
                it.putExtra("objectID", song.getSongObjectID());

                MediaPlayer mediaPlayerToGetDuration=MediaPlayer.create(theContext, Uri.parse(songDirectory));
                int duration=mediaPlayerToGetDuration.getDuration();
                it.putExtra("duration",duration);

                theContext.startActivity(it);

            }
        });

        MyLibraryActivity.seekBarLibrary.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
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


    public void play(String songPath) {
        if (player == null) {
            player = MediaPlayer.create(theContext, Uri.parse(songPath));
            player.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    viewOfPreviouslyClickedItem.setImageResource(R.drawable.play);
                    songIsPlaying = false;
                    stopPlayer();
                    MyLibraryActivity.seekBarLibrary.setProgress(0);
                }
            });
        }
        player.start();
        MyLibraryActivity.seekBarLibrary.setEnabled(true);
        MyLibraryActivity.seekBarLibrary.setMax(player.getDuration());
        MyLibraryActivity.textViewEndTime.setText(createTimerLabel(MyLibraryActivity.seekBarLibrary.getMax()));
        changeSeekBar();

    }

    private void changeSeekBar() {
        if (player != null) {
            MyLibraryActivity.seekBarLibrary.setProgress(player.getCurrentPosition());

            if (player.isPlaying()) {
                runnable = new Runnable() {
                    @Override
                    public void run() {
                        changeSeekBar();
                        MyLibraryActivity.textViewStartTime.setText(createTimerLabel(MyLibraryActivity.seekBarLibrary.getProgress()));
                    }
                };
                handler.postDelayed(runnable, 1000);
            }
        }


    }

    public void stopPlayer() {
        if (player != null) {
            player.release();
            player = null;
            MyLibraryActivity.seekBarLibrary.setEnabled(false);
             //releases the memory

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
