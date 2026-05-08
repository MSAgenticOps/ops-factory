#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""
CWD代码缺陷字典全景视图 CSV 转 Markdown 索引文件
"""

import csv
import os
from collections import defaultdict


def sanitize_filename(filename: str) -> str:
    """
    清理文件名，移除或替换不允许的字符
    """
    # 替换特殊字符
    filename = filename.replace('：', '_')
    filename = filename.replace(':', '_')
    filename = filename.replace('"', '_')
    filename = filename.replace('\'', '_')
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


def parse_cwd_id(cwd: str) -> tuple:
    """
    解析CWD字符串，返回 (cwd_id, cwd_name)
    例如: "CWD-1044 文件名或路径外部可控" -> ("CWD-1044", "文件名或路径外部可控")
    """
    if ' ' in cwd:
        parts = cwd.split(' ', 1)
        return parts[0], parts[1]
    return cwd, ''


def generate_markdown(csv_path: str, output_path: str):
    """
    读取CSV文件并生成Markdown索引文件
    """
    # 按一级分类组织数据
    categories_data = defaultdict(list)

    # 读取CSV文件（处理BOM）
    with open(csv_path, 'r', encoding='utf-8-sig') as f:
        reader = csv.DictReader(f)
        # 清理列名中的BOM字符
        fieldnames = [name.lstrip('\ufeff') for name in reader.fieldnames]
        for row in reader:
            # 清理每行的键
            clean_row = {k.lstrip('\ufeff'): v for k, v in row.items()}
            cat_level_1 = clean_row['CAT_Level_1']
            cat_level_2 = clean_row['CAT_Level_2']
            cwd = clean_row['CWD']
            language = clean_row['Language']

            cwd_id, cwd_name = parse_cwd_id(cwd)

            # 生成索引文件名
            sanitized_name = sanitize_filename(cwd_name)
            index_file = f"cwds/{cwd_id}_{sanitized_name}.md"

            categories_data[cat_level_1].append({
                'cwd_id': cwd_id,
                'cwd_name': cwd_name,
                'sanitized_name': sanitized_name,
                'cat_level_2': cat_level_2,
                'language': language,
                'index_file': index_file
            })

    # 生成Markdown内容
    md_content = """# CWD（代码缺陷字典）参考索引

本索引提供 CWD 缺陷类别的按需加载。每个类别可以单独加载或按组加载。

----

## 完整类别列表

"""

    # 按分类排序
    for cat_level_1 in sorted(categories_data.keys()):
        items = categories_data[cat_level_1]
        # 提取分类名称（去掉CAT前缀编号）
        cat_name = ' '.join(cat_level_1.split()[1:]) if ' ' in cat_level_1 else cat_level_1

        md_content += f"### {cat_name}\n\n"
        md_content += "| ID | 名称 | 子类别 | 语言 | 索引文件 | \n"
        md_content += "|----|------|-------|----------|----------| \n"

        for item in sorted(items, key=lambda x: x['cwd_id']):
            cwd_id = item['cwd_id']
            cwd_name = item['cwd_name']
            sanitized_name = item['sanitized_name']
            cat_level_2 = item['cat_level_2']
            language = item['language']
            index_file = item['index_file']

            # 如果有二级分类，显示二级分类名称
            sub_cat = ' '.join(cat_level_2.split()[1:]) if cat_level_2 and ' ' in cat_level_2 else (cat_level_2 if cat_level_2 else '')

            md_content += f"| {cwd_id} | {sanitized_name} | {sub_cat} | {language} | {index_file} | \n"

        md_content += "\n"

    # 写入Markdown文件
    with open(output_path, 'w', encoding='utf-8') as f:
        f.write(md_content)

    print(f"生成完成: {output_path}")
    print(f"共 {len(categories_data)} 个一级分类，{sum(len(v) for v in categories_data.values())} 个CWD条目")


if __name__ == "__main__":
    # 配置路径
    csv_path = "CWD代码缺陷字典全景视图_2026_在研.csv"
    output_path = "../references/cwd_index.md"

    # 获取当前脚本所在目录
    script_dir = os.path.dirname(os.path.abspath(__file__))
    csv_path = os.path.join(script_dir, csv_path)
    output_path = os.path.join(script_dir, output_path)

    generate_markdown(csv_path, output_path)