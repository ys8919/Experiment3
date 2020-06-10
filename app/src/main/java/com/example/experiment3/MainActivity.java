package com.example.experiment3;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.icu.util.Calendar;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;

import android.os.Bundle;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.cocosw.bottomsheet.BottomSheet;
import com.google.android.material.textfield.TextInputLayout;
import com.lljjcoder.Interface.OnCityItemClickListener;
import com.lljjcoder.bean.CityBean;
import com.lljjcoder.bean.DistrictBean;
import com.lljjcoder.bean.ProvinceBean;
import com.lljjcoder.citywheel.CityConfig;
import com.lljjcoder.style.citylist.Toast.ToastUtils;
import com.lljjcoder.style.citypickerview.CityPickerView;



import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;


@RequiresApi(api = Build.VERSION_CODES.N)
public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "MainActivity";
    //权限
    private static final int PERMISSION_REQUEST = 1001;
    List<String> permissionsList = new ArrayList<>();

    private ImageView img_face;
    private Context context;
    private Uri imageUri;
    public static final int TAKE_PHOTO = 1;
    public static final int CHOOSE_PHOTO = 2;
    public static final int READ_PHOTO = 3;

    private String orientation = null;
    //所选相册图片的路径(原图/压缩后/剪裁后)
    String albumPath = "";
    //用来转换相机路径用的


    //输入限制
    TextView textView;
    TextInputLayout layoutTextName;
    TextInputLayout layoutTextID;
    TextInputLayout layoutTextEmail;
    TextInputLayout layoutTextAddress;
    TextInputLayout layoutTextDetailAddress;
    TextInputLayout layoutTextDate;
    Button button;
    private EditText TextName;
    private EditText TextID;
    private EditText TextEmail;
    private EditText TextAddress;
    private EditText  TextDetailAddress;
    private EditText  TextDate;
    private Spinner spinner1;
//地址选择
    CityPickerView mPicker=new CityPickerView();
    //日期选择
    DateFormat format= DateFormat.getDateTimeInstance();
    Calendar calendar= Calendar.getInstance(Locale.CHINA);
    //民族选择
    //将可选内容与ArrayAdapter连接起来

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //禁止横屏
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        //权限


        textView=findViewById(R.id.textView);
        layoutTextName=findViewById(R.id.TextName);
        layoutTextID=findViewById(R.id.TextID);
        layoutTextEmail=findViewById(R.id.TextEmail);
        layoutTextAddress=findViewById(R.id.TextAddress);
        layoutTextDetailAddress=findViewById(R.id.TextDetailAddress);
        layoutTextDate=findViewById(R.id.TextDate);

        context=this;
        img_face =findViewById(R.id.img_face);
        img_face.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showSimpleBottomSheetList();
        }
        });

        //地址选择
        mPicker.init(this);
        TextAddress = layoutTextAddress.getEditText();
        TextAddress.setFocusableInTouchMode(false);//不可编辑
        TextAddress.setKeyListener(null);//不可粘贴，长按不会弹出粘贴框
        TextAddress.setFocusable(false);//不可编辑

        TextAddress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectAddress();
            }
        });

        //日期选择
        TextDate = layoutTextDate.getEditText();
        TextDate.setFocusableInTouchMode(false);//不可编辑
        TextDate.setKeyListener(null);//不可粘贴，长按不会弹出粘贴框
        TextDate.setFocusable(false);//不可编辑

        TextDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePickerDialog(MainActivity.this,  7, TextDate, calendar);
        }
        });
        //输入限制
        initView();

        //民族选择
        //设置下拉列表的风格
        spinner1 = (Spinner) findViewById(R.id.spinner1);
        ArrayAdapter<?> adapter = ArrayAdapter.createFromResource(MainActivity.this, R.array.plantes_04, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        //将adapter2 添加到spinner中
        spinner1.setAdapter(adapter);
        //设置默认值
        spinner1.setVisibility(View.VISIBLE);

    //提交按钮
        button=findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                Toast toast = new Toast(MainActivity.this);
                Toast toast1 = toast.makeText(MainActivity.this, "信息提交成功", Toast.LENGTH_LONG);
                toast1.show();
            }
        });
    }

    /**
     * 日期选择
     * @param activity
     * @param themeResId
     * @param TextDate
     * @param calendar
     */
    @RequiresApi(api = Build.VERSION_CODES.N)
    public static void showDatePickerDialog(Activity activity, int themeResId, final EditText TextDate, Calendar calendar) {
        // 直接创建一个DatePickerDialog对话框实例，并将它显示出来
        new DatePickerDialog(activity, themeResId, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                TextDate.setText( year + "年" + (month + 1) + "月" + dayOfMonth + "日");
            }

            // 绑定监听器(How the parent is notified that the date is set.)
        }
                // 设置初始日期
                , calendar.get(Calendar.YEAR)
                , calendar.get(Calendar.MONTH)
                , calendar.get(Calendar.DAY_OF_MONTH)).show();
    }

    //地址选择
    private void selectAddress(){
        //添加默认的配置，不需要自己定义
        CityConfig cityConfig = new CityConfig.Builder().build();
        mPicker.setConfig(cityConfig);

        //监听选择点击事件及返回结果
        mPicker.setOnCityItemClickListener(new OnCityItemClickListener() {
            @Override
            public void onSelected(ProvinceBean province, CityBean city, DistrictBean district) {
                //将选择的地址填入tv_address_set中
                TextAddress.setText(province.toString().trim() +"  "+ city.toString().trim()  +"  "+ district.toString().trim());
                //layoutTextAddress.setHint(province.toString().trim() + city.toString().trim() + district.toString().trim());
            }

            @Override
            public void onCancel() {
                ToastUtils.showLongToast(MainActivity.this, "已取消");
            }
        });

        //显示
        mPicker.showCityPicker( );
    }

    private void initView() {
        //设置提示文字

        //通过getEditText()方法来获取EditText控件
        TextName = layoutTextName.getEditText();
        TextID = layoutTextID.getEditText();
        TextEmail = layoutTextEmail.getEditText();
        TextDetailAddress=layoutTextDetailAddress.getEditText();
        TextName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!RegexUtils.isZh(s)) {
                    //显示错误提示
                    layoutTextName.setError("姓名只能为汉字");
                    layoutTextName.setErrorEnabled(true);
                } else {
                    layoutTextName.setErrorEnabled(false);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        TextID.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    if (!RegexUtils.isIDCard18(s)) {
                        //显示错误提示
                        layoutTextID.setError("身份证格式不正确");
                        layoutTextID.setErrorEnabled(true);
                    } else {
                        layoutTextID.setErrorEnabled(false);
                    }
                }

                @Override
                public void afterTextChanged(Editable s) {

            }
        });

        TextEmail.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!RegexUtils.isEmail(s)) {
                    //显示错误提示
                    layoutTextEmail.setError("邮箱不正确");
                    layoutTextEmail.setErrorEnabled(true);
                } else {
                    layoutTextEmail.setErrorEnabled(false);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        InputMethodManager inputMethodManager = (InputMethodManager) this.getSystemService(Context.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(TextID.getWindowToken(), 0);
        inputMethodManager.hideSoftInputFromWindow(TextEmail.getWindowToken(), 0);
        inputMethodManager.hideSoftInputFromWindow(TextName.getWindowToken(), 0);
        inputMethodManager.hideSoftInputFromWindow(TextAddress.getWindowToken(), 0);
        inputMethodManager.hideSoftInputFromWindow(TextDetailAddress.getWindowToken(), 0);

        return super.onTouchEvent(event);
    }




    private void showSimpleBottomSheetList() {
        //1、使用Dialog、设置style
        final Dialog dialog = new Dialog(MainActivity.this,R.style.DialogTheme);
        //2、设置布局
        View view = View.inflate(MainActivity.this,R.layout.dialog_custom_layout,null);
        dialog.setContentView(view);

        Window window = dialog.getWindow();
        //设置弹出位置
        window.setGravity(Gravity.BOTTOM);
        //设置弹出动画
        window.setWindowAnimations(R.style.main_menu_animStyle);
        //设置对话框大小
        window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.show();

        dialog.findViewById(R.id.tv_take_photo).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                File file = new File(getExternalCacheDir(), "output_image.jpg");
                try {
                    if (file.exists()) {
                        file.delete();
                    }
                    file.createNewFile();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                if (Build.VERSION.SDK_INT >= 24) {
                   // imageUri = FileProvider.getUriForFile(MainActivity.this, "com.example.experiment3.provider", file);
                    //Log.d(TAG, "onClick: imageUri:"+imageUri);
                } else {
                    imageUri = Uri.fromFile(file);

                }

                if (ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.CAMERA}, TAKE_PHOTO);
                } else {
                    callCamera();
                }
                dialog.dismiss();
            }
        });

        dialog.findViewById(R.id.tv_take_pic).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (ContextCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, CHOOSE_PHOTO);
                } else if(ContextCompat.checkSelfPermission(context, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
                    ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, READ_PHOTO);
                } else{

                    goPhotoAlbum();
                }
                dialog.dismiss();

            }
        });

        dialog.findViewById(R.id.tv_cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
    }



    //获取权限



    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case TAKE_PHOTO:
                if (resultCode == RESULT_OK) {
                    //Bitmap bitmap = null;
                    //bitmap = (Bitmap) BitmapFactory.decodeStream(getContentResolver().openInputStream(imageUri));
                    Bitmap bitmap = (Bitmap) Objects.requireNonNull(data.getExtras()).get("data");
                    int angle = 0;
                    Uri uri = data.getData();
                    //String imagePath = getImagePath(uri, null);
                    Log.d(TAG, "onActivityResult: uri"+uri);
                    /*if (angle != 0) {
                        Log.d(TAG, "onActivityResult: angle:"+angle);
                        // 下面的方法主要作用是把图片转一个角度，也可以放大缩小等
                        Matrix m = new Matrix();
                        int width = bitmap.getWidth();
                        int height = bitmap.getHeight();
                        m.setRotate(angle); // 旋转angle度
                        bitmap = Bitmap.createBitmap(bitmap, 0, 0, width, height, m, true);// 从新生成图片

                    }

                     */
                    bitmap= getCircleBitmap(bitmap);
                    img_face.setImageBitmap(bitmap);
                    textView.setText("");
                }
                break;
            case CHOOSE_PHOTO:
                if (resultCode == RESULT_OK) {
                    handleImageBeforeKitKat(data);
                    //判断手机系统版本号
                  /*  if (Build.VERSION.SDK_INT >= 19) {
                        //4.4及以上系统使用
                        handleImageOnKitKat(data);
                    } else {

                    }

                   */
                }
                break;
            default:
                break;
        }
    }

    //激活相册操作
    private void goPhotoAlbum() {
        Intent intent = new Intent("android.intent.action.GET_CONTENT");
        intent.setType("image/*");
        startActivityForResult(intent, CHOOSE_PHOTO);
    }

    //打开相机
    private void callCamera() {
        //启动相机权限
        Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
        intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
        startActivityForResult(intent, TAKE_PHOTO);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            //打开相册
            case CHOOSE_PHOTO:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    goPhotoAlbum();
                } else {

                    Toast.makeText(context, "您没有打开相册的权限", Toast.LENGTH_SHORT).show();

                }
                break;
            //相机的权限
            case TAKE_PHOTO:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    callCamera();
                } else {

                    Toast.makeText(context, "您没有打开相机的权限", Toast.LENGTH_SHORT).show();
                }
                break;
            case READ_PHOTO:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    //goPhotoAlbum();
                } else {
                    Toast.makeText(context, "您没有读取照片的权限", Toast.LENGTH_SHORT).show();

                }
                break;
            default:
                break;
        }
    }

    private void handleImageBeforeKitKat(Intent data) {
        Uri uri = data.getData();
        // String imagePath = getImagePath(uri, null);
        dispalyImage(uri);
    }
    /*
     @RequiresApi(api = Build.VERSION_CODES.N)
     private void handleImageOnKitKat(Intent data) {
         String imagePath = null;
         Uri uri = data.getData();
         Log.d(TAG, "handleImageOnKitKat: uri:"+uri);
         if (DocumentsContract.isDocumentUri(this, uri)) {
             String docId = DocumentsContract.getDocumentId(uri);
             if ("com.android.providers.media.documents".equals(uri.getAuthority())) {
                 String id = docId.split(":")[1];
                 String selection = MediaStore.Images.Media._ID + "=" + id;
                 imagePath = getImagePath(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, selection);
             } else if ("com.android.providers.downloads.documents".equals(uri.getAuthority())) {
                 Uri contentUri = ContentUris.withAppendedId(Uri.parse("content://downloads/public_downloads"), Long.valueOf(docId));
                 imagePath = getImagePath(contentUri, null);
             }
         } else if ("content".equalsIgnoreCase(uri.getScheme())) {
             //如果是content类型的Uri，则使用普通方式处理
             imagePath = getImagePath(uri, null);
         } else if ("file".equalsIgnoreCase(uri.getScheme())) {
             imagePath = uri.getPath();
         }

         dispalyImage(uri);
     }



     private String getImagePath(Uri uri, String selection) {
         String path = null;
         //通过uri和selection来获取真实的图片路径
         Cursor cursor = getContentResolver().query(uri, null, selection, null, null);
         if (cursor != null) {
             if (cursor.moveToFirst()) {
                 path = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
             }
             orientation = cursor.getString(cursor.getColumnIndex("orientation"));// 获取旋转的角度
             cursor.close();
         }
         return path;
     }


    */
    private void dispalyImage(Uri imagePath) {
        if (imagePath != null) {
            //根据path读取资源路径
            Bitmap bitmap = null;
            try {
                bitmap = BitmapFactory.decodeStream(getContentResolver().openInputStream(imagePath));
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }

            /*int angle = 0;
            if (orientation != null && !"".equals(orientation)) {
                angle = Integer.parseInt(orientation);
            }
            if (angle != 0) {
                // 下面的方法主要作用是把图片转一个角度，也可以放大缩小等
                Matrix m = new Matrix();
                int width = bitmap.getWidth();
                int height = bitmap.getHeight();
                m.setRotate(angle); // 旋转angle度
                bitmap = Bitmap.createBitmap(bitmap, 0, 0, width, height, m, true);// 从新生成图片
            }

             */
            bitmap= getCircleBitmap(bitmap);
            img_face.setImageBitmap(bitmap);
            textView.setText("");
        } else {
            Toast.makeText(context, "获取照片失败", Toast.LENGTH_SHORT).show();
        }
    }

//裁剪图片
    public static Bitmap cropBitmap(Bitmap bitmap) {//从中间截取一个正方形
        int w = bitmap.getWidth(); // 得到图片的宽，高
        int h = bitmap.getHeight();
        int cropWidth = Math.min(w, h);// 裁切后所取的正方形区域边长

        return Bitmap.createBitmap(bitmap, (bitmap.getWidth() - cropWidth) / 2,
                (bitmap.getHeight() - cropWidth) / 2, cropWidth, cropWidth);
    }

    public static Bitmap getCircleBitmap(Bitmap bitmap) {//把图片裁剪成圆形
        if (bitmap == null) {
            return null;
        }
        bitmap = cropBitmap(bitmap);//裁剪成正方形
        try {
            Bitmap circleBitmap = Bitmap.createBitmap(bitmap.getWidth(),
                    bitmap.getHeight(), Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(circleBitmap);
            final Paint paint = new Paint();
            final Rect rect = new Rect(0, 0, bitmap.getWidth(),
                    bitmap.getHeight());
            final
            RectF rectF = new RectF(new Rect(0, 0, bitmap.getWidth(),
                    bitmap.getHeight()));
            float roundPx = 0.0f;
            roundPx = bitmap.getWidth();
            paint.setAntiAlias(true);
            canvas.drawARGB(0, 0, 0, 0);
            paint.setColor(Color.WHITE);
            canvas.drawRoundRect(rectF, roundPx, roundPx, paint);
            paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
            final Rect src = new Rect(0, 0, bitmap.getWidth(),
                    bitmap.getHeight());
            canvas.drawBitmap(bitmap, src, rect, paint);
            return circleBitmap;
        } catch (Exception e) {
            return bitmap;
        }
    }

    @Override
    public void onClick(View view) {

    }
}
