# # JamBoBleSDK 使用说明
> 完全抛弃上一版，重写SDK，以下使用 Kotlin 调用 SDK

# 导入 SDK
https://github.com/chinnsenn/JamBoBleSDK/releases

由于本地导入 aar 无法传递包中的远程依赖，所以请在主程依赖以下远程库:
``` java
implementation 'androidx.localbroadcastmanager:localbroadcastmanager:1.0.0'
```
如遇见其他 SDK 引起的 ClassNotFound 错误，请根据日志判断。
# 初始化 SDK
在 Application 或者使用之前调用 

```Java
JamBoBleHelper.instance.init(this) 
```

为了不持有 context ，引起各种未知的问题，库中不再处理权限请求。使用前先自行动态请求权限。
代码请可以参考这里 [MainActivity](https://github.com/chinnsenn/JamBoBleSDK/blob/sdk/app/src/main/java/com/jianbao/jamboblesdk/MainActivity.kt)

> 使用蓝牙功能，必须先声明蓝牙相关的权限。Android 6.0以上的系统，需要额外申请位置相关的权限，并且是危险权限建议在运行时动态获取。为使使用更灵活，FastBle库中并不包含权限相关的操作，使用者根据程序的实际情况在外层自行嵌套。示例代码中有相关代码演示，供参考。

## 体脂测量
```java
//因为体脂测量用到其他 SDK，必须传入用户数据
JamBoBleHelper.instance.updateQnUser(QnUser("1", "male", 180, Date()))

//体脂测量支持实时返回体重数据
JamBoBleHelper.instance.setUnSteadyValueCallBack(
            object : UnSteadyValueCallBack {
                override fun onUnsteadyValue(value: Float) {
                    mTvValueRealtime.text = "$value kg"
                }
            }
        )
//调用扫描代码
JamBoBleHelper.instance.scanFatScaleDevice()

```
长度原因，其余代码参考 [WeightActivity](https://github.com/chinnsenn/JamBoBleSDK/blob/sdk/app/src/main/java/com/jianbao/jamboblesdk/WeightActivity.kt)
## 血压、血糖、尿酸测量（三者大同小异）
```java
//血压
JamBoBleHelper.instance.scanBloodPressureDevice()
//血糖
JamBoBleHelper.instance.scanBloodSugarDevice()
//尿酸
JamBoBleHelper.instance.scanUricAcidDevice()
```
其余代码参考 [BloodActivity](https://github.com/chinnsenn/JamBoBleSDK/blob/sdk/app/src/main/java/com/jianbao/jamboblesdk/BloodActivity.kt)
## 血液三合一设备
```java
JamBoBleHelper.instance.scanThreeOnOneDevice()
```
其余代码参考 [BloodThreeOnOneActivity](https://github.com/chinnsenn/JamBoBleSDK/blob/sdk/app/src/main/java/com/jianbao/jamboblesdk/BloodThreeOnOneActivity.kt)
## 血氧测量
```java
//血氧测量
JamBoBleHelper.instance.scanOxiMeterDevice()
```
血氧测量提供类心电图绘制，代码参考 [OxiMeterActivity](https://github.com/chinnsenn/JamBoBleSDK/blob/sdk/app/src/main/java/com/jianbao/jamboblesdk/OxiMeterActivity.kt)

## 释放资源
```java
JamBoBleHelper.instance.destroy()
```

## What's Next
- 其余蓝牙设备下一版支持
- 支持配置不自动连接设备，返回蓝牙设备列表，手动连接
...
