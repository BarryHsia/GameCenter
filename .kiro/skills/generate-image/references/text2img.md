# text2img

## Generate Image

```bash
python3 {baseDir}/scripts/text2img.py --prompt "your description" --size "2K"
```


All options:
- `--prompt`: Input image generation prompt
- `--size`: Optional: 2K or 4K, default is 2K.

## Notes

- Synchronous API: returns immediately when generation completes (no polling)
- Image URLs valid for 24 hours
- Script prints `MEDIA:` line for auto-attachment
- Include datetime in filenames to distinguish




