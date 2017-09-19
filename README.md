# ORMProvider
## 一、介绍说明
这是一个利用运行时注解对Android标准库中的ContentProvider和ContentResolver进行封装实现ORM的类库。
你可以通过SQLiteProvider和SQLiteBuilder配合一些注解就可以轻松完成ContentProvider的数据库创建、数据维护，分享功能。

## 二、远程依赖
首先， 在工程根目录下的build.gradle中添加jitpack仓库地址:
<pre>
allprojects {
	repositories {
		...
		maven { url 'https://www.jitpack.io' }
	}
}
</pre>

然后，在目标模块目录下的build.gradle中添加类库依赖：
<pre>
dependencies {
        compile 'com.github.oxsource:ORMProvider:1.1.0'
}
</pre>

## 三、Provider端使用示例

## 四、Resolver端使用示例
