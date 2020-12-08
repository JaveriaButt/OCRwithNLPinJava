package com.longlee.ocrwithnlp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.ml.vision.FirebaseVision;
import com.google.firebase.ml.vision.common.FirebaseVisionImage;
import com.google.firebase.ml.vision.text.FirebaseVisionText;
import com.google.firebase.ml.vision.text.FirebaseVisionTextRecognizer;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class MainActivity extends AppCompatActivity {

    ImageView imgview;
    TextView res;

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //finding image view
        res=findViewById(R.id.txtres);
        res.setMovementMethod(new ScrollingMovementMethod());
        imgview=findViewById(R.id.imageview1);
        if(checkSelfPermission(Manifest.permission.CAMERA)!= PackageManager.PERMISSION_GRANTED)
        {
            requestPermissions(new String[]{Manifest.permission.CAMERA},62);
        }
        if(checkSelfPermission(Manifest.permission.MANAGE_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED)
        {
            requestPermissions(new String[]{Manifest.permission.MANAGE_EXTERNAL_STORAGE},6208);
        }
    }
    int check=0;
    public void CameraClick(View view) {
        Intent intent=new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent,62);
        check=1;
    }
    Bitmap bitmap;
    Uri imageUri;
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        FirebaseVisionImage firebaseVisionImage = null;
       if(check==1)
       {
           Bundle bundle=data.getExtras();
           bitmap=(Bitmap) bundle.get("data");
           //setting image in immage view 1
           imgview.setImageBitmap(bitmap);

           //1. create a FirebaseVisionImage object from a Bitmap object

           firebaseVisionImage = FirebaseVisionImage.fromBitmap(bitmap);
       }
       else if(check==2)
       {

           if (resultCode == RESULT_OK && requestCode == 6208){
               imageUri = data.getData();
               imgview.setImageURI(imageUri);
               try {
                   firebaseVisionImage=FirebaseVisionImage.fromFilePath(this,imageUri);
               } catch (IOException e) {
                   e.printStackTrace();
               }
           }
       }
       else
       {
           firebaseVisionImage = FirebaseVisionImage.fromBitmap(bitmap);
       }
       res.setText("Processing.....\nIt might take Upto One Minute");
        //2. Get an instance of FirebaseVision
        FirebaseVision firebaseVision = FirebaseVision.getInstance();
        //3. Create an instance of FirebaseVisionTextRecognizer
        FirebaseVisionTextRecognizer firebaseVisionTextRecognizer = firebaseVision.getOnDeviceTextRecognizer();
        //4. Create a task to process the image
        Task<FirebaseVisionText> task = firebaseVisionTextRecognizer.processImage(firebaseVisionImage);
        //5. if task is success
        task.addOnSuccessListener(new OnSuccessListener<FirebaseVisionText>() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onSuccess(FirebaseVisionText firebaseVisionText) {
                String s = firebaseVisionText.getText();
                res.setText(s);
                //getting nouns definitions
                NGramGenerator generator = new NGramGenerator(getApplicationContext());
                String result= generator.GenerateNGram(res.getText().toString());
                //copy to clipboardd
                ClipboardManager myClipboard;
                myClipboard = (ClipboardManager)getSystemService(CLIPBOARD_SERVICE);
                ClipData myClip;
                myClip = ClipData.newPlainText("text",result );
                myClipboard.setPrimaryClip(myClip);
                Toast.makeText(getApplicationContext(), "Text Extracted and Copied", Toast.LENGTH_LONG).show();
            }
        });
        task.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

                Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    public void GalleryClick(View view) {
        Intent gallery = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI);
        startActivityForResult(gallery, 6208);
        check=2;
    }
    public void NextClick(View view) {
        try {
            Intent intent=new Intent(getApplicationContext(),Result.class);
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
            byte[] byteArray = stream.toByteArray();
            intent.putExtra("image",byteArray);
            startActivity(intent);
        }
        catch (Exception ex)
        {

        }
    }
}