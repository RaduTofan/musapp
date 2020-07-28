package ro.ase.musapp;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;


public class ListViewWithResult extends AppCompatActivity {

    private List<Song> songList;

    private long backPressedTime;
    private Toast backToast;
    private LVResultAdapter adapter = null;
    public static SeekBar seekBarResults;
    public static TextView textViewStartTime;
    public static TextView textViewEndTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_view_with_result);



        ImageView ivLibrary=findViewById(R.id.idImgViewLibraryLvWithResult);
        ivLibrary.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(adapter.player!=null){
                    adapter.stopPlayer();
                }
                Intent it = new Intent(ListViewWithResult.this, MyLibraryActivity.class);
                startActivity(it);
                finish();
            }
        });


        ImageView ivCreate=findViewById(R.id.idImgViewCreateLvWithResult);
        ivCreate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(adapter.player!=null){
                    adapter.stopPlayer();
                }
                Intent it = new Intent(ListViewWithResult.this,FormularActivity.class);
                startActivity(it);
                finish();
            }
        });

        seekBarResults=findViewById(R.id.seekBarResults);
        seekBarResults.setEnabled(false);
        textViewStartTime=findViewById(R.id.idTextViewStartTimeResults);
        textViewEndTime=findViewById(R.id.idTextViewEndTimeResults);

        File pathWithSongs=new File("/data/data/ro.ase.musapp/files/songs");
        try {

            String[] entries=pathWithSongs.list();

            if(entries!=null) {
                for (String s : entries) { //deletes previous songs
                    File currentFile = new File(pathWithSongs.getPath(), s);
                    currentFile.delete();
                }
            }
            pathWithSongs.mkdirs();

            unzipTheFileReceivedFromTheServer();



        } catch (IOException e) {
            e.printStackTrace();
        }


        songList=new ArrayList<>();
        File[] listOfSongs=pathWithSongs.listFiles();
        for(int i=0;i<listOfSongs.length;i++){
            if(listOfSongs[i].isFile()){
                songList.add(new Song(listOfSongs[i].getName()));
            }
        }



        ListView lv = findViewById(R.id.id_ListView_With_Results);
        adapter=new LVResultAdapter(this,R.layout.items_layout_for_results,songList);
        lv.setAdapter(adapter);


    }


    @Override
    public void onBackPressed() {
        if(backPressedTime+2000>System.currentTimeMillis()){
            if(adapter.player!=null){
                adapter.player.release();
                adapter.player=null;

            }
            backToast.cancel();
            super.onBackPressed();
            finish();
            Intent it = new Intent(this, FormularActivity.class);
            startActivity(it);
            return;
        }else{
            backToast=Toast.makeText(this, "If you leave, unsaved songs will be lost", Toast.LENGTH_SHORT);
            backToast.show();
        }

        backPressedTime=System.currentTimeMillis();
    }



    public void unzipTheFileReceivedFromTheServer() throws IOException {
        String fileZip = "/data/data/ro.ase.musapp/files/songs.zip";
        File destDir = new File("/data/data/ro.ase.musapp/files/songs");
        byte[] buffer = new byte[1024];
        ZipInputStream zis = new ZipInputStream(new FileInputStream(fileZip));
        ZipEntry zipEntry = zis.getNextEntry();
        while (zipEntry != null) {
            File newFile = newFile(destDir, zipEntry);
            FileOutputStream fos = new FileOutputStream(newFile);
            int len;
            while ((len = zis.read(buffer)) > 0) {
                fos.write(buffer, 0, len);
            }
            fos.close();
            zipEntry = zis.getNextEntry();
        }
        zis.closeEntry();
        zis.close();
    }

    public static File newFile(File destinationDir, ZipEntry zipEntry) throws IOException {
        File destFile = new File(destinationDir, zipEntry.getName());

        String destDirPath = destinationDir.getCanonicalPath();
        String destFilePath = destFile.getCanonicalPath();

        if (!destFilePath.startsWith(destDirPath + File.separator)) {
            throw new IOException("Entry is outside of the target dir: " + zipEntry.getName());
        }

        return destFile;
    }

}
