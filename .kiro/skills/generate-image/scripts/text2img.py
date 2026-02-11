#!/usr/bin/env python3
"""
Generate images using Seedream API (synchronous) with OpenAI SDK.

Usage:
    python3 generate_image.py --prompt "description" [--size 2048x2048]
"""

import argparse
import os
import sys
import requests
from pathlib import Path
from typing import Optional
from openai import OpenAI

# API配置
API_BASE = "https://ark.cn-beijing.volces.com/api/v3"


def get_api_key() -> Optional[str]:
    """Get API key (使用固定key，保持和原代码一致)"""
    return os.environ.get("API_KEY")


def generate_image(
    api_key: str,
    prompt: str,
    size: Optional[str],
) -> str:
    """Generate image using Seedream API via OpenAI SDK, return image URL."""
    # 初始化OpenAI客户端
    client = OpenAI(
        base_url=API_BASE,
        api_key=api_key,
    )
    
    # 构建请求参数
    params = {
        "model": "endpoint_id",
        "prompt": prompt,
        "response_format": "url",
        "extra_body": {
            "watermark": False,
        }
    }
    
    # 仅当size不为空时才添加到参数中（默认不传，由API处理2K默认值）
    if size:
        params["size"] = size
    
    try:
        # 调用图片生成API
        response = client.images.generate(**params)
        # 返回图片URL
        return response.data[0].url
    except Exception as e:
        raise Exception(f"API request failed: {str(e)}")


def main():
    parser = argparse.ArgumentParser(description="Generate images using Seedream API (doubao-seedream-4.5)")
    parser.add_argument("--prompt", "-p", required=True, help="Image description/prompt (required)")
    parser.add_argument("--size", "-s", default="2K", help='Image size (e.g., 2048x2048, 2K, 4K) (default: 2K)')

    args = parser.parse_args()

    # Get API key
    api_key = get_api_key()

    try:
        # Generate image
        print(f"Generating image with doubao-seedream-4.5 (size: {args.size})...")
        image_url = generate_image(
            api_key,
            args.prompt,
            args.size,
        )
        print(f"Image generated successfully! URL: {image_url}")
        
    except Exception as e:
        print(f"Error: {str(e)}", file=sys.stderr)
        sys.exit(1)


if __name__ == "__main__":
    main()