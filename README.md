### 关于YImagePicker 
[点击查看2.x版本readme](https://github.com/yangpeixing/YImagePicker/blob/master/README_2_x.md)

本文档更新于:2019/12/08 下午9点39分

[ ![Download](https://api.bintray.com/packages/yangpeixing/yimagepicker/androidx/images/download.svg?version=3.0-beta) ](https://bintray.com/yangpeixing/yimagepicker/androidx/3.0-beta/link)
 - 支持无缝切换小红书剪裁样式、微信图片多选样式
 - 支持选择器所有ui自定义，包括标题栏、底部栏、列表item、文件夹item等
 - 支持13种视频图片格式混合加载，支持过滤掉指定格式文件
 - 支持大图预览，支持超长图、超大图，拒绝too lagre
 - 支持单图自定义比例剪裁，可定制剪裁边距
 - 支持单图圆形剪裁
 - 支持单图留白剪裁（仿最新微信图片头像选择），支持生成透明背景图
 - 小红书剪裁样式支持视频多选和预览
 - 支持只选择图片或者视频类型
 - 支持恢复上一次选中的图片状态（微信样式）
 - 支持屏蔽指定媒体文件（微信样式）
 - 选择结果直接回调，拒绝配置ActivityForResult+requestCode，即调用即处理
 - 支持选择器调用失败回调
 - 支持自定义回调类型
 - 支持直接回调媒体相册列表及文件列表
 - 支持选择器所有文案定制
 - 支持多种特殊需求覆盖，支持自定义选择器拦截事件
 - androidx版本已全面适配androidQ
 - 支持直接拍摄视频、图片等
 - 轻量级，aar大小不超过300K，无so库，无任何第三方依赖
 - 支持androidx和support
 - 永久维护


### 引入依赖
**androidx版本：**

```java
implementation 'com.ypx.yimagepicker:androidx:3.0'
```
**support版本：后期可能不再维护，请使用者尽早切换androidx** （support依赖最高兼容28）
```java
implementation 'com.ypx.yimagepicker:support:3.0'
```

### 核心原理
YImagePicker与主项目通过IPickerPresenter进行交互与解耦，presenter采用序列化接口的方式实现。使用者需要自行实现presenter的方法。选择器回调采用嵌入fragment的方式实现，类似于Glide或RxPermisson.原理上还是使用OnActivityResult,但无需再配置requestCode并且支持跨进程回调。

调用选择器之前必须实现 IPickerPresenter 接口

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
 
### 微信图片选择
支持视频、GIF、长图选择，支持选择状态保存。调用前请按照demo实现IPickerPresenter接口 ，示例如下：
```java
ImagePicker.withMulti(new WeChatPresenter())//指定presenter                                 //设置选择的最大数 
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
        .setOriginal(true)  //显示原图     
        //设置单选模，当maxCount==1时，可执行单选（下次选中会取消上一次选中）
        .setSelectMode(SelectMode.MODE_SINGLE)   
        //设置视频可选取的最大时长
        .setMaxVideoDuration(2000L)  
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
        .setMaxVideoDuration(2000L)//设置可选区的最大视频时长                        
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


#### 3.0版本 [2019.12.08]
1. 【优化】重构了2.x版本选择器架构，去除原繁琐死板的ui配置，彻底解耦ui层和逻辑层
2. 【优化】合并了原两个presenter为一个IPickerPresenter，降低使用者的使用成本
3. 【优化】全面适配androidQ,本框架中生成的图片地址，只有两种，要么DCIM目录，要么为data/包名/files/imagepicker/ 目录下
4. 【优化】小红书剪裁图片剪裁质量，告别原有剪裁view的形式，改成剪裁原图
5. 【优化】presenter优化，新增了更多的人性化配置，让用户无需改动源码的情况下，轻松胜任任何需求
6. 【bug修复】修复了单图剪裁主界面卡死
7. 【bug修复】修复了长图预览toolarge崩溃
8. 【bug修复】修复android10上机器加载不出图片问题
9. 【新增】新增预览视频配置项
10. 【新增】微信样式新增原图选项
11. 【新增】剪裁图片新增saveInDCIM方法，用于指定剪裁图片生成目录，替代了原来的cropImageSaveFilePath方法
12. 【去除】cropImageSaveFilePath方法，用saveInDCIM替代
13. 【调整】原来所有mimetype方法更名为mimetypes
14. 【优化】其他更多性能优化

### 下个版本排期
时间：2020年1月中旬
 1. 剪裁支持输出大小
 2. 剪裁支持旋转（尽量）
 3. 支持darkmode模式
 4. 支持activity自定义跳转动画
 5. 内置新版本微信样式，知乎样式等
 6. 支持切换视频底层框架（吐槽：官方videoView太难用了~~/(ㄒoㄒ)/~~）
 7. **等你来提**

 永不TODO：
 1. 不会支持图片压缩，请使用者自行使用luBan
 2. 不会支持图片和视频高级编辑（滤镜、贴纸等）

### 感谢
- 本框架媒体库查询部分借鉴于知乎开源框架Matisee，并在其基础上拓展并延伸，让查询更富有定制性，在此对原作者表示感谢。

- 本库来源于mars App,想要体验城市最新的吃喝玩乐，欢迎读者下载体验mars!

- 感谢所有支持或Star本项目的使用者，感谢所有给我提Issue的朋友们 ~~ 鞠躬 ~~！

### 心声

因本人最近顺利的当了爸爸，需要照顾老婆和小孩，所以有些时候消息回复的不是很及时，很多问题没能够给使用者及时的回复，在这里由衷的表示歉意。 YImagePicker从当初的只支持微信图片选择器到支持小红书样式，再到各种自定义，可谓花费了我近一年多的时光，可能有人觉得这个项目很简单，但是从开源性的角度上来说，很多时候代码不是我们想怎么写就怎么写的。为了达成统一风格，本人也借鉴了不下于20多个图片选择库。但是随着业务的复杂和机型的多样，不得不一遍一遍重构，其中带来了不少的问题，也学习到了很多。在我的计划中，本库3.0版本算是一次较大的更新，相比2.x有着更优异的性能和定制性。虽然定制性很强，但是代码逻辑还是有些复杂，架构可能还需要不断调整。使用者在使用的过程中会出现各种各样的问题，还请不要对本库放弃，可以大胆的加我联系方式并反馈给我（喷我），如果BUG紧急，我也会加班完善它，至于那些取消star或者不看好本框架的，我也只能说声抱歉，没有解决掉你们的痛点。还是那句老话，没有什么是完美的，但我会力所能及~


- 作者：[calorYang](https://blog.csdn.net/qq_16674697)
- 邮箱：313930500@qq.com
- Q Q: 313930500 
- 微信：calor0616 
- 博客：[CSDN](https://blog.csdn.net/qq_16674697)


**遇到问题别绕路，QQ微信直接呼~ 您的star就是我前进的动力~🌹**