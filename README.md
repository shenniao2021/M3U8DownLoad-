# M3U8DownLoad-
自用的两个网址 只针对了这两个网址编写的视频下载 网站主要是ASMR视频

https://www.aisasmr.com/ 这个网站容易403 而且部分视频需要登录 登录是PHP不懂 所以需要的自己去注册账号拿cookie填进去 
https://www.gqtod.com/ 在输入框输入网址就行了 有播放器的网页就是了 ctrl+v自动补齐 ;  是以 ; 隔开每个网址的

直接用的话下载 libs文件夹的东西jar后缀那个文件和properties后缀  libs名字不能改  文件结构也不能改

properties用记事本打开就行 cookie只有第一个网站部分视频用得到 如果你不需要可以不填 outPath是默认文件存放路径 为了省事加的 
注意: properties里最好不要出现空格

转码要用到ffmpeg 考虑不是所有人都有就变成可选了 默认是开启  会转成MP4 不开就是TS 也可以去下个软件转 微软自带的播放器和爱奇艺 风暴之类的都支持TS播放

message这个文件存会放转码时的一些信息 大部分情况下没什么用 现在只是加了一点下载失败的地址 到时候直接在记事本里搜http就行了  
