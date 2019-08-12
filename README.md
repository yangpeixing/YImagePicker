## 关于YPXImagePicker
本库内部集成了微信的图片选择器、小红书多图剪裁选择器两种图片选择器。采用切入式fragment代替传统的onActivityForResult，更方便的拿到回调数据，并且支持跨进程回调，大大减少了不必要的冗余代码，从而增加开发效率！本Module与app主项目采用序列化接口方式实现实现模块化解耦，调用框架前需要实现交互层Presenter接口，比如指定固定的图片加载框架（Glide、Picasso等）以及一些定制性UI样式、业务逻辑等。本库已投入多个商业项目的使用，稳定可靠，持续迭代！

[apk安装体验地址](https://www.pgyer.com/Wfhb)

## 全局配置

 - **引用方式：**
```xml
implementation 'com.ypx.imagepicker:ypxImagePicker:2.0.3'
```
 - **全局配置：**
```java
 //注册媒体文件观察者，可放入Application或首页中                      
ImagePicker.registerMediaObserver(getApplication());
//预加载选择器，需要APP先申请存储权限，否则无效                          
//设置预加载后，可实现快速打开选择器                      
ImagePicker.preload(this, true, true, false);       
```


## 微信图片选择
支持视频、GIF、长图选择，支持单张多比例剪裁，支持多图预览、编辑、以及调序，支持直接拍照
![多选](https://img-blog.csdnimg.cn/20190801143311449.gif)          ![剪裁](https://img-blog.csdnimg.cn/20190801143237522.gif)

 - **多图/单图选择—— 支持重复和非重复选择**
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
```
 - **单张剪裁 —— 支持自定义剪裁比例**
```java
//微信样式多选，WXImgPickerPresenter为用户自定义的微信显示样式，                                  
// 以及一些交互逻辑，实现自IMultiPickerBindPresenter接口                                   
ImagePicker.withMulti(new WXImgPickerPresenter())                            
       	...//省略以上所有公共属性                                              
        .setCropRatio(1, 1)//设置剪裁比例   1：1                                       
        //调用剪裁                                                              
        .crop(this, new OnImagePickCompleteListener() {                      
            @Override                                                        
            public void onImagePickComplete(ArrayList<ImageItem> items) {    
                //处理回调回来的图片信息，主线程                                            
            }                                                                
        });                                                                  
```
 - **预览 —— 支持普通预览和预览编辑（调序、删除）**
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

 - **拍照**
```java
  //直接调用拍照                                                                                             
ImagePicker.withMulti(new WXImgPickerPresenter()).takePhoto(this, new OnImagePickCompleteListener() {
    @Override                                                                                        
    public void onImagePickComplete(ArrayList<ImageItem> imageItems) {                               
        //处理拍照回调                                                                                                                                                                   
    }                                                                                                
});                                                                                                  
```
 
 - **自定义样式**
```java
/**
 * 作者：yangpeixing on 2018/9/26 15:57
 * 功能：微信样式图片选择器
 */
public class WXImgPickerPresenter implements IMultiPickerBindPresenter {                                   
                                                                                                           
    @Override                                                                                              
    public void displayListImage(ImageView imageView, String url, int size) {                              
        Glide.with(imageView.getContext()).load(url).into(imageView);                                      
    }                                                                                                      
                                                                                                           
    @Override                                                                                              
    public void displayPerViewImage(ImageView imageView, String url) {                                     
        Glide.with(imageView.getContext()).load(url).into(imageView);                                      
    }                                                                                                      
                                                                                                           
    @Override                                                                                              
    public MultiUiConfig getUiConfig(Context context) {                                                    
        MultiUiConfig config = new MultiUiConfig();                                                        
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
        config.setTopBarBackgroundColor(Color.parseColor("#F1F1F1"));                                      
        config.setTopBarTitleGravity(Gravity.START);                                                       
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
        return config;                                                                                     
    }                                                                                                      
                                                                                                           
    @Override                                                                                              
    public void tip(Context context, String msg) {                                                         
        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();                                           
    }                                                                                                      
                                                                                                           
    @Override                                                                                              
    public void onClickVideo(ImageItem videoItem) {                                                                                                                                                               
    }                                                                                                                                                                                                                
}                                                                                                          
```
## 小红书图片剪裁选择      
高仿小红书图片剪裁框架，支持视频以及多图剪裁、支持fragment样式侵入
![在这里插入图片描述](https://img-blog.csdnimg.cn/2019080114404790.gif)
 - **Activity直接调用**
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
        //设置是否显示底部自定义View                                                     
        .showBottomView(true)                                                 
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
 - **Fragment嵌套调用**

```java
//调用小红书剪裁回调的imageItems里，imageItem.path是原图，                                                  
// imageItem.getCropUrl()才是剪裁后的图片                                                           
ImagePickAndCropFragment fragment = ImagePicker.withCropFragment(new RedBookCropPresenter())
        //设置第一张图信息，可为null,设置以后，选择器会默认                                                       
        // 以第一张图片的剪裁方式剪裁后面所有的图片                                                             
        .setFirstImageItem(new ImageItem())                                                 
        .setFirstImageUrl("这里填入外部已经选择的第一张图片地址url")                                          
        //设置要选择的最大数                                                                         
        .setMaxCount(count)                                                                 
        //设置是否显示底部自定义View                                                                   
        .showBottomView(true)                                                               
        //设置是否加载视频                                                                          
        .showVideo(true)                                                                    
        //设置第一个item是否拍照                                                                     
        .showCamera(true)                                                                   
        //设置剪裁完图片保存路径                                                                       
        .setCropPicSaveFilePath("图片保存路径")                                                   
        .pickWithFragment();                                                                
fragment.setImageListener(new OnImagePickCompleteListener() {                               
    @Override                                                                               
    public void onImagePickComplete(ArrayList<ImageItem> items) {                           
        //TODO 图片剪裁完回调                                                                      
    }                                                                                       
});                                                                                         
```

 - **自定义数据绑定交互**
```java
/**
 - Description: 小红书样式框架数据绑定
 - <p>
 - Author: peixing.yang
 - Date: 2019/2/21
 */
public class RedBookCropPresenter implements ICropPickerBindPresenter {
    //图片加载
    @Override
    public void displayListImage(ImageView imageView, String url) {
        Glide.with(imageView.getContext()).load(url).into(imageView);
    }

    @Override
    public void displayCropImage(ImageView imageView, String url) {
        Glide.with(imageView.getContext()).load(url).into(imageView);
    }

    //自定义底部栏
    @Override
    public View getBottomView(final Context context) {
        TextView textView = new TextView(context);
        textView.setText("这是底部自定义View");
        textView.setGravity(Gravity.CENTER);
        textView.setTextColor(Color.WHITE);
        textView.setBackgroundColor(Color.RED);
        textView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewSizeUtils.dp(context, 50)));
        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(context, "点击了", Toast.LENGTH_SHORT).show();
            }
        });
        return textView;
    }

    //自定义草稿箱对话框，可在进入时直接弹出是否加载草稿
    @Override
    public void showDraftDialog(Context context) {

    }

    //视频点击
    @Override
    public void clickVideo(Context context, ImageItem imageItem) {
        Toast.makeText(context, imageItem.path, Toast.LENGTH_SHORT).show();
    }
} 
```
## 相关问题

 - **本库只兼容了androidx库，如有需要support库版本请下载源码修改或者联系作者**
 - **小红书剪裁框架暂且不支持UI自定义**
 - **微信选择框架暂不支持图片高级编辑，后期会加入**
 - **小红书剪裁框架中，输出的图片全部是View所截出来的，所以宽高取决于屏幕宽度，其实这样是不对的，应该根据原图密度来截取原始图片区域，而不应该是截取View,不过经测试，View截取的图片质量挺高，所以暂未兼容原图截取。本库中有CropHelper类已经处理了原图截取的方法，赞只支持填充模式下原图截取，留白模式后期优化。**


## 鸣谢
本项目微信选择器中剪裁使用的是github上[cropView](https://github.com/oginotihiro/cropview)控件，在此感谢作者！
打个广告：本库来源于mars App,想要体验城市最新的吃喝玩乐，欢迎读者下载体验mars!

[本库Github地址:https://github.com/yangpeixing/YPXImagePicker](https://github.com/yangpeixing/YPXImagePicker)




开发者：[yangpeixing](https://blog.csdn.net/qq_16674697)
email:313930500@qq.com

