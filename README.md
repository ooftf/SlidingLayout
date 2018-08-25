# SlidingLayout
一个可以推拉控件
[![](https://jitpack.io/v/ooftf/SlidingLayout.svg)](https://jitpack.io/#ooftf/SlidingLayout)

# 使用方式
## groovy
```groovy
allprojects {
    repositories {
        maven { url 'https://jitpack.io' }
    }
}
dependencies {
    implementation 'io.reactivex.rxjava2:rxandroid:2.1.0'
    implementation 'com.github.ooftf:SlidingLayout:1.0.0'
}
```
## xml
```xml
    <com.ooftf.sliding.SlidingLayout
        android:id="@+id/sliding"
        android:layout_width="match_parent"
        android:layout_height="20dp">
        <TextView
            android:layout_width="match_parent"
            android:layout_height="wrap_content"
            android:text="@string/longtext" />
    </com.ooftf.sliding.SlidingLayout>
```
SlidingLayout的layout_height是关闭状态时的高度
## SlidingLayout 方法
|方法名|描述|
|---|---|---|
|smoothOpen|变为打开状态  |
|smoothClose|变为关闭状态 |
|smoothTurn| 改变状态 |
|isOpen|是否是打开状态 |