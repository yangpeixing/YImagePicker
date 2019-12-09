## 1.YImagePicker中拍照和剪裁的图片生成目录在哪里 ？
YImagePicker为了减少使用者对安卓Q的适配，默认采用两种存储方式。第一种为外部存储，对应的目录是DCIM，第二种是项目缓存目录存储，对应目录是：Android/data/包名/files/imagePicker/ 目录下。
如需更改缓存目录imagepicker名字，则调用如下代码：
```java
ImagePicker.DEFAULT_FILE_NAME = "自定义目录名字";
```

## 2.ImageItem中的 path 和 uriPath 有什么区别？
androidQ上废弃了DATA绝对路径，需要手动拼凑Content Uri，这里为了兼容大部分项目还没有适配androidQ的情况,默认path还是先取绝对路径，取不到或者异常才去取Uri路径.对应源码：
```java
try {
    item.path = getString(cursor, MediaStore.Files.FileColumns.DATA);
} catch (Exception ignored) {
}

if (item.path == null || item.path.length() == 0) {
     item.path = item.getUri().toString();
}
```
所以path在绝对路径取不到时候，才会取content 路径。如何判断当前path是否是content uri？
```java
item.isUriPath()
```
如何强制获取uri路径，请使用：
```java
item.getUri()
```

## 3.我的界面和 微信/小红书 差不多，只是某些颜色不一样，不想自定义PickerItemView等view，怎么做到修改样式？
PickerUiConfig中包含对 选择器/预览界面/单图剪裁页面 ui的自定义，文档链接如下：
https://github.com/yangpeixing/YImagePicker/wiki/Documentation_3.x#自定义UI
如果你的样式和框架中 WXTitleBar/WXBottomBar/WXItemView/WXFolderItemView/WXPreviewControllerView/WXSingleCropControllerView 差不多，请使用如下方式修改某些属性：
```java
 uiConfig.setPickerUiProvider(new PickerUiProvider() {
        @Override
        public PickerControllerView getTitleBar(Context context) {
            WXTitleBar titleBar = (WXTitleBar) super.getTitleBar(context);
            titleBar.setCompleteText("下一步");
            titleBar.setCompleteBackground(null, null);
            titleBar.setCompleteTextColor(Color.parseColor("#859D7B"), Color.parseColor("#50859D7B"));
            titleBar.centerTitle();
            titleBar.setShowArrow(true);
            titleBar.setCanToggleFolderList(true);
            titleBar.setBackIconID(R.mipmap.picker_icon_close_black);
            return titleBar;
        }
    });
    return uiConfig;
}
```

## 4.微信选择器的原图功能，为什么图片没有变化
本框架中不处理图片的剪裁，所以本框架里的原图功能，仅仅是一个开关，如何拿到是否启动原图，请使用如下代码：
```java
boolean isOpenOriginal = ImagePicker.isOriginalImage;
```

## 5.选择器如何嵌套在viewpager中？实现类似小红书样式？
请使用fragment方式填充在viewpager里，这里需要注意的是，小红书样式如何嵌套在viewpager里，当用户滑动剪裁区域时，会默认执行剪裁，而不会执行viewpager的滑动。
https://github.com/yangpeixing/YImagePicker/wiki/Documentation_3.x#Fragment样式

## 6.如何跳转到自己项目里自定义的预览界面，然后返回刷新选择器选择状态？
使用 IPickerPresenter 中的 interceptItemClick 方法，此方法用于拦截用户item点击操作，这里请注意，此拦截默认只拦截可点击的item，不可点击的item默认会弹出toast提示。此方法返回true，则代表拦截掉框架的item事件处理。
刷新选择器请使用如下代码：
```java
//自定义的预览页面生成的选中列表
ArrayList<ImageItem> list = new ArrayList<>();
reloadExecutor.reloadPickerWithList(list);
```

## 7.如何点击完成跳转到我项目里的某个编辑页面，返回时不关闭选择器继续选择，编辑页面完成编辑时，需要关闭掉选择器并跳转到项目里发布页面？
使用 IPickerPresenter 中的 interceptPickerCompleteClick 方法，此方法用于拦截完成按钮的回调。假设使用场景如下：

A：发布页  B：选择器  C：编辑页面 
A调用选择器B,B点击完成跳转C,C可以编辑B选择图片，可以放弃编辑，直接按返回，此时可以回到B.如果C编辑成功，则回到A并刷新编辑后的照片。
此场景demo已覆盖，调用代码如下：
```java
 @Override
public boolean interceptPickerCompleteClick(Activity activity, ArrayList<ImageItem> selectedList, BaseSelectConfig selectConfig) {
    tip(activity, "拦截了完成按钮点击" + selectedList.size());
    Intent intent = new Intent(activity, C.class);
    intent.putExtra(ImagePicker.INTENT_KEY_PICKER_RESULT, selectedList);
    activity.startActivity(intent);
    return true;
}
```
C代码如下：
```java
 public void click(View view) {
     //imageItems为编辑后的图片列表
    ImagePicker.closePickerWithCallback(imageItems);
    finish();
}
```

## 8.微信样式selectMode(SelectMode.MODE_SINGLE) 单选模式到底是什么意思？以及setSinglePickWithAutoComplete怎么用？
SelectMode.MODE_SINGLE 意思为单选模式，代表只能选择一个图片，如果此时 setMaxCount>1 时，此配置不生效，当maxcount为1时，则开启单选模式，单选模式的定义为：选中时同时取消上一次选中，每次只能有一张被选中，类似与radiogroup。
setSinglePickWithAutoComplete的含义是，当选择item是单选情况下，且setSinglePickWithAutoComplete=true时，默认不显示chekbox，和标题栏的完成按钮，使用场景类似于单图剪裁，用户点击item即直接回调数据，关闭选择器。
如果我希望，视频单选时，点击直接回调出去，那么只需要同时开启 setVideoSinglePick 和 setSinglePickWithAutoComplete 即可。如果视频item需要跳转到自定义的视频编辑页面，则在presenter的interceptItemClick方法里判断当前item如果是视频，返回true。

## 9.视频样式需要多选，也不需要选中框，如何直接点击视频item跳转到我自定义的视频编辑页面？
此场景类似于 [场景7](#7.如何点击完成跳转到我项目里的某个编辑页面，返回时不关闭选择器继续选择，编辑页面完成编辑时，需要关闭掉选择器并跳转到项目里发布页面).区别在于拦截的对象不同，此场景只需要拦截视频对象即可，流程如下：

启动选择器时代码：
```java
ImagePicker.withMulti(presenter)//指定presenter
            //...
            .setVideoSinglePick(true)//设置视频单选
            .setSinglePickWithAutoComplete(true)
            .pick(...)
```
presenter代码：
```java
@Override
public boolean interceptItemClick(@Nullable Activity activity, 
                                ImageItem imageItem,
                                ArrayList<ImageItem> selectImageList,
                                ArrayList<ImageItem> allSetImageList,
                                BaseSelectConfig selectConfig, 
                                PickerItemAdapter adapter,
                                @Nullable IReloadExecutor reloadExecutor) {
    if (imageItem.isVideo()) {
        //跳转到自定义的视频编辑页面
        return true;
    }
    return false;
}
```
视频编辑页面：
```java
public void close(View view) {
     //imageItems为编辑后的图片列表
    ImagePicker.closePickerWithCallback(imageItems);
    finish();
}
```


## 10.filterMimeTypes方法如果mimetype是动态过滤怎么做？
filterMimeTypes支持传入null，此时代表无需过滤。

## 11.小红书剪裁的四种模式是什么意思？assignGapState什么意思？
小红书剪裁的四种模式分别对应为：剪裁区域充满，剪裁区域自适应，剪裁图片充满，剪裁图片留白
assignGapState为true时代表只有 剪裁图片充满，剪裁图片留白两种状态。

