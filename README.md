# GestureLock
[![](https://jitpack.io/v/Reone/GestureLock.svg)](https://jitpack.io/#Reone/GestureLock)

九宫格手势锁View，Android phone pattern lock screen

参考 感谢: [jlertele/GestureLibray](https://github.com/jlertele/GestureLibray)

- 引用
```
allprojects {
	repositories {
		...
		maven { url 'https://jitpack.io' }
	}
}
dependencies {
	implementation 'com.github.Reone:GestureLock:$version_code'
}
```

- 使用
1.添加引用

2.在布局中添加LockView
```xml
<?xml version="1.0" encoding="utf-8"?>
<FrameLayout xmlns:android="http://schemas.android.com/apk/res/android"
    android:layout_width="match_parent" android:layout_height="match_parent"
    android:background="#1592f1"
    xmlns:app="http://schemas.android.com/apk/res-auto">
    <com.reone.gesturelibrary.view.LockView
        android:id="@+id/lockView"
        android:layout_width="300dp"
        android:layout_height="300dp"
        android:layout_gravity="center"
        android:background="#1592f1"
        app:color_error_ring="#c62828"
        app:color_on_ring="#ffffff"
        app:color_up_ring="#ffffff"
        app:color_on_background="#1592f1"
        app:inner_background_width="19dp"
        app:inner_ring_width="14dp"
        app:outer_ring_spacing_width="41dp"
        app:line_width="3dp"
        app:stroke_width="3dp"
        app:no_finger_stroke_width="3dp"/>
</FrameLayout>
```
3.创建逻辑管理类
```java
	class SimpleProcessManager extends ProcessManager
```
4.设置自定义管理类
```java
	lockView = findViewById(R.id.lockView);
	lockView.setProcessManager(new SimpleProcessManager());
```
注意:声明时需要添加泛型
```java
	LockView<SimpleProcessManager> lockView;
```
5.使用自定义管理类方法
```java
	lockView.getProcessManager().setMode(SimpleProcessManager.SETTING_PASSWORD);
```

![图例](https://static.oschina.net/uploads/img/201802/27181901_2mOT.png)
