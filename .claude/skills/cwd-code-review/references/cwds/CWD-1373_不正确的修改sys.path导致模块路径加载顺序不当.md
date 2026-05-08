# CWD-1373 不正确的修改sys.path导致模块路径加载顺序不当

**描述**
sys.path是Python在执行import语句时搜索模块使用的路径列表，由当前目录、系统环境变量、库目录、.pth文件配置组合拼装而成，直接修改之后将作用于当次Python进程运行生命周期内所有import语句。原则上sys.path只应该根据用户的系统配置来生成，默认的sys.path应该就可以支持模块的搜索过程，不应该在代码里面直接修改。如果确实需要修改，则需要注意尽量不要影响其它模块搜索sys.path的顺序，否则可能导致某些import语句因为搜索到了错误的模块，导入不了相应的符号而出现ImportError异常。

在修改sys.path时注意避免模块重名导致的导入失败，建议使用sys.path.append代替sys.path.insert(0, my_directory)。

**语言: **PYTHON

**严重等级**
提示

**cleancode特征**
可靠

#### CWD-1373-000 不正确的修改sys.path导致模块路径加载顺序不当

---

