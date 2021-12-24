# JamBoBleSDK 使用说明

# 导入 SDK
https://github.com/chinnsenn/JamBoBleSDK/releases 下载最新版本
将 aar 文件放入 ../app/libs 文件夹下,并在 build.gradle
```java
implementation files('libs/jamboble_[lastest_version].aar')
```
# 初始化 SDK
在 Application 或者使用之前调用 

```Java
JamBoHelper.getInstance().init(this) 
//开启 Log
JamBoHelper.getInstance().enableDebug(true);
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