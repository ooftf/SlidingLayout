# SlidingLayout
[ ![Download](https://api.bintray.com/packages/ooftf/maven/sliding-layout/images/download.svg) ](https://bintray.com/ooftf/maven/sliding-layout/_latestVersion)
## 介绍
一个可以使控件具有关闭打开功能的layout，目前已测试支持基本View类型和RecycleView
## 效果图
![](https://github.com/ooftf/SlidingLayout/raw/master/art/demo.gif)
# 使用方式
## Gradle配置
```groovy
dependencies {
    implementation 'com.ooftf:sliding-layout:1.1.1'
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
|---|---|
|smoothOpen|变为打开状态  |
|smoothClose|变为关闭状态 |
|smoothTurn| 改变状态 |
|isOpen|是否是打开状态 |