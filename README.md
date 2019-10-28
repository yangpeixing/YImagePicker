<!-- TOC 
- [关于YImagePicker](#关于yimagepicker)
- [引入依赖](#引入依赖)
- [核心原理](#核心原理)
- [效果图集](#效果图集)
- [微信图片选择](#微信图片选择)
    - [单张剪裁 —— 支持自定义剪裁比例](#单张剪裁--支持自定义剪裁比例)
    - [预览 —— 支持普通预览和编辑预览](#预览--支持普通预览和编辑预览)
    - [拍照](#拍照)
    - [自定义UI和presenter交互 —— 支持item自定义和文件夹列表弹入方向](#自定义ui和presenter交互--支持item自定义和文件夹列表弹入方向)
- [小红书图片剪裁选择](#小红书图片剪裁选择)  
    - [Activity直接调用](#activity直接调用)
    - [Fragment嵌套调用](#fragment嵌套调用)
    - [自定义UI和Presenter交互](#自定义ui和presenter交互)
- [版本记录](#版本记录)
    - [2.4.3版本](#243版本)
    - [2.4.2版本](#242版本)
- [下个版本排期](#下个版本排期)
/TOC -->

### 关于YImagePicker 
[ ![Download](https://api.bintray.com/packages/yangpeixing/yimagepicker/androidx/images/download.svg?version=2.4.5) ](https://bintray.com/yangpeixing/yimagepicker/androidx/2.4.5/link)
 - 支持无缝切换小红书剪裁样式并自定义UI
 - 支持微信、马蜂窝、知乎等多个不同风格样式定制
 - 支持13种视频图片文件类型混合加载(2.4.4版本加入)
 - 支持图片直接预览和编辑预览（排序、删除）
 - 支持高清预览超长图、超大图，图片放大效果胜过微信
 - 支持单图自定义比例剪裁
 - 支持单图圆形剪裁，生成png圆形图片(2.4.3版本加入)
 - 支持单图留白剪裁（仿最新微信图片头像选择），支持生成透明背景图(2.4.5版本加入)
 - 小红书剪裁样式支持视频多选和预览
 - 微信样式支持图片和视频文件混合选择或指定类型选择
 - 微信样式支持多次选择状态保存
 - 微信样式支持指定某些媒体文件不可选择
 - 选择结果直接回调，拒绝配置ActivityForResult+requestCode，即调用即处理
 - 支持选择器调用失败回调(2.4.4版本加入)
 - 支持自定义回调类型(2.4.5版本加入)
 - 轻量级，aar大小不超过300K，无so库，无任何第三方依赖
 - 支持androidx和support
 - 永久维护


### 引入依赖
**androidx版本：**

```java
implementation 'com.ypx.yimagepicker:androidx:2.4.5'
```
**support版本：** 
```java
implementation 'com.ypx.yimagepicker:support:2.4.5'
```

### 核心原理
YImagePicker与主项目通过presenter进行交互与解耦，presenter采用序列化接口的方式实现。回调采用嵌入fragment的方式实现，类似于Glide或RxPermisson.原理上还是使用OnActivityResult,但无需再配置requestCode并且支持跨进程回调。


小红书样式需要实现：ICropPickerBindPresenter
微信样式需要实现：IMultiPickerBindPresenter

[点击查看详细API文档](https://github.com/yangpeixing/YImagePicker/wiki/YImagePicker使用文档)

[apk体验地址](https://www.pgyer.com/Wfhb)

### 效果图集
 - **demo效果**
 
![demo效果](https://app-screenshot.pgyer.com/image/view/app_screenshots/49a0ff5b0eede276c94c1f094bf12e75-528)

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
 ![自定义比例剪裁](https://app-screenshot.pgyer.com/image/view/app_screenshots/4cf64a6afb74b6457103bd04debb7e58-528)


### [点击查看详细API文档](https://github.com/yangpeixing/YImagePicker/wiki/YImagePicker使用文档)
 
### 微信图片选择
支持视频、GIF、长图选择，支持选择状态保存。调用前请按照demo实现IMultiPickerBindPresenter接口 

 **调用示例**：
```java
ImagePicker.withMulti(new WXImgPickerPresenter())//指定presenter
           .setMaxCount(9)//设置选择的最大数
           .setColumnCount(4)//设置列数
           .mimeType(MimeType.ofAll())//设置要加载的文件类型，可指定单一类型
           .filterMimeType(MimeType.GIF)//设置需要过滤掉加载的文件类型
           .showCamera(true)//显示拍照
           .setPreview(true)//开启预览
           .setVideoSinglePick(true)//设置视频单选
           .setSinglePickImageOrVideoType(true)//设置图片和视频单一类型选择
           .setMaxVideoDuration(120000L)//设置视频可选取的最大时长
           .setLastImageList(null)//设置上一次操作的图片列表，下次选择时默认恢复上一次选择的状态
           .setShieldList(null)//设置需要屏蔽掉的图片列表，下次选择时已屏蔽的文件不可选择
           .pick(this, new OnImagePickCompleteListener() {
                @Override
                public void onImagePickComplete(ArrayList<ImageItem> items) {
                    //图片选择回调，主线程
                }
            });                                                                                                                            
```
### 小红书图片选择
高仿小红书图片剪裁框架，支持视频以及多图剪裁、支持视频预览，支持UI自定义，支持fragment样式侵入。调用前请按照demo实现ICropPickerBindPresenter接口 

 **调用示例**：
```java
ImagePicker.withCrop(new RedBookCropPresenter())//设置presenter
           .setMaxCount(9)//设置选择数量
           .showCamera(true)//设置显示拍照
           .setColumnCount(4)//设置列数
           .mimeType(MimeType.ofImage())//设置需要加载的文件类型
           .filterMimeType(MimeType.GIF)//设置需要过滤掉的文件类型
           .setFirstImageItem(null)//设置上一次选中的图片
           .setFirstImageUrl(null)//设置上一次选中的图片地址
           .setVideoSinglePick(true)//设置视频单选
           .setCropPicSaveFilePath("剪裁图片保存路径")
           .setMaxVideoDuration(2000L)//设置可选区的最大视频时长
           .pick(this, new OnImagePickCompleteListener() {
                @Override
                public void onImagePickComplete(ArrayList<ImageItem> items) {
                    //图片剪裁回调，主线程
                    //注意：剪裁回调里的ImageItem中getCropUrl()才是剪裁过后的图片地址
                }
            });                                                        
```
### 单图剪裁
支持选择图片完调用剪裁，支持自定义比例剪裁，支持圆形剪裁

 **调用示例**：
 ```java
ImagePicker.withMulti(new WXImgPickerPresenter())
            .mimeType(MimeType.ofImage())
            .filterMimeType(MimeType.GIF)
             //设置剪裁比例
            .setCropRatio(1,1)
            .cropSaveFilePath("剪裁图片保存路径")
            //设置剪裁框间距，单位px
            .cropRectMinMargin(50)
             //是否圆形剪裁，圆形剪裁时，setCropRatio无效
            .cropAsCircle()
             //设置剪裁模式，留白或充满  CropConfig.STYLE_GAP 或 CropConfig.STYLE_FILL
            .cropStyle(CropConfig.STYLE_FILL)
             //设置留白模式下生成的图片背景色，支持透明背景
            .cropGapBackgroundColor(Color.TRANSPARENT)
            .crop(this, new OnImagePickCompleteListener() {
                @Override
                public void onImagePickComplete(ArrayList<ImageItem> items) {
                    //图片剪裁回调，主线程
                }
            });                                                     
```

### 预览
支持普通预览和编辑预览

 **调用示例**：
```java
//配置需要预览的所有图片列表
ArrayList<ImageItem> allPreviewImageList = new ArrayList<>();
//默认选中的图片索引
int defaultPosition = 0;
//开启编辑预览
ImagePicker.preview(this, new WXImgPickerPresenter(), allPreviewImageList, defaultPosition, new OnImagePickCompleteListener() {
        @Override
        public void onImagePickComplete(ArrayList<ImageItem> items) {
            //图片编辑回调，主线程
        }
    });                                                           
```


### 直接拍照
支持直接打开摄像头拍照

 **调用示例**：
```java
ImagePicker.takePhoto(this, "拍照保存路径", new OnImagePickCompleteListener() {
            @Override
            public void onImagePickComplete(ArrayList<ImageItem> items) {
                //拍照回调，主线程
            }
        });
```
 
### 直接拍照并剪裁
支持直接打开摄像头拍照并剪裁，支持自定义比例剪裁和圆形剪裁

 **调用示例**：
```java
//配置剪裁属性
CropConfig cropConfig = new CropConfig();
 //设置剪裁比例
cropConfig.setCropRatio(1, 1);
//设置剪裁框间距，单位px
cropConfig.setCropRectMargin(100);
cropConfig.setCropSaveFilePath("剪裁生成的图片路径");
//是否圆形剪裁，圆形剪裁时，setCropRatio无效
cropConfig.setCircle(false);
//设置剪裁模式，留白或充满  CropConfig.STYLE_GAP 或 CropConfig.STYLE_FILL
cropConfig.setCropStyle(CropConfig.STYLE_GAP);
//设置留白模式下生成的图片背景色，支持透明背景
cropConfig.setCropGapBackgroundColor(Color.TRANSPARENT );
//调用拍照
ImagePicker.takePhotoAndCrop(this, new WXImgPickerPresenter(), cropConfig, 
    new OnImagePickCompleteListener() {
        @Override
        public void onImagePickComplete(ArrayList<ImageItem> items) {
            //剪裁回调，主线程
        }
    });
```

### 直接剪裁
支持直接跳转剪裁页面

 **调用示例**：
```java
CropConfig cropConfig = new CropConfig();
 //设置剪裁比例
cropConfig.setCropRatio(1, 1);
//设置剪裁框间距，单位px
cropConfig.setCropRectMargin(100);
cropConfig.setCropSaveFilePath("剪裁生成的图片路径");
//是否圆形剪裁，圆形剪裁时，setCropRatio无效
cropConfig.setCircle(false);
//设置剪裁模式，留白或充满  CropConfig.STYLE_GAP 或 CropConfig.STYLE_FILL
cropConfig.setCropStyle(CropConfig.STYLE_GAP);
//设置留白模式下生成的图片背景色，支持透明背景
cropConfig.setCropGapBackgroundColor(Color.TRANSPARENT );
//调用剪裁
String needCropImageUrl="需要剪裁的图片路径";
ImagePicker.crop(this, new WXImgPickerPresenter(), cropConfig, needCropImageUrl，
    new OnImagePickCompleteListener() {
        @Override
        public void onImagePickComplete(ArrayList<ImageItem> items) {
            //剪裁回调，主线程
        }
    });
```

### 设置选择器调用失败回调
所有OnImagePickCompleteListener回调都可以设置OnImagePickCompleteListener2监听

 **调用示例**：
```java
ImagePicker.withMulti(new WXImgPickerPresenter())
            //...省略若干属性
            .pick(new OnImagePickCompleteListener2() {
                 @Override
                public void onPickFailed(PickerError error) {
                    //调用选择器失败回调
                }

                @Override
                public void onImagePickComplete(ArrayList<ImageItem> items) {
                    //图片选择回调，主线程
                }
            })
```

### 设置自定义回调
所有OnImagePickCompleteListener回调都可以被自定义回调OnPickerCompleteListener给替换，框架默认支持两种回调

- **OnStringCompleteListener**：String回调，一般用于单图和剪裁的回调
- **OnStringListCompleteListener**：string数组回调，用于多图选择或预览回调

 **调用示例**：
```java
ImagePicker.withMulti(new WXImgPickerPresenter())
            //...省略若干属性
            .pick(new OnPickerCompleteListener<String>() {
                @Override
                public String onTransit(ArrayList<ImageItem> items) {
                    return null;
                }

                @Override
                public void onPickComplete(String s) {
                    //回调
                }
        });
```

**以上只是简单代码示例，详细功能请**
[查看详细API文档](https://github.com/yangpeixing/YImagePicker/wiki/YImagePicker使用文档)

### 版本记录
[查看详细版本记录](https://github.com/yangpeixing/YImagePicker/wiki/YImagePicker版本记录)


#### 2.4.5版本 [2019.10.27]
  1. 【BUG修复】修复拍照返回生成空文件的问题
  2. 【BUG修复】修复小红书样式切换文件夹，当文件夹中全部是视频时，视频单选的情况下直接回调clickVideo的问题
  3. 【新增】支持直接调用剪裁
  4. 【新增】支持自定义图片选择回调
  5. 【新增】支持留白式剪裁（仿最新版微信图片选择），可以让图片在剪裁区域内随意放置，镂空背景可定制
  6. 【新增】PickerError新增剪裁错误回调类型
  7. 【调整】原有调用剪裁时SelectConfig调整为为CropConfig



### 下个版本排期
时间：2019年12月左右
 1. 视频预览框架切换（吐槽：官方videoView太难用了~~/(ㄒoㄒ)/~~）
 2. 图片剪裁支持旋转
 3. ~~支持JPEG、PNG、GIF、BMP、WEBP、MPEG、MP4、QUICKTIME、THREEGPP、THREEGPP2、MKV、WEBM、TS、AVI等图片视频文件格式混合加载或指定加载~~（2.4.4已支持）
 4. **等你来提**
 


本库来源于mars App,想要体验城市最新的吃喝玩乐，欢迎读者下载体验mars!


作者：[calorYang](https://blog.csdn.net/qq_16674697)
邮箱：313930500@qq.com
Q Q: 313930500 
微信：calor0616 
博客：[CSDN](https://blog.csdn.net/qq_16674697)


**遇到问题别绕路，QQ微信直接呼~ 您的star就是我前进的动力~🌹**