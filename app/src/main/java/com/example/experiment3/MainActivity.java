package com.example.experiment3;

import android.content.Context;
import android.content.Intent;
import android.os.Environment;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.material.textfield.TextInputLayout;
import com.lljjcoder.Interface.OnCityItemClickListener;
import com.lljjcoder.bean.CityBean;
import com.lljjcoder.bean.DistrictBean;
import com.lljjcoder.bean.ProvinceBean;
import com.lljjcoder.citywheel.CityConfig;
import com.lljjcoder.style.citylist.Toast.ToastUtils;
import com.lljjcoder.style.citypickerview.CityPickerView;
import com.luck.picture.lib.PictureSelector;
import com.luck.picture.lib.config.PictureConfig;
import com.luck.picture.lib.config.PictureMimeType;
import com.luck.picture.lib.entity.LocalMedia;

import java.io.File;



import static com.luck.picture.lib.config.PictureMimeType.ofImage;


public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private ImageView img_face;

    //所选相册图片的路径(原图/压缩后/剪裁后)
    String albumPath = "";
    //用来转换相机路径用的
    LocalMedia localMedia = new LocalMedia();

    //输入限制
    TextInputLayout layoutTextName;
    TextInputLayout layoutTextID;
    TextInputLayout layoutTextEmail;
    TextInputLayout layoutTextAddress;
    TextInputLayout layoutTextDetailAddress;

    private EditText TextName;
    private EditText TextID;
    private EditText TextEmail;
    private EditText TextAddress;
    private EditText  TextDetailAddress;
//地址选择
    CityPickerView mPicker=new CityPickerView();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        layoutTextName=findViewById(R.id.TextName);
        layoutTextID=findViewById(R.id.TextID);
        layoutTextEmail=findViewById(R.id.TextEmail);
        layoutTextAddress=findViewById(R.id.TextAddress);
        layoutTextDetailAddress=findViewById(R.id.TextDetailAddress);

        img_face = (ImageView) findViewById(R.id.img_face);
        img_face.setOnClickListener(this);

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

        //输入限制
        initView();


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


    /**
     * picSelector的相册相机界面
     *
     * // 例如 LocalMedia 里面返回三种 path
     *                     // 1.media.getPath(); 为原图 path
     *                     // 2.media.getCutPath();为裁剪后 path，需判断 media.isCut();是否为 true
     *                     // 3.media.getCompressPath();为压缩后 path，需判断 media.isCompressed();是否为 true
     *                     // 如果裁剪并压缩了，以取压缩路径为准，因为是先裁剪后压缩的
     *
     */
    protected void showPicSeleDialog() {
        // 进入相册 以下是例子：不需要的api可以不写
        PictureSelector.create(MainActivity.this)
                .openGallery(PictureMimeType.ofImage())// 全部.PictureMimeType.ofAll()、图片.ofImage()、视频.ofVideo()、音频.ofAudio()
                .loadImageEngine(GlideEngine.createGlideEngine())
                .theme(R.style.picture_default_style)// 主题样式设置 具体参考 values/styles   用法：R.style.picture.white.style
                .maxSelectNum(1)// 最大图片选择数量
                .selectionMode( PictureConfig.SINGLE)// 多选 or 单选
                .isCamera(true)// 是否显示拍照按钮
                .isZoomAnim(true)// 图片列表点击 缩放效果 默认true
                .imageFormat(PictureMimeType.PNG)// 拍照保存图片格式后缀,默认jpeg
                .compress(true)// 是否压缩
                .synOrAsy(true)//同步true或异步false 压缩 默认同步
                .compressSavePath(getCompressPath())//压缩图片自定义保存地址
                //.sizeMultiplier(0.5f)// glide 加载图片大小 0~1之间 如设置 .glideOverride()无效
                .glideOverride(160, 160)// glide 加载宽高，越小图片列表越流畅，但会影响列表图片浏览的清晰度
                .forResult(PictureConfig.CHOOSE_REQUEST);//结果回调onActivityResult code
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) { // 如果返回码是可以用的
            switch (requestCode) {
                case PictureConfig.CHOOSE_REQUEST:
                    if(PictureSelector.obtainMultipleResult(data).get(0) != null)
                    {
                        // 图片选择结果回调
                        localMedia = PictureSelector.obtainMultipleResult(data).get(0);
                        if(localMedia.isCompressed())
                        {
                            albumPath = localMedia.getCompressPath();
                            img_face.setBackgroundResource(0);
                            //设置图片圆角角度
                            RoundedCorners roundedCorners = new RoundedCorners(30);
                            //通过RequestOptions扩展功能
                            RequestOptions options = RequestOptions.bitmapTransform(roundedCorners).override(300, 300)
                                    //圆形
                                    .circleCrop();
                            Glide.with(MainActivity.this)
                                    .load(albumPath).apply(options).into(img_face);
                        }
                    }

                    break;
            }
        }
    }


    // 压缩后图片文件存储位置
    private String getCompressPath() {
        String path = Environment.getExternalStorageDirectory().getAbsolutePath() + "/PictureSelector/image/";
        File file = new File(path);
        if (file.mkdirs()) {
            return path;
        }
        return path;
    }




    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.img_face:
                //弹出拍照，照片弹窗
                showPicSeleDialog();
                break;
        }
    }
}
