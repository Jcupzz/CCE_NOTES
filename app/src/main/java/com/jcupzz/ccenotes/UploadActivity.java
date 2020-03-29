package com.jcupzz.ccenotes;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;

public class UploadActivity extends AppCompatActivity {

EditText editPDFName;
Button btn_upload;
StorageReference storageReference;
DatabaseReference databaseReference;
FirebaseFirestore db;
public static String Subject_Module_Name;
 public static String Subject_Module_Link;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload);

editPDFName = findViewById(R.id.txt_pdfName);
btn_upload = findViewById(R.id.btn_upload);


storageReference = FirebaseStorage.getInstance().getReference();
//databaseReference = FirebaseDatabase.getInstance().getReference("uploads");
db = FirebaseFirestore.getInstance();

btn_upload.setOnClickListener(new View.OnClickListener() {
    @Override
    public void onClick(View v) {
        selectPDFFile();
    }
});



    }

    private void selectPDFFile() {
        Intent intent = new Intent();
        intent.setType("application/pdf");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent,"Select PDF File"),1);



    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode==1&&resultCode==RESULT_OK&&data!=null&&data.getData()!=null) {
            uploadPDFFile(data.getData());
        }



    }

    private void uploadPDFFile(final Uri data) {

        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Uploading!!!");
        progressDialog.show();

final StorageReference reference = storageReference.child("uploads/"+System.currentTimeMillis()+".pdf");
reference.putFile(data)
        .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {


                Task<Uri> uri = taskSnapshot.getStorage().getDownloadUrl();
                while (!uri.isComplete());
                    Uri url = uri.getResult();
                Subject_Module_Name = editPDFName.getText().toString();
                Subject_Module_Link = url.toString();
                DownModel downModel = new DownModel(Subject_Module_Name,Subject_Module_Link);





                db.collection("Mechanical")
                        .add(downModel)
                        .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                            @Override
                            public void onSuccess(DocumentReference documentReference) {
                                // Log.d(TAG, "DocumentSnapshot written with ID: " + documentReference.getId());

                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                // Log.w(TAG, "Error adding document", e);
                            }
                        });






                Toast.makeText(UploadActivity.this,Subject_Module_Link,Toast.LENGTH_SHORT).show();
               progressDialog.dismiss();
            }
        }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
    @Override
    public void onProgress(@NonNull UploadTask.TaskSnapshot taskSnapshot) {
double progress = (100.0*taskSnapshot.getBytesTransferred())/taskSnapshot.getTotalByteCount();
progressDialog.setMessage("Uploaded"+(int)progress+"%");
    }
});


    }
}