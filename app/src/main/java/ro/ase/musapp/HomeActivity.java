package ro.ase.musapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.view.View;

public class HomeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        ConstraintLayout constraintLayout=findViewById(R.id.id_layoutHome);
        AnimationDrawable animationDrawable=(AnimationDrawable)constraintLayout.getBackground();
        animationDrawable.setEnterFadeDuration(2000);
        animationDrawable.setExitFadeDuration(2000);
        animationDrawable.start();

    }

    public void createNewSongs(View v){
        Intent it = new Intent(this,FormularActivity.class);
        startActivity(it);
    }


    public void openMyLibrary(View view) {
        Intent it = new Intent(this, MyLibraryActivity.class);
        startActivity(it);
//        Intent it = new Intent(this, ListViewWithResult.class);
//        startActivity(it);

    }


    @Override
    protected void onResume() {
        super.onResume();
        ConstraintLayout constraintLayout=findViewById(R.id.id_layoutHome);
        AnimationDrawable animationDrawable=(AnimationDrawable)constraintLayout.getBackground();
        animationDrawable.setEnterFadeDuration(2000);
        animationDrawable.setExitFadeDuration(2000);
        animationDrawable.start();
    }
}
