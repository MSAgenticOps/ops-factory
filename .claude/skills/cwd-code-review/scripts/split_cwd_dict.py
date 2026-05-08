#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""
按H1标题拆分Markdown文件
将CWD代码缺陷字典按每个H1标题拆分为独立文件
"""

import re
import os
from pathlib import Path


def sanitize_filename(filename: str) -> str:
    """
    清理文件名，移除或替换不允许的字符
    """
    # 替换特殊字符
    filename = filename.replace('：', '_')
    filename = filename.replace(':', '_')
    filename = filename.replace('"', '_')
    filename = filename.replace('"', '_')
    filename = filename.replace('<', '_')
    filename = filename.replace('>', '_')
    filename = filename.replace('/', '_')
    filename = filename.replace('\\', '_')
    filename = filename.replace('|', '_')
    filename = filename.replace('?', '_')
    filename = filename.replace('*', '_')
    filename = filename.replace(' ', '_')
    filename = filename.strip()

    # 确保文件名不为空
    if not filename:
        filename = "unnamed_section"

    return filename


def remove_quanji_section(content: str) -> str:
    """
    删除从 **缺陷防护全景** 到 **示例** 之间的内容（包括这两个标记）

    Args:
        content: 原始markdown内容

    Returns:
        删除指定内容后的markdown内容
    """
    # 使用正则表达式匹配并删除 **缺陷防护全景** 到 **示例** 之间的所有内容
    # 模式匹配: **缺陷防护全景** 开头，后面跟任意内容，直到 **示例**
    pattern = r'\*\*缺陷防护全景\*\*(?:[\s\S]*?)\*\*示例\*\*'

    # 替换为空字符串
    cleaned_content = re.sub(pattern, '**示例**', content)

    return cleaned_content


def split_markdown_by_h1(input_file: str, output_dir: str = None) -> None:
    """
    按H1标题拆分Markdown文件

    Args:
        input_file: 输入的Markdown文件路径
        output_dir: 输出目录，默认为输入文件同级目录下的同名文件夹
    """
    input_path = Path(input_file)
    if not input_path.exists():
        print(f"错误: 文件不存在 - {input_file}")
        return

    # 设置输出目录
    if output_dir is None:
        output_dir = input_path.parent / input_path.stem
    else:
        output_dir = Path(output_dir)

    # 创建输出目录
    output_dir.mkdir(parents=True, exist_ok=True)
    print(f"输出目录: {output_dir}")

    # 读取源文件
    content = input_path.read_text(encoding='utf-8')

    # 使用正则表达式匹配H1标题
    # H1标题格式: # 标题内容
    h1_pattern = re.compile(r'^#\s+(.+)$', re.MULTILINE)

    # 找到所有H1标题的位置
    matches = list(h1_pattern.finditer(content))

    print(f"找到 {len(matches)} 个H1标题")

    if len(matches) == 0:
        print("未找到任何H1标题")
        return

    # 遍历每个H1标题，提取内容并保存
    for i, match in enumerate(matches):
        title = match.group(1).strip()
        start_pos = match.start()

        # 确定当前章节的结束位置（下一个H1标题的开始位置 或 文件末尾）
        if i + 1 < len(matches):
            end_pos = matches[i + 1].start()
        else:
            end_pos = len(content)

        # 提取章节内容（包括标题本身）
        section_content = content[start_pos:end_pos]

        # 删除从 **缺陷防护全景** 到 **示例** 之间的内容
        section_content = remove_quanji_section(section_content)

        # 清理标题作为文件名
        filename = sanitize_filename(title)

        # 确保文件名唯一（如果有重复标题）
        safe_title = filename
        counter = 1
        while True:
            output_file = output_dir / f"{safe_title}.md"
            if not output_file.exists():
                break
            safe_title = f"{filename}_{counter}"
            counter += 1

        # 保存文件
        output_file.write_text(section_content, encoding='utf-8')
        print(f"  [+] {title} -> {output_file.name}")

    print(f"\n完成! 共拆分 {len(matches)} 个文件到 {output_dir}")


def main():
    """主函数"""
    # 配置输入输出路径
    input_file = r"CWD代码缺陷字典 V1.5_2.md"

    # 执行拆分
    split_markdown_by_h1(input_file, '../references/cwds')


if __name__ == "__main__":
    main()