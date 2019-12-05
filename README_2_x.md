### 关于YImagePicker 


**重要通知：目前2.x版本为选择器基础版本，预计2019年11月底前发布升级版本3.0，内部架构将会完全重构，UI和逻辑代码完全解耦，将会支持选择器标题栏、底部栏、item、相册列表、预览、剪裁等所有UI定制，不再设置复杂的uiconfig,即可完全实现市场上百分之99的选择器样式。届时会发布邮件通知，请使用者务必watching ,3.x版本将不再兼容2.x，所有配置将会开启新纪元！** 

本文档更新于:2019/11/02


[ ![Download](https://api.bintray.com/packages/yangpeixing/yimagepicker/androidx/images/download.svg?version=2.4.5) ](https://bintray.com/yangpeixing/yimagepicker/androidx/2.4.5/link)
 - 支持小红书剪裁样式并自定义UI
 - 支持微信、马蜂窝、知乎等样式定制
 - 支持13种视频图片文件类型混合加载(2.4.4版本加入)
 - 支持大图预览（普通预览+编辑预览），支持超长图、超大图
 - 支持单图自定义比例剪裁
 - 支持单图圆形剪裁，生成png圆形图片(2.4.3版本加入)
 - 支持单图留白剪裁（仿最新微信图片头像选择），支持生成透明背景图(2.4.5版本加入)
 - 小红书剪裁样式支持视频多选和预览
 - 微信样式支持图片和视频文件混合选择或指定类型选择
 - 微信样式支持保存上一次选中的图片状态
 - 微信样式支持屏蔽上一次选中的图片
 - 选择结果直接回调，拒绝配置ActivityForResult+requestCode，即调用即处理
 - 支持选择器调用失败回调(2.4.4版本加入)
 - 支持自定义回调类型(2.4.5版本加入)
 - 轻量级，aar大小不超过300K，无so库，无任何第三方依赖
 - 支持androidx和support
 - 永久维护

 - **支持直接回调媒体相册列表及文件列表数据(2.4.6版本加入)**
 - **支持选择器所有文案定制(2.4.6版本加入)**
 - **androidx已适配AndroidQ，support版本不支持29(2.4.6版本加入)**
 - **支持直接拍摄视频(2.4.6版本加入)**


### 引入依赖
**androidx版本：**

```java
implementation 'com.ypx.yimagepicker:androidx:2.4.6.2'
```
**support版本：** （不支持targetSdkVersion>=29,最高兼容28）
```java
implementation 'com.ypx.yimagepicker:support:2.4.6'
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
支持视频、GIF、长图选择，支持选择状态保存。调用前请按照demo实现IMultiPickerBindPresenter接口 ，示例如下：
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
           .setMinVideoDuration(60000L)//设置视频可选取的最小时长
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
高仿小红书图片剪裁框架，支持视频以及多图剪裁、支持视频预览，支持UI自定义，支持fragment样式侵入。调用前请按照demo实现ICropPickerBindPresenter接口 ，示例如下：
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
           .setMaxVideoDuration(2000L)//设置可选取的最大视频时长
           .setMinVideoDuration(60000L)//设置视频可选取的最小时长
           .pick(this, new OnImagePickCompleteListener() {
                @Override
                public void onImagePickComplete(ArrayList<ImageItem> items) {
                    //图片剪裁回调，主线程
                    //注意：剪裁回调里的ImageItem中getCropUrl()才是剪裁过后的图片地址
                }
            });                                                        
```
### 预览
支持普通预览和编辑预览，示例如下：
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

### 拍照
支持直接打开摄像头拍照，示例如下：
```java
ImagePicker.takePhoto(this, "拍照保存路径", new OnImagePickCompleteListener() {
        @Override
        public void onImagePickComplete(ArrayList<ImageItem> items) {
            //拍照回调，主线程
        }
    });
```

### 拍摄视频
支持直接打开摄像头拍视频，示例如下：
```java
ImagePicker.takeVideo(this, "视频保存路径", new OnImagePickCompleteListener() {
        @Override
        public void onImagePickComplete(ArrayList<ImageItem> items) {
            //拍照回调，主线程
        }
    });
```

### 调用选择器并剪裁
支持选择图片完调用剪裁，支持自定义比例剪裁，支持圆形剪裁，示例如下：
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

### 拍照并剪裁
支持直接打开摄像头拍照并剪裁，支持自定义比例剪裁和圆形剪裁，示例如下：
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
支持直接跳转剪裁页面，示例如下：
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

### 提供媒体数据——支持回调相册数据、所有媒体数据、指定相册内媒体数据
#### 获取媒体相册数据
```java
//指定要回调的相册类型，可以指定13种图片视频文件格式混合
Set<MimeType> mimeTypes = MimeType.ofAll();
ImagePicker.provideMediaSets(this, mimeTypes, new MediaSetsDataSource.MediaSetProvider() {
    @Override
    public void providerMediaSets(ArrayList<ImageSet> imageSets) {
        //相册列表回调，主线程
    }
});
```
#### 获取全部媒体文件
```java
//指定要回调的相册类型，可以指定13种图片视频文件格式混合
Set<MimeType> mimeTypes = MimeType.ofAll();
ImagePicker.provideAllMediaItems(this, mimeTypes, new MediaItemsDataSource.MediaItemProvider() {
        @Override
        public void providerMediaItems(ArrayList<ImageItem> imageItems, ImageSet allVideoSet) {
            //全部媒体数据回调，主线程
            //只有当mimeTypes既包含图片或者视频格式文件时，allVideoSet才有值
        }
    });
```
#### 获取指定相册内全部媒体文件
```java
//指定要回调的相册类型，可以指定13种图片视频文件格式混合
Set<MimeType> mimeTypes = MimeType.ofAll();
//指定相册，id不能为空
ImageSet imageSet = new ImageSet();
ImagePicker.provideMediaItemsFromSet(this, imageSet, mimeTypes, new MediaItemsDataSource.MediaItemProvider() {
        @Override
        public void providerMediaItems(ArrayList<ImageItem> imageItems, ImageSet allVideoSet) {
            //全部媒体数据回调，主线程
            //只有当mimeTypes既包含图片或者视频格式文件时，allVideoSet才有值
        }
    });
```
#### 预加载获取指定相册内全部媒体文件
```java
//指定要回调的相册类型，可以指定13种图片视频文件格式混合
Set<MimeType> mimeTypes = MimeType.ofAll();
//指定相册，id不能为空
ImageSet imageSet = new ImageSet();
//预加载个数
int preloadSize = 40;
ImagePicker.provideMediaItemsFromSetWithPreload(this, imageSet, mimeTypes, preloadSize, 
    new MediaItemsDataSource.MediaItemPreloadProvider() {
        @Override
        public void providerMediaItems(ArrayList<ImageItem> imageItems) {
            //预加载回调，预先加载指定数目的媒体文件回调
        }
    },
    new MediaItemsDataSource.MediaItemProvider() {
        @Override
        public void providerMediaItems(ArrayList<ImageItem> imageItems, ImageSet allVideoSet) {
            //所有媒体文件回调
            }
        });
```

### presenter指定、自定义Item样式、自定义皮肤UI、自定义提示常量、设置选择器调用失败回调、自定义回调类型
详细使用方法请[查看详细API文档](https://github.com/yangpeixing/YImagePicker/wiki/YImagePicker使用文档)


### 版本记录
[查看详细版本记录](https://github.com/yangpeixing/YImagePicker/wiki/YImagePicker版本记录)


#### 2.4.6版本 [2019.11.02]
  1. 【BUG修复】修复了红米拍照闪退问题
  2. 【BUG修复】取消了选择器没有文件时直接退出选择器的策略。改为提示语句“暂未发现媒体文件”.
  3. 【适配】已适配AndroidQ，解决targetSdkVersion设置29时数据库报错的bug
  4. 【新增】新增直接拍摄视频
  5. 【新增】presenter新增overMaxCountTip、interceptPickerCancel、interceptVideoClick、getPickConstants四个方法
  6. 【新增】新增PickConstants用于修改选择器所有文案，在presenter中指定
  7. 【新增】支持直接回调媒体数据，其中包含回调相册列表、全部媒体文件、指定相册里媒体文件，支持指定数量预加载。
  8. 【新增】新增退出选择器时拦截回调，新增点击视频item的拦截回调，在presenter中指定
  9. 【新增】新增视频最小选择时长
  10. 【优化】重构了预览页面，将选择器预览和通用预览分离，降低耦合度
  11. 【优化】统一整理了资源文件命名，以及删除不必要的资源
  12. 【调整】clearAllCache方法已废弃
  13. 【调整】原有的选择器拍照会直接回调出照片，现在改为生成在选择器的第一个
  14. 【调整】当选择器只加载视频时，拍照item支持拍摄视频，其他情况均为拍照
  15. 【优化】选择器调用屏蔽多次点击，调用多次
  16. 【优化】所有不可选择的item(置灰)选中均会有具体的提示

### 下个版本排期
时间：2019年11月中旬
 1. ~~适配AndroidQ~~（2.4.6已支持）
 2. 微信选择器加入原图选项
 3. ~~支持对外暴露数据源，以便于实现类似QQ发消息时的选择图片~~（2.4.6已支持）
 4. 实现最新版微信样式
 5. 剪裁支持输出指定大小图片
 6. **等你来提**

 计划TODO：
 1. 视频预览框架切换（吐槽：官方videoView太难用了~~/(ㄒoㄒ)/~~）
 2. 图片剪裁支持旋转
 3. ~~支持JPEG、PNG、GIF、BMP、WEBP、MPEG、MP4、QUICKTIME、THREEGPP、THREEGPP2、MKV、WEBM、TS、AVI等图片视频文件格式混合加载或指定加载~~（2.4.4已支持）

 永不TODO：
 1. 不会支持图片压缩，请使用者自行使用luBan
 2. 不会支持图片和视频高级编辑（滤镜、贴纸等）

### 感谢
- 本框架媒体库查询部分借鉴于知乎开源框架Matisee，并在其基础上拓展并延伸，让查询更富有定制性，在此对原作者表示感谢。

- 本库来源于mars App,想要体验城市最新的吃喝玩乐，欢迎读者下载体验mars!

- 感谢所有支持或Star本项目的使用者，感谢所有给我提Issue的朋友们 ~~ 鞠躬 ~~！

### 心声

因本人最近顺利的当了爸爸，需要照顾老婆和小孩，所以有些时候消息回复的不是很及时，很多问题没能够给使用者及时的回复，在这里由衷的表示歉意。 YImagePicker从当初的只支持微信图片选择器到支持小红书样式，再到各种自定义，可谓花费了我近一年多的时光，可能有人觉得这个项目很简单，但是从开源性的角度上来说，很多时候代码不是我们想怎么写就怎么写的。为了达成统一风格，本人也借鉴了不下于20多个图片选择库。但是随着业务的复杂和机型的多样，不得不一遍一遍重构，其中带来了不少的问题，也学习到了很多。在我的计划中，本库只是一个开始，虽然定制性很强，但是代码逻辑还是有些复杂，架构还需要不断调整。可能使用者在使用的过程中会出现各种各样的问题，还请不要对本库放弃，可以大胆的加我联系方式并反馈给我（喷我），如果BUG紧急，我也会加班完善它，至于那些取消star或者不看好本框架的，我也只能说声抱歉，没有解决掉你们的痛点。还是那句老话，没有什么是完美的，但我会力所能及~


- 作者：[calorYang](https://blog.csdn.net/qq_16674697)
- 邮箱：313930500@qq.com
- Q Q: 313930500 
- 微信：calor0616 
- 博客：[CSDN](https://blog.csdn.net/qq_16674697)


**遇到问题别绕路，QQ微信直接呼~ 您的star就是我前进的动力~🌹**