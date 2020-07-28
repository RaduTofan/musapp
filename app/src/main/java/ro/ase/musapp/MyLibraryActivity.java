package ro.ase.musapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.mongodb.Block;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.gridfs.GridFSBucket;
import com.mongodb.client.gridfs.GridFSBuckets;
import com.mongodb.client.gridfs.model.GridFSFile;

import org.bson.types.ObjectId;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MyLibraryActivity extends AppCompatActivity {

    private List<Song> songList = new ArrayList<>();
    private LVLibraryAdapter adapter = null;
    public static MyLibraryActivity myLibraryActivity;
    public static SeekBar seekBarLibrary;
    public static TextView textViewStartTime;
    public static TextView textViewEndTime;

    MongoDatabase database = FormularActivity.mobileClient.getDatabase("MusappDB");

    GridFSBucket gridFSBucket = GridFSBuckets.create(database, "songsMIDI");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_library);



        ImageView ivCreate=findViewById(R.id.idImgViewCreateLibrary);
        ivCreate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(adapter.player!=null){
                    adapter.stopPlayer();
                }
                Intent it = new Intent(MyLibraryActivity.this,FormularActivity.class);
                startActivity(it);
                finish();
            }
        });


        seekBarLibrary=findViewById(R.id.seekBarLibrary);
        seekBarLibrary.setEnabled(false);
        textViewStartTime=findViewById(R.id.idTextViewStartTimeLibrary);
        textViewEndTime=findViewById(R.id.idTextViewEndTimeLibrary);


        myLibraryActivity=this;

        File file = new File("/data/data/ro.ase.musapp/files/myLibrary");
        if(!file.isDirectory()){
            file.mkdirs();
        }

        gridFSBucket.find().forEach(
                new Block<GridFSFile>() {
                    public void apply(final GridFSFile gridFSFile) {
                        try {
                            FileOutputStream streamToDownloadTo =
                                    new FileOutputStream("/data/data/ro.ase.musapp/files/myLibrary/"
                                            + gridFSFile.getFilename());
                            //SELECT * FROM TABELA
                            gridFSBucket.downloadToStream(gridFSFile.getId(), streamToDownloadTo);
                            streamToDownloadTo.close();
                            songList.add(new Song(gridFSFile.getFilename(), gridFSFile.getObjectId()));
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                });

        if (songList.size()>=1) {
            ListView listView = findViewById(R.id.id_ListView_My_Library);
            adapter = new LVLibraryAdapter(this, R.layout.items_layout_for_my_library, songList);
            listView.setAdapter(adapter);
            ((TextView) findViewById(R.id.id_textview_MyLibrary)).setVisibility(View.GONE);


        } else {
            ListView listView = findViewById(R.id.id_ListView_My_Library);
            adapter = new LVLibraryAdapter(this, R.layout.items_layout_for_my_library, songList);
            listView.setAdapter(adapter);
            ((TextView) findViewById(R.id.id_textview_MyLibrary)).setVisibility(View.VISIBLE);
        }

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if (adapter.player != null) {
            adapter.player.release();
            adapter.player = null;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        adapter.clear();
        gridFSBucket.find().forEach(
                new Block<GridFSFile>() {
                    public void apply(final GridFSFile gridFSFile) {
                        try {
                            FileOutputStream streamToDownloadTo =
                                    new FileOutputStream("/data/data/ro.ase.musapp/files/myLibrary/" + gridFSFile.getFilename());
                            gridFSBucket.downloadToStream(gridFSFile.getId(), streamToDownloadTo);
                            streamToDownloadTo.close();
                            songList.add(new Song(gridFSFile.getFilename(), gridFSFile.getObjectId()));
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                });


        ListView listView = findViewById(R.id.id_ListView_My_Library);
        adapter = new LVLibraryAdapter(this, R.layout.items_layout_for_my_library, songList);
        listView.setAdapter(adapter);
    }
}
