# 贝芽创想天地

![API](https://img.shields.io/badge/API-25-brightgreen.svg?style=flat)

统一签名文件 ： bearya_keystore.jks

## 版本说明：

###    v-X.Y.Z : X 主版本号 , Y 小版本号 , Z bug修复版本号
[版本更新升级说明](https://dev.git.bearya.com/Game/Programme/src/master/UPDATE.md)

## 跳跳镇

![跳跳镇](https://dev.git.bearya.com/Game/Programme/raw/master/city.png)

![Version](https://img.shields.io/badge/version-4.1.3-brightgreen.svg?style=flat)
![VersionCode](https://img.shields.io/badge/versionCode-40103-brightgreen.svg?style=flat)

- 应用包名 ：com.bearya.robot.programme

- 基础工程构建模块 ： include ':actionlib', ':city', ':city_base',':bearya_sdk'

- 创想的精选主题：可在module:city下的 'assets/theme/' 目录下继续按文件要求添加配置json文件内容和配套图片音频

## 童话世界

![童话世界](https://dev.git.bearya.com/Game/Programme/raw/master/fairy.png)

![Version](https://img.shields.io/badge/version-5.1.0-brightgreen.svg?style=flat)
![VersionCode](https://img.shields.io/badge/versionCode-50100-brightgreen.svg?style=flat)

- 应用包名 ：com.bearya.robot.fairystory

- 基础工程构建模块 ： include ':actionlib', ':fairystory', ':fairy_base'

### 说明

#### 道路配置信息 ----- 部分代码配置位于 com.bearya.robot.fairystory.walk.load 下

这里配备了障碍地垫，终点地垫，道具装备地垫，禁止通行地垫 , 动画内容和配音内容可以在这里配置。

#### 页面的Activity ----- 现在均配置在 com.bearya.robot.fairystory.ui 下

##### 1. 应用启动页 ： 启动页面 ， 点点屏幕
```
LauncherActivity
```

- 在这里配置

```
    launcherData.jumpToActivity = ThemesActivity.class; // 点击屏幕后跳转的界面
    launcherData.bg = R.mipmap.bg_splash; // 启动页面的背景图片
    launcherData.bgMp3 = "touch.mp3"; // 点点屏幕开始游戏吧的配音
    launcherData.tipMp3 = "bgm.mp3"; // 背景音乐
```

##### 2. 主题选择页 ： 主题乐园,在这里可以选择不同的关卡进行游戏
```
ThemesActivity
```

- 页面的配置已经部分提取至
- [x] [ThemeConfig](https://dev.git.bearya.com/Game/Programme/src/master/fairystory/src/main/java/com/bearya/robot/fairystory/ui/res/ThemeConfig.java)

##### 3. 场景介绍动画 ： 起始背景介绍
```
ThemeIntroduceActivity
```

- 页面的配置已经部分提取至 
- [x] [IntroduceAudio](https://dev.git.bearya.com/Game/Programme/src/master/fairystory/src/main/java/com/bearya/robot/fairystory/ui/res/IntroduceAudio.java)
- [x] [IntroduceImageArrays](https://dev.git.bearya.com/Game/Programme/src/master/fairystory/src/main/java/com/bearya/robot/fairystory/ui/res/IntroduceImageArrays.java)
- [x] [IntroduceTime](https://dev.git.bearya.com/Game/Programme/src/master/fairystory/src/main/java/com/bearya/robot/fairystory/ui/res/IntroduceTime.java)

##### 4. 行动指令 添加 / 刷卡，附加并行卡或者道具卡
```
CardControllerActivity
```

##### 5. 行动指令卡片点击修改,包含了动画过程
```
UpdateCardControllerActivity
```

##### 6. 指令执行运行状态，执行结果
```
RuntimeActivity
```

在这里，我们会处理最终的卡片指令，转换成运行时指令 ， 并在这里处理最终的结果

#### 配置信息 ----- 部分代码配置位于 com.bearya.robot.fairystory.ui.res 下

##### 7. 主题场景动画的数组
主题选择的图片可以在这里修改
```
ThemeImages
```

##### 8. 开始介绍动画 / 游戏结束动画 播放的帧动画时间间隔
```
IntroduceTime
```

##### 8. 开始介绍动画 / 2.结束结果动画（收集完成 / 收集未完成）
```
IntroduceImageArrays
```

##### 9. 场景介绍音频资源
```
IntroduceAudio
```

##### 10. 这是卡片指令的资源
```
CardType
```

##### 11. 小朋友刷卡/选卡，所有卡片的数据集合动作

这里还有包含对应的一些指令图片 ， 指令的配音
```
CardResource
```

##### 12. 主题标志配置
```
ThemeConfig
```