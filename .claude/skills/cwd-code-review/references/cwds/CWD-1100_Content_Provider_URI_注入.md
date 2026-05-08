# CWD-1100 Content Provider URI 注入

**描述**
Content Provider 作为 Android 的四大组件之一，常常用来为不同的应用之间数据共享提供统一的接口。在Android系统中各应用的数据是对外隔离的，如果想要访问其它应用的部分数据就需要 ContentProvider，例如应用访问联系人这个功能就用到了 ContentProvider。ContentProvider 的使用类似于数据库的操作，它拥有增删改查的操作，同样的 ContentProvider也具有被注入的风险。构建含有用户输入的 ContentProvider 查询指令会让攻击者能够访问未经授权的内容或者动态构造一个 ContentProvider 查询 URI，造成字符串查询注入。

**语言: **JAVA

**严重等级**
一般

**cleancode特征**
安全,可靠

**示例**
**案例1: ContentProvider URI未验证导致数据泄露或篡改漏洞**
**语言: **JAVA

**描述**
在Android开发中，ContentProvider是应用间数据共享的重要机制。当开发者未对传入的URI参数进行充分验证时，攻击者可能通过构造恶意URI来访问或修改应用的非公开数据，导致数据泄露或数据篡改。这种漏洞被称为ContentProvider URI注入漏洞。

**案例分析**
- 直接使用未经验证的URI参数：代码直接从URI中获取最后一个路径段作为表名，没有进行任何验证或过滤。
- SQL注入风险：攻击者可以构造类似`content://com.example.vulnerable.provider/data/secret_table`的URI来访问未授权的表。
- 数据泄露风险：如果`ContentProvider`未设置适当的权限，任何应用都可以查询这些数据。
- 权限绕过风险：即使设置了权限，如果URI处理不当，也可能绕过权限检查。

**反例**
```java
public class VulnerableProvider extends ContentProvider {
    private static final String AUTHORITY = "com.example.vulnerable.provider";
    private static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY + "/data");
    
    private SQLiteDatabase mDatabase;

    @Override
    public Cursor query(Uri uri, String[] projection, String selection,
                        String[] selectionArgs, String sortOrder) {
        // 危险：直接使用传入的URI构建SQL查询
        String tableName = uri.getLastPathSegment();
        
        // 危险：直接将URI参数拼接到SQL查询中
        return mDatabase.query(tableName, projection, selection, 
                             selectionArgs, null, null, sortOrder);
    }

    // 其他ContentProvider方法实现...
}
```

**正例**
```java
public class SecureProvider extends ContentProvider {
    private static final String AUTHORITY = "com.example.secure.provider";
    private static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY + "/data");
    
    // 定义允许访问的表名白名单
    private static final Set<String> ALLOWED_TABLES = new HashSet<String>() {{
        add("public_data");
        add("user_data");
    }};
    
    private SQLiteDatabase mDatabase;

    @Override
    public Cursor query(Uri uri, String[] projection, String selection,
                        String[] selectionArgs, String sortOrder) {
        // 验证URI格式
        if (!AUTHORITY.equals(uri.getAuthority())) {
            throw new IllegalArgumentException("Unknown URI: " + uri);
        }
        
        // 获取并验证表名
        String tableName = uri.getLastPathSegment();
        if (!ALLOWED_TABLES.contains(tableName)) {
            throw new SecurityException("Access to table " + tableName + " is not allowed");
        }
        
        // 使用参数化查询
        return mDatabase.query(tableName, projection, selection, 
                             selectionArgs, null, null, sortOrder);
    }

    // 其他ContentProvider方法实现...

    @Override
    public void onCreate() {
        // 初始化数据库
        // 设置适当的权限
        // 可以添加以下权限声明到AndroidManifest.xml
        // <provider ... android:readPermission="com.example.secure.permission.READ_DATA" />
    }
}
```

**修复建议**
- URI验证：检查传入URI的`authority`是否匹配预期的值。
- 表名白名单：只允许访问预定义的表名集合，拒绝其他所有请求。
- 权限控制：在`AndroidManifest.xml`中为`ContentProvider`设置适当的读写权限。
- 参数化查询：虽然此处`SQLiteDatabase.query`方法本身是安全的，但仍需确保所有数据库操作都使用参数化查询。

#### CWD-1100-000 Content Provider URI 注入

#### CWD-1100-001 直接拼接用户输入构造URI

#### CWD-1100-002 未验证URI路径

#### CWD-1100-003 使用动态查询参数

#### CWD-1100-004 未限制URI权限

#### CWD-1100-005 使用反射或动态类加载

---

