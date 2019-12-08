<!-- TOC -->
- [自定义IPickerPresenter](#自定义IPickerPresenter)
- [自定义UI](#自定义UI)
    - [全局UI设置](#全局UI设置)
    - [自定义标题栏/底部栏](#自定义标题栏/底部栏)
    - [自定义item](#自定义item)
    - [自定义文件夹item](#自定义文件夹item)
    - [自定义预览界面](#自定义预览界面)
    - [自定义单图剪裁界面](#自定义单图剪裁界面)
- [小红书图片选择器](#小红书图片选择器)
    - [属性配置](#属性配置)
    - [Activity样式](#Activity样式)
    - [Fragment样式](#Fragment样式)
- [微信图片选择器](#微信图片选择器)
    - [属性配置](#属性配置)
    - [Activity样式](#Activity样式)
    - [Fragment样式](#Fragment样式)
- [多图预览](#多图预览)
- [单图剪裁](#单图剪裁)
    - [单选并剪裁](#单选并剪裁)
    - [拍照并剪裁](#拍照并剪裁)
    - [直接调用剪裁](#直接调用剪裁)
    - [自定义剪裁页面](#自定义剪裁页面)
- [调用摄像头](#调用摄像头)
    - [拍照](#拍照)
    - [拍视频](#拍视频)
- [获取媒体库数据](#获取媒体库数据)
    - [获取媒体相册数据](#获取媒体相册数据)
    - [获取全部媒体文件](#获取全部媒体文件)
    - [获取指定相册内全部媒体文件](#获取指定相册内全部媒体文件)
    - [预加载获取指定相册内全部媒体文件](#预加载获取指定相册内全部媒体文件)
- [设置选择器调用失败回调](#设置选择器调用失败回调)
    - [错误码对照](#错误码对照)
- [自定义回调](#自定义回调)
- [全局常量配置](#常量配置)
    - [全部常量](#全部常量)

<!-- /TOC -->
## 自定义IPickerPresenter
在开启选择器之前，必须实现presenter接口，接口定义如下：
```java
public interface IPickerPresenter extends Serializable {
    /**
     * 加载列表缩略图
     *
     * @param view imageView
     * @param item 图片信息
     * @param size 加载尺寸
     */
    void displayImage(View view, ImageItem item, int size, boolean isThumbnail);

    /**
     * 设置自定义ui显示样式
     *
     * @param context 上下文
     * @return PickerUiConfig
     */
    @NonNull
    PickerUiConfig getUiConfig(@Nullable Context context);

    /**
     * 动态配置提示文本
     *
     * @param context context
     * @return PickConstants
     */
    @NonNull
    PickConstants getPickConstants(@Nullable Context context);

    /**
     * 提示
     *
     * @param context 上下文
     * @param msg     提示文本
     */
    void tip(@Nullable Context context, String msg);

    /**
     * 选择超过数量限制提示
     *
     * @param context  上下文
     * @param maxCount 最大数量
     */
    void overMaxCountTip(@Nullable Context context, int maxCount);


    /**
     * 显示loading加载框，注意需要调用show方法
     *
     * @param activity          启动对话框的activity
     * @param progressSceneEnum {@link ProgressSceneEnum}
     *                          </p>
     *                          当progressSceneEnum==当ProgressSceneEnum.loadMediaItem 时，代表在加载媒体文件时显示加载框
     *                          目前框架内规定，当文件夹内媒体文件少于1000时，强制不显示加载框，大于1000时才会执行此方法
     *
     *                          </p>
     *                          当progressSceneEnum==当ProgressSceneEnum.crop 时，代表是剪裁页面的加载框
     * @return DialogInterface 对象，用于关闭加载框，返回null代表不显示加载框
     */
    DialogInterface showProgressDialog(@Nullable Activity activity, ProgressSceneEnum progressSceneEnum);

    /**
     * 拦截选择器完成按钮点击事件
     *
     * @param activity     当前选择器activity
     * @param selectedList 已选中的列表
     * @return true:则拦截选择器完成回调， false，执行默认的选择器回调
     */
    boolean interceptPickerCompleteClick(@Nullable Activity activity,ArrayList<ImageItem> selectedList, BaseSelectConfig selectConfig);

    /**
     * 拦截选择器取消操作，用于弹出二次确认框
     *
     * @param activity     当前选择器页面
     * @param selectedList 当前已经选择的文件列表
     * @return true:则拦截选择器取消， false，不处理选择器取消操作
     */
    boolean interceptPickerCancel(@Nullable Activity activity, ArrayList<ImageItem> selectedList);

    /**
     * <p>
     * 图片点击事件拦截，如果返回true，则不会执行选中操纵，如果要拦截此事件并且要执行选中
     * 请调用如下代码：
     * <p>
     * adapter.preformCheckItem()
     * <p>
     * <p>
     * 此方法可以用来跳转到任意一个页面，比如自定义的预览
     *
     * @param activity         上下文
     * @param imageItem       当前图片
     * @param selectImageList 当前选中列表
     * @param allSetImageList 当前文件夹所有图片
     * @param adapter         当前列表适配器，用于刷新数据
     * @return 是否拦截
     */
    boolean interceptItemClick(@Nullable Activity activity, ImageItem imageItem, ArrayList<ImageItem> selectImageList,
                               ArrayList<ImageItem> allSetImageList, BaseSelectConfig selectConfig, PickerItemAdapter adapter,
                               @Nullable IReloadExecutor reloadExecutor);


    /**
     * 拍照点击事件拦截
     *
     * @param activity  当前activity
     * @param takePhoto 拍照接口
     * @return 是否拦截
     */
    boolean interceptCameraClick(@Nullable Activity activity, ICameraExecutor takePhoto);
}

```

## 自定义UI
该框架支持自定义所有界面的ui样式。通过presenter中 getUiConfig 方法生成一个 PickerUiConfig 对象。该对象包含了全局ui配置和自定义ui配置。全局ui配置通过set方法直接设置，自定义ui需要配置 PickerUiProvider 对象，PickerUiProvider中包含了6组自定义配置。分别为：选择器标题栏/选择器底部栏/选择器item/选择器文件夹item/预览页面/单图剪裁页面
### 全局UI设置
在presenter的getUiConfig方法中添加如下配置：
```java
PickerUiConfig uiConfig = new PickerUiConfig();
//设置是否显示标题栏
uiConfig.setShowStatusBar(true);
//设置标题栏颜色
uiConfig.setStatusBarColor(Color.parseColor("#F5F5F5"));
//设置选择器背景
uiConfig.setPickerBackgroundColor(Color.BLACK);
//设置单图剪裁背景色
uiConfig.setSingleCropBackgroundColor(Color.BLACK);
//设置预览页面背景色
uiConfig.setPreviewBackgroundColor(Color.BLACK);
//设置选择器文件夹打开方向
uiConfig.setFolderListOpenDirection(PickerUiConfig.DIRECTION_BOTTOM);
//设置文件夹列表距离顶部/底部边距
uiConfig.setFolderListOpenMaxMargin(0);
//设置小红书剪裁区域的背景色
uiConfig.setCropViewBackgroundColor(Color.BLACK);
//设置小红书剪裁自适应状态图标
uiConfig.setFitIconID();
//设置小红书剪裁充满状态图标
uiConfig.setFullIconID();
//设置小红书剪裁留白状态图标
uiConfig.setGapIconID();
//设置小红书剪裁填充状态图标
uiConfig.setFillIconID();
//设置视频预览暂停图标
uiConfig.setVideoPauseIconID();
uiConfig.setPickerUiProvider(new PickerUiProvider(){
    @Override
    public PickerControllerView getTitleBar(Context context) {
        return super.getTitleBar(context);
    }

    @Override
    public PickerControllerView getBottomBar(Context context) {
        return super.getBottomBar(context);
    }

    @Override
    public PickerItemView getItemView(Context context) {
        return super.getItemView(context);
    }

    @Override
    public PickerFolderItemView getFolderItemView(Context context) {
        return super.getFolderItemView(context);
    }

    @Override
    public PreviewControllerView getPreviewControllerView(Context context) {
        return super.getPreviewControllerView(context);
    }

    @Override
    public SingleCropControllerView getSingleCropControllerView(Context context) {
        return super.getSingleCropControllerView(context);
    }
});
```
### 自定义标题栏/底部栏
继承PickerControllerView，并在uiConfig.setPickerUiProvider 中返回自定义的PickerControllerView，详细使用可参考框架内WXTitleBar/RedBookTitleBar 和 WXBottomBar 实现
```java
uiConfig.setPickerUiProvider(new PickerUiProvider(){
    @Override
    public PickerControllerView getTitleBar(Context context) {
        //这里返回自定义的PickerControllerView，默认返回WXTitleBar
        return super.getTitleBar(context);
    }

    @Override
    public PickerControllerView getBottomBar(Context context) {
        //这里返回自定义的PickerControllerView，默认返回WXBottomBar，如果没有底部栏，则返回null
        return super.getBottomBar(context);
    }
    //...省略其他方法
});
```
### 自定义item
继承PickerItemView，并在uiConfig.setPickerUiProvider 中返回自定义的PickerItemView，,可参考框架内 WXItemView 和RedBookItemView 两个类
```java
uiConfig.setPickerUiProvider(new PickerUiProvider(){
    @Override
    public PickerItemView getItemView(Context context) {
        //这里返回自定义的PickerItemView，默认返回PickerItemView
        return super.getItemView(context);
    }
    //...省略其他方法
});
```
### 自定义文件夹item
继承PickerFolderItemView，并在uiConfig.setPickerUiProvider 中返回自定义的PickerFolderItemView,可参考框架内 WXFolderItemView
```java
uiConfig.setPickerUiProvider(new PickerUiProvider(){
    @Override
    public PickerFolderItemView getFolderItemView(Context context) {
         //这里返回自定义的PickerFolderItemView，默认返回 WXFolderItemView
        return super.getFolderItemView(context);
    }
    //...省略其他方法
});
```
### 自定义预览界面
继承PreviewControllerView，并在uiConfig.setPickerUiProvider 中返回自定义的PreviewControllerView,可参考框架内 WXPreviewControllerView
```java
uiConfig.setPickerUiProvider(new PickerUiProvider(){
    @Override
   public PreviewControllerView getPreviewControllerView(Context context) {
        //这里返回自定义的PreviewControllerView，默认返回 WXPreviewControllerView
        return super.getPreviewControllerView(context);
    }
    //...省略其他方法
});
```
### 自定义单图剪裁界面
继承SingleCropControllerView，并在uiConfig.setPickerUiProvider 中返回自定义的SingleCropControllerView,可参考框架内 WXSingleCropControllerView
```java
uiConfig.setPickerUiProvider(new PickerUiProvider(){
   @Override
    public SingleCropControllerView getSingleCropControllerView(Context context) {
        //这里返回自定义的PreviewControllerView，默认返回 WXSingleCropControllerView
        return super.getSingleCropControllerView(context);
    }
    //...省略其他方法
});
```
## 小红书图片选择器
### 属性配置
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

### Activity样式
```java
ImagePicker.withCrop(new RedBookPresenter())//设置presenter
        //...省略若干属性
        .pick(this, new OnImagePickCompleteListener() {
            @Override
            public void onImagePickComplete(ArrayList<ImageItem> items) {
                //图片剪裁回调，主线程
                //注意：剪裁回调里的ImageItem中getCropUrl()才是剪裁过后的图片地址
            }
        });
```
### Fragment样式
```java
MultiImageCropFragment fragment= ImagePicker.withCrop(new RedBookPresenter())//设置presenter
        //...省略若干属性
        .pickWithFragment(new OnImagePickCompleteListener() {
            @Override
            public void onImagePickComplete(ArrayList<ImageItem> items) {
                //图片剪裁回调，主线程
                //注意：剪裁回调里的ImageItem中getCropUrl()才是剪裁过后的图片地址
            }
        });
```
- 外部activity需要复写onBackPressed
```java
@Override
public void onBackPressed() {
    if (fragment != null && fragment.onBackPressed()) {
        return;
    }
    super.onBackPressed();
}
```
## 微信图片选择器
### 属性配置
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

### Fragment样式
支持Fragment嵌入自定义的activity中，使用可参考MultiImagePickerActivity。
```java
MultiImagePickerFragment fragment = ImagePicker.withMulti(new WeChatPresenter())
    //...省略若干属性
    .pickWithFragment(new OnImagePickCompleteListener() {
        @Override
        public void onImagePickComplete(ArrayList<ImageItem> items) {
            //图片选择回调，主线程
        }
    });
```

外部activity需要复写onBackPressed
```java
@Override
public void onBackPressed() {
    if (fragment != null && fragment.onBackPressed()) {
        return;
    }
    super.onBackPressed();
}
```
## 多图预览
支持对一组图片进行预览操作，支持超长图、大图、高清图的加载
```java
//配置需要预览的所有图片列表
ArrayList<ImageItem> allPreviewImageList = new ArrayList<>();
//默认选中的图片索引
int defaultPosition = 0;
//开启预览
ImagePicker.preview(this, new WeChatPresenter(), allPreviewImageList, defaultPosition, new OnImagePickCompleteListener() {
        @Override
        public void onImagePickComplete(ArrayList<ImageItem> items) {
            //图片编辑回调，主线程
        }
    });
```

## 单图剪裁
支持对单张图片进行剪裁。支持调用选择器并剪裁，拍照并剪裁和直接调用剪裁

### 单选并剪裁
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
```java
CropConfig cropConfig = new CropConfig();
 //设置剪裁比例
cropConfig.setCropRatio(1, 1);
//设置剪裁框间距，单位px
cropConfig.setCropRectMargin(100);
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

### 直接调用剪裁
需要指定剪裁的原图完整路径
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

## 调用摄像头
支持直接调用手机摄像头

### 拍照
拍照保存路径不需要精确到文件名，文件名会自定生成。默认以jpg格式保存，如果不指定拍照路径，则默认照片/视频存放在 DCIM/Camera 下
```java
ImagePicker.takePhoto(this, "拍照保存路径", new OnImagePickCompleteListener() {
        @Override
        public void onImagePickComplete(ArrayList<ImageItem> items) {
            //拍照回调，主线程
        }
    });
```

### 拍视频
拍视频保存路径不需要精确到文件名，文件名会自定生成。默认以mp4格式保存，如果不指定视频路径，则默认照片/视频存放在 DCIM/Camera 下
```java
ImagePicker.takeVideo(this, "视频保存路径", new OnImagePickCompleteListener() {
        @Override
        public void onImagePickComplete(ArrayList<ImageItem> items) {
            //拍照回调，主线程
        }
    });
```

## 获取媒体库数据
 支持直接回调出媒体数据

### 获取媒体相册数据
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

### 获取全部媒体文件
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

### 获取指定相册内全部媒体文件
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

### 预加载获取指定相册内全部媒体文件
支持指定先加载默认数量的item,防止文件夹中图片过多，导致等待过长
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

## 设置选择器调用失败回调
所有OnImagePickCompleteListener回调都可以设置OnImagePickCompleteListener2监听
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
### 错误码对照
详见PickerError类
- **CANCEL**  ：-26883  选择器返回，即取消选择
- **MEDIA_NOT_FOUND**  ：-26884  没有媒体文件
- **PRESENTER_NOT_FOUND**  ：-26885  没有配置presenter
- **UI_CONFIG_NOT_FOUND**  ：-26886  没有配置presenter里的UiConfig
- **SELECT_CONFIG_NOT_FOUND**  ：-26887  配置选项错误
- **CROP_URL_NOT_FOUND**  ：-26888  剪裁url错误
- **CROP_EXCEPTION** -26889 ：剪裁异常
- **OTHER**  ：-26890  其他错误

## 自定义回调
所有OnImagePickCompleteListener回调都可以被自定义回调OnPickerCompleteListener给替换
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
本库中默认实现了两种类型的转化
- **OnStringCompleteListener**
```java
ImagePicker.withMulti(new WXImgPickerPresenter())
            //...省略若干属性
            .pick(new OnStringCompleteListener() {
                @Override
                public void onPickComplete(String path) {
                    //回调
                }
            });
```
- **OnStringListCompleteListener**
```java
ImagePicker.withMulti(new WXImgPickerPresenter())
            //...省略若干属性
            .pick(new OnStringListCompleteListener() {
                @Override
                public void onPickComplete(ArrayList<String> list) {
                    //回调
                }
            });
```

## 全局常量配置
支持更改选择器中string文件中某些常量。需要在presenter中getPickConstants方法里返回新的PickConstants对象
示例如下：
```java
@NonNull
@Override
public PickConstants getPickConstants(Context context) {
    PickConstants pickConstants=new PickConstants(context);
    pickConstants.picker_str_only_select_image="我是自定义文本";
    //以下省略若干常量配置
    //...
    return pickConstants;
}
```
### 全部常量：
为了修改的统一，这里的常量命名严格按照string中字符串的命名来取，详见PickConstants类
```java
//拍照权限话术
public String picker_str_camera_permission;
//存储权限话术
public String picker_str_storage_permission;
//图片和视频
public String picker_str_multi_title;
//视频选择
public String picker_str_multi_title_video;
//图片选择
public String picker_str_multi_title_image;
//图片剪裁
public String picker_str_crop_title;
//充满
public String picker_str_full;
//留白
public String picker_str_gap;
//该文件已选过或无法选择
public String picker_str_shield;
//图片未加载完成，请稍候!
public String picker_str_wait_for_load;
//资源加载中,请稍后…
public String picker_str_loading;
//该视频文件路径无效或已损坏!
public String picker_str_video_error;
//暂未发现媒体文件
public String picker_str_media_not_found;
//只能选择图片!
public String picker_str_only_select_image;
//只能选择视频!
public String picker_str_only_select_video;
//视频时长不得超过
public String picker_str_video_over_max_duration;
//视频时长不得少于
public String picker_str_video_less_min_duration;
//拍摄视频
public String picker_str_take_video;
//拍摄照片
public String picker_str_take_photo;
```