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


# 体脂称数据参考

## 体重
```
float userHeight = 用户身高(单位：米)

身高的平方 x BMI 三段指标

float bmiStandar1 = (float) (Math.pow(userHeight, 2) * 18.5f);
float bmiStandar2 = (float) (Math.pow(userHeight, 2) * 24.0f);
float bmiStandar3 = (float) (Math.pow(userHeight, 2) * 28.0f);

if (weightValue < bmiStandar1) {
    "偏瘦"
} else if (weightValue < bmiStandar2) {
    "标准"
} else if (weightValue < bmiStandar3) {
    "超重"
} else {
    "肥胖"
}
```

### BMI

```
18.5, 24.0, 28.0

int index = 0;
if (bmiValue < 18.5) {
    "偏瘦"
} else if (bmiValue < 24) {
    "标准"
} else if (bmiValue < 28) {
    "超重"
} else {
    "肥胖"
}
```

## 内脏脂肪等级
```
if (visceral <= 9){
	"标准"
} else if (visceral <= 14){
    "偏高"
} else{
    "严重偏高"
}
```
## 皮下脂肪率(单位 %)

### 男士(8.6%, 16.7%)
```
if (skinPercent < 8.6){
	"偏低"
}else if (skinPercent <= 16.7){
	"标准"
}else{
	"偏高"
}
```

### 女士(18.5%,26.7%)
```
if (skinPercent < 18.5){
	"偏低"
}else if (skinPercent <= 26.7){
	"标准"
}else{
	"偏高"
}
```

## 骨骼肌率(单位 %)

### 男士(49.0%, 59.0%)
```
if (skeletalRate < 49){
	"偏低"
}else if (skeletalRate <= 59){
	"标准"
}else{
	"偏高"
}
```

### 女士(40.0%, 50.0%)
```
if (skeletalRate < 40){
	"偏低"
}else if (skeletalRate <= 50){
	"标准"
}else{
	"偏高"
}
```
## 蛋白质(单位 %)

### 男士(16.0%, 18.0%)
```
if (proteinsValue < 16){
	"偏低"
}else if (proteinsValue <= 18){
	"标准"
}else{
	"充足"
}
```

### 女士(14.0%, 16.0%)
```
if (proteinsValue < 14){
	"偏低"
}else if (proteinsValue <= 16){
	"标准"
}else{
	"充足"
}
```

## 水分(单位 %)

### 男士(55.0%, 65.0%)
```
if (waterPercent < 55){
	"偏低"
}else if (waterPercent <= 65){
	"标准"
}else{
	"充足"
}
```

### 女士(45.0%, 60.0%)
```
if (waterPercent < 45){
	"偏低"
}else if (waterPercent <= 60){
	"标准"
}else{
	"充足"
}
```

## 脂肪率
### 男士(11.0%, 21.0%, 26.0%)
```
if (fatPercent < 11){
	"偏低"
}else if (fatPercent <= 21){
	"标准"
}}else if (fatPercent <= 26){
	"偏高"
}else{
	"严重偏高"
}
```

### 女士(21.0%, 31.0%, 36.0%)
```
if (fatPercent < 21){
	"偏低"
}else if (fatPercent <= 31){
	"标准"
}}else if (fatPercent <= 36){
	"偏高"
}else{
	"严重偏高"
}
```

## 骨量
### 男士
```
根据体重，范围有变化

float indicator1 = 0; 低到标准指标
float indicator2 = 0; 标准到高指标
//weightValue 体重
if (weightValue <= 60){
	indicator1 = (float) (2.5 - 0.2);
	indicator2 = (float) (2.5 + 0.2);
}else if (weightValue < 75){
	indicator1 = (float) (2.9 - 0.2);
	indicator2 = (float) (2.9 + 0.2);
}else{
	indicator1 = (float) (3.2 - 0.1);
	indicator2 = (float) (3.2 + 0.1);
}
```
### 女士
```
if (weightValue <= 45){
	indicator1 = (float) (1.8 - 0.2);
	indicator2 = (float) (1.8 + 0.2);
}else if (weightValue < 60){
	indicator1 = (float) (2.2 - 0.2);
	indicator2 = (float) (2.2 + 0.2);
}else{
	indicator1 = (float) (2.5 - 0.1);
	indicator2 = (float) (2.5 + 0.1);
}
```
```
//boneValue 骨量
if (boneValue < indicator1){
    "偏低"
}else if (boneValue <= indicator2){
    "标准"
}else{
    "偏高"
}
```

## 去脂体重

计算公式:
`去脂体重 = 体重 x (1 - 脂肪率)`

## 基础代谢率
无