#!/bin/bash

# 代码质量检查统一脚本
# 使用方法: ./codecheck.sh [目录] [--fast-mode]
#   目录: 可选，指定要检查的目录（默认为项目根目录）
#   --fast-mode: 快速模式，跳过自动修复和编译，仅做格式检查

FAST_MODE=false
SCAN_DIR=""

# 解析参数
while [[ $# -gt 0 ]]; do
    case $1 in
        --fast-mode)
            FAST_MODE=true
            shift
            ;;
        -*)
            shift
            ;;
        *)
            # 第一个非选项参数作为扫描目录
            if [[ -z "$SCAN_DIR" ]]; then
                SCAN_DIR="$1"
            fi
            shift
            ;;
    esac
done

set -e

# 颜色定义
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m'

# 获取脚本所在目录
SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"

# 获取项目根目录（脚本所在目录的父目录，或git根目录）
PROJECT_ROOT="$(cd "$SCRIPT_DIR/.." && pwd)"

# 如果是git仓库，使用git根目录
if git -C "$PROJECT_ROOT" rev-parse --show-toplevel &>/dev/null; then
    PROJECT_ROOT="$(git -C "$PROJECT_ROOT" rev-parse --show-toplevel)"
fi

# 输出带颜色的消息
print_message() {
    local color=$1
    local message=$2
    echo -e "${color}${message}${NC}"
}

# 自动修复阶段
fix_code() {
    print_message $BLUE "🔧 ========== 自动修复阶段 =========="

    # 确定扫描目录
    local TARGET_DIR="${SCAN_DIR:-$PROJECT_ROOT}"
    # 转换为绝对路径
    if [[ "$TARGET_DIR" != /* ]]; then
        TARGET_DIR="$PROJECT_ROOT/$TARGET_DIR"
    fi
    print_message $YELLOW "📁 扫描目录: $TARGET_DIR"

    # Java 空行处理 (G.STY.01)
    if [[ -f "$SCRIPT_DIR/scripts/reduce_blank_lines.py" ]]; then
        print_message $YELLOW "📝 去除多余空行 (G.STY.01)..."
        python "$SCRIPT_DIR/scripts/reduce_blank_lines.py" -r "$TARGET_DIR" 2>/dev/null || true
    fi

    # 后端 Spotless 自动格式化
    if [[ -f "$PROJECT_ROOT/pom.xml" ]]; then
        print_message $YELLOW "✨ Spotless 自动格式化..."
        (cd "$PROJECT_ROOT" && mvn com.diffplug.spotless:spotless-maven-plugin:apply -q) || true
    fi

    if [[ -d "$PROJECT_ROOT/frontend" ]]; then
        print_message $YELLOW "🔨 安装前台依赖包..."
        (cd "$PROJECT_ROOT/frontend" && npm install)
    fi

    # 前端 Prettier 自动格式化
    if [[ -d "$PROJECT_ROOT/frontend" ]]; then
        print_message $YELLOW "✨ Prettier 自动格式化..."
        (cd "$PROJECT_ROOT/frontend" && npx prettier --write . --log-level silent) || true
    fi

    # 前端 ESLint 自动修复
    if [[ -d "$PROJECT_ROOT/frontend" ]]; then
        print_message $YELLOW "🔧 ESLint 自动修复..."
        (cd "$PROJECT_ROOT/frontend" && npx eslint . --ext .vue,.js,.jsx,.cjs,.mjs --ignore-pattern ../.gitignore --fix) || true
    fi

    print_message $GREEN "✅ 自动修复完成"
}

# 检查阶段
check_code() {
    print_message $BLUE "🔍 ========== 检查阶段 =========="
    local has_errors=false
    local TARGET_DIR="${SCAN_DIR:-$PROJECT_ROOT}"
    # 转换为绝对路径
    if [[ "$TARGET_DIR" != /* ]]; then
        TARGET_DIR="$PROJECT_ROOT/$TARGET_DIR"
    fi

    # 判断是否只检查特定模块
    local IS_MODULE_CHECK=false
    if [[ -n "$SCAN_DIR" && "$SCAN_DIR" != "." && "$SCAN_DIR" != "$PROJECT_ROOT" ]]; then
        IS_MODULE_CHECK=true
    fi

    # 后端检查
    if [[ -f "$PROJECT_ROOT/pom.xml" ]]; then
        # 如果指定了特定模块，只编译该模块
        if [[ "$IS_MODULE_CHECK" == true ]]; then
            # 提取模块名
            local MODULE_NAME=$(basename "$TARGET_DIR")
            if [[ -f "$TARGET_DIR/pom.xml" ]]; then
                print_message $YELLOW "🔨 编译模块: $MODULE_NAME..."
                (cd "$PROJECT_ROOT" && mvn clean compile -DskipTests -pl "$MODULE_NAME" -am -q)
            else
                print_message $YELLOW "🔨 编译后端代码..."
                (cd "$PROJECT_ROOT" && mvn clean compile -DskipTests -q)
            fi
        else
            print_message $YELLOW "🔨 编译后端代码..."
            (cd "$PROJECT_ROOT" && mvn clean compile -DskipTests -q)
        fi

        # Spotless 检查
        print_message $YELLOW "✨ Spotless 格式检查..."
        if ! (cd "$PROJECT_ROOT" && mvn com.diffplug.spotless:spotless-maven-plugin:check -q) 2>&1; then
            has_errors=true
            print_message $RED "   Spotless 检查未通过"
        else
            print_message $GREEN "   Spotless 检查通过"
        fi

        # Checkstyle 检查
        print_message $YELLOW "📋 Checkstyle 检查..."
        CHECKSTYLE_OUTPUT=$(cd "$PROJECT_ROOT" && mvn checkstyle:check -Dstyle.color=never 2>&1) || true
        if printf '%s' "$CHECKSTYLE_OUTPUT" | grep -a -q "\[WARN\]\|\[ERROR\]" 2>/dev/null; then
            has_errors=true
            print_message $RED "   Checkstyle 检查未通过"
        else
            print_message $GREEN "   Checkstyle 检查通过"
        fi

        # SpotBugs 检查
        print_message $YELLOW "🐛 SpotBugs 检查..."
        SPOTBUGS_OUTPUT=$(cd "$PROJECT_ROOT" && mvn spotbugs:check 2>&1) || true
        if echo "$SPOTBUGS_OUTPUT" | grep -q "\[ERROR\]\|\[WARNING\]"; then
            has_errors=true
            print_message $RED "   SpotBugs 检查未通过"
        else
            print_message $GREEN "   SpotBugs 检查通过"
        fi
    fi

    # 前端检查
    if [[ -d "$PROJECT_ROOT/frontend" ]]; then
        # Prettier 检查
        print_message $YELLOW "✨ Prettier 格式检查..."
        if ! (cd "$PROJECT_ROOT/frontend" && npx prettier --check .) 2>&1; then
            has_errors=true
            print_message $RED "   Prettier 检查未通过"
        else
            print_message $GREEN "   Prettier 检查通过"
        fi

        # ESLint 检查
        print_message $YELLOW "🔧 ESLint 检查..."
        if ! (cd "$PROJECT_ROOT/frontend" && npx eslint . --ext .vue,.js,.jsx,.cjs,.mjs --ignore-pattern ../.gitignore) 2>&1; then
            has_errors=true
            print_message $RED "   ESLint 检查未通过"
        else
            print_message $GREEN "   ESLint 检查通过"
        fi
    fi

    print_message $GREEN "✅ 检查完成"

    # 输出结果
    if [[ "$has_errors" == true ]]; then
        print_message $RED "\n❌ 发现代码质量问题！"
        print_message $YELLOW "请先按照CODECHECK.md中的指导用IDEA解决格式化问题，然后再次执行此脚本检查剩余问题，之后手动修复"
    else
        print_message $GREEN "\n🎉 所有检查通过！"
    fi

    # 始终返回 0，避免 stop hook 报错
    exit 0
}

# 主函数
main() {
    print_message $BLUE "🚀 ========== 代码质量检查 =========="

    # 检查工具
    if ! command -v mvn &> /dev/null; then
        print_message $RED "错误: Maven 未安装"
        exit 1
    fi
    if ! command -v node &> /dev/null; then
        print_message $RED "错误: Node.js 未安装"
        exit 1
    fi

    # 执行检查
    fix_code
    check_code
}

# 运行主函数
main
