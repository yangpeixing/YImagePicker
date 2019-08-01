# 欢迎使用YPXImagePicker

## 微信图片选择
支持视频、GIF、长图选择，支持单张多比例剪裁，支持多图预览、编辑、以及调序，支持直接拍照
```java
// An highlighted block
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
        .setCropRatio(1, 1)//设置剪裁比例                                          
        //调用多选                                                               
        .pick(this, new OnImagePickCompleteListener() {                      
            @Override                                                        
            public void onImagePickComplete(ArrayList<ImageItem> items) {    
                //处理回调回来的图片信息，主线程                                            
            }                                                                
        });                                                                  
```


## 小红书图片剪裁选择      
高仿小红书图片剪裁框架，支持视频以及多图剪裁



