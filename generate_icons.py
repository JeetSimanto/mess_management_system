#!/usr/bin/env python3
"""Generate proper transparent Android launcher icons from the user's MESS logo."""

from PIL import Image, ImageDraw, ImageFilter
import os

SOURCE = "/home/lucifer_vtn/.gemini/antigravity/brain/a71e6bbf-159f-4cc1-90ce-86260152e326/.tempmediaStorage/media_a71e6bbf-159f-4cc1-90ce-86260152e326_1786918966762.png"
RES_DIR = "/home/lucifer_vtn/mess_management_system/app/src/main/res"

FOREGROUND_SIZES = {
    "mipmap-mdpi": 108,
    "mipmap-hdpi": 162,
    "mipmap-xhdpi": 216,
    "mipmap-xxhdpi": 324,
    "mipmap-xxxhdpi": 432,
}

LAUNCHER_SIZES = {
    "mipmap-mdpi": 48,
    "mipmap-hdpi": 72,
    "mipmap-xhdpi": 96,
    "mipmap-xxhdpi": 144,
    "mipmap-xxxhdpi": 192,
}

def extract_transparent_text(src_img):
    """Extract non-white text pixels into a transparent RGBA image."""
    src = src_img.convert("RGBA")
    w, h = src.size
    pixels = src.load()
    
    out = Image.new("RGBA", (w, h), (0, 0, 0, 0))
    out_pixels = out.load()
    
    for y in range(h):
        for x in range(w):
            r, g, b, a = pixels[x, y]
            # Convert near-white background pixels to transparent
            if r > 225 and g > 225 and b > 225:
                out_pixels[x, y] = (0, 0, 0, 0)
            else:
                # Anti-alias edge blending
                brightness = (r + g + b) / 3.0
                if brightness > 190:
                    alpha = int(255 * (1.0 - (brightness - 190) / 40.0))
                    alpha = max(0, min(255, alpha))
                    out_pixels[x, y] = (r, g, b, alpha)
                else:
                    out_pixels[x, y] = (r, g, b, 255)
                    
    # Find bounding box of content
    bbox = out.getbbox()
    if bbox:
        # Add a tiny padding
        pad = 8
        min_x = max(0, bbox[0] - pad)
        min_y = max(0, bbox[1] - pad)
        max_x = min(w, bbox[2] + pad)
        max_y = min(h, bbox[3] + pad)
        return out.crop((min_x, min_y, max_x, max_y))
    return out

def create_foreground(text_img, canvas_size):
    """Create adaptive icon foreground with TRANSPARENT background.
    Safe zone in Android adaptive icons is 66dp of 108dp canvas (~61% diameter).
    """
    canvas = Image.new("RGBA", (canvas_size, canvas_size), (0, 0, 0, 0))
    
    safe_zone = int(canvas_size * (66.0 / 108.0))
    
    # Scale text to fit safely inside the safe zone width (with 5% breathing room)
    target_w = int(safe_zone * 0.90)
    
    tw, th = text_img.size
    scale = target_w / float(tw)
    
    nw = int(tw * scale)
    nh = int(th * scale)
    
    # Check height constraint
    if nh > safe_zone * 0.85:
        scale = (safe_zone * 0.85) / float(th)
        nw = int(tw * scale)
        nh = int(th * scale)
        
    resized = text_img.resize((nw, nh), Image.LANCZOS)
    
    x = (canvas_size - nw) // 2
    y = (canvas_size - nh) // 2
    
    canvas.paste(resized, (x, y), resized)
    return canvas

def create_launcher_square(text_img, canvas_size):
    """Create standard square launcher icon with WHITE background."""
    # White background canvas
    canvas = Image.new("RGBA", (canvas_size, canvas_size), (255, 255, 255, 255))
    
    target_w = int(canvas_size * 0.82)
    tw, th = text_img.size
    scale = target_w / float(tw)
    
    nw = int(tw * scale)
    nh = int(th * scale)
    
    if nh > canvas_size * 0.70:
        scale = (canvas_size * 0.70) / float(th)
        nw = int(tw * scale)
        nh = int(th * scale)
        
    resized = text_img.resize((nw, nh), Image.LANCZOS)
    
    x = (canvas_size - nw) // 2
    y = (canvas_size - nh) // 2
    
    canvas.paste(resized, (x, y), resized)
    return canvas

def create_launcher_round(text_img, canvas_size):
    """Create round launcher icon with WHITE circular background."""
    square = create_launcher_square(text_img, canvas_size)
    
    # Create mask for circular icon
    mask = Image.new("L", (canvas_size, canvas_size), 0)
    draw = ImageDraw.Draw(mask)
    draw.ellipse((0, 0, canvas_size - 1, canvas_size - 1), fill=255)
    
    round_img = Image.new("RGBA", (canvas_size, canvas_size), (0, 0, 0, 0))
    round_img.paste(square, (0, 0), mask)
    return round_img

def main():
    raw_src = Image.open(SOURCE)
    text_img = extract_transparent_text(raw_src)
    print(f"Extracted transparent text box: {text_img.size}")
    
    # Generate foregrounds
    for folder, size in FOREGROUND_SIZES.items():
        fg_path = os.path.join(RES_DIR, folder, "ic_launcher_foreground.png")
        fg = create_foreground(text_img, size)
        fg.save(fg_path, "PNG")
        print(f"Saved transparent foreground: {fg_path} ({size}x{size})")
        
    # Generate legacy square and round icons
    for folder, size in LAUNCHER_SIZES.items():
        sq_path = os.path.join(RES_DIR, folder, "ic_launcher.png")
        sq = create_launcher_square(text_img, size)
        sq.save(sq_path, "PNG")
        
        rd_path = os.path.join(RES_DIR, folder, "ic_launcher_round.png")
        rd = create_launcher_round(text_img, size)
        rd.save(rd_path, "PNG")
        print(f"Saved launcher icons ({folder}): {size}x{size}")
        
    # Also update drawable foreground
    draw_fg_path = os.path.join(RES_DIR, "drawable", "ic_launcher_foreground_img.png")
    fg_draw = create_foreground(text_img, 432)
    fg_draw.save(draw_fg_path, "PNG")
    print(f"Saved drawable foreground: {draw_fg_path}")
    
    print("\nAll icon assets successfully rebuilt!")

if __name__ == "__main__":
    main()
