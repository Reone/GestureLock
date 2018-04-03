# GestureLock
[![](https://jitpack.io/v/Reone/GestureLock.svg)](https://jitpack.io/#Reone/GestureLock)

九宫格手势锁View，Android phone pattern lock screen

参考 感谢: [jlertele/GestureLibray](https://github.com/jlertele/GestureLibray)

- 这是一个九宫格解锁的库
- 提供一个View -- LockView
- 支持通过xml设置颜色，大小等等属性
- 支持自定义图案，默认的有Style.NORMAL,Style.RECT，也可以完全自定义，demo中有例子
- 提供LockView的逻辑管理接口，也就是View的事件监听 -- ProcessManager
    通过这个接口你可以完成解锁的大部分需求,具体接口如下
```java
public abstract class ProcessManager {

    private LockView lockView = null;

    /**
     * 开始输入
     * @return true则允许输入，false不允许输入
     */
    public abstract boolean onInputStart();

    /**
     * 当一个新的点选中的时候调用
     * @param point
     */
    public abstract void pointAttach(@NonNull Integer point);

    /**
     * 输入完成
     * @param points
     */
    public abstract void onInputEnd(@NonNull List<Integer> points);

    /**
     * 某些变量的初始化需要用到lockView可以在这个方法中进行
     */
    public abstract void lockViewAttach();

    protected @NonNull LockView getLockView() {
        return lockView;
    }

    public void setLockView(@NonNull LockView lockView) {
        this.lockView = lockView;
        lockViewAttach();
    }
}
```

- 提供一个常用的逻辑管理类 -- SimpleProcessManager
- 提供了一些小东西，Base64.java、PasswordCache.java、VibratorUtil

### 引用
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

### 使用
1. 添加引用
2. 在布局中添加LockView
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
3. 创建逻辑管理类
```java
	class SimpleProcessManager extends ProcessManager
```
4. 设置自定义管理类
```java
	lockView = findViewById(R.id.lockView);
	lockView.setProcessManager(new SimpleProcessManager());
```
注意:声明时需要添加泛型
```java
	LockView<SimpleProcessManager> lockView;
```
5. 使用自定义管理类方法
```java
	lockView.getProcessManager().setMode(SimpleProcessManager.SETTING_PASSWORD);
```

![图例](https://static.oschina.net/uploads/img/201802/27181901_2mOT.png)
