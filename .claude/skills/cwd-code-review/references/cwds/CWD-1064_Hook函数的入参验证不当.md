# CWD-1064 Hook函数的入参验证不当

**别名: **未验证Hook函数的入参

**描述**
产品对用户可访问的 API函数添加了 `Hook`，但未验证或不正确验证参数，这可能导致安全隐患。这类 `Hook` 技术可被用于具有高权限运行的防御性软件（如杀毒软件或防火墙）中，它劫持内核调用的（代码）。若未对参数进行有效验证，攻击者可能利用该缺陷绕过防护机制，甚至直接攻击防护软件本身。例如函数未验证其参数是否为正确类型，导致任意内存写入。

**语言: **C,CPP,JAVA,JAVASCRIPT

**严重等级**
一般

**cleancode特征**
安全,可靠

**示例**
**案例1: Hook函数的入参验证不当**
**语言: **JAVASCRIPT

**描述**
一个JavaScript应用程序中，开发者实现了一个函数钩子(hook)系统，允许插件在核心函数执行前后注入自定义逻辑。然而，由于对钩子回调函数的参数缺乏适当验证，攻击者可以通过恶意插件注入危险代码，导致任意代码执行漏洞。

**案例分析**
- 参数验证缺失：`executeHook`方法直接将所有接收到的参数传递给回调函数，没有进行任何验证或过滤。
- 原型污染风险：恶意插件可以通过传入的对象污染原型链，影响整个应用程序的行为。
- 任意代码执行：如果参数被当作函数执行(如user())，攻击者可以构造特殊参数导致任意代码执行。
- 缺乏上下文控制：钩子回调可以访问和修改原始参数，可能破坏核心功能的预期行为。
- 权限提升：如示例所示，恶意插件可以注入逻辑将普通用户提升为管理员。

**反例**
```javascript
// 核心系统中的钩子管理器
class HookManager {
  constructor() {
    this.hooks = {};
  }

  // 注册钩子
  registerHook(hookName, callback) {
    if (!this.hooks[hookName]) {
      this.hooks[hookName] = [];
    }
    this.hooks[hookName].push(callback);
  }

  // 执行钩子
  executeHook(hookName, ...args) {
    if (this.hooks[hookName]) {
      this.hooks[hookName].forEach(callback => {
        // 直接调用回调函数，没有验证参数
        callback(...args);
      });
    }
  }
}

// 示例使用 - 用户认证函数
function authenticateUser(user) {
  const hookManager = new HookManager();
  
  // 执行"pre-authenticate"钩子
  hookManager.executeHook('pre-authenticate', user);
  
  // 认证逻辑...
  console.log(`Authenticating user: ${user.username}`);
  
  // 执行"post-authenticate"钩子
  hookManager.executeHook('post-authenticate', user);
}

// 恶意插件代码
const maliciousPlugin = {
  init() {
    const hookManager = new HookManager();
    hookManager.registerHook('pre-authenticate', (user) => {
      // 恶意代码 - 利用未验证的参数
      if (user && user.constructor.prototype) {
        // 污染原型链
        user.constructor.prototype.isAdmin = true;
      }
      // 更危险的例子: 如果参数被当作函数执行
      if (typeof user === 'function') {
        user(); // 可能导致任意代码执行
      }
    });
  }
};

// 使用示例
maliciousPlugin.init();
authenticateUser({username: 'attacker'});
```

**正例**
```javascript
class HookManager {
  constructor() {
    this.hooks = {};
    this.sanitizeInput = this.sanitizeInput.bind(this);
  }

  // 注册钩子时验证回调函数
  registerHook(hookName, callback) {
    if (typeof callback !== 'function') {
      throw new Error('Hook callback must be a function');
    }
    
    if (!this.hooks[hookName]) {
      this.hooks[hookName] = [];
    }
    this.hooks[hookName].push(callback);
  }

  // 输入清理函数
  sanitizeInput(input) {
    if (input && typeof input === 'object') {
      // 创建输入对象的浅拷贝，防止原型污染
      const safeInput = {...input};
      
      // 移除可能危险的属性
      delete safeInput.__proto__;
      delete safeInput.constructor;
      
      return safeInput;
    }
    return input;
  }

  // 安全的钩子执行
  executeHook(hookName, ...args) {
    if (this.hooks[hookName]) {
      // 清理所有输入参数
      const safeArgs = args.map(this.sanitizeInput);
      
      this.hooks[hookName].forEach(callback => {
        try {
          // 使用apply调用，防止直接将参数作为函数执行
          callback.apply(null, safeArgs);
        } catch (error) {
          console.error(`Error executing hook ${hookName}:`, error);
        }
      });
    }
  }
}

// 安全的使用示例
function authenticateUser(user) {
  const hookManager = new HookManager();
  
  // 只传递必要的、经过验证的数据
  const safeUserData = {
    username: user.username,
    id: user.id
  };
  
  hookManager.executeHook('pre-authenticate', safeUserData);
  
  // 认证逻辑...
  console.log(`Authenticating user: ${user.username}`);
  
  hookManager.executeHook('post-authenticate', safeUserData);
}
```

**修复建议**
- 输入验证：添加了对回调函数类型的验证，确保只有函数可以被注册为钩子。
- 参数清理：实现了`sanitizeInput`方法，创建参数的浅拷贝并移除危险属性。
- 安全调用：使用`apply`调用回调函数，而不是直接传递参数。
- 最小数据暴露：只传递必要的、经过清理的数据给钩子回调。

#### CWD-1064-000 Hook函数的入参验证不当

#### CWD-1064-001 输入未做校验时，直接对输入进行处理可能会导致空指针、注入攻击、数据格式错误等问题

#### CWD-1064-002 钩子函数接收外部参数后直接拼接系统命令，未进行安全过滤

#### CWD-1064-003 钩子函数执行特权操作时未验证调用者身份

#### CWD-1064-004 钩子函数直接反序列化未经验证的输入数据

#### CWD-1064-005 读取文件时未解析符号链接的真实路径

**业界缺陷**

- [CWE-622: Improper Validation of Function Hook Arguments](https://cwe.mitre.org/data/definitions/622.html)
---

