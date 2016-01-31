# BasePedo
android 计步器
####这是一个通过android手机来模拟计步器的软件，市面上开源的计步代码不多，有些计步误差较大，我希望可以做一款计步误差小，稳定性高的计步软件，但是请大家帮我一起来完善。
------
     
v1.1（2016-01-31）
* 为Service开启一个独立的进程进行计步，并通过messenger进行进程间传输。
* 开启Notification，将Service变为前台应用，并在Androidmanifest.xml文
   件中对Service追加action，进而提高Service的存活率。
* 添加数据库记录与更新数据。
* 添加广播事件，监听锁屏、关屏、关机等并进行相应的保存数据到数据库。
    
v1.0（2016-01-06）
* 计步功能：暂时只测试了放在裤子口袋里计步，百步误差在10步内。
* 屏蔽功能：连续运动一定时间才开始计步，屏蔽细微移动或者驾车时震动所带来的干扰。
* 停止运动超过5秒，便重新开启屏蔽功能。
  

### DownLoad：
#####[DEMO APK](https://github.com/xfmax/BasePedo/raw/master/sample/basepedo.apk)

如果你有任何问题或者想交个朋友，可以通过邮件联系我:
xufengbase@gmail.com


