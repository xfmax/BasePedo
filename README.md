# BasePedo

#### android 计步器（想尝鲜的小伙伴可以选择develop分支哦，代码更迭快，不能保证稳定，大家一起来挑bug么！ ）
#### 这是一个通过android手机来模拟计步器的软件，市面上开源的计步代码不多，有些计步误差较大，我希望可以做一款计步误差小，稳定性高的计步软件，希望大家和我一起来完善。
------
     
注：本软件只适用于走路，跑步的话，还在研究中。。。

v1.4（2020-05-23）
* master分支与develop分支的代码改为kotlin书写，如果想看java版本的童鞋可以查看java_version分支。
* 删除守护进程库，现正在寻觅新的守护后台的解决方案。
 

v1.3（2016-08-16）
* 加入跨天清零功能。
* 优化代码。
 

v1.2（2016-02-29）
* 加入android系统提供的计步算法，根据系统支持情况自动进行选择。
* 调整计步算法和计步精度。
 
v1.1（2016-01-31）
* 为Service开启一个独立的进程进行计步，并通过messenger进行进程间传输。
* 开启Notification，将Service变为前台应用，并在Androidmanifest.xml文
   件中对Service追加action，进而提高Service的存活率。
* 添加数据库记录与更新数据。
* 添加广播事件，监听锁屏、关屏、关机等事件，并进行保存数据的相关操作。
* 调整计步精度。
    
v1.0（2016-01-06）
* 计步功能：暂时只测试了放在裤子口袋里计步。
* 屏蔽功能：连续运动一定时间才开始计步，屏蔽细微移动或者驾车时震动所带来的干扰。
* 停止运动超过5秒，便重新开启屏蔽功能。
  

### DownLoad：
#####[DEMO APK](https://github.com/xfmax/BasePedo/raw/master/sample/basepedo.apk)

如果你有任何问题、想法或者想交个朋友，可以通过邮件联系我:
xufengbase@gmail.com，一般邮件我都是回的，除非我加班累成狗，或者离开地球，尽请见谅。


## License


    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.
