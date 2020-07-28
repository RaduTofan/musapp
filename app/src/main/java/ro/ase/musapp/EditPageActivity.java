package ro.ase.musapp;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

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

public class EditPageActivity extends AppCompatActivity {

    private String songName;
    private ObjectId songObjectID;

    MongoDatabase database = FormularActivity.mobileClient.getDatabase("MusappDB");

    GridFSBucket gridFSBucket = GridFSBuckets.create(database, "songsMIDI");

    private File songThatIsBeingEdited=null;

    private long mLastClickTime = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_page);



        Intent it =getIntent();
        songName=it.getStringExtra("song");
        Bundle bundle = it.getExtras();
        songObjectID= (ObjectId) bundle.get("objectID");
        String songNameWithoutExtension=songName.substring(0,songName.length()-4); //removes .mid at the end
        ((EditText)findViewById(R.id.id_EditText_SongName_EditPage)).setText(songNameWithoutExtension);

        songThatIsBeingEdited=new File("/data/data/ro.ase.musapp/files/myLibrary/"+songName);

        ImageView ivLibrary=findViewById(R.id.idImgViewLibraryEditPage);
        ivLibrary.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });


        ImageView ivCreate=findViewById(R.id.idImgViewCreateEditPage);
        ivCreate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent it = new Intent(EditPageActivity.this,FormularActivity.class);
                startActivity(it);
                finish();
            }
        });

    }


    public void rename(View view) {
        if (SystemClock.elapsedRealtime() - mLastClickTime < 2000) {
            return;
        }
        mLastClickTime = SystemClock.elapsedRealtime();

        String newName=((EditText)findViewById(R.id.id_EditText_SongName_EditPage)).getText().toString();
        boolean renameResult = songThatIsBeingEdited.renameTo(new File("/data/data/ro.ase.musapp/files/myLibrary/"+newName+".mid"));

        gridFSBucket.delete(songObjectID);

        try {
            InputStream streamToUploadFrom = new FileInputStream(new File("/data/data/ro.ase.musapp/files/myLibrary/"+newName+".mid"));
            GridFSUploadOptions options = new GridFSUploadOptions()
                    .chunkSizeBytes(255000)
                    .metadata(new Document());

            ObjectId fileId = gridFSBucket.uploadFromStream(newName+".mid", streamToUploadFrom, options);

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        finish();
        Toast.makeText(this, "Renamed successfully", Toast.LENGTH_SHORT).show();
    }


    public void continueSong(View view) {
        Intent intentDuration =getIntent();
        int duration=intentDuration.getIntExtra("duration",-1);

        if(duration>60000){
            AlertDialog.Builder builder1 = new AlertDialog.Builder(this);
            builder1.setMessage("The song's length is too big to be continued!");
            builder1.setCancelable(true);

            builder1.setPositiveButton(
                    "Close",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.cancel();
                        }
                    });



            AlertDialog alert11 = builder1.create();
            alert11.show();

        }else{
            if (SystemClock.elapsedRealtime() - mLastClickTime < 2000) {
                return;
            }
            mLastClickTime = SystemClock.elapsedRealtime();



            Intent it=new Intent(this,FormularContinueActivity.class);
            it.putExtra("song",songThatIsBeingEdited.getAbsolutePath());
            it.putExtra("fileName",songName);
            startActivity(it);
            finish();
        }



    }


}
