git-osc-android-project
===========
# **Git@OSC Android 客户端项目简析** #

*注：本文假设你已经有Android开发环境*

本项目是采用了Google官方提供的appcompat_v7 lib项目来解决低版本的兼容性问题；<br>
所以请确保你的Eclipse ADT为22.6.3以上，并且Android Support Library为19.0以上<br>

## **项目的搭建步骤** ##

**1、clone相关的项目**<br>

* clone 主项目git-osc-android-project：
> git clone https://git.oschina.net/oschina/git-osc-android-project.git

* clone 友盟的分享项目social_sdk_library_project：
> git clone https://git.oschina.net/fireant/social_sdk_library_project.git

**2、启动eclipse，导入相关项目**<br>

请确保你当前的Android SDK是最新版。<br>
推荐使用Android 4.0 以上版本的SDK,请使用JDK1.7编译<br>

* 导入git-osc-android-project项目；

* 导入social_sdk_library_project项目；

* 将social_sdk_library_project作为lib引入到git-osc-android-project


**本项目采用 GPL 授权协议，欢迎大家在这个基础上进行改进，并与大家分享。**