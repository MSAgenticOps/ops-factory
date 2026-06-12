# 运营商服务与软件 Java 最佳实践代码审查规则
> 本文件由一个公司 电子表格 规则集生成，并保留源文件中的全部规则；不要把它当作最小子集使用。
## 来源
- 来源 电子表格：`运营商服务与软件JAVA规则最佳实践(运营商服务与软件JAVA规则最佳实践集，SAI，unsafe_algorithm).xlsx`
- 来源目录：原始规则目录
- 提取规则数：429
- 问题级别分布：一般: 256, 严重: 110, 致命: 31, 提示: 30, 信息: 2
- 语言分布：JAVA: 429
- 规则类型分布：通用类（非编程规范）: 205, 安全类（非编程规范）: 97, 安全规范规则类: 57, 通用规范规则类: 38, 通用规范建议类: 19, 代码坏味道类: 7, 安全规范建议类: 6

## 代码审查使用方式
- 仅在审查触及对应语言、构建选项或安全面的变更时加载本规则集。
- 只有当变更差异 或必要的相邻代码中存在明确证据时，才输出规则违反项。
- 输出 合入请求审查问题时，需要包含来源规则名称和关联工具规则。
- `严重` 默认视为阻塞问题，除非已有明确例外说明；`一般` 默认合入前修复；`提示` 默认作为建议项，除非项目策略另有要求。

## 规则索引

| 序号 | 规则 | 级别 | 语言 | 工具规则 | 类型 |
|---|---|---|---|---|---|
| 1 | [AM_CREATES_EMPTY_JAR_FILE_ENTRY](#rule-1) | 一般 | JAVA | AM_CREATES_EMPTY_JAR_FILE_ENTRY | 通用类（非编程规范） |
| 2 | [AT_OPERATION_SEQUENCE_ON_CONCURRENT_ABSTRACTION](#rule-2) | 一般 | JAVA | AT_OPERATION_SEQUENCE_ON_CONCURRENT_ABSTRACTION | 通用类（非编程规范） |
| 3 | [BC_EQUALS_METHOD_SHOULD_WORK_FOR_ALL_OBJECTS](#rule-3) | 一般 | JAVA | BC_EQUALS_METHOD_SHOULD_WORK_FOR_ALL_OBJECTS | 通用类（非编程规范） |
| 4 | [BC_IMPOSSIBLE_CAST](#rule-4) | 一般 | JAVA | BC_IMPOSSIBLE_CAST | 通用类（非编程规范） |
| 5 | [BC_IMPOSSIBLE_DOWNCAST](#rule-5) | 一般 | JAVA | BC_IMPOSSIBLE_DOWNCAST | 通用类（非编程规范） |
| 6 | [BC_IMPOSSIBLE_DOWNCAST_OF_TOARRAY](#rule-6) | 一般 | JAVA | BC_IMPOSSIBLE_DOWNCAST_OF_TOARRAY | 通用类（非编程规范） |
| 7 | [BC_IMPOSSIBLE_INSTANCEOF](#rule-7) | 一般 | JAVA | BC_IMPOSSIBLE_INSTANCEOF | 通用类（非编程规范） |
| 8 | [BIT_ADD_OF_SIGNED_BYTE](#rule-8) | 一般 | JAVA | BIT_ADD_OF_SIGNED_BYTE | 通用类（非编程规范） |
| 9 | [BIT_IOR_OF_SIGNED_BYTE](#rule-9) | 一般 | JAVA | BIT_IOR_OF_SIGNED_BYTE | 通用类（非编程规范） |
| 10 | [BIT_SIGNED_CHECK](#rule-10) | 一般 | JAVA | BIT_SIGNED_CHECK | 通用类（非编程规范） |
| 11 | [BIT_SIGNED_CHECK_HIGH_BIT](#rule-11) | 一般 | JAVA | BIT_SIGNED_CHECK_HIGH_BIT | 通用类（非编程规范） |
| 12 | [BSHIFT_WRONG_ADD_PRIORITY](#rule-12) | 一般 | JAVA | BSHIFT_WRONG_ADD_PRIORITY | 通用类（非编程规范） |
| 13 | [BX_BOXING_IMMEDIATELY_UNBOXED](#rule-13) | 一般 | JAVA | BX_BOXING_IMMEDIATELY_UNBOXED | 通用类（非编程规范） |
| 14 | [BX_BOXING_IMMEDIATELY_UNBOXED_TO_PERFORM_COERCION](#rule-14) | 一般 | JAVA | BX_BOXING_IMMEDIATELY_UNBOXED_TO_PERFORM_COERCION | 通用类（非编程规范） |
| 15 | [BX_UNBOXING_IMMEDIATELY_REBOXED](#rule-15) | 一般 | JAVA | BX_UNBOXING_IMMEDIATELY_REBOXED | 通用类（非编程规范） |
| 16 | [CN_IDIOM](#rule-16) | 一般 | JAVA | CN_IDIOM | 通用类（非编程规范） |
| 17 | [CN_IDIOM_NO_SUPER_CALL](#rule-17) | 一般 | JAVA | CN_IDIOM_NO_SUPER_CALL | 通用类（非编程规范） |
| 18 | [CN_IMPLEMENTS_CLONE_BUT_NOT_CLONEABLE](#rule-18) | 一般 | JAVA | CN_IMPLEMENTS_CLONE_BUT_NOT_CLONEABLE | 通用类（非编程规范） |
| 19 | [CO_COMPARETO_INCORRECT_FLOATING](#rule-19) | 一般 | JAVA | CO_COMPARETO_INCORRECT_FLOATING | 通用类（非编程规范） |
| 20 | [DC_PARTIALLY_CONSTRUCTED](#rule-20) | 一般 | JAVA | DC_PARTIALLY_CONSTRUCTED | 通用类（非编程规范） |
| 21 | [DE_MIGHT_IGNORE](#rule-21) | 一般 | JAVA | DE_MIGHT_IGNORE | 通用类（非编程规范） |
| 22 | [DLS_DEAD_LOCAL_INCREMENT_IN_RETURN](#rule-22) | 一般 | JAVA | DLS_DEAD_LOCAL_INCREMENT_IN_RETURN | 通用类（非编程规范） |
| 23 | [DLS_OVERWRITTEN_INCREMENT](#rule-23) | 一般 | JAVA | DLS_OVERWRITTEN_INCREMENT | 通用类（非编程规范） |
| 24 | [DMI_ANNOTATION_IS_NOT_VISIBLE_TO_REFLECTION](#rule-24) | 一般 | JAVA | DMI_ANNOTATION_IS_NOT_VISIBLE_TO_REFLECTION | 通用类（非编程规范） |
| 25 | [DMI_BLOCKING_METHODS_ON_URL](#rule-25) | 一般 | JAVA | DMI_BLOCKING_METHODS_ON_URL | 通用类（非编程规范） |
| 26 | [DMI_CALLING_NEXT_FROM_HASNEXT](#rule-26) | 一般 | JAVA | DMI_CALLING_NEXT_FROM_HASNEXT | 通用类（非编程规范） |
| 27 | [DMI_COLLECTIONS_SHOULD_NOT_CONTAIN_THEMSELVES](#rule-27) | 一般 | JAVA | DMI_COLLECTIONS_SHOULD_NOT_CONTAIN_THEMSELVES | 通用类（非编程规范） |
| 28 | [DMI_COLLECTION_OF_URLS](#rule-28) | 一般 | JAVA | DMI_COLLECTION_OF_URLS | 通用类（非编程规范） |
| 29 | [DMI_INVOKING_HASHCODE_ON_ARRAY](#rule-29) | 一般 | JAVA | DMI_INVOKING_HASHCODE_ON_ARRAY | 通用类（非编程规范） |
| 30 | [DMI_INVOKING_TOSTRING_ON_ANONYMOUS_ARRAY](#rule-30) | 一般 | JAVA | DMI_INVOKING_TOSTRING_ON_ANONYMOUS_ARRAY | 通用类（非编程规范） |
| 31 | [DMI_INVOKING_TOSTRING_ON_ARRAY](#rule-31) | 一般 | JAVA | DMI_INVOKING_TOSTRING_ON_ARRAY | 通用类（非编程规范） |
| 32 | [DMI_RANDOM_USED_ONLY_ONCE](#rule-32) | 一般 | JAVA | DMI_RANDOM_USED_ONLY_ONCE | 通用类（非编程规范） |
| 33 | [DMI_USING_REMOVEALL_TO_CLEAR_COLLECTION](#rule-33) | 一般 | JAVA | DMI_USING_REMOVEALL_TO_CLEAR_COLLECTION | 通用类（非编程规范） |
| 34 | [DM_BOXED_PRIMITIVE_FOR_COMPARE](#rule-34) | 一般 | JAVA | DM_BOXED_PRIMITIVE_FOR_COMPARE | 通用类（非编程规范） |
| 35 | [DM_BOXED_PRIMITIVE_FOR_PARSING](#rule-35) | 一般 | JAVA | DM_BOXED_PRIMITIVE_FOR_PARSING | 通用类（非编程规范） |
| 36 | [DM_CONVERT_CASE](#rule-36) | 一般 | JAVA | DM_CONVERT_CASE | 通用类（非编程规范） |
| 37 | [DM_DEFAULT_ENCODING](#rule-37) | 一般 | JAVA | DM_DEFAULT_ENCODING | 通用类（非编程规范） |
| 38 | [DM_EXIT](#rule-38) | 一般 | JAVA | DM_EXIT | 通用类（非编程规范） |
| 39 | [DM_FP_NUMBER_CTOR](#rule-39) | 一般 | JAVA | DM_FP_NUMBER_CTOR | 通用类（非编程规范） |
| 40 | [DM_GC](#rule-40) | 一般 | JAVA | DM_GC | 通用类（非编程规范） |
| 41 | [DM_INVALID_MIN_MAX](#rule-41) | 一般 | JAVA | DM_INVALID_MIN_MAX | 通用类（非编程规范） |
| 42 | [DM_NEW_FOR_GETCLASS](#rule-42) | 一般 | JAVA | DM_NEW_FOR_GETCLASS | 通用类（非编程规范） |
| 43 | [DM_NEXTINT_VIA_NEXTDOUBLE](#rule-43) | 一般 | JAVA | DM_NEXTINT_VIA_NEXTDOUBLE | 通用类（非编程规范） |
| 44 | [DM_NUMBER_CTOR](#rule-44) | 一般 | JAVA | DM_NUMBER_CTOR | 通用类（非编程规范） |
| 45 | [DM_STRING_CTOR](#rule-45) | 一般 | JAVA | DM_STRING_CTOR | 通用类（非编程规范） |
| 46 | [DM_STRING_TOSTRING](#rule-46) | 一般 | JAVA | DM_STRING_TOSTRING | 通用类（非编程规范） |
| 47 | [DM_STRING_VOID_CTOR](#rule-47) | 一般 | JAVA | DM_STRING_VOID_CTOR | 通用类（非编程规范） |
| 48 | [DM_USELESS_THREAD](#rule-48) | 一般 | JAVA | DM_USELESS_THREAD | 通用类（非编程规范） |
| 49 | [DP_CREATE_CLASSLOADER_INSIDE_DO_PRIVILEGED](#rule-49) | 一般 | JAVA | DP_CREATE_CLASSLOADER_INSIDE_DO_PRIVILEGED | 通用类（非编程规范） |
| 50 | [DP_DO_INSIDE_DO_PRIVILEGED](#rule-50) | 一般 | JAVA | DP_DO_INSIDE_DO_PRIVILEGED | 通用类（非编程规范） |
| 51 | [EC_ARRAY_AND_NONARRAY](#rule-51) | 一般 | JAVA | EC_ARRAY_AND_NONARRAY | 通用类（非编程规范） |
| 52 | [EC_BAD_ARRAY_COMPARE](#rule-52) | 一般 | JAVA | EC_BAD_ARRAY_COMPARE | 通用类（非编程规范） |
| 53 | [EC_INCOMPATIBLE_ARRAY_COMPARE](#rule-53) | 一般 | JAVA | EC_INCOMPATIBLE_ARRAY_COMPARE | 通用类（非编程规范） |
| 54 | [EC_NULL_ARG](#rule-54) | 一般 | JAVA | EC_NULL_ARG | 通用类（非编程规范） |
| 55 | [EC_UNRELATED_CLASS_AND_INTERFACE](#rule-55) | 一般 | JAVA | EC_UNRELATED_CLASS_AND_INTERFACE | 通用类（非编程规范） |
| 56 | [EI_EXPOSE_STATIC_REP2](#rule-56) | 一般 | JAVA | EI_EXPOSE_STATIC_REP2 | 通用类（非编程规范） |
| 57 | [EQ_ALWAYS_FALSE](#rule-57) | 一般 | JAVA | EQ_ALWAYS_FALSE | 通用类（非编程规范） |
| 58 | [EQ_ALWAYS_TRUE](#rule-58) | 一般 | JAVA | EQ_ALWAYS_TRUE | 通用类（非编程规范） |
| 59 | [EQ_CHECK_FOR_OPERAND_NOT_COMPATIBLE_WITH_THIS](#rule-59) | 一般 | JAVA | EQ_CHECK_FOR_OPERAND_NOT_COMPATIBLE_WITH_THIS | 通用类（非编程规范） |
| 60 | [EQ_COMPARETO_USE_OBJECT_EQUALS](#rule-60) | 一般 | JAVA | EQ_COMPARETO_USE_OBJECT_EQUALS | 通用类（非编程规范） |
| 61 | [EQ_GETCLASS_AND_CLASS_CONSTANT](#rule-61) | 一般 | JAVA | EQ_GETCLASS_AND_CLASS_CONSTANT | 通用类（非编程规范） |
| 62 | [EQ_OTHER_NO_OBJECT](#rule-62) | 一般 | JAVA | EQ_OTHER_NO_OBJECT | 通用类（非编程规范） |
| 63 | [EQ_OVERRIDING_EQUALS_NOT_SYMMETRIC](#rule-63) | 一般 | JAVA | EQ_OVERRIDING_EQUALS_NOT_SYMMETRIC | 通用类（非编程规范） |
| 64 | [EQ_SELF_NO_OBJECT](#rule-64) | 一般 | JAVA | EQ_SELF_NO_OBJECT | 通用类（非编程规范） |
| 65 | [EQ_SELF_USE_OBJECT](#rule-65) | 一般 | JAVA | EQ_SELF_USE_OBJECT | 通用类（非编程规范） |
| 66 | [ES_COMPARING_PARAMETER_STRING_WITH_EQ](#rule-66) | 一般 | JAVA | ES_COMPARING_PARAMETER_STRING_WITH_EQ | 通用类（非编程规范） |
| 67 | [ES_COMPARING_STRINGS_WITH_EQ](#rule-67) | 一般 | JAVA | ES_COMPARING_STRINGS_WITH_EQ | 通用类（非编程规范） |
| 68 | [FE_TEST_IF_EQUAL_TO_NOT_A_NUMBER](#rule-68) | 一般 | JAVA | FE_TEST_IF_EQUAL_TO_NOT_A_NUMBER | 通用类（非编程规范） |
| 69 | [FI_EXPLICIT_INVOCATION](#rule-69) | 一般 | JAVA | FI_EXPLICIT_INVOCATION | 通用类（非编程规范） |
| 70 | [G.CMT.02 顶层public类的Javadoc应该包含功能说明和创建日期/版本信息](#rule-70) | 提示 | JAVA | TopClassComment | 通用规范建议类 |
| 71 | [G.CMT.03 方法的Javadoc中应该包含功能说明，根据实际需要按顺序使用@param、@return、@throws标签对参数、返回值、异常进行注释](#rule-71) | 提示 | JAVA | JavaDocFormat,JavaDocLineBreak | 通用规范建议类 |
| 72 | [G.CMT.04 不写空有格式的方法头注释](#rule-72) | 提示 | JAVA | MethodHeadComment | 通用规范建议类 |
| 73 | [G.CMT.05 文件头注释应该包含版权许可信息](#rule-73) | 提示 | JAVA | FileHeaderComment | 通用规范建议类 |
| 74 | [G.COL.04 不要在foreach循环中通过remove()/add()方法更改集合](#rule-74) | 严重 | JAVA | ForeachAddOrRemove | 通用规范规则类 |
| 75 | [G.CON.01 对共享变量做同步访问控制时需避开同步陷阱](#rule-75) | 致命 | JAVA | Forbid_Synchronize_Concurrency_Object,Forbid_Synchronize_getClass_Object,Insecure_Instance_Lock,Forbid_Synchronize_Reused_Object,SecJ_Lock_Object_Not_Final | 安全规范建议类 |
| 76 | [G.CON.02 在异常条件下，保证释放已持有的锁](#rule-76) | 严重 | JAVA | SecS_Unreleased_Resource_Synchronization | 安全规范规则类 |
| 77 | [G.CON.04 避免使用不正确形式的双重检查锁](#rule-77) | 一般 | JAVA | SecJ_Double_Checked_Locking | 安全规范规则类 |
| 78 | [G.CON.05 禁止使用非线程安全的方法来覆写线程安全的方法](#rule-78) | 一般 | JAVA | SecJ_Non_Synchronized_Method_Overrides_Synchronized_Method | 安全规范规则类 |
| 79 | [G.CON.06 使用新并发工具代替wait()和notify()](#rule-79) | 一般 | JAVA | DoNotUseNotify | 通用规范规则类 |
| 80 | [G.CON.07 创建新线程时必须指定线程名](#rule-80) | 一般 | JAVA | ThreadName | 通用规范规则类 |
| 81 | [G.CON.08 使用Thread对象的setUncaughtExceptionHandler方法注册未捕获异常处理者](#rule-81) | 严重 | JAVA | ThreadException | 通用规范规则类 |
| 82 | [G.CON.09 不要依赖线程调度器、线程优先级和yield()方法](#rule-82) | 一般 | JAVA | ThreadControl | 通用规范规则类 |
| 83 | [G.CON.11 禁止使用Thread.stop()来终止线程](#rule-83) | 严重 | JAVA | Forbid_Thread_Stop | 安全规范规则类 |
| 84 | [G.CON.13 线程池中的任务结束后必须清理其自定义的ThreadLocal变量](#rule-84) | 一般 | JAVA | SecJ_Uncleaned_ThreadLocal | 安全规范规则类 |
| 85 | [G.CTL.01 不要在控制性条件表达式中执行赋值操作或执行复杂的条件判断](#rule-85) | 一般 | JAVA | ConditionalExpression | 通用规范规则类 |
| 86 | [G.CTL.03 switch语句要有default分支](#rule-86) | 一般 | JAVA | SwitchMustHaveDefault | 通用规范规则类 |
| 87 | [G.CTL.04 循环必须保证可正确终止](#rule-87) | 严重 | JAVA | EmptyInfiniteLoop | 通用规范规则类 |
| 88 | [G.CTL.06 禁止switch语句中直接嵌套switch](#rule-88) | 严重 | JAVA | NestedSwitch | 通用类（非编程规范） |
| 89 | [G.DCL.01 每行声明一个变量](#rule-89) | 一般 | JAVA | MultipleVariableDeclarations | 通用规范规则类 |
| 90 | [G.DCL.03 禁止C风格的数组声明](#rule-90) | 一般 | JAVA | NoCStyleArrayName | 通用规范规则类 |
| 91 | [G.DCL.04 避免枚举常量序号的产生依赖于ordinal()方法](#rule-91) | 一般 | JAVA | Ordinal | 通用规范建议类 |
| 92 | [G.DCL.05 禁止将mutable对象声明为public static final](#rule-92) | 一般 | JAVA | Mutable | 通用规范规则类 |
| 93 | [G.EDV.01 禁止直接使用外部数据来拼接SQL语句](#rule-93) | 严重 | JAVA | SecJ_SQL_Injection_Mybatis,SecS_SQL_Injection | 安全规范规则类 |
| 94 | [G.EDV.02 禁止直接使用外部数据构造格式化字符串](#rule-94) | 严重 | JAVA | SecS_Denial_of_Service | 安全规范规则类 |
| 95 | [G.EDV.03 禁止直接向Runtime.exec() 方法或java.lang.ProcessBuilder 类传递外部数据](#rule-95) | 严重 | JAVA | SecS_OGNL_Expression_Injection,SecS_Command_Injection | 安全类（非编程规范） |
| 96 | [G.EDV.04 禁止直接使用外部数据来拼接XML](#rule-96) | 严重 | JAVA | SecS_XML_Injection | 安全规范规则类 |
| 97 | [G.EDV.05 防止解析来自外部的XML导致的外部实体（XML External Entity）攻击](#rule-97) | 严重 | JAVA | XXE_DocumentHelper,XXE_XMLReaderFactory,XXE_SAXTransformerFactory,XXE_DocumentBuilderFactoryImpl,XXE_SchemaFactory,XXE_DocumentBuilderFactory,XXE_SAXBuilder,XXE_TransformerFactory,XXE_SAXReader,XXE_SAXParserFactory,XXE_XMLInputFactory | 安全规范规则类 |
| 98 | [G.EDV.06 防止解析来自外部的XML导致的内部实体扩展（XML Entity Expansion）攻击](#rule-98) | 严重 | JAVA | XXE_DocumentHelper,XXE_XMLReaderFactory,XXE_SAXTransformerFactory,XXE_DocumentBuilderFactoryImpl,XXE_SchemaFactory,XXE_DocumentBuilderFactory,XXE_SAXBuilder,XXE_TransformerFactory,XXE_SAXReader,SecJ_XML_External_Entity_Injection_JavaStandard,XXE_SAXParserFactory,XXE_XMLInputFactory | 安全规范规则类 |
| 99 | [G.EDV.07 禁止使用不安全的XSLT转换XML文件](#rule-99) | 一般 | JAVA | Insecure_XML_Transform_By_XSLT,SecS_XSLT_Injection | 安全规范规则类 |
| 100 | [G.EDV.08 正则表达式要尽量简单，防止ReDos攻击](#rule-100) | 严重 | JAVA | SecS_Denial_of_Matches | 安全规范规则类 |
| 101 | [G.EDV.09 禁止直接使用外部数据作为反射操作中的类名/方法名](#rule-101) | 严重 | JAVA | SecS_Unsafe_Reflection | 安全类（非编程规范） |
| 102 | [G.ERR.01 不要通过一个空的catch块忽略异常](#rule-102) | 一般 | JAVA | EmptyCatch | 通用规范规则类 |
| 103 | [G.ERR.03 不要直接捕获可通过预检查进行处理的RuntimeException ，如NullPointerException 、IndexOutOfBoundsException 等](#rule-103) | 一般 | JAVA | SecJ_Forbid_Catch_PreCheck_RuntimeException | 安全规范建议类 |
| 104 | [G.ERR.05 方法抛出的异常，应该与本身的抽象层次相对应](#rule-104) | 一般 | JAVA | ThrowRawException | 通用规范规则类 |
| 105 | [G.ERR.06 在catch块中抛出新异常时，避免丢失原始异常信息](#rule-105) | 一般 | JAVA | SecJ_Missing_Original_Exception | 安全规范建议类 |
| 106 | [G.ERR.08 不要使用return、break、continue或抛出异常使finally块非正常结束](#rule-106) | 严重 | JAVA | Finally | 通用规范规则类 |
| 107 | [G.EXP.01 不要在单个表达式中对相同的变量赋值超过一次](#rule-107) | 严重 | JAVA | MultipleAssignment | 通用规范规则类 |
| 108 | [G.EXP.03 条件表达式?:的第2和第3个操作数应使用相同的类型](#rule-108) | 一般 | JAVA | OperandTypeInConditionalExpression | 通用规范建议类 |
| 109 | [G.EXP.05 禁止直接使用可能为null的对象，防止出现空指针引用](#rule-109) | 严重 | JAVA | SecS_Null_Dereference | 安全规范规则类 |
| 110 | [G.EXP.06 代码中不应使用断言（assert）](#rule-110) | 一般 | JAVA | Assertions_With_Side_Effects | 安全规范建议类 |
| 111 | [G.FIO.01 使用外部数据构造的文件路径前必须进行校验，校验前必须对文件路径进行规范化处理](#rule-111) | 严重 | JAVA | Canonical_Path | 安全规范规则类 |
| 112 | [G.FIO.02 从ZipInputStream中解压文件必须进行安全检查](#rule-112) | 一般 | JAVA | Zip_Entry_Size | 安全规范规则类 |
| 113 | [G.FIO.03 对于从流中读取一个字符或字节的方法，使用int类型的返回值](#rule-113) | 致命 | JAVA | Invalid_Read_From_Stream | 安全规范规则类 |
| 114 | [G.FIO.04 防止外部进程阻塞在输入输出流上](#rule-114) | 致命 | JAVA | IO_Process_WaitFor | 安全规范规则类 |
| 115 | [G.FIO.05 临时文件使用完毕必须及时删除](#rule-115) | 一般 | JAVA | SecS_Remove_Temporary_File | 安全规范规则类 |
| 116 | [G.FMT.09 每行不超过一个语句](#rule-116) | 提示 | JAVA | OneStatementPerLine | 通用规范建议类 |
| 117 | [G.FMT.13 用空格突出关键字和重要信息](#rule-117) | 提示 | JAVA | WhitespaceNoTrailing | 通用规范建议类 |
| 118 | [G.LOG.01 记录日志应该使用Facade模式的日志框架](#rule-118) | 一般 | JAVA | DoNotLogWithSystemPrint | 通用规范规则类 |
| 119 | [G.LOG.02 日志工具Logger类的实例必须声明为private static final或者private final](#rule-119) | 一般 | JAVA | LogModifier | 通用规范规则类 |
| 120 | [G.LOG.03 日志必须分等级](#rule-120) | 一般 | JAVA | LogLevel | 通用规范规则类 |
| 121 | [G.LOG.04 非仅限于中文区销售产品禁止用中文打印日志](#rule-121) | 一般 | JAVA | LogWithoutChinese | 通用规范规则类 |
| 122 | [G.LOG.05 禁止直接使用外部数据记录日志](#rule-122) | 严重 | JAVA | SecS_Log_Forging_Debug,SecS_Log_Forging | 安全规范规则类 |
| 123 | [G.MET.02 不要使用已标注为@Deprecated的方法、类、类的属性等](#rule-123) | 一般 | JAVA | SecJ_Call_Deprecated | 通用规范规则类 |
| 124 | [G.NAM.01 标识符应由不超过64字符的字母、数字和下划线组成](#rule-124) | 提示 | JAVA | IdentifierName | 通用规范建议类 |
| 125 | [G.NAM.03 类、枚举和接口名应采用大驼峰命名](#rule-125) | 提示 | JAVA | TypeName | 通用规范建议类 |
| 126 | [G.NAM.04 方法名应采用小驼峰命名](#rule-126) | 提示 | JAVA | MethodName | 通用规范建议类 |
| 127 | [G.NAM.06 变量采用小驼峰命名](#rule-127) | 提示 | JAVA | NonConstantName | 通用规范建议类 |
| 128 | [G.NAM.07 避免使用具有否定含义布尔变量名](#rule-128) | 提示 | JAVA | BoolVariableName | 通用规范建议类 |
| 129 | [G.OBJ.02 不要在父类的构造方法中调用可能被子类覆写的方法](#rule-129) | 一般 | JAVA | ConstructorInvokesOverridable | 通用规范规则类 |
| 130 | [G.OBJ.04 避免在无关的变量或无关的概念之间重用名字，避免隐藏（hide）、遮蔽（shadow）和遮掩（obscure）](#rule-130) | 严重 | JAVA | HiddenField | 通用规范规则类 |
| 131 | [G.OBJ.05 避免基本类型与其包装类型的同名重载方法](#rule-131) | 一般 | JAVA | WrapperClassInOverloadMethod | 通用规范建议类 |
| 132 | [G.OBJ.06 覆写equals方法时，要同时覆写hashCode方法](#rule-132) | 严重 | JAVA | EqualsHashCode | 通用规范规则类 |
| 133 | [G.OBJ.07 子类覆写父类方法或实现接口时必须加上@Override注解](#rule-133) | 一般 | JAVA | OverrideAnnotation | 通用规范规则类 |
| 134 | [G.OBJ.08 正确实现单例模式](#rule-134) | 提示 | JAVA | UntrustedSingleton | 通用规范建议类 |
| 135 | [G.OBJ.09 使用类名调用静态方法，而不要使用实例或表达式来调用](#rule-135) | 严重 | JAVA | AccessStaticViaInstance | 通用规范规则类 |
| 136 | [G.OBJ.10 接口定义中去掉多余的修饰词](#rule-136) | 一般 | JAVA | RedundantModifier | 通用规范建议类 |
| 137 | [G.OTH.01 安全场景下必须使用密码学意义上的安全随机数](#rule-137) | 一般 | JAVA | SecJ_Insecure_Randomness_Hardcoded_Seed,SecJ_Insecure_Randomness_Predictable_Seed,Insecure_Randomness,SecJ_Insecure_Randomness | 安全规范规则类 |
| 138 | [G.OTH.02 必须使用SSLSocket代替Socket来进行安全数据交互](#rule-138) | 一般 | JAVA | Bad_Practices_Sockets_Bind_IP,Bad_Practices_Sockets | 安全规范规则类 |
| 139 | [G.OTH.03 不用的代码段包括import，直接删除，不要注释掉](#rule-139) | 一般 | JAVA | CodeInComment,UnusedImport | 通用规范规则类 |
| 140 | [G.OTH.04 禁止代码中包含公网地址](#rule-140) | 严重 | JAVA | AvoidUsingHardCodedIP | 安全规范规则类 |
| 141 | [G.OTH.06 禁止在用户界面、日志中暴露不必要信息](#rule-141) | 严重 | JAVA | Privacy_Violation_Password | 安全规范规则类 |
| 142 | [G.PRM.01 将集合转为数组时使用Collection&lt;T&gt;.toArray(T[])方法；Java 11后使用Collection&lt;T&gt;.toArray(IntFunction&lt;T[]&gt;)](#rule-142) | 一般 | JAVA | ToArray | 通用规范规则类 |
| 143 | [G.PRM.02 使用System.arraycopy()或Arrays.copyOf()进行数组复制](#rule-143) | 一般 | JAVA | AvoidArrayLoops | 通用规范建议类 |
| 144 | [G.PRM.04 不要对正则表达式进行频繁重复预编译](#rule-144) | 一般 | JAVA | UsePatternCompileInMethodOrForStatement | 通用规范规则类 |
| 145 | [G.PRM.05 禁止创建不必要的对象](#rule-145) | 一般 | JAVA | UnnecessaryNew | 通用规范规则类 |
| 146 | [G.PRM.07 进行IO类操作时，必须在try-with-resource或finally里关闭资源](#rule-146) | 严重 | JAVA | ResourceRelease | 通用规范规则类 |
| 147 | [G.PRM.08 禁止使用主动GC（除非在密码、RMI等方面），尤其是在频繁/周期性的逻辑中](#rule-147) | 严重 | JAVA | LoopGC | 通用规范规则类 |
| 148 | [G.PRM.09 禁止使用Finalizer机制](#rule-148) | 一般 | JAVA | Finalizer,Finalize | 通用规范规则类 |
| 149 | [G.SEC.01 进行安全检查的方法必须声明为private或final](#rule-149) | 一般 | JAVA | Invalid_Security_Check | 安全规范规则类 |
| 150 | [G.SEC.02 自定义类加载器覆写getPermission() 时，必须先调用父类的getPermission() 方法](#rule-150) | 一般 | JAVA | Call_Super_GetPermission | 安全规范规则类 |
| 151 | [G.SEC.04 使用安全管理器来保护敏感操作](#rule-151) | 一般 | JAVA | SecurityManager_Privacy_Operation | 安全规范建议类 |
| 152 | [G.SER.01 尽量避免实现Serializable接口](#rule-152) | 提示 | JAVA | AvoidSerialization | 通用规范建议类 |
| 153 | [G.SER.04 禁止直接序列化指向系统资源的信息](#rule-153) | 严重 | JAVA | SerializeFileHandle | 通用规范规则类 |
| 154 | [G.SER.05 禁止序列化非静态的内部类](#rule-154) | 致命 | JAVA | Forbid_Serialize_Nonstatic_Inner_Class | 安全规范规则类 |
| 155 | [G.SER.07 防止反序列化被利用来绕过构造方法中的安全操作](#rule-155) | 致命 | JAVA | Missing_SecurityManager_Check_Serializable | 安全规范规则类 |
| 156 | [G.SER.08 禁止直接将外部数据进行反序列化](#rule-156) | 严重 | JAVA | SecS_Dynamic_Code_Evaluation | 安全规范规则类 |
| 157 | [G.TYP.01 进行数值运算时，避免整数溢出](#rule-157) | 一般 | JAVA | SecJ_Integer_Overflow | 安全规范建议类 |
| 158 | [G.TYP.02 确保除法运算和模运算中的除数不为0](#rule-158) | 一般 | JAVA | SecJ_Denominator_Length_Check | 安全规范规则类 |
| 159 | [G.TYP.03 禁止使用浮点数作为循环计数器](#rule-159) | 严重 | JAVA | LoopFloat | 通用规范规则类 |
| 160 | [G.TYP.05 浮点型数据判断相等不要直接使用==，浮点型包装类型不要用equals()或者 flt.compareTo(another) == 0作相等的比较](#rule-160) | 严重 | JAVA | BigDecimalEquals,FloatEquals | 通用规范规则类 |
| 161 | [G.TYP.06 禁止尝试与NaN进行比较运算，相等操作使用Double或Float的isNaN()方法](#rule-161) | 严重 | JAVA | CompareNaN | 通用规范规则类 |
| 162 | [G.TYP.08 字符串大小写转换、数字格式化为西方数字时，必须加上Locale.ROOT或Locale.ENGLISH](#rule-162) | 一般 | JAVA | Locale | 通用规范规则类 |
| 163 | [G.TYP.09 字符与字节的互相转换操作，要指明正确的编码方式](#rule-163) | 一般 | JAVA | AssignCharset | 通用规范规则类 |
| 164 | [G.TYP.11 基本类型优于包装类型，注意合理使用包装类型](#rule-164) | 一般 | JAVA | WrapperClass | 通用规范建议类 |
| 165 | [GC_UNRELATED_TYPES](#rule-165) | 一般 | JAVA | GC_UNRELATED_TYPES | 通用类（非编程规范） |
| 166 | [HE_EQUALS_NO_HASHCODE](#rule-166) | 一般 | JAVA | HE_EQUALS_NO_HASHCODE | 通用类（非编程规范） |
| 167 | [HE_EQUALS_USE_HASHCODE](#rule-167) | 一般 | JAVA | HE_EQUALS_USE_HASHCODE | 通用类（非编程规范） |
| 168 | [HE_HASHCODE_NO_EQUALS](#rule-168) | 一般 | JAVA | HE_HASHCODE_NO_EQUALS | 通用类（非编程规范） |
| 169 | [HE_HASHCODE_USE_OBJECT_EQUALS](#rule-169) | 一般 | JAVA | HE_HASHCODE_USE_OBJECT_EQUALS | 通用类（非编程规范） |
| 170 | [HE_INHERITS_EQUALS_USE_HASHCODE](#rule-170) | 一般 | JAVA | HE_INHERITS_EQUALS_USE_HASHCODE | 通用类（非编程规范） |
| 171 | [HE_USE_OF_UNHASHABLE_CLASS](#rule-171) | 一般 | JAVA | HE_USE_OF_UNHASHABLE_CLASS | 通用类（非编程规范） |
| 172 | [HRS_REQUEST_PARAMETER_TO_COOKIE](#rule-172) | 严重 | JAVA | HRS_REQUEST_PARAMETER_TO_COOKIE | 通用类（非编程规范） |
| 173 | [HRS_REQUEST_PARAMETER_TO_HTTP_HEADER](#rule-173) | 严重 | JAVA | HRS_REQUEST_PARAMETER_TO_HTTP_HEADER | 通用类（非编程规范） |
| 174 | [HSC_HUGE_SHARED_STRING_CONSTANT](#rule-174) | 一般 | JAVA | HSC_HUGE_SHARED_STRING_CONSTANT | 通用类（非编程规范） |
| 175 | [ICAST_BAD_SHIFT_AMOUNT](#rule-175) | 一般 | JAVA | ICAST_BAD_SHIFT_AMOUNT | 通用类（非编程规范） |
| 176 | [ICAST_INT_CAST_TO_DOUBLE_PASSED_TO_CEIL](#rule-176) | 一般 | JAVA | ICAST_INT_CAST_TO_DOUBLE_PASSED_TO_CEIL | 通用类（非编程规范） |
| 177 | [ICAST_INT_CAST_TO_FLOAT_PASSED_TO_ROUND](#rule-177) | 一般 | JAVA | ICAST_INT_CAST_TO_FLOAT_PASSED_TO_ROUND | 通用类（非编程规范） |
| 178 | [IC_SUPERCLASS_USES_SUBCLASS_DURING_INITIALIZATION](#rule-178) | 一般 | JAVA | IC_SUPERCLASS_USES_SUBCLASS_DURING_INITIALIZATION | 通用类（非编程规范） |
| 179 | [IIO_INEFFICIENT_LAST_INDEX_OF](#rule-179) | 一般 | JAVA | IIO_INEFFICIENT_LAST_INDEX_OF | 通用类（非编程规范） |
| 180 | [IM_MULTIPLYING_RESULT_OF_IREM](#rule-180) | 一般 | JAVA | IM_MULTIPLYING_RESULT_OF_IREM | 通用类（非编程规范） |
| 181 | [INT_BAD_COMPARISON_WITH_INT_VALUE](#rule-181) | 一般 | JAVA | INT_BAD_COMPARISON_WITH_INT_VALUE | 通用类（非编程规范） |
| 182 | [INT_BAD_COMPARISON_WITH_NONNEGATIVE_VALUE](#rule-182) | 一般 | JAVA | INT_BAD_COMPARISON_WITH_NONNEGATIVE_VALUE | 通用类（非编程规范） |
| 183 | [INT_BAD_COMPARISON_WITH_SIGNED_BYTE](#rule-183) | 一般 | JAVA | INT_BAD_COMPARISON_WITH_SIGNED_BYTE | 通用类（非编程规范） |
| 184 | [IO_APPENDING_TO_OBJECT_OUTPUT_STREAM](#rule-184) | 一般 | JAVA | IO_APPENDING_TO_OBJECT_OUTPUT_STREAM | 通用类（非编程规范） |
| 185 | [IP_PARAMETER_IS_DEAD_BUT_OVERWRITTEN](#rule-185) | 一般 | JAVA | IP_PARAMETER_IS_DEAD_BUT_OVERWRITTEN | 通用类（非编程规范） |
| 186 | [IS2_INCONSISTENT_SYNC](#rule-186) | 一般 | JAVA | IS2_INCONSISTENT_SYNC | 通用类（非编程规范） |
| 187 | [ISC_INSTANTIATE_STATIC_CLASS](#rule-187) | 一般 | JAVA | ISC_INSTANTIATE_STATIC_CLASS | 通用类（非编程规范） |
| 188 | [IS_INCONSISTENT_SYNC](#rule-188) | 一般 | JAVA | IS_INCONSISTENT_SYNC | 通用类（非编程规范） |
| 189 | [IT_NO_SUCH_ELEMENT](#rule-189) | 一般 | JAVA | IT_NO_SUCH_ELEMENT | 通用类（非编程规范） |
| 190 | [JLM_JSR166_LOCK_MONITORENTER](#rule-190) | 一般 | JAVA | JLM_JSR166_LOCK_MONITORENTER | 通用类（非编程规范） |
| 191 | [JLM_JSR166_UTILCONCURRENT_MONITORENTER](#rule-191) | 一般 | JAVA | JLM_JSR166_UTILCONCURRENT_MONITORENTER | 通用类（非编程规范） |
| 192 | [LI_LAZY_INIT_STATIC](#rule-192) | 一般 | JAVA | LI_LAZY_INIT_STATIC | 通用类（非编程规范） |
| 193 | [LI_LAZY_INIT_UPDATE_STATIC](#rule-193) | 一般 | JAVA | LI_LAZY_INIT_UPDATE_STATIC | 通用类（非编程规范） |
| 194 | [ME_ENUM_FIELD_SETTER](#rule-194) | 一般 | JAVA | ME_ENUM_FIELD_SETTER | 通用类（非编程规范） |
| 195 | [ME_MUTABLE_ENUM_FIELD](#rule-195) | 一般 | JAVA | ME_MUTABLE_ENUM_FIELD | 通用类（非编程规范） |
| 196 | [MF_CLASS_MASKS_FIELD](#rule-196) | 一般 | JAVA | MF_CLASS_MASKS_FIELD | 通用类（非编程规范） |
| 197 | [ML_SYNC_ON_FIELD_TO_GUARD_CHANGING_THAT_FIELD](#rule-197) | 一般 | JAVA | ML_SYNC_ON_FIELD_TO_GUARD_CHANGING_THAT_FIELD | 通用类（非编程规范） |
| 198 | [ML_SYNC_ON_UPDATED_FIELD](#rule-198) | 一般 | JAVA | ML_SYNC_ON_UPDATED_FIELD | 通用类（非编程规范） |
| 199 | [MSF_MUTABLE_SERVLET_FIELD](#rule-199) | 一般 | JAVA | MSF_MUTABLE_SERVLET_FIELD | 通用类（非编程规范） |
| 200 | [MS_CANNOT_BE_FINAL](#rule-200) | 一般 | JAVA | MS_CANNOT_BE_FINAL | 通用类（非编程规范） |
| 201 | [MS_MUTABLE_ARRAY](#rule-201) | 一般 | JAVA | MS_MUTABLE_ARRAY | 通用类（非编程规范） |
| 202 | [MS_MUTABLE_COLLECTION](#rule-202) | 一般 | JAVA | MS_MUTABLE_COLLECTION | 通用类（非编程规范） |
| 203 | [MS_MUTABLE_COLLECTION_PKGPROTECT](#rule-203) | 一般 | JAVA | MS_MUTABLE_COLLECTION_PKGPROTECT | 通用类（非编程规范） |
| 204 | [MS_MUTABLE_HASHTABLE](#rule-204) | 一般 | JAVA | MS_MUTABLE_HASHTABLE | 通用类（非编程规范） |
| 205 | [MS_OOI_PKGPROTECT](#rule-205) | 一般 | JAVA | MS_OOI_PKGPROTECT | 通用类（非编程规范） |
| 206 | [MS_SHOULD_BE_FINAL](#rule-206) | 一般 | JAVA | MS_SHOULD_BE_FINAL | 通用类（非编程规范） |
| 207 | [MS_SHOULD_BE_REFACTORED_TO_BE_FINAL](#rule-207) | 一般 | JAVA | MS_SHOULD_BE_REFACTORED_TO_BE_FINAL | 通用类（非编程规范） |
| 208 | [NM_CLASS_NAMING_CONVENTION](#rule-208) | 一般 | JAVA | NM_CLASS_NAMING_CONVENTION | 通用类（非编程规范） |
| 209 | [NM_CLASS_NOT_EXCEPTION](#rule-209) | 一般 | JAVA | NM_CLASS_NOT_EXCEPTION | 通用类（非编程规范） |
| 210 | [NM_FIELD_NAMING_CONVENTION](#rule-210) | 一般 | JAVA | NM_FIELD_NAMING_CONVENTION | 通用类（非编程规范） |
| 211 | [NM_LCASE_HASHCODE](#rule-211) | 一般 | JAVA | NM_LCASE_HASHCODE | 通用类（非编程规范） |
| 212 | [NM_LCASE_TOSTRING](#rule-212) | 一般 | JAVA | NM_LCASE_TOSTRING | 通用类（非编程规范） |
| 213 | [NM_METHOD_CONSTRUCTOR_CONFUSION](#rule-213) | 一般 | JAVA | NM_METHOD_CONSTRUCTOR_CONFUSION | 通用类（非编程规范） |
| 214 | [NM_SAME_SIMPLE_NAME_AS_INTERFACE](#rule-214) | 一般 | JAVA | NM_SAME_SIMPLE_NAME_AS_INTERFACE | 通用类（非编程规范） |
| 215 | [NM_SAME_SIMPLE_NAME_AS_SUPERCLASS](#rule-215) | 一般 | JAVA | NM_SAME_SIMPLE_NAME_AS_SUPERCLASS | 通用类（非编程规范） |
| 216 | [NO_NOTIFY_NOT_NOTIFYALL](#rule-216) | 一般 | JAVA | NO_NOTIFY_NOT_NOTIFYALL | 通用类（非编程规范） |
| 217 | [NP_ALWAYS_NULL](#rule-217) | 一般 | JAVA | NP_ALWAYS_NULL | 通用类（非编程规范） |
| 218 | [NP_ALWAYS_NULL_EXCEPTION](#rule-218) | 一般 | JAVA | NP_ALWAYS_NULL_EXCEPTION | 通用类（非编程规范） |
| 219 | [NP_CLONE_COULD_RETURN_NULL](#rule-219) | 一般 | JAVA | NP_CLONE_COULD_RETURN_NULL | 通用类（非编程规范） |
| 220 | [NP_CLOSING_NULL](#rule-220) | 一般 | JAVA | NP_CLOSING_NULL | 通用类（非编程规范） |
| 221 | [NP_GUARANTEED_DEREF](#rule-221) | 一般 | JAVA | NP_GUARANTEED_DEREF | 通用类（非编程规范） |
| 222 | [NP_GUARANTEED_DEREF_ON_EXCEPTION_PATH](#rule-222) | 一般 | JAVA | NP_GUARANTEED_DEREF_ON_EXCEPTION_PATH | 通用类（非编程规范） |
| 223 | [NP_NONNULL_RETURN_VIOLATION](#rule-223) | 一般 | JAVA | NP_NONNULL_RETURN_VIOLATION | 通用类（非编程规范） |
| 224 | [NP_NULL_ON_SOME_PATH_EXCEPTION](#rule-224) | 一般 | JAVA | NP_NULL_ON_SOME_PATH_EXCEPTION | 通用类（非编程规范） |
| 225 | [NP_NULL_PARAM_DEREF](#rule-225) | 一般 | JAVA | NP_NULL_PARAM_DEREF | 通用类（非编程规范） |
| 226 | [NP_NULL_PARAM_DEREF_ALL_TARGETS_DANGEROUS](#rule-226) | 一般 | JAVA | NP_NULL_PARAM_DEREF_ALL_TARGETS_DANGEROUS | 通用类（非编程规范） |
| 227 | [NP_NULL_PARAM_DEREF_NONVIRTUAL](#rule-227) | 一般 | JAVA | NP_NULL_PARAM_DEREF_NONVIRTUAL | 通用类（非编程规范） |
| 228 | [NP_TOSTRING_COULD_RETURN_NULL](#rule-228) | 一般 | JAVA | NP_TOSTRING_COULD_RETURN_NULL | 通用类（非编程规范） |
| 229 | [NP_UNWRITTEN_FIELD](#rule-229) | 一般 | JAVA | NP_UNWRITTEN_FIELD | 通用类（非编程规范） |
| 230 | [OBL_UNSATISFIED_OBLIGATION](#rule-230) | 一般 | JAVA | OBL_UNSATISFIED_OBLIGATION | 通用类（非编程规范） |
| 231 | [OBL_UNSATISFIED_OBLIGATION_EXCEPTION_EDGE](#rule-231) | 一般 | JAVA | OBL_UNSATISFIED_OBLIGATION_EXCEPTION_EDGE | 通用类（非编程规范） |
| 232 | [ODR_OPEN_DATABASE_RESOURCE](#rule-232) | 一般 | JAVA | ODR_OPEN_DATABASE_RESOURCE | 通用类（非编程规范） |
| 233 | [ODR_OPEN_DATABASE_RESOURCE_EXCEPTION_PATH](#rule-233) | 一般 | JAVA | ODR_OPEN_DATABASE_RESOURCE_EXCEPTION_PATH | 通用类（非编程规范） |
| 234 | [OS_OPEN_STREAM](#rule-234) | 一般 | JAVA | OS_OPEN_STREAM | 通用类（非编程规范） |
| 235 | [OS_OPEN_STREAM_EXCEPTION_PATH](#rule-235) | 一般 | JAVA | OS_OPEN_STREAM_EXCEPTION_PATH | 通用类（非编程规范） |
| 236 | [PT_RELATIVE_PATH_TRAVERSAL](#rule-236) | 严重 | JAVA | PT_RELATIVE_PATH_TRAVERSAL | 通用类（非编程规范） |
| 237 | [PZ_DONT_REUSE_ENTRY_OBJECTS_IN_ITERATORS](#rule-237) | 一般 | JAVA | PZ_DONT_REUSE_ENTRY_OBJECTS_IN_ITERATORS | 通用类（非编程规范） |
| 238 | [QBA_QUESTIONABLE_BOOLEAN_ASSIGNMENT](#rule-238) | 一般 | JAVA | QBA_QUESTIONABLE_BOOLEAN_ASSIGNMENT | 通用类（非编程规范） |
| 239 | [RANGE_ARRAY_INDEX](#rule-239) | 一般 | JAVA | RANGE_ARRAY_INDEX | 通用类（非编程规范） |
| 240 | [RANGE_ARRAY_OFFSET](#rule-240) | 一般 | JAVA | RANGE_ARRAY_OFFSET | 通用类（非编程规范） |
| 241 | [RANGE_STRING_INDEX](#rule-241) | 一般 | JAVA | RANGE_STRING_INDEX | 通用类（非编程规范） |
| 242 | [RC_REF_COMPARISON](#rule-242) | 一般 | JAVA | RC_REF_COMPARISON | 通用类（非编程规范） |
| 243 | [RC_REF_COMPARISON_BAD_PRACTICE](#rule-243) | 一般 | JAVA | RC_REF_COMPARISON_BAD_PRACTICE | 通用类（非编程规范） |
| 244 | [RC_REF_COMPARISON_BAD_PRACTICE_BOOLEAN](#rule-244) | 一般 | JAVA | RC_REF_COMPARISON_BAD_PRACTICE_BOOLEAN | 通用类（非编程规范） |
| 245 | [RE_BAD_SYNTAX_FOR_REGULAR_EXPRESSION](#rule-245) | 一般 | JAVA | RE_BAD_SYNTAX_FOR_REGULAR_EXPRESSION | 通用类（非编程规范） |
| 246 | [RE_CANT_USE_FILE_SEPARATOR_AS_REGULAR_EXPRESSION](#rule-246) | 一般 | JAVA | RE_CANT_USE_FILE_SEPARATOR_AS_REGULAR_EXPRESSION | 通用类（非编程规范） |
| 247 | [RE_POSSIBLE_UNINTENDED_PATTERN](#rule-247) | 一般 | JAVA | RE_POSSIBLE_UNINTENDED_PATTERN | 通用类（非编程规范） |
| 248 | [RR_NOT_CHECKED](#rule-248) | 一般 | JAVA | RR_NOT_CHECKED | 通用类（非编程规范） |
| 249 | [RS_READOBJECT_SYNC](#rule-249) | 一般 | JAVA | RS_READOBJECT_SYNC | 通用类（非编程规范） |
| 250 | [RU_INVOKE_RUN](#rule-250) | 一般 | JAVA | RU_INVOKE_RUN | 通用类（非编程规范） |
| 251 | [RV_01_TO_INT](#rule-251) | 一般 | JAVA | RV_01_TO_INT | 通用类（非编程规范） |
| 252 | [RV_ABSOLUTE_VALUE_OF_HASHCODE](#rule-252) | 一般 | JAVA | RV_ABSOLUTE_VALUE_OF_HASHCODE | 通用类（非编程规范） |
| 253 | [RV_ABSOLUTE_VALUE_OF_RANDOM_INT](#rule-253) | 一般 | JAVA | RV_ABSOLUTE_VALUE_OF_RANDOM_INT | 通用类（非编程规范） |
| 254 | [RV_CHECK_COMPARETO_FOR_SPECIFIC_RETURN_VALUE](#rule-254) | 一般 | JAVA | RV_CHECK_COMPARETO_FOR_SPECIFIC_RETURN_VALUE | 通用类（非编程规范） |
| 255 | [RV_EXCEPTION_NOT_THROWN](#rule-255) | 一般 | JAVA | RV_EXCEPTION_NOT_THROWN | 通用类（非编程规范） |
| 256 | [RV_NEGATING_RESULT_OF_COMPARETO](#rule-256) | 一般 | JAVA | RV_NEGATING_RESULT_OF_COMPARETO | 通用类（非编程规范） |
| 257 | [SA_FIELD_SELF_ASSIGNMENT](#rule-257) | 一般 | JAVA | SA_FIELD_SELF_ASSIGNMENT | 通用类（非编程规范） |
| 258 | [SA_FIELD_SELF_COMPARISON](#rule-258) | 一般 | JAVA | SA_FIELD_SELF_COMPARISON | 通用类（非编程规范） |
| 259 | [SA_FIELD_SELF_COMPUTATION](#rule-259) | 一般 | JAVA | SA_FIELD_SELF_COMPUTATION | 通用类（非编程规范） |
| 260 | [SA_LOCAL_SELF_ASSIGNMENT_INSTEAD_OF_FIELD](#rule-260) | 一般 | JAVA | SA_LOCAL_SELF_ASSIGNMENT_INSTEAD_OF_FIELD | 通用类（非编程规范） |
| 261 | [SA_LOCAL_SELF_COMPUTATION](#rule-261) | 一般 | JAVA | SA_LOCAL_SELF_COMPUTATION | 通用类（非编程规范） |
| 262 | [SBSC_USE_STRINGBUFFER_CONCATENATION](#rule-262) | 一般 | JAVA | SBSC_USE_STRINGBUFFER_CONCATENATION | 通用类（非编程规范） |
| 263 | [SC_START_IN_CTOR](#rule-263) | 一般 | JAVA | SC_START_IN_CTOR | 通用类（非编程规范） |
| 264 | [SE_METHOD_MUST_BE_PRIVATE](#rule-264) | 一般 | JAVA | SE_METHOD_MUST_BE_PRIVATE | 通用类（非编程规范） |
| 265 | [SE_NONFINAL_SERIALVERSIONID](#rule-265) | 一般 | JAVA | SE_NONFINAL_SERIALVERSIONID | 通用类（非编程规范） |
| 266 | [SE_NONLONG_SERIALVERSIONID](#rule-266) | 一般 | JAVA | SE_NONLONG_SERIALVERSIONID | 通用类（非编程规范） |
| 267 | [SE_NONSTATIC_SERIALVERSIONID](#rule-267) | 一般 | JAVA | SE_NONSTATIC_SERIALVERSIONID | 通用类（非编程规范） |
| 268 | [SE_NO_SERIALVERSIONID](#rule-268) | 一般 | JAVA | SE_NO_SERIALVERSIONID | 通用类（非编程规范） |
| 269 | [SE_NO_SUITABLE_CONSTRUCTOR](#rule-269) | 一般 | JAVA | SE_NO_SUITABLE_CONSTRUCTOR | 通用类（非编程规范） |
| 270 | [SF_DEAD_STORE_DUE_TO_SWITCH_FALLTHROUGH](#rule-270) | 一般 | JAVA | SF_DEAD_STORE_DUE_TO_SWITCH_FALLTHROUGH | 通用类（非编程规范） |
| 271 | [SF_DEAD_STORE_DUE_TO_SWITCH_FALLTHROUGH_TO_THROW](#rule-271) | 一般 | JAVA | SF_DEAD_STORE_DUE_TO_SWITCH_FALLTHROUGH_TO_THROW | 通用类（非编程规范） |
| 272 | [SIC_INNER_SHOULD_BE_STATIC](#rule-272) | 一般 | JAVA | SIC_INNER_SHOULD_BE_STATIC | 通用类（非编程规范） |
| 273 | [SIC_INNER_SHOULD_BE_STATIC_NEEDS_THIS](#rule-273) | 一般 | JAVA | SIC_INNER_SHOULD_BE_STATIC_NEEDS_THIS | 通用类（非编程规范） |
| 274 | [SI_INSTANCE_BEFORE_FINALS_ASSIGNED](#rule-274) | 一般 | JAVA | SI_INSTANCE_BEFORE_FINALS_ASSIGNED | 通用类（非编程规范） |
| 275 | [SP_SPIN_ON_FIELD](#rule-275) | 一般 | JAVA | SP_SPIN_ON_FIELD | 通用类（非编程规范） |
| 276 | [SQL_BAD_PREPARED_STATEMENT_ACCESS](#rule-276) | 一般 | JAVA | SQL_BAD_PREPARED_STATEMENT_ACCESS | 通用类（非编程规范） |
| 277 | [SQL_NONCONSTANT_STRING_PASSED_TO_EXECUTE](#rule-277) | 严重 | JAVA | SQL_NONCONSTANT_STRING_PASSED_TO_EXECUTE | 通用类（非编程规范） |
| 278 | [SS_SHOULD_BE_STATIC](#rule-278) | 一般 | JAVA | SS_SHOULD_BE_STATIC | 通用类（非编程规范） |
| 279 | [STCAL_INVOKE_ON_STATIC_CALENDAR_INSTANCE](#rule-279) | 一般 | JAVA | STCAL_INVOKE_ON_STATIC_CALENDAR_INSTANCE | 通用类（非编程规范） |
| 280 | [STCAL_INVOKE_ON_STATIC_DATE_FORMAT_INSTANCE](#rule-280) | 一般 | JAVA | STCAL_INVOKE_ON_STATIC_DATE_FORMAT_INSTANCE | 通用类（非编程规范） |
| 281 | [STCAL_STATIC_CALENDAR_INSTANCE](#rule-281) | 一般 | JAVA | STCAL_STATIC_CALENDAR_INSTANCE | 通用类（非编程规范） |
| 282 | [STCAL_STATIC_SIMPLE_DATE_FORMAT_INSTANCE](#rule-282) | 一般 | JAVA | STCAL_STATIC_SIMPLE_DATE_FORMAT_INSTANCE | 通用类（非编程规范） |
| 283 | [STI_INTERRUPTED_ON_CURRENTTHREAD](#rule-283) | 一般 | JAVA | STI_INTERRUPTED_ON_CURRENTTHREAD | 通用类（非编程规范） |
| 284 | [SecH_GTS_JAVA_ACTIVITI_JUEL](#rule-284) | 严重 | JAVA | SecH_GTS_JAVA_ACTIVITI_JUEL | 安全类（非编程规范） |
| 285 | [SecH_GTS_JAVA_BYPASS_WHITE_FILTER](#rule-285) | 严重 | JAVA | SecH_GTS_JAVA_BYPASS_WHITE_FILTER | 安全类（非编程规范） |
| 286 | [SecH_GTS_JAVA_Block_Cipher_Padding_Check](#rule-286) | 严重 | JAVA | SecH_GTS_JAVA_Block_Cipher_Padding_Check | 安全类（非编程规范） |
| 287 | [SecH_GTS_JAVA_CBB_Check_Annotation_XXMapping](#rule-287) | 提示 | JAVA | SecH_GTS_JAVA_CBB_Check_Annotation_XXMapping | 安全类（非编程规范） |
| 288 | [SecH_GTS_JAVA_CBB_Check_CSRF](#rule-288) | 提示 | JAVA | SecH_GTS_JAVA_CBB_Check_CSRF | 安全类（非编程规范） |
| 289 | [SecH_GTS_JAVA_CBB_Check_CertCheck](#rule-289) | 提示 | JAVA | SecH_GTS_JAVA_CBB_Check_CertCheck | 安全类（非编程规范） |
| 290 | [SecH_GTS_JAVA_CBB_Check_IntegrityCheck](#rule-290) | 提示 | JAVA | SecH_GTS_JAVA_CBB_Check_IntegrityCheck | 安全类（非编程规范） |
| 291 | [SecH_GTS_JAVA_CBB_Check_LogAop](#rule-291) | 提示 | JAVA | SecH_GTS_JAVA_CBB_Check_LogAop | 安全类（非编程规范） |
| 292 | [SecH_GTS_JAVA_CBB_Check_OpenRedirect](#rule-292) | 提示 | JAVA | SecH_GTS_JAVA_CBB_Check_OpenRedirect | 安全类（非编程规范） |
| 293 | [SecH_GTS_JAVA_CBB_Check_PackageScan_Us](#rule-293) | 提示 | JAVA | SecH_GTS_JAVA_CBB_Check_PackageScan_Us | 安全类（非编程规范） |
| 294 | [SecH_GTS_JAVA_CBB_Check_ParamCheck_Wsf](#rule-294) | 提示 | JAVA | SecH_GTS_JAVA_CBB_Check_ParamCheck_Wsf | 安全类（非编程规范） |
| 295 | [SecH_GTS_JAVA_CBB_Check_PasswordComplexity](#rule-295) | 提示 | JAVA | SecH_GTS_JAVA_CBB_Check_PasswordComplexity | 安全类（非编程规范） |
| 296 | [SecH_GTS_JAVA_CBB_Check_RiskDeserialization](#rule-296) | 提示 | JAVA | SecH_GTS_JAVA_CBB_Check_RiskDeserialization | 安全类（非编程规范） |
| 297 | [SecH_GTS_JAVA_CBB_Check_RiskExpression](#rule-297) | 提示 | JAVA | SecH_GTS_JAVA_CBB_Check_RiskExpression | 安全类（非编程规范） |
| 298 | [SecH_GTS_JAVA_CBB_Check_RiskFileUpload](#rule-298) | 提示 | JAVA | SecH_GTS_JAVA_CBB_Check_RiskFileUpload | 安全类（非编程规范） |
| 299 | [SecH_GTS_JAVA_CBB_Check_RiskTLS](#rule-299) | 提示 | JAVA | SecH_GTS_JAVA_CBB_Check_RiskTLS | 安全类（非编程规范） |
| 300 | [SecH_GTS_JAVA_CBB_Check_RiskXML](#rule-300) | 提示 | JAVA | SecH_GTS_JAVA_CBB_Check_RiskXML | 安全类（非编程规范） |
| 301 | [SecH_GTS_JAVA_CBB_Check_SafeAPI](#rule-301) | 提示 | JAVA | SecH_GTS_JAVA_CBB_Check_SafeAPI | 安全类（非编程规范） |
| 302 | [SecH_GTS_JAVA_CLOSE_RESOURCE](#rule-302) | 严重 | JAVA | SecH_GTS_JAVA_CLOSE_RESOURCE | 安全类（非编程规范） |
| 303 | [SecH_GTS_JAVA_Cert_PassCheck](#rule-303) | 严重 | JAVA | SecH_GTS_JAVA_Cert_PassCheck | 安全类（非编程规范） |
| 304 | [SecH_GTS_JAVA_Crypted_NoSalt](#rule-304) | 严重 | JAVA | SecH_GTS_JAVA_Crypted_NoSalt | 安全类（非编程规范） |
| 305 | [SecH_GTS_JAVA_Csv_Injection](#rule-305) | 严重 | JAVA | SecH_GTS_JAVA_Csv_Injection | 安全类（非编程规范） |
| 306 | [SecH_GTS_JAVA_Deserialization_FastJson_autoType_safeMode](#rule-306) | 严重 | JAVA | SecH_GTS_JAVA_Deserialization_FastJson_autoType_safeMode | 安全类（非编程规范） |
| 307 | [SecH_GTS_JAVA_Deserialization_Kryo](#rule-307) | 严重 | JAVA | SecH_GTS_JAVA_Deserialization_Kryo | 安全类（非编程规范） |
| 308 | [SecH_GTS_JAVA_Deserialization_YamlBeans](#rule-308) | 严重 | JAVA | SecH_GTS_JAVA_Deserialization_YamlBeans | 安全类（非编程规范） |
| 309 | [SecH_GTS_JAVA_Deserialize_Hessian](#rule-309) | 严重 | JAVA | SecH_GTS_JAVA_Deserialize_Hessian | 安全类（非编程规范） |
| 310 | [SecH_GTS_JAVA_Deserialize_SnakeYaml](#rule-310) | 严重 | JAVA | SecH_GTS_JAVA_Deserialize_SnakeYaml | 安全类（非编程规范） |
| 311 | [SecH_GTS_JAVA_电子表格_Injection](#rule-311) | 严重 | JAVA | SecH_GTS_JAVA_电子表格_Injection | 安全类（非编程规范） |
| 312 | [SecH_GTS_JAVA_电子表格_Workbook_Stream_Release](#rule-312) | 严重 | JAVA | SecH_GTS_JAVA_电子表格_Workbook_Stream_Release | 安全类（非编程规范） |
| 313 | [SecH_GTS_JAVA_Executable_Script_Check](#rule-313) | 严重 | JAVA | SecH_GTS_JAVA_Executable_Script_Check | 安全类（非编程规范） |
| 314 | [SecH_GTS_JAVA_FileChannel_Resource_Release](#rule-314) | 严重 | JAVA | SecH_GTS_JAVA_FileChannel_Resource_Release | 安全类（非编程规范） |
| 315 | [SecH_GTS_JAVA_Gzip_Over_Size](#rule-315) | 一般 | JAVA | SecH_GTS_JAVA_Gzip_Over_Size | 安全类（非编程规范） |
| 316 | [SecH_GTS_JAVA_HDFS_File_Stream_Release](#rule-316) | 严重 | JAVA | SecH_GTS_JAVA_HDFS_File_Stream_Release | 安全类（非编程规范） |
| 317 | [SecH_GTS_JAVA_HDFS_Instance](#rule-317) | 严重 | JAVA | SecH_GTS_JAVA_HDFS_Instance | 安全类（非编程规范） |
| 318 | [SecH_GTS_JAVA_Hash_Alg_Select_BC_ApacheCodec](#rule-318) | 严重 | JAVA | SecH_GTS_JAVA_Hash_Alg_Select_BC_ApacheCodec | 安全类（非编程规范） |
| 319 | [SecH_GTS_JAVA_Header_Reinforcement_CORS](#rule-319) | 严重 | JAVA | SecH_GTS_JAVA_Header_Reinforcement_CORS | 安全类（非编程规范） |
| 320 | [SecH_GTS_JAVA_Header_Reinforcement_Cache_Control](#rule-320) | 严重 | JAVA | SecH_GTS_JAVA_Header_Reinforcement_Cache_Control | 安全类（非编程规范） |
| 321 | [SecH_GTS_JAVA_Header_Reinforcement_Content_Security_Policy](#rule-321) | 严重 | JAVA | SecH_GTS_JAVA_Header_Reinforcement_Content_Security_Policy | 安全类（非编程规范） |
| 322 | [SecH_GTS_JAVA_Header_Reinforcement_Content_Type_Options](#rule-322) | 严重 | JAVA | SecH_GTS_JAVA_Header_Reinforcement_Content_Type_Options | 安全类（非编程规范） |
| 323 | [SecH_GTS_JAVA_Header_Reinforcement_Cookie_Security](#rule-323) | 一般 | JAVA | SecH_GTS_JAVA_Header_Reinforcement_Cookie_Security | 安全类（非编程规范） |
| 324 | [SecH_GTS_JAVA_Header_Reinforcement_Strict_Transport_Security](#rule-324) | 严重 | JAVA | SecH_GTS_JAVA_Header_Reinforcement_Strict_Transport_Security | 安全类（非编程规范） |
| 325 | [SecH_GTS_JAVA_Header_Reinforcement_XSS_Protection](#rule-325) | 严重 | JAVA | SecH_GTS_JAVA_Header_Reinforcement_XSS_Protection | 安全类（非编程规范） |
| 326 | [SecH_GTS_JAVA_Header_Reinforcement_X_Frame_Options](#rule-326) | 严重 | JAVA | SecH_GTS_JAVA_Header_Reinforcement_X_Frame_Options | 安全类（非编程规范） |
| 327 | [SecH_GTS_JAVA_IP_Management](#rule-327) | 严重 | JAVA | SecH_GTS_JAVA_IP_Management | 安全类（非编程规范） |
| 328 | [SecH_GTS_JAVA_Insecure_Cipher_CRC32](#rule-328) | 严重 | JAVA | SecH_GTS_JAVA_Insecure_Cipher_CRC32 | 安全类（非编程规范） |
| 329 | [SecH_GTS_JAVA_Insecure_Protocols_Ftp](#rule-329) | 严重 | JAVA | SecH_GTS_JAVA_Insecure_Protocols_Ftp | 安全类（非编程规范） |
| 330 | [SecH_GTS_JAVA_Insecure_Random_UUID_SessionId_Or_TokenId](#rule-330) | 严重 | JAVA | SecH_GTS_JAVA_Insecure_Random_UUID_SessionId_Or_TokenId | 安全类（非编程规范） |
| 331 | [SecH_GTS_JAVA_Insecure_SSL_TLS_Protocols](#rule-331) | 严重 | JAVA | SecH_GTS_JAVA_Insecure_SSL_TLS_Protocols | 安全类（非编程规范） |
| 332 | [SecH_GTS_JAVA_JDBC_Resource_Release](#rule-332) | 严重 | JAVA | SecH_GTS_JAVA_JDBC_Resource_Release | 安全类（非编程规范） |
| 333 | [SecH_GTS_JAVA_JDK_IO_Stream_Release](#rule-333) | 严重 | JAVA | SecH_GTS_JAVA_JDK_IO_Stream_Release | 安全类（非编程规范） |
| 334 | [SecH_GTS_JAVA_JasperReport_Command_Injection](#rule-334) | 严重 | JAVA | SecH_GTS_JAVA_JasperReport_Command_Injection | 安全类（非编程规范） |
| 335 | [SecH_GTS_JAVA_Jdbc_Attack_UnSafe_Url](#rule-335) | 严重 | JAVA | SecH_GTS_JAVA_Jdbc_Attack_UnSafe_Url | 安全类（非编程规范） |
| 336 | [SecH_GTS_JAVA_Jdbc_Attack_Url_Hardcoded](#rule-336) | 严重 | JAVA | SecH_GTS_JAVA_Jdbc_Attack_Url_Hardcoded | 安全类（非编程规范） |
| 337 | [SecH_GTS_JAVA_JtaTransactionManager](#rule-337) | 严重 | JAVA | SecH_GTS_JAVA_JtaTransactionManager | 安全类（非编程规范） |
| 338 | [SecH_GTS_JAVA_Log_Leak_Pwd](#rule-338) | 严重 | JAVA | SecH_GTS_JAVA_Log_Leak_Pwd | 安全类（非编程规范） |
| 339 | [SecH_GTS_JAVA_NIO_Files_CLOSE](#rule-339) | 严重 | JAVA | SecH_GTS_JAVA_NIO_Files_CLOSE | 安全类（非编程规范） |
| 340 | [SecH_GTS_JAVA_Non_Thread_Safe_Obj](#rule-340) | 严重 | JAVA | SecH_GTS_JAVA_Non_Thread_Safe_Obj | 安全类（非编程规范） |
| 341 | [SecH_GTS_JAVA_Not_Check_Domain](#rule-341) | 严重 | JAVA | SecH_GTS_JAVA_Not_Check_Domain | 安全类（非编程规范） |
| 342 | [SecH_GTS_JAVA_Office_RW_Exhaustion](#rule-342) | 严重 | JAVA | SecH_GTS_JAVA_Office_RW_Exhaustion | 安全类（非编程规范） |
| 343 | [SecH_GTS_JAVA_Plaintext_Protocol](#rule-343) | 严重 | JAVA | SecH_GTS_JAVA_Plaintext_Protocol | 安全类（非编程规范） |
| 344 | [SecH_GTS_JAVA_Prohibited_Configure_Plaintext_Password](#rule-344) | 严重 | JAVA | SecH_GTS_JAVA_Prohibited_Configure_Plaintext_Password | 安全类（非编程规范） |
| 345 | [SecH_GTS_JAVA_ReDos](#rule-345) | 一般 | JAVA | SecH_GTS_JAVA_ReDos | 安全类（非编程规范） |
| 346 | [SecH_GTS_JAVA_ReDos_Param](#rule-346) | 严重 | JAVA | SecH_GTS_JAVA_ReDos_Param | 安全类（非编程规范） |
| 347 | [SecH_GTS_JAVA_Return_Ciphertext_To_Foreground](#rule-347) | 一般 | JAVA | SecH_GTS_JAVA_Return_Ciphertext_To_Foreground | 安全类（非编程规范） |
| 348 | [SecH_GTS_JAVA_SSLContext_Unsafe_RandomSeed](#rule-348) | 严重 | JAVA | SecH_GTS_JAVA_SSLContext_Unsafe_RandomSeed | 安全类（非编程规范） |
| 349 | [SecH_GTS_JAVA_SSL_Socket_Release](#rule-349) | 严重 | JAVA | SecH_GTS_JAVA_SSL_Socket_Release | 安全类（非编程规范） |
| 350 | [SecH_GTS_JAVA_ScriptEngine_Injection](#rule-350) | 严重 | JAVA | SecH_GTS_JAVA_ScriptEngine_Injection | 安全类（非编程规范） |
| 351 | [SecH_GTS_JAVA_TOKEN_LOG](#rule-351) | 严重 | JAVA | SecH_GTS_JAVA_TOKEN_LOG | 安全类（非编程规范） |
| 352 | [SecH_GTS_JAVA_TemplateExpression_Fel](#rule-352) | 严重 | JAVA | SecH_GTS_JAVA_TemplateExpression_Fel | 安全类（非编程规范） |
| 353 | [SecH_GTS_JAVA_TemplateExpression_FreeMarker](#rule-353) | 严重 | JAVA | SecH_GTS_JAVA_TemplateExpression_FreeMarker | 安全类（非编程规范） |
| 354 | [SecH_GTS_JAVA_TemplateExpression_Groovy](#rule-354) | 严重 | JAVA | SecH_GTS_JAVA_TemplateExpression_Groovy | 安全类（非编程规范） |
| 355 | [SecH_GTS_JAVA_TemplateExpression_JEXL](#rule-355) | 严重 | JAVA | SecH_GTS_JAVA_TemplateExpression_JEXL | 安全类（非编程规范） |
| 356 | [SecH_GTS_JAVA_TemplateExpression_MVEL](#rule-356) | 严重 | JAVA | SecH_GTS_JAVA_TemplateExpression_MVEL | 安全类（非编程规范） |
| 357 | [SecH_GTS_JAVA_TemplateExpression_SpEL](#rule-357) | 严重 | JAVA | SecH_GTS_JAVA_TemplateExpression_SpEL | 安全类（非编程规范） |
| 358 | [SecH_GTS_JAVA_TemplateExpression_Thymeleaf](#rule-358) | 严重 | JAVA | SecH_GTS_JAVA_TemplateExpression_Thymeleaf | 安全类（非编程规范） |
| 359 | [SecH_GTS_JAVA_TemplateExpression_Velocity](#rule-359) | 严重 | JAVA | SecH_GTS_JAVA_TemplateExpression_Velocity | 通用类（非编程规范） |
| 360 | [SecH_GTS_JAVA_ThreadLocal_Release](#rule-360) | 严重 | JAVA | SecH_GTS_JAVA_ThreadLocal_Release | 安全类（非编程规范） |
| 361 | [SecH_GTS_JAVA_UnSafe_Ssh2_Config_Jsch](#rule-361) | 严重 | JAVA | SecH_GTS_JAVA_UnSafe_Ssh2_Config_Jsch | 安全类（非编程规范） |
| 362 | [SecH_GTS_JAVA_Unclosed_Resource_For_Sftp](#rule-362) | 严重 | JAVA | SecH_GTS_JAVA_Unclosed_Resource_For_Sftp | 安全类（非编程规范） |
| 363 | [SecH_GTS_JAVA_Unsafe_Decompression_Entry](#rule-363) | 严重 | JAVA | SecH_GTS_JAVA_Unsafe_Decompression_Entry | 安全类（非编程规范） |
| 364 | [SecH_GTS_JAVA_Unsafe_Encryption_Pwd_In_Digest](#rule-364) | 严重 | JAVA | SecH_GTS_JAVA_Unsafe_Encryption_Pwd_In_Digest | 安全类（非编程规范） |
| 365 | [SecH_GTS_JAVA_Unsafe_Encryption_Signatur_Order](#rule-365) | 严重 | JAVA | SecH_GTS_JAVA_Unsafe_Encryption_Signatur_Order | 安全类（非编程规范） |
| 366 | [SecH_GTS_JAVA_Weak_Cipher_Hash_Key_Message](#rule-366) | 严重 | JAVA | SecH_GTS_JAVA_Weak_Cipher_Hash_Key_Message | 安全类（非编程规范） |
| 367 | [SecH_GTS_JAVA_Weak_Encryption](#rule-367) | 严重 | JAVA | SecH_GTS_JAVA_Weak_Encryption | 安全类（非编程规范） |
| 368 | [SecH_GTS_JAVA_Weak_Encryption_Inadequate_RSA_Padding](#rule-368) | 严重 | JAVA | SecH_GTS_JAVA_Weak_Encryption_Inadequate_RSA_Padding | 安全类（非编程规范） |
| 369 | [SecH_GTS_JAVA_XML_Entity_Injection_SAXTransformerFactory](#rule-369) | 严重 | JAVA | SecH_GTS_JAVA_XML_Entity_Injection_SAXTransformerFactory | 安全类（非编程规范） |
| 370 | [SecH_GTS_JAVA_X_ForwardedFor_IP_Check](#rule-370) | 严重 | JAVA | SecH_GTS_JAVA_X_ForwardedFor_IP_Check | 安全类（非编程规范） |
| 371 | [SecH_GTS_JAVA_Zip_GetName_Check](#rule-371) | 严重 | JAVA | SecH_GTS_JAVA_Zip_GetName_Check | 安全类（非编程规范） |
| 372 | [SecH_GTS_JAVA_Zip_GetSize_Check](#rule-372) | 严重 | JAVA | SecH_GTS_JAVA_Zip_GetSize_Check | 安全类（非编程规范） |
| 373 | [SecH_GTS_JAVA_uploadfile_check_size](#rule-373) | 严重 | JAVA | SecH_GTS_JAVA_uploadfile_check_size | 安全类（非编程规范） |
| 374 | [SecJ_Cookie_Security_Path_not_Set](#rule-374) | 一般 | JAVA | SecJ_Cookie_Security_Path_not_Set | 安全类（非编程规范） |
| 375 | [SecJ_Privacy_Violation_Submit_By_Get_Method](#rule-375) | 一般 | JAVA | SecJ_Privacy_Violation_Submit_By_Get_Method | 安全规范规则类 |
| 376 | [SecS_GTS_JAVA_Insecure_Random_UUID_Salt](#rule-376) | 严重 | JAVA | SecS_GTS_JAVA_Insecure_Random_UUID_Salt | 安全类（非编程规范） |
| 377 | [SecS_Server_Side_Request_Forgery](#rule-377) | 严重 | JAVA | SecS_Server_Side_Request_Forgery | 安全类（非编程规范） |
| 378 | [TLW_TWO_LOCK_WAIT](#rule-378) | 一般 | JAVA | TLW_TWO_LOCK_WAIT | 通用类（非编程规范） |
| 379 | [Throw_Inside_Finally](#rule-379) | 严重 | JAVA | Throw_Inside_Finally | 安全类（非编程规范） |
| 380 | [UG_SYNC_SET_UNSYNC_GET](#rule-380) | 一般 | JAVA | UG_SYNC_SET_UNSYNC_GET | 通用类（非编程规范） |
| 381 | [UL_UNRELEASED_LOCK](#rule-381) | 一般 | JAVA | UL_UNRELEASED_LOCK | 通用类（非编程规范） |
| 382 | [UMAC_UNCALLABLE_METHOD_OF_ANONYMOUS_CLASS](#rule-382) | 一般 | JAVA | UMAC_UNCALLABLE_METHOD_OF_ANONYMOUS_CLASS | 通用类（非编程规范） |
| 383 | [UM_UNNECESSARY_MATH](#rule-383) | 一般 | JAVA | UM_UNNECESSARY_MATH | 通用类（非编程规范） |
| 384 | [UPM_UNCALLED_PRIVATE_METHOD](#rule-384) | 一般 | JAVA | UPM_UNCALLED_PRIVATE_METHOD | 通用类（非编程规范） |
| 385 | [UR_UNINIT_READ](#rule-385) | 一般 | JAVA | UR_UNINIT_READ | 通用类（非编程规范） |
| 386 | [UR_UNINIT_READ_CALLED_FROM_SUPER_CONSTRUCTOR](#rule-386) | 一般 | JAVA | UR_UNINIT_READ_CALLED_FROM_SUPER_CONSTRUCTOR | 通用类（非编程规范） |
| 387 | [UWF_UNWRITTEN_FIELD](#rule-387) | 一般 | JAVA | UWF_UNWRITTEN_FIELD | 通用类（非编程规范） |
| 388 | [UW_UNCOND_WAIT](#rule-388) | 一般 | JAVA | UW_UNCOND_WAIT | 通用类（非编程规范） |
| 389 | [VA_PRIMITIVE_ARRAY_PASSED_TO_OBJECT_VARARG](#rule-389) | 一般 | JAVA | VA_PRIMITIVE_ARRAY_PASSED_TO_OBJECT_VARARG | 通用类（非编程规范） |
| 390 | [WA_AWAIT_NOT_IN_LOOP](#rule-390) | 一般 | JAVA | WA_AWAIT_NOT_IN_LOOP | 通用类（非编程规范） |
| 391 | [WMI_WRONG_MAP_ITERATOR](#rule-391) | 一般 | JAVA | WMI_WRONG_MAP_ITERATOR | 通用类（非编程规范） |
| 392 | [WS_WRITEOBJECT_SYNC](#rule-392) | 一般 | JAVA | WS_WRITEOBJECT_SYNC | 通用类（非编程规范） |
| 393 | [Weak_Cryptographic_Hash_Salt](#rule-393) | 致命 | JAVA | Weak_Cryptographic_Hash_Salt | 安全规范规则类 |
| 394 | [XSS_REQUEST_PARAMETER_TO_SERVLET_WRITER](#rule-394) | 严重 | JAVA | XSS_REQUEST_PARAMETER_TO_SERVLET_WRITER | 通用类（非编程规范） |
| 395 | [duplication_file[JAVA]](#rule-395) | 提示 | JAVA | duplication_file | 代码坏味道类 |
| 396 | [huge_cyclomatic_complexity[JAVA]](#rule-396) | 信息 | JAVA | huge_cyclomatic_complexity | 代码坏味道类 |
| 397 | [huge_depth[JAVA]](#rule-397) | 一般 | JAVA | huge_depth | 代码坏味道类 |
| 398 | [huge_folder[JAVA]](#rule-398) | 一般 | JAVA | huge_folder | 代码坏味道类 |
| 399 | [huge_method[JAVA]](#rule-399) | 信息 | JAVA | huge_method | 代码坏味道类 |
| 400 | [huge_non_headerfile[JAVA]](#rule-400) | 提示 | JAVA | huge_non_headerfile | 代码坏味道类 |
| 401 | [redundant_code[JAVA]](#rule-401) | 一般 | JAVA | redundant_code | 代码坏味道类 |
| 402 | [安全TOP问题-PBKDF2算法迭代次数不符合预期](#rule-402) | 致命 | JAVA | Weak_Cryptographic_Hash_Iteration_Count | 安全规范规则类 |
| 403 | [安全TOP问题-SSL/TLS 连接使用默认的预加载系统证书颁发机构 (CA) 创建](#rule-403) | 一般 | JAVA | SecJ_Insecure_SSL_Overly_Broad_Certificate_Trust | 安全类（非编程规范） |
| 404 | [安全TOP问题-不安全密码算法AES](#rule-404) | 致命 | JAVA | Weak_Encryption_AES | 安全规范规则类 |
| 405 | [安全TOP问题-不安全密码算法ARCFOUR](#rule-405) | 致命 | JAVA | Weak_Encryption_ARCFOUR | 安全规范规则类 |
| 406 | [安全TOP问题-不安全密码算法Blowfish](#rule-406) | 致命 | JAVA | Weak_Encryption_Blowfish | 安全规范规则类 |
| 407 | [安全TOP问题-不安全密码算法DES](#rule-407) | 致命 | JAVA | Weak_Encryption_DES | 安全规范规则类 |
| 408 | [安全TOP问题-不安全密码算法DH](#rule-408) | 致命 | JAVA | Weak_Encryption_DH | 安全规范规则类 |
| 409 | [安全TOP问题-不安全密码算法DiffieHellman](#rule-409) | 致命 | JAVA | Weak_Encryption_DiffieHellman | 安全规范规则类 |
| 410 | [安全TOP问题-不安全密码算法ECDSA](#rule-410) | 致命 | JAVA | Weak_Hash_ECDSA | 安全规范规则类 |
| 411 | [安全TOP问题-不安全密码算法MD2](#rule-411) | 致命 | JAVA | Weak_Hash_MD2 | 安全规范规则类 |
| 412 | [安全TOP问题-不安全密码算法MD4](#rule-412) | 致命 | JAVA | Weak_Hash_MD4 | 安全规范规则类 |
| 413 | [安全TOP问题-不安全密码算法MD5](#rule-413) | 致命 | JAVA | Weak_Hash_MD5 | 安全规范规则类 |
| 414 | [安全TOP问题-不安全密码算法MD5WithRSA](#rule-414) | 致命 | JAVA | Weak_Hash_MD5WithRSA | 安全规范规则类 |
| 415 | [安全TOP问题-不安全密码算法PBKDF2WithHmacMD5](#rule-415) | 致命 | JAVA | Weak_Encryption_PBKDF2WithHmacMD5 | 安全规范规则类 |
| 416 | [安全TOP问题-不安全密码算法RC2](#rule-416) | 致命 | JAVA | Weak_Encryption_RC2 | 安全规范规则类 |
| 417 | [安全TOP问题-不安全密码算法RC4](#rule-417) | 致命 | JAVA | Weak_Encryption_RC4 | 安全规范规则类 |
| 418 | [安全TOP问题-不安全密码算法RSA](#rule-418) | 致命 | JAVA | Weak_Encryption_RSA | 安全规范规则类 |
| 419 | [安全TOP问题-不安全密码算法SHA0](#rule-419) | 致命 | JAVA | Weak_Hash_SHA0 | 安全规范规则类 |
| 420 | [安全TOP问题-不安全密码算法SHA1](#rule-420) | 致命 | JAVA | Weak_Hash_SHA1 | 安全规范规则类 |
| 421 | [安全TOP问题-不安全密码算法SHA1WithDSA](#rule-421) | 致命 | JAVA | Weak_Hash_SHA1WithDSA | 安全规范规则类 |
| 422 | [安全TOP问题-不安全密码算法SHA1WithECDSA](#rule-422) | 致命 | JAVA | Weak_Hash_SHA1WithECDSA | 安全规范规则类 |
| 423 | [安全TOP问题-不安全密码算法SHA1WithRSA](#rule-423) | 致命 | JAVA | Weak_Hash_SHA1WithRSA | 安全规范规则类 |
| 424 | [安全TOP问题-不安全的hash算法（DSA）](#rule-424) | 致命 | JAVA | Weak_Hash_DSA | 安全规范规则类 |
| 425 | [安全TOP问题-不安全的弱加密算法（DSA）](#rule-425) | 致命 | JAVA | Weak_Encryption_DSA | 安全规范规则类 |
| 426 | [安全TOP问题-使用分组加密算法时，填充方式建议选择CMS-Padding 或ISO-Padding](#rule-426) | 致命 | JAVA | Weak_Encryption_Inadequate_RSA_Padding | 安全规范规则类 |
| 427 | [安全TOP问题-使用分组密码算法时，应优先选择GCM模式](#rule-427) | 致命 | JAVA | Weak_Encryption_Insecure_Mode_AES | 安全规范规则类 |
| 428 | [安全TOP问题-忽略SSL证书验证错误漏洞](#rule-428) | 一般 | JAVA | SecJ_HTTPs_Hostname_Verifier | 安全类（非编程规范） |
| 429 | [规则 9.2.1 带有敏感数据的表单必须使用 HTTP-POST 方法提交](#rule-429) | 一般 | JAVA | SecJ_Privacy_Violation_Submit_By_Get_Method | 安全规范规则类 |

## 规则详情

<a id="rule-1"></a>

### 1. AM_CREATES_EMPTY_JAR_FILE_ENTRY

| 字段 | 内容 |
|---|---|
| 问题级别 | 一般 |
| 语言 | JAVA |
| 标签 | spotbugs |
| 关联工具规则 | AM_CREATES_EMPTY_JAR_FILE_ENTRY |
| 规则类型 | 通用类（非编程规范） |
| 预估误报率 | 20% |

**审查要点**

```text
Creates an empty jar file entry
The code calls putNextEntry(), immediatelyfollowed by a call to closeEntry(). This resultsin an empty JarFile entry. The contents of the entryshould be written to the JarFile between the calls toputNextEntry() andcloseEntry().
```

**参考信息**

- 最新更新时间：2020-05-28 19:21:00

<a id="rule-2"></a>

### 2. AT_OPERATION_SEQUENCE_ON_CONCURRENT_ABSTRACTION

| 字段 | 内容 |
|---|---|
| 问题级别 | 一般 |
| 语言 | JAVA |
| 标签 | spotbugs |
| 关联工具规则 | AT_OPERATION_SEQUENCE_ON_CONCURRENT_ABSTRACTION |
| 规则类型 | 通用类（非编程规范） |
| 预估误报率 | 10% |

**审查要点**

```text
对并发对象的调用序列可能不是原子的
此代码包含对并发对象（如ConcurrentHashMap）的调用序列，这些调用将不会以原子方式执行。
```

**修复建议**

对需保证原子性的操作序列加锁，又或者使用原子性的方法处理单个Key的值更新，例如compute、computeIfAbsent、computeIfPresent、putIfAbsent。

**正确示例**

```text
```java
ConcurrentHashMap<String, String> map = new ConcurrentHashMap<>();
Object lock = new Object();
void func1() {
// [GOOD] 对需保证原子性的操作加锁
synchronized (lock) {
map.put("key1", "value1");
map.get("key1");
map.put("key1", "value2");
map.put("key3", "value3");
}
}
void func2() {
// [GOOD] 特定业务逻辑可以使用原子性的方法更新值，避免额外加锁（推荐）
map.computeIfAbsent("key1", (k) -> "value1");
map.computeIfPresent("key2", (k) -> "newvalue2");
//还有以下方法类似
//map.compute(...)
//map.putIfAbsent(...)
}
```
```

**错误示例**

```text
```java
ConcurrentHashMap<String, String> map = new ConcurrentHashMap<>();
void func1() {
// [BAD] 虽然ConcurrentXXX集合已经是线程安全的，执行多步操作时未考虑线程同步问题
map.put("key1", "value1");
map.get("key1");
map.put("key3", "value3");
}
void func2() {
// [BAD] 判断是否存在key与给该key赋值操作不同步，在多线程环境可能会出现当前线程在判断Key不存在后，其他线程已经对Key进行了赋值了，导致value被非预期覆盖
if (!map.containsKey("key1")) {
//...
//...
map.put("key1", "value1");
}
}
```
```

**参考信息**

- 最新更新时间：2025-07-10 11:07:20

<a id="rule-3"></a>

### 3. BC_EQUALS_METHOD_SHOULD_WORK_FOR_ALL_OBJECTS

| 字段 | 内容 |
|---|---|
| 问题级别 | 一般 |
| 语言 | JAVA |
| 标签 | spotbugs |
| 关联工具规则 | BC_EQUALS_METHOD_SHOULD_WORK_FOR_ALL_OBJECTS |
| 规则类型 | 通用类（非编程规范） |
| 预估误报率 | 15% |

**审查要点**

```text
Equals method should not assume anything about the type of its argument
The equals(Object o) method shouldn't make any assumptionsabout the type of o. It should simply returnfalse if o is not the same type as this.
```

**参考信息**

- 最新更新时间：2020-05-28 19:22:47

<a id="rule-4"></a>

### 4. BC_IMPOSSIBLE_CAST

| 字段 | 内容 |
|---|---|
| 问题级别 | 一般 |
| 语言 | JAVA |
| 标签 | spotbugs |
| 关联工具规则 | BC_IMPOSSIBLE_CAST |
| 规则类型 | 通用类（非编程规范） |
| 预估误报率 | 15% |

**审查要点**

```text
Impossible cast
This cast will always throw a ClassCastException.SpotBugs tracks type information from instanceof checks,and also uses more precise information about the typesof values returned from methods and loaded from fields.Thus, it may have more precise information that justthe declared type of a variable, and can use this to determinethat a cast will always throw an exception at runtime.
```

**参考信息**

- 最新更新时间：2020-05-28 19:22:48

<a id="rule-5"></a>

### 5. BC_IMPOSSIBLE_DOWNCAST

| 字段 | 内容 |
|---|---|
| 问题级别 | 一般 |
| 语言 | JAVA |
| 标签 | spotbugs |
| 关联工具规则 | BC_IMPOSSIBLE_DOWNCAST |
| 规则类型 | 通用类（非编程规范） |
| 预估误报率 | 20% |

**审查要点**

```text
Impossible downcast
This cast will always throw a ClassCastException.The analysis believes it knowsthe precise type of the value being cast, and the attempt todowncast it to a subtype will always fail by throwing a ClassCastException.
```

**参考信息**

- 最新更新时间：2020-05-28 19:22:49

<a id="rule-6"></a>

### 6. BC_IMPOSSIBLE_DOWNCAST_OF_TOARRAY

| 字段 | 内容 |
|---|---|
| 问题级别 | 一般 |
| 语言 | JAVA |
| 标签 | spotbugs |
| 关联工具规则 | BC_IMPOSSIBLE_DOWNCAST_OF_TOARRAY |
| 规则类型 | 通用类（非编程规范） |
| 预估误报率 | 15% |

**审查要点**

```text
Impossible downcast of toArray() result
This code is casting the result of calling toArray() on a collectionto a type more specific than Object[], as in:
String[] getAsArray(Collection<String> c) { return (String[]) c.toArray();}
This will usually fail by throwing a ClassCastException. The toArray()of almost all collections return an Object[]. They can't really do anything else,since the Collection object has no reference to the declared generic type of the collection.
The correct way to do get an array of a specific type from a collection is to use c.toArray(new String[]); or c.toArray(new String[c.size()]); (the latter is slightly more efficient).
There is one common/known exception to this. The toArray()method of lists returned by Arrays.asList(...) will return a covariantlytyped array. For example, Arrays.asArray(new String[] { "a" }).toArray()will return a String []. SpotBugs attempts to detect and suppresssuch cases, but may miss some.
```

**参考信息**

- 最新更新时间：2020-05-28 19:22:49

<a id="rule-7"></a>

### 7. BC_IMPOSSIBLE_INSTANCEOF

| 字段 | 内容 |
|---|---|
| 问题级别 | 一般 |
| 语言 | JAVA |
| 标签 | spotbugs |
| 关联工具规则 | BC_IMPOSSIBLE_INSTANCEOF |
| 规则类型 | 通用类（非编程规范） |
| 预估误报率 | 15% |

**审查要点**

```text
instanceof will always return false
This instanceof test will always return false. Although this is safe, make sure it isn'tan indication of some misunderstanding or some other logic error.
```

**修复建议**

防止这类错误出现

**错误示例**

```text
public class Person {
public class Sunextends Person
{
…….
}
public boolean isPerson(){
Person obj = new Person();
if(obj instanceof Sun){
returntrue;
}
return false;
}
}
```

**参考信息**

- 链接：内部链接已省略
- 最新更新时间：2020-05-28 19:22:50

<a id="rule-8"></a>

### 8. BIT_ADD_OF_SIGNED_BYTE

| 字段 | 内容 |
|---|---|
| 问题级别 | 一般 |
| 语言 | JAVA |
| 标签 | spotbugs |
| 关联工具规则 | BIT_ADD_OF_SIGNED_BYTE |
| 规则类型 | 通用类（非编程规范） |
| 预估误报率 | 15% |

**审查要点**

```text
Bitwise add of signed byte value
Adds a byte value and a value which is known to have the 8 lower bits clear.Values loaded from a byte array are sign extended to 32 bitsbefore any bitwise operations are performed on the value.Thus, if b[0] contains the value 0xff, andx is initially 0, then the code((x << 8) + b[0]) will sign extend 0xffto get 0xffffffff, and thus give the value0xffffffff as the result.
In particular, the following code for packing a byte array into an int is badly wrong:
int result = 0;for(int i = 0; i < 4; i++) result = ((result << 8) + b[i]);
The following idiom will work instead:
int result = 0;for(int i = 0; i < 4; i++) result = ((result << 8) + (b[i] & 0xff));
```

**参考信息**

- 最新更新时间：2020-05-28 19:22:27

<a id="rule-9"></a>

### 9. BIT_IOR_OF_SIGNED_BYTE

| 字段 | 内容 |
|---|---|
| 问题级别 | 一般 |
| 语言 | JAVA |
| 标签 | spotbugs |
| 关联工具规则 | BIT_IOR_OF_SIGNED_BYTE |
| 规则类型 | 通用类（非编程规范） |
| 预估误报率 | 15% |

**审查要点**

```text
Bitwise OR of signed byte value
Loads a byte value (e.g., a value loaded from a byte array or returned by a methodwith return type byte) and performs a bitwise OR withthat value. Byte values are sign extended to 32 bitsbefore any bitwise operations are performed on the value.Thus, if b[0] contains the value 0xff, andx is initially 0, then the code((x << 8) | b[0]) will sign extend 0xffto get 0xffffffff, and thus give the value0xffffffff as the result.
In particular, the following code for packing a byte array into an int is badly wrong:
int result = 0;for(int i = 0; i < 4; i++) { result = ((result << 8) | b[i]);}
The following idiom will work instead:
int result = 0;for(int i = 0; i < 4; i++) { result = ((result << 8) | (b[i] & 0xff));}
```

**参考信息**

- 最新更新时间：2020-05-28 19:22:26

<a id="rule-10"></a>

### 10. BIT_SIGNED_CHECK

| 字段 | 内容 |
|---|---|
| 问题级别 | 一般 |
| 语言 | JAVA |
| 标签 | spotbugs |
| 关联工具规则 | BIT_SIGNED_CHECK |
| 规则类型 | 通用类（非编程规范） |
| 预估误报率 | 15% |

**审查要点**

```text
Check for sign of bitwise operation
This method compares an expression such as((event.detail & SWT.SELECTED) > 0).Using bit arithmetic and then comparing with the greater than operator canlead to unexpected results (of course depending on the value ofSWT.SELECTED). If SWT.SELECTED is a negative number, this is a candidatefor a bug. Even when SWT.SELECTED is not negative, it seems good practiceto use '!= 0' instead of '> 0'.
```

**参考信息**

- 最新更新时间：2020-05-28 19:22:27

<a id="rule-11"></a>

### 11. BIT_SIGNED_CHECK_HIGH_BIT

| 字段 | 内容 |
|---|---|
| 问题级别 | 一般 |
| 语言 | JAVA |
| 标签 | spotbugs |
| 关联工具规则 | BIT_SIGNED_CHECK_HIGH_BIT |
| 规则类型 | 通用类（非编程规范） |
| 预估误报率 | 10% |

**审查要点**

```text
Check for sign of bitwise operation involving negative number
This method compares a bitwise expression such as((val & CONSTANT) > 0) where CONSTANT is the negative number.Using bit arithmetic and then comparing with the greater than operator canlead to unexpected results. This comparison is unlikely to work as expected. The good practice isto use '!= 0' instead of '> 0'.
```

**参考信息**

- 最新更新时间：2025-06-16 15:16:17

<a id="rule-12"></a>

### 12. BSHIFT_WRONG_ADD_PRIORITY

| 字段 | 内容 |
|---|---|
| 问题级别 | 一般 |
| 语言 | JAVA |
| 标签 | spotbugs |
| 关联工具规则 | BSHIFT_WRONG_ADD_PRIORITY |
| 规则类型 | 通用类（非编程规范） |
| 预估误报率 | 15% |

**审查要点**

```text
Possible bad parsing of shift operation
The code performs an operation like (x << 8 + y). Although this might be correct, probably it was meantto perform (x << 8) + y, but shift operation hasa lower precedence, so it's actually parsed as x << (8 + y).
```

**参考信息**

- 最新更新时间：2020-05-28 19:22:53

<a id="rule-13"></a>

### 13. BX_BOXING_IMMEDIATELY_UNBOXED

| 字段 | 内容 |
|---|---|
| 问题级别 | 一般 |
| 语言 | JAVA |
| 标签 | spotbugs |
| 关联工具规则 | BX_BOXING_IMMEDIATELY_UNBOXED |
| 规则类型 | 通用类（非编程规范） |
| 预估误报率 | 15% |

**审查要点**

```text
Primitive value is boxed and then immediately unboxed
A primitive is boxed, and then immediately unboxed. This probably is due to a manual boxing in a place where an unboxed value is required, thus forcing the compilerto immediately undo the work of the boxing.
```

**参考信息**

- 最新更新时间：2020-05-28 19:21:15

<a id="rule-14"></a>

### 14. BX_BOXING_IMMEDIATELY_UNBOXED_TO_PERFORM_COERCION

| 字段 | 内容 |
|---|---|
| 问题级别 | 一般 |
| 语言 | JAVA |
| 标签 | spotbugs |
| 关联工具规则 | BX_BOXING_IMMEDIATELY_UNBOXED_TO_PERFORM_COERCION |
| 规则类型 | 通用类（非编程规范） |
| 预估误报率 | 15% |

**审查要点**

```text
Primitive value is boxed then unboxed to perform primitive coercion
A primitive boxed value constructed and then immediately converted into a different primitive type(e.g., new Double(d).intValue()). Just perform direct primitive coercion (e.g., (int) d).
```

**参考信息**

- 最新更新时间：2020-05-28 19:21:15

<a id="rule-15"></a>

### 15. BX_UNBOXING_IMMEDIATELY_REBOXED

| 字段 | 内容 |
|---|---|
| 问题级别 | 一般 |
| 语言 | JAVA |
| 标签 | spotbugs |
| 关联工具规则 | BX_UNBOXING_IMMEDIATELY_REBOXED |
| 规则类型 | 通用类（非编程规范） |
| 预估误报率 | 15% |

**审查要点**

```text
装箱会产生包装对象消耗内存，并且存在垃圾回收的负担，尤其是在循环或大规模数据处理时，频繁的拆箱和装箱会导致性能下降。
所以需要避免出现装箱的值被拆箱，然后立刻重新装箱。
常见错误编码：三目运算时，同时存在基本类型和包装类型。
```

**修复建议**

```text
针对三目运算处理时，统一表达式两边及内部赋值/计算和返回的类型，减少拆装箱。
// 使用基础类型
double good=XX;
good = (Double.compare(good,10.0001d)>0)? 10d: good;
// 使用包装类型
Double good=XX;
good = (good.compareTo(Double.valueOf(10.0001d))>0)? Double.valueOf(10d): good; // Double.valueof涉及的对象，建议定义为常量
```

**正确示例**

```text
```java
private Integer calGoodResult() {
Integer good = null;
good = (good == null)? Integer.valueOf(0): good; // [GOOD], 统一了类型，此时不会出现类型转换，提高了效率
return good;
}
```
```

**错误示例**

```text
```java
private Integer calBadResult() {
Integer bad = null;
bad = (bad == null)? 0: bad; // [BAD], bad不为null时，会被拆箱，赋值时再装箱。这是自动装箱拆箱的特性，只要运算中有不同类型，当涉及到类型转换时，编译器就会向下转型，再进行运算
return bad;
}
```
```

**参考信息**

- 最新更新时间：2024-12-23 15:31:00

<a id="rule-16"></a>

### 16. CN_IDIOM

| 字段 | 内容 |
|---|---|
| 问题级别 | 一般 |
| 语言 | JAVA |
| 标签 | spotbugs |
| 关联工具规则 | CN_IDIOM |
| 规则类型 | 通用类（非编程规范） |
| 预估误报率 | 15% |

**审查要点**

```text
Class implements Cloneable but does not define or use clone method
Class implements Cloneable but does not define or use the clone method.
```

**参考信息**

- 最新更新时间：2020-05-28 19:21:07

<a id="rule-17"></a>

### 17. CN_IDIOM_NO_SUPER_CALL

| 字段 | 内容 |
|---|---|
| 问题级别 | 一般 |
| 语言 | JAVA |
| 标签 | spotbugs |
| 关联工具规则 | CN_IDIOM_NO_SUPER_CALL |
| 规则类型 | 通用类（非编程规范） |
| 预估误报率 | 15% |

**审查要点**

```text
clone method does not call super.clone()
This non-final class defines a clone() method that does not call super.clone().If this class ("A") is extended by a subclass ("B"),and the subclass B calls super.clone(), then it is likely thatB's clone() method will return an object of type A,which violates the standard contract for clone().
If all clone() methods call super.clone(), then they are guaranteedto use Object.clone(), which always returns an object of the correct type.
```

**参考信息**

- 最新更新时间：2020-05-28 19:21:08

<a id="rule-18"></a>

### 18. CN_IMPLEMENTS_CLONE_BUT_NOT_CLONEABLE

| 字段 | 内容 |
|---|---|
| 问题级别 | 一般 |
| 语言 | JAVA |
| 标签 | spotbugs |
| 关联工具规则 | CN_IMPLEMENTS_CLONE_BUT_NOT_CLONEABLE |
| 规则类型 | 通用类（非编程规范） |
| 预估误报率 | 15% |

**审查要点**

```text
Class defines clone() but doesn't implement Cloneable
This class defines a clone() method but the class doesn't implement Cloneable.There are some situations in which this is OK (e.g., you want to control how subclassescan clone themselves), but just make sure that this is what you intended.
```

**参考信息**

- 最新更新时间：2020-05-28 19:21:07

<a id="rule-19"></a>

### 19. CO_COMPARETO_INCORRECT_FLOATING

| 字段 | 内容 |
|---|---|
| 问题级别 | 一般 |
| 语言 | JAVA |
| 标签 | spotbugs |
| 关联工具规则 | CO_COMPARETO_INCORRECT_FLOATING |
| 规则类型 | 通用类（非编程规范） |
| 预估误报率 | 15% |

**审查要点**

```text
compareTo()/compare() incorrectly handles float or double value
This method compares double or float values using pattern like this: val1 > val2 ? 1 : val1 < val2 ? -1 : 0.This pattern works incorrectly for -0.0 and NaN values which may result in incorrect sorting result or broken collection(if compared values are used as keys). Consider using Double.compare or Float.compare static methods which handle allthe special cases correctly.
```

**参考信息**

- 最新更新时间：2020-05-28 19:21:27

<a id="rule-20"></a>

### 20. DC_PARTIALLY_CONSTRUCTED

| 字段 | 内容 |
|---|---|
| 问题级别 | 一般 |
| 语言 | JAVA |
| 标签 | spotbugs |
| 关联工具规则 | DC_PARTIALLY_CONSTRUCTED |
| 规则类型 | 通用类（非编程规范） |
| 预估误报率 | 20% |

**审查要点**

```text
Possible exposure of partially initialized object
Looks like this method uses lazy field initialization with double-checked locking. While the field is correctly declared as volatile, it's possible that the internal structure of the object is changed after the field assignment, thus another thread may see the partially initialized object.
To fix this problem consider storing the object into the local variable first and save it to the volatile field only after it's fully constructed.
```

**参考信息**

- 最新更新时间：2020-05-28 19:21:19

<a id="rule-21"></a>

### 21. DE_MIGHT_IGNORE

| 字段 | 内容 |
|---|---|
| 问题级别 | 一般 |
| 语言 | JAVA |
| 标签 | spotbugs |
| 关联工具规则 | DE_MIGHT_IGNORE |
| 规则类型 | 通用类（非编程规范） |
| 预估误报率 | 15% |

**审查要点**

```text
Method might ignore exception
This method might ignore an exception.  In general, exceptions should be handled or reported in some way, or they should be thrown out of the method.
```

**修复建议**

方法有可能抛异常或者忽略异常，需要对异常进行处理,即需要在catch体中对异常进行处理

**错误示例**

try{}catch(Exception ex){}

**参考信息**

- 链接：内部链接已省略
- 最新更新时间：2020-05-28 19:21:09

<a id="rule-22"></a>

### 22. DLS_DEAD_LOCAL_INCREMENT_IN_RETURN

| 字段 | 内容 |
|---|---|
| 问题级别 | 一般 |
| 语言 | JAVA |
| 标签 | spotbugs |
| 关联工具规则 | DLS_DEAD_LOCAL_INCREMENT_IN_RETURN |
| 规则类型 | 通用类（非编程规范） |
| 预估误报率 | 15% |

**审查要点**

```text
Useless increment in return statement
This statement has a return such as return x++;.A postfix increment/decrement does not impact the value of the expression,so this increment/decrement has no effect.Please verify that this statement does the right thing.
```

**参考信息**

- 最新更新时间：2020-05-28 19:22:40

<a id="rule-23"></a>

### 23. DLS_OVERWRITTEN_INCREMENT

| 字段 | 内容 |
|---|---|
| 问题级别 | 一般 |
| 语言 | JAVA |
| 标签 | spotbugs |
| 关联工具规则 | DLS_OVERWRITTEN_INCREMENT |
| 规则类型 | 通用类（非编程规范） |
| 预估误报率 | 10% |

**审查要点**

```text
Overwritten increment
The code performs an increment operation (e.g., i++) and thenimmediately overwrites it. For example, i = i++ immediatelyoverwrites the incremented value with the original value.
```

**参考信息**

- 最新更新时间：2025-06-16 15:16:17

<a id="rule-24"></a>

### 24. DMI_ANNOTATION_IS_NOT_VISIBLE_TO_REFLECTION

| 字段 | 内容 |
|---|---|
| 问题级别 | 一般 |
| 语言 | JAVA |
| 标签 | spotbugs |
| 关联工具规则 | DMI_ANNOTATION_IS_NOT_VISIBLE_TO_REFLECTION |
| 规则类型 | 通用类（非编程规范） |
| 预估误报率 | 10% |

**审查要点**

```text
Can't use reflection to check for presence of annotation without runtime retention
Unless an annotation has itself been annotated with @Retention(RetentionPolicy.RUNTIME), the annotation can't be observed using reflection(e.g., by using the isAnnotationPresent method). .
```

**参考信息**

- 最新更新时间：2025-06-16 15:16:17

<a id="rule-25"></a>

### 25. DMI_BLOCKING_METHODS_ON_URL

| 字段 | 内容 |
|---|---|
| 问题级别 | 一般 |
| 语言 | JAVA |
| 标签 | spotbugs |
| 关联工具规则 | DMI_BLOCKING_METHODS_ON_URL |
| 规则类型 | 通用类（非编程规范） |
| 预估误报率 | 10% |

**审查要点**

```text
The equals and hashCode methods of URL are blocking
The equals and hashCodemethod of URL perform domain name resolution, this can result in a big performance hit.See http://michaelscharf.blogspot.com/2006/11/javaneturlequals-and-hashcode-make.html for more information.Consider using java.net.URI instead.
```

**参考信息**

- 最新更新时间：2025-06-16 15:16:17

<a id="rule-26"></a>

### 26. DMI_CALLING_NEXT_FROM_HASNEXT

| 字段 | 内容 |
|---|---|
| 问题级别 | 一般 |
| 语言 | JAVA |
| 标签 | spotbugs |
| 关联工具规则 | DMI_CALLING_NEXT_FROM_HASNEXT |
| 规则类型 | 通用类（非编程规范） |
| 预估误报率 | 10% |

**审查要点**

```text
hasNext method invokes next
The hasNext() method invokes the next() method. This is almost certainly wrong,since the hasNext() method is not supposed to change the state of the iterator,and the next method is supposed to change the state of the iterator.
```

**参考信息**

- 最新更新时间：2025-06-16 15:16:17

<a id="rule-27"></a>

### 27. DMI_COLLECTIONS_SHOULD_NOT_CONTAIN_THEMSELVES

| 字段 | 内容 |
|---|---|
| 问题级别 | 一般 |
| 语言 | JAVA |
| 标签 | spotbugs |
| 关联工具规则 | DMI_COLLECTIONS_SHOULD_NOT_CONTAIN_THEMSELVES |
| 规则类型 | 通用类（非编程规范） |
| 预估误报率 | 15% |

**审查要点**

```text
Collections should not contain themselves
This call to a generic collection's method would only make sense if a collection containeditself (e.g., if s.contains(s) were true). This is unlikely to be true and would causeproblems if it were true (such as the computation of the hash code resulting in infinite recursion).It is likely that the wrong value is being passed as a parameter.
```

**参考信息**

- 最新更新时间：2020-05-28 19:23:00

<a id="rule-28"></a>

### 28. DMI_COLLECTION_OF_URLS

| 字段 | 内容 |
|---|---|
| 问题级别 | 一般 |
| 语言 | JAVA |
| 标签 | spotbugs |
| 关联工具规则 | DMI_COLLECTION_OF_URLS |
| 规则类型 | 通用类（非编程规范） |
| 预估误报率 | 10% |

**审查要点**

```text
Maps and sets of URLs can be performance hogs
This method or field is or uses a Map or Set of URLs. Since both the equals and hashCodemethod of URL perform domain name resolution, this can result in a big performance hit.See http://michaelscharf.blogspot.com/2006/11/javaneturlequals-and-hashcode-make.html for more information.Consider using java.net.URI instead.
```

**参考信息**

- 最新更新时间：2025-06-16 15:16:17

<a id="rule-29"></a>

### 29. DMI_INVOKING_HASHCODE_ON_ARRAY

| 字段 | 内容 |
|---|---|
| 问题级别 | 一般 |
| 语言 | JAVA |
| 标签 | spotbugs |
| 关联工具规则 | DMI_INVOKING_HASHCODE_ON_ARRAY |
| 规则类型 | 通用类（非编程规范） |
| 预估误报率 | 10% |

**审查要点**

```text
Invocation of hashCode on an array
The code invokes hashCode on an array. Calling hashCode onan array returns the same value as System.identityHashCode, and ignoresthe contents and length of the array. If you need a hashCode thatdepends on the contents of an array a,use java.util.Arrays.hashCode(a).
```

**参考信息**

- 最新更新时间：2025-06-16 15:16:17

<a id="rule-30"></a>

### 30. DMI_INVOKING_TOSTRING_ON_ANONYMOUS_ARRAY

| 字段 | 内容 |
|---|---|
| 问题级别 | 一般 |
| 语言 | JAVA |
| 标签 | spotbugs |
| 关联工具规则 | DMI_INVOKING_TOSTRING_ON_ANONYMOUS_ARRAY |
| 规则类型 | 通用类（非编程规范） |
| 预估误报率 | 15% |

**审查要点**

```text
Invocation of toString on an unnamed array
The code invokes toString on an (anonymous) array. Calling toString on an array generates a fairly useless resultsuch as [C@16f0472. Consider using Arrays.toString to convert the array into a readableString that gives the contents of the array. See Programming Puzzlers, chapter 3, puzzle 12.
```

**修复建议**

该代码调用上匿名数组的toString（）方法，产生的结果形如[@16f0472并没有实际的意义。考虑使用Arrays.toString方法来转换成可读的字符串，提供该数组的内容数组。

**正确示例**

```text
//正确的使用为
System.out.println(Arrays.toString(a));
```

**错误示例**

```text
String[] a = { "a" };
System.out.println(a.toString());
```

**参考信息**

- 链接：内部链接已省略
- 最新更新时间：2020-05-28 19:22:54

<a id="rule-31"></a>

### 31. DMI_INVOKING_TOSTRING_ON_ARRAY

| 字段 | 内容 |
|---|---|
| 问题级别 | 一般 |
| 语言 | JAVA |
| 标签 | spotbugs |
| 关联工具规则 | DMI_INVOKING_TOSTRING_ON_ARRAY |
| 规则类型 | 通用类（非编程规范） |
| 预估误报率 | 15% |

**审查要点**

```text
Invocation of toString on an array
The code invokes toString on an array, which will generate a fairly useless resultsuch as [C@16f0472. Consider using Arrays.toString to convert the array into a readableString that gives the contents of the array. See Programming Puzzlers, chapter 3, puzzle 12.
```

**参考信息**

- 最新更新时间：2020-05-28 19:22:54

<a id="rule-32"></a>

### 32. DMI_RANDOM_USED_ONLY_ONCE

| 字段 | 内容 |
|---|---|
| 问题级别 | 一般 |
| 语言 | JAVA |
| 标签 | spotbugs |
| 关联工具规则 | DMI_RANDOM_USED_ONLY_ONCE |
| 规则类型 | 通用类（非编程规范） |
| 预估误报率 | 15% |

**审查要点**

```text
创建并且仅使用一次了Random对象
此代码创建一个java.util.Random对象，使用它生成一个随机数，然后丢弃Random对象。这会产生中等质量的随机数，并且效率低下。如果可能的话，重写代码，使得Random对象只创建一次并保存，每次需要新的随机数时调用现有的Random对象的方法来获取它。
如果重要的是生成的随机数不能被猜测，那么您绝对不能
为每个随机数创建一个新的Random；这样的值太容易被猜测了。您应该强烈考虑改用java.security.SecureRandom（并避免为每个所需的随机数分配一个新的SecureRandom）。
若是安全业务场景建议使用SecureRandom.getInstanceStrong()
1、new SecureRandom()：使用默认算法，速度快，不阻塞；
2、SecureRandom.getInstanceStrong()： 选择系统配置的最强算法，可能导致线程因缺乏熵（随机源）而阻塞；
```

**修复建议**

复用已创建的Random对象，并在安全场景下使用SecureRandom。

**正确示例**

```text
```java
private SecureRandom random = new SecureRandom();// [GOOD] DMI_RANDOM_USED_ONLY_ONCE，请在类中定义，非方法中创建
for (int i = 0; i < 10000; i++) {
random.nextInt();
}
```
```

**错误示例**

```text
```java
for (int i = 0; i < 10000; i++) {
Random random = new Random(1); // [BAD] DMI_RANDOM_USED_ONLY_ONCE
random.nextInt();
}
```
```

**参考信息**

- 链接：内部链接已省略
- 最新更新时间：2026-04-15 17:31:24

<a id="rule-33"></a>

### 33. DMI_USING_REMOVEALL_TO_CLEAR_COLLECTION

| 字段 | 内容 |
|---|---|
| 问题级别 | 一般 |
| 语言 | JAVA |
| 标签 | spotbugs |
| 关联工具规则 | DMI_USING_REMOVEALL_TO_CLEAR_COLLECTION |
| 规则类型 | 通用类（非编程规范） |
| 预估误报率 | 15% |

**审查要点**

```text
Don't use removeAll to clear a collection
If you want to remove all elements from a collection c, use c.clear,not c.removeAll(c). Calling c.removeAll(c) to clear a collectionis less clear, susceptible to errors from typos, less efficient andfor some collections, might throw a ConcurrentModificationException.
```

**参考信息**

- 最新更新时间：2020-05-28 19:23:01

<a id="rule-34"></a>

### 34. DM_BOXED_PRIMITIVE_FOR_COMPARE

| 字段 | 内容 |
|---|---|
| 问题级别 | 一般 |
| 语言 | JAVA |
| 标签 | spotbugs |
| 关联工具规则 | DM_BOXED_PRIMITIVE_FOR_COMPARE |
| 规则类型 | 通用类（非编程规范） |
| 预估误报率 | 10% |

**审查要点**

```text
Boxing a primitive to compare
A boxed primitive is created just to call compareTo method. It's more efficient to use static compare method (for double and float since Java 1.4, for other primitive types since Java 1.7) which works on primitives directly.
```

**参考信息**

- 最新更新时间：2025-06-16 15:16:17

<a id="rule-35"></a>

### 35. DM_BOXED_PRIMITIVE_FOR_PARSING

| 字段 | 内容 |
|---|---|
| 问题级别 | 一般 |
| 语言 | JAVA |
| 标签 | spotbugs |
| 关联工具规则 | DM_BOXED_PRIMITIVE_FOR_PARSING |
| 规则类型 | 通用类（非编程规范） |
| 预估误报率 | 10% |

**审查要点**

```text
Boxing/unboxing to parse a primitive
A boxed primitive is created from a String, just to extract the unboxed primitive value. It is more efficient to just call the static parseXXX method.
```

**参考信息**

- 最新更新时间：2025-06-16 15:16:17

<a id="rule-36"></a>

### 36. DM_CONVERT_CASE

| 字段 | 内容 |
|---|---|
| 问题级别 | 一般 |
| 语言 | JAVA |
| 标签 | spotbugs |
| 关联工具规则 | DM_CONVERT_CASE |
| 规则类型 | 通用类（非编程规范） |
| 预估误报率 | 15% |

**审查要点**

```text
Consider using Locale parameterized version of invoked method
A String is being converted to upper or lowercase, using the platform's default encoding. This may result in improper conversions when used with international characters. Use the
String.toUpperCase( Locale l )
String.toLowerCase( Locale l )
versions instead.
```

**参考信息**

- 最新更新时间：2025-05-27 09:58:46

<a id="rule-37"></a>

### 37. DM_DEFAULT_ENCODING

| 字段 | 内容 |
|---|---|
| 问题级别 | 一般 |
| 语言 | JAVA |
| 标签 | spotbugs |
| 关联工具规则 | DM_DEFAULT_ENCODING |
| 规则类型 | 通用类（非编程规范） |
| 预估误报率 | 15% |

**审查要点**

```text
依赖于默认编码
发现了一个调用方法的情况，该方法将执行字节到字符串（或字符串到字节）的转换，并且假设默认平台编码是合适的。这将导致应用程序行为在不同平台之间变化。请使用替代API并明确指定字符集名称或Charset对象。
```

**修复建议**

将执行字节到字符串（或字符串到字节）的转换时，需要指定编码格式

**正确示例**

```text
1、在文件读的时候需要指定文件的编码格式，如下举例
```java
public static void main(String[] args) {
try (BufferedReader reader = new BufferedReader(
new InputStreamReader(new FileInputStream("path/to/your/file.txt"), StandardCharsets.UTF_8))) {
String line;
while ((line = reader.readLine()) != null) {
System.out.println(line);
}
} catch (IOException e) {
e.printStackTrace();
}
}
```
2、在文件写入的时候也需要指定文件的编码格式，如下举例
```java
public static void main(String[] args) {
try (BufferedWriter writer = new BufferedWriter(
new OutputStreamWriter(new FileOutputStream("path/to/your/file.txt"), StandardCharsets.UTF_8))) {
// 写入多行文本
writer.write("第一行\n");
writer.write("第二行\n");
writer.write("第三行\n");
} catch (IOException e) {
e.printStackTrace();
}
}
```
3、 使用其他执行字节到字符串（或字符串到字节）的转换时，需要指定编码格式。
```

**错误示例**

```text
在这个例子中，FileReader构造函数没有提供指定字符集的选项，它将使用平台默认的字符集来读取文件。
```java
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
public class ReadFileWithoutEncoding {
public static void main(String[] args) {
try (BufferedReader reader = new BufferedReader(new FileReader("path/to/your/file.txt"))) {
String line;
while ((line = reader.readLine()) != null) {
System.out.println(line);
}
} catch (IOException e) {
e.printStackTrace();
}
}
}
```
```

**参考信息**

- 最新更新时间：2025-04-14 16:03:40

<a id="rule-38"></a>

### 38. DM_EXIT

| 字段 | 内容 |
|---|---|
| 问题级别 | 一般 |
| 语言 | JAVA |
| 标签 | spotbugs |
| 关联工具规则 | DM_EXIT |
| 规则类型 | 通用类（非编程规范） |
| 预估误报率 | 10% |

**审查要点**

```text
Method invokes System.exit(...)
Invoking System.exit shuts down the entire Java virtual machine. This should only been done when it is appropriate. Such calls make it hard or impossible for your code to be invoked by other code. Consider throwing a RuntimeException instead.
```

**参考信息**

- 最新更新时间：2025-06-16 15:16:17

<a id="rule-39"></a>

### 39. DM_FP_NUMBER_CTOR

| 字段 | 内容 |
|---|---|
| 问题级别 | 一般 |
| 语言 | JAVA |
| 标签 | spotbugs |
| 关联工具规则 | DM_FP_NUMBER_CTOR |
| 规则类型 | 通用类（非编程规范） |
| 预估误报率 | 15% |

**审查要点**

```text
Method invokes inefficient floating-point Number constructor; use static valueOf instead
Using new Double(double) is guaranteed to always result in a new object whereas Double.valueOf(double) allows caching of values to be done by the compiler, class library, or JVM. Using of cached values avoids object allocation and the code will be faster.
Unless the class must be compatible with JVMs predating Java 1.5, use either autoboxing or the valueOf() method when creating instances of Double and Float.
```

**参考信息**

- 最新更新时间：2020-05-28 19:21:14

<a id="rule-40"></a>

### 40. DM_GC

| 字段 | 内容 |
|---|---|
| 问题级别 | 一般 |
| 语言 | JAVA |
| 标签 | spotbugs |
| 关联工具规则 | DM_GC |
| 规则类型 | 通用类（非编程规范） |
| 预估误报率 | 15% |

**审查要点**

```text
Explicit garbage collection; extremely dubious except in benchmarking code
Code explicitly invokes garbage collection. Except for specific use in benchmarking, this is very dubious.
In the past, situations where people have explicitly invoked the garbage collector in routines such as close or finalize methods has led to huge performance black holes. Garbage collection can be expensive. Any situation that forces hundreds or thousands of garbage collections will bring the machine to a crawl.
```

**参考信息**

- 最新更新时间：2025-05-27 09:58:46

<a id="rule-41"></a>

### 41. DM_INVALID_MIN_MAX

| 字段 | 内容 |
|---|---|
| 问题级别 | 一般 |
| 语言 | JAVA |
| 标签 | spotbugs |
| 关联工具规则 | DM_INVALID_MIN_MAX |
| 规则类型 | 通用类（非编程规范） |
| 预估误报率 | 10% |

**审查要点**

```text
Incorrect combination of Math.max and Math.min
This code tries to limit the value bounds using the construct like Math.min(0, Math.max(100, value)). However the order of the constants is incorrect: it should be Math.min(100, Math.max(0, value)). As the result this code always produces the same result (or NaN if the value is NaN).
```

**参考信息**

- 最新更新时间：2025-06-16 15:16:17

<a id="rule-42"></a>

### 42. DM_NEW_FOR_GETCLASS

| 字段 | 内容 |
|---|---|
| 问题级别 | 一般 |
| 语言 | JAVA |
| 标签 | spotbugs |
| 关联工具规则 | DM_NEW_FOR_GETCLASS |
| 规则类型 | 通用类（非编程规范） |
| 预估误报率 | 15% |

**审查要点**

```text
Method allocates an object, only to get the class object
This method allocates an object just to call getClass() on it, in order to retrieve the Class object for it. It is simpler to just access the .class property of the class.
```

**参考信息**

- 最新更新时间：2020-05-28 19:21:16

<a id="rule-43"></a>

### 43. DM_NEXTINT_VIA_NEXTDOUBLE

| 字段 | 内容 |
|---|---|
| 问题级别 | 一般 |
| 语言 | JAVA |
| 标签 | spotbugs |
| 关联工具规则 | DM_NEXTINT_VIA_NEXTDOUBLE |
| 规则类型 | 通用类（非编程规范） |
| 预估误报率 | 15% |

**审查要点**

```text
Use the nextInt method of Random rather than nextDouble to generate a random integer
If r is a java.util.Random, you can generate a random number from 0 to n-1using r.nextInt(n), rather than using (int)(r.nextDouble() * n).
The argument to nextInt must be positive. If, for example, you want to generate a randomvalue from -99 to 0, use -r.nextInt(100).
```

**参考信息**

- 最新更新时间：2020-05-28 19:21:18

<a id="rule-44"></a>

### 44. DM_NUMBER_CTOR

| 字段 | 内容 |
|---|---|
| 问题级别 | 一般 |
| 语言 | JAVA |
| 标签 | spotbugs |
| 关联工具规则 | DM_NUMBER_CTOR |
| 规则类型 | 通用类（非编程规范） |
| 预估误报率 | 15% |

**审查要点**

```text
Method invokes inefficient Number constructor; use static valueOf instead
Using new Integer(int) is guaranteed to always result in a new object whereas Integer.valueOf(int) allows caching of values to be done by the compiler, class library, or JVM. Using of cached values avoids object allocation and the code will be faster.
Values between -128 and 127 are guaranteed to have corresponding cached instances and using valueOf is approximately 3.5 times faster than using constructor. For values outside the constant range the performance of both styles is the same.
Unless the class must be compatible with JVMs predating Java 1.5, use either autoboxing or the valueOf() method when creating instances of Long, Integer, Short, Character, and Byte.
```

**参考信息**

- 最新更新时间：2020-05-28 19:21:13

<a id="rule-45"></a>

### 45. DM_STRING_CTOR

| 字段 | 内容 |
|---|---|
| 问题级别 | 一般 |
| 语言 | JAVA |
| 标签 | spotbugs |
| 关联工具规则 | DM_STRING_CTOR |
| 规则类型 | 通用类（非编程规范） |
| 预估误报率 | 15% |

**审查要点**

```text
Method invokes inefficient new String(String) constructor
Using the java.lang.String(String) constructor wastes memory because the object so constructed will be functionally indistinguishable from the String passed as a parameter.  Just use the argument String directly.
```

**参考信息**

- 最新更新时间：2020-05-28 19:21:12

<a id="rule-46"></a>

### 46. DM_STRING_TOSTRING

| 字段 | 内容 |
|---|---|
| 问题级别 | 一般 |
| 语言 | JAVA |
| 标签 | spotbugs |
| 关联工具规则 | DM_STRING_TOSTRING |
| 规则类型 | 通用类（非编程规范） |
| 预估误报率 | 15% |

**审查要点**

```text
Method invokes toString() method on a String
Calling String.toString() is just a redundant operation. Just use the String.
```

**参考信息**

- 最新更新时间：2020-05-28 19:21:13

<a id="rule-47"></a>

### 47. DM_STRING_VOID_CTOR

| 字段 | 内容 |
|---|---|
| 问题级别 | 一般 |
| 语言 | JAVA |
| 标签 | spotbugs |
| 关联工具规则 | DM_STRING_VOID_CTOR |
| 规则类型 | 通用类（非编程规范） |
| 预估误报率 | 15% |

**审查要点**

```text
Method invokes inefficient new String() constructor
Creating a new java.lang.String object using the no-argument constructor wastes memory because the object so created will be functionally indistinguishable from the empty string constant "".  Java guarantees that identical string constants will be represented by the same String object.  Therefore, you should just use the empty string constant directly.
```

**参考信息**

- 最新更新时间：2020-05-28 19:21:12

<a id="rule-48"></a>

### 48. DM_USELESS_THREAD

| 字段 | 内容 |
|---|---|
| 问题级别 | 一般 |
| 语言 | JAVA |
| 标签 | spotbugs |
| 关联工具规则 | DM_USELESS_THREAD |
| 规则类型 | 通用类（非编程规范） |
| 预估误报率 | 15% |

**审查要点**

```text
A thread was created using the default empty run method
This method creates a thread without specifying a run method either by deriving from the Thread class, or by passing a Runnable object. This thread, then, does nothing but waste time.
```

**参考信息**

- 最新更新时间：2020-05-28 19:21:19

<a id="rule-49"></a>

### 49. DP_CREATE_CLASSLOADER_INSIDE_DO_PRIVILEGED

| 字段 | 内容 |
|---|---|
| 问题级别 | 一般 |
| 语言 | JAVA |
| 标签 | spotbugs |
| 关联工具规则 | DP_CREATE_CLASSLOADER_INSIDE_DO_PRIVILEGED |
| 规则类型 | 通用类（非编程规范） |
| 预估误报率 | 15% |

**审查要点**

```text
Classloaders should only be created inside doPrivileged block
This code creates a classloader, which needs permission if a security manage is installed. If this code might be invoked by code that does not have security permissions, then the classloader creation needs to occur inside a doPrivileged block.
```

**参考信息**

- 最新更新时间：2020-05-28 19:21:10

<a id="rule-50"></a>

### 50. DP_DO_INSIDE_DO_PRIVILEGED

| 字段 | 内容 |
|---|---|
| 问题级别 | 一般 |
| 语言 | JAVA |
| 标签 | spotbugs |
| 关联工具规则 | DP_DO_INSIDE_DO_PRIVILEGED |
| 规则类型 | 通用类（非编程规范） |
| 预估误报率 | 15% |

**审查要点**

```text
Method invoked that should be only be invoked inside a doPrivileged block
This code invokes a method that requires a security permission check. If this code will be granted security permissions, but might be invoked by code that does not have security permissions, then the invocation needs to occur inside a doPrivileged block.
```

**参考信息**

- 最新更新时间：2020-05-28 19:21:09

<a id="rule-51"></a>

### 51. EC_ARRAY_AND_NONARRAY

| 字段 | 内容 |
|---|---|
| 问题级别 | 一般 |
| 语言 | JAVA |
| 标签 | spotbugs |
| 关联工具规则 | EC_ARRAY_AND_NONARRAY |
| 规则类型 | 通用类（非编程规范） |
| 预估误报率 | 15% |

**审查要点**

```text
equals() used to compare array and nonarray
This method invokes the .equals(Object o) to compare an array and a reference that doesn't seemto be an array. If things being compared are of different types, they are guaranteed to be unequaland the comparison is almost certainly an error. Even if they are both arrays, the equals methodon arrays only determines of the two arrays are the same object.To compare thecontents of the arrays, use java.util.Arrays.equals(Object[], Object[]).
```

**参考信息**

- 最新更新时间：2025-05-27 09:58:46

<a id="rule-52"></a>

### 52. EC_BAD_ARRAY_COMPARE

| 字段 | 内容 |
|---|---|
| 问题级别 | 一般 |
| 语言 | JAVA |
| 标签 | spotbugs |
| 关联工具规则 | EC_BAD_ARRAY_COMPARE |
| 规则类型 | 通用类（非编程规范） |
| 预估误报率 | 15% |

**审查要点**

```text
Invocation of equals() on an array, which is equivalent to ==
This method invokes the .equals(Object o) method on an array. Since arrays do not override the equalsmethod of Object, calling equals on an array is the same as comparing their addresses. To compare thecontents of the arrays, use java.util.Arrays.equals(Object[], Object[]).To compare the addresses of the arrays, it would beless confusing to explicitly check pointer equality using ==.
```

**参考信息**

- 最新更新时间：2020-05-28 19:22:37

<a id="rule-53"></a>

### 53. EC_INCOMPATIBLE_ARRAY_COMPARE

| 字段 | 内容 |
|---|---|
| 问题级别 | 一般 |
| 语言 | JAVA |
| 标签 | spotbugs |
| 关联工具规则 | EC_INCOMPATIBLE_ARRAY_COMPARE |
| 规则类型 | 通用类（非编程规范） |
| 预估误报率 | 10% |

**审查要点**

```text
equals(...) used to compare incompatible arrays
This method invokes the .equals(Object o) to compare two arrays, but the arrays ofof incompatible types (e.g., String[] and StringBuffer[], or String[] and int[]).They will never be equal. In addition, when equals(...) is used to compare arrays itonly checks to see if they are the same array, and ignores the contents of the arrays.
```

**参考信息**

- 最新更新时间：2025-06-16 15:16:17

<a id="rule-54"></a>

### 54. EC_NULL_ARG

| 字段 | 内容 |
|---|---|
| 问题级别 | 一般 |
| 语言 | JAVA |
| 标签 | spotbugs |
| 关联工具规则 | EC_NULL_ARG |
| 规则类型 | 通用类（非编程规范） |
| 预估误报率 | 10% |

**审查要点**

```text
Call to equals(null)
This method calls equals(Object), passing a null value asthe argument. According to the contract of the equals() method,this call should always return false.
```

**参考信息**

- 最新更新时间：2025-06-16 15:16:17

<a id="rule-55"></a>

### 55. EC_UNRELATED_CLASS_AND_INTERFACE

| 字段 | 内容 |
|---|---|
| 问题级别 | 一般 |
| 语言 | JAVA |
| 标签 | spotbugs |
| 关联工具规则 | EC_UNRELATED_CLASS_AND_INTERFACE |
| 规则类型 | 通用类（非编程规范） |
| 预估误报率 | 15% |

**审查要点**

```text
Call to equals() comparing unrelated class and interface
This method calls equals(Object) on two references, one of which is a classand the other an interface, where neither the class nor any of itsnon-abstract subclasses implement the interface.Therefore, the objects being comparedare unlikely to be members of the same class at runtime(unless some application classes were not analyzed, or dynamic classloading can occur at runtime).According to the contract of equals(),objects of differentclasses should always compare as unequal; therefore, according to thecontract defined by java.lang.Object.equals(Object),the result of this comparison will always be false at runtime.
```

**修复建议**

```text
调用equals()比较不同的类型。
此方法调用相当于两个不同的类类型的引用，没有共同的子类（对象）。
因此，所比较的对象是不太可能在运行时相同的类成员（除非一些
应用类没有分析或动态类加载可以发生在运行时）。据
equals()的规则，不同类的对象应始终比较不平等，因此，根据由java.lang.Object.equals定义的合同（对象），FALSE将永远是比较的结果在运行时错误。
```

**错误示例**

```text
StringBuilder builder = new StringBuilder（"nihao"）;
String string = "nihao";
builder.equals（string）;
```

**参考信息**

- 链接：内部链接已省略
- 最新更新时间：2020-05-28 19:22:19

<a id="rule-56"></a>

### 56. EI_EXPOSE_STATIC_REP2

| 字段 | 内容 |
|---|---|
| 问题级别 | 一般 |
| 语言 | JAVA |
| 标签 | spotbugs |
| 关联工具规则 | EI_EXPOSE_STATIC_REP2 |
| 规则类型 | 通用类（非编程规范） |
| 预估误报率 | 15% |

**审查要点**

```text
May expose internal static state by storing a mutable object into a static field
This code stores a reference to an externally mutable object into a static field. If unchecked changes to the mutable object would compromise security or other important properties, you will need to do something different. Storing a copy of the object is better approach in many situations.
```

**参考信息**

- 最新更新时间：2020-05-28 19:21:33

<a id="rule-57"></a>

### 57. EQ_ALWAYS_FALSE

| 字段 | 内容 |
|---|---|
| 问题级别 | 一般 |
| 语言 | JAVA |
| 标签 | spotbugs |
| 关联工具规则 | EQ_ALWAYS_FALSE |
| 规则类型 | 通用类（非编程规范） |
| 预估误报率 | 15% |

**审查要点**

```text
equals method always returns false
This class defines an equals method that always returns false. This means that an object is not equal to itself, and it is impossible to create useful Maps or Sets of this class. More fundamentally, it meansthat equals is not reflexive, one of the requirements of the equals method.
The likely intended semantics are object identity: that an object is equal to itself. This is the behavior inherited from class Object. If you need to override an equals inherited from a differentsuperclass, you can use:
public boolean equals(Object o) { return this == o;}
```

**参考信息**

- 最新更新时间：2020-05-28 19:21:25

<a id="rule-58"></a>

### 58. EQ_ALWAYS_TRUE

| 字段 | 内容 |
|---|---|
| 问题级别 | 一般 |
| 语言 | JAVA |
| 标签 | spotbugs |
| 关联工具规则 | EQ_ALWAYS_TRUE |
| 规则类型 | 通用类（非编程规范） |
| 预估误报率 | 15% |

**审查要点**

```text
equals method always returns true
This class defines an equals method that always returns true. This is imaginative, but not very smart.Plus, it means that the equals method is not symmetric.
```

**参考信息**

- 最新更新时间：2020-05-28 19:21:25

<a id="rule-59"></a>

### 59. EQ_CHECK_FOR_OPERAND_NOT_COMPATIBLE_WITH_THIS

| 字段 | 内容 |
|---|---|
| 问题级别 | 一般 |
| 语言 | JAVA |
| 标签 | spotbugs |
| 关联工具规则 | EQ_CHECK_FOR_OPERAND_NOT_COMPATIBLE_WITH_THIS |
| 规则类型 | 通用类（非编程规范） |
| 预估误报率 | 15% |

**审查要点**

```text
Equals checks for incompatible operand
This equals method is checking to see if the argument is some incompatible type(i.e., a class that is neither a supertype nor subtype of the class that definesthe equals method). For example, the Foo class might have an equals methodthat looks like:
public boolean equals(Object o) { if (o instanceof Foo) return name.equals(((Foo)o).name); else if (o instanceof String) return name.equals(o); else return false;}
This is considered bad practice, as it makes it very hard to implement an equals method thatis symmetric and transitive. Without those properties, very unexpected behaviors are possible.
```

**参考信息**

- 最新更新时间：2020-05-28 19:21:22

<a id="rule-60"></a>

### 60. EQ_COMPARETO_USE_OBJECT_EQUALS

| 字段 | 内容 |
|---|---|
| 问题级别 | 一般 |
| 语言 | JAVA |
| 标签 | spotbugs |
| 关联工具规则 | EQ_COMPARETO_USE_OBJECT_EQUALS |
| 规则类型 | 通用类（非编程规范） |
| 预估误报率 | 15% |

**审查要点**

```text
这个类定义了compareTo(…)方法，但是却直接继承Object的equals()方法。
通常，compareTo方法在并且只有在equals方法返回true的时候返回0。如果没有遵守这个原则，就会出现一些奇怪和不可预测的问题。
例如，我们创建了一个TreeSet，并向其添加了三个EqComparedToUseObjectEquals实例。最后，我们尝试使用contains方法来检查集合中是否包含具有特定值的对象，但是这会产生一个警告，因为EqComparedToUseObjectEquals类没有覆盖equals方法。这个例子说明了在使用自定义排序逻辑的集合时，必须小心处理equals和hashCode方法以确保它们的实现与compareTo方法的实现一致。
```JAVA
public void test_treeset() {
TreeSet<EqComparedToUseObjectEquals> set = new TreeSet<>();
set.add(new EqComparedToUseObjectEquals("Ada", 16));
set.add(new EqComparedToUseObjectEquals("Dog", 15));
set.add(new EqComparedToUseObjectEquals("Cat", 17));
// 如果EqComparedToUseObjectEquals没有覆盖equals方法这里会报警告
Assertions.assertTrue(set.contains(new EqComparedToUseObjectEquals("Ada", 16)));
}
```
强烈建议但并不严格要求 (x.compareTo(y)==0) == (x.equals(y))。
```

**修复建议**

```text
1、在类中实现compareTo时，要同步覆写equals()方法，保证只有在equals方法返回ture的时候compareTo方法才返回0。
2、按照编程规范，覆写equals方法时，要同时覆写hashCode方法。
```

**正确示例**

```text
实现compareTo的同时，要同步覆写equals()方法和hash()方法。
```JAVA
public class EqComparedToUseObjectEquals implements Comparable<EqComparedToUseObjectEquals> {
private String name;
private int age;
public EqComparedToUseObjectEquals(String name, int age) {
this.name = name;
this.age = age;
}
public String getName() {
return this.name;
}
public int getAge() {
return this.age;
}
// 重写compareTo方法
@Override
public int compareTo(EqComparedToUseObjectEquals other) {
int nameComparison = this.name.compareTo(other.name);
return nameComparison != 0 ? nameComparison : Integer.compare(this.age, other.age);
}
// 【GOOD】实现compareTo的同时，要同步覆写equals()方法
@Override
public boolean equals(Object o) {
if (this == o) return true;
if (o == null || getClass() != o.getClass()) return false;
EqComparedToUseObjectEquals person = (EqComparedToUseObjectEquals) o;
return age == person.age &&
Objects.equals(name, person.name);
}
// 【GOOD】覆写equals方法时，要同时覆写hashCode方法
@Override
public int hashCode() {
return Objects.hash(name, age);
}
}
```
```

**错误示例**

```text
只实现了compareTo，未同步覆写equals()方法。
```JAVA
public class EqComparedToUseObjectEquals implements Comparable<EqComparedToUseObjectEquals> {
private String name;
private int age;
public EqComparedToUseObjectEquals(String name, int age) {
this.name = name;
this.age = age;
}
public String getName() {
return this.name;
}
public int getAge() {
return this.age;
}
// 缺陷：重写compareTo方法，没有重写Equals方法
@Override
public int compareTo(EqComparedToUseObjectEquals other) {
int nameComparison = this.name.compareTo(other.name);
return nameComparison != 0 ? nameComparison : Integer.compare(this.age, other.age);
}
}
```
```

**参考信息**

- 最新更新时间：2025-05-27 09:58:46

<a id="rule-61"></a>

### 61. EQ_GETCLASS_AND_CLASS_CONSTANT

| 字段 | 内容 |
|---|---|
| 问题级别 | 一般 |
| 语言 | JAVA |
| 标签 | spotbugs |
| 关联工具规则 | EQ_GETCLASS_AND_CLASS_CONSTANT |
| 规则类型 | 通用类（非编程规范） |
| 预估误报率 | 15% |

**审查要点**

```text
equals method fails for subtypes
This class has an equals method that will be broken if it is inherited by subclasses.It compares a class literal with the class of the argument (e.g., in class Fooit might check if Foo.class == o.getClass()).It is better to check if this.getClass() == o.getClass().
```

**参考信息**

- 最新更新时间：2020-05-28 19:21:24

<a id="rule-62"></a>

### 62. EQ_OTHER_NO_OBJECT

| 字段 | 内容 |
|---|---|
| 问题级别 | 一般 |
| 语言 | JAVA |
| 标签 | spotbugs |
| 关联工具规则 | EQ_OTHER_NO_OBJECT |
| 规则类型 | 通用类（非编程规范） |
| 预估误报率 | 10% |

**审查要点**

```text
equals() method defined that doesn't override equals(Object)
This class defines an equals() method, that doesn't override the normal equals(Object) method defined in the base java.lang.Object class.  Instead, it inherits an equals(Object) method from a superclass. The class should probably define a boolean equals(Object) method.
```

**参考信息**

- 最新更新时间：2025-06-16 15:16:17

<a id="rule-63"></a>

### 63. EQ_OVERRIDING_EQUALS_NOT_SYMMETRIC

| 字段 | 内容 |
|---|---|
| 问题级别 | 一般 |
| 语言 | JAVA |
| 标签 | spotbugs |
| 关联工具规则 | EQ_OVERRIDING_EQUALS_NOT_SYMMETRIC |
| 规则类型 | 通用类（非编程规范） |
| 预估误报率 | 15% |

**审查要点**

```text
equals method overrides equals in superclass and may not be symmetric
This class defines an equals method that overrides an equals method in a superclass. Both equals methodsuse instanceof in the determination of whether two objects are equal. This is fraught with peril,since it is important that the equals method is symmetrical (in other words, a.equals(b) == b.equals(a)).If B is a subtype of A, and A's equals method checks that the argument is an instanceof A, and B's equals methodchecks that the argument is an instanceof B, it is quite likely that the equivalence relation defined by thesemethods is not symmetric.
```

**参考信息**

- 最新更新时间：2020-05-28 19:21:00

<a id="rule-64"></a>

### 64. EQ_SELF_NO_OBJECT

| 字段 | 内容 |
|---|---|
| 问题级别 | 一般 |
| 语言 | JAVA |
| 标签 | spotbugs |
| 关联工具规则 | EQ_SELF_NO_OBJECT |
| 规则类型 | 通用类（非编程规范） |
| 预估误报率 | 10% |

**审查要点**

```text
Covariant equals() method defined
This class defines a covariant version of equals().  To correctly override the equals() method in java.lang.Object, the parameter of equals() must have type java.lang.Object.
```

**参考信息**

- 最新更新时间：2025-06-16 15:16:17

<a id="rule-65"></a>

### 65. EQ_SELF_USE_OBJECT

| 字段 | 内容 |
|---|---|
| 问题级别 | 一般 |
| 语言 | JAVA |
| 标签 | spotbugs |
| 关联工具规则 | EQ_SELF_USE_OBJECT |
| 规则类型 | 通用类（非编程规范） |
| 预估误报率 | 15% |

**审查要点**

```text
Covariant equals() method defined, Object.equals(Object) inherited
This class defines a covariant version of the equals() method, but inherits the normal equals(Object) method defined in the base java.lang.Object class.  The class should probably define a boolean equals(Object) method.
```

**修复建议**

```text
错误用法 -协变equals()方法定义，继承了Object.equals(Object)
类中增加public boolean equals(Object)方法的定义（见上例注释部分）。
```

**错误示例**

```text
public class Point {
private static double version = 1.0;
private transient double distance;
private int x, y;
/* public boolean equals(Object other) {
if (other == this) return true;
if (other == null) return false;
if (getClass() != other.getClass()) return false;
Point point = (Point)other;
return (x == point.x && y == point.y);
} */
}
Point p1,p2;
if(p1.equals(p2)) return ture;
Note：Regardless of which class contains the equals() method, the signature must always declare an Object argument type. Since Java's libraries look for one with an Object argument, if the signature is not correct, then the java.lang.Object method will be called instead, leading to incorrect behavior.
此处我们想比较的仅是x和y，而由于代码中未重写equals()方法，调用了Object.equals(Object)方法，对distance与version也进行了比较。
```

**参考信息**

- 链接：内部链接已省略
- 最新更新时间：2020-05-28 19:21:22

<a id="rule-66"></a>

### 66. ES_COMPARING_PARAMETER_STRING_WITH_EQ

| 字段 | 内容 |
|---|---|
| 问题级别 | 一般 |
| 语言 | JAVA |
| 标签 | spotbugs |
| 关联工具规则 | ES_COMPARING_PARAMETER_STRING_WITH_EQ |
| 规则类型 | 通用类（非编程规范） |
| 预估误报率 | 15% |

**审查要点**

不建议使用==或！=比较String类型的对象，考虑使用equals(Object)方法代替。

**修复建议**

采用equals方法来代替==或！=比较String对象

**正确示例**

```text
```java
public void goodPractice(String tokenOld, String tokenNew) {
// [GOOD] ES_COMPARING_PARAMETER_STRING_WITH_EQ
if (tokenNew.equals(tokenNew)) {
doSomething();
}
}
```
```

**错误示例**

```text
```java
public void badPractice(String tokenOld, String tokenNew) {
// [BAD] ES_COMPARING_PARAMETER_STRING_WITH_EQ
if (tokenNew == tokenNew) {
doSomething();
}
}
```
```

**参考信息**

- 最新更新时间：2020-05-28 19:21:31

<a id="rule-67"></a>

### 67. ES_COMPARING_STRINGS_WITH_EQ

| 字段 | 内容 |
|---|---|
| 问题级别 | 一般 |
| 语言 | JAVA |
| 标签 | spotbugs |
| 关联工具规则 | ES_COMPARING_STRINGS_WITH_EQ |
| 规则类型 | 通用类（非编程规范） |
| 预估误报率 | 15% |

**审查要点**

```text
使用==或!=比较String对象
这段代码使用==或!=运算符来比较java.lang.String对象的引用相等性。除非两个字符串在源文件中都是常量，或者已经使用
String.intern()方法国际化，否则相同的字符串值可能由两个不同的String对象表示。考虑使用equals(Object)方法代替。
```

**修复建议**

```text
String对象进行比较的时候：只有两种情况可以使用== or !=，这两种情况是；仅当两个字符串在源文件中都是常量或者是调用String.intern()
方法，使用String的规范化表示形式来进行比较,如果不是这两中情况的话推荐使用.equals(object)方式
```

**正确示例**

```text
```java
public class EsComparingStringsWithEq {
private final String first = new String("value");
private final String second = new String("value");
private boolean goodPractice() {
return Objects.equals(first, second); // [GOOD] ES_COMPARING_STRINGS_WITH_EQ
}
}
```
```

**错误示例**

```text
```java
String str1="java";
String str2="java";
System.out.print（str1==str2）;
// 结果：true（二者都为常量）
String str1=new String（"java"）;
String str2=new String（"java"）;
System.out.print（str1==str2）;
// 结果：false（二者为对象）
String str1="java";
String str2="blog";
String s=str1+str2;
System.out.print（s=="javablog"）;
// 结果：false（s不为常量，为对象）
String s1="java";
String s2=new String（"java"）;
System.out.print（s1.intern（）==s2.intern（））;
// 结果：true
```
```

**参考信息**

- 链接：内部链接已省略
- 最新更新时间：2020-05-28 19:21:30

<a id="rule-68"></a>

### 68. FE_TEST_IF_EQUAL_TO_NOT_A_NUMBER

| 字段 | 内容 |
|---|---|
| 问题级别 | 一般 |
| 语言 | JAVA |
| 标签 | spotbugs |
| 关联工具规则 | FE_TEST_IF_EQUAL_TO_NOT_A_NUMBER |
| 规则类型 | 通用类（非编程规范） |
| 预估误报率 | 10% |

**审查要点**

```text
Doomed test for equality to NaN
This code checks to see if a floating point value is equal to the special Not A Number value (e.g., if (x == Double.NaN)). However, because of the special semantics of NaN, no value is equal to Nan, including NaN. Thus, x == Double.NaN always evaluates to false. To check to see if a value contained in x is the special Not A Number value, use Double.isNaN(x) (or Float.isNaN(x) if x is floating point precision).
```

**参考信息**

- 最新更新时间：2025-06-16 15:16:17

<a id="rule-69"></a>

### 69. FI_EXPLICIT_INVOCATION

| 字段 | 内容 |
|---|---|
| 问题级别 | 一般 |
| 语言 | JAVA |
| 标签 | spotbugs |
| 关联工具规则 | FI_EXPLICIT_INVOCATION |
| 规则类型 | 通用类（非编程规范） |
| 预估误报率 | 10% |

**审查要点**

```text
Explicit invocation of finalizer
This method contains an explicit invocation of the finalize() method on an object.  Because finalizer methods are supposed to be executed once, and only by the VM, this is a bad idea.
If a connected set of objects beings finalizable, then the VM will invoke thefinalize method on all the finalizable object, possibly at the same time in different threads.Thus, it is a particularly bad idea, in the finalize method for a class X, invoke finalizeon objects referenced by X, because they may already be getting finalized in a separate thread.
```

**参考信息**

- 最新更新时间：2025-06-16 15:16:17

<a id="rule-70"></a>

### 70. G.CMT.02 顶层public类的Javadoc应该包含功能说明和创建日期/版本信息

| 字段 | 内容 |
|---|---|
| 问题级别 | 提示 |
| 语言 | JAVA |
| 标签 | fixbotengine-java |
| 关联工具规则 | TopClassComment |
| 规则类型 | 通用规范建议类 |
| 预估误报率 | 10% |

**审查要点**

```text
顶层public类的Javadoc中应该有功能说明、@since信息。产品自主决定是否添加@author（作者信息），其中名字（拼音或英文）可选，工号与邮箱不推荐添加到注释中；对外开源的代码不推荐添加@author。日期格式为Java 8 time包中的ISO_DATE，例如“2011-12-03”或者“2011-12-03+01:00”。
工具检查场景:
- 检查顶层pulic类的Javadoc是否有功能说明；如果没有Javadoc，这条规则不告警，会在G.CMT.01中告警
- 检查顶层pulic类的Javadoc是否包含@since标签
- 检查顶层pulic类的Javadoc的功能描述与标签之间是否只空1行，没有空行和多余空行都会告警
```

**修复建议**

增加功能说明和@since

**正确示例**

```text
/**
* 功能描述
*
* @author 王二
* @since 2012-12-22（或版本号）
*/
public class TopClass {
}
```

**错误示例**

```text
/**
* @author 王二
*/
public class TopClass {
}
```

**参考信息**

- 链接：内部链接已省略
- 最新更新时间：2021-07-21 10:49:00

<a id="rule-71"></a>

### 71. G.CMT.03 方法的Javadoc中应该包含功能说明，根据实际需要按顺序使用@param、@return、@throws标签对参数、返回值、异常进行注释

| 字段 | 内容 |
|---|---|
| 问题级别 | 提示 |
| 语言 | JAVA |
| 标签 | fixbotengine-java |
| 关联工具规则 | JavaDocFormat,JavaDocLineBreak |
| 规则类型 | 通用规范建议类 |
| 预估误报率 | 10% |

**审查要点**

```text
方法的Javadoc中应该包含功能说明，根据实际需要按顺序使用@param、@return、@throws标签对参数、返回值、异常进行注释
工具检查场景:
- 检查方法的Javadoc是否有功能说明；如果没有Javadoc，这条规则不告警，会在G.CMT.01中告警
- 检查方法的Javadoc的功能描述与标签之间是否只空1行，没有空行和多余空行都会告警
- 如果方法有参数，检查Javadoc中是否对每个参数都有@param标签
- 如果方法有返回值，检查Javadoc中是否有@return标签
- 如果方法有抛异常，检查Javadoc中是否对每个异常都有@throws标签
- 检查标签是否按照@param、@return、@throws的顺序排列
- 检查Javadoc第一行或最后一行不能有注释内容
- 例外场景：
- 不检查继承的Javadoc（ @inheritDoc）
- 不检查隐藏的Javadoc（@hide）
```

**修复建议**

增加功能说明和相应标签

**正确示例**

```text
/**
* 对示例接口的概述介绍
*
* @since 2019-01-01
*/
protected abstract class Sample {
/**
* 这是一段长注释，要根据注释内容进行合理拆分为多行注释...
* 这是第二行注释。
* 符合： 功能说明要与下面的@标签之间保留一个空行
*
* @param fox 参数fox的说明，例如：与懒狗进行挑战的狐狸对象
* @return 方法返回值的说明，例如：返回狐狸与狗的交战结果
*/
protected abstract int foo(Fox fox);
/**
* 函数的功能说明
* 符合： 功能说明要与下面的@标签之间保留一个空行
*
* @return 方法返回值的说明，例如：返回狐狸与狗的交战结果
* @throws ProblemException 异常说明，例如：懒狗死亡抛出该异常
*/
protected int bar() throws ProblemException {
// 变量注释
var aVar = ...;
// 方法注释 符合：注释要与前面的代码之间保留一个空行
doSome();
}
}
```

**错误示例**

```text
/**
*/
public void methodDemo() {
}
```

**参考信息**

- 链接：内部链接已省略
- 最新更新时间：2021-06-24 09:36:45

<a id="rule-72"></a>

### 72. G.CMT.04 不写空有格式的方法头注释

| 字段 | 内容 |
|---|---|
| 问题级别 | 提示 |
| 语言 | JAVA |
| 标签 | fixbotengine-java |
| 关联工具规则 | MethodHeadComment |
| 规则类型 | 通用规范建议类 |
| 预估误报率 | 10% |

**审查要点**

```text
对于不需要添加注释的方法无需添加空有格式的注释，这样代码更整洁。
工具检查场景:
- 检查方法的Javadoc中@param标签后面是否紧跟描述（描述的起始行必须和标签在同一行）
- 检查方法的Javadoc中@return标签后面是否紧跟描述（描述的起始行必须和标签在同一行）
- 检查方法的Javadoc中@throws标签后面是否紧跟描述（描述的起始行必须和标签在同一行）
```

**修复建议**

增加描述信息

**正确示例**

```text
/**
* 方法描述
*
* @param varDemo 示例变量
*/
public void methodDemo(int varDemo) {
}
```

**错误示例**

```text
/**
* 方法描述
*
* @param varDemo
*/
public void methodDemo(int varDemo) {
}
```

**参考信息**

- 链接：内部链接已省略
- 最新更新时间：2025-05-27 09:58:46

<a id="rule-73"></a>

### 73. G.CMT.05 文件头注释应该包含版权许可信息

| 字段 | 内容 |
|---|---|
| 问题级别 | 提示 |
| 语言 | JAVA |
| 标签 | fixbotengine-java |
| 关联工具规则 | FileHeaderComment |
| 规则类型 | 通用规范建议类 |
| 预估误报率 | 10% |

**审查要点**

```text
文件头注释应该放在package和import之前，应该包含版权许可信息，如果需要在文件头注释中增加其他内容，可以在后面以相同格式补充。版权许可不应该使用Javadoc样式或单行样式的注释，应该从文件顶头开始。如果包含“关键资产说明”类注释，则应紧随其后。
版权许可内容及格式必须如下：
中文版：
```java
/*
* 版权所有 (c) 公司技术有限公司 2012-2020.
*/
```
英文版：
```java
/*
* Copyright (c) 公司 2012-2020. All rights reserved.
*/
```
关于版本说明，应注意：
- 2012-2020 根据实际需要可以修改。
2012 是文件首次创建年份，而 2020 是最后文件修改年份。二者可以一样，如 "2020-2020"。
对文件有重大修改时，必须更新后面年份，如特性扩展，重大重构等。
- 创建年份不能大于修改年份，修改年份不能大于当前年份。
- 版权说明可以使用公司子公司，需要在使用的规则集里的G.CMT.05规则配置里设置公司名。
可以配置多个公司名，不同公司名以 | 分隔。
例如在规则配置里设置的公司名是: 海思半导体|Hisilicon Technologies Co., Ltd.
版权信息可以是
中文：版权所有 (c) 海思半导体 2012-2020
英文：Copyright (c) Hisilicon Technologies Co., Ltd. 2012-2020. All rights reserved.
- 配置选项查看路径：设置 -> 规则集 -> Java规则集 -> G.CMT.05规则 -> 选项 -> 公司名
```

**修复建议**

```text
为.java文件添加文件头注释，文件头注释中添加版权许可信息，版权许可信息中的时间信息要与实际情况保持一致。
推荐使用公司 CodeCheck插件[内部链接已省略
```

**正确示例**

```text
##### 场景1：无文件头注释
- 修复示例：
```java
/*
* Copyright (c) 公司 2022-2022. All rights reserved.
*/
package com.xxx;
public class FileHeaderComment {
public void method() {
}
}
```
```

**错误示例**

```text
##### 场景1：无文件头注释
- 错误示例：
```java
// 【不符合】无文件头注释
package com.xxx;
public class FileHeaderComment {
public void method() {
}
}
```
```

**参考信息**

- 链接：内部链接已省略
- 最新更新时间：2025-05-28 09:59:52

<a id="rule-74"></a>

### 74. G.COL.04 不要在foreach循环中通过remove()/add()方法更改集合

| 字段 | 内容 |
|---|---|
| 问题级别 | 严重 |
| 语言 | JAVA |
| 标签 | fixbotengine-java |
| 关联工具规则 | ForeachAddOrRemove |
| 规则类型 | 通用规范规则类 |
| 预估误报率 | 15% |

**审查要点**

```text
java.util.concurrent包之外的（非concurrent）集合在foreach循环中不要更改，否则可能会导致ConcurrentModificationException。当需要遍历集合并删除部分元素时，可采用removeIf()方法或Iterator的remove()方法。个别集合（例如CopyOnWriteArrayList）的Iterator中remove()方法会抛出UnsupportedOperationException。
```

**修复建议**

使用迭代器

**正确示例**

```text
// 使用Java 8 Collection中的removeIf方法
list.removeIf(item -> shouldDelete(item));
// 使用Iterator删除元素
Iterator<String> iterator = list.iterator();
while (iterator.hasNext()) {
String item = iterator.next();
if (shouldDelete(item)) {
iterator.remove();
}
}
```

**错误示例**

```text
以下代码，某些场景下可正常删除集合中的元素，但大部分场景下会抛出ConcurrentModificationException。
for (String item : list) {
if (shouldDelete(item)) {
list.remove(item);
}
}
```

**参考信息**

- 链接：内部链接已省略
- 最新更新时间：2021-06-24 09:36:57

<a id="rule-75"></a>

### 75. G.CON.01 对共享变量做同步访问控制时需避开同步陷阱

| 字段 | 内容 |
|---|---|
| 问题级别 | 致命 |
| 语言 | JAVA |
| 标签 | CodeMars,SecBrella |
| 关联工具规则 | Forbid_Synchronize_Concurrency_Object,Forbid_Synchronize_getClass_Object,Insecure_Instance_Lock,Forbid_Synchronize_Reused_Object,SecJ_Lock_Object_Not_Final |
| 规则类型 | 安全规范建议类 |
| CWE 信息 | 413 |
| 预估误报率 | 20% |

**审查要点**

用作锁的对象声明时，必须添加final修饰符，防止进行多线程同步期间由于被指向其他对象导致无法正确实现同步。

**修复建议**

锁对象必须使用final修饰。

**正确示例**

```text
##### 场景1：锁对象使用final修饰
- 修复示例1：锁对象使用final修饰。
```java
public class SomeSharedResource {
private final Object lock = new Object();
public void updateResource() {
synchronized (lock) {
// 更新共享的资源
...
}
}
}
```
```

**错误示例**

```text
##### 场景1：锁对象使用final修饰
- 错误示例：锁对象不使用final修饰
```java
public class SomeSharedResource {
// 【POTENTIAL FLAW】 非final修饰
private Object lock = new Object();
public void updateResource() {
synchronized (lock) {
// 更新共享的资源
...
}
}
}
```
```

**参考信息**

- 链接：内部链接已省略
- 最新更新时间：2021-09-15 20:16:54

<a id="rule-76"></a>

### 76. G.CON.02 在异常条件下，保证释放已持有的锁

| 字段 | 内容 |
|---|---|
| 问题级别 | 严重 |
| 语言 | JAVA |
| 标签 | SecBrella |
| 关联工具规则 | SecS_Unreleased_Resource_Synchronization |
| 规则类型 | 安全规范规则类 |
| CWE 信息 | 411 |
| 预估误报率 | 50% |

**审查要点**

一个线程中没有正确释放持有的锁会导致其他线程无法获取该锁对象，导致阻塞。在发生异常时，需要确保程序正确释放当前持有的锁。在异常条件下，同步方法或者块同步中使用的对象内置锁会自动释放。但是大多数的Java锁对象并不是Closeable，无法使用try-with-resources功能自动释放，在这种情况下需要主动释放锁。

**修复建议**

在异常条件下，同步方法或者块同步中使用的对象手动是否内置锁

**正确示例**

```text
public final class Foo {
private final Lock lock = new ReentrantLock();
public void correctReleaseLock() {
lock.lock();
try {
doSomething();
} catch (MyBizException ex) {
// 处理异常
} finally {
lock.unlock();
}
}
private void doSomething() throws MyBizException {
...
}
}
```

**错误示例**

```text
public final class Foo {
private final Lock lock = new ReentrantLock();
public void incorrectReleaseLock() {
try {
lock.lock();
doSomething();
lock.unlock();
} catch (MyBizException ex) {
// 处理异常
}
}
private void doSomething() throws MyBizException {
...
}
}
```

**参考信息**

- 链接：内部链接已省略
- 最新更新时间：2021-11-17 14:59:22

<a id="rule-77"></a>

### 77. G.CON.04 避免使用不正确形式的双重检查锁

| 字段 | 内容 |
|---|---|
| 问题级别 | 一般 |
| 语言 | JAVA |
| 标签 | SecBrella |
| 关联工具规则 | SecJ_Double_Checked_Locking |
| 规则类型 | 安全规范规则类 |
| 预估误报率 | 20% |

**审查要点**

Double-checked locking 是一种不正确的用法，并不能达到预期目标。

**修复建议**

把 instance 声明为 volatile ，当一个线程初始化 Singleton 对象时，会在这个线程和其他任何获取该实例的线程之间建立起happens-before关系。避免使用到未初始化完全的对象引用。

**正确示例**

```text
final class Singleton {
private static volatile Singleton instance = null;
private static final Object LOCK = new Object();
private Singleton() {
...
}
public static Singleton getSingletonInstance() {
if (instance == null) {
synchronized (LOCK) {
if (instance == null) {
instance = new Singleton();
}
}
}
return instance;
}
}
上述示例中，把instance声明为volatile，当一个线程初始化Singleton对象时，会在这个线程和其他任何获取该实例的线程之间建立起happens-before关系。避免使用到未初始化完全的对象引用。
```

**错误示例**

```text
final class Singleton {
private static Singleton instance = null;
private static final Object LOCK = new Object();
private Singleton() {
...
}
public static Singleton getSingletonInstance() {
if (instance == null) {
synchronized (LOCK) {
if (instance == null) {
instance = new Singleton();
}
}
}
return instance;
}
}
上述示例中，当一个线程完成对成员属性instance的赋值时，并未完成对象的完全初始化，也就是说此时另外一个并发线程调用getSingletonInstance()会得到一个指向Singleton对象的非空引用，然而该对象的数据成员可能是默认值，而不是构造方法中设置的值。
```

**参考信息**

- 链接：内部链接已省略
- 最新更新时间：2021-07-07 17:12:52

<a id="rule-78"></a>

### 78. G.CON.05 禁止使用非线程安全的方法来覆写线程安全的方法

| 字段 | 内容 |
|---|---|
| 问题级别 | 一般 |
| 语言 | JAVA |
| 标签 | SecBrella |
| 关联工具规则 | SecJ_Non_Synchronized_Method_Overrides_Synchronized_Method |
| 规则类型 | 安全规范规则类 |
| 预估误报率 | 20% |

**审查要点**

父类声明方法 ，以保证当多个线程访问相同实例时的正确行为。应将所有重写方法声明为 ，否则可能会发生意外行为。

**修复建议**

如果某个子方法的父方法声明为 synchronized，则必须将该子方法也声明为 synchronized或者用私有常量锁锁定

**正确示例**

```text
package CodeCorrectness.NonSynchronizedMethodOverridesSynchronizedMethod;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
public class SynchronizedMethodClass {
private static final Logger LOGGER = LoggerFactory.getLogger(SynchronizedMethodClass.class);
public synchronized void bad() {
for (int i = 0; i < 10; i++) {
LOGGER.info("info {}", i);
}
}
}
public class CodeCorrectness_NonSynchronizedMethodOverridesSynchronizedMethod_SynchronizedThis_02 extends SynchronizedThisClass {
private static final Logger LOGGER =
LoggerFactory.getLogger(CodeCorrectness_NonSynchronizedMethodOverridesSynchronizedMethod_SynchronizedThis_02.class);
public synchronized void bad() {
for (int i = 0; i < 10; i++) {
LOGGER.info("info {}", i);
}
}
}
```

**错误示例**

```text
package CodeCorrectness.NonSynchronizedMethodOverridesSynchronizedMethod;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
public class SynchronizedMethodClass {
private static final Logger LOGGER = LoggerFactory.getLogger(SynchronizedMethodClass.class);
public synchronized void bad() {
for (int i = 0; i < 10; i++) {
LOGGER.info("info {}", i);
}
}
}
package CodeCorrectness.NonSynchronizedMethodOverridesSynchronizedMethod;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
public class CodeCorrectness_NonSynchronizedMethodOverridesSynchronizedMethod_SynchronizedThis_02 extends SynchronizedThisClass {
private static final Logger LOGGER =
LoggerFactory.getLogger(CodeCorrectness_NonSynchronizedMethodOverridesSynchronizedMethod_SynchronizedThis_02.class);
/* POTENTIAL FLAW: 不应使用非同步方法覆盖同步方法。 */
public void bad() {
for (int i = 0; i < 10; i++) {
LOGGER.info("info {}", i);
}
}
}
```

**参考信息**

- 链接：内部链接已省略
- 最新更新时间：2021-09-16 15:40:46

<a id="rule-79"></a>

### 79. G.CON.06 使用新并发工具代替wait()和notify()

| 字段 | 内容 |
|---|---|
| 问题级别 | 一般 |
| 语言 | JAVA |
| 标签 | fixbotengine-java |
| 关联工具规则 | DoNotUseNotify |
| 规则类型 | 通用规范规则类 |
| 预估误报率 | 15% |

**审查要点**

```text
Java 5开始提供了更高级的并发工具，这些工具可以有效替代wait()和notify()。新开发的代码应该优先使用这些并发工具。
这些高级的并发工具主要位于java.util.concurrent中，包括：
Executor Framework：可参考G.CON.12 避免不加控制地创建新线程，应该使用线程池来管控资源;
并发集合（Concurrent Collection）：提供了高性能的并发实现的集合接口，在其内部实现了同步管理，不需要额外加锁，常用的并发集合包括ConcurrentHashMap、ConcurrentSkipListSet、ConcurrentLinkedQueue等；
同步器（Synchronizer）：为每种特定的同步需求提供了解决方案，常用的同步器包括Phaser、CountDownLatch、Semaphore等。
```

**修复建议**

使用notifyAll()代替notify()

**正确示例**

```text
public void test() {
notifyAll();
}
```

**错误示例**

```text
public void test() {
notify();
}
```

**参考信息**

- 链接：内部链接已省略
- 最新更新时间：2021-07-21 10:34:44

<a id="rule-80"></a>

### 80. G.CON.07 创建新线程时必须指定线程名

| 字段 | 内容 |
|---|---|
| 问题级别 | 一般 |
| 语言 | JAVA |
| 标签 | fixbotengine-java |
| 关联工具规则 | ThreadName |
| 规则类型 | 通用规范规则类 |
| 预估误报率 | 15% |

**审查要点**

推荐使用线程池管理线程，有些场景必须单独创建线程时，应遵循本规则。指定线程名可以给问题定位带来很多方便。日志或者dump文件中会包含线程的名字，但缺省的线程名Thread-n无法区分出是哪个线程，不便于问题定位。

**修复建议**

设置线程名

**正确示例**

```text
Thread t1 = new Thread();
t1.setName(name1);
t1.start();
Thread t2 = new Thread();
t2.setName(name2);
t2.start();
```

**错误示例**

```text
Thread t1 = new Thread();
t1.start();
Thread t2 = new Thread();
t2.start();
t2.setName(name);
```

**参考信息**

- 链接：内部链接已省略
- 最新更新时间：2025-05-27 09:58:46

<a id="rule-81"></a>

### 81. G.CON.08 使用Thread对象的setUncaughtExceptionHandler方法注册未捕获异常处理者

| 字段 | 内容 |
|---|---|
| 问题级别 | 严重 |
| 语言 | JAVA |
| 标签 | fixbotengine-java |
| 关联工具规则 | ThreadException |
| 规则类型 | 通用规范规则类 |
| 预估误报率 | 15% |

**审查要点**

```text
Java多线程程序中，所有线程都不允许抛出未捕获的checked exception，也就是说各个线程需要自己把自己的checked exception处理掉。但是无法避免未捕获的RuntimeException。当子线程抛出异常时，子线程会结束，但主线程不会知道，因为主线程通过try-catch是无法捕获子线程异常的。
Thread对象提供了setUncaughtExceptionHandler方法用来获取线程中产生的异常。还可以使用Thread.setDefaultUncaughtExceptionHandler，为所有线程设置默认异常处理方法。
应注意的是，在执行周期性任务例如ScheduledExecutorService时，为了程序的健壮性，可考虑在提交的Runnable的run方法内捕获高层级的异常。
ScheduledExecutorService的各种schedule方法，可以通过其返回的ScheduledFuture对象获取其异常。
```

**修复建议**

设置异常处理方法

**正确示例**

```text
public class TestUncaughtException {
public static void main(String[] args) {
TestThread thread = new TestThread("meaningful-name");
thread.setUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler() {
@Override
public void uncaughtException(Thread tr, Throwable ex) {
System.out.println(tr.getName() + " : " + ex.getMessage());
}
});
thread.start();
}
public static class TestThread extends Thread {
public TestThread(String name) {
super.setName(name);
}
@Override
public void run() {
throw new RuntimeException("just a test");
}
}
}
```

**错误示例**

```text
Thread thread = new Thread("meaningful-name");
thread.start();
```

**参考信息**

- 链接：内部链接已省略
- 最新更新时间：2021-06-24 09:36:56

<a id="rule-82"></a>

### 82. G.CON.09 不要依赖线程调度器、线程优先级和yield()方法

| 字段 | 内容 |
|---|---|
| 问题级别 | 一般 |
| 语言 | JAVA |
| 标签 | fixbotengine-java |
| 关联工具规则 | ThreadControl |
| 规则类型 | 通用规范规则类 |
| 预估误报率 | 15% |

**审查要点**

```text
Java中的线程调度，是基于操作系统以及JVM的实现，在不同的操作系统中，或者不同厂商的JVM（如Oracle、IBM等），即使是同一套代码，其多线程的调度机制也是不一样的。因此，在多线程的程序中，不要依赖于系统的线程调度器来决定程序的逻辑运作，如果程序依赖于线程调度器来达到正确性或者性能要求，会导致不可移植。
线程的优先级是高度依赖于系统的。当虚拟机依赖于系统的线程实现机制时，Java线程的优先级会被映射到系统的线程优先级上，Java线程优先级的数量会发生变化，甚至可能被忽略。所以程序功能的正确性不能依赖于线程的优先级。
而Thread.yield()对线程调度器仅仅是个提示，不保证确定的效果，因此代码也不能依赖Thread.yield()方法。
```

**修复建议**

不要使用线程方法setPriority()、Thread.yield()以及Thread.sleep(0)

**正确示例**

```text
##### 场景1：
- 修复示例：
```java
public class ProducerConsumerExample {
private static final Lock lock = new ReentrantLock();
private static final Condition product = lock.newCondition();
private static final Condition consume = lock.newCondition();
public static void main(String[] args) {
LinkedList<Integer> buffer = new LinkedList<>();
int maxSize = 10;
Thread producer = new Thread(new Producer(buffer, maxSize));
Thread consumer = new Thread(new Consumer(buffer));
producer.start();
consumer.start();
}
static class Producer implements Runnable {
private LinkedList<Integer> buffer;
private int maxSize;
public Producer(LinkedList<Integer> buffer, int maxSize) {
this.buffer = buffer;
this.maxSize = maxSize;
}
public void run() {
int num = 0;
while (true) {
try {
lock.lock();
while (buffer.size() == maxSize) {
try {
product.await();
} catch (InterruptedException e) {
e.printStackTrace();
}
}
buffer.add(num++);
System.out.println("Produced: " + num);
try {
Thread.sleep(1000);
} catch (InterruptedException e) {
e.printStackTrace();
}
consume.signal();
} finally {
lock.unlock();
}
}
}
}
static class Consumer implements Runnable {
private final LinkedList<Integer> buffer;
public Consumer(LinkedList<Integer> buffer) {
this.buffer = buffer;
}
public void run() {
while (true) {
try {
lock.lock();
while (buffer.isEmpty()) {
try {
consume.await();
} catch (InterruptedException e) {
e.printStackTrace();
}
}
int num = buffer.removeFirst();
System.out.println("Consumed: " + num);
try {
Thread.sleep(1000);
} catch (InterruptedException e) {
e.printStackTrace();
}
product.signal();
} finally {
lock.unlock();
}
}
}
}
}
```
```

**错误示例**

```text
public void test(String name) {
Thread t = new Thread(name);
t.start();
Thread.yield();
}
...
public void test(String name) {
Thread t = new Thread(name);
t.setPriority(0);
}
...
public void test(String name) {
Thread t = new Thread(name);
t.start();
Thread.sleep(0);
}
```

**参考信息**

- 链接：内部链接已省略
- 最新更新时间：2021-06-24 09:36:56

<a id="rule-83"></a>

### 83. G.CON.11 禁止使用Thread.stop()来终止线程

| 字段 | 内容 |
|---|---|
| 问题级别 | 严重 |
| 语言 | JAVA |
| 标签 | CodeMars,fixbotengine-java |
| 关联工具规则 | Forbid_Thread_Stop |
| 规则类型 | 安全规范规则类 |
| CWE 信息 | 572 |
| 预估误报率 | 20% |

**审查要点**

```text
线程在正常退出时，会维持类的不变性。某些线程API最初是用来帮助线程的暂停、恢复和终止，但随后因为设计上的缺陷而被废弃。例如，Thread.stop()方法会导致线程立即抛出一个ThreadDeath异常，这通常会停止线程。调用Thread.stop()会造成一个线程非正常释放它所获得的所有锁，可能会暴露这些锁保护的对象，使这些对象处于一个不一致的状态中。
```

**修复建议**

禁止调用Thread.stop()

**正确示例**

```text
public static void testGood() throws InterruptedException {
Thread thread = new Thread();
thread.start();
// ...
thread.interrupt();
}
```

**错误示例**

```text
public static void testBad() throws InterruptedException {
Thread thread = new Thread();
thread.start();
// ...
/* POTENTIAL FLAW: 禁止使用Thread.stop()来终止线程 */
thread.stop();
}
```

**参考信息**

- 链接：内部链接已省略
- 最新更新时间：2021-09-15 20:12:23

<a id="rule-84"></a>

### 84. G.CON.13 线程池中的任务结束后必须清理其自定义的ThreadLocal变量

| 字段 | 内容 |
|---|---|
| 问题级别 | 一般 |
| 语言 | JAVA |
| 标签 | SecBrella |
| 关联工具规则 | SecJ_Uncleaned_ThreadLocal |
| 规则类型 | 安全规范规则类 |
| 预估误报率 | 20% |

**审查要点**

线程池技术允许重新使用线程以减少线程创建开销，由于线程的复用，可能会存在线程中的变量的状态被上一个线程改变的场景，造成脏数据问题，另外由于线程的复用导致变量不会自动回收，不主动清理会导致内存泄露问题。因此必须保证线程池中每个线程使用的变量被正确初始化，线程结束后被正确清理。

**修复建议**

线程结束后必须调用ThreadLocal变量的remove方法。

**正确示例**

```text
public class TestThreadLocal {
public static void main(String[] args) {
ThreadPoolExecutor pool = new ThreadPoolExecutor(1, 2, 100,
TimeUnit.MILLISECONDS, new LinkedBlockingQueue<Runnable>(),
Executors.defaultThreadFactory(), new ThreadPoolExecutor.CallerRunsPolicy());
for (int i = 0; i < 20; i++) {
pool.execute(new TestThreadLocalTask());
}
}
}
class TestThreadLocalTask implements Runnable {
private static ThreadLocal<Integer> localValue = new ThreadLocal<>();
@Override
public void run() {
localValue.set(STATE1);
try {
...
localValue.set(STATE3);
...
} finally {
localValue.remove(); // 需要执行remove方法清理线程局部变量，避免内存泄露
}
}
}
```

**错误示例**

```text
private static class TestThreadLocalTask implements Runnable {
/* POTENTIAL FLAW: 线程池中的线程结束后必须清理自定义的ThreadLocal变量 */
private static ThreadLocal<Integer> localValue = new ThreadLocal<>();
@Override
public void run() {
localValue.set(0);
try {
// ...
localValue.set(1);
// ...
} finally {
// 需要执行remove方法清理线程局部变量，（避免内存泄露）
}
}
}
```

**参考信息**

- 链接：内部链接已省略
- 最新更新时间：2025-05-27 09:58:46

<a id="rule-85"></a>

### 85. G.CTL.01 不要在控制性条件表达式中执行赋值操作或执行复杂的条件判断

| 字段 | 内容 |
|---|---|
| 问题级别 | 一般 |
| 语言 | JAVA |
| 标签 | fixbotengine-java |
| 关联工具规则 | ConditionalExpression |
| 规则类型 | 通用规范规则类 |
| 预估误报率 | 15% |

**审查要点**

```text
控制性条件表达式常用于if、while、for、?:等条件判断中。
在控制性条件表达式中执行赋值或执行复杂的条件判断，常常导致意料之外的行为，且代码的可读性非常差。
复杂的条件判断是指在一个条件表达式中boolean运算符数量超过3。对于复杂的条件判断建议封装到一个独立的方法中，通过具有描述性的方法名让代码阅读者更容易理解复杂判断的目的，另外也方便对独立方法中的复杂条件判断逐步进行优化，最终使代码主流程和判断逻辑更加清晰可读。
```

**修复建议**

拆分控制条件表达式

**正确示例**

```text
boolean isFoo = someBoolean; // 在上面赋值，if条件判断中直接使用
if (isFoo)
public void fun(boolean isBar) {
boolean isFoo = isBar; // 在上面赋值，if条件判断中直接使用
if (isFoo) {
...
}
}
public void fun(boolean isBar, boolean isFlag) {
boolean isFoo = isBar; // 在上面赋值，while条件判断直接使用
while (isFoo && isFlag) {
...
}
}
if (isOk()) {
...
}
public boolean isOk() {
return a && b && c && d && e;
}
```

**错误示例**

```text
if (isFoo = false) // 在控制性判断中赋值不易理解
if (isFoo == false) // 冗余不简洁，容易出错
if (false == isFoo) // 冗余不简洁，容易出错
public void fun(boolean isBar) {
boolean isFoo;
if (isFoo = isBar) {
...
}
}
public void fun(boolean isBar, boolean isFlag) {
boolean isFoo;
while ((isFoo = isBar) && isFlag) {
...
}
}
if (a && b && c && d && e) {
...
}
```

**参考信息**

- 链接：内部链接已省略
- 最新更新时间：2021-06-24 09:36:51

<a id="rule-86"></a>

### 86. G.CTL.03 switch语句要有default分支

| 字段 | 内容 |
|---|---|
| 问题级别 | 一般 |
| 语言 | JAVA |
| 标签 | fixbotengine-java |
| 关联工具规则 | SwitchMustHaveDefault |
| 规则类型 | 通用规范规则类 |
| 预估误报率 | 15% |

**审查要点**

```text
每个switch语句都应该包含一个default分支，即使default分支没有业务逻辑代码。default分支中没有业务逻辑代码时，可以记录一条日志或抛出异常等，如：log("unknown condition")、throw new IllegalStateException("non-exhaustive cases")等。
```

**修复建议**

增加default分支

**正确示例**

```text
switch(var) {
case 1:
break;
default:
break;
}
```

**错误示例**

```text
switch(var) {
case 1:
break;
}
```

**参考信息**

- 链接：内部链接已省略
- 最新更新时间：2021-06-24 09:36:52

<a id="rule-87"></a>

### 87. G.CTL.04 循环必须保证可正确终止

| 字段 | 内容 |
|---|---|
| 问题级别 | 严重 |
| 语言 | JAVA |
| 标签 | fixbotengine-java |
| 关联工具规则 | EmptyInfiniteLoop |
| 规则类型 | 通用规范规则类 |
| 预估误报率 | 15% |

**审查要点**

```text
`while(true)`循环语句中如果缺少break，可能会导致循环无法终止。
另外，代码中更不应该出现空的无限循环，编写一个空的循环体，不会完成具体功能，反倒可能会消耗CPU；另一方面，如果刻意编写空循环来消耗CPU，却又可能被编译器或者JIT优化而消除。
例外场景：在线程的run方法里使用无限循环。
```

**修复建议**

要为循环语句设置合理的终止条件，或添加合理终止循环的语句。

**正确示例**

```text
##### 场景1：缺少break语句导致无限循环
- 修复示例1：
```java
public void notAlwaysRun() {
while (true) {
if(condition()) {
doSomething();
break;
} else{
doSomethingElse();
}
}
}
```
- 修复示例2：
```java
public void notAlwaysRun() {
int index = 0;
while (index < 100) {
if(condition(index)) {
doSomething();
}
index ++;
}
}
```
```

**错误示例**

```text
##### 场景1：缺少break语句导致无限循环
- 错误示例：
```java
public void alwaysRun() {
while (true) {
if(condition()) {
doSomething();
} else{
doSomethingElse();
}
}
}
```
```

**参考信息**

- 链接：内部链接已省略
- 最新更新时间：2025-05-21 19:09:41

<a id="rule-88"></a>

### 88. G.CTL.06 禁止switch语句中直接嵌套switch

| 字段 | 内容 |
|---|---|
| 问题级别 | 严重 |
| 语言 | JAVA |
| 标签 | fixbotengine-java |
| 关联工具规则 | NestedSwitch |
| 规则类型 | 通用类（非编程规范） |
| 预估误报率 | 15% |

**审查要点**

switch语句中直接嵌套switch语句，会增加代码的复杂度，代码的可读性也会变差。

**修复建议**

禁止switch语句中直接嵌套switch

**正确示例**

```text
...
switch (condition1) {
case "case1":
doSomething(condition2);
break;
case "case2":
doSomethingElse();
break;
default:
doSomethingDefault();
}
...
private void doSomething(String condition2) {
switch (condition2) {
case "case1":
doSomethingCase1();
break;
case "case2":
doSomethingCase2();
break;
default:
doSomethingDefault();
}
}
```

**错误示例**

```text
switch (condition1) {
case "case11":
switch (condition2) {
case "case21":
doSomethingCase1();
break;
case "case22":
doSomethingCase2();
break;
default:
doSomethingDefault();
}
break;
case "case12":
doSomething();
break;
default:
doSomethingDefault();
}
```

**参考信息**

- 链接：内部链接已省略
- 最新更新时间：2025-05-27 09:58:46

<a id="rule-89"></a>

### 89. G.DCL.01 每行声明一个变量

| 字段 | 内容 |
|---|---|
| 问题级别 | 一般 |
| 语言 | JAVA |
| 标签 | fixbotengine-java |
| 关联工具规则 | MultipleVariableDeclarations |
| 规则类型 | 通用规范规则类 |
| 预估误报率 | 15% |

**审查要点**

每行的变量声明（类属性或局部变量）都只声明一个变量。

**修复建议**

每个变量声明一行

**正确示例**

```text
int length;
int result;
```

**错误示例**

int length, result;

**参考信息**

- 链接：内部链接已省略
- 最新更新时间：2021-06-24 09:36:48

<a id="rule-90"></a>

### 90. G.DCL.03 禁止C风格的数组声明

| 字段 | 内容 |
|---|---|
| 问题级别 | 一般 |
| 语言 | JAVA |
| 标签 | fixbotengine-java |
| 关联工具规则 | NoCStyleArrayName |
| 规则类型 | 通用规范规则类 |
| 预估误报率 | 15% |

**审查要点**

```text
数组类型由数据元素类型紧跟中括号（[]）组成，数组声明格式应该是String[] nonEmptyArray，而不是String nonEmptyArray[]。
数组初始化的排版可以有三种写法：
1.变量、类型、成员都在一行容纳下的：
String[] nonEmptyArray = {"these", "can", "change"};
2.类型与成员分成不同行但成员一行容纳下的:
new int[] {
0, 1, 2, 3
}
3.类型与成员分成不同行但成员一行容纳不下的:
new int[] {
0, 1, 10,
2, 3, 20
}
注：初始化数组时，数组中的最后一个元素后不要添加逗号，例如String[] nonEmptyArray = {"these", "can", "change",};。
```

**修复建议**

数组元素类型紧跟中括号（[]）

**正确示例**

String[] strs;

**错误示例**

String strs[];

**参考信息**

- 链接：内部链接已省略
- 最新更新时间：2021-06-24 09:36:49

<a id="rule-91"></a>

### 91. G.DCL.04 避免枚举常量序号的产生依赖于ordinal()方法

| 字段 | 内容 |
|---|---|
| 问题级别 | 一般 |
| 语言 | JAVA |
| 标签 | fixbotengine-java |
| 关联工具规则 | Ordinal |
| 规则类型 | 通用规范建议类 |
| 预估误报率 | 15% |

**审查要点**

```text
Java枚举类型通过ordinal()方法返回枚举常量的排列序号。默认情况下，序号是根据声明顺序从0开始累加，但某些情况下我们希望指定某些枚举常量为某个固定值以代表特殊意义。（例如，键盘某个按键的具体编码）返回该固定值的方法不能基于ordinal()方法来实现。
工具检查场景:
- 检查枚举类中是否调用了ordinal方法
```

**修复建议**

覆写orinal方法

**正确示例**

```text
enum Keyboard {
MOUSE_KEY_LEFT(1),
MOUSE_KEY_RIGHT(2),
MOUSE_KEY_CANCEL(4),
MOUSE_KEY_MIDDLE(8);
private final int mouseKeyValue;
Keyboard(int value) {
this.mouseKeyValue = value;
}
public int getMouseKeyValue() {
return mouseKeyValue;
}
}
上述示例中，重写了枚举的构造方法，需要为枚举常量显式指定固定值。当新增枚举常量时，避免了原有枚举常量值发生变化。
```

**错误示例**

```text
enum Keyboard {
MOUSE_KEY_LEFT,
MOUSE_KEY_RIGHT,
MOUSE_KEY_CANCEL,
MOUSE_KEY_MIDDLE;
public int getMouseKeyValue() {
return ordinal() + 1;
}
}
上述示例中，在新增枚举常量时可能会导致原有枚举常量的值发生变化。
```

**参考信息**

- 链接：内部链接已省略
- 最新更新时间：2025-05-27 09:58:46

<a id="rule-92"></a>

### 92. G.DCL.05 禁止将mutable对象声明为public static final

| 字段 | 内容 |
|---|---|
| 问题级别 | 一般 |
| 语言 | JAVA |
| 标签 | fixbotengine-java |
| 关联工具规则 | Mutable |
| 规则类型 | 通用规范规则类 |
| 预估误报率 | 15% |

**审查要点**

```text
使用public static final的意图是定义一个常量。如果用其修饰一个mutable（可变）对象，极易产生不当使用，造成功能异常
工具检查场景：
- 检查是否使用new初始化一个mutable的对象，并赋值给一个常量
```

**修复建议**

使用immutable对象

**正确示例**

```text
// 使用Collections.unmodifiableList()以保证EMPTY_RESULT_LIST不可变
public static final List<String> EMPTY_RESULT_LIST =
Collections.unmodifiableList(new ArrayList<>());
// 更自然的写法：Collections.emptyList()
public static final List<String> EMPTY_RESULT_LIST = Collections.emptyList();
```

**错误示例**

```text
public static final List<String> EMPTY_RESULT_LIST = new ArrayList<>();
public static final List<String> RESULT_LIST = Arrays.asList("result1", "result2");
```

**参考信息**

- 链接：内部链接已省略
- 最新更新时间：2021-09-15 15:02:01

<a id="rule-93"></a>

### 93. G.EDV.01 禁止直接使用外部数据来拼接SQL语句

| 字段 | 内容 |
|---|---|
| 问题级别 | 严重 |
| 语言 | JAVA |
| 标签 | SecBrella |
| 关联工具规则 | SecJ_SQL_Injection_Mybatis,SecS_SQL_Injection |
| 规则类型 | 安全规范规则类 |
| CWE 信息 | 89 |
| 预估误报率 | 20% |

**审查要点**

```text
secBrella告警清理指导链接:内部链接已省略
SQL injection 错误在以下情况下发生：1）数据从一个不可信赖的数据源进入程序；2）数据用于动态地构造一个 SQL 查询。
```

**修复建议**

请查看告警清理指导：内部链接已省略

**正确示例**

```text
<?xml version="1.0" encoding="UTF-8" ?>
<!DOCTYPE mapper
PUBLIC "-//mybatis.org//DTD Mapper 3.0//EN"
"http://mybatis.org/dtd/mybatis-3-mapper.dtd">
<mapper namespace="com.example.mybatis.IUserDao">
<delete id="deleteUser" parameterType="com.example.pojo.User">
delete from user where username like '%#{username}%'
</delete>
<delete id="deleteUserBySearch" parameterType="com.example.pojo.User">
delete from user
<where>
<if test="address!=null">AND address = #{address}</if>
<if test="username!=null">AND username like '%#{username}%'</if>
</where>
</delete>
</mapper>
```

**错误示例**

```text
<?xml version="1.0" encoding="UTF-8" ?>
<!DOCTYPE mapper
PUBLIC "-//mybatis.org//DTD Mapper 3.0//EN"
"http://mybatis.org/dtd/mybatis-3-mapper.dtd">
<mapper namespace="com.example.mybatis.IUserDao">
<!-- /* POTENTIAL FLAW: 通过不可信来源的输入构建动态 SQL 指令，攻击者就能够修改指令的含义或者执行任意 SQL 命令。变量名称周围的 # 字符表示 MyBatis 将使用 userName 变量创建参数化查询。但是，MyBatis 还允许使用 $ 字符将变量直接连接到 SQL 指令，使其易受 SQL injection 攻击。 */ -->
<delete id="deleteUser" parameterType="com.example.pojo.User">
delete from user where username like '%${username}%'
</delete>
<delete id="deleteUserBySearch" parameterType="com.example.pojo.User">
delete from user
<where>
<if test="address!=null">AND address = #{address}</if>
<!-- /* POTENTIAL FLAW: 通过不可信来源的输入构建动态 SQL 指令，攻击者就能够修改指令的含义或者执行任意 SQL 命令。变量名称周围的 # 字符表示 MyBatis 将使用 userName 变量创建参数化查询。但是，MyBatis 还允许使用 $ 字符将变量直接连接到 SQL 指令，使其易受 SQL injection 攻击。 */ -->
<if test="username!=null">AND username like '%${username}%'</if>
</where>
</delete>
</mapper>
```

**参考信息**

- 链接：内部链接已省略
- 最新更新时间：2021-09-16 11:24:27

<a id="rule-94"></a>

### 94. G.EDV.02 禁止直接使用外部数据构造格式化字符串

| 字段 | 内容 |
|---|---|
| 问题级别 | 严重 |
| 语言 | JAVA |
| 标签 | SecBrella |
| 关联工具规则 | SecS_Denial_of_Service |
| 规则类型 | 安全规范规则类 |
| 预估误报率 | 30% |

**审查要点**

```text
secBrella告警清理指导链接:内部链接已省略
Java中的Format可以将对象按指定的格式转为某种格式的字符串，格式化字符串可以控制最终字符串的长度、内容、样式，当格式化字符串中指定的格式与格式对象不匹配时还可能会抛出异常。当攻击者可以直接控制格式化字符串时，可导致信息泄露、拒绝服务、系统功能异常等风险。
```

**修复建议**

请查看告警清理指导：内部链接已省略

**正确示例**

```text
public String formatInfo() {
String value = getData();
return String.format(""my format: %s"", value);
}
上述示例中，格式化字符串不含外部数据。
```

**错误示例**

```text
public String formatInfo(String formatStr) {
String value = getData();
return String.format(formatStr, value));
}
String formatStr = req.getParameter("format");
String formattedValue = formatInfo(formatStr);
上述示例中，直接使用外部指定的格式对字符串数据进行格式化，当外部指定的格式为非字符类型如%d，会导致格式化操作出现异常。
```

**参考信息**

- 链接：内部链接已省略
- 最新更新时间：2025-05-27 09:58:46

<a id="rule-95"></a>

### 95. G.EDV.03 禁止直接向Runtime.exec() 方法或java.lang.ProcessBuilder 类传递外部数据

| 字段 | 内容 |
|---|---|
| 问题级别 | 严重 |
| 语言 | JAVA |
| 标签 | SecBrella |
| 关联工具规则 | SecS_OGNL_Expression_Injection,SecS_Command_Injection |
| 规则类型 | 安全类（非编程规范） |
| CWE 信息 | 77,78,93 |
| 预估误报率 | 20% |

**审查要点**

```text
外部数据的定义请参考**【编程规范】**：外部数据的范围包括但不限于：网络、用户输入（包括命令行、界面）、命令行、文件（包括程序的配置文件）、环境变量、进程间通信（包括管道、消息、共享内存、socket等、RPC）、跨信任域方法参数（对于API）等。
直接使用外部数据构造命令行时，恶意用户可向命令行中注入额外的命令语句（命令注入）或向命名语句中注入额外的参数（参数注入）。
```

**修复建议**

```text
针对命令注入问题，常用的防护措施包括：
- **对外部数据进行校验**：外部数据用于拼接命令行时，可使用白名单方式对外部数据进行校验，保证外部数据中不含注入风险的特殊字符；
- **对外部数据中存在命令注入风险的特殊字符进行转义**：在执行命令行时，如果输入校验不能禁止有风险的特殊字符，需先外部输入进行转义处理，转义后的字段拼接命令行可有效防止命令注入的产生。对于转义的API，推荐使用公司 WSF安全框架中的API，另外也可以使用业界开源组件esapi中的API。
```

**正确示例**

```text
##### 场景1：
- 修复示例1：外部数据用于拼接命令行时，可使用白名单方式对外部数据进行校验，保证外部数据中不含注入风险的特殊字符。
```java
// str值来自用户输入
public void doSomething() {
String data = System.getenv("data");
String osCommand;
if (System.getProperty("os.name").toLowerCase().indexOf("win") >= 0) {
osCommand = "c:\\WINDOWS\\SYSTEM32\\cmd.exe /c dir ";
} else {
osCommand = "/bin/ls ";
}
Process process = null;
try {
// 使用自定义校验方法校验外部输入的数据
if (!validateData(data)) {
throw new IllegalArgumentException("...");
}
process = Runtime.getRuntime().exec(osCommand + data);
} catch (IOException e) {
IO.writeLine(e.getMessage());
}
...
}
private boolean validateData(String data) { // 自定义校验函数
/* 自定义校验逻辑，建议使用白名单或正则的方式来过滤出正确的数据 */
}
```
- 修复示例2：对外部数据中存在命令注入风险的特殊字符进行转义
```java
String encodeIp = HWEncoder.encodeForOS(new WindowsCodec(), ip);
String cmd = "cmd.exe /c ping " + encodeIp;
Runtime rt = Runtime.getRuntime();
Process proc = rt.exec(cmd);
...
```
```

**错误示例**

```text
##### 场景1：
- 错误示例：对于外部数据直接调用exec，未做合法性校验
```java
public void doSomething() {
String data = System.getenv("data");
String osCommand;
if (System.getProperty("os.name").toLowerCase().indexOf("win") >= 0) {
osCommand = "c:\\WINDOWS\\SYSTEM32\\cmd.exe /c dir ";
} else {
osCommand = "/bin/ls ";
}
Process process = null;
try {
// 【POTENTIAL FLAW】command injection
process = Runtime.getRuntime().exec(osCommand + data);
...
} catch (IOException e) {
IO.writeLine(e.getMessage());
}
...
}
```
```

**参考信息**

- 链接：内部链接已省略
- 最新更新时间：2024-08-20 11:44:09

<a id="rule-96"></a>

### 96. G.EDV.04 禁止直接使用外部数据来拼接XML

| 字段 | 内容 |
|---|---|
| 问题级别 | 严重 |
| 语言 | JAVA |
| 标签 | SecBrella |
| 关联工具规则 | SecS_XML_Injection |
| 规则类型 | 安全规范规则类 |
| CWE 信息 | 91 |
| 预估误报率 | 30% |

**审查要点**

```text
secBrella告警清理指导链接:内部链接已省略
XML injection 会在以下情况中出现：1) 数据从一个不可信赖的数据源进入程序; 2) 数据写入到 XML 文档中。应用程序通常使用 XML 来存储数据或发送消息。当 XML 用于存储数据时，XML 文档通常会像数据库一样进行处理，而且可能会包含敏感信息。XML 消息通常在 web 服务中使用，也可用于传输敏感信息。XML 消息甚至还可用于发送身份验证凭据。
```

**修复建议**

请查看告警清理指导：内部链接已省略

**正确示例**

```text
白名单校验
private void createXMLStream(BufferedOutputStream outStream, User user)
throws IOException {
// 仅当userID只包含字母、数字和下划线时写入XML字符串
if (!Pattern.matches("[_a-bA-B0-9]+", user.getUserId())) {
// 处理错误
}
if (!Pattern.matches("[_a-bA-B0-9]+", user.getDescription())) {
// 处理错误
}
String xmlString = "<user><id>" + user.getUserId()
+ "</id><role>operator</role><description>"
+ user.getDescription() + "</description></user>";
outStream.write(xmlString.getBytes(StandardCharsets.UTF_8));
outStream.flush();
}
```

**错误示例**

```text
String data;
data = ""; /* Initialize data */
/* Read data from cookies */
{
Cookie cookieSources[] = request.getCookies();
if (cookieSources != null) {
data = cookieSources[0].getValue();
}
}
CharacterData characterData = getCharacterData();
/* POTENTIAL FLAW: XML Injection */
characterData.appendData(data);
```

**参考信息**

- 链接：内部链接已省略
- 最新更新时间：2021-07-07 17:12:53

<a id="rule-97"></a>

### 97. G.EDV.05 防止解析来自外部的XML导致的外部实体（XML External Entity）攻击

| 字段 | 内容 |
|---|---|
| 问题级别 | 严重 |
| 语言 | JAVA |
| 标签 | CodeMars |
| 关联工具规则 | XXE_DocumentHelper,XXE_XMLReaderFactory,XXE_SAXTransformerFactory,XXE_DocumentBuilderFactoryImpl,XXE_SchemaFactory,XXE_DocumentBuilderFactory,XXE_SAXBuilder,XXE_TransformerFactory,XXE_SAXReader,XXE_SAXParserFactory,XXE_XMLInputFactory |
| 规则类型 | 安全规范规则类 |
| CWE 信息 | 611 |
| 预估误报率 | 20% |

**审查要点**

XML Entity Expansion injection 也称为 XML Bombs，属于 DoS 攻击，利用格式工整的有效 XML 块，它们在耗尽服务器分配的资源之前不断呈指数式扩张。XML 允许定义作为字符串替代宏的自定义实体。通过嵌套复发性实体解析，攻击者可以轻松使服务器资源崩溃。

**修复建议**

为了避免 XXE 注入，应对 XML 解析器进行安全配置。

**正确示例**

```text
public void good() throws Exception {
// 正确示例-- 完全禁用DTD
XMLInputFactory xmlFactory = XMLInputFactory.newFactory();
xmlFactory.setProperty(XMLInputFactory.SUPPORT_DTD, false);
XMLStreamReader reader = xmlFactory.createXMLStreamReader(new FileInputStream("demo.xml"));
}
```

**错误示例**

```text
public void bad() throws Exception {
// 错误示例-- 不禁用DTD
XMLInputFactory xmlFactory = XMLInputFactory.newFactory();
/* POTENTIAL FLAW: XML External Entities 攻击可利用能够在处理时动态构建文档的 XML 功能。XML 实体可动态包含来自给定资源的数据。外部实体允许 XML 文档包含来自外部 URI 的数据。除非另行配置，否则外部实体会迫使 XML 解析器访问由 URI 指定的资源，例如位于本地计算机或远程系统上的某个文件。这一行为会将应用程序暴露给 XML External Entity (XXE) 攻击，从而用于拒绝本地系统的服务，获取对本地计算机上文件未经授权的访问权限，扫描远程计算机，并拒绝远程系统的服务。 */
XMLStreamReader reader = xmlFactory.createXMLStreamReader(new FileInputStream("demo.xml"));
}
```

**参考信息**

- 链接：内部链接已省略
- 最新更新时间：2021-09-15 20:23:11

<a id="rule-98"></a>

### 98. G.EDV.06 防止解析来自外部的XML导致的内部实体扩展（XML Entity Expansion）攻击

| 字段 | 内容 |
|---|---|
| 问题级别 | 严重 |
| 语言 | JAVA |
| 标签 | CodeMars,SecBrella |
| 关联工具规则 | XXE_DocumentHelper,XXE_XMLReaderFactory,XXE_SAXTransformerFactory,XXE_DocumentBuilderFactoryImpl,XXE_SchemaFactory,XXE_DocumentBuilderFactory,XXE_SAXBuilder,XXE_TransformerFactory,XXE_SAXReader,SecJ_XML_External_Entity_Injection_JavaStandard,XXE_SAXParserFactory,XXE_XMLInputFactory |
| 规则类型 | 安全规范规则类 |
| CWE 信息 | 611 |
| 预估误报率 | 20% |

**审查要点**

```text
dom4j中提供的`DocumentHelper.parseText(String text)`，实现了对XML的解析功能。在dom4j的**2.0.3**以前的版本，解析XML时未禁止对XML外部实体的解析，使用该方法解析XML存在XXE漏洞。从**2.0.3**版本开始，`DocumentHelper.parseText(String text)`在解析XML时，默认禁用了外部实体解析功能，不存在XXE漏洞。
***工具误报说明：基于当前工具能力无法识别三方库版本，如果业务使用的是2.0.3及以后的dom4j版本则不存在该风险，请自行申请工具误报屏蔽***
```

**修复建议**

```text
1、使用2.0.3之前的dom4j时，将使用`DocumentHelper.parseText(String text)`解析XML改为使用其他XML解析器，并进行XXE防护配置。
2、使用2.0.3及以后的dom4j版本，属于工具误报。
```

**正确示例**

```text
##### 场景1：使用dom4j 2.0.3以前版本
- 修复示例：将`DocumentHelper.parseText(String text)`改为使用`javax.xml.parsers.DocumentBuilderFactory`解析器
```java
DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
try {
// 【GOOD】增加安全配置
documentBuilderFactory.setFeature("http://apache.org/xml/features/disallow-doctype-decl", true);
documentBuilderFactory.setFeature("http://xml.org/sax/features/external-general-entities", false);
documentBuilderFactory.setFeature("http://xml.org/sax/features/external-parameter-entities", false);
DocumentBuilder builder = documentBuilderFactory.newDocumentBuilder();
...// 解析xml文件
} catch (ParserConfigurationException localSAXException) {
...// 处理运行时异常
}
```
```

**错误示例**

```text
##### 场景1：使用dom4j 2.0.3以前版本
- 错误示例：使用`DocumentHelper.parseText(String text)`解析xml
```java
void doSomething(String xml) {
...
// 【不符合】
Document doc = DocumentHelper.parseText(xml);
...
}
```
```

**参考信息**

- 链接：内部链接已省略
- 最新更新时间：2025-12-05 14:57:08

<a id="rule-99"></a>

### 99. G.EDV.07 禁止使用不安全的XSLT转换XML文件

| 字段 | 内容 |
|---|---|
| 问题级别 | 一般 |
| 语言 | JAVA |
| 标签 | CodeMars,SecBrella,fixbotengine-java |
| 关联工具规则 | Insecure_XML_Transform_By_XSLT,SecS_XSLT_Injection |
| 规则类型 | 安全规范规则类 |
| CWE 信息 | 631 |
| 预估误报率 | 20%~50% |

**审查要点**

XSLT是一种样式转换标记语言，可以将XML数据转换为另外的XML或其他格式，如HTML网页，纯文字。因为XSLT的功能十分强大，可以导致任意代码执行，当使用TransformerFactory转换XML格式数据的时候，需要添加安全策略禁止不安全的XSLT代码执行。

**修复建议**

使用TransformerFactory对xml进行格式转换操作时，要开启其安全防护策略，参考修复示例。

**正确示例**

```text
##### 场景1：使用TransformerFactory转换XML格式数据需开启安全策略
- 修复示例：开启安全防护策略
```java
//create transformer after executing setFeature
public static void XsltTrans(String src, String dst, String xslt) {
TransformerFactory tf = TransformerFactory.newInstance();
tf.setAttribute(XMLConstants.ACCESS_EXTERNAL_DTD, "");
tf.setAttribute(XMLConstants.ACCESS_EXTERNAL_STYLESHEET, "");
try {
// 【GOOD】转换器工厂设置黑名单，禁用一些不安全的方法，类似XXE防护
tf.setFeature("http://javax.xml.XMLConstants/feature/secure-processing", true);
// 获取转换器对象实例
Transformer transformer = tf.newTransformer(new StreamSource(xslt));
// 进行转换
transformer.transform(new StreamSource(src), new StreamResult(new FileOutputStream(dst)));
} catch (Exception e) {
LOGGER.error(e.getMessage());
}
}
```
```

**错误示例**

```text
##### 场景1：使用TransformerFactory转换XML格式数据需开启安全策略
- 错误示例：不添加安全策略。
```java
// transformer of StreamSource without setFeature
public static void XsltTrans(String src, String dst, String xslt) {
TransformerFactory tf = TransformerFactory.newInstance();
tf.setAttribute(XMLConstants.ACCESS_EXTERNAL_DTD, "");
tf.setAttribute(XMLConstants.ACCESS_EXTERNAL_STYLESHEET, "");
try {
// 获取转换器对象实例
/* 【不符合】XSLT是一种样式转换标记语言，可以将XML数据档转换为另外的XML或其它格式，
* 如HTML网页，纯文字。因为XSLT的功能十分强大，可以导致任意代码执行，
* 当使用TransformerFactory转换XML格式数据的时候，需要添加安全策略禁止不安全的XSLT代码执行。
*/
Transformer transformer = tf.newTransformer(new StreamSource(xslt));
// 进行转换
transformer.transform(new StreamSource(src), new StreamResult(new FileOutputStream(dst)));
} catch (Exception e) {
LOGGER.error(e.getMessage());
}
}
```
```

**参考信息**

- 链接：内部链接已省略
- 最新更新时间：2025-05-27 09:58:46

<a id="rule-100"></a>

### 100. G.EDV.08 正则表达式要尽量简单，防止ReDos攻击

| 字段 | 内容 |
|---|---|
| 问题级别 | 严重 |
| 语言 | JAVA |
| 标签 | SecBrella |
| 关联工具规则 | SecS_Denial_of_Matches |
| 规则类型 | 安全规范规则类 |
| CWE 信息 | 730185 |
| 预估误报率 | 15% |

**审查要点**

```text
secBrella告警清理指导链接:内部链接已省略
攻击者可能通过对应用程序发送大量请求，而使它拒绝对合法用户的服务，但是这种攻击形式经常会在网络层就被排除掉了。更加严重的是那些只需要使用少量请求就可以使得攻击者让应用程序过载的 bug。这种 bug 允许攻击者去指定请求使用系统资源的数量，或者是持续使用这些系统资源的时间。
```

**修复建议**

在实际开发代码过程中，应避免直接使用外部数据构造正则或直接使用外部数据作为正则使用

**正确示例**

```text
String data;
/* FIX: Use a hardcoded string */
data = "foo";
if ("taint".matches(data)) {
IO.writeLine(data);
}
```

**错误示例**

```text
/* POTENTIAL FLAW: Denial of Service */
if ("taint".matches(data)) {
IO.writeLine(data);
}
```

**参考信息**

- 链接：内部链接已省略
- 最新更新时间：2021-07-07 17:12:54

<a id="rule-101"></a>

### 101. G.EDV.09 禁止直接使用外部数据作为反射操作中的类名/方法名

| 字段 | 内容 |
|---|---|
| 问题级别 | 严重 |
| 语言 | JAVA |
| 标签 | SecBrella |
| 关联工具规则 | SecS_Unsafe_Reflection |
| 规则类型 | 安全类（非编程规范） |
| CWE 信息 | 470494 |
| 预估误报率 | 30% |

**审查要点**

```text
外部数据的定义请参考**【编程规范】**：外部数据的范围包括但不限于：网络、用户输入（包括命令行、界面）、命令行、文件（包括程序的配置文件）、环境变量、进程间通信（包括管道、消息、共享内存、socket等、RPC）、跨信任域方法参数（对于API）等。
反射操作中直接使用外部数据作为类名或方法名，会导致系统执行非预期的逻辑流程（Unsafe Reflection）。这可被恶意用户利用来绕过安全检查或执行任意代码。当反射操作需要使用外部数据时，必须对外部数据进行白名单校验，明确允许访问的类或方法列表；另外也可以通过让用户在指定范围内选择的方式进行防护。
```

**修复建议**

```text
避免将外部数据直接用作反射操作的数据源：创建一个规定用户使用的合法名称列表，并仅允许用户从中进行选择。
如果必须使用外部数据用作反射操作的数据源，必须对外部数据进行白名单校验，明确允许访问的类或方法列表。
```

**正确示例**

```text
##### 场景1：
- 修复示例1：对外部数据进行自定义方法校验
```java
String classIndex = request.getParameter("classIndex");
if (validData(classIndex)) {
Class objClass = Class.forName(classIndex);
BaseClass obj = (BaseClass) objClass.newInstance();
obj.doSomething();
} else {
throw new IllegalStateException("Invalid reflect class!");
}
...
private boolean validData(String data) {
// 自定义校验规则，建议使用白名单或正则校验外部数据
}
```
```

**错误示例**

```text
##### 场景1：
- 错误示例：直接使用外部指定的类名通过反射构造了一个对象
```java
String className = request.getParameter("class");
...
Class objClass = Class.forName(className);
BaseClass obj = (BaseClass) objClass.newInstance();
obj.doSomething();
```
```

**参考信息**

- 链接：内部链接已省略
- 最新更新时间：2025-05-27 09:58:46

<a id="rule-102"></a>

### 102. G.ERR.01 不要通过一个空的catch块忽略异常

| 字段 | 内容 |
|---|---|
| 问题级别 | 一般 |
| 语言 | JAVA |
| 标签 | fixbotengine-java |
| 关联工具规则 | EmptyCatch |
| 规则类型 | 通用规范规则类 |
| 预估误报率 | 15% |

**审查要点**

异常表示程序运行发生了错误，发生异常会中断程序的正常处理流程。不应该使用空的catch块会忽略发生的异常，发生异常要么在catch块中对异常情况进行处理，要么将异常抛出，交由上层调用方进行处理。

**修复建议**

增加异常处理逻辑

**正确示例**

```text
InputStream inputStream = null;
try {
inputStream = new InputStream(null);
doSomething();
} catch (MyException t) {
logger.error("something is wrong");
} finally {
try {
inputStream.close();
} catch (IOException e) {
}
}
```

**错误示例**

```text
InputStream inputStream = null;
try {
inputStream = new InputStream(null);
doSomething();
} catch (SomeException ex) {
}
```

**参考信息**

- 链接：内部链接已省略
- 最新更新时间：2021-06-24 09:36:54

<a id="rule-103"></a>

### 103. G.ERR.03 不要直接捕获可通过预检查进行处理的RuntimeException ，如NullPointerException 、IndexOutOfBoundsException 等

| 字段 | 内容 |
|---|---|
| 问题级别 | 一般 |
| 语言 | JAVA |
| 标签 | SecBrella |
| 关联工具规则 | SecJ_Forbid_Catch_PreCheck_RuntimeException |
| 规则类型 | 安全规范建议类 |
| CWE 信息 | 391 |
| 预估误报率 | 20% |

**审查要点**

```text
可通过预检查的方式进行消除的RuntimeException，这类异常一般表示程序逻辑错误，不应该通过try...catch的方式进行处理（这也可能会影响代码的可读性及系统的运行效率）。推荐通过预检查方式进行消除，该类运行期异常包括：NullPointerException、IndexOutOfBoundsException等。对于NumberFormatException、IllegalArgumentException、IllegalStateException等可通过try...catch方式处理。
```

**修复建议**

通过**预检查的方式**消除NullPointerException、IndexOutOfBoundsException等异常。

**正确示例**

```text
##### 场景1：
- 修复示例1：
```java
public class SomeDemo {
private boolean doSomething(String str) {
if (str == null) {
return false;
}
String[] names = str.split(" ");
if (names.length != 2) {
return false;
}
return (isCapitalized(names[0]) && isCapitalized(names[1]));
}
private boolean isCapitalized(String str) {
for (int i = 0; i < str.length(); i++) {
if (!(str.charAt(i) >= 'A' && str.charAt(i) <= 'Z')) {
return false;
}
}
return true;
}
}
```
```

**错误示例**

```text
##### 场景1：
- 错误示例：
```java
public class SomeDemo {
private boolean doSomething1(String str) {
try {
String[] names = str.split(" ");
if (names.length != 2) {
return false;
}
return (isCapitalized(names[0]) && isCapitalized(names[1]));
/* POTENTIAL FLAW: Do not catch NullPointerException or any of its ancestors because it cannot help locate where the exception is thrown. */
} catch (NullPointerException e) {
return false;
}
}
private boolean doSomething2(String str) {
if (str == null) {
return false;
}
try {
String[] names = str.split(" ");
return (isCapitalized(names[0]) && isCapitalized(names[1]));
/* POTENTIAL FLAW: Do not catch NullPointerException or any of its ancestors because it cannot help locate where the exception is thrown. */
} catch (ArrayIndexOutOfBoundsException e) {
return false;
}
}
private boolean doSomething3(String str) {
try {
String[] names = str.split(" ");
return (isCapitalized(names[0]) && isCapitalized(names[1]));
/* POTENTIAL FLAW: Do not catch NullPointerException or any of its ancestors because it cannot help locate where the exception is thrown. */
} catch (ArrayIndexOutOfBoundsException | NullPointerException e) {
return false;
}
}
private boolean isCapitalized(String str) {
for (int i = 0; i < str.length(); i++) {
if (!(str.charAt(i) >= 'A' && str.charAt(i) <= 'Z')) {
return false;
}
}
return true;
}
}
```
```

**参考信息**

- 链接：内部链接已省略
- 最新更新时间：2022-04-13 18:04:19

<a id="rule-104"></a>

### 104. G.ERR.05 方法抛出的异常，应该与本身的抽象层次相对应

| 字段 | 内容 |
|---|---|
| 问题级别 | 一般 |
| 语言 | JAVA |
| 标签 | fixbotengine-java |
| 关联工具规则 | ThrowRawException |
| 规则类型 | 通用规范规则类 |
| 预估误报率 | 15% |

**审查要点**

```text
异常通常分为受检异常（checked exception）和运行时异常（runtime exception）。对于编程错误（即这些错误是可以通过预检查进行消除的）推荐使用运行时异常，对于需要主动对进行恢复处理的场景推荐使用受检异常。
方法抛出异常时，应该避免直接抛出RuntimeException，更不应该直接抛出Exception或Throwable，因为这些父类异常无法与异常发生的场景相关联，直接抛出父类异常会降低代码可读性。方法抛出的异常应该与方法本身的抽象层次相对应，这些异常可以是JDK中定义的标准异常，也可以是业务层自定义的异常。另外，抛出的异常中应该包含理解该异常产生原因的所有信息。
```

**修复建议**

对底层异常进行封装

**正确示例**

```text
public class Employee {
...
public TaxId getTaxId() {
...
throw new EmployeeDataNotAvailable();
}
...
}
```

**错误示例**

```text
public class Employee {
...
public TaxId getTaxId() {
...
throw new RuntimeException();
}
...
}
getTaxId把更底层的RuntimeException返回给调用方，使调用方代码与底层耦合起来。
推荐：抛出EmployeeDataNotAvailable异常，抽象层次与方法一致
```

**参考信息**

- 链接：内部链接已省略
- 最新更新时间：2021-06-24 09:36:54

<a id="rule-105"></a>

### 105. G.ERR.06 在catch块中抛出新异常时，避免丢失原始异常信息

| 字段 | 内容 |
|---|---|
| 问题级别 | 一般 |
| 语言 | JAVA |
| 标签 | SecBrella |
| 关联工具规则 | SecJ_Missing_Original_Exception |
| 规则类型 | 安全规范建议类 |
| 预估误报率 | 20% |

**审查要点**

```text
在catch代码块中更改异常类型时，如果只是使用原始异常中的message（originalException.getMessage()）或使用新的错误描述信息构造新异常，可能会导致原始异常中的有价值的信息丢失，例如异常类型、调用堆栈等信息，增加问题定位的难度。
为了保证不丢失异常信息，可以采取以下策略：
1. **包含原始异常信息**：在构造新异常时，将原始异常作为构造函数的参数传递，如 `throw new CustomException("新的错误描述", originalException);`。这样，新异常会包含原始异常的堆栈跟踪，便于追踪问题源头。
2. **记录原始异常到日志**：在抛出新异常之前，先将原始异常的详细信息记录到日志中。例如 `LOG.error("新错误描述", originalException);`。这样即使新异常中不包含全部原始异常信息，也可以通过日志获取。
3. **处理敏感信息**：如果原始异常包含敏感信息，应在记录日志或传递给新异常之前进行匿名化或脱敏处理，以保护信息安全。
遵循这些做法，可以在保证异常处理逻辑清晰的同时，也保留了足够的调试信息。
```

**修复建议**

```text
修复方法1：新异常中要包含原始异常中的所有信息（敏感信息可先进行匿名化处理）；
修复方法2：原始异常先记录到日志（敏感信息先脱敏处理）中，再抛出新构造的异常（仅包含部分原始异常信息）。
```

**正确示例**

```text
##### 场景1：丢失原始异常信息。
- 修复示例1：在新抛出的异常中放入原始异常信息ex：
```java
try {
new File("").createNewFile();
} catch (IOException ex) {
throw new CustomException("file not found", ex);// 抛出异常信息携带原始异常
}
```
- 修复示例2：原始异常记录日志后，再抛出新异常：
```java
try {
new File("").createNewFile();
} catch (IOException ex) {
LOG.error("file not found: " + ex);// 日志中打印出原始异常
throw new CustomException("file not found");
}
```
- 修复示例3：原始异常存在敏感信息的，先清理敏感信息后再记录日志，并抛出新异常：
```java
try {
new File("").createNewFile();
} catch (IOException ex) {
LOG.error("file not found: " + filterFileSensitiveInfo(ex)); // 日志中打印出清理敏感信息后的原始异常信息
throw new CustomException("file not found");
}
/**
* 过文件系统中的滤敏感信息
*
* @param message message
* @return 脱敏后的message
*/
public static String filterFileSensitiveInfo(IOException ex) {
String message = ex.getMessage();
if (message == null || message.isEmpty()) {
return "";
}
// 移除敏感路径信息
message = message.replaceAll("业务校验正则（产品依据业务自行决定)", "[FILTERED_PATH]");
return message;
}
```
```

**错误示例**

```text
##### 场景1：丢失原始异常信息。
- 错误示例：原始异常ex没有打印到日志中，也没有被包含在新抛出的异常中。
```java
try {
new File("").createNewFile();
} catch (IOException ex) {
throw new CustomException("file not found");
}
```
```

**参考信息**

- 链接：内部链接已省略
- 最新更新时间：2024-10-15 19:47:08

<a id="rule-106"></a>

### 106. G.ERR.08 不要使用return、break、continue或抛出异常使finally块非正常结束

| 字段 | 内容 |
|---|---|
| 问题级别 | 严重 |
| 语言 | JAVA |
| 标签 | fixbotengine-java |
| 关联工具规则 | Finally |
| 规则类型 | 通用规范规则类 |
| 预估误报率 | 15% |

**审查要点**

```text
在finally中使用return、break或continue会使finally块非正常结束，造成的影响是，即使在try块或catch中抛出了异常，也会因为finally非正常结束而导致无法抛出。finally块非正常结束会有编译告警。
工具检查场景:
- 检查finally块中是否有return, break, continue或throws语句
```

**修复建议**

去除finally中非正常结束语句

**正确示例**

```text
##### 场景1：
- 修复示例：
```java
public int doSomething() {
int result = 0;
...
try {
result = doSomethingElse();
...
return result;
} catch (IOException ex) {
...
}
return -1;
}
```
```

**错误示例**

```text
public static void main(String[] args) {
try {
System.out.println(func());
} catch (MyException ex) {
// 处理异常
}
}
public static int func() throws MyException {
for (int i = 1; i < 2; i++) {
try {
throw new MyException();
} finally {
continue; // 不推荐
}
}
return 0;
}
上述示例中，main方法中不会捕获到异常，而是直接输出0。
```

**参考信息**

- 链接：内部链接已省略
- 最新更新时间：2021-09-15 20:21:49

<a id="rule-107"></a>

### 107. G.EXP.01 不要在单个表达式中对相同的变量赋值超过一次

| 字段 | 内容 |
|---|---|
| 问题级别 | 严重 |
| 语言 | JAVA |
| 标签 | fixbotengine-java |
| 关联工具规则 | MultipleAssignment |
| 规则类型 | 通用规范规则类 |
| 预估误报率 | 15% |

**审查要点**

```text
对相同的变量进行多次赋值的表达式会产生混淆，并且容易产生非预期的行为。清晰的变量赋值会使代码更易懂，也更能保证程序按预期运行。
工具检查场景:
- 检查赋值语句的右侧是否有赋值变量的自增操作
```

**修复建议**

拆分表达式

**正确示例**

```text
public class Increment {
public static void main(String[] args) {
int count = 0;
for (int i = 0; i < 100; i++) {
...
count++;
}
System.out.println(count);
}
}
上述示例中，可以实现正常的循环计数，输出结果为100。
```

**错误示例**

```text
public class Increment {
public static void main(String[] args) {
int count = 0;
for (int i = 0; i < 100; i++) {
...
count = count++;
}
System.out.println(count);
}
}
上述示例中，预期使用count对循环计数，而实际输出的结果却为0。
```

**参考信息**

- 链接：内部链接已省略
- 最新更新时间：2021-06-24 09:36:51

<a id="rule-108"></a>

### 108. G.EXP.03 条件表达式?:的第2和第3个操作数应使用相同的类型

| 字段 | 内容 |
|---|---|
| 问题级别 | 一般 |
| 语言 | JAVA |
| 标签 | fixbotengine-java |
| 关联工具规则 | OperandTypeInConditionalExpression |
| 规则类型 | 通用规范建议类 |
| 预估误报率 | 15% |

**审查要点**

```text
条件运算符?:使用第1个操作数的布尔值决定后续表达式哪个被执行。但是Java语言有相当复杂的规则去判定表达式的结果类型，不一致的操作数类型，可能导致意料之外的类型转换。如第2和第3个操作数在类型对齐时，可能会因为自动拆箱导致NullPointerException。
工具检查场景：
检查条件表达式?:的第2和第3个操作数是否具有相同类型
```

**修复建议**

修改成相同类型

**正确示例**

```text
char ch = 'A';
int value = 50;
boolean condition = ...; // condition的值为true时
System.out.println(condition ? ch : ((char) value)); // 输出 A
Integer integer = null;
System.out.print(condition ? integer : Integer.valueOf(value)); // 输出 null
```

**错误示例**

```text
ar ch = 'A';
int value = 50;
boolean condition = ...; // condition的值为true时
System.out.println(condition ? ch : value); // 输出 65
Integer integer = null;
System.out.print(condition ? integer : value); // 抛 NullPointerException
```

**参考信息**

- 链接：内部链接已省略
- 最新更新时间：2021-06-24 09:36:51

<a id="rule-109"></a>

### 109. G.EXP.05 禁止直接使用可能为null的对象，防止出现空指针引用

| 字段 | 内容 |
|---|---|
| 问题级别 | 严重 |
| 语言 | JAVA |
| 标签 | SecBrella |
| 关联工具规则 | SecS_Null_Dereference |
| 规则类型 | 安全规范规则类 |
| CWE 信息 | 476 |
| 预估误报率 | 30% |

**审查要点**

```text
在Java中，禁止直接使用可能为null的对象是为了避免空指针异常（NullPointerException）。当程序试图访问一个空对象的属性、调用其方法或操作其字段时，就会抛出这种异常。这通常表示程序逻辑中存在错误，因为预期该对象应该是已初始化且非空的。
常见涉及空指针引用包括但不限于以下场景：
1、声明为null的变量在返回前存在else分支未对变量进行初始化或赋值。
2、声明为null的变量在try-catch中赋值，发生异常后，仅记录了日志而未进一步抛出异常，导致变量未正确赋值。
```

**修复建议**

```text
通过预检查（即在操作对象之前检查其是否为null），我们可以提前发现并处理这种潜在的错误，而不是依赖于运行时的异常处理机制。预检查可以通过简单的条件语句实现，如** if (object != null) { ... } **。这样可以确保只有当对象确实存在时，才会执行后续的操作，从而避免了空指针异常，提高了代码的健壮性和可靠性。
```

**正确示例**

```text
##### 场景1：
- 修复示例：对于方法返回值可能为null时，方法返回值使用前进行判空处理
```java
public String getData() {
return data == null ? null : data.toUpperCase();
}
public void doSomething() {
...
String data = getData();
/* GOOD : data在使用前先判空 */
if (data != null && data.startsWith("XXX")) {
...
}
...
}
```
##### 场景2：
- 修复示例：对于变量声明为null时，方法调用前需进行初始化或判空处理
```java
public void doSomething() {
...
String data = null;
...
/* GOOD : data在使用前先判空 */
if (data != null) {
int len = data.length();
...
}
...
}
```
```

**错误示例**

```text
##### 场景1：
- 错误示例：对于方法返回值可能为null时，方法返回值使用前未做判空处理
```java
public String getData() {
return data == null ? null : data.toUpperCase();
}
public void doSomething() {
...
String data = getData();
/* 不符合 : data可能会返回null，存在空指针风险 */
if (data.startsWith("XXX")) {
...
}
...
}
```
##### 场景2：
- 错误示例：对于变量声明为null时，方法调用前未进行初始化或判空处理
```java
public void doSomething() {
...
String data = null;
...
/* BAD : data在使用前未做判空 */
int len = data.length();
...
...
}
```
```

**参考信息**

- 链接：内部链接已省略
- 最新更新时间：2025-12-11 10:49:27

<a id="rule-110"></a>

### 110. G.EXP.06 代码中不应使用断言（assert）

| 字段 | 内容 |
|---|---|
| 问题级别 | 一般 |
| 语言 | JAVA |
| 标签 | CodeMars |
| 关联工具规则 | Assertions_With_Side_Effects |
| 规则类型 | 安全规范建议类 |
| CWE 信息 | 275 |
| 预估误报率 | 20% |

**审查要点**

使用assert断言语是对代码进行诊断测试的方便机制。当使用有副作用的断言表达式时，assert语句的行为取决于运行时属性的状态，启用时，assert语句会计算表达式的值，如果值为false时抛出AssertionError异常；禁用时，assert语句不执行

**修复建议**

不要在断言中使用有副作用的表达式

**正确示例**

```text
public void process01Good(List<String> names) {
assert names != null;
// ...
}
```

**错误示例**

```text
public void processBad() {
boolean check;
/* POTENTIAL FLAW: Expressions used in assertions must not produce side effects,which means the expressions should avoid altering or modifying the value of Objects or variables. */
assert check = true;
// ...
}
```

**参考信息**

- 链接：内部链接已省略
- 最新更新时间：2025-05-27 09:58:45

<a id="rule-111"></a>

### 111. G.FIO.01 使用外部数据构造的文件路径前必须进行校验，校验前必须对文件路径进行规范化处理

| 字段 | 内容 |
|---|---|
| 问题级别 | 严重 |
| 语言 | JAVA |
| 标签 | CodeMars,SecBrella |
| 关联工具规则 | Canonical_Path |
| 规则类型 | 安全规范规则类 |
| CWE 信息 | 171 |
| 预估误报率 | 20% |

**审查要点**

在对文件路径校验前的规范化处理，必须使用`getCanonicalPath()`，禁止使用`getAbsolutePath()`，因为该方法无法保证在所有的平台上对文件路径进行正确的规范化处理。

**修复建议**

对文件路径的规范化处理必须使用`getCanonicalPath()`。

**正确示例**

```text
##### 场景1：
- 修复示例：
```java
public void doSomething() {
File file = new File(HOME_PATH, fileName);
try {
String canonicalPath = file.getCanonicalPath(); // 【GOOD】使用 getCanonicalPath() 方法对文件路径进行规范化处理。
// 校验 canonicalPath 合理性。
if (!validatePath(canonicalPath)) {
throw new IllegalArgumentException("Path Traversal vulnerability!");
}
... // 对文件进行读写等操作
} catch (IOException ex) {
throw new IllegalArgumentException("An exception occurred ...", ex);
}
}
private boolean validatePath(String path) {
return path.startsWith(HOME_PATH);
}
```
```

**错误示例**

```text
##### 场景1：
- 错误示例：
```java
public void doSomething() {
File file = new File(HOME_PATH, fileName);
try {
String absolutePath = file.getAbsolutePath(); // 【POTENTIAL FLAW】使用 getAbsolutePath() 方法对文件路径进行规范化处理
// 校验 absolutePath 合理性。
if (!validatePath(absolutePath )) {
throw new IllegalArgumentException("Path Traversal vulnerability!");
}
... // 对文件进行读写等操作
} catch (IOException ex) {
throw new IllegalArgumentException("An exception occurred ...", ex);
}
}
private boolean validatePath(String path) {
return path.startsWith(HOME_PATH);
}
```
```

**参考信息**

- 链接：内部链接已省略
- 最新更新时间：2025-05-27 09:58:46

<a id="rule-112"></a>

### 112. G.FIO.02 从ZipInputStream中解压文件必须进行安全检查

| 字段 | 内容 |
|---|---|
| 问题级别 | 一般 |
| 语言 | JAVA |
| 标签 | CodeMars |
| 关联工具规则 | Zip_Entry_Size |
| 规则类型 | 安全规范规则类 |
| CWE 信息 | 22,73 |
| 预估误报率 | 20% |

**审查要点**

从java.util.zip.ZipInputStream中解压文件时，有两个安全问题需要注意：1）解压出的标准化路径文件在解压目标目录之外；2）解压的文件消耗过多的系统资源。

**修复建议**

从ZipInputStream中解压文件必须进行安全检查

**正确示例**

```text
public class CWE409_J_Rule1_7_Zip_Entry_Size_Good {
static final int BUFFER = 512;
static final int TOOBIG = 0x6400000; // max size of unzipped data, 100MB
static final int TOOMANY = 1024; // max number of files
// ...
// The code validates the name of each entry before extracting the entry.
// If the name is invalid, the entire extraction is aborted.
private String sanitizeFileName(String entryName, String intendedDir) throws IOException {
File f = new File(intendedDir, entryName);
String canonicalPath = f.getCanonicalPath();
File iD = new File(intendedDir);
String canonicalID = iD.getCanonicalPath();
if (canonicalPath.startsWith(canonicalID)) {
return canonicalPath;
} else {
throw new IllegalStateException("File is outside extraction target directory.");
}
}
public final void unzip_compilant(String fileName, String destDir) throws java.io.IOException {
FileInputStream fis = new FileInputStream(fileName);
ZipInputStream zis = new ZipInputStream(new BufferedInputStream(fis));
ZipEntry entry;
int total = 0;
int entries = 0;
try {
while ((entry = zis.getNextEntry()) != null) {
BufferedOutputStream dest = null;
int count;
byte data[] = new byte[BUFFER];
// Write the files to the disk, but ensure that the entryName is valid,and that the file is not insanely big
String name = sanitizeFileName(entry.getName(), destDir);
// process file
FileOutputStream fos = new FileOutputStream(name);
dest = new BufferedOutputStream(fos, BUFFER);
// check every entry's size
while ((count = zis.read(data, 0, BUFFER)) != -1) {
total += count;
if (total > TOOBIG) {
break;
}
dest.write(data, 0, count);
}
entries++;
// if the total number of entry is larger than the max number,it will throw exception.
if (entries > TOOMANY) {
//handle exception
}
// if the total size of zip file is bigger than the max size value,it will throw exception.
if (total > TOOBIG) {
//handle exception
}
// …
}
} finally {
zis.close();
}
}
}
```

**错误示例**

```text
public class CWE409_J_Rule1_7_Zip_Entry_Size_Bad {
public static final int BUFFER = 512;
public static final int TOOBIG = 0x6400000; // 100MB
public final void test01Bad(String filename) throws java.io.IOException {
FileInputStream fis = new FileInputStream(filename);
ZipInputStream zis = new ZipInputStream(new BufferedInputStream(fis));
ZipEntry entry;
try {
while ((entry = zis.getNextEntry()) != null) {
System.out.println("Extracting: " + entry);
int count;
byte data[] = new byte[BUFFER];
// Write the files to the disk, but only if the file is not
// insanely big
int a = (int) entry.getSize();
/* POTENTIAL FLAW: if语句中使用ZipEntry.getSize()来做文件大小的判断。 */
if (a > TOOBIG) {
throw new IllegalStateException("File to be unzipped is huge.");
}
/* POTENTIAL FLAW: if语句中使用ZipEntry.getSize()来做文件大小的判断。 */
if (entry.getSize() == -1) {
throw new IllegalStateException("File to be unzipped might be huge.");
}
FileOutputStream fos = new FileOutputStream(entry.getName());
BufferedOutputStream dest = new BufferedOutputStream(fos, BUFFER);
while ((count = zis.read(data, 0, BUFFER)) != -1) {
dest.write(data, 0, count);
}
dest.flush();
dest.close();
zis.closeEntry();
}
} finally {
zis.close();
}
}
}
```

**参考信息**

- 链接：内部链接已省略
- 最新更新时间：2025-05-27 09:58:45

<a id="rule-113"></a>

### 113. G.FIO.03 对于从流中读取一个字符或字节的方法，使用int类型的返回值

| 字段 | 内容 |
|---|---|
| 问题级别 | 致命 |
| 语言 | JAVA |
| 标签 | CodeMars |
| 关联工具规则 | Invalid_Read_From_Stream |
| 规则类型 | 安全规范规则类 |
| CWE 信息 | 682 |
| 预估误报率 | 20% |

**审查要点**

Java代码中常用的抽象方法Reader.read()方法用于从流中读取一个字节或字符，该方法会读取一个字节，返回值的范围为0～65535。 这个方法会返回一个32位的-1表示读取到流的末尾，流中已经没有可用数据。这个方法使用int来保存流结束标志及最大字节。

**修复建议**

```text
使用int类型的变量来保存read()的返回值，并使用该返回值判断是否读取到流的末尾，流未读完时，
将读取的内容转换为char类型。
```

**正确示例**

```text
public class CWE682_J_Rule6_4_Invalid_Read_From_Stream_01_Good {
private static final Logger LOGGER = Logger.getLogger(CWE682_J_Rule6_4_Invalid_Read_From_Stream_01_Good.class);
public static void readBytesFromStreamGood() {
try (FileInputStream in = new FileInputStream("demo.txt")) {
// Initialize stream
int data;
while ((data = in.read()) != -1) {
LOGGER.info(data);
}
} catch (Exception e) {
LOGGER.error("error");
}
}
}
```

**错误示例**

```text
public class CWE682_J_Rule6_4_Invalid_Read_From_Stream_01_Bad {
private static final Logger LOGGER = Logger.getLogger(CWE682_J_Rule6_4_Invalid_Read_From_Stream_01_Bad.class);
public static void readBytesFromStreamBad() {
try (FileInputStream in = new FileInputStream("demo.txt")) {
// Initialize stream
byte data;
/* POTENTIAL FLAW: 对于从流中读取一个字符或字节的方法，使用int类型的返回值 */
while ((data = (byte) in.read()) != -1) {
LOGGER.info(data);
}
} catch (Exception e) {
LOGGER.error("error");
}
}
}
```

**参考信息**

- 链接：内部链接已省略
- 最新更新时间：2021-09-15 20:18:05

<a id="rule-114"></a>

### 114. G.FIO.04 防止外部进程阻塞在输入输出流上

| 字段 | 内容 |
|---|---|
| 问题级别 | 致命 |
| 语言 | JAVA |
| 标签 | CodeMars |
| 关联工具规则 | IO_Process_WaitFor |
| 规则类型 | 安全规范规则类 |
| CWE 信息 | 413 |
| 预估误报率 | 20% |

**审查要点**

执行Process.waitFor()、exitValue()操作之前要处理输入流和错误流

**修复建议**

```text
在运行一个外部进程时，如果此进程往其输出流发送任何数据，则必须将其
输出流清空。类似的，如果进程会往其错误流发送数据，其错误流也必须被清空。
```

**正确示例**

```text
public void testGood() throws InterruptedException, IOException {
Runtime rt = Runtime.getRuntime();
Process proc = rt.exec("notemaker");
StreamGobbler errorGobbler = new StreamGobbler(proc.getErrorStream(), System.err);
StreamGobbler outputGobbler = new StreamGobbler(proc.getInputStream(), System.out);
errorGobbler.start();
outputGobbler.start();
proc.waitFor();
// ...
}
```

**错误示例**

```text
public void testBad() throws Exception {
Runtime rt = Runtime.getRuntime();
Process proc = rt.exec("notemaker");
/* POTENTIAL FLAW: 执行Process.waitFor()、exitValue()操作之前要处理输入流和错误流，如果否则报告警。 */
proc.waitFor();
// ...
}
```

**参考信息**

- 链接：内部链接已省略
- 最新更新时间：2021-09-15 20:19:33

<a id="rule-115"></a>

### 115. G.FIO.05 临时文件使用完毕必须及时删除

| 字段 | 内容 |
|---|---|
| 问题级别 | 一般 |
| 语言 | JAVA |
| 标签 | SecBrella |
| 关联工具规则 | SecS_Remove_Temporary_File |
| 规则类型 | 安全规范规则类 |
| CWE 信息 | 171 |
| 预估误报率 | 35% |

**审查要点**

```text
程序中有很多使用临时文件的场景，比如用于进程间的数据共享，缓存内存数据，动态构造的类文件，动态连接库文件等。临时文件可能创建于操作系统的共享临时文件目录，例如，POSIX系统下的/tmp与/var/tmp目录，Windows系统下的C:\TEMP目录，这类目录中的文件可能会被定期清理。创建在其他路径下的临时文件不会被自动清理。如果文件未被安全地创建或者用完后还是可访问的，具备本地文件系统访问权限的恶意用户便可以利用共享目录中的文件进行恶意操作，另外，临时文件不清理也可能会导致大量垃圾文件占用磁盘的存储空间。删除已经不再需要的临时文件有助于对文件名和其他资源进行回收利用。每一个程序在正常运行过程中都有责任确保已使用完毕的临时文件被删除。
```

**修复建议**

临时文件在使用结束后必须删除。

**正确示例**

```text
##### 场景1：临时文件没删除
- 修复示例：使用了delete方法删除了tempFile文件
```java
private void doSomething(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException{
File tempFile = null;
try {
tempFile = File.createTempFile("temp", "1234");
/* Set the permissions to avoid insecure temporary file incidentals */
if (!tempFile.setWritable(true, true)) {
System.out.println("Could not set Writable permissions");
}
...
} catch (IOException exceptIO) {
System.out.println("Could not create temporary file");
} finally {
/* Delete the temporary file manually */
if (tempFile.exists()) {
tempFile.delete();
}
}
}
```
```

**错误示例**

```text
##### 场景1：临时文件没删除
- 错误示例：tempFile没有删除操作
```java
@Override
public void doSomething(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
File tempFile = null;
try {
// 【不符合】 临时文件使用完毕应及时删除
tempFile = File.createTempFile("temp", "1234");
// 当调用deleteOnExit()方法时，只是相当于对deleteOnExit()作一个声明，当程序运行结束，JVM终止时才真正调用deleteOnExit()方法实现删除操作。
tempFile.deleteOnExit();
/* Set the permissions to avoid insecure temporary file incidentals */
if (!tempFile.setWritable(true, true)) {
System.out.println("Could not set Writable permissions");
}
...
} catch (IOException exceptIO) {
System.out.println("Could not create temporary file");
}
}
```
```

**参考信息**

- 链接：内部链接已省略
- 最新更新时间：2025-11-28 11:02:11

<a id="rule-116"></a>

### 116. G.FMT.09 每行不超过一个语句

| 字段 | 内容 |
|---|---|
| 问题级别 | 提示 |
| 语言 | JAVA |
| 标签 | fixbotengine-java |
| 关联工具规则 | OneStatementPerLine |
| 规则类型 | 通用规范建议类 |
| 预估误报率 | 10% |

**审查要点**

```text
每个语句后面都有一个换行符
工具检查场景:
-检查一行是否有多个语句
```

**修复建议**

增加空行

**正确示例**

```text
int a;
int b;
```

**错误示例**

int a; int b;

**参考信息**

- 链接：内部链接已省略
- 最新更新时间：2021-06-24 09:36:47

<a id="rule-117"></a>

### 117. G.FMT.13 用空格突出关键字和重要信息

| 字段 | 内容 |
|---|---|
| 问题级别 | 提示 |
| 语言 | JAVA |
| 标签 | fixbotengine-java |
| 关联工具规则 | WhitespaceNoTrailing |
| 规则类型 | 通用规范建议类 |
| 预估误报率 | 10% |

**审查要点**

```text
水平空格应该突出关键字和重要信息。单个空格应该分隔关键字与其后的左括号、与其前面的右大括号，出现在任何二元/三元运算符/类似运算符的两侧，,:;或类型转换结束括号)之后使用空格。行尾和空行不能有空格space。总体规则如下：
必须加空格的场景：
（包括复合）赋值运算符前后，例如 =、*= 等；
逗号,、非for-in的冒号:、for循环等分隔的;符号之后加空格；
二元操作符、类型并交的|和＆符号、for-in的冒号：的前后两侧，例如base + offset；
lambda表达式中的箭头前后，例如 str -> str.length()。
禁止加空格的场景：
super、this等少数关键字之后（多数关键字之后自然地须加空格）；
成员访问操作符（instance.member）前后；
圆括号、方括号、注解或数组等非换行的大括号内两侧；
一元操作符前后，例如cnt++；
函数声明或者函数调用的左括号之前。
工具检查场景:
- 检查关键字与其后的左括号、与其前面的右大括号之间是否有空格
- 检查任何二元/三元运算符/类似运算符的两侧是否有空格
- 检查逗号、冒号、分号或类型转换之后是否有空格
- 检查成员访问操作符前后是否有空格
- 检查一元运算符前后是否有空格
- 检查圆括号、方括号、注解或数组等非换行的大括号内两侧前后是否有空格
- 检查行尾或空行是否有空格
```

**修复建议**

按规范增加空格

**正确示例**

```text
void baz(boolean var1) {
if (var1) {
} else {
}
}
```

**错误示例**

```text
void baz(boolean var1) {
if(var1){
}else{
}
}
```

**参考信息**

- 链接：内部链接已省略
- 最新更新时间：2021-06-24 09:36:47

<a id="rule-118"></a>

### 118. G.LOG.01 记录日志应该使用Facade模式的日志框架

| 字段 | 内容 |
|---|---|
| 问题级别 | 一般 |
| 语言 | JAVA |
| 标签 | fixbotengine-java |
| 关联工具规则 | DoNotLogWithSystemPrint |
| 规则类型 | 通用规范规则类 |
| 预估误报率 | 10% |

**审查要点**

专用日志工具与控制台打印（System.out、System.err）相比，提供了更丰富的日志记录功能，且使用更加简单。日志打印推荐使用Facade模式的日志框架，如第三方slf4j、产品自研日志框架等，不要使用System.out与System.err进行控制台打印。

**修复建议**

使用日志打印

**正确示例**

```text
start = System.currentTimeMillis();
// 其他加载数据的代码
LOGGER.info("items loaded, use {}ms.", (System.currentTimeMillis() - start));
```

**错误示例**

```text
start = System.currentTimeMillis();
// 其他加载数据的代码
System.out.println ("items loaded, use " + (System.currentTimeMillis() - start) + "ms.");
```

**参考信息**

- 链接：内部链接已省略
- 最新更新时间：2021-06-24 09:37:00

<a id="rule-119"></a>

### 119. G.LOG.02 日志工具Logger类的实例必须声明为private static final或者private final

| 字段 | 内容 |
|---|---|
| 问题级别 | 一般 |
| 语言 | JAVA |
| 标签 | fixbotengine-java |
| 关联工具规则 | LogModifier |
| 规则类型 | 通用规范规则类 |
| 预估误报率 | 15% |

**审查要点**

```text
如果声明时并初始化了，应该是private static final，如果只是声明没有初始化赋值的，则可以是private final。
工具检查场景:
- 检查声明并初始化的Logger类的实例，是否使用private static final修饰词
- 检查声明但没初始化的Logger类的实例，是否使用private final修饰词
```

**修复建议**

增加相应的修饰词

**正确示例**

```text
private static final Logger LOGGER =
LoggerFacotry.getLogger(com.example.product.MyClass.class);
```

**错误示例**

Logger LOGGER = LoggerFacotry.getLogger(com.example.product.MyClass.class);

**参考信息**

- 链接：内部链接已省略
- 最新更新时间：2021-06-24 09:37:00

<a id="rule-120"></a>

### 120. G.LOG.03 日志必须分等级

| 字段 | 内容 |
|---|---|
| 问题级别 | 一般 |
| 语言 | JAVA |
| 标签 | fixbotengine-java |
| 关联工具规则 | LogLevel |
| 规则类型 | 通用规范规则类 |
| 预估误报率 | 15% |

**审查要点**

```text
如果日志不分等级，则定位问题时，无法快速有效屏蔽大量低级别信息，给快速定位带来难度。
日志可分为以下级别：trace（有的也叫verbose）、debug、info、warning、error、fatal。
推荐与具体实现有关的日志记录trace或debug级，一般的业务处理日志用info级，不影响业务进行的错误用warning级，例如用户输入参数错误。而error或fatal级，只记录系统逻辑出错、异常或者重要的错误信息，常常向运维系统报警。
建议生产环境不输出trace或debug日志；有选择地输出info日志；输出warning、error、fatal日志。
对info及以下级别的日志，应使用条件形式或占位符的方式进行输出。
```

**修复建议**

采用占位符或条件判断

**正确示例**

```text
// 如果日志库提供了带"msgSupplier"的API，如下这样调用可以消除不必要的消息创建
LOGGER.debug(() ->
"Processing trade with id: " + id + " and symbol: " + symbol.fetchBigMessage());
// 采用条件方式
if (LOGGER.isDebugEnabled()) {
LOGGER.debug("Processing trade with id: " + id + " and symbol: " + symbol);
}
// 或者使用占位符
LOGGER.debug("Processing trade with id: {} and symbol: {}" , id, symbol);
```

**错误示例**

```text
如果日志级别设置为warning，下面日志不会打印，但是会执行字符串拼接操作，如果symbol是对象，会执行toString()方法，这样执行了上述操作，浪费系统资源，最终日志却没有打印。
LOGGER.debug("Processing trade with id: " + id + " and symbol: " + symbol);
```

**参考信息**

- 链接：内部链接已省略
- 最新更新时间：2021-06-24 09:37:01

<a id="rule-121"></a>

### 121. G.LOG.04 非仅限于中文区销售产品禁止用中文打印日志

| 字段 | 内容 |
|---|---|
| 问题级别 | 一般 |
| 语言 | JAVA |
| 标签 | fixbotengine-java |
| 关联工具规则 | LogWithoutChinese |
| 规则类型 | 通用规范规则类 |
| 预估误报率 | 15% |

**审查要点**

非仅限于中文区销售产品禁止用中文打印日志

**修复建议**

修改成对应语言

**正确示例**

```text
String message = "message";
...
LOGGER.debug("Chinese" + message);
```

**错误示例**

```text
String message = "消息";
...
LOGGER.debug("中文" + message);
```

**参考信息**

- 链接：内部链接已省略
- 最新更新时间：2021-06-24 09:37:01

<a id="rule-122"></a>

### 122. G.LOG.05 禁止直接使用外部数据记录日志

| 字段 | 内容 |
|---|---|
| 问题级别 | 严重 |
| 语言 | JAVA |
| 标签 | SecBrella |
| 关联工具规则 | SecS_Log_Forging_Debug,SecS_Log_Forging |
| 规则类型 | 安全规范规则类 |
| CWE 信息 | 117 |
| 预估误报率 | 50% |

**审查要点**

```text
直接将外部数据记录到日志中，可能存在以下风险：
1. 日志注入：恶意用户可利用回车、换行等字符注入一条完整的日志；
2. 敏感信息泄露：当用户输入敏感信息时，直接记录到日志中可能会导致敏感信息泄露；
3. 垃圾日志或日志覆盖：当用户输入的是很长的字符串，直接记录到日志中可能会导致产生大量垃圾日志；当日志被循环覆盖时，这样还可能会导致有效日志被恶意覆盖。
所以外部数据应尽量避免直接记录到日志中，如果必须要记录到日志中，要进行必要的校验及过滤处理，对于较长字符串可以截断。
关于日志注入问题，风险主要存在于操作日志中，利用日志注入攻击伪造正常日志，会影响对系统的安全审计；对于普通的运行日志，注入伪造日志一般不会影响问题定位分析。
```

**修复建议**

对于外部数据，记录到日志前将换行符进行过滤或转义替换；对于较长的字符串记录日志中前可以进行截断处理。

**正确示例**

```text
##### 场景1：
- 修复示例：外部数据写入日志前，先将其中的\r\n等导致换行的字符进行替换，消除日志注入风险。
```java
String jsonData = getRequestBodyData(request);
if (!validateRequestData(jsonData)) {
LOG.debug("Request data validate fail! Request Data : " + replaceCRLF(jsonData));
}
public String replaceCRLF(String message) {
if (message == null) {
return "";
}
return message.replace('\n', '_').replace('\r', '_');
}
```
```

**错误示例**

```text
##### 场景1：
- 错误示例：当请求的json数据校验失败，会直接将json字符串记录到日志中，可能导致发生敏感数据泄露、或者日志注入、日志冗余。
```java
String jsonData = getRequestBodyData(request);
if (!validateRequestData(jsonData)) {
LOG.debug("Request data validate fail! Request Data : " + jsonData);
}
```
```

**参考信息**

- 链接：内部链接已省略
- 最新更新时间：2025-11-28 11:11:53

<a id="rule-123"></a>

### 123. G.MET.02 不要使用已标注为@Deprecated的方法、类、类的属性等

| 字段 | 内容 |
|---|---|
| 问题级别 | 一般 |
| 语言 | JAVA |
| 标签 | SecBrella |
| 关联工具规则 | SecJ_Call_Deprecated |
| 规则类型 | 通用规范规则类 |
| 预估误报率 | 20% |

**审查要点**

标注为@Deprecated的方法、类、类的属性等，是由于各种原因被废弃的，为了保持兼容性而没有删除。新写的代码应避免继续使用这些方法、类等，而应该使用推荐的代替实现。

**修复建议**

新写的代码应避免继续使用这些方法、类等，而应该使用推荐的代替实现。

**正确示例**

```text
public class Demo {
int i;
public void method() {
i = Class.field;
Class.method();
}
}
class Class {
public static int field = 0;
public static void method() {
}
}
```

**错误示例**

```text
public class Demo {
int i;
public void method() {
i = DeprecatedClass.deprecatedField;
DeprecatedClass.deprecatedMethod();
}
}
@Deprecated
class DeprecatedClass {
@Deprecated
public static int deprecatedField = 0;
@Deprecated
public static void deprecatedMethod() {
}
}
```

**参考信息**

- 链接：内部链接已省略
- 最新更新时间：2021-11-16 19:28:07

<a id="rule-124"></a>

### 124. G.NAM.01 标识符应由不超过64字符的字母、数字和下划线组成

| 字段 | 内容 |
|---|---|
| 问题级别 | 提示 |
| 语言 | JAVA |
| 标签 | fixbotengine-java |
| 关联工具规则 | IdentifierName |
| 规则类型 | 通用规范建议类 |
| 预估误报率 | 10% |

**审查要点**

```text
所有标识符仅使用ASCII字母、数字、下划线_，名称由正则表达式匹配\w{2,64}
工具检查场景:
- 接口，枚举，类，变量，方法名，参数，常量，枚举常量的标识符长度在2到64之间；
- 只检查标识符的声明，不检查标识符的引用；
- 例外：循环变量和catch块中的异常变量允许1个字符
```

**修复建议**

修改变量名

**正确示例**

String str;

**错误示例**

String s;

**参考信息**

- 链接：内部链接已省略
- 最新更新时间：2021-06-24 09:36:44

<a id="rule-125"></a>

### 125. G.NAM.03 类、枚举和接口名应采用大驼峰命名

| 字段 | 内容 |
|---|---|
| 问题级别 | 提示 |
| 语言 | JAVA |
| 标签 | fixbotengine-java |
| 关联工具规则 | TypeName |
| 规则类型 | 通用规范建议类 |
| 预估误报率 | 10% |

**审查要点**

```text
类名通常是名词或名词短语，采用首字母大写的驼峰命名法UpperCamelCase。例如， Character或 ImmutableList。接口名称也可以是名词或名词短语（例如List），但有时可能是形容词或形容词短语（例如Readable）。
说明：类的命名，不应用动词，而应使用名词，比如Customer，WikiPage，Account；避免采用类似Manager，Processor，Data，Info这样模糊的词。
测试类以它们正在测试的类的名称开头，并以Test结尾。例如，HashTest或HashIntegrationTest。
抽象类命名时推荐以Abstract或Base开头。
工具检查场景:
- 检查类、枚举和接口名是否采用大驼峰命名（`^[A-Z][a-zA-Z0-9]*$`)
```

**修复建议**

修改类型名

**正确示例**

```text
class MarcoPolo {}
interface TaPromotion {}
class OrderInfo {}
```

**错误示例**

```text
class marcoPolo {}
interface TAPromotion {}
class info {}
```

**参考信息**

- 链接：内部链接已省略
- 最新更新时间：2021-06-24 09:36:44

<a id="rule-126"></a>

### 126. G.NAM.04 方法名应采用小驼峰命名

| 字段 | 内容 |
|---|---|
| 问题级别 | 提示 |
| 语言 | JAVA |
| 标签 | fixbotengine-java |
| 关联工具规则 | MethodName |
| 规则类型 | 通用规范建议类 |
| 预估误报率 | 10% |

**审查要点**

```text
方法名称通常是动词或动词短语，并采用首字母小写的驼峰命名法lowerCamelCase。
工具检查场景:
- 检查方法名是否采用小驼峰命名（`^[a-z][a-zA-Z0-9]*$`)
- 例外场景
- 不检查Junit单元测试方法（@Test，@Before，@After...）
- 不检查覆写的方法（@Override）
```

**修复建议**

修改方法名

**正确示例**

```text
public boolean isFinished()
public void setVisible(boolean)
public void draw()
public void addKeyListener(Listener)
boolean hasNext()
```

**错误示例**

```text
public boolean Finished()
public void visible(boolean)
public void DRAW()
public void KeyListener(Listener)
boolean next()
```

**参考信息**

- 链接：内部链接已省略
- 最新更新时间：2021-06-24 09:36:44

<a id="rule-127"></a>

### 127. G.NAM.06 变量采用小驼峰命名

| 字段 | 内容 |
|---|---|
| 问题级别 | 提示 |
| 语言 | JAVA |
| 标签 | fixbotengine-java |
| 关联工具规则 | NonConstantName |
| 规则类型 | 通用规范建议类 |
| 预估误报率 | 10% |

**审查要点**

```text
变量、属性、参数等非常量字段的名称通常是名词或名词短语，应采用首字母小写的驼峰命名法(lowerCamelCase)。
即使局部变量是final或不可改变(immutable)的，也不应该把它视为常量，自然也不能用常量的规则去命名。
工具检查场景:
- 检查变量名（类成员变量、类变量、方法参数，局部变量）是否采用小驼峰命名（^[a-z][a-zA-Z0-9]*$）
```

**修复建议**

修改变量名

**正确示例**

```text
String customerName;
List<String> users = new ArrayList<>(DEFAULT_CAPACITY);
```

**错误示例**

```text
String Customername;
List<String> User = new ArrayList<>(DEFAULT_CAPACITY);
```

**参考信息**

- 链接：内部链接已省略
- 最新更新时间：2021-06-24 09:36:44

<a id="rule-128"></a>

### 128. G.NAM.07 避免使用具有否定含义布尔变量名

| 字段 | 内容 |
|---|---|
| 问题级别 | 提示 |
| 语言 | JAVA |
| 标签 | fixbotengine-java |
| 关联工具规则 | BoolVariableName |
| 规则类型 | 通用规范建议类 |
| 预估误报率 | 10% |

**审查要点**

```text
当使用逻辑非运算符并出现双重否定时，会出现理解问题。!isNotError意味着什么，不是很直白。
JavaBeans规范会对布尔型的类的属性自动生成isXxx()的getter。但此条目不是强制要求所有的布尔变量以is开头。
为避免有些自动处理工具（Spring，IDE，Lombok）对类的布尔属性的意外处理，不强制要求以is命名类的布尔属性。
工具检查场景:
- 检查布尔变量名是否具有否定含义（包含no或not）
注意，IDE可以定制getter/setter代码生成模版；序列化等框架也是可设置注解的，例如更改序列化字段名
```

**修复建议**

修改变量名，去除否定含义的词

**正确示例**

```text
boolean isError;
boolean isFound;
```

**错误示例**

```text
boolean isNoError;
boolean isNotFound;
```

**参考信息**

- 链接：内部链接已省略
- 最新更新时间：2025-05-27 09:58:46

<a id="rule-129"></a>

### 129. G.OBJ.02 不要在父类的构造方法中调用可能被子类覆写的方法

| 字段 | 内容 |
|---|---|
| 问题级别 | 一般 |
| 语言 | JAVA |
| 标签 | fixbotengine-java |
| 关联工具规则 | ConstructorInvokesOverridable |
| 规则类型 | 通用规范规则类 |
| 预估误报率 | 15% |

**审查要点**

```text
当在父类的构造方法中调用可能被子类覆写的方法时，构造方法的表现是不可预知的，很可能会导致异常。而问题出现后，往往难以快速定位。
这是由于在Java中，当子类初始化时，会调用父类的构造方法，当父类构造方法调用了被子类覆写的方法，往往会由于子类的初始化未完成而导致异常。
```

**修复建议**

构造函数不应调用可覆盖的函数，无论是通过将该函数指定为 `final`，还是将该类指定为 `final` 的方式。或者，如果此代码仅在构造函数中才需要，则可使用访问说明符 `private`，或直接将逻辑放在子类的构造函数中。

**正确示例**

```text
package CodeCorrectness.ConstructorInvokesOverridableFunction;
import org.springframework.util.StringUtils;
public class CodeCorrectness_ConstructorInvokesOverridableFunction_Internal_PublicMethod_01 {
private String username;
private boolean valid;
public CodeCorrectness_ConstructorInvokesOverridableFunction_Internal_PublicMethod_01(String username, String password){
this.username = username;
}
public boolean validateUser(String username, String password) {
if(!StringUtils.isEmpty(username) && !StringUtils.isEmpty(password)){
return true;
}
return false;
}
}
```

**错误示例**

```text
package CodeCorrectness.ConstructorInvokesOverridableFunction;
import org.springframework.util.StringUtils;
public class CodeCorrectness_ConstructorInvokesOverridableFunction_Internal_PublicMethod_01 {
private String username;
private boolean valid;
public CodeCorrectness_ConstructorInvokesOverridableFunction_Internal_PublicMethod_01(String username, String password){
this.username = username;
/* POTENTIAL FLAW: 类的构造函数调用了可被覆盖的函数。 */
this.valid = validateUser(username,password);
}
public boolean validateUser(String username, String password) {
if(!StringUtils.isEmpty(username) && !StringUtils.isEmpty(password)){
return true;
}
return false;
}
}
```

**参考信息**

- 链接：内部链接已省略
- 最新更新时间：2021-09-15 20:25:25

<a id="rule-130"></a>

### 130. G.OBJ.04 避免在无关的变量或无关的概念之间重用名字，避免隐藏（hide）、遮蔽（shadow）和遮掩（obscure）

| 字段 | 内容 |
|---|---|
| 问题级别 | 严重 |
| 语言 | JAVA |
| 标签 | fixbotengine-java |
| 关联工具规则 | HiddenField |
| 规则类型 | 通用规范规则类 |
| 预估误报率 | 15% |

**审查要点**

```text
在声明子类的属性、方法或嵌套类型时，除了覆写（override）、重载（overload）之外，要尽量避免重名导致的隐藏（hide）、遮蔽（shadow）和遮掩（obscure）。
这些名字重用的术语定义如下：
覆写（override）------子类与父类间
一个类的实例方法可以覆写（override）在其超类中可访问到（非private）的具有相同签名的实例方法（非static），从而使能了动态分派（dynamic dispatch）；换句话说，VM将基于实例的运行期类型来选择要调用的覆写方法。
class Base {
public void fn() {
...
}
}
class Derived extends Base {
@Override
public void fn() { // 覆写Base.fn()
...
}
}
重载（overload）------类内部
在某个类中的方法可以重载（overload）另一个方法，只要它们具有相同的名字和不同的签名。由调用所指定的重载方法是在编译期选定的。重载的方法应该按顺序放在一起，中间不要插入其他的方法，以提升代码的可读性。使重载产生歧义或混淆的场景包括：
可变参数；
包装类型，例如参数分别是int与Integer。
以上场景，不应该使用重载，应该修改方法名，如果是构造方法，则委托到不同名的静态方法。
class CircuitBreaker {
public void fn(int it) {}
public void fn(String str) {}
}
隐藏（hide）------子类与父类间
一个类的属性、静态方法或内部类可以分别隐藏（hide）在其超类中可访问到的具有相同名字（对方法而言就是相同的方法签名）的所有属性、静态方法或内部类。上述成员被隐藏后，将阻止其被继承：
class Swan {
protected String name = "Swan";
public static void fly() {
System.out.println("swan can fly ...");
}
}
class UglyDuck extends Swan {
protected String name = "UglyDuck";
public static void fly() { // 隐藏Swan.fly
System.out.println("ugly duck can't fly ...");
}
}
public class TestFly {
public static void main(String[] args) {
Swan swan = new Swan();
Swan uglyDuck = new UglyDuck();
swan.fly(); // 打印swan can fly ...
uglyDuck.fly(); // 还是打印swan can fly ...，hide让人以为是覆写了，其实不是
System.out.println(swan.name); // 打印 Swan
System.out.println(uglyDuck.name); // 打印 Swan
}
}
遮蔽（shadow）------类内部
一个变量、方法或类可以分别遮蔽（shadow）在类内部具有相同名字的变量、方法或类。如果一个实体被遮蔽了，那么就无法用简单名引用到它：
【反例】
方法的局部变量遮蔽了类的静态变量
public class WhoKnows {
static String sentence = "I don't know.";
public static void main(String[] args) {
String sentence = "I know!"; // 遮蔽了类的静态成员sentence
System.out.println(sentence); // 打印的是I know！
}
}
遮掩（obscure）------类内部
一个变量可以遮掩具有相同名字的一个类，只要它们都在同一个范围内：如果这个名字被用于变量与类都被许可的范围，那么它将引用到变量上。相似地，一个变量或一个类型可以遮掩一个包。
遮掩是唯一一种两个名字位于不同的名字空间的名字重用形式，这些名字空间包括：变量、包、方法或类型。如果一个类或一个包被遮掩了，那么不能通过其简单名引用到它。
遵守命名习惯可以极大地消除产生遮掩的可能性。
【反例】（变量命名也违反了小驼峰命名的规则）
public class Obscure {
static String System; // 遮掩java.lang.System
public static void main(String[] args) {
// 下面这行无法编译: System引用到static属性
System.out.println("hello, obscure world!");
}
}
```

**修复建议**

重命名该变量

**正确示例**

```text
public class HiddenFieldTest {
Object obj;
public void test(Object obj1) {
}
}
```

**错误示例**

```text
public class HiddenFieldTest {
Object obj;
public void test(Object obj) {
}
}
```

**参考信息**

- 链接：内部链接已省略
- 最新更新时间：2025-05-27 09:58:46

<a id="rule-131"></a>

### 131. G.OBJ.05 避免基本类型与其包装类型的同名重载方法

| 字段 | 内容 |
|---|---|
| 问题级别 | 一般 |
| 语言 | JAVA |
| 标签 | fixbotengine-java |
| 关联工具规则 | WrapperClassInOverloadMethod |
| 规则类型 | 通用规范建议类 |
| 预估误报率 | 15% |

**审查要点**

```text
有歧义的重载或者误导性的重载，会导致非预期的结果。
工具检查场景：
- 检查类中是否有基本类型和其包装类型的同名重载方法
```

**修复建议**

重命名方法，消除重载

**正确示例**

```text
class SomeResource {
HashMap<Integer, Integer> hm = ...;
public static Employee createSomeResourceByInt(int id, String name) {
// 非重载，使用int类型的id构造对象
}
public static Employee createSomeResourceByInteger(Integer id, String name) {
// 非重载，使用Integer类型的id构造对象
}
public Integer getDataByIndex(int id) {
// 非重载
}
public String getDataByValue(Integer id) {
// 非重载
}
}
```

**错误示例**

```text
class SomeResource {
HashMap<Integer, Integer> hm = ...;
public SomeResource(int id, String name) {
...
}
public SomeResource(Integer id, String name) {
...
}
public String getData(Integer id) { // 重载序列 #1
// 获取一个特定的记录
String str = hm.get(id).toString();
return str + SUFFIX;
}
public Integer getData(int id) { // 重载序列 #2
// 获取在位置id的记录
return hm.get(id);
}
}
```

**参考信息**

- 链接：内部链接已省略
- 最新更新时间：2021-06-24 09:36:53

<a id="rule-132"></a>

### 132. G.OBJ.06 覆写equals方法时，要同时覆写hashCode方法

| 字段 | 内容 |
|---|---|
| 问题级别 | 严重 |
| 语言 | JAVA |
| 标签 | fixbotengine-java |
| 关联工具规则 | EqualsHashCode |
| 规则类型 | 通用规范规则类 |
| 预估误报率 | 15% |

**审查要点**

```text
Java中==、!=运算符用于对象比较时，比较的是两个对象是否引用（references）的同一个对象，不会判断两个对象逻辑上是否相等。Java中的基类Object中的equlas()方法实现的逻辑与==运算符是相同。
当对象需要进行逻辑相等的比较时（比如判断String、Integer对象中的值是否相同），应对Object的equlas()方法进行覆写，在该方法中实现具体的判断逻辑。覆写equlas()方法时，要同步覆写hashCode()方法。Java对象在存放到基于Hash的集合（如HashMap、HashTable等）时，会使用其Hash码进行索引，如果只覆写了equals方法，而没有正确覆写hashCode方法，则会导致效率低下甚至出错。
Java对象的hashCode方法有如下约定：
1.同一次运行中，同一个对象如果equals方法中用到的信息没有改变，多次调用其hashCode方法返回值必须相同；
2.如果对两个对象调用equals方法时相等，则这两个对象的hashCode方法，也必须返回相同的值；
3.如果对两个对象调用equals方法时不相等，则对这两个对象的hashCode方法，不要求其返回值不同，但是出于减少哈希碰撞的性能考虑，最好能不同。
Java对象进行逻辑相等判断时，必须调用覆写的equlas()方法进行比较，不要使用==、!=运算符进行比较。当需要比较两个字符串（String类型的对象）是否相等时，应使用equlas()方法，当忽略大小写字母差异比较时，应使用equalsIgnoreCase()方法。
```

**修复建议**

覆写hashCode方法

**正确示例**

```text
public class ClassDemo {
@Override
public boolean equals(Object obj) {
...
}
@Override
public int hashCode() {
return ...
}
}
```

**错误示例**

```text
public class ClassDemo {
@Override
public boolean equals(Object obj) {
...
}
}
```

**参考信息**

- 链接：内部链接已省略
- 最新更新时间：2021-07-21 10:52:40

<a id="rule-133"></a>

### 133. G.OBJ.07 子类覆写父类方法或实现接口时必须加上@Override注解

| 字段 | 内容 |
|---|---|
| 问题级别 | 一般 |
| 语言 | JAVA |
| 标签 | fixbotengine-java |
| 关联工具规则 | OverrideAnnotation |
| 规则类型 | 通用规范规则类 |
| 预估误报率 | 15% |

**审查要点**

加上@Override注解的好处是，如果覆写时因为疏忽，导致子类方法的参数同父类不一致，编译时会报错，使问题在编译期就被发现；如果父类修改了方法定义造成子类不再覆写父类方法，也能使问题在编译期尽早被发现。

**修复建议**

增加@Override注解

**正确示例**

```text
public class ClassDemo {
public void methodDemo() {
...
}
}
public class SubClassDemo extends ClassDemo {
@Override
public void methodDemo() {
...
}
}
```

**错误示例**

```text
public class ClassDemo {
public void methodDemo() {
...
}
}
public class SubClassDemo extends ClassDemo {
public void methodDemo() {
...
}
}
```

**参考信息**

- 链接：内部链接已省略
- 最新更新时间：2021-06-24 09:36:53

<a id="rule-134"></a>

### 134. G.OBJ.08 正确实现单例模式

| 字段 | 内容 |
|---|---|
| 问题级别 | 提示 |
| 语言 | JAVA |
| 标签 | fixbotengine-java |
| 关联工具规则 | UntrustedSingleton |
| 规则类型 | 通用规范建议类 |
| 预估误报率 | 15% |

**审查要点**

```text
单例模式（Singleton Pattern）属于创建型模式，它确保在同一个进程内，单例类只有一个对象，并且该对象对所有其他对象提供访问，常见的如Windows系统下的资源管理器、Spring Bean等都会采用这种方式。
一般来说，正确实现单例有如下几点要求：
将其构造方法设为私有；
防止对象在初始化被多个线程同时运行；
确保该对象不可序列化；
确保该对象无法克隆。
```

**修复建议**

修改成私有构造方法

**正确示例**

```text
final class RecommandSingleton {
private static class SingletonHolder {
static final RecommandSingleton INSTANCE = new RecommandSingleton();
}
private RecommandSingleton() {
}
public static RecommandSingleton getInstance() {
return SingletonHolder.INSTANCE;
}
}
利用静态内部类实现单例
```

**错误示例**

```text
class UntrustedSingletonDemo {
private static UntrustedSingletonDemo instance;
protected UntrustedSingletonDemo() {
instance = new UntrustedSingletonDemo();
}
}
非私有构造方法
```

**参考信息**

- 链接：内部链接已省略
- 最新更新时间：2021-06-24 09:36:53

<a id="rule-135"></a>

### 135. G.OBJ.09 使用类名调用静态方法，而不要使用实例或表达式来调用

| 字段 | 内容 |
|---|---|
| 问题级别 | 严重 |
| 语言 | JAVA |
| 标签 | fixbotengine-java |
| 关联工具规则 | AccessStaticViaInstance |
| 规则类型 | 通用规范规则类 |
| 预估误报率 | 15% |

**审查要点**

明确地使用类名调用静态方法不容易造成混淆。使用实例调用静态方法时，调用的静态方法是声明类型的静态方法，与实例的实际类型无关，可能会导致与预期的结果不一致。当父类和子类有同名静态方法时，声明父类变量引用子类实例，使用该实例调用同名的静态方法调用的是父类的静态方法，而非子类的静态方法。类的静态属性也要使用类名进行调用。

**修复建议**

通过类名调用

**正确示例**

```text
用类名来调用静态方法
class Dog {
public static void bark() {
System.out.print("woof");
}
}
class Basenji extends Dog {
public static void bark() {
System.out.println("miao");
}
}
public class Bark {
public static void main(String[] args) {
Dog.bark();
Basenji.bark();
}
}
```

**错误示例**

```text
class Dog {
public static void bark() {
System.out.println("woof");
}
}
class Basenji extends Dog {
public static void bark() {
System.out.println("miao");
}
}
public class Bark {
public static void main(String[] args) {
Dog woofer = new Dog();
Dog nipper = new Basenji();
woofer.bark();
nipper.bark();
}
}
上述示例中，对bark()的两次调用，实际调用的都是Dog.bark()方法。
```

**参考信息**

- 链接：内部链接已省略
- 最新更新时间：2021-06-24 09:36:53

<a id="rule-136"></a>

### 136. G.OBJ.10 接口定义中去掉多余的修饰词

| 字段 | 内容 |
|---|---|
| 问题级别 | 一般 |
| 语言 | JAVA |
| 标签 | fixbotengine-java |
| 关联工具规则 | RedundantModifier |
| 规则类型 | 通用规范建议类 |
| 预估误报率 | 15% |

**审查要点**

```text
在接口定义中，属性已缺省具有public static final修饰词，方法已缺省具有public abstract修饰词。因此在代码中不要再次提供这些修饰词。
- 检查接口的属性是否包含public、static或final修饰词
- 检查接口的方法是否包含public或abstract修饰词
```

**修复建议**

删除相应的修饰词

**正确示例**

```text
public interface InterfaceDemo {
int STATIC_VAR = 100;
}
```

**错误示例**

```text
public interface InterfaceDemo {
public static final int STATIC_VAR = 100;
}
```

**参考信息**

- 链接：内部链接已省略
- 最新更新时间：2021-06-30 15:58:29

<a id="rule-137"></a>

### 137. G.OTH.01 安全场景下必须使用密码学意义上的安全随机数

| 字段 | 内容 |
|---|---|
| 问题级别 | 一般 |
| 语言 | JAVA |
| 标签 | SecBrella,CodeMars |
| 关联工具规则 | SecJ_Insecure_Randomness_Hardcoded_Seed,SecJ_Insecure_Randomness_Predictable_Seed,Insecure_Randomness,SecJ_Insecure_Randomness |
| 规则类型 | 安全规范规则类 |
| 预估误报率 | 20% |

**审查要点**

```text
不安全的随机数可能被部分或全部预测到，导致系统存在安全隐患，安全场景下使用的随机数必须是密码学意义上的安全随机数。密码学意义上的安全随机数分为两类：
- 真随机数产生器产生的随机数；
- 以真随机数产生器产生的少量随机数作为种子的密码学安全的伪随机数产生器产生的大量随机数。
已知的可供产品使用的密码学安全的非物理真随机数产生器有：Linux操作系统的/dev/random设备接口（存在阻塞问题）和Windows操作系统的CryptGenRandom()接口。
Java中的SecureRandom是一种密码学安全的伪随机数产生器，对于使用非真随机数产生器产生随机数时，要使用少量真随机数作为种子。
常见安全场景包括但不限于以下场景：
- 用于密码算法用途，如生成IV、盐值、密钥等；
- 会话标识（sessionId）的生成；
- 挑战算法中的随机数生成；
- 验证码的随机数生成；
```

**修复建议**

```text
参考公司Java语言编程规范 V5.x：内部链接已省略
```

**正确示例**

```text
【正例】（存在较高的阻塞风险）
```java
public byte[] generateSalt() {
byte[] salt = new byte[8];
try {
SecureRandom random = SecureRandom.getInstanceStrong();
random.nextBytes(salt);
} catch (NoSuchAlgorithmException ex) {
// 处理异常
}
return salt;
}
```
Java 8中添加了SecureRandom.getInstanceStrong()方法，用于生成不同平台上的最强的SecureRandom实现的实例：windows下默认为Windows-PRNG (sun.security.mscapi.PRNG)，使用CryptGenRandom()方法产生随机数；Solaris/Linux/macOS平台下默认为NativePRNGBlocking (sun.security.provider.NativePRNG$Blocking)，在初始种子、获取随机数、生成种子等场景下都是来自于/dev/random中的随机数，所以存在阻塞问题。对于具体算法可通过java.security配置文件中的securerandom.strongAlgorithms配置项进行设置。该方式生成的随机数可以用于安全场景，linux场景下需要考虑阻塞问题，防止影响系统的可用性，阻塞问题可参考《密码算法应用规范》。
Android系统中，该方式默认采用的是SHA1PRNG，不符合安全要求。
【正例】（JDK 9+）
```java
public byte[] generateSalt() {
byte[] salt = new byte[8];
try {
SecureRandom random = SecureRandom.getInstance("DRBG", DrbgParameters.instantiation(256, RESEED_ONLY, null));
random.nextBytes(salt);
} catch (NoSuchAlgorithmException ex) {
// 处理异常
}
return salt;
}
```
从JDK 9开始，新增了由Sun提供的符合NIST SP 800 90-A标准的DRBG伪随机数产生器，包括HASH-DRBG、HMAC-DRBG、CTR-DRBG三种，且适用于各种OS，符合公司密码算法相关要求，在使用JDK 9及以上版本时，推荐优先使用该方式生成安全随机数。
```

**错误示例**

```text
【反例】
```java
public byte[] generateSalt() {
byte[] salt = new byte[8];
Random random = new Random(123456L);
random.nextBytes(salt);
return salt;
}
```
Random生成是不安全随机数，不能用做盐值。
【反例】
```java
public byte[] generateSalt() {
byte[] salt = new byte[8];
SecureRandom random = new SecureRandom();
random.nextBytes(salt);
return salt;
}
```
new SecureRandom()在不同的操作系统下，使用不同的随机数生成器（windows下默认是SHA1PRNG(sun.security.provider.SecureRandom)、linux下默认是NativePRNG(sun.security.provider.NativePRNG)）。比如linux系统下，默认使用的随机数产生器使用的种子及生成的随机数都来源于/dev/urandom，生成的随机数是不安全随机数，不能用作盐值。
【反例】
```java
public byte[] generateSalt() {
byte[] salt = new byte[8];
try {
SecureRandom random = SecureRandom.getInstance("SHA1PRNG", "SUN");
random.setSeed(random.generateSeed(SEED_LEN)); // 频繁设置种子可能会导致阻塞
random.nextBytes(salt);
} catch (NoSuchAlgorithmException ex) {
// 处理异常
}
return salt;
}
```
上述代码中，明确指定采用sun.security.provider.SecureRandom作为随机数产生器，然后使用generateSeed()方法产生的随机数作为种子，该方法产生的随机数默认为真随机数（如linux下从/dev/random获取）。上述代码实际是使用少量真随机数作为种子（种子长度推荐不少于64bytes），然后采用伪随机数产生器来产生随机数，为了避免linux下阻塞问题，要尽量重复使用随机数生成器，避免频繁设置种子。对于需要生成大量随机数的场景，需要周期性补充种子，SHA1PRNG算法目前业界没有明确标准，推荐获取2^32次随机数后设置一次种子（调用一次nextBytes()、nextInt()等都计为一次获取随机数操作）。
但是根据公司密码算法相关要求，从2023年开始将禁止使用SHA1PRNG算法生成安全随机数。
```

**参考信息**

- 链接：内部链接已省略
- 最新更新时间：2025-05-27 09:58:46

<a id="rule-138"></a>

### 138. G.OTH.02 必须使用SSLSocket代替Socket来进行安全数据交互

| 字段 | 内容 |
|---|---|
| 问题级别 | 一般 |
| 语言 | JAVA |
| 标签 | CodeMars |
| 关联工具规则 | Bad_Practices_Sockets_Bind_IP,Bad_Practices_Sockets |
| 规则类型 | 安全规范规则类 |
| CWE 信息 | 246 |
| 预估误报率 | 20% |

**审查要点**

只有在与比较陈旧的系统进行通信时，J2EE 标准才允许 use of sockets，因为此时没有较高高级别的协议可用。

**修复建议**

当网络通信中涉及明文的敏感信息时，需要使用SSLSocket而不是Socket。

**正确示例**

```text
public void test01Good() throws IOException {
// Exception handling has been omitted for the sake of brevity
// ...
SSLServerSocketFactory sslServerSocketFactory = (SSLServerSocketFactory) SSLServerSocketFactory.getDefault();
SSLServerSocket sslServerSocket = (SSLServerSocket) sslServerSocketFactory.createServerSocket(9999);
SSLSocket sslSocket = (SSLSocket) sslServerSocket.accept();
// ...
}
```

**错误示例**

```text
public void test01Bad() throws IOException {
// Exception handling has been omitted for the sake of brevity
// ...
/* POTENTIAL FLAW: 检查代码中是否使用了Socket进行通信，如果是则报告警。 */
ServerSocket serverSocket = new ServerSocket(9999);
Socket socket = serverSocket.accept();
// ...
}
```

**参考信息**

- 链接：内部链接已省略
- 最新更新时间：2021-09-15 20:06:15

<a id="rule-139"></a>

### 139. G.OTH.03 不用的代码段包括import，直接删除，不要注释掉

| 字段 | 内容 |
|---|---|
| 问题级别 | 一般 |
| 语言 | JAVA |
| 标签 | fixbotengine-java |
| 关联工具规则 | CodeInComment,UnusedImport |
| 规则类型 | 通用规范规则类 |
| 预估误报率 | 15% |

**审查要点**

```text
不用的import语句，增加了代码的耦合度，不利于维护。
提示：202506及之前版本以下场景存在误报，升级202512及之后版本解决
场景一：“方法中使用到了jdk21的新swtich语法（case null），会导致整个方法解析失败，只在该方法中使用到的import的类，会被认为没有使用，导致误报”
```java
private Product test(String str) {
Product product = new Product();
switch (str) {
case STR1, STR2 -> product.setProductName("测试1");
case STR3, STR4 -> product.setProductName("测试2");
case null, default -> product.setProductName("测试3");
}
return product;
}
```
```

**修复建议**

```text
避免不必要的import，包括下文中未使用的、重复引入的、java.lang下面的。
推荐使用公司 CodeCheck插件[内部链接已省略
```

**正确示例**

```text
```java
import java.util.ArrayList;
import java.util.List; // [GOOD]， 所有import都是该类使用的
public class UnusedImportGood {
private void addList(){
List list = new ArrayList<String>();
list.add("A");
}
}
```
```

**错误示例**

```text
```java
import java.util.ArrayList;
import java.util.List;
import java.util.Map; // [BAD]该import语句未使用
public class UnusedImportBad {
private void addList(){
List list = new ArrayList<String>();
list.add("A");
}
}
```
```

**参考信息**

- 链接：内部链接已省略
- 最新更新时间：2026-04-29 11:01:41

<a id="rule-140"></a>

### 140. G.OTH.04 禁止代码中包含公网地址

| 字段 | 内容 |
|---|---|
| 问题级别 | 严重 |
| 语言 | JAVA |
| 标签 | CodeMars,SecBrella |
| 关联工具规则 | AvoidUsingHardCodedIP |
| 规则类型 | 安全规范规则类 |
| CWE 信息 | 265 |
| 预估误报率 | 20% |

**审查要点**

```text
代码或脚本中包含用户不可见，不可知的公网地址，可能会引起客户质疑。
对产品发布的软件（包含软件包/补丁包）中包含的公网地址（包括公网IP地址、公网URL地址/域名、邮箱地址）要求如下：
1、禁止包含用户界面不可见、或产品资料未描述的未公开的公网地址。
2、已公开的公网地址禁止写在代码或者脚本中，可以存储在配置文件或数据库中。
对于开源/第三方软件自带的公网地址必须至少满足上述第1条公开性要求。
```

**修复建议**

将代码中硬编码的公网URL地址调整到配置文件中。

**正确示例**

```text
##### 场景1：代码中存在硬编码公网地址。
- 修复示例1：可以将公网地址放入配置文件中：
```properties
// 【GOOD】将系统使用到的公网URL地址调整到配置文件中，代码从配置文件读取该公网URL地址。
config.url = https://www.example.com/111
```
```

**错误示例**

```text
##### 场景1：代码中存在硬编码公网地址。
- 错误示例：代码中硬编码公网地址。
```java
// 【POTENTIAL FLAW】URL硬编码
public static final String HTTPS_URL = "https://www.example.com/111";
```
```

**参考信息**

- 链接：内部链接已省略
- 最新更新时间：2021-09-15 19:59:17

<a id="rule-141"></a>

### 141. G.OTH.06 禁止在用户界面、日志中暴露不必要信息

| 字段 | 内容 |
|---|---|
| 问题级别 | 严重 |
| 语言 | JAVA |
| 标签 | CodeMars |
| 关联工具规则 | Privacy_Violation_Password |
| 规则类型 | 安全规范规则类 |
| 预估误报率 | 20% |

**审查要点**

对密码信息处理不当会危及用户的个人隐私，这是一种非法行为。

**修复建议**

日志中不要保存敏感数据

**正确示例**

```text
private static final Logger LOGGER = Logger.getLogger(TestCase1.class);
...
LOGGER.info("Login success, user is " + userName + ", password is ********.");
```

**错误示例**

```text
private static final Logger LOGGER = Logger.getLogger(TestCase1.class);
...
LOGGER.info("Login success, user is " + userName + ", password is " +
encrypt(pwd.getBytes(StandardCharsets.UTF_8)));
```

**参考信息**

- 链接：内部链接已省略
- 最新更新时间：2025-05-27 09:58:45

<a id="rule-142"></a>

### 142. G.PRM.01 将集合转为数组时使用Collection<T>.toArray(T[])方法；Java 11后使用Collection<T>.toArray(IntFunction<T[]>)

| 字段 | 内容 |
|---|---|
| 问题级别 | 一般 |
| 语言 | JAVA |
| 标签 | fixbotengine-java |
| 关联工具规则 | ToArray |
| 规则类型 | 通用规范规则类 |
| 预估误报率 | 15% |

**审查要点**

```text
JDK 11引入了Collection<T>.toArray(IntFunction<T[]> generator)，它更好的就是不需要创建临时数组，一方面节省空间，另一方面这样就不用去考虑toArray(T[])里的参数长度对函数行为以及结果的影响。
工具检查场景:
- 检查toArray方法的参数（数组类型）的长度是否为0或集合大小
```

**修复建议**

size设置为0

**正确示例**

```text
List<String> list = new ArrayList<>(DEFAULT_CAPACITY);
list.add(getElm());
String[] array = list.toArray(new String[0]);
```

**错误示例**

```text
List<String> list = new ArrayList<>(DEFAULT_CAPACITY);
list.add(getElm());
String[] array = list.toArray(new String[DEFAULT_CAPACITY + 1]);
```

**参考信息**

- 链接：内部链接已省略
- 最新更新时间：2021-07-21 10:53:41

<a id="rule-143"></a>

### 143. G.PRM.02 使用System.arraycopy()或Arrays.copyOf()进行数组复制

| 字段 | 内容 |
|---|---|
| 问题级别 | 一般 |
| 语言 | JAVA |
| 标签 | fixbotengine-java |
| 关联工具规则 | AvoidArrayLoops |
| 规则类型 | 通用规范建议类 |
| 预估误报率 | 15% |

**审查要点**

```text
在将一个数组对象复制成另外一个数组对象时，不要自己使用循环复制，可以使用Java提供的System.arraycopy()功能来复制数据对象，这样做可以避免出错，而且效率会更高。java.util.Arrays.copyOf()是对System.arraycopy()便利化封装。
工具检查场景:
- 检查是否存在这样的for循环：循环体中包含两个数组对应下标元素的赋值语句，形如a[i] = b[i];
```

**修复建议**

使用System.arraycopy

**正确示例**

```text
int[] src = {1, 2, 3, 4, 5};
int[] dest = new int[5];
System.arraycopy(src, 0, dest, 0, 5);
```

**错误示例**

```text
int[] src = {1, 2, 3, 4, 5};
int[] dest = new int[5];
for (int i = 0; i < 5; i++) {
dest[i] = src[i];
}
```

**参考信息**

- 链接：内部链接已省略
- 最新更新时间：2021-06-24 09:37:01

<a id="rule-144"></a>

### 144. G.PRM.04 不要对正则表达式进行频繁重复预编译

| 字段 | 内容 |
|---|---|
| 问题级别 | 一般 |
| 语言 | JAVA |
| 标签 | fixbotengine-java |
| 关联工具规则 | UsePatternCompileInMethodOrForStatement |
| 规则类型 | 通用规范规则类 |
| 预估误报率 | 15% |

**审查要点**

在频繁调用的场景（例如在方法体内或循环语句中）中，定义Pattern会导致重复预编译正则表达式，降低程序执行效率。另外，对于JDK中的某些API会接受字符串格式的正则表达式作为参数，如String.replaceAll、String.split等，对于这些API的使用也要考虑性能问题。

**修复建议**

将正则Pattern抽取为成员变量

**正确示例**

```text
public class RegexExp {
private static final Pattern CHARSET_REG = Pattern.compile("[a-z]+");
// 该方法被频繁调用
private boolean isLowerCase(String str) {
if (CHARSET_REG.matcher(str).find()) {
return true;
}
return false;
}
}
上述示例中，isLowerCase()使用的是被编译过的正则，即使是被频繁调用，也不会有正则的重复编译。
```

**错误示例**

```text
public class RegexExp {
// 该方法被频繁调用
private boolean isLowerCase(String str) {
Pattern pattern = Pattern.compile("[a-z]+");
if (pattern.matcher(str).find()) {
return true;
}
return false;
}
}
上述示例中，isLowerCase()被调用时，会对正则进行编译。该方法被频繁调用时，都导致大量重复的正则编译操作。
```

**参考信息**

- 链接：内部链接已省略
- 最新更新时间：2021-06-24 09:37:01

<a id="rule-145"></a>

### 145. G.PRM.05 禁止创建不必要的对象

| 字段 | 内容 |
|---|---|
| 问题级别 | 一般 |
| 语言 | JAVA |
| 标签 | fixbotengine-java |
| 关联工具规则 | UnnecessaryNew |
| 规则类型 | 通用规范规则类 |
| 预估误报率 | 15% |

**审查要点**

```text
对于短生存周期、不常用的对象不要使用直接缓冲区。
从长生存周期容器对象中移除短生存周期对象：对于静态容器（如HashMap、ArrayList等），其生命周期与程序一致，容器中保存的对象在程序结束之前不能被释放，这样容易导致内存泄露问题。因此，应尽量避免使用静态集合；如果必须使用静态集合，要对于不再使用的对象及时从集合中移除。
当要通过实现AutoCloseable、Closeable接口自定义资源类时，建议实现的close()方法是幂等的，即多次调用该方法与一次调用的效果是相同的，重复调用该方法不会产生副作用（如抛出异常）。
尽量在同一代码抽象层实现资源管理，例如对于在一个方法内部中创建的IO流资源，如果方法在退出后该资源不再被使用，应该在方法中直接释放该资源；如果一个IO流资源是某Class的成员变量，建议该Class中提供一个释放IO流资源的方法。
```

**修复建议**

删除不必要的new语句

**正确示例**

```text
String foo = "string";
Integer bar = Integer.valueOf(90);
...
Integer baz = Integer.valueOf(90); // 默认在-128~127间，会重用内存中缓存的对象
```

**错误示例**

```text
String foo = new String("string"); // 建立了2个String对象
Integer bar = new Integer(90);
...
Integer baz = new Integer(90);
```

**参考信息**

- 链接：内部链接已省略
- 最新更新时间：2021-06-24 09:37:02

<a id="rule-146"></a>

### 146. G.PRM.07 进行IO类操作时，必须在try-with-resource或finally里关闭资源

| 字段 | 内容 |
|---|---|
| 问题级别 | 严重 |
| 语言 | JAVA |
| 标签 | fixbotengine-java |
| 关联工具规则 | ResourceRelease |
| 规则类型 | 通用规范规则类 |
| 预估误报率 | 15% |

**审查要点**

```text
申请的资源不再使用时，需要及时释放，否则会导致资源泄露问题。释放后的资源不要继续使用，否则可能导致系统抛出异常或其他未知不安全行为。
系统异常可能导致资源释放操作被跳过，因此对于IO、数据库操作等需要显式调用关闭方法（如close()）来释放资源的场景，必须在try-catch-finally的finally中调用关闭方法。如果有多个资源需要close()，需要分别对每个资源close()时的异常进行try-catch处理，防止某个资源关闭失败导致其他资源无法正常关闭，最终保证所有资源都能被正确释放。
Java 7有自动资源管理的特性try-with-resource，不需手动关闭。该方式应该优先于try-finally，这样得到的代码将更加简洁、清晰，产生的异常也更有价值。特别是对于多个资源关闭发生异常时，try-finally可能丢失掉前面的异常，而try-with-resource会保留第一个异常，并把后续的异常作为Suppressed exceptions，可通过getSuppressed()获取这些异常信息。
try-finally也常用于锁的释放等场景，保证异常场景下锁也能正常释放。
```

**修复建议**

使用try-with-resource或在finally关闭资源

**正确示例**

```text
try (FileInputStream in = new FileInputStream(inputFileName);
FileOutputStream out = new FileOutputStream(outputFileName)) {
copy(in, out);
}
```

**错误示例**

```text
FileInputStream in = null;
FileOutputStream out = null;
try {
in = new FileInputStream(inputFileName);
out = new FileOutputStream(outputFileName);
copy(in, out);
} catch (IOException e) {
}
```

**参考信息**

- 链接：内部链接已省略
- 最新更新时间：2021-06-24 09:37:02

<a id="rule-147"></a>

### 147. G.PRM.08 禁止使用主动GC（除非在密码、RMI等方面），尤其是在频繁/周期性的逻辑中

| 字段 | 内容 |
|---|---|
| 问题级别 | 严重 |
| 语言 | JAVA |
| 标签 | fixbotengine-java |
| 关联工具规则 | LoopGC |
| 规则类型 | 通用规范规则类 |
| 预估误报率 | 15% |

**审查要点**

```text
虽然主动调用GC方法时JVM规范不承诺立即进行垃圾回收操作，但是Oracle Java SE JVM在绝大多数情况下响应此方法调用，会触发JVM的全量GC操作，这会增加GC的次数，也就增加了程序因为GC而停顿的时间；而且在GC过程中的某些阶段程序会完全停顿，这会让程序失去响应，对系统造成非常大的风险。在频率/周期性的逻辑（for循环、定时器）中更要尽量避免主动GC的调用。
```

**修复建议**

去除循环内的gc调用，改到循环外

**正确示例**

```text
不使用主动GC，或者在循环之外的关键节点上调用主动GC。
for (String bookName : bookNames) {
Book book = new Book(bookName);
checkBook(book);
... // 其他操作
}
System.gc();
```

**错误示例**

```text
在循环中调用了System.gc()，会引起JVM频繁、连续地全量GC，从而造成业务逻辑线程阻塞，不响应或者很慢地响应业务请求。
for (String bookName : bookNames) {
Book book = new Book(bookName);
checkBook(book);
... // 其他操作
System.gc();
}
```

**参考信息**

- 链接：内部链接已省略
- 最新更新时间：2021-06-24 09:37:02

<a id="rule-148"></a>

### 148. G.PRM.09 禁止使用Finalizer机制

| 字段 | 内容 |
|---|---|
| 问题级别 | 一般 |
| 语言 | JAVA |
| 标签 | fixbotengine-java |
| 关联工具规则 | Finalizer,Finalize |
| 规则类型 | 通用规范规则类 |
| 预估误报率 | 15% |

**审查要点**

finalize()方法的调用时机是不可预测的，常常也是危险的。使用finalize()方法可能会导致不稳定的行为，更差的性能，以及带来移植性问题。

**修复建议**

禁止使用finalize()方法

**正确示例**

```text
##### 场景1：
- 修复示例：
```java
void doSomething() {
NetworkDemo demo = new NetworkDemo();
...
}
```
```

**错误示例**

```text
public void test() {
finalize();
}
```

**参考信息**

- 链接：内部链接已省略
- 最新更新时间：2025-01-13 21:41:05

<a id="rule-149"></a>

### 149. G.SEC.01 进行安全检查的方法必须声明为private或final

| 字段 | 内容 |
|---|---|
| 问题级别 | 一般 |
| 语言 | JAVA |
| 标签 | CodeMars |
| 关联工具规则 | Invalid_Security_Check |
| 规则类型 | 安全规范规则类 |
| CWE 信息 | 358 |
| 预估误报率 | 20% |

**审查要点**

如果一个方法被子类覆盖，则该子类可绕过其父类中的安全检查。

**修复建议**

请确保所有执行安全操作的方法都已在 final 类中声明，或者这些方法本身已声明为最终。

**正确示例**

```text
private void good() {
SecurityManager sm = System.getSecurityManager();
if (sm != null) {
sm.checkPermission(new SecurityPermission("SomeAction"));
}
// ...
}
```

**错误示例**

```text
/* POTENTIAL FLAW: 用于执行安全检查的非最终方法可能会被绕过安全检查的多种方式覆盖 */
public void bad() {
SecurityManager sm = System.getSecurityManager();
if (sm != null) {
sm.checkPermission(new SecurityPermission("SomeAction"));
}
// ...
}
```

**参考信息**

- 链接：内部链接已省略
- 最新更新时间：2021-09-15 20:18:36

<a id="rule-150"></a>

### 150. G.SEC.02 自定义类加载器覆写getPermission() 时，必须先调用父类的getPermission() 方法

| 字段 | 内容 |
|---|---|
| 问题级别 | 一般 |
| 语言 | JAVA |
| 标签 | CodeMars |
| 关联工具规则 | Call_Super_GetPermission |
| 规则类型 | 安全规范规则类 |
| CWE 信息 | 358 |
| 预估误报率 | 20% |

**审查要点**

继承URLClassLoader类时，重载getPermissions(CodeSource)函数时没有调用super.getPermissions(...)。

**修复建议**

继承URLClassLoader类时，重载getPermissions(CodeSource)函数时需要调用super.getPermissions(...)。

**正确示例**

```text
public class CWE358_J_Rule8_2_Call_Super_GetPermission_Good extends URLClassLoader {
// Other code…
@Override
protected PermissionCollection getPermissions(CodeSource cs) {
super.getPermissions(cs);
PermissionCollection pc = new Permissions();
// allow exit from the VM anytime
pc.add(new RuntimePermission("exitVM"));
return pc;
}
// Other code…
}
```

**错误示例**

```text
public class CWE358_J_Rule8_2_Call_Super_GetPermission_Bad extends URLClassLoader {
// Other code…
/* POTENTIAL FLAW: 继承URLClassLoader类时，重载getPermissions(CodeSource)函数时是否调用了super.getPermissions(...)。 */
@Override
protected PermissionCollection getPermissions(CodeSource cs) {
PermissionCollection pc = new Permissions();
// allow exit from the VM anytime
pc.add(new RuntimePermission("exitVM"));
return pc;
}
// Other code…
}
```

**参考信息**

- 链接：内部链接已省略
- 最新更新时间：2021-09-15 20:06:42

<a id="rule-151"></a>

### 151. G.SEC.04 使用安全管理器来保护敏感操作

| 字段 | 内容 |
|---|---|
| 问题级别 | 一般 |
| 语言 | JAVA |
| 标签 | CodeMars |
| 关联工具规则 | SecurityManager_Privacy_Operation |
| 规则类型 | 安全规范建议类 |
| CWE 信息 | 732 |
| 预估误报率 | 20% |

**审查要点**

所有的敏感操作必须经过安全管理器的检查，防止被不可信的代码调用。

**修复建议**

```text
请查看告警请查看告警清理指导：内部链接已省略
```

**正确示例**

```text
新增securityCheck()方法来保护resourceMap，防止不可信代码调用removeEntry()方法。
public final class SecureSensitiveMap {
private static final SensitiveResourcePermission REMOVE_ENTRY_PERMISSION =
new SensitiveResourcePermission("removeEntry");
private final Map<Integer, String> resourceMap = new HashMap<>();
public void removeEntry(Integer key) {
this.securityCheck();
resourceMap.remove(key);
}
private void securityCheck() {
SecurityManager securityManager = System.getSecurityManager();
if (securityManager != null) {
securityManager.checkPermission(REMOVE_ENTRY_PERMISSION);
}
}
}
配置如下:
grant codeBase "file:${{trusted.code.dirs}}/*" {
permission com.example.security.SensitiveResourcePermission "removeEntry";
};
```

**错误示例**

```text
示例中， resourceMap 包含敏感信息，然而 removeEntry() 方法是 public 的并且没有被安全管理器检查，因此恶意调用者可以通过该方法随意删除敏感信息。
public final class SensitiveMap {
private final Map<Integer, String> resourceMap = new HashMap<>();
public void removeEntry(Integer key) {
resourceMap.remove(key);
}
}
```

**参考信息**

- 链接：内部链接已省略
- 最新更新时间：2022-03-15 20:16:07

<a id="rule-152"></a>

### 152. G.SER.01 尽量避免实现Serializable接口

| 字段 | 内容 |
|---|---|
| 问题级别 | 提示 |
| 语言 | JAVA |
| 标签 | fixbotengine-java |
| 关联工具规则 | AvoidSerialization |
| 规则类型 | 通用规范建议类 |
| 预估误报率 | 15% |

**审查要点**

```text
使用Java内置序列化功能的主要场景是为了在当前程序之外保存对象并在需要的时候重新获得对象。鉴于以下原因，建议除非必须使用的第三方接口要求必须实现Serializable接口，否则请选用其它方式代替。
工具检查场景:
- 检查每个类声明是否实现Serializable接口
```

**修复建议**

实现了Serialization接口的类其属性需要被static或者transie关键字修饰，或者该属性对应的类也需要实现Serialization接口

**正确示例**

```text
public class AvoidSerializationTest implements Serializable {
private AvoidSerializationTest1 avoidSerializationTest1;
}
...
public class AvoidSerializationTest1 implements Serializable {
}
```

**错误示例**

```text
public class AvoidSerializationTest implements Serializable {
private AvoidSerializationTest1 avoidSerializationTest1;
}
...
public class AvoidSerializationTest1 {
}
```

**参考信息**

- 链接：内部链接已省略
- 最新更新时间：2021-06-24 09:36:58

<a id="rule-153"></a>

### 153. G.SER.04 禁止直接序列化指向系统资源的信息

| 字段 | 内容 |
|---|---|
| 问题级别 | 严重 |
| 语言 | JAVA |
| 标签 | fixbotengine-java |
| 关联工具规则 | SerializeFileHandle |
| 规则类型 | 通用规范规则类 |
| 预估误报率 | 15% |

**审查要点**

```text
当序列化结果中含有指向系统的资源时，这些信息很容易被篡改。当恶意用户篡改了指向系统的资源时，反序列化的对象会直接操作这些被攻击者指定的系统资源，导致任意文件读取或修改。因此，建议实现Serializable的类，其成员变量为File或FileDescriptor时，用transient修饰，避免这些对象被序列化。
指向系统资源的句柄如果必须序列化，可参考G.SER.06 序列化操作要防止敏感信息泄露中的要求进行防护，另外，反序列化来自外部的序列化数据构造的对象中的文件路径信息在使用前要进行校验。
```

**修复建议**

增加transient

**正确示例**

```text
final class SomeResource implements Serializable {
private static final long serialVersionUID = 6562477636399915529L;
transient File file;
public SomeResource(String fileName) {
file = new File(fileName);
...
}
}
```

**错误示例**

```text
final class SomeResource implements Serializable {
private static final long serialVersionUID = -2589766491699675794L;
File file;
public SomeResource(String fileName) {
file = new File(fileName);
...
}
}
```

**参考信息**

- 链接：内部链接已省略
- 最新更新时间：2021-07-21 10:53:09

<a id="rule-154"></a>

### 154. G.SER.05 禁止序列化非静态的内部类

| 字段 | 内容 |
|---|---|
| 问题级别 | 致命 |
| 语言 | JAVA |
| 标签 | CodeMars |
| 关联工具规则 | Forbid_Serialize_Nonstatic_Inner_Class |
| 规则类型 | 安全规范规则类 |
| CWE 信息 | 398 |
| 预估误报率 | 20% |

**审查要点**

内部类是一个没有显示或隐式声明为静态的嵌套类。对内部类（包括本地类和匿名类）进行序列化非常容易出错。

**修复建议**

在使用内部类时，不应对其进行序列化，否则它们将变为静态嵌套类，由此就不会出现对非静态内部类执行序列化时会出现的问题。如果嵌套类为静态类型，则其本质上与实例变量并无关联（包括外部类的实例变量），并且不会导致对外部类执行序列化。

**正确示例**

```text
public class CWE398_CodeCorrectness_NonStaticInnerClassImplementsSerializable_Good_01 implements Serializable {
private int accessLevel;
static class Registrator implements Serializable {
private String data;
public Registrator(String data){
this.data = data;
}
public void show(){
System.out.println(data);
}
}
}
```

**错误示例**

```text
public class CWE398_CodeCorrectness_NonStaticInnerClassImplementsSerializable_Bad_01 implements Serializable {
private int accessLevel;
/* POTENTIAL FLAW: 禁止序列化非静态的内部类。 */
class Registrator implements Serializable {
private String data;
public Registrator(String data){
this.data = data;
}
public void show(){
System.out.println(data);
}
}
}
```

**参考信息**

- 链接：内部链接已省略
- 最新更新时间：2021-07-07 17:12:56

<a id="rule-155"></a>

### 155. G.SER.07 防止反序列化被利用来绕过构造方法中的安全操作

| 字段 | 内容 |
|---|---|
| 问题级别 | 致命 |
| 语言 | JAVA |
| 标签 | CodeMars |
| 关联工具规则 | Missing_SecurityManager_Check_Serializable |
| 规则类型 | 安全规范规则类 |
| CWE 信息 | 358 |
| 预估误报率 | 20% |

**审查要点**

可序列化的类如果在其构造函数中执行 SecurityManager 检查，那么它还需要在其 readObject() 和 readObjectNoData 方法中执行相同的检查。

**修复建议**

如果可序列化的类的构造函数中存在 SecurityManager 检查，请确保 readObject() 和 readObjectNoData() 方法中存在相同的 SecurityManager 检查。

**正确示例**

```text
public final class CWE358_J_Rule7_3_Missing_SecurityManager_Check_Serializable_01_Good implements Serializable {
private static final long serialVersionUID = 9078808681344666097L;
// Private internal state
private String town;
private static final String UNKNOWN = "UNKNOWN";
public CWE358_J_Rule7_3_Missing_SecurityManager_Check_Serializable_01_Good() {
// Initialize town to default value
town = UNKNOWN;
}
private void writeObject(ObjectOutputStream out) throws IOException {
out.writeObject(town);
}
private void readObject(ObjectInputStream in) throws Exception {
in.defaultReadObject();
}
}
```

**错误示例**

```text
public final class CWE358_J_Rule7_3_Missing_SecurityManager_Check_Serializable_02_Bad implements Serializable {
private static final long serialVersionUID = 9078808681344666097L;
// Private internal state
private String town;
private static final String UNKNOWN = "UNKNOWN";
void performSecurityManagerCheck() throws SecurityException {
// verify whether current user has rights to access the file
SecurityManager securityManager = System.getSecurityManager();
}
public CWE358_J_Rule7_3_Missing_SecurityManager_Check_Serializable_02_Bad() {
performSecurityManagerCheck();
// Initialize town to default value
town = UNKNOWN;
}
/* POTENTIAL FLAW: 1）实现Serializable接口，并且在实现类的构造函数中包含安全检查器 2）基于第一点，工具检测readObject和writeObject方法中是否包含安全检查器，如果没有则报告警。 */
private void writeObject(ObjectOutputStream out) throws IOException {
out.writeObject(town);
}
/* POTENTIAL FLAW: 1）实现Serializable接口，并且在实现类的构造函数中包含安全检查器 2）基于第一点，工具检测readObject和writeObject方法中是否包含安全检查器，如果没有则报告警。 */
private void readObject(ObjectInputStream in) throws Exception {
in.defaultReadObject();
}
}
```

**参考信息**

- 链接：内部链接已省略
- 最新更新时间：2025-05-27 09:58:45

<a id="rule-156"></a>

### 156. G.SER.08 禁止直接将外部数据进行反序列化

| 字段 | 内容 |
|---|---|
| 问题级别 | 严重 |
| 语言 | JAVA |
| 标签 | SecBrella |
| 关联工具规则 | SecS_Dynamic_Code_Evaluation |
| 规则类型 | 安全规范规则类 |
| 预估误报率 | 50% |

**审查要点**

```text
secBrella告警清理指导链接:内部链接已省略
许多现代编程语言都允许动态解析源代码指令。这使得程序员可以执行基于用户输入的动态指令。当程序员错误地认为由用户直接提供的指令仅会执行一些无害的操作时（如对当前的用户对象进行简单的计算或修改用户的状态），就会出现 code injection 漏洞：
```

**修复建议**

请查看告警清理指导：内部链接已省略

**正确示例**

```text
public final class SecureObjectInputStream extends ObjectInputStream {
public SecureObjectInputStream() throws SecurityException, IOException {
super();
}
public SecureObjectInputStream(InputStream in) throws IOException {
super(in);
}
protected Class<?> resolveClass(ObjectStreamClass desc)
throws IOException, ClassNotFoundException {
if (!desc.getName().equals("com.example.PersionInfo")) { // 白名单校验
throw new ClassNotFoundException(desc.getName() + " not find");
}
return super.resolveClass(desc);
}
}
```

**错误示例**

```text
public class DeserializeExample implements Serializable {
private static final long serialVersionUID = -5809782578272943999L;
private String name;
public String getName() {
return name;
}
public void setName(String name) {
this.name = name;
}
private void readObject(java.io.ObjectInputStream ois) {
ois.defaultReadObject();
System.out.println("Hack!");
}
}
// 使用外部数据执行反序列化操作
ObjectInputStream ois2= new ObjectInputStream(fis);
PersionInfo myPerson = (PersionInfo) ois2.readObject();
```

**参考信息**

- 链接：内部链接已省略
- 最新更新时间：2025-05-27 09:58:46

<a id="rule-157"></a>

### 157. G.TYP.01 进行数值运算时，避免整数溢出

| 字段 | 内容 |
|---|---|
| 问题级别 | 一般 |
| 语言 | JAVA |
| 标签 | SecBrella |
| 关联工具规则 | SecJ_Integer_Overflow |
| 规则类型 | 安全规范建议类 |
| 预估误报率 | 20% |

**审查要点**

内置的整数运算符不会以任何方式来标识上溢或下溢。常见的加、减、乘、除都可能会导致整数溢出。另外，Java数据类型的合法取值范围是不对称的（最小值的绝对值比最大值大1），所以对最小值取绝对值()时，也会导致溢出。

**修复建议**

对于整数溢出问题，可以通过先决条件检测、使用Math类的安全方法、向上类型转换或者使用`BigInteger`等方法进行规避。

**正确示例**

```text
public static int multNum(int num1, int num2) {
return Math.multiplyExact(num1, num2);
}
```

**错误示例**

```text
public static int multNum(int num1, int num2) {
return num1 * num2;
}
```

**参考信息**

- 链接：内部链接已省略
- 最新更新时间：2021-07-07 17:12:56

<a id="rule-158"></a>

### 158. G.TYP.02 确保除法运算和模运算中的除数不为0

| 字段 | 内容 |
|---|---|
| 问题级别 | 一般 |
| 语言 | JAVA |
| 标签 | SecBrella |
| 关联工具规则 | SecJ_Denominator_Length_Check |
| 规则类型 | 安全规范规则类 |
| CWE 信息 | 265 |
| 预估误报率 | 20% |

**审查要点**

```text
如果除法或模运算中的除数为零可能会导致程序终止或拒绝服务（DoS），因此需要在运算前保证除数不为0。
202406新增一个场景：BigDecimal的divide方法调用前先保证除数不为0，该场景目前仅能通过正确示例中的写法消除告警，其他方式误报可申请屏蔽。
```

**修复建议**

用作除数的整数，在运算前保证其不为0。

**正确示例**

```text
### 场景1：除数未判断是否为0
- 修复示例1：BigDecimal的devide使用前判断不为0
```java
public void doSomething(String s) throws Exception {
BigDecimal bigDecimal = new BigDecimal(s);
if (bigDecimal.compareTo(BigDecimal.ZERO) != 0) {
/* GOOD: 除零被除数bigDecimal已经在if中做判断 */
BigDecimal newBigDecimal = new BigDecimal("100").divide(bigDecimal, MathContext.DECIMAL128);
}
... // 其他逻辑代码
}
```
- 修复示例2：
```java
private HostAddress getAddr() {
List<HostAddress> addr = nebulaSessPoolConfig.getGraphAddrArray();
if (CollectionUtils.isNotEmpty(addr)) { // 校验addr是否为空
int newPos = (pos.getAndIncrement()) % addr.size();
return addr.get(newPos);
}
... // 其他逻辑代码
}
```
```

**错误示例**

```text
##### 场景1：除数未判断是否为0
- 错误示例1：
```java
public void testcase() {
long dividendNum = 0;
int divisorNum1 = 0;
Integer divisorNum2 = 0;
/* 不符合: 使用除法运算或模运算没有判断除数大小。 */
long result1 = dividendNum / divisorNum1;
/* 不符合: 使用除法运算或模运算没有判断除数大小。 */
long result2 = dividendNum % divisorNum2;
}
```
- 错误示例2：
```java
private HostAddress getAddr() {
List<HostAddress> addr = nebulaSessPoolConfig.getGraphAddrArray();
// 【不符合】 集合中的元素数量可能为0
int newPos = (pos.getAndIncrement()) % addr.size();
return addr.get(newPos);
}
```
```

**参考信息**

- 链接：内部链接已省略
- 最新更新时间：2024-12-12 20:49:37

<a id="rule-159"></a>

### 159. G.TYP.03 禁止使用浮点数作为循环计数器

| 字段 | 内容 |
|---|---|
| 问题级别 | 严重 |
| 语言 | JAVA |
| 标签 | fixbotengine-java |
| 关联工具规则 | LoopFloat |
| 规则类型 | 通用规范规则类 |
| 预估误报率 | 15% |

**审查要点**

由于浮点数存在精度问题，用作循环计数器可能会导致非预期的结果。

**修复建议**

使用整型作为循环变量

**正确示例**

```text
for (int index = 2000000000; index < 2000000050; index++) {
...
}
上述示例中，使用整数作为循环计数器。
```

**错误示例**

```text
for (float flt = (float) 2000000000; flt < 2000000050; flt++) {
...
}
上述示例中，由于浮点数的精度问题导致条件判断结果与预期不符：因为(float) 2000000000 == 2000000050结果为true，所以循环体不会执行。
```

**参考信息**

- 链接：内部链接已省略
- 最新更新时间：2025-05-27 09:58:46

<a id="rule-160"></a>

### 160. G.TYP.05 浮点型数据判断相等不要直接使用==，浮点型包装类型不要用equals()或者 flt.compareTo(another) == 0作相等的比较

| 字段 | 内容 |
|---|---|
| 问题级别 | 严重 |
| 语言 | JAVA |
| 标签 | fixbotengine-java |
| 关联工具规则 | BigDecimalEquals,FloatEquals |
| 规则类型 | 通用规范规则类 |
| 预估误报率 | 15% |

**审查要点**

```text
浮点型数据判断相等不要直接使用==，浮点型包装类型不要用equals()或者 flt.compareTo(another) == 0作相等的比较
2021年4月8日：新增两个浮点变量==比较的场景
工具检查场景:
- 检查浮点型数据使用==、!=来判断相等。
- 检查浮点型数据使用equals或compareTo来判断相等
```

**修复建议**

使用误差判等

**正确示例**

```text
考虑浮点数的精度问题，可在一定的误差范围内判定两个浮点数值相等。这个误差应根据实际需要进行定义。另外，对于符号不同的两个浮点数，即使在误差范围内也不应该判为相等。如下示例中，两个浮点数值误差在1e-6f内判为相等。
private static final float EPSILON = e-f;
float foo = ...;
float bar = ...;
if (Math.abs(foo - bar) < EPSILON) {
...
}
```

**错误示例**

```text
float f1 = 1.0f - 0.9f;
float f2 = 0.9f - 0.8f;
if (f1 == f2) {
// 预期进入此代码块，执行其他业务逻辑
// 但事实上 fl == f2 的结果为 false
}
Float flt1 = Float.valueOf(f1);
Float flt2 = Float.valueOf(f2);
if (flt1.equals(flt2)) {
// 预期进入此代码块，执行其他业务逻辑
// 但事实上 equals 的结果为 false
}
```

**参考信息**

- 链接：内部链接已省略
- 最新更新时间：2021-06-24 09:36:49

<a id="rule-161"></a>

### 161. G.TYP.06 禁止尝试与NaN进行比较运算，相等操作使用Double或Float的isNaN()方法

| 字段 | 内容 |
|---|---|
| 问题级别 | 严重 |
| 语言 | JAVA |
| 标签 | fixbotengine-java |
| 关联工具规则 | CompareNaN |
| 规则类型 | 通用规范规则类 |
| 预估误报率 | 15% |

**审查要点**

当任意一个操作数是NaN（Not a Number）时，数值比较符<、<=、>、>=会返回false，运算符==会返回false，运算符!=会返回true。因为无序的特性常常会导致意外结果，所以不能直接与NaN进行比较。

**修复建议**

使用isNaN

**正确示例**

```text
public class NanComparison {
public void doSomething(double num) {
// 如果num的值为0.0d，则Math.cos(infinity)返回NaN
double result = Math.cos(1 / num);
if (Double.isNaN(result)) {
System.out.println("result is NaN");
}
...
}
}
上述示例中，使用Double.isNaN()方法来检查result是否为NaN，可以获得正确的结果。
```

**错误示例**

```text
public class NanComparison {
public void doSomething(double num) {
// 如果num的值为0.0d，则Math.cos(infinity)返回NaN
double result = Math.cos(1 / num);
if (result == Double.NaN) { // 相等比较总是false
System.out.println("result is NaN");
}
...
}
}
上述示例中，与NaN进行直接比较。根据NaN的语义，代码中的比较运算返回false，不会输出“result is NaN”。
```

**参考信息**

- 链接：内部链接已省略
- 最新更新时间：2025-05-27 09:58:46

<a id="rule-162"></a>

### 162. G.TYP.08 字符串大小写转换、数字格式化为西方数字时，必须加上Locale.ROOT或Locale.ENGLISH

| 字段 | 内容 |
|---|---|
| 问题级别 | 一般 |
| 语言 | JAVA |
| 标签 | fixbotengine-java |
| 关联工具规则 | Locale |
| 规则类型 | 通用规范规则类 |
| 预估误报率 | 15% |

**审查要点**

```text
字符串大小写转换时要考虑地区语言上的差异。String类的toUpperCase()、toLowerCase()方法、format()方法，如果不指定输入参数，则会按当前系统默认的编码模式转换，可能会导致非预期的转换结果。
字符对区域不敏感的，例如协议关键字、HTML的tags等优先用ROOT，字符对区域敏感或者强调英文习惯的应使用ENGLISH。
如果确实需要在本地化GUI显示本地语言数字文字，也允许使用：
Locale.getDefault(Locale.Category.DISPLAY)
mystr.getBytes(StandardCharsets.UTF_8)
```

**修复建议**

增加编码参数

**正确示例**

```text
String testString = "i";
System.out.println(testString.toUpperCase(Locale.ROOT));
String testString2 = String.format(Locale.ROOT, "%d", 2);
System.out.println(testString2);
字符串的大小写转换一般都是对26个英文字母，建议显式指定语言为Locale.ROOT。
```

**错误示例**

```text
String testString = "i";
System.out.println(testString.toUpperCase());
String testString2 = String.format("%d", 2);
System.out.println(testString2); // locale设置为ar-SA，2格式化后输出'٢'
上述示例中，如果当前环境是土耳其Turkish/阿拉伯语/孟加拉语/尼泊尔语/马拉帝语/阿萨姆语等，那toUpperCase输出的结果将不是预期的大写I，可能是另外一个字符（?）；format格式化后的数字也不是预期的西方数字2。
```

**参考信息**

- 链接：内部链接已省略
- 最新更新时间：2021-06-24 09:36:50

<a id="rule-163"></a>

### 163. G.TYP.09 字符与字节的互相转换操作，要指明正确的编码方式

| 字段 | 内容 |
|---|---|
| 问题级别 | 一般 |
| 语言 | JAVA |
| 标签 | fixbotengine-java |
| 关联工具规则 | AssignCharset |
| 规则类型 | 通用规范规则类 |
| 预估误报率 | 15% |

**审查要点**

```text
Java虚拟机采用编码方式默认与操作系统的字符编码方式相同，String的编码方式、String.getBytes()默认采用Java虚拟机编码。当跨平台实现字符与字节之间的转换，可能会导致乱码。所以字符与字节之间转换时要明确指定编码方式。指定编码可以使用java.nio.charset包中的类编码解码字符集，更简便的写法可用String的getBytes(Charset)和带Charset参数的构造方法，它们已经通过StringCoding类对编码方式进行了封装。
本地化的自然语言文本（非ASCII）的比较、排序、查找，用java.text.Collator。
```

**修复建议**

增加编码参数

**正确示例**

```text
String data = "123ABC中国";
byte[] buf = data.getBytes(StandardCharsets.UTF_8);
```

**错误示例**

```text
String data = "123ABC中国";
byte[] buf = data.getBytes();
```

**参考信息**

- 链接：内部链接已省略
- 最新更新时间：2021-06-24 09:36:50

<a id="rule-164"></a>

### 164. G.TYP.11 基本类型优于包装类型，注意合理使用包装类型

| 字段 | 内容 |
|---|---|
| 问题级别 | 一般 |
| 语言 | JAVA |
| 标签 | fixbotengine-java |
| 关联工具规则 | WrapperClass |
| 规则类型 | 通用规范建议类 |
| 预估误报率 | 15% |

**审查要点**

```text
很多情况下基本类型优于包装类型
工具检查场景：
- 检查for循环变量是否为包装类型（Integer）
- 检查整数型包装类型变量是否用==比较
```

**修复建议**

使用基本类型

**正确示例**

```text
for(int i = 0; i < 10; ++i) {
...
}
```

**错误示例**

```text
for(Integer i = 0; i < 10; ++i) {
...
}
```

**参考信息**

- 链接：内部链接已省略
- 最新更新时间：2021-06-24 09:36:50

<a id="rule-165"></a>

### 165. GC_UNRELATED_TYPES

| 字段 | 内容 |
|---|---|
| 问题级别 | 一般 |
| 语言 | JAVA |
| 标签 | spotbugs |
| 关联工具规则 | GC_UNRELATED_TYPES |
| 规则类型 | 通用类（非编程规范） |
| 预估误报率 | 15% |

**审查要点**

```text
No relationship between generic parameter and method argument
This call to a generic collection method contains an argument with an incompatible class from that of the collection's parameter (i.e., the type of the argument is neither a supertype nor a subtype of the corresponding generic type argument). Therefore, it is unlikely that the collection contains any objects that are equal to the method argument used here. Most likely, the wrong value is being passed to the method.
In general, instances of two unrelated classes are not equal. For example, if the Foo and Bar classes are not related by subtyping, then an instance of Foo should not be equal to an instance of Bar. Among other issues, doing so will likely result in an equals method that is not symmetrical. For example, if you define the Foo class so that a Foo can be equal to a String, your equals method isn't symmetrical since a String can only be equal to a String.
In rare cases, people do define nonsymmetrical equals methods and still manage to make their code work. Although none of the APIs document or guarantee it, it is typically the case that if you check if a Collection<String> contains a Foo, the equals method of argument (e.g., the equals method of the Foo class) used to perform the equality checks.
```

**错误示例**

```text
Foo<A, B> foo;
foo.remove(a, Collections.emptySet()); //This generated a false positive because it thinks that the second parameter should be of type B, not Set
class Foo<X, Y> extends Bar<X, Set<Y>>{
...
}
class Bar<T, S> extends ConcurrentHashMap<T, S> {
@Override
public boolean remove(Object key, Object value) {
...
}
}
```

**参考信息**

- 链接：内部链接已省略
- 最新更新时间：2020-05-28 19:23:00

<a id="rule-166"></a>

### 166. HE_EQUALS_NO_HASHCODE

| 字段 | 内容 |
|---|---|
| 问题级别 | 一般 |
| 语言 | JAVA |
| 标签 | spotbugs |
| 关联工具规则 | HE_EQUALS_NO_HASHCODE |
| 规则类型 | 通用类（非编程规范） |
| 预估误报率 | 15% |

**审查要点**

```text
Class defines equals() but not hashCode()
This class overrides equals(Object), but does not override hashCode().  Therefore, the class may violate the invariant that equal objects must have equal hashcodes.
```

**参考信息**

- 最新更新时间：2020-05-28 19:21:30

<a id="rule-167"></a>

### 167. HE_EQUALS_USE_HASHCODE

| 字段 | 内容 |
|---|---|
| 问题级别 | 一般 |
| 语言 | JAVA |
| 标签 | spotbugs |
| 关联工具规则 | HE_EQUALS_USE_HASHCODE |
| 规则类型 | 通用类（非编程规范） |
| 预估误报率 | 15% |

**审查要点**

```text
Class defines equals() and uses Object.hashCode()
This class overrides equals(Object), but does not override hashCode(), and inherits the implementation of hashCode() from java.lang.Object (which returns the identity hash code, an arbitrary value assigned to the object by the VM).  Therefore, the class is very likely to violate the invariant that equal objects must have equal hashcodes.
If you don't think instances of this class will ever be inserted into a HashMap/HashTable,the recommended hashCode implementation to use is:
public int hashCode() { assert false : "hashCode not designed"; return 42; // any arbitrary constant will do}
```

**参考信息**

- 最新更新时间：2020-05-28 19:21:29

<a id="rule-168"></a>

### 168. HE_HASHCODE_NO_EQUALS

| 字段 | 内容 |
|---|---|
| 问题级别 | 一般 |
| 语言 | JAVA |
| 标签 | spotbugs |
| 关联工具规则 | HE_HASHCODE_NO_EQUALS |
| 规则类型 | 通用类（非编程规范） |
| 预估误报率 | 15% |

**审查要点**

```text
Class defines hashCode() but not equals()
This class defines a hashCode() method but not an equals() method.  Therefore, the class may violate the invariant that equal objects must have equal hashcodes.
```

**参考信息**

- 最新更新时间：2020-05-28 19:21:29

<a id="rule-169"></a>

### 169. HE_HASHCODE_USE_OBJECT_EQUALS

| 字段 | 内容 |
|---|---|
| 问题级别 | 一般 |
| 语言 | JAVA |
| 标签 | spotbugs |
| 关联工具规则 | HE_HASHCODE_USE_OBJECT_EQUALS |
| 规则类型 | 通用类（非编程规范） |
| 预估误报率 | 15% |

**审查要点**

```text
Class defines hashCode() and uses Object.equals()
This class defines a hashCode() method but inherits its equals() method from java.lang.Object (which defines equality by comparing object references).  Although this will probably satisfy the contract that equal objects must have equal hashcodes, it is probably not what was intended by overriding the hashCode() method.  (Overriding hashCode() implies that the object's identity is based on criteria more complicated than simple reference equality.)
If you don't think instances of this class will ever be inserted into a HashMap/HashTable,the recommended hashCode implementation to use is:
public int hashCode() { assert false : "hashCode not designed"; return 42; // any arbitrary constant will do}
```

**参考信息**

- 最新更新时间：2020-05-28 19:21:28

<a id="rule-170"></a>

### 170. HE_INHERITS_EQUALS_USE_HASHCODE

| 字段 | 内容 |
|---|---|
| 问题级别 | 一般 |
| 语言 | JAVA |
| 标签 | spotbugs |
| 关联工具规则 | HE_INHERITS_EQUALS_USE_HASHCODE |
| 规则类型 | 通用类（非编程规范） |
| 预估误报率 | 40% |

**审查要点**

```text
Class inherits equals() and uses Object.hashCode()
This class inherits equals(Object) from an abstract superclass, and hashCode() fromjava.lang.Object (which returns the identity hash code, an arbitrary value assigned to the object by the VM).  Therefore, the class is very likely to violate the invariant that equal objects must have equal hashcodes.
If you don't want to define a hashCode method, and/or don't believe the object will ever be put into a HashMap/Hashtable, define the hashCode() method to throw UnsupportedOperationException.
```

**参考信息**

- 最新更新时间：2020-05-28 19:21:30

<a id="rule-171"></a>

### 171. HE_USE_OF_UNHASHABLE_CLASS

| 字段 | 内容 |
|---|---|
| 问题级别 | 一般 |
| 语言 | JAVA |
| 标签 | spotbugs |
| 关联工具规则 | HE_USE_OF_UNHASHABLE_CLASS |
| 规则类型 | 通用类（非编程规范） |
| 预估误报率 | 15% |

**审查要点**

```text
Use of class without a hashCode() method in a hashed data structure
A class defines an equals(Object) method but not a hashCode() method,and thus doesn't fulfill the requirement that equal objects have equal hashCodes.An instance of this class is used in a hash data structure, making the need tofix this problem of highest importance.
```

**参考信息**

- 最新更新时间：2020-05-28 19:21:28

<a id="rule-172"></a>

### 172. HRS_REQUEST_PARAMETER_TO_COOKIE

| 字段 | 内容 |
|---|---|
| 问题级别 | 严重 |
| 语言 | JAVA |
| 标签 | spotbugs |
| 关联工具规则 | HRS_REQUEST_PARAMETER_TO_COOKIE |
| 规则类型 | 通用类（非编程规范） |
| 预估误报率 | 40% |

**审查要点**

```text
HTTP cookie formed from untrusted input
This code constructs an HTTP Cookie using an untrusted HTTP parameter. If this cookie is added to an HTTP response, it will allow a HTTP response splittingvulnerability. See http://en.wikipedia.org/wiki/HTTP_response_splittingfor more information.
SpotBugs looks only for the most blatant, obvious cases of HTTP response splitting.If SpotBugs found any, you almost certainly have morevulnerabilities that SpotBugs doesn't report. If you are concerned about HTTP response splitting, you should seriouslyconsider using a commercial static analysis or pen-testing tool.
```

**参考信息**

- 最新更新时间：2020-05-28 19:20:57

<a id="rule-173"></a>

### 173. HRS_REQUEST_PARAMETER_TO_HTTP_HEADER

| 字段 | 内容 |
|---|---|
| 问题级别 | 严重 |
| 语言 | JAVA |
| 标签 | spotbugs |
| 关联工具规则 | HRS_REQUEST_PARAMETER_TO_HTTP_HEADER |
| 规则类型 | 通用类（非编程规范） |
| 预估误报率 | 40% |

**审查要点**

```text
HTTP Response splitting vulnerability
This code directly writes an HTTP parameter to an HTTP header, which allows for a HTTP response splittingvulnerability. See http://en.wikipedia.org/wiki/HTTP_response_splittingfor more information.
SpotBugs looks only for the most blatant, obvious cases of HTTP response splitting.If SpotBugs found any, you almost certainly have morevulnerabilities that SpotBugs doesn't report. If you are concerned about HTTP response splitting, you should seriouslyconsider using a commercial static analysis or pen-testing tool.
```

**参考信息**

- 最新更新时间：2020-05-28 19:20:58

<a id="rule-174"></a>

### 174. HSC_HUGE_SHARED_STRING_CONSTANT

| 字段 | 内容 |
|---|---|
| 问题级别 | 一般 |
| 语言 | JAVA |
| 标签 | spotbugs |
| 关联工具规则 | HSC_HUGE_SHARED_STRING_CONSTANT |
| 规则类型 | 通用类（非编程规范） |
| 预估误报率 | 15% |

**审查要点**

```text
Huge string constants is duplicated across multiple class files
A large String constant is duplicated across multiple class files. This is likely because a final field is initialized to a String constant, and the Java language mandates that all references to a final field from other classes be inlined intothat classfile. See JDK bug 6447475 for a description of an occurrence of this bug in the JDK and how resolving it reduced the size of the JDK by 1 megabyte.
```

**参考信息**

- 最新更新时间：2020-05-28 19:21:26

<a id="rule-175"></a>

### 175. ICAST_BAD_SHIFT_AMOUNT

| 字段 | 内容 |
|---|---|
| 问题级别 | 一般 |
| 语言 | JAVA |
| 标签 | spotbugs |
| 关联工具规则 | ICAST_BAD_SHIFT_AMOUNT |
| 规则类型 | 通用类（非编程规范） |
| 预估误报率 | 15% |

**审查要点**

```text
32 bit int shifted by an amount not in the range -31..31
The code performs shift of a 32 bit int by a constant amount outsidethe range -31..31.The effect of this is to use the lower 5 bits of the integervalue to decide how much to shift by (e.g., shifting by 40 bits is the same as shifting by 8 bits,and shifting by 32 bits is the same as shifting by zero bits). This probably isn't what was expected,and it is at least confusing.
```

**参考信息**

- 最新更新时间：2020-05-28 19:22:53

<a id="rule-176"></a>

### 176. ICAST_INT_CAST_TO_DOUBLE_PASSED_TO_CEIL

| 字段 | 内容 |
|---|---|
| 问题级别 | 一般 |
| 语言 | JAVA |
| 标签 | spotbugs |
| 关联工具规则 | ICAST_INT_CAST_TO_DOUBLE_PASSED_TO_CEIL |
| 规则类型 | 通用类（非编程规范） |
| 预估误报率 | 15% |

**审查要点**

```text
Integral value cast to double and then passed to Math.ceil
This code converts an integral value (e.g., int or long)to a double precisionfloating point number and thenpassing the result to the Math.ceil() function, which rounds a double tothe next higher integer value. This operation should always be a no-op,since the converting an integer to a double should give a number with no fractional part.It is likely that the operation that generated the value to be passedto Math.ceil was intended to be performed using double precisionfloating point arithmetic.
```

**参考信息**

- 最新更新时间：2020-05-28 19:22:46

<a id="rule-177"></a>

### 177. ICAST_INT_CAST_TO_FLOAT_PASSED_TO_ROUND

| 字段 | 内容 |
|---|---|
| 问题级别 | 一般 |
| 语言 | JAVA |
| 标签 | spotbugs |
| 关联工具规则 | ICAST_INT_CAST_TO_FLOAT_PASSED_TO_ROUND |
| 规则类型 | 通用类（非编程规范） |
| 预估误报率 | 15% |

**审查要点**

```text
int value cast to float and then passed to Math.round
This code converts an int value to a float precisionfloating point number and thenpassing the result to the Math.round() function, which returns the int/long closestto the argument. This operation should always be a no-op,since the converting an integer to a float should give a number with no fractional part.It is likely that the operation that generated the value to be passedto Math.round was intended to be performed usingfloating point arithmetic.
```

**参考信息**

- 最新更新时间：2020-05-28 19:22:45

<a id="rule-178"></a>

### 178. IC_SUPERCLASS_USES_SUBCLASS_DURING_INITIALIZATION

| 字段 | 内容 |
|---|---|
| 问题级别 | 一般 |
| 语言 | JAVA |
| 标签 | spotbugs |
| 关联工具规则 | IC_SUPERCLASS_USES_SUBCLASS_DURING_INITIALIZATION |
| 规则类型 | 通用类（非编程规范） |
| 预估误报率 | 15% |

**审查要点**

```text
Superclass uses subclass during initialization
During the initialization of a class, the class makes an active use of a subclass.That subclass will not yet be initialized at the time of this use.For example, in the following code, foo will be null.
public class CircularClassInitialization { static class InnerClassSingleton extends CircularClassInitialization { static InnerClassSingleton singleton = new InnerClassSingleton(); } static CircularClassInitialization foo = InnerClassSingleton.singleton;}
```

**参考信息**

- 最新更新时间：2020-05-28 19:21:37

<a id="rule-179"></a>

### 179. IIO_INEFFICIENT_LAST_INDEX_OF

| 字段 | 内容 |
|---|---|
| 问题级别 | 一般 |
| 语言 | JAVA |
| 标签 | spotbugs |
| 关联工具规则 | IIO_INEFFICIENT_LAST_INDEX_OF |
| 规则类型 | 通用类（非编程规范） |
| 预估误报率 | 15% |

**审查要点**

```text
Inefficient use of String.lastIndexOf(String)
This code passes a constant string of length 1 to String.lastIndexOf().It is more efficient to use the integer implementations of String.lastIndexOf().f. e. call myString.lastIndexOf('.') instead of myString.lastIndexOf(".")
```

**参考信息**

- 最新更新时间：2020-05-28 19:22:33

<a id="rule-180"></a>

### 180. IM_MULTIPLYING_RESULT_OF_IREM

| 字段 | 内容 |
|---|---|
| 问题级别 | 一般 |
| 语言 | JAVA |
| 标签 | spotbugs |
| 关联工具规则 | IM_MULTIPLYING_RESULT_OF_IREM |
| 规则类型 | 通用类（非编程规范） |
| 预估误报率 | 10% |

**审查要点**

```text
Integer multiply of result of integer remainder
The code multiplies the result of an integer remaining by an integer constant.Be sure you don't have your operator precedence confused. For examplei % 60 * 1000 is (i % 60) * 1000, not i % (60 * 1000).
```

**参考信息**

- 最新更新时间：2025-06-16 15:16:17

<a id="rule-181"></a>

### 181. INT_BAD_COMPARISON_WITH_INT_VALUE

| 字段 | 内容 |
|---|---|
| 问题级别 | 一般 |
| 语言 | JAVA |
| 标签 | spotbugs |
| 关联工具规则 | INT_BAD_COMPARISON_WITH_INT_VALUE |
| 规则类型 | 通用类（非编程规范） |
| 预估误报率 | 15% |

**审查要点**

```text
Bad comparison of int value with long constant
This code compares an int value with a long constant that is outsidethe range of values that can be represented as an int value.This comparison is vacuous and possibly incorrect.
```

**参考信息**

- 最新更新时间：2020-05-28 19:22:25

<a id="rule-182"></a>

### 182. INT_BAD_COMPARISON_WITH_NONNEGATIVE_VALUE

| 字段 | 内容 |
|---|---|
| 问题级别 | 一般 |
| 语言 | JAVA |
| 标签 | spotbugs |
| 关联工具规则 | INT_BAD_COMPARISON_WITH_NONNEGATIVE_VALUE |
| 规则类型 | 通用类（非编程规范） |
| 预估误报率 | 15% |

**审查要点**

```text
Bad comparison of nonnegative value with negative constant or zero
This code compares a value that is guaranteed to be non-negative with a negative constant or zero.
```

**参考信息**

- 最新更新时间：2020-05-28 19:22:25

<a id="rule-183"></a>

### 183. INT_BAD_COMPARISON_WITH_SIGNED_BYTE

| 字段 | 内容 |
|---|---|
| 问题级别 | 一般 |
| 语言 | JAVA |
| 标签 | spotbugs |
| 关联工具规则 | INT_BAD_COMPARISON_WITH_SIGNED_BYTE |
| 规则类型 | 通用类（非编程规范） |
| 预估误报率 | 15% |

**审查要点**

```text
Bad comparison of signed byte
Signed bytes can only have a value in the range -128 to 127. Comparinga signed byte with a value outside that range is vacuous and likely to be incorrect.To convert a signed byte b to an unsigned value in the range 0..255,use 0xff & b.
```

**参考信息**

- 最新更新时间：2020-05-28 19:22:25

<a id="rule-184"></a>

### 184. IO_APPENDING_TO_OBJECT_OUTPUT_STREAM

| 字段 | 内容 |
|---|---|
| 问题级别 | 一般 |
| 语言 | JAVA |
| 标签 | spotbugs |
| 关联工具规则 | IO_APPENDING_TO_OBJECT_OUTPUT_STREAM |
| 规则类型 | 通用类（非编程规范） |
| 预估误报率 | 10% |

**审查要点**

```text
Doomed attempt to append to an object output stream
This code opens a file in append mode and then wraps the result in an object output stream. This won't allow you to append to an existing object output stream stored in a file. If you want to be able to append to an object output stream, you need to keep the object output stream open.
The only situation in which opening a file in append mode and the writing an object output stream could work is if on reading the file you plan to open it in random access mode and seek to the byte offset where the append started.
TODO: example.
```

**参考信息**

- 最新更新时间：2025-06-16 15:16:17

<a id="rule-185"></a>

### 185. IP_PARAMETER_IS_DEAD_BUT_OVERWRITTEN

| 字段 | 内容 |
|---|---|
| 问题级别 | 一般 |
| 语言 | JAVA |
| 标签 | spotbugs |
| 关联工具规则 | IP_PARAMETER_IS_DEAD_BUT_OVERWRITTEN |
| 规则类型 | 通用类（非编程规范） |
| 预估误报率 | 15% |

**审查要点**

```text
方法的输入参数在进入时已经失效但被覆盖
忽略了该参数的初始值，并在此处覆盖了该参数。这通常表明了一种错误的信念，即对参数的写入将传递回调用者。
```

**修复建议**

若函数的参数为不可变数据类型，则在函数内部使用时不要修改该变量值。

**正确示例**

```text
```java
public void foo(String p) {
String tmp = p; // [GOOD] 参数必须要使用，否则就不要定义。
// 其他业务
}
```
```

**错误示例**

```text
```java
public void foo(String p) {
p = "abc"; // [BAD] 对一个赋值不能改变其值，只是局部有效。
}
```
```

**参考信息**

- 链接：内部链接已省略
- 最新更新时间：2025-06-03 11:22:55

<a id="rule-186"></a>

### 186. IS2_INCONSISTENT_SYNC

| 字段 | 内容 |
|---|---|
| 问题级别 | 一般 |
| 语言 | JAVA |
| 标签 | spotbugs |
| 关联工具规则 | IS2_INCONSISTENT_SYNC |
| 规则类型 | 通用类（非编程规范） |
| 预估误报率 | 15% |

**审查要点**

```text
同步不一致
该类的字段在与同步相比似乎存在不一致的访问。这个错误报告表明了bug模式检测器判断：
* 该类包含了锁定和未锁定访问的混合，
* 该类未被标记为javax.annotation.concurrent.NotThreadSafe，
* 至少有一个由该类自己的方法执行的锁定访问，
* 未同步的字段访问次数（读和写）不超过所有访问次数的三分之一，写操作的权重是读操作的两倍。
这个bug模式的典型案例是忘记在一个预期为线程安全的类中同步一个方法。
你可以选择标记为“Unsynchronized access”的节点，以显示检测器认为字段在没有同步的情况下被访问的代码位置。
请注意，这个检测器存在各种不准确的来源；例如，检测器不能静态地检测所有持有锁的情况。此外，即使检测器在区分锁定和未锁定访问方面是准确的，所涉及的代码可能仍然是正确的。
```

**修复建议**

按照正确描述和正确示例中的描述修改，加锁一致性

**正确示例**

```text
```java
private static class GoodPractice{ // [GOOD] IS2_INCONSISTENT_SYNC
private int a = 0;
public synchronized void update(int a) {
this.a = a;
}
public synchronized void foo() {
a++;
}
}
```
```

**错误示例**

```text
```java
private static class BadPractice{ // [BAD] IS2_INCONSISTENT_SYNC
private int a = 0;
public synchronized void update(int a) {
this.a = a;
}
public void foo() {
a++;
}
}
```
```

**参考信息**

- 最新更新时间：2024-09-04 21:31:53

<a id="rule-187"></a>

### 187. ISC_INSTANTIATE_STATIC_CLASS

| 字段 | 内容 |
|---|---|
| 问题级别 | 一般 |
| 语言 | JAVA |
| 标签 | spotbugs |
| 关联工具规则 | ISC_INSTANTIATE_STATIC_CLASS |
| 规则类型 | 通用类（非编程规范） |
| 预估误报率 | 15% |

**审查要点**

```text
Needless instantiation of class that only supplies static methods
This class allocates an object that is based on a class that only supplies static methods. This objectdoes not need to be created, just access the static methods directly using the class name as a qualifier.
```

**参考信息**

- 最新更新时间：2020-05-28 19:22:42

<a id="rule-188"></a>

### 188. IS_INCONSISTENT_SYNC

| 字段 | 内容 |
|---|---|
| 问题级别 | 一般 |
| 语言 | JAVA |
| 标签 | spotbugs |
| 关联工具规则 | IS_INCONSISTENT_SYNC |
| 规则类型 | 通用类（非编程规范） |
| 预估误报率 | 15% |

**审查要点**

```text
Inconsistent synchronization
The fields of this class appear to be accessed inconsistently with respect to synchronization.  This bug report indicates that the bug pattern detector judged that
The class contains a mix of locked and unlocked accesses,
At least one locked access was performed by one of the class's own methods, and
The number of unsynchronized field accesses (reads and writes) was no more than one third of all accesses, with writes being weighed twice as high as reads
A typical bug matching this bug pattern is forgetting to synchronize one of the methods in a class that is intended to be thread-safe.
Note that there are various sources of inaccuracy in this detector; for example, the detector cannot statically detect all situations in which a lock is held.  Also, even when the detector is accurate in distinguishing locked vs. unlocked accesses, the code in question may still be correct.
```

**参考信息**

- 最新更新时间：2020-05-28 19:21:39

<a id="rule-189"></a>

### 189. IT_NO_SUCH_ELEMENT

| 字段 | 内容 |
|---|---|
| 问题级别 | 一般 |
| 语言 | JAVA |
| 标签 | spotbugs |
| 关联工具规则 | IT_NO_SUCH_ELEMENT |
| 规则类型 | 通用类（非编程规范） |
| 预估误报率 | 15% |

**审查要点**

```text
Iterator next() method can't throw NoSuchElementException
This class implements the java.util.Iterator interface.  However, its next() method is not capable of throwing java.util.NoSuchElementException.  The next() method should be changed so it throws NoSuchElementException if is called when there are no more elements to return.
```

**参考信息**

- 最新更新时间：2020-05-28 19:21:37

<a id="rule-190"></a>

### 190. JLM_JSR166_LOCK_MONITORENTER

| 字段 | 内容 |
|---|---|
| 问题级别 | 一般 |
| 语言 | JAVA |
| 标签 | spotbugs |
| 关联工具规则 | JLM_JSR166_LOCK_MONITORENTER |
| 规则类型 | 通用类（非编程规范） |
| 预估误报率 | 20% |

**审查要点**

```text
Synchronization performed on Lock
This method performs synchronization on an object that implementsjava.util.concurrent.locks.Lock. Such an object is locked/unlockedusingacquire()/release() ratherthan using the synchronized (...) construct.
```

**参考信息**

- 最新更新时间：2020-05-28 19:22:29

<a id="rule-191"></a>

### 191. JLM_JSR166_UTILCONCURRENT_MONITORENTER

| 字段 | 内容 |
|---|---|
| 问题级别 | 一般 |
| 语言 | JAVA |
| 标签 | spotbugs |
| 关联工具规则 | JLM_JSR166_UTILCONCURRENT_MONITORENTER |
| 规则类型 | 通用类（非编程规范） |
| 预估误报率 | 10% |

**审查要点**

```text
Synchronization performed on util.concurrent instance
This method performs synchronization on an object that is an instance ofa class from the java.util.concurrent package (or its subclasses). Instancesof these classes have their own concurrency control mechanisms that are orthogonal tothe synchronization provided by the Java keyword synchronized. For example,synchronizing on an AtomicBoolean will not prevent other threadsfrom modifying the AtomicBoolean.
Such code may be correct, but should be carefully reviewed and documented,and may confuse people who have to maintain the code at a later date.
```

**参考信息**

- 最新更新时间：2025-06-16 15:16:17

<a id="rule-192"></a>

### 192. LI_LAZY_INIT_STATIC

| 字段 | 内容 |
|---|---|
| 问题级别 | 一般 |
| 语言 | JAVA |
| 标签 | spotbugs |
| 关联工具规则 | LI_LAZY_INIT_STATIC |
| 规则类型 | 通用类（非编程规范） |
| 预估误报率 | 50% |

**审查要点**

```text
Incorrect lazy initialization of static field
This method contains an unsynchronized lazy initialization of a non-volatile static field.Because the compiler or processor may reorder instructions,threads are not guaranteed to see a completely initialized object,if the method can be called by multiple threads.You can make the field volatile to correct the problem.For more information, see theJava Memory Model web site.
```

**参考信息**

- 最新更新时间：2020-05-28 19:22:29

<a id="rule-193"></a>

### 193. LI_LAZY_INIT_UPDATE_STATIC

| 字段 | 内容 |
|---|---|
| 问题级别 | 一般 |
| 语言 | JAVA |
| 标签 | spotbugs |
| 关联工具规则 | LI_LAZY_INIT_UPDATE_STATIC |
| 规则类型 | 通用类（非编程规范） |
| 预估误报率 | 15% |

**审查要点**

```text
Incorrect lazy initialization and update of static field
This method contains an unsynchronized lazy initialization of a static field.After the field is set, the object stored into that location is further updated or accessed.The setting of the field is visible to other threads as soon as it is set. If thefurther accesses in the method that set the field serve to initialize the object, thenyou have a very serious multithreading bug, unless something else preventsany other thread from accessing the stored object until it is fully initialized.
Even if you feel confident that the method is never called by multiplethreads, it might be better to not set the static field until the valueyou are setting it to is fully populated/initialized.
```

**参考信息**

- 最新更新时间：2025-05-27 09:58:46

<a id="rule-194"></a>

### 194. ME_ENUM_FIELD_SETTER

| 字段 | 内容 |
|---|---|
| 问题级别 | 一般 |
| 语言 | JAVA |
| 标签 | spotbugs |
| 关联工具规则 | ME_ENUM_FIELD_SETTER |
| 规则类型 | 通用类（非编程规范） |
| 预估误报率 | 15% |

**审查要点**

```text
Public enum method unconditionally sets its field
This public method declared in public enum unconditionally sets enum field, thus this field can be changed by malicious code or by accident from another package. Though mutable enum fields may be used for lazy initialization, it's a bad practice to expose them to the outer world. Consider removing this method or declaring it package-private.
```

**参考信息**

- 最新更新时间：2020-05-28 19:21:43

<a id="rule-195"></a>

### 195. ME_MUTABLE_ENUM_FIELD

| 字段 | 内容 |
|---|---|
| 问题级别 | 一般 |
| 语言 | JAVA |
| 标签 | spotbugs |
| 关联工具规则 | ME_MUTABLE_ENUM_FIELD |
| 规则类型 | 通用类（非编程规范） |
| 预估误报率 | 15% |

**审查要点**

```text
Enum field is public and mutable
A mutable public field is defined inside a public enum, thus can be changed by malicious code or by accident from another package. Though mutable enum fields may be used for lazy initialization, it's a bad practice to expose them to the outer world. Consider declaring this field final and/or package-private.
```

**参考信息**

- 最新更新时间：2020-05-28 19:21:43

<a id="rule-196"></a>

### 196. MF_CLASS_MASKS_FIELD

| 字段 | 内容 |
|---|---|
| 问题级别 | 一般 |
| 语言 | JAVA |
| 标签 | spotbugs |
| 关联工具规则 | MF_CLASS_MASKS_FIELD |
| 规则类型 | 通用类（非编程规范） |
| 预估误报率 | 15% |

**审查要点**

```text
Class defines field that masks a superclass field
This class defines a field with the same name as a visibleinstance field in a superclass. This is confusing, andmay indicate an error if methods update or access one ofthe fields when they wanted the other.
```

**参考信息**

- 最新更新时间：2025-05-27 09:58:46

<a id="rule-197"></a>

### 197. ML_SYNC_ON_FIELD_TO_GUARD_CHANGING_THAT_FIELD

| 字段 | 内容 |
|---|---|
| 问题级别 | 一般 |
| 语言 | JAVA |
| 标签 | spotbugs |
| 关联工具规则 | ML_SYNC_ON_FIELD_TO_GUARD_CHANGING_THAT_FIELD |
| 规则类型 | 通用类（非编程规范） |
| 预估误报率 | 15% |

**审查要点**

```text
Synchronization on field in futile attempt to guard that field
This method synchronizes on a field in what appears to be an attemptto guard against simultaneous updates to that field. But guarding a fieldgets a lock on the referenced object, not on the field. This may notprovide the mutual exclusion you need, and other threads mightbe obtaining locks on the referenced objects (for other purposes). An exampleof this pattern would be:
private Long myNtfSeqNbrCounter = new Long(0);private Long getNotificationSequenceNumber() { Long result = null; synchronized(myNtfSeqNbrCounter) { result = new Long(myNtfSeqNbrCounter.longValue() + 1); myNtfSeqNbrCounter = new Long(result.longValue()); } return result;}
```

**参考信息**

- 最新更新时间：2020-05-28 19:21:39

<a id="rule-198"></a>

### 198. ML_SYNC_ON_UPDATED_FIELD

| 字段 | 内容 |
|---|---|
| 问题级别 | 一般 |
| 语言 | JAVA |
| 标签 | spotbugs |
| 关联工具规则 | ML_SYNC_ON_UPDATED_FIELD |
| 规则类型 | 通用类（非编程规范） |
| 预估误报率 | 15% |

**审查要点**

```text
Method synchronizes on an updated field
This method synchronizes on an object referenced from a mutable field. This is unlikely to have useful semantics, since differentthreads may be synchronizing on different objects.
```

**参考信息**

- 最新更新时间：2020-05-28 19:21:39

<a id="rule-199"></a>

### 199. MSF_MUTABLE_SERVLET_FIELD

| 字段 | 内容 |
|---|---|
| 问题级别 | 一般 |
| 语言 | JAVA |
| 标签 | spotbugs |
| 关联工具规则 | MSF_MUTABLE_SERVLET_FIELD |
| 规则类型 | 通用类（非编程规范） |
| 预估误报率 | 15% |

**审查要点**

```text
Mutable servlet field
A web server generally only creates one instance of servlet or JSP class (i.e., treatsthe class as a Singleton),and willhave multiple threads invoke methods on that instance to service multiplesimultaneous requests.Thus, having a mutable instance field generally creates race conditions.
```

**参考信息**

- 最新更新时间：2020-05-28 19:21:32

<a id="rule-200"></a>

### 200. MS_CANNOT_BE_FINAL

| 字段 | 内容 |
|---|---|
| 问题级别 | 一般 |
| 语言 | JAVA |
| 标签 | spotbugs |
| 关联工具规则 | MS_CANNOT_BE_FINAL |
| 规则类型 | 通用类（非编程规范） |
| 预估误报率 | 15% |

**审查要点**

```text
Field isn't final and can't be protected from malicious code
A mutable static field could be changed by malicious code or by accident from another package. Unfortunately, the way the field is used doesn't allow any easy fix to this problem.
```

**参考信息**

- 最新更新时间：2025-05-27 09:58:46

<a id="rule-201"></a>

### 201. MS_MUTABLE_ARRAY

| 字段 | 内容 |
|---|---|
| 问题级别 | 一般 |
| 语言 | JAVA |
| 标签 | spotbugs |
| 关联工具规则 | MS_MUTABLE_ARRAY |
| 规则类型 | 通用类（非编程规范） |
| 预估误报率 | 15% |

**审查要点**

字段是一个可变数组。一个final的静态字段引用了一个数组，并且可以被恶意代码或者意外代码从另一个包中访问。这段代码可以自由修改数组的内容。

**修复建议**

```text
把外部可访问的final字段类型变更为不可变类型。
建议使用Collections.unmodifiableList()方法将可变数组改为只读，或者将public修改为protected/private缩小可访问范围。
```

**正确示例**

```text
```JAVA
// 示例一：访问权限由public改为private，限制访问范围
private static final String[] SUPPORT_SSL_PROTOCOLS = new String[] {"TLSv1.2"};
// 示例二：访问权限由public改为protected，限制访问范围
protected static final String[] CRYPT_SUITES = new String[] {
"TLS_DHE_RSA_WITH_AES_128_GCM_SHA256", "TLS_DHE_RSA_WITH_AES_256_GCM_SHA384",
"TLS_DHE_DSS_WITH_AES_128_GCM_SHA256", "TLS_DHE_DSS_WITH_AES_256_GCM_SHA384"
};
// 示例三（推荐）：使用Collections.unmodifiableList()返回一个只读对象
import java.util.Collections;
public static final List<String> LOCATIONS_ARR = Collections.unmodifiableList(Arrays.asList("loc", "tableID"));
// 示例四：将数组转换为不可变list
// 原始数组
String[] array = {"Element1", "Element2", "Element3"};
// 将数组转换为List
List<String> list = Arrays.asList(array);
// 转换为不可变List
List<String> unmodifiableList = Collections.unmodifiableList(list);
```
```

**错误示例**

```text
```JAVA
// 示例一：对象为public的可变数据，可能会被意外代码从另一个包中修改
public static final String[] SUPPORT_SSL_PROTOCOLS = new String[] {"TLSv1.2"};
// 示例二：对象为public的可变数据，可能会被意外代码从另一个包中修改
public static final List<String> LOCATIONS_ARR = Arrays.asList("loc", "tableID");
```
```

**参考信息**

- 最新更新时间：2024-09-03 16:09:55

<a id="rule-202"></a>

### 202. MS_MUTABLE_COLLECTION

| 字段 | 内容 |
|---|---|
| 问题级别 | 一般 |
| 语言 | JAVA |
| 标签 | spotbugs |
| 关联工具规则 | MS_MUTABLE_COLLECTION |
| 规则类型 | 通用类（非编程规范） |
| 预估误报率 | 15% |

**审查要点**

```text
字段是一个可变集合
一个可变的集合实例被赋给一个final的静态字段，因此可以被恶意代码或者意外代码从另一个包中更改。考虑将这个字段包装成Collections.unmodifiableSet/List/Map等，以避免这个漏洞。
```

**修复建议**

使用Collections.unmodifiableSet/List/Map等，返回不可变视图

**正确示例**

```text
```java
// 使用私有的静态集合，并提供一个不可变的视图
private static final List<String> NAMES = new ArrayList(Arrays.asList("Alice", "Bob", "Charlie"));
public static List<String> getNames() {
return Collections.unmodifiableList(NAMES);
}
```
```

**错误示例**

```text
```java
private static final List<String> NAMES = new ArrayList(Arrays.asList("Alice", "Bob", "Charlie"));
public static List<String> getNames() {
return NAMES;
}
```
```

**参考信息**

- 最新更新时间：2020-05-28 19:21:42

<a id="rule-203"></a>

### 203. MS_MUTABLE_COLLECTION_PKGPROTECT

| 字段 | 内容 |
|---|---|
| 问题级别 | 一般 |
| 语言 | JAVA |
| 标签 | spotbugs |
| 关联工具规则 | MS_MUTABLE_COLLECTION_PKGPROTECT |
| 规则类型 | 通用类（非编程规范） |
| 预估误报率 | 15% |

**审查要点**

```text
字段是一个可变集合，需要进行包保护。
原因：一个可变的集合实例被赋给一个final的静态字段，因此可以被恶意代码或者意外代码从另一个包中更改。
方式1、该字段可以被设置为包保护，以避免这个漏洞。
方式2、或者将这个字段包装成Collections.unmodifiableSet/List/Map等，以避免这个漏洞。
```

**修复建议**

这条规则强调了正确使用访问控制来保护可变集合。将集合字段设置为包保护，或通过不可变视图公开集合，可以避免不必要的安全风险和意外错误。这是一种常见的封装和访问控制的最佳实践。

**正确示例**

```text
正确示例1：设置包保护
```java
static final List<String> ITEMS = new ArrayList<>();
```
正确示例2：包装成不可变集合
```java
public class Example {
private static final List<String> ITEMS = new ArrayList<>();
// 提供不可变视图
public static List<String> getItems() {
return Collections.unmodifiableList(ITEMS);
}
}
```
```

**错误示例**

```text
```java
public static final List<String> BAD_LIST = new ArrayList<>();
public static final Set<String> BAD_SET = new HashSet<>();
public static final Map<String, String> BAD_MAP = new HashMap<>();
```
```

**参考信息**

- 最新更新时间：2024-11-19 19:21:47

<a id="rule-204"></a>

### 204. MS_MUTABLE_HASHTABLE

| 字段 | 内容 |
|---|---|
| 问题级别 | 一般 |
| 语言 | JAVA |
| 标签 | spotbugs |
| 关联工具规则 | MS_MUTABLE_HASHTABLE |
| 规则类型 | 通用类（非编程规范） |
| 预估误报率 | 15% |

**审查要点**

```text
Field is a mutable Hashtable
A final static field references a Hashtable and can be accessed by malicious code or by accident from another package. This code can freely modify the contents of the Hashtable.
```

**参考信息**

- 最新更新时间：2025-05-27 09:58:46

<a id="rule-205"></a>

### 205. MS_OOI_PKGPROTECT

| 字段 | 内容 |
|---|---|
| 问题级别 | 一般 |
| 语言 | JAVA |
| 标签 | spotbugs |
| 关联工具规则 | MS_OOI_PKGPROTECT |
| 规则类型 | 通用类（非编程规范） |
| 预估误报率 | 15% |

**审查要点**

```text
Field should be moved out of an interface and made package protected
A final static field that isdefined in an interface references a mutable object such as an array or hashtable. This mutable object could be changed by malicious code or by accident from another package. To solve this, the field needs to be moved to a class and made package protected to avoid this vulnerability.
```

**参考信息**

- 最新更新时间：2020-05-28 19:21:40

<a id="rule-206"></a>

### 206. MS_SHOULD_BE_FINAL

| 字段 | 内容 |
|---|---|
| 问题级别 | 一般 |
| 语言 | JAVA |
| 标签 | spotbugs |
| 关联工具规则 | MS_SHOULD_BE_FINAL |
| 规则类型 | 通用类（非编程规范） |
| 预估误报率 | 15% |

**审查要点**

```text
Field isn't final but should be
This static field public but not final, andcould be changed by malicious code or by accident from another package. The field could be made final to avoid this vulnerability.
```

**参考信息**

- 最新更新时间：2020-05-28 19:21:41

<a id="rule-207"></a>

### 207. MS_SHOULD_BE_REFACTORED_TO_BE_FINAL

| 字段 | 内容 |
|---|---|
| 问题级别 | 一般 |
| 语言 | JAVA |
| 标签 | spotbugs |
| 关联工具规则 | MS_SHOULD_BE_REFACTORED_TO_BE_FINAL |
| 规则类型 | 通用类（非编程规范） |
| 预估误报率 | 10% |

**审查要点**

```text
Field isn't final but should be refactored to be so
This static field public but not final, andcould be changed by malicious code orby accident from another package.The field could be made final to avoidthis vulnerability. However, the static initializer contains more than one writeto the field, so doing so will require some refactoring.
```

**参考信息**

- 最新更新时间：2025-06-16 15:16:17

<a id="rule-208"></a>

### 208. NM_CLASS_NAMING_CONVENTION

| 字段 | 内容 |
|---|---|
| 问题级别 | 一般 |
| 语言 | JAVA |
| 标签 | spotbugs |
| 关联工具规则 | NM_CLASS_NAMING_CONVENTION |
| 规则类型 | 通用类（非编程规范） |
| 预估误报率 | 15% |

**审查要点**

```text
Class names should start with an upper case letter
Class names should be nouns, in mixed case with the first letter of each internal word capitalized. Try to keep your class names simple and descriptive. Use whole words-avoid acronyms and abbreviations (unless the abbreviation is much more widely used than the long form, such as URL or HTML).
```

**参考信息**

- 最新更新时间：2020-05-28 19:21:44

<a id="rule-209"></a>

### 209. NM_CLASS_NOT_EXCEPTION

| 字段 | 内容 |
|---|---|
| 问题级别 | 一般 |
| 语言 | JAVA |
| 标签 | spotbugs |
| 关联工具规则 | NM_CLASS_NOT_EXCEPTION |
| 规则类型 | 通用类（非编程规范） |
| 预估误报率 | 15% |

**审查要点**

```text
Class is not derived from an Exception, even though it is named as such
This class is not derived from another exception, but ends with 'Exception'. This willbe confusing to users of this class.
```

**参考信息**

- 最新更新时间：2020-05-28 19:21:48

<a id="rule-210"></a>

### 210. NM_FIELD_NAMING_CONVENTION

| 字段 | 内容 |
|---|---|
| 问题级别 | 一般 |
| 语言 | JAVA |
| 标签 | spotbugs |
| 关联工具规则 | NM_FIELD_NAMING_CONVENTION |
| 规则类型 | 通用类（非编程规范） |
| 预估误报率 | 15% |

**审查要点**

```text
Field names should start with a lower case letter
Names of fields that are not final should be in mixed case with a lowercase first letter and the first letters of subsequent words capitalized.
```

**参考信息**

- 最新更新时间：2020-05-28 19:21:00

<a id="rule-211"></a>

### 211. NM_LCASE_HASHCODE

| 字段 | 内容 |
|---|---|
| 问题级别 | 一般 |
| 语言 | JAVA |
| 标签 | spotbugs |
| 关联工具规则 | NM_LCASE_HASHCODE |
| 规则类型 | 通用类（非编程规范） |
| 预估误报率 | 15% |

**审查要点**

```text
Class defines hashcode(); should it be hashCode()?
This class defines a method called hashcode().  This method does not override the hashCode() method in java.lang.Object, which is probably what was intended.
```

**参考信息**

- 最新更新时间：2020-05-28 19:21:47

<a id="rule-212"></a>

### 212. NM_LCASE_TOSTRING

| 字段 | 内容 |
|---|---|
| 问题级别 | 一般 |
| 语言 | JAVA |
| 标签 | spotbugs |
| 关联工具规则 | NM_LCASE_TOSTRING |
| 规则类型 | 通用类（非编程规范） |
| 预估误报率 | 15% |

**审查要点**

```text
Class defines tostring(); should it be toString()?
This class defines a method called tostring().  This method does not override the toString() method in java.lang.Object, which is probably what was intended.
```

**参考信息**

- 最新更新时间：2020-05-28 19:21:47

<a id="rule-213"></a>

### 213. NM_METHOD_CONSTRUCTOR_CONFUSION

| 字段 | 内容 |
|---|---|
| 问题级别 | 一般 |
| 语言 | JAVA |
| 标签 | spotbugs |
| 关联工具规则 | NM_METHOD_CONSTRUCTOR_CONFUSION |
| 规则类型 | 通用类（非编程规范） |
| 预估误报率 | 10% |

**审查要点**

```text
Apparent method/constructor confusion
This regular method has the same name as the class it is defined in. It is likely that this was intended to be a constructor. If it was intended to be a constructor, remove the declaration of a void return value. If you had accidentally defined this method, realized the mistake, defined a proper constructor but can't get rid of this method due to backwards compatibility, deprecate the method.
```

**参考信息**

- 最新更新时间：2020-05-28 19:21:47

<a id="rule-214"></a>

### 214. NM_SAME_SIMPLE_NAME_AS_INTERFACE

| 字段 | 内容 |
|---|---|
| 问题级别 | 一般 |
| 语言 | JAVA |
| 标签 | spotbugs |
| 关联工具规则 | NM_SAME_SIMPLE_NAME_AS_INTERFACE |
| 规则类型 | 通用类（非编程规范） |
| 预估误报率 | 15% |

**审查要点**

```text
Class names shouldn't shadow simple name of implemented interface
This class/interface has a simple name that is identical to that of an implemented/extended interface, exceptthat the interface is in a different package (e.g., alpha.Foo extends beta.Foo).This can be exceptionally confusing, create lots of situations in which you have to look at import statementsto resolve references and creates manyopportunities to accidentally define methods that do not override methods in their superclasses.
```

**参考信息**

- 最新更新时间：2020-05-28 19:21:44

<a id="rule-215"></a>

### 215. NM_SAME_SIMPLE_NAME_AS_SUPERCLASS

| 字段 | 内容 |
|---|---|
| 问题级别 | 一般 |
| 语言 | JAVA |
| 标签 | spotbugs |
| 关联工具规则 | NM_SAME_SIMPLE_NAME_AS_SUPERCLASS |
| 规则类型 | 通用类（非编程规范） |
| 预估误报率 | 15% |

**审查要点**

```text
Class names shouldn't shadow simple name of superclass
This class has a simple name that is identical to that of its superclass, exceptthat its superclass is in a different package (e.g., alpha.Foo extends beta.Foo).This can be exceptionally confusing, create lots of situations in which you have to look at import statementsto resolve references and creates manyopportunities to accidentally define methods that do not override methods in their superclasses.
```

**参考信息**

- 最新更新时间：2020-05-28 19:21:44

<a id="rule-216"></a>

### 216. NO_NOTIFY_NOT_NOTIFYALL

| 字段 | 内容 |
|---|---|
| 问题级别 | 一般 |
| 语言 | JAVA |
| 标签 | spotbugs |
| 关联工具规则 | NO_NOTIFY_NOT_NOTIFYALL |
| 规则类型 | 通用类（非编程规范） |
| 预估误报率 | 15% |

**审查要点**

```text
Using notify() rather than notifyAll()
This method calls notify() rather than notifyAll().  Java monitors are often used for multiple conditions.  Calling notify() only wakes up one thread, meaning that the thread woken up might not be the one waiting for the condition that the caller just satisfied.
```

**参考信息**

- 最新更新时间：2020-05-28 19:22:02

<a id="rule-217"></a>

### 217. NP_ALWAYS_NULL

| 字段 | 内容 |
|---|---|
| 问题级别 | 一般 |
| 语言 | JAVA |
| 标签 | spotbugs |
| 关联工具规则 | NP_ALWAYS_NULL |
| 规则类型 | 通用类（非编程规范） |
| 预估误报率 | 15% |

**审查要点**

```text
Null pointer dereference
A null pointer is dereferenced here.  This will lead to aNullPointerException when the code is executed.
```

**参考信息**

- 最新更新时间：2020-05-28 19:22:07

<a id="rule-218"></a>

### 218. NP_ALWAYS_NULL_EXCEPTION

| 字段 | 内容 |
|---|---|
| 问题级别 | 一般 |
| 语言 | JAVA |
| 标签 | spotbugs |
| 关联工具规则 | NP_ALWAYS_NULL_EXCEPTION |
| 规则类型 | 通用类（非编程规范） |
| 预估误报率 | 10% |

**审查要点**

```text
Null pointer dereference in method on exception path
A pointer which is null on an exception path is dereferenced here. This will lead to a NullPointerException when the code is executed. Note that because SpotBugs currently does not prune infeasible exception paths,this may be a false warning.
Also note that SpotBugs considers the default case of a switch statement tobe an exception path, since the default case is often infeasible.
```

**参考信息**

- 最新更新时间：2025-06-16 15:16:17

<a id="rule-219"></a>

### 219. NP_CLONE_COULD_RETURN_NULL

| 字段 | 内容 |
|---|---|
| 问题级别 | 一般 |
| 语言 | JAVA |
| 标签 | spotbugs |
| 关联工具规则 | NP_CLONE_COULD_RETURN_NULL |
| 规则类型 | 通用类（非编程规范） |
| 预估误报率 | 15% |

**审查要点**

```text
Clone method may return null
This clone method seems to return null in some circumstances, but clone is never allowed to return a null value. If you are convinced this path is unreachable, throw an AssertionError instead.
```

**参考信息**

- 最新更新时间：2025-05-27 09:58:46

<a id="rule-220"></a>

### 220. NP_CLOSING_NULL

| 字段 | 内容 |
|---|---|
| 问题级别 | 一般 |
| 语言 | JAVA |
| 标签 | spotbugs |
| 关联工具规则 | NP_CLOSING_NULL |
| 规则类型 | 通用类（非编程规范） |
| 预估误报率 | 15% |

**审查要点**

```text
close() invoked on a value that is always null
close() is being invoked on a value that is always null. If this statement is executed,a null pointer exception will occur. But the big risk here you never closesomething that should be closed.
```

**参考信息**

- 最新更新时间：2020-05-28 19:22:08

<a id="rule-221"></a>

### 221. NP_GUARANTEED_DEREF

| 字段 | 内容 |
|---|---|
| 问题级别 | 一般 |
| 语言 | JAVA |
| 标签 | spotbugs |
| 关联工具规则 | NP_GUARANTEED_DEREF |
| 规则类型 | 通用类（非编程规范） |
| 预估误报率 | 15% |

**审查要点**

```text
Null value is guaranteed to be dereferenced
There is a statement or branch that if executed guarantees that a value is null at this point, and that value that is guaranteed to be dereferenced (except on forward paths involving runtime exceptions).
Note that a check such as if (x == null) throw new NullPointerException(); is treated as a dereference of x.
```

**修复建议**

```text
据说改为throw new RuntimeException就会消除警告
去掉throw new NullPointerException也可以
```

**错误示例**

```text
public void test() {
String var = "";
int index = 2;
if (index == -1) {
var = String.class.getName();
if (var.length() == 0) {
var = null;
}
} else {
var = Integer.class.getName();
if (var.length() == 0) {
var = null;
}
}
if (var == null) {// FINBUGS reports on this line NP_GUARANTEED_DEREF
/*
* There is a statement or branch that if executed guarantees that a value
* is null at this point, and that value that is guaranteed to be
* dereferenced (except on forward paths involving runtime exceptions).
*/
throw new NullPointerException("NULL");
}
}
```

**参考信息**

- 链接：内部链接已省略
- 最新更新时间：2020-05-28 19:22:12

<a id="rule-222"></a>

### 222. NP_GUARANTEED_DEREF_ON_EXCEPTION_PATH

| 字段 | 内容 |
|---|---|
| 问题级别 | 一般 |
| 语言 | JAVA |
| 标签 | spotbugs |
| 关联工具规则 | NP_GUARANTEED_DEREF_ON_EXCEPTION_PATH |
| 规则类型 | 通用类（非编程规范） |
| 预估误报率 | 15% |

**审查要点**

```text
Value is null and guaranteed to be dereferenced on exception path
There is a statement or branch on an exception path that if executed guarantees that a value is null at this point, and that value that is guaranteed to be dereferenced (except on forward paths involving runtime exceptions).
```

**参考信息**

- 最新更新时间：2020-05-28 19:22:12

<a id="rule-223"></a>

### 223. NP_NONNULL_RETURN_VIOLATION

| 字段 | 内容 |
|---|---|
| 问题级别 | 一般 |
| 语言 | JAVA |
| 标签 | spotbugs |
| 关联工具规则 | NP_NONNULL_RETURN_VIOLATION |
| 规则类型 | 通用类（非编程规范） |
| 预估误报率 | 15% |

**审查要点**

```text
Method may return null, but is declared @Nonnull
This method may return a null value, but the method (or a superclass method which it overrides) is declared to return @Nonnull.
```

**错误示例**

```text
void test(){
String ss = null;
sya(ss);
}
public void sya(String ad){
ad.getBytes();
}
```

**参考信息**

- 链接：内部链接已省略
- 最新更新时间：2020-05-28 19:22:11

<a id="rule-224"></a>

### 224. NP_NULL_ON_SOME_PATH_EXCEPTION

| 字段 | 内容 |
|---|---|
| 问题级别 | 一般 |
| 语言 | JAVA |
| 标签 | spotbugs |
| 关联工具规则 | NP_NULL_ON_SOME_PATH_EXCEPTION |
| 规则类型 | 通用类（非编程规范） |
| 预估误报率 | 20% |

**审查要点**

```text
Possible null pointer dereference in method on exception path
A reference value which is null on some exception control path isdereferenced here.  This may lead to a NullPointerExceptionwhen the code is executed. Note that because SpotBugs currently does not prune infeasible exception paths,this may be a false warning.
Also note that SpotBugs considers the default case of a switch statement tobe an exception path, since the default case is often infeasible.
```

**修复建议**

根据代码逻辑实际情况采取解决方法， 在异常情况下考虑好分支路径

**错误示例**

```text
private void test_NP_NULL_ON_SOME_PATH_EXCEPTION(String name, String pass){
User user = null;
int nport = 10;
try{
user = checkUser(name, pass);
if (user == null){
System.out.println("密码错误");
close();
return ;
}
}
catch (SQLException e){
e.printStackTrace();
}
user.setPort(nport); //这端代码有NP_NULL_ON_SOME_PATH_EXCEPTION
}
```

**参考信息**

- 链接：内部链接已省略
- 最新更新时间：2020-05-28 19:22:10

<a id="rule-225"></a>

### 225. NP_NULL_PARAM_DEREF

| 字段 | 内容 |
|---|---|
| 问题级别 | 一般 |
| 语言 | JAVA |
| 标签 | spotbugs |
| 关联工具规则 | NP_NULL_PARAM_DEREF |
| 规则类型 | 通用类（非编程规范） |
| 预估误报率 | 20% |

**审查要点**

```text
Method call passes null for non-null parameter
This method call passes a null value for a non-null method parameter. Either the parameter is annotated as a parameter that should always be non-null, or analysis has shown that it will always be dereferenced.
```

**修复建议**

```text
我们可以赋予函数参数默认值。
所谓默认值就是在调用时，可以不写某些参数的值，编译器会自动把默认值传递给调用语句中。默认值一般在函数声明中设置；
```

**错误示例**

```text
int FunctionOne（int x，int y=0，int z=0，int w=0）；
我们要给z传递整型值8，作如下调用：
FunctionOne（8）；
显然，编译器无法确定这个8到底要传递给哪个参数。为了达到我们的目的，必须这样调用：
FunctionOne（0，0，8）；
这是x被传递了0，y被传递了 0，z被传递了8
```

**参考信息**

- 链接：内部链接已省略
- 最新更新时间：2020-05-28 19:22:11

<a id="rule-226"></a>

### 226. NP_NULL_PARAM_DEREF_ALL_TARGETS_DANGEROUS

| 字段 | 内容 |
|---|---|
| 问题级别 | 一般 |
| 语言 | JAVA |
| 标签 | spotbugs |
| 关联工具规则 | NP_NULL_PARAM_DEREF_ALL_TARGETS_DANGEROUS |
| 规则类型 | 通用类（非编程规范） |
| 预估误报率 | 15% |

**审查要点**

```text
Method call passes null for non-null parameter
A possibly-null value is passed at a call site where all known target methods require the parameter to be non-null. Either the parameter is annotated as a parameter that should always be non-null, or analysis has shown that it will always be dereferenced.
```

**参考信息**

- 最新更新时间：2020-05-28 19:22:10

<a id="rule-227"></a>

### 227. NP_NULL_PARAM_DEREF_NONVIRTUAL

| 字段 | 内容 |
|---|---|
| 问题级别 | 一般 |
| 语言 | JAVA |
| 标签 | spotbugs |
| 关联工具规则 | NP_NULL_PARAM_DEREF_NONVIRTUAL |
| 规则类型 | 通用类（非编程规范） |
| 预估误报率 | 20% |

**审查要点**

```text
Non-virtual method call passes null for non-null parameter
A possibly-null value is passed to a non-null method parameter. Either the parameter is annotated as a parameter that should always be non-null, or analysis has shown that it will always be dereferenced.
```

**参考信息**

- 最新更新时间：2020-05-28 19:22:10

<a id="rule-228"></a>

### 228. NP_TOSTRING_COULD_RETURN_NULL

| 字段 | 内容 |
|---|---|
| 问题级别 | 一般 |
| 语言 | JAVA |
| 标签 | spotbugs |
| 关联工具规则 | NP_TOSTRING_COULD_RETURN_NULL |
| 规则类型 | 通用类（非编程规范） |
| 预估误报率 | 15% |

**审查要点**

```text
toString method may return null
This toString method seems to return null in some circumstances. A liberal reading of the spec could be interpreted as allowing this, but it is probably a bad idea and could cause other code to break. Return the empty string or some other appropriate string rather than null.
```

**参考信息**

- 最新更新时间：2020-05-28 19:22:12

<a id="rule-229"></a>

### 229. NP_UNWRITTEN_FIELD

| 字段 | 内容 |
|---|---|
| 问题级别 | 一般 |
| 语言 | JAVA |
| 标签 | spotbugs |
| 关联工具规则 | NP_UNWRITTEN_FIELD |
| 规则类型 | 通用类（非编程规范） |
| 预估误报率 | 15% |

**审查要点**

```text
Read of unwritten field
The program is dereferencing a field that does not seem to ever have a non-null value written to it.Unless the field is initialized via some mechanism not seen by the analysis,dereferencing this value will generate a null pointer exception.
```

**修复建议**

```text
引用前进行判空
privatestatic ArrayList<String>paramList =null;
publicArrayList checkValue(String str1) {
if (null!=paramList) {
paramList.clear();
}
returnparamList;
}
```

**错误示例**

```text
privatestaticArrayList<String> paramList =null;
publicArrayList checkValue(String str1) {
paramList.clear();
returnparamList;
}
//调用clear()只是清除list内容并不会使list报空指针异常，但是list为null时，就会报空指针异常了。
```

**参考信息**

- 链接：内部链接已省略
- 最新更新时间：2020-05-28 19:21:59

<a id="rule-230"></a>

### 230. OBL_UNSATISFIED_OBLIGATION

| 字段 | 内容 |
|---|---|
| 问题级别 | 一般 |
| 语言 | JAVA |
| 标签 | spotbugs |
| 关联工具规则 | OBL_UNSATISFIED_OBLIGATION |
| 规则类型 | 通用类（非编程规范） |
| 预估误报率 | 10% |

**审查要点**

```text
Method may fail to clean up stream or resource
This method may fail to clean up (close, dispose of) a stream, database object, or other resource requiring an explicit cleanup operation.
In general, if a method opens a stream or other resource, the method should use a try/finally block to ensure that the stream or resource is cleaned up before the method returns.
This bug pattern is essentially the same as the OS_OPEN_STREAM and ODR_OPEN_DATABASE_RESOURCE bug patterns, but is based on a different (and hopefully better) static analysis technique. We are interested is getting feedback about the usefulness of this bug pattern. For sending feedback, check:
contributing guideline
malinglist
In particular, the false-positive suppression heuristics for this bug pattern have not been extensively tuned, so reports about false positives are helpful to us.
See Weimer and Necula, Finding and Preventing Run-Time Error Handling Mistakes, for a description of the analysis technique.
```

**参考信息**

- 最新更新时间：2025-06-16 15:16:17

<a id="rule-231"></a>

### 231. OBL_UNSATISFIED_OBLIGATION_EXCEPTION_EDGE

| 字段 | 内容 |
|---|---|
| 问题级别 | 一般 |
| 语言 | JAVA |
| 标签 | spotbugs |
| 关联工具规则 | OBL_UNSATISFIED_OBLIGATION_EXCEPTION_EDGE |
| 规则类型 | 通用类（非编程规范） |
| 预估误报率 | 10% |

**审查要点**

```text
Method may fail to clean up stream or resource on checked exception
This method may fail to clean up (close, dispose of) a stream, database object, or other resource requiring an explicit cleanup operation.
In general, if a method opens a stream or other resource, the method should use a try/finally block to ensure that the stream or resource is cleaned up before the method returns.
This bug pattern is essentially the same as the OS_OPEN_STREAM and ODR_OPEN_DATABASE_RESOURCE bug patterns, but is based on a different (and hopefully better) static analysis technique. We are interested is getting feedback about the usefulness of this bug pattern. For sending feedback, check:
contributing guideline
malinglist
In particular, the false-positive suppression heuristics for this bug pattern have not been extensively tuned, so reports about false positives are helpful to us.
See Weimer and Necula, Finding and Preventing Run-Time Error Handling Mistakes, for a description of the analysis technique.
```

**参考信息**

- 最新更新时间：2025-06-16 15:16:17

<a id="rule-232"></a>

### 232. ODR_OPEN_DATABASE_RESOURCE

| 字段 | 内容 |
|---|---|
| 问题级别 | 一般 |
| 语言 | JAVA |
| 标签 | spotbugs |
| 关联工具规则 | ODR_OPEN_DATABASE_RESOURCE |
| 规则类型 | 通用类（非编程规范） |
| 预估误报率 | 20% |

**审查要点**

```text
Method may fail to close database resource
The method creates a database resource (such as a database connectionor row set), does not assign it to anyfields, pass it to other methods, or return it, and does not appear to closethe object on all paths out of the method.  Failure toclose database resources on all paths out of a method mayresult in poor performance, and could cause the application tohave problems communicating with the database.
```

**参考信息**

- 最新更新时间：2025-05-27 09:58:46

<a id="rule-233"></a>

### 233. ODR_OPEN_DATABASE_RESOURCE_EXCEPTION_PATH

| 字段 | 内容 |
|---|---|
| 问题级别 | 一般 |
| 语言 | JAVA |
| 标签 | spotbugs |
| 关联工具规则 | ODR_OPEN_DATABASE_RESOURCE_EXCEPTION_PATH |
| 规则类型 | 通用类（非编程规范） |
| 预估误报率 | 20% |

**审查要点**

```text
Method may fail to close database resource on exception
The method creates a database resource (such as a database connectionor row set), does not assign it to anyfields, pass it to other methods, or return it, and does not appear to closethe object on all exception paths out of the method.  Failure toclose database resources on all paths out of a method mayresult in poor performance, and could cause the application tohave problems communicating with the database.
```

**参考信息**

- 最新更新时间：2025-05-27 09:58:46

<a id="rule-234"></a>

### 234. OS_OPEN_STREAM

| 字段 | 内容 |
|---|---|
| 问题级别 | 一般 |
| 语言 | JAVA |
| 标签 | spotbugs |
| 关联工具规则 | OS_OPEN_STREAM |
| 规则类型 | 通用类（非编程规范） |
| 预估误报率 | 20% |

**审查要点**

```text
Method may fail to close stream
The method creates an IO stream object, does not assign it to anyfields, pass it to other methods that might close it,or return it, and does not appear to closethe stream on all paths out of the method.  This may result ina file descriptor leak.  It is generally a goodidea to use a finally block to ensure that streams areclosed.
```

**参考信息**

- 最新更新时间：2020-05-28 19:22:13

<a id="rule-235"></a>

### 235. OS_OPEN_STREAM_EXCEPTION_PATH

| 字段 | 内容 |
|---|---|
| 问题级别 | 一般 |
| 语言 | JAVA |
| 标签 | spotbugs |
| 关联工具规则 | OS_OPEN_STREAM_EXCEPTION_PATH |
| 规则类型 | 通用类（非编程规范） |
| 预估误报率 | 20% |

**审查要点**

```text
Method may fail to close stream on exception
The method creates an IO stream object, does not assign it to anyfields, pass it to other methods, or return it, and does not appear to closeit on all possible exception paths out of the method. This may result in a file descriptor leak.  It is generally a goodidea to use a finally block to ensure that streams areclosed.
```

**参考信息**

- 最新更新时间：2020-05-28 19:22:13

<a id="rule-236"></a>

### 236. PT_RELATIVE_PATH_TRAVERSAL

| 字段 | 内容 |
|---|---|
| 问题级别 | 严重 |
| 语言 | JAVA |
| 标签 | spotbugs |
| 关联工具规则 | PT_RELATIVE_PATH_TRAVERSAL |
| 规则类型 | 通用类（非编程规范） |
| 预估误报率 | 15% |

**审查要点**

```text
Relative path traversal in servlet
The software uses an HTTP request parameter to construct a pathname that should be within a restricted directory, but it does not properly neutralize sequences such as ".." that can resolve to a location that is outside of that directory.See http://cwe.mitre.org/data/definitions/23.htmlfor more information.
SpotBugs looks only for the most blatant, obvious cases of relative path traversal.If SpotBugs found any, you almost certainly have morevulnerabilities that SpotBugs doesn't report. If you are concerned about relative path traversal, you should seriouslyconsider using a commercial static analysis or pen-testing tool.
```

**参考信息**

- 最新更新时间：2020-05-28 19:20:58

<a id="rule-237"></a>

### 237. PZ_DONT_REUSE_ENTRY_OBJECTS_IN_ITERATORS

| 字段 | 内容 |
|---|---|
| 问题级别 | 一般 |
| 语言 | JAVA |
| 标签 | spotbugs |
| 关联工具规则 | PZ_DONT_REUSE_ENTRY_OBJECTS_IN_ITERATORS |
| 规则类型 | 通用类（非编程规范） |
| 预估误报率 | 10% |

**审查要点**

```text
Don't reuse entry objects in iterators
The entrySet() method is allowed to return a view of the underlying Map in which an Iterator and Map.Entry. This clever idea was used in several Map implementations, but introduces the possibility of nasty coding mistakes. If a map m returns such an iterator for an entrySet, then c.addAll(m.entrySet()) will go badly wrong. All of the Map implementations in OpenJDK 1.7 have been rewritten to avoid this, you should to.
```

**参考信息**

- 最新更新时间：2025-06-16 15:16:17

<a id="rule-238"></a>

### 238. QBA_QUESTIONABLE_BOOLEAN_ASSIGNMENT

| 字段 | 内容 |
|---|---|
| 问题级别 | 一般 |
| 语言 | JAVA |
| 标签 | spotbugs |
| 关联工具规则 | QBA_QUESTIONABLE_BOOLEAN_ASSIGNMENT |
| 规则类型 | 通用类（非编程规范） |
| 预估误报率 | 10% |

**审查要点**

```text
Method assigns boolean literal in boolean expression
This method assigns a literal boolean value (true or false) to a boolean variable inside an if or while expression. Most probably this was supposed to be a boolean comparison using ==, not an assignment using =.
```

**参考信息**

- 最新更新时间：2020-05-28 19:22:59

<a id="rule-239"></a>

### 239. RANGE_ARRAY_INDEX

| 字段 | 内容 |
|---|---|
| 问题级别 | 一般 |
| 语言 | JAVA |
| 标签 | spotbugs |
| 关联工具规则 | RANGE_ARRAY_INDEX |
| 规则类型 | 通用类（非编程规范） |
| 预估误报率 | 15% |

**审查要点**

```text
Array index is out of bounds
Array operation is performed, but array index is out of bounds, which will result in ArrayIndexOutOfBoundsException at runtime.
```

**参考信息**

- 最新更新时间：2020-05-28 19:22:04

<a id="rule-240"></a>

### 240. RANGE_ARRAY_OFFSET

| 字段 | 内容 |
|---|---|
| 问题级别 | 一般 |
| 语言 | JAVA |
| 标签 | spotbugs |
| 关联工具规则 | RANGE_ARRAY_OFFSET |
| 规则类型 | 通用类（非编程规范） |
| 预估误报率 | 15% |

**审查要点**

```text
Array offset is out of bounds
Method is called with array parameter and offset parameter, but the offset is out of bounds. This will result in IndexOutOfBoundsException at runtime.
```

**参考信息**

- 最新更新时间：2020-05-28 19:22:04

<a id="rule-241"></a>

### 241. RANGE_STRING_INDEX

| 字段 | 内容 |
|---|---|
| 问题级别 | 一般 |
| 语言 | JAVA |
| 标签 | spotbugs |
| 关联工具规则 | RANGE_STRING_INDEX |
| 规则类型 | 通用类（非编程规范） |
| 预估误报率 | 15% |

**审查要点**

```text
String index is out of bounds
String method is called and specified string index is out of bounds. This will result in StringIndexOutOfBoundsException at runtime.
```

**参考信息**

- 最新更新时间：2020-05-28 19:22:05

<a id="rule-242"></a>

### 242. RC_REF_COMPARISON

| 字段 | 内容 |
|---|---|
| 问题级别 | 一般 |
| 语言 | JAVA |
| 标签 | spotbugs |
| 关联工具规则 | RC_REF_COMPARISON |
| 规则类型 | 通用类（非编程规范） |
| 预估误报率 | 5% |

**审查要点**

```text
Suspicious reference comparison
This method compares two reference values using the == or != operator,where the correct way to compare instances of this type is generallywith the equals() method.It is possible to create distinct instances that are equal but do not compare as == sincethey are different objects.Examples of classes which should generallynot be compared by reference are java.lang.Integer, java.lang.Float, etc.
```

**参考信息**

- 最新更新时间：2020-05-28 19:22:17

<a id="rule-243"></a>

### 243. RC_REF_COMPARISON_BAD_PRACTICE

| 字段 | 内容 |
|---|---|
| 问题级别 | 一般 |
| 语言 | JAVA |
| 标签 | spotbugs |
| 关联工具规则 | RC_REF_COMPARISON_BAD_PRACTICE |
| 规则类型 | 通用类（非编程规范） |
| 预估误报率 | 5% |

**审查要点**

```text
Suspicious reference comparison to constant
This method compares a reference value to a constant using the == or != operator,where the correct way to compare instances of this type is generallywith the equals() method.It is possible to create distinct instances that are equal but do not compare as == sincethey are different objects.Examples of classes which should generallynot be compared by reference are java.lang.Integer, java.lang.Float, etc.
```

**参考信息**

- 最新更新时间：2025-05-27 09:58:46

<a id="rule-244"></a>

### 244. RC_REF_COMPARISON_BAD_PRACTICE_BOOLEAN

| 字段 | 内容 |
|---|---|
| 问题级别 | 一般 |
| 语言 | JAVA |
| 标签 | spotbugs |
| 关联工具规则 | RC_REF_COMPARISON_BAD_PRACTICE_BOOLEAN |
| 规则类型 | 通用类（非编程规范） |
| 预估误报率 | 15% |

**审查要点**

```text
Suspicious reference comparison of Boolean values
This method compares two Boolean values using the == or != operator.Normally, there are only two Boolean values (Boolean.TRUE and Boolean.FALSE),but it is possible to create other Boolean objects using the new Boolean(b)constructor. It is best to avoid such objects, but if they do exist,then checking Boolean objects for equality using == or != will give resultsthan are different than you would get using .equals(...).
```

**参考信息**

- 最新更新时间：2020-05-28 19:22:17

<a id="rule-245"></a>

### 245. RE_BAD_SYNTAX_FOR_REGULAR_EXPRESSION

| 字段 | 内容 |
|---|---|
| 问题级别 | 一般 |
| 语言 | JAVA |
| 标签 | spotbugs |
| 关联工具规则 | RE_BAD_SYNTAX_FOR_REGULAR_EXPRESSION |
| 规则类型 | 通用类（非编程规范） |
| 预估误报率 | 1% |

**审查要点**

```text
Invalid syntax for regular expression
The code here uses a regular expression that is invalid according to the syntaxfor regular expressions. This statement will throw a PatternSyntaxException whenexecuted.
```

**参考信息**

- 最新更新时间：2020-05-28 19:22:51

<a id="rule-246"></a>

### 246. RE_CANT_USE_FILE_SEPARATOR_AS_REGULAR_EXPRESSION

| 字段 | 内容 |
|---|---|
| 问题级别 | 一般 |
| 语言 | JAVA |
| 标签 | spotbugs |
| 关联工具规则 | RE_CANT_USE_FILE_SEPARATOR_AS_REGULAR_EXPRESSION |
| 规则类型 | 通用类（非编程规范） |
| 预估误报率 | 10% |

**审查要点**

```text
File.separator used for regular expression
The code here uses File.separatorwhere a regular expression is required. This will fail on Windowsplatforms, where the File.separator is a backslash, which is interpreted in aregular expression as an escape character. Among other options, you can just useFile.separatorChar=='\\' ? "\\\\" : File.separator instead ofFile.separator
```

**参考信息**

- 最新更新时间：2020-05-28 19:22:52

<a id="rule-247"></a>

### 247. RE_POSSIBLE_UNINTENDED_PATTERN

| 字段 | 内容 |
|---|---|
| 问题级别 | 一般 |
| 语言 | JAVA |
| 标签 | spotbugs |
| 关联工具规则 | RE_POSSIBLE_UNINTENDED_PATTERN |
| 规则类型 | 通用类（非编程规范） |
| 预估误报率 | 10% |

**审查要点**

```text
"." or "|" used for regular expression
A String function is being invoked and "." or "|" is being passedto a parameter that takes a regular expression as an argument. Is this what you intended?For example
s.replaceAll(".", "/") will return a String in which every character has been replaced by a '/' character
s.split(".") always returns a zero length array of String
"ab|cd".replaceAll("|", "/") will return "/a/b/|/c/d/"
"ab|cd".split("|") will return array with six (!) elements: [, a, b, |, c, d]
```

**修复建议**

在前面加上"\\"转义符

**错误示例**

```text
if(version != null && (!version.equalsIgnoreCase("")) && version.split(".").length < 4){
this.m_txfSoftVersion.setVersion(version);
}
String的split方法传递的参数是正则表达式，正则表达式本身用到的字符需要转义，如：句点符号"."，美元符号"$"，乘方符号"^"，大括号"{}"，方括号"[]"，圆括号"()"，竖线"|"，星号"*"，加号"+"，问号"?"等等，这些需要在前面加上"\\"转义符。
```

**参考信息**

- 链接：内部链接已省略
- 最新更新时间：2020-05-28 19:22:51

<a id="rule-248"></a>

### 248. RR_NOT_CHECKED

| 字段 | 内容 |
|---|---|
| 问题级别 | 一般 |
| 语言 | JAVA |
| 标签 | spotbugs |
| 关联工具规则 | RR_NOT_CHECKED |
| 规则类型 | 通用类（非编程规范） |
| 预估误报率 | 10% |

**审查要点**

```text
Method ignores results of InputStream.read()
This method ignores the return value of one of the variants of java.io.InputStream.read() which can return multiple bytes.  If the return value is not checked, the caller will not be able to correctly handle the case where fewer bytes were read than the caller requested.  This is a particularly insidious kind of bug, because in many programs, reads from input streams usually do read the full amount of data requested, causing the program to fail only sporadically.
```

**参考信息**

- 最新更新时间：2020-05-28 19:21:48

<a id="rule-249"></a>

### 249. RS_READOBJECT_SYNC

| 字段 | 内容 |
|---|---|
| 问题级别 | 一般 |
| 语言 | JAVA |
| 标签 | spotbugs |
| 关联工具规则 | RS_READOBJECT_SYNC |
| 规则类型 | 通用类（非编程规范） |
| 预估误报率 | 10% |

**审查要点**

```text
Class's readObject() method is synchronized
This serializable class defines a readObject() which is synchronized.  By definition, an object created by deserialization is only reachable by one thread, and thus there is no need for readObject() to be synchronized.  If the readObject() method itself is causing the object to become visible to another thread, that is an example of very dubious coding style.
```

**参考信息**

- 最新更新时间：2025-06-16 15:16:17

<a id="rule-250"></a>

### 250. RU_INVOKE_RUN

| 字段 | 内容 |
|---|---|
| 问题级别 | 一般 |
| 语言 | JAVA |
| 标签 | spotbugs |
| 关联工具规则 | RU_INVOKE_RUN |
| 规则类型 | 通用类（非编程规范） |
| 预估误报率 | 10% |

**审查要点**

```text
Invokes run on a thread (did you mean to start it instead?)
This method explicitly invokes run() on an object.  In general, classes implement the Runnable interface because they are going to have their run() method invoked in a new thread, in which case Thread.start() is the right method to call.
```

**修复建议**

```text
多线程错误 -在线程中调用了run()
run()方法只是类的一个普通方法而已，如果直接调用run方法，程序中依然只有主线程这一个线程。启动一个新的线程，我们不是直接调用Thread的子类对象的run方法，而是调用Thread子类对象的start（从Thread类中继承的）方法，Thread类对象的start方法将产生一个新的线程，并在该线程上运行该Thread类对象中的run方法，根据面向对象的多态性，在该线程上实际运行的是Thread子类（也就是我们编写的那个类）对象中的run方法。
```

**错误示例**

```text
public static void main(final String[] args) {
System.out.println("Main thread: " + Thread.currentThread().getId());
final FooThread thread = new FooThread();
thread.run();
//thread.start();
}
public static class FooThread extends Thread
{
@Override
public void run() {
System.out.println("I'm executing from thread " + Thread.currentThread().getId());
super.run();
}
}
```

**参考信息**

- 链接：内部链接已省略
- 最新更新时间：2020-05-28 19:21:34

<a id="rule-251"></a>

### 251. RV_01_TO_INT

| 字段 | 内容 |
|---|---|
| 问题级别 | 一般 |
| 语言 | JAVA |
| 标签 | spotbugs |
| 关联工具规则 | RV_01_TO_INT |
| 规则类型 | 通用类（非编程规范） |
| 预估误报率 | 10% |

**审查要点**

```text
Random value from 0 to 1 is coerced to the integer 0
A random value from 0 to 1 is being coerced to the integer value 0. You probablywant to multiply the random value by something else before coercing it to an integer, or use the Random.nextInt(n) method.
```

**参考信息**

- 最新更新时间：2020-05-28 19:21:17

<a id="rule-252"></a>

### 252. RV_ABSOLUTE_VALUE_OF_HASHCODE

| 字段 | 内容 |
|---|---|
| 问题级别 | 一般 |
| 语言 | JAVA |
| 标签 | spotbugs |
| 关联工具规则 | RV_ABSOLUTE_VALUE_OF_HASHCODE |
| 规则类型 | 通用类（非编程规范） |
| 预估误报率 | 10% |

**审查要点**

```text
Bad attempt to compute absolute value of signed 32-bit hashcode
This code generates a hashcode and then computesthe absolute value of that hashcode. If the hashcodeis Integer.MIN_VALUE, then the result will be negative as well (sinceMath.abs(Integer.MIN_VALUE) == Integer.MIN_VALUE).
One out of 2^32 strings have a hashCode of Integer.MIN_VALUE,including "polygenelubricants" "GydZG_" and ""DESIGNING WORKHOUSES".
```

**参考信息**

- 最新更新时间：2020-05-28 19:22:24

<a id="rule-253"></a>

### 253. RV_ABSOLUTE_VALUE_OF_RANDOM_INT

| 字段 | 内容 |
|---|---|
| 问题级别 | 一般 |
| 语言 | JAVA |
| 标签 | spotbugs |
| 关联工具规则 | RV_ABSOLUTE_VALUE_OF_RANDOM_INT |
| 规则类型 | 通用类（非编程规范） |
| 预估误报率 | 10% |

**审查要点**

```text
Bad attempt to compute absolute value of signed random integer
This code generates a random signed integer and then computesthe absolute value of that random integer. If the number returned by the random numbergenerator is Integer.MIN_VALUE, then the result will be negative as well (sinceMath.abs(Integer.MIN_VALUE) == Integer.MIN_VALUE). (Same problem arises for long values as well).
```

**修复建议**

对产生的随机数做判断，是否为Integer.MIN_VALUE特殊情况

**错误示例**

```text
public int getAbsRandom() {
Random random = new Random();
int raw = random.nextInt();
returnMath.abs(raw);
}
```

**参考信息**

- 链接：内部链接已省略
- 最新更新时间：2025-06-16 15:16:17

<a id="rule-254"></a>

### 254. RV_CHECK_COMPARETO_FOR_SPECIFIC_RETURN_VALUE

| 字段 | 内容 |
|---|---|
| 问题级别 | 一般 |
| 语言 | JAVA |
| 标签 | spotbugs |
| 关联工具规则 | RV_CHECK_COMPARETO_FOR_SPECIFIC_RETURN_VALUE |
| 规则类型 | 通用类（非编程规范） |
| 预估误报率 | 15% |

**审查要点**

```text
Code checks for specific values returned by compareTo
This code invoked a compareTo or compare method, and checks to see if the return value is a specific value,such as 1 or -1. When invoking these methods, you should only check the sign of the result, not for any specificnon-zero value. While many or most compareTo and compare methods only return -1, 0 or 1, some of themwill return other values.
```

**参考信息**

- 最新更新时间：2020-05-28 19:22:07

<a id="rule-255"></a>

### 255. RV_EXCEPTION_NOT_THROWN

| 字段 | 内容 |
|---|---|
| 问题级别 | 一般 |
| 语言 | JAVA |
| 标签 | spotbugs |
| 关联工具规则 | RV_EXCEPTION_NOT_THROWN |
| 规则类型 | 通用类（非编程规范） |
| 预估误报率 | 10% |

**审查要点**

```text
Exception created and dropped rather than thrown
This code creates an exception (or error) object, but doesn't do anything with it. For example,something like
if (x < 0) { new IllegalArgumentException("x must be nonnegative");}
It was probably the intent of the programmer to throw the created exception:
if (x < 0) { throw new IllegalArgumentException("x must be nonnegative");}
```

**正确示例**

```text
1、打印异常调用栈。
private void test(){
if(x<0){
new IllegalArgumentException("x must be nonnegative "). printStackTrace();
}
}
2、抛出异常。
private void test(){
if(x<0){
throw new IllegalArgumentException("x must be nonnegative ");
}
}
```

**错误示例**

```text
private void test(){
new IllegalArgumentException("x must be nonnegative");
}
```

**参考信息**

- 链接：内部链接已省略
- 最新更新时间：2020-05-28 19:22:07

<a id="rule-256"></a>

### 256. RV_NEGATING_RESULT_OF_COMPARETO

| 字段 | 内容 |
|---|---|
| 问题级别 | 一般 |
| 语言 | JAVA |
| 标签 | spotbugs |
| 关联工具规则 | RV_NEGATING_RESULT_OF_COMPARETO |
| 规则类型 | 通用类（非编程规范） |
| 预估误报率 | 15% |

**审查要点**

```text
Negating the result of compareTo()/compare()
This code negatives the return value of a compareTo or compare method.This is a questionable or bad programming practice, since if the returnvalue is Integer.MIN_VALUE, negating the return value won'tnegate the sign of the result. You can achieve the same intended resultby reversing the order of the operands rather than by negating the results.
```

**参考信息**

- 最新更新时间：2020-05-28 19:21:27

<a id="rule-257"></a>

### 257. SA_FIELD_SELF_ASSIGNMENT

| 字段 | 内容 |
|---|---|
| 问题级别 | 一般 |
| 语言 | JAVA |
| 标签 | spotbugs |
| 关联工具规则 | SA_FIELD_SELF_ASSIGNMENT |
| 规则类型 | 通用类（非编程规范） |
| 预估误报率 | 10% |

**审查要点**

```text
Self assignment of field
This method contains a self assignment of a field; e.g.
int x;public void foo() { x = x;}
Such assignments are useless, and may indicate a logic error or typo.
```

**参考信息**

- 最新更新时间：2020-05-28 19:22:20

<a id="rule-258"></a>

### 258. SA_FIELD_SELF_COMPARISON

| 字段 | 内容 |
|---|---|
| 问题级别 | 一般 |
| 语言 | JAVA |
| 标签 | spotbugs |
| 关联工具规则 | SA_FIELD_SELF_COMPARISON |
| 规则类型 | 通用类（非编程规范） |
| 预估误报率 | 10% |

**审查要点**

```text
Self comparison of field with itself
This method compares a field with itself, and may indicate a typo ora logic error. Make sure that you are comparing the right things.
```

**参考信息**

- 最新更新时间：2020-05-28 19:22:22

<a id="rule-259"></a>

### 259. SA_FIELD_SELF_COMPUTATION

| 字段 | 内容 |
|---|---|
| 问题级别 | 一般 |
| 语言 | JAVA |
| 标签 | spotbugs |
| 关联工具规则 | SA_FIELD_SELF_COMPUTATION |
| 规则类型 | 通用类（非编程规范） |
| 预估误报率 | 10% |

**审查要点**

```text
Nonsensical self computation involving a field (e.g., x & x)
This method performs a nonsensical computation of a field with anotherreference to the same field (e.g., x&x or x-x). Because of the natureof the computation, this operation doesn't seem to make sense,and may indicate a typo ora logic error. Double check the computation.
```

**参考信息**

- 最新更新时间：2025-06-16 15:16:17

<a id="rule-260"></a>

### 260. SA_LOCAL_SELF_ASSIGNMENT_INSTEAD_OF_FIELD

| 字段 | 内容 |
|---|---|
| 问题级别 | 一般 |
| 语言 | JAVA |
| 标签 | spotbugs |
| 关联工具规则 | SA_LOCAL_SELF_ASSIGNMENT_INSTEAD_OF_FIELD |
| 规则类型 | 通用类（非编程规范） |
| 预估误报率 | 15% |

**审查要点**

```text
Self assignment of local rather than assignment to field
This method contains a self assignment of a local variable, and thereis a field with an identical name.assignment appears to have been ; e.g.
int foo; public void setFoo(int foo) { foo = foo; }
The assignment is useless. Did you mean to assign to the field instead?
```

**参考信息**

- 最新更新时间：2020-05-28 19:22:00

<a id="rule-261"></a>

### 261. SA_LOCAL_SELF_COMPUTATION

| 字段 | 内容 |
|---|---|
| 问题级别 | 一般 |
| 语言 | JAVA |
| 标签 | spotbugs |
| 关联工具规则 | SA_LOCAL_SELF_COMPUTATION |
| 规则类型 | 通用类（非编程规范） |
| 预估误报率 | 10% |

**审查要点**

```text
Nonsensical self computation involving a variable (e.g., x & x)
This method performs a nonsensical computation of a local variable with anotherreference to the same variable (e.g., x&x or x-x). Because of the natureof the computation, this operation doesn't seem to make sense,and may indicate a typo ora logic error. Double check the computation.
```

**参考信息**

- 最新更新时间：2020-05-28 19:22:22

<a id="rule-262"></a>

### 262. SBSC_USE_STRINGBUFFER_CONCATENATION

| 字段 | 内容 |
|---|---|
| 问题级别 | 一般 |
| 语言 | JAVA |
| 标签 | spotbugs |
| 关联工具规则 | SBSC_USE_STRINGBUFFER_CONCATENATION |
| 规则类型 | 通用类（非编程规范） |
| 预估误报率 | 10% |

**审查要点**

```text
Method concatenates strings using + in a loop
The method seems to be building a String using concatenation in a loop.In each iteration, the String is converted to a StringBuffer/StringBuilder, appended to, and converted back to a String. This can lead to a cost quadratic in the number of iterations, as the growing string is recopied in each iteration.
Better performance can be obtained by usinga StringBuffer (or StringBuilder in Java 1.5) explicitly.
For example:
// This is badString s = "";for (int i = 0; i < field.length; ++i) { s = s + field[i];}// This is betterStringBuffer buf = new StringBuffer();for (int i = 0; i < field.length; ++i) { buf.append(field[i]);}String s = buf.toString();
```

**正确示例**

```text
// This is better
StringBuffer buf = new StringBuffer();
for (int i = 0; i < field.length; ++i) {
buf.append(field[i]);
}
String s = buf.toString();
```

**错误示例**

```text
// This is bad
String s = "";
for (int i = 0; i < field.length; ++i) {
s = s + field[i];
}
```

**参考信息**

- 链接：内部链接已省略
- 最新更新时间：2020-05-28 19:22:31

<a id="rule-263"></a>

### 263. SC_START_IN_CTOR

| 字段 | 内容 |
|---|---|
| 问题级别 | 一般 |
| 语言 | JAVA |
| 标签 | spotbugs |
| 关联工具规则 | SC_START_IN_CTOR |
| 规则类型 | 通用类（非编程规范） |
| 预估误报率 | 10% |

**审查要点**

```text
Constructor invokes Thread.start()
The constructor starts a thread. This is likely to be wrong if the class is ever extended/subclassed, since the thread will be started before the subclass constructor is started.
```

**参考信息**

- 最新更新时间：2020-05-28 19:21:55

<a id="rule-264"></a>

### 264. SE_METHOD_MUST_BE_PRIVATE

| 字段 | 内容 |
|---|---|
| 问题级别 | 一般 |
| 语言 | JAVA |
| 标签 | spotbugs |
| 关联工具规则 | SE_METHOD_MUST_BE_PRIVATE |
| 规则类型 | 通用类（非编程规范） |
| 预估误报率 | 10% |

**审查要点**

```text
Method must be private in order for serialization to work
This class implements the Serializable interface, and defines a method for custom serialization/deserialization. But since that method isn't declared private, it will be silently ignored by the serialization/deserialization API.
```

**参考信息**

- 最新更新时间：2025-05-27 09:58:46

<a id="rule-265"></a>

### 265. SE_NONFINAL_SERIALVERSIONID

| 字段 | 内容 |
|---|---|
| 问题级别 | 一般 |
| 语言 | JAVA |
| 标签 | spotbugs |
| 关联工具规则 | SE_NONFINAL_SERIALVERSIONID |
| 规则类型 | 通用类（非编程规范） |
| 预估误报率 | 10% |

**审查要点**

```text
serialVersionUID isn't final
This class defines a serialVersionUID field that is not final.  The field should be made final if it is intended to specify the version UID for purposes of serialization.
```

**参考信息**

- 最新更新时间：2025-06-16 15:16:17

<a id="rule-266"></a>

### 266. SE_NONLONG_SERIALVERSIONID

| 字段 | 内容 |
|---|---|
| 问题级别 | 一般 |
| 语言 | JAVA |
| 标签 | spotbugs |
| 关联工具规则 | SE_NONLONG_SERIALVERSIONID |
| 规则类型 | 通用类（非编程规范） |
| 预估误报率 | 10% |

**审查要点**

```text
serialVersionUID isn't long
This class defines a serialVersionUID field that is not long.  The field should be made long if it is intended to specify the version UID for purposes of serialization.
```

**参考信息**

- 最新更新时间：2025-06-16 15:16:17

<a id="rule-267"></a>

### 267. SE_NONSTATIC_SERIALVERSIONID

| 字段 | 内容 |
|---|---|
| 问题级别 | 一般 |
| 语言 | JAVA |
| 标签 | spotbugs |
| 关联工具规则 | SE_NONSTATIC_SERIALVERSIONID |
| 规则类型 | 通用类（非编程规范） |
| 预估误报率 | 10% |

**审查要点**

```text
serialVersionUID isn't static
This class defines a serialVersionUID field that is not static.  The field should be made static if it is intended to specify the version UID for purposes of serialization.
```

**参考信息**

- 最新更新时间：2025-06-16 15:16:17

<a id="rule-268"></a>

### 268. SE_NO_SERIALVERSIONID

| 字段 | 内容 |
|---|---|
| 问题级别 | 一般 |
| 语言 | JAVA |
| 标签 | spotbugs |
| 关联工具规则 | SE_NO_SERIALVERSIONID |
| 规则类型 | 通用类（非编程规范） |
| 预估误报率 | 15% |

**审查要点**

```text
Class is Serializable, but doesn't define serialVersionUID
This class implements the Serializable interface, but does not define a serialVersionUID field.  A change as simple as adding a reference to a .class object will add synthetic fields to the class, which will unfortunately change the implicit serialVersionUID (e.g., adding a reference to String.class will generate a static field class$java$lang$String). Also, different source code to bytecode compilers may use different naming conventions for synthetic variables generated for references to class objects or inner classes. To ensure interoperability of Serializable across versions, consider adding an explicit serialVersionUID.
```

**参考信息**

- 最新更新时间：2020-05-28 19:21:00

<a id="rule-269"></a>

### 269. SE_NO_SUITABLE_CONSTRUCTOR

| 字段 | 内容 |
|---|---|
| 问题级别 | 一般 |
| 语言 | JAVA |
| 标签 | spotbugs |
| 关联工具规则 | SE_NO_SUITABLE_CONSTRUCTOR |
| 规则类型 | 通用类（非编程规范） |
| 预估误报率 | 10% |

**审查要点**

```text
Class is Serializable but its superclass doesn't define a void constructor
This class implements the Serializable interface and its superclass does not. When such an object is deserialized, the fields of the superclass need to be initialized by invoking the void constructor of the superclass. Since the superclass does not have one, serialization and deserialization will fail at runtime.
```

**参考信息**

- 最新更新时间：2020-05-28 19:21:51

<a id="rule-270"></a>

### 270. SF_DEAD_STORE_DUE_TO_SWITCH_FALLTHROUGH

| 字段 | 内容 |
|---|---|
| 问题级别 | 一般 |
| 语言 | JAVA |
| 标签 | spotbugs |
| 关联工具规则 | SF_DEAD_STORE_DUE_TO_SWITCH_FALLTHROUGH |
| 规则类型 | 通用类（非编程规范） |
| 预估误报率 | 10% |

**审查要点**

```text
Dead store due to switch statement fall through
A value stored in the previous switch case is overwritten here due to a switch fall through. It is likely that you forgot to put a break or return at the end of the previous case.
```

**参考信息**

- 最新更新时间：2025-06-16 15:16:17

<a id="rule-271"></a>

### 271. SF_DEAD_STORE_DUE_TO_SWITCH_FALLTHROUGH_TO_THROW

| 字段 | 内容 |
|---|---|
| 问题级别 | 一般 |
| 语言 | JAVA |
| 标签 | spotbugs |
| 关联工具规则 | SF_DEAD_STORE_DUE_TO_SWITCH_FALLTHROUGH_TO_THROW |
| 规则类型 | 通用类（非编程规范） |
| 预估误报率 | 10% |

**审查要点**

```text
Dead store due to switch statement fall through to throw
A value stored in the previous switch case is ignored here due to a switch fall through to a place where an exception is thrown. It is likely that you forgot to put a break or return at the end of the previous case.
```

**参考信息**

- 最新更新时间：2020-05-28 19:21:52

<a id="rule-272"></a>

### 272. SIC_INNER_SHOULD_BE_STATIC

| 字段 | 内容 |
|---|---|
| 问题级别 | 一般 |
| 语言 | JAVA |
| 标签 | spotbugs |
| 关联工具规则 | SIC_INNER_SHOULD_BE_STATIC |
| 规则类型 | 通用类（非编程规范） |
| 预估误报率 | 15% |

**审查要点**

```text
Should be a static inner class
This class is an inner class, but does not use its embedded reference to the object which created it.  This reference makes the instances of the class larger, and may keep the reference to the creator object alive longer than necessary.  If possible, the class should be made static.
```

**参考信息**

- 最新更新时间：2020-05-28 19:22:00

<a id="rule-273"></a>

### 273. SIC_INNER_SHOULD_BE_STATIC_NEEDS_THIS

| 字段 | 内容 |
|---|---|
| 问题级别 | 一般 |
| 语言 | JAVA |
| 标签 | spotbugs |
| 关联工具规则 | SIC_INNER_SHOULD_BE_STATIC_NEEDS_THIS |
| 规则类型 | 通用类（非编程规范） |
| 预估误报率 | 10% |

**审查要点**

```text
Could be refactored into a static inner class
This class is an inner class, but does not use its embedded reference to the object which created it except during construction of theinner object.  This reference makes the instances of the class larger, and may keep the reference to the creator object alive longer than necessary.  If possible, the class should be made into a static inner class. Since the reference to the outer object is required during construction of the inner instance, the inner class will need to be refactored so as to pass a reference to the outer instance to the constructor for the inner class.
```

**参考信息**

- 最新更新时间：2025-06-16 15:16:17

<a id="rule-274"></a>

### 274. SI_INSTANCE_BEFORE_FINALS_ASSIGNED

| 字段 | 内容 |
|---|---|
| 问题级别 | 一般 |
| 语言 | JAVA |
| 标签 | spotbugs |
| 关联工具规则 | SI_INSTANCE_BEFORE_FINALS_ASSIGNED |
| 规则类型 | 通用类（非编程规范） |
| 预估误报率 | 10% |

**审查要点**

```text
Static initializer creates instance before all static final fields assigned
The class's static initializer creates an instance of the classbefore all of the static final fields are assigned.
```

**参考信息**

- 最新更新时间：2020-05-28 19:22:13

<a id="rule-275"></a>

### 275. SP_SPIN_ON_FIELD

| 字段 | 内容 |
|---|---|
| 问题级别 | 一般 |
| 语言 | JAVA |
| 标签 | spotbugs |
| 关联工具规则 | SP_SPIN_ON_FIELD |
| 规则类型 | 通用类（非编程规范） |
| 预估误报率 | 10% |

**审查要点**

```text
Method spins on field
This method spins in a loop which reads a field.  The compiler may legally hoist the read out of the loop, turning the code into an infinite loop.  The class should be changed so it uses proper synchronization (including wait and notify calls).
```

**参考信息**

- 最新更新时间：2025-06-16 15:16:17

<a id="rule-276"></a>

### 276. SQL_BAD_PREPARED_STATEMENT_ACCESS

| 字段 | 内容 |
|---|---|
| 问题级别 | 一般 |
| 语言 | JAVA |
| 标签 | spotbugs |
| 关联工具规则 | SQL_BAD_PREPARED_STATEMENT_ACCESS |
| 规则类型 | 通用类（非编程规范） |
| 预估误报率 | 10% |

**审查要点**

```text
Method attempts to access a prepared statement parameter with index 0
A call to a setXXX method of a prepared statement was made where theparameter index is 0. As parameter indexes start at index 1, this is always a mistake.
```

**参考信息**

- 最新更新时间：2020-05-28 19:22:36

<a id="rule-277"></a>

### 277. SQL_NONCONSTANT_STRING_PASSED_TO_EXECUTE

| 字段 | 内容 |
|---|---|
| 问题级别 | 严重 |
| 语言 | JAVA |
| 标签 | spotbugs |
| 关联工具规则 | SQL_NONCONSTANT_STRING_PASSED_TO_EXECUTE |
| 规则类型 | 通用类（非编程规范） |
| 预估误报率 | 20% |

**审查要点**

```text
Nonconstant string passed to execute or addBatch method on an SQL statement
The method invokes the execute or addBatch method on an SQL statement with a String that seemsto be dynamically generated. Consider usinga prepared statement instead. It is more efficient and less vulnerable toSQL injection attacks.
```

**参考信息**

- 最新更新时间：2020-05-28 19:21:18

<a id="rule-278"></a>

### 278. SS_SHOULD_BE_STATIC

| 字段 | 内容 |
|---|---|
| 问题级别 | 一般 |
| 语言 | JAVA |
| 标签 | spotbugs |
| 关联工具规则 | SS_SHOULD_BE_STATIC |
| 规则类型 | 通用类（非编程规范） |
| 预估误报率 | 5% |

**审查要点**

```text
Unread field: should this field be static?
This class contains an instance final field that is initialized to a compile-time static value. Consider making the field static.
```

**参考信息**

- 最新更新时间：2025-05-27 09:58:46

<a id="rule-279"></a>

### 279. STCAL_INVOKE_ON_STATIC_CALENDAR_INSTANCE

| 字段 | 内容 |
|---|---|
| 问题级别 | 一般 |
| 语言 | JAVA |
| 标签 | spotbugs |
| 关联工具规则 | STCAL_INVOKE_ON_STATIC_CALENDAR_INSTANCE |
| 规则类型 | 通用类（非编程规范） |
| 预估误报率 | 10% |

**审查要点**

```text
Call to static Calendar
Even though the JavaDoc does not contain a hint about it, Calendars are inherently unsafe for multithreaded use.The detector has found a call to an instance of Calendar that has been obtained via a staticfield. This looks suspicious.
For more information on this see JDK Bug #6231579and JDK Bug #6178997.
```

**参考信息**

- 最新更新时间：2020-05-28 19:23:02

<a id="rule-280"></a>

### 280. STCAL_INVOKE_ON_STATIC_DATE_FORMAT_INSTANCE

| 字段 | 内容 |
|---|---|
| 问题级别 | 一般 |
| 语言 | JAVA |
| 标签 | spotbugs |
| 关联工具规则 | STCAL_INVOKE_ON_STATIC_DATE_FORMAT_INSTANCE |
| 规则类型 | 通用类（非编程规范） |
| 预估误报率 | 10% |

**审查要点**

```text
Call to static DateFormat
As the JavaDoc states, DateFormats are inherently unsafe for multithreaded use.The detector has found a call to an instance of DateFormat that has been obtained via a staticfield. This looks suspicious.
For more information on this see JDK Bug #6231579and JDK Bug #6178997.
```

**参考信息**

- 最新更新时间：2025-05-27 09:58:46

<a id="rule-281"></a>

### 281. STCAL_STATIC_CALENDAR_INSTANCE

| 字段 | 内容 |
|---|---|
| 问题级别 | 一般 |
| 语言 | JAVA |
| 标签 | spotbugs |
| 关联工具规则 | STCAL_STATIC_CALENDAR_INSTANCE |
| 规则类型 | 通用类（非编程规范） |
| 预估误报率 | 10% |

**审查要点**

```text
Static Calendar field
Even though the JavaDoc does not contain a hint about it, Calendars are inherently unsafe for multithreaded use.Sharing a single instance across thread boundaries without proper synchronization will result in erratic behavior of theapplication. Under 1.4 problems seem to surface less often than under Java 5 where you will probably seerandom ArrayIndexOutOfBoundsExceptions or IndexOutOfBoundsExceptions in sun.util.calendar.BaseCalendar.getCalendarDateFromFixedDate().
You may also experience serialization problems.
Using an instance field is recommended.
For more information on this see JDK Bug #6231579and JDK Bug #6178997.
```

**参考信息**

- 最新更新时间：2020-05-28 19:23:01

<a id="rule-282"></a>

### 282. STCAL_STATIC_SIMPLE_DATE_FORMAT_INSTANCE

| 字段 | 内容 |
|---|---|
| 问题级别 | 一般 |
| 语言 | JAVA |
| 标签 | spotbugs |
| 关联工具规则 | STCAL_STATIC_SIMPLE_DATE_FORMAT_INSTANCE |
| 规则类型 | 通用类（非编程规范） |
| 预估误报率 | 10% |

**审查要点**

```text
Static DateFormat
As the JavaDoc states, DateFormats are inherently unsafe for multithreaded use.Sharing a single instance across thread boundaries without proper synchronization will result in erratic behavior of theapplication.
You may also experience serialization problems.
Using an instance field is recommended.
For more information on this see JDK Bug #6231579and JDK Bug #6178997.
```

**参考信息**

- 最新更新时间：2020-05-28 19:23:02

<a id="rule-283"></a>

### 283. STI_INTERRUPTED_ON_CURRENTTHREAD

| 字段 | 内容 |
|---|---|
| 问题级别 | 一般 |
| 语言 | JAVA |
| 标签 | spotbugs |
| 关联工具规则 | STI_INTERRUPTED_ON_CURRENTTHREAD |
| 规则类型 | 通用类（非编程规范） |
| 预估误报率 | 5% |

**审查要点**

```text
Unneeded use of currentThread() call, to call interrupted()
This method invokes the Thread.currentThread() call, just to call the interrupted() method. As interrupted() is astatic method, is more simple and clear to use Thread.interrupted().
```

**参考信息**

- 最新更新时间：2025-05-27 09:58:46

<a id="rule-284"></a>

### 284. SecH_GTS_JAVA_ACTIVITI_JUEL

| 字段 | 内容 |
|---|---|
| 问题级别 | 严重 |
| 语言 | JAVA |
| 标签 | SecBrella |
| 关联工具规则 | SecH_GTS_JAVA_ACTIVITI_JUEL |
| 规则类型 | 安全类（非编程规范） |
| 预估误报率 | 20% |

**审查要点**

activiti 使用用户自定义设计流程会允许客户上传表达式注入脚本，因此必须在后台服务中对用户输入的activiti表达式做校验

**修复建议**

```text
按照正确示例中的代码，对springboot进行配置。activiti5和activiti6只引入包有路径不同，安全示例代码相同
推荐使用US组件 内部链接已省略
```

**正确示例**

```text
```
// 用户实现SpringExpressionManager接口.重写createElResolver 方法
@Override
protected ELResolver createElResolver(VariableScope variableScope) {
CompositeELResolver compositeElResolver = new CompositeELResolver();
if (beans != null) {
compositeElResolver.add(new ReadOnlyMapELResolver(beans));
} else {
compositeElResolver.add(new ApplicationContextElResolver(applicationContext));
}
compositeElResolver.add(new ArrayELResolver());
compositeElResolver.add(new ListELResolver());
compositeElResolver.add(new MapELResolver());
// 用户自定义实现 自定义JSONObject EL解析器
// 自定义 EL 白名单校验解析器
compositeElResolver.add(new CustomizeBeanELResolver());
return compositeElResolver;
}
// 用户实现BeanELResolver类 重写invoke方法
@Override
public Object invoke(ELContext context, Object base, Object method, Class<?>[] paramTypes, Object[] params) {
String methodName = method.toString();
String className = base.getClass().getSimpleName();
if (isSecurity(methodName, className)) {
return super.invoke(context, base, method, paramTypes, params);
}
LOGGER.warn("Unsafe method :{}", methodName);
return null;
}
private boolean isSecurity(String methodName, String classSimpleName) {
if (StringUtils.equals(GET_METHOD_NAME, methodName) && StringUtils.equals(classSimpleName, HOLDER_CLASS_NAME)) {
return true;
}
if (StringUtils.equals(IS_MULTI_INSTANCE_TASK_COMPLETE_METHOD_NAME, methodName)
&& StringUtils.equals(classSimpleName, COMPLETION_CONDITION_SERVICE_CLASS_NAME)) {
return true;
}
if (StringUtils.equals(methodName, GET_TASK_ASSIGNMENTS_METHOD_NAME)
&& StringUtils.equals(classSimpleName, ASSIGNMENT_SERVICE_CLASS_NAME)) {
return true;
}
if (StringUtils.equals(methodName, IS_CONDITION_MATCHED_METHOD_NAME)
&& StringUtils.equals(classSimpleName, SEQUENCE_FLOW_SERVICE_CLASS_NAME)) {
return true;
}
if (StringUtils.equalsAny(methodName, BPM_UTILS_METHOD_NAMES)
&& StringUtils.equals(classSimpleName, BPM_UTILS_CLASS_NAME)) {
return true;
}
if (StringUtils.equals(methodName, IS_TEL_CONDITION_MATCHED_METHOD_NAME)
&& StringUtils.equals(classSimpleName, SEQUENCE_FLOW_SERVICE_CLASS_NAME)) {
return true;
}
if (StringUtils.equalsAny(methodName, SEP_STRING_METHOD_NAMES)
&& StringUtils.equals(classSimpleName, SEP_STRING_CLASS_NAME)) {
return true;
}
if (StringUtils.equalsAny(methodName, SEP_ARRAYLIST_METHOD_NAMES)
&& StringUtils.equals(classSimpleName, SEP_ARRAYLIST_CLASS_NAME)) {
return true;
}
return false;
}
// 将MySpringExpressionManager加入springboot配置中
@Configuration
public class Activiti {
@Autowired
SpringProcessEngineConfiguration springProcessEngineConfiguration;
/**
* init
*
* @remark created by w30013939 at 2021/9/10
*/
@PostConstruct
public void init() {
springProcessEngineConfiguration.setExpressionManager(
new MySpringExpressionManager(springProcessEngineConfiguration.getApplicationContext(),
springProcessEngineConfiguration.getBeans()));
}
}
```
```

**错误示例**

```text
```
/**
* saveModel
* 保存流程
*
* @param modelId modelId
* @param name name
* @param json_xml json_xml
* @param svg_xml svg_xml
* @param description description
* @remark created by w30013939 at 2021/9/10
*/
@PutMapping(value = {"/{modelId}/save"})
@ResponseStatus(HttpStatus.OK)
public void saveModel(@PathVariable String modelId, @RequestParam("name") String name,
@RequestParam("json_xml") String json_xml, @RequestParam("svg_xml") String svg_xml,
@RequestParam("description") String description) {
ByteArrayOutputStream outStream = null;
InputStream svgStream = null;
TranscoderInput input = null;
ObjectNode modelJson = null;
try {
Model model = this.repositoryService.getModel(modelId);
modelJson = (ObjectNode) this.objectMapper.readTree(model.getMetaInfo());
modelJson.put("name", name);
modelJson.put("description", description);
model.setMetaInfo(modelJson.toString());
model.setName(name);
this.repositoryService.saveModel(model);
this.repositoryService.addModelEditorSource(model.getId(), json_xml.getBytes(StandardCharsets.UTF_8)); // bad
svgStream = new ByteArrayInputStream(svg_xml.getBytes(StandardCharsets.UTF_8));
input = new TranscoderInput(svgStream);
PNGTranscoder transcoder = new PNGTranscoder();
outStream = new ByteArrayOutputStream();
TranscoderOutput output = new TranscoderOutput(outStream);
transcoder.transcode(input, output);
byte[] result = outStream.toByteArray();
this.repositoryService.addModelEditorSourceExtra(model.getId(), result); // bad
} catch (IOException e) {
logger.info("IOException");
} catch (TranscoderException e) {
logger.info("TranscoderException");
} finally {
IOUtils.closeQuietly(outStream);
IOUtils.closeQuietly(svgStream);
}
}
@Override
protected ELResolver createElResolver(VariableScope variableScope) {
CompositeELResolver compositeElResolver = new CompositeELResolver();
compositeElResolver.add(new CustomizeVariableScopeElResolver(variableScope));
compositeElResolver.add(new VariableScopeElResolver(variableScope));
if (beans != null) {
compositeElResolver.add(new ReadOnlyMapELResolver(beans));
} else {
compositeElResolver.add(new ApplicationContextElResolver(applicationContext));
}
compositeElResolver.add(new ArrayELResolver());
compositeElResolver.add(new ListELResolver());
compositeElResolver.add(new MapELResolver());
// compositeElResolver.add(new CustomizeJsonNodeELResolver());
// // 自定义JSONObject EL解析器
// compositeElResolver.add(new JSONObjectELResolver());
// 自定义 EL 白名单校验解析器
//compositeElResolver.add(new CustomizeBeanELResolver2());
return compositeElResolver;
}
```
```

**参考信息**

- 最新更新时间：2026-01-21 14:05:27

<a id="rule-285"></a>

### 285. SecH_GTS_JAVA_BYPASS_WHITE_FILTER

| 字段 | 内容 |
|---|---|
| 问题级别 | 严重 |
| 语言 | JAVA |
| 标签 | SecBrella |
| 关联工具规则 | SecH_GTS_JAVA_BYPASS_WHITE_FILTER |
| 规则类型 | 安全类（非编程规范） |
| 预估误报率 | 50% |

**审查要点**

```text
URL过滤、校验一般用于权限控制等，绕过校验会导致权限控制问题，规则对URL 过滤、校验过程进行检测
对URL进行match|matches|contains|endsWith|indexOf|startsWith|subString这些操作前建议使用正确案例进行处理
URL绕过扩展资料见
内部链接已省略
内部链接已省略
内部链接已省略
```

**修复建议**

```text
1、对URl先做归一化和标准化处理，然后校验完整的URL。
推荐使用 USecurity的 URL和URI安全工具 内部链接已省略
com.example.us.common.url.UsUriUtils.normalize|normalizeURI
2、使用方法组合获取完整的安全的URI路径：
String secURI = req.getPathInfo() == null ?
req.getServletContext().getContextPath() + req.getServletPath()
: req.getServletContext().getContextPath() + req.getServletPath() + req.getPathInfo();
```

**正确示例**

```text
```
1.使用方法组合获取完整的安全的URI路径：
public void doFilterGood(HttpServletResponse res, HttpServletRequest req, FilterChain filterChain)
throws IOException, ServletException {
ServletContext servletContext = req.getSession().getServletContext();
String uri = req.getPathInfo() == null ? servletContext.getContextPath() + req.getServletPath() :
servletContext.getContextPath() + req.getServletPath() + req.getPathInfo();
if (sendsWith(".jsp", uri) || uri.endsWith("/home.jsp")
|| uri.endsWith("login.jsp")) {
filterChain.doFilter(req, res);
}
for (int length = PASS_FILE_TTYPES.length, i = 0; i < length; i++) {
if (uri.endsWith(PASS_FILE_TTYPES[i])) {
filterChain.doFilter(req, res);
}
}
}
2.对URl先做归一化和标准化处理，然后校验完整的URL
public void doFilterGood(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain)
throws IOException, ServletException {
HttpServletResponse res = (HttpServletResponse) servletResponse;
HttpServletRequest req = (HttpServletRequest) servletRequest;
// String uri = req.getRequestURI();
String uri = req.getContextPath();
uri = UsUriUtils.normalize(uri + req.getServletPath());
if (this.disableLogin || ssssendsWith(".jsp", uri) || uri.endsWith("/home.jsp") || uri.endsWith("login.jsp")) {
filterChain.doFilter(req, res);
}
for (int length = PASS_FILE_TTYPES.length, i = 0; i < length; i++) {
if (uri.endsWith(PASS_FILE_TTYPES[i])) {
filterChain.doFilter(req, res);
}
}
}
```
```

**错误示例**

```text
```
@Override
public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse,
FilterChain filterChain) throws IOException, ServletException {
HttpServletResponse res = (HttpServletResponse) servletResponse;
HttpServletRequest req = (HttpServletRequest) servletRequest;
// String uri = req.getRequestURI();
String uri = req.getContextPath();
uri = uri + req.getServletPath();
if (endsWith(".jsp", uri) || uri.endsWith("/home.jsp")
|| uri.endsWith("login.jsp")) {
filterChain.doFilter(req, res);
}
for (int length = PASS_FILE_TTYPES.length, i = 0; i < length; i++) {
if (uri.endsWith(PASS_FILE_TTYPES[i])) {
filterChain.doFilter(req, res);
}
}
}
```
```

**参考信息**

- 最新更新时间：2025-06-30 11:16:46

<a id="rule-286"></a>

### 286. SecH_GTS_JAVA_Block_Cipher_Padding_Check

| 字段 | 内容 |
|---|---|
| 问题级别 | 严重 |
| 语言 | JAVA |
| 标签 | SecBrella |
| 关联工具规则 | SecH_GTS_JAVA_Block_Cipher_Padding_Check |
| 规则类型 | 安全类（非编程规范） |
| 预估误报率 | 20% |

**审查要点**

```text
分组加密Padding：在使用javax.crypto.Cipher类的Cipher.getInstance("AES/{ECB/CBC/PCBC}/XXXPadding")获取加解密对象时，未使用CMS-Padding 或ISO-Padding填充方式(PKCS5Padding、PKCS7Padding、ISO7816-4Padding)。注：GCM、OFB、CFB为流式加密不关注填充方式。
在使用java.security.Signature类的Signature.getInstance("XXXwithRSA")方法获取RSA签名对象时，未采用PSS填充方式。
```

**修复建议**

```text
根据业务选择正确的填充方式：
1.使用分组加密算法时，填充方式建议选择CMS-Padding或ISO-Padding。
2.使用RSA算法进行数字签名操作时，显式指定填充方式为PSS。
扩展资料：内部链接已省略
```

**正确示例**

```text
```
// 正确示例：选择AES加密算法的CBC模式，设置填充方式为PKCS5Padding，成功加密。
private byte[] goodPaddingMode(String plainText, byte[] secretKey, byte[] iv) throws NoSuchPaddingException,
NoSuchAlgorithmException, InvalidAlgorithmParameterException, InvalidKeyException, IllegalBlockSizeException,
BadPaddingException {
// Security.addProvider(new BouncyCastleProvider());
SecretKeySpec secretKeySpec = new SecretKeySpec(secretKey, "AES");
String transformation = "AES/CBC/PKCS5Padding";
IvParameterSpec ivParameterSpec = new IvParameterSpec(iv);
byte[] plainTextByte = plainText.getBytes(StandardCharsets.UTF_8);
// 选择填充方式为PKCS5Padding，还可以选择的填充方式有：PKCS7Padding（需引入BC库）、
// ISO7816-4Padding（需引入BC库）
Cipher cipher = Cipher.getInstance(transformation);
cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec, ivParameterSpec);
return cipher.doFinal(plainTextByte);
}
// 正确示例：使用RSA算法进行数字签名操作时，显式指定填充方式为PSS
private byte[] goodRsaSignaturePadding(String message) throws NoSuchAlgorithmException, InvalidKeySpecException,
InvalidAlgorithmParameterException, InvalidKeyException, SignatureException {
Security.addProvider(new BouncyCastleProvider());
String keyAlgorithm = "RSA";
KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance(keyAlgorithm);
keyPairGenerator.initialize(KEY_SIZE);
KeyPair keyPair = keyPairGenerator.generateKeyPair();
PKCS8EncodedKeySpec pkcs8EncodedKeySpec = new PKCS8EncodedKeySpec(keyPair.getPrivate().getEncoded());
KeyFactory keyFactory = KeyFactory.getInstance(keyAlgorithm);
PrivateKey generatePrivateKey = keyFactory.generatePrivate(pkcs8EncodedKeySpec);
String signatureAlgorithm = "SHA256withRSA/PSS";
// 显式指定填充方式为PSS
Signature signature = Signature.getInstance(signatureAlgorithm);
signature.setParameter(new PSSParameterSpec(MGF1ParameterSpec.SHA256.getDigestAlgorithm(), "MGF1",
MGF1ParameterSpec.SHA256, PSS_SALT_LENGTH, PSS_TRAILER_FIELD));
signature.initSign(generatePrivateKey);
signature.update(message.getBytes(StandardCharsets.UTF_8));
return signature.sign();
}
```
```

**错误示例**

```text
```
// 错误示例：选择AES加密算法的CBC分组模式，设置无填充方式，在明文长度不为16字节的整数倍时，加密失败。
private byte[] badPaddingMode(String plainText, byte[] secretKey, byte[] iv) throws NoSuchPaddingException,
NoSuchAlgorithmException, InvalidAlgorithmParameterException, InvalidKeyException, IllegalBlockSizeException,
BadPaddingException {
SecretKeySpec secretKeySpec = new SecretKeySpec(secretKey, "AES");
String transformation = "AES/CBC/NoPadding";
IvParameterSpec ivParameterSpec = new IvParameterSpec(iv);
byte[] plainTextByte = plainText.getBytes(StandardCharsets.UTF_8);
// 选择无填充方式
Cipher cipher = Cipher.getInstance(transformation);
cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec, ivParameterSpec);
return cipher.doFinal(plainTextByte);
}
// 错误示例：使用RSA算法进行数字签名操作时，未显式指定填充方式，默认填充方式为PKCS1Padding
private byte[] badRsaSignaturePadding(String message) throws NoSuchAlgorithmException, InvalidKeySpecException,
InvalidKeyException, SignatureException {
String keyAlgorithm = "RSA";
KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance(keyAlgorithm);
keyPairGenerator.initialize(KEY_SIZE);
KeyPair keyPair = keyPairGenerator.generateKeyPair();
PKCS8EncodedKeySpec pkcs8EncodedKeySpec = new PKCS8EncodedKeySpec(keyPair.getPrivate().getEncoded());
KeyFactory keyFactory = KeyFactory.getInstance(keyAlgorithm);
PrivateKey generatePrivateKey = keyFactory.generatePrivate(pkcs8EncodedKeySpec);
String signatureAlgorithm = "SHA256withRSA";
// 未显式指定填充方式，默认填充方式为PKCS1Padding，未采用PSS填充方式
Signature signature = Signature.getInstance(signatureAlgorithm);
signature.initSign(generatePrivateKey);
signature.update(message.getBytes(StandardCharsets.UTF_8));
return signature.sign();
}
```
```

**参考信息**

- 最新更新时间：2026-01-21 14:34:48

<a id="rule-287"></a>

### 287. SecH_GTS_JAVA_CBB_Check_Annotation_XXMapping

| 字段 | 内容 |
|---|---|
| 问题级别 | 提示 |
| 语言 | JAVA |
| 标签 | SecBrella |
| 关联工具规则 | SecH_GTS_JAVA_CBB_Check_Annotation_XXMapping |
| 规则类型 | 安全类（非编程规范） |
| 预估误报率 | 20% |

**审查要点**

不涉及，原因：GTS安全组件落地检测规则，用户侧不感知

**修复建议**

不涉及，原因：GTS安全组件落地检测规则，用户侧不感知

**正确示例**

不涉及，原因：GTS安全组件落地检测规则，用户侧不感知

**错误示例**

不涉及，原因：GTS安全组件落地检测规则，用户侧不感知

**参考信息**

- 最新更新时间：2026-01-21 11:50:03

<a id="rule-288"></a>

### 288. SecH_GTS_JAVA_CBB_Check_CSRF

| 字段 | 内容 |
|---|---|
| 问题级别 | 提示 |
| 语言 | JAVA |
| 标签 | SecBrella |
| 关联工具规则 | SecH_GTS_JAVA_CBB_Check_CSRF |
| 规则类型 | 安全类（非编程规范） |
| 预估误报率 | 20% |

**审查要点**

不涉及，原因：GTS安全组件落地检测规则，用户侧不感知

**修复建议**

不涉及，原因：GTS安全组件落地检测规则，用户侧不感知

**正确示例**

不涉及，原因：GTS安全组件落地检测规则，用户侧不感知

**错误示例**

不涉及，原因：GTS安全组件落地检测规则，用户侧不感知

**参考信息**

- 最新更新时间：2026-01-21 14:28:42

<a id="rule-289"></a>

### 289. SecH_GTS_JAVA_CBB_Check_CertCheck

| 字段 | 内容 |
|---|---|
| 问题级别 | 提示 |
| 语言 | JAVA |
| 标签 | SecBrella |
| 关联工具规则 | SecH_GTS_JAVA_CBB_Check_CertCheck |
| 规则类型 | 安全类（非编程规范） |
| 预估误报率 | 20% |

**审查要点**

不涉及，原因：GTS安全组件落地检测规则，用户侧不感知

**修复建议**

不涉及，原因：GTS安全组件落地检测规则，用户侧不感知

**正确示例**

不涉及，原因：GTS安全组件落地检测规则，用户侧不感知

**错误示例**

不涉及，原因：GTS安全组件落地检测规则，用户侧不感知

**参考信息**

- 最新更新时间：2026-01-21 11:54:47

<a id="rule-290"></a>

### 290. SecH_GTS_JAVA_CBB_Check_IntegrityCheck

| 字段 | 内容 |
|---|---|
| 问题级别 | 提示 |
| 语言 | JAVA |
| 标签 | SecBrella |
| 关联工具规则 | SecH_GTS_JAVA_CBB_Check_IntegrityCheck |
| 规则类型 | 安全类（非编程规范） |
| 预估误报率 | 20% |

**审查要点**

不涉及，原因：GTS安全组件落地检测规则，用户侧不感知

**修复建议**

不涉及，原因：GTS安全组件落地检测规则，用户侧不感知

**正确示例**

不涉及，原因：GTS安全组件落地检测规则，用户侧不感知

**错误示例**

不涉及，原因：GTS安全组件落地检测规则，用户侧不感知

**参考信息**

- 最新更新时间：2026-01-21 14:09:01

<a id="rule-291"></a>

### 291. SecH_GTS_JAVA_CBB_Check_LogAop

| 字段 | 内容 |
|---|---|
| 问题级别 | 提示 |
| 语言 | JAVA |
| 标签 | SecBrella |
| 关联工具规则 | SecH_GTS_JAVA_CBB_Check_LogAop |
| 规则类型 | 安全类（非编程规范） |
| 预估误报率 | 20% |

**审查要点**

不涉及，原因：GTS安全组件落地检测规则，用户侧不感知

**修复建议**

不涉及，原因：GTS安全组件落地检测规则，用户侧不感知

**正确示例**

不涉及，原因：GTS安全组件落地检测规则，用户侧不感知

**错误示例**

不涉及，原因：GTS安全组件落地检测规则，用户侧不感知

**参考信息**

- 最新更新时间：2026-01-21 14:15:32

<a id="rule-292"></a>

### 292. SecH_GTS_JAVA_CBB_Check_OpenRedirect

| 字段 | 内容 |
|---|---|
| 问题级别 | 提示 |
| 语言 | JAVA |
| 标签 | SecBrella |
| 关联工具规则 | SecH_GTS_JAVA_CBB_Check_OpenRedirect |
| 规则类型 | 安全类（非编程规范） |
| 预估误报率 | 20% |

**审查要点**

不涉及，原因：GTS安全组件落地检测规则，用户侧不感知

**修复建议**

不涉及，原因：GTS安全组件落地检测规则，用户侧不感知

**正确示例**

不涉及，原因：GTS安全组件落地检测规则，用户侧不感知

**错误示例**

不涉及，原因：GTS安全组件落地检测规则，用户侧不感知

**参考信息**

- 最新更新时间：2026-01-21 14:14:21

<a id="rule-293"></a>

### 293. SecH_GTS_JAVA_CBB_Check_PackageScan_Us

| 字段 | 内容 |
|---|---|
| 问题级别 | 提示 |
| 语言 | JAVA |
| 标签 | SecBrella |
| 关联工具规则 | SecH_GTS_JAVA_CBB_Check_PackageScan_Us |
| 规则类型 | 安全类（非编程规范） |
| 预估误报率 | 20% |

**审查要点**

不涉及，原因：GTS安全组件落地检测规则，用户侧不感知

**修复建议**

不涉及，原因：GTS安全组件落地检测规则，用户侧不感知

**正确示例**

不涉及，原因：GTS安全组件落地检测规则，用户侧不感知

**错误示例**

不涉及，原因：GTS安全组件落地检测规则，用户侧不感知

**参考信息**

- 最新更新时间：2026-01-21 15:04:15

<a id="rule-294"></a>

### 294. SecH_GTS_JAVA_CBB_Check_ParamCheck_Wsf

| 字段 | 内容 |
|---|---|
| 问题级别 | 提示 |
| 语言 | JAVA |
| 标签 | SecBrella |
| 关联工具规则 | SecH_GTS_JAVA_CBB_Check_ParamCheck_Wsf |
| 规则类型 | 安全类（非编程规范） |
| 预估误报率 | 20% |

**审查要点**

不涉及，原因：GTS安全组件落地检测规则，用户侧不感知

**修复建议**

不涉及，原因：GTS安全组件落地检测规则，用户侧不感知

**正确示例**

不涉及，原因：GTS安全组件落地检测规则，用户侧不感知

**错误示例**

不涉及，原因：GTS安全组件落地检测规则，用户侧不感知

**参考信息**

- 最新更新时间：2026-01-21 11:43:30

<a id="rule-295"></a>

### 295. SecH_GTS_JAVA_CBB_Check_PasswordComplexity

| 字段 | 内容 |
|---|---|
| 问题级别 | 提示 |
| 语言 | JAVA |
| 标签 | SecBrella |
| 关联工具规则 | SecH_GTS_JAVA_CBB_Check_PasswordComplexity |
| 规则类型 | 安全类（非编程规范） |
| 预估误报率 | 20% |

**审查要点**

不涉及，原因：GTS安全组件落地检测规则，用户侧不感知

**修复建议**

不涉及，原因：GTS安全组件落地检测规则，用户侧不感知

**正确示例**

不涉及，原因：GTS安全组件落地检测规则，用户侧不感知

**错误示例**

不涉及，原因：GTS安全组件落地检测规则，用户侧不感知

**参考信息**

- 最新更新时间：2026-01-21 14:16:18

<a id="rule-296"></a>

### 296. SecH_GTS_JAVA_CBB_Check_RiskDeserialization

| 字段 | 内容 |
|---|---|
| 问题级别 | 提示 |
| 语言 | JAVA |
| 标签 | SecBrella |
| 关联工具规则 | SecH_GTS_JAVA_CBB_Check_RiskDeserialization |
| 规则类型 | 安全类（非编程规范） |
| 预估误报率 | 20% |

**审查要点**

不涉及，原因：GTS安全组件落地检测规则，用户侧不感知

**修复建议**

不涉及，原因：GTS安全组件落地检测规则，用户侧不感知

**正确示例**

不涉及，原因：GTS安全组件落地检测规则，用户侧不感知

**错误示例**

不涉及，原因：GTS安全组件落地检测规则，用户侧不感知

**参考信息**

- 最新更新时间：2026-01-21 11:41:06

<a id="rule-297"></a>

### 297. SecH_GTS_JAVA_CBB_Check_RiskExpression

| 字段 | 内容 |
|---|---|
| 问题级别 | 提示 |
| 语言 | JAVA |
| 标签 | SecBrella |
| 关联工具规则 | SecH_GTS_JAVA_CBB_Check_RiskExpression |
| 规则类型 | 安全类（非编程规范） |
| 预估误报率 | 20% |

**审查要点**

不涉及，原因：GTS安全组件落地检测规则，用户侧不感知

**修复建议**

不涉及，原因：GTS安全组件落地检测规则，用户侧不感知

**正确示例**

不涉及，原因：GTS安全组件落地检测规则，用户侧不感知

**错误示例**

不涉及，原因：GTS安全组件落地检测规则，用户侧不感知

**参考信息**

- 最新更新时间：2026-01-21 14:14:02

<a id="rule-298"></a>

### 298. SecH_GTS_JAVA_CBB_Check_RiskFileUpload

| 字段 | 内容 |
|---|---|
| 问题级别 | 提示 |
| 语言 | JAVA |
| 标签 | SecBrella |
| 关联工具规则 | SecH_GTS_JAVA_CBB_Check_RiskFileUpload |
| 规则类型 | 安全类（非编程规范） |
| 预估误报率 | 20% |

**审查要点**

不涉及，原因：GTS安全组件落地检测规则，用户侧不感知

**修复建议**

不涉及，原因：GTS安全组件落地检测规则，用户侧不感知

**正确示例**

不涉及，原因：GTS安全组件落地检测规则，用户侧不感知

**错误示例**

不涉及，原因：GTS安全组件落地检测规则，用户侧不感知

**参考信息**

- 最新更新时间：2026-01-21 14:30:00

<a id="rule-299"></a>

### 299. SecH_GTS_JAVA_CBB_Check_RiskTLS

| 字段 | 内容 |
|---|---|
| 问题级别 | 提示 |
| 语言 | JAVA |
| 标签 | SecBrella |
| 关联工具规则 | SecH_GTS_JAVA_CBB_Check_RiskTLS |
| 规则类型 | 安全类（非编程规范） |
| 预估误报率 | 20% |

**审查要点**

不涉及，原因：GTS安全组件落地检测规则，用户侧不感知

**修复建议**

不涉及，原因：GTS安全组件落地检测规则，用户侧不感知

**正确示例**

不涉及，原因：GTS安全组件落地检测规则，用户侧不感知

**错误示例**

不涉及，原因：GTS安全组件落地检测规则，用户侧不感知

**参考信息**

- 最新更新时间：2026-01-21 14:16:51

<a id="rule-300"></a>

### 300. SecH_GTS_JAVA_CBB_Check_RiskXML

| 字段 | 内容 |
|---|---|
| 问题级别 | 提示 |
| 语言 | JAVA |
| 标签 | SecBrella |
| 关联工具规则 | SecH_GTS_JAVA_CBB_Check_RiskXML |
| 规则类型 | 安全类（非编程规范） |
| 预估误报率 | 20% |

**审查要点**

不涉及，原因：GTS安全组件落地检测规则，用户侧不感知

**修复建议**

不涉及，原因：GTS安全组件落地检测规则，用户侧不感知

**正确示例**

不涉及，原因：GTS安全组件落地检测规则，用户侧不感知

**错误示例**

不涉及，原因：GTS安全组件落地检测规则，用户侧不感知

**参考信息**

- 最新更新时间：2026-01-21 11:55:36

<a id="rule-301"></a>

### 301. SecH_GTS_JAVA_CBB_Check_SafeAPI

| 字段 | 内容 |
|---|---|
| 问题级别 | 提示 |
| 语言 | JAVA |
| 标签 | SecBrella |
| 关联工具规则 | SecH_GTS_JAVA_CBB_Check_SafeAPI |
| 规则类型 | 安全类（非编程规范） |
| 预估误报率 | 20% |

**审查要点**

不涉及，原因：GTS安全组件落地检测规则，用户侧不感知

**修复建议**

不涉及，原因：GTS安全组件落地检测规则，用户侧不感知

**正确示例**

不涉及，原因：GTS安全组件落地检测规则，用户侧不感知

**错误示例**

不涉及，原因：GTS安全组件落地检测规则，用户侧不感知

**参考信息**

- 最新更新时间：2026-01-21 14:08:41

<a id="rule-302"></a>

### 302. SecH_GTS_JAVA_CLOSE_RESOURCE

| 字段 | 内容 |
|---|---|
| 问题级别 | 严重 |
| 语言 | JAVA |
| 标签 | SecBrella |
| 关联工具规则 | SecH_GTS_JAVA_CLOSE_RESOURCE |
| 规则类型 | 安全类（非编程规范） |
| 预估误报率 | 50% |

**审查要点**

继承java.io.InputStream | java.io.OutputStream | java.io.Closeable | java.io. Reader | java.io.Writer的IO流，需要通过try-catch-finally或者try-with-resource 方式关闭，否则会告警

**修复建议**

创建自定义IO流后必须通过try-catch-finally或者try-with-resource 方式关闭

**正确示例**

```text
```
public static void readHdfsFile(String hdfsPath) throws Exception {
//文件路径的空判断
if (hdfsPath == null || hdfsPath.trim().length() == 0) {
throw new Exception("所要读取的源文件" + hdfsPath + ",不存在，请检查 !");
} //获取 conf 对应的 hdfs 集群的对象引用
FileSystem fs = FileSystem.get(conf);
//将给定的 hdfsPath 构建成一个 hdfs 的路径对象 Path
Path path = new Path(hdfsPath);
//字节转字符
FSDataInputStream fsdis = fs.open(path);
InputStreamReader isr = new InputStreamReader(fsdis);
BufferedReader br = new BufferedReader(isr);
String temp = null;
try {
while ((temp = br.readLine()) != null) {
System.out.println(temp);
}
} catch (IOException e) {
e.printStackTrace();
} finally {
fs.close();
br.close();
}
}
```
```

**错误示例**

```text
```
public static void readHdfsFile(String hdfsPath) throws Exception {
//文件路径的空判断
if (hdfsPath == null || hdfsPath.trim().length() == 0)
{
throw new Exception("所要读取的源文件" + hdfsPath + ",不存在，请检查!");
}
//获取 conf 对应的 hdfs 集群的对象引用
FileSystem fs = FileSystem.get(conf);
//将给定的 hdfsPath 构建成一个 hdfs 的路径对象 Path
Path path = new Path(hdfsPath);
//字节转字符
FSDataInputStream fsdis = fs.open(path);
InputStreamReader isr = new InputStreamReader(fsdis);
BufferedReader br = new BufferedReader(isr);
String temp = null;
try
{
while ((temp = br.readLine()) != null)
{
System.out.println(temp);
}
}
catch (IOException e)
{
e.printStackTrace();
}finally
{
br.close();
}
}
// FileSystem的fs未关闭，因此产生告警
```
```

**参考信息**

- 最新更新时间：2026-01-21 11:31:16

<a id="rule-303"></a>

### 303. SecH_GTS_JAVA_Cert_PassCheck

| 字段 | 内容 |
|---|---|
| 问题级别 | 严重 |
| 语言 | JAVA |
| 标签 | SecBrella |
| 关联工具规则 | SecH_GTS_JAVA_Cert_PassCheck |
| 规则类型 | 安全类（非编程规范） |
| 预估误报率 | 20% |

**审查要点**

```text
证书可能绕过校验，自定义X509TrustManager类函数实现，服务端证书不校验，getAcceptedIssuers 返回值null，或者checkClientTrusted、checkServerTrusted方法体为空
【误报提示】引擎适配jdk21存在问题，项目是jdk21时，该规则会误报
```

**修复建议**

对证书进行必要的有效性校验，使用GDE USecurity 证书认证API：内部链接已省略

**正确示例**

```text
```
/**
* 调用测试类
*
* @param args args
* @return void
* @throws KeyStoreException KeyStoreException
* @remark: created by 王小龙/xwx834200 at 2020/7/7
*/
public static void testRule(String[] args) throws KeyStoreException {
KeyStore keyStore = KeyStore.getInstance("qnikcleabhjkm");
HttpsTrustManager httpsTrustManager = new HttpsTrustManager(keyStore);
}
/**
* 功能描述:https证书信任keystore管理。
*
* @author l00251877
* @since 2019-12-27
*/
@Slf4j
public class HttpsTrustManager implements X509TrustManager {
/**
* 证书管理
*/
private X509TrustManager sunX509TrustManager = null;
/**
* 传入keystore对象。
*
* @param keyStore keystore对象
*/
public HttpsTrustManager(KeyStore keyStore) {
if (keyStore == null) {
return;
}
try {
TrustManagerFactory trustManagerFactory = TrustManagerFactory.getInstance("SunX509", "SunJSSE");
trustManagerFactory.init(keyStore);
TrustManager[] tms = trustManagerFactory.getTrustManagers();
for (TrustManager tm : tms) {
if (tm instanceof X509TrustManager) {
sunX509TrustManager = (X509TrustManager) tm;
return;
}
}
} catch (NoSuchAlgorithmException e) {
log.error("no such algorithm.");
} catch (KeyStoreException e) {
log.error("key store exception.");
} catch (NoSuchProviderException e) {
log.error("no such provider exception.");
}
}
@Override
public void checkClientTrusted(X509Certificate[] x509Certificates, String s) throws CertificateException {
if (sunX509TrustManager == null) {
return;
}
sunX509TrustManager.checkClientTrusted(x509Certificates, s);
}
@Override
public void checkServerTrusted(X509Certificate[] x509Certificates, String s) throws CertificateException {
if (sunX509TrustManager == null) {
return;
}
sunX509TrustManager.checkServerTrusted(x509Certificates, s);
}
@Override
public X509Certificate[] getAcceptedIssuers() {
if (sunX509TrustManager == null) {
return new X509Certificate[0];
}
return sunX509TrustManager.getAcceptedIssuers();
}
}
```
```

**错误示例**

```text
```
/**
* 调用测试类
*
* @param args args
* @return void
*/
public static void testRuleTwo(String[] args){
KeyStore keyStore = null;
HttpsTrustManager httpsTrustManager = new HttpsTrustManager(keyStore);
}
@Slf4j
public class HttpsTrustManagerTwo implements X509TrustManager {
@Override
public void checkClientTrusted(X509Certificate[] x509Certificates, String s) throws CertificateException {
}
@Override
public void checkServerTrusted(X509Certificate[] x509Certificates, String s) throws CertificateException {
}
@Override
public X509Certificate[] getAcceptedIssuers() {
return null;
}
}
```
```

**参考信息**

- 最新更新时间：2026-01-21 14:11:05

<a id="rule-304"></a>

### 304. SecH_GTS_JAVA_Crypted_NoSalt

| 字段 | 内容 |
|---|---|
| 问题级别 | 严重 |
| 语言 | JAVA |
| 标签 | SecBrella |
| 关联工具规则 | SecH_GTS_JAVA_Crypted_NoSalt |
| 规则类型 | 安全类（非编程规范） |
| 预估误报率 | 50% |

**审查要点**

```text
根据公司安全要求，在密码存储、密钥派生等安全敏感场景中对信息进行HASH处理时，必须加盐值，盐值必须为安全随机数且盐值不为固定值。
【误报提示】用户口令加密时存在盐值加扰，用户校验时需要取原始盐值进行还原校验，此处盐值来自于存储介质(数据库)，此场景为常见误报，暂时无法统一消除，开发人员应严格排查初始加密时使用盐值是否为安全随机数产生且长度大于等于16字节，然后屏蔽此问题。
```

**修复建议**

```text
在密码存储、密钥派生等安全敏感场景中对信息进行HASH处理时，必须加盐值，盐值必须为安全随机数且盐值不为固定值。
安全随机数：
SecureRandom secureRandom = SecureRandom.getInstanceStrong(); // 特定情况下可能阻塞
SecureRandom secureRandom = SecureRandom.getInstance("DRBG", ...); // 如SecureRandom.getInstance("DRBG", DrbgParameters.instantiation(128, RESEED_ONLY, null)) // JDK9 以后
SecureRandom secureRandom = com.example.us.common.random.UsSecureRandom.getInstance();
byte[] salt = com.example.wsf.core.HWRandomizer.getRandomBytes(SALT_LEN);
```

**正确示例**

```text
```
/**
* Sha2Crypt加密测试,盐值为安全随机数（不告警）
*
* @param password 密码
* @throws IOException
* @throws NoSuchAlgorithmException
* @since 2021-01-27
*/
public void goodCase(byte[] password) throws IOException, NoSuchAlgorithmException {
SecureRandom random = SecureRandom.getInstanceStrong();
byte[] salt = new byte[16];
random.nextBytes(salt);
Sha2Crypt.sha256Crypt(password, salt.toString());
}
```
```

**错误示例**

```text
```
/**
* Sha2Crypt加密测试,盐值硬编码（告警）
*
* @param password 密码
* @throws IOException
* @since 2021-01-27
*/
public void sha2CryptTest(byte[] password) throws IOException {
String salt = "123456";
Sha2Crypt.sha256Crypt(msg, salt);
String passwordWithSalt = password.toString() + salt;
Sha2Crypt.sha512Crypt(passwordWithSalt.getBytes("utf-8"));
}
```
```

**参考信息**

- 最新更新时间：2025-05-30 09:47:11

<a id="rule-305"></a>

### 305. SecH_GTS_JAVA_Csv_Injection

| 字段 | 内容 |
|---|---|
| 问题级别 | 严重 |
| 语言 | JAVA |
| 标签 | SecBrella |
| 关联工具规则 | SecH_GTS_JAVA_Csv_Injection |
| 规则类型 | 安全类（非编程规范） |
| 预估误报率 | 50% |

**审查要点**

使用CsvWriter写CSV文件，如果内容来自http请求的参数或者头，如果字符串前有增加单引号不告警。

**修复建议**

如果告警可以将csv文件写入单元格时加上单引号

**正确示例**

```text
```
【修复方式一】
public void writeCSV(HttpServletRequest request) {
String city = request.getParameter("city");
CsvWriter csvWriter = new CsvWriter(" / usr / soft / xxx",',', Charset.forName("GBK"));
String[] content = {"\"\t" + city + "\""};
try {
csvWriter.writeRecord(content);
} catch (IOException e) {
System.out.println("IOException");
} finally {
csvWriter.close();
}
}
【修复方式二】
public void writeCSV(HttpServletRequest request) {
String city = request.getParameter("city");
CsvWriter csvWriter = new CsvWriter(" / usr / soft / xxx", ',', Charset.forName("GBK"));
String[] content = {city};
try {
content = valid(content);
csvWriter.writeRecord(content);
} catch (IOException e) {
System.out.println("IOException");
} finally {
csvWriter.close();
}
}
public static String[] valid(String input) {
String[] outPut = null; //请自行编写处理逻辑
return outPut;
}
```
```

**错误示例**

```text
```
public void writeCSV(HttpServletRequest request) {
String city = request.getParameter("city");
CsvWriter csvWriter = new CsvWriter("/usr/soft/xxx", ',', Charset.forName("GBK"));
String[] content = {city};
try {
csvWriter.writeRecord(content);
} catch (IOException e) {
System.out.println("IOException");
} finally {
csvWriter.close();
}
}
```
```

**参考信息**

- 最新更新时间：2025-01-21 14:29:39

<a id="rule-306"></a>

### 306. SecH_GTS_JAVA_Deserialization_FastJson_autoType_safeMode

| 字段 | 内容 |
|---|---|
| 问题级别 | 严重 |
| 语言 | JAVA |
| 标签 | SecBrella |
| 关联工具规则 | SecH_GTS_JAVA_Deserialization_FastJson_autoType_safeMode |
| 规则类型 | 安全类（非编程规范） |
| 预估误报率 | 20% |

**审查要点**

FastJson时,在配置文件中显式开启autoType或显式关闭safemode。或者在parse或parseObject方法中显式配置Feature.SupportAutoType。

**修复建议**

```text
首先需要产品升级到FastJson新版本，然后检查是否存在错误示例中的三种场景，如果有使用则去掉。然后使用正确示例中添加白、黑名单。
使用US的安全反序列化工具 内部链接已省略
```

**正确示例**

```text
```
在全局关闭autotype的情况下，若希望某个点可以进行解析@type，可使用：
1、ParserConfig.getGlobalInstance().addAccept开启接收的白名单，参数为类全路径名。
ParserConfig.getGlobalInstance().setAutoTypeSupport(false);//autotype默认关闭
ParserConfig.getGlobalInstance().addAccept("com.example.FastJsonTest.User");
User user2 = (User)JSON.parse(serializedStr1);
仅需要确保addAccept白名单类的安全性即可。
注意：
l 在关闭autotype情况下，FastJson组件内置的黑名单是优先于此白名单的，所以黑名单中的类是无法使用 @type能力的，如果发现某个类已经通过上述函数设置为白名单，但是依然无法操作，则可能被加入FastJson内部的黑名单 （推荐此方法，关闭autotype，并addAccept开启需要的类反序列化）
l 开启autotype的情况下，则此白名单优先级是高于内置黑名单的。
白名单设置还有以下两种形式：
a、 加上JVM启动参数：
-Dfastjson.parser.autoTypeAccept=com.XXX.XXX,com.YYY。如果有多个包名前缀，用逗号隔开
b、通过fastjson.properties文件配置：
fastjson.parser.autoTypeAccept=com.XXX.XXX,com.cainiao。果有多个包名前缀，用逗号隔开
注：也提供了addDeny函数支持黑名单。黑白名单机制的选择以及名单列表由各产品自行根据实际情况设计决策
2、 或者通过JSON.parseObject(serializedStr1,User.class)，指定User可以解析autotype。只要保证User.class安全就可以了。即使autotype关闭，下述方法也会强制开启该类的反序列化，类似白名单。必须指定特定类，例如使用父类方序列化也会失败。
ParserConfig.getGlobalInstance().setAutoTypeSupport(false);//autotype默认关闭
Object obj = JSON.parseObject(serializedStr1,User.class);
若待发序列化字符串是其他类，会抛出如下异常：
Exception in thread "main" com.alibaba.fastjson.JSONException: type not match. com.example.FastJsonTest.User -> com.example.HessianTest.Person
```
```

**错误示例**

```text
```
由于解析@type属性会自动调用目标对象的构造函数、set、get方法，若这些方法存在可利用的代码，则非常有风险。所以需要禁止@type的解析和使用。其实FastJson是默认关闭autotype的，所以若不需要使用autotype能力时，查看是否通过以下方式被恶意打开：
1、 不允许在JSON.parse和JSON.parseObject设置属性Feature.SupportAutoType。例如：
JSON.parse(jsonString, Feature.SupportAutoType);
2、不要调用ParserConfig.getGlobalInstance().setAutoTypeSupport(true);，这样全局打开了autotype能力
3、不要指定-Dfastjson.parser.autoTypeSupport=true；这是JVM启动参数，也能够全局打开。（不在安全编码的范畴类，为保持完整性在此顺带提出。）
```
```

**参考信息**

- 最新更新时间：2025-04-17 11:13:14

<a id="rule-307"></a>

### 307. SecH_GTS_JAVA_Deserialization_Kryo

| 字段 | 内容 |
|---|---|
| 问题级别 | 严重 |
| 语言 | JAVA |
| 标签 | SecBrella |
| 关联工具规则 | SecH_GTS_JAVA_Deserialization_Kryo |
| 规则类型 | 安全类（非编程规范） |
| 预估误报率 | 50% |

**审查要点**

kryo反序列化漏洞检测

**修复建议**

```text
使用安全类SecureKryo进行反序列化
推荐使用US组件 内部链接已省略
```

**正确示例**

```text
```
/**
* 使用自定义的安全类SecureKryo进行反序列化，不应告警
* 该main方法可执行并复现漏洞修复后效果
*
* @param args args
* @return void void
* @remark: created by 2020/10/28
*/
public static void main(String[] args) throws FileNotFoundException {
SecureKryo kryo = new SecureKryo();
try (Input input = new Input(new FileInputStream("D:\\payload.bin"));){
Simple result = (Simple) kryo.readClassAndObject(input);
System.out.print(result);
}
}
```
```

**错误示例**

```text
```
/**
* 使用kryo.setRegistrationRequired(false)，应告警
*
* @remark: created by 2020/10/28
*/
public void serializableObject() {
Kryo kryo = new Kryo();
try (Output output = new Output(new FileOutputStream("D:/file1.bin"))) {
Simple s = new Simple();
s.age = 1;
s.name = "Tom";
kryo.setRegistrationRequired(false);
kryo.writeClassAndObject(output, s);
} catch (FileNotFoundException e) {
e.printStackTrace();
}
}
```
```

**参考信息**

- 最新更新时间：2024-11-28 10:43:20

<a id="rule-308"></a>

### 308. SecH_GTS_JAVA_Deserialization_YamlBeans

| 字段 | 内容 |
|---|---|
| 问题级别 | 严重 |
| 语言 | JAVA |
| 标签 | SecBrella |
| 关联工具规则 | SecH_GTS_JAVA_Deserialization_YamlBeans |
| 规则类型 | 安全类（非编程规范） |
| 预估误报率 | 20% |

**审查要点**

在使用com.esotericsoftware.yamlbeans.YamlReader进行反序列化时，不可直接将输入内容作为参数进行操作，应该先创建其子类，然后重写findTagClass方法进行白名单校验后，创建子类对象后进行反序列化操作即可。

**修复建议**

```text
```
进行白名单校验，参考：
public class HWSecureYamlReader extends YamlReader {
private static Set<String> ValidList = new HashSet<>();
static {
//增加需要反序列化的自定义类
ValidList.add("com.example.YamlbeansTest.Contact");
}
public HWSecureYamlReader(Reader reader) {
super(reader);
}
public HWSecureYamlReader(Reader reader, YamlConfig config) {
super(reader, config);
}
public HWSecureYamlReader(String yaml) {
super(yaml);
}
public HWSecureYamlReader(String yaml, YamlConfig config) {
super(yaml, config);
}
@Override
protected Class<?> findTagClass(String tag, ClassLoader classLoader) throws ClassNotFoundException, {
//白名单校验 不在白名单类中返回null 上层会抛出YamlReaderException异常
if (ValidList.contains(tag)){
return super.findTagClass(tag, classLoader);
}
return null;//若只需要处理基本类型，该函数直接返回null即可
}
}
推荐使用US组件 内部链接已省略
```
```

**正确示例**

```text
```
添加YamlReader的子类HWSecureYamlReader（类名可以自定义），重写findTagClass方法，进行白名单校验。
@Override
protected Class<?> findTagClass(String tag, ClassLoader classLoader) throws ClassNotFoundException, {
//白名单校验 不在白名单类中返回null 上层会抛出YamlReaderException异常
if (ValidList.contains(tag)){
return super.findTagClass(tag, classLoader);
}
return null;//若只需要处理基本类型，该函数直接返回null即可
}
```
```

**错误示例**

```text
```
testreader = new YamlReader(new FileReader("D:\\POC.yml"));
testreader.read();
```
```

**参考信息**

- 最新更新时间：2024-11-28 15:03:00

<a id="rule-309"></a>

### 309. SecH_GTS_JAVA_Deserialize_Hessian

| 字段 | 内容 |
|---|---|
| 问题级别 | 严重 |
| 语言 | JAVA |
| 标签 | SecBrella |
| 关联工具规则 | SecH_GTS_JAVA_Deserialize_Hessian |
| 规则类型 | 安全类（非编程规范） |
| 预估误报率 | 50% |

**审查要点**

```text
Hessian反序列化漏洞检查，对使用readObject()方法告警，且该方法的父类是com.caucho.hessian.io.HessianInput。
对使用setSerializerFactory()方法减除告警，且入参的类继承SerializerFactory，且重写了readMap()方法
```

**修复建议**

```text
使用正确场景进行Hessian反序列化，安全编码指南：内部链接已省略
使用GDE USecurity 安全反序列化工具组件：内部链接已省略
```

**正确示例**

```text
```
/**
* 使用自定义安全白名单校验反序列化类，不应告警
* 该main方法可执行并复现漏洞修复后效果
*
* @param args args
* @return void void
* @remark: created by 王小龙/xwx834200 at 2020/10/28
*/
public static void main(String[] args) {
TestResolve tr = new TestResolve();
File fl = new File("test_tr");
FileOutputStream fos = null;
FileInputStream fis = null;
// 序列化过程
try {
fos = new FileOutputStream(fl);
HessianOutput output = new HessianOutput(fos);
output.writeObject(tr);
} catch (IOException e) {
LOGGER.error("IOException");
} finally {
IOUtils.closeQuietly(fos);
}
// 反序列化过程
try {
fis = new FileInputStream(fl);
HessianInput input = new HessianInput(fis);
SecureSerializerFactory myFactory = new SecureSerializerFactory();
input.setSerializerFactory(myFactory); // good
input.readObject();
} catch (IOException e) {
LOGGER.error("IOException");
} finally {
IOUtils.closeQuietly(fis);
}
}
```
```

**错误示例**

```text
```
/**
* 使用自定义安全白名单校验反序列化类，没有重写readMap方法，应告警
* 该main方法可执行并复现漏洞修复后效果
*
* @param args args
* @return void void
* @remark: created by 王小龙/xwx834200 at 2020/10/28
*/
public static void main(String[] args) {
TestResolve tr = new TestResolve();
File fl = new File("test_tr");
FileOutputStream fos = null;
FileInputStream fis = null;
// 序列化过程
try {
fos = new FileOutputStream(fl);
HessianOutput output = new HessianOutput();
output.writeObject(tr);
} catch (IOException e) {
LOGGER.error("IOException");
} finally {
IOUtils.closeQuietly(fos);
}
// 反序列化过程
try {
fis = new FileInputStream(fl);
HessianInput input = new HessianInput();
SecureSerializerFactoryTwo myFactory = new SecureSerializerFactoryTwo();
input.setSerializerFactory(myFactory); // bad
input.readObject();
} catch (IOException e) {
LOGGER.error("IOException");
} finally {
IOUtils.closeQuietly(fis);
}
}
```
```

**参考信息**

- 最新更新时间：2024-11-28 14:34:09

<a id="rule-310"></a>

### 310. SecH_GTS_JAVA_Deserialize_SnakeYaml

| 字段 | 内容 |
|---|---|
| 问题级别 | 严重 |
| 语言 | JAVA |
| 标签 | SecBrella |
| 关联工具规则 | SecH_GTS_JAVA_Deserialize_SnakeYaml |
| 规则类型 | 安全类（非编程规范） |
| 预估误报率 | 20% |

**审查要点**

SnakeYaml反序列化时可以通过!!+全类名指定反序列化的类，反序列化过程中会实例化该类，可以通过构造ScriptEngineManagerpayload并利用SPI机制通过URLClassLoader或者其他payload如JNDI方式远程加载实例化恶意类从而实现任意代码执行。

**修复建议**

```text
创建构造函数时，请采用安全的构造器
推荐使用US组件 内部链接已省略
```

**正确示例**

```text
```
// 正确用例1:若业务只需要支持基础数据类型（如字符串、列表等）的一般场景，通过SafeConstructor构造Yaml对象
public void good001() {
String deserializationStr = DataGenerateFactory.getUnTrustStr();
// SafeConstructor限制了仅能反序列化基本的类型，例如整型、bool、TIMESTAMP等等。详细请查看SafeConstructor源码支持的类型
Yaml yaml = new Yaml(new SafeConstructor(new LoaderOptions())); // 兼容snakeyaml高版本没有无参构造的情况
yaml.load(deserializationStr);
// yaml.loadAs(deserializationStr, YamlTestBean.class);
// ......
}
// 正确用例2:若业务需要支持SafeConstructor以外的类型，可自定义安全的Constructor，通过白名单限制可反序列化的类型列表
public void good002() {
// String deserializationStr = getEvilSnakeYamlStr2();
String deserializationStr = DataGenerateFactory.getUnTrustStr();
// 自定义的SnakeYamlSecureConstructor内部通过白名单类列表进行校验
Yaml yaml = new Yaml(new SnakeYamlSecureConstructor());
yaml.load(deserializationStr);
// yaml.loadAs(deserializationStr, YamlTestBean.class);
// ......
}
```
```

**错误示例**

```text
```
Yaml yaml = new Yaml();
Object yamlObject = yaml.load(poc);
```
```

**参考信息**

- 最新更新时间：2025-05-30 14:23:14

<a id="rule-311"></a>

### 311. SecH_GTS_JAVA_电子表格_Injection

| 字段 | 内容 |
|---|---|
| 问题级别 | 严重 |
| 语言 | JAVA |
| 标签 | SecBrella |
| 关联工具规则 | SecH_GTS_JAVA_电子表格_Injection |
| 规则类型 | 安全类（非编程规范） |
| 预估误报率 | 50% |

**审查要点**

```text
使用POI写excel文件，如果内容来自http请求的参数或者头，报警，如果字符串前有增加单引号，或者设置excel单元格为通用格式。
如果告警可以将excel单元格设置为通用格式，使用setCellType方法，或者给setCellValue的入参增加单引号
```

**修复建议**

建议在使用setCellValue之前设置cell格式为字符串setCellType(CellType.STRING)

**正确示例**

```text
```
//【修复方式一】
public void export电子表格(HttpServletRequest request) {
String citycode = request.getParameter("citycode");
HSSFWorkbook hssfWorkbook = new HSSFWorkbook();
HSSFSheet sheet = hssfWorkbook.createSheet();
int lastRowNum = sheet.getLastRowNum();
HSSFRow dataRow = sheet.createRow(lastRowNum + 1);
HSSFCell cell = dataRow.createCell(0);
cell.setCellType(CellType.STRING);
cell.setCellValue(citycode);
}
//【修复建议】在使用setCellValue之前设置cell格式为字符串setCellType(CellType.STRING)
//【修复方式二】
public void export电子表格(HttpServletRequest request) {
String citycode = request.getParameter("citycode");
HSSFWorkbook hssfWorkbook = new HSSFWorkbook();
HSSFSheet sheet = hssfWorkbook.createSheet();
int lastRowNum = sheet.getLastRowNum();
HSSFRow dataRow = sheet.createRow(lastRowNum + 1);
HSSFCell cell = dataRow.createCell(0);
cell.setCellValue(valid(citycode));
}
public static String valid(String citycode) {
String output = null;
//请自行编写校验逻辑
return output;
}
//【修复建议】在使用setCellValue之前 校验写入内容
```
```

**错误示例**

```text
```
String citycode = request.getParameter("citycode");
HSSFWorkbook hssfWorkbook = new HSSFWorkbook();
HSSFSheet sheet = hssfWorkbook.createSheet();
int lastRowNum = sheet.getLastRowNum();
HSSFRow dataRow = sheet.createRow(lastRowNum + 1);
dataRow.createCell(0).setCellValue(citycode);
```
```

**参考信息**

- 最新更新时间：2025-01-26 17:15:54

<a id="rule-312"></a>

### 312. SecH_GTS_JAVA_电子表格_Workbook_Stream_Release

| 字段 | 内容 |
|---|---|
| 问题级别 | 严重 |
| 语言 | JAVA |
| 标签 | SecBrella |
| 关联工具规则 | SecH_GTS_JAVA_电子表格_Workbook_Stream_Release |
| 规则类型 | 安全类（非编程规范） |
| 预估误报率 | 50% |

**审查要点**

POI操作电子表格相关的Workbook流对象未得到关闭，可能导致资源泄露等问题。包括XSSFWorkbook、SXSSFWorkbook、HSSFWorkbook类。

**修复建议**

```text
1、使用try-with-resources。
2、在没有使用try-with-resources的情况下，在finally里关闭。
```

**正确示例**

```text
```
// 正确用例：所有相关流都放在try-with-resource中
public static void good001(FileOutputStream outputStream) throws IOException, BusinessException {
try(Workbook workbook = new SXSSFWorkbook()) {
workbook.write(outputStream);
BusinessMock.doBusinessWithException(workbook, outputStream);
}
}
```
```

**错误示例**

```text
```
// 错误用例：close代码未写在finally块中，导致中间代码可能发生异常返回时无法释放HSSFWorkbook资源
public static void bad00303(FileOutputStream outputStream, boolean isTrue) throws IOException, BusinessException {
Workbook workbook = new HSSFWorkbook();
workbook.write(outputStream);
if (isTrue) {
BusinessMock.doBusinessWithoutException(workbook);
workbook.close();
} else {
BusinessMock.doBusinessWithException(workbook); // 该函数抛出异常后，将导致后续close未被执行，产生流资源泄露问题
workbook.close();
}
}
```
```

**参考信息**

- 最新更新时间：2026-01-21 14:19:44

<a id="rule-313"></a>

### 313. SecH_GTS_JAVA_Executable_Script_Check

| 字段 | 内容 |
|---|---|
| 问题级别 | 严重 |
| 语言 | JAVA |
| 标签 | SecBrella |
| 关联工具规则 | SecH_GTS_JAVA_Executable_Script_Check |
| 规则类型 | 安全类（非编程规范） |
| 预估误报率 | 50% |

**审查要点**

检测new File时检测传入文件名后缀是否为可执行脚本即.sh和.py

**修复建议**

```text
1、尽量不要动态生成脚本文件
2、一般动态生成脚本文件，都需要根据外部输入生成脚本内容，对外部输入严格校验，如果已严格校验，可以屏蔽告警
```

**正确示例**

```text
1、如果需要动态创建脚本，对外部输入进行严格校验
2、尽量不要动态创建脚本文件
```

**错误示例**

```text
```
File file = new File（"script.sh"）;
String param = ...; // 外部输入数据
String code = "mkdir " + param ;
FileWriter writer = new FileWriter(file);
writer.write(code);
writer.close();
```
```

**参考信息**

- 最新更新时间：2026-01-21 11:45:42

<a id="rule-314"></a>

### 314. SecH_GTS_JAVA_FileChannel_Resource_Release

| 字段 | 内容 |
|---|---|
| 问题级别 | 严重 |
| 语言 | JAVA |
| 标签 | SecBrella |
| 关联工具规则 | SecH_GTS_JAVA_FileChannel_Resource_Release |
| 规则类型 | 安全类（非编程规范） |
| 预估误报率 | 50% |

**审查要点**

FileChannel对象未得到正确关闭，可能导致资源泄露等问题。

**修复建议**

```text
1、使用try-with-resources。
2、在没有使用try-with-resources的情况下，在finally里关闭。
```

**正确示例**

```text
```
// 正确用例：通过try-with-resource实现FileChannel的释放回收
public static void good000(Path path) throws IOException {
try (FileChannel channel = FileChannel.open(path, StandardOpenOption.READ)) {
BusinessMock.doBusinessWithoutException(channel);
}
}
```
```

**错误示例**

```text
```
// 错误用例：close代码未写在finally块中，导致中间代码可能发生异常返回时无法释放FileChannel对象
public static void bad003(Path path, boolean isTrue) throws IOException, BusinessException {
FileChannel channel = FileChannel.open(path, StandardOpenOption.READ);
if (isTrue) {
BusinessMock.doBusinessWithoutException(channel);
channel.close();
} else {
BusinessMock.doBusinessWithException(channel); // 该函数抛出异常后，将导致后续close未被执行，产生流资源泄露问题
channel.close();
}
}
```
```

**参考信息**

- 最新更新时间：2026-01-21 11:37:57

<a id="rule-315"></a>

### 315. SecH_GTS_JAVA_Gzip_Over_Size

| 字段 | 内容 |
|---|---|
| 问题级别 | 一般 |
| 语言 | JAVA |
| 标签 | SecBrella |
| 关联工具规则 | SecH_GTS_JAVA_Gzip_Over_Size |
| 规则类型 | 安全类（非编程规范） |
| 预估误报率 | 50% |

**审查要点**

```text
为防止ZIP炸弹攻击，我们在读取ZIP包并进行解压前，需要进行压缩包大小校验。
支持框架：org.apache.tools.zip.ZipFile、java.util.zip.ZipFile、
org.apache.commons.compress.archivers.zip.ZipFile、
java.util.zip.GZIPInputStream|ZipInputStream、
org.apache.commons.compress.archivers. ArchiveInputStream、
org.apache.commons.compress compressors.gzip.GzipCompressorInputStream等方法读取ZIP文件流。
【误报提示】由于校验方法及位置多样，规则未能识别所有的情况，如果确定已进行校验，则建议屏蔽。
```

**修复建议**

```text
解压缩过程中，应该限制文件大小，包括单个文件的大小和整个压缩包的大小，避免磁盘占满引发DoS攻击
应该在while循环中边读边统计实际解压出的文件总大小，如果达到指定的阈值（MAX_TOTAL_FILE_SIZE），终止解压操作；
同时，统计解压出来的文件的数量，数量非常多的小文件一样会引发磁盘问题，如果达到指定阈值（MAX_FILE_COUNT），终止解压操作，请参考正确示例。
说明：在统计解压文件的大小时，不应该使用entry.getSize() 来统计文件大小， entry.getSize() 的文件大小可被恶意篡改，不能表示真实文件大小。
```

**正确示例**

```text
```
private static final long MAX_FILE_COUNT = 100L;
private static final long MAX_TOTAL_FILE_SIZE = 1024L * 1024L;
public void unzip(FileInputStream zipFileInputStream, String dir) throws IOException {
long fileCount = 0;
long totalFileSize = 0;
try (ZipInputStream zis = new ZipInputStream(zipFileInputStream)) {
ZipEntry entry;
String entryName;
String entryFilePath;
File entryFile;
byte[] buf = new byte[10240];
int length;
while ((entry = zis.getNextEntry()) != null) {
entryName = entry.getName();
entryFilePath = sanitizeFileName(entryName, dir);
entryFile = new File(entryFilePath);
if (entry.isDirectory()) {
creatDir(entryFile);
continue;
}
fileCount++;
if (fileCount > MAX_FILE_COUNT) {
throw new IOException("The ZIP package contains too many files.");
}
try (FileOutputStream fos = new FileOutputStream(entryFile)) {
while ((length = zis.read(buf)) != -1) {
totalFileSize += length;
if (totalFileSize > MAX_TOTAL_FILE_SIZE) {
throw new IOException("Zip Bomb! File size is too large. ...");
}
fos.write(buf, 0, length);
}
}
}
}
}
private String sanitizeFileName(String fileName, String dir) throws IOException {
File file = new File(dir, fileName);
String canonicalPath = file.getCanonicalPath();
if (canonicalPath.startsWith(dir)) {
return canonicalPath;
}
throw new IOException("Path Traversal vulnerability: ...");
}
private void creatDir(File dirPath) throws IOException {
boolean result = dirPath.mkdirs();
if (!result) {
throw new IOException("Create dir failed, path is : " + dirPath.getPath());
}
}
```
```

**错误示例**

```text
```
public void unzip(String fileName, String dir) throws IOException {
try (FileInputStream fis = new FileInputStream(fileName);
ZipInputStream zis = new ZipInputStream(fis)) {
ZipEntry entry;
File tempFile;
byte[] buf = new byte[10240];
int length;
while ((entry = zis.getNextEntry()) != null) {
tempFile = new File(dir, entry.getName());
if (entry.isDirectory()) {
tempFile.mkdirs();
continue;
}
try (FileOutputStream fos = new FileOutputStream(tempFile)) {
while ((length = zis.read(buf)) != -1) {
fos.write(buf, 0, length);
}
}
}
}
}
上述示例中，未对解压文件的操作做任何安全校验防护。
```
```

**参考信息**

- 最新更新时间：2026-01-29 10:08:48

<a id="rule-316"></a>

### 316. SecH_GTS_JAVA_HDFS_File_Stream_Release

| 字段 | 内容 |
|---|---|
| 问题级别 | 严重 |
| 语言 | JAVA |
| 标签 | SecBrella |
| 关联工具规则 | SecH_GTS_JAVA_HDFS_File_Stream_Release |
| 规则类型 | 安全类（非编程规范） |
| 预估误报率 | 50% |

**审查要点**

HDFS相关的文件输入输出Stream流对象未得到正确关闭，可能导致资源泄露等问题。常包括：FSDataInputStream/FSDataOutputStream。

**修复建议**

```text
1、使用try-with-resources。
2、在没有使用try-with-resources的情况下，在finally里关闭。
```

**正确示例**

```text
```
// 正确用例：所有相关流都放在try-with-resource中
public static void good001(FileSystem fileSystem, String hdfsPath) {
try (FSDataInputStream fsDataInputStream = fileSystem.open(new Path(hdfsPath));
InputStreamReader inputStreamReader = new InputStreamReader(fsDataInputStream, Charset.defaultCharset())) {
BusinessMock.doBusinessWithException(inputStreamReader);
} catch (IOException | BusinessException exception) {
LOGGER.error("business error!");
}
}
```
```

**错误示例**

```text
```
// 错误用例：close代码未写在finally块中，导致中间代码可能发生异常返回时无法释放FSDataInputStream资源
public static void bad00301(FileSystem fileSystem, String hdfsPath, boolean isTrue) throws IOException, BusinessException {
FSDataInputStream fsDataInputStream = fileSystem.open(new Path(hdfsPath));
if (isTrue) {
BusinessMock.doBusinessWithoutException(fsDataInputStream);
fsDataInputStream.close();
} else {
BusinessMock.doBusinessWithException(fsDataInputStream); // 该函数抛出异常后，将导致后续close未被执行，产生资源泄露问题
fsDataInputStream.close();
}
}
```
```

**参考信息**

- 最新更新时间：2026-01-21 14:11:17

<a id="rule-317"></a>

### 317. SecH_GTS_JAVA_HDFS_Instance

| 字段 | 内容 |
|---|---|
| 问题级别 | 严重 |
| 语言 | JAVA |
| 标签 | SecBrella |
| 关联工具规则 | SecH_GTS_JAVA_HDFS_Instance |
| 规则类型 | 安全类（非编程规范） |
| 预估误报率 | 50% |

**审查要点**

```text
HDFS分布式文件系统FileSystem实例需要手动关闭
当调用FileSystem.newInstance创建FileSystem实例时，需要手动关闭
当调用FileSystem.get创建FileSystem实例时，如果uri或user来自外部，需要手动关闭
```

**修复建议**

```text
1、使用trywithresources创建FileSystem资源
2、在finally里关闭
```

**正确示例**

```text
```
public void newInstanceGoodCase() {
Configuration conf = new Configuration();
URI uri = URI.create("hdfs://localhost:9000/example/path");
try(FileSystem fs = FileSystem.newInstance(uri, conf)) { // trywithresources
if (fs.exists(path)) {
System.out.println("Directory exists!");
} else {
System.out.println("Directory does not exist!");
}
}
}
@RequestMapping(value="/good_one", method = RequestMethod.POST)
@ResponseBody
public boolean getGoodCase(HttpServletRequest servletRequest) throws IOException, URISyntaxException {
URI uri = new URI(servletRequest.getParameter("fs.defaultFS")); // 外部数据
Configuration conf = new Configuration();
FileSystem fileSystem = null;
try {
fileSystem = FileSystem.get(uri,conf);
if (fs.exists(path)) {
return true;
} else {
return false;
}
} finally{
fileSystem.close(); // finally里关闭
}
}
```
```

**错误示例**

```text
```
public void newInstanceBadCase() {
Configuration conf = new Configuration();
URI uri = URI.create("hdfs://localhost:9000/example/path");
FileSystem fs = FileSystem.newInstance(uri, conf); // 未关闭
if (fs.exists(path)) {
System.out.println("Directory exists!");
} else {
System.out.println("Directory does not exist!");
}
}
@RequestMapping(value="/one", method = RequestMethod.POST)
@ResponseBody
private boolean getBadCase(HttpServletRequest servletRequest) throws IOException, URISyntaxException {
URI uri = new URI(servletRequest.getParameter("fs.defaultFS")); // 外部数据
Configuration conf = new Configuration();
FileSystem fileSystem = FileSystem.get(uri,conf); // 未关闭
if (fs.exists(path)) {
return true;
} else {
return false;
}
}
```
```

**参考信息**

- 最新更新时间：2026-01-21 11:37:23

<a id="rule-318"></a>

### 318. SecH_GTS_JAVA_Hash_Alg_Select_BC_ApacheCodec

| 字段 | 内容 |
|---|---|
| 问题级别 | 严重 |
| 语言 | JAVA |
| 标签 | SecBrella |
| 关联工具规则 | SecH_GTS_JAVA_Hash_Alg_Select_BC_ApacheCodec |
| 规则类型 | 安全类（非编程规范） |
| 预估误报率 | 50% |

**审查要点**

```text
规则检测以下不安全场景
【细则1】使用弱密码算法：使用不安全的哈希算法：计算消息认证码的不安全哈希算法使用
在使用com.google.common.hash.Hashing类进行消息认证码计算时使用不安全的哈希算法（SHA1、MD5）
在使用org.apache.commons.codec.digest.HmacUtils类进行消息认证码计算时使用不安全的哈希算法（SHA1、MD5）
在使用BouncyCastle库计算消息摘要时使用不安全的哈希算法（SHA1、MD2、MD4、MD5、RIPEMD128、RIPEMD160）
在使用com.google.common.hash.Hashing类计算消息摘要时使用不安全的哈希算法（SHA1、MD5）
在使用org.apache.commons.codec.digest.DigestUtils类计算消息摘要时使用不安全的哈希算法（SHA1、MD2、MD5）
规范要求链接：
内部链接已省略 规则【F.D.CFD.CAA.1.1.2.a】
```

**修复建议**

```text
产品应优先使用高强度密码算法，不建议使用中强度密码算法及弱密码算法：计算消息认证码禁止使用不安全哈希算法
在使用com.google.common.hash.Hashing类进行消息认证码计算时禁止使用不安全的哈希算法（SHA1、MD5）
在使用org.apache.commons.codec.digest.HmacUtils类进行消息认证码计算时禁止使用不安全的哈希算法（SHA1、MD5）
在使用BouncyCastle库计算消息摘要时禁止使用不安全的哈希算法（SHA1、MD2、MD4、MD5、RIPEMD128、RIPEMD160）
在使用com.google.common.hash.Hashing类计算消息摘要时禁止使用不安全的哈希算法（SHA1、MD5）
在使用org.apache.commons.codec.digest.DigestUtils类计算消息摘要时禁止使用不安全的哈希算法（SHA1、MD2、MD5）
```

**正确示例**

```text
```
// 正确用例1：使用SHA256算法计算哈希值
private byte[] goodCalculateMessageDigestBySha256(String message) throws NoSuchAlgorithmException {
String algorithm = "SHA-256";
MessageDigest digest = MessageDigest.getInstance(algorithm);
digest.update(message.getBytes(StandardCharsets.UTF_8));
return digest.digest();
}
// 正确用例2：使用SHA256算法作为计算消息认证码的哈希算法
private byte[] goodCalculateMacBySha256(String message, byte[] secretKey)
throws NoSuchAlgorithmException, InvalidKeyException {
// 使用SHA256算法计算消息认证码
String algorithm = "HmacSHA256";
Mac mac = Mac.getInstance(algorithm);
SecretKeySpec secretKeySpec = new SecretKeySpec(secretKey, algorithm);
mac.init(secretKeySpec);
return mac.doFinal(message.getBytes(StandardCharsets.UTF_8));
}
```
```

**错误示例**

```text
```
// 错误用例1：使用Bouncy Castle加密库中的MD5Digest计算哈希值
private byte[] badCalculateMessageDigestFromBouncyCastle(String message) {
Security.addProvider(new BouncyCastleProvider());
byte[] messageBytes = message.getBytes(StandardCharsets.UTF_8);
MD5Digest md5Digest = new MD5Digest();
md5Digest.update(messageBytes, 0, messageBytes.length);
byte[] md5Hash = new byte[md5Digest.getDigestSize()];
md5Digest.doFinal(md5Hash, 0);
return md5Hash;
}
// 错误用例2：使用commons-codec中的DigestUtils.md2Hex方法计算消息摘要
private String bad001CalculateMessageDigestFromCommonsCodec(String message) {
return DigestUtils.md2Hex(message);
}
// 错误用例3：使用commons-codec中的DigestUtils.md2方法计算消息摘要
private byte[] bad002CalculateMessageDigestFromCommonsCodec(String message) {
return DigestUtils.md2(message);
}
// 错误用例4：使用HmacUtils.hmacMd5方法计算消息认证码
private byte[] bad001CalculateHmacFromCommonsCodec(String message, byte[] secretKey) {
return HmacUtils.hmacMd5(secretKey, message.getBytes(StandardCharsets.UTF_8));
}
// 错误用例5：使用HmacUtils.hmacMd5Hex方法计算消息认证码
private String bad002CalculateHmacFromCommonsCodec(String message, byte[] secretKey) {
return HmacUtils.hmacMd5Hex(secretKey, message.getBytes(StandardCharsets.UTF_8));
}
// 错误用例6：使用google guava中的Hashing.hmacMd5方法计算消息认证码
private String bad001CalculateHmacFromGoogleGuava(String message, byte[] secretKey) {
return Hashing.hmacMd5(secretKey).hashString(message, StandardCharsets.UTF_8).toString();
}
```
```

**参考信息**

- 最新更新时间：2026-01-21 11:47:41

<a id="rule-319"></a>

### 319. SecH_GTS_JAVA_Header_Reinforcement_CORS

| 字段 | 内容 |
|---|---|
| 问题级别 | 严重 |
| 语言 | JAVA |
| 标签 | SecBrella |
| 关联工具规则 | SecH_GTS_JAVA_Header_Reinforcement_CORS |
| 规则类型 | 安全类（非编程规范） |
| 预估误报率 | 50% |

**审查要点**

```text
规则检测以下不安全场景
【细则1】跨域资源共享配置信任所有域Access-Control-Allow-Origin:*，存在数据劫持风险。（禁止配置*信任所有域，应仅在业务需要时，开启特定网页的跨源资源共享，并对来源/域进行限制）
HTML/JSP页面中配置场景：META元素配置的场景：HTML/JSP页面在META元素配置跨域资源共享Access-Control-Allow-Origin属性时设置为*
Web服务器配置场景-Tomcat：CorsFilter设置cors.allowed.origins属性值为* - FilterRegistrationBean代码注册过滤器场景
Web服务器配置场景-Tomcat：CorsFilter设置cors.allowed.origins属性值为* - web.xml配置过滤器场景
Web框架配置场景-Spring框架：CorsRegistry中通过allowedOrigins/allowedOriginPatterns函数设置信任所有域
Web框架配置场景-Spring框架：mvc:mapping中设置allowed-origin属性为*
Web框架配置场景-Spring框架：WebSocket设置响应头添加信任所有域 - Bean配置webSocket时设置allowed-origins响应头属性为*
Web框架配置场景-Spring框架：通过@CrossOrigin注解的方式在Controller接口上设置信任所有域
规范要求链接：
内部链接已省略 规则21.1
```

**修复建议**

```text
HTML/JSP页面中配置场景：META元素配置的场景：仅在业务需要时，开启特定网页的跨源资源共享，并对来源/域进行限制，不可将Access-Control-Allow-Origin宽泛的设置为*
Web框架配置场景-Spring框架：仅在业务需要时，开启特定网页的跨源资源共享，并对来源/域进行限制，不可将allowedOrigins/allowedOriginPatterns宽泛的设置为*
Web框架配置场景-Spring框架：仅在业务需要时，开启特定网页的跨源资源共享，并对来源/域进行限制，不可将allowed-origins宽泛的设置为*
Web框架配置场景-Spring框架：仅在业务需要时，开启特定网页的跨源资源共享，并对来源/域进行限制，不可将@CrossOrigin宽泛的设置为*
Web服务器配置场景-Tomcat：仅在业务需要时，开启特定网页的跨源资源共享，并对来源/域进行限制，不可将ors.allowed.origins宽泛的设置为*
如果业务需要，开启特定网页的跨源资源共享，并对来源/域进行限制，方法如下：
Java： Response.AddHeader("Access-Control-Allow-Origin", "http://www.1688hot.com:80");
html： <meta http-equiv="Access-Control-Allow-Origin"
content="http://www.1688hot.com:80">
```

**正确示例**

```text
```
//正确用例：开启特定网页的跨源资源共享，并对来源/域进行限制，方法如下：
Response.AddHeader("Access-Control-Allow-Origin", "http://www.1688hot.com:80");
<meta http-equiv="Access-Control-Allow-Origin"content="http://www.1688hot.com:80">
```
```

**错误示例**

```text
```
<!--错误用例1：仅在业务需要时，开启特定网页的跨源资源共享，并对来源/域进行限制，不可将Access-Control-Allow-Origin宽泛的设置为*-->
<html lang="zh">
<head>
<title>Access-Control-Allow-Origin</title>
<meta http-equiv="Access-Control-Allow-Origin" content="*">
</head>
</html>
// 错误用例2：通过@CrossOrigin注解的方式在Controller接口上设置信任所有域
@CrossOrigin(origins = "*", maxAge = 3600)
<!--错误用例3：仅在业务需要时，开启特定网页的跨源资源共享，并对来源/域进行限制，不可将allowed-origins宽泛的设置为*-->
<websocket:handlers allowed-origins="*">
<websocket:mapping path="/textHandler" handler="textHandler"/>
</websocket:handlers>
<mvc:cors>
<!--错误用例4：仅在业务需要时，开启特定网页的跨源资源共享，并对来源/域进行限制，不可将allowed-origins宽泛的设置为*-->
<mvc:mapping path="/**" allowed-origins="*" allow-credentials="false"/>
</mvc:cors>
<!--错误用例5：仅在业务需要时，开启特定网页的跨源资源共享，并对来源/域进行限制，不可将ors.allowed.origins宽泛的设置为*-->
<filter>
<filter-name>CorsFilter</filter-name>
<filter-class>org.apache.catalina.filters.CorsFilter</filter-class>
<init-param>
<param-name>cors.allowed.origins</param-name>
<param-value>*</param-value>
</init-param>
<init-param>
<param-name>cors.allowed.credentials</param-name>
<param-value>false</param-value>
</init-param>
</filter>
// 错误用例6：仅在业务需要时，开启特定网页的跨源资源共享，并对来源/域进行限制，不可将cors.allowed.origins宽泛的设置为*
@Bean
public FilterRegistrationBean<CorsFilter> corsFilterSetStar001() {
FilterRegistrationBean<CorsFilter> headReinforce = new FilterRegistrationBean<>();
headReinforce.setFilter(new CorsFilter());
Map<String, String> initParameters = new HashMap<>();
initParameters.put("cors.allowed.origins", "*");
headReinforce.setInitParameters(initParameters);
headReinforce.addUrlPatterns("/**");
headReinforce.setOrder(1);
return headReinforce;
}
// 错误用例7：仅在业务需要时，开启特定网页的跨源资源共享，并对来源/域进行限制，不可将CorsRegistry宽泛的设置为*
@Override
public void addCorsMappings(CorsRegistry registry) {
// 方式1：allowedOrigins设置信任所有域
registry.addMapping("/**").allowedOrigins("*").allowCredentials(false);
// 方式2：allowedOriginPatterns设置信任所有域
registry.addMapping("/**").allowedOriginPatterns("*").allowCredentials(false);
}
```
```

**参考信息**

- 最新更新时间：2026-01-21 14:06:36

<a id="rule-320"></a>

### 320. SecH_GTS_JAVA_Header_Reinforcement_Cache_Control

| 字段 | 内容 |
|---|---|
| 问题级别 | 严重 |
| 语言 | JAVA |
| 标签 | SecBrella |
| 关联工具规则 | SecH_GTS_JAVA_Header_Reinforcement_Cache_Control |
| 规则类型 | 安全类（非编程规范） |
| 预估误报率 | 50% |

**审查要点**

```text
规则检测以下不安全场景
【细则1】缓存控制响应头Cache-control配置不当，存在敏感信息泄露问题。(建议配置Cache-control:no-cache, no-store, must-revalidate)
HTML/JSP页面中配置场景：META元素配置的场景：HTML/JSP页面在META元素配置Cache-control时配置值不合理
Web框架配置场景-BME框架：通过HtmlResourceCacheFilter过滤器设置Cache-Control的值不正确 - BME框架bme.properties配置文件中cache-control配置的值不正确
Web框架配置场景-BME框架：通过HtmlResourceCacheFilter过滤器设置Cache-Control的值不正确 - 调用HtmlResourceCacheFilter的setCacheControl函数配置的值不正确
Web框架配置场景-example WSF安全框架：hwsf-security.xml配置文件中Cache-control节点配置disable来关闭默认安全头
Web框架配置场景-example WSF安全框架：通过HeaderWriterFilter过滤器设置Cache-Control的值不正确 - FilterRegistrationBean代码注册过滤器场景
Web框架配置场景-example WSF安全框架：通过HeaderWriterFilter过滤器设置Cache-Control的值不正确 - web.xml配置过滤器场景
Web框架配置场景-servlet框架：servlet过滤器函数中，通过setHeader/addHeader配置Cache-Control，配置值不合理
Web框架配置场景-spring security安全框架：通过HttpSecurity类调用disable函数关闭缓存控制响应头Cache-control的安全配置
Web框架配置场景-Usecurity安全框架：通过UsHeaderWriterFilter过滤器设置Cache-Control的值不正确 - FilterRegistrationBean代码注册过滤器场景
Web框架配置场景-Usecurity安全框架：通过UsHeaderWriterFilter过滤器设置Cache-Control的值不正确 - web.xml配置过滤器场景
对各个请求单独设置缓存控制响应头Cache-control，可能造成部分请求对应的Response响应没有设置(推荐在web容器、nginx、网关、过滤器中统一设置，避免遗漏)
规范要求链接：
内部链接已省略 规则9.1.6
```

**修复建议**

```text
带有敏感数据的Web页面都应该禁止缓存，以防止敏感信息泄漏或通过代理服务器上网的用户数据互窜问题。
特定条件下，即便指定了”Cache-Control:no-cache”头域，浏览器仍会执行缓存动作。建议应考虑使用”no-store”指令。参考网址： http://www.w3.org/Protocols/rfc2616/rfc2616-sec14.html#sec14.9.1
Web框架配置场景-BME框架：通过HtmlResourceCacheFilter过滤器设置Cache-Control的值为建议值
Web框架配置场景-example WSF安全框架：通过HeaderWriterFilter过滤器设置Cache-Control的值为建议值
Web框架配置场景-servlet框架：servlet过滤器函数中，通过setHeader/addHeader配置Cache-Control，配置值为建议值
Web框架配置场景-Usecurity安全框架：通过UsHeaderWriterFilter过滤器设置Cache-Control的值为建议值
禁止对各个请求单独设置缓存控制响应头Cache-control，可能造成部分请求对应的Response响应没有设置(推荐在web容器、nginx、网关、过滤器中统一设置，避免遗漏)
```

**正确示例**

```text
```
// 正确用例1：Cache-Control已配置推荐值 no-cache,no-store,must-revalidate
public void doFilter_good(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain)
throws IOException, ServletException {
if (servletResponse instanceof HttpServletResponse) {
HttpServletResponse httpServletResponse = (HttpServletResponse) servletResponse;
httpServletResponse.addHeader("Cache-Control", "no-cache,no-store,must-revalidate"); // addHeader
httpServletResponse.setHeader("Cache-Control", "no-cache,no-store,must-revalidate"); // setHeader
}
filterChain.doFilter(servletRequest, servletResponse);
}
// 正确用例2：Cache-control配置推荐值 no-cache,no-store,must-revalidate
@Bean
public FilterRegistrationBean<UsHeaderWriterFilter> usHeaderWriterFilter_good() {
FilterRegistrationBean<UsHeaderWriterFilter> usFilter = new FilterRegistrationBean<>();
usFilter.setFilter(new UsHeaderWriterFilter());
usFilter.addInitParameter("Cache-Control", "no-cache,no-store,must-revalidate");
usFilter.addUrlPatterns("/**");
usFilter.setOrder(3);
return usFilter;
}
<!--正确用例3：Cache-Control设置了推荐值 no-cache, no-store, must-revalidate-->
<filter>
<filter-name>HeaderWriterFilterGood</filter-name>
<filter-class>com.example.springframework.security.header.writer.HeaderWriterFilter</filter-class>
<init-param>
<param-name>Cache-Control</param-name>
<param-value>no-cache,no-store,max-age=0,must-revalidate</param-value>
</init-param>
</filter>
```
```

**错误示例**

```text
```
// 错误用例1：setHeader/addHeader方式单独为请求设置控制响应头X-Content-Type-Options，可能造成部分请求对应的Response响应没有设置，推荐在web容器、nginx、网关、过滤器中统一设置，避免遗漏
@GetMapping("/setSingleApiContentTypeOptions")
public Result setSingleApiContentTypeOptions(HttpServletResponse response) {
response.addHeader("X-Content-Type-Options", "nosniff");
response.setHeader("X-Content-Type-Options", "nosniff");
return Result.ok("Success");
}
<!--错误用例2：Cache-Control未配置推荐值:no-cache,no-store,must-revalidate-->
<html lang="zh">
<head>
<meta http-equiv="Cache-Control" Content="max-age=3600">
<title>Cache-Control Settings</title>
</head>
</html>
// 错误用例3:Web框架配置场景：spring security安全框架：通过HttpSecurity类调用disable函数关闭缓存控制响应头Cache-control的安全配置
@Override
protected void configure(HttpSecurity http) throws Exception {
http.headers().cacheControl().disable();
}
// 错误用例4：Cache-Control未配置推荐值 no-cache,no-store,must-revalidate
@Override
public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain)
throws IOException, ServletException {
if (servletResponse instanceof HttpServletResponse) {
HttpServletResponse httpServletResponse = (HttpServletResponse) servletResponse;
httpServletResponse.addHeader("Cache-Control", "max-age=3600"); // addHeader
httpServletResponse.setHeader("Cache-Control", "max-age=3600"); // setHeader
}
filterChain.doFilter(servletRequest, servletResponse);
}
// 错误用例5：通过addInitParameter配置Cache-control时未配置推荐值 no-cache,no-store,must-revalidate
@Bean
public FilterRegistrationBean<UsHeaderWriterFilter> usHeaderWriterFilter_bad002() {
FilterRegistrationBean<UsHeaderWriterFilter> usFilter = new FilterRegistrationBean<>();
usFilter.setFilter(new UsHeaderWriterFilter());
usFilter.addInitParameter("Cache-Control", "max-age=3600");
usFilter.addUrlPatterns("/**");
usFilter.setOrder(3);
return usFilter;
}
<!--错误用例6：Cache-Control未设置推荐值 no-cache, no-store, must-revalidate-->
<filter>
<filter-name>HeaderWriterFilterBad</filter-name>
<filter-class>com.example.springframework.security.header.writer.HeaderWriterFilter</filter-class>
<init-param>
<param-name>Cache-Control</param-name>
<param-value>max-age=3600,no-cache</param-value>
</init-param>
</filter>
```
```

**参考信息**

- 最新更新时间：2026-01-21 14:35:58

<a id="rule-321"></a>

### 321. SecH_GTS_JAVA_Header_Reinforcement_Content_Security_Policy

| 字段 | 内容 |
|---|---|
| 问题级别 | 严重 |
| 语言 | JAVA |
| 标签 | SecBrella |
| 关联工具规则 | SecH_GTS_JAVA_Header_Reinforcement_Content_Security_Policy |
| 规则类型 | 安全类（非编程规范） |
| 预估误报率 | 50% |

**审查要点**

```text
规则检测以下不安全场景
【细则1】content-security-policy配置信任所有域资源，可通过资源注入手段造成XSS攻击、点击劫持等问题。
(建议配置例如default-src 'self' 代表仅允许加载相同源的内容，禁止使用通配符*信任所有的域资源。其它的内容安全策略还包括child-src、connect-src、font-src、frame-src、img-src、manifest-src、media-src、object-src、script-src、style-src、worker-src等)
META元素配置的场景：HTML/JSP页面在meta元素配置Content-Security-Policy时通过使用通配符*信任了所有域的资源
Web框架配置场景-example WSF安全框架：HeaderWriterFilter过滤器配置Content-Security-Policy内容安全策略时，存在信任所有域的情况-FilterRegistrationBean代码注册过滤器场景
Web框架配置场景-example WSF安全框架：HeaderWriterFilter过滤器配置Content-Security-Policy内容安全策略时，存在信任所有域的情况-web.xml配置过滤器场景
Web框架配置场景-example WSF安全框架：hwsf-security.xml配置文件中在配置content-security-policy元素的policy-directives属性时，配置csp策略信任了所有域
Web框架配置场景-servlet框架：servlet过滤器函数中，通过setHeader/addHeader配置Content-Security-Policy值信任所有域
Web框架配置场景-Spring Security框架：通过HttpSecurity类调用contentSecurityPolicy函数配置内容安全策略时，存在信任所有域的情况
Web框架配置场景-USecurity 安全框架：UsHeaderWriterFilter过滤器配置Content-Security-Policy内容安全策略时，存在信任所有域的情况-FilterRegistrationBean代码注册过滤器场景
Web框架配置场景-USecurity 安全框架：UsHeaderWriterFilter过滤器配置Content-Security-Policy内容安全策略时，存在信任所有域的情况-web.xml配置过滤器场景
规范要求链接：
内部链接已省略 规则22.5
```

**修复建议**

```text
配置CSP响应头时，默认建议配置为‘self’，禁止 * 。不推荐使用‘unsafe-inline’ ‘unsafe-eval’（但不禁止使用，业界google、facebook也在使用）
其它的policy详细说明请参考：规范要求中CSP(内容安全策略)详解：内部链接已省略
场景1：
当某页面只允许加载本网站的资源，并且不加载<iframe>,<frame>,<object>,<embed>,<applet>时，可按如下方式设置CSP响应头：
response.setHeader("Content-Security-Policy" "default-src 'self'; child-src 'none';
object-src 'none'; frame-ancestors 'none'");
场景2:
当某页面只允许加载同域及a.com域下的资源，可按如下方式设置CSP响应头：
response.setHeader("Content-Security-Policy", "default-src ‘self‘ *.a.com");
说明：CSP配置太严格，可能会导致部分资源加载失败，设置完后需进行功能验证。
```

**正确示例**

```text
```
// 正确用例1：Content-Security-Policy配置内容安全策略值为self，仅信任自己域的内容
@Bean
public FilterRegistrationBean<UsHeaderWriterFilter> usHeaderWriterFilter_good() {
FilterRegistrationBean<UsHeaderWriterFilter> usFilter = new FilterRegistrationBean<>();
usFilter.setFilter(new UsHeaderWriterFilter());
usFilter.addInitParameter("Content-Security-Policy", "default-src 'self'");
usFilter.addUrlPatterns("/**");
usFilter.setOrder(3);
return usFilter;
}
<!--正确用例2：建议配置CSP属性值为default-src 'self' 代表仅允许加载相同源的内容-->
<filter>
<filter-name>HeaderWriterFilterGood</filter-name>
<filter-class>com.example.springframework.security.header.writer.HeaderWriterFilter</filter-class>
<init-param>
<param-name>Content-Security-Policy</param-name>
<param-value>default-src 'self'</param-value>
</init-param>
</filter>
<!--正确用例3：建议配置CSP属性值为default-src 'self' 代表仅允许加载相同源的内容-->
<filter>
<filter-name>UsHeaderWriterFilterGood</filter-name>
<filter-class>com.example.us.wsf.filter.UsHeaderWriterFilter</filter-class>
<init-param>
<param-name>Content-Security-Policy</param-name>
<param-value>default-src 'self'</param-value>
</init-param>
</filter>
```
```

**错误示例**

```text
```
<!--错误用例1：配置Content-Security-Policy内容安全策略default-src内容时使用通配符信任所有域资源，容易造成资源注入问题。其它的内容安全策略
还包括child-src、connect-src、font-src、frame-src、img-src、manifest-src、media-src、object-src、script-src、style-src、
worker-src等-->
<html lang="zh">
<head>
<!--建议default-src设置为self-->
<meta http-equiv="Content-Security-Policy" content="default-src 'https://*'">
<title>Content Security Policy</title>
</head>
</html>
// 错误用例2：UsHeaderWriterFilter过滤器通过setInitParameters配置Content-Security-Policy时信任了所有域，存在资源注入风险。其它的内容安全策略还包括child-src、connect-src、font-src、frame-src、img-src、manifest-src、media-src、object-src、 script-src、style-src、worker-src等
@Bean
public FilterRegistrationBean<UsHeaderWriterFilter> usHeaderWriterFilter_bad001() {
FilterRegistrationBean<UsHeaderWriterFilter> usFilter = new FilterRegistrationBean<>();
usFilter.setFilter(new UsHeaderWriterFilter());
Map<String, String> initParameters = new HashMap<>();
initParameters.put("Content-Security-Policy", "default-src https://*");
usFilter.setInitParameters(initParameters);
usFilter.addUrlPatterns("/**");
usFilter.setOrder(1);
return usFilter;
}
// 错误用例3：UsHeaderWriterFilter过滤器通过addInitParameter配置Content-Security-Policy时信任了所有域，存在资源注入风险。其它的内容安全策略还包括child-src、connect-src、font-src、frame-src、img-src、manifest-src、media-src、object-src、script-src、style-src、worker-src等
@Bean
public FilterRegistrationBean<UsHeaderWriterFilter> usHeaderWriterFilter_bad002() {
FilterRegistrationBean<UsHeaderWriterFilter> usFilter = new FilterRegistrationBean<>();
usFilter.setFilter(new UsHeaderWriterFilter());
usFilter.addInitParameter("Content-Security-Policy", "default-src https://*");
usFilter.addUrlPatterns("/**");
usFilter.setOrder(2);
return usFilter;
}
<!--错误用例4：default-src配置信任了所有域，存在资源注入风险。其它的内容安全策略还包括child-src、connect-src、font-src、frame-src、
img-src、manifest-src、media-src、object-src、script-src、style-src、worker-src等-->
<filter>
<filter-name>HeaderWriterFilterBad</filter-name>
<filter-class>com.example.springframework.security.header.writer.HeaderWriterFilter</filter-class>
<init-param>
<param-name>Content-Security-Policy</param-name>
<param-value>default-src https://*</param-value>
</init-param>
</filter>
// 错误用例5：过滤器中通过addHeader/setHeader配置Content-Security-Policy时信任了所有域，存在资源注入风险。其它的内容安全策略还包括child-src、connect-src、font-src、frame-src、img-src、manifest-src、media-src、object-src、script-src、style-src、worker-src等
@Override
public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain)
throws IOException, ServletException {
if (servletResponse instanceof HttpServletResponse) {
HttpServletResponse response = (HttpServletResponse) servletResponse;
response.addHeader("Content-Security-Policy", "default-src https://*");
response.setHeader("Content-Security-Policy", "default-src https://*");
}
filterChain.doFilter(servletRequest, servletResponse);
}
```
```

**参考信息**

- 最新更新时间：2026-01-21 14:15:52

<a id="rule-322"></a>

### 322. SecH_GTS_JAVA_Header_Reinforcement_Content_Type_Options

| 字段 | 内容 |
|---|---|
| 问题级别 | 严重 |
| 语言 | JAVA |
| 标签 | SecBrella |
| 关联工具规则 | SecH_GTS_JAVA_Header_Reinforcement_Content_Type_Options |
| 规则类型 | 安全类（非编程规范） |
| 预估误报率 | 50% |

**审查要点**

```text
规则检测以下不安全场景
【细则1】关闭浏览器类型猜测响应头X-Content-Type-Options，可能造成XSS问题。对各个请求单独设置X-Content-Type-Options，可能造成部分请求对应的Response响应没有设置（推荐在web容器、nginx、网关、过滤器中统一设置，避免遗漏）
请求单独设置X-Content-Type-Options，可能造成部分请求对应的Response响应没有设置（推荐在web容器、nginx、网关、过滤器中统一设置，避免遗漏），不建议通过setHeader/addHeader方式单独为请求设置控制响应头X-Content-Type-Options
Web服务器配置场景-Tomcat：通过HttpHeaderSecurityFilter关闭了内容猜解头content-type-options加固 - FilterRegistrationBean代码注册过滤器场景：通过HttpHeaderSecurityFilter调用setBlockContentTypeSniffingEnabled关闭类型猜测响应头能力
Web服务器配置场景-Tomcat：通过HttpHeaderSecurityFilter关闭了内容猜解头content-type-options加固 - FilterRegistrationBean代码注册过滤器场景：通过设置blockContentTypeSniffingEnabled初始化参数的方式
Web服务器配置场景-Tomcat：通过HttpHeaderSecurityFilter关闭了内容猜解头content-type-options加固 - web.xml配置过滤器场景
Web框架配置场景-example WSF安全框架：hwsf-security.xml配置文件中content-type-options节点配置disable关闭了内容猜测头的加固
Web框架配置场景-SpringSecurity安全框架：通过HttpSecurity类调用disable函数关闭X-Content-Type-Options的安全配置
规范要求链接：
内部链接已省略 规则22.2
```

**修复建议**

```text
不单独设置X-Content-Type-Options，防止造成部分请求对应的Response响应没有设置（推荐在web容器、nginx、网关、过滤器中统一设置，避免遗漏）
Web服务器配置场景-Tomcat：通过HttpHeaderSecurityFilter开启内容猜解头content-type-options加固
Web框架配置场景-example WSF安全框架：hwsf-security.xml配置文件中content-type-options节点配置打开内容猜测头的加固
Web框架配置场景-SpringSecurity安全框架：通过HttpSecurity类调用disable函数开启X-Content-Type-Options的安全配置
```

**正确示例**

```text
```
// 正确用例1：通过addInitParameter配置blockContentTypeSniffingEnabled为true，开启了content-type-options加固
@Bean
public FilterRegistrationBean<HttpHeaderSecurityFilter> headerWriterFilter_good001() {
FilterRegistrationBean<HttpHeaderSecurityFilter> headReinforce = new FilterRegistrationBean<>();
headReinforce.setFilter(new HttpHeaderSecurityFilter());
// HttpHeaderSecurityFilter的blockContentTypeSniffingEnabled默认值为true，默认安全，无需代码设置
headReinforce.addInitParameter("blockContentTypeSniffingEnabled", "true");
headReinforce.addUrlPatterns("/**");
headReinforce.setOrder(2);
return headReinforce;
}
// 正确用例2：通过HttpHeaderSecurityFilter调用setBlockContentTypeSniffingEnabled(true)关闭类型猜测响应头能力
@Bean
public FilterRegistrationBean<HttpHeaderSecurityFilter> httpHeaderSecurityFilter_good002() {
FilterRegistrationBean<HttpHeaderSecurityFilter> headReinforce = new FilterRegistrationBean<>();
HttpHeaderSecurityFilter httpHeaderSecurityFilter = new HttpHeaderSecurityFilter();
// HttpHeaderSecurityFilter的blockContentTypeSniffingEnabled默认值为true，默认安全，无需代码设置
httpHeaderSecurityFilter.setBlockContentTypeSniffingEnabled(true);
headReinforce.setFilter(httpHeaderSecurityFilter);
headReinforce.addUrlPatterns("/**");
headReinforce.setOrder(3);
return headReinforce;
}
<!--正确用例3: HttpHeaderSecurityFilter过滤器中的blockContentTypeSniffingEnabled初始化参数设置为true，
开启了类型猜测响应头加固。实际上HttpHeaderSecurityFilter是默认安全的，无需代码重新设置blockContentTypeSniffingEnabled -->
<filter>
<filter-name>HttpHeaderSecurityGood</filter-name>
<filter-class>org.apache.catalina.filters.HttpHeaderSecurityFilter</filter-class>
<init-param>
<param-name>blockContentTypeSniffingEnabled</param-name>
<param-value>true</param-value>
</init-param>
</filter>
<filter-mapping>
<filter-name>HttpHeaderSecurityGood</filter-name>
<url-pattern>/*</url-pattern>
</filter-mapping>
```
```

**错误示例**

```text
```
// 错误用例1：通过setInitParameters配置blockContentTypeSniffingEnabled为false，关闭了content-type-options加固
@Bean
public FilterRegistrationBean<HttpHeaderSecurityFilter> httpHeaderSecurityFilter_bad001() {
FilterRegistrationBean<HttpHeaderSecurityFilter> headReinforce = new FilterRegistrationBean<>();
headReinforce.setFilter(new HttpHeaderSecurityFilter());
Map<String, String> initParameters = new HashMap<>();
initParameters.put("blockContentTypeSniffingEnabled", "false");
headReinforce.setInitParameters(initParameters);
headReinforce.addUrlPatterns("/**");
headReinforce.setOrder(1);
return headReinforce;
}
// 错误用例2：通过addInitParameter配置blockContentTypeSniffingEnabled为false、，关闭了content-type-options加固
@Bean
public FilterRegistrationBean<HttpHeaderSecurityFilter> httpHeaderSecurityFilter_bad002() {
FilterRegistrationBean<HttpHeaderSecurityFilter> headReinforce = new FilterRegistrationBean<>();
headReinforce.setFilter(new HttpHeaderSecurityFilter());
headReinforce.addInitParameter("blockContentTypeSniffingEnabled", "false");
headReinforce.addUrlPatterns("/**");
headReinforce.setOrder(2);
return headReinforce;
}
// 错误用例3：通过HttpHeaderSecurityFilter调用setBlockContentTypeSniffingEnabled(false)关闭类型猜测响应头能力
@Bean
public FilterRegistrationBean<HttpHeaderSecurityFilter> httpHeaderSecurityFilter_bad003() {
FilterRegistrationBean<HttpHeaderSecurityFilter> headReinforce = new FilterRegistrationBean<>();
HttpHeaderSecurityFilter httpHeaderSecurityFilter = new HttpHeaderSecurityFilter();
httpHeaderSecurityFilter.setBlockContentTypeSniffingEnabled(false); // 关闭类型猜测响应头能力
headReinforce.setFilter(httpHeaderSecurityFilter);
headReinforce.addUrlPatterns("/**");
headReinforce.setOrder(3);
return headReinforce;
}
<!--错误用例4: HttpHeaderSecurityFilter过滤器中的blockContentTypeSniffingEnabled初始化参数设置为false，
关闭了类型猜测响应头加固 -->
<filter>
<filter-name>HttpHeaderSecurityBad</filter-name>
<filter-class>org.apache.catalina.filters.HttpHeaderSecurityFilter</filter-class>
<init-param>
<param-name>blockContentTypeSniffingEnabled</param-name>
<param-value>false</param-value>
</init-param>
</filter>
<filter-mapping>
<filter-name>HttpHeaderSecurityBad</filter-name>
<url-pattern>/*</url-pattern>
</filter-mapping>
// 错误用例5：setHeader/addHeader方式单独为请求设置控制响应头X-Content-Type-Options，可能造成部分请求对应的Response响应没有设置，推荐在web容器、nginx、网关、过滤器中统一设置，避免遗漏
@GetMapping("/setSingleApiContentTypeOptions")
public Result setSingleApiContentTypeOptions(HttpServletResponse response) {
response.addHeader("X-Content-Type-Options", "nosniff");
response.setHeader("X-Content-Type-Options", "nosniff");
return Result.ok("Success");
}
// 错误用例6：通过HttpSecurity的contentTypeOptions().disable()函数关闭了类型猜测响应头的加固
@Override
protected void configure(HttpSecurity http) throws Exception {
http.headers().contentTypeOptions().disable();
}
<http auto-config="true">
<headers>
<!--问题用例7：通过content-type-options的disabled属性设置为true，关闭了类型猜测响应头的加固 -->
<content-type-options disabled="true"/>
</headers>
</http>
```
```

**参考信息**

- 最新更新时间：2026-01-21 11:37:40

<a id="rule-323"></a>

### 323. SecH_GTS_JAVA_Header_Reinforcement_Cookie_Security

| 字段 | 内容 |
|---|---|
| 问题级别 | 一般 |
| 语言 | JAVA |
| 标签 | SecBrella |
| 关联工具规则 | SecH_GTS_JAVA_Header_Reinforcement_Cookie_Security |
| 规则类型 | 安全类（非编程规范） |
| 预估误报率 | 20% |

**审查要点**

```text
规则检测以下不安全场景
【细则1】为包含会话标识的cookie设置适当限制的domain属性，会话cookie要设置HttpOnly、SameSite属性。未给cookie设置secure标志，无法防止cookie在http（明文协议）下被嗅探
手动构建会话cookie响应头时，secure、domain、HttpOnly、SameSite添加到header值中设置错误或错漏
web.xml中配置session-config会话属性时，secure、domain、HttpOnly添加到header值中设置错误或错漏
使用Spring框架创建会话cookie时，相关属性设置错误
（使用Cookie做为会话标识或Token的载体时，Cookie必须设置HttpOnly属性以及secure属性，建议设置domain、path、Samesite等属性，有效提升会话的安全性。）
规范要求链接：
内部链接已省略 建议5.2
【误报场景】使用CSRF Token Cookie的场景下，不能设置 HttpOnly 标志，会破坏整个 CSRF 防护机制，导致它无法正常工作。此场景下告警为误报，暂无法统一消除，请开发人员申请此处告警屏蔽忽略。
```

**修复建议**

```text
手动构建cookie响应头时，将HttpOnly值加到header值中
手动构建会话cookie响应头时，将SameSite值加到header值中
手动构建cookie响应头时，将Secure值加到header值中
web.xml中配置session-config会话属性时，设置http-only属性为true
web.xml中配置session-config会话属性时，设置secure属性为true
使用Spring框架的CookieGenerator类创建会话cookie时，调用setCookieHttpOnly函数将httponly设置为true
使用Spring框架的ResponseCookie类创建会话cookie时，调用httpOnly函数将httponly设置为true
通过实例化Cookie类实现会话cookie功能时，通过setHttpOnly函数设置httponly为true
使用Spring框架的ResponseCookie类创建会话cookie时，调用sameSite函数为会话cookie增加sameSite属性
使用Spring框架的ResponseCookie类创建cookie时，调用secure函数将secure设置为true
```

**正确示例**

```text
```
/**
* 正确示例1：会话cookie增加了SameSite值 - Servlet 6.0 + 使用setAttribute
*/
@GetMapping("/good001")
public Result good001(HttpServletResponse response) throws NoSuchAlgorithmException {
String sessionID = CookieUtil.getSessionID();
Cookie cookie = new Cookie("SessionID", sessionID);
cookie.setAttribute("SameSite", "Strict"); // 增加了SameSite值
cookie.setHttpOnly(true);
cookie.setSecure(true);
response.addCookie(cookie);
return Result.ok("Success");
}
/**
* 正确示例2：使用Spring框架的CookieGenerator类创建会话Cookie时，设置SameSite值
*/
@GetMapping("/good002")
public Result good002(HttpServletResponse response) throws NoSuchAlgorithmException {
CookieGenerator cookieGenerator = new CookieGenerator();
cookieGenerator.setCookieName("sessionId");
cookieGenerator.setCookieHttpOnly(true);
cookieGenerator.setCookieSecure(true);
// 增加了SameSite值
cookieGenerator.addCookie(response, CookieUtil.getSessionID() + ";SameSite=Strict");
return Result.ok("Success");
}
/**
* 正确示例3：ResponseCookie调用sameSite函数设置sameSite属性
*/
@GetMapping("/good003")
public Result good003(HttpServletResponse response) throws NoSuchAlgorithmException {
ResponseCookie responseCookie = ResponseCookie.from("sessionID", CookieUtil.getSessionID())
.httpOnly(true).secure(true).sameSite("Strict") // 设置sameSite属性
.build();
response.setHeader(HttpHeaders.SET_COOKIE, responseCookie.toString());
return Result.ok("Success");
}
/**
* 正确示例4：手动构建会话cookie响应头时，设置SameSite值
*/
@GetMapping("/good004")
public Result good004(HttpServletResponse response) throws NoSuchAlgorithmException {
String sessionValue = "sessionId=" + CookieUtil.getSessionID() + ";Secure;HttpOnly;SameSite=Strict";
response.setHeader("Set-Cookie", sessionValue);
return Result.ok("Success");
}
```
```

**错误示例**

```text
```
// 错误用例1：ResponseCookie未调用sameSite设置属性
@GetMapping("/bad001")
public Result bad001(HttpServletResponse response) throws NoSuchAlgorithmException {
// cookie名称包含session，该cookie为会话Cookie
ResponseCookie responseCookie = ResponseCookie.from("sessionID", CookieObjNoSetSameSite.getSessionID())
.httpOnly(true)
.secure(true)
.maxAge(Duration.ofSeconds(300))
.build();
response.setHeader(HttpHeaders.SET_COOKIE, responseCookie.toString());
return Result.ok("Success");
}
// 错误用例2：httponly属性建议设置为true
@GetMapping("/bad001")
public Result bad001(HttpServletResponse response) throws NoSuchAlgorithmException {
String sessionID = getSessionID();
// cookie的名称为SessionID，表明该cookie为会话cookie
Cookie cookie = new Cookie("SessionID", sessionID);
// cookie.setHttpOnly(false); // 或者手动错误关闭了HttpOnly属性
cookie.setSecure(true);
cookie.setMaxAge(300);
response.addCookie(cookie);
return Result.ok("Success");
}
<!--错误用例3：需要显示设置secure的属性为true-->
<session-config>
<session-timeout>5</session-timeout>
<cookie-config>
<name>SessionID</name>
<!-- <domain>example.com</domain> -->
<path> / </path>
<http-only>false</http-only>
<secure>false</secure>
</cookie-config>
</session-config>
<!--错误用例4：http-only属性应设置为true-->
<session-config>
<session-timeout>5</session-timeout>
<cookie-config>
<name>SessionID</name>
<!-- <domain>example.com</domain> -->
<path> / </path>
<http-only>false</http-only>
<secure>true</secure>
</cookie-config>
</session-config>
```
```

**参考信息**

- 最新更新时间：2026-01-21 14:34:05

<a id="rule-324"></a>

### 324. SecH_GTS_JAVA_Header_Reinforcement_Strict_Transport_Security

| 字段 | 内容 |
|---|---|
| 问题级别 | 严重 |
| 语言 | JAVA |
| 标签 | SecBrella |
| 关联工具规则 | SecH_GTS_JAVA_Header_Reinforcement_Strict_Transport_Security |
| 规则类型 | 安全类（非编程规范） |
| 预估误报率 | 50% |

**审查要点**

```text
【提示】当前只需设置max-age=?且?>=31536000即可屏蔽，参考规范：内部链接已省略
规则检测以下不安全场景
【细则1】强制https请求响应头Strict-Transport-Security设置不当，存在使用不安全协议问题（推荐设置max-age=31536000; includeSubDomains)
Web服务器配置场景-Tomcat:通过HttpHeaderSecurityFilter过滤器关闭了HSTS头加固-FilterRegistrationBean代码注册过滤器场景
Web服务器配置场景-Tomcat:通过HttpHeaderSecurityFilter过滤器关闭了HSTS头加固-web.xml配置过滤器场景
Web服务器配置场景-Tomcat:通过HttpHeaderSecurityFilter过滤器设置的HSTS值不正确-FilterRegistrationBean代码注册过滤器场景
Web服务器配置场景-Tomcat:通过HttpHeaderSecurityFilter过滤器设置的HSTS值不正确-web.xml配置过滤器场景
Web框架配置场景-example WSF安全框架:hwsf-security.xml配置文件中HSTS节点属性配置不合理
Web框架配置场景-example WSF安全框架:通过HeaderWriterFilter过滤器设置HSTS的值不正确 - FilterRegistrationBean代码注册过滤器场景
Web框架配置场景-example WSF安全框架:通过HeaderWriterFilter过滤器设置HSTS的值不正确 - web.xml配置过滤器场景
Web框架配置场景-servlet框架:servlet过滤器函数中，通过setHeader/addHeader配置HSTS值不合理
Web框架配置场景-Spring Security框架:通过HttpSecurity类调用disable函数关闭HSTS的header加固项
Web框架配置场景-Spring Security框架:通过HttpSecurity类设置HSTS的header加固项值不正确
Web框架配置场景-USecurity安全框架:通过UsHeaderWriterFilter过滤器设置HSTS的值不正确-FilterRegistrationBean代码注册过滤器场景
Web框架配置场景-USecurity安全框架:通过UsHeaderWriterFilter过滤器设置HSTS的值不正确-web.xml配置过滤器场景
对请求单独设置Strict-Transport-Security，可能造成部分请求对应的Response响应没有设置（推荐在web容器、nginx、网关、过滤器中统一设置，避免遗漏），setHeader/addHeader方式单独为请求设置控制响应头Strict-Transport-Security
规范要求链接：
内部链接已省略 规则22.4
```

**修复建议**

```text
Web服务器配置场景-Tomcat:通过HttpHeaderSecurityFilter过滤器开启HSTS头加固
Web框架配置场景-example WSF安全框架:hwsf-security.xml配置文件中HSTS节点属性配置为"max-age=31536000; incluedeSubDomains"
Web框架配置场景-example WSF安全框架:通过HeaderWriterFilter过滤器设置HSTS的值为"max-age=31536000; incluedeSubDomains"
Web框架配置场景-example WSF安全框架:通过HeaderWriterFilter过滤器设置HSTS的值为"max-age=31536000; incluedeSubDomains"
Web框架配置场景-servlet框架:servlet过滤器函数中，通过setHeader/addHeader配置HSTS值为"max-age=31536000; incluedeSubDomains"
Web框架配置场景-Spring Security框架:通过HttpSecurity类设置HSTS的header加固项值为"max-age=31536000; incluedeSubDomains"
Web框架配置场景-USecurity安全框架:通过UsHeaderWriterFilter过滤器设置HSTS的值为"max-age=31536000; incluedeSubDomains"
不单独设置Strict-Transport-Security，防止造成部分请求对应的Response响应没有设置（推荐在web容器、nginx、网关、过滤器中统一设置，避免遗漏）
```

**正确示例**

```text
```
// 正确用例1：UsHeaderWriterFilter过滤器通过addInitParameter配置Strict-Transport-Security值已经使用了推荐值 max-age=31536000; includeSubDomains
@Bean
public FilterRegistrationBean<UsHeaderWriterFilter> usHeaderWriterFilter_good() {
FilterRegistrationBean<UsHeaderWriterFilter> headReinforce = new FilterRegistrationBean<>();
headReinforce.setFilter(new UsHeaderWriterFilter());
headReinforce.addInitParameter("Strict-Transport-Security", "max-age=31536000; includeSubDomains");
headReinforce.addUrlPatterns("/**");
headReinforce.setOrder(2);
return headReinforce;
}
<!--正确用例2： HttpHeaderSecurityFilter过滤器通过配置初始化参数hstsEnabled值为true打开了HSTS的头加固能力。并且设置的初始化参数
hstsIncludeSubDomains和hstsMaxAgeSeconds为推荐的值-->
<filter>
<filter-name>HttpHeaderSecurityGood</filter-name>
<filter-class>org.apache.catalina.filters.HttpHeaderSecurityFilter</filter-class>
<init-param>
<param-name>hstsEnabled</param-name>
<param-value>true</param-value>
</init-param>
<init-param>
<param-name>hstsIncludeSubDomains</param-name>
<param-value>true</param-value>
</init-param>
<init-param>
<param-name>hstsMaxAgeSeconds</param-name>
<param-value>31536000</param-value>
</init-param>
</filter>
// 正确用例3：Strict-Transport-Security已经配置为推荐的值 max-age=31536000; includeSubDomains
public void doFilter_good(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain)
throws IOException, ServletException {
if (servletResponse instanceof HttpServletResponse) {
HttpServletResponse httpServletResponse = (HttpServletResponse) servletResponse;
httpServletResponse.addHeader("Strict-Transport-Security", "max-age=31536000; includeSubDomains");
httpServletResponse.setHeader("Strict-Transport-Security", "max-age=31536000; includeSubDomains");
}
filterChain.doFilter(servletRequest, servletResponse);
}
```
```

**错误示例**

```text
```
// 错误用例1：UsHeaderWriterFilter过滤器通过setInitParameters配置Strict-Transport-Security值未使用推荐值 max-age=31536000; incluedeSubDomains
@Bean
public FilterRegistrationBean<UsHeaderWriterFilter> usHeaderWriterFilter_bad001() {
FilterRegistrationBean<UsHeaderWriterFilter> headReinforce = new FilterRegistrationBean<>();
headReinforce.setFilter(new UsHeaderWriterFilter());
Map<String, String> initParameters = new HashMap<>();
initParameters.put("Strict-Transport-Security", "max-age=3600");
headReinforce.setInitParameters(initParameters);
headReinforce.addUrlPatterns("/**");
headReinforce.setOrder(1);
return headReinforce;
}
// 错误用例2：UsHeaderWriterFilter过滤器通过addInitParameter配置Strict-Transport-Security值未使用推荐值 max-age=31536000; incluedeSubDomains
@Bean
public FilterRegistrationBean<UsHeaderWriterFilter> usHeaderWriterFilter_bad002() {
FilterRegistrationBean<UsHeaderWriterFilter> headReinforce = new FilterRegistrationBean<>();
headReinforce.setFilter(new UsHeaderWriterFilter());
headReinforce.addInitParameter("Strict-Transport-Security", "max-age=3600");
headReinforce.addUrlPatterns("/**");
headReinforce.setOrder(2);
return headReinforce;
}
<!--错误用例3： HttpHeaderSecurityFilter过滤器配置初始化参数hstsEnabled为false，关闭了HSTS的头加固能力 -->
<filter>
<filter-name>HttpHeaderSecuritybad001</filter-name>
<filter-class>org.apache.catalina.filters.HttpHeaderSecurityFilter</filter-class>
<init-param>
<param-name>hstsEnabled</param-name>
<param-value>false</param-value>
</init-param>
</filter>
<!--错误用例4： HttpHeaderSecurityFilter过滤器配置初始化参数hstsIncludeSubDomains和hstsMaxAgeSeconds的值非推荐-->
<filter>
<filter-name>HttpHeaderSecuritybad002</filter-name>
<filter-class>org.apache.catalina.filters.HttpHeaderSecurityFilter</filter-class>
<init-param>
<param-name>hstsEnabled</param-name>
<param-value>true</param-value>
</init-param>
<init-param>
<param-name>hstsIncludeSubDomains</param-name>
<param-value>false</param-value>
</init-param>
<init-param>
<param-name>hstsMaxAgeSeconds</param-name>
<param-value>3600</param-value>
</init-param>
</filter>
<http auto-config="true">
<headers defaults-disabled="true" disabled="true">
<!--错误用例5：hsts元素的include-subdomains属性subdomains和max-age-seconds未采用推荐值-->
<hsts include-subdomains="false" max-age-seconds="1"/>
</headers>
</http>
<!--错误用例6：HeaderWriterFilter过滤器设置初始化参数Strict-Transport-Security时不采用推荐的值-->
<filter>
<filter-name>HeaderWriterFilterBad</filter-name>
<filter-class>com.example.springframework.security.header.writer.HeaderWriterFilter</filter-class>
<init-param>
<param-name>Strict-Transport-Security</param-name>
<param-value>max-age=1</param-value>
</init-param>
</filter>
// 错误用例7：Strict-Transport-Security未配置为推荐值 max-age=31536000; includeSubDomains
@Override
public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain)
throws IOException, ServletException {
if (servletResponse instanceof HttpServletResponse) {
HttpServletResponse httpServletResponse = (HttpServletResponse) servletResponse;
httpServletResponse.addHeader("Strict-Transport-Security", "max-age=3600"); // addHeader
httpServletResponse.setHeader("Strict-Transport-Security", "max-age=3600"); // setHeader
}
filterChain.doFilter(servletRequest, servletResponse);
}
// 错误用例8：对请求单独设置Strict-Transport-Security，可能造成部分请求对应的Response响应没有设置。建议通过过滤器、 网关等方式进行全局设置
@GetMapping("/setSingleApiHsts")
public Result setSingleApiHsts(HttpServletResponse response) {
response.addHeader("Strict-Transport-Security", "max-age=31536000; includeSubDomains");
response.setHeader("Strict-Transport-Security", "max-age=31536000; includeSubDomains");
return Result.ok("Success");
}
```
```

**参考信息**

- 最新更新时间：2026-01-21 11:46:12

<a id="rule-325"></a>

### 325. SecH_GTS_JAVA_Header_Reinforcement_XSS_Protection

| 字段 | 内容 |
|---|---|
| 问题级别 | 严重 |
| 语言 | JAVA |
| 标签 | SecBrella |
| 关联工具规则 | SecH_GTS_JAVA_Header_Reinforcement_XSS_Protection |
| 规则类型 | 安全类（非编程规范） |
| 预估误报率 | 50% |

**审查要点**

```text
规则检测以下不安全场景
【细则1】XSS防护头X-XSS-Protection设置不当，存在XSS攻击风险(建议设置为"1;mode=block"）
Web服务器配置场景-Tomcat:通过HttpHeaderSecurityFilter过滤器关闭了X-XSS-Protection头加固-FilterRegistrationBean代码注册过滤器场景：通过HttpHeaderSecurityFilter调用setXssProtectionEnabled关闭类型猜测响应头能力
Web服务器配置场景-Tomcat:通过HttpHeaderSecurityFilter过滤器关闭了X-XSS-Protection头加固-FilterRegistrationBean代码注册过滤器场景：通过设置xssProtectionEnabled初始化参数的方式
Web服务器配置场景-Tomcat:通过HttpHeaderSecurityFilter过滤器关闭了X-XSS-Protection头加固-web.xml配置过滤器场景
Web框架配置场景-example WSF安全框架:hwsf-security.xml配置文件中X-XSS-Protection节点配置disable来关闭默认安全头
Web框架配置场景-example WSF安全框架:通过HeaderWriterFilter过滤器设置X-XSS-Protection的值不正确-FilterRegistrationBean代码注册过滤器场景
Web框架配置场景-example WSF安全框架:通过HeaderWriterFilter过滤器设置X-XSS-Protection的值不正确-web.xml配置过滤器场景
Web框架配置场景-servlet框架:servlet过滤器函数中，通过setHeader/addHeader配置X-XSS-Protection值不合理
Web框架配置场景-Spring Security框架:通过HttpSecurity类调用xssProtection().xssProtectionEnabled(false)函数关闭X-XSS-Protection的安全配置
Web框架配置场景-USecurity安全框架:通过UsHeaderWriterFilter过滤器设置X-XSS-Protection的值不正确-FilterRegistrationBean代码注册过滤器场景
Web框架配置场景-USecurity安全框架:通过UsHeaderWriterFilter过滤器设置X-XSS-Protection的值不正确-web.xml配置过滤器场景
对各个请求单独设置X-XSS-Protection，可能造成部分请求对应的Response响应没有设置（推荐在web容器、nginx、网关、过滤器中统一设置，避免遗漏），setHeader/addHeader方式单独为请求设置控制响应头X-XSS-Protection
规范要求链接：
内部链接已省略 规则22.3
```

**修复建议**

```text
response.setHeader("X-XSS-Protection", "1; mode=block");
Web服务器配置场景-Tomcat:通过HttpHeaderSecurityFilter过滤器开启X-XSS-Protection头加固
Web框架配置场景-example WSF安全框架:hwsf-security.xml配置文件中X-XSS-Protection节点开启安全头
Web框架配置场景-example WSF安全框架:通过HeaderWriterFilter过滤器设置X-XSS-Protection的值为"1; mode=block"
Web框架配置场景-servlet框架:servlet过滤器函数中，通过setHeader/addHeader配置X-XSS-Protection值为"1; mode=block"
Web框架配置场景-Spring Security框架:通过HttpSecurity类调用xssProtection().xssProtectionEnabled(true)函数开启X-XSS-Protection的安全配置
Web框架配置场景-USecurity安全框架:通过UsHeaderWriterFilter过滤器设置X-XSS-Protection的值为"1; mode=block"
不单独为请求设置控制响应头X-XSS-Protection，防止部分请求对应的Response响应没有设置（推荐在web容器、nginx、网关、过滤器中统一设置，避免遗漏）
```

**正确示例**

```text
// 正确用例1：HttpHeaderSecurityFilter过滤器通过设置xssProtectionEnabled初始化参数为true，开启了XSS头加固能力。HttpHeaderSecurityFilter默认安全，可不用手动重复设置
@Bean
public FilterRegistrationBean<HttpHeaderSecurityFilter> httpHeaderSecurityFilterWrongXSSProtect_good001() {
FilterRegistrationBean<HttpHeaderSecurityFilter> headReinforce = new FilterRegistrationBean<>();
headReinforce.setFilter(new HttpHeaderSecurityFilter());
// HttpHeaderSecurityFilter默认安全，可不用代码重复设置
headReinforce.addInitParameter("xssProtectionEnabled", "true");
headReinforce.addUrlPatterns("/**");
headReinforce.setOrder(2);
return headReinforce;
}
// 正确用例2：HttpHeaderSecurityFilter过滤器通过调用setXssProtectionEnabled(true)开启了XSS头加固能力。 HttpHeaderSecurityFilter默认安全，可不用手动重复设置
@Bean
public FilterRegistrationBean<HttpHeaderSecurityFilter> httpHeaderSecurityFilterWrongXSSProtect_good002() {
FilterRegistrationBean<HttpHeaderSecurityFilter> headReinforce = new FilterRegistrationBean<>();
HttpHeaderSecurityFilter httpHeaderSecurityFilter = new HttpHeaderSecurityFilter();
// HttpHeaderSecurityFilter默认安全，可不用代码重复设置
httpHeaderSecurityFilter.setXssProtectionEnabled(true);
headReinforce.setFilter(httpHeaderSecurityFilter);
headReinforce.addUrlPatterns("/**");
headReinforce.setOrder(2);
return headReinforce;
}
// 正确用例3：建议配置X-XSS-Protection的值为1;mode=block
public void doFilter_good(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain)
throws IOException, ServletException {
if (servletResponse instanceof HttpServletResponse) {
HttpServletResponse httpServletResponse = (HttpServletResponse) servletResponse;
httpServletResponse.addHeader("X-XSS-Protection", "1; mode=block"); // addHeader
httpServletResponse.setHeader("X-XSS-Protection", "1; mode=block"); // setHeader
}
filterChain.doFilter(servletRequest, servletResponse);
}
<!--正确用例4：HttpHeaderSecurityFilter过滤器的初始化参数xssProtectionEnabled设置为true，开启了XSS保护头的加固。可无需额外配置，因为HttpHeaderSecurityFilter默认安全值为true-->
<filter>
<filter-name>HttpHeaderSecurityFilterBadGood</filter-name>
<filter-class>org.apache.catalina.filters.HttpHeaderSecurityFilter</filter-class>
<init-param>
<param-name>xssProtectionEnabled</param-name>
<param-value>true</param-value>
</init-param>
</filter>
<!--正确用例5：建议配置X-XSS-Protection的值为1;mode=block-->
<filter>
<filter-name>UsHeaderWriterFilterGood</filter-name>
<filter-class>com.example.us.wsf.filter.UsHeaderWriterFilter</filter-class>
<init-param>
<param-name>X-XSS-Protection</param-name>
<param-value>1;mode=block</param-value>
</init-param>
</filter>
```

**错误示例**

```text
// 错误用例1：HttpHeaderSecurityFilter过滤器通过setInitParameters配置xssProtectionEnabled值为false，关闭了XSS头加固能力
@Bean
public FilterRegistrationBean<HttpHeaderSecurityFilter> httpHeaderSecurityFilterWrongXSSProtect_bad001() {
FilterRegistrationBean<HttpHeaderSecurityFilter> headReinforce = new FilterRegistrationBean<>();
headReinforce.setFilter(new HttpHeaderSecurityFilter());
Map<String, String> initParameters = new HashMap<>();
initParameters.put("xssProtectionEnabled", "false");
headReinforce.setInitParameters(initParameters);
headReinforce.addUrlPatterns("/**");
headReinforce.setOrder(1);
return headReinforce;
}
// 错误用例2：HttpHeaderSecurityFilter过滤器通过addInitParameter配置xssProtectionEnabled值为false，关闭了XSS头加固能力
@Bean
public FilterRegistrationBean<HttpHeaderSecurityFilter> httpHeaderSecurityFilterWrongXSSProtect_bad002() {
FilterRegistrationBean<HttpHeaderSecurityFilter> headReinforce = new FilterRegistrationBean<>();
headReinforce.setFilter(new HttpHeaderSecurityFilter());
headReinforce.addInitParameter("xssProtectionEnabled", "false");
headReinforce.addUrlPatterns("/**");
headReinforce.setOrder(2);
return headReinforce;
}
// 错误用例3：HttpHeaderSecurityFilter过滤器通过调用setXssProtectionEnabled(false)关闭了XSS头加固能力
@Bean
public FilterRegistrationBean<HttpHeaderSecurityFilter> httpHeaderSecurityFilterWrongXSSProtect_bad003() {
FilterRegistrationBean<HttpHeaderSecurityFilter> headReinforce = new FilterRegistrationBean<>();
HttpHeaderSecurityFilter httpHeaderSecurityFilter = new HttpHeaderSecurityFilter();
httpHeaderSecurityFilter.setXssProtectionEnabled(false);
headReinforce.setFilter(httpHeaderSecurityFilter);
headReinforce.addUrlPatterns("/**");
headReinforce.setOrder(2);
return headReinforce;
}
// 错误用例4：未配置X-XSS-Protection的推荐值：1;mode=block
@Override
public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain)
throws IOException, ServletException {
if (servletResponse instanceof HttpServletResponse) {
HttpServletResponse httpServletResponse = (HttpServletResponse) servletResponse;
httpServletResponse.addHeader("X-XSS-Protection", "0"); // addHeader
httpServletResponse.setHeader("X-XSS-Protection", "0"); // setHeader
}
filterChain.doFilter(servletRequest, servletResponse);
}
// 错误用例5：HttpSecurity通过xssProtection().xssProtectionEnabled(false)关闭了XSS头加固的能力
@Override
protected void configure(HttpSecurity http) throws Exception {
http.headers().xssProtection().xssProtectionEnabled(false);
}
<!--错误用例6：HttpHeaderSecurityFilter过滤器的初始化参数xssProtectionEnabled设置为false，关闭了XSS保护头的加固-->
<filter>
<filter-name>HttpHeaderSecurityFilterBad</filter-name>
<filter-class>org.apache.catalina.filters.HttpHeaderSecurityFilter</filter-class>
<init-param>
<param-name>xssProtectionEnabled</param-name>
<param-value>false</param-value>
</init-param>
</filter>
<!--错误用例7：未配置X-XSS-Protection的推荐值：1;mode=block-->
<filter>
<filter-name>UsHeaderWriterFilterBad</filter-name>
<filter-class>com.example.us.wsf.filter.UsHeaderWriterFilter</filter-class>
<init-param>
<param-name>X-XSS-Protection</param-name>
<param-value>0</param-value>
</init-param>
</filter>
// 错误用例8：建议不要在单个接口中配置，通过过滤器、 网关等方式进行全局设置
@GetMapping("/setSingleApiXssProtection")
public Result setSingleApiXssProtection(HttpServletResponse response) {
response.addHeader("X-XSS-Protection", "1;mode=block"); // addHeader
response.setHeader("X-XSS-Protection", "1;mode=block"); // setHeader
return Result.ok("Success");
}
```

**参考信息**

- 最新更新时间：2026-01-21 11:35:28

<a id="rule-326"></a>

### 326. SecH_GTS_JAVA_Header_Reinforcement_X_Frame_Options

| 字段 | 内容 |
|---|---|
| 问题级别 | 严重 |
| 语言 | JAVA |
| 标签 | SecBrella |
| 关联工具规则 | SecH_GTS_JAVA_Header_Reinforcement_X_Frame_Options |
| 规则类型 | 安全类（非编程规范） |
| 预估误报率 | 50% |

**审查要点**

```text
规则检测以下不安全场景
【细则1】页面嵌套属性X-Frame-Options未正确配置为(DENY/SAMEORIGIN)或者禁用，存在点击劫持风险
Web服务器配置场景-Tomcat:通过HttpHeaderSecurityFilter过滤器关闭了X-Frame-Options头加固-FilterRegistrationBean代码注册过滤器场景
Web服务器配置场景-Tomcat:通过HttpHeaderSecurityFilter过滤器关闭了X-Frame-Options头加固-web.xml配置过滤器场景
Web服务器配置场景-Tomcat:通过HttpSecurity类设置X-Frame-Options的header加固项值为ALLOW-FROM，ALLOW-FROM在Chrome和Safari不支持，存在风险-FilterRegistrationBean代码注册过滤器场景
Web服务器配置场景-Tomcat:通过HttpSecurity类设置X-Frame-Options的header加固项值为ALLOW-FROM，ALLOW-FROM在Chrome和Safari不支持，存在风险-web.xml配置过滤器场景
Web框架配置场景-example WSF安全框架:hwsf-security.xml配置文件中frame-options元素属性配置不合理:配置点击劫持头的policy属性为ALLOW-FROM，ALLOW-FROM在Chrome和Safari不支持，存在风险
Web框架配置场景-example WSF安全框架:hwsf-security.xml配置文件中frame-options元素属性配置不合理:通过disable属性关闭了点击劫持头的加固
Web框架配置场景-example WSF安全框架:通过HeaderWriterFilter过滤器设置X-Frame-Options的值为ALLOW-FROM，ALLOW-FROM在Chrome和Safari不支持，存在风险-FilterRegistrationBean代码注册过滤器场景
Web框架配置场景-example WSF安全框架:通过HeaderWriterFilter过滤器设置X-Frame-Options的值为ALLOW-FROM，ALLOW-FROM在Chrome和Safari不支持，存在风险-web.xml配置过滤器场景
Web框架配置场景-servlet框架:servlet过滤器函数中，通过setHeader/addHeader配置X-Frame-Options值为ALLOW-FROM，ALLOW-FROM在Chrome和Safari不支持，存在风险
Web框架配置场景-Spring Security框架:通过HttpSecurity类关闭了frameOptions头加固，存在点击劫持风险
Web框架配置场景-USecurity安全框架:通过UsHeaderWriterFilter过滤器设置X-Frame-Options的值为ALLOW-FROM，ALLOW-FROM在Chrome和Safari不支持，存在风险-FilterRegistrationBean代码注册过滤器场景
Web框架配置场景-USecurity安全框架:通过UsHeaderWriterFilter过滤器设置X-Frame-Options的值为ALLOW-FROM，ALLOW-FROM在Chrome和Safari不支持，存在风险-web.xml配置过滤器场景
对各个请求单独设置X-Frame-Options，可能造成部分请求对应的Response响应没有设置（推荐在web容器、nginx、网关、过滤器中统一设置，避免遗漏），setHeader/addHeader方式单独为请求设置控制响应头X-Frame-Options
规范要求链接：
内部链接已省略 规则22.1
```

**修复建议**

```text
Web服务器配置场景-Tomcat:通过HttpHeaderSecurityFilter过滤器开启X-Frame-Options头加固
Web服务器配置场景-Tomcat:通过HttpSecurity类设置X-Frame-Options的header加固项值不为ALLOW-FROM
Web框架配置场景-example WSF安全框架:通过HeaderWriterFilter过滤器设置X-Frame-Options的值不为ALLOW-FROM
Web框架配置场景-Spring Security框架:通过HttpSecurity类开启frameOptions头加固
Web框架配置场景-USecurity安全框架:通过UsHeaderWriterFilter过滤器设置X-Frame-Options的值不为ALLOW-FROM
不单独设置X-Frame-Options，防止造成部分请求对应的Response响应没有设置（推荐在web容器、nginx、网关、过滤器中统一设置，避免遗漏）
例外场景：
1、响应不是由浏览器处理的请求，可以不设置该响应头；
2、 Nginx容器自动跳转的错误页面（该页面中无法自主添加响应头）；
```

**正确示例**

```text
```
// 正确用例1：通过addInitParameter配置HeaderWriterFilter过滤器的X-Frame-Options值时已经使用推荐值 DENY
@Bean
public FilterRegistrationBean<HeaderWriterFilter> headerWriterFilter_good() {
FilterRegistrationBean<HeaderWriterFilter> headReinforce = new FilterRegistrationBean<>();
headReinforce.setFilter(new HeaderWriterFilter());
headReinforce.addInitParameter("X-Frame-Options", "DENY");
headReinforce.addUrlPatterns("/test/**");
headReinforce.setOrder(3);
return headReinforce;
}
<!--正确用例2：HeaderWriterFilter配置初始化参数X-Frame-Options时已经使用了推荐值 DENY-->
<filter>
<filter-name>HeaderWriterFilterGood</filter-name>
<filter-class>com.example.springframework.security.header.writer.HeaderWriterFilter</filter-class>
<init-param>
<param-name>X-Frame-Options</param-name>
<param-value>DENY</param-value>
</init-param>
</filter>
<http auto-config="true">
<headers>
<!--正确用例3：disabled设置为false，表示不禁用点击劫持头加固能力，此处可不用手动设置，因为默认就是false；同时policy策略设置
为安全的DENY-->
<frame-options disabled="false" policy="DENY"/>
</headers>
</http>
<!--正确用例4: HttpHeaderSecurityFilter过滤器初始化参数antiClickJackingEnabled设置为true，开启了X-Frame-Options的header加固
能力。并且初始化参数antiClickJackingOption已经设置为了推荐值DENY。HttpHeaderSecurityFilter是默认安全的，实际无需代码手动重复设置antiClickJackingEnabled和antiClickJackingOption值-->
<filter>
<filter-name>HttpHeaderSecurityGood</filter-name>
<filter-class>org.apache.catalina.filters.HttpHeaderSecurityFilter</filter-class>
<init-param>
<param-name>antiClickJackingEnabled</param-name>
<param-value>true</param-value>
</init-param>
<init-param>
<param-name>antiClickJackingOption</param-name>
<param-value>DENY</param-value>
</init-param>
</filter>
```
```

**错误示例**

```text
```
// 错误用例1：通过setInitParameters配置HeaderWriterFilter过滤器的X-Frame-Options值为ALLOW-FROM，ALLOW-FROM在Chrome和Safari
@Bean
public FilterRegistrationBean<HeaderWriterFilter> headerWriterFilter_bad001() {
FilterRegistrationBean<HeaderWriterFilter> headReinforce = new FilterRegistrationBean<>();
headReinforce.setFilter(new HeaderWriterFilter());
Map<String, String> initParameters = new HashMap<>();
initParameters.put("X-Frame-Options", "ALLOW-FROM https://example.com/");
headReinforce.setInitParameters(initParameters);
headReinforce.addUrlPatterns("/test/**");
headReinforce.setOrder(1);
return headReinforce;
}
// 错误用例2：通过addInitParameter配置HeaderWriterFilter过滤器的X-Frame-Options值为ALLOW-FROM，ALLOW-FROM在Chrome和Safari均不支持，存在风险。
@Bean
public FilterRegistrationBean<HeaderWriterFilter> headerWriterFilter_bad002() {
FilterRegistrationBean<HeaderWriterFilter> headReinforce = new FilterRegistrationBean<>();
headReinforce.setFilter(new HeaderWriterFilter());
headReinforce.addInitParameter("X-Frame-Options", "ALLOW-FROM https://example.com/");
headReinforce.addUrlPatterns("/test/**");
headReinforce.setOrder(2);
return headReinforce;
}
// 错误用例3：通过HttpSecurity的frameOptions().disable()函数关闭了消减点击劫持的安全响应头X-Frame-Options的加固能力
@Override
protected void configure(HttpSecurity http) throws Exception {
http.headers().frameOptions().disable();
}
// 错误用例4：setHeader/addHeader方式单独为请求设置控制响应头X-Frame-Options，存在遗漏配置风险。建议通过过滤器、网关等方式进行全局设置
@GetMapping("/setSingleApiFrameOptions")
public Result setSingleApiFrameOptions(HttpServletResponse response) {
response.addHeader("X-Frame-Options", "DENY"); // addHeader
response.setHeader("X-Frame-Options", "DENY"); // setHeader
return Result.ok("Success");
}
<!--错误用例5：HeaderWriterFilter过滤器配置X-Frame-Options属性的值为ALLOW-FROM，ALLOW-FROM在Chrome和Safari均不
支持，存在风险。详情查看《公司Web应用安全开发规范》-->
<filter>
<filter-name>HeaderWriterFilterBad</filter-name>
<filter-class>com.example.springframework.security.header.writer.HeaderWriterFilter</filter-class>
<init-param>
<param-name>X-Frame-Options</param-name>
<param-value>ALLOW-FROM https://example.com/</param-value>
</init-param>
</filter>
<http auto-config="true">
<headers>
<!--错误用例6：此处关闭了点击劫持头的加固。请勿手动设置，保持默认值false-->
<frame-options disabled="true"/>
</headers>
<headers>
<!--错误用例7：配置frame-options的policy策略为ALLOW-FROM，ALLOW-FROM在Chrome和Safari均不支持，存在风险。-->
<frame-options policy="ALLOW-FROM" strategy="static" value="https://example.com/"/>
</headers>
</http>
<!--错误用例8: HttpHeaderSecurityFilter过滤器的初始化参数antiClickJackingEnabled设置为false，关闭了X-Frame-Options加固能力-->
<filter>
<filter-name>HttpHeaderSecurityBad001</filter-name>
<filter-class>org.apache.catalina.filters.HttpHeaderSecurityFilter</filter-class>
<init-param>
<param-name>antiClickJackingEnabled</param-name>
<param-value>false</param-value>
</init-param>
</filter>
<!--错误用例9: HttpHeaderSecurityFilter过滤器的初始化参数antiClickJackingOption设置为ALLOW-FROM，
ALLOW-FROM在Chrome和Safari均不支持，存在风险。详情查看《公司Web应用安全开发规范》 -->
<filter>
<filter-name>HttpHeaderSecurityBad002</filter-name>
<filter-class>org.apache.catalina.filters.HttpHeaderSecurityFilter</filter-class>
<init-param>
<param-name>antiClickJackingOption</param-name>
<param-value>ALLOW-FROM https://example.com/</param-value>
</init-param>
</filter>
```
```

**参考信息**

- 最新更新时间：2026-01-21 14:29:42

<a id="rule-327"></a>

### 327. SecH_GTS_JAVA_IP_Management

| 字段 | 内容 |
|---|---|
| 问题级别 | 严重 |
| 语言 | JAVA |
| 标签 | SecBrella |
| 关联工具规则 | SecH_GTS_JAVA_IP_Management |
| 规则类型 | 安全类（非编程规范） |
| 预估误报率 | 20% |

**审查要点**

禁止公网IPV4、IPV6等地址硬编码，包括代码和注解

**修复建议**

不应将公网IPV4、IPV6地址硬编码到代码中

**正确示例**

不应将公网IPV4、IPV6地址硬编码到代码中，包括注解

**错误示例**

```text
```
/**
* 测试IPV4地址使用
*
*/
public static String returnIP(String[] args) {
return "39.38.101.1";
}
/**
* 测试IPV6地址使用
*
*/
public static String returnIP(String[] args){
return "ABCD::EF01:EF01:EF01:EF01";
}
@WebServiceClient(name = "SurveyInfoService",
targetNamespace = "http://webservice.test.com",
wsdlLocation = "http://133.64.36.172:8080/test/CaseSurvey/services/SurveyInfoService?wsdl")
@Controller
public class SurveyInfoService extends Service {
protected SurveyInfoService(URL wsdlDocumentLocation, QName serviceName) {
super(wsdlDocumentLocation, serviceName);
}
protected SurveyInfoService(URL wsdlDocumentLocation, QName serviceName, WebServiceFeature... features) {
super(wsdlDocumentLocation, serviceName, features);
}
@RequestMapping(produces = "/test")
public void test() {
// ....
}
}
```
```

**参考信息**

- 最新更新时间：2026-01-21 14:34:19

<a id="rule-328"></a>

### 328. SecH_GTS_JAVA_Insecure_Cipher_CRC32

| 字段 | 内容 |
|---|---|
| 问题级别 | 严重 |
| 语言 | JAVA |
| 标签 | SecBrella |
| 关联工具规则 | SecH_GTS_JAVA_Insecure_Cipher_CRC32 |
| 规则类型 | 安全类（非编程规范） |
| 预估误报率 | 50% |

**审查要点**

禁止使用CRC32加密敏感数据（匹配变量名为.*(?i)(card|mobileNumber|phone|mac|email|password|pwd|pass|username|user|personName|gender|Marital|blood).*

**修复建议**

不要使用CRC32加密敏感数据（匹配变量名为.*(?i)(card|mobileNumber|phone|mac|email|password|pwd|pass|username|user|personName|gender|Marital|blood).*

**正确示例**

```text
```
无正确示例
```
```

**错误示例**

```text
```
/**
* CRC32加密测试，告警
*
* @return 密文
* @throws IOException
* @since 2020-12-11
*/
public long test2() throws IOException {
CRC32 crc32 = new CRC32();
String pwd = "kaasjfa";
crc32.update(pwd.getBytes("UTF-8"), 1, 5);
return crc32.getValue();
}
```
```

**参考信息**

- 最新更新时间：2024-11-28 09:47:17

<a id="rule-329"></a>

### 329. SecH_GTS_JAVA_Insecure_Protocols_Ftp

| 字段 | 内容 |
|---|---|
| 问题级别 | 严重 |
| 语言 | JAVA |
| 标签 | SecBrella |
| 关联工具规则 | SecH_GTS_JAVA_Insecure_Protocols_Ftp |
| 规则类型 | 安全类（非编程规范） |
| 预估误报率 | 20% |

**审查要点**

禁止使用使用不安全的协议ftp

**修复建议**

禁止使用使用不安全的协议ftp

**正确示例**

```text
```
使用ftps、sftp
```
```

**错误示例**

```text
```
/**
* ftp连接
*
* @param addr ip地址
* @param port 端口
* @param username 用户名
* @param password 密码
* @return 连接结果
* @throws IOException
* @since 2021-02-02
*/
public boolean connectServer(String addr, int port, String username, String password)
throws IOException {
boolean isSuccess = false;
FTPClient ftp = new FTPClient();
ftp.connect(addr, port);
ftp.login(username, password);
isSuccess = true;
return isSuccess;
}
```
```

**参考信息**

- 最新更新时间：2024-11-28 14:59:43

<a id="rule-330"></a>

### 330. SecH_GTS_JAVA_Insecure_Random_UUID_SessionId_Or_TokenId

| 字段 | 内容 |
|---|---|
| 问题级别 | 严重 |
| 语言 | JAVA |
| 标签 | SecBrella |
| 关联工具规则 | SecH_GTS_JAVA_Insecure_Random_UUID_SessionId_Or_TokenId |
| 规则类型 | 安全类（非编程规范） |
| 预估误报率 | 50% |

**审查要点**

禁止使用UUID生产的随机数作为tokenid，sessionid

**修复建议**

使用安全随机数生成SeesionId和tokenId

**正确示例**

```text
```
/**
* sessionId由安全随机数生成器测试,不告警
*
* @param request 请求
* @throws NoSuchAlgorithmException
* @author 何倩茹/hwx978855
* @since 2020-12-15
*/
public void test7(HttpServletRequest request) throws NoSuchAlgorithmException {
SecureRandom random = SecureRandom.getInstanceStrong();
byte[] sessionId = new byte[8];
random.nextBytes(sessionId);
HttpSession session = request.getSession();
session.setAttribute("session", sessionId);
}
```
```

**错误示例**

```text
```
public void test6(HttpServletRequest request) {
UUID sessionId = UUID.randomUUID();
HttpSession session = request.getSession();
session.setAttribute("sessionid", sessionId.toString());
}
```
```

**参考信息**

- 最新更新时间：2025-05-09 09:03:03

<a id="rule-331"></a>

### 331. SecH_GTS_JAVA_Insecure_SSL_TLS_Protocols

| 字段 | 内容 |
|---|---|
| 问题级别 | 严重 |
| 语言 | JAVA |
| 标签 | SecBrella |
| 关联工具规则 | SecH_GTS_JAVA_Insecure_SSL_TLS_Protocols |
| 规则类型 | 安全类（非编程规范） |
| 预估误报率 | 20% |

**审查要点**

检测java中使用不安全的协议TLSv1.0,TLSv1.1,SSLV3.0,SSLV2.0,SSHv1,IKEv1

**修复建议**

```text
根据业务需求选择安全协议
扩展资料：内部链接已省略
```

**正确示例**

禁止使用不安全的协议TLSv1.0,TLSv1.1,SSLV3.0,SSLV2.0,SSHv1,IKEv1

**错误示例**

```text
```
/**
* UsCertValidationSSLContextWrapper实现TLSv1.1，告警
*
* @return UsCertValidationSSLContextWrapper
* @throws NoSuchAlgorithmException
* @create 2020-12-30 15:57
*/
public UsCertValidationSSLContextWrapper getUsCertValidationSSLContextWrapper() throws NoSuchAlgorithmException {
UsCertValidationSSLContextWrapper sslContextWrapper = UsCertValidationSSLContextWrapper.getInstance("TLSv1.1");
return sslContextWrapper;
}
```
```

**参考信息**

- 最新更新时间：2026-01-21 14:06:21

<a id="rule-332"></a>

### 332. SecH_GTS_JAVA_JDBC_Resource_Release

| 字段 | 内容 |
|---|---|
| 问题级别 | 严重 |
| 语言 | JAVA |
| 标签 | SecBrella |
| 关联工具规则 | SecH_GTS_JAVA_JDBC_Resource_Release |
| 规则类型 | 安全类（非编程规范） |
| 预估误报率 | 50% |

**审查要点**

JDBC相关资源PreparedStatement、ResultSet、Statement、Connection未得到正确关闭或释放，可能导致资源泄露等问题。暂未考虑close中封装可以抛函数的情况。

**修复建议**

```text
1、使用try-with-resources。
2、在没有使用try-with-resources的情况下，在finally里关闭。
```

**正确示例**

```text
```
// 正确用例：PreparedStatement 对象利用try-with-resource做到退出时是释放
public static void good001(Connection conn) throws ClassNotFoundException {
try(PreparedStatement preparedStatement = conn.prepareStatement(QUERY_SQL)) {
BusinessMock.doBusinessWithException(preparedStatement);
} catch (SQLException | BusinessException exception) {
LOGGER.error("sql error!");
}
}
```
```

**错误示例**

```text
```
// 错误用例：else分支中存在异常抛出问题，导致close资源释放操作未被执行
public static void bad003(Connection conn, boolean isTrue) throws SQLException, BusinessException {
PreparedStatement preparedStatement = conn.prepareStatement(QUERY_SQL);
if (isTrue) {
BusinessMock.doBusinessWithoutException(preparedStatement);
preparedStatement.close();
} else {
BusinessMock.doBusinessWithException(preparedStatement); // 该函数抛出异常后，将导致后续close未被执行，产生资源未释放问题
preparedStatement.close();
}
}
```
```

**参考信息**

- 最新更新时间：2026-01-21 14:06:53

<a id="rule-333"></a>

### 333. SecH_GTS_JAVA_JDK_IO_Stream_Release

| 字段 | 内容 |
|---|---|
| 问题级别 | 严重 |
| 语言 | JAVA |
| 标签 | SecBrella |
| 关联工具规则 | SecH_GTS_JAVA_JDK_IO_Stream_Release |
| 规则类型 | 安全类（非编程规范） |
| 预估误报率 | 50% |

**审查要点**

```text
JDK内部继承java.io.InputStream|java.io.OutputStream|java.io.Closeable|java.io.Reader|java.io.Writer的类对象未得到关闭，可能导致资源泄露等问题。常见的包括：FileinputStream/FileOutPutStream、FileReader/FileWriter、RandomAccessFile、ObjectInputStream/ObjectOutputStream等等。
```

**修复建议**

```text
1、使用try-with-resources。
2、在没有使用try-with-resources的情况下，在finally里关闭。
```

**正确示例**

```text
```
// 正确用例：使用try-with-resource处理所有流
public static void good001(File inputFile, File outputFile) {
try (FileInputStream fileInputStream = new FileInputStream(inputFile);
FileOutputStream fileOutputStream = new FileOutputStream(outputFile)) {
BusinessMock.doBusinessWithException(fileInputStream, fileOutputStream);
} catch (IOException | BusinessException exception) {
LOGGER.error("io error!");
}
}
```
```

**错误示例**

```text
```
// 错误用例：字节节点输入流FileInputStream未在finally中关闭，异常抛出后未执行到close函数
public static void bad00300(File inputFile, boolean isTrue) throws IOException, BusinessException {
FileInputStream fileInputStream = new FileInputStream(inputFile);
if (isTrue) {
BusinessMock.doBusinessWithoutException(fileInputStream);
fileInputStream.close();
} else {
BusinessMock.doBusinessWithException(fileInputStream); // 该函数抛出异常后，将导致后续close未被执行，产生流资源泄露问题
fileInputStream.close();
}
}
```
```

**参考信息**

- 最新更新时间：2026-01-21 11:47:56

<a id="rule-334"></a>

### 334. SecH_GTS_JAVA_JasperReport_Command_Injection

| 字段 | 内容 |
|---|---|
| 问题级别 | 严重 |
| 语言 | JAVA |
| 标签 | SecBrella |
| 关联工具规则 | SecH_GTS_JAVA_JasperReport_Command_Injection |
| 规则类型 | 安全类（非编程规范） |
| 预估误报率 | 50% |

**审查要点**

```text
JasperReports 是一个开源的 Java 报表引擎，拥有将内容转换成PDF，HTML，XLS，RTF，ODT，CSV，TXT和XML文件的能力。JasperReports 提供报表表达式，可以调用 Java 对象类型来定制化实现报表内容的展现，也可以调用 Runtime 静态方法执行任意系统命令。若服务允许用户自定义上传模板，且未做相关校验或防御，则会引入命令执行风险。
```

**修复建议**

```text
1、禁止用户上传jrxml、jasper报表模板文件，防止代码注入
2、渲染数据之前进行白名单校验表达式和数据
```

**正确示例**

```text
```
public static JasperPrint fillReport(InputStream inputStream, Map<String, Object> parameters, JRDataSource dataSource) throws CommandInjectionException, JRException {
JasperReport jasperReport = (JasperReport) JRLoader.loadObject(inputStream);
// 重写jasperReport校验方法
JRBand[] jrBands = jasperReport.getAllBands();
checkDataSourceCommandInjection(dataSource);
// 检查值是否有命令注入
if (jrBands != null) {
for (JRBand jrBand : jrBands) {
checkValueCommandInjection((JRBaseBand) jrBand);
}
}
JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, parameters, dataSource);
return jasperPrint;
}
```
```

**错误示例**

```text
```
// 编译
JasperReport jasperReport = JasperCompileManager.compileReport(ResourceUtils.getFile("classpath:employees-details.jrxml").getAbsolutePath());
FileInputStream fis = new FileInputStream("Foo");
// 填充数据
// 此处应有告警
JasperPrint jasperPrint1 = JasperFillManager.fillReport(fis, empParams, new JREmptyDataSource());
```
```

**参考信息**

- 最新更新时间：2025-01-26 17:16:49

<a id="rule-335"></a>

### 335. SecH_GTS_JAVA_Jdbc_Attack_UnSafe_Url

| 字段 | 内容 |
|---|---|
| 问题级别 | 严重 |
| 语言 | JAVA |
| 标签 | SecBrella |
| 关联工具规则 | SecH_GTS_JAVA_Jdbc_Attack_UnSafe_Url |
| 规则类型 | 安全类（非编程规范） |
| 预估误报率 | 50% |

**审查要点**

针对DriverManager/JdbcTemple/dbcp/c3p0/德鲁伊等外部动态传入url 则告警

**修复建议**

```text
针对DriverManager/JdbcTemple/dbcp/c3p0/德鲁伊等组件，禁止外部数据未经校验作为URL参数传入
如果外部参数可以让URL添加autoDeserialize=true和allowUrlInloadLocal=true 参数，则可以对数据库进行攻击，执行任意代码，应该过滤这些参数
扩展资料：内部链接已省略
```

**正确示例**

```text
```
@GetMapping("/c3p0/Four")
@ResponseBody
public void c3p0Four(HttpServletRequest request) {
String driver = "com.mysql.cj.jdbc.Driver";
String url = request.getParameter("url");
String username = request.getParameter("username");
String password = request.getParameter("password");
DriverManagerDataSource dataSource = new DriverManagerDataSource();
dataSource.setJdbcUrl(check(url)); // 对外部输入进行校验
dataSource.setPassword(password);
dataSource.setDriverClass(driver);
dataSource.setUser(username);
JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
}
```
```

**错误示例**

```text
```
@GetMapping("/c3p0/Two")
@ResponseBody
public void c3p0Two(HttpServletRequest request) {
String driver = "com.mysql.cj.jdbc.Driver";
String url = request.getParameter("url");
String username = request.getParameter("username");
String password = request.getParameter("password");
DriverManagerDataSource dataSource = new DriverManagerDataSource();
dataSource.setJdbcUrl(url);
dataSource.setPassword(password);
dataSource.setDriverClass(driver);
dataSource.setUser(username);
JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
}
```
```

**参考信息**

- 最新更新时间：2025-01-26 17:17:07

<a id="rule-336"></a>

### 336. SecH_GTS_JAVA_Jdbc_Attack_Url_Hardcoded

| 字段 | 内容 |
|---|---|
| 问题级别 | 严重 |
| 语言 | JAVA |
| 标签 | SecBrella |
| 关联工具规则 | SecH_GTS_JAVA_Jdbc_Attack_Url_Hardcoded |
| 规则类型 | 安全类（非编程规范） |
| 预估误报率 | 50% |

**审查要点**

JDBC url 不安全的反序列化，检测java.sql.包下的DriverManager类的方法getConnection(...)的 第一个参数也就是JDBC连接串，如果autoDeserialize=true那么就会告警

**修复建议**

禁止开启autoDeserialize=true

**正确示例**

检测java.sql.包下的DriverManager类的方法getConnection(...)的 第一个参数也就是JDBC连接串，如果autoDeserialize=true那么就会告警

**错误示例**

```text
```
String urlTwo = url + "&autoDeserialize=true";
Connection connOne = DriverManager.getConnection(urlTwo);
```
```

**参考信息**

- 最新更新时间：2024-11-28 14:15:36

<a id="rule-337"></a>

### 337. SecH_GTS_JAVA_JtaTransactionManager

| 字段 | 内容 |
|---|---|
| 问题级别 | 严重 |
| 语言 | JAVA |
| 标签 | SecBrella |
| 关联工具规则 | SecH_GTS_JAVA_JtaTransactionManager |
| 规则类型 | 安全类（非编程规范） |
| 预估误报率 | 50% |

**审查要点**

org.springframework.transaction.jta.JtaTransactionManager.setUserTransactionName()存在反序列化风险,需要校验setUserTransactionName的入参，确保入参即IP地址无误

**修复建议**

使用check|valid|verify|safe|filter等方法进行校验

**正确示例**

```text
```
public static void goodCase(HttpServletRequest request){
String title = request.getHeader("title");
String titleChecked = check(title);
JtaTransactionManager object = new JtaTransactionManager();
object.setUserTransactionName(titleChecked );
}
private static String check(String title) {
// todo 校验
return title;
}
```
```

**错误示例**

```text
```
public static void badCase(HttpServletRequest request){
String title = request.getHeader("title");
JtaTransactionManager object = new JtaTransactionManager();
object.setUserTransactionName(title);
}
```
```

**参考信息**

- 最新更新时间：2025-01-26 17:17:25

<a id="rule-338"></a>

### 338. SecH_GTS_JAVA_Log_Leak_Pwd

| 字段 | 内容 |
|---|---|
| 问题级别 | 严重 |
| 语言 | JAVA |
| 标签 | SecBrella |
| 关联工具规则 | SecH_GTS_JAVA_Log_Leak_Pwd |
| 规则类型 | 安全类（非编程规范） |
| 预估误报率 | 50% |

**审查要点**

```text
Java语言安全编程规范G.LOG.06 禁止在日志中保存口令，密钥和其他敏感信息,包括这些敏感信息的加密密文，防止产生敏感信息泄露风险.
【告警场景】变量名称中包含token,tokenId,password,pwd,pass,pswd,privatekey等关键字，使用日志API写入日志/文件/存储介质中告警，请严格检测数据流中所包含的关键字是否为敏感信息，若为敏感信息，请严格脱敏后使用。
【提示信息】此场景为Source点正则匹配关键词，为严重问题，为防止敏感信息泄露，存在一定误报可能，请严格排查写出信息是否为关键敏感信息，如果不是，请屏蔽。
【误报提示】此规则会告警IO流写入的场景，如果确定IO流并非向文件里写日志，可申请屏蔽。
```

**修复建议**

禁止将token|tokenid|password|pwd|pswd|cert|privatekey|tokeninfo|configkey|passwordkey|secretkey|appcode|cakey|serverkey等敏感信息写入日志、文件、存储介质中

**正确示例**

脱敏、不要记录敏感信息

**错误示例**

```text
```
@RequestMapping(value =”/home”, method = RequestMethod.GET)
public String showHomePage(HttpServletRequest req, HttpServletResponse rep) {
String tokenId = req.getParameter(“tokenId”);
String password = req.getParameter(“password”);
LOGGER.error(tokenId);
LOGGER.info(password);
return "index";
}
```
```

**参考信息**

- 最新更新时间：2026-02-10 09:15:38

<a id="rule-339"></a>

### 339. SecH_GTS_JAVA_NIO_Files_CLOSE

| 字段 | 内容 |
|---|---|
| 问题级别 | 严重 |
| 语言 | JAVA |
| 标签 | SecBrella |
| 关联工具规则 | SecH_GTS_JAVA_NIO_Files_CLOSE |
| 规则类型 | 安全类（非编程规范） |
| 预估误报率 | 50% |

**审查要点**

NIO操作文件流，Files.lines()|Files.list()|Files.walk()|Files.find()等返回文件流需要手动关流。

**修复建议**

NIO操作文件流,没有自动关闭，需要手动关流。

**正确示例**

```text
```
/**
* 使用close关闭
* 不需告警
*
* @remark: created by 王小龙/xwx834200 at 2020/3/23
*/
public static void testThree_good() {
String path = "D://modelFour.sql";
Stream<String> stream = null;
try {
stream = Files.lines(Paths.get(path));
stream.forEach(System.out::println);
} catch (IOException e) {
//开发自行编写日志
} finally {
if (null != stream) {
stream.close();
}
}
}
```
```

**错误示例**

```text
```
/**
* 未关闭
* 需告警
*
* @remark: created by 王小龙/xwx834200 at 2020/3/23
*/
public static void testTwo_bad() {
String path = "D://modelFour.sql";
try {
// POTENTIAL FLAW:[[@LINE+1]]
Stream<String> stream = Files.lines(Paths.get(path));
stream.forEach(System.out::println);
} catch (IOException e) {
//开发自行编写日志
}
}
```
```

**参考信息**

- 最新更新时间：2026-01-21 14:27:05

<a id="rule-340"></a>

### 340. SecH_GTS_JAVA_Non_Thread_Safe_Obj

| 字段 | 内容 |
|---|---|
| 问题级别 | 严重 |
| 语言 | JAVA |
| 标签 | SecBrella |
| 关联工具规则 | SecH_GTS_JAVA_Non_Thread_Safe_Obj |
| 规则类型 | 安全类（非编程规范） |
| 预估误报率 | 20% |

**审查要点**

```text
并发环境中使用线程不安全对象：多线程并发操作时，如果多个线程同时对同一个不可重入类对象进行读写操作，可能会导致数据的不一致性或者错误的结果。
说明：调用方可能对不安全的Runable进行过安全处理，本规则主要检测可能存在多线程使用非线程安全成员对象的场景，因此若Ruanble类中Run方法若存在使用非线程安全成员对象可能会出现告警
```

**修复建议**

```text
多线程场景中，建议使用线程安全类对象替换非线程安全类对象：
1.用原子类对象AtomicInteger.incrementAndGet替换变量自增++或+1场景。
2.使用StringBuffer线程安全对象替换StringBuilder线程不安全对象
3.ArrayList和LinkedList属于非线程安全类型，Vector、Stack、CopyOnWriteArrayList、Collections.synchronizedList(new ArrayList<>())属于线程安全类型
4.HashMap、LinkedHashMap、TreeMap和IdentityHashMap属于非线程安全类型，Hashtable、ConcurrentHashMap、ConcurrentSkipListMap和Collections.synchronizedMap(new HashMap<>())属于线程安全类型
5.PriorityQueue和ArrayDeque属于非线程安全类型，ConcurrentLinkedQueue、ConcurrentLinkedDeque、LinkedBlockingQueue、PriorityBlockingQueue和ArrayBlockingQueue属于线程安全类型
6.HashSet、LinkedHashSet和TreeSet属于非线程安全类型，CopyOnWriteArraySet、ConcurrentSkipListSet和Collections.synchronizedSet(new HashSet<>())属于线程安全类型
7.实现Runnable、继承TimerTask、继承Thread重写Run方法的场景中若使用了非线程安全类对象可以替换成线程安全类型或者加锁例如synchronized、lock.lock();
```

**正确示例**

```text
```
1.Runable、Thread中Run方法存在非线程安全类型正确示例：
@Override
public void run() {
lock.lock();
try {
arrayList.add("abc");
} finally {
lock.unlock();
}
}
@Override
public void run() {
synchronized (lock) { // 显式对象锁
arrayList.add("abc");
}
}
List<Integer> synchronizedList = Collections.synchronizedList(new ArrayList<>());
@Override
public void run() {
synchronizedList.add("abc");
}
2.Lambda、匿名Lambda 表达式使用非安全类型正确示例（还可以将非安全类型成员变量替换掉）：
Runnable safeHashMapTask = () -> {
int idx = 0;
while (idx++ < 50) {
synchronized (hashMap) {
hashMap.put(idx, String.valueOf(idx));
}
}
};
for (int i = 0; i < 10; i++) {
new Thread(() -> {
synchronized (arrayList) {
// Lambda表达式访问共享集合
for (int j = 0; j < 1000; j++) {
arrayList.add(j); // 非原子操作
}
}
}).start();
}
3.字符串类操作正确示例：
Runnable bufferTask = () -> {
int idx = 0;
while (idx++ < 50) {
stringBuffer.append("A");
}
};
4.集合类操作正确示例：
Runnable goodeCase = () -> {
int idx = 0;
while (idx++ < 50) {
if (!copyOnWriteArrayList.contains(idx)) {
copyOnWriteArrayList.add(idx);
}
}
};
```
```

**错误示例**

```text
```
1.原子类操作错误示例：
Runnable task = () -> {
int idx = 0;
while (idx++ < 10000) {
// count++;count += 1;count = count + 1;均为非原子性操作，线程不安全
count ++;
}
};
2.字符串类操作错误示例：
Thread thread = new Thread(() -> {
int idx = 0;
while (idx++ < 50) {
stringBuilder.append("A");
}
});
3.集合类操作错误示例：
Runnable arrayListTask = () -> {
int idx = 0;
while (idx++ < 50) {
arrayList.add(idx);
}
};
1.原子类操作正确示例：
Runnable goodCase = () -> {
int idx = 0;
while (idx++ < 10000) {
// AtomicInteger提供原子操作来进行Integer的使用，适合并发情况下的使用，atomicCount最终的值为20000
atomicCount.incrementAndGet();
}
};
```
```

**参考信息**

- 最新更新时间：2026-01-21 11:41:45

<a id="rule-341"></a>

### 341. SecH_GTS_JAVA_Not_Check_Domain

| 字段 | 内容 |
|---|---|
| 问题级别 | 严重 |
| 语言 | JAVA |
| 标签 | SecBrella |
| 关联工具规则 | SecH_GTS_JAVA_Not_Check_Domain |
| 规则类型 | 安全类（非编程规范） |
| 预估误报率 | 20% |

**审查要点**

创建SSLConnectionSocketFactory对象时，检测对域名的校验，连接的主机域名和证书中一致，防止中间人攻击

**修复建议**

```text
创建SSLConnectionSocketFactory对象时,要校验域名，防止中间人攻击
其他参考资料：内部链接已省略
内部链接已省略
内部链接已省略
```

**正确示例**

```text
```
/**
* 创建SSLConnectionSocketFactory
*
* @param keyStoreType keyStoreType
* @param keyPassword keyPassword
* @return SSLConnectionSocketFactory
* @throws NoSuchAlgorithmException
* @throws KeyStoreException
* @throws KeyManagementException
* @create 2021-01-05 10:46
*/
public SSLConnectionSocketFactory sslFactory(String keyStoreType, String keyPassword)
throws NoSuchAlgorithmException, KeyStoreException, KeyManagementException {
SSLConnectionSocketFactory sslConnectionSocketFactory = null;
SSLContext sslcontext = SSLContexts.custom()
//忽略掉对服务器端证书的校验,不告警
.loadTrustMaterial(new TrustStrategy() {
@Override
public boolean isTrusted(X509Certificate[] chain, String authType) throws CertificateException {
SingleResp singleResp = null;
singleResp.getCertStatus();
return true;
}
}).build();
sslConnectionSocketFactory = new SSLConnectionSocketFactory(sslcontext, new String[]{"TLSv1"}, null,
SSLConnectionSocketFactory.getDefaultHostnameVerifier());
return sslConnectionSocketFactory;
}
```
```

**错误示例**

```text
```
/**
* 创建SSLConnectionSocketFactory
*
* @return SSLConnectionSocketFactory
* @throws NoSuchAlgorithmException
* @throws KeyManagementException
* @create 2021-01-05 10:28
*/
private SSLConnectionSocketFactory buildSSLSocketFactory()
throws NoSuchAlgorithmException, KeyManagementException {
SSLContext sslContext = SSLContext.getInstance("SSL");
// 设置信任证书（绕过TrustStore验证）
sslContext.init(null, new TrustManager[]{new AuthX509TrustManager()}, null);
HttpsURLConnection.setDefaultSSLSocketFactory(sslContext.getSocketFactory());
SSLConnectionSocketFactory sslConnectionSocketFactory = new SSLConnectionSocketFactory(sslContext,
new String[]{"TLSv1"}, null, new HostnameVerifier() {
// hostname,默认返回true,不验证hostname
@Override
public boolean verify(String urlHostName, SSLSession session) {
return true;
}
});
return sslConnectionSocketFactory;
}
```
```

**参考信息**

- 最新更新时间：2026-01-21 14:13:41

<a id="rule-342"></a>

### 342. SecH_GTS_JAVA_Office_RW_Exhaustion

| 字段 | 内容 |
|---|---|
| 问题级别 | 严重 |
| 语言 | JAVA |
| 标签 | SecBrella |
| 关联工具规则 | SecH_GTS_JAVA_Office_RW_Exhaustion |
| 规则类型 | 安全类（非编程规范） |
| 预估误报率 | 50% |

**审查要点**

```text
Apache POI组件解析/创建xlsx、doc、ppt等类型文件导致的内存资源消耗多的问题
检测场景一（写xlsx）：使用XSSFWorkbook构造xlsx文件(写xlsx)造成内存消耗高的问题，请使用SXSSFWorkbook替换，并限制刷新大小。修改请参考正确示例一
检测场景二（读xlsx、ppt、doc）：使用XSSFWorkbook\XWPFDocument\XMLSlideShow\NiceXWPFDocument类读文件造成内存消耗高的问题，请通过POI提供参数加固解压和读取的大小限制。修改请参考正确示例二
特别注意，不可行修改：new SXSSFWorkbook(new XSSFWorkbook(file))，该修改方式无法解决内存消耗高的问题
检测场景三：使用CSVReader.readNext、csvReader.readNextSilently、csvReader.readAll解析不可信csv文件造成内存消耗过大的问题。请对不可信csv文件进行校验。
【特殊】读取模版后写入。需要结合场景一、场景二：
1、通过XSSFWorkbook读取模版，并通过POI提供参数加固解压和读取的大小限制
2、使用上述XSSFWorkbook对象构造SXSSFWorkbook对象并限制刷新大小
当前该规则会漏报第2点，请自行排查
```

**修复建议**

```text
1、使用SXSSFWorkbook替代XSSFWorkbook构造xlsx文件，并限制刷新大小。
2、使用XSSFWorkbook\XWPFDocument\XMLSlideShow\NiceXWPFDocument类读文件时，通过POI提供参数加固解压和读取的大小限制，避免内存过度消耗问题。
3、解析不可信csv文件前，进行校验，避免造成内存消耗过大的问题。
```

**正确示例**

```text
```
// 正确示例一：使用SXSSFWorkbook取代XSSFWorkbook动态创建xlsx文件，并设置固定的值指定内存始终保存的行数
public void good001() throws IOException {
String rootDir = Objects.requireNonNull(this.getClass().getResource("/")).getPath();
try (FileOutputStream out = new FileOutputStream(rootDir + "xlsxfile" + File.separator + "newxlsx.xlsx");
// keep 100 rows in memory, exceeding rows will be flushed to disk
SXSSFWorkbook wb = new SXSSFWorkbook(100)) {
Sheet sh = wb.createSheet();
for (int rownum = 0; rownum < 10000; rownum++) {
Row row = sh.createRow(rownum);
for (int cellnum = 0; cellnum < 256; cellnum++) {
Cell cell = row.createCell(cellnum);
cell.setCellValue("hello world!");
}
}
wb.write(out);
wb.dispose();
}
}
// 正确示例二：通过POI提供参数加固解压和读取的大小限制，避免内存过度消耗问题
// 注：需要将Apache POI升级到当前GA的版本5.2.5
// 示例中设置的参数值只作参考，实际应根据业务需求进行配置
public void good001() throws IOException {
// 下面2个值实际可以防御解压的总大小问题
ZipSecureFile.setMaxEntrySize(1 * 1024 * 1024); // 限制单个条目解压后大小不超过1M
ZipSecureFile.setMaxFileCount(35); // 文件数量,一般正常的表格文件内部基础的有30个左右的文件，可结合业务的表格，改成zip文件解压看
// 或通过压缩率解决，例如首先校验文件大小是10M，若内存压力最多是50M，则设置压缩率为10/50=0.2
// ZipSecureFile.setMinInflateRatio(0.5); // 压缩率=压缩后文件大小/压缩前文件大小；默认0.01
// ZipSecureFile.setGraceEntrySize(0); // 条目解压后大小的安全阈值：entry解压后大小 小于 该值时，绕过压缩率校验。特别的，设置成0，强制所有的entry进行压缩率校验；默认100K
ZipSecureFile.setMaxTextSize(10 * 1024 * 1024); // 后续读文件内容时，文件内容解析的总大小 10M；默认10M
String rootDir = Objects.requireNonNull(this.getClass().getResource("/")).getPath();
File file = new File(rootDir + "xlsxfile" + File.separator + "dos.xlsx");
try (InputStream is = new FileInputStream(file); Workbook workbook = new XSSFWorkbook(is)) {
LOGGER.info("start business!");
// 后续业务代码逻辑略......
}
}
```
```

**错误示例**

```text
```
// 错误示例一：使用XSSFWorkbook动态构造仅29MB内容，导致内存占用异常
public void badCase() throws IOException {
String rootDir = Objects.requireNonNull(this.getClass().getResource("/")).getPath();
try (FileOutputStream out = new FileOutputStream(rootDir + "xlsxfile" + File.separator + "newxlsx.xlsx");
XSSFWorkbook wb = new XSSFWorkbook()) {
// 单从内容上来看，表格内容占用量为10000*256*12≈29MB，貌似不会占用过多的内存空间。然而，从实际运行监控内存达到3.2Gb
// 如果不控制xlsx文件构造的内容，很容易造成内存资源的泄露
// XSSFWorkbook内存消耗问题内源原理参考文档：内部链接已省略
Sheet sh = wb.createSheet();
for (int rownum = 0; rownum < 10000; rownum++) {
Row row = sh.createRow(rownum);
for (int cellnum = 0; cellnum < 256; cellnum++) {
Cell cell = row.createCell(cellnum);
cell.setCellValue("hello world!");
}
}
wb.write(out);
}
}
// 错误示例二：直接使用XSSFWorkbook实例化xlsx文档，没有做任何限制，存在内存耗尽的风险
public void badCase() throws IOException {
// 1、获取xlsx文件，工程内的resource/xlsxfile目录下的dos.xlsx文档，可造成下述代码大量的内存资源消耗
String rootDir = Objects.requireNonNull(this.getClass().getResource("/")).getPath();
File file = new File(rootDir + "xlsxfile" + File.separator + "dos.xlsx");
// 2、XSSFWorkbook对象未进行任何安全限制措施直接解析xlsx文件，可使用jconsole工具查看到仅不到6M大小的文件在解析时占用了1.6Gb的内存
// 如果上述dos.xlsx的文件再构造大一点,或者并发解析多个文件，将导致内存资源耗尽问题
// XSSFWorkbook内存消耗问题内源原理参考文档：内部链接已省略
try (InputStream is = new FileInputStream(file); Workbook workbook = new XSSFWorkbook(is)) {
LOGGER.info("start business!");
// 后续业务代码逻辑略......
}
}
```
```

**参考信息**

- 最新更新时间：2026-02-09 15:06:29

<a id="rule-343"></a>

### 343. SecH_GTS_JAVA_Plaintext_Protocol

| 字段 | 内容 |
|---|---|
| 问题级别 | 严重 |
| 语言 | JAVA |
| 标签 | SecBrella |
| 关联工具规则 | SecH_GTS_JAVA_Plaintext_Protocol |
| 规则类型 | 安全类（非编程规范） |
| 预估误报率 | 20% |

**审查要点**

```text
敏感数据使用明文传输协议。包含使用OkHttp、retrofit、HttpClient组件通过HTTP协议传输敏感数据，使用JDK原生(HttpURLConnection)API传输敏感数据，使用Spring RestTemplate组件通过HTTP协议传输敏感数据；使用commons-net组件通过telnet协议传输敏感数据。
```

**修复建议**

避免使用明文协议传输敏感信息。

**正确示例**

```text
```
// 正确用例：创建使用https协议的安全OkHttpClient对象
public String good001(UserLoginInfo userLoginInfo) throws IOException {
OkHttpClient okHttpClient = getSecureHttpsClient(); // 获取https协议的OkHttpClient对象
if (Objects.isNull(okHttpClient)) {
return "";
}
Request request = new Request.Builder().url(Constant.TEST_HTTPS_URL)
.post(RequestBody.create(MediaType.parse("application/json"), JSON.toJSONString(userLoginInfo)))
.build();
try (Response response = okHttpClient.newCall(request).execute()) {
return response.body().string();
}
}
/**
* 创建OKHttpClient
*/
@Nullable
public static OkHttpClient getSecureHttpsClient() {
SSLContext sslContext = SSLContextUtils.getSecureSSLContext(Constant.KEY_STORE_PATH, Constant.KEY_STORE_PWD,
Constant.TRUST_STORE_PATH, Constant.TRUST_STORE_PWD);
TrustManagerFactory trustManagerFactory = SSLContextUtils.getTrustManagerFactory(Constant.TRUST_STORE_PATH,
Constant.TRUST_STORE_PWD);
if (Objects.isNull(sslContext) || Objects.isNull(trustManagerFactory)) {
return null;
}
SSLSocketFactory socketFactory = sslContext.getSocketFactory();
ConnectionSpec connectionSpec = new ConnectionSpec.Builder(ConnectionSpec.MODERN_TLS)
.tlsVersions(TlsVersion.TLS_1_2)
.cipherSuites(CipherSuite.TLS_ECDH_RSA_WITH_AES_256_GCM_SHA384)
.build();
return new OkHttpClient.Builder()
.sslSocketFactory(socketFactory, (X509TrustManager) trustManagerFactory.getTrustManagers()[0])
.connectionSpecs(Collections.singletonList(connectionSpec))
.hostnameVerifier(new DefaultHostnameVerifier())
.build();
}
```
```

**错误示例**

```text
```
// 错误用例：OkHttpClient使用http协议传输UserLoginInfo敏感对象
public String bad001(UserLoginInfo userLoginInfo) throws IOException {
OkHttpClient okHttpClient = new OkHttpClient();
Request request = new Request.Builder().url(Constant.TEST_HTTP_URL)
.post(RequestBody.create(MediaType.parse("application/json"), JSON.toJSONString(userLoginInfo)))
.build();
try (Response response = okHttpClient.newCall(request).execute()) {
return response.body().string();
}
}
```
```

**参考信息**

- 最新更新时间：2026-01-21 14:20:12

<a id="rule-344"></a>

### 344. SecH_GTS_JAVA_Prohibited_Configure_Plaintext_Password

| 字段 | 内容 |
|---|---|
| 问题级别 | 严重 |
| 语言 | JAVA |
| 标签 | SecBrella |
| 关联工具规则 | SecH_GTS_JAVA_Prohibited_Configure_Plaintext_Password |
| 规则类型 | 安全类（非编程规范） |
| 预估误报率 | 20% |

**审查要点**

禁止配置文件中明文密码存储

**修复建议**

配置文件中明文密码使用正确的加密方式存储

**正确示例**

配置文件中明文密码使用正确的加密方式存储

**错误示例**

```text
```
properties中
gts.test.pwd=123456
gts.test.pass=klfsaf254fsa7*
gts.test.word=@%@#@%0_safsdf
gts.test.pword=+_)(*&^%$#@!1235sdaa
gts.test.paswd=mfhas@@4tr554&*56
gts.test.pswd=ifisf?><M31236%$##
xml中
<value>
<pass>CharacterEncodingFilter</pass>
<word>org.springframework</word><paswd>encoding</paswd>
<param>
<paswd>encoding</paswd>
<pswd>UTF-8</pswd>
</param>
</value>
```
```

**参考信息**

- 最新更新时间：2026-01-21 14:25:38

<a id="rule-345"></a>

### 345. SecH_GTS_JAVA_ReDos

| 字段 | 内容 |
|---|---|
| 问题级别 | 一般 |
| 语言 | JAVA |
| 标签 | SecBrella |
| 关联工具规则 | SecH_GTS_JAVA_ReDos |
| 规则类型 | 安全类（非编程规范） |
| 预估误报率 | 20% |

**审查要点**

Java代码中，字符串匹配matcher,replace等操作用到正则表达式。若正则表达式存在问题，会产生ReDos攻击，造成资源耗尽。本规则对这些有问题的正则表达式进行检测。

**修复建议**

```text
建议不使用复杂分组，对分组次数进行限制。存疑表达式可通过工具二次确认，若存在问题请修改。
推荐使用ICSL的ReDosKiller
内部链接已省略
也可以使用SDL Regex Fuzzer，SDL Regex Fuzzer是一款微软推出的正则表达式测试工具，可以用来发现这些潜在的漏洞。
内部链接已省略
如果通过了工具检测，可以屏蔽
```

**正确示例**

```text
常见安全分组场景:
不包含分组场景
([.][0-9a-zA-Z_-]+)* 安全场景
(ab+)+ 安全场景
```

**错误示例**

```text
常见有问题的分组方式:
1．长度超过32位判断未复杂表达式直接告警
2．(ab|abab)+ 样式 (a|a?)+ 直接告警
3．(aaa[a-zA-Z]+)* 或([a-zA-Z][a-zA-Z]+)* 样式 直接告警
4．([-a-z0-9]*)*直接告警
5．(a+)+和(\w+)+ 样式 (\w+a?)+直接告警
```

**参考信息**

- 最新更新时间：2026-01-21 14:24:51

<a id="rule-346"></a>

### 346. SecH_GTS_JAVA_ReDos_Param

| 字段 | 内容 |
|---|---|
| 问题级别 | 严重 |
| 语言 | JAVA |
| 标签 | SecBrella |
| 关联工具规则 | SecH_GTS_JAVA_ReDos_Param |
| 规则类型 | 安全类（非编程规范） |
| 预估误报率 | 50% |

**审查要点**

```text
告警场景一：使用不可信数据构造正则表达式，可能造成redos攻击。
告警场景二：有redos风险的硬编码正则表达式。
```

**修复建议**

```text
避免可控数据拼接regex，或增加校检函数。
校检函数：只要变量传入带有check|valid|verify|safe|filter|matches|noneMatch|match|allMatch关键字的函数视为安全。
针对常量正则，推荐使用ICSL检测工具进行校验：ReDosKiller
内部链接已省略
```

**正确示例**

```text
```
进行正则匹配前，先对匹配的文本的长度进行校验；
在编写正则时，尽量不要使用过于复杂的正则，尽量少用分组；
避免动态构建正则，当使用外部数据构造正则时，要使用白名单进行严格校验。
```
```

**错误示例**

```text
// 问题大类：ReDos攻击
// 问题子类：ReDos攻击
// 问题场景：使用Fastjson的JSONPath的eval、read等方法时，输入存在ReDOS风险的正则表达式作为参数
// 问题子场景：正则表达式动态构建，可以注入引起ReDos攻击的字符串
// 问题用例：JSONPath实例化形式调用eval
//CodeCheck TP:SecH_GTS_JAVA_ReDos_Param
@GetMapping(value = "/bad003")
public String bad003(String emailAccountFormat, String userJsonStr) {
String regexPath = EMAIL_REGEX_PREFIX + emailAccountFormat + EMAIL_REGEX_SUFFIX;
Object temp2 = new JSONPath(regexPath).eval(userJsonStr);
return "business end!";
}
```

**参考信息**

- 最新更新时间：2026-01-31 17:22:17

<a id="rule-347"></a>

### 347. SecH_GTS_JAVA_Return_Ciphertext_To_Foreground

| 字段 | 内容 |
|---|---|
| 问题级别 | 一般 |
| 语言 | JAVA |
| 标签 | SecBrella |
| 关联工具规则 | SecH_GTS_JAVA_Return_Ciphertext_To_Foreground |
| 规则类型 | 安全类（非编程规范） |
| 预估误报率 | 50% |

**审查要点**

```text
禁止将加密后的密文返回到前台。
如需要在界面显示密码、通过邮件发送初始化密码等，请参考红线RL-7.1.11-1例外处理。
```

**修复建议**

```text
返回用户端的信息应缩小返回信息的范围，减少影响范围，将敏感信息脱敏后再返回给客户端。如需要在界面显示密码、通过邮件发送初始化密码等，请参考红线RL-7.1.11-1例外处理。
资料扩展：
内部链接已省略
内部链接已省略
```

**正确示例**

```text
```
public String good(@RequestBody UserInfo userInfo) {
UserManager userManager = new UserManagerImpl();
String password = userInfo.getPassword();
if (userManager.verifyPassword(password)) {
// 返回带有敏感信息-密码的对象
// 设置UserID: userInfo.setUserID();
return userInfo.getUserId();
} else {
return "login fail";
}
}
```
```

**错误示例**

```text
```
public UserInfo bad(@RequestBody UserInfo userInfo) {
UserManager userManager = new UserManagerImpl();
String password = userInfo.getPassword();
if (userManager.verifyPassword(password)) {
// 返回带有敏感信息-密码的对象
// 设置UserID: userInfo.setUserID();
return userInfo;
} else {
return null;
}
}
```
```

**参考信息**

- 最新更新时间：2026-01-21 14:29:07

<a id="rule-348"></a>

### 348. SecH_GTS_JAVA_SSLContext_Unsafe_RandomSeed

| 字段 | 内容 |
|---|---|
| 问题级别 | 严重 |
| 语言 | JAVA |
| 标签 | SecBrella |
| 关联工具规则 | SecH_GTS_JAVA_SSLContext_Unsafe_RandomSeed |
| 规则类型 | 安全类（非编程规范） |
| 预估误报率 | 50% |

**审查要点**

```text
SSLSocket初始化参数不能使用默认的null，也不能使用不安全随机数，如new SecureRandom()以及NativePRNG 、SHA1PRNG、NativePRNGNonBlocking等生成的随机数
通过HttpCore获取SSLContext，需要显式调用setSecureRandom来设置安全随机数
```

**修复建议**

SSLSocket初始化参数使用安全随机数

**正确示例**

```text
```
// 正确示例：初始化SSL上下文时，设置强安全随机数生成器
private SSLSocket goodStrongSecureRandom(KeyManagerFactory kmf, TrustManagerFactory tmf, SSLContext sslContext)
throws IOException, KeyManagementException, NoSuchAlgorithmException {
SecureRandom secureRandom = SecureRandom.getInstanceStrong();
// 初始化SSL上下文时，设置init方法的第三个参数为SecureRandom.getInstanceStrong()，在Linux中，
// SecureRandom.getInstanceStrong()使用了伪随机数生成器/dev/random，返回的是安全伪随机数，符合公司密码规范要求
sslContext.init(kmf.getKeyManagers(), tmf.getTrustManagers(), secureRandom);
SSLSocketFactory sslSocketFactory = sslContext.getSocketFactory();
return (SSLSocket) sslSocketFactory.createSocket(HOST_ADDRESS, PORT);
}
// 正确示例
SSLContexts.custom().setSecureRandom(SecureRandom.getInstanceStrong()).build();
// 正确示例
SSLContextBuilder.create().setSecureRandom(SecureRandom.getInstanceStrong()).build();
```
```

**错误示例**

```text
```
// 错误示例1：初始化SSL上下文时，随机数生成器直接设置为null。
private SSLSocket badSecureRandomIsNull(KeyManagerFactory kmf, TrustManagerFactory tmf, SSLContext sslContext)
throws KeyManagementException, IOException {
// 初始化SSL上下文时，设置init方法的第三个参数为null，则默认使用new SecureRandom()作为随机数产生器，在Linux中，
// new SecureRandom()使用了伪随机数生成器/dev/urandom，返回的是不安全伪随机数，不符合公司密码规范要求
sslContext.init(kmf.getKeyManagers(), tmf.getTrustManagers(), null);
SSLSocketFactory sslSocketFactory = sslContext.getSocketFactory();
return (SSLSocket) sslSocketFactory.createSocket(HOST_ADDRESS, PORT);
}
// 错误示例2：初始化SSL上下文时，随机数生成器直接设置为new SecureRandom()。
private SSLSocket badSecureRandom(KeyManagerFactory kmf, TrustManagerFactory tmf, SSLContext sslContext)
throws KeyManagementException, IOException {
// 在Linux中new SecureRandom()使用了伪随机数生成器/dev/urandom，返回的是不安全伪随机数，不符合公司密码规范要求
sslContext.init(kmf.getKeyManagers(), tmf.getTrustManagers(), new SecureRandom());
SSLSocketFactory sslSocketFactory = sslContext.getSocketFactory();
return (SSLSocket) sslSocketFactory.createSocket(HOST_ADDRESS, PORT);
}
// 错误示例
SSLContexts.custom().build(); // 未调用setSecureRandom
SSLContexts.custom()..setSecureRandom(new SecureRandom()).build(); // 未设置安全随机数
```
```

**参考信息**

- 最新更新时间：2026-01-21 14:28:55

<a id="rule-349"></a>

### 349. SecH_GTS_JAVA_SSL_Socket_Release

| 字段 | 内容 |
|---|---|
| 问题级别 | 严重 |
| 语言 | JAVA |
| 标签 | SecBrella |
| 关联工具规则 | SecH_GTS_JAVA_SSL_Socket_Release |
| 规则类型 | 安全类（非编程规范） |
| 预估误报率 | 50% |

**审查要点**

SSLSocket、SSLServerSocket连接资源未关闭，可能导致资源泄露等问题。

**修复建议**

```text
1、使用try-with-resources。
2、在没有使用try-with-resources的情况下，在finally里关闭。
```

**正确示例**

```text
```
// 正确用例：使用try-with-resource处理socket
public static void good001(SSLContext sslContext) throws IOException {
try (SSLServerSocket sslServerSocket = (SSLServerSocket) sslContext.getServerSocketFactory()
.createServerSocket()) {
BusinessMock.doBusinessWithException(sslServerSocket);
} catch (BusinessException exception) {
LOGGER.error("business error!");
}
}
```
```

**错误示例**

```text
```
// 错误用例：else分支中，SSLServerSocket的close代码未写在finally块中，中间代码可能发生异常，导致socket未关闭，造成socket资源泄露
public static void bad003(SSLContext sslContext, boolean isTrue) throws IOException, BusinessException {
SSLServerSocket sslServerSocket = (SSLServerSocket) sslContext.getServerSocketFactory().createServerSocket();
if (isTrue) {
BusinessMock.doBusinessWithoutException(sslServerSocket);
sslServerSocket.close();
} else {
BusinessMock.doBusinessWithException(sslServerSocket); // 该函数抛出异常后，将导致后续close未被执行，产生流资源泄露问题
sslServerSocket.close();
}
}
```
```

**参考信息**

- 最新更新时间：2026-01-21 14:21:20

<a id="rule-350"></a>

### 350. SecH_GTS_JAVA_ScriptEngine_Injection

| 字段 | 内容 |
|---|---|
| 问题级别 | 严重 |
| 语言 | JAVA |
| 标签 | SecBrella |
| 关联工具规则 | SecH_GTS_JAVA_ScriptEngine_Injection |
| 规则类型 | 安全类（非编程规范） |
| 预估误报率 | 50% |

**审查要点**

当外部输入未经校验传入eval方法，则会引入代码注入问题

**修复建议**

```text
1、使用白名单限制输入
2、使用Java安全开发框架
3、参考资料如下：
https://forum.butian.net/share/487
内部链接已省略
```

**正确示例**

```text
```
//evil_poc为外部输入，未经校验传入eval方法
public void scriptEngineVul(String evil_poc) throws ScriptException {
String good_poc = check(evil_poc);
ScriptEngineManager manager = new ScriptEngineManager();
ScriptEngine engine = manager.getEngineByName("javascript");
engine.eval(good_poc);
}
```
```

**错误示例**

```text
```
//evil_poc为外部输入，未经校验传入eval方法
public void scriptEngineVul(String evil_poc) throws ScriptException {
ScriptEngineManager manager = new ScriptEngineManager();
ScriptEngine engine = manager.getEngineByName("javascript");
engine.eval(evil_poc);
}
```
```

**参考信息**

- 最新更新时间：2026-01-21 14:30:14

<a id="rule-351"></a>

### 351. SecH_GTS_JAVA_TOKEN_LOG

| 字段 | 内容 |
|---|---|
| 问题级别 | 严重 |
| 语言 | JAVA |
| 标签 | SecBrella |
| 关联工具规则 | SecH_GTS_JAVA_TOKEN_LOG |
| 规则类型 | 安全类（非编程规范） |
| 预估误报率 | 50% |

**审查要点**

```text
敏感信息泄露检测：禁止session中的token敏感信息泄露，如token、password。
如从session.getAttribute("token")等方式获取token并日志打印输出。
```

**修复建议**

将敏感信息进行匿名化处理，替换为等长的******然后打印到日志，或使用usecurity的日志组件处理敏感信息

**正确示例**

```text
```
将敏感信息进行匿名化处理
```
```

**错误示例**

```text
```
public void badCase(HttpServletRequest request){
HttpSession session = request.getSession();
String token = (String)session.getAttribute("token");
StringBuilder url = new StringBuilder();
url.append("?BMEWebToken=").append(token);
LOGGER.debug("AbstractCustomizeFilter: redirect url is " + url.toString());
}
```
```

**参考信息**

- 最新更新时间：2026-01-21 11:33:43

<a id="rule-352"></a>

### 352. SecH_GTS_JAVA_TemplateExpression_Fel

| 字段 | 内容 |
|---|---|
| 问题级别 | 严重 |
| 语言 | JAVA |
| 标签 | SecBrella |
| 关联工具规则 | SecH_GTS_JAVA_TemplateExpression_Fel |
| 规则类型 | 安全类（非编程规范） |
| 预估误报率 | 50% |

**审查要点**

在使用 Fel 表达式引擎执行外部可控的内容时，如果过滤不当，存在表达式注入的风险

**修复建议**

```text
在非必要的情况下不使用 Fel 引擎执行外部可控的表达式，如果使用，需对外部入参进行过滤校验
推荐使用US组件 内部链接已省略
```

**正确示例**

```text
```
FelEngine fel = new FelEngineImpl();
// 使用自定义的检查函数对外部入参进行检查，过滤危险的类、常见反射调用函数等
poc = verify(poc);
Object result = fel.eval(poc);
```
```

**错误示例**

```text
```
// 场景一，poc 为外部传入参数
FelEngine fel = new FelEngineImpl();
Object result = fel.eval(poc);
// 场景二，poc 为外部传入参数
FelEngine fel1 = new FelEngineImpl();
FelContext ctx1 = fel.getContext();
ctx1.set("aa",300);
ctx1.set("bb",400);
Object result1 = fel.eval(poc,ctx1);
// 场景三，编译执行，poc 为外部传入参数
FelEngine fel2 = new FelEngineImpl();
FelContext ctx2 = fel.getContext();
ctx2.set("aa",300);
Expression compile = fel.compile(poc, ctx2);
Object result2 = compile.eval(ctx2);
System.out.println(result2);
// 场景四，poc 为外部传入参数
Object eval = FelEngine.instance.eval(poc);
```
```

**参考信息**

- 最新更新时间：2026-01-21 14:03:06

<a id="rule-353"></a>

### 353. SecH_GTS_JAVA_TemplateExpression_FreeMarker

| 字段 | 内容 |
|---|---|
| 问题级别 | 严重 |
| 语言 | JAVA |
| 标签 | SecBrella |
| 关联工具规则 | SecH_GTS_JAVA_TemplateExpression_FreeMarker |
| 规则类型 | 安全类（非编程规范） |
| 预估误报率 | 50% |

**审查要点**

```text
检测FreeMarker模板表达式注入，编码指南见内部链接已省略
检测如下场景：
1：Configuration.setAPIBuiltinEnabled(true)被开启直接告警;
2：Configuration未设置ALLOWS_NOTHING_RESOLVER和黑白名单校验直接告警;
3：外部可控模板直接流入到getTemplate直接告警;
```

**修复建议**

```text
参考Freemaker编码指南：
内部链接已省略
使用GDE USecurity 表达式安全工具：内部链接已省略
```

**正确示例**

```text
```
/**
* 存在freemarker模板注入的风险
* @param dataMap 数据
* @param templateName 模板名字
* @return
*/
public static String getFreemarkContentString(final Map<String, ?> dataMap, final String templateName) {
try (StringWriter stringWriter = new StringWriter()) {
Configuration configuration = new Configuration(Configuration.VERSION_2_3_28);
configuration.setNewBuiltinClassResolver(TemplateClassResolver.ALLOWS_NOTHING_RESOLVER);
// 应该置为false,禁止开启 api，以防恶意用户调用freemarker内部api来实现攻击目的，即禁止存在如下设置api开启的代码
configuration.setAPIBuiltinEnabled(false);
configuration.setDefaultEncoding(StandardCharsets.UTF_8.name());
Template template = configuration.getTemplate(templateName);
template.process(dataMap, stringWriter);
return template.toString();
} catch (IOException | TemplateException e) {
}
return "";
}
```
```

**错误示例**

```text
```
/**
* 存在freemarker模板注入的风险
* @param dataMap 数据
* @return
*/
public static String getFreemarkContentString2(final Map<String, ?> dataMap) {
String templateName = System.getenv("templateName");
try (StringWriter stringWriter = new StringWriter()) {
Configuration configuration = new Configuration(Configuration.VERSION_2_3_28);
// 这里应该置为false,禁止开启 api，以防恶意用户调用freemarker内部api来实现攻击目的，即禁止存在如下设置api开启的代码
configuration.setAPIBuiltinEnabled(true); // bad
configuration.setDefaultEncoding(StandardCharsets.UTF_8.name());
Template template = configuration.getTemplate(templateName);
template.process(dataMap, stringWriter);
return template.toString();
} catch (IOException | TemplateException e) {
}
return "";
}
```
```

**参考信息**

- 最新更新时间：2026-01-21 11:44:07

<a id="rule-354"></a>

### 354. SecH_GTS_JAVA_TemplateExpression_Groovy

| 字段 | 内容 |
|---|---|
| 问题级别 | 严重 |
| 语言 | JAVA |
| 标签 | SecBrella |
| 关联工具规则 | SecH_GTS_JAVA_TemplateExpression_Groovy |
| 规则类型 | 安全类（非编程规范） |
| 预估误报率 | 50% |

**审查要点**

```text
Groovy不当使用造成的表达式注入命令执行问题。覆盖场景如下：
场景 1：groovyshell调用evaluate
场景 2：groovyshell调用parse参数来自于外部，且返回值调用run
场景 3：GroovyClassLoader对象调用parseClass
场景 4：GroovyScriptEngine对象调用run
场景 5：evaluate等方法数据流跟踪，如果全局有GroovyInterceptor 类子实例调用register方法 就算降误报 否则就告警（降误报）
```

**修复建议**

```text
1、调用危险函数前进行参数校验
2、使用沙盒安全机制，限制内部调用的API
推荐使用US组件 内部链接已省略
3、使用Java安全开发组件，如wushan或usecurity等
```

**正确示例**

```text
```
//参考自内部链接已省略
//引入groovy-sandbox沙盒（https://github.com/jenkinsci/groovy-sandbox）
//由于Groovy脚本可能来自于非信任域，可通过使用Groovy沙箱保证安全。Groovy沙箱由使用者定制，groovy-sandbox实现了一个SandboxTransformer, 扩展自CompilationCustomizer, 在Groovy代码编译时进行转换. 脚本转换后, 让脚本执行的每一步都会被拦截, 调用Checker进行检查。
//自定义拦截器并注册拦截器：
class NoSystemExitSandbox extends GroovyInterceptor {
@Override
public Object onStaticCall(GroovyInterceptor.Invoker invoker, Class receiver, String method, Object... args) throws Throwable {
if (receiver==System.class && method=="exit")
throw new SecurityException("No call on System.exit() please");
return super.onStaticCall(invoker, receiver, method, args);
}//此处仅为案例，该处实现黑白名单类和方法的校验
}
@Test
public void UseSandBox() {
final GroovyShell sh = new GroovyShell(new CompilerConfiguration().addCompilationCustomizers(new SandboxTransformer()));
new NoSystemExitSandbox().register();//沙箱注册
sh.evaluate("System.exit(0)");
}
```
```

**错误示例**

```text
```
//场景一：在Groovy脚本中调用runtime执行命令，该脚本作为evil.groovy作为以下方法的输入
//场景二：通过GroovyShell执行
groovyShell.evaluate(poc);
groovyShell.parse(new File("src\\main\\resources\\evil.groovy"));
groovyShell.run(new File("src\\main\\resources\\evil.groovy"), list);
//场景三：通过GroovyScriptEngine加载
groovyScriptEngine.run("src\\main\\resources\\evil.groovy", new Binding());
//场景四：通过GroovyClassLoader加载
groovyClassLoader.parseClass(new File("src\\main\\resources\\evil.groovy"));
//场景五：通过Groovy的闭包执行命令
new MethodClosure(new java.lang.ProcessBuilder("calc"), "start");
//场景六：通过Groovy的Eval执行
Eval.me(evil_str);
Eval.x(1, evil_str);
Eval.xy(1, 2, evil_str);
Eval.xyz(1, 2, 3, evil_str);
//场景七：通过InvokeHelper执行脚本
Class clazz = groovyClassLoader.parseClass(str);
InvokerHelper.newScript(clazz, new Binding());
```
```

**参考信息**

- 最新更新时间：2024-11-28 11:24:40

<a id="rule-355"></a>

### 355. SecH_GTS_JAVA_TemplateExpression_JEXL

| 字段 | 内容 |
|---|---|
| 问题级别 | 严重 |
| 语言 | JAVA |
| 标签 | SecBrella |
| 关联工具规则 | SecH_GTS_JAVA_TemplateExpression_JEXL |
| 规则类型 | 安全类（非编程规范） |
| 预估误报率 | 50% |

**审查要点**

如果 JEXL 表达式外部可控，则可造成表达式注入，产生RCE问题

**修复建议**

```text
1、对于支持表达式执行的业务场景，必须对支持的表达式语句进行白名单校验；
2、业务功能需要执行 JEXL 表达式语句，整理出一个允许列表给外部用户选择。不在允许列表中的语句无法执行；
3、如果业务功能执行的 JEXL 语句来自外部用户输入，且难以列表化语句限制需要进行如下限制：
a. 类实例化的限制：对需要实例化的类进行白名单限制，仅期望的类型可以进行 new 实例化
b. 反射调用限制：限制属性 class 的访问、限制方法 getclass 和 forname 的调用
eference链接1：内部链接已省略
eference链接2：https://codeql.github.com/codeql-query-help/java/java-jexl-expression-injection/
```

**正确示例**

```text
```
//正确示例一：使用 JexlSandbox
// JexlSandbox 的参数为 true ，代表能执行任意表达式
// JexlSandbox 的参数为 false ，代表任何表达式都无法执行。和 JexlSandbox.white() 配合使用，开启白名单类
JexlSandbox onlymath = new JexlSandbox(false);
// 此处白名单为 java.lang.Math 类
onlymath.white("java.lang.Math");
JexlEngine jexlEngine = new JexlBuilder().sandbox(onlymath).create();
JexlContext jexlContext = new MapContext();
JexlExpression jexlExpression = jexlEngine.createExpression(poc);
jexlExpression.evaluate(jexlContext);
//正确示例二：继承 Uberspect 类，限制反射属性和方法的使用
内部链接已省略
//正确示例三 part1：
//JexlSandbox 类的另一种实现方式
JexlUberspect sandbox = new JexlUberspectSandbox();
//将 sandbox 注入 jexlEngine
JexlEngine jexlEngine = new JexlBuilder().uberspect(sandbox).create();
JexlExpression jexlExpression = jexlEngine.createExpression(poc);
JexlContext jexlContext = new MapContext();
jexlExpression.evaluate(jexlContext);
//正确示例三 part2：
//以下为 sandbox 的实现类，通过白名单
class JexlUberspectSandbox implements JexlUberspect {
//通过白名单类限制
private static final List<String> ALLOWED_CLASSES =
Arrays.asList("java.lang.Math", "java.util.Random");
private final JexlUberspect uberspect = new JexlBuilder().create().getUberspect();
private void checkAccess(Object obj) {
if (!ALLOWED_CLASSES.contains(obj.getClass().getCanonicalName())) {
throw new AccessControlException("Not allowed");
}
}
//TODO：
//具体实现请参考链接：https://codeql.github.com/codeql-query-help/java/java-jexl-expression-injection/
}
```
```

**错误示例**

```text
```
//场景一：JexlEngine调用createExpression参数来自于外部，最终调用evaluate执行时进行告警；
JexlEngine jexlEngine = new JexlBuilder().create();
JexlContext jexlContext = new MapContext();
JexlExpression jexlExpression = jexlEngine.createExpression(EVILINPUT);
return jexlExpression.evaluate(jexlContext).toString();
//场景二：JexlEngine调用createScript,参数来自于外部，最终调用excute执行时进行告警；
JexlEngine jexlEngine = new JexlBuilder().create();
JexlScript jexlScript = jexlEngine.createScript(EVILINPUT);
JexlContext jexlContext = new MapContext();
return jexlScript.execute(jexlContext).toString();
```
```

**参考信息**

- 最新更新时间：2024-11-28 14:50:03

<a id="rule-356"></a>

### 356. SecH_GTS_JAVA_TemplateExpression_MVEL

| 字段 | 内容 |
|---|---|
| 问题级别 | 严重 |
| 语言 | JAVA |
| 标签 | SecBrella |
| 关联工具规则 | SecH_GTS_JAVA_TemplateExpression_MVEL |
| 规则类型 | 安全类（非编程规范） |
| 预估误报率 | 50% |

**审查要点**

```text
MVEL为 MVFLEX Expression Language（MVFLEX表达式语言）的缩写，它是一种动态/静态的可嵌入的表达式语言和为Java平台提供Runtime（运行时）的语言。最初是作为一个应用程序框架实用程序的语言开始，该项目现已发展完全独立。MVEL通常用于执行用户（程序员）通过配置XML文件或注释等定义的基本逻辑。它也可以用来解析简单的JavaBean表达式。Runtime（运行时）允许MVEL表达式通过解释执行或者预编译生成字节码后执行。
```

**修复建议**

优先选用白名单方式对用户的输入的表达式进行校验

**正确示例**

```text
需要对用户传入的MVEL表达式进行白名单、黑名单校验，以下实例代码为样例，不作为唯一标准解决方案，仅供扩宽解决思路。
```
/**
* An expression filter is used to allow/deny scripts for execution.
*/
public class ExpressionFilter {
private static final Logger logger = LoggerFactory.getLogger(ExpressionFilter.class.getName());
private final Set<Pattern> allowedExpressionPatterns;
private final Set<Pattern> forbiddenExpressionPatterns;
public ExpressionFilter(Set<Pattern> allowedExpressionPatterns, Set<Pattern> forbiddenExpressionPatterns) {
this.allowedExpressionPatterns = allowedExpressionPatterns;
this.forbiddenExpressionPatterns = forbiddenExpressionPatterns;
}
public String filter(String expression) {
if (forbiddenExpressionPatterns != null && expressionMatches(expression, forbiddenExpressionPatterns)) {
logger.warn("Expression {} is forbidden by expression filter", expression);
return null;
}
if (allowedExpressionPatterns != null && !expressionMatches(expression, allowedExpressionPatterns)) {
logger.warn("Expression {} is not allowed by expression filter", expression);
return null;
}
return expression;
}
private boolean expressionMatches(String expression, Set<Pattern> patterns) {
for (Pattern pattern : patterns) {
if (pattern.matcher(expression).matches()) {
return true;
}
}
return false;
}
}
```
```

**错误示例**

```text
```
@Override
public String mvelInterpreter(String poc, String function) throws IOException {
Map vars = new HashMap();
vars.put("foobar", new Integer(100));
String result = "";
switch (function.toLowerCase()){
case "evaltostring":
result = MVEL.evalToString(poc);
break;
case "evaltobean":
result = MVEL.evalToBoolean(poc,vars).toString();
break;
case "evalfile":
File file = new File("src\\main\\resources\\evil");
MVEL.evalFile(file);
result = "success";
break;
default:
result = (String) MVEL.eval(poc, vars);
}
return result;
}
```
```

**参考信息**

- 最新更新时间：2026-01-31 17:22:39

<a id="rule-357"></a>

### 357. SecH_GTS_JAVA_TemplateExpression_SpEL

| 字段 | 内容 |
|---|---|
| 问题级别 | 严重 |
| 语言 | JAVA |
| 标签 | SecBrella |
| 关联工具规则 | SecH_GTS_JAVA_TemplateExpression_SpEL |
| 规则类型 | 安全类（非编程规范） |
| 预估误报率 | 50% |

**审查要点**

SpEL表达式支持类的实例化和(静态)方法的调用，若表达式参数可控，则可以构造恶意表达式造成RCE

**修复建议**

```text
对参数进行白名单校验
推荐使用US组件 内部链接已省略
```

**正确示例**

```text
```
/**
* 若不需要实例化新对象以及调用对象的（静态）方法，即仅仅获取bean的属性的话。使用下述方法进行限制：
* SimpleEvaluationContext仅允许属性的只读访问
*/
public void goodCase() {
SpelExpressionParser spel = new SpelExpressionParser();
EvaluationContext context = SimpleEvaluationContext.forReadOnlyDataBinding().build();
spel.parseExpression("expression" ).getValue(context);
}
```
```

**错误示例**

```text
```
/**
* SpEL表达式支持类的实例化和(静态)方法的调用，若表达式参数可控，则可以构造恶意表达式造成RCE
*/
public void badCase() {
SpelExpressionParser spel = new SpelExpressionParser();
spel.parseExpression("T(java.lang.Runtime).getRuntime().exec('calc')" ).getValue();
}
```
```

**参考信息**

- 最新更新时间：2026-01-21 14:32:07

<a id="rule-358"></a>

### 358. SecH_GTS_JAVA_TemplateExpression_Thymeleaf

| 字段 | 内容 |
|---|---|
| 问题级别 | 严重 |
| 语言 | JAVA |
| 标签 | SecBrella |
| 关联工具规则 | SecH_GTS_JAVA_TemplateExpression_Thymeleaf |
| 规则类型 | 安全类（非编程规范） |
| 预估误报率 | 50% |

**审查要点**

```text
Thymeleaf模板表达式注入问题：使用TemplateEngine对象的process方法渲染外部可控数据时，容易造成表达式注入问题。
【修复建议】（1）对外部数据进行黑白名单数据校验
（2）自定义安全的SecOGNLVariableExpressionEvaluator/SecSPELVariableExpressionEvaluator，重写executeExpression方法。自定义安全类继承StandardDialect类，并重写getVariableExpressionEvaluator方法，从而系统获取上述安全的Evaluator, 调用TemplateEngine的addDialect(final IDialect dialect) 方法将使用自定义的安全解析器。
```

**修复建议**

```text
调用TemplateEngine对象的process方法，添加addDialect方法
内部链接已省略
若Thymeleaf模板完全由上游/外部输入，遍历内容进行校验难以实现，可参照下述博客方案在执行过程中限制类型白名单的使用，以进一步防止恶意方法的调用：内部链接已省略
```

**正确示例**

```text
```
public boolean getThymeleafWrongForXSS(HttpServletRequest request, HttpServletResponse response)
throws IOException {
TemplateEngine engine = TemplateEngineUtil.getTemplateEngine(request.getServletContext());
WebContext context = new WebContext(request, response, request.getServletContext());
String input = request.getParameter("temp");
context.setVariable("poc", input);
SecStandardDialect secStandarddialect = new SecStandardDialect();
engine.addDialect(secStandarddialect);
// 不告警
engine.process("indexOGNL.html", context, response.getWriter());
return true;
}
/**
* 正确用例2：若通过参数动态拼接构造Thymeleaf模板，则拼接的参数结合业务需要进行严格的校验
* 例如上述错误用例bad002，由于拼接的是name，一般来说用户姓名只能为字母/数组，所以进行字符集的白名单交校验
*/
@GetMapping(value = "/good")
@ResponseBody
public void good(@RequestParam String name, HttpServletResponse response, HttpServletRequest request)
throws IOException {
// 入参也校验，如此处name允许字母数字
if (!check(name)) {
LOGGER.error("parameter is illegal");
return;
}
String template = "" + name + "";
TemplateEngine engine = new TemplateEngine();
WebContext context = new WebContext(request, response, request.getServletContext());
engine.process(template, context, response.getWriter());
}
```
```

**错误示例**

```text
```
public boolean getThymeleafWrong1(HttpServletRequest request, HttpServletResponse response) throws IOException {
TemplateEngine engine = TemplateEngineUtil.getTemplateEngine(request.getServletContext());
WebContext context = new WebContext(request, response, request.getServletContext());
String input = request.getParameter("temp");
context.setVariable("poc", input);
// 告警
engine.process("indexOGNL.html", context, response.getWriter());
return true;
}
```
```

**参考信息**

- 最新更新时间：2024-11-28 14:38:10

<a id="rule-359"></a>

### 359. SecH_GTS_JAVA_TemplateExpression_Velocity

| 字段 | 内容 |
|---|---|
| 问题级别 | 严重 |
| 语言 | JAVA |
| 标签 | SecBrella |
| 关联工具规则 | SecH_GTS_JAVA_TemplateExpression_Velocity |
| 规则类型 | 通用类（非编程规范） |
| 预估误报率 | 50% |

**审查要点**

在使用 Velocity 模板渲染外部可控的内容时，如果设置不当，存在模板注入的风险。

**修复建议**

```text
在使用 Velocity 模板渲染外部可控的内容时，设置安全属性。
推荐使用US组件 内部链接已省略
```

**正确示例**

```text
```
/*
velocity 使用 merge 方法渲染本地模板，并设置安全属性
安全！
*/
@Override
public String renderSecure(String poc) throws IOException {
VelocityEngine velocityEngine = new VelocityEngine();
velocityEngine.setProperty(RuntimeConstants.RESOURCE_LOADER,"classpath");
velocityEngine.setProperty("classpath.resource.loader.class", ClasspathResourceLoader.class.getName());
// 设置安全属性
velocityEngine.setProperty(RuntimeConstants.UBERSPECT_CLASSNAME, "org.apache.velocity.util.introspection.SecureUberspector");
velocityEngine.init();
// templates/vt.vm 中的内容外部可控
Template template = velocityEngine.getTemplate("templates/vt.vm");
VelocityContext ctx = new VelocityContext();
ctx.put("name",poc);
StringWriter sw = new StringWriter();
template.merge(ctx,sw);
return sw.toString();
}
```
```

**错误示例**

```text
```
/*
velocity 使用 merge 方法渲染本地模板
危险！
*/
@Override
public String renderMerge(String poc) throws IOException {
// #set($e=\"e\");$e.getClass().forName(\"java.lang.Runtime\").getMethod(\"getRuntime\",null).invoke(null,null).exec(\"calc\")
VelocityEngine velocityEngine = new VelocityEngine();
velocityEngine.setProperty(RuntimeConstants.RESOURCE_LOADER,"classpath");
velocityEngine.setProperty("classpath.resource.loader.class", ClasspathResourceLoader.class.getName());
velocityEngine.init();
// templates/vt.vm 中的内容外部可控
Template template = velocityEngine.getTemplate("templates/vt.vm");
VelocityContext ctx = new VelocityContext();
ctx.put("name",poc);
StringWriter sw = new StringWriter();
template.merge(ctx,sw);
return sw.toString();
}
```
```

**参考信息**

- 最新更新时间：2026-01-21 14:26:52

<a id="rule-360"></a>

### 360. SecH_GTS_JAVA_ThreadLocal_Release

| 字段 | 内容 |
|---|---|
| 问题级别 | 严重 |
| 语言 | JAVA |
| 标签 | SecBrella |
| 关联工具规则 | SecH_GTS_JAVA_ThreadLocal_Release |
| 规则类型 | 安全类（非编程规范） |
| 预估误报率 | 50% |

**审查要点**

```text
ThreadLocal资源未释放，可能导致资源泄露等问题。
【误报提示】当ThreadLocal字段声明不在源码中时（即通过依赖引入），无法追踪是否remove，建议屏蔽。
```

**修复建议**

```text
ThreadLocal资源在finally里释放
不要使用set(null) 来释放，存在内存泄露风险，建议改为remove()方式
```

**正确示例**

```text
```
class ThreadLocalWithRemove implements Runnable {
private static final Logger LOGGER = LoggerFactory.getLogger(ThreadLocalWithRemove.class);
private static final ThreadLocal<List<Integer>> THREAD_LOCAL = new ThreadLocal<>();
@Override
public void run() {
List<Integer> cache = THREAD_LOCAL.get();
if (cache == null) {
cache = new ArrayList<>();
THREAD_LOCAL.set(cache);
}
cache.add(0);
try {
BusinessMock.doBusinessWithException(cache);
} catch (BusinessException exception) {
LOGGER.info("fail!");
} finally {
// ThreadLocal资源在finally里释放
THREAD_LOCAL.remove();
}
}
}
```
```

**错误示例**

```text
```
class ThreadLocalLeakWithoutRemove implements Runnable {
private static final Logger LOGGER = LoggerFactory.getLogger(ThreadLocalLeakWithoutRemove.class);
private static final ThreadLocal<List<Integer>> THREAD_LOCAL = new ThreadLocal<>();
@Override
public void run() {
List<Integer> cache = THREAD_LOCAL.get();
if (cache == null) {
cache = new ArrayList<>();
THREAD_LOCAL.set(cache);
}
cache.add(0);
BusinessMock.doBusinessWithoutException(cache);
//ThreadLocal资源未释放
}
}
```
```

**参考信息**

- 最新更新时间：2026-01-21 11:39:05

<a id="rule-361"></a>

### 361. SecH_GTS_JAVA_UnSafe_Ssh2_Config_Jsch

| 字段 | 内容 |
|---|---|
| 问题级别 | 严重 |
| 语言 | JAVA |
| 标签 | SecBrella |
| 关联工具规则 | SecH_GTS_JAVA_UnSafe_Ssh2_Config_Jsch |
| 规则类型 | 安全类（非编程规范） |
| 预估误报率 | 50% |

**审查要点**

```text
JSch、Session配置参数中StrictHostKeyChecking设置为no则告警。
【规范】内部链接已省略
JSch中的静态代码块中有默认实现cbc和md5算法，所以在使用new JSch()必须重新配置cipher.s2c cipher.c2s mas.s2c mas.c2s算法
【规范】内部链接已省略
【规范】内部链接已省略
```

**修复建议**

使用new JSch()必须重新配置cipher.s2c cipher.c2s mas.s2c mas.c2s算法

**正确示例**

```text
```
/**
* jsch秘钥连接
*
* @param args args
* @since : 2020/11/18
*/
public static void goodCase() {
String dir = JschUtil.class.getClassLoader().getResource(".").getFile();
String username = "root";
String host = "10.44.199.104";
int port = 22;
String password = "GTS_DB@2020";
JSch jsch = new JSch();
try {
String path = dir + ".ssh/known_hosts";
jsch.setKnownHosts(path);
session = jsch.getSession(username, host, port);
session.setPassword(password);
Properties config = new Properties();
config.put("cipher.s2c", "aes128-ctr,aes192-ctr,aes256-ctr");
config.put("cipher.c2s", "aes128-ctr,aes192-ctr,aes256-ctr");
config.put("mac.s2c", "hmac-sha2-256");
config.put("mac.c2s", "hmac-sha2-256");
config.put("compression.s2c", "none");
session.setConfig(config);
session.connect();
Channel channel = session.openChannel("sftp");
channel.connect();
} catch (JSchException e) {
LOGGER.error("JSchException");
}
}
```
```

**错误示例**

```text
```
/**
* 测试对于StrictHostKeyChecking 设置为no的检测
* 应告警
*
* @param username 用户名
* @param host IP
* @param port 端口号
* @param password password
* @throws JSchException JSchException
* @since : 2020/11/18
*/
public static void badCase(String username, String host, int port, String password)
throws JSchException {
JSch jsch = new JSch();
session = jsch.getSession(username, host, port);
session.setPassword(password);
Properties config = new Properties();
config.put("StrictHostKeyChecking", "no");
session.setConfig(config);
session.connect();
}
/**
* jsch秘钥连接
*
* @param args args
* @since : 2020/11/18
*/
public static void badCase(String[] args) {
String dir = JschUtilTwo.class.getClassLoader().getResource(".").getFile();
String username = "root";
String host = "10.44.199.104";
int port = 22;
String password = "GTS_DB@2020";
JSch jsch = new JSch();
try {
String path = dir + ".ssh/known_hosts";
jsch.setKnownHosts(path);
session = jsch.getSession(username, host, port);
session.setPassword(password);
session.connect();
Channel channel = session.openChannel("sftp");
channel.connect();
} catch (JSchException e) {
LOGGER.error(e.getMessage());
}
}
```
```

**参考信息**

- 最新更新时间：2026-02-06 11:20:48

<a id="rule-362"></a>

### 362. SecH_GTS_JAVA_Unclosed_Resource_For_Sftp

| 字段 | 内容 |
|---|---|
| 问题级别 | 严重 |
| 语言 | JAVA |
| 标签 | SecBrella |
| 关联工具规则 | SecH_GTS_JAVA_Unclosed_Resource_For_Sftp |
| 规则类型 | 安全类（非编程规范） |
| 预估误报率 | 50% |

**审查要点**

jsch组件 sftp连接后需关闭连接

**修复建议**

sftp连接后要关闭连接

**正确示例**

```text
```
JSch jsch = new JSch();
Session session = jsch.getSession(username, host, port);
session.setPassword(password);
session.connect();
session.disconnect();
```
```

**错误示例**

```text
```
JSch jsch = new JSch();
Session session = jsch.getSession(username, host, port);
session.setPassword(password);
session.connect();
```
```

**参考信息**

- 最新更新时间：2024-11-28 10:05:09

<a id="rule-363"></a>

### 363. SecH_GTS_JAVA_Unsafe_Decompression_Entry

| 字段 | 内容 |
|---|---|
| 问题级别 | 严重 |
| 语言 | JAVA |
| 标签 | SecBrella |
| 关联工具规则 | SecH_GTS_JAVA_Unsafe_Decompression_Entry |
| 规则类型 | 安全类（非编程规范） |
| 预估误报率 | 50% |

**审查要点**

解压过程中未通过Entry的个数统计解压数量并进行限制

**修复建议**

通过Entry的个数统计解压数量并进行限制，见正确用例中在循环中统计个数并检查是否超出数量。可以在循环中包含check等关键字函数。

**正确示例**

```text
```
/**
* 正确用例：使用org.apache.commons.compress.archivers.zip.ZipFile进行zip解压
* 1. 使用entry.getName()获取路径后，校验路径正确性
* 2. 统计并限制ZipEntry个数
* 3. 统计并限制解压流大小
* 上传classpath:filedir/normalFile.zip为成功
*/
@PostMapping("/good001")
public void good001(@RequestBody MultipartFile multipartFile) throws IOException {
// 忽略文件上传部分校验过程，请参考09_file_upload_download部分内容
File file = new File(ResourceUtils.getFile("classpath:filedir").getCanonicalPath(),
multipartFile.getOriginalFilename());
multipartFile.transferTo(file);
try (ZipFile zipFile = new ZipFile(file)) {
Enumeration entries = zipFile.getEntries();
byte[] buf = new byte[BUFFER_SIZE];
long fileCount = 0;
long totalFileSize = 0;
String parentFilePath = file.getParent();
while (entries.hasMoreElements()) {
// 问题子场景2解决：统计压缩文件个数并加以限制，上传classpath:filedir/lotsFile.zip文件复现
ZipArchiveEntry entry = (ZipArchiveEntry) entries.nextElement();
fileCount++;
if (fileCount > MAX_FILE_COUNT) {
LOGGER.info("The ZIP package contains too many files.");
return;
}
// 问题子场景1解决：校验解压文件路径正确性，上传classpath:filedir/unsafePath.zip文件复现
File tempFile = new File(parentFilePath + "/" + entry.getName());
String canonicalPath = tempFile.getCanonicalPath();
if (!canonicalPath.startsWith(parentFilePath)) {
LOGGER.info("Path Traversal vulnerability...");
return;
}
if (entry.isDirectory()) {
tempFile.mkdirs();
continue;
}
try (BufferedInputStream zis = new BufferedInputStream(zipFile.getInputStream(entry));
FileOutputStream fos = new FileOutputStream(tempFile);
BufferedOutputStream zos = new BufferedOutputStream(fos, BUFFER_SIZE)) {
int length = 0;
while ((length = zis.read(buf)) != -1) {
// 问题子场景3解决：统计解压流大小并加以限制，上传classpath:filedir/largeFile.zip文件复现
totalFileSize += length;
if (totalFileSize > MAX_TOTAL_FILE_SIZE) {
LOGGER.info("Zip Bomb! File size is too large.");
return;
}
zos.write(buf, 0, length);
}
}
}
}
}
```
```

**错误示例**

```text
```
* 问题子场景:使用org.apache.commons.compress.archivers.zip.ZipFile提供的解压功能解压Zip压缩包时
* 1. 通过可被控制的ZipArchiveEntry.getName()获取解压文件名来构造解压路径，未进行标准化和路径校验的处理
* 2. 解压过程中未通过ZipArchiveEntry的个数统计解压数量并进行限制
* 3. 解压过程中未统计解压流实际大小并进行限制
* 错误用例1: 覆盖上述三个子场景
* /unsafeFile/unsafePath.zip/../aaa.txt为恶意伪造的ZipArchiveEntry的name
*/
//CodeCheck TP:SecH_GTS_JAVA_Gzip_Over_Size
//CodeCheck FN:SecH_GTS_JAVA_Zip_GetName_Check
@PostMapping("/bad001")
public void bad001(@RequestBody MultipartFile multipartFile) throws IOException {
// 忽略文件上传部分校验过程，请参考09_file_upload_download部分内容
File file = new File(ResourceUtils.getFile("classpath:filedir").getCanonicalPath(),
multipartFile.getOriginalFilename());
multipartFile.transferTo(file);
try (ZipFile zipFile = new ZipFile(file)) {
Enumeration entries = zipFile.getEntries();
byte[] buf = new byte[BUFFER_SIZE];
String parentFilePath = file.getParent();
while (entries.hasMoreElements()) {
// 问题子场景2：未统计压缩文件总数并加以限制
ZipArchiveEntry entry = (ZipArchiveEntry) entries.nextElement();
// 问题子场景1：未校验解压文件路径的正确性，上传classpath:filedir/unsafePath.zip文件复现
File tempFile = new File(parentFilePath + "/" + entry.getName());
if (entry.isDirectory()) {
tempFile.mkdirs();
continue;
}
try (BufferedInputStream zis = new BufferedInputStream(zipFile.getInputStream(entry));
FileOutputStream fos = new FileOutputStream(tempFile);
BufferedOutputStream zos = new BufferedOutputStream(fos, BUFFER_SIZE)) {
int length = 0;
while ((length = zis.read(buf)) != -1) {
// 问题子场景3：未统计解压流大小并加以限制
zos.write(buf, 0, length);
}
}
}
}
}
```
```

**参考信息**

- 最新更新时间：2026-01-21 14:28:09

<a id="rule-364"></a>

### 364. SecH_GTS_JAVA_Unsafe_Encryption_Pwd_In_Digest

| 字段 | 内容 |
|---|---|
| 问题级别 | 严重 |
| 语言 | JAVA |
| 标签 | SecBrella |
| 关联工具规则 | SecH_GTS_JAVA_Unsafe_Encryption_Pwd_In_Digest |
| 规则类型 | 安全类（非编程规范） |
| 预估误报率 | 50% |

**审查要点**

检测口令采用SHA256等Hash摘要算法加密的场景

**修复建议**

```text
使用正确的加密方式
【F.D.ATH.CMP.2.3.a】认证凭据不需要还原的场景，必须使用PBKDF2或公司密码学专家组认可的算法加密，对于性能极其敏感且安全性要求不高的场景允许使用HMAC
```

**正确示例**

```text
```
/**
* 使用PBKDF2 加密密码
*/
public static void goodPbkdf2(String password) throws NoSuchAlgorithmException, InvalidKeySpecException {
SecureRandom random = SecureRandom.getInstanceStrong();
byte[] salt = new byte[16];
random.nextBytes(salt);
KeySpec spec = new PBEKeySpec(password.toCharArray(), salt, PBKDF2_ITERATIONS, HASH_SIZE);
SecretKeyFactory secretKeyFactory = SecretKeyFactory.getInstance(PBKDF2_ALGORITHM);
byte[] hash = secretKeyFactory.generateSecret(spec).getEncoded();
Arrays.toString(hash);
}
```
```

**错误示例**

```text
```
/**
* 错误示例：使用SHA256加密密码
* @param request 请求
* @return 返回值
*/
public String badCaseSha256(HttpServletRequest request){
String password = request.getHeader("password");
try {
MessageDigest messageDigest = MessageDigest.getInstance("SHA256");
byte[] digest = messageDigest.digest(password.getBytes(StandardCharsets.UTF_8));
return new String(digest);
} catch (NoSuchAlgorithmException e) {
throw new RuntimeException("digest failed, ", e);
}
}
```
```

**参考信息**

- 最新更新时间：2025-01-22 16:28:14

<a id="rule-365"></a>

### 365. SecH_GTS_JAVA_Unsafe_Encryption_Signatur_Order

| 字段 | 内容 |
|---|---|
| 问题级别 | 严重 |
| 语言 | JAVA |
| 标签 | SecBrella |
| 关联工具规则 | SecH_GTS_JAVA_Unsafe_Encryption_Signatur_Order |
| 规则类型 | 安全类（非编程规范） |
| 预估误报率 | 50% |

**审查要点**

加密后的数据流入签名方法，加密签名顺序问题

**修复建议**

使用正确的加密顺序，先签名后加密。禁止先加密后签名。

**正确示例**

```text
```
// 正确示例：先通过SHA256withECDSA算法对消息签名，再通过AES算法对签名进行对称加密
private SignatureData goodRightOrderBetweenSignatureAndEncrypt(String data, byte[] secretKey, byte[] iv)
throws NoSuchAlgorithmException, InvalidKeyException, SignatureException, NoSuchPaddingException,
InvalidAlgorithmParameterException, IllegalBlockSizeException, BadPaddingException {
SignatureData signatureData = new SignatureData();
// 数字签名过程
KeyPairGenerator signaturePairGenerator = KeyPairGenerator.getInstance("EC");
signaturePairGenerator.initialize(SIGNATURE_KEY_SIZE, SecureRandom.getInstanceStrong());
KeyPair signatureKeyPair = signaturePairGenerator.generateKeyPair();
Signature signature = Signature.getInstance("SHA256withECDSA");
signature.initSign(signatureKeyPair.getPrivate());
signature.update(data.getBytes(StandardCharsets.UTF_8));
byte[] digitalSignature = signature.sign();
byte[] allData = new byte[data.length() + digitalSignature.length];
System.arraycopy(data.getBytes(StandardCharsets.UTF_8), 0, allData, 0, data.length());
System.arraycopy(digitalSignature, 0, allData, data.length(), allData.length);
// 对称加密过程
SecretKeySpec secretKeySpec = new SecretKeySpec(secretKey, "AES");
String transformation = "AES/CBC/PKCS5Padding";
IvParameterSpec ivParameterSpec = new IvParameterSpec(iv);
Cipher cipher = Cipher.getInstance(transformation);
cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec, ivParameterSpec);
byte[] bytes = cipher.doFinal(allData);
signatureData.setCipherData(bytes);
return signatureData;
}
```
```

**错误示例**

```text
```
// 错误示例：先通过AES算法对消息进行对称加密，再通过SHA256withECDSA算法对密文签名
private SignatureData badWrongOrderBetweenSignatureAndEncrypt1111(String data, byte[] secretKey, byte[] iv)
throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidAlgorithmParameterException,
InvalidKeyException, IllegalBlockSizeException, BadPaddingException, SignatureException {
SignatureData signatureData = new SignatureData();
// 对称加密过程
SecretKeySpec secretKeySpec = new SecretKeySpec(secretKey, "AES");
String transformation = "AES/CBC/PKCS5Padding";
IvParameterSpec ivParameterSpec = new IvParameterSpec(iv);
byte[] plainTextByte = data.getBytes(StandardCharsets.UTF_8);
Cipher cipher = Cipher.getInstance(transformation);
cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec, ivParameterSpec);
byte[] bytes = cipher.doFinal(plainTextByte);
signatureData.setCipherData(bytes);
// 数字签名过程
KeyPairGenerator signaturePairGenerator = KeyPairGenerator.getInstance("EC");
signaturePairGenerator.initialize(SIGNATURE_KEY_SIZE, SecureRandom.getInstanceStrong());
KeyPair signatureKeyPair = signaturePairGenerator.generateKeyPair();
Signature signature = Signature.getInstance("SHA256withECDSA");
signature.initSign(signatureKeyPair.getPrivate());
signature.update(bytes);
byte[] sign = signature.sign();
signatureData.setSignatureData(sign);
return signatureData;
}
```
```

**参考信息**

- 最新更新时间：2026-01-21 14:33:52

<a id="rule-366"></a>

### 366. SecH_GTS_JAVA_Weak_Cipher_Hash_Key_Message

| 字段 | 内容 |
|---|---|
| 问题级别 | 严重 |
| 语言 | JAVA |
| 标签 | SecBrella |
| 关联工具规则 | SecH_GTS_JAVA_Weak_Cipher_Hash_Key_Message |
| 规则类型 | 安全类（非编程规范） |
| 预估误报率 | 50% |

**审查要点**

禁止将hash(key||message)或者hash(message||key)当作MAC来使用，来源《密码算法应用规范V1.5》 2.5.1 规则：禁止将hash(key||message)或者hash(message||key)当作MAC来使用

**修复建议**

不能将 密钥与消息进行拼接 后再进行哈希运算。

**正确示例**

```text
```
/**
* sha256加密,只对消息加密
*
* @param str 要加密的字符串
* @return 加密后的字符串
*/
public static String getSha256Str(String str) {
MessageDigest messageDigest;
String encodeStr = "";
try {
messageDigest = MessageDigest.getInstance("SHA-256");
messageDigest.update(str.getBytes(StandardCharsets.UTF_8));
encodeStr = byte2Hex(messageDigest.digest());
} catch (NoSuchAlgorithmException e) {
e.printStackTrace();
}
return encodeStr;
}
```
```

**错误示例**

```text
```
场景一：使用system.arrayCopy合并消息和秘钥
Security.addProvider(new BouncyCastleProvider());
// 1、消息
String message = "hello world！";
KeyGenerator keyGenerator = KeyGenerator.getInstance("AES");
keyGenerator.init(256);
SecretKey secretKey = keyGenerator.generateKey();
// 2、密钥
byte[] key = secretKey.getEncoded();
MessageDigest md = MessageDigest.getInstance("SHA512");
byte[] msg = new byte[key.length + message.getBytes(StandardCharsets.UTF_8).length];
System.arraycopy(key, 0, msg, 0, key.length);
System.arraycopy(message.getBytes(StandardCharsets.UTF_8), 0, msg, key.length,
message.getBytes(StandardCharsets.UTF_8).length);
// 4、 拼接的内容进行hash
md.update(msg);
byte[] result = md.digest();
场景二：使用ArrayUtils.addAll 或add合并消息和秘钥
Security.addProvider(new BouncyCastleProvider());
// 1、消息
String message = "hello world！";
KeyGenerator keyGenerator = KeyGenerator.getInstance("AES");
keyGenerator.init(256);
SecretKey secretKey = keyGenerator.generateKey();
// 2、密钥
byte[] key = secretKey.getEncoded();
MessageDigest md = MessageDigest.getInstance("SHA512");
byte[] msg = ArrayUtils.addAll(key, message.getBytes(StandardCharsets.UTF_8));
// 4、 拼接的内容进行hash
md.update(msg);
byte[] result = md.digest();
```
```

**参考信息**

- 最新更新时间：2025-01-20 09:15:20

<a id="rule-367"></a>

### 367. SecH_GTS_JAVA_Weak_Encryption

| 字段 | 内容 |
|---|---|
| 问题级别 | 严重 |
| 语言 | JAVA |
| 标签 | SecBrella |
| 关联工具规则 | SecH_GTS_JAVA_Weak_Encryption |
| 规则类型 | 安全类（非编程规范） |
| 预估误报率 | 50% |

**审查要点**

禁止使用弱加密算法MD5WithRSA、SHA1WithRSA、SHA1WithDSA、ECDSA、SHA1WithECDSA、DSA、MD2、MD4、MD5、SHA、SHA0、SHA1、SHA-1

**修复建议**

根据业务使用正确的加密算法

**正确示例**

不能使用弱加密算法MD5WithRSA、SHA1WithRSA、SHA1WithDSA、ECDSA、SHA1WithECDSA、DSA、MD2、MD4、MD5、SHA、SHA0、SHA1、SHA-1

**错误示例**

```text
```
/**
* 测试.
*
* @throws NoSuchAlgorithmException
* @create 2020-11-24 16:40
*/
public static void test1() throws NoSuchAlgorithmException {
Signature signature1 = Signature.getInstance("SHA1WithRSA");
Signature signature2 = Signature.getInstance("SHA1WithDSA");
Signature signature3 = Signature.getInstance("ECDSA");
Signature signature4 = Signature.getInstance("SHA1WithECDSA");
Signature signature5 = Signature.getInstance("MD5WithRSA");
Signature signature6 = Signature.getInstance("DSA");
}
```
```

**参考信息**

- 最新更新时间：2026-01-21 14:28:21

<a id="rule-368"></a>

### 368. SecH_GTS_JAVA_Weak_Encryption_Inadequate_RSA_Padding

| 字段 | 内容 |
|---|---|
| 问题级别 | 严重 |
| 语言 | JAVA |
| 标签 | SecBrella |
| 关联工具规则 | SecH_GTS_JAVA_Weak_Encryption_Inadequate_RSA_Padding |
| 规则类型 | 安全类（非编程规范） |
| 预估误报率 | 50% |

**审查要点**

在使用RSA算法进行加密操作时，应使用OAEP填充方式

**修复建议**

```text
在使用RSA算法进行加密操作时，应使用OAEP填充方式
扩展资料：内部链接已省略
```

**正确示例**

```text
```
// 正确示例：使用RSA算法进行加密时，显式设置填充方式为OAEPWITHSHA-256ANDMGF1PADDING
private byte[] goodRsaPadding(String plainText) throws NoSuchAlgorithmException, NoSuchPaddingException,
IllegalBlockSizeException, BadPaddingException, InvalidKeyException {
KeyPairGenerator generator = KeyPairGenerator.getInstance("RSA");
generator.initialize(KEY_SIZE);
KeyPair keyPair = generator.generateKeyPair();
// 设置填充方式为OAEPWITHSHA-256ANDMGF1PADDING
Cipher encryptCipher = Cipher.getInstance("RSA/ECB/OAEPWITHSHA-256ANDMGF1PADDING");
encryptCipher.init(Cipher.ENCRYPT_MODE, keyPair.getPublic());
return encryptCipher.doFinal(plainText.getBytes(StandardCharsets.UTF_8));
}
```
```

**错误示例**

```text
```
private byte[] bad001RsaPadding(String plainText) throws NoSuchAlgorithmException, NoSuchPaddingException,
IllegalBlockSizeException, BadPaddingException, InvalidKeyException {
KeyPairGenerator generator = KeyPairGenerator.getInstance("RSA");
generator.initialize(KEY_SIZE);
KeyPair keyPair = generator.generateKeyPair();
// 未设置填充方式，默认的填充方式为PKCS1Padding
// Cipher encryptCipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
Cipher encryptCipher = Cipher.getInstance("RSA");
encryptCipher.init(Cipher.ENCRYPT_MODE, keyPair.getPublic());
return encryptCipher.doFinal(plainText.getBytes(StandardCharsets.UTF_8));
}
```
```

**参考信息**

- 最新更新时间：2025-04-10 17:21:52

<a id="rule-369"></a>

### 369. SecH_GTS_JAVA_XML_Entity_Injection_SAXTransformerFactory

| 字段 | 内容 |
|---|---|
| 问题级别 | 严重 |
| 语言 | JAVA |
| 标签 | SecBrella |
| 关联工具规则 | SecH_GTS_JAVA_XML_Entity_Injection_SAXTransformerFactory |
| 规则类型 | 安全类（非编程规范） |
| 预估误报率 | 50% |

**审查要点**

使用JDK包下javax.xml.transform.TransformerFactory、javax.xml.transform.sax.SAXTransformerFactory造成的XML实体注入

**修复建议**

禁用外部实体的使用 + 内部实体扩展数量的限制（具体方法见正确案例）

**正确示例**

```text
```
/**
* 正确用例：禁用外部实体的使用 + 内部实体扩展数量的限制
*/
@RequestMapping(value = "/SAXTransformerFactory_good001", method = RequestMethod.POST)
public void good001(MultipartFile file) throws IOException, TransformerException {
// TransformerFactory factory = TransformerFactory.newInstance();
TransformerFactory factory = SAXTransformerFactory.newInstance();
// 1.防止外部实体的使用，避免XXE
factory.setAttribute(XMLConstants.ACCESS_EXTERNAL_DTD, "");
factory.setAttribute(XMLConstants.ACCESS_EXTERNAL_STYLESHEET, "");
// 2. 防Bomb漏洞
factory.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, true);
// 修改JDK默认的64000内部实体扩展的值为50个
// 注意：如果使用的是Saxon解析器，则应加saxon前缀http://saxon.sf.net/feature/parserProperty?uri=
factory.setAttribute("http://www.oracle.com/xml/jaxp/properties/entityExpansionLimit", "50");
Transformer transformer = factory.newTransformer();
Source xmlInput = new StreamSource(file.getInputStream());
StreamResult xmlOutput = new StreamResult(new StringWriter());
transformer.transform(xmlInput, xmlOutput);
LOGGER.info(xmlOutput.getWriter().toString());
}
```
```

**错误示例**

```text
```
/**
* 错误用例1: 功能不需要XML实体解析，但未禁止，存在XXE&XEE漏洞。
*/
@RequestMapping(value = "/SAXTransformerFactory_bad001", method = RequestMethod.POST)
public void bad001(MultipartFile file) throws TransformerException, IOException {
TransformerFactory factory = SAXTransformerFactory.newInstance();
Transformer transformer = factory.newTransformer();
Source xmlInput = new StreamSource(file.getInputStream());
StreamResult xmlOutput = new StreamResult(new StringWriter());
transformer.transform(xmlInput, xmlOutput);
LOGGER.info(xmlOutput.getWriter().toString());
}
```
```

**参考信息**

- 最新更新时间：2026-01-21 14:03:47

<a id="rule-370"></a>

### 370. SecH_GTS_JAVA_X_ForwardedFor_IP_Check

| 字段 | 内容 |
|---|---|
| 问题级别 | 严重 |
| 语言 | JAVA |
| 标签 | SecBrella |
| 关联工具规则 | SecH_GTS_JAVA_X_ForwardedFor_IP_Check |
| 规则类型 | 安全类（非编程规范） |
| 预估误报率 | 50% |

**审查要点**

```text
request.getheader（”x-forwarded-for”）获取的IP不经过校验告警。
用户可以直接在x-forwarded-for中插入 ip1;ip2等值，不一定第一个IP就是真实IP，要经过校验，使用假IP记录客户操作日志、限制用户频次等都存在问题，用户可以更换x-forwarded-for中的值绕过
【误报提示】：Nginx已正确配置，可参考修复建议
【补充】
1、无代理时，可通过remote_addr获取用户ip
2、有代理时
方法一：确保最外层代理nginx：proxy_set_header X-Forwarded-For $remote_addr;
方法二：通过x-forwarded-for获取，并自右向左进行代理白名单校验，将已知代理服务器ip过滤后，拿到的第一个ip就是客户ip
特殊：只有一层代理且nginx配置proxy_set_header X-Real-IP $remote_addr时，才可以用x-real-ip获取
【误报提示】若先获取X-Real-Client-Addr的header值，如果没有值说明是单层代理，可以通过X-Real-IP的header值获取客户端ip，建议屏蔽。可参考内部链接已省略
```

**修复建议**

```text
方法一：
最外层Nginx配置proxy_set_header X-Forwarded-For $remote_addr;
方法二：
java代码中自右向左进行代理白名单校验，第一个不符合白名单的就是客户端IP（通过x-forwarded-for的拼接的特性，请求到达服务器时，伪造的IP也会在X-Forwarded-For值的左边，从右向左遍历就可以避免取到这些伪造的IP地址。在遍历的过程中通过白名单、正则匹配等方式，将已知的代理服务IP进行过滤，最后拿到的第一个IP就是客户端IP）（前提是输入为ip且白名单完备）
```

**正确示例**

```text
```
// 正确案例：
public String getClientIp(HttpServletRequest request) {
String xForwardedFor = request.getHeader("X-Forwarded-For");
if (xForwardedFor == null || xForwardedFor.isEmpty()) {
return request.getRemoteAddr();
}
String[] ips = xForwardedFor.split(",");
for (int i = ips.length - 1; i >= 0; i--) {
String ip = ips[i].trim();
if (isValidProxy(ip)) { // 规则只检测有无check、valid等字眼的函数，具体实现一般为白名单检测（前提要保证不含非ip内容）
continue;
}
return ip;
}
return request.getRemoteAddr(); // 全部都是代理IP时回退
}
public boolean isValidProxy(String ip) throws IllegalArgumentException{
return PROXY_WHITELIST.contains(ip);
}
```
```

**错误示例**

```text
```
public String checkIp1(HttpServletRequest request) {
String ipStr = request.getHeader("x-forwarded-for");
return ipStr;
}
```
```

**参考信息**

- 最新更新时间：2026-01-31 09:33:46

<a id="rule-371"></a>

### 371. SecH_GTS_JAVA_Zip_GetName_Check

| 字段 | 内容 |
|---|---|
| 问题级别 | 严重 |
| 语言 | JAVA |
| 标签 | SecBrella |
| 关联工具规则 | SecH_GTS_JAVA_Zip_GetName_Check |
| 规则类型 | 安全类（非编程规范） |
| 预估误报率 | 50% |

**审查要点**

ZipEntry.getName() 函数返回文件路径信息容易被用户串改，为不可信数据，使用前应对getName()函数返回值信息进行归一化和校验，避免路径遍历问题。

**修复建议**

ZipEntry.getName() 函数返回文件路径信息容易被用户串改，为不可信数据，使用前应对getName()函数返回值信息进行归一化和校验，避免路径遍历问题。

**正确示例**

```text
```
public void unZip(String unZipfileName) {
ZipFile zipFile = new ZipFile(unZipfileName);
Enumeration entries = zipFile.getEntries();
ZipEntry entry = (ZipEntry) entries.nextElement();
file = new File(entry.getName());
if(!valid(file)) {
return;
}
}
public static boolean valid(File file) {
// 路径归一化处理
String canonicalPath = file.getCanonicalPath();
// 校验路径起始路径是否合法
if (canonicalPath.startsWith("dir")) {
return true;
}
return false;
}
```
```

**错误示例**

```text
```
ZipFile zipFile = new ZipFile(“/usr/test.txt”)
Enumeration entries = zipFile.getEntries()
ZipEntry entry = (ZipEntry)entries.nextElement();
file = new File(entry.getName());
```
```

**参考信息**

- 最新更新时间：2024-11-28 11:15:07

<a id="rule-372"></a>

### 372. SecH_GTS_JAVA_Zip_GetSize_Check

| 字段 | 内容 |
|---|---|
| 问题级别 | 严重 |
| 语言 | JAVA |
| 标签 | SecBrella |
| 关联工具规则 | SecH_GTS_JAVA_Zip_GetSize_Check |
| 规则类型 | 安全类（非编程规范） |
| 预估误报率 | 20% |

**审查要点**

```text
Java语言常见的解压工具，通过getSize()获取的大小为压缩文件大小且容易被篡改，直接使用getSize()方法获取大小用于解压前的校验，容易存在Dos攻击风险，造成磁盘资源耗尽。
【提示信息】规则对压缩工具的getSize()方法直接告警，此方法非可能并非实际文件的大小，具有欺诈性，请通过文件流字节码累计压缩包实际大小，参考实例如下。
```

**修复建议**

禁止使用ZipEntry.getSize()判断大小，应使用压缩包实际文件大小

**正确示例**

```text
```
// 【修复方式】压缩包解压时，通过文件流累计字符大小和文件数量，超过数量以后抛出异常或终止
public void unzip(String fileName) {
try {
ZipFile zis = new ZipFile(fileName);
for (Enumeration entries = zis.getEntries(); entries.hasMoreElements(); ) {
entry = (ZipEntry) entries.nextElement();
file = new File(entry.getName());
inputStream = zis.getInputStream(entry);
fileOut = new FileOutputStream(file);
dest = new BufferedOutputStream(fileOut, BUFFER);
while (total < TOOBIG && (count = inputStream.read(this.buf)) > 0) {
dest.write(this.buf, 0, this.readedBytes);
total += count;
if (total > TOOBIG) {
break;
}
}
countEntries++;
if (countEntries > TOOMANY) {
break;
}
}
} catch (IOException e) {
//请用户自行编写日志
} finally {
//请用户自行编写关流
}
}
```
```

**错误示例**

```text
```
ZipFile zipFile = new ZipFile(“/usr/test.txt”)
Enumeration entries = zipFile.getEntries();
ZipEntry entry = (ZipEntry)entries.nextElement();
// 该场景直接使用entry.getSize()，getSize()方法返回的值不可信任，告警
long size = entry.getSize();
```
```

**参考信息**

- 最新更新时间：2024-11-28 10:35:59

<a id="rule-373"></a>

### 373. SecH_GTS_JAVA_uploadfile_check_size

| 字段 | 内容 |
|---|---|
| 问题级别 | 严重 |
| 语言 | JAVA |
| 标签 | SecBrella |
| 关联工具规则 | SecH_GTS_JAVA_uploadfile_check_size |
| 规则类型 | 安全类（非编程规范） |
| 预估误报率 | 50% |

**审查要点**

文件上传因限制文件大小。

**修复建议**

```text
上传文件需要对上传内容大小进行判断
推荐使用wsf框架校验
```

**正确示例**

```text
```
// 正确场景1：MultipartFile方式文件上传，自定义方法校验文件大小
@RequestMapping("/upload")
public String handleFileUpload(MultipartFile file) {
if (file.isEmpty()) {
return "文件为空";
}
if (checkFileSize(file.getSize(),1000,"M")){
return "文件大小不能超过1000M";
}
String fileName = file.getOriginalFilename(); // 获取文件名
String filePath = "dir/"; // 文件上传后的路径
File dest = new File(filePath + fileName);
file.transferTo(dest); // 文件保存到本地
return "上传成功";
}
// 判断文件大小
public boolean checkFileSize(Long len, int size, String unit) {
double fileSize = 0;
if ("B".equals(unit.toUpperCase())) {
fileSize = (double) len;
} else if ("K".equals(unit.toUpperCase())) {
fileSize = (double) len / 1024;
} else if ("M".equals(unit.toUpperCase())) {
fileSize = (double) len / 1048576;
} else if ("G".equals(unit.toUpperCase())) {
fileSize = (double) len / 1073741824;
}
return fileSize > size;
}
// 场景二：安全场景，通过内置API限制文件上传大小
public void doPostTwo(HttpServletRequest request, HttpServletResponse response) throws IOException {
request.setCharacterEncoding("UTF-8");
response.setContentType("text/html;charset=utf-8");
// 1、设置临时上传的路径
DiskFileItemFactory disk = new DiskFileItemFactory(10240, new File("disk/"));
// 2、设置文件上传的目标路径
String servePath = getServletContext().getRealPath("/serviceDisk");
// 3 、申明upload
ServletFileUpload up = new ServletFileUpload(disk);
long fileSize = 1024*20; // 大小为20M
up.setFileSizeMax(fileSize);
try {
List<FileItem> list = up.parseRequest(request); // 4、解析request
for (FileItem file : list) {
// 获取上传文件的名字
String fileName = file.getName();
fileName = fileName.substring(fileName.lastIndexOf("\\") + 1);
// 获取上传文件的后缀
String extName = fileName.substring(fileName.lastIndexOf("."));
// 申明UUID
String uuid = UUID.randomUUID().toString().replace("-", "");
// 组成新的名称
String newName = uuid + extName;
// 上传文件
FileUtils.copyInputStreamToFile(file.getInputStream(), new File(servePath + "/" + newName));
}
} catch (FileUploadException e) {
// todo
}
}
```
```

**错误示例**

```text
```
// 错误场景1：MultipartFile方式文件上传，未校验文件大小
@RequestMapping("/upload")
public String handleFileUpload(MultipartFile file) {
if (file.isEmpty()) {
return "文件为空";
}
String fileName = file.getOriginalFilename(); // 获取文件名
String filePath = "dir/"; // 文件上传后的路径
File dest = new File(filePath + fileName);
file.transferTo(dest); // 文件保存到本地
return "上传成功";
}
// 错误场景2：上传文件未校验文件大小
public class UploadServlet extends HttpServlet {
private static final long serialVersionUID = 1L;
protected void service(HttpServletRequest request, HttpServletResponse response) throws IOException {
// 设定编码，可以获取中文文件名
request.setCharacterEncoding("UTF-8");
// 获取tomcat下的upload目录的路径
String path = getServletContext().getRealPath("/upload");
// 临时文件目录
String tempPath = getServletContext().getRealPath("/temp");
// 检查我们是否有文件上传请求
// 1、声明DiskFileItemFactory工厂类，用于在指定磁盘上设置一个临时目录
DiskFileItemFactory disk = new DiskFileItemFactory(1024 * 10, new File(tempPath));
// 2、声明ServletFileUpload，接收上面的临时文件。也可以默认值
ServletFileUpload up = new ServletFileUpload(disk);
// 3、解析request
List<FileItem> list = up.parseRequest(request);
if (list.isEmpty()) {
return;
}
for (FileItem file : list) {
// 获取文件本身的名称
String fileName = file.getName();
// 处理文件名称
fileName = fileName.substring(fileName.lastIndexOf("\\") + 1);
// 修改名称
String extName = fileName.substring(fileName.lastIndexOf("."));
String newName = UUID.randomUUID().toString().replace("-", "") + extName;
// 保存新的名称，并写出到新文件中
try {
file.write(new File(path + "/" + newName));
} catch (Exception e) {
throw new RuntimeException(e);
}
}
}
```
```

**参考信息**

- 最新更新时间：2026-01-21 11:42:59

<a id="rule-374"></a>

### 374. SecJ_Cookie_Security_Path_not_Set

| 字段 | 内容 |
|---|---|
| 问题级别 | 一般 |
| 语言 | JAVA |
| 标签 | SecBrella |
| 关联工具规则 | SecJ_Cookie_Security_Path_not_Set |
| 规则类型 | 安全类（非编程规范） |
| CWE 信息 | 310 |
| 预估误报率 | 20% |

**审查要点**

Path（路径）用于设置可以读取一个cookie的最顶层的目录。如果要限定只有/service/test/目录及其子目录下的页面可以访问该cookie，要把cookies的path属性设置成“/service/test/”。

**修复建议**

为会话标识的cookie设置适当限制的path属性值。

**正确示例**

```text
public void good(ServletRequest request, ServletResponse response, FilterChain chain)
throws IOException, ServletException {
if (null == chain) {
return;
}
HttpServletResponse httpResponse = (HttpServletResponse) response;
try {
httpResponse.addHeader("Set-Cookie", "Path=xxx");
} catch (Exception e) {
}
chain.doFilter(request, httpResponse);
}
```

**错误示例**

```text
public void bad(ServletRequest request, ServletResponse response, FilterChain chain)
throws IOException, ServletException {
if (null == chain) {
return;
}
HttpServletResponse httpResponse = (HttpServletResponse) response;
try {
StringBuffer userCookie = new StringBuffer(32);
userCookie.append("Max-Age=").append(60 * 60 * 4).append("; ");
userCookie.append("cc");
/* POTENTIAL FLAW: cookie未设置Secure、domain和path参数会导致cookie不能生效。*/
httpResponse.addHeader("Set-Cookie", userCookie.toString());
} catch (Exception e) {
}
chain.doFilter(request, httpResponse);
}
```

**参考信息**

- 链接：内部链接已省略
- 最新更新时间：2020-12-03 18:20:45

<a id="rule-375"></a>

### 375. SecJ_Privacy_Violation_Submit_By_Get_Method

| 字段 | 内容 |
|---|---|
| 问题级别 | 一般 |
| 语言 | JAVA |
| 标签 | SecBrella |
| 关联工具规则 | SecJ_Privacy_Violation_Submit_By_Get_Method |
| 规则类型 | 安全规范规则类 |
| CWE 信息 | 200 |
| 预估误报率 | 20% |

**审查要点**

web系统中泄露个人敏感数据数据。

**修复建议**

禁止使用Get方式提交个人数据,可以使用Post请求来避免个人数据被缓存、记录。

**正确示例**

```text
使用Post请求来避免个人数据被缓存、记录，在用javax.servlet.http.HttpServletResponse.sendRedirect方法重定向时URL参数不要拼接个人数据，如果URL为(delete|add|insert|update|edit)?xxx (xxx为个人数据)则会被判定为传递个人数据 ， 如果需要传递个人数据参数可使用spring框架RedirectAttributes提供的addFlashAttribute传递参数。
```java
@Controller
@RequestMapping({"v1"})
public class ResourceStatusController {
@PostMapping(value = "/test")
public String index(RedirectAttributes attributes) {
attributes.addFlashAttribute("pass", "***");
return "redirect:/test/next";
}
}
```
如果是服务器内部forward跳转使用request.setAttribute传递个人数据参数。
```java
public class CWE359_HW_NSCC_JAVA_Privacy_Violation_Submit_by_Get_Method_Good extends HttpServlet {
@Override
protected void doPost(HttpServletRequest req, HttpServletResponse response) throws ServletException, IOException {
response.setContentType("text/html; charset=UTF-8");
req.setAttribute("pass", "sa");
req.getRequestDispatcher("new.html").forward(req,response);
}
}
```
```

**错误示例**

```text
public class CWE359_HW_NSCC_JAVA_Privacy_Violation_Submit_by_Get_Method_01 extends HttpServlet {
@Override
protected void doGet(HttpServletRequest req, HttpServletResponse response) throws ServletException, IOException {
String ss = "add?pass='sa'";
response.setContentType("text/html; charset=UTF-8");
/* POTENTIAL FLAW: 使用Get方式提交表单数据，通过sendRedirect()方法重定向到指定地址，有可能在浏览器地址栏暴露提交的参数，容易导致个人数据泄露。 */
response.sendRedirect(ss);
}
}
```

**参考信息**

- 最新更新时间：2021-09-15 20:04:47

<a id="rule-376"></a>

### 376. SecS_GTS_JAVA_Insecure_Random_UUID_Salt

| 字段 | 内容 |
|---|---|
| 问题级别 | 严重 |
| 语言 | JAVA |
| 标签 | SecBrella |
| 关联工具规则 | SecS_GTS_JAVA_Insecure_Random_UUID_Salt |
| 规则类型 | 安全类（非编程规范） |
| 预估误报率 | 50% |

**审查要点**

禁止使用UUID生成的随机数作为盐值

**修复建议**

应该使用安全随机数生成盐值

**正确示例**

```text
```
SecureRandom random = SecureRandom.getInstance("SHA1PRNG");
byte[] salt = new byte[8];
random.nextBytes(salt);
int iterCount = 10000;
PBEKeySpec spec = new PBEKeySpec(password, salt, iterCount, 256);
SecretKeyFactory skf = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
```
```

**错误示例**

```text
```
UUID salt = UUID.randomUUID();
int iterCount = 10000;
PBEKeySpec spec = new PBEKeySpec(password, salt.toString().getBytes(), iterCount, 256);
SecretKeyFactory skf = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
byte[] hashed = skf.generateSecret(spec).getEncoded();
```
```

**参考信息**

- 最新更新时间：2025-01-22 16:57:55

<a id="rule-377"></a>

### 377. SecS_Server_Side_Request_Forgery

| 字段 | 内容 |
|---|---|
| 问题级别 | 严重 |
| 语言 | JAVA |
| 标签 | SecBrella |
| 关联工具规则 | SecS_Server_Side_Request_Forgery |
| 规则类型 | 安全类（非编程规范） |
| CWE 信息 | 918 |
| 预估误报率 | 35% |

**审查要点**

```text
当攻击者可以影响应用程序服务器建立的网络连接时，将会发生 Server-Side Request Forgery，攻击者可以通过这种漏洞构造形成由服务端发起请求。一般情况下，SSRF攻击的目标是从外网无法直接访问的内部系统。具体来说，攻击者向服务端发送包含恶意 URL 链接的请求，借由服务端去访问此 URL，以获取受保护网络内的资源。
```

**修复建议**

```text
请勿基于用户控制的数据建立网络连接，并要确保请求发送给预期的目的地。如果需要提供用户数据来构建目的地 URI，请采用间接方法：例如创建一份合法资源名的白名单，并且规定用户只能选择其中的文件名。通过这种方法，用户就不能直接由自己来指定资源的名称了。
当前规则判断是否实现了checkAccessURL校验函数，如存在其余校验函数，建议按屏蔽处理。
```

**正确示例**

```text
```java
String data = ""; /* Initialize data */
/* Read data from cookies */
Cookie cookieSources[] = request.getCookies();
if (cookieSources != null) {
data = cookieSources[0].getValue();
}
// 【GOOD】 data经过了校验
if (validURL(data)) {
return;
}
try {
URL url = new URL(data);
url.openStream();
} catch (MalformedURLException e) {
e.printStackTrace();
} catch (IOException e) {
e.printStackTrace();
}
private boolean validURL(String url) {
// 对url做校验，建议使用白名单、正则等方式校验
}
```
```

**错误示例**

```text
```java
String data;
data = ""; /* Initialize data */
/* Read data from cookies */
Cookie cookieSources[] = request.getCookies();
if (cookieSources != null) {
data = cookieSources[0].getValue();
}
try {
// 【POTENTIAL FLAW】: data由cookie得到，存在被控制风险
URLDataSource urlDataSource = new URLDataSource(new URL(data));
} catch (MalformedURLException e) {
e.printStackTrace();
}
```
```

**参考信息**

- 链接：内部链接已省略
- 最新更新时间：2024-08-19 19:37:48

<a id="rule-378"></a>

### 378. TLW_TWO_LOCK_WAIT

| 字段 | 内容 |
|---|---|
| 问题级别 | 一般 |
| 语言 | JAVA |
| 标签 | spotbugs |
| 关联工具规则 | TLW_TWO_LOCK_WAIT |
| 规则类型 | 通用类（非编程规范） |
| 预估误报率 | 10% |

**审查要点**

```text
Wait with two locks held
Waiting on a monitor while two locks are held may cause deadlock.   Performing a wait only releases the lock on the object being waited on, not any other locks.  This not necessarily a bug, but is worth examining closely.
```

**参考信息**

- 最新更新时间：2025-06-16 15:16:17

<a id="rule-379"></a>

### 379. Throw_Inside_Finally

| 字段 | 内容 |
|---|---|
| 问题级别 | 严重 |
| 语言 | JAVA |
| 标签 | CodeMars |
| 关联工具规则 | Throw_Inside_Finally |
| 规则类型 | 安全类（非编程规范） |
| CWE 信息 | 391 |
| 预估误报率 | 15% |

**审查要点**

使用 finally 块中的 throw 语句会通过 try-catch-finally 破坏逻辑进度。

**修复建议**

将返回指令移到 finally 块之外。如果必须要 finally 块返回一个值，可以简单地将该返回值赋给一个本地变量，然后在 finally 块执行完毕后返回该变量。

**正确示例**

```text
public void test01Good(Connection conn) throws FileNotFoundException {
FileInputStream fis = null;
Statement stmt = null;
try {
stmt = conn.createStatement();
fis = new FileInputStream("badFile.txt");
...
} catch (FileNotFoundException fe) {
log("File not found.");
throw fe;
} catch (SQLException se) {
//handle error
} finally {
if (fis != null) {
try {
fis.close();
} catch (IOException ie) {
log(ie);
}
}
if (stmt != null) {
try {
stmt.close();
} catch (SQLException e) {
log(e);
}
}
}
}
```

**错误示例**

```text
public void test01Bad() {
try {
return;
} finally {
/* POTENTIAL FLAW: Using a throw statement inside a finally block breaks the logical progression through the try-catch-finally. */
throw new RuntimeException("Run Exception");
}
}
```

**参考信息**

- 链接：内部链接已省略
- 最新更新时间：2019-11-14 15:46:57

<a id="rule-380"></a>

### 380. UG_SYNC_SET_UNSYNC_GET

| 字段 | 内容 |
|---|---|
| 问题级别 | 一般 |
| 语言 | JAVA |
| 标签 | spotbugs |
| 关联工具规则 | UG_SYNC_SET_UNSYNC_GET |
| 规则类型 | 通用类（非编程规范） |
| 预估误报率 | 5% |

**审查要点**

```text
Unsynchronized get method, synchronized set method
This class contains similarly-named get and set methods where the set method is synchronized and the get method is not.  This may result in incorrect behavior at runtime, as callers of the get method will not necessarily see a consistent state for the object.  The get method should be made synchronized.
```

**参考信息**

- 最新更新时间：2020-05-28 19:21:36

<a id="rule-381"></a>

### 381. UL_UNRELEASED_LOCK

| 字段 | 内容 |
|---|---|
| 问题级别 | 一般 |
| 语言 | JAVA |
| 标签 | spotbugs |
| 关联工具规则 | UL_UNRELEASED_LOCK |
| 规则类型 | 通用类（非编程规范） |
| 预估误报率 | 10% |

**审查要点**

```text
Method does not release lock on all paths
This method acquires a JSR-166 (java.util.concurrent) lock,but does not release it on all paths out of the method. In general, the correct idiomfor using a JSR-166 lock is:
Lock l = ...;l.lock();try { // do something} finally { l.unlock();}
```

**参考信息**

- 最新更新时间：2025-05-27 09:58:46

<a id="rule-382"></a>

### 382. UMAC_UNCALLABLE_METHOD_OF_ANONYMOUS_CLASS

| 字段 | 内容 |
|---|---|
| 问题级别 | 一般 |
| 语言 | JAVA |
| 标签 | spotbugs |
| 关联工具规则 | UMAC_UNCALLABLE_METHOD_OF_ANONYMOUS_CLASS |
| 规则类型 | 通用类（非编程规范） |
| 预估误报率 | 10% |

**审查要点**

```text
Uncallable method defined in anonymous class
This anonymous class defines a method that is not directly invoked and does not overridea method in a superclass. Since methods in other classes cannot directly invoke methodsdeclared in an anonymous class, it seems that this method is uncallable. The methodmight simply be dead code, but it is also possible that the method is intended tooverride a method declared in a superclass, and due to a typo or other error the method does not,in fact, override the method it is intended to.
```

**修复建议**

```text
错误用法 -匿名内部类中定义的不可调用的方法
Added check for uncallable method of an anonymous inner class.
```

**错误示例**

```text
For example, in the following code, it is impossible to invoke the initalValue method (because the name is misspelled and as a result is doesn't override a method in ThreadLocal).
private static ThreadLocal serialNum = new ThreadLocal() {
protected synchronized Object initalValue() {
return new Integer(nextSerialNum++);
}
};
```

**参考信息**

- 链接：内部链接已省略
- 最新更新时间：2020-05-28 19:22:30

<a id="rule-383"></a>

### 383. UM_UNNECESSARY_MATH

| 字段 | 内容 |
|---|---|
| 问题级别 | 一般 |
| 语言 | JAVA |
| 标签 | spotbugs |
| 关联工具规则 | UM_UNNECESSARY_MATH |
| 规则类型 | 通用类（非编程规范） |
| 预估误报率 | 1% |

**审查要点**

```text
Method calls static Math class method on a constant value
This method uses a static method from java.lang.Math on a constant value. This method'sresult in this case, can be determined statically, and is faster and sometimes more accurate tojust use the constant. Methods detected are:
Method
Parameter
abs
-any-
acos
0.0 or 1.0
asin
0.0 or 1.0
atan
0.0 or 1.0
atan2
0.0
cbrt
0.0 or 1.0
ceil
-any-
cos
0.0
cosh
0.0
exp
0.0 or 1.0
expm1
0.0
floor
-any-
log
0.0 or 1.0
log10
0.0 or 1.0
rint
-any-
round
-any-
sin
0.0
sinh
0.0
sqrt
0.0 or 1.0
tan
0.0
tanh
0.0
toDegrees
0.0 or 1.0
toRadians
0.0
```

**参考信息**

- 最新更新时间：2020-05-28 19:22:43

<a id="rule-384"></a>

### 384. UPM_UNCALLED_PRIVATE_METHOD

| 字段 | 内容 |
|---|---|
| 问题级别 | 一般 |
| 语言 | JAVA |
| 标签 | spotbugs |
| 关联工具规则 | UPM_UNCALLED_PRIVATE_METHOD |
| 规则类型 | 通用类（非编程规范） |
| 预估误报率 | 15% |

**审查要点**

```text
私有方法从未被调用。此私有方法从未被调用。虽然可能会通过反射调用该方法，但更可能的是该方法从未被使用，应该删除。
注意事项：
1、PostConstruct注解的方法属于高误报场景之一，分析属实后可通过误报流程处理
2、为尽可能减少误报，建议放到版本级扫描
```

**修复建议**

删除未被调用的私有方法。

**正确示例**

```text
```java
public void init() {
goodPractice();
}
private void goodPractice() {
// [GOOD] 私有方法被调用
// code
}
```
```

**错误示例**

```text
```java
private void badPractice() {
// [BAD] 方法未被调用
// code
}
```
```

**参考信息**

- 最新更新时间：2026-04-25 09:48:03

<a id="rule-385"></a>

### 385. UR_UNINIT_READ

| 字段 | 内容 |
|---|---|
| 问题级别 | 一般 |
| 语言 | JAVA |
| 标签 | spotbugs |
| 关联工具规则 | UR_UNINIT_READ |
| 规则类型 | 通用类（非编程规范） |
| 预估误报率 | 10% |

**审查要点**

```text
Uninitialized read of field in constructor
This constructor reads a field which has not yet been assigned a value.  This is often caused when the programmer mistakenly uses the field instead of one of the constructor's parameters.
```

**修复建议**

使用函数的参数或初始化的字段

**错误示例**

```text
String a;
public FindBugsTest(String b) {
String abc = a; // UR_UNINIT_READ ,将b误写为a
System.out.println(abc);
}
```

**参考信息**

- 链接：内部链接已省略
- 最新更新时间：2020-05-28 19:21:36

<a id="rule-386"></a>

### 386. UR_UNINIT_READ_CALLED_FROM_SUPER_CONSTRUCTOR

| 字段 | 内容 |
|---|---|
| 问题级别 | 一般 |
| 语言 | JAVA |
| 标签 | spotbugs |
| 关联工具规则 | UR_UNINIT_READ_CALLED_FROM_SUPER_CONSTRUCTOR |
| 规则类型 | 通用类（非编程规范） |
| 预估误报率 | 20% |

**审查要点**

```text
Uninitialized read of field method called from constructor of superclass
This method is invoked in the constructor of the superclass. At this point, the fields of the class have not yet initialized.
To make this more concrete, consider the following classes:
abstract class A { int hashCode; abstract Object getValue(); A() { hashCode = getValue().hashCode(); }}class B extends A { Object value; B(Object v) { this.value = v; } Object getValue() { return value; }}
When a B is constructed,the constructor for the A class is invokedbefore the constructor for B sets value.Thus, when the constructor for A invokes getValue,an uninitialized value is read for value.
```

**错误示例**

```text
abstract class A {
int hashCode;
abstract Object getValue();
A(){
hashCode = getValue().hashCode();
}
}
class B extends A {
Object value;
B(Object v) {
this.value = v;
}
Object getValue() {
return value;
}
}
当B是创建时，A的构造函数将在B为value赋值之前触发，然而在A的初始化方法调用getValue方法时value这个变量还没有被初始化。
```

**参考信息**

- 链接：内部链接已省略
- 最新更新时间：2020-05-28 19:21:36

<a id="rule-387"></a>

### 387. UWF_UNWRITTEN_FIELD

| 字段 | 内容 |
|---|---|
| 问题级别 | 一般 |
| 语言 | JAVA |
| 标签 | spotbugs |
| 关联工具规则 | UWF_UNWRITTEN_FIELD |
| 规则类型 | 通用类（非编程规范） |
| 预估误报率 | 10% |

**审查要点**

```text
Unwritten field
This field is never written.  All reads of it will return the defaultvalue. Check for errors (should it have been initialized?), or remove it if it is useless.
```

**参考信息**

- 最新更新时间：2020-05-28 19:21:58

<a id="rule-388"></a>

### 388. UW_UNCOND_WAIT

| 字段 | 内容 |
|---|---|
| 问题级别 | 一般 |
| 语言 | JAVA |
| 标签 | spotbugs |
| 关联工具规则 | UW_UNCOND_WAIT |
| 规则类型 | 通用类（非编程规范） |
| 预估误报率 | 10% |

**审查要点**

```text
Unconditional wait
This method contains a call to java.lang.Object.wait() which is not guarded by conditional control flow.  The code should verify that condition it intends to wait for is not already satisfied before calling wait; any previous notifications will be ignored.
```

**修复建议**

```text
多线程错误 -无条件等待
将wait()放到条件控制流中：If we are not enabled, then waitif (!enabled) { /*条件判断不在同步块内*/
try {
synchronized (lock) { /*if (!enabled) 应该在这里*/
lock.wait();
…
```

**错误示例**

```text
public class Test
{
void clueless() throws Exception
{
synchronized(this)
{
this.wait(); // VIOLATION
}
}
}
该检测模式寻找在进入同步块时无条件地wait()，与wait相关的条件判断在无锁（不在同步块内）情况下进行的，有可能导致其它线程的notification被忽略。
```

**参考信息**

- 链接：内部链接已省略
- 最新更新时间：2020-05-28 19:21:35

<a id="rule-389"></a>

### 389. VA_PRIMITIVE_ARRAY_PASSED_TO_OBJECT_VARARG

| 字段 | 内容 |
|---|---|
| 问题级别 | 一般 |
| 语言 | JAVA |
| 标签 | spotbugs |
| 关联工具规则 | VA_PRIMITIVE_ARRAY_PASSED_TO_OBJECT_VARARG |
| 规则类型 | 通用类（非编程规范） |
| 预估误报率 | 10% |

**审查要点**

```text
Primitive array passed to function expecting a variable number of object arguments
This code passes a primitive array to a function that takes a variable number of object arguments.This creates an array of length one to hold the primitive array and passes it to the function.
```

**参考信息**

- 最新更新时间：2020-05-28 19:22:47

<a id="rule-390"></a>

### 390. WA_AWAIT_NOT_IN_LOOP

| 字段 | 内容 |
|---|---|
| 问题级别 | 一般 |
| 语言 | JAVA |
| 标签 | spotbugs |
| 关联工具规则 | WA_AWAIT_NOT_IN_LOOP |
| 规则类型 | 通用类（非编程规范） |
| 预估误报率 | 10% |

**审查要点**

```text
Condition.await() not in loop
This method contains a call to java.util.concurrent.await() (or variants) which is not in a loop.  If the object is used for multiple conditions, the condition the caller intended to wait for might not be the one that actually occurred.
```

**参考信息**

- 最新更新时间：2025-05-27 09:58:46

<a id="rule-391"></a>

### 391. WMI_WRONG_MAP_ITERATOR

| 字段 | 内容 |
|---|---|
| 问题级别 | 一般 |
| 语言 | JAVA |
| 标签 | spotbugs |
| 关联工具规则 | WMI_WRONG_MAP_ITERATOR |
| 规则类型 | 通用类（非编程规范） |
| 预估误报率 | 10% |

**审查要点**

```text
Inefficient use of keySet iterator instead of entrySet iterator
This method accesses the value of a Map entry, using a key that was retrieved froma keySet iterator. It is more efficient to use an iterator on the entrySet of the map, to avoid theMap.get(key) lookup.
```

**参考信息**

- 最新更新时间：2020-05-28 19:22:41

<a id="rule-392"></a>

### 392. WS_WRITEOBJECT_SYNC

| 字段 | 内容 |
|---|---|
| 问题级别 | 一般 |
| 语言 | JAVA |
| 标签 | spotbugs |
| 关联工具规则 | WS_WRITEOBJECT_SYNC |
| 规则类型 | 通用类（非编程规范） |
| 预估误报率 | 10% |

**审查要点**

```text
Class's writeObject() method is synchronized but nothing else is
This class has a writeObject() method which is synchronized; however, no other method of the class is synchronized.
```

**参考信息**

- 最新更新时间：2025-06-16 15:16:17

<a id="rule-393"></a>

### 393. Weak_Cryptographic_Hash_Salt

| 字段 | 内容 |
|---|---|
| 问题级别 | 致命 |
| 语言 | JAVA |
| 标签 | CodeMars |
| 关联工具规则 | Weak_Cryptographic_Hash_Salt |
| 规则类型 | 安全规范规则类 |
| CWE 信息 | 328,760,916,325 |
| 预估误报率 | 20% |

**审查要点**

采用硬编码处理 salt 绝非一个好方法。这不仅是因为所有项目开发人员都可以使用 hardcoded salt 来查看该 salt，而且还会使解决这一问题变得极其困难。一旦将该代码投入生产，则无法轻易更改该 salt。如果攻击者知道该 salt 的值，他们就可以计算出该应用程序的“彩虹表”，并更轻松地确定散列值。

**修复建议**

salt 始终不能为硬编码

**正确示例**

```text
public static byte[] createHash(char[] password) throws NoSuchAlgorithmException, InvalidKeySpecException {
SecureRandom random = SecureRandom.getInstance("SHA1PRNG");
byte[] salt = new byte[8];
random.nextBytes(salt);
PBEKeySpec spec = new PBEKeySpec(password, salt, 20000000, 256);
//PBKDF2WithHmacSHA256 is supportted from JDK1.8
SecretKeyFactory skf = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
byte[] hashed = skf.generateSecret(spec).getEncoded();
return hashed;
}
```

**错误示例**

```text
public static byte[] createHash(char[] password) throws NoSuchAlgorithmException, InvalidKeySpecException {
PBEKeySpec spec = new PBEKeySpec(password, "salt".getBytes(), 20000000, 256);
//PBKDF2WithHmacSHA256 is supportted from JDK1.8
SecretKeyFactory skf = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
byte[] hashed = skf.generateSecret(spec).getEncoded();
return hashed;
}
```

**参考信息**

- 链接：内部链接已省略
- 最新更新时间：2021-06-09 10:38:29

<a id="rule-394"></a>

### 394. XSS_REQUEST_PARAMETER_TO_SERVLET_WRITER

| 字段 | 内容 |
|---|---|
| 问题级别 | 严重 |
| 语言 | JAVA |
| 标签 | spotbugs |
| 关联工具规则 | XSS_REQUEST_PARAMETER_TO_SERVLET_WRITER |
| 规则类型 | 通用类（非编程规范） |
| 预估误报率 | 10% |

**审查要点**

```text
Servlet reflected cross site scripting vulnerability
This code directly writes an HTTP parameter to Servlet output, which allows for a reflected cross site scriptingvulnerability. See http://en.wikipedia.org/wiki/Cross-site_scriptingfor more information.
SpotBugs looks only for the most blatant, obvious cases of cross site scripting.If SpotBugs found any, you almost certainly have more cross site scriptingvulnerabilities that SpotBugs doesn't report. If you are concerned about cross site scripting, you should seriouslyconsider using a commercial static analysis or pen-testing tool.
```

**参考信息**

- 最新更新时间：2020-05-28 19:20:59

<a id="rule-395"></a>

### 395. duplication_file[JAVA]

| 字段 | 内容 |
|---|---|
| 问题级别 | 提示 |
| 语言 | JAVA |
| 标签 | cmetrics |
| 关联工具规则 | duplication_file |
| 规则类型 | 代码坏味道类 |
| 预估误报率 | 10% |

**审查要点**

重复文件：计算所有文件的md5值，如果md5值一样，则认为是重复文件。

**修复建议**

高重复率意味着相同或类似功能的单板、模块或功能单元缺乏抽象和管理。代码重构，提升代码的抽象和管理可以减少重复文件。

**正确示例**

度量指标，无正确示例

**错误示例**

度量指标，无错误示例

**参考信息**

- 链接：内部链接已省略
- 最新更新时间：2019-09-29 14:31:28

<a id="rule-396"></a>

### 396. huge_cyclomatic_complexity[JAVA]

| 字段 | 内容 |
|---|---|
| 问题级别 | 信息 |
| 语言 | JAVA |
| 标签 | cmetrics |
| 关联工具规则 | huge_cyclomatic_complexity |
| 规则类型 | 代码坏味道类 |
| 预估误报率 | 10% |

**审查要点**

```text
超大圈复杂度：圈复杂度超过阈值的函数，超大圈复杂度的阈值可以在CodeCheck上进行配置。圈复杂度的理论标准算法需要绘制控制流图，然后采用计算公式进行计算。对于复杂的程序，确保人人都能准确绘制其控制流图还是比较困难，不能直观快捷计算圈复杂度。因此，CMetrics采用了直观便捷的人人都可手工计算对比的简单方法，即计算控制条件的个数，其中包括了if、else if、else、for、while、case、?表达式、&&、||等的个数，在此基础上再 +1 的结果即为函数的圈复杂度个数。这种计算方法，也与公司早期使用的SourceMonitor工具计算方法一致。而业界其他的工具，可能不会计算else的个数，这是最主要的差异。相比较而言，计算else的个数更为合理，即有无else，其代码复杂度还是稍有差别的。
```

**修复建议**

降低函数复杂度，可以解决超大圈复杂度问题，牵引良好设计。

**正确示例**

度量指标，无正确示例

**错误示例**

度量指标，无错误示例

**参考信息**

- 链接：内部链接已省略
- 最新更新时间：2019-09-29 14:31:28

<a id="rule-397"></a>

### 397. huge_depth[JAVA]

| 字段 | 内容 |
|---|---|
| 问题级别 | 一般 |
| 语言 | JAVA |
| 标签 | cmetrics |
| 关联工具规则 | huge_depth |
| 规则类型 | 代码坏味道类 |
| 预估误报率 | 10% |

**审查要点**

```text
超大深度函数：函数深度默认为1，每出现以下关键字一次且关键字之间为嵌套关系，则该代码片段深度加1，若关键字之间不是嵌套关系，则深度不变。
c/c++,java,microcode,javascrip,typescript,c#,cangjie,arkts: if|else if|for|switch|do|while|try|catch|finally
go:else if|if|for|switch|select|func
lua:if|for|while|repeat|until|end
rust: if|match|for|while|loop|fn
python 按照缩进计算深度
注意！！！闭包也会计算深度，但是不会计算圈复杂度，即：其他大括号嵌套场景也会计算深度。
超大深度的阈值可以在CodeCheck上进行配置。
注意：并不是每一处超大深度函数的告警，都需要修复，度量是一个全局数据，仅是对代码的整体现状进行评估；
产品自己选择此规则，并进行扫描，意图在于：将可能需要重构的代码扫描出来进行确认，这也是业界的通用方式；这个扫描跟业务无关，不会考虑业务实际需要多少行可以完成功能；
并且度量：是以业务优先，并牵引更好的架构和设计；他产生告警，是为了指出可疑的超大深度函数，而不是要消灭一切超大深度函数，确认合理的超大深度函数，告警可以屏蔽。
关于规则和场景使用说明，请见：内部链接已省略
```

**修复建议**

降低函数复杂度，可以解决超大深度函数问题，牵引良好设计。

**正确示例**

```text
```
public void optimizedFunction(int a, int b, int c) {
if (a <= 0) {
try {
doSomethingDangerous();
} catch (Exception e) {
if (b > 0) {
doSomethingOnError();
} else {
throw new RuntimeException("Unexpected error occurred", e);
}
} finally {
doSomethingAlways();
}
return;
}
for (int i = 0; i < b; i++) {
if (c == 1) {
if (a > 10) {
doSomething();
} else {
doSomethingElse();
}
} else if (c == 2) {
if (a < 5) {
doAnotherThing();
} else {
doSomethingDifferent();
}
} else {
throw new IllegalArgumentException("Invalid value for c");
}
}
}
优化方法：
1. 将if和for语句的顺序调换，使得if语句在for循环之前执行，可以减少一层深度。
2. 将switch语句转化为if-else语句，可以消除switch语句的一层深度。
3. 将if语句中的条件取反，可以减少一层深度，同时将try-catch语句移到if语句之外，可以消除一层深度。
4. 将for循环中的变量声明移到函数开头，可以消除一层深度。
```
```

**错误示例**

```text
```
public void complexFunction(int a, int b, int c) {
if (a > 0) {
for (int i = 0; i < b; i++) {
switch (c) {
case 1:
if (a > 10) {
doSomething();
} else {
doSomethingElse();
}
break;
case 2:
if (a < 5) {
doAnotherThing();
} else {
doSomethingDifferent();
}
break;
default:
throw new IllegalArgumentException("Invalid value for c");
}
}
} else {
try {
doSomethingDangerous();
} catch (Exception e) {
if (b > 0) {
doSomethingOnError();
} else {
throw new RuntimeException("Unexpected error occurred", e);
}
} finally {
doSomethingAlways();
}
}
}
```
```

**参考信息**

- 链接：内部链接已省略
- 最新更新时间：2025-07-09 17:20:16

<a id="rule-398"></a>

### 398. huge_folder[JAVA]

| 字段 | 内容 |
|---|---|
| 问题级别 | 一般 |
| 语言 | JAVA |
| 标签 | cmetrics |
| 关联工具规则 | huge_folder |
| 规则类型 | 代码坏味道类 |
| 预估误报率 | 10% |

**审查要点**

```text
超大目录（超过50个被认为是超大目录）：本目录节点下子目录数和文件数之和超过阈值的目录（注意：不计算该目录节点下子目录下更深层次的子目录数和文件数）。
超大目录的阈值可以在CodeCheck上进行配置。
注意：空子目录（子目录里面没有源码文件的）不计算
```

**修复建议**

设置合理的目录结构，也可以提升易读性，可维护性。

**正确示例**

不涉及 ：度量指标，无正确示例

**错误示例**

不涉及 ：度量指标，无错误示例

**参考信息**

- 链接：内部链接已省略
- 最新更新时间：2019-09-29 14:31:28

<a id="rule-399"></a>

### 399. huge_method[JAVA]

| 字段 | 内容 |
|---|---|
| 问题级别 | 信息 |
| 语言 | JAVA |
| 标签 | cmetrics |
| 关联工具规则 | huge_method |
| 规则类型 | 代码坏味道类 |
| 预估误报率 | 10% |

**审查要点**

超大函数：代码行超过阈值的函数，超大函数的阈值可以在CodeCheck上进行配置。

**修复建议**

降低函数复杂度，可以解决超大函数问题，牵引良好设计。

**正确示例**

度量指标，无正确示例

**错误示例**

度量指标，无错误示例

**参考信息**

- 链接：内部链接已省略
- 最新更新时间：2019-09-29 14:31:00

<a id="rule-400"></a>

### 400. huge_non_headerfile[JAVA]

| 字段 | 内容 |
|---|---|
| 问题级别 | 提示 |
| 语言 | JAVA |
| 标签 | cmetrics |
| 关联工具规则 | huge_non_headerfile |
| 规则类型 | 代码坏味道类 |
| 预估误报率 | 10% |

**审查要点**

超大源文件：代码行超过阈值的源文件（不包含头文件），超大源文件的阈值可以在CodeCheck上进行配置。

**修复建议**

超大源文件意味着代码架构管理不清晰，可以通过代码重构等方式消减。

**正确示例**

度量指标，无正确示例

**错误示例**

度量指标，无错误示例

**参考信息**

- 链接：内部链接已省略
- 最新更新时间：2019-11-27 17:54:12

<a id="rule-401"></a>

### 401. redundant_code[JAVA]

| 字段 | 内容 |
|---|---|
| 问题级别 | 一般 |
| 语言 | JAVA |
| 标签 | cmetrics |
| 关联工具规则 | redundant_code |
| 规则类型 | 代码坏味道类 |
| 预估误报率 | 10% |

**审查要点**

```text
冗余代码：（注释中的代码被视作冗余代码）
当前的统计规则是注释中包含编程语言关键字的或符合语法规则的连续单词组会被判断为冗余代码。
Cmetrics通过正则表达式找出的#if 0代码和注释中的代码。
如果在注释中出现以下表达，则认为是冗余代码（......代表代码，一般是字符串）：
```java
return;
return ......;
};
}......;
=......;
......(......);
......[......];
if/else/else if/for/while/switch/case/catch/finally/try/default等表达式。
```
冗余代码极小概率会存在漏报的情况，某些情况下连续单词组可以认为是注释，也可以认为是代码，工具无法从语义上识别出是否为冗余代码，此情况本着允许漏报，不许误报的原则，不予预警。
```

**修复建议**

废弃的代码不要用注释符注释掉，而是直接删除掉。

**正确示例**

废弃的代码不要用注释符注释掉，而是直接删除掉。

**错误示例**

废弃的代码用注释符注释掉，这是不被允许的。

**参考信息**

- 链接：内部链接已省略
- 最新更新时间：2024-09-06 19:38:00

<a id="rule-402"></a>

### 402. 安全TOP问题-PBKDF2算法迭代次数不符合预期

| 字段 | 内容 |
|---|---|
| 问题级别 | 致命 |
| 语言 | JAVA |
| 标签 | CodeMars |
| 关联工具规则 | Weak_Cryptographic_Hash_Iteration_Count |
| 规则类型 | 安全规范规则类 |
| CWE 信息 | 916 |
| 预估误报率 | 20% |

**审查要点**

```text
实践中，一个口令可以编码为一个哈希值，且无法从哈希值逆向计算出原始的口令。口令是否相等可以通过 比较它们的哈希值是否相等来判断。如果一个口令的哈希值储存在一个数据库中，由于哈希算法的不可逆性，攻击 者就应该不可能还原出口令。如果说可以恢复口令，那么唯一的方式就是暴力破解攻击，比如计算所有可能口令的 哈希值，或是字典攻击，计算出所有常用的口令的哈希值。如果每个口令都只仅经过简单哈希，相同的口令将得到 相同的哈希值。仅保存口令哈希有以下两个缺陷：1. 由于“生日判定”，攻击者可以快速找到一个口令，尤其是当数据库中的口令数量较大的时候。2. 攻击者可以使用事先计算好的哈希列表在几秒钟之内破解口令。 为了解决这些问题，可以在进行哈希运算之前在口令中引入盐值。一个盐值是一个固定长度的随机数。这个盐值对 于每个存储入口来说必须是不同的。可以明文方式紧邻哈希后的口令一起保存。在这样的配置下，攻击者必须对每 一个口令分别进行暴力破解攻击。这样数据库便能抵御“生日”或者“彩虹表”攻击。
```

**修复建议**

```text
使用一个基于密码的密钥派生函数时，迭代计数应至少为 10,000,000。这将大幅增加穷尽式密码搜索的代价，而对派生各个密钥的代价不会产生显著影响。
迭代次数和安全性成正比例，也与计算时间成正比例，迭代次数越大，意味着计算密钥花费时间越长，同时抗暴力破解能力越强，对于性能不敏感或高安全性要求场景推荐迭代次数至少需要10,000,000次，其它场景迭代次数默认推荐至少10,000次，对于性能有特殊要求的产品最低可以迭代1000次。
```

**正确示例**

```text
正确示例：
使用一个基于密码的密钥派生函数时，迭代计数至少为 10,000,000
public static byte[] createHash(char[] password) throws NoSuchAlgorithmException, InvalidKeySpecException {
SecureRandom random = SecureRandom.getInstance(""SHA1PRNG"");
byte[] salt = new byte[8];
random.nextBytes(salt);
PBEKeySpec spec = new PBEKeySpec(password, salt, 20000000, 256);
//PBKDF2WithHmacSHA256 is supportted from JDK1.8
SecretKeyFactory skf = SecretKeyFactory.getInstance(""PBKDF2WithHmacSHA256"");
byte[] hashed = skf.generateSecret(spec).getEncoded();
return hashed;
}
```

**错误示例**

```text
错误示例：
使用一个基于密码的密钥派生函数时，迭代计数小于10,000,000。
public static byte[] createHash(char[] password) throws NoSuchAlgorithmException, InvalidKeySpecException {
SecureRandom random = SecureRandom.getInstance(""SHA1PRNG"");
byte[] salt = new byte[8];
random.nextBytes(salt);
/* POTENTIAL FLAW: 检测口令哈希时哈希迭代值是否大于10,000,000，如果否则报告警。 */
PBEKeySpec spec = new PBEKeySpec(password, salt, 5000, 256);
//PBKDF2WithHmacSHA256 is supportted from JDK1.8
SecretKeyFactory skf = SecretKeyFactory.getInstance(""PBKDF2WithHmacSHA256"");
byte[] hashed = skf.generateSecret(spec).getEncoded();
return hashed;
}
```

**参考信息**

- 链接：内部链接已省略
- 最新更新时间：2021-06-09 10:38:29

<a id="rule-403"></a>

### 403. 安全TOP问题-SSL/TLS 连接使用默认的预加载系统证书颁发机构 (CA) 创建

| 字段 | 内容 |
|---|---|
| 问题级别 | 一般 |
| 语言 | JAVA |
| 标签 | SecBrella |
| 关联工具规则 | SecJ_Insecure_SSL_Overly_Broad_Certificate_Trust |
| 规则类型 | 安全类（非编程规范） |
| CWE 信息 | 297296298299 |
| 预估误报率 | 20% |

**审查要点**

不安全的SSL校验

**修复建议**

getAcceptedIssuers方法中返回特定的预加载系统证书。

**正确示例**

```text
public class MyTrustManagerGood implements X509TrustManager {
@Override
public X509Certificate[] getAcceptedIssuers() {
return new X509Certificate[0];
}
@Override
public void checkClientTrusted(X509Certificate[] chain, String authType)
throws java.security.cert.CertificateException {
}
@Override
public void checkServerTrusted(X509Certificate[] chain, String authType)
throws java.security.cert.CertificateException {
}
}
```

**错误示例**

```text
public class MyTrustManagerBad implements X509TrustManager {
@Override
public X509Certificate[] getAcceptedIssuers() {
/* POTENTIAL FLAW: 继承javax.net.ssl.X509TrustManager类的getAcceptedIssuers方法的返回值为null。 */
return null;
}
@Override
public void checkClientTrusted(X509Certificate[] chain, String authType)
throws java.security.cert.CertificateException {
}
@Override
public void checkServerTrusted(X509Certificate[] chain, String authType)
throws java.security.cert.CertificateException {
}
}
```

**参考信息**

- 链接：内部链接已省略
- 最新更新时间：2020-12-03 18:20:58

<a id="rule-404"></a>

### 404. 安全TOP问题-不安全密码算法AES

| 字段 | 内容 |
|---|---|
| 问题级别 | 致命 |
| 语言 | JAVA |
| 标签 | CodeMars |
| 关联工具规则 | Weak_Encryption_AES |
| 规则类型 | 安全规范规则类 |
| CWE 信息 | 326 |
| 预估误报率 | 20% |

**审查要点**

程序使用了弱加密 AES算法，无法保证敏感数据的保密性

**修复建议**

最低限度下，确保 RSA 密钥长度不少于 2048 位。未来几年需要较强加密的应用程序的密码长度应至少为 4096 位。

**正确示例**

```text
public void initKeyGood() throws Exception {
KeyGenerator kg = KeyGenerator.getInstance("AES");
kg.init(128);
}
```

**错误示例**

```text
public void initKeyBad() throws Exception {
KeyGenerator kg = KeyGenerator.getInstance("AES");
/* POTENTIAL FLAW: 程序使用了弱加密 AES算法，无法保证敏感数据的保密性。 */
kg.init(126);
}
```

**参考信息**

- 链接：内部链接已省略
- 最新更新时间：2021-06-03 17:06:19

<a id="rule-405"></a>

### 405. 安全TOP问题-不安全密码算法ARCFOUR

| 字段 | 内容 |
|---|---|
| 问题级别 | 致命 |
| 语言 | JAVA |
| 标签 | CodeMars |
| 关联工具规则 | Weak_Encryption_ARCFOUR |
| 规则类型 | 安全规范规则类 |
| CWE 信息 | 327 |
| 预估误报率 | 20% |

**审查要点**

程序使用了弱加密 ARCFOUR算法，无法保证敏感数据的保密性

**修复建议**

换用安全的加密算法，比如AES算法

**正确示例**

```text
public void doSomething() throws Exception {
Cipher.getInstance("AES/GCM/PKCS5Padding");
}
```

**错误示例**

```text
public void doSomething() throws Exception {
Cipher.getInstance("ARCFOUR");
}
```

**参考信息**

- 链接：内部链接已省略
- 最新更新时间：2021-06-09 10:38:30

<a id="rule-406"></a>

### 406. 安全TOP问题-不安全密码算法Blowfish

| 字段 | 内容 |
|---|---|
| 问题级别 | 致命 |
| 语言 | JAVA |
| 标签 | CodeMars |
| 关联工具规则 | Weak_Encryption_Blowfish |
| 规则类型 | 安全规范规则类 |
| CWE 信息 | 327 |
| 预估误报率 | 20% |

**审查要点**

程序使用了弱加密 Blowfish算法，无法保证敏感数据的保密性

**修复建议**

换用安全的加密算法，比如AES算法

**正确示例**

```text
public void doSomething() throws Exception {
Cipher.getInstance("AES/GCM/PKCS5Padding");
}
```

**错误示例**

```text
public void doSomething() throws Exception {
Cipher.getInstance("Blowfish");
}
```

**参考信息**

- 链接：内部链接已省略
- 最新更新时间：2021-06-09 10:38:30

<a id="rule-407"></a>

### 407. 安全TOP问题-不安全密码算法DES

| 字段 | 内容 |
|---|---|
| 问题级别 | 致命 |
| 语言 | JAVA |
| 标签 | CodeMars |
| 关联工具规则 | Weak_Encryption_DES |
| 规则类型 | 安全规范规则类 |
| CWE 信息 | 327 |
| 预估误报率 | 20% |

**审查要点**

程序使用了弱加密 DES算法，无法保证敏感数据的保密性

**修复建议**

换用安全的加密算法，比如AES算法

**正确示例**

```text
public void doSomething() throws Exception {
Cipher.getInstance("AES/GCM/PKCS5Padding");
}
```

**错误示例**

```text
public void doSomething() throws Exception {
Cipher.getInstance("DES");
}
```

**参考信息**

- 链接：内部链接已省略
- 最新更新时间：2021-06-09 10:38:30

<a id="rule-408"></a>

### 408. 安全TOP问题-不安全密码算法DH

| 字段 | 内容 |
|---|---|
| 问题级别 | 致命 |
| 语言 | JAVA |
| 标签 | CodeMars |
| 关联工具规则 | Weak_Encryption_DH |
| 规则类型 | 安全规范规则类 |
| CWE 信息 | 326 |
| 预估误报率 | 20% |

**审查要点**

程序使用了弱加密 DH算法，无法保证敏感数据的保密性

**修复建议**

最低限度下，确保 RSA 密钥长度不少于 2048 位。未来几年需要较强加密的应用程序的密码长度应至少为 4096 位。

**正确示例**

```text
public void testGood() throws Exception {
KeyPairGenerator keyPairGen = KeyPairGenerator.getInstance("RSA");
/* POTENTIAL FLAW: 程序使用了弱加密 RSA算法，无法保证敏感数据的保密性。 */
keyPairGen.initialize(2048);
}
```

**错误示例**

```text
public void testBad() throws Exception {
KeyPairGenerator keyPairGen = KeyPairGenerator.getInstance("RSA");
/* POTENTIAL FLAW: 程序使用了弱加密 RSA算法，无法保证敏感数据的保密性。 */
keyPairGen.initialize(1024);
}
```

**参考信息**

- 链接：内部链接已省略
- 最新更新时间：2021-06-03 17:06:21

<a id="rule-409"></a>

### 409. 安全TOP问题-不安全密码算法DiffieHellman

| 字段 | 内容 |
|---|---|
| 问题级别 | 致命 |
| 语言 | JAVA |
| 标签 | CodeMars |
| 关联工具规则 | Weak_Encryption_DiffieHellman |
| 规则类型 | 安全规范规则类 |
| CWE 信息 | 326 |
| 预估误报率 | 20% |

**审查要点**

程序使用了弱加密 DiffieHellman算法，无法保证敏感数据的保密性

**修复建议**

最低限度下，确保 RSA 密钥长度不少于 2048 位。未来几年需要较强加密的应用程序的密码长度应至少为 4096 位。

**正确示例**

```text
public void testGood() throws Exception {
KeyPairGenerator keyPairGen = KeyPairGenerator.getInstance("RSA");
/* POTENTIAL FLAW: 程序使用了弱加密 RSA算法，无法保证敏感数据的保密性。 */
keyPairGen.initialize(2048);
}
```

**错误示例**

```text
public void testBad() throws Exception {
KeyPairGenerator keyPairGen = KeyPairGenerator.getInstance("RSA");
/* POTENTIAL FLAW: 程序使用了弱加密 RSA算法，无法保证敏感数据的保密性。 */
keyPairGen.initialize(1024);
}
```

**参考信息**

- 链接：内部链接已省略
- 最新更新时间：2021-06-03 17:06:22

<a id="rule-410"></a>

### 410. 安全TOP问题-不安全密码算法ECDSA

| 字段 | 内容 |
|---|---|
| 问题级别 | 致命 |
| 语言 | JAVA |
| 标签 | CodeMars |
| 关联工具规则 | Weak_Hash_ECDSA |
| 规则类型 | 安全规范规则类 |
| CWE 信息 | 327 |
| 预估误报率 | 20% |

**审查要点**

在生成加密签名过程中，不要使用ECDSA参数

**修复建议**

停止使用 MD2、MD4、MD5、RIPEMD-160 和 SHA-1 对安全性关键的上下文中的数据进行验证。使用强加密算法。

**正确示例**

```text
public void testGood() throws Exception {
MessageDigest md2 = MessageDigest.getInstance("SHA-256");
}
```

**错误示例**

```text
public void testBad() throws Exception {
/* POTENTIAL FLAW: 弱加密散列值无法保证数据完整性，且不能在安全性关键的上下文中使用(MD2)。 */
MessageDigest md2 = MessageDigest.getInstance("MD2");
/* POTENTIAL FLAW: 弱加密散列值无法保证数据完整性，且不能在安全性关键的上下文中使用(SHA0)。 */
MessageDigest sha0 = MessageDigest.getInstance("SHA0");
/* POTENTIAL FLAW: 在生成加密签名过程中，不要使用DSA参数。 */
Signature.getInstance("DSA");
/* POTENTIAL FLAW: 在生成加密签名过程中，不要使用SHA1WithDSA参数。 */
Signature.getInstance("SHA1WithDSA");
}
```

**参考信息**

- 链接：内部链接已省略
- 最新更新时间：2025-05-27 09:58:45

<a id="rule-411"></a>

### 411. 安全TOP问题-不安全密码算法MD2

| 字段 | 内容 |
|---|---|
| 问题级别 | 致命 |
| 语言 | JAVA |
| 标签 | CodeMars |
| 关联工具规则 | Weak_Hash_MD2 |
| 规则类型 | 安全规范规则类 |
| CWE 信息 | 327 |
| 预估误报率 | 20% |

**审查要点**

弱加密散列值无法保证数据完整性，且不能在安全性关键的上下文中使用(MD2)

**修复建议**

停止使用 MD2、MD4、MD5、RIPEMD-160 和 SHA-1 对安全性关键的上下文中的数据进行验证。使用强加密算法。

**正确示例**

```text
public void testGood() throws Exception {
MessageDigest md2 = MessageDigest.getInstance("SHA-256");
}
```

**错误示例**

```text
public void testBad() throws Exception {
/* POTENTIAL FLAW: 弱加密散列值无法保证数据完整性，且不能在安全性关键的上下文中使用(MD2)。 */
MessageDigest md2 = MessageDigest.getInstance("MD2");
/* POTENTIAL FLAW: 弱加密散列值无法保证数据完整性，且不能在安全性关键的上下文中使用(SHA0)。 */
MessageDigest sha0 = MessageDigest.getInstance("SHA0");
/* POTENTIAL FLAW: 在生成加密签名过程中，不要使用DSA参数。 */
Signature.getInstance("DSA");
/* POTENTIAL FLAW: 在生成加密签名过程中，不要使用SHA1WithDSA参数。 */
Signature.getInstance("SHA1WithDSA");
}
```

**参考信息**

- 链接：内部链接已省略
- 最新更新时间：2021-06-03 17:06:28

<a id="rule-412"></a>

### 412. 安全TOP问题-不安全密码算法MD4

| 字段 | 内容 |
|---|---|
| 问题级别 | 致命 |
| 语言 | JAVA |
| 标签 | CodeMars |
| 关联工具规则 | Weak_Hash_MD4 |
| 规则类型 | 安全规范规则类 |
| CWE 信息 | 327 |
| 预估误报率 | 20% |

**审查要点**

弱加密散列值无法保证数据完整性，且不能在安全性关键的上下文中使用(MD4)

**修复建议**

停止使用 MD2、MD4、MD5、RIPEMD-160 和 SHA-1 对安全性关键的上下文中的数据进行验证。使用强加密算法。

**正确示例**

```text
public void testGood() throws Exception {
MessageDigest md2 = MessageDigest.getInstance("SHA-256");
}
```

**错误示例**

```text
public void testBad() throws Exception {
/* POTENTIAL FLAW: 弱加密散列值无法保证数据完整性，且不能在安全性关键的上下文中使用(MD2)。 */
MessageDigest md2 = MessageDigest.getInstance("MD2");
/* POTENTIAL FLAW: 弱加密散列值无法保证数据完整性，且不能在安全性关键的上下文中使用(SHA0)。 */
MessageDigest sha0 = MessageDigest.getInstance("SHA0");
/* POTENTIAL FLAW: 在生成加密签名过程中，不要使用DSA参数。 */
Signature.getInstance("DSA");
/* POTENTIAL FLAW: 在生成加密签名过程中，不要使用SHA1WithDSA参数。 */
Signature.getInstance("SHA1WithDSA");
}
```

**参考信息**

- 链接：内部链接已省略
- 最新更新时间：2021-06-03 17:06:29

<a id="rule-413"></a>

### 413. 安全TOP问题-不安全密码算法MD5

| 字段 | 内容 |
|---|---|
| 问题级别 | 致命 |
| 语言 | JAVA |
| 标签 | CodeMars |
| 关联工具规则 | Weak_Hash_MD5 |
| 规则类型 | 安全规范规则类 |
| CWE 信息 | 327 |
| 预估误报率 | 20% |

**审查要点**

弱加密散列值无法保证数据完整性，且不能在安全性关键的上下文中使用(MD5)

**修复建议**

停止使用 MD2、MD4、MD5、RIPEMD-160 和 SHA-1 对安全性关键的上下文中的数据进行验证。使用强加密算法。

**正确示例**

```text
public void testGood() throws Exception {
MessageDigest md2 = MessageDigest.getInstance("SHA-256");
}
```

**错误示例**

```text
public void testBad() throws Exception {
/* POTENTIAL FLAW: 弱加密散列值无法保证数据完整性，且不能在安全性关键的上下文中使用(MD2)。 */
MessageDigest md2 = MessageDigest.getInstance("MD2");
/* POTENTIAL FLAW: 弱加密散列值无法保证数据完整性，且不能在安全性关键的上下文中使用(SHA0)。 */
MessageDigest sha0 = MessageDigest.getInstance("SHA0");
/* POTENTIAL FLAW: 在生成加密签名过程中，不要使用DSA参数。 */
Signature.getInstance("DSA");
/* POTENTIAL FLAW: 在生成加密签名过程中，不要使用SHA1WithDSA参数。 */
Signature.getInstance("SHA1WithDSA");
}
```

**参考信息**

- 链接：内部链接已省略
- 最新更新时间：2021-06-03 17:06:29

<a id="rule-414"></a>

### 414. 安全TOP问题-不安全密码算法MD5WithRSA

| 字段 | 内容 |
|---|---|
| 问题级别 | 致命 |
| 语言 | JAVA |
| 标签 | CodeMars |
| 关联工具规则 | Weak_Hash_MD5WithRSA |
| 规则类型 | 安全规范规则类 |
| CWE 信息 | 327 |
| 预估误报率 | 20% |

**审查要点**

在生成加密签名过程中，不要使用MD5WithRSA参数

**修复建议**

停止使用 MD2、MD4、MD5、RIPEMD-160 和 SHA-1 对安全性关键的上下文中的数据进行验证。使用强加密算法。

**正确示例**

```text
public void testGood() throws Exception {
MessageDigest md2 = MessageDigest.getInstance("SHA-256");
}
```

**错误示例**

```text
public void testBad() throws Exception {
/* POTENTIAL FLAW: 弱加密散列值无法保证数据完整性，且不能在安全性关键的上下文中使用(MD2)。 */
MessageDigest md2 = MessageDigest.getInstance("MD2");
/* POTENTIAL FLAW: 弱加密散列值无法保证数据完整性，且不能在安全性关键的上下文中使用(SHA0)。 */
MessageDigest sha0 = MessageDigest.getInstance("SHA0");
/* POTENTIAL FLAW: 在生成加密签名过程中，不要使用DSA参数。 */
Signature.getInstance("DSA");
/* POTENTIAL FLAW: 在生成加密签名过程中，不要使用SHA1WithDSA参数。 */
Signature.getInstance("SHA1WithDSA");
}
```

**参考信息**

- 链接：内部链接已省略
- 最新更新时间：2021-06-03 17:06:30

<a id="rule-415"></a>

### 415. 安全TOP问题-不安全密码算法PBKDF2WithHmacMD5

| 字段 | 内容 |
|---|---|
| 问题级别 | 致命 |
| 语言 | JAVA |
| 标签 | CodeMars |
| 关联工具规则 | Weak_Encryption_PBKDF2WithHmacMD5 |
| 规则类型 | 安全规范规则类 |
| CWE 信息 | 327 |
| 预估误报率 | 20% |

**审查要点**

使用密钥导出算法PBKDF2时禁止使用MD5哈希函数

**修复建议**

换用安全的加密算法，比如AES算法

**正确示例**

```text
public void doSomething() throws Exception {
Cipher.getInstance("AES/GCM/PKCS5Padding");
}
```

**错误示例**

```text
public void doSomething() throws Exception {
SecretKeyFactory.getInstance("PBKDF2WithHmacMD5");
}
```

**参考信息**

- 链接：内部链接已省略
- 最新更新时间：2021-06-09 10:38:30

<a id="rule-416"></a>

### 416. 安全TOP问题-不安全密码算法RC2

| 字段 | 内容 |
|---|---|
| 问题级别 | 致命 |
| 语言 | JAVA |
| 标签 | CodeMars |
| 关联工具规则 | Weak_Encryption_RC2 |
| 规则类型 | 安全规范规则类 |
| CWE 信息 | 327 |
| 预估误报率 | 20% |

**审查要点**

程序使用了弱加密 RC2算法，无法保证敏感数据的保密性

**修复建议**

换用安全的加密算法，比如AES算法

**正确示例**

```text
public void doSomething() throws Exception {
Cipher.getInstance("AES/GCM/PKCS5Padding");
}
```

**错误示例**

```text
public void doSomething() throws Exception {
Cipher.getInstance("RC2");
}
```

**参考信息**

- 链接：内部链接已省略
- 最新更新时间：2021-06-09 10:38:30

<a id="rule-417"></a>

### 417. 安全TOP问题-不安全密码算法RC4

| 字段 | 内容 |
|---|---|
| 问题级别 | 致命 |
| 语言 | JAVA |
| 标签 | CodeMars |
| 关联工具规则 | Weak_Encryption_RC4 |
| 规则类型 | 安全规范规则类 |
| CWE 信息 | 327 |
| 预估误报率 | 20% |

**审查要点**

程序使用了弱加密 RC4算法，无法保证敏感数据的保密性

**修复建议**

换用安全的加密算法，比如AES算法

**正确示例**

```text
public void doSomething() throws Exception {
Cipher.getInstance("AES/GCM/PKCS5Padding");
}
```

**错误示例**

```text
public void doSomething() throws Exception {
Cipher.getInstance("RC4");
}
```

**参考信息**

- 链接：内部链接已省略
- 最新更新时间：2021-06-09 10:38:30

<a id="rule-418"></a>

### 418. 安全TOP问题-不安全密码算法RSA

| 字段 | 内容 |
|---|---|
| 问题级别 | 致命 |
| 语言 | JAVA |
| 标签 | CodeMars |
| 关联工具规则 | Weak_Encryption_RSA |
| 规则类型 | 安全规范规则类 |
| CWE 信息 | 326 |
| 预估误报率 | 20% |

**审查要点**

程序使用了弱加密 RSA算法，无法保证敏感数据的保密性

**修复建议**

最低限度下，确保 RSA 密钥长度不少于 2048 位。未来几年需要较强加密的应用程序的密码长度应至少为 4096 位。

**正确示例**

```text
public void testGood() throws Exception {
KeyPairGenerator keyPairGen = KeyPairGenerator.getInstance("RSA");
/* POTENTIAL FLAW: 程序使用了弱加密 RSA算法，无法保证敏感数据的保密性。 */
keyPairGen.initialize(2048);
}
```

**错误示例**

```text
public void testBad() throws Exception {
KeyPairGenerator keyPairGen = KeyPairGenerator.getInstance("RSA");
/* POTENTIAL FLAW: 程序使用了弱加密 RSA算法，无法保证敏感数据的保密性。 */
keyPairGen.initialize(1024);
}
```

**参考信息**

- 链接：内部链接已省略
- 最新更新时间：2021-06-03 17:06:26

<a id="rule-419"></a>

### 419. 安全TOP问题-不安全密码算法SHA0

| 字段 | 内容 |
|---|---|
| 问题级别 | 致命 |
| 语言 | JAVA |
| 标签 | CodeMars |
| 关联工具规则 | Weak_Hash_SHA0 |
| 规则类型 | 安全规范规则类 |
| CWE 信息 | 327 |
| 预估误报率 | 20% |

**审查要点**

弱加密散列值无法保证数据完整性，且不能在安全性关键的上下文中使用(SHA0)

**修复建议**

停止使用 MD2、MD4、MD5、RIPEMD-160 和 SHA-1 对安全性关键的上下文中的数据进行验证。使用强加密算法。

**正确示例**

```text
public void testGood() throws Exception {
MessageDigest md2 = MessageDigest.getInstance("SHA-256");
}
```

**错误示例**

```text
public void testBad() throws Exception {
/* POTENTIAL FLAW: 弱加密散列值无法保证数据完整性，且不能在安全性关键的上下文中使用(MD2)。 */
MessageDigest md2 = MessageDigest.getInstance("MD2");
/* POTENTIAL FLAW: 弱加密散列值无法保证数据完整性，且不能在安全性关键的上下文中使用(SHA0)。 */
MessageDigest sha0 = MessageDigest.getInstance("SHA0");
/* POTENTIAL FLAW: 在生成加密签名过程中，不要使用DSA参数。 */
Signature.getInstance("DSA");
/* POTENTIAL FLAW: 在生成加密签名过程中，不要使用SHA1WithDSA参数。 */
Signature.getInstance("SHA1WithDSA");
}
```

**参考信息**

- 链接：内部链接已省略
- 最新更新时间：2021-06-03 17:06:30

<a id="rule-420"></a>

### 420. 安全TOP问题-不安全密码算法SHA1

| 字段 | 内容 |
|---|---|
| 问题级别 | 致命 |
| 语言 | JAVA |
| 标签 | CodeMars |
| 关联工具规则 | Weak_Hash_SHA1 |
| 规则类型 | 安全规范规则类 |
| CWE 信息 | 327 |
| 预估误报率 | 20% |

**审查要点**

弱加密散列值无法保证数据完整性，且不能在安全性关键的上下文中使用(SHA1)

**修复建议**

停止使用 MD2、MD4、MD5、RIPEMD-160 和 SHA-1 对安全性关键的上下文中的数据进行验证。使用强加密算法。

**正确示例**

```text
public void testGood() throws Exception {
MessageDigest md2 = MessageDigest.getInstance("SHA-256");
}
```

**错误示例**

```text
public void testBad() throws Exception {
/* POTENTIAL FLAW: 弱加密散列值无法保证数据完整性，且不能在安全性关键的上下文中使用(MD2)。 */
MessageDigest md2 = MessageDigest.getInstance("MD2");
/* POTENTIAL FLAW: 弱加密散列值无法保证数据完整性，且不能在安全性关键的上下文中使用(SHA0)。 */
MessageDigest sha0 = MessageDigest.getInstance("SHA0");
/* POTENTIAL FLAW: 在生成加密签名过程中，不要使用DSA参数。 */
Signature.getInstance("DSA");
/* POTENTIAL FLAW: 在生成加密签名过程中，不要使用SHA1WithDSA参数。 */
Signature.getInstance("SHA1WithDSA");
}
```

**参考信息**

- 链接：内部链接已省略
- 最新更新时间：2021-06-03 17:06:31

<a id="rule-421"></a>

### 421. 安全TOP问题-不安全密码算法SHA1WithDSA

| 字段 | 内容 |
|---|---|
| 问题级别 | 致命 |
| 语言 | JAVA |
| 标签 | CodeMars |
| 关联工具规则 | Weak_Hash_SHA1WithDSA |
| 规则类型 | 安全规范规则类 |
| CWE 信息 | 327 |
| 预估误报率 | 20% |

**审查要点**

在生成加密签名过程中，不要使用SHA1WithDSA参数

**修复建议**

停止使用 MD2、MD4、MD5、RIPEMD-160 和 SHA-1 对安全性关键的上下文中的数据进行验证。使用强加密算法。

**正确示例**

```text
public void testGood() throws Exception {
MessageDigest md2 = MessageDigest.getInstance("SHA-256");
}
```

**错误示例**

```text
public void testBad() throws Exception {
/* POTENTIAL FLAW: 弱加密散列值无法保证数据完整性，且不能在安全性关键的上下文中使用(MD2)。 */
MessageDigest md2 = MessageDigest.getInstance("MD2");
/* POTENTIAL FLAW: 弱加密散列值无法保证数据完整性，且不能在安全性关键的上下文中使用(SHA0)。 */
MessageDigest sha0 = MessageDigest.getInstance("SHA0");
/* POTENTIAL FLAW: 在生成加密签名过程中，不要使用DSA参数。 */
Signature.getInstance("DSA");
/* POTENTIAL FLAW: 在生成加密签名过程中，不要使用SHA1WithDSA参数。 */
Signature.getInstance("SHA1WithDSA");
}
```

**参考信息**

- 链接：内部链接已省略
- 最新更新时间：2021-06-03 17:06:32

<a id="rule-422"></a>

### 422. 安全TOP问题-不安全密码算法SHA1WithECDSA

| 字段 | 内容 |
|---|---|
| 问题级别 | 致命 |
| 语言 | JAVA |
| 标签 | CodeMars |
| 关联工具规则 | Weak_Hash_SHA1WithECDSA |
| 规则类型 | 安全规范规则类 |
| CWE 信息 | 327 |
| 预估误报率 | 20% |

**审查要点**

在生成加密签名过程中，不要使用SHA1WithECDSA参数

**修复建议**

停止使用 MD2、MD4、MD5、RIPEMD-160 和 SHA-1 对安全性关键的上下文中的数据进行验证。使用强加密算法。

**正确示例**

```text
public void testGood() throws Exception {
MessageDigest md2 = MessageDigest.getInstance("SHA-256");
}
```

**错误示例**

```text
public void testBad() throws Exception {
/* POTENTIAL FLAW: 弱加密散列值无法保证数据完整性，且不能在安全性关键的上下文中使用(MD2)。 */
MessageDigest md2 = MessageDigest.getInstance("MD2");
/* POTENTIAL FLAW: 弱加密散列值无法保证数据完整性，且不能在安全性关键的上下文中使用(SHA0)。 */
MessageDigest sha0 = MessageDigest.getInstance("SHA0");
/* POTENTIAL FLAW: 在生成加密签名过程中，不要使用DSA参数。 */
Signature.getInstance("DSA");
/* POTENTIAL FLAW: 在生成加密签名过程中，不要使用SHA1WithDSA参数。 */
Signature.getInstance("SHA1WithDSA");
}
```

**参考信息**

- 链接：内部链接已省略
- 最新更新时间：2021-06-03 17:06:32

<a id="rule-423"></a>

### 423. 安全TOP问题-不安全密码算法SHA1WithRSA

| 字段 | 内容 |
|---|---|
| 问题级别 | 致命 |
| 语言 | JAVA |
| 标签 | CodeMars |
| 关联工具规则 | Weak_Hash_SHA1WithRSA |
| 规则类型 | 安全规范规则类 |
| CWE 信息 | 327 |
| 预估误报率 | 20% |

**审查要点**

在生成加密签名过程中，不要使用SHA1WithRSA参数

**修复建议**

停止使用 MD2、MD4、MD5、RIPEMD-160 和 SHA-1 对安全性关键的上下文中的数据进行验证。使用强加密算法。

**正确示例**

```text
public void testGood() throws Exception {
MessageDigest md2 = MessageDigest.getInstance("SHA-256");
}
```

**错误示例**

```text
public void testBad() throws Exception {
/* POTENTIAL FLAW: 弱加密散列值无法保证数据完整性，且不能在安全性关键的上下文中使用(MD2)。 */
MessageDigest md2 = MessageDigest.getInstance("MD2");
/* POTENTIAL FLAW: 弱加密散列值无法保证数据完整性，且不能在安全性关键的上下文中使用(SHA0)。 */
MessageDigest sha0 = MessageDigest.getInstance("SHA0");
/* POTENTIAL FLAW: 在生成加密签名过程中，不要使用DSA参数。 */
Signature.getInstance("DSA");
/* POTENTIAL FLAW: 在生成加密签名过程中，不要使用SHA1WithDSA参数。 */
Signature.getInstance("SHA1WithDSA");
}
```

**参考信息**

- 链接：内部链接已省略
- 最新更新时间：2021-06-03 17:06:33

<a id="rule-424"></a>

### 424. 安全TOP问题-不安全的hash算法（DSA）

| 字段 | 内容 |
|---|---|
| 问题级别 | 致命 |
| 语言 | JAVA |
| 标签 | CodeMars |
| 关联工具规则 | Weak_Hash_DSA |
| 规则类型 | 安全规范规则类 |
| CWE 信息 | 327 |
| 预估误报率 | 20% |

**审查要点**

在生成加密签名过程中，不要使用DSA参数

**修复建议**

停止使用 MD2、MD4、MD5、RIPEMD-160 和 SHA-1 对安全性关键的上下文中的数据进行验证。使用强加密算法。

**正确示例**

```text
public void testGood() throws Exception {
MessageDigest md2 = MessageDigest.getInstance("SHA-256");
}
```

**错误示例**

```text
public void testBad() throws Exception {
/* POTENTIAL FLAW: 弱加密散列值无法保证数据完整性，且不能在安全性关键的上下文中使用(MD2)。 */
MessageDigest md2 = MessageDigest.getInstance("MD2");
/* POTENTIAL FLAW: 弱加密散列值无法保证数据完整性，且不能在安全性关键的上下文中使用(SHA0)。 */
MessageDigest sha0 = MessageDigest.getInstance("SHA0");
/* POTENTIAL FLAW: 在生成加密签名过程中，不要使用DSA参数。 */
Signature.getInstance("DSA");
/* POTENTIAL FLAW: 在生成加密签名过程中，不要使用SHA1WithDSA参数。 */
Signature.getInstance("SHA1WithDSA");
}
```

**参考信息**

- 链接：内部链接已省略
- 最新更新时间：2021-06-03 17:06:27

<a id="rule-425"></a>

### 425. 安全TOP问题-不安全的弱加密算法（DSA）

| 字段 | 内容 |
|---|---|
| 问题级别 | 致命 |
| 语言 | JAVA |
| 标签 | CodeMars |
| 关联工具规则 | Weak_Encryption_DSA |
| 规则类型 | 安全规范规则类 |
| CWE 信息 | 326 |
| 预估误报率 | 20% |

**审查要点**

程序使用了弱加密 DSA算法，无法保证敏感数据的保密性

**修复建议**

最低限度下，确保 RSA 密钥长度不少于 2048 位。未来几年需要较强加密的应用程序的密码长度应至少为 4096 位。

**正确示例**

```text
public void testGood() throws Exception {
KeyPairGenerator keyPairGen = KeyPairGenerator.getInstance("RSA");
/* POTENTIAL FLAW: 程序使用了弱加密 RSA算法，无法保证敏感数据的保密性。 */
keyPairGen.initialize(2048);
}
```

**错误示例**

```text
public void testBad() throws Exception {
KeyPairGenerator keyPairGen = KeyPairGenerator.getInstance("RSA");
/* POTENTIAL FLAW: 程序使用了弱加密 RSA算法，无法保证敏感数据的保密性。 */
keyPairGen.initialize(1024);
}
```

**参考信息**

- 链接：内部链接已省略
- 最新更新时间：2021-06-03 17:06:22

<a id="rule-426"></a>

### 426. 安全TOP问题-使用分组加密算法时，填充方式建议选择CMS-Padding 或ISO-Padding

| 字段 | 内容 |
|---|---|
| 问题级别 | 致命 |
| 语言 | JAVA |
| 标签 | CodeMars |
| 关联工具规则 | Weak_Encryption_Inadequate_RSA_Padding |
| 规则类型 | 安全规范规则类 |
| CWE 信息 | 780 |
| 预估误报率 | 20% |

**审查要点**

RSA 算法在没有 OAEP 填充模式下使用，因此加密机制比较脆弱

**修复建议**

为安全使用 RSA，在执行加密时必须使用 OAEP（最优非对称加密填充模式）。

**正确示例**

```text
public void testGood() throws Exception {
Cipher.getInstance("RSA/ECB/OAEPPadding");
}
```

**错误示例**

```text
public void testBad() throws Exception {
/* POTENTIAL FLAW: RSA 算法在没有 OAEP 填充模式下使用，因此加密机制比较脆弱。 */
Cipher.getInstance("RSA");
/* POTENTIAL FLAW: RSA 算法在没有 OAEP 填充模式下使用，因此加密机制比较脆弱。 */
Cipher.getInstance("RSA/ECB/NoPadding");
}
```

**参考信息**

- 链接：内部链接已省略
- 最新更新时间：2021-06-03 17:06:23

<a id="rule-427"></a>

### 427. 安全TOP问题-使用分组密码算法时，应优先选择GCM模式

| 字段 | 内容 |
|---|---|
| 问题级别 | 致命 |
| 语言 | JAVA |
| 标签 | CodeMars |
| 关联工具规则 | Weak_Encryption_Insecure_Mode_AES |
| 规则类型 | 安全规范规则类 |
| CWE 信息 | 327 |
| 预估误报率 | 20% |

**审查要点**

```text
ECB模式对于同样的明文块会生成相同的密文块，不能提供严格的数据保密性，不能抵抗替换攻击，攻击者可以调换加密块的顺序而不被发现。因此，应禁止使用ECB模式。
CBC模式有可能有问题，应禁止标准协议中或自定义协议使用CBC模式
```

**修复建议**

加密大于块的数据时，避免使用 ECB 操作模式。

**正确示例**

```text
public void testGood() throws Exception {
Cipher.getInstance("AES/GCM/PKCS5Padding");
}
```

**错误示例**

```text
"public void testBad() throws Exception {
/* POTENTIAL FLAW: ECB模式对于同样的明文块会生成相同的密文块，不能提供严格的数据保密性，不能抵抗替换攻击，攻击者可以调换加密块的顺序而不被发现。因此，应禁止使用ECB模式。 */
Cipher.getInstance(""AES"");
/* POTENTIAL FLAW: ECB模式对于同样的明文块会生成相同的密文块，不能提供严格的数据保密性，不能抵抗替换攻击，攻击者可以调换加密块的顺序而不被发现。因此，应禁止使用ECB模式。 */
Cipher.getInstance(""AES/ECB/NoPadding"");
/* POTENTIAL FLAW: CBC模式可能有风险，应禁用CBC模式 */
Cipher.getInstance(""AES/CBC/ECB/PKCS5Padding"");
}"
```

**参考信息**

- 链接：内部链接已省略
- 最新更新时间：2021-06-03 17:06:24

<a id="rule-428"></a>

### 428. 安全TOP问题-忽略SSL证书验证错误漏洞

| 字段 | 内容 |
|---|---|
| 问题级别 | 一般 |
| 语言 | JAVA |
| 标签 | SecBrella |
| 关联工具规则 | SecJ_HTTPs_Hostname_Verifier |
| 规则类型 | 安全类（非编程规范） |
| CWE 信息 | 297296298299 |
| 预估误报率 | 20% |

**审查要点**

```text
浏览器能够验证证书的合法性，因此使用权威证书颁发机构颁发的SSL服务器端证书，且证书未过期、没有被撤销、和域名匹配，能够帮助用户确认当前所访问的网站是否合法。备注：由于开发阶段往往无法确认部署时的域名和有效期，所以只能使用开发人员临时生成SSL证书，需要在用户资料中要求客户从权威证书颁发机构购买正式的SSL证书，替换临时SSL证书，并提供相应的实施指导。
```

**修复建议**

用户可以根据自己业务需要，决定是否需要校验证书，如果不需要校验建议由安全SE或其他资质的角色审核屏蔽。

**正确示例**

```text
```java
// 场景一
private class MyHostnameVerifierGood implements HostnameVerifier {
public boolean verify(String hostname, SSLSession session) {
// 【GOOD】 verify方法根据实际使用进行了逻辑验证
if ("example".equals(hostname)) {
return false;
}
return true;
}
}
// 场景二
public static class MyX509TrustManager implements X509TrustManager {
private static final String TRUSTED_CERT_ISSUER = "CN=HWIT Enterprise CA 1,CN=kube-ca";
// 【GOOD】 校验了MyX509TrustManager的证书
@Override
public void checkServerTrusted(X509Certificate[] x509Certificates, String str) throws CertificateException {
for (X509Certificate certificate : x509Certificates) {
if (!TRUSTED_CERT_ISSUER.contains(certificate.getIssuerDN().getName())) {
throw new CertificateException("untrusted cert issuer!");
}
}
}
}
```
```

**错误示例**

```text
```java
// 场景一
private class MyHostnameVerifierBad implements HostnameVerifier {
/* 【POTENTIAL FLAW】: HostnameVerifier继承类的verify方法未做逻辑验证,直接返回了成功。 */
public boolean verify(String hostname, SSLSession session) {
/* 不能直接返回true，需要校验hostname */
return true;
}
}
// 场景二
public static class DevopsTrustManager implements X509TrustManager, TrustManager
{
/* 【POTENTIAL FLAW】: X509TrustManager继承类的checkServerTrusted方法未做逻辑验证*/
@Override public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {
/* 需要增加校验逻辑 */
}
}
```
```

**参考信息**

- 链接：内部链接已省略
- 最新更新时间：2024-08-26 20:20:23

<a id="rule-429"></a>

### 429. 规则 9.2.1 带有敏感数据的表单必须使用 HTTP-POST 方法提交

| 字段 | 内容 |
|---|---|
| 问题级别 | 一般 |
| 语言 | JAVA |
| 标签 | SecBrella |
| 关联工具规则 | SecJ_Privacy_Violation_Submit_By_Get_Method |
| 规则类型 | 安全规范规则类 |
| CWE 信息 | 200 |
| 预估误报率 | 20% |

**审查要点**

web系统中泄露个人敏感数据数据。

**修复建议**

禁止使用Get方式提交个人数据,可以使用Post请求来避免个人数据被缓存、记录。

**正确示例**

```text
使用Post请求来避免个人数据被缓存、记录，在用javax.servlet.http.HttpServletResponse.sendRedirect方法重定向时URL参数不要拼接个人数据，如果URL为(delete|add|insert|update|edit)?xxx (xxx为个人数据)则会被判定为传递个人数据 ， 如果需要传递个人数据参数可使用spring框架RedirectAttributes提供的addFlashAttribute传递参数。
```java
@Controller
@RequestMapping({"v1"})
public class ResourceStatusController {
@PostMapping(value = "/test")
public String index(RedirectAttributes attributes) {
attributes.addFlashAttribute("pass", "***");
return "redirect:/test/next";
}
}
```
如果是服务器内部forward跳转使用request.setAttribute传递个人数据参数。
```java
public class CWE359_HW_NSCC_JAVA_Privacy_Violation_Submit_by_Get_Method_Good extends HttpServlet {
@Override
protected void doPost(HttpServletRequest req, HttpServletResponse response) throws ServletException, IOException {
response.setContentType("text/html; charset=UTF-8");
req.setAttribute("pass", "sa");
req.getRequestDispatcher("new.html").forward(req,response);
}
}
```
```

**错误示例**

```text
public class CWE359_HW_NSCC_JAVA_Privacy_Violation_Submit_by_Get_Method_01 extends HttpServlet {
@Override
protected void doGet(HttpServletRequest req, HttpServletResponse response) throws ServletException, IOException {
String ss = "add?pass='sa'";
response.setContentType("text/html; charset=UTF-8");
/* POTENTIAL FLAW: 使用Get方式提交表单数据，通过sendRedirect()方法重定向到指定地址，有可能在浏览器地址栏暴露提交的参数，容易导致个人数据泄露。 */
response.sendRedirect(ss);
}
}
```

**参考信息**

- 最新更新时间：2024-05-21 20:04:47

