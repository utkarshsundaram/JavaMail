package com.example.user.sendingemail;
/**
 *
 * This activity takes the email no and other necessary information to
 * send along with the attachment
 * @author utkarsh sundaram
 *
*/

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.user.sendingemail.Asyntask.MyAsynTask;

public class MainActivity extends AppCompatActivity implements View.OnClickListener
{
    private EditText mEditTextEmail;
    private EditText mEditTextName;
    private EditText mEditTextPassword;
    private Button buttonRegister,buttonAttachment;
    private MyAsynTask asynTask;
    private static final int PICK_FROM_GALLERY = 101;
    public static final int MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE = 123;
    private static final int PICKFILE_RESULT_CODE = 1;
    private  String FilePath;
    Uri URI = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mEditTextEmail = (EditText) findViewById(R.id.txtemail_reg);
        mEditTextPassword = (EditText) findViewById(R.id.txtpass_reg);
        mEditTextName = (EditText) findViewById(R.id.txtname_reg);
        buttonRegister = (Button) findViewById(R.id.btn_reg);
        buttonAttachment = (Button) findViewById(R.id.btn_attachment);

        buttonRegister.setOnClickListener(this);
        buttonAttachment.setOnClickListener(this);
    }


/**
 *
 *This function is used to send the intent for reading
 * and selecting the files from the storage
 *
 */


public void readTheFile()
    {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("file/*");
        startActivityForResult(intent,PICKFILE_RESULT_CODE);

//Toast.makeText(MainActivity.this,"For code commiting",Toast.LENGTH_LONG).show();
       /* Intent intent = new Intent(Intent.ACTION_PICK,android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.setType("image*//*");
        startActivityForResult(Intent.createChooser(intent, "Complete action using"),
                PICK_FROM_GALLERY);
                */
    }

    /**
     *
     *This function is used to get the result from
     * the intent which was send
     *
     */

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        try {

           /* if ((requestCode == PICK_FROM_GALLERY) && (resultCode == RESULT_OK)) {
                Uri selectedImage = data.getData();
                String[] filePathColumn = {MediaStore.Images.Media.DATA};
                Cursor cursor = getContentResolver().query(selectedImage, filePathColumn, null, null, null);
                cursor.moveToFirst();
                columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                attachmentFile = cursor.getString(columnIndex);
                Log.e("Attachment Path:", attachmentFile);
                URI = Uri.parse("file://" + attachmentFile);
                Log.d(TAG,""+URI);
                cursor.close();*/
               switch(requestCode) {
                case PICKFILE_RESULT_CODE:
                    if (resultCode == RESULT_OK) {
                        FilePath = data.getData().getPath();

                    }

            }
        }catch (Exception e)
        {
            String err = (e.getMessage()==null)?"SD Card failed":e.getMessage();
            Log.e("sdcard-err2:",err);
        }
    }

/**
 *
 *This function is used to call the asynctask
 * for the network call to send the message
 *
 */

public void  sendTheMail()
{
    String email=mEditTextEmail.getText().toString();
    String Password=mEditTextPassword.getText().toString();
    String name=mEditTextName.getText().toString();
    asynTask=new MyAsynTask(MainActivity.this,email,Password,name,FilePath);
    asynTask.execute();

}

    /**
     *
     *This function is used to define the event
     * on the click of the button
     *
     *
     */
    @Override
    public void onClick(View v)
    {
       if(v==buttonAttachment)
       {
           checkPermissionREAD_EXTERNAL_STORAGE(MainActivity.this);
       }
       if (v== buttonRegister)
       {
           sendTheMail();
       }

    }
    /**
     *
     *This function is used to check the permission
     * to  read and write in external storage
     *
     */
    public boolean checkPermissionREAD_EXTERNAL_STORAGE(
            final Context context) {
        int currentAPIVersion = Build.VERSION.SDK_INT;
        if (currentAPIVersion >= android.os.Build.VERSION_CODES.M)
        {
            if (ContextCompat.checkSelfPermission(context,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)
            {
                if (ActivityCompat.shouldShowRequestPermissionRationale(
                        (Activity) context,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE))
                {
                    showDialog("External storage", context,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE);
                } else {
                    ActivityCompat
                            .requestPermissions(
                                    (Activity) context,
                                    new String[] { Manifest.permission.WRITE_EXTERNAL_STORAGE },
                                    MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE);

                }
                return false;
            } else {
                showDialog("External storage", context,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE);
                return true;
            }

        } else {
            showDialog("Permission needed for external storage", context,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE);
            return true;
        }
    }

    /**
     *
     *This function is used to show a dialog window
     * for giving permission
     *
     */


    public void showDialog(final String msg, final Context context,
                           final String permission) {
        AlertDialog.Builder alertBuilder = new AlertDialog.Builder(context);
        alertBuilder.setCancelable(true);
        alertBuilder.setTitle("Permission necessary");
        alertBuilder.setMessage(msg + " permission is necessary");
        alertBuilder.setPositiveButton("allow",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        ActivityCompat.requestPermissions((Activity) context,
                                new String[] { permission },
                                MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE);

                    }
                });
        alertBuilder.setNegativeButton("deny",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                       dialog.dismiss();
                    }
                });
        AlertDialog alert = alertBuilder.create();
        alert.show();
    }

    /**
     *
     *This function is used check whether the
     * permission has been granted or not
     *
     *
     */

    public void onRequestPermissionsResult(int requestCode,
                                           String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED)
                {
                    readTheFile();

                } else {
                    Toast.makeText(MainActivity.this, "GET_ACCOUNTS Denied",
                            Toast.LENGTH_SHORT).show();
                }
                break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions,
                        grantResults);
        }
    }
}
