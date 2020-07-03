# JamBoBleSDK 使用说明

# 导入 SDK
https://github.com/chinnsenn/JamBoBleSDK/releases 下载最新版本
将 aar 文件放入 ../app/libs 文件夹下,并在 build.gradle
```java
implementation files('libs/jamboble_1.3.aar')
```
# 初始化 SDK
在 Application 或者使用之前调用 

```Java
JamBoHelper.getInstance().init(this) 
```

为了不持有 context ，引起各种未知的问题，库中不再处理权限请求。使用前先自行动态请求权限。
代码请可以参考这里 [MainActivity](https://github.com/chinnsenn/JamBoBleSDK/blob/sdk/app/src/main/java/com/jianbao/jamboblesdk/MainActivity.java)

> 使用蓝牙功能，必须先声明蓝牙相关的权限。Android 6.0以上的系统，需要额外申请位置相关的权限，并且是危险权限建议在运行时动态获取。为使使用更灵活，FastBle库中并不包含权限相关的操作，使用者根据程序的实际情况在外层自行嵌套。示例代码中有相关代码演示，供参考。 

# 1.3版本 更新胎心检测功能
### 扫描胎心设备
```java
FetalHeartHelper.getInstance().scanFetalHeartDevice();
```
### 扫描设备回调
```java
//扫描胎心设备不会自动连接，会返回扫描到的符合条件的设备
FetalHeartHelper.getInstance().setFetalHeartBleCallback(new FetalHeartBleCallback() {
            //扫描完成后返回所有胎心设备列表
            @Override
            public void onBTDeviceFound(List<BleDevice> list) {
            
            }

            @Override
            //扫描中实时返回扫描到的胎心设备
            public void onBTDeviceScanning(BleDevice device) {
                mPbLoading.setVisibility(View.GONE);
                devicesAdapter.addDevice(device);
            }
//省略其余代码...
}
```
### 连接设备
```java
FetalHeartHelper.getInstance().connectDevice(device);
```
### 断开连接
```java
FetalHeartHelper.getInstance().disconnect();
```
### 录音
```java
//返回音频存放路径
String path = FetalHeartHelper.getInstance().startRecord();
```
### 停止录音
```java
FetalHeartHelper.getInstance().finishRecord();
```
更多代码详见 [FetalHeartActivity](https://github.com/chinnsenn/JamBoBleSDK/blob/sdk/app/src/main/java/com/jianbao/jamboblesdk/FetalHeartActivity.java)
# 体脂测量
```java
//因为体脂测量用到其他 SDK，必须传入用户数据
JamBoHelper.getInstance().updateQnUser(new QnUser("1", "male", 180, new Date()));

//体脂测量支持实时返回体重数据
JamBoHelper.getInstance().setUnSteadyValueCallBack(new UnSteadyValueCallBack() {
            @Override
            public void onUnsteadyValue(float value) {
                mTvValueRealtime.setText(value + "kg");
            }
        });
//调用扫描代码
JamBoHelper.getInstance().scanFatScaleDevice();
//停止扫描
JamBoHelper.getInstance().stopScan();
```
长度原因，其余代码参考 [WeightActivity](https://github.com/chinnsenn/JamBoBleSDK/blob/sdk/app/src/main/java/com/jianbao/jamboblesdk/WeightActivity.java)
# 血压、血糖、尿酸测量（三者大同小异）
```java
//血压
JamBoHelper.getInstance().scanBloodPressureDevice()
//血糖
JamBoHelper.getInstance().scanBloodSugarDevice()
//尿酸
JamBoHelper.getInstance().scanUricAcidDevice()
```
其余代码参考 [BloodActivity](https://github.com/chinnsenn/JamBoBleSDK/blob/sdk/app/src/main/java/com/jianbao/jamboblesdk/BloodActivity.java)
# 血液三合一设备
```java
JamBoHelper.getInstance().scanThreeOnOneDevice()
```
其余代码参考 [BloodThreeOnOneActivity](https://github.com/chinnsenn/JamBoBleSDK/blob/sdk/app/src/main/java/com/jianbao/jamboblesdk/BloodThreeOnOneActivity.java)
# 血氧测量
```java
//血氧测量
JamBoHelper.getInstance().scanOxiMeterDevice()
```
血氧测量提供类心电图绘制，代码参考 [OxiMeterActivity](https://github.com/chinnsenn/JamBoBleSDK/blob/sdk/app/src/main/java/com/jianbao/jamboblesdk/OxiMeterActivity.java)
# 释放资源
```java
JamBoHelper.getInstance().destroy()
```
# What's Next
- 支持配置不自动连接设备，手动连接
...