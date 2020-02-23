package com.jackg.programmers.challenges.imagememo;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

public class MainActivity extends AppCompatActivity {

    private NavController navController;
    private AppBarConfiguration appBarConfiguration;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        navController = Navigation.findNavController(this, R.id.fragment);
        appBarConfiguration =
                        new AppBarConfiguration.Builder(navController.getGraph()).build();
        Toolbar toolbar = findViewById(R.id.toolbar);

        setSupportActionBar(toolbar);

        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);

        if (!hasPermissionWrite()) {
            requestPermission();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        new MenuInflater(this).inflate(R.menu.menu_main, menu);

        navController.addOnDestinationChangedListener((controller, destination, arguments) -> {
            switch (destination.getId()) {
                case R.id.memoListFragment:
                    menu.findItem(R.id.memoListFragment).setVisible(false);
                    menu.findItem(R.id.memoWriteFragment).setVisible(true);
                    menu.findItem(R.id.memoDetailFragment).setVisible(false);
                    menu.findItem(R.id.memoModifyFragment).setVisible(false);
                    break;
                case R.id.memoDetailFragment:
                case R.id.memoModifyFragment:
                    menu.findItem(R.id.memoListFragment).setVisible(true);
                    menu.findItem(R.id.memoWriteFragment).setVisible(true);
                    menu.findItem(R.id.memoDetailFragment).setVisible(false);
                    menu.findItem(R.id.memoModifyFragment).setVisible(false);
                    break;
                case R.id.memoWriteFragment:
                    menu.findItem(R.id.memoListFragment).setVisible(true);
                    menu.findItem(R.id.memoWriteFragment).setVisible(false);
                    menu.findItem(R.id.memoDetailFragment).setVisible(false);
                    menu.findItem(R.id.memoModifyFragment).setVisible(false);
                    break;
            }
        });

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        return NavigationUI.onNavDestinationSelected(item, navController) || super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onSupportNavigateUp() {
        return NavigationUI.navigateUp(navController, appBarConfiguration) || super.onSupportNavigateUp();
    }

    public boolean hasPermissionWrite(){
        return ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                        == PackageManager.PERMISSION_GRANTED;
    }

    public void requestPermission() {
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 121);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == 121) {
            if (grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "권한이 없으면 앱 이용에 제한이 있습니다.", Toast.LENGTH_SHORT).show();
                requestPermission();
            }
        }

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }
}
