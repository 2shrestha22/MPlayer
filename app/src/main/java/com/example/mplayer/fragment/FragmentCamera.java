package com.example.mplayer.fragment;


import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.example.mplayer.R;
import com.example.mplayer.model.Song;
import com.example.mplayer.model.SongList;
import com.example.mplayer.tflite.Classifier;
import com.example.mplayer.tflite.TensorFlowImageClassifier;
import com.example.mplayer.utils.FaceCropper;
import com.example.mplayer.utils.SquareImageView;

import java.io.File;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import static android.app.Activity.RESULT_OK;

/**
 * A simple {@link Fragment} subclass.
 */
public class FragmentCamera extends Fragment {

    FaceCropper mFaceCropper = new FaceCropper();

    private Executor executor = Executors.newSingleThreadExecutor();
    private static final String MODEL_PATH = "AWFERmodel.tflite";

    private static final boolean QUANT = false;
    private static final String LABEL_PATH = "labels.txt";
    private static final int INPUT_SIZE = 48;

    /*Why CAMERA_REQUEST?
    * You can make several calls in a single Activity to startActivityForResult() which allows different Intents to do different actions.
    * Use a request code to identify which is the Intent you are returning from.
    * https://stackoverflow.com/questions/38507965/what-does-camera-request-code-mean-in-android
    * */
    private static final int CAMERA_REQUEST = 1888;
    private static final int MY_CAMERA_PERMISSION_CODE = 100;
    private static final int REQUEST_IMAGE_CAPTURE = 1;
    private static final int REQUEST_TAKE_PHOTO = 1;
    private static final int PIXEL_WIDTH = 48;

    Classifier classifier;
    //ImageUtils imageConverter = new ImageUtils();

    private SquareImageView faceImageView;
    private TextView emotionTextView;
    private Button detectButton, photoButton, clearButton, setListButton;

    String eLabel = "All";

    private String TAG = "FragmentCamera";

    public FragmentCamera() {
        // Required empty public  constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_camera, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        //initialize buttons and imageview
        photoButton = view.findViewById(R.id.phototaker);
        emotionTextView = view.findViewById(R.id.emotionTxtView);
        detectButton = view.findViewById(R.id.detect);
        faceImageView = view.findViewById(R.id.facialImageView);
        clearButton = view.findViewById(R.id.reset);
        setListButton = view.findViewById(R.id.setList);


        //OnClick listeners
        photoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //start camera here
//                dispatchTakePictureIntent();
                clearStatus();
                myPictureDispatcher();
            }
        });

        detectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //start image detection
                detectEmotion();
            }
        });

        clearButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //clear and reset image
                clearStatus();
            }
        });

        setListButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //set playlist based on music
                Log.d(TAG, "onClick: FragmentCamera, setListButton.setOnClickListner");
                setList();
            }
        });

        //disable detectButton before taking photo
        detectButton.setEnabled(false);
        emotionTextView = view.findViewById(R.id.emotionTxtView);
        //load TensorFlow model
        loadModel();

        mFaceCropper.setFaceMinSize(20);
        //mFaceCropper.setDebug(true);
        mFaceCropper.setMaxFaces(1);
//        mFaceCropper.setEyeDistanceFactorMargin((float) 2 / 10);
        mFaceCropper.setFaceMarginPx(100);
    }

    private void setList() {
        ArrayList<Song> songArrayList;
        songArrayList = new SongList().getAllList(getContext(), eLabel);

        //send broadcast
        final LocalBroadcastManager localBroadcastManager = LocalBroadcastManager.getInstance(getActivity().getApplicationContext());
        Intent intent = new Intent("com.example.mplayer.intent.CONTROL_SIGNAL");
        intent.putExtra("signal", "PLAY_THIS");
        intent.putParcelableArrayListExtra("songList", songArrayList);
        intent.putExtra("listPos", 0);
        localBroadcastManager.sendBroadcast(intent);
        Log.d(TAG, eLabel+"-playlist and 0 position sent");
    }

    private void loadModel() {

        executor.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    classifier = TensorFlowImageClassifier.create(
                            getActivity().getAssets(),
                            MODEL_PATH,
                            LABEL_PATH,
                            INPUT_SIZE,
                            QUANT);
                    //makeButtonVisible();
                } catch (final Exception e) {
                    throw new RuntimeException("Error initializing TensorFlow!", e);
                }
            }
        });

    }

    private void clearStatus() {
        detectButton.setEnabled(false);
        faceImageView.setImageResource(R.drawable.maximize);
        emotionTextView.setText("Status: none");
    }

    private void detectEmotion() {
        //get RGB bitmap from faceImageView
        Bitmap image = ((BitmapDrawable)faceImageView.getDrawable()).getBitmap();

        if (image.getConfig() != Bitmap.Config.ARGB_8888) {
                Log.d(TAG, "onActivityResult: Bitmap converting");
                image = image.copy(Bitmap.Config.ARGB_8888,true);
            }
        //crop image to center
        //Bitmap croppedImage = ImageUtils.cropCenter(image);

        //scale croppedImage to 48x48
        //Bitmap scaledImage = Bitmap.createScaledBitmap(croppedImage, INPUT_SIZE, INPUT_SIZE, false);

        final List<Classifier.Recognition> results = classifier.recognizeImage(image);

        faceImageView.setImageBitmap(image);
        emotionTextView.setText(results.toString());
        eLabel = results.get(0).getTitle();

    }


    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getActivity().getPackageManager()) != null)
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
    }


    //getting full size bitmap image instead of thumbnail only
    //https://stackoverflow.com/questions/6448856/android-camera-intent-how-to-get-full-sized-photo

    private Uri mImageUri;
    private File photo;

    private void myPictureDispatcher(){

        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        try
        {
            // place where to store camera taken picture
            photo = createTemporaryFile("picture", ".jpg");
            photo.delete();
        }
        catch(Exception e)
        {
            Log.d(TAG, "Can't create file to take picture!");
            Toast.makeText(getContext(), "Please check SD card! Image shot is impossible!", Toast.LENGTH_SHORT);
        }
        if (photo == null)
            Log.d(TAG, "myPictureDispatcher: photo is null @third");
        mImageUri = Uri.fromFile(photo);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, mImageUri);
        //start camera intent
        startActivityForResult(intent, REQUEST_IMAGE_CAPTURE);

    }

    private File createTemporaryFile(String part, String ext) throws Exception
    {
        File tempDir=new File(Environment.getExternalStorageDirectory().getAbsolutePath()+"/.MPlayer/");
        if(!tempDir.exists())
        {
            tempDir.mkdirs();
        }
        return File.createTempFile(part, ext, tempDir);
    }

    private void deleteTemporaryFile() {
        File tempDir=new File(Environment.getExternalStorageDirectory().getAbsolutePath()+"/.MPlayer/");
        if(!tempDir.exists())
        {
            tempDir.delete();
        }
    }
    public void grabImage(ImageView imageView)
    {
        getContext().getContentResolver().notifyChange(mImageUri, null);
        ContentResolver cr = getContext().getContentResolver();
        Bitmap bitmap;
        try
        {
            bitmap = android.provider.MediaStore.Images.Media.getBitmap(cr, mImageUri);
            Bitmap imageBitmap1 = mFaceCropper.getCroppedImage(bitmap);
            if (bitmap!=imageBitmap1)
                bitmap.recycle();
            imageView.setImageBitmap(imageBitmap1);
        }
        catch (Exception e)
        {
            Toast.makeText(getContext(), "Failed to load", Toast.LENGTH_SHORT).show();
            Log.d(TAG, "Failed to load", e);
        }
    }

    //called after camera intent finished
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent intent)
    {
        //MenuShootImage is user defined menu option to shoot image
        if(requestCode==REQUEST_IMAGE_CAPTURE && resultCode==RESULT_OK)
        {
            //ImageView imageView;
            //... some code to inflate/create/find appropriate ImageView to place grabbed image
            grabImage(faceImageView);
            detectButton.setEnabled(true);

            detectEmotion();
        }
        super.onActivityResult(requestCode, resultCode, intent);
    }

//
//    @Override
//    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
//            detectButton.setEnabled(true);
//            Bundle extras = data.getExtras();
//            Bitmap imageBitmap = (Bitmap) extras.get("data");
//            //faceImageView.setImageBitmap(imageBitmap);
//
//            Bitmap imageBitmap1 = mFaceCropper.getCroppedImage(imageBitmap);
//            if (imageBitmap1.getConfig() != Bitmap.Config.ARGB_8888) {
//                Log.d(TAG, "onActivityResult: Bitmap converting");
//                imageBitmap1 = imageBitmap1.copy(Bitmap.Config.ARGB_8888,true);
//            }
//            if (imageBitmap != imageBitmap1)
//                imageBitmap.recycle();
//            faceImageView.setImageBitmap(imageBitmap1);
//            detectEmotion();
//
//        }
//    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        classifier.close();
        //delete image files while captured
        deleteTemporaryFile();
    }

}
