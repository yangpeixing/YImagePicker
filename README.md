### 关于YImagePicker 
[点击查看2.x版本readme](https://github.com/yangpeixing/YImagePicker/blob/master/README_2_x.md)

本文档更新于:2020/4/15 上午10点36分

[ ![Download](https://api.bintray.com/packages/yangpeixing/yimagepicker/androidx/images/download.svg?version=3.1.4) ](https://bintray.com/yangpeixing/yimagepicker/androidx/3.1.4/link)

 - 支持小红书多图剪裁、微信多图选择、单图剪裁、多图批量剪裁、大图预览
 - 支持自定义所有UI，包括标题栏、底部栏、列表item、文件夹item、剪裁页面、预览页面等
 - 支持13种视频图片格式混合加载，支持过滤掉指定格式文件
 - 支持大图预览，支持超长图、超大图，拒绝too lagre(已修复单图剪裁长图)
 - 支持自定义剪裁比例、剪裁边距、圆形剪裁、镂空/充满剪裁（仿最新微信图片头像选择）
 - 支持视频多选和预览
 - 支持只选择图片或者视频类型
 - 支持恢复上一次选中的图片状态（微信样式）
 - 支持屏蔽指定媒体文件（微信样式）
 - 选择结果直接回调，拒绝配置ActivityForResult+requestCode，即调用即处理
 - 支持选择器调用失败回调
 - 支持自定义回调类型
 - 支持直接回调媒体相册列表及文件列表
 - 支持选择器所有文案修改、国际化定制
 - 支持多种特殊需求覆盖，支持自定义选择器拦截事件
 - 已全面适配androidQ
 - 支持直接拍摄视频、照片等
 - 轻量级，aar大小不超过300K，无so库，无任何第三方依赖
 - 支持androidx和support
 - 永久维护




### 引入依赖
**androidx版本：**

```java
implementation 'com.ypx.yimagepicker:androidx:3.1.4'
```
**support版本：后期可能不再维护，请使用者尽早切换androidx** （support依赖最高兼容28）
```java
implementation 'com.ypx.yimagepicker:support:3.1.4.1'
```

### 核心原理
YImagePicker与主项目通过IPickerPresenter进行交互与解耦，presenter采用序列化接口的方式实现。使用者需要自行实现presenter的方法。选择器回调采用嵌入fragment的方式实现，类似于Glide或RxPermisson.原理上还是使用OnActivityResult,但无需再配置requestCode并且支持跨进程回调。

调用选择器之前必须实现 [IPickerPresenter](https://github.com/yangpeixing/YImagePicker/wiki/Documentation_3.x#自定义IPickerPresenter)  接口

[选择器问题解答汇总](https://github.com/yangpeixing/YImagePicker/wiki/questions)

[点击查看3.x详细API文档](https://github.com/yangpeixing/YImagePicker/wiki/Documentation_3.x)

[apk体验地址 - 密码：123456](https://www.pgyer.com/imagepicker) 

### 效果图集
 - **demo效果**
 
![demo效果](https://imgconvert.csdnimg.cn/aHR0cHM6Ly93d3cucGd5ZXIuY29tL2ltYWdlL3ZpZXcvYXBwX3NjcmVlbnNob3RzL2Y5YWUxNmNiNWZmZWFjZGY4OWVmZDA2YzE5MTA4MWIxLTUyOA?x-oss-process=image/format,png)

 - **小红书样式**

![小红书样式](https://www.pgyer.com/image/view/app_screenshots/5378234cde61dec8d3823ebbbfa0255a-528)
![小红书样式](https://app-screenshot.pgyer.com/image/view/app_screenshots/87b43cb9ef8f40377bc3910b3ad3737b-528)
![小红书样式](https://www.pgyer.com/image/view/app_screenshots/9f6ccada146068692fda0e30b7a5df18-528)

 - **微信样式**
 
![微信样式](https://www.pgyer.com/image/view/app_screenshots/825b3d168d70ab4539aed61af6be7f36-528)
![微信样式](https://app-screenshot.pgyer.com/image/view/app_screenshots/21145d344498c57954704bde3e0e7dfc-528)
![微信样式](https://www.pgyer.com/image/view/app_screenshots/1ec0113d850e5ee9917172a447c2737f-528)

 - **自定义样式**
 
![自定义样式](https://www.pgyer.com/image/view/app_screenshots/74e132b3b997df394a41f0c0b8622467-528)
![自定义样式](https://app-screenshot.pgyer.com/image/view/app_screenshots/57a62bcc84844400878fdb343cf762e8-528) 
 

 - **自定义比例剪裁**
 
 ![自定义比例剪裁](https://app-screenshot.pgyer.com/image/view/app_screenshots/15483adb087360ff49e831cb988adce1-528)
 ![自定义比例剪裁](https://app-screenshot.pgyer.com/image/view/app_screenshots/4cf64a6afb74b6457103bd04debb7e58-528)
 
 
 - **多图剪裁页面(仿aloha)**
 
 ![aloha](https://www.pgyer.com/image/view/app_screenshots/a35c837bd25612c9b04f097f99457d62-528)
 
### 微信图片选择
支持视频、GIF、长图选择，支持选择状态保存。调用前请按照demo实现IPickerPresenter接口 ，示例如下：
[WeChatPresenter](https://github.com/yangpeixing/YImagePicker/blob/master/YPX_ImagePicker_androidx/app/src/main/java/com/ypx/imagepickerdemo/style/WeChatPresenter.java) 

```java
ImagePicker.withMulti(new WeChatPresenter())//指定presenter                                 
        //设置选择的最大数 
        .setMaxCount(9)  
        //设置列数                                                  
        .setColumnCount(4)      
        //设置要加载的文件类型，可指定单一类型  
        .mimeTypes(MimeType.ofAll()) 
        //设置需要过滤掉加载的文件类型 
        .filterMimeTypes(MimeType.GIF)       
        .showCamera(true)//显示拍照 
        .setPreview(true)//开启预览                                                  
        //大图预览时是否支持预览视频
        .setPreviewVideo(true)  
        //设置视频单选                                     
        .setVideoSinglePick(true) 
        //设置图片和视频单一类型选择 
        .setSinglePickImageOrVideoType(true) 
        //当单选或者视频单选时，点击item直接回调，无需点击完成按钮          
        .setSinglePickWithAutoComplete(false)
        //显示原图  
        .setOriginal(true)  
        //显示原图时默认原图选项开关  
        .setDefaultOriginal(false)
        //设置单选模式，当maxCount==1时，可执行单选（下次选中会取消上一次选中）
        .setSelectMode(SelectMode.MODE_SINGLE)   
        //设置视频可选取的最大时长,同时也是视频可录制的最大时长
        .setMaxVideoDuration(1200000L)  
        //设置视频可选取的最小时长                                  
        .setMinVideoDuration(60000L) 
        //设置上一次操作的图片列表，下次选择时默认恢复上一次选择的状态 
        .setLastImageList(null) 
        //设置需要屏蔽掉的图片列表，下次选择时已屏蔽的文件不可选择
        .setShieldList(null)               
        .pick(this, new OnImagePickCompleteListener() {  
            @Override    
            public void onImagePickComplete(ArrayList<ImageItem> items) {          
                //图片选择回调，主线程                  
            }                
        });                                                                                                                              
```
### 小红书图片选择
高仿小红书图片剪裁框架，支持视频以及多图剪裁、支持视频预览，支持UI自定义，支持fragment样式侵入。调用前请按照demo实现IPickerPresenter接口 ，示例如下：
[RedBookPresenter](https://github.com/yangpeixing/YImagePicker/blob/master/YPX_ImagePicker_androidx/app/src/main/java/com/ypx/imagepickerdemo/style/RedBookPresenter.java) 
```java
ImagePicker.withCrop(new RedBookPresenter())//设置presenter                
        .setMaxCount(9)//设置选择数量                                          
        .showCamera(true)//设置显示拍照                                        
        .setColumnCount(4)//设置列数                                         
        .mimeTypes(MimeType.ofImage())//设置需要加载的文件类型                      
        .filterMimeTypes(MimeType.GIF)//设置需要过滤掉的文件类型                     
        .assignGapState(false)//强制留白模式                                   
        .setFirstImageItem(null)//设置上一次选中的图片                             
        .setFirstImageItemSize(1,1)//设置上一次选中的图片地址                        
        .setVideoSinglePick(true)//设置视频单选                                
        .setMaxVideoDuration(1200000L)//设置可选区的最大视频时长                        
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
支持多图预览和自定义预览界面，支持加载大图，超长图和高清图，示例如下：
```java
//配置需要预览的所有图片列表,其中imageitem可替换为Uri或者String(绝对路径)
ArrayList<ImageItem> allPreviewImageList = new ArrayList<>();

//默认选中的图片索引
int defaultPosition = 0;
//开启预览
ImagePicker.preview(this, new WXImgPickerPresenter(), allPreviewImageList, defaultPosition, new OnImagePickCompleteListener() {
        @Override
        public void onImagePickComplete(ArrayList<ImageItem> items) {
            //图片编辑回调，主线程
        }
    });                                                           
```

### 拍照 3.1版本已变更
支持直接打开摄像头拍照，3.1版本去除了原有的拍照保存路径,新增了isCopyInDCIM入参,代表是否将拍照的图片copy一份到外部DCIM目录中
因为安卓Q禁止直接写入文件到系统DCIM文件下，所以拍照入参必须是私有目录路径.所以废弃掉原有的imagepath入参
如果想让拍摄的照片写入外部存储中，则需要copy一份文件到DCIM目录中并刷新媒体库
示例如下：
```java
String name="图片名称,不要加后缀";//可为null
boolean isCopyInDCIM=true;//copy一份保存到系统相册文件
ImagePicker.takePhoto(this,name,isCopyInDCIM, new OnImagePickCompleteListener() {
        @Override
        public void onImagePickComplete(ArrayList<ImageItem> items) {
            //拍照回调，主线程
        }
    });
```

### 拍摄视频 3.1版本已变更
支持直接打开摄像头拍视频，3.1已变更,变更理由参考拍照 示例如下：
```java
String name="视频名称,不要加后缀";//可为null
long maxDuration=10000l;//可录制的最大时常,单位毫秒ms
boolean isCopyInDCIM=true;//copy一份保存到系统相册文件
ImagePicker.takeVideo(this,name,maxDuration, isCopyInDCIM,new OnImagePickCompleteListener() {
        @Override
        public void onImagePickComplete(ArrayList<ImageItem> items) {
            //视频回调，主线程
        }
    });
```

### 调用选择器并剪裁
支持选择图片完调用剪裁，支持自定义比例剪裁，支持圆形剪裁，示例如下：
 ```java
ImagePicker.withMulti(new WeChatPresenter())
            .mimeTypes(MimeType.ofImage())
            .filterMimeTypes(MimeType.GIF)
            //剪裁完成的图片是否保存在DCIM目录下
            //true：存储在DCIM下 false：存储在 data/包名/files/imagePicker/ 目录下
            .cropSaveInDCIM(false)
             //设置剪裁比例
            .setCropRatio(1,1)
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
//设置上一次剪裁矩阵位置信息,用于恢复上一次剪裁,Info类型从imageitem或者cropimageview中取,可为null
cropConfig.setCropRestoreInfo(new Info());
 //设置剪裁比例
cropConfig.setCropRatio(1, 1);
//设置剪裁框间距，单位px
cropConfig.setCropRectMargin(100);
//是否保存到DCIM目录下，false时会生成在 data/files/imagepicker/ 目录下
cropConfig.saveInDCIM(false);
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
//设置上一次剪裁矩阵位置信息,用于恢复上一次剪裁,Info类型从imageitem或者cropimageview中取,可为null
cropConfig.setCropRestoreInfo(new Info());
 //设置剪裁比例
cropConfig.setCropRatio(1, 1);
//设置剪裁框间距，单位px
cropConfig.setCropRectMargin(100);
//是否保存到DCIM目录下，false时会生成在 data/files/imagepicker/ 目录下
cropConfig.saveInDCIM(false);
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

### 使用前请务必阅读[选择器问题解答汇总](https://github.com/yangpeixing/YImagePicker/wiki/questions)以及 [3.x使用文档](https://github.com/yangpeixing/YImagePicker/wiki/Documentation_3.x)

### 版本记录
[查看详细版本记录](https://github.com/yangpeixing/YImagePicker/wiki/YImagePicker版本记录)

#### 3.1.4版本 [2020.04.15]
1. 修复3.1.3版本path取不到绝对路径问题
2. 修复quicktime类型视频无法过滤的问题

#### 3.1.3版本 [2020.04.09]
1. 修复安卓10上全部视频文件夹缩略图不展示 
2. 修复安卓10上部分手机上部分图片无法展示问题 
3. 修复大图预览放大缩小卡顿问题 
4. 彻底修复toolarge闪退 
5. 优化了图片剪裁预览质量 
6. 修复预览界面无法预览视频问题 
7. 预览支持自定义预览item，可根据图片还是视频动态显示布局 
8. presenter的interceptItemClick方法新增isClickCheckBox入参，代表拦截的对象是否是点击右上角checkbox 
9. 新增showCameraOnlyInAllMediaSet方法代表是否只在全部文件夹里才展示拍照item，其他文件夹不展示 
10. imageitem新增displayName字段代表文件名称 
11. 其他更多细节优化

#### 3.1版本 [2020.02.24]
1. [优化]isOriginalImage加入到imageitem里
2. [变更]自定义文本统一,删除了PickerConstants类,presenter中无需对选择器文案进行修改,若需要修改文案,则直接复制imagepicker中string文件,另外demo已覆盖英文适配
3. [优化]多图剪裁已加入demo(AlohaActivity)
4. [bug修复]长图剪裁崩溃问题
5. [bug修复]关闭屏幕旋转问题
6. [新增]新增setDefaultOriginal用于设置此次打开选择器的默认原图开关
7. [新增]单图剪裁支持保存状态,下次恢复
8. [新增]录制视频添加最大时长配置
9. [优化]完全兼容androudQ拍照问题
10. [新增]UiPickerConfig新增主题色设置
11. [优化]demo架构调整,使用者参考更清晰


### 下个版本排期
时间：暂定2020年5月中旬
 1. [剪裁支持输出大小](https://github.com/yangpeixing/YImagePicker/issues/19)
 2. [剪裁支持旋转（尽量)](https://github.com/yangpeixing/YImagePicker/issues/32)
 3. [文件大小加载限制](https://github.com/yangpeixing/YImagePicker/issues/41)
 4. 支持darkmode模式
 5. 支持activity自定义跳转动画
 6. 内置新版本微信样式，知乎样式等
 7. 支持切换视频底层框架（吐槽：官方videoView太难用了~~/(ㄒoㄒ)/~~）
 8. **等你来提**

 永不TODO：
 1. 不会支持图片压缩，请使用者自行使用luBan
 2. 不会支持图片和视频高级编辑（滤镜、贴纸等）

### 感谢
- 本框架媒体库查询部分借鉴于知乎开源框架Matisee，并在其基础上拓展并延伸，让查询更富有定制性，在此对Matisee原作者表示感谢。

- 本库来源于mars App,想要体验城市最新的吃喝玩乐，欢迎读者下载体验mars!

- 感谢所有支持或star本项目的使用者，感谢所有给我提issue的朋友们 ！

### 心声

 YImagePicker从当初的只支持微信图片选择器到支持小红书样式，再到各种自定义，可谓花费了我近一年多的时光，可能有人觉得这个项目很简单，但是从开源性的角度上来说，很多时候代码不是我们想怎么写就怎么写的。为了达成统一风格，本人也借鉴了不下于20多个图片选择库。但是随着业务的复杂和机型的多样，不得不一遍遍重构，其中带来了不少的问题，也学习到了很多。在我的计划中，本库3.0版本算是一次较大的更新，相比2.x有着更优异的稳定性和定制性。但这绝不是最优版本，随着越来越多的定制化需求，架构可能会面临一次又一次的更新。所以使用者在使用的过程中如果出现各种各样的问题，还请不要放弃本库，可以直接加我联系方式并反馈给我（喷我），如果有更好的建议，也可以给我提request，我们一起完善此框架。最后说一句，没有什么是完美的，但我会力所能及~


- 作者：[calorYang](https://blog.csdn.net/qq_16674697)
- 邮箱：313930500@qq.com
- QQ号: 313930500 
- 微信：calor0616 
- 博客：[CSDN](https://blog.csdn.net/qq_16674697)


**遇到问题别绕路，QQ微信直接呼~ 您的star就是我前进的动力~🌹**