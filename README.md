### 关于YImagePicker 
[ ![Download](https://api.bintray.com/packages/yangpeixing/yimagepicker/androidx/images/download.svg?version=2.4.1) ](https://bintray.com/yangpeixing/yimagepicker/androidx/2.4.1/link)
 - 支持无缝切换小红书剪裁样式并自定义UI
 - 支持微信、马蜂窝、知乎等多个不同风格样式定制
 - 支持图片直接预览和编辑预览（排序、删除）
 - 支持单图自定义比例剪裁
 - 支持视频、图片、GIF图等不同类型混合加载
 - 支持视频图片混合单选或多选
 - 支持高清预览超长图、超大图，图片放大效果胜过微信
 - 小红书剪裁样式支持视频预览
 - 微信样式支持指定单一类型选择（图片、视频）
 - 微信样式支持多次选择状态保存
 - 微信样式支持指定某些媒体文件不可选择
 - 选择结果直接回调，拒绝配置ActivityForResult+requestCode，即调用即处理
 - 轻量级，aar大小不超过300K，无so库，无任何第三方依赖
 - 支持androidx和support
 - 永久维护


### 引入依赖
**androidx版本：**

```java
implementation 'com.ypx.yimagepicker:androidx:2.4.2'
```
**support版本：**
```java
implementation 'com.ypx.yimagepicker:support:2.4.2'
```

### 核心原理
YImagePicker与主项目通过presenter进行交互与解耦，presenter采用序列化接口的方式实现。回调采用嵌入fragment的方式实现，类似于Glide或RxPermisson.原理上还是使用OnActivityResult,但无需再配置requestCode并且支持跨进程回调。

小红书样式需要实现：ICropPickerBindPresenter
微信样式需要实现：IMultiPickerBindPresenter

[apk体验地址](https://www.pgyer.com/Wfhb)

### 效果图集
 - **demo效果**
 
![demo效果](https://app-screenshot.pgyer.com/image/view/app_screenshots/3957d904273e547143955ca993bbf7ae-528)

 - **小红书样式**

![小红书样式](https://app-screenshot.pgyer.com/image/view/app_screenshots/fc09bb8d2ac27b91820593430469cc17-528)
![小红书样式](https://app-screenshot.pgyer.com/image/view/app_screenshots/87b43cb9ef8f40377bc3910b3ad3737b-528)
![小红书样式](https://app-screenshot.pgyer.com/image/view/app_screenshots/daf41cb9f9a54c3c9879555ddf4ec8c8-528)

 - **微信样式**
 
![微信样式](https://app-screenshot.pgyer.com/image/view/app_screenshots/fad19096a28cec65094f6120c154b47f-528)
![微信样式](https://app-screenshot.pgyer.com/image/view/app_screenshots/21145d344498c57954704bde3e0e7dfc-528)
![微信样式](https://app-screenshot.pgyer.com/image/view/app_screenshots/2cb198df6739d1a9f91d9ee60ec3c29f-528)

 - **自定义样式**
 
![自定义样式](https://app-screenshot.pgyer.com/image/view/app_screenshots/44b8fdecff62aa20eb51b4f54cfec30a-528)
![自定义样式](https://app-screenshot.pgyer.com/image/view/app_screenshots/57a62bcc84844400878fdb343cf762e8-528) 
 

 - **自定义比例剪裁**
 
 ![自定义比例剪裁](https://app-screenshot.pgyer.com/image/view/app_screenshots/15483adb087360ff49e831cb988adce1-528)
 ![自定义比例剪裁](https://app-screenshot.pgyer.com/image/view/app_screenshots/c32921bd346904cec77b7fea919afb56-528)


 
### 微信图片选择
支持视频、GIF、长图选择，支持单张多比例剪裁，支持多图预览、编辑、以及调序，支持直接拍照。调用前请按照demo实现IMultiPickerBindPresenter 接口          

 #### 单选/多选—— 支持视频和图片单一选择模式
```java
//微信样式多选，WXImgPickerPresenter为用户自定义的微信显示样式，                                  
// 以及一些交互逻辑，实现自IMultiPickerBindPresenter接口                                   
ImagePicker.withMulti(new WXImgPickerPresenter())                            
        .setMaxCount(9)//设置最大选择数量                                            
        .setColumnCount(4)//设置显示列数                                           
        .showVideo(true)//设置是否加载视频                                           
        .showGif(true)//设置是否加载GIF                                            
        .showCamera(true)//设置是否显示拍照按钮（在列表第一个）          
        .showImage(true)//设置是否加载图片
        .setMaxVideoDuration(120000)//设置视频可选择的最大时长
        //设置只能选择视频或图片
        .setSinglePickImageOrVideoType(true)
        //设置只能选择一个视频 
        .setVideoSinglePick(true)               
        //设置下次选择需要屏蔽的图片或视频（简单点就是不可重复选择）                                      
        .setShieldList(new ArrayList<String>())                              
        //设置下次选择需要带入的图片和视频（简单点就是记录上次选择的图片，可以取消之前选择）                          
        .setLastImageList(new ArrayList<String>())                                                             
        //调用多选                                                               
        .pick(this, new OnImagePickCompleteListener() {                      
            @Override                                                        
            public void onImagePickComplete(ArrayList<ImageItem> items) {    
                //处理回调回来的图片信息，主线程                                            
            }                                                                
        });     
        
//Fragment调用：    
MultiImagePickerFragment mFragment = ImagePicker.withMultiFragment(new WXImgPickerPresenter())    
              	...//省略以上若干属性
               .pickWithFragment(new OnImagePickCompleteListener() {
                    @Override
                    public void onImagePickComplete(ArrayList<ImageItem> items) {
                        //处理回调回来的图片信息，主线程         
                    }
                });

//---------外部Activity需要重写的方法------------         
@Override                                                                                   
public void onBackPressed() {                                                               
    if (null != mFragment && mFragment.onBackPressed()) {                                   
        return;                                                                             
    }                                                                                       
    super.onBackPressed();                                                                  
}                                                                                           
                                                                                            
@Override                                                                                   
protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {   
    super.onActivityResult(requestCode, resultCode, data);                                  
    if (mFragment != null) {                                                                
        mFragment.onTakePhotoResult(requestCode, resultCode);                               
    }                                                                                       
}           
});
                                                         
```
#### 单张剪裁 —— 支持自定义剪裁比例
```java
//微信样式多选，WXImgPickerPresenter为用户自定义的微信显示样式，                                  
// 以及一些交互逻辑，实现自IMultiPickerBindPresenter接口                                   
ImagePicker.withMulti(new WXImgPickerPresenter())                            
       	...//省略以上所有公共属性                                              
        .setCropRatio(1, 1)//设置剪裁比例   1：1            
        .cropSaveFilePath("剪裁图片保存地址")
        .cropRectMinMargin(0)//设置剪裁边框间距,单位 px                           
        //调用剪裁                                                              
        .crop(this, new OnImagePickCompleteListener() {                      
            @Override                                                        
            public void onImagePickComplete(ArrayList<ImageItem> items) {    
                //处理回调回来的图片信息，主线程                                            
            }                                                                
        });                                                                  
```
#### 预览 —— 预览 —— 支持普通预览和编辑预览
```java
  //预览数据源，只接受ArrayList<String> 和ArrayList<ImageItem> 两种泛型                          
ArrayList<String> imageList = new ArrayList<>();                                 
//默认选择的index                                                                     
int currentPos = 1;                                                              
//调用预览                                                                           
ImagePicker.withMulti(new WXImgPickerPresenter())                                
        //第二个参数为预览图片数组、第三个参数为默认选中的index，第四个参数为预览回调，                              
        //如果第四个参数为null,则代表无需对预览的图片进行编辑（调序、删除操作），反之可以编辑预览图                        
        .preview(this, imageList, currentPos, new OnImagePickCompleteListener() {
            @Override                                                            
            public void onImagePickComplete(ArrayList<ImageItem> items) {        
                //处理预览回调的数据                                                      
            }                                                                    
        });                                                                      
```

#### 拍照
```java
  //直接调用拍照                                                                                             
ImagePicker.withMulti(new WXImgPickerPresenter()).takePhoto(this, new OnImagePickCompleteListener() {
    @Override                                                                                        
    public void onImagePickComplete(ArrayList<ImageItem> imageItems) {                               
        //处理拍照回调                                                                                                                                                                   
    }                                                                                                
});                                                                                                  
```
 
#### 自定义UI和presenter交互 —— 支持item自定义和文件夹列表弹入方向
```java
/**
 * 作者：yangpeixing on 2018/9/26 15:57
 * 功能：微信样式图片选择器
 */
public class WXImgPickerPresenter implements IMultiPickerBindPresenter {                                       
                                                                                                               
    @Override                                                                                                  
    public void displayListImage(ImageView imageView, ImageItem item, int size) {                              
        Glide.with(imageView.getContext()).load(item.path).into(imageView);                                    
    }                                                                                                          
                                                                                                               
    @Override                                                                                                  
    public void displayPerViewImage(ImageView imageView, String url) {                                         
        Glide.with(imageView.getContext()).load(url).into(imageView);                                          
    }                                                                                                          
                                                                                                               
    @Override                                                                                                  
    public PickerUiConfig getUiConfig(Context context) {                                                       
        PickerUiConfig config = new PickerUiConfig();                                                          
        //是否沉浸式状态栏，状态栏颜色将根据TopBarBackgroundColor指定，                                                            
        // 并动态更改状态栏图标颜色                                                                                        
        config.setImmersionBar(true);                                                                          
        //设置主题色                                                                                                
        config.setThemeColor(Color.parseColor("#09C768"));                                                     
        //设置选中和未选中时图标                                                                                          
        config.setSelectedIconID(R.mipmap.picker_wechat_select);                                               
        config.setUnSelectIconID(R.mipmap.picker_wechat_unselect);                                             
        //设置返回图标以及返回图标颜色                                                                                       
        config.setBackIconID(R.mipmap.picker_icon_back_black);                                                 
        config.setBackIconColor(Color.BLACK);                                                                  
        //设置标题栏背景色和对齐方式，设置标题栏文本颜色                                                                              
        config.setTitleBarBackgroundColor(Color.parseColor("#F1F1F1"));                                        
        config.setTitleBarGravity(Gravity.START);                                                              
        config.setTitleColor(Color.BLACK);                                                                     
        //设置标题栏右上角完成按钮选中和未选中样式，以及文字颜色                                                                          
        int r = ViewSizeUtils.dp(context, 2);                                                                  
        config.setOkBtnSelectBackground(CornerUtils.cornerDrawable(Color.parseColor("#09C768"), r));           
        config.setOkBtnUnSelectBackground(CornerUtils.cornerDrawable(Color.parseColor("#B4ECCE"), r));         
        config.setOkBtnSelectTextColor(Color.WHITE);                                                           
        config.setOkBtnUnSelectTextColor(Color.parseColor("#50ffffff"));                                       
        config.setOkBtnText("完成");                                                                             
        //设置选择器背景色                                                                                             
        config.setPickerBackgroundColor(Color.WHITE);                                                          
        //设置选择器item背景色                                                                                         
        config.setPickerItemBackgroundColor(Color.parseColor("#484848"));                                      
        //设置底部栏颜色                                                                                              
        config.setBottomBarBackgroundColor(Color.parseColor("#333333"));                                       
        //设置拍照按钮图标和背景色                                                                                         
        config.setCameraIconID(R.mipmap.picker_ic_camera);                                                     
        config.setCameraBackgroundColor(Color.parseColor("#484848"));  
        
       	//标题栏模式，从标题栏选择相册
        config.setPickStyle(PickerUiConfig.PICK_STYLE_TITLE); 
        //设置选择器自定义item样式
        config.setPickerItemView(new CustomPickerItem(context));                                       
        return config;                                                                                         
    }                                                                                                          
                                                                                                               
    @Override                                                                                                  
    public void tip(Context context, String msg) {                                                             
        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();                                               
    }                                                                                                          
                                                                                                               
    @Override                                                                                                  
    public void imageItemClick(Context context, ImageItem imageItem, ArrayList<ImageItem> selectImageList,     
                               ArrayList<ImageItem> allSetImageList, MultiGridAdapter adapter) {               
        tip(context, "我是自定义的图片点击事件");                                                                          
    }                                                                                                          
}                                                                                                                                                                                                                                                                                                                                       
```
 
### 小红书图片剪裁选择      
高仿小红书图片剪裁框架，支持视频以及多图剪裁、支持fragment样式侵入

#### Activity直接调用
```java
//调用小红书剪裁回调的imageItems里，imageItem.path是原图，                                    
// imageItem.getCropUrl()才是剪裁后的图片                                             
ImagePicker.withCrop(new RedBookCropPresenter())                              
        //设置第一张图信息，可为null,设置以后，选择器会默认                                         
        // 以第一张图片的剪裁方式剪裁后面所有的图片                                               
        .setFirstImageItem(new ImageItem())                                   
        .setFirstImageUrl("这里填入外部已经选择的第一张图片地址url")                            
        //设置要选择的最大数                                                           
        .setMaxCount(count)                                                                                             
        //设置是否加载视频                                                            
        .showVideo(true)                                                      
        //设置第一个item是否拍照                                                       
        .showCamera(true)                                                     
        //设置剪裁完图片保存路径                                                         
        .setCropPicSaveFilePath("图片保存路径")                                     
        .pick(this, new OnImagePickCompleteListener() {                       
            @Override                                                         
            public void onImagePickComplete(ArrayList<ImageItem> imageItems) {
                //调用小红书剪裁回调的imageItems里，imageItem.path是原图，                    
                // imageItem.getCropUrl()才是剪裁后的图片                             
                //TODO剪裁回调                                                    
            }                                                                 
        });                                                                                                                                               
```

#### Fragment嵌套调用

```java
//调用小红书剪裁回调的imageItems里，imageItem.path是原图，                                                  
// imageItem.getCropUrl()才是剪裁后的图片                                                           
ImagePickAndCropFragment fragment = ImagePicker.withCropFragment(new RedBookCropPresenter())                                      
       	//...省略以上属性                                             
        .pickWithFragment(new OnImagePickCompleteListener() {
                    @Override
                    public void onImagePickComplete(ArrayList<ImageItem> items) {
                        //TODO 图片剪裁完回调 主线程
                    }
                });     
                
//---------外部Activity需要重写的方法------------         
@Override                                                                                   
public void onBackPressed() {                                                               
    if (null != mFragment && mFragment.onBackPressed()) {                                   
        return;                                                                             
    }                                                                                       
    super.onBackPressed();                                                                  
}                                                                                           
                                                                                            
@Override                                                                                   
protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {   
    super.onActivityResult(requestCode, resultCode, data);                                  
    if (mFragment != null) {                                                                
        mFragment.onTakePhotoResult(requestCode, resultCode);                               
    }                                                                                       
}                                                                                                                                               
```

#### 自定义UI和Presenter交互
```java
/**
 - Description: 小红书样式框架数据绑定
 - <p>
 - Author: peixing.yang
 - Date: 2019/2/21
 */
public class RedBookCropPresenter implements ICropPickerBindPresenter {

    @Override
    public void displayListImage(ImageView imageView, ImageItem item, int size) {
        Glide.with(imageView.getContext()).load(item.path).into(imageView);
    }

    /**
     * 加载剪裁区域里的图片
     *
     * @param imageView imageView
     * @param item      当前图片信息
     */
    @Override
    public void displayCropImage(ImageView imageView, ImageItem item) {
        Glide.with(imageView.getContext()).load(item.path)
                .apply(new RequestOptions().format(DecodeFormat.PREFER_ARGB_8888))
                .into(imageView);
    }

    @Override
    public CropUiConfig getUiConfig(Context context) {
        CropUiConfig config = new CropUiConfig();
        //设置主题色，包含选中样式的圆形背景色和边框色
        config.setThemeColor(Color.RED);
        //设置item未选中图标
        config.setUnSelectIconID(R.mipmap.picker_icon_unselect);
        //设置相机图标
        config.setCameraIconID(R.mipmap.picker_ic_camera);
        //设置返回图标
        config.setBackIconID(R.mipmap.picker_icon_close_black);
        //设置剪裁区域自适应图标
        config.setFitIconID(R.mipmap.picker_icon_fit);
        //设置剪裁区域充满图标
        config.setFullIconID(R.mipmap.picker_icon_full);
        //设置留白图标
        config.setGapIconID(R.mipmap.picker_icon_haswhite);
        //设置填充图标
        config.setFillIconID(R.mipmap.picker_icon_fill);
        //设置视频暂停图标
        config.setVideoPauseIconID(R.mipmap.video_play_small);
        //设置返回按钮颜色
        config.setBackIconColor(Color.WHITE);
        //设置剪裁区域颜色
        config.setCropViewBackgroundColor(Color.parseColor("#111111"));
        //设置拍照图标背景色
        config.setCameraBackgroundColor(Color.BLACK);
        //设置标题栏背景色
        config.setTitleBarBackgroundColor(Color.BLACK);
        //设置下一步按钮选中文字颜色
        config.setNextBtnSelectedTextColor(Color.WHITE);
        //设置下一步按钮未选中文字颜色
        config.setNextBtnUnSelectTextColor(Color.WHITE);
        //设置标题文字颜色
        config.setTitleTextColor(Color.WHITE);
        //设置item列表背景色
        config.setGridBackgroundColor(Color.BLACK);
        //设置下一步按钮未选中时背景drawable
        config.setNextBtnUnSelectBackground(PCornerUtils.cornerDrawable(Color.parseColor("#B0B0B0"), PViewSizeUtils.dp(context, 30)));
        //设置下一步按钮选中时背景drawable
        config.setNextBtnSelectedBackground(PCornerUtils.cornerDrawable(Color.RED, PViewSizeUtils.dp(context, 30)));
        //设置是否显示下一步数量提示
        config.setShowNextCount(false);
        //设置下一步按钮文字
        config.setNextBtnText("下一步");
        return config;
    }

    /**
     * 选择超过数量限制提示
     *
     * @param context    上下文
     * @param maxCount   最大数量
     * @param defaultTip 默认提示文本 “最多选择maxCount张图片”
     */
    @Override
    public void overMaxCountTip(Context context, int maxCount, String defaultTip) {
        if (context == null) {
            return;
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setMessage(defaultTip);
        builder.setPositiveButton(com.ypx.imagepicker.R.string.picker_str_isee,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    /**
     * 在单选视频里，点击视频item会触发此回调
     *
     * @param activity  页面
     * @param imageItem 当前选中视频
     */
    @Override
    public void clickVideo(Activity activity, ImageItem imageItem) {
        Toast.makeText(activity, imageItem.path, Toast.LENGTH_SHORT).show();
    }
}

```

### 下个版本排期
时间：2019年12月左右
 1. 视频预览框架切换（吐槽：官方videoView太难用了~~/(ㄒoㄒ)/~~）
 2. 图片剪裁支持旋转
 3. 支持JPEG、PNG、GIF、BMP、WEBP、MPEG、MP4、QUICKTIME、THREEGPP、THREEGPP2、MKV、WEBM、TS、AVI等图片视频文件格式混合加载或指定加载


本库来源于mars App,想要体验城市最新的吃喝玩乐，欢迎读者下载体验mars!


作者：[calorYang](https://blog.csdn.net/qq_16674697)
邮箱：313930500@qq.com
Q Q: 313930500 
微信：calor0616 


**遇到问题别绕路，QQ微信直接呼~ 您的star就是我前进的动力~🌹**