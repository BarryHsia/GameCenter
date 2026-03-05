#!/usr/bin/env python3
"""
Android 内存泄漏静态分析脚本
扫描 Kotlin/Java 源码，检测常见内存泄漏模式。

用法: python analyze.py [目录路径] [--severity high|medium|low|all]
默认扫描: app/src/main
"""

import os
import re
import sys
import json
from dataclasses import dataclass, asdict
from enum import Enum
from pathlib import Path
from typing import Optional


class Severity(Enum):
    HIGH = "HIGH"
    MEDIUM = "MEDIUM"
    LOW = "LOW"


@dataclass
class LeakFinding:
    file: str
    line: int
    rule: str
    severity: Severity
    problem: str
    fix: str


# ── 检测规则 ──────────────────────────────────────────────

CONTEXT_TYPES = r'(?:Context|Activity|Fragment|View|Dialog|Service)'
STATIC_BLOCK_KT = re.compile(r'companion\s+object', re.IGNORECASE)
STATIC_FIELD_JAVA = re.compile(r'static\s+.*?' + CONTEXT_TYPES, re.IGNORECASE)
STATIC_CONTEXT_REF = re.compile(
    r'(?:var|val|lateinit\s+var)\s+\w+\s*[:\=]\s*.*?' + CONTEXT_TYPES, re.IGNORECASE
)

INNER_CLASS_KT = re.compile(r'^\s*inner\s+class\s+', re.MULTILINE)
NON_STATIC_HANDLER = re.compile(
    r'(?:inner\s+class|class)\s+\w*Handler\w*|'
    r'(?:val|var)\s+\w*[hH]andler\w*\s*=\s*(?:object\s*:\s*Handler|Handler\s*\()',
    re.IGNORECASE
)
HANDLER_REMOVE = re.compile(r'removeCallbacksAndMessages', re.IGNORECASE)

REGISTER_RECEIVER = re.compile(r'registerReceiver\s*\(', re.IGNORECASE)
UNREGISTER_RECEIVER = re.compile(r'unregisterReceiver\s*\(', re.IGNORECASE)
ADD_OBSERVER = re.compile(r'(?:addObserver|addEventListener|addListener)\s*\(', re.IGNORECASE)
REMOVE_OBSERVER = re.compile(r'(?:removeObserver|removeEventListener|removeListener)\s*\(', re.IGNORECASE)
REGISTER_SP = re.compile(r'registerOnSharedPreferenceChangeListener', re.IGNORECASE)
UNREGISTER_SP = re.compile(r'unregisterOnSharedPreferenceChangeListener', re.IGNORECASE)

GLOBAL_SCOPE = re.compile(r'GlobalScope\s*\.', re.IGNORECASE)
LIFECYCLE_SCOPE = re.compile(r'(?:lifecycleScope|viewModelScope|repeatOnLifecycle)', re.IGNORECASE)

CURSOR_OPEN = re.compile(r'(?:query|rawQuery|managedQuery)\s*\(', re.IGNORECASE)
CURSOR_CLOSE = re.compile(r'\.close\s*\(', re.IGNORECASE)
CURSOR_USE = re.compile(r'\.use\s*\{', re.IGNORECASE)
TYPED_ARRAY = re.compile(r'obtainStyledAttributes|obtainAttributes', re.IGNORECASE)
TYPED_ARRAY_RECYCLE = re.compile(r'\.recycle\s*\(', re.IGNORECASE)


def scan_file(filepath: str) -> list[LeakFinding]:
    """扫描单个文件，返回检测结果列表。"""
    findings = []
    try:
        with open(filepath, 'r', encoding='utf-8', errors='ignore') as f:
            content = f.read()
            lines = content.split('\n')
    except (IOError, OSError):
        return findings

    rel_path = filepath
    is_activity_or_fragment = bool(
        re.search(r':\s*(?:\w+)?(?:Activity|Fragment|Service)\s*\(', content)
    )

    # 1. 静态引用泄漏
    in_companion = False
    companion_start = -1
    brace_count = 0
    for i, line in enumerate(lines, 1):
        if STATIC_BLOCK_KT.search(line):
            in_companion = True
            companion_start = i
            brace_count = 0
        if in_companion:
            brace_count += line.count('{') - line.count('}')
            if STATIC_CONTEXT_REF.search(line):
                # 提取实际匹配的类型名
                type_match = re.search(r'(Context|Activity|Fragment|View|Dialog|Service)', line)
                type_name = type_match.group(1) if type_match else "Context/View"
                findings.append(LeakFinding(
                    file=rel_path, line=i, rule="static-context-leak",
                    severity=Severity.HIGH,
                    problem=f"companion object 中持有 {type_name} 类型引用",
                    fix="改用 ApplicationContext 或 WeakReference<T>"
                ))
            if brace_count <= 0 and i > companion_start:
                in_companion = False

        if STATIC_FIELD_JAVA.search(line):
            type_match = re.search(r'(Context|Activity|Fragment|View|Dialog|Service)', line)
            type_name = type_match.group(1) if type_match else "Context/View"
            findings.append(LeakFinding(
                file=rel_path, line=i, rule="static-context-leak",
                severity=Severity.HIGH,
                problem=f"static 字段持有 {type_name} 类型引用",
                fix="改用 ApplicationContext 或 WeakReference<T>"
            ))

    # 2. 内部类泄漏
    if is_activity_or_fragment:
        for i, line in enumerate(lines, 1):
            if INNER_CLASS_KT.search(line):
                findings.append(LeakFinding(
                    file=rel_path, line=i, rule="inner-class-leak",
                    severity=Severity.MEDIUM,
                    problem="inner class 隐式持有外部 Activity/Fragment 引用",
                    fix="去掉 inner 关键字，改为独立类或通过构造函数传入 WeakReference"
                ))

    # 3. Handler 泄漏
    if is_activity_or_fragment:
        has_handler = False
        handler_line = 0
        for i, line in enumerate(lines, 1):
            if NON_STATIC_HANDLER.search(line):
                has_handler = True
                handler_line = i
        if has_handler and not HANDLER_REMOVE.search(content):
            findings.append(LeakFinding(
                file=rel_path, line=handler_line, rule="handler-leak",
                severity=Severity.MEDIUM,
                problem="Handler 未在 onDestroy 中调用 removeCallbacksAndMessages(null)",
                fix="onDestroy 中添加 handler.removeCallbacksAndMessages(null)，或改用 lifecycleScope"
            ))

    # 4. 监听器未注销
    if REGISTER_RECEIVER.search(content) and not UNREGISTER_RECEIVER.search(content):
        line_num = next(
            (i for i, l in enumerate(lines, 1) if REGISTER_RECEIVER.search(l)), 0
        )
        findings.append(LeakFinding(
            file=rel_path, line=line_num, rule="receiver-not-unregistered",
            severity=Severity.HIGH,
            problem="registerReceiver 无对应 unregisterReceiver",
            fix="在 onDestroy/onStop 中调用 unregisterReceiver"
        ))

    if ADD_OBSERVER.search(content) and not REMOVE_OBSERVER.search(content):
        line_num = next(
            (i for i, l in enumerate(lines, 1) if ADD_OBSERVER.search(l)), 0
        )
        findings.append(LeakFinding(
            file=rel_path, line=line_num, rule="observer-not-removed",
            severity=Severity.MEDIUM,
            problem="addObserver/addListener 无对应 remove",
            fix="在对应生命周期回调中 removeObserver/removeListener"
        ))

    if REGISTER_SP.search(content) and not UNREGISTER_SP.search(content):
        line_num = next(
            (i for i, l in enumerate(lines, 1) if REGISTER_SP.search(l)), 0
        )
        findings.append(LeakFinding(
            file=rel_path, line=line_num, rule="sp-listener-not-unregistered",
            severity=Severity.MEDIUM,
            problem="SharedPreference listener 未注销",
            fix="在 onDestroy 中调用 unregisterOnSharedPreferenceChangeListener"
        ))

    # 5. 协程泄漏
    if is_activity_or_fragment:
        for i, line in enumerate(lines, 1):
            if GLOBAL_SCOPE.search(line):
                findings.append(LeakFinding(
                    file=rel_path, line=i, rule="global-scope-leak",
                    severity=Severity.HIGH,
                    problem="Activity/Fragment 中使用 GlobalScope，不会随生命周期取消",
                    fix="改用 lifecycleScope（Activity/Fragment）或 viewModelScope（ViewModel）"
                ))

    # 6. 资源未释放
    if CURSOR_OPEN.search(content) and not CURSOR_CLOSE.search(content) and not CURSOR_USE.search(content):
        line_num = next(
            (i for i, l in enumerate(lines, 1) if CURSOR_OPEN.search(l)), 0
        )
        findings.append(LeakFinding(
            file=rel_path, line=line_num, rule="cursor-not-closed",
            severity=Severity.HIGH,
            problem="Cursor 未调用 close() 或 use {}",
            fix="使用 cursor.use { } 块或在 finally 中 close()"
        ))

    if TYPED_ARRAY.search(content) and not TYPED_ARRAY_RECYCLE.search(content):
        line_num = next(
            (i for i, l in enumerate(lines, 1) if TYPED_ARRAY.search(l)), 0
        )
        findings.append(LeakFinding(
            file=rel_path, line=line_num, rule="typed-array-not-recycled",
            severity=Severity.MEDIUM,
            problem="TypedArray 未调用 recycle()",
            fix="在使用完毕后调用 typedArray.recycle()"
        ))

    return findings


# ── 主流程 ────────────────────────────────────────────────

SEVERITY_ICON = {
    Severity.HIGH: "🔴",
    Severity.MEDIUM: "🟡",
    Severity.LOW: "🟢",
}

SEVERITY_ORDER = {Severity.HIGH: 0, Severity.MEDIUM: 1, Severity.LOW: 2}


def scan_directory(directory: str, severity_filter: Optional[str] = None) -> list[LeakFinding]:
    """递归扫描目录下所有 .kt/.java 文件。"""
    all_findings = []
    extensions = ('.kt', '.java')

    for root, _, files in os.walk(directory):
        for fname in files:
            if fname.endswith(extensions):
                fpath = os.path.join(root, fname)
                all_findings.extend(scan_file(fpath))

    # 按严重级别排序
    all_findings.sort(key=lambda f: SEVERITY_ORDER[f.severity])

    # 过滤
    if severity_filter and severity_filter != 'all':
        target = severity_filter.upper()
        all_findings = [f for f in all_findings if f.severity.value == target]

    return all_findings


def format_findings(findings: list[LeakFinding]) -> str:
    """格式化输出结果。"""
    if not findings:
        return "✅ 未检测到内存泄漏风险。"

    output_lines = [f"共检测到 {len(findings)} 个潜在内存泄漏风险：\n"]

    for f in findings:
        icon = SEVERITY_ICON[f.severity]
        output_lines.append(f"{icon} [{f.severity.value}] {f.file}:{f.line}")
        output_lines.append(f"   规则: {f.rule}")
        output_lines.append(f"   问题: {f.problem}")
        output_lines.append(f"   修复: {f.fix}")
        output_lines.append("")

    # 统计
    high = sum(1 for f in findings if f.severity == Severity.HIGH)
    medium = sum(1 for f in findings if f.severity == Severity.MEDIUM)
    low = sum(1 for f in findings if f.severity == Severity.LOW)
    output_lines.append(f"统计: 🔴 高危 {high} | 🟡 中危 {medium} | 🟢 低危 {low}")

    return '\n'.join(output_lines)


def output_json(findings: list[LeakFinding]) -> str:
    """输出 JSON 格式结果。"""
    return json.dumps(
        [{"file": f.file, "line": f.line, "rule": f.rule,
          "severity": f.severity.value, "problem": f.problem, "fix": f.fix}
         for f in findings],
        ensure_ascii=False, indent=2
    )


def main():
    # 解析参数
    args = sys.argv[1:]
    directory = "app/src/main"
    severity_filter = "all"
    output_format = "text"

    i = 0
    while i < len(args):
        if args[i] == '--severity' and i + 1 < len(args):
            severity_filter = args[i + 1]
            i += 2
        elif args[i] == '--json':
            output_format = "json"
            i += 1
        elif not args[i].startswith('--'):
            directory = args[i]
            i += 1
        else:
            i += 1

    if not os.path.isdir(directory):
        print(f"错误: 目录不存在 - {directory}")
        sys.exit(1)

    findings = scan_directory(directory, severity_filter)

    if output_format == "json":
        print(output_json(findings))
    else:
        print(format_findings(findings))

    # 有高危问题时返回非零退出码
    if any(f.severity == Severity.HIGH for f in findings):
        sys.exit(2)


if __name__ == '__main__':
    main()
