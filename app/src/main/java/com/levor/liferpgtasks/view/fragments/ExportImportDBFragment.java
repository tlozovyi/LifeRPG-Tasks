package com.levor.liferpgtasks.view.fragments;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.Toast;

import com.levor.liferpgtasks.R;
import com.levor.liferpgtasks.Utils.FileUtils;
import com.levor.liferpgtasks.controller.LifeController;
import com.levor.liferpgtasks.dataBase.DataBaseHelper;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import butterknife.Bind;
import butterknife.ButterKnife;

public class ExportImportDBFragment extends DefaultFragment {

    private static final int WRITE_EXTERNAL_STORAGE_PERMISSION_CODE = 1;

    @Bind(R.id.auto_backup_to_dropbox_layout)       View autoExportToDropboxView;
    @Bind(R.id.auto_backup_to_dropbox_switch)       Switch autoExportToDropboxSwitch;
    @Bind(R.id.manual_export_DB_to_dropbox)         View exportToDropboxView;
    @Bind(R.id.import_db_from_dropbox_layout)       View importFromDropboxView;
    @Bind(R.id.export_db_to_filesystem_layout)      View exportToFileSystemView;
    @Bind(R.id.import_db_from_filesystem_layout)    View importFromFileSystemView;


    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_import_export_db, container, false);
        ButterKnife.bind(this, v);

        autoExportToDropboxView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                autoExportToDropboxSwitch.setChecked(!autoExportToDropboxSwitch.isChecked());
            }
        });

        autoExportToDropboxSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    getCurrentActivity().checkAndBackupToDropBox(true);
                    getController().getSharedPreferences().edit().putBoolean(LifeController.DROPBOX_AUTO_BACKUP_ENABLED, true).apply();
                } else {
                    getController().getSharedPreferences().edit().putBoolean(LifeController.DROPBOX_AUTO_BACKUP_ENABLED, false).apply();
                }
            }
        });

        exportToDropboxView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getCurrentActivity().checkAndBackupToDropBox(false);
            }
        });

        importFromDropboxView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getCurrentActivity().checkAndImportFromDropBox();
            }
        });

        exportToFileSystemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getController().closeDBConnection();
                File db = getCurrentActivity().getDatabasePath(DataBaseHelper.DATABASE_NAME);
                File exportFile = new File(LifeController.DB_EXPORT_FILE_NAME);
                if (db.exists()){
                    try {
                        if ( ContextCompat.checkSelfPermission(getCurrentActivity(),
                                Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED) {
                                ActivityCompat.requestPermissions(getCurrentActivity(),
                                        new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                                        WRITE_EXTERNAL_STORAGE_PERMISSION_CODE);
                        } else {
                            if (!exportFile.exists()) {
                                new File(LifeController.FILE_EXPORT_PATH).mkdir();
                                exportFile.createNewFile();
                            }
                            FileUtils.copyFile(new FileInputStream(db), new FileOutputStream(exportFile));
                            Toast.makeText(getContext(), getString(R.string.db_exported_to_filesystem), Toast.LENGTH_LONG)
                                    .show();
                        }
                    } catch (IOException e){
                        String message = Build.VERSION.SDK_INT >= Build.VERSION_CODES.M ?
                                getString(R.string.db_export_error_android6) :
                                getString(R.string.db_export_error);
                        Toast.makeText(getContext(), message, Toast.LENGTH_LONG)
                                .show();
                    } finally {
                        getController().openDBConnection();
                    }
                }
            }
        });

        importFromFileSystemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getCurrentActivity().showFileChooserDialog();
            }
        });

        setHasOptionsMenu(true);
        getCurrentActivity().setActionBarTitle(getResources().getString(R.string.db_export_import_title));
        getCurrentActivity().showActionBarHomeButtonAsBack(true);
        return v;
    }

    @Override
    public void onResume() {
        getController().sendScreenNameToAnalytics("Export/Import DB Fragment");
        SharedPreferences prefs = getCurrentActivity().getSharedPreferences(LifeController.SHARED_PREFS_TAG, Context.MODE_PRIVATE);
        autoExportToDropboxSwitch.setChecked(prefs.getBoolean(LifeController.DROPBOX_AUTO_BACKUP_ENABLED, false));
        getController().updateMiscToDB();
        super.onResume();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        ButterKnife.unbind(this);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case WRITE_EXTERNAL_STORAGE_PERMISSION_CODE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    exportToFileSystemView.callOnClick();
                }
                break;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK){
            if (requestCode == LifeController.SELECT_FILE_IN_FILESYSTEM_REQUEST){
                Uri uri = data.getData();
                String path = FileUtils.getPathFromUri(getCurrentActivity(), uri);

                getController().closeDBConnection();
                File newDB = new File(path);
                File oldDB = getCurrentActivity().getDatabasePath(DataBaseHelper.DATABASE_NAME);
                if (newDB.exists()){
                    try {
                        FileUtils.copyFile(new FileInputStream(newDB), new FileOutputStream(oldDB));
                        Toast.makeText(getContext(), getString(R.string.db_imported), Toast.LENGTH_LONG)
                                .show();
                        getController().openDBConnection();
                        getCurrentActivity().onDBFileUpdated(false);
                    } catch (IOException e){
                        Toast.makeText(getContext(), getString(R.string.db_import_error), Toast.LENGTH_LONG)
                                .show();
                    } finally {
                        getController().openDBConnection();
                    }
                }
            }
        }
    }
}
