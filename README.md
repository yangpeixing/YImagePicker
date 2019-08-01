# 欢迎使用YPXImagePicker
本库包含了微信的图片选择器、小红书多张剪裁图片选择器，其中微信图片选择器支持高度定制UI、图片加载框架以及跨进程回调，无需使用原始onActivityForResult拿到数据，直接设置选择监听，降低代码耦合度，易于维护！本库已投入多个大型商业项目使用，持续迭代，稳定可靠！

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
	//列表图片加载框架指定
    @Override
    public void displayListImage(ImageView imageView, String url, int size) {
        Glide.with(imageView.getContext()).load(url).into(imageView);
    }
	//预览大图加载框架指定
    @Override
    public void displayPerViewImage(ImageView imageView, String url) {
        Glide.with(imageView.getContext()).load(url).into(imageView);
    }
	//自定义UI样式以及主题
    @Override
    public MultiUiConfig getUiConfig(Context context) {
        MultiUiConfig config = new MultiUiConfig();
        config.setImmersionBar(true);
        config.setThemeColor(Color.parseColor("#C000FF00"));
        config.setSelectedIconID(R.mipmap.wechat_select);
        config.setUnSelectIconID(R.mipmap.wechat_unselect);
        config.setBackIconID(com.ypx.imagepickerdemo.R.mipmap.ypx_icon_back_black);
        config.setCameraIconID(R.mipmap.ypx_ic_camera);
        config.setoKBtnText("完成");
        config.setTitleColor(Color.WHITE);
        config.setTopBarTitleGravity(Gravity.START);
        config.setRightBtnBackground(0);
        config.setRightBtnTextColor(Color.GREEN);
        config.setTopBarBackgroundColor(Color.parseColor("#303030"));
        config.setBottomBarBackgroundColor(Color.parseColor("#f0303030"));
        config.setGridViewBackgroundColor(Color.BLACK);
        config.setImageItemBackgroundColor(Color.parseColor("#404040"));
        config.setLeftBackIconColor(Color.WHITE);
        return config;
    }
	//toast提示
    @Override
    public void tip(Context context, String msg) {
        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
    }
	//视频点击回调
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



开发者：[yangpeixing](https://blog.csdn.net/qq_16674697)
email:313930500@qq.com




